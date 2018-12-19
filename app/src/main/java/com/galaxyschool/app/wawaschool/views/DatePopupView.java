package com.galaxyschool.app.wawaschool.views;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.views.wheelview.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePopupView extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    public interface OnDateChangeListener {
        void onDateChange(String dateStr);
    }

    private View mRootView;
    private TextView mCancelBtn, mConfirmBtn;
    private CalendarView mCalendarView;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    private String mDateStr;
    private boolean isOnline;
    private OnDateChangeListener listener;

    public DatePopupView(Context context, String dateStr, OnDateChangeListener listener) {
        super(context);
        mContext = context;
        mDateStr = dateStr;
        this.listener = listener;

        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
    }

    public DatePopupView(Context context, String dateStr, boolean isOnline, OnDateChangeListener
            listener) {
        super(context);
        mContext = context;
        mDateStr = dateStr;
        this.listener = listener;

        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.isOnline = isOnline;
        initViews();
    }

    private void initViews() {
        mRootView = mLayoutInflater.inflate(R.layout.date_popup_view, null);
        mCancelBtn = (TextView) mRootView.findViewById(R.id.date_cancel_btn);
        mConfirmBtn = (TextView) mRootView.findViewById(R.id.date_confirm_btn);
        mCalendarView = (CalendarView) mRootView.findViewById(R.id.date_calendarview);
        mRootView.setOnTouchListener(this);
        mCancelBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

        initCalendarView();

        setContentView(mRootView);
        setFocusable(true);
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        int color = mContext.getResources().getColor(R.color.popup_root_view_bg);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        setBackgroundDrawable(colorDrawable);

    }

    private void initCalendarView() {
        if (!TextUtils.isEmpty(mDateStr)) {
            SimpleDateFormat dateFormat;
            if (isOnline) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            } else {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            }
            try {
                Date date = dateFormat.parse(mDateStr);
                int year = date.getYear() + 1900;
                int month = date.getMonth();
                int day = date.getDate();
                if (isOnline) {
                    int time = date.getHours();
                    int min = date.getMinutes();
                    mCalendarView.setCurrentYearMonthDay(year, month, day, time, min, true);
                } else {
                    mCalendarView.setCurrentYearMonthDay(year, month, day);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            mCalendarView.setCurrentYearMonthDay(year, month, day);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_confirm_btn:
                if (listener != null && !TextUtils.isEmpty(mCalendarView.GetCurrentTime())) {
                    listener.onDateChange(mCalendarView.GetCurrentTime());
                }
                dismiss();
                break;
            case R.id.date_cancel_btn:
                if (listener != null) {
                    listener.onDateChange(mDateStr);
                }
                dismiss();
                break;
            default:
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mRootView.findViewById(R.id.date_pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                if (listener != null) {
                    listener.onDateChange(mDateStr);
                }
                dismiss();
            }
        }
        return true;
    }
}
