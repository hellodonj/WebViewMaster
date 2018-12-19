package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by KnIghT on 16-6-15.
 */
public class CalenderPopwindow extends PopupWindow implements View.OnClickListener, OnCalendarClickListener {
    private Activity mContext;
    private View mRootView;
    private TextView backToToday;
    private TextView currentMonth;
    private ImageView preMonth;
    private ImageView nextMonth;
    private int mWidth = 0;
    private OnDatePickListener datePickListener;
    private MonthCalendarView mCalendarView;
    private int mSelectYear, mSelectMonth, mSelectDay;
    private String filterStartTimeBegin;
    private Date date;

    public CalenderPopwindow(Date date,final Activity context, OnDatePickListener datePickListener ) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(R.layout.activity_calender, null);
        this.datePickListener = datePickListener;
        this.date = date;
        initView(date);
        setProperty();
    }

    @Override
    public void onClickDate(int year, int month, int day) {
        //有没有切换月（相应的改变小绿点）

        boolean isChangeMonth = false;
        mSelectYear = year;
        if (mSelectMonth != month) {
            isChangeMonth = true;
        }
        mSelectMonth = month;
        mSelectDay = day;
        currentMonth.setText(String.format("%d年%d月", year, month + 1));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, day);
        if (datePickListener != null ) {

            datePickListener.onDatePick(calendar.getTime(),mCalendarView.getCurrentItem());
        }

        filterStartTimeBegin = getTimeData(year, month, day);
        if (isChangeMonth) {
            if (datePickListener != null) {

                datePickListener.loadDateSignData(filterStartTimeBegin,mCalendarView.getCurrentItem());
            }
        }
    }

    @Override
    public void onPageChange(int year, int month, int day) {

        currentMonth.setText(String.format("%d年%d月", year, month + 1));

    }

    private String getTimeData(int year, int month, int day) {
        return String.valueOf(year) + "-" + (month + 1) + "-" + day;
    }


    public interface OnDatePickListener {
        void onDatePick(Date monthDay, int position);

        void loadDateSignData(String filterStartTimeBegin, int position);
    }

    public void initView(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null) {
            filterStartTimeBegin = DateUtils.getDateYmdStr();
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            filterStartTimeBegin = formatter.format(date);
            calendar.setTime(date);

        }
        mSelectYear = calendar.get(Calendar.YEAR);
        mSelectMonth = calendar.get(Calendar.MONTH);
        mSelectDay = calendar.get(Calendar.DAY_OF_MONTH);

        backToToday = (TextView) mRootView.findViewById(R.id.back_to_today);
        preMonth = (ImageView) mRootView.findViewById(R.id.pre_month);
        nextMonth = (ImageView) mRootView.findViewById(R.id.next_month);
        currentMonth = (TextView) mRootView.findViewById(R.id.current_month);
        backToToday.setOnClickListener(this);
        preMonth.setOnClickListener(this);
        nextMonth.setOnClickListener(this);
        mCalendarView = (MonthCalendarView) mRootView.findViewById(R.id.monthCalendarView);
        mCalendarView.setOnCalendarClickListener(this);

        currentMonth.setText(String.format("%d年%d月", mSelectYear, mSelectMonth + 1));
        if (datePickListener != null) {
            datePickListener.loadDateSignData(filterStartTimeBegin,mCalendarView.getCurrentItem());
        }

    }

    private void setProperty() {
        //int h = mContext.getWindowManager().getDefaultDisplay().getHeight();
        int w = ScreenUtils.getScreenWidth(mContext);
        mWidth = w;
        this.setContentView(mRootView);
        this.setWidth((int) (w * 1.0));
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_to_today:
                backToToday();
                break;
            case R.id.pre_month:
                switchToPreMonth();
                break;
            case R.id.next_month:
                switchToNextMonth();
                break;
            default:
                break;
        }

    }

    public void showPopupMenu(View parent,boolean loadSignData) {
        if (!this.isShowing()) {
            if (loadSignData){
                if (datePickListener != null && mCalendarView != null) {
                    datePickListener.loadDateSignData(filterStartTimeBegin,mCalendarView.getCurrentItem());
                }
            }
            this.showAsDropDown(parent, mWidth * 3/10, 0);
        } else {
            this.dismiss();
        }
    }
    private void backToToday() {
        if (mCalendarView != null) {

            mCalendarView.setTodayToView();
        }
    }

    private void switchToNextMonth() {
        if (mCalendarView != null) {

            mCalendarView.setCurrentItem(mCalendarView.getCurrentItem() + 1);
        }

    }

    private void switchToPreMonth() {
        if (mCalendarView != null) {

            mCalendarView.setCurrentItem(mCalendarView.getCurrentItem() - 1);
        }
    }

    public void updateSignData() {
        if (mCalendarView != null) {
            mCalendarView.getCurrentMonthView().invalidate();
        }
    }

    public void resetMonthView() {
        if (mCalendarView != null) {
            mCalendarView.setTodayToView(date);
        }
    }

    public void upDateCalendarView(Date dateTime){
        if (dateTime != null) {
            this.date = dateTime;
            resetMonthView();
        }
    }
}
