package com.jeek.calendar.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.jeek.calendar.widget.calendar.CalendarUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthAdapter extends PagerAdapter {

    private SparseArray<MonthView> mViews;
    private Context mContext;
    private TypedArray mArray;
    private MonthCalendarView mMonthCalendarView;
    private int mMonthCount = 2400 - 12;
    public static DateTime baseDate;
    static {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd");
        baseDate = DateTime.parse("2000-07-5", format);
    }

    public MonthAdapter(Context context, TypedArray array, MonthCalendarView monthCalendarView) {
        mContext = context;
        mArray = array;
        mMonthCalendarView = monthCalendarView;
        mViews = new SparseArray<>();
    }

    @Override
    public int getCount() {
        return mMonthCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mViews.get(position) == null) {
            int date[] = getYearAndMonth(position);
            MonthView monthView = new MonthView(mContext, mArray, date[0], date[1]);
            monthView.setId(position);
            monthView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            monthView.invalidate();
            monthView.setOnDateClickListener(mMonthCalendarView);
            mViews.put(position, monthView);
        }
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    private int[] getYearAndMonth(int position) {
        return getYearAndMonth(position, baseDate);
    }

    private int[] getYearAndMonth(int position, final DateTime bDate){
        int date[] = new int[2];
        DateTime time = bDate;
        time = time.plusMonths(position - mMonthCount / 2);
        date[0] = time.getYear();
        date[1] = time.getMonthOfYear() - 1;
//        Log.d("测试", new StringBuilder("基础年月：")
//                .append(bDate.getYear()).append("年")
//                .append(bDate.getMonthOfYear()).append("月")
//                .append("跳转年月：位置:").append(position).append("-")
//                .append(time.getYear()).append("年")
//                .append(time.getMonthOfYear()).append("月").toString());
        return date;
    }

    public int getTodayItemPosition(){
        return CalendarUtils.getMonthDistence(DateTime.now(), baseDate) + mMonthCount / 2;
    }

    public int getTodayItemPosition(Date date){
        return CalendarUtils.getMonthDistence(new DateTime(date), baseDate) + mMonthCount / 2;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public SparseArray<MonthView> getViews() {
        return mViews;
    }

    public int getMonthCount() {
        return mMonthCount;
    }

}
