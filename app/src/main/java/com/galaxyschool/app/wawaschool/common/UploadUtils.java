package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.PublishResourceFragment;
import com.galaxyschool.app.wawaschool.fragment.TeachResourceFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.UploadCourseType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UploadSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.lqwawa.client.pojo.MediaType;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.share.ShareType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangchao on 1/3/16.
 */
public class UploadUtils {
    public static void uploadResource(Activity activity, UploadParameter uploadParameter, CallbackListener listener) {
        UploadManager manager = UploadManager.getDefault(activity);
        manager.uploadResource(activity, uploadParameter, listener);
    }

    public static void uploadMedia(Activity activity, UploadParameter uploadParameter, CallbackListener listener) {
        UploadManager manager = UploadManager.getDefault(activity);
        manager.uploadMedia(uploadParameter, listener);
    }

    //for wawapage
    public static UploadParameter getUploadParameter(
        UserInfo userInfo, MediaInfo mediaInfo, int colType) {
        if (userInfo == null || mediaInfo == null) {
            return null;
        }
        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCreateName(userInfo.getRealName());
        uploadParameter.setAccount(userInfo.getNickName());
//        uploadParameter.setZipFilePath(mediaInfo.getPath());
        uploadParameter.setThumbPath(mediaInfo.getThumbnail());
        uploadParameter.setFileName(mediaInfo.getTitle());
        uploadParameter.setFilePath(mediaInfo.getPath());
        uploadParameter.setTotalTime(mediaInfo.getDuration());
        uploadParameter.setKnowledge("");
        uploadParameter.setDescription(mediaInfo.getDescription());
        if(!TextUtils.isEmpty(mediaInfo.getMicroId())) {
            uploadParameter.setResId(Long.parseLong(mediaInfo.getMicroId()));
        }
//        uploadParameter.setResType(ResType.RES_TYPE_ONEPAGE);
        int courseType = BaseUtils.getCoursetType(mediaInfo.getPath());
        if(courseType > 0) {
            uploadParameter.setResType(courseType);
        }
        uploadParameter.setScreenType(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LocalCourseInfo localCourseInfo = null;
        localCourseInfo = mediaInfo.getLocalCourseInfo();
        if (localCourseInfo != null && localCourseInfo.mOrientation >= 0) {
            uploadParameter.setScreenType(localCourseInfo.mOrientation);
        }
        uploadParameter.setColType(colType);
        String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
        if (!TextUtils.isEmpty(mediaInfo.getMicroId()) && !TextUtils.isEmpty(mediaInfo.getResourceUrl())) {
            String baseUrl = getBaseUploadUrl(mediaInfo.getResourceUrl());
            if (!TextUtils.isEmpty(baseUrl)) {
                uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, baseUrl);
            }
        }
        uploadParameter.setUploadUrl(uploadUrl);
        return uploadParameter;
    }

    public static UploadParameter getUploadParameter(
        UserInfo userInfo, int mediaType, int colType) {
        if (userInfo == null) {
            return null;
        }
        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCreateName(userInfo.getRealName());
        uploadParameter.setAccount(userInfo.getNickName());
        int resType = transMediaType(mediaType);
        if(resType >= 0) {
            uploadParameter.setResType(resType);
        }
        uploadParameter.setMediaType(mediaType);
        uploadParameter.setColType(colType);
        return uploadParameter;
    }

    public static UploadParameter getUploadParameter(UserInfo userInfo, LocalCourseInfo localCourseInfo,
                                                     UploadSchoolInfo uploadSchoolInfo, int type) {

        if (userInfo == null || localCourseInfo == null) {
            return null;
        }

        String thumbPath = null;
        if (localCourseInfo.mPath != null && !localCourseInfo.mPath.endsWith(File.separator)) {
            localCourseInfo.mPath = localCourseInfo.mPath + File.separator;
        }
        String courseName = localCourseInfo.mTitle;
        if (!TextUtils.isEmpty(localCourseInfo.mPath)) {
            thumbPath = localCourseInfo.mPath + Utils.RECORD_HEAD_IMAGE_NAME;
        }

        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCreateName(userInfo.getRealName());
        if (TextUtils.isEmpty(userInfo.getRealName())){
            uploadParameter.setCreateName(userInfo.getNickName());
        }
        uploadParameter.setAccount(userInfo.getNickName());
        uploadParameter.setFilePath(localCourseInfo.mPath);
        uploadParameter.setThumbPath(thumbPath);
        uploadParameter.setFileName(courseName);
        uploadParameter.setTotalTime(localCourseInfo.mDuration);
        uploadParameter.setKnowledge(localCourseInfo.mPoints);
        uploadParameter.setDescription(localCourseInfo.mDescription);
        uploadParameter.setResId(localCourseInfo.mMicroId);
        uploadParameter.setUploadSchoolInfo(uploadSchoolInfo);
        if (uploadSchoolInfo != null) {
            uploadParameter.setSchoolIds(uploadSchoolInfo.SchoolId);
        }
        int courseType = BaseUtils.getCoursetType(localCourseInfo.mPath);
        if(courseType > 0) {
            uploadParameter.setResType(courseType);
        }
        uploadParameter.setColType(1);  //upload resource
        uploadParameter.setScreenType(localCourseInfo.mOrientation);

        String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
        uploadParameter.setUploadUrl(uploadUrl);

        uploadParameter.setType(type);
        return uploadParameter;
    }

