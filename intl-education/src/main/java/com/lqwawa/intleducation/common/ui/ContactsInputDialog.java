package com.lqwawa.intleducation.common.ui;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ToastUtil;

public class ContactsInputDialog extends ContactsDialog {

    public ContactsInputDialog(Context context, String title,
                               String inputBoxDefaultText, String inputBoxHintText,
                               String leftButtonText, OnClickListener leftButtonClickListener,
                               String rightButtonText, OnClickListener rightButtonClickListener) {
        this(context, R.style.Theme_ContactsDialog, title,
                inputBoxDefaultText, inputBoxHintText,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener);
    }

    public ContactsInputDialog(Context context, int theme, String title,
                               String inputBoxDefaultText, String inputBoxHintText,
                               String leftButtonText, OnClickListener leftButtonClickListener,
                               String rightButtonText, OnClickListener rightButtonClickListener) {
        super(context, theme, title, R.layout.contacts_dialog_inputbox,
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

    public void setUnitDisplay(boolean isDisplay) {
        TextView textView = (TextView) getContentView().findViewById(R.id.tv_unit);
        if (isDisplay) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


    public void setInputLimitNumber(int length) {
        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        if (textView != null) {
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        }
    }

    public void setInputLimit() {
        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textView.getText().toString().matches("^0")) {//判断当前的输入第一个数是不是为0
                    textView.setText("");
                    ToastUtil.showToast(getContext(), R.string.label_price_tip);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
