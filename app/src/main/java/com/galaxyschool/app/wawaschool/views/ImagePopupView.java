package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.osastudio.common.utils.PhotoUtils;

import java.io.File;


public class ImagePopupView extends PopupWindow implements OnClickListener, OnTouchListener {

    private View mRootView;
    private TextView mTakePhotoBtn, mFetchPhotoBtn, mCancelBtn;
    private LayoutInflater mLayoutInflater;
    private Activity mContext;
    private boolean mIsImage;

    public ImagePopupView(Activity context, boolean isImage) {
        super(context);
        mContext = context;
        mIsImage = isImage;

        mLayoutInflater = (LayoutInflater) mContext
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
    }

    private void initViews() {
        mRootView = mLayoutInflater.inflate(R.layout.image_popup_view, null);
        mTakePhotoBtn = (TextView) mRootView.findViewById(R.id.image_take_photo_btn);
        mFetchPhotoBtn = (TextView) mRootView.findViewById(R.id.image_fetch_photo_btn);
        mCancelBtn = (TextView) mRootView.findViewById(R.id.image_cancel_btn);
        mRootView.setOnTouchListener(this);
        mTakePhotoBtn.setOnClickListener(this);
        mFetchPhotoBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        setContentView(mRootView);
        setFocusable(true);
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        int color = mContext.getResources().getColor(R.color.popup_root_view_bg);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        setBackgroundDrawable(colorDrawable);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //拍照
            case R.id.image_take_photo_btn:
                // TODO: 2017/9/30  检查权限(6.0以上做权限判断)
                    openCamera();

                break;
            //从相册选取
            case R.id.image_fetch_photo_btn:
                PhotoUtils.startFetchPhoto(mContext);
                break;
            case R.id.image_cancel_btn:
                break;
            default:
                break;
        }
        dismiss();

    }

    /**
     * 打开系统相机
     */
    private void openCamera() {

        String urlFolder = Utils.ICON_FOLDER;
        String url = urlFolder + Utils.ICON_NAME;
        if (mIsImage) {
            urlFolder = Utils.IMAGE_FOLDER;
            url = urlFolder + Utils.TEMP_IMAGE_NAME;
        }
        File fileFolder = new File(urlFolder);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }

        File file = new File(url);

        PhotoUtils.startTakePhoto(mContext,file, PhotoUtils.REQUEST_CODE_TAKE_PHOTO);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mRootView.findViewById(R.id.image_pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                dismiss();
            }
        }
        return true;
    }
}