    public static int transMediaType(int mediaType) {
        int type = -1;
        switch (mediaType) {
            case MediaType.PICTURE:
                type = ResType.RES_TYPE_IMG;
                break;
            case MediaType.AUDIO:
                type = ResType.RES_TYPE_VOICE;
                break;
            case MediaType.VIDEO:
                type = ResType.RES_TYPE_VIDEO;
                break;
        }
        return type;
    }

    public static String getBaseUploadUrl(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        // 资源链接地址是阿里云oss地址，更新使用原有地址
        if (path.contains("aliyuncs.com")) {
            return  null;
        }
        int index = path.indexOf("/", 8); //find "/" after "http://"
        if (index > 0 && index < path.length()) {
            return path.substring(0, index);
        }

        return null;
    }

    public static void enterContactsPicker(Activity activity, UploadParameter uploadParameter) {
        Bundle args = new Bundle();
        if (uploadParameter != null) {
            args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        }

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
                ContactsPickerActivity.EXTRA_PICKER_MODE, ContactsPickerActivity.PICKER_MODE_SINGLE);
        args.putString(
                ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT, activity.getString(R.string
                        .send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE, ContactsPickerActivity
                    .ROLE_TYPE_STUDENT);

        Intent intent = new Intent();
        intent.setClass(activity, ContactsPickerActivity.class);
        intent.putExtras(args);
        if (uploadParameter.isTempData()){
            activity.startActivityForResult(intent,ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
        }else {
            activity.startActivity(intent);
        }
    }

    public static void enterContactsPicker(
            Activity activity, UploadParameter uploadParameter, int type) {
        Bundle args = new Bundle();
        args.putInt("TargetType", 1);
        if (uploadParameter != null) {
            args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        }
        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        if (type == PublishResourceFragment.Constants.TYPE_CHAT_RESOURCE) {
            args.putInt(
                    ContactsPickerActivity.EXTRA_PICKER_TYPE,
                    ContactsPickerActivity.PICKER_TYPE_PERSONAL);
            args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_CHAT_RESOURCE, true);
        } else {
            args.putInt(
                    ContactsPickerActivity.EXTRA_PICKER_TYPE,
                    ContactsPickerActivity.PICKER_TYPE_GROUP);
            args.putInt(
                    ContactsPickerActivity.EXTRA_GROUP_TYPE,
                    ContactsPickerActivity.GROUP_TYPE_CLASS);
            if (type == PublishResourceFragment.Constants.TYPE_SCHOOL_COURSE
                    || type == PublishResourceFragment.Constants.TYPE_SCHOOL_MOVEMENT) {
                args.putInt(
                        ContactsPickerActivity.EXTRA_GROUP_TYPE,
                        ContactsPickerActivity.GROUP_TYPE_SCHOOL);
            }
            args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
            if (uploadParameter != null && uploadParameter.getShareType() != ShareType.SHARE_TYPE_COMMENT) {
                args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE, ContactsPickerActivity.ROLE_TYPE_ALL);
            }
        }
        if (type == PublishResourceFragment.Constants.TYPE_CLASS_SPACE) {
            args.putInt(
                    ContactsPickerActivity.EXTRA_MEMBER_TYPE,
                    ContactsPickerActivity.MEMBER_TYPE_STUDENT);
        } else {
            args.putInt(
                    ContactsPickerActivity.EXTRA_MEMBER_TYPE,
                    ContactsPickerActivity.MEMBER_TYPE_ALL);
        }
        args.putInt(
                ContactsPickerActivity.EXTRA_PICKER_MODE,
                ContactsPickerActivity.PICKER_MODE_SINGLE);
        args.putString(
                ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT,
                activity.getString(R.string.send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        Intent intent = new Intent(activity, ContactsPickerActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    /**
     * 向空中课堂中增加学习资料
     * @param context
     * @param onlineId
     * @param memberId
     * @param content
     * @param resId
     * @param resUrl
     * @param resTitle
     * @param handler
     */
    public static void addAirClassroomMateria(final Activity context, int onlineId, String
            memberId, String content, String resId, String resUrl, String resTitle, final Handler handler){
        if (TextUtils.isEmpty(memberId)||context==null){
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        //必填
        params.put("ExtId",onlineId);
        params.put("MemberId",memberId);
        if (TextUtils.isEmpty(content)){
            params.put("ResId",resId);
        }else {
            params.put("Contents",content);
        }
        params.put("ResUrl",resUrl);
        //非必填
        params.put("ResTitle",resTitle);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        context, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (context == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        } else {
                            String errorMessage=getResult().getErrorMessage();
                            if (TextUtils.isEmpty(errorMessage)){
                                if (handler==null){
                                Intent intent=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putBoolean(ActivityUtils.REQUEST_CODE_NEED_TO_REFRESH,true);
                                intent.putExtras(bundle);
                                context.setResult(Activity.RESULT_OK,intent);
//                                TipsHelper.showToast(context,context.getString(R.string
//                                        .add_reporterId_success));
                                context.finish();
                                }else {
                                 handler.sendEmptyMessage(TeachResourceFragment.MSG_RETURN_NEED_REFRESH_DATA);
                                }
                            }else {
                                TipMsgHelper.ShowLMsg(context,errorMessage);
                            }

                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                };
        RequestHelper.sendPostRequest(context, ServerUrl.ADD_AIRCLASSROOM_MATERIA_BASE_URL, params, listener);
    }
}
