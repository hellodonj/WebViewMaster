package com.lqwawa.mooc.modle.MyCourse.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ScoreStatisticsActivity;
import com.galaxyschool.app.wawaschool.SpeechAssessmentActivity;
import com.galaxyschool.app.wawaschool.TeacherReviewDetailActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CourseOpenUtils;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkResult;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.pojo.CommitTaskResult;
import com.galaxyschool.app.wawaschool.pojo.ExerciseAnswerCardParam;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfoTag;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.PPTAndPDFCourseInfoCode;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.DoTaskOrderTipsDialog;
import com.galaxyschool.app.wawaschool.views.OrientationSelectDialog;
import com.libs.gallery.ImageInfo;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.response.LQResourceDetailVo;
import com.lqwawa.intleducation.factory.helper.LearningTaskHelper;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;
import com.lqwawa.intleducation.module.learn.vo.LqTaskInfoVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskOriginVo;
import com.lqwawa.intleducation.module.learn.vo.TaskInfoVo;
import com.lqwawa.intleducation.module.learn.vo.TaskUploadBackVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.libs.filedownloader.DownloadService;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.utils.TimerUtils;
import com.umeng.socialize.media.UMImage;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SectionTaskDetailsActivityEx extends SectionTaskDetailsActivity {

    private NewResourceInfoTag newInfoTag;
    private TextView accessDetails;
    // 批阅接收的广播Action
    private static final String ACTION_MARK_SCORE = "com.galaxyschool.app.wawaschool.Action_Mark_score";
    // 语音评测接收广播的Action
    private static final String ACTION_MARK_EVAL_SCORE = "com.galaxyschool.app.wawaschool.Action_Mark_eval_score";
    public static final String COMMIT_AUTO_MARK_SCORE_ACTION = "commit_auto_mark_score_action";

    private LocalBroadcastManager mBroadcastManager = null;
    private LqTaskCommitVo itemStudentTask;
    private MyBroadCastReceiver receiver;
    private String score = "0";
    private int schemeId;
    private String resultContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerResultBroadcast();
        initBroadCastReceiver();
        // @date   :2018/4/23 0023 下午 2:23
        // @func   :V5.5关闭分享功能
        /*this.topBar.setRightFunctionText1TextColor(UIUtil.getColor(R.color.colorAccent));
        this.topBar.setRightFunctionText1(getText(R.string.share), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTaskInfo(sectionResListVo.getResId());
            }
        });*/
        //查阅详情
        accessDetails = (TextView) findViewById(R.id.tv_access_details);
        updateAccessDetails();

        if (sectionResListVo == null) {
            if (TextUtils.isEmpty(taskId)) {
                TipMsgHelper.ShowLMsg(SectionTaskDetailsActivityEx.this, com.lqwawa.intleducation.R.string.task_id_null);
                return;
            }
            LessonHelper.getCommittedTaskByTaskId(taskId, "", new DataSource
                    .Callback<LqTaskCommitListVo>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                }

                @Override
                public void onDataLoaded(LqTaskCommitListVo vo) {
                    if (vo != null && vo.getTaskInfo() != null) {
                        getSectionResListVo(vo.getTaskInfo());
                    }
                }
            });

        }

        ((MyApplication) (MainApplication.getInstance())).bindDownloadService(activity, downloadServiceConn);
        UIUtils.currentSourceFromType = getSourceType();
    }

    private void getSectionResListVo(final LqTaskInfoVo lqTaskInfoVo) {
        if (lqTaskInfoVo == null) {
            return;
        }
        String resIdType = lqTaskInfoVo.getResId();
        String resId = null;
        String resType = null;
        if (!TextUtils.isEmpty(resIdType) && resIdType.contains("-")) {
            int index = resIdType.indexOf("-");
            if (index >= 0) {
                resId = resIdType.substring(0, index);
                resType = resIdType.substring(index + 1, resIdType.length());
            }
        }
        if (!TextUtils.isEmpty(resId)) {
            int type = Integer.parseInt(resType);
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(SectionTaskDetailsActivityEx.this);
            if (type == 23 || type == 10023 || type == 10019) {
                if (type == 10019) {
                    wawaCourseUtils.loadSplitCourseDetail(Integer.parseInt(resId));
                } else {
                    wawaCourseUtils.loadSplitLearnCardDetail(resId);
                }
                wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
                    @Override
                    public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                        if (info != null) {
                            CourseData courseData = info.getCourseData();
                            sectionResListVo = getSectionResListVo(courseData, lqTaskInfoVo);
                            if (sectionResListVo != null) {
                                initViews();
                                updateAccessDetails();
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
                        sectionResListVo = getSectionResListVo(courseData, lqTaskInfoVo);
                        if (sectionResListVo != null) {
                            initViews();
                            updateAccessDetails();
                        }
                    }
                });
            }
        }
    }

    private SectionResListVo getSectionResListVo(CourseData data, LqTaskInfoVo taskInfoVo) {
        if (data == null || taskInfoVo == null) {
            return null;
        }
        SectionResListVo vo = new SectionResListVo();
        vo.setName(data.nickname);
        vo.setResId(String.valueOf(data.id));
        vo.setResType(data.type);
        vo.setResourceUrl(data.resourceurl);
        vo.setScreenType(data.screentype);
        vo.setTaskId(taskId);
        vo.setCreateId(taskInfoVo.getCreateId());
        vo.setScreenType(data.screentype);
        if (TextUtils.isEmpty(examId)) {
            vo.setTaskType(taskInfoVo.getType());
        } else {
            //考试的任务单
            vo.setTaskType(3);
        }
        vo.setTaskName(taskInfoVo.getTaskTitle());
        SectionTaskOriginVo originVo = new SectionTaskOriginVo();
        originVo.setId(data.getResId());
        originVo.setName(data.nickname);
        originVo.setCreatName(data.createname);
        originVo.setThumbnail(data.thumbnailurl);
        vo.setSectionTaskOriginVo(originVo);
        return vo;
    }

    private void updateAccessDetails() {
        if (sectionResListVo == null || accessDetails == null) {
            return;
        }
        int resType = sectionResListVo.getResType();
        if (resType > 10000) {
            resType -= 10000;
        }
        if (resType == 5
                || resType == 18
                || resType == 19
                || resType == 23) {
            accessDetails.setVisibility(View.VISIBLE);
            if (accessDetails != null) {
                accessDetails.setVisibility(View.VISIBLE);
                accessDetails.setText(getResources().getString(R.string.access_details));
                final int resTypeSet = resType;
                accessDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (EmptyUtil.isEmpty(mLqTaskCommitListVo)) {
                            UIUtil.showToastSafe(R.string.task_id_null);
                            return;
                        }
                        openCourseWareDetails(
                                sectionResListVo.getResId(), sectionResListVo.getResType(),
                                sectionResListVo.getName(), sectionResListVo.getScreenType(),
                                sectionResListVo.getResourceUrl(),
                                mLqTaskCommitListVo.getTaskInfo().getResThumbnailUrl());
                        // sectionTaskDetailsVo.getOrigin().getThumbnail());
                    }
                });
            }
            findViewById(com.lqwawa.intleducation.R.id.iv_res_icon).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (TaskSliderHelper.onTaskSliderListener != null &&
                                    sectionResListVo != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(
                                        activity, sectionResListVo.getResId(),
                                        sectionResListVo.getResType(),
                                        activity.getIntent().getStringExtra("schoolId"),
                                        getSourceType());
                            }
                        }
                    });
        } else {
            analysisRetellCourseData();
            accessDetails.setVisibility(View.GONE);
            findViewById(com.lqwawa.intleducation.R.id.iv_res_icon).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (sectionResListVo != null) {
                                if (sectionResListVo.getResType() == ResType.RES_TYPE_PDF
                                        || sectionResListVo.getResType() == ResType.RES_TYPE_PPT
                                        || sectionResListVo.getResType() == ResType.RES_TYPE_IMG
                                        || sectionResListVo.getResType() == ResType.RES_TYPE_DOC) {
                                    openPptAndPdf();
                                }
                            }
                        }
                    });
        }
    }


    @Override
    protected void openCourseWareDetails(String resId, int resType, @NonNull String resTitle, int screenType,
                                         @NonNull String resourceUrl, @Nullable String resourceThumbnailUrl) {
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

    public void getTaskInfo(String vid) {
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("resId", vid);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.WAWATV_COURSE_DETAIL_URL + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<List<TaskInfoVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<TaskInfoVo>>>() {
                        });
                if (result.getCode() == 0) {
                    if (result.getData() != null && result.getData().size() > 0) {
                        TaskInfoVo vo = result.getData().get(0);
                        share(vo);
                    }
                }

            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    @Override
    protected void checkMarkTaskDetail(Activity activity, int roleType, SectionResListVo task, LqTaskCommitVo studentCommit, boolean isCheckMark, int sourceType) {
        super.checkMarkTaskDetail(activity, roleType, task, studentCommit, isCheckMark, sourceType);
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo) || EmptyUtil.isEmpty(sectionResListVo)) return;
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo.getTaskInfo())) return;

        if (sectionResListVo.isAutoMark()) {
            // 做任务单 自动批阅的类型
            final String resourceId = sectionResListVo.getResId() + "-" + sectionResListVo.getResType();


            LearningTaskHelper.requestResourceDetailById(resourceId, true, new DataSource.Callback<LQResourceDetailVo>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(LQResourceDetailVo vo) {
                    ExerciseAnswerCardParam cardParam = new ExerciseAnswerCardParam();
                    //答题卡返回的信息
                    List<LQResourceDetailVo.DataBean> data = vo.getData();
                    if (EmptyUtil.isEmpty(vo.getExercise())) return;
                    String answerString = JSON.toJSONString(vo.getExercise());

                    if (isCheckMark) {

                        // 处理过的角色
                        int resultRoleType = transferRoleType(mHandleRole);
                        // 是否是主编
                        boolean isOnlineReporter = resultRoleType == RoleType.ROLE_TYPE_EDITOR;
                        // 是否是小编
                        boolean isOnlineHost = resultRoleType == RoleType.ROLE_TYPE_TEACHER;

                        // 成绩统计 主编
                        if (resultRoleType == RoleType.ROLE_TYPE_EDITOR) {
                            // 成绩统计没有主编概念
                            resultRoleType = RoleType.ROLE_TYPE_TEACHER;
                        }
                        if (isAudition && !mTaskParams.isTeacherVisitor()) {
                            // 如果是试听,点击批阅cell或者查看批阅的时候 都是浏览者
                            resultRoleType = RoleType.ROLE_TYPE_VISITOR;
                        }

                        int commitTaskId = studentCommit.getId();

                        String taskScoreReMark = studentCommit.getTaskScoreRemark();
                        String courseId = mCourseParams.getCourseId();
                        String courseName = mCourseParams.getCourseName();

                        String classId = mCourseParams.getClassId();
                        String className = mCourseParams.getClassName();
                        TaskSliderHelper.enterExerciseDetailActivity(activity,
                                sectionResListVo.getPoint(),
                                resourceId,
                                sectionResListVo.getScreenType(),
                                answerString,
                                sectionResListVo.getTaskId(),
                                resultRoleType,
                                sectionResListVo.getName(),
                                false,
                                isOnlineHost,
                                isOnlineReporter,
                                studentCommit.getStudentName(),
                                studentCommit.getStudentId(),
                                commitTaskId,
                                taskScoreReMark,
                                courseId,
                                courseName,
                                classId,
                                className);

                    } else {

                        // 班级学程中必传
                        String schoolId = mCourseParams.getSchoolId();
                        String classId = mCourseParams.getClassId();

                        // 如果从大厅进来，提交的时候需要传绑定的机构班级
                        if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
                            schoolId = mCourseParams.getBindSchoolId();
                            classId = mCourseParams.getBindClassId();
                        } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
                            // 学程馆学习任务入口
                            // 课程发生了绑定
                            // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
                            // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
                            if (mCourseParams.isBindClass()) {
                                if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                                    // 学程馆Id和绑定的Id,相等
                                    schoolId = mCourseParams.getBindSchoolId();
                                    classId = mCourseParams.getBindClassId();
                                }
                            } else {
                                schoolId = mCourseParams.getSchoolId();
                                classId = null;
                            }
                        }

                        int commitTaskId = studentCommit.getId();
                        String courseId = mCourseParams.getCourseId();
                        String courseName = mCourseParams.getCourseName();
                        TaskSliderHelper.doExerciseTask(
                                activity,
                                answerString,
                                sectionResListVo.getTaskId(),
                                mTaskParams.getMemberId(),
                                resourceId,
                                sectionResListVo.getName(),
                                schoolId,
                                null,
                                classId,
                                null,
                                studentCommit.getStudentName(),
                                commitTaskId, false,
                                courseId,courseName);

                    }

                }
            });

            return;
        }

        if (!EmptyUtil.isEmpty(TaskSliderHelper.onTaskSliderListener)) {
            itemStudentTask = studentCommit;
            int resultRoleType = transferRoleType(roleType);
            if (isAudition) {
                // 如果是试听,点击批阅cell或者查看批阅的时候 都是浏览者
                resultRoleType = RoleType.ROLE_TYPE_VISITOR;
            }

            int scoringRule = mLqTaskCommitListVo.getTaskInfo().getScoringRule();
            scoringRule = 2;

            // 设置CourseId 和 CourseName信息
            task.setCourseId(mCourseParams.getCourseId());
            task.setCourseName(mCourseParams.getCourseName());

            // 班级学程中必传
            String schoolId = mCourseParams.getSchoolId();
            String classId = mCourseParams.getClassId();

            // 如果从大厅进来，提交的时候需要传绑定的机构班级
            /*if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
                schoolId = mCourseParams.getBindSchoolId();
                classId = mCourseParams.getBindClassId();
            } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
                // 学程馆学习任务入口
                // 课程发生了绑定
                // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
                // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
                if (mCourseParams.isBindClass()) {
                    if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                        // 学程馆Id和绑定的Id,相等
                        schoolId = mCourseParams.getBindSchoolId();
                        classId = mCourseParams.getBindClassId();
                    }
                } else {
                    schoolId = mCourseParams.getSchoolId();
                    classId = null;
                }
            }*/

            task.setClassId(classId);
            task.setClassName(mCourseParams.getClassName());
            TaskSliderHelper.onTaskSliderListener.checkMarkTaskDetail(activity, resultRoleType,
                    task, studentCommit, isCheckMark, sourceType, scoringRule, isAudition);
        }
    }

    private void share(TaskInfoVo vo) {
        if (vo == null) {
            return;
        }
        String url = vo.getShareurl();
        //分享地址为空就没得玩了
        if (!StringUtils.isValidString(url)) {
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        String title = vo.getNickname();
        String thumbnail = vo.getImgurl();
        String schoolName = sectionResListVo != null ? sectionResListVo.getOriginName() : "";
        String className = "";
        String description = schoolName + className;
        shareInfo.setTitle(title);
        shareInfo.setContent(description);
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(thumbnail)) {
            umImage = new UMImage(activity, AppSettings.getFileUrl(thumbnail));
        } else {
            umImage = new UMImage(activity, R.drawable.default_cover);
        }
        shareInfo.setuMediaObject(umImage);
        //蛙蛙好友分享资源
        SharedResource resource = new SharedResource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setShareUrl(url);
        if (!TextUtils.isEmpty(thumbnail)) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(thumbnail));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(activity);
        shareUtils.share(getRootView(), shareInfo);
    }

    protected View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    private DownloadService downloadService;
    private ServiceConnection downloadServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.DownloadBinder) service).getService();
        }
    };

    @Override
    protected void doTask() {
        if (sectionResListVo != null) {
            int resType = sectionResListVo.getResType();
            if (resType > 10000) {
                resType -= 10000;
            }
            if (sectionResListVo.getTaskType() == 2) {//复述微课
                if (resType == ResType.RES_TYPE_OLD_COURSE || resType == ResType.RES_TYPE_COURSE
                        || resType == ResType.RES_TYPE_COURSE_SPEAKER) {//有声相册
                    DoCourseHelper doCourseHelper = new DoCourseHelper(activity, downloadService);
                    NewResourceInfo newResourceInfo = new NewResourceInfo();
                    newResourceInfo.setTitle(sectionResListVo.getName());
                    newResourceInfo.setResourceId(sectionResListVo.getResId() + "-" + sectionResListVo.getResType());
                    newResourceInfo.setScreenType(sectionResListVo.getScreenType());
                    newResourceInfo.setResourceUrl(sectionResListVo.getResourceUrl());
                    activity.getIntent().putExtra("orientation", sectionResListVo.getScreenType());
                    doCourseHelper.doRemoteLqCourse(newResourceInfo, DoCourseHelper.FromType.Do_Retell_Course, true);
                } else if (resType == 18) {//点读
                    //retellOnePageCourse(sectionResListVo.getResId() + "-" + sectionResListVo.getResType());

                    activity.getIntent().putExtra("orientation", sectionResListVo.getScreenType());
                    TaskSliderHelper.doTask(activity, "" + sectionResListVo.getResId(), getSourceType());
                } else if (resType == ResType.RES_TYPE_PDF
                        || resType == ResType.RES_TYPE_PPT
                        || resType == ResType.RES_TYPE_IMG
                        || resType == ResType.RES_TYPE_DOC) {///pdf/ppt/Doc/图片
                    if (newInfoTag != null) {
                        List<String> imageList = new ArrayList<>();
                        List<NewResourceInfo> splitInfo = newInfoTag.getSplitInfoList();
                        if (splitInfo != null && splitInfo.size() > 0) {
                            for (int i = 0; i < splitInfo.size(); i++) {
                                NewResourceInfo info = splitInfo.get(i);
                                imageList.add(info.getResourceUrl());
                            }
                        }
                        selectOrientation(imageList, newInfoTag);
                    } else {
                        TipMsgHelper.ShowLMsg(SectionTaskDetailsActivityEx.this, R.string
                                .please_retry_for_resource);
                    }
                }
            } else if (sectionResListVo.getTaskType() == 3) {
                if (sectionResListVo.isAutoMark()) {
                    // ShowWarning
                    // boolean ignore = SPUtil.getInstance().getBoolean(SharedConstant.KEY_AUTO_MARK_WARNING);
                    boolean hasEnabled = DemoApplication.getInstance().getPrefsManager()
                            .isDoTaskOrderTipsEnabled();
                    if (/*!ignore && */!hasEnabled) {
                        // 没有check 弹出框
                        Dialog doTaskTipDialog = new DoTaskOrderTipsDialog(this, new CallbackListener() {
                            @Override
                            public void onBack(Object result) {
                                //答题卡
                                doAutoMarkTask();
                            }
                        });
                        doTaskTipDialog.setCancelable(true);
                        doTaskTipDialog.show();
                        return;
                    }

                    doAutoMarkTask();
                } else {
                    // TODO 做任务单
                    // retellOnePageCourse(sectionResListVo.getResId() + "-" + sectionResListVo.getResType());
                    activity.getIntent().putExtra("orientation", sectionResListVo.getScreenType());
                    TaskSliderHelper.doTask(activity, "" + sectionResListVo.getResId(), getSourceType());
                }
            }
        }
    }

    /**
     * 做自动批阅的读写单
     */
    private void doAutoMarkTask() {
        // 自动批阅的读写单
        String resourceId = sectionResListVo.getResId() + "-" + sectionResListVo.getResType();
        LearningTaskHelper.requestResourceDetailById(resourceId, true, new DataSource.Callback<LQResourceDetailVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(LQResourceDetailVo vo) {
                if (EmptyUtil.isEmpty(vo.getExercise())) return;
                String answerString = JSON.toJSONString(vo.getExercise());
                // 班级学程中必传
                String schoolId = mCourseParams.getSchoolId();
                String classId = mCourseParams.getClassId();

                // 如果从大厅进来，提交的时候需要传绑定的机构班级
                if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
                    schoolId = mCourseParams.getBindSchoolId();
                    classId = mCourseParams.getBindClassId();
                } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
                    // 学程馆学习任务入口
                    // 课程发生了绑定
                    // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
                    // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
                    if (mCourseParams.isBindClass()) {
                        if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                            // 学程馆Id和绑定的Id,相等
                            schoolId = mCourseParams.getBindSchoolId();
                            classId = mCourseParams.getBindClassId();
                        }
                    } else {
                        schoolId = mCourseParams.getSchoolId();
                        classId = null;
                    }
                }

                String courseId = mCourseParams.getCourseId();
                String courseName = mCourseParams.getCourseName();
                TaskSliderHelper.doExerciseTask(
                        activity,
                        answerString,
                        sectionResListVo.getTaskId(),
                        mTaskParams.getMemberId(),
                        resourceId,
                        sectionResListVo.getName(),
                        schoolId,
                        null,
                        classId,
                        null,
                        null, 0, true,
                        courseId,courseName);
            }
        });
    }

    @Override
    protected void doSpeechEvaluation() {
        super.doSpeechEvaluation();
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo) || EmptyUtil.isEmpty(sectionResListVo)) return;
        int orientation = sectionResListVo.getScreenType();
        String resId = sectionResListVo.getResId() + "-" + sectionResListVo.getResType();
        String taskId = sectionResListVo.getTaskId();
        // String taskName = sectionResListVo.getTaskName();
        String taskName = sectionResListVo.getName();
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo.getTaskInfo())) return;
        // 打分规则
        int scoringRule = mLqTaskCommitListVo.getTaskInfo().getScoringRule();
        scoringRule = 2;
        // 班级学程中必传
        String schoolId = mCourseParams.getSchoolId();
        String classId = mCourseParams.getClassId();

        // 如果从大厅进来，提交的时候需要传绑定的机构班级
        if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
            schoolId = mCourseParams.getBindSchoolId();
            classId = mCourseParams.getBindClassId();
        } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
            // 学程馆学习任务入口
            // 课程发生了绑定
            // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
            // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
            if (mCourseParams.isBindClass()) {
                if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                    // 学程馆Id和绑定的Id,相等
                    schoolId = mCourseParams.getBindSchoolId();
                    classId = mCourseParams.getBindClassId();
                }
            } else {
                schoolId = mCourseParams.getSchoolId();
                classId = null;
            }
        }

        SpeechAssessmentActivity.start(this,
                orientation,
                resId, taskId,
                taskName, scoringRule,
                schoolId, null,
                classId, null,
                true);
    }


    /**
     * 在线打开语音评测
     */
    private void openSpeechEvaluation(@NonNull LqTaskCommitVo vo) {
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo) || EmptyUtil.isEmpty(sectionResListVo)) return;
        int orientation = sectionResListVo.getScreenType();
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo.getTaskInfo())) return;
        String studentResUrl = vo.getStudentResUrl();
        // 打分规则
        int scoringRule = mLqTaskCommitListVo.getTaskInfo().getScoringRule();
        scoringRule = 2;
        SpeechAssessmentActivity.start(this, orientation, studentResUrl, scoringRule);
    }

    /**
     * 去老师点评页面
     *
     * @param vo 点击的实体
     */
    private void openTeacherReview(@NonNull LqTaskCommitVo vo) {
        if (EmptyUtil.isEmpty(sectionResListVo) || EmptyUtil.isEmpty(mLqTaskCommitListVo)) return;
        int taskId = vo.getId();
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo.getTaskInfo())) return;
        // 打分规则
        int scoringRule = mLqTaskCommitListVo.getTaskInfo().getScoringRule();
        scoringRule = 2;
        // 自动测评分数
        String taskScore = vo.getTaskScore();
        TeacherReviewDetailActivity.start(this, Integer.toString(taskId), null, scoringRule, taskScore);
    }

    /**
     * 打开老师点评详情页
     *
     * @param vo 点击的实体
     */
    private void openTeacherReviewDetail(@NonNull LqTaskCommitVo vo) {
        if (EmptyUtil.isEmpty(sectionResListVo) || EmptyUtil.isEmpty(mLqTaskCommitListVo)) return;


        // 是否老师点评
        boolean isHasVoiceReview = vo.isHasVoiceReview();
        int handleRole = mTaskParams.getHandleRole();
        // 是否有点评的权限
        // 主编小编,任务的创建者
        boolean isReviewPermission = handleRole == UserHelper.MoocRoleType.TEACHER
                || handleRole == UserHelper.MoocRoleType.EDITOR
                || TextUtils.equals(sectionResListVo.getCreateId(), UserHelper.getUserId());
        // 自动测评每页的分数
        ArrayList<Integer> autoEvalArray = vo.buildAutoEvalList();
        // 老师点评的分数,老师点评的评语
        String taskScore = vo.getTaskScore();
        String taskScoreReMark = vo.getTaskScoreRemark();
        String taskId = Integer.toString(vo.getId());
        if (EmptyUtil.isEmpty(mLqTaskCommitListVo.getTaskInfo())) return;
        // 打分规则
        int scoringRule = mLqTaskCommitListVo.getTaskInfo().getScoringRule();
        scoringRule = 2;
        // 方向
        int orientation = sectionResListVo.getScreenType();
        TeacherReviewDetailActivity.start(this,
                isHasVoiceReview, isReviewPermission,
                autoEvalArray, taskScore, taskScoreReMark,
                scoringRule, orientation, vo.getStudentResUrl(), taskId, null);
    }

    @Override
    protected void checkSpeechTaskDetail(@NonNull LqTaskCommitVo vo, boolean isCheckImmediate) {
        super.checkSpeechTaskDetail(vo, isCheckImmediate);
        // 处理过的角色
        int resultRoleType = transferRoleType(mHandleRole);
        if (isAudition) {
            // 如果是试听,点击批阅cell或者查看批阅的时候 都是浏览者
            resultRoleType = RoleType.ROLE_TYPE_VISITOR;
        }

        boolean isReviewPermission =
                mHandleRole == UserHelper.MoocRoleType.TEACHER
                        || mHandleRole == UserHelper.MoocRoleType.EDITOR
                        || TextUtils.equals(sectionResListVo.getCreateId(), UserHelper.getUserId());

        if (isReviewPermission) {
            // 老师
            // 如果已经点评，那都是跳转点评的详情页
            // 如果还未点评，点击cell是语音评测自动测评，点击立即点评
            if (vo.isHasVoiceReview()) {
                // 点击老师点评 打开详情页
                openTeacherReviewDetail(vo);
            } else {
                if (isCheckImmediate) {
                    // 点击立即点评
                    openTeacherReview(vo);
                } else {
                    // 点击Cell 去打开点评详情页
                    openTeacherReviewDetail(vo);
                }
            }
        } else {
            // 学生 不管点Cell 还是点老师点评,都是进入点评的详情页
            // 点击老师点评 打开详情页
            openTeacherReviewDetail(vo);
        }

        /*if(isCheckImmediate){
            if(vo.isHasVoiceReview()){
                // 点击老师点评 打开详情页
                openTeacherReviewDetail(vo);
            }else{
                // 点击立即点评
                openTeacherReview(vo);
            }
        }else{
            // 点击cell 在线打开语音评测
            openSpeechEvaluation(vo);
        }*/
    }

    @Override
    protected void doStatisticalScores(@NonNull List<LqTaskCommitVo> data) {
        super.doStatisticalScores(data);
        if (EmptyUtil.isEmpty(sectionResListVo) || EmptyUtil.isEmpty(mLqTaskCommitListVo)) return;
        // 用未拆分的数据进行统计
        data = mLqTaskCommitListVo.getListCommitTaskOnline();

        // 复述课件用来过滤到最高分的提交记录的Map集合 Key = studentId
        Map<String, CommitTask> retellFilterMap = new HashMap<>();
        // 语音评测用来过滤到最高分的提交记录的Map集合 Key = studentId
        Map<String, CommitTask> evalFilterMap = new HashMap<>();

        if (EmptyUtil.isNotEmpty(data)) {
            // 循环遍历提交列表，复述课件提交和语音评测
            for (LqTaskCommitVo vo : data) {
                if(sectionResListVo.isAutoMark() &&
                        EmptyUtil.isNotEmpty(vo.getStudentResId())){
                    // 过滤人工批阅的
                    continue;
                }

                CommitTask commitTask = CommitTask.buildVo(vo);
                commitTask.setCommitTaskId(commitTask.getId());
                if (EmptyUtil.isEmpty(commitTask.getTaskScore())) continue;
                String studentId = vo.getStudentId();
                if (vo.isSpeechEvaluation()) {
                    CommitTask maxCommit = evalFilterMap.get(studentId);
                    if (EmptyUtil.isEmpty(maxCommit)) {
                        // 第一次提交，put
                        evalFilterMap.put(studentId, commitTask);
                    } else {
                        // 不是第一次提交,需要进行比较分数高低
                        if (EmptyUtil.isEmpty(commitTask.getTaskScore()) ||
                                EmptyUtil.isEmpty(maxCommit.getTaskScore())) {
                            continue;
                        }
                        if ((Double.parseDouble(commitTask.getTaskScore()) >
                                Double.parseDouble(maxCommit.getTaskScore()))) {
                            // 如果遍历到的提交记录分数大于之前保存的，覆盖保存
                            evalFilterMap.put(studentId, commitTask);
                        }
                    }
                } else {
                    // 批阅CEll
                    CommitTask maxCommit = retellFilterMap.get(studentId);
                    if (EmptyUtil.isEmpty(maxCommit)) {
                        // 第一次提交，put
                        if (sectionResListVo.isAutoMark() || vo.isHasCommitTaskReview()) {
                            // 有批阅 自动批阅的读写单，或者正常的读写单但已经是批阅过后的
                            retellFilterMap.put(studentId, commitTask);
                        }
                    } else {
                        // 不是第一次提交,需要进行比较分数高低
                        if (EmptyUtil.isEmpty(commitTask.getTaskScore()) ||
                                EmptyUtil.isEmpty(maxCommit.getTaskScore())) {
                            continue;
                        }
                        if ((Double.parseDouble(commitTask.getTaskScore()) >
                                Double.parseDouble(maxCommit.getTaskScore()))) {
                            // 如果遍历到的提交记录分数大于之前保存的，覆盖保存
                            if (sectionResListVo.isAutoMark() || vo.isHasCommitTaskReview()) {
                                // 有批阅 自动批阅的读写单，或者正常的读写单但已经是批阅过后的
                                retellFilterMap.put(studentId, commitTask);
                            }
                        }
                    }
                }
            }
        }

        // 获取到提交作业学程的最高提交记录集合
        Collection<CommitTask> retellCollections = retellFilterMap.values();
        Collection<CommitTask> evalCollections = evalFilterMap.values();
        ArrayList<CommitTask> retellArrays = new ArrayList<>(retellCollections);
        ArrayList<CommitTask> evalArrays = new ArrayList<>(evalCollections);
        int classAllMemberCount = mLqTaskCommitListVo.getStudentCount();

        if (EmptyUtil.isEmpty(mLqTaskCommitListVo.getTaskInfo())) return;
        // 打分规则
        int scoringRule = mLqTaskCommitListVo.getTaskInfo().getScoringRule();
        scoringRule = 2;
        // 处理过的角色
        int resultRoleType = transferRoleType(mHandleRole);
        // 是否是主编
        boolean isOnlineReporter = resultRoleType == RoleType.ROLE_TYPE_EDITOR;
        // 是否是小编
        boolean isOnlineHost = resultRoleType == RoleType.ROLE_TYPE_TEACHER;

        // 成绩统计 主编
        if (resultRoleType == RoleType.ROLE_TYPE_EDITOR) {
            // 成绩统计没有主编概念
            resultRoleType = RoleType.ROLE_TYPE_TEACHER;
        }
        if (isAudition) {
            // 如果是试听,点击批阅cell或者查看批阅的时候 都是浏览者
            resultRoleType = RoleType.ROLE_TYPE_VISITOR;
        }

        final int cardRoleType = resultRoleType;
        final int cardScoringRule = scoringRule;

        // 是否支持语音评测
        boolean hasEvalAssessment =
                SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(sectionResListVo.getResProperties()) &&
                        sectionResListVo.getTaskType() == 2;

        // 是否是自动批阅
        boolean hasAutoMark = sectionResListVo.isAutoMark();

        // 是否是特殊类型
        boolean resultMark = hasEvalAssessment || hasAutoMark;

        /*enterScoreStatisticsActivity(this,retellArrays,evalArrays,classAllMemberCount,
                resultRoleType,scoringRule,resultMark,null);*/

        if (sectionResListVo.getTaskType() == 3 && sectionResListVo.isAutoMark()) {
            // 做任务单
            final String resourceId = sectionResListVo.getResId() + "-" + sectionResListVo.getResType();
            LearningTaskHelper.requestResourceDetailById(resourceId, true, new DataSource.Callback<LQResourceDetailVo>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(LQResourceDetailVo vo) {
                    ExerciseAnswerCardParam cardParam = new ExerciseAnswerCardParam();
                    //答题卡返回的信息
                    List<LQResourceDetailVo.DataBean> data = vo.getData();
                    if (EmptyUtil.isEmpty(vo.getExercise())) return;
                    String answerString = JSON.toJSONString(vo.getExercise());

                    if (EmptyUtil.isNotEmpty(data)) {
                        LQResourceDetailVo.DataBean dataBean = data.get(0);
                        cardParam.setExerciseTotalScore(dataBean.getPoint());
                        cardParam.setResId(resourceId);
                        cardParam.setScreenType(sectionResListVo.getScreenType());
                        cardParam.setExerciseAnswerString(answerString);
                        cardParam.setTaskId(sectionResListVo.getTaskId());
                        cardParam.setRoleType(cardRoleType);
                        cardParam.setIsOnlineHost(isOnlineHost);
                        cardParam.setIsOnlineReporter(isOnlineReporter);
                        // cardParam.setCommitTaskTitle(sectionResListVo.getTaskName());
                        cardParam.setCommitTaskTitle(sectionResListVo.getName());
                        cardParam.setIsHeadMaster(false);
                        cardParam.setFromOnlineStudyTask(true);


                        // 班级学程中必传
                        String schoolId = mCourseParams.getSchoolId();
                        String classId = mCourseParams.getClassId();

                        // 如果从大厅进来，提交的时候需要传绑定的机构班级
                        if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
                            schoolId = mCourseParams.getBindSchoolId();
                            classId = mCourseParams.getBindClassId();
                        } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
                            // 学程馆学习任务入口
                            // 课程发生了绑定
                            // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
                            // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
                            if (mCourseParams.isBindClass()) {
                                if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                                    // 学程馆Id和绑定的Id,相等
                                    schoolId = mCourseParams.getBindSchoolId();
                                    classId = mCourseParams.getBindClassId();
                                }
                            } else {
                                schoolId = mCourseParams.getSchoolId();
                                classId = null;
                            }
                        }

                        cardParam.setSchoolId(schoolId);
                        cardParam.setClassId(classId);

                        enterScoreStatisticsActivity(SectionTaskDetailsActivityEx.this, retellArrays, evalArrays, classAllMemberCount,
                                cardRoleType, cardScoringRule, hasEvalAssessment, cardParam);
                    }

                }
            });
        } else {
            // 听说课
            enterScoreStatisticsActivity(this, retellArrays, evalArrays, classAllMemberCount,
                    resultRoleType, scoringRule, hasEvalAssessment, null);
        }
    }

    /**
     * @param context
     * @param retellCourseList    复述课件的数据list
     * @param evalCourseList      语音评测的数据list
     * @param classAllMemberCount 班级的总人数
     * @param roleType            角色信息
     * @param scoreRule           ScoreRule
     */
    private void enterScoreStatisticsActivity(Context context,
                                              ArrayList<CommitTask> retellCourseList,
                                              ArrayList<CommitTask> evalCourseList,
                                              int classAllMemberCount,
                                              int roleType,
                                              int scoreRule,
                                              boolean hasEvalAssessment,
                                              ExerciseAnswerCardParam cardParam) {
        ScoreStatisticsActivity.start(this, retellCourseList, evalCourseList, classAllMemberCount,
                roleType, scoreRule, hasEvalAssessment, cardParam);
    }

    private void analysisRetellCourseData() {
        if (sectionResListVo == null) {
            return;
        }
        if (sectionResListVo.getResType() == ResType.RES_TYPE_PDF
                || sectionResListVo.getResType() == ResType.RES_TYPE_PPT
                || sectionResListVo.getResType() == ResType.RES_TYPE_DOC) {
            JSONObject jsonObject = new JSONObject();
            try {
                if (sectionResListVo != null) {
                    jsonObject.put("resId", "" + sectionResListVo.getResId() + "-" + sectionResListVo.getResType());
                }
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
                        if (sectionResListVo.getResType() > ResType.RES_TYPE_BASE) {
                            sectionResListVo.setResType(sectionResListVo.getResType() % ResType.RES_TYPE_BASE);
                        }
                        if (splitList.size() > 0) {
                            for (int i = 0; i < splitList.size(); i++) {
                                SplitCourseInfo splitCourse = splitList.get(i);
                                NewResourceInfo newResourceInfo = new NewResourceInfo();
                                newResourceInfo.setTitle(splitCourseInfo.get(0).getOriginname());
                                newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getPlayUrl()));
                                newResourceInfo.setResourceId(sectionResListVo.getResId());
                                newResourceInfo.setResourceType(sectionResListVo.getResType());
                                newResourceInfo.setAuthorId(sectionResListVo.getCreateId());
                                resourceInfoList.add(newResourceInfo);
                            }
                        }
                        if (newInfoTag == null) {
                            newInfoTag = new NewResourceInfoTag();
                            if (resourceInfoList.size() > 0) {
                                newInfoTag.setTitle(isLive ? sectionResListVo.getName() : sectionResListVo.getOriginName());
                                newInfoTag.setResourceUrl(resourceInfoList.get(0).getResourceUrl());
                                newInfoTag.setDescription("");
                                newInfoTag.setSplitInfoList(resourceInfoList);
                            }
                        }
                        //更新缩略图
                        // @date   :2018/4/14 0014 上午 10:58
                        // @func   :V5.5 Mooc改版 批阅页面更改优化
                        /*if (coverIv != null) {
                            x.image().bind(coverIv,AppSettings.getFileUrl(
                                    newInfoTag.getResourceUrl()), imageOptions);
                        }*/
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
            //更新缩略图
            // @func   :V5.5 Mooc改版 批阅页面更改优化
           /* if (coverIv != null && newInfoTag != null) {
                x.image().bind(coverIv,AppSettings.getFileUrl(
                        newInfoTag.getResourceUrl()), imageOptions);
            }*/
        } else if (sectionResListVo.getResType() == ResType.RES_TYPE_IMG) {
            //图片的显示
            String resUrl = sectionResListVo.getResourceUrl();
            String resId = sectionResListVo.getResId();
            String authorId = sectionResListVo.getCreateId();
            if (newInfoTag == null) {
                newInfoTag = new NewResourceInfoTag();
                List<NewResourceInfo> resourceInfoList = new ArrayList<>();
                NewResourceInfo tempInfo;
                if (resUrl.contains(",")) {
                    String[] resUrlArray = resUrl.split(",");
                    String[] resIdArray = resId.split(",");
                    String[] authorIdArray = authorId.split(",");
                    for (int i = 0; i < resUrlArray.length; i++) {
                        tempInfo = new NewResourceInfo();
                        tempInfo.setTitle(sectionResListVo.getOriginName());
                        tempInfo.setResourceUrl(AppSettings.getFileUrl(resUrlArray[i]));
                        tempInfo.setResourceId(resIdArray[i]);
                        tempInfo.setResourceType(sectionResListVo.getResType());
                        tempInfo.setAuthorId(authorIdArray[i]);
                        resourceInfoList.add(tempInfo);
                    }
                } else {
                    tempInfo = new NewResourceInfo();
                    tempInfo.setTitle(sectionResListVo.getOriginName());
                    tempInfo.setResourceUrl(AppSettings.getFileUrl(resUrl));
                    tempInfo.setResourceId(resId);
                    tempInfo.setResourceType(sectionResListVo.getResType());
                    tempInfo.setAuthorId(authorId);
                    resourceInfoList.add(tempInfo);
                }
                newInfoTag.setTitle(isLive ? sectionResListVo.getName() : sectionResListVo.getOriginName());
                newInfoTag.setResourceUrl(resourceInfoList.get(0).getResourceUrl());
                newInfoTag.setDescription("");
                newInfoTag.setSplitInfoList(resourceInfoList);
            }
            //更新缩略图
            // @func   :V5.5 Mooc改版 批阅页面更改优化
            /*if (coverIv != null) {
                x.image().bind(coverIv,AppSettings.getFileUrl(
                        newInfoTag.getResourceUrl()), imageOptions);
            }*/
        }
    }


    @Override
    protected void clickCommitListItem(SectionTaskCommitListVo vo) {
        if (ButtonUtils.isFastDoubleClick()) {
            return;
        }
        UserInfo userInfo =
                ((MyApplication) (MainApplication.getInstance())).getUserInfo();
        StudyTask task = new StudyTask();
        task.setResId(vo.getId());
        task.setResUrl(/*data.getResourcePath()*/ "");
        task.setResThumbnailUrl(vo.getThumbnail());
        task.setTaskTitle(vo.getName());
        task.setCollectSchoolId(getIntent().getStringExtra("schoolId"));
        PassParamhelper mParam = new PassParamhelper();
        mParam.isFromLQMOOC = true;
        Bundle bundle = new Bundle();
        bundle.putSerializable(PassParamhelper.class.getSimpleName(), mParam);
        CourseOpenUtils.openCourseDetailsDirectly(activity, task,
                TextUtils.equals(UserHelper.getUserId(),
                        activity.getIntent().getStringExtra("memberId"))
                        ? RoleType.ROLE_TYPE_STUDENT : RoleType.ROLE_TYPE_PARENT,
                userInfo.getMemberId(),
                activity.getIntent().getStringExtra("memberId"),
                userInfo, false, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "" + requestCode);
        if (requestCode == ResourceBaseFragment.REQUEST_CODE_RETELLCOURSE || requestCode == 105) {
            if (data != null) {
                String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                String title = data.getStringExtra(SlideManager.LOAD_FILE_TITLE);
                //复述课件的制作时间
                int viewTotalTime = TimerUtils.getInstance().getCurrentTotalTime();
                if (sectionResListVo.getResType() == ResType.RES_TYPE_ONEPAGE
                        || sectionResListVo.getResType() == ResType.RES_TYPE_STUDY_CARD) {
                    if (!TextUtils.isEmpty(slidePath)) {
                        LocalCourseInfo info = getLocalCourseInfo(slidePath);
                        if (info != null) {
                            uploadCourse(info, title);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                        LocalCourseInfo info = getLocalCourseInfo(coursePath);
                        if (info != null) {
                            uploadCourse(info, title);
                        }
                    } else if (!TextUtils.isEmpty(slidePath)) {
                        //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                        if (slidePath.endsWith(File.separator)) {
                            slidePath = slidePath.substring(0, slidePath.length() - 1);
                        }
                        LocalCourseDTO.deleteLocalCourseByPath(activity, UserHelper.getUserId(),
                                slidePath, true);
                    }
                }
            }
        }
    }

    private void uploadCourse(final LocalCourseInfo info, final String title) {
        showProgressDialog(getText(R.string.label_uploading_course_files).toString());
        FileZipHelper.ZipUnzipParam param =
                new FileZipHelper.ZipUnzipParam(
                        info.mPath, Utils.TEMP_FOLDER + Utils.getFileNameFromPath
                        (info.mPath) + Utils.COURSE_SUFFIX);
        FileZipHelper.zip(param, new FileZipHelper.ZipUnzipFileListener() {
            @Override
            public void onFinish(FileZipHelper.ZipUnzipResult result) {
                if (result != null && result.mIsOk) {
                    zipFilePath = result.mParam.mOutputPath;
                    if (TextUtils.isEmpty(title)) {
                        return;
                    }
                    uploadCourseFile(zipFilePath, title, info.mDuration, info.mDescription);
                }
            }
        });
    }

    private LocalCourseInfo getLocalCourseInfo(String coursePath) {
        LocalCourseInfo result = null;
        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        try {
            List<LocalCourseDTO> dtos = localCourseDao.getLocalCourseByPath
                    (UserHelper.getUserId(), coursePath);
            if (dtos != null && dtos.size() > 0) {
                LocalCourseDTO localCourseDTO = dtos.get(0);
                String path = localCourseDTO.getmPath();
                int pageCount = localCourseDTO.getmPageCount();
                long lastModifyTime = localCourseDTO.getmLastModifiedTime();
                if (!TextUtils.isEmpty(path) && new File(path).exists()) {
                    result = new LocalCourseInfo(
                            path, localCourseDTO.getmParentPath
                            (), localCourseDTO.getmCurrentPage(), pageCount, lastModifyTime,
                            CourseType.COURSE_TYPE_LOCAL);
                    result.mOrientation = localCourseDTO.getmOrientation();
                    result.mDescription = localCourseDTO.getmDescription();
                    result.mDuration = localCourseDTO.getmDuration();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 打开ppt 或者 pdf 的资源
     */
    private void openPptAndPdf() {
        if (newInfoTag != null) {
            List<NewResourceInfo> splitList = newInfoTag.getSplitInfoList();
            ArrayList<ImageInfo> imageItemInfos = new ArrayList<>();
            if (splitList != null && splitList.size() > 0) {
                if (splitList != null && splitList.size() > 0) {
                    for (int i = 0; i < splitList.size(); i++) {
                        NewResourceInfo splitCourse = splitList.get(i);
                        ImageInfo newResourceInfo = new ImageInfo();
                        newResourceInfo.setTitle(isLive ? sectionResListVo.getName() : sectionResListVo.getOriginName());
                        newResourceInfo.setResourceUrl(AppSettings.getFileUrl(splitCourse.getResourceUrl()));
                        newResourceInfo.setResourceId(splitCourse.getResourceId());
                        newResourceInfo.setResourceType(sectionResListVo.getResType());
                        newResourceInfo.setAuthorId(splitCourse.getAuthorId());
                        imageItemInfos.add(newResourceInfo);
                    }
                }
            }
            if (imageItemInfos.size() > 1) {
                GalleryActivity.newInstance(activity, imageItemInfos, true, 0, false, true, false);
            } else {
                GalleryActivity.newInstance(activity, imageItemInfos, false, 0, false, true, false);
            }
        } else {
            TipMsgHelper.ShowLMsg(SectionTaskDetailsActivityEx.this, R.string
                    .please_retry_for_resource);
        }
    }

    /**
     * 打开图片
     */
    public void openImage(String courseId) {
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
                            processOpenImageData(courseData);
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
                    processOpenImageData(courseData);
                }
            });
        }
    }

    /**
     * 打开图片逻辑
     *
     * @param courseData
     */
    private void processOpenImageData(CourseData courseData) {
        if (courseData != null) {
            int resType = courseData.type % ResType.RES_TYPE_BASE;

            PlaybackParam mParam = new PlaybackParam();
            //隐藏收藏按钮
            mParam.mIsHideCollectTip = true;
            //受保护的资源
            courseData.setIsPublicRes(false);

            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.playOnlineCourse(activity, courseData.getCourseInfo(), false, null);
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                //直接打开，不带编辑。
                ActivityUtils.openOnlineOnePage(activity, courseData.getNewResourceInfo(), true, null);
            } else if (resType == ResType.RES_TYPE_ONEPAGE) {
                ActivityUtils.openOnlineOnePage(activity, courseData.getNewResourceInfo(), true, null);
            } else if (resType == ResType.RES_TYPE_NOTE) {
                //直接打开帖子
                if (!TextUtils.isEmpty(courseData.resourceurl)) {
                    ActivityUtils.openOnlineNote(activity, courseData.getCourseInfo(), false, true);
                }
            }
        }
    }

    private void selectOrientation(final List<String> imageList, final NewResourceInfo newResourceInfo) {
        OrientationSelectDialog dialog = new OrientationSelectDialog(activity,
                new OrientationSelectDialog.SelectHandler() {
                    DoCourseHelper doCourseHelper = new DoCourseHelper(activity);

                    @Override
                    public void orientationSelect(int orientation) {
                        activity.getIntent().putExtra("orientation", orientation);
                        doCourseHelper.doRemoteLqCourseFromImage(imageList, newResourceInfo, orientation,
                                DoCourseHelper.FromType.Do_Retell_Course, true);
                    }
                });
        dialog.show();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();

        WindowManager m = activity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = null; // 获取对话框当前的参数值
        if (window != null) {
            p = window.getAttributes();
            window.setGravity(Gravity.CENTER);
            p.width = (int) (d.getWidth() * 0.75f);
            p.height = (int) (d.getHeight() * 0.3);
            window.setAttributes(p);
        }
    }

    @Override
    protected void commitStudentStudyTask(final TaskUploadBackVo taskUploadBackVo, final int roleType) {
        if (taskUploadBackVo == null || sectionResListVo == null) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("TaskId", sectionResListVo.getTaskId());
        params.put("StudentId", UserHelper.getUserId());
        params.put("StudentResId", taskUploadBackVo.getId() + "-" + taskUploadBackVo.getType());
        params.put("StudentResUrl", taskUploadBackVo.getResourceurl());
        params.put("StudentResTitle", taskUploadBackVo.getNickname());

        String schoolId = mCourseParams.getSchoolId();
        String classId = mCourseParams.getClassId();

        // 如果从大厅进来，提交的时候需要传绑定的机构班级
        if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_MOOC_ENTER) {
            schoolId = mCourseParams.getBindSchoolId();
            classId = mCourseParams.getBindClassId();
        } else if (mCourseParams.getCourseEnterType() == CourseDetailType.COURSE_DETAIL_SCHOOL_ENTER) {
            // 学程馆学习任务入口
            // 课程发生了绑定
            // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
            // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
            if (mCourseParams.isBindClass()) {
                if (TextUtils.equals(mCourseParams.getSchoolId(), mCourseParams.getBindSchoolId())) {
                    // 学程馆Id和绑定的Id,相等
                    schoolId = mCourseParams.getBindSchoolId();
                    classId = mCourseParams.getBindClassId();
                }
            } else {
                schoolId = mCourseParams.getSchoolId();
                classId = null;
            }
        }

        if (EmptyUtil.isNotEmpty(schoolId)) {
            params.put("SchoolId", schoolId);
        }

        if (EmptyUtil.isNotEmpty(classId)) {
            params.put("ClassId", classId);
        }

        // 是否是语音评测
        params.put("IsVoiceReview", false);

        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener(this, DataResult.class) {
            @Override
            public void onSuccess(String json) {
                closeProgressDialog();
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    CommitTaskResult taskResult = JSON.parseObject(json, CommitTaskResult.class);
                    if (result != null && result.isSuccess()) {
                        if (!TextUtils.isEmpty(examId)) {
                            //考试提交的做任务单
                            TaskSliderHelper.taskPaperId = examId;
                            TaskSliderHelper.activity = SectionTaskDetailsActivityEx.this;
                            TaskSliderHelper.commitTaskInfo(taskUploadBackVo, getSourceType());
                            // 刷新UI
                            if (!EmptyUtil.isEmpty(mFragment0)) {
                                mFragment0.updateData();

                            }

                            if (!EmptyUtil.isEmpty(mFragment1)) {
                                mFragment1.updateData();
                            }
                        } else {
                            if (roleType != UserHelper.MoocRoleType.EDITOR
                                    && roleType != UserHelper.MoocRoleType.TEACHER) {
                                // Teacher 是小编 Editor 是主编
                                // 不是主编和小编才FlagRead
                               /* if (Double.valueOf(score) > 0) {
                                    SetCommitTaskScore(taskResult.Model.CommitTaskId, taskUploadBackVo);
                                    return;
                                }*/
                                flagRead(taskUploadBackVo, sectionResListVo.getId()
                                        , "" + taskUploadBackVo.getId());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
                closeProgressDialog();
            }
        };
        // listener.setShowLoading(true);
        RequestHelper.sendPostRequest(this, ServerUrl.NEW_PUBLISH_STUDENT_HOMEWORK_URL, params,
                listener);
    }


    /**
     * 上传自动批阅分数
     */
    private void SetCommitTaskScore(int taskId, TaskUploadBackVo taskUploadBackVo) {
        Map<String, Object> params = new HashMap<>();

        params.put("CommitTaskId", taskId);
        params.put("TaskScore", score);
        if (taskUploadBackVo != null) {
            params.put("ResId", taskUploadBackVo.getId() + "-" + taskUploadBackVo.getType());
            params.put("ResUrl", taskUploadBackVo.getResourceurl());
        }
        params.put("AutoEvalCompanyType", schemeId);
        params.put("AutoEvalContent", resultContent);
        RequestHelper.RequestListener<DataResult> listener = new RequestHelper.RequestListener<DataResult>(this, DataResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                if (getResult() != null && getResult().isSuccess()) {
                    //自动批阅分数上传成功
                    // 刷新UI
                    if (!EmptyUtil.isEmpty(mFragment0)) {
                        mFragment0.updateData();

                    }

                    if (!EmptyUtil.isEmpty(mFragment1)) {
                        mFragment1.updateData();
                    }
                }
                score = "0";
            }
        };
        RequestHelper.sendPostRequest(this, ServerUrl.COMMIT_AUTO_MARK_SCORE,
                params, listener);
    }


    /**
     * mooc用户角色转化为两栖蛙蛙用户角色
     *
     * @param moocRoleType
     * @return
     */
    private int transferRoleType(int moocRoleType) {

        int roleType;
        switch (moocRoleType) {
            case UserHelper.MoocRoleType.STUDENT:
                roleType = RoleType.ROLE_TYPE_STUDENT;
                break;
            case UserHelper.MoocRoleType.PARENT:
                roleType = RoleType.ROLE_TYPE_VISITOR;
                break;
            case UserHelper.MoocRoleType.TEACHER:
                roleType = RoleType.ROLE_TYPE_TEACHER;
                break;
            case UserHelper.MoocRoleType.EDITOR:
                roleType = RoleType.ROLE_TYPE_EDITOR;
                break;
            default:
                roleType = RoleType.ROLE_TYPE_VISITOR;
                break;
        }

        return roleType;

    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
        unRegisterResultBroadcast();
    }

    /**
     * 注册广播,接收批阅完成的广播
     */
    private void registerResultBroadcast() {
        if (null == mBroadcastManager) {
            mBroadcastManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
            IntentFilter filter = new IntentFilter(ACTION_MARK_SCORE);
            filter.addAction(ACTION_MARK_EVAL_SCORE);
            mBroadcastManager.registerReceiver(mReceiver, filter);
        }
    }

    /**
     * 取消广播事件的接收
     */
    private void unRegisterResultBroadcast() {
        // LocalBroadcastManager mBroadcastManager = LocalBroadcastManager.getInstance(UIUtil.getContext());
        if (mBroadcastManager != null && mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
            mBroadcastManager = null;
            mReceiver = null;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (EmptyUtil.isEmpty(intent)) return;
            if (ACTION_MARK_SCORE.equals(intent.getAction())) {
                if(sectionResListVo.isAutoMark() &&
                        mOriginalRole == UserHelper.MoocRoleType.STUDENT){
                    // 学生身份, 自动批阅读写单的提交,批阅都广播的这个
                    flagRead(null,sectionResListVo.getId(),sectionResListVo.getResId());
                }

                if (!EmptyUtil.isEmpty(mFragment0)) {
                    // 复述列表刷新UI
                    // 刷新UI
                    if (!EmptyUtil.isEmpty(mFragment0)) {
                        mFragment0.updateData();

                    }
                    if (!TextUtils.isEmpty(examId)) {
                        //任务单考试专用
                        commitExamCheckMarkData();
                    }
                }
            } else if (ACTION_MARK_EVAL_SCORE.equals(intent.getAction())) {
                // 语音评测刷新
                if (!EmptyUtil.isEmpty(mFragment1)) {
                    mFragment1.updateData();
                    Bundle extras = intent.getExtras();
                    if (EmptyUtil.isNotEmpty(extras) && extras.containsKey("commit_resId")) {
                        int resId = intent.getExtras().getInt("commit_resId");
                        String resIdStr = Integer.toString(resId);
                        flagRead(null, sectionResListVo.getId(), resIdStr);
                    }
                }
            }
        }
    };

    private void commitExamCheckMarkData() {
        if (itemStudentTask == null || itemStudentTask.isHasCommitTaskReview()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("token", UserHelper.getUserId());
        //学生的schoolId
        params.put("userId", itemStudentTask.getStudentId());
        String resId = itemStudentTask.getStudentResId();
        if (resId != null && resId.contains("-")) {
            resId = resId.split("-")[0];
        }
        params.put("taskId", resId);
        RequestHelper.RequestResourceResultListener listener = new RequestHelper
                .RequestResourceResultListener(this, DataResult.class) {
            @Override
            public void onSuccess(String json) {
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendGetRequest(this, AppConfig.ServerUrl.GET_USER_EXAM_MARK_TASK_URL, params,
                listener);
    }

    public void updateCommitTaskResId(final String resId, final int resType, final String resTitle, final int screenType,
                                      final String resourceUrl, final String resourceThumbnailUrl, int commitTaskId) {
        Map<String, Object> params = new ArrayMap<>();
        // params.put("CommitTaskId", commitTaskId + "");
        // 新版本更改字段名称
        params.put("CommitTaskOnlineId", commitTaskId + "");
        RequestHelper.RequestDataResultListener<CheckMarkResult> listener = new
                RequestHelper.RequestDataResultListener<CheckMarkResult>(this,
                        CheckMarkResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        CheckMarkInfo result = com.alibaba.fastjson.JSONObject.parseObject(jsonString, CheckMarkInfo.class);
                        if (result != null && result.getModel() != null && result.getModel().size() > 0) {
                            openCourseWareDetails(result.getModel().get(0).getResId(), resType, resTitle,
                                    screenType,
                                    resourceUrl,
                                    resourceThumbnailUrl);
                        } else {
                            openCourseWareDetails(resId, resType, resTitle, screenType, resourceUrl, resourceThumbnailUrl);
                        }
                    }
                };

        RequestHelper.sendPostRequest(this, ServerUrl.GET_LOADCOMMITTASKREVIEWLIST, params, listener);
    }

    @Override
    protected void openSpeechEvaluationCourseWareDetails(@NonNull LqTaskCommitVo vo) {
        super.openSpeechEvaluationCourseWareDetails(vo);
        // 打开课件详情
        openSpeechEvaluation(vo);

        // 学程馆学习任务入口
        // 课程发生了绑定
        // 如果绑定的机构Id等于学程馆的Id 提交和列表都是用学程馆的机构Id， 只有提交才传ClassId
        // 如果绑定的机构Id不等于学程馆的Id 列表用学程馆的Id
    }

    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(COMMIT_AUTO_MARK_SCORE_ACTION)) {
                score = getScore(intent.getStringExtra("score"));
                schemeId = intent.getIntExtra("schemeId", 1);
                resultContent = intent.getStringExtra("result");
            }
        }
    }


    private String getScore(String score) {
        if (score.contains(".")) {
            String[] split = score.split("\\.");
            return split[0];
        }
        return score;
    }

    private void initBroadCastReceiver() {
        receiver = new MyBroadCastReceiver();
        IntentFilter filter = new IntentFilter(COMMIT_AUTO_MARK_SCORE_ACTION);
        registerReceiver(receiver, filter);
    }
}
