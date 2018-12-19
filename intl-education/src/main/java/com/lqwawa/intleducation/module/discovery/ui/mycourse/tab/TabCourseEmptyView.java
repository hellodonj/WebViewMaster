package com.lqwawa.intleducation.module.discovery.ui.mycourse.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 我的课程空布局
 * @date 2018/05/03 14:18
 * @history v1.0
 * **********************************
 */
public class TabCourseEmptyView extends FrameLayout{

    // mLoading Text
    private TextView mLoadingText;
    // Action Button
    private Button mBtnSubmit;

    private CharSequence[] mTexts = new CharSequence[2];

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public TabCourseEmptyView(@NonNull Context context) {
        this(context,null);
    }

    public TabCourseEmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TabCourseEmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                attrs, R.styleable.TabCourseEmptyView, defStyle, 0);

        mTexts[0] = a.getText(R.styleable.TabCourseEmptyView_tab_empty_text_remind);
        mTexts[1] = a.getText(R.styleable.TabCourseEmptyView_tab_empty_text_action);

        a.recycle();


        final View view = mLayoutInflater.inflate(R.layout.holder_tab_course_empty_layout,this);
        mLoadingText = (TextView) view.findViewById(R.id.loading_text);
        mBtnSubmit = (Button) view.findViewById(R.id.btn_submit);

        mLoadingText.setText(mTexts[0]);
        mBtnSubmit.setText(mTexts[1]);
    }

    /**
     * 设置点击事件
     * @param actionText 动作文本
     * @param listener 事件监听
     */
    public void setSubmitListener(@NonNull String actionText,@Nullable OnClickListener listener){
        mBtnSubmit.setText(actionText);
        this.setSubmitListener(listener);
    }

    public void setSubmitListener(@Nullable OnClickListener listener){
        mBtnSubmit.setOnClickListener(listener);
    }


}

