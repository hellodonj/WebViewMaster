package com.lqwawa.intleducation.base.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.StringUtil;

/**
 * @desc 自定义组合View
 * @author medici
 */
public class PriceArrowView extends FrameLayout{
    // 闲置
    public static final int STATE_IDLE = 0;
    // 升序
    public static final int STATE_UP = 1;
    // 降序
    public static final int STATE_DOWN = 2;

    private int currentState;

    private CheckedTextView mTabTitle;
    private ImageView mUpView,mDownView;
    private View mRootView;

    public PriceArrowView(@NonNull Context context) {
        this(context,null);
    }

    public PriceArrowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PriceArrowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.tab_price_updown_layout,this);
        mTabTitle = (CheckedTextView) findViewById(R.id.tv_title);
        mUpView = (ImageView) findViewById(R.id.iv_up);
        mDownView = (ImageView) findViewById(R.id.iv_down);
        mUpView.setActivated(false);
        mDownView.setActivated(false);
        currentState = STATE_IDLE;
    }

    /**
     * 返回布局
     * @return mRootView
     */
    public View getRootView(){
        return mRootView;
    }

    /**
     * 设置Title
     * @param tabTitle 标题
     */
    public void setTabTitle(@NonNull String tabTitle){
        StringUtil.fillSafeTextView(mTabTitle,tabTitle);
    }

    /**
     * 变换状态
     */
    public int triggerSwitch(){
        if(currentState == STATE_IDLE){
            triggerUp();
            currentState = STATE_UP;
        }else if(currentState == STATE_UP){
            triggerDown();
            currentState = STATE_DOWN;
        }else if(currentState == STATE_DOWN){
            triggerUp();
            currentState = STATE_UP;
        }

        return currentState;
    }

    /**
     * 升序
     */
    private void triggerUp(){
        mUpView.setActivated(true);
        mDownView.setActivated(false);
        mTabTitle.setChecked(true);
    }

    /**
     * 降序
     */
    private void triggerDown(){
        mUpView.setActivated(false);
        mDownView.setActivated(true);
        mTabTitle.setChecked(true);
    }

    /**
     * 重置操作
     */
    public void reset(){
        currentState = STATE_IDLE;
        mUpView.setActivated(false);
        mDownView.setActivated(false);
        mTabTitle.setChecked(false);
    }
}
