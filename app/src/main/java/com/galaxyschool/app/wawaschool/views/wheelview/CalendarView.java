package com.galaxyschool.app.wawaschool.views.wheelview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;

import java.util.Calendar;

public class CalendarView extends LinearLayout {
    private Context context;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private WheelView time;
    private WheelView min;

    private final int MINYEAR = 1900;
    private final int MAXYEAR = 2100;

    private final String mintes[] = new String[]{"0","10","20","30","40","50"};
    private final String fiveMins [] = new String[]{"0","5","10","15","20","25","30","35","40","45",
            "50","55"};
    private boolean isOnline;
    private  int hour;
    private  int minutes;
    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(
        Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews();
    }

    public void setCurrentYearMonthDay(int theYear, int theMonth, int theDay) {
        year.setCurrentItem(theYear - MINYEAR);
        month.setCurrentItem(theMonth);
        updateDays(year, month, day);
        day.setCurrentItem(theDay - 1);
    }
    public void setCurrentYearMonthDay(int theYear, int theMonth, int theDay,int theTime,int
            thiMin,boolean isOnline) {
        this.isOnline=isOnline;
        year.setCurrentItem(theYear - MINYEAR);
        month.setCurrentItem(theMonth);
        this.hour=theTime;
        this.minutes=thiMin;
        updateDays(year, month, day);
        day.setCurrentItem(theDay - 1);
    }
    private void initViews() {

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.caledar_view, this, true);
        Calendar calendar = Calendar.getInstance();

        month = (WheelView) viewGroup.findViewById(R.id.month);
        year = (WheelView) viewGroup.findViewById(R.id.year);
        day = (WheelView) viewGroup.findViewById(R.id.day);
        //区分时间点的时分
        time=(WheelView) viewGroup.findViewById(R.id.time);
        min=(WheelView) viewGroup.findViewById(R.id.min);

        // day
        day.setCyclic(true);
        WheelView.OnWheelChangedListener listener = new WheelView.OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day);
            }
        };

        // month
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = new String[]{"1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12"};
        month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
        month.setCurrentItem(curMonth);
        month.addChangingListener(listener);
        month.setCyclic(true);

        // year
        int curYear = calendar.get(Calendar.YEAR);
        year.setViewAdapter(
            new DateNumericAdapter(
                context, MINYEAR,
                MAXYEAR, curYear - MINYEAR));
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
            MINYEAR + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(
            new DateNumericAdapter(
                context, 1, maxDays, calendar
                .get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
        //如果来自创建直播的设置时间

        if (isOnline){
            time.setVisibility(View.VISIBLE);
            min.setVisibility(View.VISIBLE);
            time.setViewAdapter(new DateNumericAdapter(context,0,23,hour));
            time.setCyclic(true);
            time.setCurrentItem(hour);
            //min.setViewAdapter(new DateNumericAdapter(context,0,59,minutes));
            min.setViewAdapter(new DateArrayAdapter(context,fiveMins,minutes));
            min.setCyclic(true);
            min.setCurrentItem(minutes/5);
        }
    }

    public String GetCurrentTime() {

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
        if(day.getCurrentItem() >= 9) {
            dayStr = String.valueOf(day.getCurrentItem() + 1);
        } else {
            dayStr = "0" + String.valueOf(day.getCurrentItem() + 1);
        }
        String result = String.valueOf(MINYEAR + index) + "-" + monthStr
            + "-" + dayStr;
        if (isOnline){
            String currentHour;
            if (time.getCurrentItem()<10){
                currentHour="0"+String.valueOf(time.getCurrentItem());
            }else {
                currentHour=String.valueOf(time.getCurrentItem());
            }
            String currentMin;

          /*  if (min.getCurrentItem()<10){
                currentMin="0"+String.valueOf(min.getCurrentItem());
            }else {
                currentMin=String.valueOf(min.getCurrentItem());
            }*/
          if (min.getCurrentItem() == 0 || min.getCurrentItem() == 1){
              currentMin = "0"+fiveMins[min.getCurrentItem()];
          }else {
              currentMin = fiveMins[min.getCurrentItem()];
          }
            result=result+" "+currentHour+":"+currentMin;
        }

        return result;
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
