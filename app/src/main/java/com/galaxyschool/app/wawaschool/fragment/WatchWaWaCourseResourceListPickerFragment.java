package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.BookStoreListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.galaxyschool.app.wawaschool.fragment.resource.MyAttendedSchooListFragment;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerItemDecoration;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.TreeView;
import com.galaxyschool.app.wawaschool.adapter.adapterbinder.factory.WatchCourseResourceBinderFactory;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.LQCourseHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.ImputAuthorizationCodeDialog;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassResourceData;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.organlibrary.OrganLibraryViewPresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;
import com.lqwawa.intleducation.module.organcourse.OrganLibraryUtils;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.filtrate.NewOrganCourseFiltrateActivity;
import com.lqwawa.intleducation.module.organcourse.online.CourseShopListActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.watchcourse.list.CourseResourceParams;
import com.lqwawa.intleducation.module.watchcourse.list.WatchCourseResourceListActivity;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.mooc.select.SchoolClassSelectActivity;
import com.lqwawa.mooc.select.SchoolClassSelectFragment;
import com.osastudio.apps.Config;
import com.osastudio.common.utils.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 看课件多类型资源选取列表页面
 */
public class WatchWaWaCourseResourceListPickerFragment extends AdapterFragment implements DataSource.Callback<ResponseVo<List<LQCourseConfigEntity>>>, OrganLibraryViewPresenter.View {

    public static final String TAG = WatchWaWaCourseResourceListPickerFragment.class.getSimpleName();
    static final int TAB_LOCAL_COURSE = 0;//本机课件
    static final int TAB_CLOUD_COURSE = 1; //个人资源库
    static final int TAB_SCHOOL_COURSE = 2; //校本资源库
    static final int TAB_LQ_PROGRAM = 3; //LQ学程
    static final int TAB_SCHOOL_PICTUREBOOK = 4;//精品资源库
    static final int TAB_LQCOURSE_SHOP = 5;//学程馆
    static final int TAB_CLASS_LESSON = 6;
    static final int TAB_COMMON_LIBRARY = 7;//图书馆
    private MyAttendedSchooListFragment lqProgramFragment;//LQ精品学程
    private int taskType;
    //听说 + 读写 选取读写单
    private boolean isCheckTaskOrderRes;
    private int selectMaxCount;
    private boolean isSuperTask;
    private int superTaskType;
    private boolean isOnlineClass;
    private String classId;
    private String schoolId;
    private String controlGetStudyTaskTypeString;
    private String vipSchoolId = "D8FE8280-FB40-4B61-9936-08819AA7E611";
    private boolean openSelectSchoolRes = false;
    private String titleName = null;
    private TreeView treeView;
    private TextView rightBtn;
    private TreeNode root;
    private FrameLayout container;
    private OrganLibraryViewPresenter organLibraryViewPresenter;
    private ImputAuthorizationCodeDialog imputAuthorizationCodeDialog;
    private List<LQCourseConfigEntity> filteredLabelEntities;
    private boolean isAuthorized;
    private boolean isExist;
    private CheckSchoolPermissionEntity mPermissionEntity;

    private static HashMap<String, String> authorizationErrorMapZh =
            new HashMap<>();
    private static HashMap<String, String> authorizationErrorMapEn =
            new HashMap<>();

    static {
        authorizationErrorMapZh.put("1001", "授权码错误，请重新输入");
        authorizationErrorMapZh.put("1002", "授权码已过期，请重新输入");
        authorizationErrorMapZh.put("1003", "授权码尚未生效，请重新输入");
        authorizationErrorMapZh.put("1004", "授权码已被使用，请重新输入");
        authorizationErrorMapEn.put("1001", "Incorrect authorization code, please re-enter");
        authorizationErrorMapEn.put("1002", "Authorization code expired，please re-enter");
        authorizationErrorMapEn.put("1003", "Invalid authorization code, please re-enter");
        authorizationErrorMapEn.put("1004", "Authorization code has been used, please re-enter");
    }

