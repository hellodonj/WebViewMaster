package com.lqwawa.apps.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lqwawa.apps.R;
import com.lqwawa.tools.DensityUtils;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/10/13 11:04
 * 描    述：文字描边
 * 修订历史：
 * ================================================
 */

public class StrokeTextView extends TextView {

    private TextView outlineTextView = null;
    int mStrokeWideth;
    ColorStateList mOuterColor;

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initAttrs(context, attrs);

        init();
    }


    private void initAttrs(Context context, AttributeSet attrs) {
        //获取自定义的XML属性名称
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        //获取对应的属性值

        mOuterColor = a.getColorStateList(R.styleable.StrokeTextView_outerColor);
        if (mOuterColor == null)
            mOuterColor = ColorStateList.valueOf(getResources().getColor(android.R.color.primary_text_light));
        mStrokeWideth = (int) a.getDimension(R.styleable.StrokeTextView_strokeWideth,  DensityUtils.dp2px(getContext(), 1));

        a.recycle();

        outlineTextView = new TextView(context, attrs);

    }

    public void init() {

        TextPaint paint = outlineTextView.getPaint();
        paint.setStrokeWidth(mStrokeWideth);// 描边宽度
        paint.setStyle(Paint.Style.STROKE);
        outlineTextView.setTextColor(mOuterColor);// 描边颜色
        outlineTextView.setGravity(getGravity());
    }

    @Override
    public void setLayoutParams (ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        outlineTextView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 设置轮廓文字
        CharSequence outlineText = outlineTextView.getText();
        if (outlineText == null || !outlineText.equals(this.getText())) {
            outlineTextView.setText(getText());
            postInvalidate();
        }
        outlineTextView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        outlineTextView.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        outlineTextView.draw(canvas);
        super.onDraw(canvas);
    }
}