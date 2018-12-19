package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.module.learn.adapter.ExamPagerAdapter;
import com.lqwawa.intleducation.module.learn.vo.ExamActionType;
import com.lqwawa.intleducation.module.learn.vo.ExamItemVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class DoExamActivity extends MyBaseFragmentActivity implements View.OnClickListener {

    private TopBar topBar;
    private ViewPager viewPager;
    private Button buttonPreviousPager;
    private Button buttonNextPager;
    private Button buttonCommit;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;


    private String id;
    private int examType;//1作业2考试
    private int type;//1做题，2查看
    private int startPos = 0;

    private String memberId;
    private String courseExamId;
    private String stuMemberId;
    private int examActionType;
    private int roleType;
    private int lastQuestionIndex = -1;
    ExamPagerAdapter examPagerAdapter;

    public static int RC_DoExam = 1025;

    public static void startForResult(Activity activity, String examName, String id, int examType,
                                      int type, String memberId, String courseExamId, String
                                              stuMemberId, int examActionType, int roleType) {
        activity.startActivityForResult(new Intent(activity, DoExamActivity.class)
                .putExtra("examName", examName)
                .putExtra("id", id)
                .putExtra("examType", examType)
                .putExtra("type", type)
                .putExtra("memberId", memberId)
                .putExtra("courseExamId", courseExamId)
                .putExtra("stuMemberId", stuMemberId)
                .putExtra("examActionType", examActionType)
                .putExtra("roleType", roleType), RC_DoExam);
    }

    public static void startForResult(Activity activity, String examName, String id, int examType,
                                      int type, int startPos, String memberId,String
                                              courseExamId, String stuMemberId, int examActionType, int roleType) {
        activity.startActivityForResult(new Intent(activity, DoExamActivity.class)
                .putExtra("examName", examName)
                .putExtra("id", id)
                .putExtra("examType", examType)
                .putExtra("type", type)
                .putExtra("startPos", startPos)
                .putExtra("memberId", memberId)
                .putExtra("courseExamId", courseExamId)
                .putExtra("stuMemberId", stuMemberId)
                .putExtra("examActionType", examActionType)
                .putExtra("roleType", roleType), RC_DoExam);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_exam);

        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setTitle(getIntent().getStringExtra("examName"));
        topBar.setRightFunctionText1TextColor(activity.getResources().getColor(R.color.text_green));
        viewPager = (ViewPager) findViewById(R.id.view_paper);
        buttonPreviousPager = (Button) findViewById(R.id.previous_pager_bt);
        buttonNextPager = (Button) findViewById(R.id.next_pager_bt);
        buttonCommit = (Button) findViewById(R.id.commit_bt);
        loadFailedLayout = (RelativeLayout) findViewById(R.id.load_failed_layout);
        btnReload = (Button) findViewById(R.id.reload_bt);

        id = getIntent().getStringExtra("id");
        examType = getIntent().getIntExtra("examType", 1);
        type = getIntent().getIntExtra("type", 2);
        startPos = getIntent().getIntExtra("startPos", 0);
        memberId = getIntent().getStringExtra("memberId");
        courseExamId = getIntent().getStringExtra("courseExamId");
        stuMemberId = getIntent().getStringExtra("stuMemberId");
        examActionType = getIntent().getIntExtra("examActionType", -1);
        roleType = getIntent().getIntExtra("roleType", 0);
        initViews();
    }

    private void initViews() {
        topBar.setTitleVisibility(View.INVISIBLE);
        btnReload.setOnClickListener(this);
        topBar.setBack(true);
        buttonPreviousPager.setOnClickListener(this);
        buttonNextPager.setOnClickListener(this);
        buttonCommit.setOnClickListener(this);
        examPagerAdapter = new ExamPagerAdapter(this, type, new MyBaseAdapter.OnContentChangedListener() {
            @Override
            public void OnContentChanged() {
                if(buttonCommit.getVisibility() != View.VISIBLE){
                    int curItem = viewPager.getCurrentItem();
                    if (curItem < examPagerAdapter.getCount() - 1) {
                        viewPager.setCurrentItem(curItem + 1);
                        changePage(curItem, examPagerAdapter.getCount());
                    }
                }
            }
        });
        examPagerAdapter.setExamActionType(examActionType);
        examPagerAdapter.setRoleType(roleType);
        viewPager.setAdapter(examPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                changePage(i, examPagerAdapter.getCount());
                hideKeyboard();
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        getData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reload_bt) {
            getData();
        } else if (v.getId() == R.id.previous_pager_bt) {
            int curItem = viewPager.getCurrentItem();
            if (curItem > 0) {
                viewPager.setCurrentItem(curItem - 1);
                changePage(curItem, examPagerAdapter.getCount());
            }
        } else if (v.getId() == R.id.next_pager_bt) {
            int curItem = viewPager.getCurrentItem();
            if (curItem < examPagerAdapter.getCount() - 1) {
                viewPager.setCurrentItem(curItem + 1);
                changePage(curItem, examPagerAdapter.getCount());
            }
        } else if (v.getId() == R.id.commit_bt) {
            if (examActionType == ExamActionType.MARK) {
                showTeacherMarkDialog();
            } else {
                showStuCommitDialog();
            }
        }
    }

    private void showTeacherMarkDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        if(examPagerAdapter.isDoAllQuestions()) {
            builder.setMessage(activity.getResources().getString(R.string.mark_commit_tip));
        }else{
            builder.setMessage(activity.getResources().getString(R.string.mark_not_commit_tip));
        }
        builder.setTitle(activity.getResources().getString(R.string.tip));
        builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 老师问答题没有全部批阅不允许提交
                        if (!examPagerAdapter.isDoAllQuestions()) {
                            return;
                        }
                        doMark();
                    }
                });

        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void showStuCommitDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(activity);
        if(examPagerAdapter.isDoAll()) {
            builder.setMessage(activity.getResources().getString(type == 1 ?
                    R.string.test_commit_tip : R.string.exam_commit_tip));
        }else{
            builder.setMessage(activity.getResources().getString(R.string.some_exam_no_answer));
        }
        builder.setTitle(activity.getResources().getString(R.string.tip));
        builder.setPositiveButton(activity.getResources().getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doCommit();
                    }
                });

        builder.setNegativeButton(activity.getResources().getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void getData() {
        loadFailedLayout.setVisibility(View.GONE);
        showProgressDialog(getResources().getString(R.string.loading));
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("token", !TextUtils.isEmpty(stuMemberId) ? stuMemberId : memberId);
        requestVo.addParams("courseExamId", id);
        requestVo.addParams("type", 2);
        RequestParams params =
                new RequestParams((type > 0 ? AppConfig.ServerUrl.cExamExerList
                        : AppConfig.ServerUrl.getCourseExamItemsList)+ requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<List<ExamItemVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ExamItemVo>>>() {
                        });
                if (result.getCode() == 0) {
                    loadFailedLayout.setVisibility(View.GONE);
                    List<ExamItemVo> examItemVoList = result.getData();
                    examPagerAdapter.setData(examItemVoList);
                    examPagerAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(startPos, false);
                    lastQuestionIndex = getLastQuestionIndex(examItemVoList);
                    changePage(startPos, examItemVoList.size());
                    topBar.setTitleVisibility(View.VISIBLE);
                } else {
                    loadFailedLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                loadFailedLayout.setVisibility(View.VISIBLE);
                closeProgressDialog();
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private int getLastQuestionIndex(List<ExamItemVo> examItemVoList) {
        if (examItemVoList != null && !examItemVoList.isEmpty()) {
            int size = examItemVoList.size();
            if (size > 0) {
                for (int i = size - 1; i >= 0; i--) {
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

    private void doMark() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("userExamId", courseExamId);
        requestVo.addParams("token", UserHelper.getUserId());
        requestVo.addParams("userExerList", examPagerAdapter.getTeacherMarkJson(), true);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.userExamMark + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, getResources().getString(R.string.mooc_mark)
                            + getResources().getString(R.string.success));
                    setResult(Activity.RESULT_OK);
                    sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.userExamMark));
                    finish();
                } else {
                    ToastUtil.showToast(activity, getResources().getString(R.string.mooc_mark)
                            + getResources().getString(R.string.failed)
                            + " error code:" + result.getCode());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(activity, getResources().getString(R.string.commit)
                        + getResources().getString(R.string.failed) + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void doCommit() {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("cexamId", id);
        requestVo.addParams("exers", examPagerAdapter.getUexerJson(), true);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.userExamSave + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, getResources().getString(R.string.commit)
                            + getResources().getString(R.string.success));
                    //ExamResultListActivity.start(activity, id, examType);
                    //DoExamActivity.startForResult(activity, id, examType, 2);
                    setResult(Activity.RESULT_OK);
                    sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.userExamSave));
                    finish();
                } else {
                    ToastUtil.showToast(activity, getResources().getString(R.string.commit)
                            + getResources().getString(R.string.failed)
                            + " error code:" + result.getCode());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(activity, getResources().getString(R.string.commit)
                        + getResources().getString(R.string.failed) + throwable.getMessage());
            }

            @Override
            public void onFinished() {
            }
        });
    }

    View.OnClickListener topBarRightText1ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private void changePage(int curIndex, int size) {
        topBar.setRightFunctionText1((curIndex + 1) + "/" + size, topBarRightText1ClickListener);
        boolean isTeacherMark = (examActionType == ExamActionType.MARK)
                && (lastQuestionIndex >= 0)
                && (curIndex == lastQuestionIndex);
        if((curIndex == size - 1 && type == 1) || isTeacherMark){
            findViewById(R.id.bottom_lay).setVisibility(View.VISIBLE);
            buttonCommit.setVisibility(View.VISIBLE);
            if (isTeacherMark) {
                buttonCommit.setText(R.string.mooc_finish_mark);
            } else {
                buttonCommit.setText(R.string.commit_exam);
            }
        }else{
            findViewById(R.id.bottom_lay).setVisibility(View.GONE);
            buttonCommit.setVisibility(View.GONE);
        }
        /*if (curIndex == 0) {
            if (size == 1) {
                buttonPreviousPager.setVisibility(View.GONE);
                buttonNextPager.setVisibility(View.GONE);
                buttonCommit.setVisibility(View.VISIBLE);
            } else {
                buttonPreviousPager.setVisibility(View.GONE);
                buttonNextPager.setVisibility(View.VISIBLE);
                buttonCommit.setVisibility(View.GONE);
            }
        } else if (curIndex == size - 1) {
            buttonPreviousPager.setVisibility(View.VISIBLE);
            buttonNextPager.setVisibility(View.GONE);
            buttonCommit.setVisibility(View.VISIBLE);
        } else {
            buttonPreviousPager.setVisibility(View.VISIBLE);
            buttonNextPager.setVisibility(View.VISIBLE);
            buttonCommit.setVisibility(View.GONE);
        }

        if (type != 1) {
            buttonCommit.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onBackPressed() {
        if (viewPager != null) {
            int curItem = viewPager.getCurrentItem();
            if (curItem > 0) {
                viewPager.setCurrentItem(curItem - 1);
                changePage(curItem, examPagerAdapter.getCount());
                return;
            }
        }
        super.onBackPressed();
    }
}
