package com.lqwawa.intleducation.module.learn.tool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.entity.course.CourseResourceEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.TaskUploadBackVo;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.BaseUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/7/31.
 * email:man0fchina@foxmail.com
 */

public class TaskSliderHelper {
    protected static ProgressDialog progressDialog;
    public static OnPlayListListener onPlayListListener = null;
    public static OnTaskSliderListener onTaskSliderListener = null;
    private static OnCommitTaskListener onCommitTaskListener = null;
    public static OnWorkCartListener onWorkCartListener = null;
    public static OnTutorialMarkingListener onTutorialMarkingListener = null;
    public static OnLearnStatisticListener onLearnStatisticListener = null;

    public interface OnPlayListListener {
        Object setPlayListInfo(List<CourseResourceEntity> playListVo);

        Object setActivity(Activity activity);

        void startPlay();

        int getPlayResourceSize();

        void releasePlayResource();

        void showPlayListDialog(Activity activity);

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public interface OnLearnStatisticListener{

        void enterCourseStatisticActivity(@NonNull Activity activity,
                                          @NonNull int courseId,
                                          @NonNull String courseName,
                                          @NonNull String classId,
                                          @NonNull Bundle bundle);

        void enterLearnStatisticActivity(@NonNull Activity activity,
                                         @NonNull int courseId,
                                         @NonNull String courseName,
                                         @NonNull String classId,
                                         @NonNull int roleType,
                                         @NonNull String studentId);
    }

    public interface OnTutorialMarkingListener {

        void enterOnlineSchoolSpaceActivity(@NonNull Context context,
                                            @NonNull String schoolId);

        void enterTutorialHomePager(@NonNull Context context,
                                    @NonNull String tutorMemberId,
                                    @NonNull String tutorName,
                                    String classId);

        void openAssistanceMark(@NonNull Activity activity,
                                @NonNull TaskEntity entity,
                                @NonNull @TutorialRoleType.TutorialRoleRes String roleType);

        void openCourseWareDetails(@NonNull Activity activity, boolean isAudition,
                                   @NonNull String resId, int resType,
                                   @NonNull String resTitle, int screenType,
                                   @NonNull String resourceUrl, @Nullable String resourceThumbnailUrl);

        void skipMyCourseQuestionWork(@NonNull Activity activity);
    }

    public interface OnWorkCartListener {
        // 添加到任务库
        void putResourceToCart(@NonNull ArrayList<SectionResListVo> choiceArray, int taskType);

        // 清除所有任务库的资源内容
        void clearCartResource();

        // 获取任务库的资源数目
        int takeTaskCount();

        // 跳转综合任务
        void enterIntroTaskDetailActivity(@NonNull Activity activity,
                                          @NonNull String schoolId,
                                          @NonNull String classId,
                                          @Nullable Bundle extras);
    }

    public interface OnTaskSliderListener {
        void doExamTask(Activity activity, String resId, int sourceType, String name);

        void viewCourse(Activity activity, String resId, int resType,
                        String schoolId, int sourceType);

        void viewCourse(Activity activity, String resId, int resType,
                        String schoolId, boolean isPublic, int sourceType);

        void viewPdfOrPPT(Activity activity, String resId,
                          int resType, String title, String createId, int sourceType);

        void studyInfoRecord(Activity activity, String sourceCourseResId,
                             TaskUploadBackVo taskUploadBackVo,
                             int resType, int sourceType);

        void checkMarkTaskDetail(Activity activity,
                                 int roleType,
                                 SectionResListVo task,
                                 LqTaskCommitVo studentCommit,
                                 boolean isCheckMark,
                                 int sourceType,
                                 int scoringRule,
                                 boolean isAudition);

        /**
         * 打开学生提交的列表数据
         *
         * @param activity
         * @param exerciseTotalScore 试卷总分
         * @param resId              课件的resId（id-type）
         * @param screenType         课件的方向
         * @param exerciseData       课件的答题卡信息
         * @param taskId             任务的taskId
         * @param roleType           角色信息
         * @param commitTaskTitle    列表任务的title
         * @param isHeadMaster       班主任的角色
         * @param isOnlineHost       小编
         * @param isOnlineReporter   主编
         * @param studentName        列表提交的学生的姓名
         * @param studentId          列表提交色学生studentId
         * @param commitTaskId       列表item的commitTaskId
         */
        void enterExerciseDetailActivity(Activity activity,
                                         String exerciseTotalScore,
                                         String resId,
                                         int screenType,
                                         String exerciseData,
                                         String taskId,
                                         int roleType,
                                         String commitTaskTitle,
                                         boolean isHeadMaster,
                                         boolean isOnlineHost,
                                         boolean isOnlineReporter,
                                         String studentName,
                                         String studentId,
                                         int commitTaskId,
                                         String taskScoreRemark,
                                         @NonNull String courseId,
                                         @NonNull String courseName,
                                         String classId,
                                         String className,
                                         boolean isTutorialPermission);

