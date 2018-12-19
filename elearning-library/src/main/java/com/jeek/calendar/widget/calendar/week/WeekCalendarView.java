package com.jeek.calendar.widget.calendar.week;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.lqwawa.apps.R;

import org.joda.time.DateTime;

import java.util.Calendar;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class WeekCalendarView extends ViewPager implements OnWeekClickListener {

    private OnCalendarClickListener mOnCalendarClickListener;
    private WeekAdapter mWeekAdapter;
    public static int lastSelectWeekNumber = 0;
    public static boolean isWeekPageChangedClick = false;

    public WeekCalendarView(Context context) {
        this(context, null);
    }

    public WeekCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        addOnPageChangeListener(mOnPageChangeListener);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initWeekAdapter(context, context.obtainStyledAttributes(attrs, R.styleable.WeekCalendarView));
    }

    private void initWeekAdapter(Context context, TypedArray array) {
        mWeekAdapter = new WeekAdapter(context, array, this);
        setAdapter(mWeekAdapter);
        setCurrentItem(mWeekAdapter.getWeekCount() / 2, false);
    }

    static {
        Calendar calendar = Calendar.getInstance();
        WeekCalendarView.lastSelectWeekNumber
                = CalendarUtils.getWeek(calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        if (mOnCalendarClickListener != null) {
            mOnCalendarClickListener.onClickDate(year, month, day);
        }
        if(isWeekPageChangedClick){
            isWeekPageChangedClick = false;
        }else {
            WeekCalendarView.lastSelectWeekNumber = CalendarUtils.getWeek(year, month, day);
        }
    }
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(final int position) {
            WeekView weekView = mWeekAdapter.getViews().get(position);
            if (weekView != null) {
                isWeekPageChangedClick = true;
                DateTime selectDateTime = weekView.getStartDate().plusDays(lastSelectWeekNumber);
                int selectMonth = selectDateTime.getMonthOfYear() - 1;
                //切换周时，选择日期的月发生变化时，需设置当前选择日期的年和月
                if (weekView.getSelectMonth() != selectMonth) {
                    weekView.clickThisWeek(selectDateTime.getYear(), selectDateTime.getMonthOfYear() - 1,
                            weekView.getDayFromWeekNumber(lastSelectWeekNumber));
                } else {
                    weekView.clickThisWeek(weekView.getSelectYear(), weekView.getSelectMonth(),
                            weekView.getDayFromWeekNumber(lastSelectWeekNumber));
                }

                if (mOnCalendarClickListener != null) {
                    mOnCalendarClickListener.onPageChange(weekView.getSelectYear(),
                            weekView.getSelectMonth(),
                            weekView.getDayFromWeekNumber(lastSelectWeekNumber));
                }
            } else {
                WeekCalendarView.this.postDelayed(new Runnable() {
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
     * 设置点击日期监听
     * @param onCalendarClickListener
     */
    public void setOnCalendarClickListener(OnCalendarClickListener onCalendarClickListener) {
        mOnCalendarClickListener = onCalendarClickListener;
    }

    public SparseArray<WeekView> getWeekViews() {
        return mWeekAdapter.getViews();
    }

    public WeekAdapter getWeekAdapter() {
        return mWeekAdapter;
    }

    public WeekView getCurrentWeekView() {
        return getWeekViews().get(getCurrentItem());
    }

}
