package com.lqwawa.intleducation.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;

import net.qiujuer.genius.ui.widget.Loading;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 显示加载状态的View
 * @date 2018/05/16 10:51
 * @history v1.0
 * **********************************
 */
public class LoadingView extends FrameLayout{

    private Loading mLoading;
    private TextView mLoadingText;

    public LoadingView(@NonNull Context context) {
        this(context,null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs,defStyleAttr);
    }

    private void initView(AttributeSet attrs, int defStyleAttr) {
        inflate(getContext(), R.layout.holder_loading_layout,this);
        mLoading = (Loading) findViewById(R.id.loading);
        mLoadingText = (TextView) findViewById(R.id.txt_empty);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LoadingView, defStyleAttr, 0);

        CharSequence mText = a.getText(R.styleable.LoadingView_loading_loading_text);
        setLoadingText(mText);

        a.recycle();
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(visibility == View.VISIBLE){
            mLoading.start();
        }else{
            mLoading.stop();
        }
    }

    /**
     * 设置Loading文本
     * @param loadingText 文本
     */
    public void setLoadingText(@NonNull CharSequence loadingText){
        mLoadingText.setText(loadingText);
    }
}
