package com.lqwawa.intleducation.base;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.utils.ActivityUtils;
import com.lqwawa.intleducation.common.utils.UIUtil;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空布局
 * @date 2018/05/03 14:18
 * @history v1.0
 * **********************************
 */
public class CourseEmptyView extends FrameLayout{

    // 联系客服
    private TextView mTvCall;
    // empty view
    private ImageView mEmptyView;
    // mRemind Text
    private TextView mRemindText;
    // mLoading Text
    private TextView mLoadingText;
    // mCall Layout
    private LinearLayout mCallLayout;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    // 是否显示客服热线
    private boolean mShowCall;
    private boolean mLoadingTextVisible;
    private boolean mRemindTextVisible;
    private Drawable mEmptyDrawable;
    private CharSequence[] mTexts = new CharSequence[2];

    public CourseEmptyView(@NonNull Context context) {
        this(context,null);
    }

    public CourseEmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CourseEmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView(attrs,defStyleAttr);
    }

    /**
     * 初始化布局
     */
    private void initView(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CourseEmptyView, defStyle, 0);

        mEmptyDrawable = a.getDrawable(R.styleable.CourseEmptyView_empty_icon_drawable);
        mTexts[0] = a.getText(R.styleable.CourseEmptyView_empty_text_remind);
        mRemindTextVisible = a.getBoolean(R.styleable.CourseEmptyView_empty_text_remind_visible,false);
        mTexts[1] = a.getText(R.styleable.CourseEmptyView_empty_text_loading);
        mLoadingTextVisible = a.getBoolean(R.styleable.CourseEmptyView_empty_text_loading_visible,false);
        mShowCall = a.getBoolean(R.styleable.CourseEmptyView_call_show,false);

        a.recycle();


        final View view = mLayoutInflater.inflate(R.layout.holder_course_empty_layout,this);
        mEmptyView = (ImageView) view.findViewById(R.id.empty_view);
        mRemindText = (TextView) view.findViewById(R.id.remind_text);
        mLoadingText = (TextView) view.findViewById(R.id.loading_text);
        mCallLayout = (LinearLayout) view.findViewById(R.id.call_layout);
        mTvCall = (TextView) view.findViewById(R.id.tv_call);

        mLoadingText.setVisibility(mLoadingTextVisible ? View.VISIBLE : View.GONE);
        mRemindText.setVisibility(mRemindTextVisible ? View.VISIBLE : View.GONE);

        mEmptyView.setImageDrawable(mEmptyDrawable);
        mRemindText.setText(mTexts[0]);
        mLoadingText.setText(mTexts[1]);
        mCallLayout.setVisibility(mShowCall ? View.VISIBLE : View.GONE);

        mTvCall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拨打电话
                ActivityUtils.gotoTelephone(mContext);
            }
        });
    }

    /**
     * 设置空图片Drawable
     * @param drawable drawable对象
     */
    public void setEmptyDrawable(@NonNull Drawable drawable){
        mEmptyView.setImageDrawable(drawable);
    }

    /**
     * 设置空图片Id
     * @param resourceId 图片资源Id
     */
    public void setEmptyResource(@DrawableRes int resourceId){
        mEmptyView.setImageResource(resourceId);
    }


}
