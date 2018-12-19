package com.lqwawa.intleducation.module.onclass.detail.base.plan;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.DateUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.LiveEntity;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空中课堂直播列表
 * @date 2018/06/02 14:41
 * @history v1.0
 * **********************************
 */
public class PlanLiveAdapter extends RecyclerAdapter<LiveEntity>{


    // 角色信息
    private String mRole;
    private boolean isParent;
    private Activity mParentActivity;
    private ClassDetailEntity mEntity;
    // 是否已经结束授课
    private boolean isGiveFinish;
    // 是否是历史班
    private boolean isGiveHistory;
    // 是否已经加入
    private boolean isJoin;
    private TeachingPlanNavigator mNavigator;

    public PlanLiveAdapter(@NonNull Activity mParentActivity,
                           @NonNull @OnlineClassRole.RoleRes String role,
                           @NonNull ClassDetailEntity entity,
                           boolean isParent,
                           boolean isJoin) {
        this.mRole = role;
        this.mParentActivity = mParentActivity;
        this.mEntity = entity;
        this.isParent = isParent;
        this.isJoin = isJoin;
    }

    @Override
    protected int getItemViewType(int position, LiveEntity liveEntity) {
        return R.layout.item_online_class_teaching_plan_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LiveEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 设置是否授课结束
     * @param isGiveFinish 是否授课结束
     */
    public void setGiveFinish(boolean isGiveFinish){
        this.isGiveFinish = isGiveFinish;
    }

    public void setGiveHistory(boolean isGiveHistory){
        this.isGiveHistory = isGiveHistory;
    }


    /**
     * 设置点击事件的监听
     * @param navigator 事件导航
     */
    public void setNavigator(TeachingPlanNavigator navigator){
        this.mNavigator = navigator;
    }

    public class ViewHolder extends RecyclerAdapter.ViewHolder<LiveEntity>{

        private View mViewPoint;
        private TextView mLiveTitle;
        private TextView mAudition;
        private TextView mLiveState;
        private TextView mLiveTime;
        private ImageView mLiveDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            mViewPoint = itemView.findViewById(R.id.view_point);
            mLiveTitle = (TextView) itemView.findViewById(R.id.tv_live_title);
            mAudition = (TextView) itemView.findViewById(R.id.tv_audition);
            mLiveState = (TextView) itemView.findViewById(R.id.tv_live_state);
            mLiveTime = (TextView) itemView.findViewById(R.id.tv_live_time);
            mLiveDelete = (ImageView) itemView.findViewById(R.id.iv_live_delete);
        }

        @Override
        protected void onBind(LiveEntity liveEntity) {
            String title = liveEntity.getTitle();
            if(EmptyUtil.isNotEmpty(title) && title.length() > 12){
                title = title.substring(0,12) + "...";
            }

            //直播的标题
            mLiveTitle.setText(title);
            //直播时间 以区间的形式来显示
            mLiveTime.setText(DateUtil.formatLiveTime(liveEntity.getStartTime(),liveEntity.getEndTime()));
            //当前直播的状态
            int state = liveEntity.getState();
            if (state == 0) {
                mLiveState.setBackgroundResource(R.drawable.live_trailer);
                mLiveState.setText(R.string.live_trailer);
            } else if (state == 1) {
                mLiveState.setBackgroundResource(R.drawable.live_living);
                mLiveState.setText(R.string.live_living);
            } else if (state == 2) {
                mLiveState.setBackgroundResource(R.drawable.live_review);
                mLiveState.setText(R.string.live_review);
            }

            // 显示试听
            int position = getAdapterPosition();
            if(isAudition(position)){
                mAudition.setVisibility(View.VISIBLE);
            }else{
                mAudition.setVisibility(View.GONE);
            }

            mLiveDelete.setOnClickListener(v -> {
                if(!EmptyUtil.isEmpty(mNavigator)){
                    mNavigator.onDelete(liveEntity);
                }
            });

            // 只有是已加入,并且是老师角色才显示删除,且老师只能删除自己创建的直播
            if(EmptyUtil.isEmpty(mEntity) || EmptyUtil.isEmpty(mEntity.getData())){
                return;
            }

            ClassDetailEntity.DataBean dataBean = mEntity.getData().get(0);

            if(!isParent &&
                    !isGiveFinish && !isGiveHistory &&
                    mParentActivity instanceof JoinClassDetailActivity &&
                    // 优先家长身份判断
                    // 如果授课已结束或者是历史班,不允许删除授课计划
                    (UserHelper.getUserId().equals(dataBean.getCreateId()) ||
                            // 前一种代表是班主任
                            (OnlineClassRole.ROLE_TEACHER.equals(mRole) &&
                                    UserHelper.getUserId().equals(liveEntity.getCreateId())))){
                            // 是老师身份,且当前直播也是自己创建的
                mLiveDelete.setVisibility(View.VISIBLE);
            }else{
                mLiveDelete.setVisibility(View.GONE);
            }
        }

        /**
         * 获取当前直播显示的起止时间
         *
         * @param entity 数据实体
         * @return 显示字串 2018-05-12 18:20 -- 20:22
         */
        private String getCurrentOnlineTime(@NonNull LiveEntity entity) {
            String beginTime = entity.getStartTime();
            String endTime = entity.getEndTime();
            if (!TextUtils.isEmpty(beginTime)) {
                beginTime = beginTime.substring(0, beginTime.length() - 3);
            }
            if (!TextUtils.isEmpty(endTime)) {
                endTime = endTime.substring(endTime.length() - 8, endTime.length() - 3);
            }
            String showTime = beginTime + " -- " + endTime;
            if (TextUtils.isEmpty(showTime)) {
                return "";
            }
            return showTime;
        }

        /**
         * 是否试听Item
         * @param position 列表位置
         * @return true 显示位置
         */
        private boolean isAudition(int position){
            if(position == 0 && !isJoin){
                return true;
            }
            return false;
        }
    }
}
