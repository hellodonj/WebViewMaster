package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyAttendedSchoolListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.helper.LqIntroTaskHelper;
import com.galaxyschool.app.wawaschool.pojo.ClassInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassCourseParams;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.ClassResourceData;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.intleducation.module.watchcourse.list.CourseResourceParams;
import com.lqwawa.intleducation.module.watchcourse.list.WatchCourseResourceListActivity;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 看课件多类型资源选取列表页面
 */
public class HandleCheckResourceFragment extends AdapterFragment {

    public static final String TAG = HandleCheckResourceFragment.class.getSimpleName();
    private TextView taskCountTextV;
    private boolean isOnlineClass;
    private String classId;
    private String schoolId;
    private List<SubscribeClassInfo> dataList = new ArrayList<SubscribeClassInfo>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_handle_check_resource, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null) {
            hideSoftKeyboard(getActivity());
        }
        loadIntentData();
        initViews();
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTaskCount();
    }

    private void updateTaskCount(){
        if (taskCountTextV != null){
            int count = LqIntroTaskHelper.getInstance().getTaskCount();
            if (count > 0){
                taskCountTextV.setText(String.valueOf(count));
                taskCountTextV.setVisibility(View.VISIBLE);
            } else {
                taskCountTextV.setVisibility(View.GONE);
            }
        }
    }

    private void loadIntentData(){
        Bundle args = getArguments();
        if (args != null){
            schoolId = args.getString(ActivityUtils.EXTRA_SCHOOL_ID);
            classId = args.getString(ActivityUtils.EXTRA_CLASS_ID);
            isOnlineClass = args.getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
        }
    }

    private void initData() {
        if (!TextUtils.isEmpty(schoolId) && TextUtils.isEmpty(classId)) {
            loadClassListData();
        } else {
            loadViews();
        }
    }

    private void loadViews() {
        if (getCurrAdapterViewHelper().hasData()) {
            getCurrAdapterViewHelper().update();
        } else {
            loadEntries();
        }
    }

    private void loadEntries() {
        List<HomeTypeEntry> list = new ArrayList<>();
        HomeTypeEntry item = null;

        // 关联学程
        if (isOnlineClass && !TextUtils.isEmpty(classId)) {
            item = new HomeTypeEntry();
            item.icon = R.drawable.icon_connect_course;
            item.typeName = getString(R.string.label_space_school_relevance_course);
            item.type = 0;
            list.add(item);
        }

        //学程馆
        item = new HomeTypeEntry();
        item.icon = R.drawable.icon_lqcourse_circle;
        item.typeName = getString(R.string.common_course_shop);
        item.type = 1;
        list.add(item);

        if (dataList != null && dataList.size() > 0){
            for (SubscribeClassInfo classInfo : dataList){
                item = new HomeTypeEntry();
                item.icon = R.drawable.icon_class_lesson_task;
                String typeName = classInfo.getClassName() + getString(R.string.str_class_lesson);
                item.typeName = typeName;
                item.type = 2;
                item.classId = classInfo.getClassId();
                list.add(item);
            }
        } else {
            //班级学程
            item = new HomeTypeEntry();
            item.icon = R.drawable.icon_class_lesson_task;
            item.typeName = getString(R.string.str_class_lesson);
            item.type = 2;
            item.classId = this.classId;
            list.add(item);
        }
        getCurrAdapterViewHelper().setData(list);
    }

    public void initViews() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        if (linearLayout != null) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.text_white));
        }
        if (getArguments() != null) {
            isOnlineClass = getArguments().getBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS);
            classId = getArguments().getString(ActivityUtils.EXTRA_CLASS_ID);
            schoolId = getArguments().getString(ActivityUtils.EXTRA_SCHOOL_ID);
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getTitleView().setText(getString(R.string.assign_task_line));
            toolbarTopView.getBackView().setOnClickListener(v -> getActivity().finish());
        }
        taskCountTextV = (TextView) findViewById(R.id.tv_task_count);
        //作业库的按钮
        FrameLayout workFl = (FrameLayout) findViewById(R.id.fl_cart_container);
        if (workFl != null){
            workFl.setOnClickListener(v -> enterWorkLibDetailActivity());
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
                    enterEntries((HomeTypeEntry) holder.data);
                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }
    }

    /**
     * 加载班级信息
     */
    private void loadClassListData() {
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("IsHistory", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PICBOOKS_GET_CLASS_MAIL_URL, params, new RequestHelper.RequestDataResultListener<ClassInfoListResult>(getActivity(), ClassInfoListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ClassInfoListResult result = getResult();
                if (result == null || !result.isSuccess() || result.getModel() == null) {
                    return;
                }
                List<SubscribeClassInfo> list = result.getModel().getData();
                for (SubscribeClassInfo item : list) {
                    if (item.getType() != 1) {
                        if (item.isTeacherByRoles()) {
                            //筛选出老师身份的班级
                            dataList.add(item);
                        }
                    }
                }
                loadViews();
            }
        });
    }

    /**
     * 点击ListView的item进入具体的详情页面
     *
     * @param data
     */
    public void enterEntries(HomeTypeEntry data) {
        if (data.type == 0){
            //关联学程
            chooseLqConnectCourse();
        } else if (data.type == 1){
            //学程馆
            enterLqCourseShopSpace();
        } else if (data.type == 2){
            //班级学程
            chooseClassLessonCourse(data.classId);
        }
    }

    /**
     * 进入学程馆选取资源
     */
    private void enterLqCourseShopSpace() {
        if (isOnlineClass){
            chooseSchoolResources();
        } else {
            enterLqCourseShopDetail();
        }
    }

    /**
     * 选取校本资源库资源
     */
    private void chooseSchoolResources() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), MyAttendedSchoolListActivity.class);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
        intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
        intent.putExtra(ActivityUtils.EXTRA_LQCOURSE_SHOP, true);
        intent.putExtra(ActivityUtils.EXTRA_ASSIGN_WORK_LIB_TASK,true);
        startActivityForResult(intent, LQCourseCourseListActivity.RC_SelectCourseRes);
    }

    private void enterLqCourseShopDetail(){
        CourseShopClassifyParams params = new CourseShopClassifyParams(schoolId,true,new ShopResourceData());
        params.setInitiativeTrigger(true);
        CourseShopClassifyActivity.show(getActivity(),params);
    }

    /**
     * 选择班级学程
     */
    private void chooseClassLessonCourse(String classId) {
        ClassCourseParams classCourseParams = new ClassCourseParams(schoolId, classId);
        ClassResourceData data = new ClassResourceData();
        data.setInitiativeTrigger(true);
        ClassCourseActivity.show(getActivity(),classCourseParams,data);
    }

    /**
     * 点击关联学程
     */
    private void chooseLqConnectCourse() {
        OnlineCourseHelper.requestOnlineCourseWithClassId(classId, new DataSource.Callback<String>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                TipMsgHelper.ShowMsg(getActivity(), R.string.tip_class_not_relevance_course);
            }

            @Override
            public void onDataLoaded(String courseId) {
                //点击关联学程
                CourseResourceParams params = new CourseResourceParams(getString(R.string.label_space_school_relevance_course), courseId, 0, 0);
                params.setInitiativeTrigger(true);
                params.setRequestCode(LQCourseCourseListActivity.RC_SelectCourseRes);
                WatchCourseResourceListActivity.show(getActivity(), params);
            }
        });
    }

    private void enterWorkLibDetailActivity(){
        LqIntroTaskHelper.getInstance().enterIntroTaskDetailActivity(getActivity(),
                schoolId,classId);
    }


    class HomeTypeEntry {
        int icon;
        String typeName;
        int type;
        String classId;
    }

}
