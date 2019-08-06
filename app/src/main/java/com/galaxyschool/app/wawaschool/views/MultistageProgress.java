package com.galaxyschool.app.wawaschool.views;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class MultistageProgress extends View {

    private Paint backgroundPaint, progressPaint, linePaint;//背景和进度条画笔
    private Rect progressRect = new Rect();//进度条;
    private Rect backgroundRects[];//背景矩形区域
    private float weights[];//每个区域的权重
    private int colors[];//颜色
    private float totalWeight;//总的权重
    public static final int DEF_COLORS[];//默认背景颜色数组
    public static final float DEF_WEIGHTS[];//每段对应的权重
    private float progress = 10, maxProgress = 100;//进度和最大进度

    static {
        DEF_COLORS = new int[]{
                Color.parseColor("#00B6D0"),
                Color.parseColor("#0198AE"),
                Color.parseColor("#008396"),
                Color.parseColor("#007196"),
                Color.parseColor("#005672")
        };
        DEF_WEIGHTS = new float[]{
                10, 20, 20, 10, 40
        };
    }

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
        progressPaint.setColor(Color.parseColor("#d9d9d9"));
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linePaint.setColor(Color.parseColor("#e7eaf0"));
        linePaint.setStrokeWidth(2);
        setColors(DEF_COLORS, DEF_WEIGHTS);

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
    public void setColors(int[] colors, float weights[]) {
        if (colors == null || weights == null) {
            throw new NullPointerException("colors And weights must be not null");
        }
        if (colors.length != weights.length) {
            throw new IllegalArgumentException("colors And weights length must be same");
        }
        backgroundRects = new Rect[colors.length];
        this.colors = colors;
        this.weights = weights;
        totalWeight = 0;
        for (int i = 0; i < weights.length; i++) {
            totalWeight += weights[i];
            backgroundRects[i] = new Rect();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundRects == null) {
            return;
        }
        if (maxProgress <= 0) {
            maxProgress = getWidth();
        }
        //绘制背景颜色块
        int x = 0, y = getHeight();
        int progressX = (int) getWidthForWeight(progress, maxProgress);
        for (int i = 0; i < colors.length; i++) {
            Rect rect = backgroundRects[i];
            backgroundPaint.setColor(colors[i]);
            int width = (int) (getWidthForWeight(weights[i], totalWeight));
            rect.set(x, 0, x + width, y);
            x += width;//计算下一个的开始位置
            canvas.drawRect(rect, backgroundPaint);//绘制矩形
        }
        progressRect.set(0, 0, progressX, getHeight());//设置进度条区域
        canvas.drawRect(progressRect, progressPaint);//绘制进度条
        for (int i = 0, lineX = 0; i < colors.length; i++) {
            int width = (int) (getWidthForWeight(weights[i], totalWeight));
            //绘制矩形块之间的分割线
            lineX = lineX + width;
            if (lineX < progressX) {//给已经走过了的区域画上竖线
                canvas.drawLine(lineX, 0, lineX, getHeight(), linePaint);
            }
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
