package com.lqwawa.intleducation.module.learn.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;
import com.lqwawa.intleducation.common.ui.treeview.base.BaseNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.binder.SecondLevelNodeViewBinder;
import com.lqwawa.intleducation.common.ui.treeview.factroy.MyNodeViewFactory;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.vo.ExamsAndTestExtrasVo;
import com.lqwawa.intleducation.module.discovery.vo.SxExamDetailVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;
import java.util.List;

public class ExamsAndTestsActivity extends AppCompatActivity implements DataSource.Callback<ResponseVo<SxExamDetailVo>>, View.OnClickListener {

    private TopBar topBar;
    private FrameLayout mNewCartContainer, mBottomLayout, container;
    private TreeNode root;
    private TreeView treeView;
    private boolean mTeacherVisitor;
    private CourseDetailParams courseParams;
    private TextView selectAll, cancelBtn, mAddCartContainer;
    private String TAG = getClass().getSimpleName();
    private boolean lessonNeedFlag;
    private int status;
    private boolean isVideoCourse;
    private boolean mClassTeacher;
    private LinearLayout llSelectAction;
    private MyNodeViewFactory myNodeViewFactory;

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
        mBottomLayout = (FrameLayout) findViewById(R.id.bottom_layout);
        mNewCartContainer = (FrameLayout) findViewById(R.id.new_cart_container);
        mAddCartContainer = (TextView) findViewById(R.id.add_to_cart);
        cancelBtn = (TextView) findViewById(R.id.cancel_btn);
        selectAll = (TextView) findViewById(R.id.select_all);
        llSelectAction = (LinearLayout) findViewById(R.id.ll_select_action);

        root = TreeNode.root();
        myNodeViewFactory = new MyNodeViewFactory();
        treeView = new TreeView(root, this, myNodeViewFactory);
        topBar.setBack(true);

        mNewCartContainer.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        mAddCartContainer.setOnClickListener(this);
        selectAll.setOnClickListener(this);
    }

    private void getData() {
        Intent intent = getIntent();
        String courseId = intent.getStringExtra("courseId");
        String sectionId = intent.getStringExtra("sectionId");
        mTeacherVisitor = intent.getBooleanExtra("isTeacherVisitor", false);
        courseParams = (CourseDetailParams) intent.getSerializableExtra("courseDetailParams");
        int role = intent.getIntExtra("role", -1);
        status = intent.getIntExtra("status", -1);
//        Log.e(TAG, "getData: " + role );
        lessonNeedFlag = role != UserHelper.MoocRoleType.TEACHER;

        isVideoCourse = courseParams != null && (courseParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                || (courseParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && courseParams.isVideoCourse()));
        mClassTeacher = EmptyUtil.isNotEmpty(courseParams) && courseParams.isClassCourseEnter() && EmptyUtil.isNotEmpty(courseParams.getClassId());

        if (!mTeacherVisitor &&
                courseParams.isClassCourseEnter() &&
                courseParams.isClassTeacher()) {
            mBottomLayout.setVisibility(View.VISIBLE);
            mNewCartContainer.setVisibility(View.VISIBLE);

        } else {
            mBottomLayout.setVisibility(View.GONE);
        }

        LQCourseHelper.getSxExamDetail(courseId, sectionId, this);
    }

    //fromType 最初是从哪个页面跳转过来的，比如班级课程，三系教案馆等
    public static void start(Context context, String courseId, String sectionId, int role, boolean mTeacherVisitor, CourseDetailParams courseDetailParams, int status) {
        Intent intent = new Intent(context, ExamsAndTestsActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("sectionId", sectionId);
        intent.putExtra("role", role);
        intent.putExtra("status", status);
        intent.putExtra("mTeacherVisitor", mTeacherVisitor);
        intent.putExtra("courseDetailParams", courseDetailParams);
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
        ExamsAndTestExtrasVo extrasVo = new ExamsAndTestExtrasVo(lessonNeedFlag, status, isVideoCourse, mClassTeacher, false, false);

        List<SxExamDetailVo.TaskListVO> taskList = examDetailVo.taskList;
        for (SxExamDetailVo.TaskListVO taskListVO : taskList) {
            TreeNode treeNode = new TreeNode(taskListVO);
            treeNode.setLevel(0);
            for (SxExamDetailVo.TaskListVO.DetailVo datum : taskListVO.data) {
                datum.taskType = taskListVO.taskType;
                TreeNode treeNode1 = new TreeNode(datum);
                treeNode1.setExtras(extrasVo);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_to_cart || id == R.id.cancel_btn) {
            boolean isAddToCart = id == R.id.add_to_cart;
            updateView(isAddToCart);
        } else if (id == R.id.select_all) {
            String selectAllText = getString(R.string.select_all);
            String deselectAll = getString(R.string.deselect_all);
            String text = selectAll.getText().toString();
            boolean isSelectState = selectAllText.equals(text);
            if (isSelectState) {
                selectAll.setText(deselectAll);
                treeView.selectAll();
            } else {
                selectAll.setText(selectAllText);
                treeView.deselectAll();
            }
        }
    }

    private void updateView(boolean isAddToCart) {
        llSelectAction.setVisibility(isAddToCart ? View.VISIBLE : View.GONE);
        mAddCartContainer.setVisibility(isAddToCart ? View.GONE : View.VISIBLE);
        selectAll.setVisibility(isAddToCart ? View.VISIBLE : View.GONE);
        treeView.setIsShowCheckBox(isAddToCart);
        treeView.notifychanged();
    }

}
