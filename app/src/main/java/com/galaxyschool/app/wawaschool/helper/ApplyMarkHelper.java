package com.galaxyschool.app.wawaschool.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckReplaceIPAddressHelper;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.MessageEventConstantUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.SelectedReadingDetailFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseImageListResult;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.ExerciseItem;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MediaData;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.intleducation.module.tutorial.marking.choice.TutorChoiceActivity;
import com.lqwawa.intleducation.module.tutorial.marking.choice.TutorChoiceParams;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.tools.FileZipHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ======================================================
 * Describe: 申请批阅辅助类
 * ======================================================
 */
public class ApplyMarkHelper {

    public static void showApplyMarkView(Context mContext,
                                         TextView textView) {
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(mContext.getString(R.string.str_apply_mark));
            textView.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable
                    .green_10dp_red));
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            textView.setTextSize(14);
            textView.setPadding(DensityUtils.dp2px(mContext, 7),
                    DensityUtils.dp2px(mContext, 2),
                    DensityUtils.dp2px(mContext, 7),
                    DensityUtils.dp2px(mContext, 2));
        }
    }

    public static void doApplyMarkTask(Context mContext,
                                       ExerciseAnswerCardParam cardParam,
                                       ExerciseItem itemData,
                                       List<String> imageList) {
        if (imageList == null) {
            if (itemData == null) {
                return;
            }
            imageList = new ArrayList<>();
            List<MediaData> dataList = itemData.getDatas();
            for (int m = 0; m < dataList.size(); m++) {
                imageList.add(dataList.get(m).resourceurl);
            }
        }
        String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator().generate(imageList.get(0));
        cardParam.setExerciseItem(itemData);
        DoCourseHelper doCourseHelper = new DoCourseHelper(mContext);
        doCourseHelper.doAnswerQuestionCheckMarkData(
                cardParam,
                savePath,
                imageList,
                cardParam.getCommitTaskTitle(),
                cardParam.getScreenType(),
                DoCourseHelper.FromType.Do_Answer_Card_Check_Course);
    }

    public static void loadCourseImageList(Context mContext,
                                           ExerciseAnswerCardParam cardParam,
                                           List<Integer> pageIndex) {
        final HashMap<String, Object> params = new HashMap<>();
        if (TextUtils.isEmpty(cardParam.getResId())) {
            return;
        }
        String resId = cardParam.getResId();
        if (!TextUtils.isEmpty(resId)) {
            params.put("courseId", resId);
        }
        RequestHelper.RequestResourceResultListener listener = new RequestHelper
                .RequestResourceResultListener(mContext, CourseImageListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                CourseImageListResult result = (CourseImageListResult) getResult();
                if (result == null || result.getCode() != 0) {
                    TipsHelper.showToast(mContext, R.string.no_course_images);
                    return;
                }
                List<CourseData> courseDatas = result.getCourse();
                if (courseDatas != null && courseDatas.size() > 0) {
                    CourseData courseData = courseDatas.get(0);
                    if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                        List<String> paths = result.getData();
                        if (paths == null || paths.size() == 0) {

                        } else {
                            List<String> pageList = new ArrayList<>();
                            if (pageIndex != null && pageIndex.size() > 0) {
                                for (int i = 0; i < pageIndex.size(); i++) {
                                    if (pageIndex.get(i) < paths.size()) {
                                        pageList.add(paths.get(pageIndex.get(i)));
                                    }
                                }
                            }
                            if (pageList.size() > 0) {
                                checkCanReplaceIPAddress(mContext,
                                        pageList,
                                        courseData,
                                        cardParam);
                            }
                        }
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendGetRequest(mContext, ServerUrl.COURSE_IMAGES_URL, params, listener);
    }

    /**
     * 校验是否用内网的IP进行下载
     */
    private static void checkCanReplaceIPAddress(Context mContext,
                                                 final List<String> paths,
                                                 CourseData courseData,
                                                 ExerciseAnswerCardParam cardParam) {
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
                        doApplyMarkTask(mContext, cardParam, null, imageUrl);
                    }
                })
                .checkIP();
    }

    public static void enterApplyTeacherMarkActivity(Context mContext,
                                                     CourseData courseData,
                                                     QuestionResourceModel markModel) {
        markModel.setResId(courseData.getIdType());
        markModel.setResUrl(courseData.resourceurl);
        TutorChoiceParams choiceParams = new TutorChoiceParams();
        choiceParams.setMemberId(markModel.getStuMemberId());
        choiceParams.setChapterId(String.valueOf(markModel.getT_ResCourseId()));
        choiceParams.setCourseId(markModel.getT_CourseId());
        choiceParams.setModel(markModel);
        TutorChoiceActivity.show(mContext, choiceParams);
        ((Activity) mContext).finish();
    }

    public static void commitAssistantMarkData(Activity activity,
                                               CourseData courseData,
                                               int assistTaskId,
                                               boolean needFinish) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("MemberId", DemoApplication.getInstance().getMemberId());
        params.put("SubmitRole", MainApplication.isTutorialMode() ? 0 : 1);
        params.put("ResId", courseData.getIdType());
        params.put("ResUrl", courseData.resourceurl);
        params.put("AssistTask_Id", assistTaskId);
        RequestHelper.RequestResourceResultListener listener = new RequestHelper
                .RequestResourceResultListener(activity, ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                JSONObject jsonObject = JSONObject.parseObject(jsonString);
                if (jsonObject != null) {
                    int errorCode = jsonObject.getInteger("ErrorCode");
                    if (errorCode == 0) {
                        TipMsgHelper.ShowMsg(activity,R.string.upload_comment_success);
                        //发送成功
                        if (MainApplication.isTutorialMode()) {
                            EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.UPDATE_LIST_DATA));
                        }
                        if (needFinish) {
                            activity.finish();
                        }
                    }
                }
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(activity, ServerUrl.GET_ADD_ASSIST_REVIEW_BASE_URL, params, listener);
    }

    public  void uploadCourse(Activity activity,
                              String slidePath,
                              String coursePath,
                              int assistantTaskId,
                              boolean needFinish){
        if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
            LocalCourseInfo info = getLocalCourseInfo(activity,coursePath);
            if (info != null) {
                uploadCourse(activity,info,assistantTaskId,needFinish);
            }
        } else if (!TextUtils.isEmpty(slidePath)) {
            //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
            LocalCourseInfo info = getLocalCourseInfo(activity,slidePath);
            if (info != null) {
                uploadCourse(activity,info,assistantTaskId,needFinish);
            }
        }
    }

    private void uploadCourse(Activity activity,
                              final LocalCourseInfo localCourseInfo,
                              int assistantTaskId,
                              boolean needFinish) {
        UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(activity);
            return;
        }
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo,
                localCourseInfo, null, 1);
        if (uploadParameter != null) {
            //增加参数控制上传的资源是否需要拆分
            uploadParameter.setIsNeedSplit(false);
            showLoadingDialog(activity);
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    localCourseInfo.mPath, Utils.TEMP_FOLDER + Utils.getFileNameFromPath
                    (localCourseInfo.mPath) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param, new FileZipHelper.ZipUnzipFileListener() {
                @Override
                public void onFinish(FileZipHelper.ZipUnzipResult result) {
                    // TODO Auto-generated method stub
                    if (result != null && result.mIsOk) {
                        uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                        UploadUtils.uploadResource(activity, uploadParameter, new CallbackListener() {
                            @Override
                            public void onBack(final Object result) {
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dismissLoadingDialog();
                                            if (result != null) {
                                                CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                if (uploadResult.code != 0) {
                                                    TipMsgHelper.ShowLMsg(activity,
                                                            R.string.upload_file_failed);
                                                    return;
                                                }
                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                    final CourseData courseData = uploadResult.data.get(0);
                                                    if (courseData != null) {
                                                        commitAssistantMarkData(activity,
                                                                courseData,assistantTaskId,needFinish);
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
            });
        }
    }

    private  LocalCourseInfo getLocalCourseInfo(Activity activity,String coursePath) {
        LocalCourseInfo result = null;
        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        try {
            LocalCourseDTO localCourseDTO = localCourseDao.getLocalCourseDTOByPath
                    (DemoApplication.getInstance().getMemberId(), coursePath);
            if (localCourseDTO != null) {
                return localCourseDTO.toLocalCourseInfo();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private DialogHelper.LoadingDialog loadingDialog;
    public Dialog showLoadingDialog(Activity activity) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return loadingDialog;
        }
        loadingDialog = DialogHelper.getIt(activity).GetLoadingDialog(0);
        return loadingDialog;
    }

    public void dismissLoadingDialog() {
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