        /**
         * 做答题卡
         *
         * @param activity
         * @param exerciseString 答题卡信息
         * @param TaskId         任务的taskId
         * @param StudentId      学生studentId(家长传学生studentId)
         * @param courseId       任务的resId(id-type)
         */
        void doExerciseTask(Activity activity,
                            String exerciseString,
                            String TaskId,
                            String StudentId,
                            String courseId,
                            String taskTitle,
                            String schoolId,
                            String schoolName,
                            String classId,
                            String className,
                            String studentName,
                            int commitTaskId,
                            boolean isDoExercise,
                            @NonNull String CourseId,
                            @NonNull String courseName,
                            boolean isTutorialPermission);
    }

    public interface OnCommitTaskListener {
        void onCommitSuccess();
    }

    /**
     * 打开学生提交的列表数据
     *
     * @param activity
     * @param exerciseTotalScore 试卷总分
     * @param resId              课件的resId（id-type）
     * @param screenType         课件的方向
     * @param exerciseData       课件的答题卡信息
     * @param taskId             任务的taskId
     * @param roleType           角色信息
     * @param commitTaskTitle    列表任务的title
     * @param isHeadMaster       班主任的角色
     * @param isOnlineHost       小编
     * @param isOnlineReporter   主编
     * @param studentName        列表提交的学生的姓名
     * @param studentId          列表提交色学生studentId
     * @param commitTaskId       列表item的commitTaskId
     */
    public static void enterExerciseDetailActivity(Activity activity,
                                                   String exerciseTotalScore,
                                                   String resId,
                                                   int screenType,
                                                   String exerciseData,
                                                   String taskId,
                                                   int roleType,
                                                   String commitTaskTitle,
                                                   boolean isHeadMaster,
                                                   boolean isOnlineHost,
                                                   boolean isOnlineReporter,
                                                   String studentName,
                                                   String studentId,
                                                   int commitTaskId,
                                                   String taskScoreRemark,
                                                   @NonNull String courseId,
                                                   @NonNull String courseName,
                                                   String classId,
                                                   String className,
                                                   boolean isTutorialPermission) {
        if (!ButtonUtils.isFastDoubleClick()) {
            if (onTaskSliderListener != null && activity != null) {
                onTaskSliderListener.enterExerciseDetailActivity(activity,
                        exerciseTotalScore,
                        resId,
                        screenType,
                        exerciseData,
                        taskId,
                        roleType,
                        commitTaskTitle,
                        isHeadMaster,
                        isOnlineHost,
                        isOnlineReporter,
                        studentName,
                        studentId,
                        commitTaskId,
                        taskScoreRemark,
                        courseId,
                        courseName,
                        classId,
                        className,
                        isTutorialPermission);
            }
        }
    }

    /**
     * 做答题卡
     *
     * @param activity
     * @param exerciseString 答题卡信息
     * @param TaskId         任务的taskId
     * @param StudentId      学生studentId(家长传学生studentId)
     * @param courseId       任务的resId(id-type)
     */
    public static void doExerciseTask(Activity activity,
                                      String exerciseString,
                                      String TaskId,
                                      String StudentId,
                                      String courseId,
                                      String taskTitle,
                                      String schoolId,
                                      String schoolName,
                                      String classId,
                                      String className,
                                      String studentName,
                                      int commitTaskId,
                                      boolean isDoExercise,
                                      @NonNull String CourseId,
                                      @NonNull String courseName,
                                      boolean isTutorialPermission) {
        if (!ButtonUtils.isFastDoubleClick()) {
            if (onTaskSliderListener != null && activity != null) {
                onTaskSliderListener.doExerciseTask(
                        activity,
                        exerciseString,
                        TaskId,
                        StudentId,
                        courseId,
                        taskTitle,
                        schoolId,
                        schoolName,
                        classId,
                        className,
                        studentName,
                        commitTaskId,
                        isDoExercise,
                        CourseId,
                        courseName,
                        isTutorialPermission);
            }
        }
    }

    public static void doTask(Activity activity, String resId, int sourceType, String name) {
        if (!ButtonUtils.isFastDoubleClick()) {
            if (onTaskSliderListener != null && activity != null) {
                onTaskSliderListener.doExamTask(activity, resId, sourceType, name);
            }
        }
    }

