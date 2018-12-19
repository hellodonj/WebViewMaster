package com.lqwawa.intleducation.base.widgets.adapter;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * @author MrMedici
 * @desc 实现TextWatcher,用于监听EditText文本onTextChanged函数
 */
public abstract class TextWatcherAdapter implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
