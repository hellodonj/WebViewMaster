package com.lqwawa.intleducation.base.widgets;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;


public class TopBar extends LinearLayout {
    public static int HEIGHT_WITHOUT_SHADW = 48;
    private TextView titleTv;
    // 按钮顺序为：属性定义顺序
    private ImageButton leftFunctionImage1;
    private TextView leftFunctionText1;
    private ImageButton leftFunctionImage2;
    private TextView leftFunctionText2;
    private ImageButton leftFunctionImage3;
    private ImageView leftAvatarImage;

    private ImageButton rightFunctionImage1;
    private TextView rightFunctionText1;
    private ImageButton rightFunctionImage2;
    private TextView rightFunctionText2;
    private ImageButton rightFunctionImage3;

    private ImageButton rightFunctionImage4;
    private ImageButton rightFunctionImage5;

    private Activity activity;

    public TopBar(Context context) {
        super(context);
        init(context);
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setMyBackground(int id) {
        setBackgroundColor(getResources().getColor(id));
    }

    public void setTitleColor(int id) {
        titleTv.setTextColor(getResources().getColor(id));
    }

    public void showBottomSplitView(boolean show){
        if(findViewById(R.id.bottom_split_view) != null){
            findViewById(R.id.bottom_split_view).setVisibility(show ? VISIBLE : GONE);
        }
    }

    private void init(Context context) {
        this.activity = (Activity) context;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widgets_top_bar, this);
        setMyBackground(R.color.com_bg_white);
        titleTv = (TextView) findViewById(R.id.title_tv);
        leftFunctionImage1 = (ImageButton) findViewById(R.id.left_function1_image);
        leftFunctionText1 = (TextView) findViewById(R.id.left_function1_text);
        leftFunctionImage2 = (ImageButton) findViewById(R.id.left_function2_image);
        leftFunctionText2 = (TextView) findViewById(R.id.left_function2_text);
        leftFunctionImage3 = (ImageButton) findViewById(R.id.left_function3_image);
        leftAvatarImage = (ImageView) findViewById(R.id.left_avatar_image);
        rightFunctionImage1 = (ImageButton) findViewById(R.id.right_function1_image);
        rightFunctionText1 = (TextView) findViewById(R.id.right_function1_text);
        rightFunctionImage2 = (ImageButton) findViewById(R.id.right_function2_image);
        rightFunctionText2 = (TextView) findViewById(R.id.right_function2_text);
        rightFunctionImage3 = (ImageButton) findViewById(R.id.right_function3_image);
        rightFunctionImage4 = (ImageButton) findViewById(R.id.right_function4_image);
        rightFunctionImage5 = (ImageButton) findViewById(R.id.right_function5_image);

        leftFunctionImage1.setVisibility(View.GONE);
        leftFunctionText1.setVisibility(View.GONE);
        leftFunctionImage2.setVisibility(View.GONE);
        leftFunctionText2.setVisibility(View.GONE);
        leftFunctionImage3.setVisibility(View.GONE);
        leftAvatarImage.setVisibility(View.GONE);
        rightFunctionImage1.setVisibility(View.GONE);
        rightFunctionText1.setVisibility(View.GONE);
        rightFunctionImage2.setVisibility(View.GONE);
        rightFunctionText2.setVisibility(View.GONE);
        rightFunctionImage3.setVisibility(View.GONE);
        rightFunctionImage4.setVisibility(View.GONE);
        rightFunctionImage5.setVisibility(View.GONE);
//        findViewById(R.id.top_bar_root).setPadding(0,
//                DisplayUtil.getStatusBarHeight(activity), 0, 0);
    }
    public void setTitleVisibility(int visibility){
        titleTv.setVisibility(visibility);
    }
    public void setTitle(CharSequence title) {
        titleTv.setText(title);
    }

    public void setTitle(int resId) {
        titleTv.setText(resId);
    }

    public void setTitleWide(int width) {
        titleTv.setWidth(width);
    }

