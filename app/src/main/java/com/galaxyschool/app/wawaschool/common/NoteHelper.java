package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.PublishResourceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.NoteDao;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;
import com.galaxyschool.app.wawaschool.fragment.PublishResourceFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.net.course.UserApis;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.FileSuffixType;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UploadSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.views.ContactsInputBoxDialog;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.oosic.apps.share.ShareType;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: wangchao
 * Time: 2015/10/30 16:31
 */
public class NoteHelper {

    public static void uploadNote(
        Activity activity, final UploadParameter uploadParameter, long
        dateTime, CallbackListener
            listener) {
        UploadNoteTask uploadNoteTask = new UploadNoteTask(activity, uploadParameter, dateTime, listener);
        uploadNoteTask.execute();
    }


    private static class UploadNoteTask extends AsyncTask<Void, Void, CourseUploadResult> {
        Activity mActivity = null;
        UploadParameter mUploadParameter;
        long mdateTime;
        CallbackListener mListener;

        public UploadNoteTask(
            Activity activity, UploadParameter uploadParameter, long dateTime,
            CallbackListener listener) {
            mActivity = activity;
            mUploadParameter = uploadParameter;
            mdateTime = dateTime;
            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CourseUploadResult doInBackground(Void... arg0) {
            CourseUploadResult upload_result = null;
            if (mActivity == null || mUploadParameter == null) {
                return null;
            }
            File rsc = new File(mUploadParameter.getFilePath());
            try {
                String fileName = Utils.getFileNameFromPath(mUploadParameter.getFilePath());
                File zipFile = zipFile(rsc, fileName, mUploadParameter.getResType());
                if (zipFile.exists()) {
                    String path = zipFile.getPath();
                    long size = new File(path).length();
                    mUploadParameter.setZipFilePath(path);
                    mUploadParameter.setSize(size);
                    upload_result = UserApis.uploadResource(mActivity, mUploadParameter);
                    if (upload_result != null && upload_result.code == 0) {
                        if (upload_result.data != null && upload_result.data.size() > 0) {
                            CourseData courseData = upload_result.data.get(0);
                            if (courseData != null) {
                                if (mdateTime > 0) {
                                    NoteDTO noteDTO = new NoteDTO();
                                    noteDTO.setNoteId(courseData.id);
                                    noteDTO.setIsUpdate(false);
                                    NoteDao noteDao = new NoteDao(mActivity);
                                    try {
                                        noteDao.updateNoteDTO(mdateTime, mUploadParameter.getNoteType(), noteDTO);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return upload_result;
        }

        @Override
        protected void onPostExecute(CourseUploadResult result) {
            super.onPostExecute(result);
            if (mListener != null) {
                mListener.onBack(result);
            }
        }
    }

    private static File zipFile(File rsc, String fileName, int resType) {
        boolean rtn = false;
        File zipFile = null;

        String suffix = Utils.COURSE_SUFFIX;
        int type = resType % ResType.RES_TYPE_BASE;
        if (type == ResType.RES_TYPE_COURSE) {
            suffix = FileSuffixType.SUFFIX_TYPE_CMC;
        } else if (type == ResType.RES_TYPE_NOTE) {
            suffix = FileSuffixType.SUFFIX_TYPE_CMP;
        }

        String zipFileName = Utils.getFileTitle(fileName + Utils.COURSE_SUFFIX, Utils.TEMP_FOLDER, suffix);
        zipFile = new File(Utils.TEMP_FOLDER, zipFileName);
        File parent = new File(Utils.TEMP_FOLDER);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (zipFile.exists()) {
            zipFile.delete();
        }
        try {
            zipFile.createNewFile();
            rtn = Utils.zip(rsc, zipFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rtn) {
            return zipFile;
        } else {
            if (zipFile.exists()) {
                zipFile.delete();
            }
            return null;
        }
    }

    public static UploadParameter getUploadParameter(
        UserInfo userInfo, NoteInfo noteInfo,
        UploadSchoolInfo uploadSchoolInfo,int type, int noteType) {

        if (userInfo == null || noteInfo == null) {
            return null;
        }

        File file = new File(Utils.NOTE_FOLDER, String.valueOf(noteInfo.getDateTime()));
        String notePath = file.getPath();
        if (notePath != null && !notePath.endsWith(File.separator)) {
            notePath = notePath + File.separator;
        }

        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCreateName(userInfo.getRealName());
        uploadParameter.setAccount(userInfo.getNickName());
        uploadParameter.setFilePath(notePath);
        uploadParameter.setThumbPath(noteInfo.getThumbnail());
        uploadParameter.setFileName(noteInfo.getTitle());
        uploadParameter.setTotalTime(0);
        uploadParameter.setKnowledge("");
        uploadParameter.setDescription("");
        uploadParameter.setResId(noteInfo.getNoteId());
        uploadParameter.setResType(ResType.RES_TYPE_NOTE);

        uploadParameter.setUploadSchoolInfo(uploadSchoolInfo);
        if (uploadSchoolInfo != null) {
            uploadParameter.setSchoolIds(uploadSchoolInfo.SchoolId);
        }
        uploadParameter.setColType(1);
        uploadParameter.setType(type);
        uploadParameter.setNoteType(noteType);
        uploadParameter.setScreenType(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
        if (noteInfo.getNoteId() > 0 && !TextUtils.isEmpty(noteInfo.getResourceUrl())) {
            String baseUrl = UploadUtils.getBaseUploadUrl(noteInfo.getResourceUrl());
            if (!TextUtils.isEmpty(baseUrl)) {
                uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, baseUrl);
            }
        }
        uploadParameter.setUploadUrl(uploadUrl);
        return uploadParameter;
    }

    public static void publishLocalPostBar(Activity activity, UserInfo userInfo, NoteInfo noteInfo) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(activity);
            return;
        }
        if (noteInfo == null) {
            return;
        }

        Bundle args = new Bundle();
        args.putParcelable(NoteInfo.class.getSimpleName(), noteInfo);
        args.putString(PublishResourceActivity.EXTRA_DATA_TITLE, noteInfo.getTitle());
        args.putInt(
            PublishResourceActivity.EXTRA_RESOURE_FROM,
            PublishResourceActivity.NOTE_FROM_LOCAL);
        Intent intent = new Intent(
            activity,
            PublishResourceActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    public static void showPublishToContactsDialog(
        final Activity activity, final UploadParameter uploadParameter, final
    NoteInfo noteInfo, String dialogTitle, final String dataTitle, String dataDesc, final int type, final boolean
            isFinish) {
        showEditDialog(activity, dialogTitle, dataTitle,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    String title = ((ContactsInputBoxDialog) dialog).getInputText();
                    if (TextUtils.isEmpty(title)) {
                        title = dataTitle;
                    }
                    enterContactsPicker(activity, uploadParameter, noteInfo, title, null, type, isFinish);
                }
            }).show();
    }

    public static ContactsInputBoxDialog showEditDialog(
        Activity activity,
        String dialogTitle, String dataTitle,
        DialogInterface.OnClickListener confirmButtonClickListener) {
        ContactsInputBoxDialog dialog = new ContactsInputBoxDialog(
            activity,
            dialogTitle, dataTitle, activity.getString(R.string.pls_enter_title),
            activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, activity.getString(R.string.confirm), confirmButtonClickListener);
        dialog.show();
        return dialog;
    }

    public static void enterContactsPicker(
        Activity activity, UploadParameter uploadParameter, NoteInfo noteInfo, String dataTitle,
        String dataDesc, int type, boolean isFinish) {
        Bundle args = new Bundle();
        args.putString("Title", dataTitle);
        args.putString("Content", dataDesc);
        args.putInt("TargetType", 1);
        if (uploadParameter != null) {
            uploadParameter.setFileName(dataTitle);
            if (uploadParameter.getCourseData() != null) {
                uploadParameter.getCourseData().setNickname(dataTitle);
            }
            args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        }
        if (noteInfo != null) {
            args.putParcelable(NoteInfo.class.getSimpleName(), noteInfo);
        }
//        if (type == PublishResourceFragment.Constants.TYPE_COURSE || type == PublishResourceFragment.Constants.TYPE_HOMEWORK) {
//            args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
//            args.putInt(
//                ContactsPickerActivity.EXTRA_PICKER_TYPE,
//                ContactsPickerActivity.PICKER_TYPE_MEMBER
//                    | ContactsPickerActivity.PICKER_TYPE_PERSONAL);
//            args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
//        } else if (type == PublishResourceFragment.Constants.TYPE_COMMENT) {
//            args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
//            args.putInt(
//                ContactsPickerActivity.EXTRA_PICKER_TYPE,
//                ContactsPickerActivity.PICKER_TYPE_MEMBER
//                    | ContactsPickerActivity.PICKER_TYPE_FAMILY);
//            args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
//        } else if (type == PublishResourceFragment.Constants.TYPE_CHAT_RESOURCE) {
//            args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
//            args.putInt(
//                ContactsPickerActivity.EXTRA_PICKER_TYPE,
//                ContactsPickerActivity.PICKER_TYPE_GROUP
//                    | ContactsPickerActivity.PICKER_TYPE_PERSONAL);
//            args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_CHAT_RESOURCE, true);
//        } else {
//            args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
//            args.putInt(
//                ContactsPickerActivity.EXTRA_PICKER_TYPE,
//                ContactsPickerActivity.PICKER_TYPE_MEMBER);
//            args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);
//        }
        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        if (type == PublishResourceFragment.Constants.TYPE_CHAT_RESOURCE) {
//            args.putInt(
//                ContactsPickerActivity.EXTRA_PICKER_TYPE,
//                ContactsPickerActivity.PICKER_TYPE_GROUP
//                    | ContactsPickerActivity.PICKER_TYPE_PERSONAL);
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
                args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE, ContactsPickerActivity.ROLE_TYPE_TEACHER);
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
        //默认单选
        args.putInt(
            ContactsPickerActivity.EXTRA_PICKER_MODE,
            ContactsPickerActivity.PICKER_MODE_SINGLE);
//        if (type == PublishResourceFragment.Constants.TYPE_CHAT_RESOURCE
//            || type == PublishResourceFragment.Constants.TYPE_SCHOOL_COURSE
//            || type == PublishResourceFragment.Constants.TYPE_SCHOOL_MOVEMENT) {
//            args.putInt(
//                ContactsPickerActivity.EXTRA_PICKER_MODE,
//                ContactsPickerActivity.PICKER_MODE_SINGLE);
//        } else {
//            args.putInt(
//                ContactsPickerActivity.EXTRA_PICKER_MODE,
//                ContactsPickerActivity.PICKER_MODE_MULTIPLE);
//        }
        if (type == PublishResourceFragment.Constants.TYPE_CLASS_SPACE){
            if (uploadParameter != null && uploadParameter.getShareType() ==
                    ShareType.SHARE_TYPE_HOMEWORK) {
                //作业支持多选
                args.putInt(
                        ContactsPickerActivity.EXTRA_PICKER_MODE, ContactsPickerActivity.
                                PICKER_MODE_MULTIPLE);
            }
        }

        args.putString(
            ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT,
            activity.getString(R.string.send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        Intent intent = new Intent(activity, ContactsPickerActivity.class);
        intent.putExtras(args);
        activity.startActivity(intent);
        if (isFinish && activity != null) {
            activity.finish();
        }
    }

    public static void updateNoteToSchoolCourse(final Activity activity, UserInfo userInfo, CourseData courseData, String schoolId) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(activity, R.string.pls_login);
            return;
        }
        if (courseData == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("SchoolId", schoolId);
        params.put("MicroID", courseData.getIdType());
        params.put("Title", courseData.nickname);
        params.put("SubjectId", courseData.subjectId);
        params.put("GradeId", courseData.gradeId);
        params.put("LevelId", courseData.levelId);
        RequestHelper.sendPostRequest(activity, ServerUrl.UPLOAD_NOTE_SCHOOL_COURSE_URL,
            params, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (jsonString != null) {
                        DataResult result = JSON.parseObject(jsonString, DataResult.class);
                        if (activity != null) {
                            if (result != null && result.isSuccess()) {
                                TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_success);
                                activity.finish();
                            } else {
                                TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_error);
                            }
                        }
                    }
                }
            });
    }

    //校园动态
    public static void updateNoteToSchoolMovement(final Activity activity,
                                                  final UserInfo userInfo,
                                                  final CourseData courseData,
                                                  final String schoolId) {
        updateNoteToSchoolMovement(activity,userInfo,courseData,schoolId,false,0,false);
    }

    public static void updateNoteToSchoolMovement(final Activity activity,
                                                  final UserInfo userInfo,
                                                  final CourseData courseData,
                                                  final String schoolId,
                                                  boolean isOnlineSchool,
                                                  int messageType,
                                                  boolean isTeacher){
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(activity, R.string.pls_login);
            return;
        }
        if (courseData == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        if (TextUtils.isEmpty(courseData.code)){
            params.put("MemberId", userInfo.getMemberId());
        } else {
            //转发其他人的课件资源 课件作者保持原作者
            params.put("MemberId",courseData.code);
        }
        params.put("SchoolId", schoolId);
        params.put("MicroID", courseData.getIdType());
        params.put("Title", courseData.nickname);
        if (isOnlineSchool){
            //校园论坛
            params.put("SchoolSpaceType", messageType);
            if (isTeacher){
                params.put("Role", RoleType.ROLE_TYPE_TEACHER);
            } else {
                params.put("Role", RoleType.ROLE_TYPE_STUDENT);
            }
        }
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<NewResourceInfoListResult>(activity,
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        NewResourceInfoListResult result =getResult();
                        if (result != null && result.isSuccess()) {
                            List<NewResourceInfo> list = result.getModel().getData();
                            if (list == null || list.size() <= 0) {
                                return;
                            }
                            NewResourceInfo newResourceInfo = list.get(0);
                            createBroadcast(activity,userInfo,courseData,schoolId,1, newResourceInfo.getId());
                            TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_success);
                        } else {
                            TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_error);
                        }
                    }
                };
        RequestHelper.sendPostRequest(activity, ServerUrl.UPLOAD_NOTE_SCHOOL_MOVEMENT_URL, params, listener);
    }

