package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class MyEditText extends EditText {
	private CharSequence mText;
	private MovementMethod mMovement;
	private long mShowCursor;
	private boolean mEatTouchRelease = false;
	private Context mContext;
	
	private boolean mFlagEnd = true;

	public MyEditText(Context context) {
		this(context, null);
	}

	public MyEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);//com.android.internal.R.attr.editTextStyle);
	}

	public MyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
      mFlagEnd = true;
	}

	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();

		final boolean superResult = super.onTouchEvent(event);
		/*pp add for fix bug of text select when copy 20111104*/
	   int start=getSelectionStart();   
	   int end=getSelectionEnd();   
	   if (end != start) {
	      return superResult;
	   }
		/*pp end*/

		if (superResult) {
			mText = super.getText();
			mMovement = super.getMovementMethod();

			boolean handled = false;

			if (mMovement != null) {
				handled |= mMovement.onTouchEvent(this, (Spannable) mText, event);
			}
			
			if ((mMovement != null || onCheckIsTextEditor())
					&& mText instanceof Spannable) {

			}
			
			if (mText instanceof Editable && onCheckIsTextEditor()) {
				if (action == MotionEvent.ACTION_UP && isFocused()) {

					
					int newSelStart = Selection.getSelectionStart(mText);
					int newSelEnd = Selection.getSelectionEnd(mText);

					Selection
							.setSelection((Spannable) mText, newSelStart, newSelEnd);
				}
			}

			if (handled) {
				return true;
			}
		}
		
		Layout layout = getLayout();
		int line = 0 , off = 0, curOff = 0;
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
//			line = layout.getLineForVertical(getScrollY() + (int) event.getY());
//			off = layout.getOffsetForHorizontal(line, (int) event.getX());
//			Selection.setSelection(getEditableText(), off);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			line = layout.getLineForVertical(getScrollY() + (int) event.getY());
			curOff = layout.getOffsetForHorizontal(line, (int) event.getX());
			Selection.setSelection(getEditableText(), curOff, curOff);

			break;
		}
		
		return superResult;

	}
	
	public boolean ismFlagEnd() {
		return mFlagEnd;
	}

	public void setmFlagEnd(boolean mFlagEnd) {
		this.mFlagEnd = mFlagEnd;
	}

	public boolean performLongClick() {
		if (super.performLongClick()) {
			mEatTouchRelease = true;
			return true;
		}

		return false;
	}
	public void mySetText(CharSequence text) {
		this.setText(text);
//      PaperUtils.textViewSetTextAndEmotion(mContext, this, text);
   }
	
   public boolean onTextContextMenuItem(int id) {
      boolean rtn = super.onTextContextMenuItem(id);
      if (id == android.R.id.paste) {
         int selection = getSelectionStart();
         mySetText(getText());
         setSelection(selection);
      }
      return rtn;
   }

}
