package com.lqwawa.apps.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lqwawa.apps.R;
import com.lqwawa.tools.DensityUtils;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/10/13 17:27
  * 描    述：画小圆点,方点
  * 修订历史：
  * ================================================
  */

public class DrawPointView extends View {

    private int mPointWideth;
    private int mPointColor;
    private int mPointType;

    public DrawPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        //获取自定义的XML属性名称
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawPointView);
        //获取对应的属性值
        mPointColor = a.getColor(R.styleable.DrawPointView_pointColor,getResources().getColor(android.R.color.primary_text_light));
        mPointWideth = (int) a.getDimension(R.styleable.DrawPointView_pointWideth,  DensityUtils.dp2px(getContext(), 3));
        mPointType = a.getInt(R.styleable.DrawPointView_pointShape, 0);
        a.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        使用 canvas.drawPoint() 方法画点
//        一个圆点，一个方点
//        圆点和方点的切换使用 paint.setStrokeCap(cap)：`ROUND` 是圆点，`BUTT` 或 `SQUARE` 是方点

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(mPointType == 0 ? Paint.Cap.SQUARE : Paint.Cap.ROUND);
        paint.setStrokeWidth(mPointWideth);
        paint.setColor(mPointColor);
        canvas.drawPoint(mPointWideth/2,mPointWideth/2,paint);

    }

    public void setPointWideth(int pointWideth) {
        mPointWideth = pointWideth;
        postInvalidate();

    }

    public void setPointColor(int pointColor) {
        mPointColor = pointColor;
        postInvalidate();

    }

    public void setPointType(int pointType) {
        mPointType = pointType;
        postInvalidate();

    }
}
