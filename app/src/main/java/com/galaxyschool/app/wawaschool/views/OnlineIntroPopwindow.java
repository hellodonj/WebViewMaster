package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;
import com.galaxyschool.app.wawaschool.pojo.PerformClassList;

import java.util.List;

public class OnlineIntroPopwindow extends PopupWindow implements View.OnClickListener {

    private View mRootView;
    private Context mContext;
    private int height;
    private Emcee onlineRes;
    private PerformClassList performClassList;
    private boolean isFromMOOC = false;

    public OnlineIntroPopwindow(Context mContext, PerformClassList performClassList, int height) {
        super(mContext);
        this.mContext = mContext;
        this.performClassList = performClassList;
        this.height = height;
        initActView();
        setProperty();
    }

    public OnlineIntroPopwindow(Context mContext, Emcee onlineRes, int height, boolean isFromMOOC) {
        super(mContext);
        this.mContext = mContext;
        this.height = height;
        this.onlineRes = onlineRes;
        this.isFromMOOC = isFromMOOC;
        initOnlineView();
        setProperty();
    }

    public OnlineIntroPopwindow(Context mContext,int height,String commentDetail){
        super(mContext);
        this.mContext = mContext;
        this.height = height;
        initCommentDetailData(commentDetail);
        setProperty();
    }

