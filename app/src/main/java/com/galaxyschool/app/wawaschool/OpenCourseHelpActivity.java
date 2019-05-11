package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.LetvVodHelperNew;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.ClassContactsDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.GroupExpandListFragment;
import com.galaxyschool.app.wawaschool.fragment.LiveTimetableFragment;
import com.galaxyschool.app.wawaschool.helper.ApplyMarkHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.libs.mediapaper.MediaPaper;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfoCode;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageInfo;
import com.oosic.apps.share.ShareType;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenCourseHelpActivity extends BaseFragmentActivity {

    private String mCourseId;
    private int mResType = -1;
    private boolean isShowCollectBtn;
    //判断是不是拆分的课程资源
    private boolean isSplitCourse;
    private WawaCourseUtils wawaCourseUtils;
    //是否打开帖子
    private boolean isOpenNote;
    private ClassNotificationEntity courseData;
    //打开机构主页
    private boolean isOpenSchoolSpace;
    private String schoolId;
    //打开空中课堂的课堂表
    private boolean isOpenAirClassLiveTable;
    //创建直播
    private boolean isCreateOnline;
    //授课计划新建通知
    private boolean isTeachingPlanNotice;
    //是不是进入班级详情
    private boolean isEnterClassDetail;
    //成绩统计
    private boolean isScoreStatics;
    //来自我要帮辅
    private boolean fromMyAssistantMark;

    public static void startActivity(Activity activity,
                                     String mCourseId,
                                     int mResType) {
        startActivity(activity, mCourseId, mResType, false);
    }

    public static void startActivity(Activity activity,
                                     String mCourseId,
                                     int mResType,
                                     boolean isShowCollectBtn) {
        activity.startActivity(new Intent(activity, OpenCourseHelpActivity.class)
                .putExtra("courseId", mCourseId)
                .putExtra("resType", mResType)
                .putExtra("isShowCollectBtn", isShowCollectBtn));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        loadIntentData();
        if (isOpenNote) {
            //打开帖子
            openOnlineNote();
        } else if (isOpenSchoolSpace) {
            //打开机构的详情页
            openSchoolSpaceDetail();
        } else if (isOpenAirClassLiveTable) {
            //打开空中课堂的课堂表
            openAirClassLiveTable();
        } else if (isCreateOnline) {
            //创建直播
            createOnlineData();
        } else if (isTeachingPlanNotice) {
            //授课计划新建通知
            createClassNotice();
        } else if (isEnterClassDetail){
            //进入班级详情
            enterClassDetail();
        } else if (isScoreStatics){
          //成绩统计
          enterScoreStaticActivity();
        } else if (fromMyAssistantMark){
          //我要帮辅
          enterMyApplyAssistantMarkDetail();
        } else {
            //打开资源
            loadCourseData();
        }
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            //资源详情字段
            mCourseId = intent.getStringExtra("courseId");
            mResType = intent.getIntExtra("resType", -1);
            isShowCollectBtn = intent.getBooleanExtra("isShowCollectBtn", false);
            //打开帖子字段
            isOpenNote = intent.getBooleanExtra("isOpenNote", false);
            courseData = (ClassNotificationEntity) intent.getSerializableExtra("courseData");
            //打开机构详情页
            isOpenSchoolSpace = intent.getBooleanExtra("isOpenSchoolSpace", false);
            schoolId = intent.getStringExtra("schoolId");
            //打开空中课堂的课程表
            isOpenAirClassLiveTable = intent.getBooleanExtra("isOpenAirClassLiveTable", false);
            //创建直播
            isCreateOnline = intent.getBooleanExtra("isCreateOnline", false);
            //授课计划的通知
            isTeachingPlanNotice = intent.getBooleanExtra("isTeachingPlanNotice", false);
            //进入班级详情
            isEnterClassDetail = intent.getBooleanExtra("isEnterClassDetail",false);
            //成绩统计
            isScoreStatics = intent.getBooleanExtra("isScoreStatics,",false);
            //来自我要帮辅
            fromMyAssistantMark = intent.getBooleanExtra("fromMyAssistantMark",false);
        }
    }

    private void loadCourseData() {
        if (mResType == 10019 || mResType == 10023) {
            isSplitCourse = true;
        }
        if (!TextUtils.isEmpty(mCourseId)) {
            if (isSplitCourse) {
                //来自分页的信息
                loadSplitCourseDetail(mCourseId);
            } else {
                if (mResType == ResType.RES_TYPE_PPT
                        || mResType == ResType.RES_TYPE_PDF
                        || mResType == ResType.RES_TYPE_DOC) {
                    String resId = mCourseId + "-" + mResType;
                    loadPptAndPdfDetail(resId);
                } else {
                    //如果是视频或者音频 需要在resId后面拼接一个type
                    if (mResType == ResType.RES_TYPE_VIDEO || mResType == ResType.RES_TYPE_VOICE
                            || mResType == ResType.RES_TYPE_IMG) {
                        mCourseId = mCourseId + "-" + mResType;
                    }
                    loadCourseDetail(mCourseId);
                }
            }
        }
    }

    /**
     * course分页的信息
     */
    private void loadSplitCourseDetail(String mCourseId) {
        if (wawaCourseUtils == null) {
            wawaCourseUtils = new WawaCourseUtils(this);
        }
        String courseId = mCourseId;
        if (courseId.contains("-")) {
            courseId = courseId.substring(0, courseId.indexOf("-"));
        }
        if (TextUtils.isEmpty(courseId) || !TextUtils.isDigitsOnly(courseId)) {
            return;
        }
        wawaCourseUtils.loadSplitCourseDetail(Long.parseLong(courseId));
        wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {

            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    NewResourceInfo newResourceInfo = info.getNewResourceInfo();
                    if (newResourceInfo.getFileSize() <= 0) {
                        newResourceInfo.setFileSize(info.getFileSize());
                    }
                    if (TextUtils.isEmpty(newResourceInfo.getShareAddress())) {
                        newResourceInfo.setShareAddress(info.getShareUrl());
                    }
                    analysisCourseData(newResourceInfo);
                }
            }
        });
    }

    /**
     * Course详情页的信息
     */
    private void loadCourseDetail(String mCourseId) {
        if (wawaCourseUtils == null) {
            wawaCourseUtils = new WawaCourseUtils(this);
        }

        wawaCourseUtils.loadCourseDetail(mCourseId);
        wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
            @Override
            public void onCourseDetailFinish(CourseData courseData) {
                if (courseData != null) {
                    NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                    if (TextUtils.isEmpty(newResourceInfo.getThumbnail())) {
                        newResourceInfo.setThumbnail(courseData.imgurl);
                    }
                    analysisCourseData(newResourceInfo);
                }
            }
        });
    }

    /**
     * 加载ppt 和 pdf的详情页信息
     */
    private void loadPptAndPdfDetail(final String resId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", resId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        final ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                PPTAndPDFCourseInfoCode result = com.alibaba.fastjson.JSONObject.parseObject
                        (jsonString, PPTAndPDFCourseInfoCode.class);
                if (result != null) {
                    List<ImageInfo> resourceInfoList = new ArrayList<>();
                    List<PPTAndPDFCourseInfo> splitCourseInfo = result.getData();
                    List<SplitCourseInfo> splitList = new ArrayList<>();
                    if (splitCourseInfo != null && splitCourseInfo.size() > 0) {
                        splitList = splitCourseInfo.get(0).getSplitList();
                    }
                    if (splitList == null || splitList.size() == 0) {
                        TipMsgHelper.ShowLMsg(OpenCourseHelpActivity.this, R.string
                                .ppt_pdf_not_have_pic);
                        finish();
                        return;
                    }
                    int type = mResType;
                    if (type > ResType.RES_TYPE_BASE) {
                        type = type % ResType.RES_TYPE_BASE;
                    }
                    for (int i = 0; i < splitList.size(); i++) {
                        SplitCourseInfo splitCourse = splitList.get(i);
                        ImageInfo newResourceInfo = new ImageInfo();
                        newResourceInfo.setTitle(splitCourse.getSubResName());
                        newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getPlayUrl()));
                        newResourceInfo.setResourceId(resId);
                        newResourceInfo.setResourceType(type);
                        newResourceInfo.setAuthorId(splitCourse.getMemberId());
                        resourceInfoList.add(newResourceInfo);
                    }
                    GalleryActivity.newInstance(OpenCourseHelpActivity.this, resourceInfoList,
                            true, 0, false, false, false);
                    finish();
                }
            }

            @Override
            public void onError(NetroidError error) {
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(this);
    }

    /**
     * 解析相应的数据
     *
     * @param newResourceInfo
     */
    private void analysisCourseData(NewResourceInfo newResourceInfo) {
        int resType = newResourceInfo.getResourceType();
        if (resType > ResType.RES_TYPE_BASE) {
            resType = resType % ResType.RES_TYPE_BASE;
        }
        switch (resType) {
            //图片
            case ResType.RES_TYPE_IMG:
                enterImageDetail(newResourceInfo);
                break;
            //音频
            case ResType.RES_TYPE_VOICE:
                enterMediaDetail(VodVideoSettingUtil.AUDIO_TYPE, newResourceInfo);
                break;
            //视频
            case ResType.RES_TYPE_VIDEO:
                enterMediaDetail(VodVideoSettingUtil.VIDEO_TYPE, newResourceInfo);
                break;
            //有声相册 //老课件 微课
            case ResType.RES_TYPE_ONEPAGE:
            case ResType.RES_TYPE_OLD_COURSE:
            case ResType.RES_TYPE_COURSE:
            case ResType.RES_TYPE_COURSE_SPEAKER:
                enterLqCourseDetail(newResourceInfo);
                break;
            //任务单  暂无
            case ResType.RES_TYPE_STUDY_CARD:
                break;
        }
    }

    private void enterLqCourseDetail(NewResourceInfo newResourceInfo) {
        PassParamhelper mParam = new PassParamhelper();
        if (!isShowCollectBtn) {
            mParam.isFromLQMOOC = true;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
        ActivityUtils.openPictureDetailActivity(this, newResourceInfo,
                PictureBooksDetailActivity.FROM_OTHRE, false, bundle);
        finish();
    }

    private void enterMediaDetail(int mediaType, NewResourceInfo newResourceInfo) {
        String filePath = AppSettings.getFileUrl(newResourceInfo.getResourceUrl());
        String microId = newResourceInfo.getMicroId();
        int resourceType = newResourceInfo.getResourceType();
        String idType = microId + "-" + resourceType;

        String leValue = newResourceInfo.getLeValue();
        LetvVodHelperNew.VodVideoBuilder builder = new LetvVodHelperNew.VodVideoBuilder(this)
                .setNewUI(true)//使用自定义UI
                .setTablet(false)
                .setTitle(newResourceInfo.getTitle())//视频标题
                .setMediaType(mediaType)
                .setResId(idType)
                .setResourceType(resourceType)
                .setAuthorId(newResourceInfo.getAuthorId())
                .setLeStatus(newResourceInfo.getLeStatus())
                .setHideBtnMore(true);
        if (TextUtils.isEmpty(leValue)) {
            builder.setUrl(filePath);
            builder.create();
        } else {
            String[] values = leValue.split("&");
            String uUid = values[1].split("=")[1];
            String vUid = values[2].split("=")[1];
            builder.setUuid(uUid);
            builder.setVuid(vUid);
            builder.setUrl(filePath);
            builder.create();
        }
        finish();
    }

    private void enterImageDetail(NewResourceInfo newResourceInfo) {
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        ImageInfo newInfo = new ImageInfo();
        newInfo.setTitle(newResourceInfo.getTitle());
        newInfo.setResourceUrl(AppSettings.getFileUrl(newResourceInfo.getResourceUrl()));
        newInfo.setResourceId(newResourceInfo.getResourceId());
        newInfo.setResourceType(ResType.RES_TYPE_IMG);
        newInfo.setAuthorId(newResourceInfo.getAuthorId());
        resourceInfoList.add(newInfo);
        ActivityUtils.openImage(this, resourceInfoList, true, 0, false, false, false);
        finish();
    }

    private void openOnlineNote() {
        if (courseData != null) {
            CourseInfo courseInfo = new CourseInfo();
            ActivityUtils.openOnlineNote(
                    OpenCourseHelpActivity.this,
                    courseInfo.toCourseInfo(courseData),
                    false,
                    false);
        }
        finish();
    }

    private void openSchoolSpaceDetail() {
        if (!TextUtils.isEmpty(schoolId)) {
            ActivityUtils.enterSchoolSpace(OpenCourseHelpActivity.this, schoolId);
        }
        finish();
    }

    private void openAirClassLiveTable() {
        Intent intent = new Intent(OpenCourseHelpActivity.this, CommonFragmentActivity.class);
        Bundle bundle = getIntent().getExtras();
        bundle.putSerializable(CommonFragmentActivity.EXTRA_CLASS_OBJECT, LiveTimetableFragment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void createOnlineData() {
        ActivityUtils.createOnlineData(OpenCourseHelpActivity.this, getIntent().getExtras());
        finish();
    }

    private void createClassNotice() {
        Bundle args = getIntent().getExtras();
        if (args == null){
            finish();
        }
        long dateTime = System.currentTimeMillis();
        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
        NoteOpenParams params = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
                MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
                MediaPaperActivity.SourceType.CLASS_SPACE, false);
        params.schoolId = args.getString("schoolId");
        params.classId = args.getString("classId");
        params.shareType = ShareType.SHARE_TYPE_NOTICE;
        ActivityUtils.openLocalNote(OpenCourseHelpActivity.this, params,
                CampusPatrolPickerFragment.CREATE_NEW_RESOURCE_REQUEST_CODE);
        finish();
    }

    private void enterClassDetail(){
        Intent intent = new Intent(OpenCourseHelpActivity.this, ContactsActivity.class);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            JoinClassEntity classEntity = (JoinClassEntity) args.getSerializable(JoinClassEntity.class.getSimpleName());
            if (classEntity != null) {
                boolean isOnlineSchool = args.getBoolean("isOnlineSchool", false);
                args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, classEntity.getType());
                args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classEntity.getClassMailListId());
                args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classEntity.getClassName());
                args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, classEntity.getSchoolId());
                args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, classEntity.getSchoolName());
                args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, classEntity.getClassId());
                args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, classEntity.getClassName());
                args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classEntity.getGroupId());
                args.putInt(ClassContactsDetailsFragment.Constants.EXTRA_CLASS_STATUS, classEntity.getIsHistory());
                args.putString("from", GroupExpandListFragment.TAG);
                args.putBoolean(ClassContactsDetailsFragment.Constants.IS_ONLINE_SCHOOL, isOnlineSchool);
                intent.putExtras(args);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * 成绩统计
     */
    private void enterScoreStaticActivity(){

    }

    /**
     * 进入我要申请帮辅界面
     */
    private void enterMyApplyAssistantMarkDetail(){
        Bundle args = getIntent().getExtras();
        if (args == null){
            return;
        }
        int screenType = args.getInt("screenType",1);
        QuestionResourceModel model = (QuestionResourceModel) args.getSerializable(QuestionResourceModel.class.getSimpleName());
        ExerciseAnswerCardParam cardParam = new ExerciseAnswerCardParam();
        cardParam.setScreenType(screenType);
        cardParam.setFromMyAssistantMark(true);
        cardParam.setMarkModel(model);
        ApplyMarkHelper.doApplyMarkTask(OpenCourseHelpActivity.this,cardParam);
        finish();
    }

}