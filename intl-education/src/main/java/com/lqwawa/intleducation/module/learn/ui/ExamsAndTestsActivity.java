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
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.common.ui.ContactsMessageDialog;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;
import com.lqwawa.intleducation.common.ui.treeview.factroy.MyNodeViewFactory;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lqwawa.intleducation.module.learn.ui.LessonDetailsActivity.SUBJECT_SETTING_REQUEST_CODE;

public class ExamsAndTestsActivity extends AppCompatActivity implements DataSource.Callback<ResponseVo<SxExamDetailVo>>, View.OnClickListener {

    private static Activity activity;
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
    private int taskType, libraryType;
    private boolean isVideoCourse;
    private boolean mClassTeacher;
    private LinearLayout llSelectAction;
    private MyNodeViewFactory myNodeViewFactory;
    private ReadWeikeHelper mReadWeikeHelper;
    private LessonSourceParams lessonSourceParams;
    private Map<Integer, List<SectionResListVo>> addToCartInDifferentTypes = new HashMap<>();
    private ArrayList<SectionResListVo> selectedTask = new ArrayList<>();
    private ExamsAndTestExtrasVo extrasVo;
    private boolean choiceModeAndIntiativeTrigger;
    private boolean isChoiceMode;
    private boolean isInitiativeTrigger;
    private static String[] mTypes = UIUtil.getStringArray(R.array.label_test_lesson_source_type);

    private String courseId;
    private int sourceType;

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
        courseId = intent.getStringExtra("courseId");
        sourceType = intent.getIntExtra("sourceType",1);
        String sectionId = intent.getStringExtra("sectionId");
        mTeacherVisitor = intent.getBooleanExtra("isTeacherVisitor", false);
//        courseParams = (CourseDetailParams) intent.getSerializableExtra("courseDetailParams");
        lessonSourceParams = (LessonSourceParams) intent.getSerializableExtra("lessonSourceParams");
        status = intent.getIntExtra("status", -1);
        libraryType = intent.getIntExtra("libraryType", 5);
        taskType = intent.getIntExtra("taskType", -1);

        lessonNeedFlag = lessonSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER;
        if (lessonSourceParams != null) courseParams = lessonSourceParams.getCourseParams();
        mReadWeikeHelper = new ReadWeikeHelper(this);
        treeView.setExtras(mReadWeikeHelper);

