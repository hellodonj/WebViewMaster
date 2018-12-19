package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

/**
 * Created by KnIghT on 16-6-16.
 */
public class ColorUtil {
    public  static void spannableGreenColor(Activity activity,TextView textView ,int start,int end,int start1,int end1){
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color.text_green));
        ForegroundColorSpan greenSpan1 = new ForegroundColorSpan(activity.getResources().getColor(R.color.text_green));
        builder.setSpan(greenSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(greenSpan1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }
    public  static void spannableGreenColor(Activity activity,TextView textView ,int start,int end){
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(activity.getResources().getColor(R.color.text_green));
        builder.setSpan(greenSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }
}
