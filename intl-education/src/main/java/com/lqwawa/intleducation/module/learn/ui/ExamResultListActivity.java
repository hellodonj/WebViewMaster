package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.learn.adapter.ExamResultListAdapter;
import com.lqwawa.intleducation.module.learn.vo.ExamItemVo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

public class ExamResultListActivity extends MyBaseActivity implements View.OnClickListener {

    private TopBar topBar;

    private LinearLayout result_title_lay;
    private TextView exam_result_tv;
    private RelativeLayout loadFailedLayout;
    private Button btnReload;
    private ListView listView;
    private Button buttonViewAnswerParsing;

    private String id;
    private int examType;//1作业2考试
    private ExamResultListAdapter examResultListAdapter;

    public static void start(Activity activity, String id, int examType) {
        activity.startActivity(new Intent(activity, ExamResultListActivity.class)
                .putExtra("id", id).putExtra("examType", examType));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result_list);
        topBar = (TopBar)findViewById(R.id.top_bar);
        result_title_lay = (LinearLayout)findViewById(R.id.result_title_lay);
        exam_result_tv = (TextView)findViewById(R.id.exam_result_tv);
        loadFailedLayout = (RelativeLayout)findViewById(R.id.load_failed_layout);
        btnReload = (Button)findViewById(R.id.reload_bt);
        listView = (ListView)findViewById(R.id.list_view);
        buttonViewAnswerParsing = (Button)findViewById(R.id.view_answer_parsing_bt);
        id = getIntent().getStringExtra("id");
        examType = getIntent().getIntExtra("examType", 1);
        initViews();
    }

    private void initViews() {
        topBar.setTitle("" +
                getResources().getString(examType == 1 ? R.string.homework : R.string.exam)
                + getResources().getString(R.string.exam_result));
        btnReload.setOnClickListener(this);
        topBar.setBack(true);
        buttonViewAnswerParsing.setOnClickListener(this);
        examResultListAdapter = new ExamResultListAdapter(activity);
        listView.setAdapter(examResultListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DoExamActivity.startForResult(activity, "",
                        ExamResultListActivity.this.id, examType, 2, position,
                        getIntent().getStringExtra("memberId"), null, null, -1, 0);
            }
        });
        getData();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.reload_bt) {
            getData();
        }else if(v.getId()==R.id.view_answer_parsing_bt) {
            DoExamActivity.startForResult(activity, "", id, examType, 2,
                    getIntent().getStringExtra("memberId"), null, null, -1, 0);
        }
    }

    private void getData() {
        loadFailedLayout.setVisibility(View.GONE);
        showProgressDialog(getResources().getString(R.string.loading));
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("courseExamId", id);
        requestVo.addParams("type", 2);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.cExamExerList+ requestVo.getParams());
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
                    examResultListAdapter.setData(examItemVoList);
                    examResultListAdapter.notifyDataSetChanged();
                    int rstScore = 0;
                    int totalScore = 0;
                    boolean marked = true;
                    for (int i = 0; i < examItemVoList.size(); i++) {
                        if (examItemVoList.get(i).getUexer() != null) {
                            if (examItemVoList.get(i).getUexer().getScore() >= 0) {
                                int score = examItemVoList.get(i).getUexer().getScore();
                                if (score < 0) {
                                    marked = false;
                                }
                                rstScore += score;
                                totalScore += examItemVoList.get(i).getCexer().getScore();
                            }
                        }
                    }
                    if (marked) {
                        result_title_lay.setVisibility(View.VISIBLE);
                        exam_result_tv.setText(getResources().getString(R.string.this_time)
                                + getResources().getString(examType == 1 ? R.string.homework : R.string.exam)
                                + getResources().getString(R.string.score_is_with_colon)
                                + rstScore + getResources().getString(R.string.points)
                        + "！(" + getResources().getString(R.string.total_score)
                                + totalScore + getResources().getString(R.string.points)+ ")");
                    } else {
                        result_title_lay.setVisibility(View.GONE);
                    }
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
                LogUtil.d("test", throwable.getMessage());
                closeProgressDialog();
            }

            @Override
            public void onFinished() {
            }
        });
    }


}
