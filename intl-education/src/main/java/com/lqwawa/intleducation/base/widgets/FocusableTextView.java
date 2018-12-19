package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 跑马灯效果的TextView
 * @date 2018/04/09 15:37
 * @history v1.0
 * **********************************
 */
public class FocusableTextView extends TextView{
    public FocusableTextView(Context context) {
        super(context);
    }

    public FocusableTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
