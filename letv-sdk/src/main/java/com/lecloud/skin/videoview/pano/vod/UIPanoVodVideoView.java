package com.lecloud.skin.videoview.pano.vod;


import android.content.Context;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.sdk.utils.NetworkUtils;
import com.lecloud.skin.ui.impl.LetvVodUICon;
import com.lecloud.skin.videoview.pano.base.BasePanoSurfaceView;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.letv.pano.IPanoListener;

/**
 * Created by heyuekuai on 16/5/27.
 */
public class UIPanoVodVideoView extends UIVodVideoView {
    ISurfaceView surfaceView;
    private int controllMode = -1;
    private int displayMode;
    private boolean isConfigurationCanBeChanged=true;
    public UIPanoVodVideoView(Context context) {
        super(context);
        letvVodUICon.canGesture(false);
    }
    public UIPanoVodVideoView(Context context,boolean isConfigurationCanBeChanged,FullScreenPlay listener) {
        super(context,isConfigurationCanBeChanged,listener);
        this.isConfigurationCanBeChanged=isConfigurationCanBeChanged;
        letvVodUICon.canGesture(false);
    }

    @Override
    public void prepareVideoSurface() {
        surfaceView = new BasePanoSurfaceView(context);
        controllMode = ((BasePanoSurfaceView) surfaceView).switchControllMode(controllMode);
        displayMode = ((BasePanoSurfaceView) surfaceView).switchDisplayMode(displayMode);
        setVideoView(surfaceView);
        ((BasePanoSurfaceView) surfaceView).registerPanolistener(new IPanoListener() {
            @Override
            public void setSurface(Surface surface) {
                if(!NetworkUtils.hasConnect(context)){
                    return;
                }
                player.setDisplay(surface);
            }

            @Override
            public void onSingleTapUp(MotionEvent e) {
                letvVodUICon.performClick();
            }

            @Override
            public void onNotSupport(int mode) {
                Toast.makeText(context, "not support current mode " + mode, Toast.LENGTH_LONG).show();
            }
        });
        ((LetvVodUICon) letvVodUICon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(player !=null && player.isPlaying()){
                    ((BasePanoSurfaceView) surfaceView).onPanoTouch(v, event);
                    return true;
                }else{
                    return false;
                }
            }
        });

        letvVodUICon.isPano(true);
    }

    @Override
    protected int switchControllMode(int mode) {
        controllMode = ((BasePanoSurfaceView) surfaceView).switchControllMode(mode);
        return controllMode;
    }

    @Override
    protected int switchDisplayMode(int mode) {
        if (surfaceView == null) {
            displayMode = mode;
        } else {
            displayMode = ((BasePanoSurfaceView) surfaceView).switchDisplayMode(mode);
        }
        return displayMode;
    }
}
