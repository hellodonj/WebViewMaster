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

import java.util.List;


/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class CommonPickerPopupView extends PopupWindow
        implements PopupWindow.OnDismissListener,
        View.OnClickListener, View.OnTouchListener{
    protected List<String> list;
    protected WheelPicker wheelPicker;
    protected Activity parentActivity;
    protected View contentView;
    protected int currentIndex;
    protected View topView;
    protected View bottomView;
    protected TextView textViewTitle;
    protected TextView textViewBottomButton;
    protected BlackType blackType = BlackType.BLACK_ALL;

    public enum BlackType {
        BLACK_ALL, BLACK_TOP, BLACK_BOTTOM
    }

    PopupWindowListener popupWindowListener;

    public CommonPickerPopupView(Activity activity,
                                 List<String> data,
                                 BlackType type,
                                 int currentIndex,
                                 PopupWindowListener listener) {
        this.parentActivity = activity;
        this.list = data;
        this.blackType = type;
        this.currentIndex = currentIndex;
        this.popupWindowListener = listener;

        LayoutInflater inflater = activity.getLayoutInflater();
        contentView = inflater.inflate(R.layout.widgets_common_picker_pop_window, null);
        this.setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        wheelPicker = (WheelPicker) contentView.findViewById(R.id.listView);
        topView = (View) contentView.findViewById(R.id.pop_top_bg);
        bottomView = (View) contentView.findViewById(R.id.pop_bottom_bg);
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
        wheelPicker.setData(list);
        wheelPicker.setSelectedItemPosition(currentIndex);
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
                this.popupWindowListener.onOKClick(wheelPicker.getCurrentItemPosition());
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
        void onOKClick(int index);
        void onBottomButtonClickListener();
        void onDismissListener();
    }
}