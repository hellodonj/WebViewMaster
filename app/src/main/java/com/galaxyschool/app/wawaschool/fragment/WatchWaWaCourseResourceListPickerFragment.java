package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.MyAttendedSchoolListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.WatchWawaCourseResourceSplicingUtils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.MyAttendedSchooListFragment;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassResourceData;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.HideSortType;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.organcourse.online.CourseShopListActivity;
import com.lqwawa.intleducation.module.watchcourse.list.CourseResourceParams;
import com.lqwawa.intleducation.module.watchcourse.list.WatchCourseResourceListActivity;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.mooc.select.SchoolClassSelectActivity;
import com.lqwawa.mooc.select.SchoolClassSelectFragment;
import com.osastudio.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 看课件多类型资源选取列表页面
 */
public class WatchWaWaCourseResourceListPickerFragment extends AdapterFragment {

    public static final String TAG = WatchWaWaCourseResourceListPickerFragment.class.getSimpleName();
    static final int TAB_LOCAL_COURSE = 0;//本机课件
    static final int TAB_CLOUD_COURSE = 1; //个人资源库
    static final int TAB_SCHOOL_COURSE = 2; //校本资源库
    static final int TAB_LQ_PROGRAM = 3; //LQ学程
    static final int TAB_SCHOOL_PICTUREBOOK = 4;//精品资源库
    static final int TAB_LQCOURSE_SHOP = 5;//学程馆
    static final int TAB_CLASS_LESSON = 6;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_type_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            hideSoftKeyboard(getActivity());
        }
        initViews();
        if (!isOnlineClass && !TextUtils.isEmpty(classId)) {
            chooseClassLessonCourse(true);
        } else {
            loadGetStudyTaskResControl();
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadEntries();
        }
    }

    public void initViews() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        if (linearLayout != null) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.text_white));
        }
        if (getArguments() != null) {
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            isCheckTaskOrderRes = getArguments().getBoolean(ActivityUtils.EXTRA_CHOOSE_TASKORDER_DATA);
            selectMaxCount = getArguments().getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT);
            isSuperTask = getArguments().getBoolean(ActivityUtils.EXTRA_FROM_SUPER_TASK);
            superTaskType = getArguments().getInt(ActivityUtils.EXTRA_SUPER_TASK_TYPE);
            isOnlineClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            classId = getArguments().getString(ActivityUtils.EXTRA_CLASS_ID);
            schoolId = getArguments().getString(ActivityUtils.EXTRA_SCHOOL_ID);
            if (!TextUtils.isEmpty(schoolId)){
                if (TextUtils.equals(schoolId.toLowerCase(),vipSchoolId.toLowerCase())){
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
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getTitleView().setText(titleName);
            toolbarTopView.getBackView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        if (listView != null) {
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg_color)));
            listView.setDividerHeight(1);
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .media_type_list_item_model) {
                @Override
                public void loadData() {
                    loadEntries();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    HomeTypeEntry data = (HomeTypeEntry) getDataAdapter().getData().get(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
                    TextView name = (TextView) view.findViewById(R.id.name);
                    thumbnail.setImageResource(data.icon);
                    name.setText(data.typeName);
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }

                    HomeTypeEntry data = (HomeTypeEntry) holder.data;
                    if (data != null) {
                        enterEntries(data);
                    }
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }

    /**
     * 点击ListView的item进入具体的详情页面
     *
     * @param data
     */
    public void enterEntries(HomeTypeEntry data) {
        if (data.type == TAB_CLOUD_COURSE) {
            //个人资源库
            if (taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
                choosePersonalCourseResource();
            } else {
                choosePersonalResources();
            }
        } else if (data.type == TAB_SCHOOL_COURSE) {
            //校本资源库
            chooseSchoolResources(TAB_SCHOOL_COURSE);
        } else if (data.type == TAB_LOCAL_COURSE) {
            //本机课件
            chooseLocalResource();
        } else if (data.type == TAB_LQ_PROGRAM) {
            //关联学程
//            chooseLQProgramResources();
            chooseLqConnectCourse();
        } else if (data.type == TAB_SCHOOL_PICTUREBOOK) {
            //精品资源库
            chooseSchoolResources(TAB_SCHOOL_PICTUREBOOK);
        } else if (data.type == TAB_LQCOURSE_SHOP) {
            //学程馆
            if (isOnlineClass){
//                chooseSchoolResources(TAB_LQCOURSE_SHOP);
                chooseOnlineLqCourseShopRes();
            } else {
                enterLqCourseShopSpace();
            }
        } else if (data.type == TAB_CLASS_LESSON) {
            //班级学程
            chooseClassLessonCourse(false);
        }
    }

    private void chooseOnlineLqCourseShopRes(){
        int taskType = getTaskTypeOrSelectCount(true);
        int selectMaxCount = getTaskTypeOrSelectCount(false);
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
        } else if (taskType == StudyTaskType.TASK_ORDER){
            arrayList.add(ResType.RES_TYPE_STUDY_CARD);
        } else if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE ||
                taskType == StudyTaskType.WATCH_WAWA_COURSE){
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
            arrayList.add(ResType.RES_TYPE_IMG);
            arrayList.add(ResType.RES_TYPE_PPT);
            arrayList.add(ResType.RES_TYPE_PDF);
            arrayList.add(ResType.RES_TYPE_VOICE);
            arrayList.add(ResType.RES_TYPE_VIDEO);
            arrayList.add(ResType.RES_TYPE_DOC);
        }
        ShopResourceData resourceData = new ShopResourceData(taskType,selectMaxCount,arrayList, LQCourseCourseListActivity.RC_SelectCourseRes);
        CourseShopClassifyParams params = new CourseShopClassifyParams(schoolId,true,resourceData);
        CourseShopListActivity.show(getActivity(),
                titleName,
                HideSortType.TYPE_SORT_ONLINE_COURSE,
                params,null);
    }

    /**
     * 进入学程馆选取资源
     */
    private void enterLqCourseShopSpace(){
        int taskType = getTaskTypeOrSelectCount(true);
        int selectMaxCount = getTaskTypeOrSelectCount(false);
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
        } else if (taskType == StudyTaskType.TASK_ORDER){
            arrayList.add(ResType.RES_TYPE_STUDY_CARD);
        } else if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE ||
                taskType == StudyTaskType.WATCH_WAWA_COURSE){
            arrayList.add(ResType.RES_TYPE_COURSE_SPEAKER);
            arrayList.add(ResType.RES_TYPE_ONEPAGE);
            arrayList.add(ResType.RES_TYPE_IMG);
            arrayList.add(ResType.RES_TYPE_PPT);
            arrayList.add(ResType.RES_TYPE_PDF);
            arrayList.add(ResType.RES_TYPE_VOICE);
            arrayList.add(ResType.RES_TYPE_VIDEO);
            arrayList.add(ResType.RES_TYPE_DOC);
        }
        ShopResourceData resourceData = new ShopResourceData(taskType,selectMaxCount,arrayList, LQCourseCourseListActivity.RC_SelectCourseRes);
        // OrganCourseClassifyActivity.show(getActivity(),data.getSchoolId(),true,resourceData);
        CourseShopClassifyParams params = new CourseShopClassifyParams(schoolId,true,resourceData);
        CourseShopClassifyActivity.show(getActivity(),params);
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
            if (type == StudyTaskType.RETELL_WAWA_COURSE){
                ArrayList<Integer> selectType = new ArrayList<>();
                selectType.add(18);
                selectType.add(19);
                data = new ClassResourceData(type,count,selectType, LQCourseCourseListActivity
                        .RC_SelectCourseRes);
            } else {
                data = new ClassResourceData(type,count,new ArrayList<Integer>(),
                        LQCourseCourseListActivity.RC_SelectCourseRes);
            }
            data.setIsDirectToClassCourse(isDirectToClassCourse);
            ClassCourseActivity.show(getActivity(),classCourseParams,data);
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
        intent.setClass(getActivity(), MyAttendedSchoolListActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
        intent.putExtra(ActivityUtils.EXTRA_TASK_TYPE, taskType);
        intent.putExtra(ActivityUtils.EXTRA_CHOOSE_TASKORDER_DATA, isCheckTaskOrderRes);
        ArrayList<Integer> mediaTypeList = new ArrayList<>();
        //复述课件选取类型只要 lq 图片 pdf ppt
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                || (isSuperTask && superTaskType == StudyTaskType.RETELL_WAWA_COURSE)
                || (taskType == StudyTaskType.LISTEN_READ_AND_WRITE && !isCheckTaskOrderRes)) {
            mediaTypeList.add(MediaType.SCHOOL_COURSEWARE); //LQ课件
            mediaTypeList.add(MediaType.SCHOOL_PICTURE); //图片
            mediaTypeList.add(MediaType.SCHOOL_PDF); //PDF
            mediaTypeList.add(MediaType.SCHOOL_PPT); //PPT
            mediaTypeList.add(MediaType.SCHOOL_DOC); //DOC
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
        if (type == TAB_LQCOURSE_SHOP) {
            startActivityForResult(intent, LQCourseCourseListActivity.RC_SelectCourseRes);
        } else {
            startActivityForResult(intent, IntroductionForReadCourseFragment.REQUEST_CODE_PICKER_RESOURCES);
        }
    }

    private void loadEntries() {
        List<HomeTypeEntry> list = new ArrayList<>();
        HomeTypeEntry item = null;

//        if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
//            //本机课件
//            item=new HomeTypeEntry();
//            item.icon=R.drawable.local_study_course_icon;
//            item.typeName=R.string.local_course_task;
//            item.type=TAB_LOCAL_COURSE;
//            list.add(item);
//        }

        //个人资源库
//        item=new HomeTypeEntry();
//        item.icon=R.drawable.personal_cloud_course_icon;
//        item.typeName=R.string.personal_resource_library;
//        item.type=TAB_CLOUD_COURSE;
//        list.add(item);

        if (openSelectSchoolRes || !TextUtils.isEmpty(controlGetStudyTaskTypeString) &&
                controlGetStudyTaskTypeString.contains("1")) {
            //校本资源库
            item = new HomeTypeEntry();
            item.icon = R.drawable.school_course_library_icon;
            item.typeName = R.string.public_course;
            item.type = TAB_SCHOOL_COURSE;
            list.add(item);
        }

        if (openSelectSchoolRes || !TextUtils.isEmpty(controlGetStudyTaskTypeString) &&
                controlGetStudyTaskTypeString.contains("2")){
            //精品资源库
            item = new HomeTypeEntry();
            item.icon = R.drawable.icon_lq_program;
            item.typeName = R.string.choice_books;
            item.type = TAB_SCHOOL_PICTUREBOOK;
            list.add(item);
        }

        if (isOnlineClass) {
            if (!TextUtils.isEmpty(classId)) {
                // 关联学程
                item = new HomeTypeEntry();
                item.icon = R.drawable.icon_connect_course;
                item.typeName = R.string.label_space_school_relevance_course;
                item.type = TAB_LQ_PROGRAM;
                list.add(item);
            }
            
            // 线上学程馆
            item = new HomeTypeEntry();
            item.icon = R.drawable.ic_lqcourse_circle;
            item.typeName = R.string.common_course_library;
            item.type = TAB_LQCOURSE_SHOP;
            list.add(item);
        }



        if (isOnlineClass) {

        } else {
            //班级学程
            item = new HomeTypeEntry();
            item.icon = R.drawable.icon_class_lesson_task;
            item.typeName = R.string.str_class_lesson;
            item.type = TAB_CLASS_LESSON;
            list.add(item);
        }

        getCurrAdapterViewHelper().setData(list);
    }

    class HomeTypeEntry {
        int icon;
        int typeName;
        int type;
    }

    private void loadGetStudyTaskResControl() {
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
                loadViews();
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_STUDYTASK_CONTROL_BASE_URL, new HashMap<>(),
                listener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            //LQ学程返回的数据需要处理
            if (requestCode == LQCourseCourseListActivity.RC_SelectCourseRes) {
                if (data != null) {
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
                }
            }
        }
    }
}