    /**
     * 表演课堂的数据
     */
    private void initActView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_act_production_introduction,
                null);

        TextView actIntro = (TextView) mRootView.findViewById(R.id.act_introduction);
        TextView actDefaultThumbnail = (TextView) mRootView.findViewById(R.id.act_default_thumbnail);
        if (actIntro != null) {
            if (performClassList != null) {
                //根据条件判断 如果Intro为空 显示默认的暂无内容图片
                if (TextUtils.isEmpty(performClassList.getIntro())) {
                    actDefaultThumbnail.setVisibility(View.VISIBLE);
                    actIntro.setVisibility(View.GONE);
                } else {
                    actIntro.setText(Html.fromHtml(performClassList.getIntro()));
                    actDefaultThumbnail.setVisibility(View.GONE);
                    actIntro.setVisibility(View.VISIBLE);
                }
            }
        }

        TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_online_title);
        if (tvTitle != null) {
            tvTitle.setText(performClassList.getTitle());
        }
        ImageView ivArrow = (ImageView) mRootView.findViewById(R.id.iv_arrow);
        if (ivArrow != null) {
            ivArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    /**
     * 空中课堂的数据
     */
    private void initOnlineView() {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.classroom_introduction_item, null);
        TextView tvHost = (TextView) mRootView.findViewById(R.id.online_host_list_mooc);
        if (isFromMOOC){
            tvHost.setSingleLine(true);
            tvHost.setEllipsize(TextUtils.TruncateAt.END);
        }
        TextView tvHostItem = (TextView) mRootView.findViewById(R.id.online_host_item);
        TextView tvHide = (TextView) mRootView.findViewById(R.id.online_host_list);
        if (tvHide != null){
            tvHide.setVisibility(View.GONE);
        }
        if (tvHostItem != null) {
            tvHostItem.setText(new StringBuilder(mContext.getResources()
                    .getString(R.string.online_host)).append("：").toString());
        }
        if (tvHost != null) {
            tvHost.setVisibility(View.VISIBLE);
            if (onlineRes != null) {
                StringBuilder builder = new StringBuilder();
                List<EmceeList> reporterList = onlineRes.getEmceeList();
                if (reporterList != null && reporterList.size() > 0) {
                    for (int i = 0; i < reporterList.size(); i++) {
                        String hostName = reporterList.get(i).getRealName();
                        if (TextUtils.isEmpty(hostName)){
                            hostName = reporterList.get(i).getNickName();
                        }
                        if (i == 0) {
                            builder.append(hostName);
                        } else {
                            builder.append("  ").append(hostName);
                        }
                    }
                }
                tvHost.setText(builder.toString());
                if (!TextUtils.isEmpty(onlineRes.getEmceeNames())) {
                    tvHost.setText(onlineRes.getEmceeNames());
                }
            }
        }
        TextView tvIntro = (TextView) mRootView.findViewById(R.id.online_introduction);
        if (tvIntro != null) {
            if (onlineRes != null) {
                tvIntro.setText(onlineRes.getIntro());
            }
        }

        TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_online_title);
        if (tvTitle != null) {
            tvTitle.setText(onlineRes.getTitle());
        }

        ImageView ivArrow = (ImageView) mRootView.findViewById(R.id.iv_arrow);
        if (ivArrow != null) {
            ivArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        LinearLayout layTime = (LinearLayout) mRootView.findViewById(R.id.time_root_lay);
        TextView textViewTime = (TextView) mRootView.findViewById(R.id.online_time);

        if (layTime != null) {
            layTime.setVisibility(View.VISIBLE);
        }
        if (textViewTime != null) {
            textViewTime.setText(getCurrentOnlineTime(onlineRes));
        }

        if (isFromMOOC) {
            LinearLayout laySourceName = (LinearLayout) mRootView.findViewById(R.id.source_root_lay);
            TextView textViewSourceName = (TextView) mRootView.findViewById(R.id.online_source_name);

            LinearLayout layPrice = (LinearLayout) mRootView.findViewById(R.id.price_root_lay);
            TextView textViewPrice = (TextView) mRootView.findViewById(R.id.online_price);

            if (laySourceName != null) {
                laySourceName.setVisibility(View.VISIBLE);
            }
            if (textViewSourceName != null) {
                textViewSourceName.setText(onlineRes.getSchoolName());
            }
            if (layPrice != null) {
                layPrice.setVisibility(View.VISIBLE);
            }
            if (textViewPrice != null) {
                textViewPrice.setText(onlineRes.getPayType() == 0
                        ? mContext.getResources().getString(R.string.free)
                        : "¥" + onlineRes.getPrice());
            }
        }
    }

    private String getCurrentOnlineTime(Emcee data) {
        String beginTime = data.getStartTime();
        String endTime = data.getEndTime();
        if (!TextUtils.isEmpty(beginTime)) {
            beginTime = beginTime.substring(0, beginTime.length() - 3);
        }
        if (!TextUtils.isEmpty(endTime)) {
            int dayNum = DateUtils.compareDate(endTime,beginTime);
            if (dayNum < 1){
                endTime = endTime.substring(endTime.length() - 8, endTime.length() - 3);
            } else {
                endTime = endTime.substring(0, endTime.length() - 3);
            }
        }
        String showTime = beginTime + " -- " + endTime;
        if (TextUtils.isEmpty(showTime)) {
            return "";
        }
        return showTime;
    }

    private void initCommentDetailData(String commentDetail){
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_act_production_introduction, null);
        TextView actIntro = (TextView) mRootView.findViewById(R.id.act_introduction);
        TextView actDefaultThumbnail = (TextView) mRootView.findViewById(R.id.act_default_thumbnail);
        if (actIntro != null) {
            actDefaultThumbnail.setVisibility(View.GONE);
            actIntro.setText(commentDetail);
            actIntro.setVisibility(View.VISIBLE);
        }

        TextView tvTitle = (TextView) mRootView.findViewById(R.id.tv_online_title);
        if (tvTitle != null) {
            //显示老师评语
            tvTitle.setText(mContext.getString(R.string.str_teacher_comment_no_point));
        }
        ImageView ivArrow = (ImageView) mRootView.findViewById(R.id.iv_arrow);
        if (ivArrow != null) {
            ivArrow.setOnClickListener(v -> dismiss());
        }
    }

    private void setProperty() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        this.setContentView(mRootView);
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        this.setHeight(ScreenUtils.getScreenHeight(mContext) - height - getStatusBarHeight());
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        this.setAnimationStyle(R.style.AnimBottom);
    }

    public void showPopupMenu(View v) {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }

    /**
     * 状态栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void showPopupMenu() {
        if (!this.isShowing()) {
            this.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

    }
}
