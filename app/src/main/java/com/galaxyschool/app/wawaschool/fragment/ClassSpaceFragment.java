package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.galaxyschool.app.wawaschool.*;
import com.galaxyschool.app.wawaschool.Note.OnlineMediaPaperActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.NewResourceDeleteHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourceAdapterViewHelper;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;

import java.util.*;

public class ClassSpaceFragment extends ContactsListFragment {

    public static final String TAG = ClassSpaceFragment.class.getSimpleName();

    public interface Constants {
        public static final int REQUEST_CODE_CLASS_SPACE = 6102;

        public static final String EXTRA_CLASS_ID = "classId";
        public static final String EXTRA_CLASS_NAME = "className";
        public static final String EXTRA_CLASS_HEADTEACHER_ID = "headTeacherId";
        public static final String EXTRA_CLASS_HEADTEACHER_NAME = "headTeacherName";
        public static final String EXTRA_CLASS_STATUS = "classStatus";
        public static final String EXTRA_CLASS_SPACE_CHANGED = "spaceChanged";
        public static final String EXTRA_CLASS_NAME_CHANGED = "nameChanged";
        public static final String EXTRA_CLASS_HEADTEACHER_CHANGED = "headteacherChanged";
        public static final String EXTRA_CLASS_STATUS_CHANGED = "statusChanged";

        public static final int CLASS_STATUS_HISTORY =
                ClassContactsDetailsFragment.Constants.CLASS_STATUS_HISTORY;
        public static final int CLASS_STATUS_PRESENT =
                ClassContactsDetailsFragment.Constants.CLASS_STATUS_PRESENT;
    }

