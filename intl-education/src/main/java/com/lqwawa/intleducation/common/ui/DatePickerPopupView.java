package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.WheelPicker.WheelPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class DatePickerPopupView extends PopupWindow
        implements PopupWindow.OnDismissListener,
        View.OnClickListener, View.OnTouchListener{
    protected List<String> yearData = new ArrayList<>();
    protected List<String> monthData = new ArrayList<>();
    protected WheelPicker yearPicker;
    protected WheelPicker monthPicker;
    protected Activity parentActivity;
    protected View contentView;
    protected View topView;
    protected View bottomView;
    protected TextView textViewTitle;
    protected TextView textViewBottomButton;
    protected BlackType blackType = BlackType.BLACK_ALL;
    protected int selYear, selMonth;
    protected int currentYear;

    public enum BlackType {
        BLACK_ALL, BLACK_TOP, BLACK_BOTTOM
    }

    PopupWindowListener popupWindowListener;

    public DatePickerPopupView(Activity activity,
                               int year, int month,
                               BlackType type,
                               PopupWindowListener listener) {
        this.parentActivity = activity;
        this.blackType = type;
        this.selYear = year;
        this.selMonth = month;
        this.popupWindowListener = listener;

        currentYear = 2000;
        for(int i = currentYear - 99; i < currentYear + 100; i++){
            yearData.add(new StringBuilder().append(i).append("年").toString());
        }
        for(int i = 0; i < 12; i++){
            monthData.add(new StringBuilder().append(i + 1).append("月").toString());
        }

        LayoutInflater inflater = activity.getLayoutInflater();
        contentView = inflater.inflate(R.layout.widgets_date_picker_pop_window, null);
        this.setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        yearPicker = (WheelPicker) contentView.findViewById(R.id.year_piker);
        monthPicker = (WheelPicker) contentView.findViewById(R.id.month_piker);
        topView = contentView.findViewById(R.id.pop_top_bg);
        bottomView = contentView.findViewById(R.id.pop_bottom_bg);
        textViewTitle = (TextView) contentView.findViewById(R.id.pop_title_tv);
        textViewBottomButton = (TextView) contentView.findViewById(R.id.bottom_btn);

        LinearLayout layoutButtonRoot =
                (LinearLayout) contentView.findViewById(R.id.button_root_lay);
        TextView textViewCancel = (TextView) contentView.findViewById(R.id.cancel_tv);
        TextView textViewOK = (TextView) contentView.findViewById(R.id.ok_tv);
        layoutButtonRoot.setVisibility(View.VISIBLE);
        textViewCancel.setOnClickListener(this);
        textViewOK.setOnClickListener(this);

        if (blackType == BlackType.BLACK_ALL) {
            topView.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
        } else if (blackType == BlackType.BLACK_TOP) {
            topView.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.GONE);
        } else if (blackType == BlackType.BLACK_BOTTOM) {
            topView.setVisibility(View.GONE);
            bottomView.setVisibility(View.VISIBLE);
        }
        textViewBottomButton.setOnClickListener(this);
        topView.setOnClickListener(this);
        bottomView.setOnClickListener(this);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setTouchable(true);
        setTouchInterceptor(this);
        //添加Item到网格中
        yearPicker.setCyclic(true);//数据循环
        yearPicker.setData(yearData);
        yearPicker.setSelectedItemPosition(selYear - currentYear + 99);
        monthPicker.setCyclic(true);//数据循环
        monthPicker.setData(monthData);
        monthPicker.setSelectedItemPosition(selMonth + 1);
        setOnDismissListener(this);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.pop_top_bg
            || view.getId()==R.id.pop_bottom_bg
                || view.getId() == R.id.cancel_tv) {
            dismiss();
        }else if(view.getId()== R.id.bottom_btn) {
            if (this.popupWindowListener != null) {
                popupWindowListener.onBottomButtonClickListener();
            }
            this.dismiss();
        }else if(view.getId() == R.id.ok_tv){
            if (this.popupWindowListener != null) {
                this.popupWindowListener.onOKClick(
                        yearPicker.getCurrentItemPosition() + currentYear - 99,
                        monthPicker.getCurrentItemPosition());
            }
            this.dismiss();
        }
    }

    @Override
    public void onDismiss() {
        darkenBackground(parentActivity, 1.0f);
        if (this.popupWindowListener != null) {
            this.popupWindowListener.onDismissListener();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
        // 这里如果返回true的话，touch事件将被拦截
        // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
    }


    /**
     * 控制弹出popupView屏幕变暗与恢复
     *
     * @param bgcolor
     */
    protected void darkenBackground(Activity activity, Float bgcolor) {
        if (blackType == BlackType.BLACK_ALL) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgcolor;
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            activity.getWindow().setAttributes(lp);
        }
    }

    public interface PopupWindowListener {
        void onOKClick(int selYear, int selMonth);
        void onBottomButtonClickListener();
        void onDismissListener();
    }
}