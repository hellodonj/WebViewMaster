package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.apps.views.ContainsEmojiEditText;
import com.osastudio.common.utils.Constants;

public class InputBoxDialog extends ContactsDialog {

    public static final int TITLE_MAX_LEN = Constants.MAX_TITLE_LENGTH;

    public InputBoxDialog(Context context, String title,
                          String inputBoxDefaultText, String inputBoxHintText,
                          String leftButtonText, OnClickListener leftButtonClickListener,
                          String rightButtonText, OnClickListener rightButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title,
                inputBoxDefaultText, inputBoxHintText,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
    }

    public InputBoxDialog(Context context, int theme, String title,
                          String inputBoxDefaultText, String inputBoxHintText,
                          String leftButtonText, OnClickListener leftButtonClickListener,
                          String rightButtonText, OnClickListener rightButtonClickListener) {
        super(context, theme, title, R.layout.dialog_content_inputbox,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);

        ContainsEmojiEditText textView = (ContainsEmojiEditText) getContentView().findViewById(
                R.id.dialog_content_text);
        if (textView != null) {
            textView.setMaxlen(TITLE_MAX_LEN);
            textView.setText(inputBoxDefaultText);
            int length = TextUtils.isEmpty(inputBoxDefaultText) ? 0 : inputBoxDefaultText.length();
            if(length > 0 && length > TITLE_MAX_LEN) {
                length = TITLE_MAX_LEN;
            }
            textView.setSelection(length);
            textView.setHint(inputBoxHintText);
        }
    }

    public String getInputText() {
        EditText textView = (EditText) getContentView().findViewById(
                R.id.dialog_content_text);
        if (textView != null) {
            return textView.getText().toString();
        }
        return null;
    }

    public EditText getEditText() {
        ContainsEmojiEditText textView = (ContainsEmojiEditText) getContentView().findViewById(
                R.id.dialog_content_text);
        return textView;
    }

}
