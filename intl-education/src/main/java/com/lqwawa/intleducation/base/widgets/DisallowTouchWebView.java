package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 对WebView的共用封装
 * @date 2018/06/13 16:34
 * @history v1.0
 * **********************************
 */
public class DisallowTouchWebView extends WebView{

    public DisallowTouchWebView(Context context) {
        super(context);
        initWebSetting();
    }

    public DisallowTouchWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebSetting();
    }

    public DisallowTouchWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebSetting();
    }

    /**
     * 初始化一些设置
     */
    private void initWebSetting(){
        setBackgroundColor(0);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
