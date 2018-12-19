package com.lecloud.skin.videoview.pano.live;

import android.content.Context;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.Toast;

import com.lecloud.sdk.surfaceview.ISurfaceView;
import com.lecloud.skin.ui.impl.LetvLiveUICon;
import com.lecloud.skin.videoview.live.UIActionLiveVideoView;
import com.lecloud.skin.videoview.pano.base.BasePanoSurfaceView;
import com.letv.pano.IPanoListener;

/**
 * Created by heyuekuai on 16/5/31.
 */
public class UIPanoActionLiveVideoView extends UIActionLiveVideoView {
    ISurfaceView surfaceView;
    protected int controllMode = -1;
    private int displayMode;
    private boolean isConfigurationCanBeChanged=true;
    public UIPanoActionLiveVideoView(Context context,boolean isConfigurationCanBeChanged,FullScreenPlay listener) {
        super(context,isConfigurationCanBeChanged,listener);
        this.isConfigurationCanBeChanged=isConfigurationCanBeChanged;
    }
    public UIPanoActionLiveVideoView(Context context,boolean isConfigurationCanBeChanged) {
        super(context,isConfigurationCanBeChanged);
        this.isConfigurationCanBeChanged=isConfigurationCanBeChanged;
    }
    public UIPanoActionLiveVideoView(Context context) {
        super(context);
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
                player.setDisplay(surface);
            }
            @Override
            public void onSingleTapUp(MotionEvent e) {
                letvLiveUICon.performClick();
            }

            @Override
            public void onNotSupport(int mode) {
                Toast.makeText(context, "not support current mode " + mode, Toast.LENGTH_LONG).show();
            }
        });

        ((LetvLiveUICon) letvLiveUICon).setOnTouchListener(new OnTouchListener() {
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
        letvLiveUICon.isPano(true);
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

    protected void enablePanoGesture(boolean enable){
//    	if(enable){
//    		if(lastPanoVideoMode != -1){
//    			switchControllMode(lastPanoVideoMode);
//    		}
//    	}else{
//    		if(panoVideoMode == BaseChangeModeBtn.MODE_MOVE){
//	    		lastPanoVideoMode = panoVideoMode;
//	    		switchControllMode(BaseChangeModeBtn.MODE_TOUCH);
//    		}
//    	}
    }
}
