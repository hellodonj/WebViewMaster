package com.lqwawa.mooc.common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.AnswerCardDetailActivity;
import com.galaxyschool.app.wawaschool.CheckMarkActivity;
import com.galaxyschool.app.wawaschool.CommonFragmentActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.StudyInfoRecordUtil;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.DownloadOnePageTask;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.CheckMarkFragment;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.helper.DoTaskOrderHelper;
import com.galaxyschool.app.wawaschool.helper.LqIntroTaskHelper;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkResult;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfoCode;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.TaskMarkParam;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.libs.gallery.ImageInfo;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.TaskUploadBackVo;
import com.lqwawa.intleducation.module.tutorial.marking.choice.QuestionResourceModel;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.osastudio.common.utils.TimerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XChen on 2017/7/31.
 * email:man0fchina@foxmail.com
 */

public class MOOCHelper {

    protected static ProgressDialog progressDialog;

    public static void init(UserInfo userInfo) {
        TaskSliderHelper.onTaskSliderListener = onTaskSliderListener;
        TaskSliderHelper.onWorkCartListener = onWorkCartListener;
        TaskSliderHelper.onTutorialMarkingListener = onTutorialMarkingListener;
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setUserId(userInfo.getMemberId());
        userInfoVo.setAccount(userInfo.getNickName());
        userInfoVo.setUserName(StringUtils.isValidString(userInfo.getRealName())
                ? userInfo.getRealName() : userInfo.getNickName());
        userInfoVo.setThumbnail(userInfo.getHeaderPic());
        userInfoVo.setRoles(userInfo.getRoles());
        userInfoVo.setSchoolIds(getSchoolsFromUserInfo(userInfo));
        UserHelper.setUserInfo(userInfoVo);
    }

    private static String getSchoolsFromUserInfo(UserInfo info) {
        String schoolIds = "";
        if (info != null) {
            if (info.getSchoolList() == null) {
                info.setSchoolList(MyApplication.getSchoolInfoList(MyApplication.getInstance()));
            }
            if (info.getSchoolList() != null) {
                if (info.getSchoolList().size() > 0) {
                    for (int i = 0; i < info.getSchoolList().size(); i++) {
                        schoolIds += info.getSchoolList().get(i).getSchoolId();
                        if (i < info.getSchoolList().size() - 1) {
                            schoolIds += ",";
                        }
                    }
                }
            }
        }
        return schoolIds;
    }

