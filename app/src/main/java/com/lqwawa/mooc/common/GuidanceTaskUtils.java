package com.lqwawa.mooc.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.MediaDao;
import com.galaxyschool.app.wawaschool.db.dto.MediaDTO;
import com.galaxyschool.app.wawaschool.medias.activity.MyLocalPictureListActivity;
import com.galaxyschool.app.wawaschool.medias.fragment.MyLocalPictureListFragment;
import com.galaxyschool.app.wawaschool.pojo.MaterialResourceType;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaUploadList;
import com.galaxyschool.app.wawaschool.pojo.weike.ShortCourseInfo;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.libs.mediapaper.AudioPopwindow;
import com.lqwawa.libs.videorecorder.SimpleVideoRecorder;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.osastudio.common.utils.FileUtils;
import com.osastudio.common.utils.PhotoUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuidanceTaskUtils {
    private DialogHelper.LoadingDialog loadingDialog;
    private Context mContext;
    private int roleType;
    private String taskId;
    private String studentId;
    private boolean hasAlreadyCommit;
    private int commitTaskId;
    private boolean hasMarkPermission;
    private AudioPopwindow audioPopwindow;
    private String audioRecordPath;
    private boolean fromStudyTaskIntro;
    private CallbackListener listener;

    public static GuidanceTaskUtils getInstance() {
        return GuidanceTaskUtilHolder.instance;
    }

    public GuidanceTaskUtils setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public GuidanceTaskUtils setRoleType(int roleType) {
        this.roleType = roleType;
        return this;
    }

    public GuidanceTaskUtils setStudentId(String studentId) {
        this.studentId = studentId;
        return this;
    }

    public GuidanceTaskUtils setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public GuidanceTaskUtils setHasAlreadyCommit(boolean hasAlreadyCommit) {
        this.hasAlreadyCommit = hasAlreadyCommit;
        return this;
    }

    public GuidanceTaskUtils setCommitTaskId(int commitTaskId) {
        this.commitTaskId = commitTaskId;
        return this;
    }

    public GuidanceTaskUtils setHasMarkPermission(boolean hasMarkPermission) {
        this.hasMarkPermission = hasMarkPermission;
        return this;
    }

    public GuidanceTaskUtils setFromStudyTaskIntro(boolean fromStudyTaskIntro) {
        this.fromStudyTaskIntro = fromStudyTaskIntro;
        return this;
    }

    public GuidanceTaskUtils setCallBackListener(CallbackListener listener) {
        this.listener = listener;
        return this;
    }

    private static class GuidanceTaskUtilHolder {
        public static GuidanceTaskUtils instance = new GuidanceTaskUtils();
    }

    public void uploadMedias(List<MediaInfo> mediaInfos) {
        if (mediaInfos == null || mediaInfos.size() == 0) {
            return;
        }
        uploadMediasToServer(mediaInfos);
    }

    private void uploadMediasToServer(List<MediaInfo> mediaInfos) {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(MainApplication.getApplication(), R.string.pls_login);
            return;
        }
        UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo,
                mediaInfos.get(0).getMediaType(), 1);
        List<String> paths = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        for (MediaInfo mediaInfo : mediaInfos) {
            if (mediaInfo != null) {
                paths.add(mediaInfo.getPath());
                builder.append(Utils.removeFileNameSuffix(mediaInfo.getTitle()) + ";");
            }
        }
        String fileName = builder.toString();
        if (!TextUtils.isEmpty(fileName) && fileName.endsWith(";")) {
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        if (!TextUtils.isEmpty(fileName)) {
            uploadParameter.setFileName(fileName);
        }
        uploadParameter.setPaths(paths);
        showLoadingDialog();
        UploadUtils.uploadMedia((Activity) mContext, uploadParameter, result -> {
            dismissLoadingDialog();
            if (result == null) {
                return;
            }
            MediaUploadList uploadResult = (MediaUploadList) result;
            if (uploadResult.getCode() == 0) {
                List<MediaData> datas = uploadResult.getData();
                if (datas != null && datas.size() > 0) {
                    if (fromStudyTaskIntro) {
                        //来自导学任务选择任务的type
                        if (listener != null) {
                            ((Activity) mContext).runOnUiThread(() -> listener.onBack(datas));
                            int type = datas.get(0).type;
                            int mediaType = 0;
                            if (type == MaterialResourceType.PICTURE) {
                                mediaType = MediaType.PICTURE;
                            } else if (type == MaterialResourceType.VIDEO) {
                                mediaType = MediaType.VIDEO;
                            } else if (type == MaterialResourceType.AUDIO) {
                                mediaType = MediaType.AUDIO;
                            }
                            updateMediaInfo((Activity) mContext, uploadResult.getShortCourseInfoList(), mediaType);
                        }
                    } else {
                        MediaData data = datas.get(0);
                        if (data != null) {
                            if (hasAlreadyCommit) {
                                commitMarkData(data.toCourseData(), null);
                            } else {
                                commitGuidanceWork(data.toCourseData());
                            }
                        }
                    }
                }
            } else {
                ((Activity) mContext).runOnUiThread(() -> {
                    TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_failed);
                });
            }
        });
    }

    /**
     * 同步到个人资源库的接口
     */
    private void updateMediaInfo(final Activity activity,
                                 List<ShortCourseInfo> shortCourseInfos,
                                 int mediaType) {
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", DemoApplication.getInstance().getMemberId());
        params.put("MType", String.valueOf(mediaType));
        params.put("MaterialList", shortCourseInfos);
        RequestHelper.sendPostRequest(activity, ServerUrl.PR_UPLOAD_WAWAWEIKE_URL,
                params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        activity, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                    }
                });
    }


    private void commitGuidanceWork(CourseData courseData) {
        if (TextUtils.isEmpty(taskId)) {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("TaskId", taskId);
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            params.put("StudentId", DemoApplication.getInstance().getMemberId());
        } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
            //家长复述微课需要传递孩子的Id
            params.put("StudentId", studentId);
        }
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", courseData.nickname);
        }
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(mContext, DataResult.class) {
            @Override
            public void onSuccess(String json) {
                try {
                    if (mContext == null) {
                        return;
                    }
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        TipMsgHelper.ShowMsg(mContext, R.string.commit_success);
                        EventBus.getDefault().post(new MessageEvent(EventConstant.TRIGGER_UPDATE_COURSE));
                    } else {
                        String errorMessage = mContext.getString(R.string.publish_course_error);
                        if (result != null && !TextUtils.isEmpty(result.getErrorMessage())) {
                            errorMessage = result.getErrorMessage();
                        }
                        TipMsgHelper.ShowLMsg(mContext, errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(mContext, ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL, params,
                listener);
    }


    public void commitMarkData(CourseData courseData, String reViewContent) {
        Map<String, Object> params = new ArrayMap<>();
        params.put("CommitTaskId", commitTaskId);
        if (TextUtils.isEmpty(reViewContent)) {
            if (roleType == RoleType.ROLE_TYPE_TEACHER && hasMarkPermission) {
                params.put("IsTeacher", true);
                params.put("CreateId", DemoApplication.getInstance().getMemberId());
            } else {
                params.put("IsTeacher", false);
                String passStudentId = DemoApplication.getInstance().getMemberId();
                if (roleType == RoleType.ROLE_TYPE_PARENT) {
                    params.put("CreateId", studentId);
                } else {
                    params.put("CreateId", passStudentId);
                }
            }
        } else {
            //立即点评的数据
            params.put("IsTeacher", true);
            params.put("CreateId", DemoApplication.getInstance().getMemberId());
            params.put("IsVoiceReview", true);
            params.put("TaskScoreRemark", reViewContent);
        }
        if (courseData != null) {
            params.put("ResId", courseData.getIdType());
            params.put("ResUrl", courseData.resourceurl);
        }
        RequestHelper.sendPostRequest(mContext, ServerUrl.GET_ADDCOMMITTASKREVIEW,
                params, new RequestHelper.RequestDataResultListener<DataModelResult>(mContext,
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            dismissLoadingDialog();
                            return;
                        }
                        TipMsgHelper.ShowLMsg(mContext, R.string.commit_success);
                        Bundle bundle = null;
                        if (!TextUtils.isEmpty(reViewContent)) {
                            bundle = new Bundle();
                            bundle.putString("reviewContent", reViewContent);
                            EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.UPDATE_LIST_DATA));
                        }
                        EventBus.getDefault().post(new MessageEvent(bundle, EventConstant.TRIGGER_UPDATE_COURSE));
                    }
                });
    }

