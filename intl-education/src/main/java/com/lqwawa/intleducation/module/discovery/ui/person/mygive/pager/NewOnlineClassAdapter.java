package com.lqwawa.intleducation.module.discovery.ui.person.mygive.pager;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.DateUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;

/**
 * @author medici
 * @desc 横向的在线班级Item Adapter
 */
public class NewOnlineClassAdapter extends RecyclerAdapter<OnlineClassEntity> {

    @Override
    protected int getItemViewType(int position, OnlineClassEntity onlineClassEntity) {
        return R.layout.item_new_online_class_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<OnlineClassEntity> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    public static class ViewHolder extends RecyclerAdapter.ViewHolder<OnlineClassEntity>{

        private ImageView mClassIcon;
        private TextView mClassCreateTime,mClassName,mClassTeachers,mClassCount;
        // private TextView mClassCourseMoney;
        private TextView mPrice,mOriginalPrice;
        // 授课状态
        private TextView mGiveState;

        public ViewHolder(View itemView) {
            super(itemView);
            mClassIcon = (ImageView) itemView.findViewById(R.id.iv_class_icon);
            mClassCreateTime = (TextView) itemView.findViewById(R.id.tv_create_time);
            mClassName = (TextView) itemView.findViewById(R.id.tv_class_name);
            mClassTeachers = (TextView) itemView.findViewById(R.id.tv_teachers);
            mClassCount = (TextView) itemView.findViewById(R.id.tv_count);
            // mClassCourseMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mPrice = (TextView) itemView.findViewById(R.id.tv_price);
            mOriginalPrice = (TextView) itemView.findViewById(R.id.tv_original_price);
            mGiveState = (TextView) itemView.findViewById(R.id.tv_give_state);
        }

        @Override
        protected void onBind(OnlineClassEntity onlineClassEntity) {
            // 模拟数据
            // onlineClassEntity.setDiscount(true);

            ImageUtil.fillDefaultView(mClassIcon,onlineClassEntity.getThumbnailUrl());
            mClassName.setText(EmptyUtil.isEmpty(onlineClassEntity.getName())? "" : onlineClassEntity.getName());
            mClassTeachers.setText(EmptyUtil.isEmpty(onlineClassEntity.getTeachersName())? "" : onlineClassEntity.getTeachersName());
            mClassCount.setText(String.format(UIUtil.getString(R.string.label_many_people_join_desc),onlineClassEntity.getJoinCount()));
            mClassCreateTime.setText(getCurrentOnlineTime(onlineClassEntity));
            // mClassCourseMoney.setText(onlineClassEntity.getPrice() == 0? UIUtil.getString(R.string.label_class_gratis) : Common.Constance.MOOC_MONEY_MARK + onlineClassEntity.getPrice());
            if(onlineClassEntity.getPrice() == 0){
                // 免费
                mOriginalPrice.setVisibility(View.GONE);
                StringUtil.fillSafeTextView(mPrice,UIUtil.getString(R.string.label_class_gratis));
            }else{
                // 收费
                StringUtil.fillSafeTextView(mPrice,Common.Constance.MOOC_MONEY_MARK + onlineClassEntity.getPrice());
                if(onlineClassEntity.isDiscount()){
                    // 有打折价
                    mOriginalPrice.setVisibility(View.VISIBLE);
                    // 中划线
                    mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    StringUtil.fillSafeTextView(mOriginalPrice,Common.Constance.MOOC_MONEY_MARK + onlineClassEntity.getOriginalPrice());
                }else{
                    // 无打折价
                    mOriginalPrice.setVisibility(View.GONE);
                }
            }

            if(onlineClassEntity.getProgressStatus() == OnlineClassEntity.PROGRESS_STATUS_IDLE){
                // 即将开始
                mGiveState.setBackgroundResource(R.drawable.bg_status_radius_idle);
                mGiveState.setText(UIUtil.getString(R.string.label_new_give_idle));
            }else if(onlineClassEntity.getProgressStatus() == OnlineClassEntity.PROGRESS_STATUS_START){
                // 正在授课
                mGiveState.setBackgroundResource(R.drawable.bg_status_radius_start);
                mGiveState.setText(UIUtil.getString(R.string.label_give_start));
            }else if(onlineClassEntity.getProgressStatus() == OnlineClassEntity.PROGRESS_STATUS_FINISH){
                // 结束授课
                // 后面改成完成授课
                mGiveState.setBackgroundResource(R.drawable.bg_status_radius_finish);
                mGiveState.setText(UIUtil.getString(R.string.label_give_complete));
            }else if(onlineClassEntity.getProgressStatus() == OnlineClassEntity.PROGRESS_STATUS_HISTORY){
                // 历史授课
                mGiveState.setBackgroundResource(R.drawable.bg_status_radius_history);
                mGiveState.setText(UIUtil.getString(R.string.label_history_give));
            }
        }

        /**
         * 获取当前直播显示的起止时间
         *
         * @param onlineClassEntity 数据实体
         * @return 返回处理过需要显示的直播时间信息
         */
        private String getCurrentOnlineTime(@NonNull OnlineClassEntity onlineClassEntity) {
            String beginTime = onlineClassEntity.getStartTime();
            String endTime = onlineClassEntity.getEndTime();
            // 如果隔天精确到天，如果是同一天，显示到分钟
            return DateUtil.formatLiveTime(beginTime,endTime);
            /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long beginMillis = TimeUtil.string2Millis(beginTime,format);
            long endMillis = TimeUtil.string2Millis(endTime,format);

            beginTime = TimeUtil.millis2String(beginMillis,new SimpleDateFormat("yyyy-MM-dd"));
            endTime = TimeUtil.millis2String(endMillis,new SimpleDateFormat("yyyy-MM-dd"));

            if (!TextUtils.isEmpty(beginTime) && !TextUtils.isEmpty(endTime)) {
                String showTime = beginTime + " -- " + endTime;
                return showTime;
            }
            return "";*/
        }
    }
}