    //播报厅
    private static void createBroadcast(final Activity activity, UserInfo userInfo, CourseData
            courseData, String schoolId,  int type,String id){
        Map<String, Object> params = new HashMap();
        params.put("Id", id);
        params.put("SchoolId", schoolId);
        params.put("MicroId", courseData.getIdType());
        params.put("MemberId", userInfo.getMemberId());
        params.put("Type", type);
        RequestHelper.sendPostRequest(activity, ServerUrl.CREATE_BROADCAST_LIST_URL,
                params, new Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (jsonString != null) {
                            DataResult result = JSON.parseObject(jsonString, DataResult.class);
                            if (activity != null) {
                                //资源发布成功
                                MediaPaperActivity.setHasResourceSended(true);
                                activity.finish();
                                if (result != null && result.isSuccess()) {
//                                    TipMsgHelper.ShowLMsg(activity, R.string.upload_file_sucess);

                                } else {
//                                    TipMsgHelper.ShowLMsg(activity, R.string.upload_file_failed);
                                }
                            }
                        }
                    }
                });
    }

    public static void updateBroadcast(final Activity activity,String microId,String createId){
        Map<String, Object> params = new HashMap();
        params.put("MicroId", microId);
        params.put("MemberId", createId);
        RequestHelper.sendPostRequest(activity, ServerUrl.UPDATE_BROADCAST_LIST_URL,
                params, new Listener<String>() {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (jsonString != null) {
                            DataResult result = JSON.parseObject(jsonString, DataResult.class);
                            if (activity != null) {
                                if (result != null && result.isSuccess()) {
                                } else {
                                }
                            }
                        }
                    }
                });
    }

