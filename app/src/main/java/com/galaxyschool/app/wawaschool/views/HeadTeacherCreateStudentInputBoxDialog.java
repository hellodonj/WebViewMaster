package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;

import com.galaxyschool.app.wawaschool.R;

public class HeadTeacherCreateStudentInputBoxDialog extends StudentContactsDialog {
    private EditText userNameEditText;
    private EditText realNameEditText;

    public HeadTeacherCreateStudentInputBoxDialog(Context context, String title,
                                                  String upperInputBoxDefaultText,
                                                  String upperInputBoxHintText,
                                                  String lowerInputBoxDefaultText,
                                                  String lowerInputBoxHintText,
                                                  String leftButtonText,
                                                  OnClickListener leftButtonClickListener,
                                                  String rightButtonText,
                                                  OnClickListener rightButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title,
                upperInputBoxDefaultText, upperInputBoxHintText,
                lowerInputBoxDefaultText, lowerInputBoxHintText,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
    }

    public HeadTeacherCreateStudentInputBoxDialog(Context context, int theme, String title,
                                                  String upperInputBoxDefaultText,
                                                  String upperInputBoxHintText,
                                                  String lowerInputBoxDefaultText,
                                                  String lowerInputBoxHintText,
                                                  String leftButtonText,
                                                  OnClickListener leftButtonClickListener,
                                                  String rightButtonText,
                                                  OnClickListener rightButtonClickListener) {
        super(context, theme, title, R.layout.my_contacts_dialog_double_inputbox,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);

        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_upper_text);
        if (textView != null) {
            textView.setText(upperInputBoxDefaultText);
            textView.setSelection(TextUtils.isEmpty(upperInputBoxDefaultText) ?
                    0 : upperInputBoxDefaultText.length());
            textView.setHint(upperInputBoxHintText);
        }
        textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_lower_text);
        if (textView != null) {
            textView.setText(lowerInputBoxDefaultText);
            textView.setSelection(TextUtils.isEmpty(lowerInputBoxDefaultText) ?
                    0 : lowerInputBoxDefaultText.length());
            textView.setHint(lowerInputBoxHintText);
        }
        initViews();
    }

    private void initViews() {
        userNameEditText = (EditText) getContentView().findViewById(R.id.user_name);
        setEditTextViewSelection(userNameEditText);

        realNameEditText = (EditText) getContentView().findViewById(R.id.real_name);
        setEditTextViewSelection(realNameEditText);
    }

    private void setEditTextViewSelection(EditText view) {
        if (view != null) {
            view.setSelection(TextUtils.isEmpty(view.getText().toString()) ?
                    0 : view.getText().toString().length());
        }
    }


    public String getUpperInputText() {
        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_upper_text);
        if (textView != null) {
            return textView.getText().toString();
        }
        return null;
    }

    public String getLowerInputText() {
        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_lower_text);
        if (textView != null) {
            return textView.getText().toString();
        }
        return null;
    }

    public String getUserNameInputText() {
        return userNameEditText.getText().toString().trim();
    }

    public String getRealNameInputText() {
        return realNameEditText.getText().toString().trim();
    }

    public void setRealNameInputLength(int length){
        if (realNameEditText != null){
            realNameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        }
    }
}