//    public void openGuidanceTypeData(CheckMarkInfo.ModelBean data,
//                                     StudyTask studyTask,
//                                     int roleType) {
//        ResourceInfoTag resourceInfoTag = new ResourceInfoTag();
//        resourceInfoTag.setResId(data.getResId());
//        resourceInfoTag.setResourcePath(data.getResUrl());
//        resourceInfoTag.setTitle(data.getResTitle());
//        ShareInfo shareInfo = new ShareInfo();
//        shareInfo.setThumbnailUrl(data.getThumbnailUrl());
//        String shareTitle = mContext.getString(R.string.str_guidance_type_show, data.getCreateName());
//        shareInfo.setTitle(shareTitle);
//        String shareContent = null;
//        if (studyTask != null){
//            shareContent = studyTask.getTaskTitle();
//        }
//        String content = mContext.getString(R.string.str_share_guidance_content, shareContent);
//        shareInfo.setContent(content);
//        StringBuilder targetUrl = new StringBuilder();
//        if (data.isBelongToMain()) {
//            targetUrl.append(ServerUrl.SHARE_NEW_GUID_READ_DETAIL_BASE_URL)
//                    .append("commitTaskId=")
//                    .append(data.getCommitTaskId())
//                    .append("&SchoolId=")
//                    .append(AppBaseSetting.INOOT_ONLINE_SCHOOL_ID);
//        } else {
//            targetUrl.append(ServerUrl.SHARE_NEW_GUID_READ_DETAIL_BASE_URL)
//                    .append("CommitTaskReviewId=")
//                    .append(data.getId())
//                    .append("&SchoolId=")
//                    .append(AppBaseSetting.INOOT_ONLINE_SCHOOL_ID);
//        }
//        shareInfo.setTargetUrl(targetUrl.toString());
//        resourceInfoTag.setShareInfo(shareInfo);
//        resourceInfoTag.setRoleType(roleType);
//        PassMiddleParameterObj obj = new PassMiddleParameterObj();
//        if (data.isBelongToMain()){
//            obj.setTaskId(String.valueOf(data.getCommitTaskId()));
//            obj.setType(1);
//        } else {
//            obj.setTaskId(String.valueOf(data.getId()));
//            obj.setType(2);
//        }
//        obj.setResId(data.getResId());
//        if (studyTask != null){
//            obj.setSchoolId(studyTask.getSchoolId());
//            obj.setSchoolName(studyTask.getSchoolName());
//            obj.setClassId(studyTask.getClassId());
//            obj.setClassName(studyTask.getClassName());
//        }
//        resourceInfoTag.setParameterObj(obj);
//        //处理图片的数据
//        boolean isPicture = handlePictureTypeData(data, resourceInfoTag);
//        if (isPicture) {
//            GalleryActivity.newInstance(mContext, resourceInfoTag);
//        } else {
//            WatchWawaCourseResourceOpenUtils.openResource(mContext, resourceInfoTag,
//                    false, false);
//        }
//    }
//
//    private boolean handlePictureTypeData(CheckMarkInfo.ModelBean data, ResourceInfoTag resourceInfoTag) {
//        boolean isPicture = false;
//        String resId = data.getResId();
//        if (!TextUtils.isEmpty(resId) && resId.contains("-")) {
//            int resType = Integer.valueOf(resId.split("-")[1]);
//            if (resType == MaterialResourceType.PICTURE) {
//                isPicture = true;
//                List<ResourceInfo> spitInfos = new ArrayList<>();
//                ResourceInfo resourceInfo = new ResourceInfo();
//                resourceInfo.setResId(data.getResId());
//                resourceInfo.setTitle(data.getResTitle());
//                resourceInfo.setResourcePath(data.getResUrl());
//                resourceInfo.setImgPath(data.getThumbnailUrl());
//                spitInfos.add(resourceInfo);
//                resourceInfoTag.setSplitInfoList(spitInfos);
//            }
//        }
//        return isPicture;
//    }

