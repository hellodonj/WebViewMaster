package com.lqwawa.intleducation.module.discovery.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.BannerHeaderView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.module.discovery.adapter.ClassifyAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.IndexCourseAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.LiveAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.OrganAdapter;
import com.lqwawa.intleducation.module.discovery.vo.BannerInfoVo;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.DiscoveryItemVo;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import com.lqwawa.intleducation.module.learn.tool.LiveDetails;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.LiveListVo;
import com.lqwawa.intleducation.module.learn.vo.LiveVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LQCourseFragment extends MyBaseFragment implements View.OnClickListener {
    private static final String TAG = "LQCourseFragment";

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
    private NoScrollGridView liveNoScrollGridView;
    private LiveAdapter liveAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_classify_index, container, false);
        topBar = (TopBar) view.findViewById(R.id.top_bar);
        pullToRefresh = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
        bannerHeaderView = (BannerHeaderView) view.findViewById(R.id.banner_header_view);
        classifyGridView = (NoScrollGridView) view.findViewById(R.id.classify_grid_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerBroadcastReceiver();
        initViews();
    }

    private void initViews() {
        topBar.setTitle(getResources().getString(R.string.lq_course));
        topBar.setVisibility(View.GONE);
        /*if(AppConfig.BaseConfig.needShowPay()) {
            topBar.setRightFunctionText1(activity.getResources().getString(R.string.my_orders), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyOrderListActivity.newInstance(LQCourseFragment.this);
                }
            });
        }*/
        updateBannerView();
        pullToRefresh.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                updateBannerView();
                getClassifyData();
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
                            vo.getConfigValue(), "-1",false);
                }
            }
        });


        /**
         * ===========================================================================
         *  直播
         * ===========================================================================
         */

        View liveView = activity.getLayoutInflater().inflate(
                R.layout.mod_discovery_item, contentLayout, false);
        TextView liveTitleText = (TextView) liveView.findViewById(R.id.title_name);
        liveTitleText.setText(getText(R.string.live_room));
        LinearLayout liveTitle = (LinearLayout) liveView.findViewById(R.id.item_title);
        liveTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classifyVo != null) {
                    LQCourseCourseListActivity.start(activity,
                            new ArrayList<>(classVos),
                            -1,
                            getString(R.string.live_room), "3",true);
                }
            }
        });
        liveNoScrollGridView = (NoScrollGridView) liveView.findViewById(R.id.item_grid_view);
        liveNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LiveVo vo = (LiveVo) liveAdapter.getItem(position);
                if (vo != null) {
                    LiveDetails.jumpToLiveDetails(activity, vo, false, true ,false);
                    if (vo.getState() != 0) {
                        vo.setBrowseCount(vo.getBrowseCount() + 1);
                    }
                }
            }
        });
        liveNoScrollGridView.setNumColumns(2);
        contentLayout.addView(liveView);

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
                            getString(R.string.hot_recommended), "1",true);
//                    HQCCourseListSimpleActivity.start(activity, UserHelper.getUserId(),
//                            "d4abddcf-2634-457e-887e-2cd4a03784fa");
                }
            }
        });
        hotNoScrollGridView = (NoScrollGridView) hotView.findViewById(R.id.item_grid_view);
        hotNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    CourseVo vo = (CourseVo) hotCourseAdapter.getItem(position);
                    if(vo != null) {
                        CourseDetailsActivity.start(activity,vo.getId(), true, UserHelper.getUserId());
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        hotNoScrollGridView.setNumColumns(3);
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
                            getString(R.string.latest_update), "2",true);
                }
            }
        });
        latestNoScrollGridView = (NoScrollGridView) latestView.findViewById(R.id.item_grid_view);
        latestNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    CourseVo vo = (CourseVo) latestCourseAdapter.getItem(position);
                    if(vo != null) {
                        CourseDetailsActivity.start(activity,vo.getId(), true, UserHelper.getUserId());
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        latestNoScrollGridView.setNumColumns(3);
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
    private void updateBannerView() {
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
        RequestVo requestVo = new RequestVo();

        requestVo.addParams("language", Utils.isZh(getActivity()) ? 0 : 1);

        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassList+requestVo.getParams());
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
        updateLive();
    }

    /**
     * 直播
     */
    private void updateLive() {
        liveAdapter = new LiveAdapter(activity, false);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", 0);
        requestVo.addParams("pageSize", 10);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetLiveList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {

                LiveListVo result = JSON.parseObject(s,
                        new TypeReference<LiveListVo>() {
                        });
                if (result.getCode() == 0) {
                    List<LiveVo> list = result.getData();
                    if(list != null && list.size() > 2) {
                        liveAdapter.setData(list.subList(0, 2));
                    }else{
                        liveAdapter.setData(list);
                    }
                    liveNoScrollGridView.setAdapter(liveAdapter);
                    liveAdapter.notifyDataSetChanged();
//                    contentLayout.invalidate();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    private void updateClassifyList() {
        classifyAdapter = new ClassifyAdapter(activity);
        if (classVos != null) {
            for (int i = 0; i < classVos.size(); i++) {
                classVos.get(i).setThumbnail("ic_lq_english_child_" + i);
            }
        }
        classifyAdapter.setData(classVos);
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
                    if(hotCourseList != null && hotCourseList.size() > 2) {
                        hotCourseAdapter.setData(hotCourseList.subList(0, 3));
                    }else{
                        hotCourseAdapter.setData(hotCourseList);
                    }
                    hotNoScrollGridView.setAdapter(hotCourseAdapter);
                    hotCourseAdapter.notifyDataSetChanged();
                    latestCourseList = result.getZjCourseList();
                    if(latestCourseList != null && latestCourseList.size() > 2){
                        latestCourseAdapter.setData(latestCourseList.subList(0,3));
                    }else{
                        latestCourseAdapter.setData(latestCourseList);
                    }
                    latestNoScrollGridView.setAdapter(latestCourseAdapter);
                    latestCourseAdapter.notifyDataSetChanged();
                    organList = result.getOrganList();
                    organAdapter.setData(organList);
                    organNoScrollGridView.setAdapter(organAdapter);
                    organAdapter.notifyDataSetChanged();
//                    contentLayout.invalidate();
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
    public void onDestroyView() {
        super.onDestroyView();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**BroadcastReceiver************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("LIVE_STATUS_CHANGED")){
                updateLive();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction("LIVE_STATUS_CHANGED");
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
}
