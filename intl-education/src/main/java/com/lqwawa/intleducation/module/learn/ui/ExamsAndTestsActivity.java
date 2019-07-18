package com.lqwawa.intleducation.module.learn.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;
import com.lqwawa.intleducation.common.ui.treeview.factroy.MyNodeViewFactory;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.vo.SxExamDetailVo;

import java.util.List;

public class ExamsAndTestsActivity extends AppCompatActivity implements DataSource.Callback<ResponseVo<SxExamDetailVo>> {

    private TopBar topBar;
    private FrameLayout container;
    private TreeNode root;
    private TreeView treeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_and_tests);

        initView();
        getData();
    }

    private void initView() {
        topBar = (TopBar) findViewById(R.id.top_bar);
        container = (FrameLayout) findViewById(R.id.container);

        root = TreeNode.root();
        treeView = new TreeView(root, this, new MyNodeViewFactory());
        topBar.setBack(true);

    }

    private void getData() {
        Intent intent = getIntent();
        String courseId = intent.getStringExtra("courseId");
        String sectionId = intent.getStringExtra("sectionId");
        String fromType = intent.getStringExtra("fromType");
        int role = intent.getIntExtra("role", -1);
        LQCourseHelper.getSxExamDetail(courseId, sectionId, this);
    }

    //fromType 最初是从哪个页面跳转过来的，比如班级课程，三系教案馆等
    public static void start(Context context, String courseId, String sectionId, int role, String fromType) {
        Intent intent = new Intent(context, ExamsAndTestsActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("sectionId", sectionId);
        intent.putExtra("role", role);
        intent.putExtra("fromType", fromType);
        context.startActivity(intent);
    }

    @Override
    public void onDataLoaded(ResponseVo<SxExamDetailVo> responseVo) {
        SxExamDetailVo examDetailVo = responseVo.getData();
        if (examDetailVo == null) return;
        topBar.setTitle(examDetailVo.sectionName);
        formatData(examDetailVo);
    }

    private void formatData(SxExamDetailVo examDetailVo) {
        List<SxExamDetailVo.TaskListVO> taskList = examDetailVo.taskList;
        for (SxExamDetailVo.TaskListVO taskListVO : taskList) {
            TreeNode treeNode = new TreeNode(taskListVO);
            treeNode.setLevel(0);
            for (SxExamDetailVo.TaskListVO.DetailVo datum : taskListVO.data) {
                TreeNode treeNode1 = new TreeNode(datum);
                treeNode1.setLevel(1);
                treeNode.addChild(treeNode1);
            }
            root.addChild(treeNode);
        }
        View view = treeView.getView();
        treeView.expandAll();
        container.addView(view);
    }

    @Override
    public void onDataNotAvailable(int strRes) {
        UIUtil.showToastSafe(strRes);
    }
}
