package com.lqwawa.intleducation.module.learn.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;
import com.lqwawa.intleducation.common.ui.treeview.factroy.MyNodeViewFactory;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.ReadWeikeHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.add.AddSubjectActivity;
import com.lqwawa.intleducation.module.discovery.vo.ExamsAndTestExtrasVo;
import com.lqwawa.intleducation.module.discovery.vo.SxExamDetailVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lqwawa.intleducation.module.learn.ui.LessonDetailsActivity.SUBJECT_SETTING_REQUEST_CODE;

public class ExamsAndTestsActivity extends AppCompatActivity implements DataSource.Callback<ResponseVo<SxExamDetailVo>>, View.OnClickListener {

    private TopBar topBar;
    private FrameLayout mNewCartContainer, mBottomLayout, container;
    private TreeNode root;
    private TreeView treeView;
    private boolean mTeacherVisitor;
    private CourseDetailParams courseParams;
    private TextView okBtn, selectAll, mTvPoint, mTvCartPoint, cancelBtn, mAddCartContainer;
    private String TAG = getClass().getSimpleName();
    private boolean lessonNeedFlag;
    private int status;
    private int libraryType;
    private boolean isVideoCourse;
    private boolean mClassTeacher;
    private LinearLayout llSelectAction;
    private MyNodeViewFactory myNodeViewFactory;
    private ReadWeikeHelper mReadWeikeHelper;
    private LessonSourceParams lessonSourceParams;
    private Map<Integer, List<SectionResListVo>> addToCartInDifferentTypes = new HashMap<>();
    private ExamsAndTestExtrasVo extrasVo;

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
        okBtn = (TextView) findViewById(R.id.ok_btn);
        llSelectAction = (LinearLayout) findViewById(R.id.ll_select_action);
        mTvCartPoint = (TextView) findViewById(R.id.tv_cart_point);
        root = TreeNode.root();
        myNodeViewFactory = new MyNodeViewFactory();
        treeView = new TreeView(root, this, myNodeViewFactory);
        topBar.setBack(true);

