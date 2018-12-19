package com.lqwawa.intleducation.base.widgets;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * 作者：XChen on 2016/4/25 15:56
 * 邮箱：man0fchina@foxmail.com
 * 在TextView中添加变色文字并可点击且不带下划线
 */
public abstract class TouchableSpan extends ClickableSpan {
    private boolean mIsPressed;
    private int mPressedBackgroundColor;
    private int mNormalTextColor;
    private int mPressedTextColor;

    public abstract void onClick(View widget);


    public TouchableSpan(int normalTextColor, int pressedTextColor, int pressedBackgroundColor) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;
    }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
        ds.bgColor = mIsPressed ? mPressedBackgroundColor : 0x00000000;
        ds.setUnderlineText(false);
    }
}