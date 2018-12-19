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

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;

/**
 * 校本资源库收藏
 */
public class CollectPopupView extends PopupWindow implements OnClickListener, OnTouchListener {

    private View mRootView;
    private TextView mFetchPhotoBtn, mCancelBtn;
    private LayoutInflater mLayoutInflater;
    private Activity mContext;
    private UserInfo userInfo;
    private String MType,MicroID,Title,Author,Tag;

    /**
     *
     * @param context  Activity
     * @param microId 	资源ID
     * @param title     资源标题
     * @param authorId  作者id

     */
    public CollectPopupView(Activity context, String microId, String title, String authorId,String tag) {
        super(context);
        mContext = context;
        this.MicroID = microId;
        this.Title = title;
        this.Author = authorId;
        this.Tag = tag;
        userInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
    }

    private void initViews() {
        mRootView = mLayoutInflater.inflate(R.layout.collect_popup_view, null);

        mFetchPhotoBtn = (TextView) mRootView.findViewById(R.id.collect_pop_btn);
        mCancelBtn = (TextView) mRootView.findViewById(R.id.collect_pop_cancel_btn);
        mRootView.setOnTouchListener(this);
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
            case R.id.collect_pop_btn:
                doCollect();
                dismiss();
                break;
            case R.id.collect_pop_cancel_btn:
                dismiss();
                break;
        }

    }

    private void doCollect() {
        CollectionHelper collectionHelper = new CollectionHelper(mContext);
        collectionHelper.collectDifferentResource(MicroID,Title,Author,Tag);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mRootView.findViewById(R.id.collect_pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                dismiss();
            }
        }
        return true;
    }
}
