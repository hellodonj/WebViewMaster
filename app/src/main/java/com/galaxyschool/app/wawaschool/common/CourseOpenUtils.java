package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.HomeworkCommitActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.TopicDiscussionActivity;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolMainFragment;
import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkCommitFragment;
import com.galaxyschool.app.wawaschool.fragment.HomeworkMainFragment;
import com.galaxyschool.app.wawaschool.pojo.HomeworkListInfo;
import com.galaxyschool.app.wawaschool.pojo.LookResDto;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wei on 2017/05/11.
 * 打开课件工具类
 */
public class CourseOpenUtils {

    public static UserInfo stuUserInfo;


    /**
     * 直接打开微课详情
     * @param activity
     * @param task
     * @param roleType
     * @param memberId
     * @param studentId
     * @param stuUserInfo 学生的信息，学习任务里面才有数据，用来上传课件。
     * @param isFromStudyTask
     */
    public static void openCourseDetailsDirectly(Activity activity, StudyTask task,
                                                 int roleType, String memberId,
                                                 String studentId, UserInfo stuUserInfo,
                                                 boolean isFromStudyTask){
        openCourseDetailsDirectly(activity, task, roleType, memberId, studentId, stuUserInfo, isFromStudyTask, null);
    }

    public static void openCourseDetailsDirectly(Activity activity, StudyTask task,
                                                 int roleType, String memberId,
                                                 String studentId, UserInfo stuUserInfo,
                                                 boolean isFromStudyTask, Bundle bundle){
        if (activity == null || task == null){
            return;
        }
        String resId = task.getResId();
        StudyTaskInfo info = task.toStudyTaskInfo();
        info.setRoleType(roleType);
        //设置是否来自学习任务
        info.setFromStudyTask(isFromStudyTask);
        //设置孩子id
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            info.setStudentId(memberId);
        }else if (roleType == RoleType.ROLE_TYPE_PARENT){
            info.setStudentId(studentId);
        }
        if (stuUserInfo != null){
            //设置UserInfo
            info.setUserInfo(stuUserInfo);
            CourseOpenUtils.stuUserInfo = stuUserInfo;
        }