    /**
     * 设置返回按钮-左边第一个图片按钮
     *
     * @param back
     */
    public void setBack(boolean back) {
        if (back) {
            setLeftFunctionImage1(R.drawable.ic_back_green, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        } else {
            setLeftFunctionImage1(R.drawable.ic_back_green, null);
        }
    }

    public void setBack(boolean back, int iconResId) {
        if (back) {
            setLeftFunctionImage1(iconResId, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        } else {
            setLeftFunctionImage1(iconResId, null);
        }
    }

    public void setTranslationBackground(boolean translationBackground){
        if (translationBackground){
            setBackgroundColor(getResources().getColor(R.color.translation));
            leftFunctionImage1.setBackground(activity.getResources().getDrawable(
                    R.drawable.com_circle_black_trans_bg_selecter));
            rightFunctionImage1.setBackground(activity.getResources().getDrawable(
                    R.drawable.com_circle_black_trans_bg_selecter));
            rightFunctionText1.setBackground(activity.getResources().getDrawable(
                    R.drawable.com_green_bar_bg));
            rightFunctionText1.setTextColor(activity.getResources().getColor(
                    R.color.com_text_white));
            showBottomSplitView(false);
        }else{
            setBackgroundColor(getResources().getColor(R.color.com_bg_black));
            leftFunctionImage1.setBackgroundColor(activity.getResources().getColor(
                    R.color.translation));
            rightFunctionImage1.setBackgroundColor(activity.getResources().getColor(
                    R.color.translation));
            rightFunctionText1.setBackgroundColor(activity.getResources().getColor(
                    R.color.translation));
            showBottomSplitView(true);
        }
    }

    public void setLeftFunctionImage1(int iconResId, OnClickListener operate) {
        if (null == operate) {
            leftFunctionImage1.setVisibility(View.GONE);
            return;
        }
        leftFunctionImage1.setVisibility(View.VISIBLE);
        leftFunctionImage1.setImageResource(iconResId);
        leftFunctionImage1.setOnClickListener(operate);
    }

    public void setLeftFunctionImage2(int iconResId, OnClickListener operate) {
        if (null == operate) {
            leftFunctionImage2.setVisibility(View.GONE);
            return;
        }
        leftFunctionImage2.setVisibility(View.VISIBLE);
        leftFunctionImage2.setImageResource(iconResId);
        leftFunctionImage2.setOnClickListener(operate);
    }

    public void setLeftFunctionImage3(int iconResId, OnClickListener operate) {
        if (null == operate) {
            leftFunctionImage3.setVisibility(View.GONE);
            return;
        }
        leftFunctionImage3.setVisibility(View.VISIBLE);
        leftFunctionImage3.setImageResource(iconResId);
        leftFunctionImage3.setOnClickListener(operate);
    }

    public void setLeftAvatarImage(int iconResId, OnClickListener operate) {
        if (null == operate) {
            leftAvatarImage.setVisibility(View.GONE);
            return;
        }
        leftAvatarImage.setVisibility(View.VISIBLE);
        leftAvatarImage.setImageResource(iconResId);
        leftAvatarImage.setOnClickListener(operate);
    }

    public void setRightFunctionImage1(int iconResId) {
        rightFunctionImage1.setVisibility(View.VISIBLE);
        rightFunctionImage1.setImageResource(iconResId);
    }

    public void setRightFunctionImage1(int iconResId, OnClickListener operate) {
        if (null == operate) {
            rightFunctionImage1.setVisibility(View.GONE);
            return;
        }
        rightFunctionImage1.setVisibility(View.VISIBLE);
        rightFunctionImage1.setImageResource(iconResId);
        rightFunctionImage1.setOnClickListener(operate);
    }

    public void setRightFunctionImage2(int iconResId, OnClickListener operate) {
        if (null == operate) {
            rightFunctionImage2.setVisibility(View.GONE);
            return;
        }
        rightFunctionImage2.setVisibility(View.VISIBLE);
        rightFunctionImage2.setImageResource(iconResId);
        rightFunctionImage2.setOnClickListener(operate);
    }

    public void setRightFunctionImage3(int iconResId, OnClickListener operate) {
        if (null == operate) {
            rightFunctionImage3.setVisibility(View.GONE);
            return;
        }
        rightFunctionImage3.setVisibility(View.VISIBLE);
        rightFunctionImage3.setImageResource(iconResId);
        rightFunctionImage3.setOnClickListener(operate);
    }

    public void setRightFunctionImage4(int iconResId, OnClickListener operate) {
        if (null == operate) {
            rightFunctionImage4.setVisibility(View.GONE);
            return;
        }
        rightFunctionImage4.setVisibility(View.VISIBLE);
        rightFunctionImage4.setImageResource(iconResId);
        rightFunctionImage4.setOnClickListener(operate);
    }

    public void setRightFunctionImage5(int iconResId, OnClickListener operate) {
        if (null == operate) {
            rightFunctionImage5.setVisibility(View.GONE);
            return;
        }
        rightFunctionImage5.setVisibility(View.VISIBLE);
        rightFunctionImage5.setImageResource(iconResId);
        rightFunctionImage5.setOnClickListener(operate);
    }

    public void setLeftFunctionText1(int resId, OnClickListener operate) {
        if (null == operate) {
            leftFunctionText1.setVisibility(View.GONE);
            return;
        }
        leftFunctionText1.setVisibility(View.VISIBLE);
        leftFunctionText1.setText(resId);
        leftFunctionText1.setOnClickListener(operate);
    }

    public void setLeftFunctionText1(CharSequence text, OnClickListener operate) {
        if (null == operate) {
            leftFunctionText1.setVisibility(View.GONE);
            return;
        }
        leftFunctionText1.setVisibility(View.VISIBLE);
        leftFunctionText1.setText(text);
        leftFunctionText1.setOnClickListener(operate);
    }

    public void setLeftFunctionText2(int resId, OnClickListener operate) {
        if (null == operate) {
            leftFunctionText2.setVisibility(View.GONE);
            return;
        }
        leftFunctionText2.setVisibility(View.VISIBLE);
        leftFunctionText2.setText(resId);
        leftFunctionText2.setOnClickListener(operate);
    }

    public void setLeftFunctionText2(CharSequence text, OnClickListener operate) {
        if (null == operate) {
            leftFunctionText2.setVisibility(View.GONE);
            return;
        }
        leftFunctionText2.setVisibility(View.VISIBLE);
        leftFunctionText2.setText(text);
        leftFunctionText2.setOnClickListener(operate);
    }

    public void setRightFunctionText1(int resId, OnClickListener operate) {
        if (null == operate) {
            rightFunctionText1.setVisibility(View.GONE);
            return;
        }
        rightFunctionText1.setVisibility(View.VISIBLE);
        rightFunctionText1.setText(resId);
        rightFunctionText1.setOnClickListener(operate);
    }

    public void setRightFunctionText1TextColor(int color){
        rightFunctionText1.setTextColor(color);
    }

    public void setRightFunctionText1TextColor(ColorStateList color){
        rightFunctionText1.setTextColor(color);
    }

    public void setRightFunctionText1Enabled(boolean enabled){
        rightFunctionText1.setEnabled(enabled);
    }

    public void setRightFunctionText1(CharSequence text, OnClickListener operate) {
        if (null == operate) {
            rightFunctionText1.setVisibility(View.GONE);
            return;
        }
        rightFunctionText1.setVisibility(View.VISIBLE);
        rightFunctionText1.setText(text);
        rightFunctionText1.setOnClickListener(operate);
    }

    public void setRightFunctionText2(int resId, OnClickListener operate) {
        if (null == operate) {
            rightFunctionText2.setVisibility(View.GONE);
            return;
        }
        rightFunctionText2.setVisibility(View.VISIBLE);
        rightFunctionText2.setText(resId);
        rightFunctionText2.setOnClickListener(operate);
    }

    public void setRightFunctionText2(CharSequence text, OnClickListener operate) {
        if (null == operate) {
            rightFunctionText2.setVisibility(View.GONE);
            return;
        }
        rightFunctionText2.setVisibility(View.VISIBLE);
        rightFunctionText2.setText(text);
        rightFunctionText2.setOnClickListener(operate);
    }

    public TextView getRightFunctionText2() {
        return rightFunctionText2;
    }

    @Override
    public void setBackgroundColor(int color) {
        if(findViewById(R.id.top_bar_root) != null){
            findViewById(R.id.top_bar_root).setBackgroundColor(color);
        }
    }

    public void setTitleContentView(View view){
        if(findViewById(R.id.title_content) != null && view != null){
            ((LinearLayout)findViewById(R.id.title_content)).addView(view);
            this.titleTv.setVisibility(GONE);
        }
    }
    public void setTitleContentView(View view, int rightPadding){
        if(findViewById(R.id.title_content) != null && view != null){
            findViewById(R.id.title_content).setPadding(DisplayUtil
                    .dip2px(activity, 40), 0, rightPadding == 0 ?
                    DisplayUtil.dip2px(activity, 5)
                    : rightPadding * DisplayUtil.dip2px(activity, 40), 0);
            ((LinearLayout)findViewById(R.id.title_content)).addView(view);
            this.titleTv.setVisibility(GONE);
        }
    }
}

