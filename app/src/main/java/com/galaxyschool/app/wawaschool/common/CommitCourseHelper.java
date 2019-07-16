package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.SlideActivityNew;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.net.course.UploadCourseManager;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitle;
import com.galaxyschool.app.wawaschool.pojo.ResourceTitleResult;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.InputBoxDialog;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.oosic.apps.share.ShareItem;
import com.oosic.apps.share.ShareType;
import com.osastudio.common.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchao on 3/31/16.
 */
public class CommitCourseHelper extends CommitHelper {
    private DialogHelper.LoadingDialog loadingDialog;
    private boolean isLocal;
    private boolean isStudent;
    private Activity activity;
    private boolean isTempData;
    private boolean hiddenGenESchool = false;//隐藏创e学堂

    private int courseTypeFrom = -1;

    public CommitCourseHelper(Activity activity) {
        super(activity);
        this.activity = activity;
    }
    public void setCourseTypeFrom(int courseTypeFrom) {
        this.courseTypeFrom = courseTypeFrom;
    }
    public boolean isTempData() {
        return isTempData;
    }

    public void setIsTempData(boolean tempData) {
        isTempData = tempData;
    }

    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }

    public void setIsStudent(boolean isStudent) {
        this.isStudent = isStudent;
    }

    public void setHiddenGenESchool(boolean hiddenGenESchool) {
        this.hiddenGenESchool = hiddenGenESchool;
    }

    @Override
    protected void initShareItems() {
        shareItems = new ArrayList<ShareItem>();
        if (isLocal) {
            //个人资源库
            shareItems.add(new ShareItem(R.string.cloud, R.drawable.pub_public_personal_resource_library_ico,
                    ShareType.SHARE_TYPE_CLOUD_COURSE));
        }
        if(isStudent) {
            //学习任务
            shareItems.add(new ShareItem(R.string.learning_tasks, R.drawable.pub_homework_ico, ShareType
                    .SHARE_TYPE_STUDY_TASK));
        }
        //控制创e学堂是否显示
        if (!hiddenGenESchool) {
            //创意学堂
            shareItems.add(new ShareItem(R.string.lectures, R.drawable.pub_eclassrom_ico, ShareType
                    .SHARE_TYPE_CLASSROOM));
        }
        if(isTeacher) {
            //校本资源库
            shareItems.add(new ShareItem(R.string.public_course, R.drawable.pub_public_course_ico, ShareType.SHARE_TYPE_PUBLIC_COURSE));
        }
        shareItems.add(new ShareItem(R.string.wechat_friends, R.drawable.umeng_share_wechat_btn, ShareType.SHARE_TYPE_WECHAT));
        shareItems.add(new ShareItem(R.string.wxcircle, R.drawable.umeng_share_wxcircle_btn, ShareType
            .SHARE_TYPE_WECHATMOMENTS));
        shareItems.add(new ShareItem(R.string.qq_friends, R.drawable.umeng_share_qq_btn, ShareType.SHARE_TYPE_QQ));
        shareItems.add(new ShareItem(R.string.qzone, R.drawable.umeng_share_qzone_btn, ShareType.SHARE_TYPE_QZONE));
//        shareItems.add(new ShareItem(R.string.wawachat, R.drawable.umeng_share_wawachat_btn, ShareType.SHARE_TYPE_CONTACTS));
    }

    public void uploadCourse(UserInfo userInfo, LocalCourseInfo localCourseInfo, CourseData courseData, int shareType) {
        String dialogTitle = getDialogTitle(shareType);
        String courseName = null;
        if (localCourseInfo != null) {
            courseName = localCourseInfo.mTitle;
        }

        UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, localCourseInfo, null, shareType);
        if (uploadParameter == null && courseData != null) {
            courseName = courseData.nickname;
            uploadParameter = new UploadParameter();
            uploadParameter.setType(courseData.type);
            uploadParameter.setMemberId(userInfo.getMemberId());
            uploadParameter.setCourseData(courseData);
            uploadParameter.setType(shareType);
        }
        if (uploadParameter != null) {
            if (courseData != null) {
                uploadParameter.setCourseData(courseData);
                publishByShareType(uploadParameter, courseName, shareType);
            } else {
                if(localCourseInfo != null) {
                    LocalCourseDTO dto = localCourseInfo.toLocalCourseDTO();
                    uploadParameter.setLocalCourseDTO(dto);
                }
                String filePath = uploadParameter.getFilePath();
                if(!TextUtils.isEmpty(filePath)) {
                     if(isUploading(filePath)) {
                         TipMsgHelper.ShowLMsg(activity, R.string.uploading_file);
                     } else {
                         if (courseTypeFrom == SlideActivityNew.CourseTypeFrom.FROMLQCOURSE){
                            //来自lq云板新建的发送
                             publishByShareType(uploadParameter, courseName, shareType);
                         }else {
                             //如果是分享到第三方不弹起编辑标题的情况
                             switch (shareType){
                                 case ShareType.SHARE_TYPE_WECHAT:
                                 case ShareType.SHARE_TYPE_WECHATMOMENTS:
                                 case ShareType.SHARE_TYPE_QQ:
                                 case ShareType.SHARE_TYPE_QZONE:
                                 case ShareType.SHARE_TYPE_CONTACTS:
                                     publishByShareType(uploadParameter, courseName, shareType);
                                     break;
                                 default:
                                     //备注:lq云板课件 发送时弹出面板重新编辑标题 其他的地方发送时不弹面板 isTempData就是
                                     //相应的标识
                                     if (isTempData){
                                         publishByShareType(uploadParameter, courseName, shareType);
                                     }else {
                                         showPublishDialog(uploadParameter, dialogTitle, courseName, shareType);
                                     }
                                     break;
                             }

                         }
                     }
                }
            }
        }
    }

    private boolean isUploading(String filePath) {
        boolean isUploading = false;
        UploadCourseManager uploadManager = UploadCourseManager
            .getDefault(activity);
        if (uploadManager != null) {
            isUploading = uploadManager.isUploading(filePath);
        }
        return isUploading;
    }

    public void showPublishDialog(final UploadParameter uploadParameter, String dialogTitle, final String dataTitle, final int shareType) {
        showEditDialog(dialogTitle, dataTitle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = ((InputBoxDialog) dialog).getInputText().trim();
                //标题增加空判断
                if (TextUtils.isEmpty(title)){
                    TipsHelper.showToast(mContext,mContext.getString(R.string.pls_input_title));
                    return;
                }
                //过滤特殊字符
                boolean isValid = Utils.checkEditTextValid(mContext,title);
                if (!isValid){
                    return;
                }
                if (title.length() > Constants.MAX_TITLE_LENGTH){
                    TipMsgHelper.ShowLMsg(mContext,mContext.getString(
                            R.string.words_count_over_limit));
                    return;
                }
                dialog.dismiss();
                publishByShareType(uploadParameter, title, shareType);
//                if(uploadParameter.getLocalCourseDTO() != null)  {
//                    if(uploadParameter.getLocalCourseDTO().getmMicroId() > 0) {
//                            publishByShareType(uploadParameter, title, shareType);
//                    } else {
//                        checkResourceTitle(uploadParameter, title, shareType);
//                    }
//                } else {
//                    publishByShareType(uploadParameter, title, shareType);
//                }

            }
        }).show();
    }

    private void checkResourceTitle(final UploadParameter uploadParameter, final String title,
                                    final int shareType) {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", uploadParameter.getMemberId());
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(MediaType.MICROCOURSE));
        builder.append(",");
        builder.append(String.valueOf(MediaType.ONE_PAGE));
        params.put("MTypes", builder.toString());
        List<String> titles = new ArrayList<String>();
        titles.add(title);
        params.put("Title", titles);
        RequestHelper.RequestDataResultListener listener = new RequestHelper.RequestDataResultListener<ResourceTitleResult>(
                activity, ResourceTitleResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                ResourceTitleResult result = getResult();
                if (result == null || result.getModel() == null || !result.isSuccess()) {
                    return;
                }
                ResourceTitle resourceTitle = result.getModel().getData();
                List<String> titleList;
                if (resourceTitle != null) {
                    titleList = resourceTitle.getTitle();
                    if (titleList != null && titleList.size() > 0) {
                        StringBuilder builder = new StringBuilder();
                        int size = titleList.size();
                        String title;
                        for (int i = 0; i < size - 1; i++) {
                            title = titleList.get(i);
                            builder.append(title + ",");
                        }
                        title = titleList.get(size - 1);
                        builder.append(title);
                        showUploadInfoDialog(builder.toString());
                    }
                } else {
                    publishByShareType(uploadParameter, title, shareType);
                }
            }
        };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(activity, ServerUrl.CHECK_CLOUD_RESOURCE_TITLE_URL,
                params, listener);
    }


    private void showUploadInfoDialog(String info) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(activity, null, activity
                .getString(R.string.cloud_resource_upload_exist, info),
                activity.getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },  "",  null);
        dialog.show();
    }

    void publishByShareType(UploadParameter uploadParameter, String title, final int shareType) {
        uploadParameter.setTempData(isTempData);
        uploadParameter.setIsLocal(isLocal);
        if (shareType == ShareType.SHARE_TYPE_CLOUD_COURSE) {
            publishToPersonal(uploadParameter, title, shareType);
        } else if (shareType == ShareType.SHARE_TYPE_CLASSROOM
            || shareType == ShareType.SHARE_TYPE_PICTUREBOOK
            || shareType == ShareType.SHARE_TYPE_PUBLIC_COURSE) {
            enterContactsPicker(uploadParameter, title, null, shareType);
        } else if (shareType == ShareType.SHARE_TYPE_STUDY_TASK) {
            if(uploadParameter != null && !TextUtils.isEmpty(title)) {
                uploadParameter.setFileName(title);
            }
            UploadUtils.enterContactsPicker(mContext, uploadParameter);
        }else {
           final String filePath = uploadParameter.getFilePath();
            switch (shareType) {
                case ShareType.SHARE_TYPE_WECHAT:
                case ShareType.SHARE_TYPE_WECHATMOMENTS:
                case ShareType.SHARE_TYPE_QQ:
                case ShareType.SHARE_TYPE_QZONE:
                case ShareType.SHARE_TYPE_CONTACTS:
                    if (uploadParameter != null) {
                        showLoadingDialog();
                        UploadUtils.uploadResource(mContext, uploadParameter, new CallbackListener() {
                            @Override
                            public void onBack(Object result) {
                                final CourseUploadResult courseUploadResult = (CourseUploadResult) result;
                                if (mContext != null) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            if (courseUploadResult != null) {
                                                List<CourseData> courseDatas = courseUploadResult.data;
                                                if (courseDatas != null && courseDatas.size() > 0) {
                                                    final CourseData data = courseDatas.get(0);
                                                    if (data != null) {
                                                        MediaListFragment.updateMedia(mContext,
                                                                DemoApplication.getInstance().getUserInfo(),
                                                                data.getShortCourseInfoList(),
                                                                MediaType.MICROCOURSE, new CallbackListener() {
                                                                    @Override
                                                                    public void onBack(Object result) {
                                                                        if (isTempData) {
                                                                            setLocalCourseShare(true);
                                                                            setBackFilePath(filePath);
                                                                        }
                                                                        shareTo(shareType, data.getShareInfo(mContext));
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    break;
            }
            }
        }

    void publishToPersonal(UploadParameter uploadParameter, final String dataTitle, final int shareType) {
        if (uploadParameter == null) {
            return;
        }

        if (uploadParameter != null && !TextUtils.isEmpty(dataTitle)) {
            uploadParameter.setFileName(dataTitle);
        }

        if (uploadParameter != null) {
            new UploadCourseHelper(mContext).uploadResource(uploadParameter, shareType);
            if (uploadParameter.isTempData()){
                Intent intent =new Intent();
                intent.putExtra(SlideManagerHornForPhone.SAVE_PATH, uploadParameter.getFilePath());
                mContext.setResult(mContext.RESULT_OK, intent);
                mContext.finish();
            }
        }
    }

    public void enterContactsPicker(UploadParameter uploadParameter, String dataTitle, String dataDesc,
                              int shareType) {
        if (uploadParameter == null) {
            return;
        }
        
        Bundle args = new Bundle();
        args.putString("Title", dataTitle);
        args.putString("Content", dataDesc);
        args.putInt("TargetType", shareType);
        if(uploadParameter != null && !TextUtils.isEmpty(dataTitle)) {
            uploadParameter.setFileName(dataTitle);
        }
        args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        args.putBoolean(ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER, true);
        args.putInt(
            ContactsPickerActivity.EXTRA_PICKER_TYPE, ContactsPickerActivity.PICKER_TYPE_GROUP);
        if (shareType == ShareType.SHARE_TYPE_CLASSROOM) {
            args.putInt(ContactsPickerActivity.EXTRA_GROUP_TYPE,
                ContactsPickerActivity.GROUP_TYPE_CLASS);
        } else if (shareType == ShareType.SHARE_TYPE_PICTUREBOOK ||
            shareType == ShareType.SHARE_TYPE_PUBLIC_COURSE) {
            args.putInt(ContactsPickerActivity.EXTRA_GROUP_TYPE,
                ContactsPickerActivity.GROUP_TYPE_SCHOOL);
            args.putInt(ContactsPickerActivity.EXTRA_ROLE_TYPE,
                    ContactsPickerActivity.ROLE_TYPE_TEACHER);
        }
        args.putInt(
            ContactsPickerActivity.EXTRA_PICKER_MODE,
            ContactsPickerActivity.PICKER_MODE_SINGLE);
        args.putString(ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT, mContext.getString(R.string.send));
        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        args.putBoolean(ContactsPickerActivity.EXTRA_PUBLISH_RESOURCE, true);

        args.putBoolean(ContactsPickerActivity.EXTRA_PICKER_SUPERUSER, true);
        Intent intent = new Intent(mContext, ContactsPickerActivity.class);
        intent.putExtras(args);
        //用于lq云板里面来回接数据
        if (uploadParameter.isTempData()){
            mContext.startActivityForResult(intent,ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
        }else {
            mContext.startActivity(intent);
        }
    }

    private String getDialogTitle(int shareType) {
        String title = null;
        switch (shareType) {
            case ShareType.SHARE_TYPE_CLOUD_COURSE:
                title = mContext.getString(R.string.send_to_sp, mContext.getString(R.string.personal_cloudspace));
                break;
            case ShareType.SHARE_TYPE_CLASSROOM:
                title = mContext.getString(R.string.send_to_sp, mContext.getString(R.string.lectures));
                break;
            case ShareType.SHARE_TYPE_PICTUREBOOK:
                title = mContext.getString(R.string.send_to_sp, mContext.getString(R.string.picturebook));
                break;
            case ShareType.SHARE_TYPE_PUBLIC_COURSE:
                title = mContext.getString(R.string.send_to_sp, mContext.getString(R.string.public_course));
                break;
            case ShareType.SHARE_TYPE_STUDY_TASK:
                title = mContext.getString(R.string.send_to_sp, mContext.getString(R.string
                        .learning_tasks));
                break;
        }
        return title;
    }

//    ContactsInputDialog showEditDialog(String dialogTitle, String dataTitle,
//                                          DialogInterface.OnClickListener confirmButtonClickListener) {
//        ContactsInputDialog dialog = new ContactsInputDialog(mContext, dialogTitle, dataTitle,
//            mContext.getString(R.string.pls_enter_title),
//                mContext.getString(R.string.cancel),
//                new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }, mContext.getString(R.string.confirm), confirmButtonClickListener);
//        dialog.show();
//        return dialog;
//    }

    /**
     * 支持过滤表情
     * @param dialogTitle
     * @param dataTitle
     * @param confirmButtonClickListener
     * @return
     */
    private InputBoxDialog showEditDialog(
            String dialogTitle, String dataTitle,
            DialogInterface.OnClickListener confirmButtonClickListener) {
        InputBoxDialog dialog = new InputBoxDialog(mContext, dialogTitle, dataTitle,
                mContext.getString(R.string.pls_enter_title),
                mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, mContext.getString(R.string.confirm), confirmButtonClickListener);
        //设置不自动消失
        dialog.setIsAutoDismiss(false);
        dialog.show();
        return dialog;
    }
    public Dialog showLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return loadingDialog;
        }
        loadingDialog = DialogHelper.getIt(mContext).GetLoadingDialog(0);
        return loadingDialog;
    }
    public void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
