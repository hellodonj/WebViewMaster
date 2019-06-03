package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.NetWorkUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.module.discovery.adapter.CourseListAdapter;
import com.lqwawa.intleducation.module.discovery.adapter.TeacherResListAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.discovery.vo.ResourceVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqresviewlib.LqResViewHelper;
import com.lqwawa.lqresviewlib.image.ImageDetailActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/13.
 * email:man0fchina@foxmail.com
 * 表演学习课程列表 仅用于表演课堂app调用
 */
public class HQCCourseListSimpleActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {
    private static String TAG = HQCCourseListSimpleActivity.class.getSimpleName();

    private TopBar topBar;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;
    private CourseListAdapter courseListAdapter;
    private String memberId;
    private String schoolId;
    List<CourseVo> courseList;

    /**
     * start
     * @param activity 启动此界面的上一级activity
     * @param memberId 用户memberId
     * @param schoolId 机构id
     */
    public static void start(Activity activity, String memberId, String schoolId) {
        activity.startActivity(new Intent(activity, HQCCourseListSimpleActivity.class)
                .putExtra("memberId", memberId)
                .putExtra("schoolId", schoolId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_activity_refresh_list);
        topBar = (TopBar) findViewById(R.id.top_bar);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        memberId = getIntent().getStringExtra("memberId");
        schoolId = getIntent().getStringExtra("schoolId");
        initViews();
    }

    private void initViews() {
        //初始化下拉刷新
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.hideFootView();
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });
        pullToRefreshView.setOnFooterRefreshListener(new PullToRefreshView.OnFooterRefreshListener() {
            @Override
            public void onFooterRefresh(PullToRefreshView view) {
                getMore();
            }
        });

        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        //初始化顶部工具条
        topBar.setBack(true);
        topBar.setTitle("表演学习");

        btnReload.setOnClickListener(this);

        courseListAdapter = new CourseListAdapter(this);
        listView.setAdapter(courseListAdapter);
        listView.setOnItemClickListener(this);
        pullToRefreshView.showRefresh();
        getData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final CourseVo vo = (CourseVo) courseListAdapter.getItem(position);
        if (vo != null) {
            CourseDetailsActivity.start(activity, vo.getId(), false,
                    UserHelper.getUserId());
        }
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        getData(AppConfig.PAGE_SIZE);
    }

    private void getData(int pageSize) {
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", pageSize);
        requestVo.addParams("schoolId", schoolId);
        requestVo.addParams("sort", 0);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.getByktCourseList + requestVo.getParams());
        pageIndex = 0;
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<CourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    courseList = result.getData();
                    pullToRefreshView.setLoadMoreEnable(
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
                LogUtil.d(TAG, "获取精品课程列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
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
        requestVo.addParams("schoolId", schoolId);
        requestVo.addParams("sort", 0);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.getByktCourseList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                ResponseVo<List<CourseVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<CourseVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<CourseVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pullToRefreshView.setLoadMoreEnable(courseList.size() >= AppConfig.PAGE_SIZE);
                        courseList.addAll(listMore);
                        pageIndex++;
                        courseListAdapter.addData(listMore);
                        courseListAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToastBottom(activity.getApplicationContext(), R.string.no_more_data);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取精品课程列表失败:" + throwable.getMessage());
                pullToRefreshView.onFooterRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