    public static String zipFilePath;
    public static String taskPaperId;
    public static Activity activity;

    public static void commitTask(Activity activity, String mOpenSlidePath, String taskPaperId,
                                  OnCommitTaskListener listener, final int sourceType) {
        TaskSliderHelper.activity = activity;
        progressDialog = new ProgressDialog(activity);
        TaskSliderHelper.taskPaperId = taskPaperId;
        onCommitTaskListener = listener;
        FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                mOpenSlidePath,
                Utils.TEMP_FOLDER + Utils.getFileNameFromPath(mOpenSlidePath) + Utils.COURSE_SUFFIX);
        FileZipHelper.zip(param,
                new FileZipHelper.ZipUnzipFileListener() {
                    @Override
                    public void onFinish(FileZipHelper.ZipUnzipResult result) {
                        // TODO Auto-generated method stub
                        if (result != null && result.mIsOk) {
                            zipFilePath = result.mParam.mOutputPath;
                            uploadTaskFile(zipFilePath, sourceType);
                        }
                    }
                });
    }

    private static void uploadTaskFile(String filePath, final int sourceType) {
        RequestVo requestVo = new RequestVo();
        String fileName = BaseUtils.getFileNameFromPath(filePath);
        requestVo.addParams("fileName", fileName);
        if (fileName.contains(".")) {
            requestVo.addParams("nickName", fileName.substring(0, fileName.indexOf(".")));
        } else {
            requestVo.addParams("nickName", fileName);
        }
        requestVo.addParams("createName", UserHelper.getUserName());
        requestVo.addParams("memberId", UserHelper.getUserId());
        ;
        requestVo.addParams("account", UserHelper.getLastAccount());
        requestVo.addParams("courseType", 235);
        requestVo.addParams("resType", 18);
        requestVo.addParams("type", 5);
        requestVo.addParams("colType", 1);
        requestVo.addParams("screenType", activity.getIntent().getIntExtra("orientation",
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));
        requestVo.addParams("description", "");
        requestVo.addParams("size", 0);
        requestVo.addParams("totalTime", 0);
        requestVo.addParams("message", "");
        requestVo.addParams("point", "");

        String paramString = requestVo.getParamsWithoutToken();
        try {
            paramString = URLEncoder.encode(paramString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.uploadAndCreate + paramString);
        params.setConnectTimeout(60 * 10000);
        params.addBodyParameter("file", new File(filePath));
        params.setMultipart(true);
        showProgressDialog(activity.getResources().getString(R.string.uploading));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException e) {
                closeProgressDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                try {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.upload_failed) + ":"
                                    + throwable.getMessage());
                } catch (Exception ex) {

                }
            }

            @Override
            public void onFinished() {
                closeProgressDialog();
            }

            @Override
            public void onSuccess(String s) {
                ResponseVo<List<TaskUploadBackVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<TaskUploadBackVo>>>() {
                        });
                if (result.getCode() == 0) {
                    if (result.getData() != null && result.getData().size() > 0)
                        commitTaskInfo(result.getData().get(0), sourceType);
                    return;
                }
                closeProgressDialog();
                ToastUtil.showToast(activity,
                        result.getMessage());

            }
        });
    }

    public static void commitTaskInfo(final TaskUploadBackVo taskUploadBackVo, final int sourceType) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("cexamId", taskPaperId);
        requestVo.addParams("taskId", taskUploadBackVo.getId());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.userTaskSave + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException e) {
                closeProgressDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                try {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.upload_failed) + ":"
                                    + throwable.getMessage());
                } catch (Exception ex) {

                }
            }

            @Override
            public void onFinished() {
                closeProgressDialog();
            }

            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ToastUtil.showToast(activity, activity.getResources().getString(R.string.commit)
                            + activity.getResources().getString(R.string.success));
                    activity.setResult(Activity.RESULT_OK);
                    activity.sendBroadcast(new Intent().setAction(AppConfig.ServerUrl.userExamSave));
                    if (onCommitTaskListener != null) {
                        onCommitTaskListener.onCommitSuccess();
                    }
                    if (TaskSliderHelper.onTaskSliderListener != null) {
                        TaskSliderHelper.onTaskSliderListener.studyInfoRecord(
                                activity, "",
                                taskUploadBackVo, 23, sourceType);
                    }
                } else {
                    ToastUtil.showToast(activity,
                            result.getMessage());
                }
            }
        });
    }

    /**
     * 显示提示框
     */
    protected static void showProgressDialog(final String msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(activity);
                }
                progressDialog.setMessage(msg);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        });
    }

    /**
     * 关闭提示框
     */
    protected static void closeProgressDialog() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }
}
