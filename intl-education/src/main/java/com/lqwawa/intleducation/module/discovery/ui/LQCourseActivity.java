package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.lqwawa.intleducation.base.widgets.BannerHeaderView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.ClassifyAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.IndexCourseAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.OrganAdapter;
import com.lqwawa.intleducation.module.discovery.vo.BannerInfoVo;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.DiscoveryItemVo;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.ui.MyOrderListActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LQCourseActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "LQCourseActivity";

    public static boolean isShow = false;
    public static void start(final  Activity activity){
        if (isShow) {
            return;
        }
        isShow = true;
        activity.startActivity(new Intent(activity, LQCourseActivity.class));
    }


    private TopBar topBar;
    private PullToRefreshView pullToRefresh;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private NoScrollGridView classifyGridView;

    private List<ClassifyVo> classifyList;
    private ClassifyAdapter classifyAdapter;

    private List<CourseVo> hotCourseList;
    private IndexCourseAdapter hotCourseAdapter;

    private List<CourseVo> latestCourseList;
    private IndexCourseAdapter latestCourseAdapter;

    private List<OrganVo> organList;
    private OrganAdapter organAdapter;

    private NoScrollGridView hotNoScrollGridView;
    private NoScrollGridView latestNoScrollGridView;
    private NoScrollGridView organNoScrollGridView;
    private BannerHeaderView bannerHeaderView;

    private List<ClassifyVo> classVos;
    private ClassifyVo classifyVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_index);
        topBar = (TopBar) findViewById(R.id.top_bar);
        pullToRefresh = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        bannerHeaderView = (BannerHeaderView) findViewById(R.id.banner_header_view);
        classifyGridView = (NoScrollGridView) findViewById(R.id.classify_grid_view);
        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.lq_course));
        if(AppConfig.BaseConfig.needShowPay()) {
            topBar.setRightFunctionText1(activity.getResources().getString(R.string.my_orders), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyOrderListActivity.newInstance(LQCourseActivity.this);
                }
            });
        }
        updateBannerViwe();
        pullToRefresh.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                if (classifyVo == null) {
                    getClassifyData();
                } else {
                    getData();
                }
            }
        });
        pullToRefresh.setLoadMoreEnable(false);
        pullToRefresh.setLastUpdated(new Date().toLocaleString());

        classifyGridView.setNumColumns(2);
        classifyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ClassifyVo vo = (ClassifyVo) classifyAdapter.getItem(position);
                if (vo != null && classifyVo != null) {
                    LQCourseCourseListActivity.start(activity,
                            new ArrayList<>(classVos),
                            position,
                            vo.getConfigValue(), "-1");
                }
            }
        });

        View hotView = activity.getLayoutInflater().inflate(
                R.layout.mod_discovery_item, contentLayout, false);
        TextView hotTitleText = (TextView) hotView.findViewById(R.id.title_name);
        hotTitleText.setText(getText(R.string.hot_recommended));
        LinearLayout hotTitle = (LinearLayout) hotView.findViewById(R.id.item_title);
        hotTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classifyVo != null) {
                    LQCourseCourseListActivity.start(activity,
                            new ArrayList<>(classVos),
                            -1,
                            getString(R.string.hot_recommended), "1");
                }