    private String classId;
    private String className;
    private int classStatus;
    private String classHeadTeacherId;
    private String classHeadTeacherName;
    private SubscribeClassInfo classInfo;
    private NewResourceInfoListResult resourceListResult;
    private String itemId = null;//标识当前选中的条目id
    public static boolean hasContentChanged;//监听内容是否改变
    private static boolean hasDeletedResource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_space, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        refreshData();
    }

    public static void setHasContentChanged(boolean hasContentChanged) {
        ClassSpaceFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }

    public static void setHasDeletedResource(boolean hasDeletedResource) {
        ClassSpaceFragment.hasDeletedResource = hasDeletedResource;
    }

    public static boolean hasDeletedResource() {
        return hasDeletedResource;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData(){
        if (classInfo == null) {
            loadClassInfo();
        }
        loadClassMessageStatistics();

        getPageHelper().clear();
        loadResourceList();
    }

    private void init() {
        Bundle args = getArguments();
        if (args.containsKey(Constants.EXTRA_CLASS_ID)) {
            classId = args.getString(Constants.EXTRA_CLASS_ID);
        }
        if (args.containsKey(SubscribeClassInfo.class.getSimpleName())) {
            classInfo = (SubscribeClassInfo) args.getSerializable(
                    SubscribeClassInfo.class.getSimpleName());
            classId = classInfo.getClassId();
            className = classInfo.getClassName();
            classStatus = classInfo.getIsHistory();
        }

        initViews();
    }

    private void initViews() {
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            if (classInfo != null) {
                textView.setText(classInfo.getGradeName() != null ?
                        classInfo.getGradeName() + classInfo.getClassName() :
                        classInfo.getClassName());
            }
        }
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            textView.setText(R.string.class_info);
            textView.setTextColor(getResources().getColor(R.color.text_green));
            textView.setVisibility(classInfo != null ? View.VISIBLE : View.INVISIBLE);
            textView.setOnClickListener(this);
        }

        ImageView imageView = null;
        View view = findViewById(R.id.course_item_layout);
        if (view != null) {
            imageView = (ImageView) view.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setImageResource(R.drawable.pub_course_ico);
            }
            textView = (TextView) view.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setText(R.string.courses);
            }
            view.setOnClickListener(this);
            view.setVisibility(View.GONE);
        }
        view = findViewById(R.id.homework_item_layout);
        if (view != null) {
            imageView = (ImageView) view.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setImageResource(R.drawable.pub_homework_ico);
            }
            textView = (TextView) view.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setText(R.string.learning_tasks);
            }
            view.setOnClickListener(this);
        }
        view = findViewById(R.id.notice_item_layout);
        if (view != null) {
            imageView = (ImageView) view.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setImageResource(R.drawable.pub_notice_ico);
            }
            textView = (TextView) view.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setText(R.string.notices);
            }
            view.setOnClickListener(this);
        }
        view = findViewById(R.id.comment_item_layout);
        if (view != null) {
            imageView = (ImageView) view.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setImageResource(R.drawable.pub_comment_ico);
            }
            textView = (TextView) view.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setText(R.string.shows);
            }
            view.setOnClickListener(this);
        }
        view = findViewById(R.id.lecture_item_layout);
        if (view != null) {
            imageView = (ImageView) view.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setImageResource(R.drawable.pub_eclassrom_ico);
            }
            textView = (TextView) view.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setText(R.string.lectures);
            }
            view.setOnClickListener(this);
        }

        PullToRefreshView pullToRefreshView = (PullToRefreshView)
                findViewById(R.id.contacts_pull_to_refresh);
        setPullToRefreshView(pullToRefreshView);

        GridView listView = (GridView) findViewById(R.id.resource_list_view);
        if (listView != null) {
            AdapterViewHelper adapterViewHelper = new NewResourceAdapterViewHelper(
                    getActivity(), listView) {
                @Override
                public void loadData() {
                    if (classInfo == null) {
                        loadClassInfo();
                    }
                    loadClassMessageStatistics();
                    loadResourceList();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    TextView textView = (TextView) view.findViewById(R.id.resource_type);
                    if (textView != null) {
                        textView.setText(NewResourceInfo.getClassResourceTypeString(getContext(), data.getType()));
                        textView.setVisibility(View.VISIBLE);
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.resource_indicator);
                    if (imageView != null) {
                        imageView.setVisibility(data.isRead() ? View.GONE : View.VISIBLE);
                    }
                    ImageView imageViewDelete = (ImageView) view.findViewById(R.id.resource_delete);
                    String tip="";
                    if (data.getType() == NewResourceInfo.TYPE_CLASS_NOTICE) {
                        tip=getString(R.string.delete_note);
                    } else if (data.getType() == NewResourceInfo.TYPE_CLASS_SHOW) {
                        tip=getString(R.string.delete_show);
                    } else if (data.getType() == NewResourceInfo.TYPE_CLASS_COURSE) {
                        tip=getString(R.string.delete_course);
                    } else if (data.getType() == NewResourceInfo.TYPE_CLASS_HOMEWORK) {
                        tip=getString(R.string.delete_homework);
                    }
                    NewResourceDeleteHelper helper = new NewResourceDeleteHelper(getActivity(),
                            getCurrAdapterViewHelper(), NewResourceDeleteHelper
                            .SHOW_CLASS_HOMEWORK, data, imageViewDelete,data.getType());
                    helper.initImageViewEvent(getMemeberId(), tip);
                    if(classInfo != null) {
                        helper.initImageViewEvent(tip, classInfo.isHeadMaster(), data.getType());
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
                    if(data != null) {
                        //记录当前点击的条目Id
                        itemId = data.getId();
                        markResourceAsRead(data);
                        CourseInfo courseInfo = data.getCourseInfo();
                        if(data.getType() == NewResourceInfo.TYPE_CLASS_HOMEWORK) {
                            courseInfo.setIsHomework(true);
                        } else {
                            courseInfo.setIsHomework(false);
                        }
                        int resType = data.getResourceType();
                        if(resType == ResType.RES_TYPE_NOTE) {
                            ActivityUtils.openOnlineNote(getActivity(),  data
                                    .getCourseInfo(), false, false);
                        } else if(resType == ResType.RES_TYPE_COURSE_SPEAKER
                            || resType == ResType.RES_TYPE_COURSE) {
                            ActivityUtils.playOnlineCourse(getActivity(), data.getCourseInfo(),
                                    false, null);
                        }
                    }
                }
            };
            setCurrAdapterViewHelper(listView, adapterViewHelper);
        }
    }

    private void loadResourceList() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ClassId", classId);
