package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.learn.vo.CourseProcessVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2018/1/21.
 * email:man0fchina@foxmail.com
 * history: v5.5弃用
 */
@Deprecated
public class CourseProcessAdapter extends MyBaseAdapter {
    private List<CourseProcessVo> weeklist;
    private Activity activity;
    private String chapterName;
    public CourseProcessAdapter(Activity parent, String parentChapterName){
        weeklist = new ArrayList<>();
        activity = parent;
        chapterName = parentChapterName;
    }

    @Override
    public int getCount() {
        return weeklist.size();
    }

    @Override
    public Object getItem(int position) {
        return weeklist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CourseProcessVo chapterVo = weeklist.get(position);
        TextView textView = new TextView(activity);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_3));
        if (chapterVo.getStatus() == 0) {
            textView.setBackground(activity.getResources().getDrawable(R.drawable.chapter_status0));
        } else if (chapterVo.getStatus() == 5) {
            textView.setBackground(activity.getResources().getDrawable(R.drawable.chapter_status5));
        } else if (chapterVo.getStatus() == 10) {
            textView.setBackground(activity.getResources().getDrawable(R.drawable.chapter_status10));
        }
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
            /*layoutParams.setMargins(DisplayUtil.dip2px(activity, 1), 0,
                    DisplayUtil.dip2px(activity, 1), 0);*/
        textView.setLayoutParams(layoutParams);
        textView.setPadding(DisplayUtil.dip2px(activity, 2),
                DisplayUtil.dip2px(activity, 1),
                DisplayUtil.dip2px(activity, 2),
                DisplayUtil.dip2px(activity, 1));
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine();
        textView.setText(StringUtils.getChapterNumString(activity, chapterName,
                chapterVo.getWeek()));
        return textView;
    }

    public void setData(List<CourseProcessVo> list, int start, int end) {
        weeklist = new ArrayList<>();
        if(list != null) {
            for (int i = start; i <= end && i < list.size(); i++) {
                weeklist.add(list.get(i));
            }
        }
    }
    public void clearData(){
        weeklist = new ArrayList<>();
    }
}