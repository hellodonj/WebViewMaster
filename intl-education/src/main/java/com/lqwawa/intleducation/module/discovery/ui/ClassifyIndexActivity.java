package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.adapter.ClassifyAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.IndexCourseAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.OrganAdapter;
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

public class ClassifyIndexActivity extends MyBaseActivity implements View.OnClickListener {
    public static class ClassifyValues {
        public static String LQGalaxyInt = "93";//lq银河国际
        public static String Speaker = "218";//阅演家
        public static String NZUP = "206";//NZUP
        public static String BaseEdu = "64";//基础教育
        public static String PerformArts = "81";//艺术表演
    }
    public static class LQGISubClassifyValues{
        public static String ExaminationForGoingAbroad = "93.98";
    }
    public static class SubjectLabelIds {
        public static String Chinese = "39";//语文
        public static String Eglish = "40";//英语
    }

    public static boolean isShow = false;
    public static void start(final Activity activity, ClassifyVo vo) {
        if (isShow) {
            return;
        }
        isShow = true;
        activity.startActivity(new Intent(activity, ClassifyIndexActivity.class)
                .putExtra("classify", vo));
    }

    public static void start(final Activity activity, String level) {
        if (isShow) {
            return;
        }
        isShow = true;
        activity.startActivity(new Intent(activity, ClassifyIndexActivity.class)
                .putExtra("level", level));
    }

    public static void start(final  Activity activity, String level, String labelId){
        if (isShow) {
            return;
        }
        isShow = true;
        activity.startActivity(new Intent(activity, ClassifyIndexActivity.class)
                .putExtra("level", level).putExtra("setSubjectLabelId", labelId));
    }
    //指定调用一级分类在一起列表的序号值
    //仅在vo = null的情况下 index才会起作用
    //因为从外部调用的时候没有拉取分类数据 vo = null 需要指定index
    public static void start(final Activity activity, ClassifyVo vo, int index) {
        if (isShow) {
            return;
        }
        isShow = true;
        activity.startActivity(new Intent(activity, ClassifyIndexActivity.class)
                .putExtra("classify", vo).putExtra("classifyIndex", index));
    }

    private static final String TAG = "DiscoveryFragment";

    private TopBar topBar;
    private PullToRefreshView pullToRefresh;
    private ScrollView scrollView;
    private LinearLayout contentLayout;
    private ImageView bannerHeaderView;
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

    private ClassifyVo classifyVo;//分类的level值
    private int classifyIndex = -1;
    private String setTopLevel;
    private String setLableId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify_index);
        classifyVo = (ClassifyVo) getIntent().getSerializableExtra("classify");
        setTopLevel = getIntent().getStringExtra("level");
        if (setTopLevel == null){
            setTopLevel = "93";
        }
        setLableId = getIntent().getStringExtra("setSubjectLabelId");

        topBar = (TopBar) findViewById(R.id.top_bar);
        pullToRefresh = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        bannerHeaderView = (ImageView) findViewById(R.id.banner_header_iv);
        bannerHeaderView.setVisibility(View.VISIBLE);
        classifyGridView = (NoScrollGridView) findViewById(R.id.classify_grid_view);
        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        if (classifyVo != null) {
            topBar.setTitle("" + classifyVo.getConfigValue());
        }
        if(TextUtils.equals(SubjectLabelIds.Eglish,setLableId)){
            topBar.setTitle(getResources().getString(R.string.lq_english));
            if(AppConfig.BaseConfig.needShowPay()) {
                topBar.setRightFunctionText1(getResources().getString(R.string.my_orders), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyOrderListActivity.newInstance(ClassifyIndexActivity.this);
                    }
                });
            }
        }

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

        activity.findViewById(R.id.banner_header_view).setVisibility(View.GONE);

        classifyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassifyVo vo = (ClassifyVo) classifyAdapter.getItem(position);

                if (vo != null && classifyVo != null) {
                    //出国考试分类进行特殊处理
                    if(LQGISubClassifyValues.ExaminationForGoingAbroad.equals(vo.getLevel())){
                        ChildCourseListActivityEA.start(activity,
                                classifyVo,
                                vo.getLevel(),
                                vo.getConfigValue(), "", setLableId);
                    }else{
                        //LQ银河国际下的其他子分类处理
                        ChildCourseListActivity.start(activity,
                                classifyVo,
                                vo.getLevel(),
                                vo.getConfigValue(), null, setLableId);
                    }
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
                    ChildCourseListActivity.start(activity,
                            classifyVo,
                            classifyVo.getLevel(),
                            getString(R.string.hot_recommended), "1", setLableId);
                }
            }
        });
        hotNoScrollGridView = (NoScrollGridView) hotView.findViewById(R.id.item_grid_view);
        hotNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) hotCourseAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(),
                        SubjectLabelIds.Eglish
                                .equals(getIntent().getStringExtra("setSubjectLabelId")),
                        UserHelper.getUserId());
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
                    ChildCourseListActivity.start(activity,
                            classifyVo,
                            classifyVo.getLevel(),
                            getString(R.string.latest_update), "2", setLableId);
                }
            }
        });
        latestNoScrollGridView = (NoScrollGridView) latestView.findViewById(R.id.item_grid_view);
        latestNoScrollGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) latestCourseAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(),
                        SubjectLabelIds.Eglish
                                .equals(getIntent().getStringExtra("setSubjectLabelId")),
                        UserHelper.getUserId());
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
        if (classifyVo == null) {
            classifyIndex = getIntent().getIntExtra("classifyIndex", -1);
            getClassifyData();
        } else {
            getData();
        }
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
                    List<ClassifyVo> classVos = result.getData();
                    if (classVos != null && classVos.size() > 0) {
                        if (classifyIndex >= classVos.size() || classifyIndex < 0) {
                            for(int i =0; i < classVos.size(); i++){
                                if(TextUtils.equals(classVos.get(i).getLevel(),setTopLevel)){
                                    classifyVo = classVos.get(i);
                                }
                            }
                        }else{
                            classifyVo = classVos.get(classifyIndex);
                        }
                        if(classifyVo == null){
                            return;
                        }

                        topBar.setTitle("" + classifyVo.getConfigValue());
                        if(TextUtils.equals(SubjectLabelIds.Eglish,setLableId)){
                            topBar.setTitle(getResources().getString(R.string.lq_english));
                        }
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
        requestVo.addParams("level", classifyVo.getLevel());
        if(setLableId != null) {
            requestVo.addParams("labelId", setLableId);
        }
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
