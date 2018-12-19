package com.galaxyschool.app.wawaschool.views.sortlistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import com.galaxyschool.app.wawaschool.R;

public class ClearEditText extends EditText implements
        OnFocusChangeListener, TextWatcher {

    private Drawable clearDrawable;
    private OnClearClickListener clearClickListener;
 
    public ClearEditText(Context context) {
    	this(context, null); 
    } 
 
    public ClearEditText(Context context, AttributeSet attrs) {
    	this(context, attrs, android.R.attr.editTextStyle);
    } 
    
    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() { 
    	clearDrawable = getCompoundDrawables()[2];
        if (clearDrawable == null) {
        	clearDrawable = getResources()
                    .getDrawable(R.drawable.search_bar_clear);
        } 
        clearDrawable.setBounds(0, 0, clearDrawable.getIntrinsicWidth(), clearDrawable.getIntrinsicHeight());
        setClearIconVisible(false); 
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
    }

    public void setOnClearClickListener(OnClearClickListener listener) {
        this.clearClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) {
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - clearDrawable.getIntrinsicWidth())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                    this.setText("");
                    if (this.clearClickListener != null) {
                        this.clearClickListener.onClearClick();
                    }
                } 
            } 
        } 
 
        return super.onTouchEvent(event); 
    } 
 
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
    } 
 
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? clearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 

    @Override
    public void onTextChanged(CharSequence s, int start, int count,
            int after) { 
        setClearIconVisible(s.length() > 0); 
    } 
 
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) { 
         
    } 
 
    @Override
    public void afterTextChanged(Editable s) {
         
    } 

    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }

    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }

    public interface OnClearClickListener {
        public void onClearClick();
    }

}
