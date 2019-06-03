package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyDownloadListActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.CacheCourseImagesTask;
import com.galaxyschool.app.wawaschool.course.CopyCourseTask;
import com.galaxyschool.app.wawaschool.course.DownloadOnePageTask;
import com.galaxyschool.app.wawaschool.course.PlaybackActivityNew;
import com.galaxyschool.app.wawaschool.course.SlideActivityNew;
import com.galaxyschool.app.wawaschool.course.library.ImportImage;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.PictureBooksDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.resource.ImportImageTask;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseImageListResult;
import com.galaxyschool.app.wawaschool.pojo.CourseSectionData;
import com.galaxyschool.app.wawaschool.pojo.CourseSectionDataListResult;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.libs.gallery.ImageInfo;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.libs.filedownloader.DownloadService;
import com.lqwawa.libs.filedownloader.FileInfo;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.PageInfo;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInputParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;
import com.oosic.apps.iemaker.base.playback.xml.Loader;
import com.oosic.apps.iemaker.base.playback.xml.PageXmlReader;
import com.osastudio.common.library.Md5FileNameGenerator;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DoCourseHelper {
    private WawaCourseUtils utils;
    private Context mContext;
    private String courseSectionDataString;
    private UserInfo userInfo;
    private String savePath;
    private NewResourceInfo newResourceInfo;
    private int screenType = -1;
    private boolean isRemoteSlide;

    private DialogHelper.LoadingDialog mLoadingDialog;

    private DownloadService downloadService;
    private int fromType = 0;
    private boolean isFromScanTask;
    private boolean isFromMoocModel;
    private String studyTaskTitle;
    private boolean needOriginVoicePath = true;
    private static CreateSlideHelper.CreateSlideParam mCreateSlideParam;
    private boolean autoMark = false;
    private String courseId;
    private ExerciseAnswerCardParam cardParam;
    private boolean isTeacherMark;//老师批阅

    public interface FromType {
        //我要做课件类型
        int DO_LQ_COURSE = 1;
        //我要加点读类型
        int Do_SLIDE_COURSE = 2;
        //图片  ppt 和 pdf的复述
        int Do_Retell_Course = 3;
        //任务答题卡的批阅
        int Do_Answer_Card_Check_Course = 4;
        //点读课件的学习任务
        int DO_SLIDE_COURSE_TASK = 5;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ImportImage.MSG_IMPORT_FINISH:
                    LocalCourseInfo localCourseInfo = (LocalCourseInfo) msg.obj;
                    if (localCourseInfo != null) {
                        if (newResourceInfo != null && screenType == -1) {
                            localCourseInfo.mOrientation = newResourceInfo.getScreenType();
                        } else {
                            localCourseInfo.mOrientation = screenType;
                        }
                        saveData(localCourseInfo);
                        if (fromType == FromType.Do_Retell_Course) {
                            if (newResourceInfo != null && needOriginVoicePath) {
                                //播放原音的路径
                                localCourseInfo.mOriginVoicePath = newResourceInfo.getResourceUrl();
                                if (localCourseInfo.mOriginVoicePath != null
                                        && localCourseInfo.mOriginVoicePath.contains(".zip")) {
                                    //截取字符串
                                    localCourseInfo.mOriginVoicePath
                                            = localCourseInfo.mOriginVoicePath.substring(0,
                                            localCourseInfo.mOriginVoicePath.indexOf(".zip"));
                                }
                            }
                            enterSlideNew(localCourseInfo, MaterialType.RECORD_BOOK,
                                    ResourceBaseFragment.REQUEST_CODE_RETELLCOURSE, true, -1);
                        } else {
                            enterSlideNew(localCourseInfo, MaterialType.RECORD_BOOK,
                                    ResourceBaseFragment.REQUEST_CODE_SLIDE, false, -1);
                        }
                    }
                    break;
            }
        }
    };

    public DoCourseHelper(Context context, DownloadService downloadService) {
        this.mContext = context;
        this.downloadService = downloadService;
        if (utils == null) {
            utils = new WawaCourseUtils((Activity) context);
        }
        if (userInfo == null) {
            userInfo = DemoApplication.getInstance().getUserInfo();
        }
    }

    public DoCourseHelper(Context context) {
        this.mContext = context;
        if (utils == null) {
            utils = new WawaCourseUtils((Activity) context);
        }
        if (userInfo == null) {
            userInfo = DemoApplication.getInstance().getUserInfo();
        }
    }

    public void setTeacherMark(boolean isTeacherMark) {
        this.isTeacherMark = isTeacherMark;
    }

    /**
     * 我要做任务单
     *
     * @param orderId
     */
    public void makeOrderCourse(final String orderId) {
        utils.loadSplitLearnCardDetail(orderId, true);
        utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    CourseData courseData = info.getCourseData();
                    if (courseData != null) {
                        if (isFromScanTask) {
                            //如果来自任务单扫码是任务首先判断一下是否有课程
                            processData(courseData, orderId);
                        } else {
                            downloadOnePage(courseData, courseSectionDataString);
                        }
                    }
                }
            }
        });
    }

    /**
     * 本地打开 我要做课件
     *
     * @param newResourceInfo
     */
    public void doLocalLqCourse(NewResourceInfo newResourceInfo, int fromType) {
        if (newResourceInfo != null) {
            this.newResourceInfo = newResourceInfo;
            this.fromType = fromType;
            this.screenType = newResourceInfo.getScreenType();
            if (!islogin()) {
                return;
            }
            String localPath = getLocalCoursePath(newResourceInfo);
            if (!TextUtils.isEmpty(localPath)) {
                String courseRootPath = getCourseRootPath(localPath);
                if (TextUtils.isEmpty(courseRootPath)) {
                    unzip(localPath, newResourceInfo.getTitle(), 0, null,
                            PictureBooksDetailFragment.Constants.OPERATION_TYPE_MAKEPICBOOK,
                            newResourceInfo);
                } else {
                    File file = new File(courseRootPath);
                    if (file.exists()) {
                        importLocalPicResources(courseRootPath, newResourceInfo.getTitle(), newResourceInfo);
                    } else {
                        unzip(localPath, newResourceInfo.getTitle(), 0, null,
                                PictureBooksDetailFragment.Constants
                                        .OPERATION_TYPE_MAKEPICBOOK, newResourceInfo);
                    }
                }
            }
        }
    }

    /**
     * 云端打开 我要做课件 例如个人资源库 和 校本资源库 等等
     *
     * @param newResourceInfo
     */

    public void doRemoteLqCourse(final NewResourceInfo newResourceInfo, final int fromType, boolean
            isFromMoocModel) {
        doRemoteLqCourse(newResourceInfo, fromType, isFromMoocModel, false);
    }


    public void doRemoteLqCourse(NewResourceInfo newResourceInfo, final int fromType) {
        doRemoteLqCourse(newResourceInfo, fromType, false);
    }

    public void doRemoteLqCourse(final NewResourceInfo newResourceInfo,
                                 final int fromType,
                                 boolean isFromMoocModel,
                                 boolean isTeacherMark) {
        if (newResourceInfo != null) {
            this.isFromMoocModel = isFromMoocModel;
            this.newResourceInfo = newResourceInfo;
            this.isTeacherMark = isTeacherMark;
            this.fromType = fromType;
            this.screenType = newResourceInfo.getScreenType();
            if (isTeacherMark || fromType == FromType.Do_Retell_Course) {
                this.studyTaskTitle = newResourceInfo.getTitle();
            }
            if (!islogin()) {
                return;
            }
            int haveFree = Utils.checkStorageSpace((Activity) mContext);
            if (haveFree != 0) {
                return;
            }
            final HashMap<String, Object> params = new HashMap<String, Object>();
            if (newResourceInfo != null) {
                String resId = newResourceInfo.getResourceId();
                if (!TextUtils.isEmpty(resId)) {
//                if (resId.contains("-")) {
//                    resId = resId.substring(0, resId.lastIndexOf("-"));
//                }
                    //传参microId-resourceType
                    params.put("courseId", resId);
                }
            }
            RequestHelper.RequestResourceResultListener listener = new RequestHelper
                    .RequestResourceResultListener(mContext, CourseImageListResult.class) {
                @Override
                public void onSuccess(String jsonString) {
                    if (mContext == null) {
                        return;
                    }
                    super.onSuccess(jsonString);
                    CourseImageListResult result = (CourseImageListResult) getResult();
                    if (result == null || result.getCode() != 0) {
                        TipsHelper.showToast(mContext, R.string.no_course_images);
                        return;
                    }
                    String savePath = null;
                    List<CourseData> courseDatas = result.getCourse();
                    if (courseDatas != null && courseDatas.size() > 0) {
                        CourseData courseData = courseDatas.get(0);
                        if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                            int screenType = courseData.screentype;
                            savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator().generate
                                    (courseData.resourceurl);
                            List<String> paths = result.getData();
                            if (paths == null || paths.size() == 0) {
                                String originVoicePath = newResourceInfo.getResourceUrl();
                                if (!TextUtils.isEmpty(originVoicePath) && originVoicePath.contains(".zip")) {
                                    //截取字符串
                                    originVoicePath = originVoicePath.substring(0, originVoicePath.indexOf(".zip"));
                                }
                                if (fromType == FromType.Do_Retell_Course) {
                                    createNewRetellCourseSlidePage(screenType, true, originVoicePath);
                                } else if (fromType == FromType.Do_SLIDE_COURSE
                                        || fromType == FromType.DO_SLIDE_COURSE_TASK) {
                                    //创建一个空白的点读
                                    createNewDoSlideCourse(screenType);
                                } else {
                                    createNewRetellCourseSlidePage(screenType);
                                }
                            } else {
//                                downloadCourseImages(savePath, result.getData(), courseData.nickname);
                                if (fromType == FromType.Do_Retell_Course && TextUtils.equals("1", courseData.getResproperties())) {
                                    //自动批阅
                                    courseId = newResourceInfo.getResourceId();
                                    if (!courseId.contains("-")) {
                                        courseId = courseId + "-19";
                                    }
                                    autoMark = true;
                                }
                                checkCanReplaceIPAddress(savePath, result.getData(), courseData);
                            }
                        }
                    }
                }
            };
            listener.setShowLoading(false);
            RequestHelper.sendGetRequest(mContext, ServerUrl.COURSE_IMAGES_URL, params, listener);
        }
    }

    /**
     * 任务单答题卡的批阅
     *
     * @param savePath
     * @param paths
     * @param title
     */
    public void doAnswerQuestionCheckMarkData(ExerciseAnswerCardParam cardParam,
                                              String savePath,
                                              List<String> paths,
                                              String title,
                                              int screenType,
                                              int fromType) {
        if (TextUtils.isEmpty(savePath)) {
            return;
        }
        if (paths == null || paths.size() == 0) {
            return;
        }
        this.cardParam = cardParam;
        this.studyTaskTitle = title;
        this.screenType = screenType;
        this.fromType = fromType;
        downloadCourseImages(savePath, paths, title);
    }

    public void doAnswerQuestionCheckMarkData(ExerciseAnswerCardParam cardParam,
                                              int fromType) {
        if (cardParam == null){
            return;
        }
        this.fromType = fromType;
        this.cardParam = cardParam;
        createNewDoSlideCourse(cardParam.getScreenType());
    }

    /**
     * 创建一个空白的点读课件
     *
     * @param orientation
     */
    private void createNewDoSlideCourse(int orientation) {
        int haveFree = Utils.checkStorageSpace((Activity) mContext);
        if (haveFree == 0) {
            CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam();
            param.mActivity = (Activity) mContext;
            param.mEntryType = Common.LIST_TYPE_SHARE;
            param.mEditable = true;
            param.mSlideType = CreateSlideHelper.SLIDETYPE_WHITEBOARD;
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                param.mMemberId = userInfo.getMemberId();
            }
            param.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true, true, true);
            if (fromType == DoCourseHelper.FromType.Do_Answer_Card_Check_Course){
                param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE;
                param.cardParam = cardParam;
            } else {
                param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_LQCLOUD_COURSE;
            }
            param.mOrientation = orientation;
            CreateSlideHelper.createSlide(param);
        }
    }

    /**
     * 打开本地的 我要加点读
     *
     * @param newResourceInfo
     */
    public void doLocalSlide(NewResourceInfo newResourceInfo) {
        if (newResourceInfo == null) return;
        this.newResourceInfo = newResourceInfo;
        if (!islogin()) {
            return;
        }
        int resType = BaseUtils.getCoursetType(newResourceInfo.getResourceUrl());
        if (newResourceInfo != null) {
            String courseType = String.valueOf(resType);
            if (!TextUtils.isEmpty(courseType)) {
                String localPath = null;
                if (TextUtils.isEmpty(newResourceInfo.getResourceSingleId())) {
                    localPath = newResourceInfo.getResourceUrl();
                } else {
                    localPath = getLocalCoursePath(newResourceInfo);
                }
                if (!TextUtils.isEmpty(localPath)) {
                    String courseRootPath = getCourseRootPath(localPath);
                    if (TextUtils.isEmpty(courseRootPath)) {
                        unzip(localPath, newResourceInfo.getTitle(), newResourceInfo.getScreenType(),
                                newResourceInfo.getDescription(), PictureBooksDetailFragment
                                        .Constants.OPERATION_TYPE_EDITCOURSE, newResourceInfo);
                    } else {
                        File file = new File(courseRootPath);
                        if (file.exists()) {
                            copyLocalCourse(courseRootPath, newResourceInfo.getScreenType(), newResourceInfo.getDescription());
                        } else {
                            unzip(localPath, newResourceInfo.getTitle(), newResourceInfo.getScreenType(),
                                    newResourceInfo.getDescription(), PictureBooksDetailFragment
                                            .Constants.OPERATION_TYPE_EDITCOURSE, newResourceInfo);
                        }
                    }
                }
            } else {
                TipsHelper.showToast(mContext, R.string.course_not_edit);
            }
        }
    }

    /**
     * 打开云端的 我要做点读
     *
     * @param newResourceInfo
     */
    public void doRemoteSlide(NewResourceInfo newResourceInfo) {
        if (newResourceInfo != null) {
            isRemoteSlide = true;
            if (!islogin()) {
                return;
            }
            DownloadOnePageTask task = new DownloadOnePageTask(
                    (Activity) mContext, newResourceInfo.getResourceUrl(), newResourceInfo.getTitle(),
                    newResourceInfo.getScreenType(), Utils.DOWNLOAD_TEMP_FOLDER, null);
            task.setCallbackListener(callbackListener);
            task.checkCanReplaceIPAddress(Integer.valueOf(newResourceInfo.getMicroId()),
                    newResourceInfo.getResourceType(), task);
        }
    }

    /**
     * 传入图片的路径来实现我要做课件 或者 我要加点读
     * fromType 用来区分做课件的类型
     */
    public void doRemoteLqCourseFromImage(List<String> paths, NewResourceInfo newResourceInfo,
                                          int screenType, int fromType, boolean isFromMoocModel) {
        if (newResourceInfo == null) return;
        if (!islogin()) {
            return;
        }
        needOriginVoicePath = false;
        savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                .generate(newResourceInfo.getResourceUrl());
        this.screenType = screenType;
        this.newResourceInfo = newResourceInfo;
        this.fromType = fromType;
        this.isFromMoocModel = isFromMoocModel;
        this.studyTaskTitle = newResourceInfo.getTitle();
        if (paths == null) return;
        downloadCourseImages(savePath, paths, newResourceInfo.getTitle());
    }

    public void doRemoteLqCourseFromImage(List<String> paths, NewResourceInfo newResourceInfo,
                                          int screenType, int fromType) {
        doRemoteLqCourseFromImage(paths, newResourceInfo, screenType, fromType, false);
    }


    /**
     * 传入图片的路径来实现我要做课件 或者 我要加点读
     * fromType 用来区分做课件的类型
     */
    public void doRemoteLqCourseFromImage(List<String> paths, ImageInfo newResourceInfo,
                                          int screenType, int fromType) {
        if (newResourceInfo == null) return;
        if (!islogin()) {
            return;
        }
        needOriginVoicePath = false;
        savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                .generate(newResourceInfo.getResourceUrl());
        this.screenType = screenType;
        this.newResourceInfo = new NewResourceInfo();
        this.newResourceInfo.setResourceUrl(newResourceInfo.getResourceUrl());
        this.newResourceInfo.setTitle(newResourceInfo.getTitle());
        this.newResourceInfo.setResourceType(newResourceInfo.getResourceType());
        this.studyTaskTitle = newResourceInfo.getTitle();
        this.fromType = fromType;
        if (paths == null) return;
        downloadCourseImages(savePath, paths, newResourceInfo.getTitle());
    }

    private void importDoSlideCourseImage(List<String> imagePaths) {
        mCreateSlideParam = new CreateSlideHelper.CreateSlideParam();
        mCreateSlideParam.mActivity = (Activity) mContext;
        mCreateSlideParam.mFragment = null;
        mCreateSlideParam.mEntryType = Common.LIST_TYPE_SHARE;
        mCreateSlideParam.mEditable = true;
        mCreateSlideParam.mIsPickOneImage = false;
        mCreateSlideParam.mSlideType = CreateSlideHelper.SLIDETYPE_IMAGE;
        mCreateSlideParam.mOrientation = screenType;
        mCreateSlideParam.mIsIntroducationTask = false;
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
            mCreateSlideParam.mMemberId = userInfo.getMemberId();
        }
        mCreateSlideParam.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true,
                true, true);
        mCreateSlideParam.mAttachmentPath = SlideManager.INSERT_IMAGES;
        mCreateSlideParam.mIsCreateAndPassResParam = true;
        if (newResourceInfo != null && (newResourceInfo.getResourceType() == ResType
                .RES_TYPE_PDF || newResourceInfo.getResourceType() == ResType.RES_TYPE_PPT)) {
            mCreateSlideParam.mTitle = studyTaskTitle;
        }
        if (fromType == FromType.Do_Answer_Card_Check_Course) {
            mCreateSlideParam.cardParam = cardParam;
            mCreateSlideParam.mTitle = studyTaskTitle;
            mCreateSlideParam.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE;
        } else if (fromType == FromType.DO_SLIDE_COURSE_TASK) {
            mCreateSlideParam.mTitle = studyTaskTitle;
            mCreateSlideParam.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE;
        } else {
            mCreateSlideParam.fromType = SlideManagerHornForPhone.FromWhereData.FROM_LQCLOUD_COURSE;
        }
        mCreateSlideParam.isTeacherMark = isTeacherMark;
        mCreateSlideParam.mSlideParam = CreateSlideHelper.getDefaultSlideParam();
        List<com.libs.yilib.pickimages.MediaInfo> imageInfos = new ArrayList<>();
        if (imagePaths != null && imagePaths.size() > 0) {
            imageInfos = getMediaInfos(imagePaths);
            mCreateSlideParam.mMediaInfos = (ArrayList<com.libs.yilib.pickimages.MediaInfo>) imageInfos;
            mCreateSlideParam.mMediaType = com.lqwawa.client.pojo.MediaType.PICTURE;
            Intent it = CreateSlideHelper.getSlideNewIntent(mCreateSlideParam);
            if (fromType == FromType.Do_SLIDE_COURSE
                    || fromType == FromType.Do_Answer_Card_Check_Course
                    || fromType == FromType.DO_SLIDE_COURSE_TASK) {
                ((Activity) mContext).startActivityForResult(it, ResourceBaseFragment.REQUEST_CODE_DO_SLIDE_TOAST);
            } else {
                ((Activity) mContext).startActivityForResult(it, 0);
            }
        }
    }

    private List<com.libs.yilib.pickimages.MediaInfo> getMediaInfos(List<String> imagePaths) {
        List<com.libs.yilib.pickimages.MediaInfo> imageInfos = new ArrayList<>();
        if (imagePaths != null && imagePaths.size() > 0) {
            for (int i = 0; i < imagePaths.size(); i++) {
                com.libs.yilib.pickimages.MediaInfo mediaInfo = new com.libs.yilib.pickimages.MediaInfo(imagePaths.get(i));
                imageInfos.add(mediaInfo);
            }
            return imageInfos;
        }
        return null;
    }

    private void enterMyCollectionBookList() {
        Intent intent = new Intent(mContext, MyDownloadListActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 校验是否用内网的IP进行下载
     */
    private void checkCanReplaceIPAddress(final String savePath, final List<String> paths, final CourseData courseData) {
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper((Activity) mContext);
        helper.setResId(courseData.id)
                .setResType(courseData.type)
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        List<String> imageUrl = paths;
                        if (flag) {
                            imageUrl = helper.getChangeIPUrlArray(paths);
                        }
                        downloadCourseImages(savePath, imageUrl, courseData.nickname);
                    }
                })
                .checkIP();
    }

    private void downloadCourseImages(String savePath, List<String> paths, String title) {
        if (paths == null || paths.size() == 0) {
            return;
        }
        CacheCourseImagesTask task = new CacheCourseImagesTask((Activity) mContext, paths,
                savePath, title);
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    List<String> imagePaths = (List<String>) result;
                    if (imagePaths != null && imagePaths.size() > 0) {
                        String title = "";
                        if (newResourceInfo != null) {
                            title = newResourceInfo.getTitle();
                        }
                        if (fromType == FromType.Do_SLIDE_COURSE) {
                            //我要加点读
                            importDoSlideCourseImage(imagePaths);
                        } else if (fromType == FromType.Do_Answer_Card_Check_Course
                                || fromType == FromType.DO_SLIDE_COURSE_TASK) {
                            //任务单批阅
                            importDoSlideCourseImage(imagePaths);
                        } else {
                            importLocalPicResources(imagePaths, title);
                        }
                    }
                }
            }
        });
        task.execute();
    }

    protected void createNewRetellCourseSlidePage(int orientation, boolean isRetellCourse, String originVoicePath) {
        Intent it = new Intent(mContext, SlideActivityNew.class);
        if (isRetellCourse) {
            it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, studyTaskTitle);
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
            it.putExtra(SlideActivityNew.MODEL_SOURCE_FROM, isFromMoocModel);
            it.putExtra(SlideActivityNew.IS_FROM_TEACHER_MARK, isTeacherMark);
        } else {
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        }
        it.putExtra(SlideActivityNew.ORIENTATION, orientation);
        SlideInputParam slideInputParam = getSlideInputParam(true, true);
        slideInputParam.mOriginVoicePath = originVoicePath;
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        ((Activity) mContext).startActivityForResult(it, ResourceBaseFragment.REQUEST_CODE_RETELLCOURSE);
    }

    protected void createNewRetellCourseSlidePage(int orientation) {
        Intent it = new Intent(mContext, SlideActivityNew.class);
        it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        it.putExtra(SlideActivityNew.ORIENTATION, orientation);
        SlideInputParam slideInputParam = getSlideInputParam(true, true);
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        ((Activity) mContext).startActivityForResult(it, ResourceBaseFragment.REQUEST_CODE_SLIDE);
    }

    public void processData(final CourseData courseData, String orderId) {
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        String id = orderId;
        if (id.contains("-")) {
            id = id.substring(0, id.indexOf('-'));
        }
        Map<String, Object> params = new HashMap();
        params.put("id", id);
        if (!AppConfig.BaseConfig.needShowPay()) {//只显示免费课程
            params.put("payType", 0);
        }
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestResourceResultListener<CourseSectionDataListResult>(
                        mContext, CourseSectionDataListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        List<CourseSectionData> list = getResult().getData();
                        if (list == null || list.size() <= 0) {
                            return;
                        }

                        courseSectionDataString = jsonString;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        downloadOnePage(courseData, courseSectionDataString);
                    }
                };

        listener.setShowLoading(true);
        listener.setTarget(courseData);
        RequestHelper.sendGetRequest(mContext,
                ServerUrl.LQMOOC_COURSE_SECTION_DATA_LIST_URL, params, listener);
    }

    /**
     * 下载有声相册
     *
     * @param courseData
     * @param jsonString
     */
    private void downloadOnePage(CourseData courseData, String jsonString) {
        DownloadOnePageTask task = new DownloadOnePageTask(
                (Activity) mContext, courseData.resourceurl, courseData.nickname,
                courseData.screentype, Utils.DOWNLOAD_TEMP_FOLDER, jsonString);
        task.setCallbackListener(callbackListener);
        task.checkCanReplaceIPAddress(courseData.id, courseData.type, task);
    }


    private void openLocalOnePage(LocalCourseDTO data, int screenType, String jsonString, int fromType) {
        if (data == null) {
            return;
        }
        CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam(
                (Activity) mContext, null, data.getmPath(), data.getmTitle(),
                data.getmDescription(), screenType);
        param.fromType = fromType;
        param.mIsScanTask = true;
        param.mSchoolId = "";
        param.mClassId = "";
        param.courseSectionDataString = jsonString;
        CreateSlideHelper.startSlide(param, Common.ACTIVITY_REQUEST_ATTACHMENGT_EDIT);
    }


    public Dialog showLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            return mLoadingDialog;
        }
        mLoadingDialog = DialogHelper.getIt((Activity) mContext).GetLoadingDialog(0);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        return mLoadingDialog;
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void playLocalCourse(LocalCourseInfo info, boolean isShareScreen) {
        String path = info.mPath;
        int resType = BaseUtils.getCoursetType(info.mPath);
        Intent it = ActivityUtils.getIntentForPlayLocalCourse(mContext, path,
                info.mTitle, info.mDescription,
                info.mOrientation, resType, true, isShareScreen);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity) mContext).startActivityForResult(it, 2);
    }

    private void unzip(final String destPath, final String title, final int orientaion, final String description,
                       final int operationType, final NewResourceInfo newResourceInfo) {
        if (newResourceInfo != null) {
            if (downloadService == null) {
                return;
            }
            FileInfo fileInfo = downloadService.getFileInfo(userInfo.getMemberId(),
                    newResourceInfo.getResourceId());
            if (fileInfo != null && fileInfo.isDownloaded()) {
                String zipFilePath = fileInfo.getFilePath();
                if (!TextUtils.isEmpty(zipFilePath) && new File(zipFilePath).exists()) {
                    FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(zipFilePath, destPath);
                    showLoadingDialog();
                    FileZipHelper.unzip(param, new FileZipHelper.ZipUnzipFileListener() {
                        @Override
                        public void onFinish(
                                FileZipHelper.ZipUnzipResult result) {
                            if (result.mIsOk && mContext != null) {
                                ((Activity) mContext).runOnUiThread(new Runnable() {
                                    public void run() {
                                        dismissLoadingDialog();
                                        if (!TextUtils.isEmpty(destPath) && new File(destPath).exists()) {
                                            String courseRootPath = getCourseRootPath(destPath);
                                            if (!TextUtils.isEmpty(courseRootPath)) {
                                                if (operationType == PictureBooksDetailFragment.Constants.OPERATION_TYPE_SHARESCEEN) {
                                                    LocalCourseInfo info = new LocalCourseInfo();
                                                    info.mPath = courseRootPath;
                                                    info.mOrientation = newResourceInfo.getScreenType();
                                                    info.mTitle = newResourceInfo.getTitle();
                                                    playLocalCourse(info, true);
                                                } else if (operationType == PictureBooksDetailFragment.Constants.OPERATION_TYPE_MAKEPICBOOK) {
                                                    importLocalPicResources(courseRootPath,
                                                            title, newResourceInfo);
                                                } else if (operationType == PictureBooksDetailFragment.Constants.OPERATION_TYPE_EDITCOURSE) {
                                                    copyLocalCourse(courseRootPath, orientaion, description);
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }

    private void importLocalPicResources(String coursePath, final String title, NewResourceInfo
            newResourceInfo) {
        int resType = BaseUtils.getCoursetType(coursePath);
        if (resType == ResType.RES_TYPE_ONEPAGE) {
            getSlidePicPath(coursePath, title);
        } else {
            getPicPaths(coursePath, title);
        }
//        final List<String> paths = getPicPaths(coursePath);
//        if (paths == null || paths.size() == 0){
//            createNewRetellCourseSlidePage(newResourceInfo.getScreenType());
//        }else {
//            importLocalPicResources(paths, title);
//        }
    }

    /**
     * 获取微课图片的路径
     *
     * @param coursePath
     * @param title
     */
    private void getPicPaths(final String coursePath, final String title) {
        if (!TextUtils.isEmpty(coursePath)) {
            File courseIndexFile = new File(coursePath, BaseUtils.RECORD_XML_NAME);
            if (courseIndexFile != null && courseIndexFile.exists() && courseIndexFile.canRead()) {
                Loader loader = new Loader(mContext, null);
                loader.startload(courseIndexFile.getPath(), new Loader.OnLoadFinishListener() {
                    @Override
                    public void onFinish(Loader loader, String path, boolean bSuccess) {
                        if (bSuccess) {
                            List<String> rawImages = loader.getRawImages();
                            if (rawImages == null || rawImages.size() == 0) {
                                if (fromType == FromType.Do_SLIDE_COURSE) {
                                    createNewDoSlideCourse(newResourceInfo.getScreenType());
                                } else {
                                    createNewRetellCourseSlidePage(newResourceInfo.getScreenType());
                                }
                            } else {
                                List<String> paths = new ArrayList<String>();
                                File pdfFile = new File(coursePath, "pdf");
                                for (String item : rawImages) {
                                    if (!TextUtils.isEmpty(item)) {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append(pdfFile.getPath());
                                        builder.append(File.separator);
                                        builder.append(item);
                                        paths.add(builder.toString());
                                    }
                                }
                                if (fromType == FromType.Do_SLIDE_COURSE) {
                                    //我要加点读
                                    importDoSlideCourseImage(paths);
                                } else {
                                    importLocalPicResources(paths, title);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 获取点读课件的图片路径
     *
     * @param coursePath
     * @param title
     */
    private void getSlidePicPath(final String coursePath, final String title) {
        if (!TextUtils.isEmpty(coursePath)) {
            File pageIndexFile = new File(coursePath, BaseUtils.PAGE_INDEX_FILE_NAME);
            if (pageIndexFile != null && pageIndexFile.exists() && pageIndexFile.canRead()) {
                PageXmlReader reader = new PageXmlReader(pageIndexFile.getPath());
                reader.setParseFinishListener(new PageXmlReader.ParseFinishListener() {
                    @Override
                    public void onParseFinish(boolean isSuccess, List<PageInfo> pageList, String version) {
                        if (pageList == null || pageList.size() == 0) {
                            createNewDoSlideCourse(newResourceInfo.getScreenType());
                        } else {
                            List<String> paths = new ArrayList<String>();
                            File pdfFile = new File(coursePath, "pdf");
                            for (PageInfo item : pageList) {
                                if (item != null && item.getmPageType() == PageInfo.PAGE_TYPE.IMAGE) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append(pdfFile.getPath());
                                    builder.append(File.separator);
                                    builder.append(item.getmPath());
                                    paths.add(builder.toString());
                                }
                            }
                            if (fromType == FromType.Do_SLIDE_COURSE) {
                                //点读课件 的 我要加点读
                                if (paths.size() > 0) {
                                    importDoSlideCourseImage(paths);
                                } else {
                                    createNewDoSlideCourse(newResourceInfo.getScreenType());
                                }
                            } else {
                                //点读课件 的 我要做课件
                                if (paths.size() > 0) {
                                    importLocalPicResources(paths, title);
                                } else {
                                    createNewRetellCourseSlidePage(newResourceInfo.getScreenType());
                                }
                            }
                        }
                    }
                });
                reader.load();
            }
        }
    }


    private void importLocalPicResources(final List<String> paths, final String title) {
        if (paths == null || paths.size() == 0) {
            TipMsgHelper.ShowLMsg(mContext, R.string.fetch_no_resources);
            return;
        }
        savePath = Utils.getUserCourseRootPath(userInfo.getMemberId(), CourseType
                .COURSE_TYPE_IMPORT, false);
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }

        ImportImageTask importResourceTask = new ImportImageTask((Activity) mContext,
                userInfo.getMemberId(), paths, savePath, title, handler);
        importResourceTask.execute();
    }

    private void copyLocalCourse(final String coursePath, final int orientation, final String description) {
        if (TextUtils.isEmpty(coursePath)) {
            return;
        }
        savePath = Utils.getUserCourseRootPath(userInfo.getMemberId(), CourseType.COURSE_TYPE_LOCAL, false);
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }
        String tempTitle = null;
        if (newResourceInfo != null) {
            tempTitle = newResourceInfo.getTitle();
        }
        if (TextUtils.isEmpty(tempTitle)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(savePath);
        builder.append(DateUtils.millSecToDateStr(System.currentTimeMillis()));
        builder.append(File.separator);
        final String destPath = builder.toString();

        copyLocalCourse(coursePath, destPath, orientation, tempTitle, description);
    }

    private void copyLocalCourse(String srcPath, String destPath, int orientation, String
            title, String description) {
        CopyCourseTask copyCourseTask = new CopyCourseTask((Activity) mContext, srcPath, destPath,
                orientation, title, description);
        copyCourseTask.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    LocalCourseInfo info = (LocalCourseInfo) result;
                    if (info != null) {
                        int resType = BaseUtils.getCoursetType(info.mPath);
                        if (resType == ResType.RES_TYPE_ONEPAGE) {
                            openLocalOnePage(info.toLocalCourseDTO(), info.mOrientation,
                                    courseSectionDataString, SlideManagerHornForPhone.FromWhereData.FROM_DO_ONEPAGE_COUSRSE);
                        } else {
                            enterLocalCourse(info, resType);
                        }
                    }

                }
            }
        });
        copyCourseTask.execute();
    }

    protected void enterLocalCourse(LocalCourseInfo info, int resType) {
        Intent intent = ActivityUtils.getIntentForPlayLocalCourse(mContext, info.mPath,
                info.mTitle, info.mDescription, info.mOrientation,
                resType, true, false);
        intent.putExtra(PlaybackActivityNew.COURSETYPEFORM, PlaybackActivityNew.CourseTypeFrom
                .Do_Read_Course);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    protected LocalCourseInfo saveCourseData(String path, int orientation, String description) {
        if (path == null) {
            return null;
        }
        String memberId = userInfo.getMemberId();
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        String parent = new File(path).getParentFile().getPath();

        if (parent.endsWith(File.separator)) {
            parent = parent.substring(0, parent.length() - 1);
        }
        LocalCourseInfo info = new LocalCourseInfo(
                path, parent, 0,
                System.currentTimeMillis(), CourseType.COURSE_TYPE_LOCAL, "", description);
        info.mParentPath = parent;
        info.mOrientation = orientation;
        info.mMemberId = memberId;
        LocalCourseDao localCourseDao = new LocalCourseDao(mContext);
        try {
            List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalCourseByPath(memberId, path);
            if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                localCourseDao.updateLocalCourse(memberId, path, info);
            } else {
                LocalCourseDTO dto = info.toLocalCourseDTO();
                dto.setmMemberId(memberId);
                localCourseDao.addOrUpdateLocalCourseDTO(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    protected void showMessageDialog(final String message, DialogInterface.OnClickListener confirmButtonClickListener) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(mContext, "", message,
                mContext.getString(R.string.cancel), null,
                mContext.getString(R.string.ok), confirmButtonClickListener);
        dialog.show();
//        resizeDialog(dialog, 0.9f);
    }

    protected boolean checkFileExist(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        } else {
            return new File(path).exists();
        }
    }

    private List<String> getPicPaths(String coursePath) {
        List<String> paths = new ArrayList<String>();
        if (!TextUtils.isEmpty(coursePath)) {
            File pdfFile = new File(coursePath, "pdf");
            File[] files = pdfFile.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0, len = files.length; i < len; i++) {
                    if (files[i] != null) {
                        String fileName = files[i].getName();
                        if (!TextUtils.isEmpty(fileName) && fileName.contains("pdf_page")) {
                            paths.add(files[i].getAbsolutePath());
                        }
                    }
                }
            }
        }

        return paths;
    }

    private String getLocalCoursePath(NewResourceInfo info) {
        String path = null;
        if (info != null && !TextUtils.isEmpty(info.getResourceUrl())) {
            path = Common.DownloadWeike + Md5FileNameGenerator.generate(info.getResourceUrl()) + "/";
            if (!TextUtils.isEmpty(info.getResourceId())) {
                String[] ids = info.getResourceId().split("-");
                if (ids != null && ids.length == 2) {
                    if (!TextUtils.isEmpty(ids[1])) {
                        int resType = Integer.parseInt(ids[1]);
                        if (resType > ResType.RES_TYPE_BASE) {
                            path = path + info.getTitle() + "/";
                        }
                    }
                }
            }
        }
        return path;
    }

    private String getCourseRootPath(String folder) {
        String result = null;
        if (!TextUtils.isEmpty(folder)) {
            File file = new File(folder);
            if (file.exists() && file.isDirectory()) {
                if (isCourseFolder(folder)) {
                    return folder;
                } else {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        result = getCourseRootPath(files[i].getPath());
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean isCourseFolder(String folder) {
        File headFile = new File(folder, "head.jpg");
        File pageIndexFile = new File(folder, "page_index.xml");
        File courseIndexFile = new File(folder, "course_index.xml");
        if (headFile == null || pageIndexFile == null || courseIndexFile == null) {
            return false;
        }
        if (headFile.exists() || pageIndexFile.exists() || courseIndexFile.exists()) {
            return true;
        }
        return false;
    }

    protected void saveData(LocalCourseInfo courseInfo) {
        if (courseInfo != null) {
//            String parentPath = courseInfo.mParentPath;
//            if (parentPath != null && parentPath.endsWith(File.separator)) {
//                parentPath = parentPath.substring(0, parentPath.length() - 1);
//            }
//            courseInfo.mParentPath = parentPath;
//            String path = courseInfo.mPath;
//            if (path != null && path.endsWith(File.separator)) {
//                path = path.substring(0, path.length() - 1);
//            }
//            courseInfo.mPath = path;
            courseInfo.mParentPath = Utils.removeFolderSeparator(courseInfo.mParentPath);
            courseInfo.mPath = Utils.removeFolderSeparator(courseInfo.mPath);
            LocalCourseDTO localCourseDTO = courseInfo.toLocalCourseDTO();
            localCourseDTO.setmType(CourseType.COURSE_TYPE_IMPORT);
            localCourseDTO.setmMemberId(userInfo.getMemberId());
            LocalCourseDao localCourseDao = new LocalCourseDao(mContext);
            if (localCourseDTO != null) {
                localCourseDao.addOrUpdateLocalCourseDTO(localCourseDTO);
            }
        }
    }

    protected void enterSlideNew(LocalCourseInfo info, int type, int requestCode,
                                 boolean isRetellCourse, int taskType) {
        Intent it = new Intent(mContext, SlideActivityNew.class);
        it.putExtra(SlideActivityNew.LOAD_FILE_PATH, info.mPath);
        it.putExtra(SlideActivityNew.LOAD_FILE_PAGES, info.mPageCount);
        it.putExtra(SlideActivityNew.COUTSE_TYPE, type);
        it.putExtra(SlideActivityNew.ORIENTATION, info.mOrientation);
        if (isRetellCourse) {
            it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, studyTaskTitle);
            it.putExtra(SlideActivityNew.ISNEEDDIRECTORY, true);
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
            it.putExtra(SlideActivityNew.MODEL_SOURCE_FROM, isFromMoocModel);
            it.putExtra(SlideActivityNew.IS_FROM_TEACHER_MARK, isTeacherMark);
            if (autoMark) {
                //复述课件评字标识
                it.putExtra(SlideActivityNew.COURSE_ID, courseId);
                it.putExtra(SlideActivityNew.AUTO_MARK, autoMark);
            }
        } else {
            if (newResourceInfo != null && (newResourceInfo.getResourceType() == ResType
                    .RES_TYPE_PDF || newResourceInfo.getResourceType() == ResType.RES_TYPE_PPT)) {
                it.putExtra(SlideActivityNew.LOAD_FILE_TITLE, studyTaskTitle);
            }
            it.putExtra(SlideActivityNew.COURSETYPEFROM, SlideActivityNew.CourseTypeFrom.FROMLQCOURSE);
        }
        SlideInputParam slideInputParam = getSlideInputParam(false, isRetellCourse);
        //support A4 paper ratio for course maker
        if (info.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            slideInputParam.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            slideInputParam.mRatioScreenWToH = 210.0f / 297.0f;
        }
        slideInputParam.mOriginVoicePath = info.mOriginVoicePath;
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        LocalCourseDao localCourseDao = new LocalCourseDao(mContext);
        try {
            List<LocalCourseDTO> dtos = localCourseDao.getLocalCourseByPath(userInfo.getMemberId(), info.mPath);
            if (dtos != null && dtos.size() > 0) {
                it.putExtra(SlideActivityNew.ORIENTATION, dtos.get(0).getmOrientation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((Activity) mContext).startActivityForResult(it, requestCode);
    }

    private SlideInputParam getSlideInputParam(boolean isNew, boolean isRetellCourse) {
        SlideInputParam param = new SlideInputParam();
        param.mCurUser = new User();
        if (userInfo != null) {
            param.mCurUser.mId = userInfo.getMemberId();
            if (TextUtils.isEmpty(userInfo.getRealName())) {
                param.mCurUser.mName = userInfo.getRealName();
            } else {
                param.mCurUser.mName = userInfo.getNickName();
            }
        }
        param.mNotShowShareBoxBtn = true;
        param.mIsCreateAndPassResParam = isNew;
//        if(isRetellCourse) {
//            int[] rayMenuV = {BaseSlideManager.MENU_ID_CAMERA, BaseSlideManager.MENU_ID_IMAGE,
//                    BaseSlideManager.MENU_ID_WHITEBOARD, BaseSlideManager.MENU_ID_AUDIO};
//            param.mRayMenusV = rayMenuV;
//        } else {
//            int[] rayMenuV = {BaseSlideManager.MENU_ID_CAMERA, BaseSlideManager.MENU_ID_IMAGE,
//                    BaseSlideManager.MENU_ID_WHITEBOARD, BaseSlideManager.MENU_ID_AUDIO,
//                    BaseSlideManager.MENU_ID_PERSONAL_MATERIAL};
//            param.mRayMenusV = rayMenuV;
//        }

        int[] rayMenuV = {
                BaseSlideManager.MENU_ID_CAMERA,
                BaseSlideManager.MENU_ID_IMAGE,
                BaseSlideManager.MENU_ID_WHITEBOARD,
                BaseSlideManager.MENU_ID_AUDIO,
                BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
        };

        if (fromType != FromType.Do_Retell_Course) {
            MyApplication application = (MyApplication) mContext.getApplicationContext();
            if (application != null) {
                UserInfo userInfo = application.getUserInfo();
                if (userInfo != null && userInfo.isTeacher()) {
                    rayMenuV = Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
                    rayMenuV[rayMenuV.length - 1] = BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
                }
            }
        }

        param.mRayMenusV = rayMenuV;
        int[] rayMenuH = {BaseSlideManager.MENU_ID_CURVE,
                BaseSlideManager.MENU_ID_LASER,
                BaseSlideManager.MENU_ID_ERASER};
        param.mRayMenusH = rayMenuH;
        return param;
    }

    private boolean islogin() {
        if ((userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) && mContext != null) {
            TipMsgHelper.ShowLMsg(mContext, mContext.getString(R.string.pls_login));
            ActivityUtils.enterLogin((Activity) mContext, false);
            return false;
        }
        return true;
    }

    public void setFromScanTask(boolean fromScanTask) {
        isFromScanTask = fromScanTask;
    }

    private CallbackListener callbackListener = new CallbackListener() {
        @Override
        public void onBack(Object result) {
            if (result != null) {
                LocalCourseDTO localCourseDTO = (LocalCourseDTO) result;
                if (isRemoteSlide) {
                    if (localCourseDTO != null) {
                        //云端的我要加点读
                        LocalCourseInfo info = saveCourseData(userInfo.getMemberId(),
                                localCourseDTO.getmPath(), localCourseDTO.getmOrientation(),
                                localCourseDTO.getmTitle(), localCourseDTO.getmDescription());
                        if (info != null) {
                            int resType = BaseUtils.getCoursetType(info.mPath);
                            if (resType == ResType.RES_TYPE_ONEPAGE) {
                                openLocalOnePage(info.toLocalCourseDTO(), info.mOrientation,
                                        courseSectionDataString, SlideManagerHornForPhone
                                                .FromWhereData.FROM_DO_ONEPAGE_COUSRSE);
                            } else {
                                enterLocalCourse(info, resType);
                            }
                        }
                    }
                } else {
                    //我要做任务单走此方法
                    openLocalOnePage(localCourseDTO, localCourseDTO.getmOrientation(), courseSectionDataString,
                            SlideManagerHornForPhone.FromWhereData.FROM_DO_TASKORDER_COUSRSE);
                }
            }
        }
    };

    protected LocalCourseInfo saveCourseData(String memberId, String path, int orientation, String
            title, String description) {
        if (path == null) {
            return null;
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        String parent = new File(path).getParentFile().getPath();

        if (parent.endsWith(File.separator)) {
            parent = parent.substring(0, parent.length() - 1);
        }
        LocalCourseInfo info = new LocalCourseInfo(
                path, parent, 0,
                System.currentTimeMillis(), CourseType.COURSE_TYPE_LOCAL, "", description);
        info.mParentPath = parent;
        info.mOrientation = orientation;
        info.mMemberId = memberId;
        info.mTitle = title;
        LocalCourseDao localCourseDao = new LocalCourseDao(mContext);
        try {
            List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalCourseByPath(memberId, path);
            if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
                localCourseDao.updateLocalCourse(memberId, path, info);
            } else {
                LocalCourseDTO dto = info.toLocalCourseDTO();
                dto.setmMemberId(memberId);
                localCourseDao.addOrUpdateLocalCourseDTO(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }
}
