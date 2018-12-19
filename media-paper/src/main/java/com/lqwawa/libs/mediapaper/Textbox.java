package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.lqwawa.apps.views.ContainsEmojiEditText;

/**
 * Created by pp on 15/12/15.
 */
public class Textbox extends BaseChild {
    Context mContext = null;
    ContainsEmojiEditText mEditText = null;

    public Textbox(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.myedit_view, this);
//        setBackgroundResource(R.drawable.text_frame);
        mEditText = (ContainsEmojiEditText) findViewById(R.id.edittext);

        setDeleteMode(false);
    }

    @Override
    public void setDeleteMode(boolean bDelMode) {
        mbDelMode = bDelMode;
        View deleteBtn = findViewById(R.id.dele);
        if (deleteBtn != null) {
            if (bDelMode) {
                deleteBtn.setVisibility(View.VISIBLE);
//                mEditText.setBackgroundResource(R.drawable.image_frame_big);
            } else {
                deleteBtn.setVisibility(View.GONE);
//                mEditText.setBackgroundResource(R.drawable.image_frame_big);
            }
        }
    }

    public void setText(String text) {
        if (mEditText != null) {
            mEditText.setText(text);
        }
    }

    public void setTextChangeListener(ContainsEmojiEditText.OnTextChangeListener listener) {
        if (mEditText != null) {
            mEditText.setOnTextChangeListener(listener);
        }
    }

    public void setEditMode(boolean bEdit) {
        if (mEditText != null) {
            if (bEdit) {
                mEditText.setEnabled(true);
            } else {
                mEditText.setEnabled(false);
            }
        }

    }

    public void getFocus() {
        if (mEditText != null) {
            mEditText.setFocusableInTouchMode(true);
            mEditText.setFocusable(true);
            mEditText.requestFocus();
            showSoftInput(mEditText);
        }
    }

    private void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
    }
}
