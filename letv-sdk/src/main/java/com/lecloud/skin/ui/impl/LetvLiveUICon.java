package com.lecloud.skin.ui.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.playerlibrelease.R;
import com.lecloud.skin.ui.ILetvSwitchMachineListener;
import com.lecloud.skin.ui.base.BaseChgScreenBtn;
import com.lecloud.skin.ui.base.IBaseLiveSeekBar;
import com.lecloud.skin.ui.utils.ReUtils;
import com.lecloud.skin.ui.view.V4LargeLiveMediaControllerNew;
import com.lecloud.skin.ui.view.V4SmallLiveMediaControllerNew;
import com.lecloud.skin.ui.view.V4TopTitleView;

public class LetvLiveUICon extends BaseLetvLiveUICon {

    public LetvLiveUICon(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LetvLiveUICon(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LetvLiveUICon(Context context) {
        super(context);
    }

    ILetvSwitchMachineListener machineListener;

    @Override
    public void setMachineListener(ILetvSwitchMachineListener machineListener) {
        this.machineListener = machineListener;
    }

    @Override
    protected void init(Context context) {
        super.init(context);
//		UI 分层
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rlSkin = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.letv_skin_v4_skin_live, null);
        addView(rlSkin, params);
        videoLock = (ImageView) findViewById(ReUtils.getId(context, "iv_video_lock"));
        videoLock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lockFlag = !lockFlag;
                if (machineListener != null) {
                    machineListener.showSwitchMachineBtn(lockFlag);
                }
                if (lockFlag) {
                    hide();
                    videoLock.setImageResource(R.drawable.screen_lock_drawable);
                } else {
                    show();
                    videoLock.setImageResource(R.drawable.screen_unlock_drawable);
                }
            }
        });
        mLargeMediaController = (V4LargeLiveMediaControllerNew) findViewById(R.id.v4_large_media_controller);
        mV4TopTitleView = (V4TopTitleView) findViewById(R.id.v4_letv_skin_v4_top_layout);
        mSmallMediaController = (V4SmallLiveMediaControllerNew) findViewById(R.id.v4_small_media_controller);
    }
    public void setEndOnlineBtnStatus(boolean isShow){
        TextView tvEndOnlineSmall= (TextView) mSmallMediaController.findViewById(R.id
                .bottom_end_online);
        TextView tvEndOnlineBig= (TextView) mLargeMediaController.findViewById(R.id.bottom_end_online);

        if (isShow){
            if (tvEndOnlineSmall!=null) {
                tvEndOnlineSmall.setVisibility(View.VISIBLE);
            }
            if (tvEndOnlineBig!=null) {
                tvEndOnlineBig.setVisibility(View.VISIBLE);
            }
        }else {
            if (tvEndOnlineSmall!=null) {
                tvEndOnlineSmall.setVisibility(View.GONE);
            }
            if (tvEndOnlineBig!=null){
                tvEndOnlineBig.setVisibility(View.GONE);
            }
        }
    }

    public void setBottomShowStatus(boolean isShow){
        if (mLargeMediaController!=null){
            if (isShow){
                mLargeMediaController.setVisibility(View.VISIBLE);
            }else {
                mLargeMediaController.setVisibility(View.GONE);
            }
        }
        if (mSmallMediaController!=null){
            if (isShow){
                mSmallMediaController.setVisibility(View.VISIBLE);
            }else {
                mSmallMediaController.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mV4TopTitleView.setTitle(title);
            mV4TopTitleView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean performClick() {
        if (lockFlag) {
            return false;
        }
        if (rlSkin != null) {
            if (!mRlSkinHide) {
                hide();
            } else {
                show();
            }
            return false;
        }
        return super.performClick();
    }

    @Override
    protected void seekTo(int seekGap) {
        IBaseLiveSeekBar seekBar = ((V4LargeLiveMediaControllerNew) mLargeMediaController).getSeekbar();
        if (seekBar != null) {
            seekBar.startTrackingTouch();
            Log.i("zsn", "--------seekGap:" + seekGap);
            seekBar.setProgressGap(seekGap);
            seekBar.setSeekToTime(seekGap, true);
            if (mGestureControl.mSeekToPopWindow != null) {
                mGestureControl.mSeekToPopWindow.setProgress(seekBar.getSeekToTime());
            }
        }
        super.seekTo(seekGap);
    }

    @Override
    protected void touchUp() {
        IBaseLiveSeekBar seekBar = ((V4LargeLiveMediaControllerNew) mLargeMediaController).getSeekbar();
        if (seekBar != null) {
            seekBar.stopTrackingTouch();
        }
        super.touchUp();
    }

    /**
     * 同步进度条
     *
     */
    @Override
    public void syncSeekProgress(int progress) {
        if (mLargeMediaController != null) {
            ((V4LargeLiveMediaControllerNew) mLargeMediaController).getSeekbar().setProgress(progress);
        }
        if (mSmallMediaController != null) {
            ((V4SmallLiveMediaControllerNew) mSmallMediaController).getSeekbar().setProgress(progress);
        }
    }

    public void setTitleVisible(boolean isVisible){
        if (isVisible){
            if (mV4TopTitleView!=null){
                mV4TopTitleView.setVisibility(View.VISIBLE);
            }
        }else {
            if (mV4TopTitleView!=null){
                mV4TopTitleView.setVisibility(View.GONE);
            }
        }
    }

    public void setCurrentFullBtnStatus(boolean isFullScreen){
        BaseChgScreenBtn mSmall= (BaseChgScreenBtn) mSmallMediaController.findViewById(R.id.vnew_chg_btn);
        BaseChgScreenBtn mBig= (BaseChgScreenBtn) mLargeMediaController.findViewById(R.id.vnew_chg_btn);
        if (mSmall!=null&&mBig!=null){
            if (isFullScreen){
                mSmall.showZoomInState();
                mBig.showZoomInState();
            }else {
                mSmall.showZoomOutState();
                mBig.showZoomOutState();
            }
        }
    }
}
