package com.lqwawa.zbarlib;

import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.womob.albumvideo.GLView;
import com.womob.albumvideo.HelloAR;
import com.womob.albumvideo.InitAR;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/25 18:11
 * desp: 描 述： AR扫描界面
 * ================================================
 */

public class ARScanFragment extends Fragment implements HelloAR.videoPath {

    public static final String TAG = ARScanFragment.class.getSimpleName();

    private GLView glView;
    private FrameLayout container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arscan, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();

        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                Log.i("IdleHandler", "queueIdle");
                onInit();
                //false 表示只监听一次IDLE事件,之后就不会再执行这个函数了.
                return false;
            }
        });
    }

    private void initViews() {
       if (getView() != null) {
           ImageView imageView = (ImageView)getView().findViewById(R.id.iv_close);
           if (imageView != null) {
               imageView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if (getActivity() != null) {
                           getActivity().finish();
                       }
                   }
               });
           }

           TextView textView = (TextView)getView().findViewById(R.id.tv_title);
           if (textView != null) {
               textView.setText(R.string.ar_scanning);
           }
           textView = (TextView)getView().findViewById(R.id.tv_photo);
           if (textView != null) {
               textView.setVisibility(View.GONE);
           }
           
           container = (FrameLayout) getView().findViewById(R.id.preview);
       }
    }

    private void onInit() {
        InitAR initAR = new InitAR(ARScanFragment.this, getActivity(), glView);
        glView = initAR.initInfo();
        if (glView != null) {
            if (container != null) {
                container.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                        .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            glView.onResume();
        } else {
            Log.i(TAG, "AR Init Error!");
        }
    }

    @Override
    public void getVideoPath(String s) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (glView != null) {
            glView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (glView != null) {
            glView.onPause();
        }
    }
}
