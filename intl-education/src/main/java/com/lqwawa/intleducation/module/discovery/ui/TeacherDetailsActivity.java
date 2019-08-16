package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.lqwawa.intleducation.module.discovery.vo.TeacherDetailsVo;
import com.lqwawa.intleducation.module.discovery.vo.TeacherVo;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.Date;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 老师详细信息
 */
public class TeacherDetailsActivity extends MyBaseFragmentActivity implements View.OnClickListener
        , ScrollViewEx.ScrollViewListener {
    private static final String TAG = "TeacherDetailsActivity";

    private TopBar topBar;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ScrollViewEx scrollView;
    private ImageView imageViewCover;
    private ImageView imageViewTeacherAvatar;
    private TextView textViewIntroduction;
    private RadioGroup rg_tab;
    private RadioGroup rg_tab_f;

    private String teacherId;
    private TeacherVo teacherVo;

    private int img_width;
    private int img_height;
    private ImageOptions imageOptionsAvatar;

    TeacherDetailsFragment courseFragment;
    TeacherDetailsFragment resourceFragment;
    OnLoadStatusChangeListener onLoadStatusChangeListener;
    private boolean isFriend;
    private boolean canLoadMore = false;

    public static void start(Activity activity, String teacherId) {
        activity.startActivity(new Intent(activity, TeacherDetailsActivity.class)
                .putExtra("id", teacherId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.showBottomSplitView(false);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        scrollView = (ScrollViewEx) findViewById(R.id.scrollview);
        imageViewCover = (ImageView) findViewById(R.id.cover_iv);
        imageViewTeacherAvatar = (ImageView) findViewById(R.id.teacher_avatar_iv);
        textViewIntroduction = (TextView) findViewById(R.id.introduction_tv);
        rg_tab = (RadioGroup) findViewById(R.id.rg_tab);
        rg_tab_f = (RadioGroup) findViewById(R.id.rg_tab_f);

        teacherId = getIntent().getStringExtra("id");
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
        pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                if (courseFragment.isVisible()) {
                    courseFragment.getMore();
                }
            }
        });
        //pullToRefreshView.hideFootView();
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        //初始化顶部工具条
        topBar.setBack(true);
        topBar.setTranslationBackground(true);
        int p_width = getWindowManager().getDefaultDisplay().getWidth();
        int p_height = getWindowManager().getDefaultDisplay().getHeight();
        img_width = p_width;
        img_height = img_width * 315 / 720;
        findViewById(R.id.fragment_container).setMinimumHeight(p_height);
        imageViewCover.setLayoutParams(new FrameLayout.LayoutParams(img_width, img_height));
        imageOptionsAvatar = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.ic_avatar_def, false, true, null);

        btnReload.setOnClickListener(this);
        scrollView.setScrollViewListener(this);
        initTabAndFragment();
        initData();
    }

    private void updateData() {
        if (courseFragment.isVisible()) {
            courseFragment.updateData();
        }
        if (resourceFragment.isVisible()) {
            resourceFragment.updateData();
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
                pullToRefreshView.onHeaderRefreshComplete();
                pullToRefreshView.onFooterRefreshComplete();
                if (courseFragment.isVisible()) {
                    pullToRefreshView.setLoadMoreEnable(canLoadMore);
                } else {
                    pullToRefreshView.setLoadMoreEnable(false);
                }
                TeacherDetailsActivity.this.canLoadMore = canLoadMore;
            }

            @Override
            public void onCommitComment() {

            }
        };
        courseFragment = new TeacherDetailsFragment();
        resourceFragment = new TeacherDetailsFragment();
        courseFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        resourceFragment.setOnLoadStatusChangeListener(onLoadStatusChangeListener);
        Bundle bundle1 = new Bundle();
        bundle1.putInt("type", 2);
        bundle1.putString("id", teacherId);
        courseFragment.setArguments(bundle1);
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 3);
        bundle2.putString("id", teacherId);
        resourceFragment.setArguments(bundle2);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, courseFragment);
        fragmentTransaction.add(R.id.fragment_container, resourceFragment);
        fragmentTransaction.hide(resourceFragment);
        fragmentTransaction.show(courseFragment);
        fragmentTransaction.commit();

        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction fragmentTransaction =
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(courseFragment);
                fragmentTransaction.hide(resourceFragment);
                if (checkedId == R.id.rb_course) {
                    fragmentTransaction.show(courseFragment);
                } else if (checkedId == R.id.rb_resource) {
                    fragmentTransaction.show(resourceFragment);
                }
                if (rg_tab.getVisibility() == View.VISIBLE) {
                    if (checkedId == R.id.rb_course) {
                        fragmentTransaction.show(courseFragment);
                        rg_tab_f.check(R.id.rb_course_f);
                    } else if (checkedId == R.id.rb_resource) {
                        fragmentTransaction.show(resourceFragment);
                        rg_tab_f.check(R.id.rb_resource_f);
                    }
                } else if (rg_tab_f.getVisibility() == View.VISIBLE) {
                    if (checkedId == R.id.rb_course_f) {
                        fragmentTransaction.show(courseFragment);
                        rg_tab.check(R.id.rb_course);
                    } else if (checkedId == R.id.rb_resource_f) {
                        fragmentTransaction.show(resourceFragment);
                        rg_tab.check(R.id.rb_resource);
                    }
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        rg_tab.setOnCheckedChangeListener(listener);
        rg_tab_f.setOnCheckedChangeListener(listener);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            initData();
        }
    }

    private void flowTheTeacher() {
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.login(this);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("teacherId", teacherId);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.FollowTheTeacher + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.send)
                                    + getResources().getString(R.string.success)
                                    + "!");
                    //textViewFlowTheTeacher.setVisibility(View.GONE);//成为他的学生成功 隐藏收藏按钮
                } else {
                    ToastUtil.showToast(activity,
                            getResources().getString(R.string.send)
                                    + getResources().getString(R.string.failed)
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "失败:" + throwable.getMessage());
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void initData() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("teacherId", teacherId);
        requestVo.addParams("dataType", 1);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetTeacherDetailsById + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TeacherDetailsVo result = JSON.parseObject(s,
                        new TypeReference<TeacherDetailsVo>() {
                        });
                if (result.getCode() == 0) {
                    isFriend = result.isIsFriend();
                    if (result.getTeacher() != null) {
                        if (result.getTeacher().size() > 0) {
                            teacherVo = result.getTeacher().get(0);
                        }
                    }
                    updateView();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取老师详情数据失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void updateView() {
        if (teacherVo == null) {
            return;
        }
        topBar.setTitle(teacherVo.getName() + "");
        XImageLoader.loadImage(imageViewTeacherAvatar,
                teacherVo.getThumbnail().trim(),
                imageOptionsAvatar);
        String teacherIntroduction = teacherVo.getIntroduction();
        textViewIntroduction.setText(teacherIntroduction);

        if (isFriend) {
            topBar.setRightFunctionText1("", null);
        } else {
            topBar.setRightFunctionText1(getString(R.string.follow_the_teacher),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            flowTheTeacher();
                        }
                    });
        }
    }
}