    private static TaskSliderHelper.OnTutorialMarkingListener onTutorialMarkingListener
            = new TaskSliderHelper.OnTutorialMarkingListener(){
        @Override
        public void openAssistanceMark(@NonNull Activity activity, @NonNull TaskEntity entity) {
            enterAssistanceMarkActivity(activity,entity);
        }

        @Override
        public void openCourseWareDetails(@NonNull Activity activity,boolean isAudition,
                                          @NonNull String resId, int resType,
                                          @NonNull String resTitle, int screenType,
                                          @NonNull String resourceUrl, @Nullable String resourceThumbnailUrl){
            if (resType == 23) {
                NewResourceInfo newResourceInfo = new NewResourceInfo();
            /*newResourceInfo.setTitle(sectionResListVo.getName());
            newResourceInfo.setResourceId(sectionResListVo.getResId() + "-" + sectionResListVo.getResType());
            newResourceInfo.setMicroId(sectionResListVo.getResId());
            newResourceInfo.setScreenType(sectionResListVo.getScreenType());
            newResourceInfo.setResourceUrl(sectionResListVo.getResourceUrl());*/

                newResourceInfo.setTitle(resTitle);
                if (resType == -1) {
                    newResourceInfo.setResourceId(resId);
                } else {
                    newResourceInfo.setResourceId(resId + "-" + resType);
                }
                newResourceInfo.setMicroId(resId);
                newResourceInfo.setScreenType(screenType);
                newResourceInfo.setResourceUrl(resourceUrl);

                newResourceInfo.setIsFromAirClass(true);
                newResourceInfo.setIsFromSchoolResource(true);
                newResourceInfo.setCollectionOrigin(activity.getIntent().getStringExtra("schoolId"));
                PassParamhelper mParam = new PassParamhelper();
                mParam.isFromLQMOOC = true;
                mParam.isAudition = isAudition;
                ActivityUtils.enterTaskOrderDetailActivity(activity, newResourceInfo, mParam);
            } else {
                UserInfo userInfo =
                        ((MyApplication) (MainApplication.getInstance())).getUserInfo();
                StudyTask task = new StudyTask();
            /*task.setResId(sectionResListVo.getResId() + "-" + sectionResListVo.getResType());
            task.setResUrl(*//*data.getResourcePath()*//* "");
            task.setResThumbnailUrl(sectionTaskDetailsVo.getOrigin().getThumbnail());
            task.setTaskTitle(sectionResListVo.getOriginName());*/

                task.setTaskTitle(resTitle);
                if (resType == -1) {
                    task.setResId(resId);
                } else {
                    task.setResId(resId + "-" + resType);
                }
                task.setResThumbnailUrl(resourceThumbnailUrl);
                task.setResUrl(resourceUrl);

                task.setCollectSchoolId(activity.getIntent().getStringExtra("schoolId"));
                PassParamhelper mParam = new PassParamhelper();
                mParam.isFromLQMOOC = true;
                mParam.isAudition = isAudition;
                Bundle bundle = new Bundle();
                bundle.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
                CourseOpenUtils.openCourseDetailsDirectly(activity, task,
                        TextUtils.equals(UserHelper.getUserId(),
                                activity.getIntent().getStringExtra("memberId"))
                                ? RoleType.ROLE_TYPE_STUDENT : RoleType.ROLE_TYPE_PARENT,
                        userInfo.getMemberId(),
                        activity.getIntent().getStringExtra("memberId"),
                        userInfo, true, bundle);
            }
        }

        /**
         * 进入申请批阅详情
         * @param activity 上下文
         * @param taskEntity 列表的对象
         */
        public void enterAssistanceMarkActivity(Activity activity,
                                                TaskEntity taskEntity){
            if (activity == null || taskEntity == null) {
                return;
            }
            CommitTask commitTask = new CommitTask();
            commitTask.setIsAssistantMark(true);
            commitTask.setStudentResId(taskEntity.getResId());
            commitTask.setStudentResThumbnailUrl(taskEntity.getResThumbnailUrl());
            commitTask.setStudentResTitle(taskEntity.getTitle());
            commitTask.setId(taskEntity.getId());
            commitTask.setStudentId(taskEntity.getStuMemberId());
            commitTask.setStudentResTitle(taskEntity.getTitle());
            commitTask.setAirClassId(taskEntity.getT_AirClassId());
            commitTask.setTaskId(taskEntity.getT_TaskId());
            if (!TextUtils.isEmpty(taskEntity.getT_EQId())) {
                commitTask.setEQId(Integer.valueOf(taskEntity.getT_EQId()));
            }
            commitTask.setCommitTaskId(taskEntity.getT_CommitTaskId());
            commitTask.setCommitTaskOnlineId(taskEntity.getT_CommitTaskOnlineId());
            StudyTask studyTask = new StudyTask();
            studyTask.setType(taskEntity.getT_TaskType());
            studyTask.setClassId(taskEntity.getT_ClassId());
            studyTask.setClassName(taskEntity.getT_ClassName());
            studyTask.setCourseId(String.valueOf(taskEntity.getT_CourseId()));
            studyTask.setCourseName(taskEntity.getT_CourseName());
            studyTask.setResCourseId(taskEntity.getT_ResCourseId());
            CheckMarkActivity.start(activity,commitTask,studyTask);
        }
    };

