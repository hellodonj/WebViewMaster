package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.learn.adapter.TaskChapterListAdapter;
import com.lqwawa.intleducation.module.learn.vo.ExamVo;
import com.lqwawa.intleducation.module.learn.vo.TaskChaptersVo;

public class TaskChapterSelectActivity extends MyBaseActivity {
    public static int Rc_taskChapterSelect = 2560;

    private TopBar topBar;
    private ListView listView;
    private Button buttonClear;
    private Button buttonCommit;

    private TaskChapterListAdapter taskChapterListAdapter;
    private TaskChaptersVo taskChaptersVo = null;
    private ExamVo selectExamVo = null;
    public static void startForResult(Activity activity,TaskChaptersVo taskChaptersVo){
        activity.startActivityForResult(new Intent(activity, TaskChapterSelectActivity.class)
                .putExtra("TaskChaptersVo", taskChaptersVo), Rc_taskChapterSelect);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_chapter_select);
        taskChaptersVo = (TaskChaptersVo)getIntent().getSerializableExtra("TaskChaptersVo");
        initView();
    }

    private void initView(){
        topBar = (TopBar) findViewById(R.id.top_bar);
        topBar.setBack(true);
        topBar.setTitle(getResources().getString(R.string.select_chapter_please));
        buttonClear = (Button) findViewById(R.id.clear_bt);
        buttonCommit = (Button) findViewById(R.id.commit_bt);
        buttonCommit.setEnabled(false);
        buttonClear.setEnabled(false);
        listView = (ListView)findViewById(R.id.listView);
        taskChapterListAdapter = new TaskChapterListAdapter(activity, new TaskChapterListAdapter.SelectChangedListener() {
            @Override
            public void onSelectOn(ExamVo vo) {
                if (vo != null){
                    selectExamVo = vo;
                    buttonCommit.setEnabled(true);
                    buttonClear.setEnabled(true);
                }
            }
        });
        taskChapterListAdapter.setData(taskChaptersVo.getList());
        listView.setAdapter(taskChapterListAdapter);
        taskChapterListAdapter.notifyDataSetChanged();
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskChapterListAdapter.clearSelect(true);
                buttonCommit.setEnabled(false);
                buttonClear.setEnabled(false);
            }
        });

        buttonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*setResult(Activity.RESULT_OK,
                        new Intent().putExtra(TaskEditActivity.TASK_ID, selectExamVo.getId()));*/
                finish();
            }
        });
    }
}