//秀秀
    public static void updateNoteToClassSpace(final Activity activity, final UserInfo userInfo,
                                              final CourseData courseData,final String schoolId,
                                              String
                                                      classId, final int type) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(activity, R.string.pls_login);
            return;
        }
        if (courseData == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("SchoolId", schoolId);
        params.put("MicroID", courseData.getIdType());
        params.put("Title", courseData.nickname);
        params.put("ClassId", classId);
        params.put("ActionType", String.valueOf(type));

        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<NewResourceInfoListResult>(activity,
                        NewResourceInfoListResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        NewResourceInfoListResult result =getResult();
                        if (result != null && result.isSuccess()) {
                            List<NewResourceInfo> list = result.getModel().getData();
                            if (list == null || list.size() <= 0) {
                                return;
                            }
                            TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_success);
                            NewResourceInfo newResourceInfo = list.get(0);
                            if(type == NewResourceInfo.TYPE_CLASS_SHOW){
                                createBroadcast(activity,userInfo,courseData,schoolId,0,
                                        newResourceInfo.getId());
                            }else{
                                EventBus.getDefault().post(new MessageEvent("createTeachingPlanNoticeSuccess"));
                                if (activity != null){
                                    //资源发布成功
                                    MediaPaperActivity.setHasResourceSended(true);
                                    activity.finish();
                                }
                            }
                        } else {
                            TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_error);
                        }
                    }
                };

        RequestHelper.sendPostRequest(activity, ServerUrl.UPLOAD_NOTE_CLASS_SPACE_URL,
                params, listener);

    }




    public static void updateNoteToPostBar(final Activity activity, UserInfo userInfo, CourseData courseData, final boolean isShare) {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            TipMsgHelper.ShowLMsg(activity, R.string.pls_login);
            return;
        }
        if (courseData == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("MicroID", courseData.getIdType());
        params.put("Title", courseData.nickname);
        RequestHelper.sendPostRequest(activity, ServerUrl.UPLOAD_NOTE_POSTBAR_URL,
            params, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (jsonString != null) {
                        DataResult result = JSON.parseObject(jsonString, DataResult.class);
                        if (activity != null && !isShare) {
                            if (result != null && result.isSuccess()) {
                                TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_success);
                                //资源发布成功
                                MediaPaperActivity.setHasResourceSended(true);
                                activity.finish();
                            } else {
                                TipMsgHelper.ShowLMsg(activity, R.string.upload_comment_error);
                            }
                        }
                    }
                }
            });
    }

    //add for sync homework to cloud post bar only, not use for others
    private static void updateNoteToPostBar(final Activity activity, String memberId, CourseData courseData) {
        if (courseData == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", memberId);
        params.put("MicroID", courseData.getIdType());
        params.put("Title", courseData.nickname);
        RequestHelper.sendPostRequest(activity, ServerUrl.UPLOAD_NOTE_POSTBAR_URL,
            params, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (jsonString != null) {
                    }
                }
                @Override
                public void onFinish() {
                    super.onFinish();
                    if(activity != null) {
                        activity.finish();
                    }
                }
            });
    }

    public static void deletePostBar(final Activity activity, String id) {
        Map<String, Object> params = new HashMap();
        params.put("PostBarId", id);
        RequestHelper.sendPostRequest(activity, ServerUrl.DELETE_POSTBAR_URL,
            params, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    if (jsonString != null) {
                        DataResult result = JSON.parseObject(jsonString, DataResult.class);
                        if (activity != null) {
                            if (result != null && result.isSuccess()) {
                                TipMsgHelper.ShowLMsg(activity, R.string.delete_success);
                            } else {
                                TipMsgHelper.ShowLMsg(activity, R.string.delete_failure);
                            }
                        }
                    }
                }
            });
    }