//                LQCourseCourseListActivity.startForSelRes(activity,9);
            }
        });
        hotNoScrollGridView = (NoScrollGridView) hotView.findViewById(R.id.item_grid_view);
        hotNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) hotCourseAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(), true, UserHelper.getUserId());
            }
        });
        hotNoScrollGridView.setNumColumns(2);
        contentLayout.addView(hotView);

        View latestView = activity.getLayoutInflater().inflate(
                R.layout.mod_discovery_item, contentLayout, false);
        TextView latestTitleText = (TextView) latestView.findViewById(R.id.title_name);
        latestTitleText.setText(getText(R.string.latest_update));
        LinearLayout latestTitle = (LinearLayout) latestView.findViewById(R.id.item_title);
        latestTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classifyVo != null) {
                    LQCourseCourseListActivity.start(activity,
                            new ArrayList<>(classVos),
                            -1,
                            getString(R.string.latest_update), "2");
                }
            }
        });
        latestNoScrollGridView = (NoScrollGridView) latestView.findViewById(R.id.item_grid_view);
        latestNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) latestCourseAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(),true, UserHelper.getUserId());
            }
        });
        latestNoScrollGridView.setNumColumns(2);
        contentLayout.addView(latestView);

        View organView = activity.getLayoutInflater().inflate(
                R.layout.mod_discovery_item, contentLayout, false);
        TextView organTitleText = (TextView) organView.findViewById(R.id.title_name);
        organTitleText.setText(getText(R.string.organ_in));
        LinearLayout organTitle = (LinearLayout) organView.findViewById(R.id.item_title);
        organTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, OrganListActivity.class));
            }
        });
        organNoScrollGridView = (NoScrollGridView) organView.findViewById(R.id.item_grid_view);
        organNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrganDetailsActivity.start(activity,
                        ((OrganVo) organAdapter.getItem(position)).getId());
            }
        });
        organNoScrollGridView.setNumColumns(4);
        organView.setVisibility(View.GONE);
        contentLayout.addView(organView);

        pullToRefresh.setLastUpdated(new Date().toLocaleString());
        pullToRefresh.showRefresh();
        getClassifyData();
    }
    private List<BannerInfoVo> bannerInfoList;
    private void updateBannerViwe() {
        bannerInfoList = new ArrayList<BannerInfoVo>();
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetBanners);
        LogUtil.d(TAG, params.getUri());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<BannerInfoVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<BannerInfoVo>>>() {
                        });
                if (result.getCode() == 0) {
                    bannerInfoList = result.getData();
                    if (bannerInfoList != null && bannerInfoList.size() > 0) {
                        List<String> imgUrlList = new ArrayList<>();
                        for (int i = 0; i < bannerInfoList.size(); i++) {
                            if (bannerInfoList.get(i).getThumbnail() != null) {
                                imgUrlList.add(bannerInfoList.get(i).getThumbnail());
                            }
                        }
                        bannerHeaderView.setImgUrlData(imgUrlList);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取banner信息失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getClassifyData() {
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassList);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onHeaderRefreshComplete();
                ResponseVo<List<ClassifyVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ClassifyVo>>>() {
                        });
                if (result.getCode() == 0) {
                    classVos = result.getData();
                    if (classVos != null && classVos.size() > 0) {
                        if(TextUtils.equals(classVos.get(0).getLevel(),"93")
                                && classVos.size() == 5) {
                            classVos.remove(0);
                        }
                        classifyVo = classVos.get(0);
                        pullToRefresh.setLastUpdated(new Date().toLocaleString());
                        pullToRefresh.showRefresh();
                        getData();
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                pullToRefresh.onHeaderRefreshComplete();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefresh.onHeaderRefreshComplete();
                LogUtil.d(TAG, "拉取分类列表失败:" + throwable.getMessage());
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getData() {
        //添加分类列表
        updateClassifyList();
        //添加热门推荐/最近更新/入驻机构
        updateItems();
    }

    @Override
    public void onClick(View view) {

    }

    private void updateClassifyList() {
        classifyAdapter = new ClassifyAdapter(activity);
        classifyList = new ArrayList<>(classifyVo.getChildList());
        if (classifyList != null) {
            for (int i = 0; i < classifyList.size(); i++) {
                classifyList.get(i).setThumbnail("ic_lq_english_child_" + i);
            }
        }
        classifyAdapter.setData(classifyList);
        classifyGridView.setAdapter(classifyAdapter);
        classifyAdapter.notifyDataSetChanged();
    }

    private void updateItems() {
        hotCourseAdapter = new IndexCourseAdapter(activity, false);
        latestCourseAdapter = new IndexCourseAdapter(activity, false);
        organAdapter = new OrganAdapter(activity);

        RequestVo requestVo = new RequestVo();
        if(!AppConfig.BaseConfig.needShowPay()){//只显示免费课程
           requestVo.addParams("payType", 0);
        }
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetDiscoveryItemList
                + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onHeaderRefreshComplete();
                DiscoveryItemVo result = JSON.parseObject(s,
                        new TypeReference<DiscoveryItemVo>() {
                        });
                if (result.getCode() == 0) {
                    hotCourseList = result.getRmCourseList();
                    hotCourseAdapter.setData(hotCourseList);
                    hotNoScrollGridView.setAdapter(hotCourseAdapter);
                    hotCourseAdapter.notifyDataSetChanged();
                    latestCourseList = result.getZjCourseList();
                    latestCourseAdapter.setData(latestCourseList);
                    latestNoScrollGridView.setAdapter(latestCourseAdapter);
                    latestCourseAdapter.notifyDataSetChanged();
                    organList = result.getOrganList();
                    organAdapter.setData(organList);
                    organNoScrollGridView.setAdapter(organAdapter);
                    organAdapter.notifyDataSetChanged();
                    contentLayout.invalidate();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉去热门推荐/最近更新/入驻机构失败:" + throwable.getMessage());
                pullToRefresh.onHeaderRefreshComplete();
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.REQUEST_CODE_LOGIN
                && resultCode == Activity.RESULT_OK) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShow = false;
    }
}
