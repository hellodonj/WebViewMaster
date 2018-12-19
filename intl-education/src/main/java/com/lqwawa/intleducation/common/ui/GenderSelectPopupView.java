package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import com.lqwawa.intleducation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class GenderSelectPopupView extends CommonListPopupView {
    public static final int[] GENDER_STR_IDS = new int[]{
            R.string.gender_secrecy,
            R.string.gender_man,
            R.string.gender_woman
    };
    public GenderSelectPopupView(Activity activity,
                                 List<String> data,
                                 BlackType type,
                                 PopupWindowListener listener) {
        super(activity, data, type, listener);
    }

    public GenderSelectPopupView(Activity activity,
                                 List<String> data,
                                 PopupWindowListener listener) {
        super(activity, data, BlackType.BLACK_TOP, listener);
        textViewTitle.setVisibility(View.GONE);
        textViewBottomButton.setVisibility(View.GONE);
    }

    public static void showView(Activity activity,PopupWindowListener listener){
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < GENDER_STR_IDS.length; i++){
            data.add(activity.getResources().getString(GENDER_STR_IDS[i]));
        }
        GenderSelectPopupView view = new GenderSelectPopupView(activity, data, listener);
        view.showAtLocation(activity.getWindow().getDecorView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (this.popupWindowListener != null){
            this.popupWindowListener.onItemClickListener(arg2);
        }
        this.dismiss();
    }
}
