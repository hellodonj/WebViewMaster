package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class ImageSelectPopupView extends CommonListPopupView {
    private boolean isImage;

    public ImageSelectPopupView(Activity activity,
                                List<String> data,
                                BlackType type,
                                PopupWindowListener listener) {
        super(activity, data, type, listener);

    }

    public ImageSelectPopupView(Activity activity,
                                List<String> data,
                                boolean isImage,
                                PopupWindowListener listener) {
        super(activity, data, BlackType.BLACK_TOP, listener);
        this.isImage = isImage;
        textViewTitle.setText(activity.getResources().getString(R.string.select_avatar));
        textViewBottomButton.setText(activity.getResources().getString(R.string.cancel));
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    public static void showView(Activity activity, boolean isImage,
                                PopupWindowListener listener) {
        List<String> data = new ArrayList<String>();
        data.add(activity.getResources().getString(R.string.camera));
        data.add(activity.getResources().getString(R.string.gallery));
        ImageSelectPopupView view = new ImageSelectPopupView(activity, data, isImage, listener);
        view.showAtLocation(activity.getWindow().getDecorView(),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (this.popupWindowListener != null){
            this.popupWindowListener.onItemClickListener(adapter.getItem(arg2));
        }
        if (arg2 == 0) {
            if (isImage) {
                ActivityUtils.startTakePhoto(parentActivity);
            } else {
                ActivityUtils.startTakeIconPhoto(parentActivity);
            }
        } else if (arg2 == 1) {
            ActivityUtils.startFetchPhoto(parentActivity);
            dismiss();
        }
        this.dismiss();
    }
}