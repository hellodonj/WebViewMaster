package com.lqwawa.intleducation.base.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;

import com.lqwawa.intleducation.base.widgets.TouchableSpan;

/**
 * Created by XChen on 2016/12/15.
 * email:man0fchina@foxmail.com
 */

public class SpannableUtils {
    public static SpannableString createClickSpannable(
            String content,
            int normalTextColor,
            int pressedTextColor,
            int pressedBackgroundColor,
            final OnClickListener listener) {
        SpannableString spanContent = new SpannableString(content);
        TouchableSpan clickSpanContent = new TouchableSpan(
                normalTextColor,
                pressedTextColor,
                pressedBackgroundColor) {
            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    listener.OnClick(widget);
                }
            }
        };
        spanContent.setSpan(clickSpanContent, 0,
                content.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spanContent;
    }


    public interface OnClickListener {
        void OnClick(View widget);
    }
}