        isVideoCourse = courseParams != null && (courseParams.getLibraryType() == OrganLibraryType.TYPE_VIDEO_LIBRARY
                || (courseParams.getLibraryType() == OrganLibraryType.TYPE_BRAIN_LIBRARY && courseParams.isVideoCourse()));
        mClassTeacher = EmptyUtil.isNotEmpty(courseParams) && courseParams.isClassCourseEnter() && EmptyUtil.isNotEmpty(courseParams.getClassId());
        //主动进入
        //主动进入，并选择true，非主动进入，并选择，false， 非主动进入，并不选择，false
        isChoiceMode = lessonSourceParams != null && lessonSourceParams.isChoiceMode();
        isInitiativeTrigger = lessonSourceParams != null && lessonSourceParams.isInitiativeTrigger();
        choiceModeAndIntiativeTrigger = isChoiceMode && isInitiativeTrigger;
        boolean isShowBottomLayout = !mTeacherVisitor && courseParams != null && courseParams.isClassCourseEnter() && courseParams.isClassTeacher() || choiceModeAndIntiativeTrigger;
        if (isShowBottomLayout) {
            llSelectAction.setVisibility(choiceModeAndIntiativeTrigger ? View.VISIBLE : View.GONE);
            mAddCartContainer.setVisibility(choiceModeAndIntiativeTrigger ? View.GONE : View.VISIBLE);
            cancelBtn.setVisibility(isChoiceMode ? View.GONE : View.VISIBLE);
            mBottomLayout.setVisibility(View.VISIBLE);
            mNewCartContainer.setVisibility(View.VISIBLE);
        } else {
            mBottomLayout.setVisibility(View.GONE);
            mNewCartContainer.setVisibility(View.GONE);
        }
        //被动进入选择,并且是选择模式
        if (!choiceModeAndIntiativeTrigger && lessonSourceParams != null && lessonSourceParams.isChoiceMode()) {
            topBar.setRightFunctionText1(getString(R.string.ok), v -> {
                selectedTask.clear();
                List<TreeNode> selectedNodes = treeView.getSelectedNodes();
                for (TreeNode selectedNode : selectedNodes) {
                    Object value = selectedNode.getValue();
                    if (value instanceof SectionResListVo) {
                        SectionResListVo vo = (SectionResListVo) value;
                        selectedTask.add(vo);
                    }
                }
                if (selectedTask.size() <= 0) {
                    ToastUtil.showToast(this, getString(R.string.str_select_tips));
                } else {
                    // 学程馆选取资源使用的
                    for (int i = 0; i < selectedTask.size(); i++) {
                        SectionResListVo resListVo = selectedTask.get(i);
                        resListVo.setCourseId(courseId);
                        resListVo.setSourceType(sourceType);
                    }
                    EventBus.getDefault().post(new EventWrapper(selectedTask, EventConstant.COURSE_SELECT_RESOURCE_EVENT));
                    //数据回传
                    setResult(Activity.RESULT_OK, intent.putExtra(CourseSelectItemFragment.RESULT_LIST, selectedTask));
                    RefreshUtil.getInstance().clear();
                    if (activity != null) activity.finish();
                    finish();
                }
            });
        }
        refreshCartPoint();
        LQCourseHelper.getSxExamDetail(courseId, sectionId, this);
    }

    /**
     *
     * @param context
     * @param courseId
     * @param sectionId
     * @param mTeacherVisitor
     * @param status
     * @param libraryType
     * @param sourceType 0作业 1考试 2 测试 统计加的参数
     * @param lessonSourceParams
     */
    public static void start(Context context, String courseId, String sectionId, boolean mTeacherVisitor,
                             int status, int libraryType, int sourceType, LessonSourceParams lessonSourceParams) {
        if (context instanceof Activity) activity = (Activity) context;
        Intent intent = new Intent(context, ExamsAndTestsActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("sectionId", sectionId);
        intent.putExtra("lessonSourceParams", lessonSourceParams);
        intent.putExtra("status", status);
        intent.putExtra("libraryType", libraryType);
        intent.putExtra("sourceType", sourceType);
        intent.putExtra("mTeacherVisitor", mTeacherVisitor);
        context.startActivity(intent);
    }

    public static void start(Context context, int taskType, String courseId, String sectionId, boolean mTeacherVisitor,
                             int status, int libraryType,int sourceType,LessonSourceParams lessonSourceParams) {
        if (context instanceof Activity) activity = (Activity) context;
        Intent intent = new Intent(context, ExamsAndTestsActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("sectionId", sectionId);
        intent.putExtra("lessonSourceParams", lessonSourceParams);
        intent.putExtra("status", status);
        intent.putExtra("libraryType", libraryType);
        intent.putExtra("sourceType", sourceType);
        intent.putExtra("mTeacherVisitor", mTeacherVisitor);
        intent.putExtra("taskType", taskType);
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
        extrasVo = new ExamsAndTestExtrasVo(courseParams == null ? "" : courseParams.getSchoolId(), lessonSourceParams, lessonNeedFlag,
                status, isVideoCourse, mClassTeacher, false, lessonSourceParams.isChoiceMode(), libraryType);

        List<SxExamDetailVo.TaskListVO> taskList = examDetailVo.taskList;
        for (SxExamDetailVo.TaskListVO taskListVO : taskList) {
            if (!isInitiativeTrigger && isChoiceMode && !isShowType(taskType, taskListVO)) continue;
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
            if (isAddToCart && lessonSourceParams != null && lessonSourceParams.isChoiceMode())
                addToCart();
            else
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
            addToCart();
            updateView(false);
        } else if (id == R.id.new_cart_container) {
            handleSubjectSettingData(this, UserHelper.getUserId());
        }
    }

    private void addToCart() {
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
            if (count >= 0 && count < 6) {
                int count1 = chooseResourceSum();
                if (count + count1 > 6) {
                    int needCount = 6 - count;
                    UIUtil.showToastSafe(String.format(UIUtil.getString(R.string.label_work_cart_add_count_tip), needCount));
                    return;
                } else {
                    //子任务个数
                    List<String> taskNameLists = choosedChildResource();
                    if (EmptyUtil.isNotEmpty(taskNameLists) && taskNameLists != null) {
                        String nameStr = StringUtils.join(taskNameLists, "、");
                        UIUtil.showToastSafe(String.format(UIUtil.getString(R.string.label_work_cart_choose_count_tip), nameStr));
                        return;
                    }
                }
            } else if (count >= 6) {
                UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
                return;
            }
//            int count = TaskSliderHelper.onWorkCartListener.takeTaskCount();
//            if (count > 6) {
//                UIUtil.showToastSafe(R.string.label_work_cart_max_count_tip);
//                return;
//            }
        }
        Set<Map.Entry<Integer, List<SectionResListVo>>> entries = addToCartInDifferentTypes.entrySet();
        for (Map.Entry<Integer, List<SectionResListVo>> entry : entries) {
            List<SectionResListVo> choiceArray = entry.getValue();
            confirmResourceCart(choiceArray);
        }
//        if (isChoiceMode) treeView.deselectAll();
//        else updateView(false);
    }

    private void updateView(boolean isAddToCart) {
        if (!isChoiceMode) {
            llSelectAction.setVisibility(isAddToCart ? View.VISIBLE : View.GONE);
            mAddCartContainer.setVisibility(isAddToCart ? View.GONE : View.VISIBLE);
            if (extrasVo != null) extrasVo.setmChoiceMode(isAddToCart);
            treeView.notifychanged();
        }
        selectAll.setText(getString(R.string.select_all));
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

    /**
     * @return 选中了几条资源
     */
    private int chooseResourceSum() {
        // 获取指定Tab所有的选中的作业库资源
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
        Set<Map.Entry<Integer, List<SectionResListVo>>> entries = addToCartInDifferentTypes.entrySet();
        return entries.size();
    }

    /**
     * @return 选中的子任务的超过10的typeName
     */
    private  List<String> choosedChildResource() {
        // 获取指定Tab所有的选中的作业库资源
        List<TreeNode> selectedNodes =treeView.getSelectedNodes();
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
        List<String> taskNameLists = new ArrayList<>();
        for (Map.Entry<Integer, List<SectionResListVo>> entry : addToCartInDifferentTypes.entrySet()) {
            int taskType = entry.getKey();
            List<SectionResListVo> taskList = entry.getValue();
            if (taskList.size() > 10) {
                taskNameLists.add(mTypes[taskType - 1]);
            }
        }
        return taskNameLists;
    }

    private int confirmResourceCart(List<SectionResListVo> choiceArray) {
        // UIUtil.showToastSafe("确定所有作业库中的资源");
        // 获取指定Tab所有的选中的作业库资源
//        Log.e(TAG, "confirmResourceCart: " + choiceArray.size());
        if (EmptyUtil.isEmpty(choiceArray)) {
            UIUtil.showToastSafe(R.string.str_select_tips);
            return 0;
        }
        for (int i = 0; i < choiceArray.size(); i++) {
            SectionResListVo resListVo = choiceArray.get(i);
            resListVo.setCourseId(courseId);
            resListVo.setSourceType(sourceType);
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


    /**
     * 判断是否需要显示
     *
     * @param realTaskType 当前选取类型
     * @param vo           资源数据集合
     * @return boolean true 需要显示
     */
    private boolean isShowType(int realTaskType, SxExamDetailVo.TaskListVO vo) {
        int taskType = vo.taskType;
        if (realTaskType == CourseSelectItemFragment.KEY_RELL_COURSE) {
            // 选择复述课件
            if (taskType == 2
                    || taskType == 4
                    || taskType == 5) {
                // 听说作业 看课件 讲解课
                return true;
            }
        } else if (realTaskType == CourseSelectItemFragment.KEY_TASK_ORDER) {
            if (taskType == 3
                    || taskType == 4) {
                // 读写作业 看课件
                return true;
            }
        } else if (realTaskType == CourseSelectItemFragment.KEY_TEXT_BOOK) {
            // 视频课类型
            if (taskType == 1 || taskType == 6) {
                return true;
            }
        } else if (realTaskType == CourseSelectItemFragment.KEY_WATCH_COURSE) {
            // 看课本类型
            if (taskType == 1 || taskType == 4 || taskType == 2 || taskType == 5) {
                // 看课件 视频课
                // 新增选择讲解课 听说作业
                return true;
            }
        }

        return false;
    }
}
