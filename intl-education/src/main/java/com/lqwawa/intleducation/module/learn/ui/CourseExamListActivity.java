package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.adapter.CourseExamListAdapter;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.CourseExamDataVo;
import com.lqwawa.intleducation.module.learn.vo.ExamListVo;
import com.oosic.apps.iemaker.base.SlideManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 课程的考试列表
 * */
public class CourseExamListActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {

    private CourseExamListAdapter testListAdapter;
    private TopBar topBar;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;
    private String courseId;

    public static void start(Activity activity, String courseId, boolean needFlag,
                             boolean canEdit, String memberId, String schoolId, boolean isFromMyCourse){
        activity.startActivity(new Intent(activity, CourseExamListActivity.class)
                .putExtra("courseId", courseId).putExtra("needFlag", needFlag)
                .putExtra("canEdit", canEdit).putExtra("memberId", memberId)
                .putExtra("schoolId", schoolId)
                .putExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, isFromMyCourse));
    }

    public static void start(Activity activity, String courseId, boolean needFlag,
                             boolean canEdit, String memberId, String schoolId,
                             CourseVo courseVo, CourseDetailParams courseDetailParams,
                             boolean isFromMyCourse){
        activity.startActivity(new Intent(activity, CourseExamListActivity.class)
                .putExtra("courseId", courseId)
                .putExtra("needFlag", needFlag)
                .putExtra("canEdit", canEdit)
                .putExtra("memberId", memberId)
                .putExtra("schoolId", schoolId)
                .putExtra(CourseVo.class.getSimpleName(), courseVo)
                .putExtra(CourseDetailParams.class.getSimpleName(), courseDetailParams)
                .putExtra(MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, isFromMyCourse));
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
        courseId = getIntent().getStringExtra("courseId");
        initViews();
        registerBoradcastReceiver();
    }

    private void initViews() {
        //初始化下拉刷新
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.hideFootView();
        pullToRefreshView.setOnHeaderRefreshListener(new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });

        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        topBar.setBack(true);
        btnReload.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.exam));

        testListAdapter = new CourseExamListAdapter(activity);
        listView.setAdapter(testListAdapter);
        listView.setOnItemClickListener(this);
        pullToRefreshView.showRefresh();
        getData();
    }

    private void getData() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", getIntent().getStringExtra("memberId"));
        requestVo.addParams("pageIndex", 0);
        requestVo.addParams("pageSize", Integer.MAX_VALUE);
        requestVo.addParams("courseId", courseId);
        requestVo.addParams("type", 2);

        LogUtil.d(getClass().getSimpleName(), requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetCourseHomeworkOrExamList+ requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                CourseExamDataVo result = JSON.parseObject(s,
                        new TypeReference<CourseExamDataVo>() {
                        });
                if (result.getCode() == 0) {
                    List<ExamListVo> list = result.getData();
                    if (list != null) {
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).getCexam().setChapterName(result.getChapterName());
                            list.get(i).getCexam().setSectionName(result.getSectionName());
                        }
                        List<ExamListVo> tempList = new ArrayList<>(list);
                        list.clear();
                        for(int i = 0; i< tempList.size(); i++){
                            if(tempList.get(i).getCexam().getType() == 2){
                                list.add(tempList.get(i));
                            }
                        }
                        for(int i = 0; i< tempList.size(); i++){
                            if(tempList.get(i).getCexam().getType() == 4){
                                list.add(tempList.get(i));
                            }
                        }
                    }
                    testListAdapter.setData(list);
                    testListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefreshView.onHeaderRefreshComplete();
                LogUtil.d(getClass().getSimpleName(), "拉取通知列表失败:"
                        + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**BroadcastReceiver************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.userExamSave)//提交试卷成功
                    || action.equals(AppConfig.ServerUrl.userTaskSave) //提交任务单成功
                    || action.equals(AppConfig.ServerUrl.userExamMark)) { //老师完成批阅成功
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
        myIntentFilter.addAction(AppConfig.ServerUrl.userExamSave);//提交试卷成功
        myIntentFilter.addAction(AppConfig.ServerUrl.userTaskSave);//提交任务单成功
        myIntentFilter.addAction(AppConfig.ServerUrl.userExamMark);//老师完成批阅成功
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DoExamActivity.RC_DoExam){
            getData();
        }else if(requestCode == 105){
            if (data != null) {
                String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                if (!TextUtils.isEmpty(slidePath)){
                    TaskSliderHelper.commitTask(activity, slidePath,
                            getIntent().getStringExtra("taskPaperId"),
                            new TaskSliderHelper.OnCommitTaskListener() {
                        @Override
                        public void onCommitSuccess() {
                            getData();
                        }
                    },
                            activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                    .KEY_IS_FROM_MY_COURSE, false)
                                    ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                }
            }
        }
    }

}