        mNewCartContainer.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        mAddCartContainer.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshCartPoint();
    }

    private void getData() {
        Intent intent = getIntent();
        String courseId = intent.getStringExtra("courseId");
        String sectionId = intent.getStringExtra("sectionId");
        mTeacherVisitor = intent.getBooleanExtra("isTeacherVisitor", false);
//        courseParams = (CourseDetailParams) intent.getSerializableExtra("courseDetailParams");
        lessonSourceParams = (LessonSourceParams) intent.getSerializableExtra("lessonSourceParams");
        status = intent.getIntExtra("status", -1);
        libraryType = intent.getIntExtra("libraryType",5);
//        Log.e(TAG, "getData: " + lessonSourceParams.getRole() + "---courseParams" + courseParams.getSchoolId());
        lessonNeedFlag = lessonSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER;
        if (lessonSourceParams != null) courseParams = lessonSourceParams.getCourseParams();
        mReadWeikeHelper = new ReadWeikeHelper(this);
        treeView.setExtras(mReadWeikeHelper);

        isVideoCourse = courseParams != null && (courseParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                || (courseParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && courseParams.isVideoCourse()));
        mClassTeacher = EmptyUtil.isNotEmpty(courseParams) && courseParams.isClassCourseEnter() && EmptyUtil.isNotEmpty(courseParams.getClassId());

        if (!mTeacherVisitor && courseParams != null &&
                courseParams.isClassCourseEnter() &&
                courseParams.isClassTeacher() ||
                lessonSourceParams.isChoiceMode()) {
            mBottomLayout.setVisibility(View.VISIBLE);
            mNewCartContainer.setVisibility(View.VISIBLE);
        } else {
            mBottomLayout.setVisibility(View.GONE);
        }
        refreshCartPoint();
        LQCourseHelper.getSxExamDetail(courseId, sectionId, this);
    }

    public static void start(Context context, String courseId, String sectionId, boolean mTeacherVisitor, int status,int libraryType, LessonSourceParams lessonSourceParams) {
        Intent intent = new Intent(context, ExamsAndTestsActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("sectionId", sectionId);
        intent.putExtra("lessonSourceParams", lessonSourceParams);
        intent.putExtra("status", status);
        intent.putExtra("libraryType",libraryType);
        intent.putExtra("mTeacherVisitor", mTeacherVisitor);
//        intent.putExtra("courseDetailParams", courseDetailParams);

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
        extrasVo = new ExamsAndTestExtrasVo(courseParams == null ? "" : courseParams.getSchoolId(), lessonSourceParams, lessonNeedFlag, status, isVideoCourse, mClassTeacher, false, lessonSourceParams.isChoiceMode(),libraryType);

        List<SxExamDetailVo.TaskListVO> taskList = examDetailVo.taskList;
        for (SxExamDetailVo.TaskListVO taskListVO : taskList) {
            TreeNode treeNode = new TreeNode(taskListVO);
            treeNode.setLevel(0);
            for (SectionResListVo datum : taskListVO.data) {
                datum.setTaskType(taskListVO.taskType);
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
        } else if (id == R.id.ok_btn) {
            List<TreeNode> selectedNodes = treeView.getSelectedNodes();
            addToCartInDifferentTypes.clear();
            for (int i = 0; i < selectedNodes.size(); i++) {
                Object value = selectedNodes.get(i).getValue();
                if (value instanceof SectionResListVo) {
                    SectionResListVo vo = (SectionResListVo) value;
                    int taskType = vo.getTaskType();
                    List<SectionResListVo> vos = addToCartInDifferentTypes.get(taskType);
                    if (vos == null) vos = new ArrayList<>();
                    vos.add(vo);
                    addToCartInDifferentTypes.put(taskType, vos);
                }
            }
            if (EmptyUtil.isEmpty(selectedNodes)) {
                UIUtil.showToastSafe(R.string.str_select_tips);
                return;
            }
            if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
                int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
                if (count > 6) {
                    UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                    return;
                }
            }
            Set<Map.Entry<Integer, List<SectionResListVo>>> entries = addToCartInDifferentTypes.entrySet();
            for (Map.Entry<Integer, List<SectionResListVo>> entry : entries) {
                List<SectionResListVo> choiceArray = entry.getValue();
                confirmResourceCart(choiceArray);
            }
            updateView(false);
        } else if (id == R.id.new_cart_container) {
            handleSubjectSettingData(this, UserHelper.getUserId());
        }
    }

    private void updateView(boolean isAddToCart) {
        llSelectAction.setVisibility(isAddToCart ? View.VISIBLE : View.GONE);
        mAddCartContainer.setVisibility(isAddToCart ? View.GONE : View.VISIBLE);
//        treeView.setIsShowCheckBox(isAddToCart);
        if (extrasVo !=null) extrasVo.setmChoiceMode(isAddToCart);
        treeView.notifychanged();
        treeView.deselectAll();
    }

    /**
     * 刷新红点
     */
    private void refreshCartPoint() {
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
//            mTvPoint.setText(Integer.toString(count));
            mTvCartPoint.setText(Integer.toString(count));
            if (count == 0 || mBottomLayout.isActivated()) {
//                mTvPoint.setVisibility(View.GONE);
                if (count == 0) {
                    mTvCartPoint.setVisibility(View.GONE);
                }
            } else {
//                mTvPoint.setVisibility(View.VISIBLE);
                mTvCartPoint.setVisibility(View.VISIBLE);
            }
        }
    }

    private int confirmResourceCart(List<SectionResListVo> choiceArray) {
        // UIUtil.showToastSafe("确定所有作业库中的资源");
        // 获取指定Tab所有的选中的作业库资源
//        Log.e(TAG, "confirmResourceCart: " + choiceArray.size());
        if (EmptyUtil.isEmpty(choiceArray)) {
            UIUtil.showToastSafe(R.string.str_select_tips);
            return 0;
        }
        // 添加到作业库中
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            // 默认看课件
            int lqwawaTaskType = 9;
            int moocTaskType = choiceArray.get(0).getTaskType();
            if (moocTaskType == 1) {
                lqwawaTaskType = 9;
            } else if (moocTaskType == 2) {
                lqwawaTaskType = 5;
            } else if (moocTaskType == 3) {
                lqwawaTaskType = 8;
            } else if (moocTaskType == 4) {
                // 多出来的看课本类型
                lqwawaTaskType = 9;
            } else if (moocTaskType == 5) {
                // 讲解课类型
                lqwawaTaskType = 5;
            } else if (moocTaskType == 6) {
                // Q配音
                lqwawaTaskType = 14;
            }
            TaskSliderHelper.onWorkCartListener.putResourceToCart((ArrayList<SectionResListVo>) choiceArray, lqwawaTaskType);
            // 刷新数目
            refreshCartPoint();
        }

        return choiceArray.size();
    }

    public void handleSubjectSettingData(Context context,
                                         String memberId) {
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestSetupConfigData(memberId, SetupConfigType.TYPE_TEACHER, languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                //没有数据
                popChooseSubjectDialog(context);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                if (entities == null || entities.size() == 0) {
                    popChooseSubjectDialog(context);
                } else {
                    //有数据
                    triggerWatchCart();
                }
            }
        });
    }

    /**
     * 触发查看作业库的动作
     */
    private void triggerWatchCart() {
        // UIUtil.showToastSafe("触发查看作业库的动作");
        if (EmptyUtil.isNotEmpty(TaskSliderHelper.onWorkCartListener)) {
            if (EmptyUtil.isNotEmpty(courseParams)) {
                Bundle extras = getIntent().getBundleExtra(Common.Constance.KEY_EXTRAS_STUDY_TASK);
                TaskSliderHelper.onWorkCartListener.enterIntroTaskDetailActivity(this, courseParams.getSchoolId(), courseParams.getClassId(), extras);
            }
        }
    }

    private static void popChooseSubjectDialog(Context context) {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                context,
                null,
                context.getString(R.string.label_unset_choose_subject),
                context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                context.getString(R.string.label_choose_subject),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddSubjectActivity.show((Activity) context, false, SUBJECT_SETTING_REQUEST_CODE);
                    }
                });
        messageDialog.show();
    }

}
