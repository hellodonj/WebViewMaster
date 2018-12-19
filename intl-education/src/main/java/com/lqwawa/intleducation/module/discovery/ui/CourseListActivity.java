package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CourseFilterPopupWindow;
import com.lqwawa.intleducation.common.ui.LinePopupWindow;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.CourseSortType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.module.discovery.vo.FilterVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 课程列表
 */
public class CourseListActivity extends MyBaseActivity implements View.OnClickListener{
    private static final String TAG = "CourseListActivity";
    //头部
    private TopBar topBar;
    //下拉刷新
    private PullToRefreshView pullToRefresh;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button reloadBt;

    /**
     * sort view start
     *****/
    private LinearLayout hotLayout;
    private TextView hotTextview;
    private ImageView hotImageview;
    private ImageView hotSelIv;

    private LinearLayout statusLayout;
    private TextView statusTextview;
    private ImageView statusImageview;
    private ImageView statusSelIv;

    private View filterSplitter;
    private LinearLayout filterLayout;
    private TextView filterTextview;
    private ImageView filterImageview;
    private ImageView filterSelIv;

    private List<CourseSortType> hotSortTypeList;
    private List<CourseSortType> statusSortTypeList;
    private List<FilterVo> filterSortTypeList = null;
    FilterVo segmentSelect = null;
    FilterVo ageSelect = null;

    private String paramCourseName = "";
    private String paramSort = "1";
    private String paramLevel = "";
    private String paramStatus = "";
    private String paramOrganId;
    private String levelName;
    /**
     * sort view end
     *****/
    private List<CourseVo> courseList;
    private CourseListAdapter courseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        
        topBar = (TopBar) findViewById(R.id.top_bar);
        pullToRefresh = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        hotLayout = (LinearLayout) findViewById(R.id.hot_layout);
        hotTextview = (TextView) findViewById(R.id.hot_textview);
        hotImageview = (ImageView) findViewById(R.id.hot_imageview);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        statusTextview = (TextView) findViewById(R.id.status_textview);
        statusImageview = (ImageView) findViewById(R.id.status_imageview);
        filterSplitter = findViewById(R.id.filter_splitter);
        filterLayout = (LinearLayout) findViewById(R.id.filter_layout);
        filterTextview = (TextView) findViewById(R.id.filter_textview);
        filterImageview = (ImageView) findViewById(R.id.filter_imageview);
        hotSelIv = (ImageView) findViewById(R.id.hot_sel_iv);
        statusSelIv = (ImageView) findViewById(R.id.status_sel_iv);
        filterSelIv = (ImageView) findViewById(R.id.filter_sel_iv);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        reloadBt = (Button) findViewById(R.id.reload_bt);
        findViewById(R.id.reload_bt).setOnClickListener(this);

