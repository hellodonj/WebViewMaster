package com.galaxyschool.app.wawaschool.views.switchbutton;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;

/**
 * Generate thumb and background color state list use tintColor
 * Created by kyle on 15/11/4.
 */
public class ColorUtils {
	private static final int ENABLE_ATTR = android.R.attr.state_enabled;
	private static final int CHECKED_ATTR = android.R.attr.state_checked;
	private static final int PRESSED_ATTR = android.R.attr.state_pressed;

	public static ColorStateList generateThumbColorWithTintColor(final int tintColor) {
		int[][] states = new int[][]{
				{-ENABLE_ATTR, CHECKED_ATTR},
				{-ENABLE_ATTR},
				{PRESSED_ATTR, -CHECKED_ATTR},
				{PRESSED_ATTR, CHECKED_ATTR},
				{CHECKED_ATTR},
				{-CHECKED_ATTR}
		};

		int[] colors = new int[] {
				tintColor - 0xAA000000,
				0xFFBABABA,
				tintColor - 0x99000000,
				tintColor - 0x99000000,
				tintColor | 0xFF000000,
				0xFFEEEEEE
		};
		return new ColorStateList(states, colors);
	}

	public static ColorStateList generateBackColorWithTintColor(final int tintColor) {
		int[][] states = new int[][]{
				{-ENABLE_ATTR, CHECKED_ATTR},
				{-ENABLE_ATTR},
				{CHECKED_ATTR, PRESSED_ATTR},
				{-CHECKED_ATTR, PRESSED_ATTR},
				{CHECKED_ATTR},
				{-CHECKED_ATTR}
		};

		int[] colors = new int[] {
				tintColor - 0xE1000000,
				0x10000000,
				tintColor - 0xD0000000,
				0x20000000,
				tintColor - 0xD0000000,
				0x20000000
		};
		return new ColorStateList(states, colors);
	}

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
