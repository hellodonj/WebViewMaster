package com.lecloud.skin.ui.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;

import com.lecloud.skin.ui.LetvUIListener;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;

import java.util.List;

public abstract class BaseVodMediaController extends BaseMediaController {

    protected BasePlayerSeekBar mBasePlayerSeekBar;

    protected TextTimerView mTextTimerView;

    public BaseVodMediaController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSeekBarColor();
    }

    public BaseVodMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSeekBarColor();
    }

    public BaseVodMediaController(Context context) {
        super(context);
        setSeekBarColor();
    }

    @Override
    public void setLetvUIListener(LetvUIListener mLetvUIListener) {
        this.mLetvUIListener = mLetvUIListener;
        if (mLetvUIListener != null) {
            if (mBasePlayBtn != null) {
                mBasePlayBtn.setLetvUIListener(mLetvUIListener);
            }
            if (mBaseChgScreenBtn != null) {
                mBaseChgScreenBtn.setLetvUIListener(mLetvUIListener);
            }
//			if (mBaseDownloadBtn != null) {
//				mBaseDownloadBtn.setLetvVodUIListener(mLetvVodUIListener);
//			}
            if (mBaseRateTypeBtn != null) {
                mBaseRateTypeBtn.setLetvUIListener(mLetvUIListener);
            }
            if (mBasePlayerSeekBar != null) {
                mBasePlayerSeekBar.setLetvUIListener(mLetvUIListener);
            }
            if (mBaseChangeModeBtn != null) {
                mBaseChangeModeBtn.setLetvUIListener(mLetvUIListener);
            }
            if (mBaseChangeVRModeBtn != null) {
                mBaseChangeVRModeBtn.setLetvUIListener(mLetvUIListener);
            }
        }
    }

    @Override
    public void setRateTypeItems(List<String> ratetypes, String definition) {
        if (ratetypes != null && ratetypes.size() > 0) {
            mBaseRateTypeBtn.setVisibility(VISIBLE);
        }
        if (mBaseRateTypeBtn != null) {
            mBaseRateTypeBtn.setRateTypeItems(ratetypes, definition);
        }
    }

    @Override
    public void setPlayState(boolean isPlayState) {
        if (mBasePlayBtn != null) {
            mBasePlayBtn.setPlayState(isPlayState);
        }
    }

    @Override
    public void setCurrentPosition(final long position) {
        mBasePlayerSeekBar.setCurrentPosition(position);
        if (mTextTimerView != null) {
            post(new Runnable() {

                @Override
                public void run() {
                    if (duration != 0) {
                        mTextTimerView.setTextTimer(position, duration);
                    } else {
                        mTextTimerView.setTextTimer(0, duration);
                    }
                }
            });
        }
    }

    long duration;

    @Override
    public void setDuration(long duration) {
        if (duration != 0) {
            this.duration = duration;
        }
        mBasePlayerSeekBar.setDuration(duration);
    }

    @Override
    public void setBufferPercentage(long bufferPercentage) {
        // TODO Auto-generated method stub
        mBasePlayerSeekBar.setBufferPercentage(bufferPercentage);
    }

    public BasePlayerSeekBar getSeekbar() {
        return mBasePlayerSeekBar;
    }

    /**
     * 设置进度条颜色
     *
     */
    public void setSeekBarColor() {

        int[] colorArray = VodVideoSettingUtil.getInstance().getColorArray();
        if (colorArray == null) {
            return;
        }
        //获取seerbar层次drawable对象
        LayerDrawable layerDrawable = (LayerDrawable) mBasePlayerSeekBar.getProgressDrawable();
        // 有多少个层次（最多三个）
        int layers = layerDrawable.getNumberOfLayers();
        Drawable[] drawables = new Drawable[layers];
        for (int i = 0; i < layers; i++) {
            switch (layerDrawable.getId(i)) {
                // 如果是seekbar背景
                case android.R.id.background:
                    drawables[i] = new PaintDrawable(colorArray[0]);
                    drawables[i].setBounds(layerDrawable.getDrawable(i).getBounds());
                    break;
                // 如果是拖动条第一进度
                case android.R.id.progress:
                    //这里为动态的颜色值
                    drawables[i] = new PaintDrawable(colorArray[1]);
                    drawables[i].setBounds(layerDrawable.getDrawable(i).getBounds());
                    break;
                case android.R.id.secondaryProgress:
                    //这里为动态的颜色值
                    drawables[i] = new PaintDrawable(colorArray[2]);
                    drawables[i].setBounds(layerDrawable.getDrawable(i).getBounds());
                    break;

            }
        }
        mBasePlayerSeekBar.setProgressDrawable(new LayerDrawable(drawables));
        mBasePlayerSeekBar.invalidate();
    }

}