//    public void shareGuidanceResData(ShareInfo shareInfo) {
//        if (shareInfo == null) {
//            return;
//        }
//        String thumbnail = shareInfo.getThumbnailUrl();
//        UMImage umImage = null;
//        if (!TextUtils.isEmpty(thumbnail)) {
//            umImage = new UMImage(mContext, AppSettings.getFileUrl(thumbnail));
//        } else {
//            umImage = new UMImage(mContext, R.drawable.icon_look_audio);
//        }
//        shareInfo.setuMediaObject(umImage);
//        ShareUtils shareUtils = new ShareUtils((Activity) mContext);
//        shareUtils.share(((Activity) mContext).getWindow().getDecorView(), shareInfo);
//    }

    public void doGuidanceTypeWork(int guidanceType) {
        switch (guidanceType) {
            case GuidanceResourceType.TAKE_CAMERA:
                //交图片（拍照）
                takePhoto();
                break;
            case GuidanceResourceType.PHOTO:
                //交图片（相册）
                takeSystemPicture();
                break;
            case GuidanceResourceType.VIDEO:
                //交视频
                takeVideo();
                break;
            case GuidanceResourceType.AUDIO:
                //交音频
                takeAudio();
                break;
        }
    }

    private void  takePhoto() {
        deleteFiles();
        String urlFolder = Utils.ICON_FOLDER;
        String url = urlFolder + Utils.ZOOM_ICON_NAME;
        File fileFolder = new File(urlFolder);
        if (!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        File file = new File(url);
        PhotoUtils.startTakePhoto((Activity) mContext, file, PhotoUtils.REQUEST_CODE_TAKE_PHOTO);
    }

    private void takeSystemPicture() {
        Intent intent = new Intent(mContext, MyLocalPictureListActivity.class);
        intent.putExtra(CampusPatrolUtils.Constants.EXTRA_MEDIA_NAME,
                mContext.getString(R.string.start_image));
        intent.putExtra(CampusPatrolUtils.Constants.EXTRA_IS_PICK, true);
        intent.putExtra(MyLocalPictureListFragment.Contants.PICK_PICTURE_COUNT, fromStudyTaskIntro ? 10 : 1);
        intent.putExtra(MyLocalPictureListFragment.Contants.FROM_IMPLEMENTTATION_PLAN, true);
        ((Activity) mContext).startActivityForResult(intent, PhotoUtils.REQUEST_CODE_CHOOSE_PHOTOS);
    }

    private void takeVideo() {
        String dateFormat = "yyyyMMdd_HHmmss";
        String fileName = "VID_" + DateUtils.format(System.currentTimeMillis(), dateFormat) + ".mp4";
        Bundle args = new Bundle();
        args.putInt(SimpleVideoRecorder.EXTRA_VIDEO_DURATION, 180);
        args.putString(SimpleVideoRecorder.EXTRA_VIDEO_PATH, new File(Utils.VIDEO_FOLDER, fileName).getAbsolutePath());
        Intent intent = new Intent(mContext, SimpleVideoRecorder.class);
        intent.putExtras(args);
        try {
            ((Activity) mContext).startActivityForResult(intent, SimpleVideoRecorder.REQUEST_CODE_CAPTURE_VIDEO);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void takeAudio() {
        File audioFile = new File(Utils.AUDIO_FOLDER);
        if (!audioFile.exists()) {
            audioFile.mkdirs();
        }
        if (!TextUtils.isEmpty(audioRecordPath)) {
            File file = new File(audioRecordPath);
            if (file.exists()) {
                file.delete();
            }
        }
        audioPopwindow = new AudioPopwindow((Activity) mContext, Utils.AUDIO_FOLDER, path -> {
            if (TextUtils.isEmpty(path)) {
                return;
            }
            audioRecordPath = path;
            File file = new File(path);
            if (file.exists()) {
                long time = System.currentTimeMillis();
                String fileName = file.getName();

                MediaDTO mediaDTO = new MediaDTO();
                mediaDTO.setPath(path);
                mediaDTO.setTitle(fileName);
                mediaDTO.setMediaType(MediaType.AUDIO);
                mediaDTO.setCreateTime(time);
                MediaDao mediaDao = new MediaDao(mContext);
                mediaDao.addOrUpdateMediaDTO(mediaDTO);
                MediaInfo mediaInfo = mediaDTO.toMediaInfo();
                List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                if (mediaInfo != null) {
                    mediaInfos.add(mediaInfo);
                }
                uploadMedias(mediaInfos);
            }
        }, true);
        audioPopwindow.setAnimationStyle(R.style.AnimBottom);
        audioPopwindow.showPopupMenu(((Activity) mContext).getWindow().getDecorView());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == SimpleVideoRecorder.REQUEST_CODE_CAPTURE_VIDEO) {
            String filePath = data.getStringExtra(SimpleVideoRecorder.EXTRA_VIDEO_PATH);
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            File file = new File(filePath);
            if (file.exists()) {
                MediaDTO mediaDTO = new MediaDTO();
                mediaDTO.setPath(filePath);
                mediaDTO.setTitle(Utils.getFileNameFromPath(filePath));
                mediaDTO.setMediaType(MediaType.VIDEO);
                mediaDTO.setCreateTime(System.currentTimeMillis());
                MediaDao mediaDao = new MediaDao(mContext);
                mediaDao.addOrUpdateMediaDTO(mediaDTO);
                MediaInfo mediaInfo = mediaDTO.toMediaInfo();
                List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                if (mediaInfo != null) {
                    mediaInfos.add(mediaInfo);
                }
                uploadMedias(mediaInfos);
            }
        } else if (data != null && requestCode == PhotoUtils.REQUEST_CODE_CHOOSE_PHOTOS) {
            List<MediaInfo> mediaInfos = (List<MediaInfo>) data.getSerializableExtra("mediaInfos");
            if (mediaInfos != null && mediaInfos.size() > 0) {
                uploadMedias(mediaInfos);
            }
        } else if (requestCode == PhotoUtils.REQUEST_CODE_TAKE_PHOTO) {
            String imagePath = Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME;
            if (!TextUtils.isEmpty(imagePath)) {
                File imageFile = new File(imagePath);
                if (TextUtils.isEmpty(FileUtils.getFileSize(imageFile))) {
                    //没有拍照直接返回
                    return;
                }
                String path = imageFile.getPath();
                MediaInfo mediaInfo = new MediaInfo();
                mediaInfo.setTitle(Utils.getFileNameFromPath(path));
                mediaInfo.setPath(path);
                mediaInfo.setMediaType(MediaType.PICTURE);
                List<MediaInfo> mediaInfos = new ArrayList<MediaInfo>();
                mediaInfos.add(mediaInfo);
                uploadMedias(mediaInfos);
            }
        }
    }

    public void stopRecordingAudio() {
        if (audioPopwindow != null && audioPopwindow.isShowing()) {
            audioPopwindow.stopRecordingAudio();
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

    private Dialog showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return loadingDialog;
        }
        loadingDialog = DialogHelper.getIt((Activity) mContext).GetLoadingDialog(0);
        return loadingDialog;
    }

    private void dismissLoadingDialog() {
        try {
            if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
                this.loadingDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (this.loadingDialog != null) {
                this.loadingDialog = null;
            }
        }
    }
}