//    //此参数用来控制发送到多个学习任务的多个班级 是否发送成功 或者超时
//    public static boolean isSendSuccess = false;
//    public static void publishStudyTask(final Activity activity, final UploadParameter uploadParameter,
//                                        final CourseData courseData, boolean isShowLoading) {
//        Map<String, Object> params = new HashMap<String, Object>();
//        if(uploadParameter != null) {
//            params.put("TaskType", uploadParameter.getTaskType());
//            params.put("TaskCreateId", uploadParameter.getMemberId());
//            params.put("TaskCreateName", uploadParameter.getCreateName());
//            params.put("SchoolClassList", uploadParameter.getShortSchoolClassInfos());
//            params.put("TaskTitle", uploadParameter.getFileName());
//            if(courseData != null) {
//                params.put("ResId", courseData.getIdType());
//                params.put("ResUrl",courseData.resourceurl);
//            } else {
//                params.put("ResId", "");
//                params.put("ResUrl","");
//            }
//            params.put("StartTime", uploadParameter.getStartDate());
//            params.put("EndTime", uploadParameter.getEndDate());
//            params.put("DiscussContent", uploadParameter.getDescription());
//        }
//
//        RequestHelper.RequestModelResultListener listener = new RequestHelper.RequestModelResultListener(activity, DataResult.class) {
//            @Override
//            public void onSuccess(String json) {
//                try {
//                    isSendSuccess = true;
//                    DataResult result = JSON.parseObject(json, DataResult.class);
//                    if (result != null &&result.isSuccess()) {
//                        TipMsgHelper.ShowLMsg(activity, R.string.publish_course_ok);
////                        if(activity != null) {
////                            activity.finish();
////                        }
//                        if(uploadParameter != null) {
//                            updateNoteToPostBar(activity, uploadParameter.getMemberId(), courseData);
//                        }
//                    } else {
//                        TipMsgHelper.ShowLMsg(activity, R.string.publish_course_error);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                if (!isSendSuccess){
//                    if(uploadParameter != null) {
//                        updateNoteToPostBar(activity, uploadParameter.getMemberId(), courseData);
//                    }
//                }
//            }
//        };
//        isSendSuccess = false;
//        listener.setShowLoading(isShowLoading);
//        listener.setShowErrorTips(false);
//        RequestHelper.sendPostRequest(activity, ServerUrl.ADD_STUDY_TASK_URL,params, listener);
//    }

    public static void publishStudyTask(final Activity activity, final UploadParameter uploadParameter,
                                        final CourseData courseData, boolean isShowLoading) {
        final DialogHelper.LoadingDialog loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        List<ShortSchoolClassInfo> schoolClassInfos = uploadParameter.getShortSchoolClassInfos();
        JSONObject taskParams = new JSONObject();
        if (uploadParameter != null) {
            try {
                taskParams.put("TaskType", uploadParameter.getTaskType());
                taskParams.put("TaskCreateId", uploadParameter.getMemberId());
                taskParams.put("TaskCreateName", uploadParameter.getCreateName());
                JSONArray schoolArray = new JSONArray();
                JSONObject schoolObject = null;
                if (schoolClassInfos != null && schoolClassInfos.size() > 0) {
                    for (int i = 0; i < schoolClassInfos.size(); i++) {
                        schoolObject = new JSONObject();
                        ShortSchoolClassInfo info = schoolClassInfos.get(i);
                        schoolObject.put("ClassName", info.getClassName());
                        schoolObject.put("ClassId", info.getClassId());
                        schoolObject.put("SchoolName", info.getSchoolName());
                        schoolObject.put("SchoolId", info.getSchoolId());
                        schoolArray.put(schoolObject);
                    }
                }
                taskParams.put("SchoolClassList", schoolArray);

                taskParams.put("TaskTitle", uploadParameter.getFileName());
                if(courseData != null) {
                    taskParams.put("ResId", courseData.getIdType());
                    taskParams.put("ResUrl",courseData.resourceurl);
                } else {
                    taskParams.put("ResId", "");
                    taskParams.put("ResUrl","");
                }
                taskParams.put("StartTime", uploadParameter.getStartDate());
                taskParams.put("EndTime", uploadParameter.getEndDate());
                taskParams.put("DiscussContent", uploadParameter.getDisContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        RequestHelper.RequestModelResultListener listener = new RequestHelper.RequestModelResultListener(activity, DataResult.class) {
            @Override
            public void onSuccess(String json) {
                if (TextUtils.isEmpty(json)) return;
                try {
                    loadingDialog.dismiss();
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null &&result.isSuccess()) {
                        TipMsgHelper.ShowLMsg(activity, R.string.publish_course_ok);
                        if(uploadParameter != null) {
                            updateNoteToPostBar(activity, uploadParameter.getMemberId(), courseData);
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(activity, R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFinish() {
                super.onFinish();
                loadingDialog.dismiss();
              if(uploadParameter != null) {
                  updateNoteToPostBar(activity, uploadParameter.getMemberId(), courseData);
              }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                loadingDialog.dismiss();
            }
        };
        RequestHelper.postRequest(activity, ServerUrl.ADD_STUDY_TASK_URL,taskParams.toString(), listener);
    }

    //设置已读（通知/秀秀/创意学堂）
    public static void markResourceAsRead(final Activity activity, NewResourceInfo data, String
            memberId, final AdapterViewHelper helper) {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", memberId);
        params.put("Id", data.getId());
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestListener<DataResult>(
                        activity, DataResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (activity == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        NewResourceInfo data = (NewResourceInfo) getTarget();
                        data.setIsRead(true);
                        helper.update();
                    }
                };
        listener.setTarget(data);
        RequestHelper.sendPostRequest(activity,
                ServerUrl.MARK_MY_RESOURCE_AS_READ_URL, params, listener);
    }
}
