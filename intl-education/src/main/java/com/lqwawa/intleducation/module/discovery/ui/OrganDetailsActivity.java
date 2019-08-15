package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.ScrollViewEx;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 机构详情
 */
public class OrganDetailsActivity extends MyBaseFragmentActivity implements View.OnClickListener
        , ScrollViewEx.ScrollViewListener {
    private static String TAG = "OrganDetailsActivity";

    private TopBar topBar;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;

    private ScrollViewEx scrollView;
    private ImageView imageViewCover;
    private TextView textViewOrganInfo;

    private RadioGroup rg_tab;
    private RadioGroup rg_tab_f;
    private OrganVo organVo;
    private String organId;

    private int img_width;
    private int img_height;
    private ImageOptions imageOptions;

    OrganDetailsItemFragment courseFragment;
    OrganDetailsItemFragment teacherFragment;
    OrganDetailsItemFragment credentialFragment;
    OnLoadStatusChangeListener onLoadStatusChangeListener;

    public static void start(Activity activity, String id) {
        activity.startActivity(new Intent(activity, OrganDetailsActivity.class)
                .putExtra("OrganId", id));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organ_details);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.showBottomSplitView(false);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        scrollView = (ScrollViewEx) findViewById(R.id.scrollview);
        imageViewCover = (ImageView) findViewById(R.id.cover_iv);
        textViewOrganInfo = (TextView) findViewById(R.id.organ_info_tv);

        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        rg_tab_f = (RadioGroup) findViewById(R.id.rg_tab_f);

        organId = getIntent().getStringExtra("OrganId");
        initViews();
    }

    private void initViews() {
        //初始化下拉刷新
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                updateData();
            }
        });
        pullToRefreshView.hideFootView();
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        //初始化顶部工具条
        topBar.setBack(true);
        topBar.setTranslationBackground(true);
        //初始化封面
        int p_width = getWindowManager().getDefaultDisplay().getWidth();
        int p_height = getWindowManager().getDefaultDisplay().getHeight();
        img_width = p_width;
        img_height = img_width * 315 / 720;
        imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.img_def, false, false, null);

        findViewById(R.id.fragment_container).setMinimumHeight(p_height);
        imageViewCover.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        btnReload.setOnClickListener(this);
        initData();
        initTabAndFragment();
        scrollView.setScrollViewListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            initData();
        }
    }

    private void addAttention() {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.login(this);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId", organVo.getId());
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.AddAttention + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.has)
                                    + getResources().getString(R.string.attention)
                                    + "!");
                    topBar.setRightFunctionText1("", null);//已关注
                } else {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.attention)
                                    + getResources().getString(R.string.failed)
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "关注失败:" + throwable.getMessage());

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }


    //scrollview 滚动条位置变动时调整顶部工具条的状态及tab
    @Override
    public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
        if(y > 1000){
            return;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > 0xee && oldy <= 0xee) {
            topBar.setBackgroundColor(getResources().getColor(R.color.com_bg_white));
            topBar.showBottomSplitView(true);
        } else if (y <= 0xee) {
            String ap = Integer.toHexString(y);
            if (ap.length() == 1) {
                ap = "0" + ap;
            }
            String colorString = "#" + ap + "ffffff";
            LogUtil.d(TAG, colorString);
            topBar.setBackgroundColor(Color.parseColor(colorString));
            topBar.showBottomSplitView(false);
        }

        int tabTop = rg_tab.getTop() - rg_tab.getHeight() - DisplayUtil.dip2px(activity, 8);
        if (y > tabTop && oldy <= tabTop) {
            rg_tab_f.setVisibility(View.VISIBLE);
            rg_tab.setVisibility(View.INVISIBLE);
        } else if (y <= tabTop && oldy > tabTop) {
            rg_tab_f.setVisibility(View.GONE);
            rg_tab.setVisibility(View.VISIBLE);
        }
    }

    private void initTabAndFragment() {
        //下拉刷新异步回调
        onLoadStatusChangeListener = new OnLoadStatusChangeListener() {
            @Override
            public void onLoadSuccess() {
                loadFailedLayout.setVisibility(View.GONE);
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onLoadFlailed() {
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onLoadFinish(boolean canLoadMore) {

            }

            @Override
            public void onCommitComment() {

            }
        };
        //初始化tab分页
        courseFragment = new OrganDetailsItemFragment();
        teacherFragment = new OrganDetailsItemFragment();
        credentialFragment = new OrganDetailsItemFragment();
        courseFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        teacherFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        credentialFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        Bundle bundle1 = new Bundle();
        bundle1.putInt("type", 1);
        bundle1.putString("id", organId);
        courseFragment.setArguments(bundle1);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 2);
        bundle2.putString("id", organId);
        teacherFragment.setArguments(bundle2);
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 3);
        bundle3.putString("id", organId);
        credentialFragment.setArguments(bundle3);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, courseFragment);
        fragmentTransaction.add(R.id.fragment_container, teacherFragment);
        fragmentTransaction.add(R.id.fragment_container, credentialFragment);
        fragmentTransaction.hide(credentialFragment);
        fragmentTransaction.hide(teacherFragment);
        fragmentTransaction.show(courseFragment);
        credentialFragment.setUserVisibleHint(true);
        fragmentTransaction.commit();
        RadioGroup.OnCheckedChangeListener listener =
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        FragmentTransaction fragmentTransaction =
                                getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.hide(courseFragment);
                        fragmentTransaction.hide(teacherFragment);
                        fragmentTransaction.hide(credentialFragment);
                        if (rg_tab.getVisibility() == View.VISIBLE) {
                            if (checkedId == R.id.rb_course) {
                                fragmentTransaction.show(courseFragment);
                                rg_tab_f.check(R.id.rb_course_f);
                            } else if (checkedId == R.id.rb_teacher) {
                                fragmentTransaction.show(teacherFragment);
                                rg_tab_f.check(R.id.rb_teacher_f);
                            } else if (checkedId == R.id.rb_credential) {
                                fragmentTransaction.show(credentialFragment);
                                rg_tab_f.check(R.id.rb_credential_f);
                            }
                        } else if (rg_tab_f.getVisibility() == View.VISIBLE) {
                            if (checkedId == R.id.rb_course_f) {
                                fragmentTransaction.show(courseFragment);
                                rg_tab.check(R.id.rb_course);
                            } else if (checkedId == R.id.rb_teacher_f) {
                                fragmentTransaction.show(teacherFragment);
                                rg_tab.check(R.id.rb_teacher);
                            } else if (checkedId == R.id.rb_credential_f) {
                                fragmentTransaction.show(credentialFragment);
                                rg_tab.check(R.id.rb_credential);
                            }
                        }
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                };
        rg_tab.setOnCheckedChangeListener(listener);
        rg_tab_f.setOnCheckedChangeListener(listener);
    }

    private void initData() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("dataType", 0);
        requestVo.addParams("organId", organId);
        final RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetOrganItemList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<OrganVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<OrganVo>>>() {
                        });
                LogUtil.d(TAG, result.getData().toString());
                if (result.getCode() == 0) {
                    List<OrganVo> newList = result.getData();
                    if (newList != null && newList.size() > 0) {
                        organVo = newList.get(0);
                        updateInfo();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取入驻机构列表失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void updateInfo() {
        if (organVo != null) {
            XImageLoader.loadImage(imageViewCover,
                    organVo.getPublicizeImg().trim(),
                    imageOptions);
            textViewOrganInfo.setText(organVo.getName() + " " + organVo.getIntroduction());
            SpannableStringBuilder builder =
                    new SpannableStringBuilder(textViewOrganInfo.getText().toString());
            ForegroundColorSpan spanName = new ForegroundColorSpan(
                    getResources().getColor(R.color.com_text_green));
            builder.setSpan(spanName, 0, organVo.getName().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewOrganInfo.setText(builder);
            if (organVo.isAttented()) {
                topBar.setRightFunctionText1("", null);
            } else {
                topBar.setRightFunctionText1("+" + getResources().getString(R.string.attention),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addAttention();
                            }
                        });
            }
        }
    }

    private void updateData() {
        if (courseFragment.isVisible()) {
            courseFragment.updateData();
        }
        if (teacherFragment.isVisible()) {
            teacherFragment.updateData();
        }
        if (credentialFragment.isVisible()) {
            credentialFragment.updateData();
        }
    }
}
