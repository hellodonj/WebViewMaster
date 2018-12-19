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
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.MyAttendedSchooListFragment;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.watchcourse.list.CourseResourceParams;
import com.lqwawa.intleducation.module.watchcourse.list.WatchCourseResourceListActivity;
import com.lqwawa.mooc.select.SchoolClassSelectActivity;
import com.lqwawa.mooc.select.SchoolClassSelectFragment;
import java.util.ArrayList;
import java.util.List;

import static com.galaxyschool.app.wawaschool.fragment.IntroductionForReadCourseFragment.ASSOCIATE_TASK_ORDER;

public class CoursePickerFragment extends AdapterFragment {

    public static final String TAG = CourseMainFragment.class.getSimpleName();
    static final int TAB_LOCAL_COURSE = 0;
    static final int TAB_CLOUD_COURSE = 1;
    static final int TAB_SCHOOL_COURSE = 2;
    static final int TAB_SCHOOL_PICTUREBOOK = 3;
    static final int TAB_LQ_PROGRAM = 4; //LQ学程
    static final int TAB_LQCOURSE_SHOP = 5;
    static final int TAB_CLASS_LESSON = 6;
    WawaCourseFragment localCourseFragment;
    WawaCourseFragment myCloudCourseFragment;
    MyAttendedSchooListFragment schoolCourseFragment;
    MyAttendedSchooListFragment pictureBookFragment;
    //LQ精品课程
    private MyAttendedSchooListFragment lqProgramFragment;
    int taskType;
    private boolean associateTaskOrder;//关联任务单，true代表导读，false代表任务单。
    private boolean isOnlineClass;
    private String classId;
    private String schoolId;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        loadViews();
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadEntries();
        }
    }

    public void initViews() {
        if (getArguments() != null) {
            //关联任务单
            associateTaskOrder = getArguments().getBoolean(ASSOCIATE_TASK_ORDER);
            isOnlineClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            classId = getArguments().getString(ActivityUtils.EXTRA_CLASS_ID);
            schoolId = getArguments().getString(ActivityUtils.EXTRA_SCHOOL_ID);
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        if (linearLayout != null) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.text_white));
        }
        String titleName = null;
        taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
        if (taskType == StudyTaskType.WATCH_WAWA_COURSE) {
            titleName = getString(R.string.look_through_courseware);
        } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
            titleName = getString(R.string.retell_course);
        } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE || taskType == StudyTaskType.LISTEN_READ_AND_WRITE) {
            titleName = getString(R.string.appoint_course_no_point);
        } else if (taskType == StudyTaskType.TASK_ORDER) {
            titleName = getString(R.string.pls_add_work_task);
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getTitleView().setText(titleName);
            toolbarTopView.getBackView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popStack();
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
                    enterEntries(data);

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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putInt(ActivityUtils.EXTRA_COURSE_TYPE, data.type);
        if (data.type == TAB_LOCAL_COURSE) {
            //本机课件
            if (this.localCourseFragment == null) {
                this.localCourseFragment = new WawaCourseFragment();
            }
            args.putInt(WawaCourseFragment.RETELL_COURSE_TYPE, TAB_LOCAL_COURSE);
            localCourseFragment.setArguments(args);
            ft.replace(R.id.activity_body, localCourseFragment, WawaCourseFragment.TAG);
        } else if (data.type == TAB_CLOUD_COURSE) {
            //个人资源库（lq课件、任务单）
            if (taskType == StudyTaskType.TASK_ORDER) {
                args.putBoolean(TaskOrderFragment.IS_PICK, true);
                TaskOrderFragment fragment = new TaskOrderFragment();
                fragment.setArguments(args);
                ft.replace(R.id.activity_body, fragment, TaskOrderFragment.TAG);
            } else {
                if (this.myCloudCourseFragment == null) {
                    this.myCloudCourseFragment = new WawaCourseFragment();
                }
                args.putInt(WawaCourseFragment.RETELL_COURSE_TYPE, TAB_CLOUD_COURSE);
                myCloudCourseFragment.setArguments(args);
                ft.replace(R.id.activity_body, myCloudCourseFragment, WawaCourseFragment.TAG);
            }
        } else if (data.type == TAB_SCHOOL_COURSE) {
            //校本资源库
            if (this.schoolCourseFragment == null) {
                this.schoolCourseFragment = new MyAttendedSchooListFragment();
            }
            args = new Bundle();
            args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
            args.putInt(ActivityUtils.EXTRA_COURSE_TYPE, data.type);
            args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
            args.putInt(MyAttendedSchooListFragment.SCHOOL_TYPE, MyAttendedSchooListFragment.SCHOOL_1);
            args.putBoolean(ASSOCIATE_TASK_ORDER, getArguments().getBoolean(ASSOCIATE_TASK_ORDER, false));
            args.putBoolean(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
            ArrayList<Integer> mediaTypeList = new ArrayList<>();
            if (taskType == StudyTaskType.TASK_ORDER || associateTaskOrder){
                mediaTypeList.add(MediaType.SCHOOL_TASKORDER);
            } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                    || (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE)){
                mediaTypeList.add(MediaType.SCHOOL_COURSEWARE);
            }
            args.putIntegerArrayList(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES, mediaTypeList);
            schoolCourseFragment.setArguments(args);
            ft.replace(R.id.activity_body, schoolCourseFragment, MyAttendedSchooListFragment.TAG);
        } else if (data.type == TAB_SCHOOL_PICTUREBOOK || data.type == TAB_LQCOURSE_SHOP) {
            //精品资源库
            if (this.pictureBookFragment == null) {
                this.pictureBookFragment = new MyAttendedSchooListFragment();
            }
            args = new Bundle();
            args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
            args.putInt(ActivityUtils.EXTRA_COURSE_TYPE, data.type);
            args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
            args.putInt(MyAttendedSchooListFragment.SCHOOL_TYPE, MyAttendedSchooListFragment.SCHOOL_2);
            //加一个参数区分老板的精品资源库
            args.putBoolean(ActivityUtils.IS_FROM_CHOICE_LIB, true);
            if (data.type == TAB_LQCOURSE_SHOP){
                args.putBoolean(ActivityUtils.EXTRA_LQCOURSE_SHOP, true);
            }
            ArrayList<Integer> mediaTypeList = new ArrayList<>();
            if (taskType == StudyTaskType.TASK_ORDER || associateTaskOrder){
                mediaTypeList.add(MediaType.SCHOOL_TASKORDER);
            } else if (taskType == StudyTaskType.RETELL_WAWA_COURSE
                    || (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE)){
                mediaTypeList.add(MediaType.SCHOOL_COURSEWARE);
            }
            args.putIntegerArrayList(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES, mediaTypeList);
            pictureBookFragment.setArguments(args);
            ft.replace(R.id.activity_body, pictureBookFragment, MyAttendedSchooListFragment.TAG);
        } else if (data.type == TAB_LQ_PROGRAM) {
            //LQ精品学程
//            if (lqProgramFragment == null) {
//                lqProgramFragment = new MyAttendedSchooListFragment();
//            }
//            args = new Bundle();
//            args.putInt(ActivityUtils.EXTRA_COURSE_TYPE,data.type);
//            //从LQ精品学程点进去
//            args.putInt(MyAttendedSchooListFragment.SCHOOL_TYPE,
//                    MyAttendedSchooListFragment.SCHOOL_1);
//            args.putBoolean(ActivityUtils.EXTRA_IS_PICK, true);
//            args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
//            lqProgramFragment.setArguments(args);
//            lqProgramFragment.setOnEnterSchoolSpaceListener(new MyAttendedSchooListFragment.
//                    OnEnterSchoolSpaceListener() {
//                @Override
//                public void onEnterSchoolSpace(SchoolInfo schoolInfo) {
//                    //条目点击事件
//                    WatchWawaCourseResourceSplicingUtils.
//                            chooseLQProgramResources(getActivity(),
//                                    CoursePickerFragment.this,taskType,schoolInfo);
//                }
//            });
//            ft.replace(R.id.activity_body,lqProgramFragment,MyAttendedSchooListFragment.TAG);
            chooseLqConnectCourse();
            return;
        } else if (data.type == TAB_CLASS_LESSON){
            //班级学程
            chooseClassLessonCourse();
            return;
        }
        ft.addToBackStack(null);
        ft.commit();
    }


    private void chooseLqConnectCourse() {
        OnlineCourseHelper.requestOnlineCourseWithClassId(classId, new DataSource.Callback<String>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(R.string.tip_class_not_relevance_course);
            }

            @Override
            public void onDataLoaded(String courseId) {
                int type = taskType;
                if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                    type = StudyTaskType.RETELL_WAWA_COURSE;
                }
                loadLqCourseData(courseId,type);
            }
        });
    }

    private void loadLqCourseData(String courseId,int taskType){
        if (taskType == StudyTaskType.RETELL_WAWA_COURSE){
            ArrayList<Integer> selectType = new ArrayList<>();
            selectType.add(19);
            CourseResourceParams params = new CourseResourceParams(
                    getString(R.string.label_space_school_relevance_course),courseId,taskType,1);

            params.setFilterArray(selectType);
            params.setRequestCode(LQCourseCourseListActivity.RC_SelectCourseRes);
            WatchCourseResourceListActivity.show(getActivity(),params);
        } else {
            CourseResourceParams params = new CourseResourceParams(
                    getString(R.string.label_space_school_relevance_course),courseId,taskType,1
            );
            params.setRequestCode(LQCourseCourseListActivity.RC_SelectCourseRes);
            WatchCourseResourceListActivity.show(getActivity(),params);
        }
    }

    private void chooseClassLessonCourse(){
        int type = taskType;
        if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            type = StudyTaskType.RETELL_WAWA_COURSE;
        }
        Intent intent = new Intent(getActivity(), SchoolClassSelectActivity.class);
        Bundle args = new Bundle();
        args.putBoolean(SchoolClassSelectFragment.Constants.FROM_STUDYTASK_CHECK_DATA,true);
        args.putInt(SchoolClassSelectFragment.Constants.CHECK_STUDY_TASK_TYPE,type);
        args.putInt(SchoolClassSelectFragment.Constants.CHECK_STUDY_TASK_COUNT,1);
        intent.putExtras(args);
        startActivityForResult(intent, LQCourseCourseListActivity.RC_SelectCourseRes);
    }

    private void loadEntries() {
        List<HomeTypeEntry> list = new ArrayList<>();
        HomeTypeEntry item = new HomeTypeEntry();
//        if (taskType != StudyTaskType.TASK_ORDER) {
//            //本机课件
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

        //校本资源库
        item = new HomeTypeEntry();
        item.icon = R.drawable.school_course_library_icon;
        item.typeName = R.string.public_course;
        item.type = TAB_SCHOOL_COURSE;
        list.add(item);

//        //精品资源库
//        if (taskType != StudyTaskType.TASK_ORDER) {
//
//            item = new HomeTypeEntry();
//            item.icon = R.drawable.school_picture_library_icon;
//            item.typeName = R.string.choice_books;
//            item.type = TAB_SCHOOL_PICTUREBOOK;
//            list.add(item);
//        }

        //做任务单,导读不显示
        if (!associateTaskOrder && taskType == StudyTaskType.TASK_ORDER) {
            //LQ精品学程
//            item = new HomeTypeEntry();
//            item.icon = R.drawable.icon_lq_program;
//            item.typeName = R.string.lq_excellent_program;
//            item.type = TAB_LQ_PROGRAM;
//            list.add(item);
            //精品资源库
            item = new HomeTypeEntry();
            item.icon = R.drawable.icon_lq_program;
            item.typeName = R.string.choice_books;
            item.type = TAB_SCHOOL_PICTUREBOOK;
            list.add(item);
        }

        // LQ精品学程
        if (isOnlineClass && !TextUtils.isEmpty(classId)) {
            item = new HomeTypeEntry();
            item.icon = R.drawable.icon_connect_course;
            item.typeName = R.string.label_space_school_relevance_course;
            item.type = TAB_LQ_PROGRAM;
            list.add(item);
        }

        //学程馆
        item = new HomeTypeEntry();
        item.icon = R.drawable.icon_lqcourse_circle;
        item.typeName = R.string.common_course_shop;
        item.type = TAB_LQCOURSE_SHOP;
        list.add(item);

        //班级学程
        item = new  HomeTypeEntry();
        item.icon = R.drawable.icon_class_lesson_task;
        item.typeName = R.string.str_class_lesson;
        item.type = TAB_CLASS_LESSON;
        list.add(item);

        //清除数据
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().clearData();
        }
        getCurrAdapterViewHelper().setData(list);
    }

    class HomeTypeEntry {
        int icon;
        int typeName;
        int type;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LQCourseCourseListActivity.RC_SelectCourseRes) {
            if (data != null) {
                //退出
                ArrayList<SectionResListVo> selectedList = (ArrayList<SectionResListVo>)
                        data.getSerializableExtra(CourseSelectItemFragment.RESULT_LIST);
                if (selectedList != null && selectedList.size() > 0) {
                    //处理LQ学程选取的数据
                    SectionResListVo vo = selectedList.get(0);
                    if (vo != null) {
                        IntroductionForReadCourseFragment fragment =
                                (IntroductionForReadCourseFragment) getFragmentManager()
                                        .findFragmentByTag(IntroductionForReadCourseFragment.TAG);

                        int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
                        if ((taskType == StudyTaskType.TASK_ORDER
                                || taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE)
                                && !associateTaskOrder) {
                            CourseData courseData = new CourseData();
                            String resId = vo.getResId();
                            if (!TextUtils.isEmpty(resId) && resId.contains("-")) {
                                int i = resId.indexOf("-");
                                resId = resId.substring(0, i);
                            }
                            courseData.id = Integer.parseInt(resId);
                            courseData.nickname = vo.getName();
                            courseData.type = vo.getResType();
                            courseData.resourceurl = vo.getResourceUrl();
                            String thumbnailUrl = vo.getResourceUrl();
                            //截取zip包的缩略图
                            if (!TextUtils.isEmpty(thumbnailUrl)) {
                                String suffix = ".zip";
                                String headSuffix = "/head.jpg";
                                if (thumbnailUrl.contains(suffix)) {
                                    thumbnailUrl = thumbnailUrl
                                            .substring(0, thumbnailUrl.lastIndexOf(suffix));
                                    thumbnailUrl += headSuffix;
                                }
                            }
                            courseData.thumbnailurl = thumbnailUrl;
                            courseData.screentype = vo.getScreenType();
                            UploadParameter uploadParameter = UploadReourceHelper
                                    .getUploadParameter(getUserInfo(), null, courseData, null, 1);
                            if (uploadParameter != null) {
                                if (getArguments() != null) {
                                    uploadParameter.setTaskType(taskType);
                                    fragment.setData(uploadParameter);
                                }
                            }
                            if (vo.getResType() == StudyTaskType.TASK_ORDER) {
                                fragment.setWorkOrderId(vo.getResId());
                            }
                        }
                        if (associateTaskOrder) {
                            String thumbnailUrl = vo.getResourceUrl();
                            //截取zip包的缩略图
                            if (!TextUtils.isEmpty(thumbnailUrl)) {
                                String suffix = ".zip";
                                String headSuffix = "/head.jpg";
                                if (thumbnailUrl.contains(suffix)) {
                                    thumbnailUrl = thumbnailUrl
                                            .substring(0, thumbnailUrl.lastIndexOf(suffix));
                                    thumbnailUrl += headSuffix;
                                }
                            }
                            fragment.setWorkOrderId(vo.getResId());
                            fragment.setResourceUrl(vo.getResourceUrl());
                            fragment.setConnectThumbnail(thumbnailUrl);
                            fragment.updateAppointTaskOrderData();
                        }
                        int num = getFragmentManager().getBackStackEntryCount();
                        while (num > 1) {
                            popStack();
                            num = num - 1;
                        }
                    }
                }
            }
        }
    }
}
