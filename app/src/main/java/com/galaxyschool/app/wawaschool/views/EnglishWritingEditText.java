package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.lqwawa.apps.views.ContainsEmojiEditText;

/**
 * Created by Administrator on 2017/2/21.
 */

public class EnglishWritingEditText extends ContainsEmojiEditText {
    private Context mContext;
    private TextView textView;
    public EnglishWritingEditText(Context context) {
        super(context);
        this.mContext = context;
        initData();
    }

    public EnglishWritingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initData();
    }

    public EnglishWritingEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initData();
    }
    private void initData(){
        //设置软键盘半屏显示
        this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        this.addTextChangedListener(new EditTextListener());
    }
    private class EditTextListener implements TextWatcher {
        int numCount=0;
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int count=0;
            if (s.length()!=0) {
                String string = s.toString();
                String [] array=string.split("\\s+");
                int size=array.length;
                if (size>0){
                    for (int i=0;i<size;i++){
                        if (!TextUtils.isEmpty(array[i])) {
                            count = array[i].split("\\n+").length + count;
                        }

                    }
                }
            }
            numCount=count;
            if (textView!=null) {
                textView.setText(String.valueOf(numCount));
            }
        }
    }
    public void setParams(TextView textView){
        this.textView=textView;
    }
}