    private static TaskSliderHelper.OnWorkCartListener onWorkCartListener
            = new TaskSliderHelper.OnWorkCartListener() {
        @Override
        public void putResourceToCart(@NonNull ArrayList<SectionResListVo> choiceArray, int taskType) {
            LqIntroTaskHelper.getInstance().addTask(choiceArray,taskType);
        }

        @Override
        public void clearCartResource() {
            LqIntroTaskHelper.getInstance().clearTaskList();
        }

        @Override
        public int takeTaskCount() {
            return LqIntroTaskHelper.getInstance().getTaskCount();
        }

        @Override
        public void enterIntroTaskDetailActivity(@NonNull Activity activity, @NonNull String schoolId, @NonNull String classId) {
            LqIntroTaskHelper.getInstance().enterIntroTaskDetailActivity(activity,schoolId,classId);
        }
    };

    private static TaskSliderHelper.OnTaskSliderListener onTaskSliderListener
            = new TaskSliderHelper.OnTaskSliderListener() {
        @Override
        public void doExamTask(Activity activity, String resId, int sourceType) {
            UIUtils.currentSourceFromType = sourceType;
            doTask(activity, resId);
        }

        @Override
        public void viewCourse(Activity activity, String resId, int resType,
                               String schoolId, int sourceType) {
            UIUtils.currentSourceFromType = sourceType;
            openCourse(activity, resId, resType, schoolId, false);
        }

        @Override
        public void viewCourse(Activity activity, String resId, int resType,
                               String schoolId, boolean isPublic, int sourceType) {
            UIUtils.currentSourceFromType = sourceType;
            openCourse(activity, resId, resType, schoolId, isPublic);
        }

        @Override
        public void viewPdfOrPPT(Activity activity, String resId,
                                 int resType, String title, String createId, int sourceType) {
            UIUtils.currentSourceFromType = sourceType;
            openPptOrPdf(activity, resId, resType, title, createId);
        }

        private void openCourse(Activity activity, String resId,
                                int resType, String schoolId, boolean isPublicRes) {
            if (StringUtils.isValidString(resId)) {
                if (!resId.contains("-") && resType >= 10000) {
                    CourseOpenUtils.openCourseDirectly(activity, resId + "-" + resType, isPublicRes,
                            schoolId, true);
                } else {
                    CourseOpenUtils.openCourseDirectly(activity, resId, isPublicRes, schoolId, true);
                }
            }
        }


        /**
         * @param activity 上下文
         * @param roleType 角色信息
         * roleType == ROLE_TYPE_TEACHER [小编 或者 任务的创建着]
         * roleType == ROLE_TYPE_EDITOR [主编]
         * roleType == ROLE_TYPE_STUDENT [学生身份]
         * roleType == ROLE_TYPE_VISITOR [无权限对该任务进行操作 俗称 游客]
         *
         * @param task 任务对象
         * @param studentCommit 学生提交的任务
         * @param isCheckMark 是不是查看批阅 （查看批阅[true] 查看item进入提问和批阅的详情[false]）
         * @param sourceType 资源type
         * @param scoringRule 打分标准 1 十分制，2百分制
         * @param isAudition 是不是试听
         */
        @Override
        public void checkMarkTaskDetail(Activity activity,
                                        int roleType,
                                        SectionResListVo task,
                                        LqTaskCommitVo studentCommit,
                                        boolean isCheckMark,
                                        int sourceType,
                                        int scoringRule,
                                        boolean isAudition) {
            if (activity == null || task == null || studentCommit == null) {
                return;
            }
            UIUtils.currentSourceFromType = sourceType;
            //备注 这里涉及两者对象数据的转化
            CommitTask data = new CommitTask();
            data.setCommitTaskId(studentCommit.getCommitTaskId());
            // 新版本用Id
            data.setCommitTaskId(studentCommit.getId());
            data.setCommitTime(studentCommit.getCommitTime());
            data.setCreateId(studentCommit.getCreateId());
            data.setCreateName(studentCommit.getCreateName());
            data.setDeleted(studentCommit.isDeleted());
            data.setHeadPicUrl(studentCommit.getHeadPicUrl());
            data.setId(studentCommit.getId());
            data.setIsRead(studentCommit.isIsRead());
            data.setModifyTimes(studentCommit.getModifyTimes());
            data.setScore(studentCommit.getScore());
            data.setReadTime(studentCommit.getReadTime());
            data.setCorrectResult(studentCommit.getCorrectResult());
            data.setStudentId(studentCommit.getStudentId());
            data.setStudentName(studentCommit.getStudentName());
            data.setStudentResId(studentCommit.getStudentResId());
            data.setStudentResTitle(studentCommit.getStudentResTitle());
            data.setTaskState(studentCommit.getTaskState());
            data.setTeacherResId(studentCommit.getTeacherResId());
            data.setUpdateId(studentCommit.getUpdateId());
            data.setUpdateName(studentCommit.getUpdateName());
            data.setHasCommitTaskReview(studentCommit.isHasCommitTaskReview());
            data.setStudentResUrl(studentCommit.getStudentResUrl());
            data.setTaskScore(studentCommit.getTaskScore());
            if (!TextUtils.isEmpty(task.getTaskId())) {
                data.setTaskId(Integer.valueOf(task.getTaskId()));
            }
            StudyTask studyTask = new StudyTask();
            studyTask.setTaskTitle(task.getTaskName());
            studyTask.setResId(task.getResId());
            studyTask.setCreateId(task.getCreateId());
            studyTask.setCreateName(task.getOriginName());
            studyTask.setResUrl(task.getResourceUrl());
            //把task的scoreRule拿出来赋值
            studyTask.setScoringRule(scoringRule);
            studyTask.setType(task.getLqwawaType());
            studyTask.setCourseId(task.getCourseId());
            studyTask.setCourseName(task.getCourseName());
//            studyTask.setClassId();
//            studyTask.setClassName();
            if (isCheckMark) {
                //查看批阅
                enterCheckMarkDetail(activity, data, studyTask, roleType, isAudition);
            } else {
                loadMarkData(activity, data, studyTask, roleType);
            }
        }

        /**
         * 打开学生提交的列表数据
         * @param activity
         * @param exerciseTotalScore 试卷总分
         * @param resId  课件的resId（id-type）
         * @param screenType 课件的方向
         * @param exerciseData 课件的答题卡信息
         * @param taskId 任务的taskId
         * @param roleType 角色信息
         * @param commitTaskTitle 列表任务的title
         * @param isHeadMaster 班主任的角色
         * @param isOnlineHost 小编
         * @param isOnlineReporter 主编
         * @param studentName 列表提交的学生的姓名
         * @param studentId 列表提交色学生studentId
         * @param commitTaskId 列表item的commitTaskId
         */
        @Override
        public void enterExerciseDetailActivity(Activity activity,
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
                                                String className) {
            QuestionResourceModel markModel = new QuestionResourceModel();
            markModel.setTitle(commitTaskTitle);
            if (!TextUtils.isEmpty(taskId)) {
                markModel.setT_TaskId(Integer.valueOf(taskId));
            }
            markModel.setT_TaskType(StudyTaskType.TASK_ORDER);
            markModel.setT_CommitTaskOnlineId(commitTaskId);
//            markModel.setT_ClassId(classId);
//            markModel.setT_ClassName(className);
            markModel.setStuMemberId(DemoApplication.getInstance().getMemberId());
            markModel.setT_CourseName(courseName);
            markModel.setT_CourseId(courseId);

            ExerciseAnswerCardParam cardParam = new ExerciseAnswerCardParam();
            cardParam.setExerciseTotalScore(exerciseTotalScore);
            cardParam.setResId(resId);
            cardParam.setScreenType(screenType);
            cardParam.setExerciseAnswerString(exerciseData);
            cardParam.setTaskId(taskId);
            cardParam.setRoleType(roleType);
            cardParam.setCommitTaskTitle(commitTaskTitle);
            cardParam.setIsHeadMaster(isHeadMaster);
            cardParam.setStudentName(studentName);
            cardParam.setStudentId(studentId);
            cardParam.setCommitTaskId(commitTaskId);
            cardParam.setIsOnlineReporter(isOnlineReporter);
            cardParam.setIsOnlineHost(isOnlineHost);
            cardParam.setFromOnlineStudyTask(true);
            cardParam.setTaskScoreRemark(taskScoreRemark);
            cardParam.setMarkModel(markModel);

            AnswerCardDetailActivity.start(activity, cardParam);
        }

        /**
         * 做答题卡
         * @param activity
         * @param exerciseString  答题卡信息
         * @param TaskId 任务的taskId
         * @param StudentId 学生studentId(家长传学生studentId)
         * @param courseId 任务的resId(id-type)
         */
        @Override
        public void doExerciseTask(Activity activity,
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
                                   @NonNull String courseName) {
            QuestionResourceModel markModel = new QuestionResourceModel();
            markModel.setTitle(taskTitle);
            if (!TextUtils.isEmpty(TaskId)) {
                markModel.setT_TaskId(Integer.valueOf(TaskId));
            }
            markModel.setT_TaskType(StudyTaskType.TASK_ORDER);
            markModel.setT_CommitTaskOnlineId(commitTaskId);
//            markModel.setT_ClassId(classId);
//            markModel.setT_ClassName(className);
            markModel.setStuMemberId(DemoApplication.getInstance().getMemberId());
            markModel.setT_CourseName(courseName);
            markModel.setT_CourseId(CourseId);

            DoTaskOrderHelper.openExerciseDetail(activity,
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
                    true,
                    isDoExercise,
                    markModel);
        }

        /**
         * @param sourceCourseResId
         * @param taskUploadBackVo
         * @param resType           作业类型 -复述课件/做任务单
         */
        @Override
        public void studyInfoRecord(final Activity activity, String sourceCourseResId,
                                    TaskUploadBackVo taskUploadBackVo, int resType, final int sourceType) {
            final CourseData commitData = CourseData.fromTaskUploadBackVo(taskUploadBackVo);
            final UserInfo userInfo = DemoApplication.getInstance().getUserInfo();
            if (userInfo == null || !StringUtils.isValidString(userInfo.getMemberId())) {
                return;
            }
            if (resType == 19 || resType == 5 || resType == 16 || resType == 10019) {
                if (resType > ResType.RES_TYPE_BASE) {
                    //分页信息
                    if (TextUtils.isEmpty(sourceCourseResId)) {
                        return;
                    }
                    WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
                    wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(sourceCourseResId));
                    wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                            .OnSplitCourseDetailFinishListener() {
                        @Override
                        public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                            if (info != null) {
                                CourseData courseData = info.getCourseData();
                                if (courseData != null) {
                                    commitStudyInfoRecordData(activity, userInfo, commitData,
                                            courseData.totaltime, 2, sourceType);

                                }
                            }
                        }
                    });
                } else {
                    //非分页信息
                    WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
                    wawaCourseUtils.loadCourseDetail(sourceCourseResId);
                    wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                            OnCourseDetailFinishListener() {
                        @Override
                        public void onCourseDetailFinish(CourseData courseData) {
                            if (courseData != null) {
                                commitStudyInfoRecordData(activity, userInfo, commitData,
                                        courseData.totaltime, 2, sourceType);
                            }
                        }
                    });
                }
            } else if (resType == 18 || resType == 1 || resType == 6 || resType == 20) {
                commitStudyInfoRecordData(activity, userInfo, commitData,
                        0, 2, sourceType);
            } else if (resType == 23) {
                commitStudyInfoRecordData(activity, userInfo, commitData,
                        0, 3, sourceType);
            }
        }
    };

    private static void commitStudyInfoRecordData(Activity activity,
                                                  UserInfo userInfo,
                                                  CourseData courseData,
                                                  int originalTime,
                                                  int recordType,
                                                  int sourceType) {
        StudyInfoRecordUtil recordUtil = StudyInfoRecordUtil.getInstance().
                clearData().
                setActivity(activity).
                setCourseData(courseData).
                setCurrentModel(StudyInfoRecordUtil.RecordModel.STUDY_MODEL).
                setRecordType(recordType).
                setOriginalTotalTime(originalTime).
                setSourceType(sourceType).
                setUserInfo(userInfo);
        if (recordType == 3) {
            recordUtil.setRecordTime(0);
        } else {
            recordUtil.setRecordTime(TimerUtils.getInstance().getCurrentTotalTime());
        }
        recordUtil.send();
    }

    private static void doTask(final Activity activity, String taskId) {
        if (taskId.contains("-")) {
            taskId = taskId.split("-")[0];
        }
        WawaCourseUtils utils = new WawaCourseUtils(activity);
        utils.loadSplitLearnCardDetail(taskId, true);
        utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    CourseData courseData = info.getCourseData();
                    activity.getIntent().putExtra("orientation", info.getScreenType());
                    if (courseData != null) {
                        processData(activity, courseData, false);
                    }
                }
            }
        });
    }

    private static void processData(Activity activity, CourseData courseData, boolean isFinish) {
        if (courseData != null) {
            int resType = courseData.type % ResType.RES_TYPE_BASE;
            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.playOnlineCourse(activity, courseData.getCourseInfo(), false, null);
                if (isFinish) {
                    activity.finish();
                }
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                downLoadOnePageData(activity, courseData);
            } else if (resType == ResType.RES_TYPE_ONEPAGE) {
                downLoadOnePageData(activity, courseData);
            } else if (resType == ResType.RES_TYPE_NOTE) {
                //直接打开帖子
                if (!TextUtils.isEmpty(courseData.resourceurl)) {
                    ActivityUtils.openOnlineNote(activity, courseData.getCourseInfo(), false, true);
                }
            }
        }
    }

    /**
     * 下载有声相册
     *
     * @param courseData
     */
    private static void downLoadOnePageData(final Activity activity, final CourseData courseData) {
        DownloadOnePageTask task = new DownloadOnePageTask(activity, courseData.
                resourceurl, courseData.nickname, courseData.screentype, Utils
                .DOWNLOAD_TEMP_FOLDER, null);
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    LocalCourseDTO data = (LocalCourseDTO) result;
                    openLocalOnePageRetellCourse(activity, data, courseData.screentype);
                }
            }
        });
        task.checkCanReplaceIPAddress(courseData.id, courseData.type, task);
    }

    /**
     * 复述课件 有声相册
     *
     * @param data
     * @param screenType
     */
    private static void openLocalOnePageRetellCourse(Activity activity, LocalCourseDTO data, int screenType) {
        if (data == null) {
            return;
        }
        int requestCode = ResourceBaseFragment.REQUEST_CODE_RETELLCOURSE;
        CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam
                (activity, null, data.getmPath(), data.getmTitle(), data.getmDescription(), screenType);
        param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_STUDY_TASK_COURSE;
        param.isFromMoocModel = true;
        CreateSlideHelper.startSlide(param, requestCode);
    }

    private static void openPptOrPdf(final Activity activity, final String resId,
                                     final int resType, final String title, final String createId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", "" + resId + "-" + resType);
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
                if (activity == null) {
                    return;
                }
                if (TextUtils.isEmpty(jsonString)) {
                    return;
                }
                PPTAndPDFCourseInfoCode result = com.alibaba.fastjson.JSONObject.parseObject
                        (jsonString, PPTAndPDFCourseInfoCode.class);
                if (result != null) {
                    List<NewResourceInfo> resourceInfoList = new ArrayList<>();
                    List<PPTAndPDFCourseInfo> splitCourseInfo = result.getData();
                    if (splitCourseInfo == null || splitCourseInfo.size() == 0) {
                        return;
                    }
                    List<SplitCourseInfo> splitList = splitCourseInfo.get(0).getSplitList();
                    if (splitList == null || splitList.size() == 0) {
                        return;
                    }
                    int resTypeNew = resType;
                    if (resType > ResType.RES_TYPE_BASE) {
                        resTypeNew = resType % ResType.RES_TYPE_BASE;
                    }
                    if (splitList.size() > 0) {
                        for (int i = 0; i < splitList.size(); i++) {
                            SplitCourseInfo splitCourse = splitList.get(i);
                            NewResourceInfo newResourceInfo = new NewResourceInfo();
                            newResourceInfo.setTitle(splitCourseInfo.get(0).getOriginname());
                            newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getPlayUrl()));
                            newResourceInfo.setResourceId(resId);
                            newResourceInfo.setResourceType(resTypeNew);
                            newResourceInfo.setAuthorId(createId);
                            resourceInfoList.add(newResourceInfo);
                        }
                    }

                    NewResourceInfoTag newInfoTag = new NewResourceInfoTag();
                    if (resourceInfoList.size() > 0) {
                        newInfoTag.setTitle(title);
                        newInfoTag.setResourceUrl(resourceInfoList.get(0).getResourceUrl());
                        newInfoTag.setDescription("");
                        newInfoTag.setSplitInfoList(resourceInfoList);
                        openPptAndPdf(activity, newInfoTag, title, resType);
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (activity == null) {
                    return;
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(activity);
    }

    /**
     * 打开ppt 或者 pdf 的资源
     */
    private static void openPptAndPdf(Activity activity, NewResourceInfoTag newInfoTag, String title, int resType) {
        if (newInfoTag != null) {
            List<NewResourceInfo> splitList = newInfoTag.getSplitInfoList();
            ArrayList<ImageInfo> imageItemInfos = new ArrayList<>();
            if (splitList != null && splitList.size() > 0) {
                if (splitList != null && splitList.size() > 0) {
                    for (int i = 0; i < splitList.size(); i++) {
                        NewResourceInfo splitCourse = splitList.get(i);
                        ImageInfo newResourceInfo = new ImageInfo();
                        newResourceInfo.setTitle(title);
                        newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getResourceUrl()));
                        newResourceInfo.setResourceId(splitCourse.getResourceId());
                        newResourceInfo.setResourceType(resType);
                        newResourceInfo.setAuthorId(splitCourse.getAuthorId());
                        imageItemInfos.add(newResourceInfo);
                    }
                }
            }
            if (imageItemInfos != null && imageItemInfos.size() > 0) {
                GalleryActivity.newInstance(activity, imageItemInfos, true, 0, false, true, false);
            }
        }
    }

    public static void enterCheckMarkDetail(final Activity activity,
                                            final CommitTask data,
                                            final StudyTask task,
                                            final int roleType,
                                            boolean isAudition) {
        String taskScore = data.getTaskScore();
        if (TextUtils.isEmpty(taskScore)) {
            taskScore = "";
        }
        boolean isNeedMark = true;
        if (task.getScoringRule() == 0) {//不打分
            isNeedMark = false;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean(CheckMarkFragment.Constants.EXTRA_IS_FROM_MOOC, true);
        bundle.putBoolean(CheckMarkFragment.Constants.EXTRA_IS_AUDITION, isAudition);
        bundle.putString(CheckMarkFragment.Constants.COMMITTASK_ID, data.getCommitTaskId() + "");
        bundle.putString(CheckMarkFragment.Constants.STUDENT_ID, data.getStudentId());
        bundle.putString(CheckMarkFragment.Constants.RES_ID, data.getStudentResId() + "");
        bundle.putString(CheckMarkFragment.Constants.TASK_TITLE, data.getStudentResTitle());
        bundle.putString(CheckMarkFragment.Constants.TASK_SCORE, taskScore);
        bundle.putString(CheckMarkFragment.Constants.TASK_PIC, data.getStudentResUrl());
        bundle.putSerializable(CheckMarkFragment.Constants.STUDYTASK, task);
        bundle.putInt(CheckMarkFragment.Constants.ROLE_TYPE, roleType);
        bundle.putBoolean(CheckMarkFragment.Constants.EXTRA_ISONLINEREPORTER, roleType == RoleType.ROLE_TYPE_TEACHER);
        bundle.putBoolean(CheckMarkFragment.Constants.EXTRA_ISONLINEHOST, roleType == RoleType
                .ROLE_TYPE_EDITOR);
        if (data != null) {
            bundle.putSerializable(CheckMarkFragment.Constants.COMMIT_TASK, data);
        }
        if (!(roleType == RoleType.ROLE_TYPE_VISITOR)) {
            bundle.putSerializable(CheckMarkFragment.Constants.ACTION_TASKMARKPARAM,
                    new TaskMarkParam(
                            data.isHasCommitTaskReview(),
                            task.getScoringRule() == 2,
                            roleType,
                            data.getCommitTaskId() + "",
                            false,
                            isNeedMark,
                            data.getTaskScore(), true));
        }
        Intent intent = new Intent(activity, CommonFragmentActivity.class);
        bundle.putSerializable(CommonFragmentActivity.EXTRA_CLASS_OBJECT, CheckMarkFragment.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * 拉取最新批阅提问数据
     */
    public static void loadMarkData(final Activity activity,
                                    final CommitTask data,
                                    final StudyTask task,
                                    final int roleType) {
        Map<String, Object> params = new HashMap<>();
        params.put("CommitTaskOnlineId", data.getCommitTaskId() + "");
        RequestHelper.RequestDataResultListener<CheckMarkResult> listener = new
                RequestHelper.RequestDataResultListener<CheckMarkResult>(activity, CheckMarkResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        CheckMarkInfo result = com.alibaba.fastjson.JSONObject.parseObject(jsonString,
                                CheckMarkInfo.class);
                        if (result.getErrorCode() != 0 || result.getModel() == null) {
                            return;
                        }
                        //当前mooc这边的批阅暂不支持打分的功能
                        boolean isNeedMark = true;
                        if (task.getScoringRule() == 0) {//不打分
                            isNeedMark = false;
                        }
                        if (result.getModel().size() > 0) {
                            openCourse(activity, data, task, isNeedMark, result.getModel().get(0)
                                    .getResId(), roleType);
                        } else {
                            openCourse(activity, data, task, isNeedMark, null, roleType);
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(activity,
                ServerUrl.GET_LOADCOMMITTASKREVIEWLIST, params, listener);

    }

    /**
     * 打开课件
     */
    public static void openCourse(final Activity activity,
                                  final CommitTask data,
                                  final StudyTask task,
                                  final boolean isNeedMark,
                                  String resId,
                                  final int roleType) {
        String courseId = resId;
        if (TextUtils.isEmpty(courseId)) {
            courseId = data.getStudentResId();
        }
        String tempResId = courseId;
        int resType = 0;
        if (courseId.contains("-")) {
            String[] ids = courseId.split("-");
            if (ids != null && ids.length == 2) {
                tempResId = ids[0];
                if (ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        if (resType > ResType.RES_TYPE_BASE) {
            //分页信息
            if (TextUtils.isEmpty(tempResId)) {
                return;
            }
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            processOpenImageData(activity, courseData, data, task, isNeedMark, roleType);
                        }
                    }
                }
            });
        } else {
            //非分页信息
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
            wawaCourseUtils.loadCourseDetail(courseId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    processOpenImageData(activity, courseData, data, task, isNeedMark, roleType);
                }
            });
        }
    }

    /**
     * 打开逻辑
     */
    private static void processOpenImageData(Activity activity,
                                             CourseData courseData,
                                             CommitTask data,
                                             final StudyTask task,
                                             boolean isNeedMark,
                                             int roleType) {
        if (courseData != null) {
            NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
            PlaybackParam playbackParam = null;
            if (roleType == RoleType.ROLE_TYPE_VISITOR) {
                playbackParam = new PlaybackParam();
                playbackParam.mIsHideToolBar = true;
            } else {
                playbackParam = new PlaybackParam();
                playbackParam.taskMarkParam = new TaskMarkParam(
                        data.isHasCommitTaskReview(),
                        task.getScoringRule() == 2,
                        roleType,
                        data.getCommitTaskId() + "",
                        false,
                        isNeedMark,
                        data.getTaskScore(), true);
            }
            if (newResourceInfo.isOnePage() || newResourceInfo.isStudyCard()) {
                ActivityUtils.openOnlineOnePage(activity, newResourceInfo, false, playbackParam);
            } else {
                ActivityUtils.playOnlineCourse(activity, newResourceInfo.getCourseInfo(),
                        false, playbackParam);
            }
        }
    }
}
