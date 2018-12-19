package com.lqwawa.intleducation.module.discovery.ui.lqcourse.search;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 搜索页面的Adapter
 * @date 2018/05/05 14:14
 * @history v1.0
 * **********************************
 */
public class SearchKeyAdapter extends BaseAdapter{

    private List<String> mKeys;

    public SearchKeyAdapter() {
        mKeys = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mKeys.size();
    }

    @Override
    public Object getItem(int position) {
        return mKeys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String key = mKeys.get(position);
        if (convertView == null) {
            convertView = new TextView(UIUtil.getContext());
            convertView.setBackgroundColor(UIUtil.getColor(R.color.com_bg_light_white));
            convertView.setPadding(24, 16, 24, 16);
            ((TextView) convertView).setSingleLine(true);
            ((TextView) convertView).setEllipsize(TextUtils.TruncateAt.END);
            ((TextView) convertView).setGravity(Gravity.CENTER);
            ((TextView) convertView).setTextColor(UIUtil.getColor(R.color.com_text_black));
        }
        ((TextView) convertView).setText(key);
        return convertView;
    }

    public void setData(List<String> list) {
        if (list != null) {
            this.mKeys = new ArrayList<>(list);
        } else {
            this.mKeys.clear();
        }
    }

    public void addData(List<String> list) {
        this.mKeys.addAll(list);
    }

}
