package com.lqwawa.libs.mediapaper;

 
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;  
import android.widget.LinearLayout;
  
public class KeyboardListener extends LinearLayout { 
    private static final int DISTANCE_CHANGE_OFFSET = 80;  
    public static final byte KEYBOARD_STATE_SHOW = -3;  
    public static final byte KEYBOARD_STATE_HIDE = -2;  
    public static final byte KEYBOARD_STATE_INIT = -1; 
    private boolean mHasInit = false;  
    private boolean mHasKeyboard = false;  
    private int mHeight;  
    Rect mRect;
    

	private IOnKeyboardStateChangedListener onKeyboardStateChangedListener;  
      
    public KeyboardListener(Context context) {  
        super(context);  
    }  
    public KeyboardListener(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
      
    public KeyboardListener(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
      
    public void setOnKeyboardStateChangedListener(IOnKeyboardStateChangedListener onKeyboardStateChangedListener) {  
        this.onKeyboardStateChangedListener = onKeyboardStateChangedListener;  
    }  
      
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        super.onLayout(changed, l, t, r, b);  
        if(!mHasInit) {  
            mHasInit = true; 
            mHasKeyboard = false;
            mRect = new Rect();
            mHeight = b;  
            if(onKeyboardStateChangedListener != null) {  
                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_INIT);  
            }  
        } else {  
            mHeight = mHeight < b ? b : mHeight;  
        }  
//          
//        if(mHasInit && mHeight > b) {  
//            mHasKeyboard = true;  
//            if(onKeyboardStateChangedListener != null) {  
//                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_SHOW);  
//            }  
//        }  
//        if(mHasInit && mHasKeyboard && mHeight == b) {  
//            mHasKeyboard = false;  
//            if(onKeyboardStateChangedListener != null) {  
//                onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_HIDE);  
//            }  
//        }   
        //added by rmpan  judge whether keyboard show or hide 
        this.getWindowVisibleDisplayFrame(mRect);
        int rootInvisibleHeight = this.getRootView().getHeight() - mRect.bottom;
         
        if (rootInvisibleHeight <= DISTANCE_CHANGE_OFFSET) { 
        	if(mHasInit && mHasKeyboard) {  
                mHasKeyboard = false;  
                if(onKeyboardStateChangedListener != null) {   
    	            onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_HIDE);  
    	        } 
             } 
        } else {
        	if(mHasInit && !mHasKeyboard  ) {  
                mHasKeyboard = true;  
                if(onKeyboardStateChangedListener != null) {  
	        		onKeyboardStateChangedListener.onKeyboardStateChanged(KEYBOARD_STATE_SHOW);  
	            } 
            } 
        } 
    }  
      
    public interface IOnKeyboardStateChangedListener {  
        public void onKeyboardStateChanged(int state);  
    }  
}  
