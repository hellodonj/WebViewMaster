package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.adapter.MyCredentialCourseListAdapter;
import com.lqwawa.intleducation.module.learn.vo.MyCredentialCourseListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Date;
import java.util.List;

@Deprecated
public class MyCredentialCourseListActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "MyCredentialCourseListActivity";

    private TopBar topBar;

    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //数据列表
    private ListView listView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;

    private String id;
    private MyCredentialCourseListAdapter courseListAdapter;

    public static void start(Activity activity, String id, String name) {
        activity.startActivity(new Intent(activity, MyCredentialCourseListActivity.class)
                .putExtra("id", id).putExtra("name", name));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_credential_course_list);
        topBar = (TopBar) findViewById(R.id.top_bar);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        id = getIntent().getStringExtra("id");
        initViews();
        registerBoradcastReceiver();
    }

    private void initViews() {
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
        topBar.setTitle(getIntent().getStringExtra("name"));
        btnReload.setOnClickListener(this);
        topBar.setBack(true);
        courseListAdapter = new MyCredentialCourseListAdapter(activity);
        listView.setAdapter(courseListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyCredentialCourseListVo vo = (MyCredentialCourseListVo) courseListAdapter.getItem(position);
                if (vo.isIsAdd() || vo.isIsBuy()) {
                    MyCourseDetailsActivity.start(activity, vo.getCourse().getId(), false, true,
                            UserHelper.getUserId(), false, false, false, false,false, null, null);
                } else {
                    CourseDetailsActivity.start(activity, vo.getCourse().getId(), true, UserHelper.getUserId());
                }
            }
        });
        getData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reload_bt) {
            getData();
        }
    }

    private int pageIndex = 0;

    private void getData() {
        pageIndex = 0;
        loadFailedLayout.setVisibility(View.GONE);
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("pageIndex", pageIndex);
        requestVo.addParams("pageSize", AppConfig.PAGE_SIZE);
        requestVo.addParams("certificateId", id);

        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCertificateCoutseList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCredentialCourseListVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCredentialCourseListVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<MyCredentialCourseListVo> courseList = result.getData();
                    courseListAdapter.setData(courseList);
                    courseListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取我的课程列表失败:" + throwable.getMessage());
                loadFailedLayout.setVisibility(View.VISIBLE);
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
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

        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCertificateCoutseList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<List<MyCredentialCourseListVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<MyCredentialCourseListVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<MyCredentialCourseListVo> listMore = result.getData();
                    if (listMore != null && listMore.size() > 0) {
                        pageIndex++;
                        courseListAdapter.addData(listMore);
                        courseListAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToastBottom(activity, R.string.no_more_data);
                    }
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "获取我的课程列表失败:" + throwable.getMessage());
                pullToRefreshView.onFooterRefreshComplete();
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * BroadcastReceiver
     ************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.joinInCourse)//
                    || action.equals(AppConfig.ServerUrl.Login)) {//
                getData();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.joinInCourse);//
        myIntentFilter.addAction(AppConfig.ServerUrl.Login);//
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }
}
