package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageTextButton extends LinearLayout {

    private Context context;
    private ImageView imageView;
    private TextView textView;

    public ImageTextButton(Context context) {
        this(context, null);
    }

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews();
    }

    private void initViews() {
        imageView = new ImageView(context);
        imageView.setPadding(0, 0, 0, 0);
        addView(imageView);

        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 10, 0, 0);
        addView(textView);

        setClickable(true);
        setFocusable(true);
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(Color.TRANSPARENT);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setText(String text) {
        if(textView != null) {
            textView.setText(text);
        }
    }

    public void setTextColor(int color) {
        if(textView != null) {
            textView.setTextColor(color);
        }
    }

    public void setTextSize(int unit, float size) {
        if(textView != null) {
            textView.setTextSize(unit, size);
        }
    }

    public void setImageResource(int resId) {
        if(imageView != null) {
            imageView.setImageResource(resId);
        }
    }
}
