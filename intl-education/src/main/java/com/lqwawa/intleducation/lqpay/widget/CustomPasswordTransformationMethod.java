package com.lqwawa.intleducation.lqpay.widget;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/27 16:56
  * 描    述：
  * 修订历史：
  * ================================================
  */

public class CustomPasswordTransformationMethod extends PasswordTransformationMethod {
    String transformation;

    public CustomPasswordTransformationMethod(String transformation) {
        this.transformation = transformation;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source;
        }

        @Override
        public int length() {
            return mSource.length();
        }

        @Override
        public char charAt(int index) {
            return transformation.charAt(0);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end);
        }
    }

}