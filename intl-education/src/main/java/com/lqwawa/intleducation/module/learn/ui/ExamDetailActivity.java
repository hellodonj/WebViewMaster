package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.adapter.ExamResultGridAdapter;
import com.lqwawa.intleducation.module.learn.vo.ExamActionType;
import com.lqwawa.intleducation.module.learn.vo.ExamDetailVo;
import com.lqwawa.intleducation.module.learn.vo.ExamItemVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class ExamDetailActivity extends MyBaseActivity implements View.OnClickListener {

    private TopBar topBar;
    private TextView textViewExamDetail;
    private TextView textViewTotalScore;
    private TextView textViewExamScore;
    private TextView textViewExamType;
    private NoScrollGridView gridViewExamResult;
    private Button buttonDone;
    //下拉刷新
    private PullToRefreshView pullToRefreshView;
    //加载失败图片
    private RelativeLayout loadFailedLayout;
    //重新加载
    private Button btnReload;

    private ExamResultGridAdapter examResultGridAdapter;

    private String id;
    private int examType = 1;
    ExamDetailVo examDetailVo = null;
    private CourseVo courseVo;
    private String courseExamId;
    private String stuMemberId;
    private String memberId;
    private int firstQuestionIndex;

    public static void start(Activity activity, String id, int examType, String memberId,
                             CourseVo courseVo, String courseExamId, String stuMemberId) {
        activity.startActivityForResult(new Intent(activity, ExamDetailActivity.class)
                .putExtra("id", id).putExtra("examType", examType)
                .putExtra("memberId", memberId)
                .putExtra(CourseVo.class.getSimpleName(), courseVo)
                .putExtra("courseExamId", courseExamId)
                .putExtra("stuMemberId", stuMemberId),
                DoExamActivity.RC_DoExam);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_detail);
        topBar = (TopBar) findViewById(R.id.top_bar);
        gridViewExamResult = (NoScrollGridView) findViewById(R.id.exam_result_grid);
        textViewExamDetail = (TextView) findViewById(R.id.exam_detail_tv);
        textViewExamType = (TextView) findViewById(R.id.exam_type_tv);
        textViewTotalScore = (TextView) findViewById(R.id.total_score_tv);
        textViewExamScore = (TextView) findViewById(R.id.exam_score_tv);
        buttonDone = (Button) findViewById(R.id.done_bt);
        pullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);
        id = getIntent().getStringExtra("id");
        examType = getIntent().getIntExtra("examType", 1);
        courseVo = (CourseVo) getIntent().getSerializableExtra(CourseVo.class.getSimpleName());
        courseExamId = getIntent().getStringExtra("courseExamId");
        stuMemberId = getIntent().getStringExtra("stuMemberId");
        memberId = getIntent().getStringExtra("memberId");
        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        buttonDone.setOnClickListener(this);
        buttonDone.setVisibility(View.GONE);

        btnReload.setOnClickListener(this);
        pullToRefreshView.setLoadMoreEnable(false);
        pullToRefreshView.hideFootView();
        pullToRefreshView.setOnHeaderRefreshListener(
                new PullToRefreshView.OnHeaderRefreshListener() {
            @Override
            public void onHeaderRefresh(PullToRefreshView view) {
                getData();
            }
        });

        examResultGridAdapter = new ExamResultGridAdapter(activity);
        gridViewExamResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ExamItemVo vo = (ExamItemVo)examResultGridAdapter.getItem(i);
                if(vo != null){
                    int examActionType = getExamActionType();
                    if (examActionType == ExamActionType.MARK || examActionType == ExamActionType
                            .WATCH_TEST) {
                        doExam(i, 2, examActionType);
                    } else {
                        doExam(i, 2, -1);
                    }
                }
            }
        });
        gridViewExamResult.setAdapter(examResultGridAdapter);
        pullToRefreshView.showRefresh();
        getData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.done_bt) {
//            if (examDetailVo != null) {
//                if (examDetailVo.getScore() == -2) {
//                    if(TextUtils.equals(activity.getIntent().getStringExtra("memberId"),
//                            UserHelper.getUserId())) {
//                        DoExamActivity.startForResult(activity,
//                                examDetailVo.getPaper().getName(), id, examType, 1,
//                                activity.getIntent().getStringExtra("memberId"), courseExamId);
//                    } else {
//                        DoExamActivity.startForResult(activity,
//                                examDetailVo.getPaper().getName(), id, examType, 0,
//                                activity.getIntent().getStringExtra("memberId"), courseExamId);
//                    }
//                } else {
//                    //ExamResultListActivity.start(activity, id, examType);
//                    DoExamActivity.startForResult(activity,
//                            examDetailVo.getPaper().getName(), id, examType, 2,
//                            activity.getIntent().getStringExtra("memberId"), courseExamId);
//                }
//            }
            doneClick();
        }else if(v.getId() == R.id.reload_bt) {
            pullToRefreshView.showRefresh();
            getData();
        }
    }

    private void getData() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", !TextUtils.isEmpty(stuMemberId) ? stuMemberId : memberId);
        requestVo.addParams("courseExamId", id);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.courseExamDetail + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                pullToRefreshView.onHeaderRefreshComplete();
                ResponseVo<ExamDetailVo> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<ExamDetailVo>>() {
                        });
                if (result.getCode() == 0) {
                    examDetailVo = result.getData();
                    if (examResultGridAdapter != null) {
                        examResultGridAdapter.setExamDetailVo(examDetailVo);
                    }
                    updateView();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                pullToRefreshView.onHeaderRefreshComplete();
            }

            @Override
            public void onFinished() {
                pullToRefreshView.onHeaderRefreshComplete();
            }
        });
    }

    private void updateView() {
        if (examDetailVo != null) {
            topBar.setTitle(examDetailVo.getPaper().getName());
            textViewExamDetail.setText("" + examDetailVo.getPaper().getTotal()
                    + getResources().getString(R.string.exam_item));//题
            String questionTypeText = "";
            if (examDetailVo.getPaper().getSingleNum() > 0) {
                questionTypeText += getResources().getString(R.string.exam_single) + "×";//单选
                questionTypeText += examDetailVo.getPaper().getSingleNum() + ", ";
            }
            if (examDetailVo.getPaper().getMultipleNum() > 0) {
                questionTypeText += getResources().getString(R.string.exam_multiple) + "×";//多选
                questionTypeText += examDetailVo.getPaper().getMultipleNum() + ", ";
            }
            if (examDetailVo.getPaper().getJudgmentNum() > 0) {
                questionTypeText += getResources().getString(R.string.exam_judgment) + "×";//判断
                questionTypeText += examDetailVo.getPaper().getJudgmentNum() + ", ";
            }
            if (examDetailVo.getPaper().getEssayquestionScore() > 0) {
                questionTypeText += getResources().getString(R.string.exam_essayquestion) + "×";//问答
                questionTypeText += examDetailVo.getPaper().getEssayquestionNum() + ", ";
            }
            if (questionTypeText.contains(",")) {
                int index = questionTypeText.lastIndexOf(",");
                if (index >= 0) {
                    questionTypeText = questionTypeText.substring(0, index);
                }
            }
            textViewExamType.setText(questionTypeText);

            textViewTotalScore.setText("" + examDetailVo.getPaper().getScore()
                    + getResources().getString(R.string.points));

            String sortString = "";
            if (examDetailVo.getScore() == -2) {//未提交
                sortString = activity.getResources().getString(R.string.not_commit);
                findViewById(R.id.exam_result_score_lay).setVisibility(View.GONE);
                textViewExamScore.setTextColor(getResources().getColor(R.color.com_text_red));
            } else {
                findViewById(R.id.exam_result_score_lay).setVisibility(View.VISIBLE);
                if (examDetailVo.getScore() == -1) {//未批阅
                    sortString = activity.getResources().getString(R.string.not_mark);
                    textViewExamScore.setTextColor(getResources().getColor(R.color.com_text_red));
                } else {//成绩已公布
                    sortString = examDetailVo.getScore() + activity.getResources().getString(R.string.points);
                    textViewExamScore.setTextColor(getResources().getColor(R.color.com_bg_blue));
                }
                getExamResult();
            }
            textViewExamScore.setText(sortString);
            buttonDone.setVisibility(View.VISIBLE);
            updateDoneButton(buttonDone, getExamActionType());
        }
    }

    private int getExamActionType() {
        int actionType;
        if (examDetailVo == null) {
            return -1;
        }

        int role = UserHelper.getCourseAuthorRole(memberId, courseVo);
        if (examDetailVo.getScore() == -2) {
            if(TextUtils.equals(UserHelper.getUserId(), memberId)) {
                actionType = ExamActionType.INTO_EXAM;
            }else{
                actionType = ExamActionType.VIEW_HOMEWORK;
            }
            if (role == UserHelper.MoocRoleType.TEACHER) {
                actionType = ExamActionType.WATCH_TEST;
            }
        } else {
            actionType = ExamActionType.VIEW_EXAM;
            if (examDetailVo.getScore() == -1) {
                if (!UserHelper.isCourseCounselor(courseVo)
                        && role == UserHelper.MoocRoleType.TEACHER) {
                    actionType = ExamActionType.MARK;
                }
            }
        }
        return actionType;
    }

    private void updateDoneButton(Button button, int actionType) {
        if (button == null) {
            return;
        }
        switch (actionType) {
            case ExamActionType.INTO_EXAM:
                button.setText(R.string.into_exam);
                break;
            case ExamActionType.VIEW_HOMEWORK:
                button.setText(R.string.view_homework);
                break;
            case ExamActionType.VIEW_EXAM:
                button.setText(R.string.view_exam);
                break;
            case ExamActionType.WATCH_TEST:
                button.setText(R.string.watch_test);
                break;
            case ExamActionType.MARK:
                button.setText(R.string.mooc_start_mark);
                break;
        }
    }

    private void doneClick() {
        int actionType = getExamActionType();
        switch (actionType) {
            case ExamActionType.INTO_EXAM:
                doExam(1, -1);
                break;
            case ExamActionType.VIEW_HOMEWORK:
                doExam(0, -1);
                break;
            case ExamActionType.VIEW_EXAM:
                doExam(2, -1);
                break;
            case ExamActionType.WATCH_TEST:
                doExam(2, actionType);
                break;
            case ExamActionType.MARK:
                if (firstQuestionIndex < 0) {
                    firstQuestionIndex = 0;
                }
                doExam(firstQuestionIndex, 2, actionType);
                break;
        }
    }

    private void doExam(int type, int actionType) {
        int roleType = UserHelper.getCourseAuthorRole(memberId, courseVo);
        DoExamActivity.startForResult(activity,
                examDetailVo.getPaper().getName(), id, examType, type,
                memberId, courseExamId, stuMemberId, actionType, roleType);
    }

    private void doExam(int position, int type, int actionType) {
        int roleType = UserHelper.getCourseAuthorRole(memberId, courseVo);
        DoExamActivity.startForResult(activity,
                examDetailVo.getPaper().getName(), id, examType, type, position,
                memberId, courseExamId, stuMemberId, actionType, roleType);
    }

    private void getExamResult() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", !TextUtils.isEmpty(stuMemberId) ? stuMemberId : memberId);
        requestVo.addParams("courseExamId", id);
        requestVo.addParams("type", 2);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.cExamExerList + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<List<ExamItemVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ExamItemVo>>>() {
                        });
                if (result.getCode() == 0) {
                    List<ExamItemVo> examItemVoList = result.getData();
                    examResultGridAdapter.setData(examItemVoList);
                    examResultGridAdapter.notifyDataSetChanged();
                    firstQuestionIndex = getFirstQuestionIndex(examItemVoList);
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                loadFailedLayout.setVisibility(View.VISIBLE);
                LogUtil.d("test", throwable.getMessage());
                closeProgressDialog();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private int getFirstQuestionIndex(List<ExamItemVo> examItemVoList) {
        if (examItemVoList != null && !examItemVoList.isEmpty()) {
            int size = examItemVoList.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    ExamItemVo examItemVo = examItemVoList.get(i);
                    if (examItemVo != null && examItemVo.getCexer() != null
                            && examItemVo.getCexer().getExerciseType() == 4) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DoExamActivity.RC_DoExam
                && resultCode == Activity.RESULT_OK){
            pullToRefreshView.showRefresh();
            getData();
        }
    }
}