//        params.put("KeyWord", keyword);
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        DefaultPullToRefreshDataListener listener =
                new DefaultPullToRefreshDataListener<NewResourceInfoListResult>(
                        NewResourceInfoListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()
                        || getResult().getModel() == null) {
                    return;
                }
                updateResourceListView(getResult());
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CLASS_MESSAGE_LIST_URL, params, listener);
    }

    private void updateResourceListView(NewResourceInfoListResult result) {
        if (getPageHelper().isFetchingPageIndex(result.getModel().getPager())) {
            List<NewResourceInfo> list = result.getModel().getData();
            if (list == null || list.size() <= 0) {
                if (getPageHelper().isFetchingFirstPage()) {
                    getCurrAdapterViewHelper().clearData();
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_data));
                } else {
                    TipsHelper.showToast(getActivity(),
                            getString(R.string.no_more_data));
                }
                return;
            }

            if (getPageHelper().isFetchingFirstPage()) {
                getCurrAdapterViewHelper().clearData();
            }
            getPageHelper().updateByPagerArgs(result.getModel().getPager());
            getPageHelper().setCurrPageIndex(
                    getPageHelper().getFetchingPageIndex());
            if (getCurrAdapterViewHelper().hasData()) {
                int position = getCurrAdapterViewHelper().getData().size();
                if (position > 0) {
                    position--;
                }
                getCurrAdapterViewHelper().getData().addAll(list);
                getCurrAdapterView().setSelection(position);
                resourceListResult.getModel().setData(getCurrAdapterViewHelper().getData());
            } else {
                getCurrAdapterViewHelper().setData(list);
                resourceListResult = result;
            }
        }
    }

    private void markResourceAsRead(NewResourceInfo data) {
        if (getUserInfo() == null || data.isRead()){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", data.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        getActivity(), DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        NewResourceInfo data = (NewResourceInfo) getTarget();
                        data.setIsRead(true);
                        //内容改变：数据阅读状态改变
                        setHasContentChanged(true);
                        getCurrAdapterViewHelper().update();
                    }
                };
        listener.setTarget(data);
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.MARK_MY_RESOURCE_AS_READ_URL, params, listener);
    }

    private void loadClassInfo() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ClassId", classId);
        DefaultDataListener listener =
                new DefaultDataListener<SubscribeClassInfoResult>(
                        SubscribeClassInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                SubscribeClassInfoResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateClassInfo(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.CONTACTS_CLASS_INFO_URL, params, listener);
    }

    private void updateClassInfo(SubscribeClassInfoResult result) {
        if (classInfo != null) {
            return;
        }
        classInfo = result.getModel().getData();
        TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
        if (textView != null) {
            if (classInfo != null) {
                textView.setText(classInfo.getGradeName() != null ?
                        classInfo.getGradeName() + classInfo.getClassName() :
                        classInfo.getClassName());
            }
        }
        textView = ((TextView) findViewById(R.id.contacts_header_right_btn));
        if (textView != null) {
            if (classInfo != null) {
                textView.setVisibility(View.VISIBLE);
            }
        }
        className = classInfo.getClassName();
        classStatus = classInfo.getIsHistory();
    }

    private void loadClassMessageStatistics() {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ClassId", classId);
        DefaultDataListener listener =
                new DefaultDataListener<ClassMessageStatisticsListResult>(
                        ClassMessageStatisticsListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if(getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ClassMessageStatisticsListResult result = getResult();
                if (result == null || !result.isSuccess()
                        || result.getModel() == null) {
                    return;
                }
                updateClassMessageStatistics(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_CLASS_MESSAGE_STATISTICS_LIST_URL, params, listener);
    }

    private void updateClassMessageStatistics(ClassMessageStatisticsListResult result) {
        List<ClassMessageStatistics> list = result.getModel().getData();
        if (list == null || list.size() <= 0) {
            return;
        }

        TextView textView = null;
        View view = null;
        for (ClassMessageStatistics msg : list) {
            switch (msg.getTypeCode()) {
            case ClassMessageStatistics.CLASS_MESSAGE_TYPE_COURSE:
                view = findViewById(R.id.course_item_layout);
                break;
//            case ClassMessageStatistics.CLASS_MESSAGE_TYPE_HOMEWORK:
            case ClassMessageStatistics.CLASS_MESSAGE_TYPE_STUDY_TASK:
                view = findViewById(R.id.homework_item_layout);
                break;
            case ClassMessageStatistics.CLASS_MESSAGE_TYPE_NOTICE:
                view = findViewById(R.id.notice_item_layout);
                break;
            case ClassMessageStatistics.CLASS_MESSAGE_TYPE_SHOW:
                view = findViewById(R.id.comment_item_layout);
                break;
            case ClassMessageStatistics.CLASS_MESSAGE_TYPE_LECTURE:
                view = findViewById(R.id.lecture_item_layout);
                break;
            }
            if (view != null) {
                textView = (TextView) view.findViewById(R.id.item_tips);
                if (textView != null) {
                    if (msg.getUnReadNumber() > 99) {
                        textView.setText("99+");
                    } else {
                        textView.setText(String.valueOf(msg.getUnReadNumber()));
                    }
                    textView.setVisibility(msg.getUnReadNumber() > 0 ?
                            View.VISIBLE : View.GONE);
                }
            }
            view = null;
        }
    }

    @Override
    public boolean onBackPressed() {
        super.onBackPressed();
        notifyChanges();
        return true;
    }

    @Override
    public void finish() {
        getActivity().setResult(getResultCode(), getResultData());
        super.finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_right_btn) {
            enterGroupMembers();
        } else if (v.getId() == R.id.course_item_layout) {
            enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_COURSE);
        } else if (v.getId() == R.id.homework_item_layout) {
            enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_HOMEWORK);
        } else if (v.getId() == R.id.notice_item_layout) {
            enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_NOTICE);
        } else if (v.getId() == R.id.comment_item_layout) {
            enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_SHOW);
        } else if (v.getId() == R.id.lecture_item_layout) {
            enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_LECTURE);
        } else {
            super.onClick(v);
        }
    }

    private void enterClassResourceByChannel(int channelType) {
        if (classInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(ClassResourceListActivity.EXTRA_CLASS_ID, classInfo.getClassId());
        args.putString(ClassResourceListActivity.EXTRA_SCHOOL_ID, classInfo.getSchoolId());
        args.putInt(ClassResourceListActivity.EXTRA_CHANNEL_TYPE, channelType);
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putInt(ClassResourceListActivity.EXTRA_ROLE_TYPE, classInfo.getRoleType());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HEAD_MASTER,classInfo.isHeadMaster());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HISTORY, classInfo.isHistory());
        Intent intent = new Intent(getActivity(), ClassResourceListActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent,ClassResourceListBaseFragment
                .REQUEST_CODE_CHANGE_NOTE_CONTENT);
    }

    private void enterGroupMembers() {
        if (classInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, classInfo.getType());
        args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, classInfo.getSchoolName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_GRADE_ID, classInfo.getGradeId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_GRADE_NAME, classInfo.getGradeName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classInfo.getGroupId());
        if (classInfo.isClass()) {
            args.putInt(ClassContactsDetailsFragment.Constants.EXTRA_CLASS_STATUS,
                    classInfo.getIsHistory());
        }
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        intent.putExtras(args);
        if (classInfo.isClass()) {
            startActivityForResult(intent,
                    ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS);
        } else {
            startActivity(intent);
        }
    }

    private void notifyChanges() {
        boolean classNameChanged = classInfo != null
                && !this.className.equals(classInfo.getClassName());
        boolean classHeadTeacherChanged = classHeadTeacherId != null;
        boolean classStatusChanged = classInfo != null
                && this.classStatus != classInfo.getIsHistory();

        boolean changed = false;
        if (classNameChanged || classHeadTeacherChanged || classStatusChanged) {
            changed = true;
        }
        if (changed) {
            Bundle data = new Bundle();
            data.putString(Constants.EXTRA_CLASS_ID, this.classId);
            data.putBoolean(Constants.EXTRA_CLASS_SPACE_CHANGED, changed);
            if (classNameChanged) {
                data.putBoolean(Constants.EXTRA_CLASS_NAME_CHANGED,
                        classNameChanged);
                data.putString(Constants.EXTRA_CLASS_NAME, classInfo.getClassName());
            }
            if (classHeadTeacherChanged) {
                data.putBoolean(Constants.EXTRA_CLASS_HEADTEACHER_CHANGED,
                        classHeadTeacherChanged);
                data.putString(Constants.EXTRA_CLASS_HEADTEACHER_ID, classHeadTeacherId);
                data.putString(Constants.EXTRA_CLASS_HEADTEACHER_NAME, classHeadTeacherName);
            }
            if (classStatusChanged) {
                data.putBoolean(Constants.EXTRA_CLASS_STATUS_CHANGED,
                        classStatusChanged);
                data.putInt(Constants.EXTRA_CLASS_STATUS, classInfo.getIsHistory());
            }
            Intent intent = new Intent();
            intent.putExtras(data);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode ==
                    ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS) {
//                boolean classDetailsChanged = data.getBooleanExtra(
//                        ClassContactsDetailsFragment.Constants.EXTRA_CLASS_DETAILS_CHANGED, false);
//                if (!classDetailsChanged) {
//                    return;
//                }
//                String classId = data.getStringExtra(
//                        ClassContactsDetailsFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
//                if (this.classInfo == null || TextUtils.isEmpty(classId)
//                        || !this.classInfo.getClassId().equals(classId)) {
//                    return;
//                }
//                boolean classHeadTeacherChanged = data.getBooleanExtra(
//                        ClassContactsDetailsFragment.Constants.
//                                EXTRA_CLASS_HEADTEACHER_CHANGED, false);
//                boolean classStatusChanged = data.getBooleanExtra(
//                        ClassContactsDetailsFragment.Constants.EXTRA_CLASS_STATUS_CHANGED, false);
//                if (classStatusChanged) {
//                    this.classInfo.setIsHistory(data.getIntExtra(
//                            ClassContactsDetailsFragment.Constants.EXTRA_CLASS_STATUS,
//                            ClassContactsDetailsFragment.Constants.CLASS_STATUS_PRESENT));
//                }
//                if (classHeadTeacherChanged) {
//                    classHeadTeacherId = data.getStringExtra(
//                            ClassContactsDetailsFragment.Constants.EXTRA_CLASS_HEADTEACHER_ID);
//                    classHeadTeacherName = data.getStringExtra(
//                            ClassContactsDetailsFragment.Constants.EXTRA_CLASS_HEADTEACHER_NAME);
//                    if (getUserInfo().getMemberId().equals(classHeadTeacherId)) {
//                        this.classInfo.setHeadMaster(true);
//                    } else {
//                        if (this.classInfo.isHeadMaster()) {
//                            this.classInfo.setHeadMaster(false);
//                        }
//                    }
//                }
//                boolean classNameChanged = data.getBooleanExtra(
//                        ClassContactsDetailsFragment.Constants.EXTRA_CLASS_NAME_CHANGED, false);
//                if (classNameChanged) {
//                    this.classInfo.setClassName(
//                            data.getStringExtra(ClassContactsDetailsFragment.
//                                    Constants.EXTRA_CONTACTS_CLASS_NAME));
//                    TextView textView = ((TextView) findViewById(R.id.contacts_header_title));
//                    if (textView != null) {
//                        textView.setText(classInfo.getGradeName() != null ?
//                                classInfo.getGradeName() + classInfo.getClassName() :
//                                classInfo.getClassName());
//                    }
//                }
                if (ClassContactsDetailsFragment.hasClassContentChanged()){
                    ClassContactsDetailsFragment.setHasClassContentChanged(false);
                    setHasContentChanged(true);
                }
            }
        }

        if (data == null){
            //从上个帖子列表页面返回
            if (requestCode == ClassResourceListBaseFragment.REQUEST_CODE_CHANGE_NOTE_CONTENT){
                //返回的时候，需要刷新一下页面
                refreshData();
                if (ClassResourceListBaseFragment.isHasReadMessage()){
                    //阅读了消息
                    ClassResourceListBaseFragment.setHasReadMessage(false);
                    //本页面标志位
                    setHasContentChanged(true);
                }

                if (ClassResourceListBaseFragment.hasDeletedResource()){
                    ClassResourceListBaseFragment.setHasDeletedResource(false);
                    //本页面刷新标识
                    setHasContentChanged(true);
                }
                //创建帖子
                if (ClassResourceListBaseFragment.hasCreatedResource()){
                    ClassResourceListBaseFragment.setHasCreatedResource(false);
                    //本页面刷新标识
                    setHasContentChanged(true);
                }
            }else //手动打开帖子，刷新帖子。
                if (requestCode == CampusPatrolPickerFragment.EDIT_NOTE_DETAILS_REQUEST_CODE){

                    //帖子打开后返回列表，需要手动更新阅读人数（手动累加）
                    updateReaderNumber(itemId);

                    //帖子内容改变需要刷新
                    if (OnlineMediaPaperActivity.hasContentChanged()){
                        OnlineMediaPaperActivity.setHasContentChanged(false);
                        //刷新帖子
                        refreshData();
                    }
                }
        }
    }

    /**
     * 更新阅读人数
     * @param itemId
     */
    private void updateReaderNumber(String itemId) {
        if (TextUtils.isEmpty(itemId)){
            return;
        }
        AdapterViewHelper helper = getCurrAdapterViewHelper();
        if (helper != null && helper.hasData()){
            List<NewResourceInfo> infoList = helper.getData();
            if (infoList != null && infoList.size() > 0){
                for (NewResourceInfo info : infoList){
                    if (info != null){
                        String id = info.getId();
                        if (!TextUtils.isEmpty(id) && id.equals(itemId)){
                            //找到刚才点击的那个条目(id是唯一的，position不靠谱)，增加阅读数。
                            info.setReadNumber(info.getReadNumber() + 1);
                            break;
                        }
                    }
                }
            }
            //更新一下布局
            helper.update();
        }
    }
}
