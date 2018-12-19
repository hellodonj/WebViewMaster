package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.HeaderAndFooterGridView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.IndexCourseAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.CredentialDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.CredentialVo;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.Date;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * ֤证书详情
 */
public class CredentialDetailsActivity extends MyBaseActivity implements View.OnClickListener
        , AbsListView.OnScrollListener {
    private static final String TAG = "CredentialDetailsActivity";
    private PullToRefreshView pullToRefresh;
    private HeaderAndFooterGridView itemGridView;
    private TopBar topBar;
    private ImageView coverIv;
    private TextView credentialNameTv;
    private TextView credentialIntroductionTv;
    private TextView credentialCostTv;
    private TextView conditionGetTv;
    private LinearLayout itemTitle;
    private TextView titleName;
    private Button buttonJoin;
    private String credentialId;

    private CredentialDetailsVo credentialDetailsVo;
    private IndexCourseAdapter courseAdapter;

    private int img_width;
    private int img_height;
    private ImageOptions imageOptions;
    private int mTopBarTransStatus = 0;

    private int joinStatus = 0;//0未加入1申请认证2去支付


    public static void start(Activity activity, String id) {
        activity.startActivity(new Intent(activity, CredentialDetailsActivity.class)
                .putExtra("id", id));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential_details);
        pullToRefresh = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        itemGridView = (HeaderAndFooterGridView) findViewById(R.id.item_grid_view);
        topBar = (TopBar) findViewById(R.id.top_bar);
        credentialId = getIntent().getStringExtra("id");
        initViews();
        initData();
    }

    private void initViews() {
        View headView = activity.getLayoutInflater().inflate(
                R.layout.activity_credential_details_head,
                itemGridView, false);
        itemGridView.addHeaderView(headView);
        coverIv = (ImageView) headView.findViewById(R.id.cover_iv);
        credentialNameTv = (TextView) headView.findViewById(R.id.credential_name_tv);
        credentialIntroductionTv = (TextView) headView.findViewById(R.id.credential_introduction_tv);
        credentialCostTv = (TextView) headView.findViewById(R.id.credential_cost_tv);
        conditionGetTv = (TextView) headView.findViewById(R.id.condition_get_tv);
        headView.findViewById(R.id.join_bt).setOnClickListener(this);
        itemTitle = (LinearLayout) headView.findViewById(R.id.item_title);
        titleName = (TextView) headView.findViewById(R.id.title_name);
        buttonJoin = (Button) headView.findViewById(R.id.join_bt);

        int p_width = getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width;
        img_height = img_width * 315 / 720;
        coverIv.setLayoutParams(new FrameLayout.LayoutParams(img_width, img_height));
        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setCircular(true)
                .setLoadingDrawableId(R.drawable.ic_avatar_def)//
                .setFailureDrawableId(R.drawable.ic_avatar_def)//
                .build();

        //初始化顶部工具条
        topBar.setBack(true);
        topBar.setTranslationBackground(true);
        topBar.showBottomSplitView(false);

        titleName.setText(getResources().getString(R.string.course));
        pullToRefresh.setLoadMoreEnable(false);
        pullToRefresh.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefresh.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                getMore();
            }
        });
        pullToRefresh.setLastUpdated(new Date().toLocaleString());
        courseAdapter = new IndexCourseAdapter(this, true);
        itemGridView.setAdapter(courseAdapter);
        itemGridView.setNumColumns(2);
        itemGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(),true, UserHelper.getUserId());
            }
        });

        itemGridView.setOnScrollListener(this);
        buttonJoin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.join_bt) {
            if (!UserHelper.isLogin()) {
                LoginActivity.loginForResult(activity);
                return;
            }
            if (joinStatus == 0) {//加入认证学习计划
                addCertificationPlan();
            } else if (joinStatus == 1) {//申请认证
                applyCertification();
            } else if (joinStatus == 2) {//提交订单
                ToastUtil.showToast(activity, "暂未实现");
            } else if (joinStatus == 3) {//去支付
                ToastUtil.showToast(activity, "暂未实现");
            }
        }
    }

    private void addCertificationPlan() {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.loginForResult(this);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("certificationId", credentialId);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.AddCertificationPlan + requestVo.getParams());


        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.join_stuty_plan)
                                    + getResources().getString(R.string.success)
                                    + "!");
                    sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.AddCertificationPlan));
                    initData();
                } else {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.join_stuty_plan)
                                    + getResources().getString(R.string.failed)
                                    + "!");
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "加入证书学习计划失败:" + throwable.getMessage());

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    //申请认证
    private void applyCertification() {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.loginForResult(this);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("certificationId", credentialId);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.ApplyCertification + requestVo.getParams());


        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.apply_certification)
                                    + getResources().getString(R.string.success)
                                    + "!");

                    sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.AddCertificationPlan));
                    initData();
                } else {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.apply_certification)
                                    + getResources().getString(R.string.failed)
                                    + "：" + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "申请认证失败:" + throwable.getMessage());

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_CODE_LOGIN &&
                resultCode == Activity.RESULT_OK) {
            getData();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem == 0) {
            View headView = itemGridView.getChildAt(0);
            if (headView != null) {
                int headViewTop = headView.getTop();
                if (headViewTop < 0) {
                    mTopBarTransStatus = 1;
                    if (headViewTop < -0xee) {
                        topBar.setBackgroundColor(getResources().getColor(R.color.com_bg_white));
                        topBar.showBottomSplitView(true);
                    } else {
                        String ap = Integer.toHexString(Math.abs(headViewTop));
                        if (ap.length() == 1) {
                            ap = "0" + ap;
                        }
                        String colorString = "#" + ap + "ffffff";
                        LogUtil.d(TAG, colorString);
                        topBar.setBackgroundColor(Color.parseColor(colorString));
                        topBar.showBottomSplitView(false);
                    }
                } else {
                    if (mTopBarTransStatus != 0) {
                        mTopBarTransStatus = 0;
                        topBar.setBackgroundColor(Color.TRANSPARENT);
                        topBar.showBottomSplitView(false);
                    }
                }
            }
        } else {
            if (mTopBarTransStatus != 1) {
                mTopBarTransStatus = 1;
                topBar.setBackgroundColor(getResources().getColor(R.color.com_bg_trans_black));
            }
        }
    }

    private void initData() {
        pullToRefresh.showRefresh();
        getData();
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        pullToRefresh.setLoadMoreEnable(false);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", credentialId);
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCredentiialDetails + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onHeaderRefreshComplete();
                CredentialDetailsVo result = JSON.parseObject(s,
                        new TypeReference<CredentialDetailsVo>() {
                        });
                LogUtil.d(TAG, result.toString());
                if (result.getCode() == 0) {
                    credentialDetailsVo = result;
                    updateView();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取证书详情失败:" + throwable.getMessage());
                pullToRefresh.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getMore() {
        pageIndex = 0;
        pullToRefresh.setLoadMoreEnable(false);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", credentialId);
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCredentiialDetails + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onFooterRefreshComplete();
                CredentialDetailsVo result = JSON.parseObject(s,
                        new TypeReference<CredentialDetailsVo>() {
                        });
                LogUtil.d(TAG, result.toString());
                if (result.getCode() == 0) {
                    pageIndex++;
                    if (result.getCourseList() != null
                            && result.getCourseList().size() > 0) {
                        pullToRefresh.setLoadMoreEnable(
                                result.getCourseList().size() >= AppConfig.PAGE_SIZE);
                        credentialDetailsVo.getCourseList().addAll(result.getCourseList());
                        courseAdapter.addData(result.getCourseList());
                        courseAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "加载更多失败:" + throwable.getMessage());
                pullToRefresh.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void updateView() {
        if (credentialDetailsVo != null) {
            buttonJoin.setVisibility(View.VISIBLE);
            if (!credentialDetailsVo.isIsAdd()) {//没有加入
                buttonJoin.setCompoundDrawables(null, null, null, null);
                buttonJoin.setEnabled(true);
                buttonJoin.setBackground(getResources().getDrawable(R.drawable.com_green_bt_bg));
                buttonJoin.setTextColor(getResources().getColor(R.color.com_text_white));
                joinStatus = 0;
                buttonJoin.setText(getResources().getString(R.string.join_stuty_plan));
            } else {
                if (!credentialDetailsVo.isCourseFinish()) {//已加入 但没有完成课程
                    buttonJoin.setCompoundDrawables(null, null, null, null);
                    buttonJoin.setEnabled(false);
                    buttonJoin.setBackground(getResources().getDrawable(R.drawable.com_gray_bt_bg));
                    buttonJoin.setTextColor(getResources().getColor(R.color.com_text_black));
                    buttonJoin.setText(getResources().getString(R.string.joined_whit_not_finish));
                } else {//已完成课程
                    if (!credentialDetailsVo.isIsApply()) {//已完成课程但是没有认证
                        buttonJoin.setCompoundDrawables(null, null, null, null);
                        buttonJoin.setEnabled(true);
                        buttonJoin.setBackground(getResources().getDrawable(R.drawable.com_green_bt_bg));
                        buttonJoin.setTextColor(getResources().getColor(R.color.com_text_white));
                        joinStatus = 1;
                        buttonJoin.setText(getResources().getString(R.string.joined));
                    } else {//已完成课程而且已经申请了认证
                        if (!credentialDetailsVo.isIsBuy()) {//已经申请了认证但是没有购买
                            buttonJoin.setCompoundDrawables(null, null, null, null);
                            buttonJoin.setBackground(getResources().getDrawable(R.drawable.com_green_bt_bg));
                            buttonJoin.setTextColor(getResources().getColor(R.color.com_text_white));
                            buttonJoin.setEnabled(true);
                            joinStatus = 2;
                            buttonJoin.setText(getResources().getString(R.string.has_apply_to_pay));
                        } else {//已认证而且已确认订单
                            if (!credentialDetailsVo.isIsPay()) {//已经确认订单但是没有购买
                                buttonJoin.setCompoundDrawables(null, null, null, null);
                                buttonJoin.setBackground(getResources().getDrawable(R.drawable.com_green_bt_bg));
                                buttonJoin.setTextColor(getResources().getColor(R.color.com_text_white));
                                buttonJoin.setEnabled(true);
                                joinStatus = 3;
                                buttonJoin.setText(getResources().getString(R.string.has_buy_to_pay));
                            } else {//已认证而且支付完成了
                                buttonJoin.setCompoundDrawables(
                                        getResources().getDrawable(R.drawable.ic_has_pay)
                                        , null, null, null);
                                buttonJoin.setBackgroundColor(getResources().getColor(R.color.translation));
                                buttonJoin.setTextColor(getResources().getColor(R.color.com_text_green));
                                buttonJoin.setEnabled(false);
                                buttonJoin.setText(getResources().getString(R.string.has_certification
                                ));
                            }
                        }
                    }
                }
            }
            if (credentialDetailsVo.getCertification() != null &&
                    credentialDetailsVo.getCertification().size() > 0) {
                CredentialVo credentialVo = credentialDetailsVo.getCertification().get(0);
                if (credentialVo != null) {
                    x.image().bind(coverIv,
                            credentialVo.getThumbnail().trim(),
                            imageOptions);
                    credentialNameTv.setText(credentialVo.getName() + "");
                    credentialIntroductionTv.setText(credentialVo.getIntroduction() + "");
                    credentialCostTv.setText("¥" + credentialVo.getFee());
                    conditionGetTv.setText(credentialVo.getAchieveCondition());
                }
            }
        }else {
            buttonJoin.setVisibility(View.GONE);
        }
        pullToRefresh.setLoadMoreEnable(credentialDetailsVo.getCourseList() != null &&
                credentialDetailsVo.getCourseList().size() >= AppConfig.PAGE_SIZE);
        courseAdapter.setData(credentialDetailsVo.getCourseList());
        courseAdapter.notifyDataSetChanged();
    }
}
