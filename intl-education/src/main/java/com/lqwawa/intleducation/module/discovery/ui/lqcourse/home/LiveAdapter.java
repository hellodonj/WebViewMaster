package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQ学程直播模块的Adapter
 * @date 2018/05/02 16:21
 * @history v1.0
 * **********************************
 */
public class LiveAdapter extends RecyclerAdapter<LiveVo>{

    private static final int STATE_TRAILER = 0;
    private static final int STATE_LIVING = 1;
    private static final int STATE_REVIEW =2;


    @Override
    protected int getItemViewType(int position, LiveVo liveVo) {
        return R.layout.item_lq_discovery_live_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LiveVo> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    private static class ViewHolder extends RecyclerAdapter.ViewHolder<LiveVo>{
        // 直播状态
        private TextView mLiveState;
        // 直播标题
        private TextView mLiveTitle;
        // 直播学校
        private TextView mSchoolName;
        // 直播图片
        private ImageView mLiveAvatar;
        // 直播讲师
        private TextView mLiveDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            mLiveAvatar = (ImageView) itemView.findViewById(R.id.iv_live_avatar);
            mSchoolName = (TextView) itemView.findViewById(R.id.tv_live_school);
            mLiveTitle = (TextView) itemView.findViewById(R.id.tv_live_title);
            mLiveDesc = (TextView) itemView.findViewById(R.id.tv_live_desc);
            mLiveState = (TextView) itemView.findViewById(R.id.tv_online_state);
        }

        @Override
        protected void onBind(LiveVo liveVo) {
            //当前直播的状态
            int status = liveVo.getState();
            if (status == STATE_TRAILER) {
                mLiveState.setBackgroundResource(R.drawable.live_trailer);
                mLiveState.setText(R.string.live_trailer);
            } else if (status == STATE_LIVING) {
                mLiveState.setBackgroundResource(R.drawable.live_living);
                mLiveState.setText(R.string.live_living);
            } else if (status == STATE_REVIEW) {
                mLiveState.setBackgroundResource(R.drawable.live_review);
                mLiveState.setText(R.string.live_review);
            }
            // 设置直播标题
            mLiveTitle.setText(liveVo.getTitle());
            // 设置直播学校
            mSchoolName.setText(liveVo.getSchoolName());
            // 设置直播学校
            mLiveDesc.setText(String.format(UIUtil.getString(R.string.join_count),
                    StringUtils.getThousandText(liveVo.getJoinCount())) + " | "
                    + getLiveTypeText(liveVo.isIsEbanshuLive()));
            // 设置直播url
            String coverUrl = liveVo.getCoverUrl();
            if (!TextUtils.isEmpty(coverUrl) && coverUrl.contains("coverUrl")) {
                JSONObject jsonObject = JSON.parseObject(coverUrl, Feature.AutoCloseSource);
                coverUrl = jsonObject.getString("coverUrl");
            }

            ImageUtil.fillDefaultView(mLiveAvatar,coverUrl);
        }

        public static String getLiveTypeText(boolean isEbanshuLive){
            return UIUtil.getString(isEbanshuLive ? R.string.live_type_blackboard : R.string.live_type_video);
        }
    }
}
