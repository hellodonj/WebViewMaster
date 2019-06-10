package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;

import java.util.ArrayList;


/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/10/17 19:14
 * 描    述：打分
 * 修订历史：
 * ================================================
 */
public class MarkScoreDialog extends ContactsDialogNew implements TextWatcher {
    boolean isPercentageSystem = true;//百分制
    private String totalScore;
    private AdapterViewHelper adapterViewHelper;
    private final String[] DEFAULT_LETTER_LIST = {
            "A+","A","A-","B+","B","B-","C+","C","C-","D "
    };
    private  ArrayList<Item> mList;
    Context context;
    private EditText mEditText;

    public MarkScoreDialog(Context context,boolean isPercentageSystem, String title,
                           String inputBoxDefaultText, String inputBoxHintText,
                           String leftButtonText, OnClickListener leftButtonClickListener,
                           String rightButtonText, OnClickListener rightButtonClickListener) {

        this(context, isPercentageSystem, title, inputBoxDefaultText, inputBoxHintText, leftButtonText, leftButtonClickListener
                , rightButtonText, rightButtonClickListener,null,null,null);

    }

    /**
     *
     * @param context
     * @param isPercentageSystem true : 百分制
     * @param title
     * @param inputBoxDefaultText
     * @param inputBoxHintText
     * @param leftButtonText
     * @param leftButtonClickListener
     * @param rightButtonText
     * @param rightButtonClickListener
     */
    public MarkScoreDialog(Context context,
                           boolean isPercentageSystem,
                           String title,
                           String inputBoxDefaultText,
                           String inputBoxHintText,
                           String leftButtonText,
                           OnClickListener leftButtonClickListener,
                           String rightButtonText,
                           OnClickListener rightButtonClickListener,
                           String middleButtonText,
                           DialogInterface.OnClickListener middleButtonClickListener,
                           String totalScore) {

        super(context, R.style.Theme_ContactsDialog, title, isPercentageSystem ?
                        R.layout.contacts_dialog_mark_score : R.layout.contacts_dialog_gride_list,
                leftButtonText, leftButtonClickListener,
                rightButtonText, rightButtonClickListener,middleButtonText,middleButtonClickListener);
        this.isPercentageSystem = isPercentageSystem;
        this.context = context;
        if (isPercentageSystem) {
            //百分制
            mEditText = (EditText) getContentView().findViewById(
                    R.id.contacts_dialog_content_text);
            if (mEditText != null) {
                //只能输入数字和小数点
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                if (!TextUtils.isEmpty(inputBoxDefaultText)) {
                    mEditText.setText(inputBoxDefaultText);
                }
                mEditText.setSelection(TextUtils.isEmpty(inputBoxDefaultText) ?
                        0 : inputBoxDefaultText.length());
                mEditText.setHint(inputBoxHintText);
                mEditText.setTextColor(context.getResources().getColor(R.color.black));
                mEditText.addTextChangedListener(this);
            }
            TextView totalScoreView = (TextView) getContentView().findViewById(R.id.tv_total_score);
            if (!TextUtils.isEmpty(totalScore) && totalScoreView != null){
                totalScoreView.setText(context.getString(R.string.str_subject_total_score,totalScore));
                totalScoreView.setVisibility(View.VISIBLE);
            }
        } else {
            mList = new ArrayList<>();
            boolean flag = false;
            for (String adefaultLetterList : DEFAULT_LETTER_LIST) {
                Item item = new Item();
                item.title = adefaultLetterList;
                if (adefaultLetterList.trim().equalsIgnoreCase(inputBoxDefaultText)) {
                    //默认分数
                    item.checked = true;
                    flag = true;
                }
                mList.add(item);
            }
            if (!flag) {
                mList.get(0).checked = true;
            }

            MyGridView gridView = (MyGridView) getContentView().findViewById(R.id.list_gridview);
            final Myadapter myadapter = new Myadapter();
            gridView.setAdapter(myadapter);
        }

        setIsAutoDismiss(false);

    }

    /**
     * 获取分数
     * @return
     */
    public String getScore() {
        if (!isPercentageSystem) {
            for (Item item : mList) {
                if (item.checked) {
                    return item.title.trim();
                }
            }
        } else {//百分制
            EditText textView = (EditText) getContentView().findViewById(
                    R.id.contacts_dialog_content_text);
            if (textView != null) {
                return textView.getText().toString().trim();
            }
        }

        return "";
    }


    public void setInputLimitNumber(int length){
        if (!isPercentageSystem) return;
        EditText textView = (EditText) getContentView().findViewById(
                R.id.contacts_dialog_content_text);
        if (textView!=null){
            textView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //在afterTextChanged中，调用setText()方法会循环递归触发监听器，必须合理退出递归，不然会产生异常
//        if (isExerciseMark) {
            //可以输入小数点但是只能输入一位
            String text = s.toString();
            if (text.length() == 1 && text.contains(".")){
                mEditText.setText("");
                return;
            }
            if (text.contains(".")){
                int index = text.indexOf(".");
                int size = text.length();
                if (size - index > 2){
                    text = text.substring(0, index + 2);
                    mEditText.setText(text);
                }
            }
            Selection.setSelection(mEditText.getText(), mEditText.getText().length());
//        } else {
//            if (s.length() > 1 && s.charAt(0) == '0') {
//                Integer integer = Integer.valueOf(s.toString());
//                mEditText.setText(integer.toString());
//                //设置新光标所在的位置
//                Selection.setSelection(mEditText.getText(), mEditText.getText().length());
//            }
//        }
    }

    class Item {
        String title;
        boolean checked;
    }

    class Myadapter extends BaseAdapter {
        @Override
        public int getCount() {
            return DEFAULT_LETTER_LIST.length;
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            final Item item = mList.get(position);
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_mark_score, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }

            holder.mButton.setText(item.title);
            holder.mButton.setChecked(item.checked);

            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (Item item : mList) {
                        item.checked = false;
                    }
                    item.checked = true;
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }
    private class ViewHolder {
        RadioButton mButton;

        public ViewHolder(View parentView) {
            mButton = (RadioButton) parentView.findViewById(R.id.mark_rb);
        }
    }


}
