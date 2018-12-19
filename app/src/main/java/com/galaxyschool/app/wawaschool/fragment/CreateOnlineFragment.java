package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.galaxyschool.app.wawaschool.AirClassroomAddHostActivity;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyAttendedSchoolListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.LetvVodHelperNew;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.contacts.ContactItem;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.ContactsClassMemberListResult;
import com.galaxyschool.app.wawaschool.pojo.PublishClass;
import com.galaxyschool.app.wawaschool.pojo.ResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolClassDetail;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadCourseType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.CircleImageView;
import com.galaxyschool.app.wawaschool.views.DatePopupView;
import com.galaxyschool.app.wawaschool.views.ImagePopupView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.lqbaselib.net.FileApi;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.osastudio.common.utils.LQImageLoader;
import com.osastudio.common.utils.PhotoUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOnlineFragment extends ContactsListFragment {
    public static String TAG = CreateOnlineFragment.class.getSimpleName();
    public String SCHOOL_CLASS_NAME_TAG = "school_class_tag";
    private TextView addOnlineTitle, addOnlineIntroduction;
    private TextView onlineBeginTime, onlineEndTime;
    private ImageView addOnlineCover, deleteCover;
    private ImageView addSchoolResVideo, deleteSchoolResVideo;
    private ImageView addPublishImageV;
    private RelativeLayout rlPlaySchoolResVideoLayout;
    private LinearLayout videoLiveLayout;//视频直播layout
    private LinearLayout blackboardLiveLayout;//板书直播layout
    private LinearLayout schoolResVideoLayout;//校本的视频资源
    private ImageView videoLiveIv;//视频直播的小图标
    private ImageView blackboardLiveIv;//板书直播的小图标
    private List<ContactsClassMemberInfo> teachers = new ArrayList();
    private List<ContactsClassMemberInfo> allTeachers = new ArrayList<>();
    private String coverThumhnail;
    private String schoolId, classId, groupId;
    private String schoolName, className;
    private ContactsClassMemberInfo onlinePerson;
    private List<SchoolClassDetail> schoolClasses = new ArrayList<>();
    private int currentOnlineType = OnlineType.VIDEO_LIVE;
    private JSONObject creatorObject;
    private String startTime;
    private String endTime;
    private ResourceInfoTag videoData;

    public interface OnlineType {
        int VIDEO_LIVE = 1;
        int BLACKBOARD_LIVE = 2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_online_course, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIntentData();
        initViews();
        upDateAdatper();
        setSchoolClassNameShow();
        loadAddSchoolClass();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getIntentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            schoolId = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_SCHOOL_ID);
            classId = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_CLASS_ID);
            groupId = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_ID);
            schoolName = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_SCHOOL_NAME);
            className = bundle.getString(AirClassroomFragment.Constants.EXTRA_CONTACTS_CLASS_NAME);
            startTime = bundle.getString(AirClassroomFragment.Constants.EXTRA_FILTER_START_TIME_BEGIN);
            endTime = bundle.getString(AirClassroomFragment.Constants.EXTRA_FILTER_START_TIME_END);
        }
    }

    private void initViews() {
        initHeadData();
        findViews();
        initGridView();
    }

    private void initHeadData() {
        ToolbarTopView topView = (ToolbarTopView) findViewById(R.id.toolbar_top_view);
        if (topView != null) {
            TextView headTitle = topView.getTitleView();
            if (headTitle != null) {
                headTitle.setText(getString(R.string.create));
            }
            ImageView leftBtn = topView.getBackView();
            if (leftBtn != null) {
                leftBtn.setOnClickListener(this);
            }
        }
        TextView onlinePublishView = (TextView) findViewById(R.id.online_publish);
        if (onlinePublishView != null) {
            onlinePublishView.setOnClickListener(this);
        }
    }

    private void upDateAdatper() {
        loadContacts();
    }

    private void loadAddSchoolClass() {
        if (schoolClasses == null || schoolClasses.size() == 0) {
            return;
        }
        getAdapterViewHelper(SCHOOL_CLASS_NAME_TAG).setData(schoolClasses);
    }

    private void setSchoolClassNameShow() {
        schoolClasses.add(new SchoolClassDetail(false, schoolId, classId, schoolName, className, true));
        GridView gridView = (GridView) findViewById(R.id.resource_list_view);
        if (gridView != null) {
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(), gridView, R
                    .layout.item_school_class_name) {
                @Override
                public void loadData() {
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        final SchoolClassDetail data = (SchoolClassDetail) getData().get(position);
                        if (data != null) {
                            TextView textView = (TextView) view.findViewById(R.id.tv_school_class_name);
                            if (textView != null) {
                                textView.setText(data.getSchoolName() + data.getClassName());
                            }
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv_delete);
                            if (imageView != null) {
                                if (data.isNeedDelete()) {
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getAdapterViewHelper(SCHOOL_CLASS_NAME_TAG).getData().remove(data);
                                            schoolClasses.remove(data);
                                            removeAddPubHost(data);
                                            getAdapterViewHelper(SCHOOL_CLASS_NAME_TAG).update();
                                        }
                                    });
                                } else {
                                    imageView.setVisibility(View.INVISIBLE);
                                }
                            }
                            ViewHolder holder = (ViewHolder) view.getTag();
                            if (holder == null) {
                                holder = new ViewHolder();
                            }
                            holder.data = data;
                            view.setTag(holder);
                        }
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {

                }
            };
            addAdapterViewHelper(SCHOOL_CLASS_NAME_TAG, adapterViewHelper);
        }
    }

    private void findViews() {
        //添加标题
        addOnlineTitle = (TextView) findViewById(R.id.add_online_title);
        //添加简介
        addOnlineIntroduction = (TextView) findViewById(R.id.add_online_introduction);
        //添加封面
        addOnlineCover = (ImageView) findViewById(R.id.add_online_cover);
        addOnlineCover.setOnClickListener(this);
        deleteCover = (ImageView) findViewById(R.id.delete_cover_icon);
        deleteCover.setOnClickListener(this);
        //添加的发布对象View以及显示
        addPublishImageV = (ImageView) findViewById(R.id.add_publish_object);
        addPublishImageV.setOnClickListener(this);

        videoLiveLayout = (LinearLayout) findViewById(R.id.layout_video_live);
        videoLiveLayout.setOnClickListener(this);
        videoLiveIv = (ImageView) findViewById(R.id.iv_video_live_icon);
        videoLiveIv.setSelected(true);
        blackboardLiveLayout = (LinearLayout) findViewById(R.id.layout_blackboard_live);
        blackboardLiveLayout.setOnClickListener(this);
        blackboardLiveIv = (ImageView) findViewById(R.id.iv_blackboard_live_icon);
        schoolResVideoLayout = (LinearLayout) findViewById(R.id.ll_add_school_res_video);
        addSchoolResVideo = (ImageView) findViewById(R.id.iv_add_school_res_video);
        addSchoolResVideo.setOnClickListener(this);
        deleteSchoolResVideo = (ImageView) findViewById(R.id.iv_delete_school_video);
        deleteSchoolResVideo.setOnClickListener(this);
        rlPlaySchoolResVideoLayout = (RelativeLayout) findViewById(R.id.rl_play_school_video);
        setDate();
    }

    private void setDate() {
        //String startTime = DateUtils.getDateStr(new Date());
        String liveStartTime = DateUtils.getLive();
        String liveEndTime = DateUtils.getLiveEndTime(liveStartTime);
        if (!TextUtils.isEmpty(startTime)) {
            liveStartTime = startTime;
        }
        if (!TextUtils.isEmpty(endTime)) {
            liveEndTime = endTime;
        }
        //直播开始时间
        onlineBeginTime = (TextView) findViewById(R.id.online_begin_time);
        onlineBeginTime.setOnClickListener(this);
        onlineBeginTime.setText(liveStartTime);

        //直播结束时间
        onlineEndTime = (TextView) findViewById(R.id.online_end_time);
        onlineEndTime.setOnClickListener(this);

       /* Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 60);
        String endTime = DateUtils.getDateStr(calendar.getTime());*/
        onlineEndTime.setText(liveEndTime);
    }

    private void updateSchoolResVideoData(boolean isDeleteVideoData) {
        if (isDeleteVideoData) {
            //删除视频数据
            addSchoolResVideo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addSchoolResVideo.setImageResource(R.drawable.add_online_res);
            deleteSchoolResVideo.setVisibility(View.GONE);
            rlPlaySchoolResVideoLayout.setVisibility(View.GONE);
            blackboardLiveLayout.setVisibility(View.VISIBLE);
            videoData = null;
        } else {
            //更新video数据
            MyApplication.getThumbnailManager(getActivity()).displayThumbnailWithDefault
                    (videoData.getImgPath(), addSchoolResVideo, R.drawable.icon_default_image);
            addSchoolResVideo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            deleteSchoolResVideo.setVisibility(View.VISIBLE);
            rlPlaySchoolResVideoLayout.setVisibility(View.VISIBLE);
            blackboardLiveLayout.setVisibility(View.GONE);
        }
    }

    private void initGridView() {
        GridView gridView = (GridView) findViewById(R.id.add_online_host_gridview);
        if (gridView != null) {
            AdapterViewHelper adapterViewHelper = new AdapterViewHelper(getActivity(), gridView, R
                    .layout.circle_icon_item) {
                @Override
                public void loadData() {

                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (view != null) {
                        ContactsClassMemberInfo data = (ContactsClassMemberInfo) getData().get(position);
                        if (data == null) {
                            return view;
                        }
                        TextView teacherName = (TextView) view.findViewById(R.id.textView1);
                        if (teacherName != null) {
                            if (TextUtils.isEmpty(data.getMemberId())) {
                                teacherName.setText("");
                            } else {
                                String name = null;
                                if (!TextUtils.isEmpty(data.getRealName())) {
                                    name = data.getRealName();
                                } else if (!TextUtils.isEmpty(data.getNoteName())) {
                                    name = data.getNoteName();
                                } else {
                                    name = data.getNickname();
                                }
                                teacherName.setText(name);
                            }
                        }
                        ImageView userIcon = (CircleImageView) view.findViewById(R.id.imageView1);
                        if (userIcon != null) {
                            if (TextUtils.isEmpty(data.getMemberId())) {
                                getThumbnailManager().displayUserIconWithDefault(
                                        AppSettings.getFileUrl(data.getHeadPicUrl()), userIcon,
                                        R.drawable.add_online_host);
                            } else {
                                getThumbnailManager().displayUserIconWithDefault(
                                        AppSettings.getFileUrl(data.getHeadPicUrl()), userIcon,
                                        R.drawable.default_user_icon);
                            }
                        }
                    }
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ContactsClassMemberInfo data = (ContactsClassMemberInfo) getCurrAdapterViewHelper().getData().get(position);
                    if (data == null) return;
                    addOnlineHost(data);
                }

            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.online_publish) {
            //发布
            final String title = addOnlineTitle.getText().toString().trim();
            String startDate = onlineBeginTime.getText().toString().trim();
            String endDate = onlineEndTime.getText().toString().trim();
            if (TextUtils.isEmpty(title)) {
                TipsHelper.showToast(getActivity(), R.string.title_cannot_null);
                return;
            }
            //比较开始应该大于当前的时间
            if (!TextUtils.isEmpty(startDate)) {
                int result = DateUtils.compareDate(startDate, DateUtils.format(DateUtils.getCurDate
                                (), DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM).toString(),
                        DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM);
                if (result != 1) {
                    TipMsgHelper.ShowMsg(getActivity(), R.string.online_start_time_limit);
                    return;
                }
            }
            //比较开始时间应该小于结束时间
            if (!TextUtils.isEmpty(startDate) && !TextUtils.isEmpty(endDate)) {
                int result = DateUtils.compareDate(startDate, endDate, DateUtils
                        .DATE_PATTERN_yyyy_MM_dd_HH_MM);
                if (result != -1) {
                    TipMsgHelper.ShowMsg(getActivity(), R.string.online_time_limit);
                    return;
                }
            }
//            if (TextUtils.isEmpty(coverThumhnail)){
//                TipMsgHelper.ShowMsg(getActivity(),R.string.thumbnail_not_null);
//                return;
//            }
            if (TextUtils.isEmpty(getMemeberId()) || TextUtils.isEmpty(coverThumhnail)) {
                createOnlineData();
            } else {
                upLoadOnlineCover(getMemeberId(), coverThumhnail);
            }
        } else if (v.getId() == R.id.toolbar_top_back_btn) {
            //结束当前的界面
            if (getActivity() != null) {
                getActivity().finish();
            }
        } else if (v.getId() == R.id.add_online_cover) {
            UIUtils.hideSoftKeyboard(getActivity());
            insertOnlineCover();
        } else if (v.getId() == R.id.online_begin_time || v.getId() == R.id.online_end_time) {
            UIUtils.hideSoftKeyboard(getActivity());
            chooseOnlineTime((TextView) v);
        }
        if (v.getId() == R.id.delete_cover_icon) {
            addOnlineCover.setEnabled(true);
            addOnlineCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addOnlineCover.setImageResource(R.drawable.add_online_res);
            deleteCover.setVisibility(View.GONE);
            deleteFiles();
            coverThumhnail = null;
        } else if (v.getId() == R.id.add_publish_object) {
            UIUtils.hideSoftKeyboard(getActivity());
            //添加发布对象
            addOnlinePublishObject();
        } else if (v.getId() == R.id.layout_video_live) {
            //视频直播
            changOnlineType(OnlineType.VIDEO_LIVE);
        } else if (v.getId() == R.id.layout_blackboard_live) {
            //板书直播
            changOnlineType(OnlineType.BLACKBOARD_LIVE);
        } else if (v.getId() == R.id.iv_add_school_res_video) {
            //添加校本视频
            addSchoolResVideoData();
        } else if (v.getId() == R.id.iv_delete_school_video) {
            //删除当前的视频
            updateSchoolResVideoData(true);
        } else {
            UIUtils.hideSoftKeyboard(getActivity());
            super.onClick(v);
        }
    }

    private void addSchoolResVideoData() {
        if (videoData == null){
            //添加视频数据
            Intent intent = new Intent();
            intent.setClass(getActivity(), MyAttendedSchoolListActivity.class);
            intent.putExtra(ActivityUtils.EXTRA_IS_PICK, true);
            intent.putExtra(ActivityUtils.EXTRA_IS_PICK_SCHOOL_RESOURCE, true);
            intent.putExtra(ActivityUtils.EXTRA_IS_GET_APPOINT_RESOURCE, true);
            ArrayList<Integer> mediaTypeList = new ArrayList<>();
            mediaTypeList.add(MediaType.SCHOOL_VIDEO); //视频
            intent.putIntegerArrayListExtra(MediaListFragment.EXTRA_SHOW_MEDIA_TYPES, mediaTypeList);
            //看课件支持多类型
            intent.putExtra(MediaListFragment.EXTRA_SUPPORT_MULTI_TYPE_WATCH_WAWA_COURSE, true);
            intent.putExtra(ActivityUtils.EXTRA_SELECT_MAX_COUNT, 1);
            startActivityForResult(intent, IntroductionForReadCourseFragment.REQUEST_CODE_PICKER_RESOURCES);
        } else {
            //打开视频数据
            playVideo();
        }
    }

    private void playVideo(){
        String filePath = AppSettings.getFileUrl(videoData.getResourcePath());
        int resourceType = videoData.getResourceType();
        String idType = videoData.getResId();
        String leValue  = videoData.getLeValue();
        LetvVodHelperNew.VodVideoBuilder builder =new LetvVodHelperNew.VodVideoBuilder
                (getActivity())
                .setNewUI(true)//使用自定义UI
                .setTitle(videoData.getTitle())//视频标题
                .setMediaType(VodVideoSettingUtil.VIDEO_TYPE)
                .setResId(idType)
                .setResourceType(resourceType)
                .setAuthorId(videoData.getAuthorId())
                .setLeStatus(videoData.getLeStatus())
                //对精品和校本资源的收藏标识
                .setIsFromChoiceLib(true)
                .setHideBtnMore(true);
        if (TextUtils.isEmpty(leValue)){
            builder.setUrl(filePath);
            builder.create();
        }else {
            String [] values = leValue.split("&");
            String uUid = values[1].split("=")[1];
            String vUid = values[2].split("=")[1];
            builder.setUuid(uUid);
            builder.setVuid(vUid);
            builder.setUrl(filePath);
            builder.create();
        }
    }

    /**
     * 改变当前的直播类型
     *
     * @param onlineType 直播的类型
     */
    private void changOnlineType(int onlineType) {
        if (currentOnlineType == onlineType) return;
        currentOnlineType = onlineType;
        if (onlineType == OnlineType.VIDEO_LIVE) {
            videoLiveIv.setSelected(true);
            blackboardLiveIv.setSelected(false);
            schoolResVideoLayout.setVisibility(View.VISIBLE);
        } else if (onlineType == OnlineType.BLACKBOARD_LIVE) {
            videoLiveIv.setSelected(false);
            blackboardLiveIv.setSelected(true);
            schoolResVideoLayout.setVisibility(View.GONE);
        }
    }

    private void addOnlinePublishObject() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        GroupExpandPickerFragment fragment = new GroupExpandPickerFragment();
        Bundle args = getArguments();
        args.putBoolean(ActivityUtils.EXTRA_TEMP_DATA, true);
        args.putParcelableArrayList("schoolClass", (ArrayList<? extends Parcelable>) schoolClasses);
        args.putInt(ContactsPickerActivity.EXTRA_UPLOAD_TYPE, UploadCourseType.STUDY_TASK);
        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_TYPE, ContactsPickerActivity.PICKER_TYPE_GROUP);
        args.putInt(
                ContactsPickerActivity.EXTRA_GROUP_TYPE, ContactsPickerActivity.GROUP_TYPE_CLASS);
        args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
        args.putInt(
                ContactsPickerActivity.EXTRA_MEMBER_TYPE, ContactsPickerActivity.MEMBER_TYPE_STUDENT);
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_MODE, ContactsPickerActivity.PICKER_MODE_MULTIPLE);
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE, ContactsPickerActivity
                .ROLE_TYPE_TEACHER);
        fragment.setArguments(args);
        ft.add(R.id.activity_body, fragment, GroupExpandPickerFragment.TAG);
        ft.hide(this);
        ft.show(fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void chooseOnlineTime(final TextView textView) {
        String dateString = textView.getText().toString().trim();
        DatePopupView stateDatePopView = new DatePopupView(getActivity(), dateString, true, new DatePopupView
                .OnDateChangeListener() {
            @Override
            public void onDateChange(String dateStr) {
                textView.setText(dateStr);
            }
        });
        stateDatePopView.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 添加封面
     */
    private void insertOnlineCover() {
        deleteFiles();
        if (!new File(Utils.ICON_FOLDER).exists()) {
            new File(Utils.ICON_FOLDER).mkdirs();
        }
        ImagePopupView imagePopupView = new ImagePopupView(getActivity(), false);
        imagePopupView.showAtLocation(getView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearMemoryCache();
    }

    /**
     * 发布一条直播数据
     */
    private void createOnlineData() {
        if (!TextUtils.isEmpty(coverThumhnail) && coverThumhnail.contains("coverUrl")) {
            JSONObject jsonObject = JSON.parseObject(coverThumhnail, Feature.AutoCloseSource);
            coverThumhnail = jsonObject.getString("coverUrl");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        //必填
        if (!TextUtils.isEmpty(schoolId)) {
            params.put("SchoolId", schoolId);
        }
        if (!TextUtils.isEmpty(classId)) {
            params.put("ClassId", classId);
        }
        if (!TextUtils.isEmpty(getMemeberId())) {
            params.put("AcCreateId", getMemeberId());
        }
        params.put("Title", addOnlineTitle.getText().toString().trim());
        //非必填
        params.put("StartTimeStr", onlineBeginTime.getText().toString().trim());
        params.put("EndTimeStr", onlineEndTime.getText().toString().trim());
        params.put("Intro", addOnlineIntroduction.getText().toString().trim());
        params.put("CoverUrl", coverThumhnail);
        //区分板书直播（true） 和 视频直播(false)
        if (currentOnlineType == OnlineType.VIDEO_LIVE) {
            //视频直播
            params.put("IsEbanshuLive", false);
            if (videoData != null){
                params.put("LiveType", 1);
                params.put("DemandId", videoData.getResId());
                params.put("ResTitle", videoData.getTitle());
            }
        } else if (currentOnlineType == OnlineType.BLACKBOARD_LIVE) {
            //板书直播
            params.put("IsEbanshuLive", true);
        }
        //添加发布对象需要的集合字段
        params.put("PublishClassList", getPublishClass());
        //添加主持人的集合
        params.put("EmceeList", getPublishReporterList());

        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        getActivity(), DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            String errorMessage = getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ActivityUtils.REQUEST_CODE_NEED_TO_REFRESH, true);
                                intent.putExtras(bundle);
                                getActivity().setResult(Activity.RESULT_OK, intent);
                                TipsHelper.showToast(getActivity(), getString(R.string.create_success));
                                EventBus.getDefault().post(new MessageEvent("createOnlineSuccess"));
                                getActivity().finish();
                            } else {
                                TipMsgHelper.ShowLMsg(getActivity(), errorMessage);
                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        dismissLoadingDialog();
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CREATE_AIR_CLASSROOM_NEW_BASE_URL, params, listener);
    }

    /**
     * 添加主持人
     *
     * @param info
     */
    private void addOnlineHost(ContactsClassMemberInfo info) {
        if (info != null) {
            if (TextUtils.isEmpty(info.getMemberId())) {
                Intent intent = new Intent(getActivity(), AirClassroomAddHostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(AirClassroomAddHostFragment.PUBLISH_OBJECT_DATA_LIST, (Serializable) getPublishClass());
                bundle.putParcelableArrayList(AirClassroomAddHostFragment.TEACHER_LIST_SELECT, (ArrayList<? extends Parcelable>) allTeachers);
                intent.putExtras(bundle);
                startActivityForResult(intent, ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS);
            } else {
                ActivityUtils.enterPersonalSpace(getActivity(), String.valueOf(info.getMemberId()));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            //接受回传的老师信息
            if (requestCode == ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS) {
                allTeachers.clear();
                allTeachers = data.getParcelableArrayListExtra("teacherHost");
                upDateHostListAdapter();
            } else if (requestCode == IntroductionForReadCourseFragment.REQUEST_CODE_PICKER_RESOURCES) {
                //选择视频回调
                List<ResourceInfoTag> resultList = data.getParcelableArrayListExtra(MediaListFragment.EXTRA_RESOURCE_INFO_LIST);
                if (resultList != null && resultList.size() > 0) {
                    videoData = resultList.get(0);
                    if (videoData != null){
                        updateSchoolResVideoData(false);
                    }
                }
            }
        }
        //选择封面
        switch (requestCode) {
            case ActivityUtils.REQUEST_CODE_TAKE_PHOTO:
                File iconFile = new File(Utils.ICON_FOLDER + Utils.ICON_NAME);
                if (iconFile != null && iconFile.exists()) {
                    if (getActivity() == null) {
                        return;
                    }

                    File file = Utils.getZoomFile();
                    PhotoUtils.startZoomPhoto(getActivity(), iconFile, file, 1, 1,
                            Utils.IMAGE_LONG_SIZE, Utils.IMAGE_LONG_SIZE, PhotoUtils.REQUEST_CODE_ZOOM_PHOTO);
                }
                break;
            case ActivityUtils.REQUEST_CODE_FETCH_PHOTO:
                if (data != null) {
                    if (getActivity() == null) {
                        return;
                    }

                    String photo_path = null;

                    photo_path = PhotoUtils.getImageAbsolutePath(getActivity(), data
                            .getData());

                    if (TextUtils.isEmpty(photo_path)) {
                        return;
                    }

                    File file = Utils.getZoomFile();
                    PhotoUtils.startZoomPhoto(getActivity(), new File(photo_path), file, 1, 1,
                            Utils.IMAGE_LONG_SIZE, Utils.IMAGE_LONG_SIZE, PhotoUtils.REQUEST_CODE_ZOOM_PHOTO);
                }
                break;
            case ActivityUtils.REQUEST_CODE_ZOOM_PHOTO:
                String zoomIconPath = Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME;
                if (!TextUtils.isEmpty(zoomIconPath)) {
                    File iconZoomFile = new File(zoomIconPath);
                    if (iconZoomFile != null && iconZoomFile.length() > 0) {
                        addOnlineCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        deleteCover.setVisibility(View.VISIBLE);
                        coverThumhnail = iconZoomFile.getPath();
                        addOnlineCover.setEnabled(false);
                        //本地文件加载图片路径特有的方法
                        LQImageLoader.DIOptBuiderParam param = new LQImageLoader.DIOptBuiderParam();
                        param.mIsCacheInMemory = true;
                        param.mOutWidth = LQImageLoader.OUT_WIDTH;
                        param.mOutHeight = LQImageLoader.OUT_HEIGHT;
                        LQImageLoader.displayImage(coverThumhnail, addOnlineCover, param);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void deleteFiles() {
        String iconPath = Utils.ICON_FOLDER + Utils.ICON_NAME;
        if (new File(iconPath).exists()) {
            Utils.deleteFile(iconPath);
        }

        String zoomIconPath = Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME;
        if (new File(zoomIconPath).exists()) {
            Utils.deleteFile(zoomIconPath);
        }
    }

    /**
     * 上传直播封面的接口
     *
     * @param filePath
     */
    private void upLoadOnlineCover(String memberId, String filePath) {
        showLoadingDialog();
        UploadIconThread uploadIconThread = new UploadIconThread(memberId, filePath);
        uploadIconThread.start();
    }

    private class UploadIconThread extends Thread {
        private String url;
        Map<String, File> files;

        public UploadIconThread(String memberId, String filePath) {
            StringBuilder builder = new StringBuilder();
            builder.append(ServerUrl.UPLOAD_BASE_URL);
            builder.append("ID=" + memberId);
            builder.append("&token=1&flag=airclass&Extension=.jpg");
            this.url = builder.toString();
            files = new HashMap<String, File>();
            files.put("iconFile", new File(filePath));
        }

        @Override
        public void run() {
            try {
                String coverUrl = FileApi.postFile(url, files);
                coverThumhnail = coverUrl;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createOnlineData();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadContacts() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("Id", this.groupId);
        DefaultPullToRefreshListener listener =
                new DefaultPullToRefreshListener<ContactsClassMemberListResult>(
                        ContactsClassMemberListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ContactsClassMemberListResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        updateViews(result);
                    }
                };
        postRequest(ServerUrl.CONTACTS_CLASS_MEMBER_LIST_URL, params, listener);
    }

    private void updateViews(ContactsClassMemberListResult result) {
        List<ContactsClassMemberInfo> list = result.getModel().getClassMailListDetailList();
        UserInfo userInfo = getUserInfo();
        if (list == null || list.size() <= 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ContactsClassMemberInfo memberInfo = list.get(i);
            if (memberInfo.getRole() == RoleType.ROLE_TYPE_TEACHER && TextUtils.equals
                    (getMemeberId(), memberInfo.getMemberId())) {
                memberInfo.setClassId(classId);
                memberInfo.setSchoolId(schoolId);
                allTeachers.add(memberInfo);
            }
        }
        //有一个添加老师的按钮
        if (userInfo != null && allTeachers != null && allTeachers.size() > 0) {
            for (int i = 0, len = allTeachers.size(); i < len; i++) {
                ContactsClassMemberInfo info = allTeachers.get(i);
                if (TextUtils.equals(info.getMemberId().toLowerCase(), userInfo.getMemberId().toLowerCase())) {
                    onlinePerson = new ContactsClassMemberInfo();
                    onlinePerson.setMemberId(info.getMemberId());
                    onlinePerson.setHeadPicUrl(info.getHeadPicUrl());
                    onlinePerson.setNickname(info.getNickname());
                    onlinePerson.setNoteName(info.getNoteName());
                    onlinePerson.setRealName(userInfo.getRealName());
                    onlinePerson.setSchoolId(schoolId);
                    onlinePerson.setClassId(classId);
                    onlinePerson.setIsSelect(true);
                    teachers.add(onlinePerson);
                    break;
                }
            }
        }
        teachers.add(new ContactsClassMemberInfo());
        getCurrAdapterViewHelper().setData(teachers);
    }

    public void setCurrentClassParams(List<ContactItem> result) {
        if (result != null && result.size() > 0) {
            schoolClasses.clear();
            SchoolClassDetail tempData;
            for (int i = 0; i < result.size(); i++) {
                ContactItem item = result.get(i);
                tempData = new SchoolClassDetail();
                tempData.setSchoolName(item.getSchoolName());
                tempData.setClassName(item.getName());
                tempData.setSchoolId(item.getSchoolId());
                tempData.setClassId(item.getClassId());
                tempData.setIsSelect(true);
                if (classId.equals(item.getClassId())) {
                    tempData.setIsNeedDelete(false);
                } else {
                    tempData.setIsNeedDelete(true);
                }
                //把自己当前所在的班级放在第一位
                if (tempData.isNeedDelete()) {
                    schoolClasses.add(tempData);
                } else {
                    schoolClasses.add(0, tempData);
                }
            }
            loadAddSchoolClass();
            //更新主持人以及小编的信息
            updateReporterDataList();
        }
    }

    /**
     * 根据发布对象来更新主持人
     */
    private void updateReporterDataList() {
        List<ContactsClassMemberInfo> tempMemberInfo = new ArrayList<>();
        //所有的班级小编成员
        for (ContactsClassMemberInfo teacherInfo : allTeachers) {
            //发布对象
            boolean flag = false;
            for (SchoolClassDetail classDetail : schoolClasses) {
                //比较有没有相同的classId
                if (TextUtils.equals(teacherInfo.getClassId(), classDetail.getClassId())) {
                    flag = true;
                }
            }
            //当前的老师不在发布班级中
            if (!flag) {
                tempMemberInfo.add(teacherInfo);
            }
        }
        //从小编列表中移除不在发布对象中的小编
        if (tempMemberInfo.size() > 0) {
            for (ContactsClassMemberInfo memberInfo : tempMemberInfo) {
                allTeachers.remove(memberInfo);
            }
            upDateHostListAdapter();
        }
    }

    /**
     * 更新主持人列表数据 过滤小编相同的memberId
     */
    private void upDateHostListAdapter() {
        List<ContactsClassMemberInfo> infos = new ArrayList<>();
        for (int i = 0, len = allTeachers.size(); i < len; i++) {
            ContactsClassMemberInfo memberInfo = allTeachers.get(i);
            if (infos.size() == 0) {
                infos.add(memberInfo);
            } else {
                boolean flag = false;
                for (int j = 0, length = infos.size(); j < length; j++) {
                    ContactsClassMemberInfo teacherInfo = infos.get(j);
                    if (TextUtils.equals(teacherInfo.getMemberId(), memberInfo.getMemberId())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    infos.add(memberInfo);
                }
            }
        }
        infos.add(new ContactsClassMemberInfo());
        getCurrAdapterViewHelper().setData(infos);
    }

    /**
     * 移除根据发布班级增加的主持人
     */
    private void removeAddPubHost(SchoolClassDetail detail) {
        if (detail == null) return;
        if (allTeachers != null && allTeachers.size() > 0) {
            List<ContactsClassMemberInfo> tempTeacherInfo = new ArrayList<>();
            //校验在这个班级的成员
            for (int i = 0, len = allTeachers.size(); i < len; i++) {
                ContactsClassMemberInfo info = allTeachers.get(i);
                if (TextUtils.equals(info.getSchoolId(), detail.getSchoolId())
                        && TextUtils.equals(info.getClassId(), detail.getClassId())) {
                    tempTeacherInfo.add(info);
                }
            }
            //移除这个班级的成员
            if (tempTeacherInfo.size() > 0) {
                for (int i = 0, len = tempTeacherInfo.size(); i < len; i++) {
                    allTeachers.remove(tempTeacherInfo.get(i));
                }
            }
            upDateHostListAdapter();
        }
    }


    /**
     * 获取添加发布对象的班级集合
     */
    private List<PublishClass> getPublishClass() {
        List<PublishClass> publishClasses = new ArrayList<>();
        PublishClass pubClass = null;
        if (schoolClasses != null && schoolClasses.size() > 0) {
            StringBuilder classIdBuilder = new StringBuilder();
            StringBuilder schoolIdBuilder = new StringBuilder();
            for (int i = 0; i < schoolClasses.size(); i++) {
                SchoolClassDetail detail = schoolClasses.get(i);
                pubClass = new PublishClass();
                pubClass.setClassId(detail.getClassId());
                pubClass.setSchoolId(detail.getSchoolId());
                pubClass.setSchoolName(detail.getSchoolName());
                pubClass.setClassName(detail.getClassName());
                pubClass.setRole(RoleType.ROLE_TYPE_TEACHER);
                publishClasses.add(pubClass);

                if (classIdBuilder.length() == 0) {
                    classIdBuilder.append(detail.getClassId());
                    schoolIdBuilder.append(detail.getSchoolId());
                } else {
                    classIdBuilder.append(",").append(detail.getClassId());
                    schoolIdBuilder.append(",").append(detail.getSchoolId());
                }
            }
            creatorObject = new JSONObject();
            creatorObject.put("MemberId", getMemeberId());
            creatorObject.put("SchoolIds", schoolIdBuilder.toString());
            creatorObject.put("ClassIds", classIdBuilder.toString());
        }
        return publishClasses;
    }

    private JSONArray getPublishReporterList() {
        JSONArray array = new JSONArray();
        JSONObject object = null;
        List<ContactsClassMemberInfo> teachersInfo = getCurrAdapterViewHelper().getData();
        if (teachersInfo != null && teachersInfo.size() > 1) {
            teachersInfo = teachersInfo.subList(0, teachersInfo.size() - 1);
            if (teachersInfo != null && teachersInfo.size() > 0) {
                for (int i = 0, len = teachersInfo.size(); i < len; i++) {
                    ContactsClassMemberInfo info = teachersInfo.get(i);
                    if (info != null) {
                        if (creatorObject != null && TextUtils.equals(getMemeberId(), info.getMemberId())) {
                            //创建者自己
                            object = creatorObject;
                        } else {
                            StringBuilder classIdBuilder = new StringBuilder();
                            StringBuilder schoolIdBuilder = new StringBuilder();
                            for (int j = 0, length = allTeachers.size(); j < length; j++) {
                                ContactsClassMemberInfo memberInfo = allTeachers.get(j);
                                if (TextUtils.equals(memberInfo.getMemberId(), info.getMemberId())) {
                                    if (classIdBuilder.length() == 0) {
                                        classIdBuilder.append(memberInfo.getClassId());
                                        schoolIdBuilder.append(memberInfo.getSchoolId());
                                    } else {
                                        classIdBuilder.append(",").append(memberInfo.getClassId());
                                        schoolIdBuilder.append(",").append(memberInfo.getSchoolId());
                                    }
                                }
                            }
                            object = new JSONObject();
                            object.put("MemberId", info.getMemberId());
                            object.put("SchoolIds", schoolIdBuilder.toString());
                            object.put("ClassIds", classIdBuilder.toString());
                        }
                        array.add(object);
                    }
                }
            }
        }
        return array;
    }
}