    private CheckSchoolPermissionEntity entity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pick_wawa_course_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            hideSoftKeyboard(getActivity());
        }
        initViews();
        if (isOnlineClass){
         //直接填充数据
            handleTreeView();
        } else {
            loadGetStudyTaskResControl();
        }
    }

    private void loadSixlLibraryLabelData() {
        LQCourseHelper.loadSixlLibraryLabelData(schoolId, this);
    }

    public void initViews() {
        Bundle args = getArguments();
        if (args != null) {
            taskType = args.getInt(ActivityUtils.EXTRA_TASK_TYPE);
            isCheckTaskOrderRes = args.getBoolean(ActivityUtils.EXTRA_CHOOSE_TASKORDER_DATA);
            selectMaxCount = args.getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT);
            isSuperTask = args.getBoolean(ActivityUtils.EXTRA_FROM_SUPER_TASK);
            superTaskType = args.getInt(ActivityUtils.EXTRA_SUPER_TASK_TYPE);
            isOnlineClass = args.getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            classId = args.getString(ActivityUtils.EXTRA_CLASS_ID);
            schoolId = args.getString(ActivityUtils.EXTRA_SCHOOL_ID);
            if (!TextUtils.isEmpty(schoolId)) {
                if (TextUtils.equals(schoolId.toLowerCase(), vipSchoolId.toLowerCase())) {
                    openSelectSchoolRes = true;
                }
            }
        }
        if (taskType == StudyTaskType.WATCH_WAWA_COURSE
                || taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
            //看课件
            titleName = getString(R.string.look_through_courseware);
        } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            titleName = getString(R.string.retell_course);
        } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            titleName = getString(R.string.appoint_course_no_point);
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            titleName = getString(R.string.pls_add_work_task);
        } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            boolean isCheckTaskOrderRes = false;
            if (getArguments() != null) {
                isCheckTaskOrderRes = getArguments().getBoolean(ActivityUtils
                        .EXTRA_CHOOSE_TASKORDER_DATA);
            }
            if (isCheckTaskOrderRes) {
                titleName = getString(R.string.pls_add_work_task);
            } else {
                titleName = getString(R.string.appoint_course_no_point);
            }
        }
        findViewById(R.id.contacts_header_left_btn).setOnClickListener(v -> {
            getActivity().finish();
        });
        ((TextView) findViewById(R.id.contacts_header_title)).setText(titleName);
        rightBtn = (TextView) findViewById(R.id.contacts_header_right_btn);
        rightBtn.setText(getString(R.string.label_request_authorization));
        if (isOnlineClass){
            rightBtn.setVisibility(View.GONE);
        } else {
            rightBtn.setVisibility(View.VISIBLE);
        }
        rightBtn.setOnClickListener(v -> {
            // 点击获取授权
            if (isAuthorized) {
                // 已经获取到授权
                UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.label_request_authorization_succeed);
                return;
            }
            // 获取授权
            requestAuthorizedPermission(isExist);
        });
        //六大馆
        container = (FrameLayout) findViewById(R.id.container);
        root = TreeNode.root();
        treeView = new TreeView(root, getActivity(), new WatchCourseResourceBinderFactory());
        treeView.setOnItemClilcedListener((position, treeNode, expanded, context) -> {
            LQCourseConfigEntity entity = (LQCourseConfigEntity) treeNode.getValue();
            if (entity == null){
                return;
            }
            if (treeNode.getLevel() == 0){
                //一级界面
                if (entity.isDirectAccessNextPage()) {
                    //展开
                    return;
                }
                onClickTreeViewItem(entity,true);
            } else {
                //二级界面
                onClickTreeViewItem(entity,false);
            }
        });
        initPresenter();
    }

    private void onClickTreeViewItem(LQCourseConfigEntity entity,boolean isGroupView){
        if (isGroupView){
            if (entity.getType() == OrganLibraryType.TYPE_SCHOOL_LIBRARY){
                enterPickTypeDetail(TAB_SCHOOL_COURSE);
            } else if (entity.getType() == OrganLibraryType.TPYE_CHOICE_LIBRARY){
                enterPickTypeDetail(TAB_SCHOOL_PICTUREBOOK);
            } else if (entity.getType() == OrganLibraryType.TYPE_CLASS_COURSE){
                enterPickTypeDetail(TAB_CLASS_LESSON);
            } else if (entity.getType() == OrganLibraryType.TYPE_CONNECT_COURSE){
                enterPickTypeDetail(TAB_LQ_PROGRAM);
            } else if (entity.getType() == OrganLibraryType.TYPE_ONLINE_COMMON_LIBRARY){
                enterPickTypeDetail(TAB_LQCOURSE_SHOP);
            } else if (entity.getType() == OrganLibraryType.TYPE_LIBRARY){
                //图书馆
                enterPickTypeDetail(TAB_COMMON_LIBRARY);
            } else if (entity.getType() == OrganLibraryType.TYPE_BRAIN_LIBRARY){

            }

        } else {

        }
    }

    private void initPresenter() {
        if (isOnlineClass){
            return;
        }
        if (organLibraryViewPresenter == null)
            organLibraryViewPresenter = new OrganLibraryViewPresenter(this);
    }

    public void enterPickTypeDetail(int pickType) {
        if (pickType == TAB_CLOUD_COURSE) {
            //个人资源库
            if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                choosePersonalCourseResource();
            } else {
                choosePersonalResources();
            }
        } else if (pickType == TAB_SCHOOL_COURSE) {
            //校本资源库
            chooseSchoolResources(TAB_SCHOOL_COURSE);
        } else if (pickType == TAB_LOCAL_COURSE) {
            //本机课件
            chooseLocalResource();
        } else if (pickType == TAB_LQ_PROGRAM) {
            //关联学程
//            chooseLQProgramResources();
            chooseLqConnectCourse();
        } else if (pickType == TAB_SCHOOL_PICTUREBOOK) {
            //精品资源库
            chooseSchoolResources(TAB_SCHOOL_PICTUREBOOK);
        } else if (pickType == TAB_LQCOURSE_SHOP) {
            //学程馆
            if (isOnlineClass) {
                chooseOnlineLqCourseShopRes();
            } else {
                enterLqCourseShopSpace();
            }
        } else if (pickType == TAB_CLASS_LESSON) {
            //班级学程
            chooseClassLessonCourse(false);
        } else if (pickType == TAB_COMMON_LIBRARY) {
            //图书馆
            chooseCommonLibraryResource();
        }
    }

    /**
     * 选择图书馆中的资源
     */
    private void chooseCommonLibraryResource() {
        int taskType = getTaskTypeOrSelectCount(true);
        int selectMaxCount = getTaskTypeOrSelectCount(false);
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(ResType.RES_TYPE_VIDEO);

        ShopResourceData resourceData = new ShopResourceData(taskType, selectMaxCount, arrayList, LQCourseCourseListActivity.RC_SelectCourseRes);

        LQCourseConfigEntity entity = new LQCourseConfigEntity();
        entity.setConfigValue(getString(R.string.str_q_dubbing));
        entity.setLibraryType(OrganLibraryType.TYPE_LIBRARY);
        entity.setId(OrganLibraryUtils.LIBRARY_QDUBBING_ID);
        entity.setLevel(OrganLibraryUtils.LIBRARY_QDUBBING_LEVEL);
        entity.setEntityOrganId(schoolId);

        SchoolHelper.requestSchoolInfo(UserHelper.getUserId(), schoolId,
                new DataSource.Callback<SchoolInfoEntity>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                    }

                    @Override
                    public void onDataLoaded(SchoolInfoEntity schoolInfoEntity) {
                        String roles = schoolInfoEntity.getRoles();
                        NewOrganCourseFiltrateActivity.show(
                                getActivity(),
                                entity, true, false, resourceData,
                                false, false, true,
                                roles, OrganLibraryType.TYPE_LIBRARY);
                    }
                });
    }

    private void chooseOnlineLqCourseShopRes() {
        int taskType = getTaskTypeOrSelectCount(true);
        int selectMaxCount = getTaskTypeOrSelectCount(false);
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            arrayList.add(ResType.RES_TYPE_STUDY_CARD);
        } else if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE ||
                taskType == StudyTaskType.WATCH_WAWA_COURSE) {
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
            arrayList.add(ResType.RES_TYPE_IMG);
            arrayList.add(ResType.RES_TYPE_PPT);
            arrayList.add(ResType.RES_TYPE_PDF);
            arrayList.add(ResType.RES_TYPE_VOICE);
            arrayList.add(ResType.RES_TYPE_VIDEO);
            arrayList.add(ResType.RES_TYPE_DOC);
        }
        ShopResourceData resourceData = new ShopResourceData(taskType, selectMaxCount, arrayList, LQCourseCourseListActivity.RC_SelectCourseRes);
        CourseShopClassifyParams params = new CourseShopClassifyParams(schoolId, true, resourceData);
        CourseShopListActivity.show(getActivity(),
                titleName,
                HideSortType.TYPE_SORT_ONLINE_COURSE,
                params, null);
    }

    /**
     * 进入学程馆选取资源
     */
    private void enterLqCourseShopSpace() {
        int taskType = getTaskTypeOrSelectCount(true);
        int selectMaxCount = getTaskTypeOrSelectCount(false);
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            arrayList.add(ResType.RES_TYPE_STUDY_CARD);
        } else if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE ||
                taskType == StudyTaskType.WATCH_WAWA_COURSE) {
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
            arrayList.add(ResType.RES_TYPE_IMG);
            arrayList.add(ResType.RES_TYPE_PPT);
            arrayList.add(ResType.RES_TYPE_PDF);
            arrayList.add(ResType.RES_TYPE_VOICE);
            arrayList.add(ResType.RES_TYPE_VIDEO);
            arrayList.add(ResType.RES_TYPE_DOC);
        }
        ShopResourceData resourceData = new ShopResourceData(taskType, selectMaxCount, arrayList, LQCourseCourseListActivity.RC_SelectCourseRes);
        // OrganCourseClassifyActivity.show(getActivity(),data.getSchoolId(),true,resourceData);
        CourseShopClassifyParams params = new CourseShopClassifyParams(schoolId, true, resourceData);
        CourseShopClassifyActivity.show(getActivity(), params);
    }

    private void chooseLqConnectCourse() {
        OnlineCourseHelper.requestOnlineCourseWithClassId(classId, new DataSource.Callback<String>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(R.string.tip_class_not_relevance_course);
            }

            @Override
            public void onDataLoaded(String courseId) {
                loadLqCourseData(courseId, getTaskTypeOrSelectCount(true), getTaskTypeOrSelectCount(false));
            }
        });
    }

    public int getTaskTypeOrSelectCount(boolean isTaskType) {
        int count = 1;
        int type = taskType;
        if (isSuperTask) {
            count = 5;
            type = superTaskType;
            if (superTaskType == StudyTaskType.WATCH_WAWA_COURSE) {
                type = StudyTaskType.NEW_WATACH_WAWA_COURSE;
            }
        } else if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            count = 9;
            if (isCheckTaskOrderRes) {
                type = StudyTaskType.TASK_ORDER;
            } else {
                type = StudyTaskType.RETELL_WAWA_COURSE;
            }
        } else if (taskType == StudyTaskType.WATCH_WAWA_COURSE || taskType ==
                StudyTaskType.NEW_WATACH_WAWA_COURSE) {
            count = 9;
            type = StudyTaskType.NEW_WATACH_WAWA_COURSE;
        } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            type = taskType;
            count = 1;
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            type = taskType;
            count = 1;
        }
        if (isTaskType) {
            return type;
        }
        return count;
    }

    private void loadLqCourseData(String courseId, int taskType, int count) {
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            ArrayList<Integer> selectType = new ArrayList<>();
            selectType.add(18);
            selectType.add(19);
            CourseResourceParams params = new CourseResourceParams(
                    getString(R.string.label_space_school_relevance_course), courseId, taskType, count);
            params.setFilterArray(selectType);
            params.setRequestCode(LQCourseCourseListActivity.RC_SelectCourseRes);
            WatchCourseResourceListActivity.show(getActivity(), params);
        } else {
            CourseResourceParams params = new CourseResourceParams(
                    getString(R.string.label_space_school_relevance_course), courseId, taskType, count);
            params.setRequestCode(LQCourseCourseListActivity.RC_SelectCourseRes);
            WatchCourseResourceListActivity.show(getActivity(), params);
        }
    }

    private void chooseClassLessonCourse(boolean isDirectToClassCourse) {
        int type = getTaskTypeOrSelectCount(true);
        int count = getTaskTypeOrSelectCount(false);
        if (isOnlineClass || TextUtils.isEmpty(classId)) {
            Intent intent = new Intent(getActivity(), SchoolClassSelectActivity.class);
            Bundle args = new Bundle();
            args.putBoolean(SchoolClassSelectFragment.Constants.FROM_STUDYTASK_CHECK_DATA, true);
            args.putInt(SchoolClassSelectFragment.Constants.CHECK_STUDY_TASK_TYPE, type);
            args.putInt(SchoolClassSelectFragment.Constants.CHECK_STUDY_TASK_COUNT, count);
            args.putString(ActivityUtils.EXTRA_SCHOOL_ID, schoolId);
            args.putBoolean(SchoolClassSelectFragment.Constants.FILTER_APPOINT_CLASS_INFO, !isOnlineClass);
            intent.putExtras(args);
            startActivityForResult(intent, LQCourseCourseListActivity.RC_SelectCourseRes);
        } else {
            ClassCourseParams classCourseParams = new ClassCourseParams(schoolId, classId);
            ClassResourceData data = null;
            if (type == StudyTaskType.RETELL_WAWA_COURSE) {
                ArrayList<Integer> selectType = new ArrayList<>();
                selectType.add(18);
                selectType.add(19);
                data = new ClassResourceData(type, count, selectType, LQCourseCourseListActivity
                        .RC_SelectCourseRes);
            } else {
                data = new ClassResourceData(type, count, new ArrayList<Integer>(),
                        LQCourseCourseListActivity.RC_SelectCourseRes);
            }
            data.setIsDirectToClassCourse(isDirectToClassCourse);
            ClassCourseActivity.show(getActivity(), classCourseParams, data);
        }
    }


    /**
     * 选取LQ精品学程
     */
    private void chooseLQProgramResources() {
        if (lqProgramFragment == null) {
            lqProgramFragment = new MyAttendedSchooListFragment();
        }
        Bundle args = new Bundle();
        //从LQ精品学程点进去
        args.putInt(MyAttendedSchooListFragment.SCHOOL_TYPE,
                MyAttendedSchooListFragment.SCHOOL_1);
        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
        args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
        //来自LQ精品学程
        args.putBoolean(IntroductionForReadCourseFragment.FROM_LQ_PROGRAM, true);
        lqProgramFragment.setArguments(args);
        lqProgramFragment.setOnEnterSchoolSpaceListener(new MyAttendedSchooListFragment.
                OnEnterSchoolSpaceListener() {
            @Override
            public void onEnterSchoolSpace(SchoolInfo schoolInfo) {
                //条目点击事件
                WatchWawaCourseResourceSplicingUtils.
                        chooseLQProgramResources(getActivity(),
                                WatchWaWaCourseResourceListPickerFragment.this, taskType, schoolInfo);
            }
        });
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, lqProgramFragment, MyAttendedSchooListFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 选取本机的课件
     */
    private void chooseLocalResource() {
        WawaCourseFragment wawaCourseFragment = new WawaCourseFragment();
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = new Bundle();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        bundle.putInt(WawaCourseFragment.RETELL_COURSE_TYPE, TAB_LOCAL_COURSE);
        bundle.putInt(ActivityUtils.EXTRA_COURSE_TYPE, TAB_LOCAL_COURSE);
        wawaCourseFragment.setArguments(bundle);
        ft.replace(R.id.activity_body, wawaCourseFragment, WawaCourseFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 选择个人资源库资源
     */
    private void choosePersonalResources() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ShellActivity.class);
        intent.putExtra("Window", "media_type_list");
        intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
        ArrayList<Integer> mediaTypeList = new ArrayList<>();
        //复述课件选取类型只要 lq 图片 pdf ppt
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE || (isSuperTask && superTaskType ==
                StudyTaskType.RETELL_WAWA_COURSE)) {
            mediaTypeList.add(MediaType.lQ_CLASS); //LQ课件
            mediaTypeList.add(MediaType.PICTURE); //图片
            mediaTypeList.add(MediaType.PDF); //PDF
            mediaTypeList.add(MediaType.PPT); //PPT
            mediaTypeList.add(MediaType.DOC); //DOC
            //不显示分页
            intent.putExtra(MediaListFragment.EXTRA_IS_FORM_ONLINE, true);
        } else {
            mediaTypeList.add(MediaType.lQ_CLASS); //LQ课件
            mediaTypeList.add(MediaType.PICTURE); //图片
            mediaTypeList.add(MediaType.AUDIO); //音频
            mediaTypeList.add(MediaType.VIDEO); //视频
            mediaTypeList.add(MediaType.PDF); //PDF
            mediaTypeList.add(MediaType.PPT); //PPT
            mediaTypeList.add(MediaType.DOC); //DOC
        }
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES, mediaTypeList);
        intent.putExtra(ActivityUtils.EXTRA_SELECT_MAX_COUNT, selectMaxCount);
        //看课件支持多类型
        intent.putExtra(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE, true);
        intent.putExtra(ActivityUtils.EXTRA_SELECT_MAX_COUNT, selectMaxCount);
        intent.putExtra(ActivityUtils.EXTRA_FROM_SUPER_TASK, isSuperTask);
        intent.putExtra(ActivityUtils.EXTRA_SUPER_TASK_TYPE, superTaskType);
        startActivityForResult(intent,
                IntroductionForReadCourseFragment.REQUEST_CODE_PICKER_RESOURCES);
    }

    /**
     * 仅仅选取个人资源库听说课的资源
     */
    private void choosePersonalCourseResource() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
        bundle.putBoolean(MediaListFragment.EXTRA_IS_PICK, true);
        bundle.putBoolean(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE, true);
        if (isCheckTaskOrderRes) {
            bundle.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, MediaType.TASK_ORDER);
            bundle.putString(MediaListFragment.EXTRA_MEDIA_NAME, getString(R.string.pls_add_work_task));
        } else {
            bundle.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, MediaType.ONE_PAGE);
            bundle.putString(MediaListFragment.EXTRA_MEDIA_NAME, getString(R.string.appoint_course_no_point));
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        MediaMainFragment fragment = new MediaMainFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.activity_body, fragment, MediaMainFragment.TAG);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * 选取校本资源库资源
     */
    private void chooseSchoolResources(int type) {
        Intent intent = new Intent();
//        intent.setClass(getActivity(), MyAttendedSchoolListActivity.class);
        //直接跳转到校本资源库的界面
        intent.setClass(getActivity(), BookStoreListActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
        intent.putExtra(ActivityUtils.EXTRA_TASK_TYPE, taskType);
        intent.putExtra(ActivityUtils.EXTRA_CHOOSE_TASKORDER_DATA, isCheckTaskOrderRes);
        ArrayList<Integer> mediaTypeList = new ArrayList<>();
        //复述课件选取类型只要 lq 图片 pdf ppt
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                || (isSuperTask && superTaskType == StudyTaskType.RETELL_WAWA_COURSE)
                || (taskType == StudyTaskType.LISTEN_READ_AND_WRITE && !isCheckTaskOrderRes)) {
            if (superTaskType == StudyTaskType.Q_DUBBING) {
                mediaTypeList.add(MediaType.SCHOOL_VIDEO); //视频
            } else {
                mediaTypeList.add(MediaType.SCHOOL_COURSEWARE); //LQ课件
                mediaTypeList.add(MediaType.SCHOOL_PICTURE); //图片
                mediaTypeList.add(MediaType.SCHOOL_PDF); //PDF
                mediaTypeList.add(MediaType.SCHOOL_PPT); //PPT
                mediaTypeList.add(MediaType.SCHOOL_DOC); //DOC
            }
        } else if (taskType == StudyTaskType.TASK_ORDER
                || (isSuperTask && superTaskType == StudyTaskType.TASK_ORDER)
                || (taskType == StudyTaskType.LISTEN_READ_AND_WRITE)) {
            mediaTypeList.add(MediaType.SCHOOL_TASKORDER);//任务单
            mediaTypeList.add(MediaType.SCHOOL_PICTURE); //图片
            mediaTypeList.add(MediaType.SCHOOL_PDF); //PDF
            mediaTypeList.add(MediaType.SCHOOL_PPT); //PPT
            mediaTypeList.add(MediaType.SCHOOL_DOC); //DOC
        } else {
            mediaTypeList.add(MediaType.SCHOOL_COURSEWARE); //LQ课件
            mediaTypeList.add(MediaType.SCHOOL_PICTURE); //图片
            mediaTypeList.add(MediaType.SCHOOL_AUDIO); //音频
            mediaTypeList.add(MediaType.SCHOOL_VIDEO); //视频
            mediaTypeList.add(MediaType.SCHOOL_PDF); //PDF
            mediaTypeList.add(MediaType.SCHOOL_PPT); //PPT
            mediaTypeList.add(MediaType.SCHOOL_DOC); //DOC
        }
        intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES, mediaTypeList);
        //看课件支持多类型
        intent.putExtra(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE, true);
        intent.putExtra(ActivityUtils.EXTRA_SELECT_MAX_COUNT, selectMaxCount);
        intent.putExtra(ActivityUtils.EXTRA_FROM_SUPER_TASK, isSuperTask);
        intent.putExtra(ActivityUtils.EXTRA_SUPER_TASK_TYPE, superTaskType);
        if (type == TAB_SCHOOL_PICTUREBOOK) {
            //如果是精品资源库的增加添加区分
            intent.putExtra(ActivityUtils.IS_FROM_CHOICE_LIB, true);
        } else if (type == TAB_LQCOURSE_SHOP) {
            intent.putExtra(ActivityUtils.EXTRA_LQCOURSE_SHOP, true);
            intent.putExtra(ActivityUtils.EXTRA_SELECT_MAX_COUNT, getTaskTypeOrSelectCount(false));
            intent.putExtra(ActivityUtils.EXTRA_TASK_TYPE, getTaskTypeOrSelectCount(true));
        }
        intent.putExtra(BookDetailActivity.SCHOOL_ID, schoolId);
        intent.putExtra(BookDetailActivity.ORIGIN_SCHOOL_ID, "");
        intent.putExtra(ActivityUtils.EXTRA_FROM_INTRO_STUDY_TASK, true);
        if (type == TAB_LQCOURSE_SHOP) {
            startActivityForResult(intent, LQCourseCourseListActivity.RC_SelectCourseRes);
        } else {
            startActivityForResult(intent, IntroductionForReadCourseFragment.REQUEST_CODE_PICKER_RESOURCES);
        }
    }

    @Override
    public void onDataNotAvailable(int strRes) {
        UIUtil.showToastSafe(strRes);
    }

    @Override
    public void onDataLoaded(ResponseVo<List<LQCourseConfigEntity>> responseVo) {
        List<LQCourseConfigEntity> libraryLabelEntities = responseVo.getData();
        if (libraryLabelEntities == null || libraryLabelEntities.isEmpty()) {
            return;
        }
        filteredLabelEntities = LQCourseConfigEntity.generateData(
                superTaskType > 0, superTaskType, libraryLabelEntities);
        //给子item权限
        if (filteredLabelEntities != null) {
            for (LQCourseConfigEntity filteredLabelEntity : filteredLabelEntities) {
                if (entity != null)
                    entity.assembleAuthorizedInClassify(filteredLabelEntity.getList());
            }
        }
        handleTreeView();
    }

    private void handleTreeView(){
        //增加local Entity
        putLocalEntity();
        for (LQCourseConfigEntity filteredLabelEntity : filteredLabelEntities) {
            TreeNode treeNode = new TreeNode(filteredLabelEntity);
            treeNode.setItemExpandedEnable(filteredLabelEntity.isDirectAccessNextPage());
            treeNode.setLevel(0);
            List<LQCourseConfigEntity> list = filteredLabelEntity.getList();
            if (list != null && list.size() > 0) {
                for (LQCourseConfigEntity libraryLabelEntity : list) {
                    libraryLabelEntity.setParentId(filteredLabelEntity.getType());
                    TreeNode treeNode1 = new TreeNode(libraryLabelEntity);
                    treeNode1.setItemExpandedEnable(libraryLabelEntity.isDirectAccessNextPage());
                    treeNode1.setLevel(1);
                    treeNode.addChild(treeNode1);
                }
            }
            root.addChild(treeNode);
        }
        View view = treeView.getView();
        treeView.addItemDecoration(new RecyclerItemDecoration(getActivity(), RecyclerItemDecoration.VERTICAL_LIST));
        container.addView(view);
    }

    private void putLocalEntity(){
        if (filteredLabelEntities == null){
            filteredLabelEntities = new ArrayList<>();
        }
        LQCourseConfigEntity configEntity = null;
        if (isOnlineClass){
            if (!TextUtils.isEmpty(classId)) {
                // 关联学程
                configEntity = new LQCourseConfigEntity();
                configEntity.setType(OrganLibraryType.TYPE_CONNECT_COURSE);
                configEntity.setName(getString(R.string.label_space_school_relevance_course));
                configEntity.setDirectAccessNextPage(false);
                configEntity.setDrawableId(R.drawable.icon_connect_course);
                filteredLabelEntities.add(configEntity);
            }
            // 线上学程馆
            configEntity = new LQCourseConfigEntity();
            configEntity.setType(OrganLibraryType.TYPE_ONLINE_COMMON_LIBRARY);
            configEntity.setName(getString(R.string.common_course_library));
            configEntity.setDirectAccessNextPage(false);
            configEntity.setDrawableId(R.drawable.ic_lqcourse_circle);
            filteredLabelEntities.add(configEntity);
        } else {
            boolean hasSchoolRes = false;
            if (openSelectSchoolRes || !TextUtils.isEmpty(controlGetStudyTaskTypeString) &&
                    controlGetStudyTaskTypeString.contains("1")) {
                hasSchoolRes = true;
                configEntity = new LQCourseConfigEntity();
                configEntity.setType(OrganLibraryType.TYPE_SCHOOL_LIBRARY);
                configEntity.setName(getString(R.string.public_course));
                configEntity.setDirectAccessNextPage(false);
                configEntity.setDrawableId(R.drawable.school_course_library_icon);
                filteredLabelEntities.add(0, configEntity);
            }

            if (openSelectSchoolRes || !TextUtils.isEmpty(controlGetStudyTaskTypeString) &&
                    controlGetStudyTaskTypeString.contains("2")) {
                //精品资源库
                configEntity = new LQCourseConfigEntity();
                configEntity.setType(OrganLibraryType.TPYE_CHOICE_LIBRARY);
                configEntity.setName(getString(R.string.choice_books));
                configEntity.setDirectAccessNextPage(false);
                configEntity.setDrawableId(R.drawable.icon_lq_program);
                filteredLabelEntities.add(hasSchoolRes ? 1 : 0, configEntity);
            }

            //班级学程
            configEntity = new LQCourseConfigEntity();
            configEntity.setType(OrganLibraryType.TYPE_CLASS_COURSE);
            configEntity.setName(getString(R.string.str_class_lesson));
            configEntity.setDirectAccessNextPage(false);
            configEntity.setDrawableId(R.drawable.icon_class_lesson_task);
            filteredLabelEntities.add(configEntity);
        }
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
        if (EmptyUtil.isNotEmpty(entity)) {
            this.entity = entity;
//            treeView.notifychanged();
            if (entity.isAuthorized()) {
                // 已经获取授权,并且没有失效
                isAuthorized = true;
                isExist = entity.isExist();
            } else {
                if (autoRequest) {
                    // 点击获取授权
                    requestAuthorizedPermission(entity.isExist());
                }
            }
        }
        loadSixlLibraryLabelData();
    }

    @Override
    public void updateRequestPermissionView(@NonNull CheckPermissionResponseVo<Void> responseVo) {
        if (EmptyUtil.isEmpty(responseVo)) return;
        if (responseVo.isSucceed()) {
            UIUtil.showToastSafe(com.lqwawa.intleducation.R.string.label_request_authorization_succeed);

            // 刷新权限信息
            String rightValue = responseVo.getRightValue();
            CheckSchoolPermissionEntity entity = new CheckSchoolPermissionEntity();
            entity.setRightValue(rightValue);
            entity.setAuthorized(true);
            entity.setExist(false);
            mPermissionEntity = entity;
            for (LQCourseConfigEntity filteredLabelEntity : filteredLabelEntities) {
                entity.assembleAuthorizedInClassify(filteredLabelEntity.getList());
            }
            treeView.notifychanged();

            isAuthorized = true;
            isExist = false;
            if (imputAuthorizationCodeDialog != null) {
                imputAuthorizationCodeDialog.setCommited(true);
                imputAuthorizationCodeDialog.dismiss();
            }
        } else {
            String language = Locale.getDefault().getLanguage();
            //提示授权码错误原因然后退出
            UIUtil.showToastSafe(language.equals("zh") ? authorizationErrorMapZh.get("" + responseVo.getCode()) : authorizationErrorMapEn.get("" + responseVo.getCode()));

            if (imputAuthorizationCodeDialog != null) {
                imputAuthorizationCodeDialog.clearPassword();
            }
        }
    }

    private void loadGetStudyTaskResControl() {
        //测试的时候放开 上线关闭
        Map<String, Object> params = new HashMap<>();
        if (!Config.UPLOAD_BUGLY_EXCEPTION) {
            params.put("Flag", 1);
        }
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(getActivity(), ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                try {
                    JSONObject jsonObject = JSONObject.parseObject(jsonString);
                    if (jsonObject != null) {
                        JSONObject modelObj = jsonObject.getJSONObject("Model");
                        if (modelObj != null) {
                            controlGetStudyTaskTypeString = modelObj.getString("data");
                            LogUtils.logd("TEST", "controlGetStudyTaskTypeString = " +
                                    controlGetStudyTaskTypeString);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (organLibraryViewPresenter != null) {
                    organLibraryViewPresenter.requestCheckSchoolPermission(schoolId, 0, false);
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_STUDYTASK_CONTROL_BASE_URL, params,
                listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            //LQ学程返回的数据需要处理
            if (requestCode == LQCourseCourseListActivity.RC_SelectCourseRes) {
                ArrayList<SectionResListVo> selectedList = (ArrayList<SectionResListVo>)
                        data.getSerializableExtra(CourseSelectItemFragment.RESULT_LIST);
                if (selectedList != null && selectedList.size() > 0) {
                    //处理LQ学程选取的数据
                    WatchWawaCourseResourceSplicingUtils.splicingLQProgramResources
                            (getActivity(), selectedList);
                } else {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            } else if (requestCode == IntroductionForReadCourseFragment.REQUEST_CODE_PICKER_RESOURCES) {
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK, data);
                    getActivity().finish();
                }
            }
        }
    }

    /**
     * 申请授权
     */
    private void requestAuthorizedPermission(boolean isExist) {
        String tipInfo = UIUtil.getString(com.lqwawa.intleducation.R.string.label_request_authorization_tip);
        if (isExist) {
            tipInfo = UIUtil.getString(com.lqwawa.intleducation.R.string.authorization_out_time_tip);
        }
        if (imputAuthorizationCodeDialog == null) {
            imputAuthorizationCodeDialog = new ImputAuthorizationCodeDialog(getActivity(), tipInfo,
                    new ImputAuthorizationCodeDialog.CommitCallBack() {
                        @Override
                        public void onCommit(String code) {
                            commitAuthorizationCode(code);
                        }

                        @Override
                        public void onCancel() {
                            if (EmptyUtil.isNotEmpty(imputAuthorizationCodeDialog)) {
                                imputAuthorizationCodeDialog.dismiss();
                            }
                        }
                    });
        }
        imputAuthorizationCodeDialog.setTipInfo(tipInfo);
        if (!imputAuthorizationCodeDialog.isShowing()) {
            imputAuthorizationCodeDialog.show();
        }
    }

    private void commitAuthorizationCode(String code) {
        if (organLibraryViewPresenter != null)
            organLibraryViewPresenter.commitAuthorizationCode(schoolId, code);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (organLibraryViewPresenter != null) organLibraryViewPresenter.onDestory();
    }
}