        openTask(activity, resId,info, roleType,studentId,bundle);
    }

    public static void openTask(final Activity activity, String resId, final StudyTaskInfo task,
                                final int roleType, final String studentId, final Bundle bundle) {
        String tempResId = resId;
        int resType = 0;
        if(!TextUtils.isEmpty(resId) && resId.contains("-")) {
            String[] ids = resId.split("-");
            if(ids != null && ids.length >= 2) {
                tempResId = ids[0];
                if(ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
        if(resType > ResType.RES_TYPE_BASE) {
            if (TextUtils.isEmpty(tempResId)) {
                return;
            }
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    if(info != null) {
                        CourseData courseData = info.getCourseData();
                        if(courseData != null) {
                            openCourseDetail(activity, courseData, task, roleType,task
                                    .getTaskType(),studentId,bundle);
                        }
                    }
                }
            });
        } else {
            wawaCourseUtils.loadCourseDetail(resId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    if (courseData != null) {
                        openCourseDetail(activity, courseData, task, roleType,task
                                .getTaskType(),studentId,bundle);
                    }
                }
            });
        }
    }

    /**
     * 打开课件详情
     * @param activity
     * @param data
     * @param studyTaskInfo
     * @param roleType
     * @param taskType
     * @param studentId
     */
    public static void openCourseDetail(Activity activity, CourseData data,
                                        StudyTaskInfo studyTaskInfo, int roleType,
                                        int taskType, String studentId,Bundle bundle) {
        if (data == null || data.type < 0) {
            return;
        }
        int resType = data.type % ResType.RES_TYPE_BASE;
        //是否来自学习任务
        boolean isFromStudyTask = false;
        if (studyTaskInfo != null){
            isFromStudyTask = studyTaskInfo.getIsFromStudyTask();
        }
        switch (resType) {
            case ResType.RES_TYPE_COURSE:
            case ResType.RES_TYPE_COURSE_SPEAKER:
            case ResType.RES_TYPE_OLD_COURSE:
            case ResType.RES_TYPE_STUDY_CARD: // 任务单

                //不是学习任务都直接进入微课详情。
                if (!isFromStudyTask){
                    //进入微课详情
                    ActivityUtils.openCourseDetail(activity, data.getNewResourceInfo(),
                            PictureBooksDetailActivity.FROM_OTHRE,bundle);
                }else {
                    //任务单特殊处理
                    if (resType == ResType.RES_TYPE_STUDY_CARD){
                        //进入任务单详情
                        NewResourceInfo info = data.getNewResourceInfo();
                        if (info != null){
                            //控制显示收藏，默认是不显示。
                            info.setIsFromSchoolResource(true);
                            //这个表示不要右上角的返回主页按钮
                            info.setIsFromAirClass(true);
                            if (bundle != null){
                                PassParamhelper passParamhelper = (PassParamhelper) bundle.getSerializable
                                        (PassParamhelper.class.getSimpleName());
                                if (passParamhelper.isAudition){
                                    info.setIsQualityCourse(true);
                                }
                            }
                        }
                        PassParamhelper mParam = new PassParamhelper();
                        mParam.isFromLQMOOC = true;
                        ActivityUtils.enterTaskOrderDetailActivity(activity, info,mParam);
                    }
                    else {
                        //学习任务
                        data.setStudyTaskInfo(studyTaskInfo);
                        ActivityUtils.openCourseDetail(activity, data.getNewResourceInfo(),
                                PictureBooksDetailActivity.FROM_OTHRE,bundle);
                    }
                }
                break;
            case ResType.RES_TYPE_NOTE:
                if (!TextUtils.isEmpty(data.resourceurl)) {
                    ActivityUtils.openOnlineNote(activity, data.getCourseInfo(), false,true);
                }
                break;
            case ResType.RES_TYPE_ONEPAGE:
                //学习任务
                if (isFromStudyTask) {
                    data.setStudyTaskInfo(studyTaskInfo);
                }
                ActivityUtils.openCourseDetail(activity, data.getNewResourceInfo(),
                        PictureBooksDetailActivity.FROM_OTHRE,bundle);
                break;
        }
    }

    public static void openCourseDirectly(final Activity activity, String resId, final boolean
            isPublicRes) {
        openCourseDirectly(activity,resId,isPublicRes,"",false);
    }
    /**
     * 直接打开课件
     */
    public static void openCourseDirectly(final Activity activity, String resId, final boolean
            isPublicRes, final String schoolId, final boolean isFromMooc){
        if (activity == null || TextUtils.isEmpty(resId)){
            return;
        }
        String tempResId = resId;
        int resType = 0;
        if(!TextUtils.isEmpty(resId) && resId.contains("-")) {
            String[] ids = resId.split("-");
            if(ids != null && ids.length >= 2) {
                tempResId = ids[0];
                if(ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        if(resType > ResType.RES_TYPE_BASE) {
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
                    if(info != null) {
                        CourseData courseData = info.getCourseData();
                        if(courseData != null) {
                            courseData.setIsPublicRes(isPublicRes);
                            if (!TextUtils.isEmpty(schoolId)){
                                //从mooc收藏时需要schoolId
                                courseData.setCollectionOrigin(schoolId);
                            }
                            processOpenImageData(activity,courseData,isFromMooc);
                        }
                    }
                }
            });
        }else {
            //非分页信息
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
            wawaCourseUtils.loadCourseDetail(resId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.
                    OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    courseData.setIsPublicRes(isPublicRes);
                    if (!TextUtils.isEmpty(schoolId)){
                        //从mooc收藏时需要schoolId
                        courseData.setCollectionOrigin(schoolId);
                    }
                    processOpenImageData(activity,courseData,isFromMooc);
                }
            });
        }
    }

    /**
     * 打开课件逻辑
     * @param courseData
     */
    public static void processOpenImageData(Activity activity,CourseData courseData,boolean
            isFromMooc) {
        if (activity == null){
            return;
        }
        if (courseData != null) {
            int resType = courseData.type % ResType.RES_TYPE_BASE;

            //加一个参数控制播放页的收藏是否显示
            PlaybackParam mParam = new PlaybackParam();
            mParam.mIsHideCollectTip = isFromMooc;

            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.playOnlineCourse(activity, courseData.getCourseInfo(), false,
                        mParam);
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                //直接打开，不带编辑。
                ActivityUtils.openOnlineOnePage(activity, courseData.getNewResourceInfo(),
                        false, mParam);
            }else if(resType == ResType.RES_TYPE_ONEPAGE){
                ActivityUtils.openOnlineOnePage(activity, courseData.getNewResourceInfo(),
                        false, mParam);
            }else if (resType == ResType.RES_TYPE_NOTE){
                //直接打开帖子
                if (!TextUtils.isEmpty(courseData.resourceurl)) {
                    ActivityUtils.openOnlineNote(activity, courseData.getCourseInfo(), false, true);
                }
            }
        }
    }

    /**
     * 统一打开学习任务
     * @param activity
     * @param data
     * @param roleType
     * @param isHeadMaster
     * @param memberId
     * @param sortStudentId
     * @param studentId
     * @param userInfo
     * @param isCampusPatrolTag
     */
    public static void openStudyTask(Activity activity, HomeworkListInfo data,
                                     int roleType,boolean isHeadMaster,
                                     String memberId,String sortStudentId,
                                     String studentId,UserInfo userInfo,
                                     boolean isCampusPatrolTag) {
        if (activity == null || data == null){
            return;
        }
        int taskType = -1;
        if(!TextUtils.isEmpty(data.getTaskType())) {
            taskType = Integer.parseInt(data.getTaskType());
        }
        //从校园巡查进入学习任务，就当老师身份打开。
        if (isCampusPatrolTag) {
            roleType = RoleType.ROLE_TYPE_TEACHER;
        }
        //除了话题讨论，现在统一进入详情（HomeworkCommitFragment）页面。
        if(taskType == StudyTaskType.TOPIC_DISCUSSION){
            Intent intent =new Intent(activity,TopicDiscussionActivity.class);
            intent.putExtra("TaskId",Integer.parseInt(data.getTaskId()));
            intent.putExtra("commentTitle",data.getTaskTitle());
            intent.putExtra("commentContent",data.getDiscussContent());
            intent.putExtra("roleType",roleType);
            intent.putExtra("fromType","homeworkList");
            intent.putExtra("taskCreateId",data.getTaskCreateId());
            intent.putExtra(ActivityUtils.EXTRA_IS_HISTORY_CLASS,data.isHistoryClass());
            //校园巡查标识
            intent.putExtra(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,isCampusPatrolTag);
            //直接传递data过去，省心了。
            intent.putExtra(HomeworkListInfo.class.getSimpleName(),data);
            activity.startActivityForResult(intent, CampusPatrolPickerFragment.
                    REQUEST_CODE_DISCUSSION_TOPIC);
        }else {
            // 页面跳转
            Intent intent = new Intent(activity, HomeworkCommitActivity.class);
            intent.putExtra("roleType", roleType);
            intent.putExtra("TaskId", data.getTaskId());
            intent.putExtra("TaskType", taskType);
            //标题
            intent.putExtra("TaskTitle",data.getTaskTitle());
            intent.putExtra(ActivityUtils.EXTRA_IS_HISTORY_CLASS,data.isHistoryClass());
            intent.putExtra(HomeworkMainFragment.Constants.EXTRA_IS_HEAD_MASTER, isHeadMaster);
            intent.putExtra(ActivityUtils.EXTRA_IS_ONLINE_CLASS,data.isOnlineSchoolClass());
            if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                intent.putExtra("StudentId", memberId);
                intent.putExtra("sortStudentId", memberId);
            } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
                //家长传递多个孩子id，以逗号分隔。
                intent.putExtra("sortStudentId", sortStudentId);
                //单个孩子id
                intent.putExtra("StudentId", studentId);
                if(userInfo != null) {
                    intent.putExtra(UserInfo.class.getSimpleName(), userInfo);
                }
            } else if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                intent.putExtra("StudentId", "");
                intent.putExtra("sortStudentId", "");
            }
            //校园巡查标识
            intent.putExtra(CampusPatrolMainFragment.IS_CAMPUS_PATROL_TAG,isCampusPatrolTag);

            //传递新版看课件数据
            if (taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE) {
                List<LookResDto> lookResDtoList = data.getLookResList();
                intent.putExtra(HomeworkCommitFragment.LOOK_RES_DTO_LIST,
                        (Serializable)lookResDtoList);
            }
            //直接传递data过去，省心了。
            intent.putExtra(HomeworkListInfo.class.getSimpleName(), data);
            activity.startActivityForResult(intent,CampusPatrolPickerFragment.
                    REQUEST_CODE_HOMEWORK_COMMIT);
        }
    }
}
