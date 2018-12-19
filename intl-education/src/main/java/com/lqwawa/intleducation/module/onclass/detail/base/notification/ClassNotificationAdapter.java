package com.lqwawa.intleducation.module.onclass.detail.base.notification;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级通知的Adapter
 * @date 2018/06/04 16:22
 * @history v1.0
 * **********************************
 */
public class ClassNotificationAdapter extends RecyclerAdapter<ClassNotificationEntity>{

    // 角色信息
    private String mRole;
    private Activity mParentActivity;
    private ClassDetailEntity mEntity;
    private boolean isGiveFinish;
    private boolean isGiveHistory;
    private boolean isParent;
    private ClassNotificationNavigator mNavigator;

    public ClassNotificationAdapter(@NonNull Activity activity,
                                    @NonNull @OnlineClassRole.RoleRes String role,
                                    @NonNull ClassDetailEntity entity) {
        mParentActivity = activity;
        this.mRole = role;
        this.mEntity = entity;
    }

    @Override
    protected int getItemViewType(int position, ClassNotificationEntity classNotificationEntity) {
        return R.layout.item_online_class_notification_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    public void setNavigator(@NonNull ClassNotificationNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * 是否授课结束
     * @param isGiveFinish true 授课结束
     */
    public void setGiveFinish(boolean isGiveFinish){
        this.isGiveFinish = isGiveFinish;
    }

    /**
     * 是否历史班
     * @param isGiveHistory true 历史班
     */
    public void setGiveHistory(boolean isGiveHistory){
        this.isGiveHistory = isGiveHistory;
    }

    /**
     * 是否是家长身份
     * @param isParent true 家长身份
     */
    public void setParent(boolean isParent){
        this.isParent = isParent;
    }

    private class ViewHolder extends RecyclerAdapter.ViewHolder<ClassNotificationEntity>{

        private ImageView mNotificationAvatar;
        private TextView mNotificationTitle;
        private TextView mNotificationTeacher;
        private TextView mNotificationTime;
        private ImageView mNotificationDelete;
        // 阅读量 评论 赞
        private TextView mReadCount,mCommentCount,mZanCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mNotificationAvatar = (ImageView) itemView.findViewById(R.id.iv_notification_avatar);
            mNotificationTitle = (TextView) itemView.findViewById(R.id.tv_notification_title);
            mNotificationTeacher = (TextView) itemView.findViewById(R.id.tv_notification_teacher);
            mNotificationTime = (TextView) itemView.findViewById(R.id.tv_notification_time);
            mNotificationDelete = (ImageView) itemView.findViewById(R.id.iv_notification_delete);
            mReadCount = (TextView) itemView.findViewById(R.id.tv_read_count);
            mCommentCount = (TextView) itemView.findViewById(R.id.tv_comment);
            mZanCount = (TextView) itemView.findViewById(R.id.tv_zan);
        }

        @Override
        protected void onBind(ClassNotificationEntity entity) {
            ImageUtil.fillNotificationView(mNotificationAvatar,entity.getThumbnail());
            mNotificationTitle.setText(EmptyUtil.isEmpty(entity.getTitle()) ? "" : entity.getTitle());
            mNotificationTeacher.setText(EmptyUtil.isEmpty(entity.getAuthorName()) ? "" : entity.getAuthorName());
            mNotificationTime.setText(EmptyUtil.isEmpty(entity.getCreatedTime()) ? "" : entity.getCreatedTime());
            mNotificationDelete.setOnClickListener(Void -> {
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onNotificationDelete(entity);
                }
            });

            // 只有是已加入,并且是老师角色才显示删除,且老师只能删除自己创建的直播
            if(EmptyUtil.isEmpty(mEntity) || EmptyUtil.isEmpty(mEntity.getData())){
                return;
            }

            ClassDetailEntity.DataBean dataBean = mEntity.getData().get(0);
            // 没有加入不会进入到通知列表
            if(!isParent && !isGiveFinish && !isGiveHistory/*&& mParentActivity instanceof JoinClassDetailActivity*/ &&
                    (UserHelper.getUserId().equals(dataBean.getCreateId()) ||
                            // 前一种代表是班主任
                            (OnlineClassRole.ROLE_TEACHER.equals(mRole) && UserHelper.getUserId().equals(entity.getAuthorId())))){
                // 授课技术,不允许删除通知
                // 是老师身份,且当前直播也是自己创建的
                mNotificationDelete.setVisibility(View.VISIBLE);
            }else{
                mNotificationDelete.setVisibility(View.GONE);
            }

            mReadCount.setText(String.format(UIUtil.getString(R.string.label_notification_read_count),entity.getReadNumber()));
            mCommentCount.setText(String.format(UIUtil.getString(R.string.label_notification_comment_count),entity.getCommentNumber()));
            mZanCount.setText(String.format(UIUtil.getString(R.string.label_notification_zan_count),entity.getPointNumber()));
        }
    }
}
