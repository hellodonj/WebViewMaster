package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.ActClassroomActivity;
import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.ContactsClassManagementActivity;
import com.galaxyschool.app.wawaschool.QrcodeProcessActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.ImageLoader;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ClassMessageStatistics;
import com.galaxyschool.app.wawaschool.pojo.ClassMessageStatisticsListResult;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassQrCodeInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassQrCodeInfoResult;
import com.galaxyschool.app.wawaschool.pojo.QrcodeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.QrcodeSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfoResult;
import com.galaxyschool.app.wawaschool.pojo.TabEntityPOJO;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.R;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDetailsFragment extends ContactsListFragment
        implements View.OnClickListener {

    public static final String TAG = ClassDetailsFragment.class.getSimpleName();
    public static final int COLUMN_NUM = 4;
    private HashMap<String,EntryInfo> entryInfoHashMap=new HashMap<String, EntryInfo>();
    private boolean isOnlineSchool;

    public interface Constants {
        String GROUP_ID = "group_id";
        String SCHOOL_ID = "school_id";
        String CLASS_ID = "class_id";
        String CONTACT_ID = "contact_id";
        String SCHOOL_NAME = "school_name";
        String IS_JOIN_CLASS = "is_join_class";
        String FROM_TYPE = "from_type";
        String CLASS_STATE = "class_state";
        String EXTRA_CONTACTS_HAS_INSPECT_AUTH = "has_inspect_auth";
        int FROM_TYPE_TEACHER_CONTACT = 1;
        int FROM_TYPE_CLASS_HEAD_PIC = 2;
        int ENTRY_TYPE_STUDY_TASK = 1;
        int ENTRY_TYPE_LECTURES = 2;
        int ENTRY_TYPE_CLASS_INFO = 3;
        int ENTRY_TYPE_NOTICE = 4;
        int ENTRY_TYPE_SHOW = 5;
        int TAB_ENTITY_TYPE_AIR_CLASSROOM=6;
        int TAB_ENTITY_TYPE_ACT_CLASSROOM=7;
        int ENTRY_TYPE_RELEVANCE_COURSE=8;
        int ENTRY_TYPE_CLASS_LESSON = 9;
    }

    private String groupId = "";
    private String schoolId = "";
    private String classId = "";
    private String contactId = "";
    private SubscribeClassInfo classInfo;
    private ImageView qrCodeView;
    private String qrCodeImageUrl;
    private String qrCodeImagePath;
    private boolean hasJoinedClass = false;
    private int fromType = 0;
    private TextView attendClassBtn;
    private MyGridView gridView;
    private String headPicUrl;
    private String className;
    private String schoolName;
    private String qrCode;
    private int classState= ContactsClassManagementActivity.CLASS_STATUS_PRESENT;
    public static final int REQUEST_CODE_CLASS_DETAILS = 908;
    private static boolean hasContentChanged;
    private SchoolInfo schoolInfo;
    private boolean hasInspectAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_details, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntent();
        initView();
        refreshData();
    }

    private void initView() {
        gridView = (MyGridView) findViewById(R.id.class_grid_view);
        attendClassBtn = (TextView) findViewById(R.id.attend_btn);
        attendClassBtn.setOnClickListener(this);
        AdapterViewHelper adapterViewHelper = null;
        if (gridView != null) {
            gridView.setNumColumns(4);
            adapterViewHelper = new MyAdapterViewHelper(
                    getActivity(), gridView, R.layout.item_gridview_join);
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
        loadEntries();
    }

    private void getIntent() {
        if (getArguments() != null) {
            isOnlineSchool = getArguments().getBoolean(ClassContactsDetailsFragment.Constants.IS_ONLINE_SCHOOL);
            groupId = getArguments().getString(Constants.GROUP_ID);
            schoolId = getArguments().getString(Constants.SCHOOL_ID);
            classId = getArguments().getString(Constants.CLASS_ID);
            fromType = getArguments().getInt(Constants.FROM_TYPE);
            hasJoinedClass = getArguments().getBoolean(Constants.IS_JOIN_CLASS);
            contactId = getArguments().getString(Constants.CONTACT_ID);
            schoolName = getArguments().getString(Constants.SCHOOL_NAME);
            classState = getArguments().getInt(Constants.CLASS_STATE);
            schoolInfo = (SchoolInfo) getArguments().getSerializable(ActivityUtils.EXTRA_SCHOOL_INFO);
            hasInspectAuth = getArguments().getBoolean(Constants.EXTRA_CONTACTS_HAS_INSPECT_AUTH);
        }
    }

    private void loadQrCodeDetails() {
        Map<String, Object> params = new HashMap();
        params.put("Id", contactId);
        DefaultListener listener =
                new DefaultListener<ContactsClassQrCodeInfoResult>(
                        ContactsClassQrCodeInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ContactsClassQrCodeInfoResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        Object data = result.getModel();
                        if (data != null) {
                            ContactsClassQrCodeInfo obj = (ContactsClassQrCodeInfo) data;
                            headPicUrl = obj.getHeadPicUrl();
                            className = obj.getClassMailName();
                            qrCode = obj.getQRCode();
                            updateView();
                        }
                    }
                };
        listener.setShowLoading(true);
        postRequest(ServerUrl.CONTACTS_CLASS_QRCODE_URL, params, listener);
    }

    private void enterSchoolContact() {
        Bundle args = new Bundle();
        args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, 1);
        args.putString(ContactsActivity.EXTRA_CONTACTS_ID, contactId);
        args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, className);
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, schoolId);
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, schoolName);
        args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, groupId);
        args.putBoolean(ContactsActivity.EXTRA_CONTACTS_HAS_INSPECT_AUTH, hasInspectAuth);
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        intent.putExtras(args);
        startActivity(intent);
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
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        SubscribeClassInfoResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        classInfo = result.getModel().getData();
                        if (classInfo != null) {
                            headPicUrl = classInfo.getHeadPicUrl();
                            className = classInfo.getClassName();
                            schoolName = classInfo.getSchoolName();
                            qrCode = classInfo.getClassQRCode();
                            hasJoinedClass = classInfo.isInClass();
                            classState=classInfo.getIsHistory();

                            String userId = UserHelper.getUserId();
                            SchoolHelper.requestSchoolInfo(userId, classInfo.getSchoolId(), new DataSource.Callback<SchoolInfoEntity>() {
                                @Override
                                public void onDataNotAvailable(int strRes) {

                                }

                                @Override
                                public void onDataLoaded(SchoolInfoEntity entity) {
                                    if(entity.isOnlineSchool()){
                                        EntryInfo item = new EntryInfo();
                                        item.type = Constants.ENTRY_TYPE_RELEVANCE_COURSE;
                                        item.title = R.string.label_space_school_relevance_course;
                                        item.icon = R.drawable.ic_class_relevance_course;

                                        List<EntryInfo> data = getCurrAdapterViewHelper().getData();
                                        data.add(item);
                                        entryInfoHashMap.put(item.type+"",item);

                                        EntryInfo info = data.get(1);
                                        info.title = R.string.str_online_class_message;
                                        info.icon = R.drawable.icon_online_class_detail;
                                        getCurrAdapterViewHelper().setData(data);

                                        if (schoolInfo == null){
                                            createSchoolInfoObject();
                                        }
                                        schoolInfo.setIsOnlineSchool(entity.isOnlineSchool());
                                    }
                                }
                            });
                            createSchoolInfoObject();
                            updateView();
                            loadClassMessageStatistics();
                        }
                    }
                };
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.CONTACTS_CLASS_INFO_URL, params, listener);
    }

    private void createSchoolInfoObject(){
        if (schoolInfo == null){
            schoolInfo = new SchoolInfo();
            schoolInfo.setSchoolId(classInfo.getSchoolId());
            schoolInfo.setSchoolName(classInfo.getSchoolName());
        }
    }

    private void loadClassMessageStatistics() {
        if(classInfo==null){
            return;
        }
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ClassId", classInfo.getClassId());
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
        for (ClassMessageStatistics msg : list) {
            switch (msg.getTypeCode()) {
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_STUDY_TASK:
                    EntryInfo entryInfo=  entryInfoHashMap.
                            get(Constants.ENTRY_TYPE_STUDY_TASK+"");
                    entryInfo.count=msg.getUnReadNumber();
                    break;
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_NOTICE:
                    entryInfo=  entryInfoHashMap.
                            get(Constants.ENTRY_TYPE_NOTICE+"");
                    entryInfo.count=msg.getUnReadNumber();
                    break;
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_SHOW:
                    entryInfo=  entryInfoHashMap.
                            get(Constants.ENTRY_TYPE_SHOW+"");
                    entryInfo.count=msg.getUnReadNumber();
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_LECTURE:
                    entryInfo=  entryInfoHashMap.
                            get(Constants.ENTRY_TYPE_LECTURES+"");
                    entryInfo.count=msg.getUnReadNumber();
                    break;
            }
        }
        getCurrAdapterViewHelper().update();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void refreshData(){
        if (fromType == Constants.FROM_TYPE_CLASS_HEAD_PIC) {
            loadClassInfo();
        } else {
            loadQrCodeDetails();
            loadSchoolInfo();
        }
    }
    @Override
    public void finish() {
        super.finish();
        getActivity().finish();
    }

    private void initTitle() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            if (fromType == Constants.FROM_TYPE_CLASS_HEAD_PIC) {
                textView.setText(getString(R.string.class_infos));
            } else {
                textView.setText(getString(R.string.contacts_infos));
            }
        }
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }
        imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setOnClickListener(this);
            imageView.setImageResource(R.drawable.icon_more_green);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateView() {
        initTitle();
        ImageView imageView = (ImageView) findViewById(R.id.contacts_user_icon);
        if (imageView != null) {
            getThumbnailManager().displayUserIconWithDefault(
                    AppSettings.getFileUrl(headPicUrl),
                    imageView, R.drawable.default_class_icon);
        }
        TextView textView = (TextView) findViewById(R.id.contacts_user_name);
        if (textView != null) {
            textView.setText(className);
        }
        textView = (TextView) findViewById(R.id.contacts_user_description);
        if (textView != null) {
            textView.setText(schoolName);
        }
        imageView = (ImageView) findViewById(R.id.contacts_qrcode_image);
        if (imageView != null) {
            qrCodeImageUrl = AppSettings.getFileUrl(qrCode);
            qrCodeView = imageView;
            loadQrCodeImage();
        }

        if (fromType == Constants.FROM_TYPE_CLASS_HEAD_PIC) {
            if (hasJoinedClass) {
                attendClassBtn.setVisibility(View.GONE);
                if(classState== ContactsClassManagementActivity.CLASS_STATUS_PRESENT){
                    gridView.setVisibility(View.VISIBLE);
                }else{
                    gridView.setVisibility(View.GONE);
                }
            } else {
                attendClassBtn.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.GONE);
                attendClassBtn.setText(getString(R.string.join_class));
            }
        } else {
            gridView.setVisibility(View.GONE);
            attendClassBtn.setVisibility(View.VISIBLE);
            if (hasJoinedClass) {
                attendClassBtn.setText(getString(R.string.contacts_detail));
            } else {
                attendClassBtn.setText(getString(R.string.join_school));
            }
        }
    }

    private void loadQrCodeImage() {
        if (TextUtils.isEmpty(qrCodeImageUrl)) {
            return;
        }
        qrCodeImagePath = ImageLoader.getCacheImagePath(qrCodeImageUrl);
        File file = new File(qrCodeImagePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(qrCodeImagePath);
            if (bitmap != null) {
                qrCodeView.setImageBitmap(bitmap);
                return;
            }
            file.delete();
        }

        Netroid.downloadFile(getActivity(), qrCodeImageUrl, qrCodeImagePath,
                new Listener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(getActivity() == null) {
                            return;
                        }
                        qrCodeView.setImageBitmap(BitmapFactory.decodeFile(qrCodeImagePath));
                    }

                    @Override
                    public void onError(NetroidError error) {
                        if(getActivity() == null) {
                            return;
                        }
                        super.onError(error);
                        TipsHelper.showToast(getActivity(),
                                R.string.picture_download_failed);
                    }
                });
    }

    private void loadSchoolInfo() {
        if (TextUtils.isEmpty(schoolId) || TextUtils.isEmpty(getMemeberId())){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getMemeberId());
        params.put("SchoolId", schoolId);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
                new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        schoolInfo = getResult().getModel();
                        if (schoolInfo != null) {
                            //开放校园巡查功能
                            if(schoolInfo.isSchoolInspector()) {
                                hasInspectAuth = true;
                            }else {
                                hasInspectAuth = false;
                            }
                        }
                    }
                });
    }


    private class EntryInfo {
        public int type;
        public int title;
        public int icon;
        public boolean hasHeader;
        public int count;
    }



    private void enterClassInfoEvent() {
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
        args.putString("from", ClassDetailsFragment.TAG);
        if (isOnlineSchool){
            args.putBoolean(ClassContactsDetailsFragment.Constants.IS_ONLINE_SCHOOL, true);
        }
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

    /**
     * 进入空中课堂的详情界面
     */
    private void enterAirClassroom(){
        if (classInfo==null) return;
        Intent intent=new Intent(getActivity(), AirClassroomActivity.class);
        Bundle args=new Bundle();
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_SCHOOL_NAME, classInfo.getSchoolName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_GRADE_ID, classInfo.getGradeId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_GRADE_NAME, classInfo.getGradeName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putBoolean(AirClassroomActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putBoolean(AirClassroomActivity.EXTRA_IS_HEADMASTER,classInfo.isHeadMaster());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO,schoolInfo);
        if (schoolInfo != null){
            args.putBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS,schoolInfo.isOnlineSchool());
        }
        intent.putExtras(args);
        startActivity(intent);
    }

    /**
     * 进入表演课堂的列表详情
     */
    private void enterActClassroom(){
        if (classInfo==null) return;
        Intent intent=new Intent(getActivity(), ActClassroomActivity.class);
        Bundle args=new Bundle();
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_SCHOOL_NAME, classInfo.getSchoolName());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_GRADE_ID, classInfo.getGradeId());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_GRADE_NAME, classInfo.getGradeName());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putBoolean(ActClassroomActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putBoolean(ActClassroomActivity.EXTRA_IS_HEADMASTER,classInfo.isHeadMaster());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putSerializable(ActClassroomActivity.EXTRA_IS_SCHOOLINFO,schoolInfo);
        intent.putExtras(args);
        startActivity(intent);

    }
    private void enterClassResourceByChannel(int channelType) {
        if (classInfo == null) {
            return;
        }
        if (channelType == ClassResourceListActivity.CHANNEL_TYPE_SHOW){
            classInfo.setIsTempData(true);
        } else {
            classInfo.setIsTempData(false);
        }
        Bundle args = new Bundle();
        args.putString(ClassResourceListActivity.EXTRA_CLASS_ID, classInfo.getClassId());
        args.putString(ClassResourceListActivity.EXTRA_SCHOOL_ID, classInfo.getSchoolId());
        args.putInt(ClassResourceListActivity.EXTRA_CHANNEL_TYPE, channelType);
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putInt(ClassResourceListActivity.EXTRA_ROLE_TYPE, classInfo.getRoleType());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HEAD_MASTER,classInfo.isHeadMaster());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HISTORY, classInfo.isHistory());
        if (schoolInfo != null){
            args.putBoolean(ClassResourceListActivity.EXTRA_IS_ONLINE_SCHOOL_CLASS, schoolInfo.isOnlineSchool());
        }
        Intent intent = new Intent(getActivity(), ClassResourceListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }
    private void enterEntry(EntryInfo info) {
        if (info == null) {
            return;
        }
        switch (info.type) {
            case Constants.ENTRY_TYPE_STUDY_TASK:
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_HOMEWORK);
                break;
            case Constants.ENTRY_TYPE_CLASS_LESSON:
                //班级学程
                ActivityUtils.enterClassCourseDetailActivity(getActivity(),schoolInfo,classInfo);
                break;
            case Constants.ENTRY_TYPE_LECTURES:
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_LECTURE);
                break;
            case Constants.ENTRY_TYPE_CLASS_INFO:
                enterClassInfoEvent();
                break;
            case Constants.ENTRY_TYPE_NOTICE:
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_NOTICE);
                break;

            case Constants.ENTRY_TYPE_SHOW:
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_SHOW);
                break;
            //空中课堂
            case Constants.TAB_ENTITY_TYPE_AIR_CLASSROOM:
                enterAirClassroom();
                break;
            //表演课堂
            case Constants.TAB_ENTITY_TYPE_ACT_CLASSROOM:
                enterActClassroom();
                break;
            case Constants.ENTRY_TYPE_RELEVANCE_COURSE:
                if(EmptyUtil.isEmpty(classInfo)) return;
                // WatchCourseResourceActivity.show(this,"420", WatchResourceType.TYPE_TASK_ORDER,2,0);
                OnlineCourseHelper.requestOnlineCourseWithClassId(classInfo.getClassId(), new DataSource.Callback<String>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(R.string.tip_class_not_relevance_course);
                    }

                    @Override
                    public void onDataLoaded(String courseId) {
                        // 进入课程详情
                        String roles = classInfo.getRoles();
                        boolean isTeacher = UserHelper.isTeacher(roles);
                        CourseDetailsActivity.start(getActivity(),courseId, true, UserHelper.getUserId(),isTeacher);
                    }
                });
                break;
        }
    }

    private void loadEntries() {
        List<EntryInfo> itemList = new ArrayList<EntryInfo>();
        entryInfoHashMap.clear();
        EntryInfo item = new EntryInfo();
        item.type = Constants.ENTRY_TYPE_NOTICE;
        item.title = R.string.notices;
        item.icon = R.drawable.icon_class_notice;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);

        item = new EntryInfo();
        item.type = Constants.ENTRY_TYPE_SHOW;
        //秀秀
        item.title = R.string.shows;
        item.icon = R.drawable.icon_class_show;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);

        item = new EntryInfo();
        item.type = Constants.ENTRY_TYPE_STUDY_TASK;
        item.title = R.string.learning_tasks;
        item.icon = R.drawable.icon_learning_tasks;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);

        //班级学程
        item = new EntryInfo();
        item.type = Constants.ENTRY_TYPE_CLASS_LESSON;
        item.title = R.string.str_class_lesson;
        item.icon = R.drawable.icon_class_lesson;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);


        item = new EntryInfo();
        item.type = Constants.ENTRY_TYPE_LECTURES;
        item.title = R.string.lectures;
        item.icon = R.drawable.icon_gen_e_school;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);

        item=new EntryInfo();
        item.type= Constants.TAB_ENTITY_TYPE_AIR_CLASSROOM;
        item.title=R.string.air_classroom;
        item.icon=R.drawable.airclass_icon;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);

        //表演课堂
        item=new EntryInfo();
        item.type=Constants.TAB_ENTITY_TYPE_ACT_CLASSROOM;
        item.title=R.string.act_classroom;
        item.icon=R.drawable.act_classroom_icon;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);

        item = new EntryInfo();
        item.type = Constants.ENTRY_TYPE_CLASS_INFO;
        item.title = R.string.class_detail;
        item.icon = R.drawable.icon_class_information;
        itemList.add(item);
        entryInfoHashMap.put(item.type+"",item);
        getCurrAdapterViewHelper().setData(itemList);
    }

    private class MyAdapterViewHelper extends AdapterViewHelper {

        public MyAdapterViewHelper(Context context, AdapterView adapterView,
                                   int itemViewLayout) {
            super(context, adapterView, itemViewLayout);
        }

        @Override
        public void loadData() {
            loadEntries();
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            EntryInfo data = (EntryInfo) getDataAdapter().getItem(position);
            if (data == null) {
                return view;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
            }
            holder.data = data;

            int itemWidth = ScreenUtils.getScreenWidth(getActivity()) / COLUMN_NUM ;
            int iconSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);

            view.setLayoutParams(new AbsListView.LayoutParams(itemWidth, AbsListView
                    .LayoutParams.WRAP_CONTENT));

            ImageView imageView = (ImageView) view.findViewById(R.id.item_icon);
            if (imageView != null) {
                imageView.setImageResource(data.icon);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView
                        .getLayoutParams();
                layoutParams.width = iconSize;
                layoutParams.height = iconSize;
                imageView.setLayoutParams(layoutParams);
            }
            TextView textView = (TextView) view.findViewById(R.id.item_title);
            if (textView != null) {
                textView.setText(data.title);
            }
            imageView = (ImageView) view.findViewById(R.id.icon_selector);
            if (imageView != null) {
//                if (data.count > 99) {
//                    textView.setText("99+");
//                } else {
//                    textView.setText(String.valueOf(data.count));
//                }
                imageView.setVisibility(data.count> 0 ?
                        View.VISIBLE : View.GONE);

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
            enterEntry((EntryInfo) holder.data);
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn) {
            finish();
        } else if (v.getId() == R.id.contacts_header_right_ico) {
            showMoreMenu(findViewById(R.id.contacts_header_layout));
        } else if (v.getId() == R.id.attend_btn) {
            attendEvent();
        }
    }

    private void attendEvent() {
        if (fromType == Constants.FROM_TYPE_CLASS_HEAD_PIC) {
            if (!hasJoinedClass) {
                joinClass();
            }
        } else {
            if (hasJoinedClass) {
                enterSchoolContact();
            } else {
                joinSchool();
            }
        }
    }

    private void joinSchool() {
        QrcodeSchoolInfo data = new QrcodeSchoolInfo();
        data.setId(schoolId);
        data.setSname(schoolName);
        data.setLogoUrl(headPicUrl);
        Bundle args = new Bundle();
        args.putSerializable(ActivityUtils.KEY_QRCODE_SCHOOL_INFO, data);
        Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joinClass() {
        QrcodeClassInfo data = new QrcodeClassInfo();
        data.setClassId(classInfo.getClassId());
        StringBuilder builder = new StringBuilder();
        builder.append(classInfo.getClassName());
        data.setCname(builder.toString());
        data.setHeadPicUrl(classInfo.getHeadPicUrl());
        data.setSname(classInfo.getSchoolName());
        Bundle args = new Bundle();
        args.putSerializable(ActivityUtils.KEY_QRCODE_CLASS_INFO, data);
        Intent intent = new Intent(getActivity(), QrcodeProcessActivity.class);
        intent.putExtras(args);
        try {
            startActivity(intent);
        } catch (Exception e) {

        }
    }


    public void showMoreMenu(View view) {
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList();
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.save_qrcode));
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.subscription_recommend));
        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (position == 0) {
                            saveQrCodeImage();
                        } else if (position == 1) {
                            share();
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, itemDatas);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void saveQrCodeImage() {
        if (TextUtils.isEmpty(qrCodeImageUrl)) {
            return;
        }
        String filePath = ImageLoader.saveImage(getActivity(), qrCodeImageUrl);
        if (filePath != null) {
            TipsHelper.showToast(getActivity(),
                    getString(R.string.image_saved_to, filePath));
        } else {
            TipsHelper.showToast(getActivity(), getString(R.string.save_failed));
        }
    }

    private void share() {
        String shareAddress;
        shareAddress = String.format(ServerUrl.SUBSCRIBE_SHARE_QRCODE_URL,contactId )
                    + "&Type=" + 0;//班级
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(className);
        if (!TextUtils.isEmpty(schoolName)) {
            shareInfo.setContent(schoolName);
        } else {
            shareInfo.setContent(" ");
        }
        shareInfo.setTargetUrl(shareAddress);
        UMImage umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        if (!TextUtils.isEmpty(qrCodeImageUrl)) {
            umImage = new UMImage(getActivity(), qrCodeImageUrl);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setTitle(className);
        resource.setDescription(schoolName);
        resource.setShareUrl(shareAddress);
        resource.setThumbnailUrl(qrCodeImageUrl);
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    public static void setHasContentChanged(boolean hasContentChanged) {
        ClassDetailsFragment.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
            if (requestCode == ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS){
                if (data!=null){
                    boolean back=data.getBooleanExtra("back",false);
                    if (back){
                        Intent intent=new Intent();
                        Bundle bundle=new Bundle();
                        bundle.putBoolean("back",true);
                        intent.putExtras(bundle);
                        getActivity().setResult(Activity.RESULT_OK,intent);
                        getActivity().finish();
                        return;
                    }
                }
                //班级信息改变
                if (ClassContactsDetailsFragment.hasClassContentChanged()){
                    ClassContactsDetailsFragment.setHasClassContentChanged(false);
                    //设置本页面标志位
                    ClassDetailsFragment.setHasContentChanged(true);
                    //刷新页面
                    refreshData();
                }
            }
        }
    }
}
