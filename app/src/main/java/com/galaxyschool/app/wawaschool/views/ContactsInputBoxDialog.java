package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;
import com.galaxyschool.app.wawaschool.R;

public class ContactsInputBoxDialog extends ContactsDialog {

    public ContactsInputBoxDialog(Context context, String title,
            String inputBoxDefaultText, String inputBoxHintText,
            String leftButtonText, OnClickListener leftButtonClickListener,
            String rightButtonText, OnClickListener rightButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title,
                inputBoxDefaultText, inputBoxHintText,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
    }

    public ContactsInputBoxDialog(Context context, int theme, String title,
            String inputBoxDefaultText, String inputBoxHintText,
            String leftButtonText, OnClickListener leftButtonClickListener,
            String rightButtonText, OnClickListener rightButtonClickListener) {
        super(context, theme, title, R.layout.contacts_dialog_content_inputbox,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);

        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        if (textView != null) {
            textView.setText(inputBoxDefaultText);
            textView.setSelection(TextUtils.isEmpty(inputBoxDefaultText) ?
                    0 : inputBoxDefaultText.length());
            textView.setHint(inputBoxHintText);
        }
    }

    public String getInputText() {
        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        if (textView != null) {
            return textView.getText().toString();
        }
        return null;
    }
    public void setInputLimitNumber(int length){
        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        if (textView!=null){
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        }
    }
}
