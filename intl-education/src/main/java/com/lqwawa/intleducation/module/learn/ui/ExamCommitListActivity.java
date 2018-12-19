package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.adapter.ExamCommitListAdapter;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.ExamCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.ExamCommitVo;
import com.lqwawa.intleducation.module.learn.vo.ExamListVo;
import com.lqwawa.tools.ScreenUtils;
import com.oosic.apps.iemaker.base.SlideManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Date;
import java.util.List;

/**
 * 批阅列表
 */
public class ExamCommitListActivity extends MyBaseActivity implements View.OnClickListener,
        ListView.OnItemClickListener {

    private ExamCommitListAdapter examCommitListAdapter;
    private TopBar topBar;
    private Button btnReload;
    private View testContentLayout;
    private ImageView testIcon;
    private TextView testName;
    private PullToRefreshView pullToRefreshView;
    private ListView listView;

    private ExamListVo examListVo;
    private CourseVo courseVo;

    public static void start(Activity activity, ExamListVo examListVo, String memberId,
                             CourseVo courseVo) {
        activity.startActivity(new Intent(activity, ExamCommitListActivity.class)
                        .putExtra(ExamListVo.class.getSimpleName(), examListVo)
                        .putExtra("memberId", memberId).putExtra(CourseVo.class.getSimpleName(), courseVo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_commit_list);
        topBar = (TopBar) findViewById(R.id.top_bar);
        btnReload = (Button) findViewById(R.id.reload_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        listView = (ListView) findViewById(R.id.listView);
        testContentLayout = findViewById(R.id.test_content_rl);
        testIcon = (ImageView) findViewById(R.id.test_icon_iv);
        testName = (TextView) findViewById(R.id.test_name_tv);

        examListVo = (ExamListVo)getIntent().getSerializableExtra(ExamListVo.class.getSimpleName
                ());
        courseVo = (CourseVo)getIntent().getSerializableExtra(CourseVo.class.getSimpleName());
        initViews();
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

        initTestContent();
        pullToRefreshView.setLastUpdated(new Date().toLocaleString());
        topBar.setBack(true);
        btnReload.setOnClickListener(this);
        testContentLayout.setOnClickListener(this);
        topBar.setTitle(getResources().getString(R.string.mooc_mark));

        examCommitListAdapter = new ExamCommitListAdapter(activity);
        examCommitListAdapter.setExamListVo(examListVo);
        listView.setAdapter(examCommitListAdapter);
        listView.setOnItemClickListener(this);
        pullToRefreshView.showRefresh();
        getData();
    }

    private void initTestContent() {
        if (examListVo != null) {
            if (examListVo.getCexam() != null) {
                testName.setText(examListVo.getCexam().getPaperName());
            }
        }
        int screenWidth = ScreenUtils.getScreenWidth(ExamCommitListActivity.this);
        int screenHeight = ScreenUtils.getScreenHeight(ExamCommitListActivity.this);
        int size = Math.min(screenWidth, screenHeight);
        int imageSize = (size  - DisplayUtil.dip2px(activity, 16)) / 7;
        testIcon.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
    }

    private void getData() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", activity.getIntent().getStringExtra("memberId"));
        if (examListVo != null && examListVo.getCexam() != null) {
            requestVo.addParams("courseExamId", examListVo.getCexam().getId());
        }
        LogUtil.d(getClass().getSimpleName(), requestVo.getParams().toString());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.GetExamCommitList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ExamCommitListVo result = JSON.parseObject(s,
                        new TypeReference<ExamCommitListVo>() {
                        });
                if (result.getCode() == 0) {
                    List<ExamCommitVo> list = result.getData();
                    examCommitListAdapter.setData(list);
                    examCommitListAdapter.notifyDataSetChanged();
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
        } else if (view.getId() == R.id.test_content_rl) {
            if (examListVo == null) {
                return;
            }
            ExamDetailActivity.start(activity, examListVo.getCexam().getId(), examListVo.getCexam().getType(),
                    activity.getIntent().getStringExtra("memberId"), courseVo, null, null);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                                    ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE
                    );
                }
            }
        }
    }
}
