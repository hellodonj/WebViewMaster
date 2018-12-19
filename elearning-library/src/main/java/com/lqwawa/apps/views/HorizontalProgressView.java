package com.lqwawa.apps.views;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/1/30 10:22
 * E-Mail Address:863378689@qq.com
 * Describe:webView加载的水平方向的进度条
 * ======================================================
 */

public class HorizontalProgressView extends View{
    private int DEFAULT_COLOR = Color.GRAY;
    private int DEFAULT_PAINT_WIDTH = 10;
    private Paint mPaint;
    private int mWidth, mHeight;
    private int progress;//加载进度

    public HorizontalProgressView(Context context) {
        this(context, null);
    }

    public HorizontalProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化画笔
        mPaint = new Paint();
        //图片抖动功能 画出来更加的清晰
        mPaint.setDither(true);
        //抗锯齿
        mPaint.setAntiAlias(true);
        //画笔的宽度
        mPaint.setStrokeWidth(DEFAULT_PAINT_WIDTH);
        //画笔的颜色
        mPaint.setColor(DEFAULT_COLOR);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        mWidth = w;
        mHeight = h;
        super.onSizeChanged(w, h, ow, oh);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, mWidth * progress / 100, mHeight, mPaint);
        super.onDraw(canvas);
    }

    /**
     * 设置新进度 重新绘制
     *
     * @param newProgress 新进度
     */
    public void setProgress(int newProgress) {
        this.progress = newProgress;
        invalidate();
    }

    /**
     * 设置进度条颜色
     *
     * @param color 色值
     */
    public void setColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 设置画笔的宽度
     * @param width 宽度
     */
    public void setPaintWidth(int width){
        mPaint.setStrokeWidth(width);
    }
}
