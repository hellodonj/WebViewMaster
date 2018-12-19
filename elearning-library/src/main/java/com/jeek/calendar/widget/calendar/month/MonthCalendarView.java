package com.jeek.calendar.widget.calendar.month;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;
import com.lqwawa.apps.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class MonthCalendarView extends ViewPager implements OnMonthClickListener {

    private MonthAdapter mMonthAdapter;
    private OnCalendarClickListener mOnCalendarClickListener;
    private boolean clickLastOrNextMonth = false;
    private boolean isFromStudyTask;

    public MonthCalendarView(Context context) {
        this(context, null);
    }

    public MonthCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initMonthAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.MonthCalendarView));
    }

    private void initMonthAdapter(Context context, TypedArray array) {
        mMonthAdapter = new MonthAdapter(context, array, this);
        setAdapter(mMonthAdapter);
        setCurrentItem(mMonthAdapter.getTodayItemPosition(), false);
    }

    @Override
    public void onClickThisMonth(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
        if(MonthCalendarView.this.getVisibility() == VISIBLE) {
            WeekCalendarView.lastSelectWeekNumber = CalendarUtils.getWeek(year, month, day);
        }
    }

    @Override
    public void onClickLastMonth(int year, int month, int day) {
        clickLastOrNextMonth = true;
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() - 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
        }
        setCurrentItem(getCurrentItem() - 1, true);
        clickLastOrNextMonth = true;
    }

    @Override
    public void onClickNextMonth(int year, int month, int day) {
        clickLastOrNextMonth = true;
        MonthView monthDateView = mMonthAdapter.getViews().get(getCurrentItem() + 1);
        if (monthDateView != null) {
            monthDateView.setSelectYearMonth(year, month, day);
            monthDateView.invalidate();
        }
        //onClickThisMonth(year, month, day);
        setCurrentItem(getCurrentItem() + 1, true);
    }
    private int lastSelectDay = 0;
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());
            if (monthView != null) {
                lastSelectDay = monthView.getSelectDay();
            }
        }

        @Override
        public void onPageSelected(final int position) {
            MonthView monthView = mMonthAdapter.getViews().get(getCurrentItem());
            if (monthView != null) {
                int selectDay = monthView.getSelectDay();
                if(clickLastOrNextMonth){
                    clickLastOrNextMonth = false;
                }else{
                    selectDay = Math.min(lastSelectDay, monthView.getMonthDays());
                }
                if (!isFromStudyTask) {

                    monthView.clickThisMonth(monthView.getSelectYear(), monthView.getSelectMonth(),
                            selectDay);
                }
                if (mOnCalendarClickListener != null) {
                    mOnCalendarClickListener.onPageChange(monthView.getSelectYear(),
                            monthView.getSelectMonth(),
                            selectDay);
                }
                if(MonthCalendarView.this.getVisibility() == VISIBLE) {
                    WeekCalendarView.lastSelectWeekNumber =
                            CalendarUtils.getWeek(monthView.getSelectYear(),
                                    monthView.getSelectMonth(), monthView.getSelectDay());
                }

            } else {
                MonthCalendarView.this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPageSelected(position);
                    }
                }, 50);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 跳转到今天
     */
    public void setTodayToView() {
        int todayMonthPosition = mMonthAdapter.getTodayItemPosition();
        setCurrentItem(todayMonthPosition, true);
        MonthView monthView = mMonthAdapter.getViews().get(todayMonthPosition);
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            monthView.clickThisMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }
    }

    /**
     * 跳转到某一天
     */
    public void setTodayToView(final Date date) {
        isFromStudyTask = true;
        int todayMonthPosition = mMonthAdapter.getTodayItemPosition(date);
        setCurrentItem(todayMonthPosition, true);
        MonthView monthView = mMonthAdapter.getViews().get(todayMonthPosition);
        if (monthView != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            isFromStudyTask = false;
            monthView.clickThisMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        }else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTodayToView(date);
                }
            }, 50);
        }
    }

    /**
     * 设置点击日期监听
     *
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<MonthView> getMonthViews() {
        return mMonthAdapter.getViews();
    }

    public MonthView getCurrentMonthView() {
        return getMonthViews().get(getCurrentItem());
    }

}
