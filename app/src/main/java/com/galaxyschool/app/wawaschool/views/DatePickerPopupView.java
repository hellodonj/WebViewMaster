package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.views.wheelview.ArrayWheelAdapter;
import com.galaxyschool.app.wawaschool.views.wheelview.NumericWheelAdapter;
import com.galaxyschool.app.wawaschool.views.wheelview.WheelView;

import java.util.Calendar;

/**
 * ʱ��ѡ����
 */
public class DatePickerPopupView extends PopupWindow implements View.OnClickListener, View.OnTouchListener {
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private TextView mCancelBtn, mConfirmBtn;
    private View mRootView;
    private int minYear = 1900;
    private int maxYear = 2100;
    private int[] dateArray = new int[2];
    private String[] dateTypeArray;
    private Context context;
    private OnDatePickerItemSelectedListener listener;
    private LayoutInflater mLayoutInflater;
    private View targetView;

    public interface OnDatePickerItemSelectedListener {
        void onDatePickerItemSelected(String pickedResultStr, View view);
    }

    public DatePickerPopupView(Context context,OnDatePickerItemSelectedListener listener,View targetView) {
        this.context = context;
        this.listener=listener;
        this.targetView=targetView;
        initViews();
    }

    public DatePickerPopupView(Context context,OnDatePickerItemSelectedListener listener,int[] dateArray) {
        this.context = context;
        this.listener=listener;
        if (dateArray != null && dateArray.length > 0) {
            this.dateArray = dateArray;
            minYear = this.dateArray[0];
            maxYear = this.dateArray[1];
        }
        initViews();
    }


    public void setCurrentYearMonthDay(int theYear, int theMonth, int theDay) {
        year.setCurrentItem(theYear - minYear);
        month.setCurrentItem(theMonth);
        updateDays(year, month, day);
        day.setCurrentItem(theDay - 1);
    }

    private void initViews() {

        dateTypeArray = context.getResources().getStringArray(R.array.date_type);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = mLayoutInflater.inflate(R.layout.date_picker_popup_view, null);
        mRootView.setOnTouchListener(this);
        setContentView(mRootView);
        setFocusable(true);
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        int color = context.getResources().getColor(R.color.popup_root_view_bg);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        setBackgroundDrawable(colorDrawable);

        Calendar calendar = Calendar.getInstance();

        mCancelBtn = (TextView) mRootView.findViewById(R.id.relation_cancel_btn);
        mCancelBtn.setTextColor(mRootView.getResources().getColor(R.color.text_green));

        mConfirmBtn = (TextView) mRootView.findViewById(R.id.relation_confirm_btn);
        mConfirmBtn.setTextColor(mRootView.getResources().getColor(R.color.text_green));

        mCancelBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

        month = (WheelView) mRootView.findViewById(R.id.month);
        year = (WheelView) mRootView.findViewById(R.id.year);
        day = (WheelView) mRootView.findViewById(R.id.day);

        // day
        day.setCyclic(true);
        WheelView.OnWheelChangedListener listener = new WheelView.OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day);
            }
        };

        // month
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = new String[]{"01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"};
        DateArrayAdapter monthAdapter = new DateArrayAdapter(context, months, curMonth);
        //��
        monthAdapter.setTextType(dateTypeArray[1]);
        month.setViewAdapter(monthAdapter);
        month.setCurrentItem(curMonth);
        month.addChangingListener(listener);
        month.setCyclic(true);

        // year
        int curYear = calendar.get(Calendar.YEAR);
        DateNumericAdapter yearAdapter = new DateNumericAdapter(
                context, minYear,
                maxYear, curYear - minYear);
        //��
        yearAdapter.setTextType(dateTypeArray[0]);
        year.setViewAdapter(yearAdapter);
        year.setCurrentItem(0);
        year.addChangingListener(listener);
        year.setCyclic(true);

        // day
        updateDays(year, month, day);
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
    }

    private void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                Calendar.YEAR,
                minYear + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DateNumericAdapter dayAdapter = new DateNumericAdapter(
                context, 1, maxDays, calendar
                .get(Calendar.DAY_OF_MONTH) - 1);
        //��
        dayAdapter.setTextType(dateTypeArray[2]);
        day.setViewAdapter(dayAdapter);
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

    public String getCurrentTime() {

        Calendar calendar = Calendar.getInstance();
        int curYear = calendar.get(Calendar.YEAR);
        int index = year.getCurrentItem();
        String monthStr = "";
        if (month.getCurrentItem() >= 9) {
            monthStr = String.valueOf(month.getCurrentItem() + 1);
        } else {
            monthStr = "0" + String.valueOf(month.getCurrentItem() + 1);
        }
        String dayStr = "";
        if (day.getCurrentItem() >= 9) {
            dayStr = String.valueOf(day.getCurrentItem() + 1);
        } else {
            dayStr = "0" + String.valueOf(day.getCurrentItem() + 1);
        }
        String result = String.valueOf(minYear + index) + "-" + monthStr
                + "-" + dayStr;
        return result;
    }

    @Override
    public void onClick(View v) {
        if (v == mCancelBtn) {
            if (listener != null) {
                listener.onDatePickerItemSelected(null,targetView);
            }
            dismiss();

        } else if (v == mConfirmBtn) {
            if (listener != null) {
                listener.onDatePickerItemSelected(getCurrentTime(),targetView);
            }
            dismiss();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mRootView.findViewById(R.id.relation_pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                dismiss();
            }
        }
        return true;
    }

    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(
                Context context, int minValue, int maxValue,
                int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(20);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                // view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(20);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                // view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

}
