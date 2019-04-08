package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BookDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.BookStoreListFragment;
import com.galaxyschool.app.wawaschool.fragment.ChoiceBooksFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsListFragment;
import com.galaxyschool.app.wawaschool.fragment.IntroductionForReadCourseFragment;
import com.galaxyschool.app.wawaschool.fragment.WatchWaWaCourseResourceListPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseCourseListActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyActivity;
import com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect.CourseShopClassifyParams;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.organcourse.OrganCourseClassifyActivity;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AddedSchoolInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAttendedSchooListFragment extends ContactsListFragment {

    public static final String TAG = MyAttendedSchooListFragment.class.getSimpleName();
    public static final String FROM_TYPE = "from_type";
    public static final int FROM_HAOM_PAGE = 0;//来自学校空间
    public static final int NOT_FROM_HAOM_PAGE = 1;//
    public static final int MAX_SCHOOL_COLUMN_NUM = 5;
    private HashMap<String, SchoolInfo> schoolInfoHashMap = new HashMap<String, SchoolInfo>();
    private List<SchoolInfo> schoolInfos;
    public static final String SCHOOL_TYPE = "school_type";
    public static final int SCHOOL_1 = 1;//校本资源库
    public static final int SCHOOL_2 = 2;//精品资源库点进来
    private int fromType = SCHOOL_1;
    boolean isPick;
    boolean isPickSchoolResource;
    private int taskType;
    private boolean fromLQProgram;//来自LQ精品课程
    private boolean isFromChoiceLib;//来自精品资源库
    private boolean isLqcourseShop;//来自学程馆
    private int selectMaxCount;
    private BookStoreListFragment bookListFragment;
    private OnEnterSchoolSpaceListener listener;
    private boolean fromWorkLibTask;

    public interface OnEnterSchoolSpaceListener {
        void onEnterSchoolSpace(SchoolInfo schoolInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_type_list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        loadSchools();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initViews() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        if (linearLayout != null) {
            linearLayout.setBackgroundColor(getResources().getColor(R.color.text_white));
        }
        if (getArguments() != null) {
            fromType = getArguments().getInt(SCHOOL_TYPE, SCHOOL_1);
            isPick = getArguments().getBoolean(ActivityUtils.EXTRA_IS_PICK);
            taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            isPickSchoolResource = getArguments().getBoolean(ActivityUtils
                    .EXTRA_IS_PICK_SCHOOL_RESOURCE);
            fromLQProgram = getArguments().getBoolean(IntroductionForReadCourseFragment
                    .FROM_LQ_PROGRAM);
            isFromChoiceLib = getArguments().getBoolean(ActivityUtils.IS_FROM_CHOICE_LIB);
            isLqcourseShop = getArguments().getBoolean(ActivityUtils.EXTRA_LQCOURSE_SHOP);
            selectMaxCount = getArguments().getInt(ActivityUtils.EXTRA_SELECT_MAX_COUNT,1);
            fromWorkLibTask = getArguments().getBoolean(ActivityUtils.EXTRA_ASSIGN_WORK_LIB_TASK);
        }
        ToolbarTopView toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (toolbarTopView != null) {
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
//            if (fromType==SCHOOL_1){
//                toolbarTopView.getTitleView().setText(R.string.public_course);
//            }else {
//                toolbarTopView.getTitleView().setText(R.string.choice_books);
//            }
            if (isPick) {
                String headerTitle = null;
                if (taskType == StudyTaskType.RETELL_WAWA_COURSE) {
                    headerTitle = getString(R.string.n_create_task, getString(R.string.retell_course));
                } else if (taskType == StudyTaskType.WATCH_WAWA_COURSE) {
                    headerTitle = getString(R.string.n_create_task, getString(R.string.look_through_courseware));
                } else if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE || taskType ==
                        StudyTaskType.LISTEN_READ_AND_WRITE) {
                    headerTitle = getString(R.string.appoint_course_no_point);
                } else if (taskType == StudyTaskType.TASK_ORDER) {
                    headerTitle = getString(R.string.pls_add_work_task);
                }
                if (isPickSchoolResource || fromLQProgram) {
                    //选择机构
                    headerTitle = getString(R.string.select_institution);
                }
                toolbarTopView.getTitleView().setText(headerTitle);
            } else {
                toolbarTopView.getTitleView().setText(R.string.public_course);
            }
            toolbarTopView.getBackView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPickSchoolResource) {
                        finish();
                        return;
                    }
                    popStack();
                }
            });
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        if (listView != null) {
            //设置距离顶部栏10dp的灰色背景
            View view = findViewById(R.id.ten_background);
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            listView.setDivider(new ColorDrawable(getResources().getColor(R.color.main_bg_color)));
            listView.setDividerHeight(1);
            AdapterViewHelper helper = new AdapterViewHelper(getActivity(), listView, R.layout
                    .media_type_list_item_model) {
                @Override
                public void loadData() {
                    loadSchools();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    SchoolInfo data = (SchoolInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    ImageView imageView = (ImageView) view.findViewById(R.id.thumbnail);
                    if (imageView != null) {
                        getThumbnailManager().displayUserIconWithDefault(
                                AppSettings.getFileUrl(data.getSchoolLogo()), imageView,
                                R.drawable.default_group_icon);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                                imageView.getLayoutParams();
                        params.width = DensityUtils.dp2px(getActivity(), 60);
                        params.height = DensityUtils.dp2px(getActivity(), 60);
                        params.rightMargin = 0;
                        imageView.setLayoutParams(params);
                    }
                    TextView textView = (TextView) view.findViewById(R.id.name);
                    if (textView != null) {
                        textView.setText(data.getSchoolName());
                        textView.setGravity(Gravity.CENTER);
                    }
                    ImageView arrowRight = (ImageView) view.findViewById(R.id.arrow_right);
                    if (arrowRight != null) {
                        arrowRight.setVisibility(View.GONE);
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    SchoolInfo data = (SchoolInfo) holder.data;
                    if (isLqcourseShop){
                        //学程馆选择资源
                        enterLqCourseShopSpace(data);
                    } else if (fromType == SCHOOL_1) {
                        enterSchoolSpace(data);
                    } else {
                        if (isFromChoiceLib) {
                            enterSchoolSpace(data);
                            return;
                        }
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ChoiceBooksFragment fragment = new ChoiceBooksFragment();
                        Bundle args = getArguments();
                        args.putSerializable(SchoolInfo.class.getSimpleName(), data);
                        args.putInt(ChoiceBooksFragment.FROM_TYPE, ChoiceBooksFragment.NOT_FROM_HAOM_PAGE);
                        args.putBoolean(ActivityUtils.EXTRA_IS_PICK, isPick);
                        args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
                        fragment.setArguments(args);
                        ft.replace(R.id.activity_body, fragment, ChoiceBooksFragment.TAG);
                        ft.addToBackStack(null);
                        ft.commit();
                    }

                }
            };
            setCurrAdapterViewHelper(listView, helper);
        }

//        GridView gridView = (GridView) findViewById(R.id.gridview);
//        if (gridView != null) {
//            gridView.setHorizontalSpacing(0);
//            gridView.setVerticalSpacing(0);
//            final int itemSize = ScreenUtils.getScreenWidth(getActivity()) / MAX_SCHOOL_COLUMN_NUM;
//            gridView.setNumColumns(MAX_SCHOOL_COLUMN_NUM);
//            AdapterViewHelper listViewHelper = new AdapterViewHelper(getActivity(),
//                    gridView, R.layout.contacts_list_item) {
//                @Override
//                public void loadData() {
//                    loadSchools1();
//                }
//                @Override
//                public View getView(int position, View convertView, ViewGroup parent) {
//                    View view = super.getView(position, convertView, parent);
//                    SchoolInfo data =
//                            (SchoolInfo) getDataAdapter().getItem(position);
//                    if (data == null) {
//                        return view;
//                    }
//                    ViewHolder holder = (ViewHolder) view.getTag();
//                    if (holder == null) {
//                        holder = new ViewHolder();
//                    }
//                    holder.data = data;
//                    LinearLayout layout = (LinearLayout) view.findViewById(R.id
//                            .contacts_list_item_layout);
//                    if(layout != null) {
//                        layout.setOrientation(LinearLayout.VERTICAL);
//                        layout.setGravity(Gravity.CENTER);
//                        layout.setLayoutParams(new AbsListView.LayoutParams(itemSize, itemSize));
//                    }
//                    ImageView imageView = (ImageView) view.findViewById(R.id.contacts_item_icon);
//                    if (imageView != null) {
//                        getThumbnailManager().displayUserIconWithDefault(
//                                AppSettings.getFileUrl(data.getSchoolLogo()), imageView,
//                                R.drawable.default_group_icon);
//                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                                imageView.getLayoutParams();
//                        params.width = DensityUtils.dp2px(getActivity(), 90);
//                        params.height = DensityUtils.dp2px(getActivity(), 90);
//                        params.rightMargin = 0;
//                        imageView.setLayoutParams(params);
//                    }
//                    TextView textView = (TextView) view.findViewById(R.id.contacts_item_title);
//                    if (textView != null) {
//                        textView.setText(data.getSchoolName());
//                        textView.setGravity(Gravity.CENTER);
//                    }
//                    view.setTag(holder);
//                    return view;
//                }
//
//                @Override
//                public void onItemClick(AdapterView parent, View view, int position, long id) {
//                    ViewHolder holder = (ViewHolder) view.getTag();
//                    if (holder == null) {
//                        return;
//                    }
//                    SchoolInfo data = (SchoolInfo)holder.data;
//                    if(fromType==SCHOOL_2){
//                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                        ChoiceBooksFragment fragment = new ChoiceBooksFragment();
//                        Bundle args = getArguments();
//                        args.putSerializable(SchoolInfo.class.getSimpleName(),data);
//                        args.putInt(ChoiceBooksFragment.FROM_TYPE,ChoiceBooksFragment.NOT_FROM_HAOM_PAGE);
//                        args.putBoolean(ActivityUtils.EXTRA_IS_PICK,isPick);
//                        args.putInt(ActivityUtils.EXTRA_TASK_TYPE,taskType);
//                        fragment.setArguments(args);
//                        ft.add(R.id.activity_body, fragment, ChoiceBooksFragment.TAG);
//                        ft.addToBackStack(null);
//                        ft.commit();
//                    }else{
//                        enterSchoolSpace((SchoolInfo) holder.data);
//                    }
//                }
//            };
//
//            setCurrAdapterViewHelper(gridView, listViewHelper);
//        }
    }

    /**
     * 进入学程馆选取资源
     */
    private void enterLqCourseShopSpace(SchoolInfo data){
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
        CourseShopClassifyParams params = new CourseShopClassifyParams(data.getSchoolId(),true,resourceData);
        params.setInitiativeTrigger(fromWorkLibTask);
        CourseShopClassifyActivity.show(getActivity(),params);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateViews() {
        loadSchools();
    }

    private void updateViews(AddedSchoolInfoListResult result) {
        List<SchoolInfo> list = result.getModel().getData();
        if (list == null || list.size() <= 0) {
            TipsHelper.showToast(getActivity(), getString(R.string.no_data));
            return;
        }
        List<SchoolInfo> teacherSchoolInfo = new ArrayList<>();
        for (int i = 0, len = list.size(); i < len; i++) {
            SchoolInfo schoolInfo = list.get(i);
            if (schoolInfo.isTeacher()) {
//                if (schoolInfo.isOnlineSchool() && !isFromChoiceLib){
//
//                } else {
//                    teacherSchoolInfo.add(schoolInfo);
//                }
                if (schoolInfo.isOnlineSchool()){

                } else {
                    teacherSchoolInfo.add(schoolInfo);
                }
            }
        }
        //筛选出老师所以的机构
        if (teacherSchoolInfo.size() > 0) {
            getCurrAdapterViewHelper().setData(teacherSchoolInfo);
        }
    }


    private void loadSchools() {
        if (schoolInfoHashMap != null) {
            schoolInfoHashMap.clear();
        }
        Map<String, Object> params = new HashMap();
        if (getUserInfo() != null) {
            params.put("MemberId", getUserInfo().getMemberId());
        }
        //拉取已加入的机构
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_JOIN_SCHOOL_LIST_URL, params,
                new DefaultDataListener<AddedSchoolInfoListResult>(
                        AddedSchoolInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AddedSchoolInfoListResult result = getResult();
                        if (result == null || !result.isSuccess() || result.getModel() == null) {
                        } else {
                            updateViews(result);
                        }
                    }
                });
    }

    public void setOnEnterSchoolSpaceListener(OnEnterSchoolSpaceListener listener) {
        this.listener = listener;
    }

    /**
     * 进入校本资源的详情界面
     *
     * @param schoolInfo
     */
    private void enterSchoolSpace(SchoolInfo schoolInfo) {
        if (this.listener != null) {
            this.listener.onEnterSchoolSpace(schoolInfo);
        } else {
            if (schoolInfo == null) {
                return;
            }
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            bookListFragment = new BookStoreListFragment();
            Bundle args = getArguments();
            int taskType = getArguments().getInt(ActivityUtils.EXTRA_TASK_TYPE);
            args.putBoolean(ActivityUtils.EXTRA_IS_PICK, isPick);
            args.putInt(ActivityUtils.EXTRA_TASK_TYPE, taskType);
            args.putString(BookDetailActivity.SCHOOL_ID, schoolInfo.getSchoolId());
            args.putString(BookDetailActivity.ORIGIN_SCHOOL_ID, "");
            args.putBoolean(ActivityUtils.IS_FROM_CHOICE_LIB, isFromChoiceLib);
            bookListFragment.setArguments(args);
            ft.replace(R.id.activity_body, bookListFragment, BookStoreListFragment.TAG);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (bookListFragment != null && requestCode == NewOnlineStudyFiltrateActivity.SEARCH_REQUEST_CODE){
            //支持搜索的回调
            bookListFragment.onActivityResult(requestCode,resultCode,data);
        } else if (data != null){
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        }
    }
}
