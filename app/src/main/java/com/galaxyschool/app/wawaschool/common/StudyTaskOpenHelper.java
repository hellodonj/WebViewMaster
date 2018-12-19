package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.course.PlaybackWawaPageActivityPhone;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.oosic.apps.iemaker.base.PlaybackActivity;

/**
 * Created by Administrator on 2016/6/21.
 */
public class StudyTaskOpenHelper {
    public static UserInfo stuUserInfo;
    private static String preResId = null;

    public static void openTask(final Activity activity, String resId, final StudyTaskInfo task,
                                final int
                                        roleType, final String studentId) {
        preResId = resId;
        String tempResId = resId;
        int resType = 0;
        if (resId.contains("-")) {
            String[] ids = resId.split("-");
            if (ids != null && ids.length == 2) {
                tempResId = ids[0];
                if (ids[1] != null) {
                    resType = Integer.parseInt(ids[1]);
                }
            }
        }
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
        if (resType > ResType.RES_TYPE_BASE) {
            if (TextUtils.isEmpty(tempResId)) {
                return;
            }
            wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(tempResId));
            wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils
                    .OnSplitCourseDetailFinishListener() {
                @Override
                public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                    preResId = null;
                    if (info != null) {
                        CourseData courseData = info.getCourseData();
                        if (courseData != null) {
                            openStudyTaskDetail(activity, courseData, task, roleType, task
                                    .getTaskType(), studentId);
                        }
                    }
                }
            });
        } else {
            wawaCourseUtils.loadCourseDetail(resId);
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    preResId = null;
                    if (courseData != null) {
                        openStudyTaskDetail(activity, courseData, task, roleType, task
                                .getTaskType(), studentId);

                    }
                }
            });
        }
    }

    /**
     * 打开学习任务详情
     *
     * @param activity
     * @param data
     * @param studyTaskInfo
     * @param roleType
     * @param taskType
     * @param studentId
     */
    public static void openStudyTaskDetail(Activity activity, CourseData data,
                                           StudyTaskInfo studyTaskInfo, int roleType,
                                           int taskType, String studentId) {
        if (data == null || data.type < 0) {
            return;
        }
        int resType = data.type % ResType.RES_TYPE_BASE;
        switch (resType) {
            case ResType.RES_TYPE_COURSE:
            case ResType.RES_TYPE_COURSE_SPEAKER:
            case ResType.RES_TYPE_OLD_COURSE:
            case ResType.RES_TYPE_STUDY_CARD: // 任务单
                //导读
                //做任务单,复用导读页面。
                if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE
                        || taskType == StudyTaskType.TASK_ORDER) {
                    ActivityUtils.openSelectedReadingDetailActivity(activity, data
                            .getNewResourceInfo(), studyTaskInfo, roleType, studentId, stuUserInfo);
                } else {
                    //学习任务
                    data.setStudyTaskInfo(studyTaskInfo);
                    ActivityUtils.openCourseDetail(activity, data.getNewResourceInfo(),
                            PictureBooksDetailActivity.FROM_OTHRE);
                }
                break;
            case ResType.RES_TYPE_NOTE:
                if (!TextUtils.isEmpty(data.resourceurl)) {
                    ActivityUtils.openOnlineNote(activity, data.getCourseInfo(), false,
                            true);
                }
                break;
            case ResType.RES_TYPE_ONEPAGE:
                //学习任务
                data.setStudyTaskInfo(studyTaskInfo);
                ActivityUtils.openCourseDetail(activity, data.getNewResourceInfo(),
                        PictureBooksDetailActivity.FROM_OTHRE);
                break;
        }
    }

    public static void openTask(Activity activity, CourseData data, StudyTaskInfo task,
                                int roleType, String studentId) {
        if (data == null || data.type < 0) {
            return;
        }
        int resType = data.type % ResType.RES_TYPE_BASE;
        switch (resType) {
            case ResType.RES_TYPE_COURSE:
            case ResType.RES_TYPE_COURSE_SPEAKER:
            case ResType.RES_TYPE_OLD_COURSE:
                if (task != null && task.getTaskType() == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
                    ActivityUtils.openSelectedReadingDetailActivity(activity, data
                            .getNewResourceInfo(), task, roleType, studentId, stuUserInfo);
                } else {
                    ActivityUtils.openPictureDetailActivity(activity, data.getNewResourceInfo());
                }
                break;
            case ResType.RES_TYPE_NOTE:
                if (!TextUtils.isEmpty(data.resourceurl)) {
                    ActivityUtils.openOnlineNote(activity, data.getCourseInfo(), false, true);
                }
                break;
            case ResType.RES_TYPE_ONEPAGE:
                ActivityUtils.openPictureDetailActivity(activity, data.getNewResourceInfo());
                break;

        }
    }
}