        paramCourseName = getIntent().getStringExtra("CourseName");
        paramSort = getIntent().getStringExtra("Sort");
        if (paramSort == null){
            paramSort = "1";
        }
        paramLevel = getIntent().getStringExtra("Level");
        paramOrganId = getIntent().getStringExtra("OrganId");
        levelName = getIntent().getStringExtra("LevelName");
        topBar.setTitle(getString(R.string.all));
        if (levelName != null && !levelName.isEmpty()){
            topBar.setTitle(levelName);
        }
        initViews();
        initData();
    }

    private void initViews() {
        initSortView();
        topBar.setBack(true);
        if (paramCourseName == null){
            topBar.setRightFunctionImage2(R.drawable.search, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, SearchActivity.class));
                }
            });
        }
        topBar.setRightFunctionImage1(R.drawable.ic_menu, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectClassifyActivity.startForResult(activity, paramLevel, paramCourseName == null);
            }
        });
        
        if (paramLevel != null && paramLevel.equals("324")){
            filterSplitter.setVisibility(View.VISIBLE);
            filterLayout.setVisibility(View.VISIBLE);
            filterTextview.setVisibility(View.VISIBLE);
            filterImageview.setVisibility(View.VISIBLE);
        }else{
            filterSplitter.setVisibility(View.GONE);
            filterLayout.setVisibility(View.GONE);
            filterTextview.setVisibility(View.GONE);
            filterImageview.setVisibility(View.GONE);
            filterSelIv.setVisibility(View.GONE);
        }

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
                CourseDetailsActivity.start(activity, vo.getId(), true, UserHelper.getUserId());
            }
        });
    }

    @Override
    public void onClick(View view){
        if (view.getId() == R.id.reload_bt){
            pullToRefresh.showRefresh();
            getData();
        }
    }

    private void initSortView() {
        hotSortTypeList = new ArrayList<CourseSortType>();
        statusSortTypeList = new ArrayList<CourseSortType>();
        hotSortTypeList.add(new CourseSortType("1", getString(R.string.hottest), true));
        hotSortTypeList.add(new CourseSortType("2", getString(R.string.newest), false));
        statusSortTypeList.add(new CourseSortType("-1", getString(R.string.course_status_all), true));
        statusSortTypeList.add(new CourseSortType("0", getString(R.string.course_status_0), false));
        statusSortTypeList.add(new CourseSortType("1", getString(R.string.course_status_1), false));
        statusSortTypeList.add(new CourseSortType("2", getString(R.string.course_status_2), false));
        if (paramSort != null && (paramSort.equals("1") || paramSort.equals("2"))) {
            int selectPosition = Integer.parseInt(paramSort) - 1;
            for (int i = 0; i < hotSortTypeList.size(); i++) {
                if (selectPosition == i) {
                    hotSortTypeList.get(i).setIsSelect(true);
                    hotTextview.setText(hotSortTypeList.get(i).getName());
                } else {
                    hotSortTypeList.get(i).setIsSelect(false);
                }
            }
        }

        if (paramStatus != null && (paramStatus.equals("-1") || paramStatus.equals("1")
                || paramStatus.equals("2") || paramStatus.equals("3"))) {
            int selectPosition = Integer.parseInt(paramStatus);
            for (int i = 0; i < statusSortTypeList.size(); i++) {
                if (selectPosition == i) {
                    statusSortTypeList.get(i).setIsSelect(true);
                    statusTextview.setText(statusSortTypeList.get(i).getName());
                } else {
                    statusSortTypeList.get(i).setIsSelect(false);
                }
            }
        }

        hotLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotTextview.setTextColor(getResources().getColor(R.color.com_text_green));
                hotImageview.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_ico));
                hotSelIv.setVisibility(View.VISIBLE);
                List<String> sortList = new ArrayList<String>();

                for (int i = 0; i < hotSortTypeList.size(); i++) {
                    String name = hotSortTypeList.get(i).getName();
                    sortList.add(name);
                }
                LinePopupWindow linePopupWindow =
                        new LinePopupWindow(CourseListActivity.this, sortList,hotTextview.getText().toString(),
                                new LinePopupWindow.PopupWindowListener() {
                                    @Override
                                    public void onItemClickListener(Object object) {
                                        String name = (String) object;
                                        hotTextview.setText(name);
                                        for (int i = 0; i < hotSortTypeList.size(); i++) {
                                            if (name.equals(hotSortTypeList.get(i).getName())) {
                                                hotSortTypeList.get(i).setIsSelect(true);
                                                paramSort = hotSortTypeList.get(i).getId();
                                            } else {
                                                hotSortTypeList.get(i).setIsSelect(false);
                                            }
                                        }
                                        hotTextview.setTextColor(getResources().getColor(R.color.com_text_black));
                                        hotImageview.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        hotSelIv.setVisibility(View.INVISIBLE);
                                        pullToRefresh.showRefresh();
                                        getData();
                                    }

                                    @Override
                                    public void onDismissListener() {
                                        hotTextview.setTextColor(getResources().getColor(R.color.com_text_black));
                                        hotImageview.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        hotSelIv.setVisibility(View.INVISIBLE);
                                    }
                                }, false);
                linePopupWindow.showPopupWindow(hotLayout, true);
            }
        });

        statusLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusTextview.setTextColor(getResources().getColor(R.color.com_text_green));
                statusImageview.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_ico));
                statusSelIv.setVisibility(View.VISIBLE);
                List<String> sortList = new ArrayList<String>();

                for (int i = 0; i < statusSortTypeList.size(); i++) {
                    String name = statusSortTypeList.get(i).getName();
                        sortList.add(name);
                }
                LinePopupWindow linePopupWindow =
                        new LinePopupWindow(CourseListActivity.this, sortList,statusTextview.getText().toString(),
                                new LinePopupWindow.PopupWindowListener() {
                                    @Override
                                    public void onItemClickListener(Object object) {
                                        String name = (String) object;
                                        statusTextview.setText(name);
                                        for (int i = 0; i < statusSortTypeList.size(); i++) {
                                            if (name.equals(statusSortTypeList.get(i).getName())) {
                                                paramStatus = statusSortTypeList.get(i).getId();
                                                statusSortTypeList.get(i).setIsSelect(true);
                                            } else {
                                                statusSortTypeList.get(i).setIsSelect(false);
                                            }
                                        }
                                        statusTextview.setTextColor(
                                                getResources().getColor(R.color.com_text_black));
                                        statusImageview.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        statusSelIv.setVisibility(View.INVISIBLE);
                                        pullToRefresh.showRefresh();
                                        getData();
                                    }

                                    @Override
                                    public void onDismissListener() {
                                        statusTextview.setTextColor(
                                                getResources().getColor(R.color.com_text_black));
                                        statusImageview.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        statusSelIv.setVisibility(View.INVISIBLE);
                                    }
                                }, false);
                linePopupWindow.showPopupWindow(statusLayout, true);
            }
        });

        updateFilterList();
    }

    private void updateFilterList() {
        RequestParams params = new RequestParams(AppConfig.ServerUrl.getLearningSegmentAgeList);
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<FilterVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<FilterVo>>>() {
                        });
                if (result.getCode() == 0) {
                    filterSortTypeList = result.getData();
                    setFilter();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取分类列表失败:" + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private int reTryTimes = 0;
    private void setFilter(){
        if (filterSortTypeList == null){
            if (reTryTimes ++ < 3) {
                updateFilterList();
            }else{
                filterLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtil.showToast(activity,
                                getResources().getString(R.string.get_segment_age_data_failed));
                    }
                });
            }
            return;
        }
        filterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterTextview.setTextColor(getResources().getColor(R.color.com_text_green));
                filterImageview.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_ico));
                filterSelIv.setVisibility(View.VISIBLE);
                CourseFilterPopupWindow filterPopupWindow =
                        new CourseFilterPopupWindow(CourseListActivity.this, filterSortTypeList,
                                segmentSelect, ageSelect,
                                new CourseFilterPopupWindow.PopupWindowListener() {
                                    @Override
                                    public void onAllReset() {
                                        segmentSelect = null;
                                        ageSelect = null;
                                        filterTextview.setTextColor(
                                                getResources().getColor(R.color.com_text_black));
                                        filterImageview.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        filterSelIv.setVisibility(View.INVISIBLE);
                                        pullToRefresh.showRefresh();
                                        getData();
                                    }

                                    @Override
                                    public void onConfirm(Object segmentVo, Object ageVo) {
                                        segmentSelect = (FilterVo) segmentVo;
                                        ageSelect = (FilterVo) ageVo;
                                        filterTextview.setTextColor(
                                                getResources().getColor(R.color.com_text_black));
                                        filterImageview.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        filterSelIv.setVisibility(View.INVISIBLE);
                                        pullToRefresh.showRefresh();
                                        getData();
                                    }

                                    @Override
                                    public void onDismissListener() {
                                        filterTextview.setTextColor(
                                                getResources().getColor(R.color.com_text_black));
                                        filterImageview.setImageDrawable(
                                                getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                                        filterSelIv.setVisibility(View.INVISIBLE);
                                    }
                                });
                filterPopupWindow.showPopupWindow(filterLayout);
            }
        });
    }

    private void initData() {
        courseList = new ArrayList<CourseVo>();
        courseListAdapter = new CourseListAdapter(this);
        getData();
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        if (paramCourseName != null && !paramCourseName.equals("")) {
            try {
                requestVo.addParams("courseName", URLEncoder.encode(paramCourseName.trim(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (paramSort != null && !paramSort.equals("")) {
            requestVo.addParams("sort", paramSort);
        }
        if (paramLevel != null && !paramLevel.equals("")) {
            requestVo.addParams("level", paramLevel);
        }
        if (paramStatus != null && !paramStatus.equals("")) {
            requestVo.addParams("progressStatus", paramStatus);
        }
        if (paramOrganId != null && !paramOrganId.equals("")) {
            requestVo.addParams("organId", paramOrganId);
        }
        if (segmentSelect != null){
            requestVo.addParams("schoolSectionId", segmentSelect.getId());
        }
        if (ageSelect != null){
            requestVo.addParams("generationId", ageSelect.getId());
        }
        if(!AppConfig.BaseConfig.needShowPay()){//只显示免费课程
            requestVo.addParams("payType", 0);
        }

        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onHeaderRefreshComplete();
                ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<CourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    courseList = result.getData();
                    pullToRefresh.setLoadMoreEnable(
                            courseList != null && courseList.size() >= AppConfig.PAGE_SIZE);
                    courseListAdapter.setData(courseList);
                    listView.setAdapter(courseListAdapter);
                    courseListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取课程列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefresh.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getMore() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex + 1);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        if (paramCourseName != null && !paramCourseName.equals("")) {
            try {
                requestVo.addParams("courseName", URLEncoder.encode(paramCourseName.trim(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (paramSort != null && !paramSort.equals("")) {
            requestVo.addParams("sort", paramSort);
        }
        if (paramLevel != null && !paramLevel.equals("")) {
            requestVo.addParams("level", paramLevel);
        }
        if (paramStatus != null && !paramStatus.equals("")) {
            requestVo.addParams("progressStatus", paramStatus);
        }
        if (paramOrganId != null && !paramOrganId.equals("")) {
            requestVo.addParams("organId", paramOrganId);
        }
        if(!AppConfig.BaseConfig.needShowPay()){//只显示免费课程
            requestVo.addParams("payType", 0);
        }
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefresh.onFooterRefreshComplete();
                ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<CourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<CourseVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pullToRefresh.setLoadMoreEnable(courseList.size() >= AppConfig.PAGE_SIZE);
                        courseList.addAll(listMore);
                        pageIndex++;
                        courseListAdapter.addData(listMore);
                        courseListAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToastBottom(getApplicationContext(), R.string.no_more_data);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "拉取入驻机构列表失败:" + throwable.getMessage());
                pullToRefresh.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SelectClassifyActivity.Rc_SelectClasify){
            if (data != null){
                ClassifyVo select = (ClassifyVo) data.getSerializableExtra("Classify");
                if (select != null){
                    this.paramLevel = select.getLevel();
                    topBar.setTitle(select.getLevelName());
                    if (paramLevel != null && paramLevel.contains("324")){
                        filterSplitter.setVisibility(View.VISIBLE);
                        filterLayout.setVisibility(View.VISIBLE);
                        filterTextview.setVisibility(View.VISIBLE);
                        filterImageview.setVisibility(View.VISIBLE);
                    }else{
                        filterSplitter.setVisibility(View.GONE);
                        filterLayout.setVisibility(View.GONE);
                        filterTextview.setVisibility(View.GONE);
                        filterImageview.setVisibility(View.GONE);
                        filterSelIv.setVisibility(View.GONE);
                    }
                    getData();
                }
            }


        }
    }
}
