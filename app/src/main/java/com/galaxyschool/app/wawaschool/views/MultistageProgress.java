package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.lqwawa.intleducation.common.utils.UIUtil;

public class MultistageProgress extends View {

    private Paint backgroundPaint, progressPaint;//背景和进度条画笔
    private int weights[];//每个区域的权重
    private int colors[];//颜色
    private float totalWeight;//总的权重
    private float progress = 0, maxProgress = 100;//进度和最大进度

    public float getProgress() {
        return progress;
    }

    public float getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    public MultistageProgress(Context context) {
        super(context);
        init();
    }

    public MultistageProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultistageProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        backgroundPaint.setColor(Color.RED);
        progressPaint = new Paint();
        progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        progressPaint.setColor(Color.parseColor("#ffffff"));
    }

    /**
     * 设置进度条颜色
     *
     * @param color
     */
    public void setProgressColor(int color) {
        progressPaint.setColor(color);
    }

    /**
     * 设置每一段的颜色以及对应的权重
     *
     * @param colors
     * @param weights
     */
    public void setColors(int[] colors, int[] weights) {
        if (colors == null || weights == null || colors.length != weights.length) {
            invalidate();
            return;
        }
        this.colors = colors;
        this.weights = weights;
        totalWeight = 0;
        for (int i = 0; i < weights.length; i++) {
            totalWeight += weights[i];
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maxProgress <= 0) {
            maxProgress = getWidth();
        }
        int x = 0, y = getHeight();
        if (colors != null && colors.length > 0) {
            int length = colors.length;
            RectF rectF = new RectF();
            rectF.top = 0;
            rectF.left = 0;
            rectF.right = getWidth();
            rectF.bottom = getHeight();
            progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            progressPaint.setAntiAlias(true);
            if (length > 1){
                progressPaint.setColor(colors[length - 1]);
                progressPaint.setStrokeWidth(0f);
            } else {
                progressPaint.setColor(colors[0]);
                progressPaint.setStrokeWidth(1.5f);
            }
            canvas.drawRoundRect(rectF, DensityUtils.dp2px(UIUtil.getContext(), 10), DensityUtils.dp2px(UIUtil.getContext(), 10), progressPaint);
            if (length > 1) {
                for (int i = 0; i < length - 1; i++) {
                    Rect rect = new Rect();
                    progressPaint.setColor(colors[i]);
                    progressPaint.setStrokeWidth(0f);
                    progressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    progressPaint.setAntiAlias(true);
                    int width = (int) (getWidthForWeight(weights[i], totalWeight));
                    rect.set(x, 0, x + width, y);
                    RectF rectFProgress = new RectF();
                    if (i == 0) {
                        rectFProgress.top = 0;
                        rectFProgress.left = 0;
                        rectFProgress.right = width + DensityUtils.dp2px(UIUtil.getContext(), 10);
                        rectFProgress.bottom = y;
                        canvas.drawRoundRect(rectFProgress, DensityUtils.dp2px(UIUtil.getContext(), 10), DensityUtils.dp2px(UIUtil.getContext(), 10), progressPaint);
                    } else {
                        canvas.drawRect(rect, progressPaint);//绘制矩形
                    }
                    x = x + width;//计算下一个的开始位置
                }
            }
        } else {
            RectF rectF = new RectF();
            rectF.top = 0;
            rectF.left = 0;
            rectF.right = getWidth();
            rectF.bottom = getHeight();
            progressPaint.setStyle(Paint.Style.STROKE);
            progressPaint.setStrokeWidth(1.5f);
            progressPaint.setAntiAlias(true);
            progressPaint.setColor(Color.parseColor("#cdcdcd"));
            canvas.drawRoundRect(rectF, DensityUtils.dp2px(UIUtil.getContext(), 10), DensityUtils.dp2px(UIUtil.getContext(), 10), progressPaint);
        }
    }

    /**
     * 根据权重获取对应的宽度
     *
     * @param weight
     * @param totalWeight
     * @return
     */
    public float getWidthForWeight(float weight, float totalWeight) {
        return getWidth() * (weight / totalWeight) + 0.5f;
    }

    /**
     * 根据根据权重在数组中的索引获取对应的位置
     *
     * @param position
     * @return
     */
    public float getXForWeightPosition(int position) {
        float xPosition = 0;
        for (int i = 0; i < position; i++) {
            xPosition += getWidthForWeightPosition(i);
        }
        return xPosition;
    }

    /**
     * 根据根据权重在数组中的索引获取对应的宽度
     *
     * @param position
     * @return
     */
    public float getWidthForWeightPosition(int position) {
        return getWidth() * (weights[position] / totalWeight) + 0.5f;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
