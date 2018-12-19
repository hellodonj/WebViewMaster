package com.lqwawa.intleducation.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;

/**
 * @author cnbilzh
 * @desc 简单的占位控件, 实现了显示一个空的图片显示, 可以和MVP or MVVM配合显示没有数据，正在加载等状态
 */
public class EmptyView extends LinearLayout implements PlaceHolderView {
    private CourseEmptyView mEmptyLayout;
    private NetErrorView mNetView;
    private LoadingView mLoadingView;

    // 是否显示Loading
    private boolean mLoadingShow;
    private Drawable[] mDrawables = new Drawable[2];
    private CharSequence[] mTextIds = new CharSequence[3];

    private View[] mBindViews;

    public EmptyView(Context context) {
        super(context);
        init(null, 0);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        inflate(getContext(), R.layout.empty_layout, this);
        mEmptyLayout = (CourseEmptyView) findViewById(R.id.empty_layout);
        mNetView = (NetErrorView) findViewById(R.id.net_layout);
        mLoadingView = (LoadingView) findViewById(R.id.loading_layout);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.EmptyView, defStyle, 0);

        mDrawables[0] = a.getDrawable(R.styleable.EmptyView_empty_drawable);
        mDrawables[1] = a.getDrawable(R.styleable.EmptyView_error_drawable);
        mTextIds[0] = a.getText(R.styleable.EmptyView_empty_text);
        mTextIds[1] = a.getText(R.styleable.EmptyView_error_text);
        mTextIds[2] = a.getText(R.styleable.EmptyView_empty_loading_text);
        mLoadingShow = a.getBoolean(R.styleable.EmptyView_empty_loading_show, false);
        if (!EmptyUtil.isEmpty(mDrawables[0])) {
            mEmptyLayout.setEmptyDrawable(mDrawables[0]);
        }

        if (!EmptyUtil.isEmpty(mDrawables[1])) {
            mNetView.setNetErrorIcon(mDrawables[1]);
        }

        if (!EmptyUtil.isEmpty(mTextIds[1])) {
            mNetView.setNetErrorText(mTextIds[1]);
        }

        if (!EmptyUtil.isEmpty(mTextIds[2])) {
            mLoadingView.setLoadingText(mTextIds[2]);
        }
        a.recycle();

        // 默认全部都不显示
        setVisibility(View.GONE);
    }

    /**
     * 绑定一系列数据显示的布局
     * 当前布局隐藏时（有数据时）自动显示绑定的数据布局
     * 而当数据加载时，自动显示Loading，并隐藏数据布局
     *
     * @param views 数据显示的布局
     */
    public void bind(View... views) {
        this.mBindViews = views;
    }

    /**
     * 更改绑定布局的显示状态
     *
     * @param visible 显示的状态
     */
    private void changeBindViewVisibility(int visible) {
        final View[] views = mBindViews;
        if (views == null || views.length == 0)
            return;

        for (View view : views) {
            view.setVisibility(visible);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerEmpty() {
        mLoadingView.setVisibility(GONE);
        mNetView.setVisibility(GONE);
        mEmptyLayout.setVisibility(VISIBLE);
        setVisibility(VISIBLE);
        changeBindViewVisibility(GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerNetError() {
        mLoadingView.setVisibility(GONE);
        mNetView.setVisibility(VISIBLE);
        mEmptyLayout.setVisibility(GONE);
        setVisibility(VISIBLE);
        changeBindViewVisibility(GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerError(@StringRes int strRes) {
        UIUtil.showToastSafe(strRes);
        mNetView.setVisibility(VISIBLE);
        mLoadingView.setVisibility(GONE);
        mEmptyLayout.setVisibility(GONE);
        setVisibility(VISIBLE);
        changeBindViewVisibility(GONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerLoading() {
        mLoadingView.setVisibility(VISIBLE);
        mNetView.setVisibility(GONE);
        mEmptyLayout.setVisibility(GONE);
        setVisibility(VISIBLE);
        changeBindViewVisibility(GONE);
    }

    @Override
    public boolean isShowLoading() {
        return mLoadingShow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerOk() {
        setVisibility(GONE);
        changeBindViewVisibility(VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void triggerOkOrEmpty(boolean isOk) {
        if (isOk)
            triggerOk();
        else
            triggerEmpty();
    }

    @Override
    public void setOnReloadDataListener(NetErrorView.OnReloadDataListener listener) {
        if(!EmptyUtil.isEmpty(mNetView)){
            mNetView.setOnReloadDataListener(listener);
        }
    }
}