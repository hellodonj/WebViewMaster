package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 搜索界面
 */
public class SearchFragment extends MyBaseFragment implements View.OnClickListener, TextWatcher
        , TextView.OnEditorActionListener {

    private static String histotyKeyName = "SearchHistoryKey";
    private EditText editTextSearch;
    private ImageView imageViewSearchClear;
    private TextView textViewCancel;
    private LinearLayout layoutHistory;
    private ImageView imgageViewClearHistory;
    private NoScrollGridView gridViewHistory;

    private List<String> listHistoryKey;
    private SearchKeyAdapter historyAdapter;
    private String keyString;
    private OnSearchStatusChangeListener onSearchStatusChangeListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_search, container, false);

        editTextSearch = (EditText) view.findViewById(R.id.search_et);
        imageViewSearchClear = (ImageView) view.findViewById(R.id.search_clear_iv);
        textViewCancel = (TextView) view.findViewById(R.id.cancel_tv);
        layoutHistory = (LinearLayout) view.findViewById(R.id.hishory_layout);
        imgageViewClearHistory = (ImageView) view.findViewById(R.id.clear_history_iv);
        gridViewHistory = (NoScrollGridView) view.findViewById(R.id.history_list);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        getHistoryKey();
    }

    private void initViews() {
        historyAdapter = new SearchKeyAdapter(activity);
        gridViewHistory.setAdapter(historyAdapter);
        gridViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = (String) historyAdapter.getItem(position);
                if(onSearchStatusChangeListener != null){
                    onSearchStatusChangeListener.onSearch(key);
                }
            }
        });
        getHistoryKey();
        editTextSearch.setOnEditorActionListener(this);
        editTextSearch.addTextChangedListener(this);
        imageViewSearchClear.setOnClickListener(this);
        textViewCancel.setOnClickListener(this);
        imgageViewClearHistory.setOnClickListener(this);


        editTextSearch.setMaxLines(1);
        editTextSearch.setInputType(EditorInfo.TYPE_CLASS_TEXT
                | EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE
                | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    public void setOnSearchStatusChangeListener(OnSearchStatusChangeListener listener){
        this.onSearchStatusChangeListener = listener;
    }

    public void reInitSearch(){
        if(editTextSearch == null){
            return;
        }
        keyString = editTextSearch.getText().toString();
        if (!"".equals(keyString)) {
            //检查该搜索关键字是否在历史记录中
            for (int i = 0; i < listHistoryKey.size(); i++) {
                if (listHistoryKey.get(i).equals(keyString)) {
                    listHistoryKey.remove(i);
                }
            }
            //新增到历史搜索关键字中
            listHistoryKey.add(0, keyString);
            setHistoryKey();
            if(onSearchStatusChangeListener != null){
                onSearchStatusChangeListener.onSearch(keyString);
            }
        }
        editTextSearch.setText("");
        getHistoryKey();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            keyString = editTextSearch.getText().toString();
            if (!"".equals(keyString)) {
                //检查该搜索关键字是否在历史记录中
                for (int i = 0; i < listHistoryKey.size(); i++) {
                    if (listHistoryKey.get(i).equals(keyString)) {
                        listHistoryKey.remove(i);
                    }
                }
                //新增到历史搜索关键字中
                listHistoryKey.add(0, keyString);
                setHistoryKey();
                if(onSearchStatusChangeListener != null){
                    onSearchStatusChangeListener.onSearch(keyString);
                }
            }
            return true;
        } else {
            return false;
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
        String keyWord = editTextSearch.getText().toString();
        imageViewSearchClear.setVisibility(keyWord.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.search_clear_iv) {
            editTextSearch.setText("");
        }else if(view.getId() == R.id.clear_history_iv) {
            listHistoryKey.clear();
            historyAdapter.setData(listHistoryKey);
            historyAdapter.notifyDataSetChanged();
            layoutHistory.setVisibility(View.GONE);
            SharedPreferences sp = activity.getPreferences(Context.MODE_APPEND);
            SharedPreferences.Editor editor = sp.edit();
            String nameHead = UserHelper.isLogin() ? UserHelper.getUserId() : "";
            for (int i = 0; i < 6; i++) {
                editor.remove(nameHead + "_" + histotyKeyName + i);
            }
            editor.commit();
        }else if(view.getId() == R.id.cancel_tv) {
            hideKeyboard();
            if(onSearchStatusChangeListener != null){
                onSearchStatusChangeListener.onCancel();
            }
        }
    }

    /*@Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            if(onSearchStatusChangeListener != null){
                onSearchStatusChangeListener.onCancel();
            }
        }
    }*/

    private void getHistoryKey() {
        listHistoryKey = new ArrayList<String>();
        SharedPreferences sp = activity.getPreferences(Context.MODE_APPEND);
        String nameHead = UserHelper.isLogin() ? UserHelper.getUserId() : "";
        for (int i = 0; i < 6; i++) {
            if (!sp.getString(nameHead + "_" + histotyKeyName + i, "").equals("")) {
                listHistoryKey.add(sp.getString(nameHead + "_" + histotyKeyName + i, ""));
            }
        }

        if (listHistoryKey != null && listHistoryKey.size() > 0) {
            layoutHistory.setVisibility(View.VISIBLE);
            historyAdapter.setData(listHistoryKey);
            historyAdapter.notifyDataSetChanged();
        } else {
            layoutHistory.setVisibility(View.GONE);
        }
    }

    private void setHistoryKey() {
        SharedPreferences sp = activity.getPreferences(Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        String nameHead = UserHelper.isLogin() ? UserHelper.getUserId() : "";
        if (listHistoryKey != null && listHistoryKey.size() > 0) {
            for (int i = 0; i < listHistoryKey.size(); i++) {
                editor.putString(nameHead + "_" + histotyKeyName + i, listHistoryKey.get(i));
            }
            editor.commit();
        }
    }



    public interface OnSearchStatusChangeListener{
        void onSearch(String keyWord);
        void onCancel();
    }

    private class SearchKeyAdapter extends BaseAdapter {
        private List<String> list;
        Activity parentActivity;

        public SearchKeyAdapter(Activity activity) {
            parentActivity = activity;
            list = new ArrayList<String>();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String name = list.get(position);
            if (convertView == null) {
                convertView = new TextView(parentActivity);
                convertView.setBackgroundColor(
                        activity.getResources().getColor(R.color.com_bg_light_white));
                convertView.setPadding(24, 16, 24, 16);
                ((TextView) convertView).setSingleLine(true);
                ((TextView) convertView).setEllipsize(TextUtils.TruncateAt.END);
                ((TextView) convertView).setGravity(Gravity.CENTER);
                ((TextView) convertView).setTextColor(
                        activity.getResources().getColor(R.color.com_text_black));
            }
            ((TextView) convertView).setText(name);
            return convertView;
        }

        public void setData(List<String> list) {
            if (list != null) {
                this.list = new ArrayList<String>(list);
            } else {
                this.list.clear();
            }
        }

        public void addData(List<String> list) {
            this.list.addAll(list);
        }
    }
}
