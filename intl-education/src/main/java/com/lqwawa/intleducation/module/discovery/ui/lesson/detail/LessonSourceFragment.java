package com.lqwawa.intleducation.module.discovery.ui.lesson.detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lecloud.skin.ui.utils.VodVideoSettingUtil;
import com.libs.gallery.ImageBrowserActivity;
import com.libs.gallery.ImageInfo;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.IBaseFragment;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.NetWorkUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.SuperListView;
import com.lqwawa.intleducation.common.ui.CustomDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LetvVodHelperNew;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.adapter.CourseResListAdapter;
import com.lqwawa.intleducation.module.discovery.tool.CourseDetails;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.task.detail.SectionTaskParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.LessonDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqresviewlib.LqResViewHelper;
import com.oosic.apps.iemaker.base.slide_audio.AudioRecorder;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程节详情的Fragment
 * @date 2018/05/16 11:19
 * @history v1.0
 * **********************************
 */
public class LessonSourceFragment extends IBaseFragment implements LessonSourceNavigator{

    // 选择资源支持的最大数
    private static final int MAX_CHOICE_COUNT = 5;

    private static final String KEY_EXTRA_NEED_FLAG = "KEY_EXTRA_NEED_FLAG";
    private static final String KEY_EXTRA_CAN_READ = "KEY_EXTRA_CAN_READ";
    private static final String KEY_EXTRA_CAN_EDIT = "KEY_EXTRA_CAN_EDIT";
    private static final String KEY_EXTRA_ONLINE_TEACHER = "KEY_EXTRA_ONLINE_TEACHER";
    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static final String KEY_EXTRA_SECTION_ID = "KEY_EXTRA_SECTION_ID";
    private static final String KEY_EXTRA_TASK_TYPE = "KEY_EXTRA_TASK_TYPE";


    public static String SECTION_NAME = "section_name";
    public static String SECTION_TITLE = "section_title";
    public static String STATUS = "status";

    private ListView mListView;
    private CourseEmptyView mEmptyLayout;
    private CourseResListAdapter mCourseResListAdapter;
    private boolean needFlag;
    private boolean canRead;
    private boolean canEdit;
    private boolean isOnlineTeacher;
    private String courseId;
    private String sectionId;
    private int mTaskType;
    private SectionDetailsVo mSectionDetailsVo;
    private LessonSourceParams mSourceParams;
    private ReadWeikeHelper mReadWeikeHelper;

    public static LessonSourceFragment newInstance(boolean needFlag,
                                       boolean canEdit,
                                       boolean canRead,
                                       boolean isOnlineTeacher,
                                       @NonNull String courseId,
                                       @NonNull String sectionId,
                                       int taskType,
                                       @NonNull LessonSourceParams params){
        LessonSourceFragment fragment = new LessonSourceFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(KEY_EXTRA_NEED_FLAG,needFlag);
        arguments.putBoolean(KEY_EXTRA_CAN_EDIT,canEdit);
        arguments.putBoolean(KEY_EXTRA_CAN_READ,canRead);
        arguments.putBoolean(KEY_EXTRA_ONLINE_TEACHER,isOnlineTeacher);
        arguments.putString(KEY_EXTRA_COURSE_ID,courseId);
        arguments.putString(KEY_EXTRA_SECTION_ID,sectionId);
        arguments.putInt(KEY_EXTRA_TASK_TYPE,taskType);
        arguments.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_lesson_source;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        needFlag = bundle.getBoolean(KEY_EXTRA_NEED_FLAG);
        canEdit = bundle.getBoolean(KEY_EXTRA_CAN_EDIT);
        canRead = bundle.getBoolean(KEY_EXTRA_CAN_READ);
        isOnlineTeacher = bundle.getBoolean(KEY_EXTRA_ONLINE_TEACHER);
        courseId = bundle.getString(KEY_EXTRA_COURSE_ID);
        sectionId = bundle.getString(KEY_EXTRA_SECTION_ID);
        mTaskType = bundle.getInt(KEY_EXTRA_TASK_TYPE);
        if(bundle.containsKey(FRAGMENT_BUNDLE_OBJECT)){
            mSourceParams = (LessonSourceParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        }

        if(mTaskType == 1 || mTaskType == 2){
            // 看课件  或者是  复述课件(没有复述课件权限的)
            mReadWeikeHelper = new ReadWeikeHelper(getActivity());
        }

        if(EmptyUtil.isEmpty(courseId) ||
                EmptyUtil.isEmpty(sectionId) ||
                EmptyUtil.isEmpty(mSourceParams)) return false;
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mListView = (ListView) mRootView.findViewById(R.id.listView);
        mEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.empty_layout);
        // 老师身份不显示
        boolean lessonNeedFlag = needFlag && (mSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER);
        mCourseResListAdapter = new CourseResListAdapter(getActivity(), lessonNeedFlag,true);
        CourseDetailParams courseParams = mSourceParams.getCourseParams();
        mCourseResListAdapter.setClassTeacher(courseParams.isClassCourseEnter() && courseParams.isClassTeacher());
        // canRead 是否可以查阅资源
        // 试听功能，都可以查阅资源，以及学程馆授权的
        if (canRead) {
            mCourseResListAdapter.setOnItemClickListener(new CourseResListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View convertView) {
                    if (ButtonUtils.isFastDoubleClick()) {
                        return;
                    }

                    final SectionResListVo resVo = (SectionResListVo) mCourseResListAdapter.getItem(position);
                    if (resVo.isIsShield()) {
                        UIUtil.showToastSafe(R.string.res_has_shield);
                        return;
                    }

                    boolean freeUser = getActivity().getIntent().getBooleanExtra(LessonDetailsActivity.KEY_ROLE_FREE_USER,false);

                    if (resVo.getTaskType() == 1) {
                        // 看课件
                        mReadWeikeHelper.readWeike(resVo);

                        if((needFlag && !mSourceParams.isParentRole())){
                            // 是已经加入的课程, 并且不是家长身份
                            if(mSourceParams.getRole() != UserHelper.MoocRoleType.EDITOR &&
                                    mSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER){
                                // 如果不是主编或者小编
                                // 看课件才FlagRead
                                flagRead(resVo, position);
                            }
                        }

                        /*if (needFlag && canEdit || freeUser) {
                            if(getRoleWithCourse() != UserHelper.MoocRoleType.EDITOR
                                    && getRoleWithCourse() != UserHelper.MoocRoleType.TEACHER && !freeUser){
                                // Teacher 是小编 Editor 是主编
                                // 不是主编和小编才FlagRead
                                flagRead(resVo, position);
                            }
                        }*/
                    } else if (resVo.getTaskType() == 2) {
                        //复述微课
                        if((needFlag || !mSourceParams.isParentRole()) || mSourceParams.isAudition()){
                            // 是已经加入的课程
                            // 或者是试听身份
                            enterSectionTaskDetail(resVo);
                        }else{
                            // 其它情况，只能看微课
                            mReadWeikeHelper.readWeike(resVo);
                        }
                        /*if(canEdit && needFlag || freeUser
                                || !TextUtils.equals(getActivity().getIntent().getStringExtra("memberId"),
                                UserHelper.getUserId())){
                            enterSectionTaskDetail(resVo);
                        }else{
                            readWeike(resVo, position);
                        }*/
                    } else if (resVo.getTaskType() == 3) {
                        if((needFlag || !mSourceParams.isParentRole()) || mSourceParams.isAudition()){
                            // 是已经加入的课程
                            // 或者是试听身份
                            enterSectionTaskDetail(resVo);
                        }else{
                            if(TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(getActivity(),
                                        resVo.getResId(), resVo.getResType(),
                                        getActivity().getIntent().getStringExtra("schoolId"),
                                        getActivity().getIntent().getBooleanExtra("isPublic", false),
                                        getActivity().getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }


                        /*if(canEdit && needFlag || freeUser
                                || !TextUtils.equals(getActivity().getIntent().getStringExtra("memberId"),
                                UserHelper.getUserId())) {
                            enterSectionTaskDetail(resVo);
                        }else{
                            if(TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(getActivity(),
                                        resVo.getResId(), resVo.getResType(),
                                        getActivity().getIntent().getStringExtra("schoolId"),
                                        getActivity().getIntent().getBooleanExtra("isPublic", false),
                                        getActivity().getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }*/
                    }
                }

                @Override
                public void onItemChoice(int position, View convertView) {
                    SectionResListVo item = (SectionResListVo) mCourseResListAdapter.getItem(position);
                    if(item.isIsShield()){
                        UIUtil.showToastSafe(R.string.res_has_shield);
                        return;
                    }

                    if(!item.isActivated()){
                        // 点击的当前条目不是选择的状态,需判断上限
                        // 判断是否已经超过五个选择了
                        int count = takeChoiceResourceCount();
                        if(count >= MAX_CHOICE_COUNT){
                            UIUtil.showToastSafe(getString(R.string.str_select_count_tips,MAX_CHOICE_COUNT));
                            mCourseResListAdapter.notifyDataSetChanged();
                            return;
                        }
                    }

                    item.setActivated(!item.isActivated());
                    mCourseResListAdapter.notifyDataSetChanged();
                }
            });
        } else if (!needFlag) {
            // needFlag 是否标记已读的字段
            // V5.8之后，试听功能就可以查看听说课，读写单内容了
            // mListView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        registerBroadcastReceiver();
        getData();
    }

    @Override
    public void triggerChoice(boolean open) {
        if(EmptyUtil.isNotEmpty(mCourseResListAdapter)){
            mCourseResListAdapter.triggerChoiceMode(open);
        }
    }

    /**
     * 获取已经选择的资源个数
     * @return
     */
    private int takeChoiceResourceCount(){
        List<SectionResListVo> choiceArray = takeChoiceResource();
        int count = choiceArray.size();
        choiceArray.clear();
        return count;
    }

    @Override
    public List<SectionResListVo> takeChoiceResource() {
        List<SectionResListVo> data =mCourseResListAdapter.getData();
        List<SectionResListVo> choiceArray = new ArrayList<>();
        if(EmptyUtil.isNotEmpty(data)){
            for (SectionResListVo vo:data) {
                if(vo.isActivated()){
                    choiceArray.add(vo);
                }
            }
        }

        return choiceArray;
    }

    @Override
    public void clearAllResourceState() {
        List<SectionResListVo> data = mCourseResListAdapter.getData();
        if(EmptyUtil.isNotEmpty(data)){
            for (SectionResListVo vo:data) {
                vo.setActivated(false);
            }
        }

        mCourseResListAdapter.notifyDataSetChanged();
    }

    private void getData() {
        String token = mSourceParams.getMemberId();
        int role = 2;
        if(mSourceParams.getRole() == UserHelper.MoocRoleType.TEACHER){
            role = 1;
        }

        String classId = "";
        if(role == 1 && mSourceParams.getCourseParams().isClassCourseEnter()){
            classId = mSourceParams.getCourseParams().getClassId();
        }

        LessonHelper.requestChapterStudyTask(token, classId, courseId, sectionId, role,new DataSource.Callback<SectionDetailsVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SectionDetailsVo sectionDetailsVo) {
                mSectionDetailsVo = sectionDetailsVo;
                if(EmptyUtil.isEmpty(sectionDetailsVo)) return;
                updateView();
            }
        });
    }

    private void updateView() {
        mCourseResListAdapter.setData(null);
        if (mSectionDetailsVo != null) {
            getActivity().getIntent().putExtra(SECTION_NAME, mSectionDetailsVo.getSectionName());
            // topBar.setTitle(sectionDetailsVo.getSectionName());
            getActivity().getIntent().putExtra(SECTION_TITLE, mSectionDetailsVo.getSectionTitle());
            getActivity().getIntent().putExtra(STATUS, mSectionDetailsVo.getStatus());
            getActivity().getIntent().putExtra("isPublic", mSectionDetailsVo.isIsOpen());
            if (mSectionDetailsVo.getTaskList() != null) {
                if (mSectionDetailsVo.getTaskList().size() > 0) {
                    if (mSectionDetailsVo.getTaskList().get(0).getData() != null) {
                        // this.textViewLessonIntroduction.setText("" + sectionDetailsVo.getIntroduction());
                        List<SectionResListVo> voList = mSectionDetailsVo.getTaskList().get(0).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(0));
                            vo.setTaskType(mSectionDetailsVo.getTaskList().get(0).getTaskType());
                        }
                        if(mSectionDetailsVo.getTaskList().get(0).getTaskType() == mTaskType){
                            mCourseResListAdapter.setData(voList);
                        }
                        mListView.setAdapter(mCourseResListAdapter);
                    }
                }
                if (mSectionDetailsVo.getTaskList().size() > 1) {
                    if (mSectionDetailsVo.getTaskList().get(1).getData() != null) {
                        // this.textViewLessonIntroduction.setText("" + sectionDetailsVo.getIntroduction());
                        List<SectionResListVo> voList = mSectionDetailsVo.getTaskList().get(1).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(1));
                            vo.setTaskType(mSectionDetailsVo.getTaskList().get(1).getTaskType());
                        }
                        if(mSectionDetailsVo.getTaskList().get(1).getTaskType() == mTaskType){
                            mCourseResListAdapter.addData(voList);
                        }

                    }
                }
                if (mSectionDetailsVo.getTaskList().size() > 2) {
                    if (mSectionDetailsVo.getTaskList().get(2).getData() != null) {
                        // this.textViewLessonIntroduction.setText("" + sectionDetailsVo.getIntroduction());
                        List<SectionResListVo> voList = mSectionDetailsVo.getTaskList().get(2).getData();
                        if (voList.size() > 0) {
                            voList.get(0).setIsTitle(true);
                        }
                        for (SectionResListVo vo : voList) {
                            vo.setTaskName(getTaskName(2));
                            vo.setTaskType(mSectionDetailsVo.getTaskList().get(2).getTaskType());
                        }

                        if(mSectionDetailsVo.getTaskList().get(2).getTaskType() == mTaskType){
                            mCourseResListAdapter.addData(voList);
                        }

                    }
                }
                mCourseResListAdapter.notifyDataSetChanged();
            }
        }

        if(mCourseResListAdapter.getCount() > 0){
            mListView.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
        }else{
            mListView.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据上层页面传来的信息判断角色
     */
    /*private int getRoleWithCourse(){
        String memberId = getActivity().getIntent().getStringExtra("memberId");
        CourseVo courseVo = (CourseVo) getActivity().getIntent().getSerializableExtra(CourseVo
                .class.getSimpleName());
        int role = UserHelper.getCourseAuthorRole(memberId, courseVo);
        return role;
    }*/

    /**
     * 进入复述课件，任务单详情
     * @param vo 复述课件，任务单消息实体
     */
    protected void enterSectionTaskDetail(SectionResListVo vo) {
        String curMemberId = mSourceParams.getMemberId();
        int originalRole = mSourceParams.getRole();
        if(mSourceParams.isTeacherVisitor()){
            // 这才是真实的角色身份
            originalRole = mSourceParams.getRealRole();
        }


        final String taskId = vo.getTaskId();
        if (originalRole == UserHelper.MoocRoleType.STUDENT && !TextUtils.isEmpty(taskId)) {
            // 分发任务
            LessonHelper.DispatchTask(taskId, curMemberId, null);
        }

        // 处理需要转换的角色
        int handleRole = originalRole;
        boolean isAudition = mSourceParams.isAudition();
        if(mSourceParams.isCounselor() || mSourceParams.isAudition()){
            // 如果是辅导老师身份 或者试听
            // 角色按照浏览者，家长身份处理
            handleRole = UserHelper.MoocRoleType.PARENT;
        }

        if(handleRole != UserHelper.MoocRoleType.PARENT){
            // 家长身份优先
            if(TextUtils.equals(UserHelper.getUserId(),vo.getCreateId())){
                // 任务的创建者 小编
                handleRole = UserHelper.MoocRoleType.TEACHER;
            }

            if(mSourceParams.isLecturer()){
                // 如果是讲师身份 主编身份处理
                handleRole = UserHelper.MoocRoleType.EDITOR;
            }
        }


        // 生成任务详情参数
        SectionTaskParams params = new SectionTaskParams(originalRole,handleRole);
        params.fillParams(mSourceParams);

        SectionTaskDetailsActivity.startForResultEx(getActivity(), vo, curMemberId, getActivity().getIntent
                ().getStringExtra("schoolId"), getActivity().getIntent().getBooleanExtra
                (MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false),
                null, originalRole,handleRole, null,isAudition,params);


        /*String memberId = getActivity().getIntent().getStringExtra("memberId");
        CourseVo courseVo = (CourseVo) getActivity().getIntent().getSerializableExtra(CourseVo
                .class.getSimpleName());
        int originRole = UserHelper.getCourseAuthorRole(memberId, courseVo);
        final String taskId = vo.getTaskId();
        if (originRole == UserHelper.MoocRoleType.STUDENT && !TextUtils.isEmpty(taskId)) {
            LessonHelper.DispatchTask(taskId, memberId, null);
        }

        boolean freeUser = getActivity().getIntent().getBooleanExtra(LessonDetailsActivity.KEY_ROLE_FREE_USER,false);
        if(freeUser){
            // 如果是游客身份,试听也是做辅导老师身份处理，也就是老师
            originRole = UserHelper.MoocRoleType.TEACHER;
        }
        // 辅导老师的身份等同家长处理 已经角色处理过的 role
        int role = originRole;
        if(UserHelper.isCourseCounselor(courseVo,isOnlineTeacher) || freeUser){
            // 如果是在线课堂的老师,当做家长处理 如果是游客身份,试听也是做家长身份处理
            role = UserHelper.MoocRoleType.PARENT;
        }

        if (originRole == UserHelper.MoocRoleType.TEACHER || isOnlineTeacher) {
            if (UserHelper.isCourseCounselor(courseVo,isOnlineTeacher)) {
                role = UserHelper.MoocRoleType.PARENT;
            }
            if (UserHelper.isCourseTeacher(courseVo)) {
                role = UserHelper.MoocRoleType.EDITOR;
            }
        }

        SectionTaskDetailsActivity.startForResultEx(getActivity(), vo, memberId, getActivity().getIntent
                ().getStringExtra("schoolId"), getActivity().getIntent().getBooleanExtra
                (MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false), null, originRole,role, null,freeUser);*/
    }

    /**
     * 看微课
     * @param resVo
     * @param position
     */
    /*private void readWeike(final SectionResListVo resVo, int position) {
        int resType = resVo.getResType();
        if (resType > 10000) {
            resType -= 10000;
        }
        switch (resType) {
            case 1:
                showPic(resVo);
//
//                ImageDetailActivity.showStatic(activity,
//                        resVo.getResourceUrl().trim(), resVo.getName());
                break;
            case 2:
                playMedia(resVo, VodVideoSettingUtil.AUDIO_TYPE);
                break;
            case 6:
            case 20:
                if (TaskSliderHelper.onTaskSliderListener != null) {
                    TaskSliderHelper.onTaskSliderListener
                            .viewPdfOrPPT(getActivity(), "" + resVo.getResId(), resVo.getResType(),
                                    resVo.getOriginName(), resVo.getCreateId(),
                                    getActivity().getIntent().getBooleanExtra(MyCourseDetailsActivity
                                            .KEY_IS_FROM_MY_COURSE, false)
                                            ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                }
                break;
            case 24:
            case 25:
                LqResViewHelper.playBaseRes(resVo.getResType(), getActivity(),
                        resVo.getResourceUrl().trim(), resVo.getName());
                break;
            case 5:
            case 16:
            case 17:
            case 18:
            case 19:
            case 3:
            case 23:
                if (!SharedPreferencesHelper.getBoolean(getActivity(),
                        AppConfig.BaseConfig.KEY_ALLOW_4G, false)) {
                    if (NetWorkUtils.isWifiActive(getActivity().getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
//                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            *//*LqResViewHelper.playWeike(activity,
                                    UserHelper.getUserId(),
                                    UserHelper.getUserName(),
                                    resVo.getResourceUrl().trim(),
                                    resVo.getOriginName(),
                                    1,
                                    Utils.getCacheDir(),
                                    resVo.getScreenType(),
                                    resVo.getResType());*//*
                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(getActivity(),
                                        resVo.getResId(), resVo.getResType(),
                                        getActivity().getIntent().getStringExtra("schoolId"),
                                        getActivity().getIntent().getBooleanExtra("isPublic", false),
                                        getActivity().getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    } else {
                        UIUtil.showToastSafe(R.string.can_not_use_4g);
                    }
                } else {
                    if (NetWorkUtils.isWifiActive(getActivity().getApplication().getApplicationContext())) {
                        if (resVo.getResType() == 3) {
//                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                        } else {
                            *//*LqResViewHelper.playWeike(activity,
                                    UserHelper.getUserId(),
                                    UserHelper.getUserName(),
                                    resVo.getResourceUrl().trim(),
                                    resVo.getOriginName(),
                                    1,
                                    Utils.getCacheDir(),
                                    resVo.getScreenType(),
                                    resVo.getResType());*//*
                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                TaskSliderHelper.onTaskSliderListener.viewCourse(getActivity(),
                                        resVo.getResId(), resVo.getResType(),
                                        getActivity().getIntent().getStringExtra("schoolId"),
                                        getActivity().getIntent().getBooleanExtra("isPublic", false),
                                        getActivity().getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                .KEY_IS_FROM_MY_COURSE, false)
                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                            }
                        }
                    } else {
                        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
                        builder.setMessage(UIUtil.getString(R.string.play_use_4g) + "?");
                        builder.setTitle(UIUtil.getString(R.string.tip));
                        builder.setPositiveButton(UIUtil.getResources().getString(R.string.continue_play),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        if (resVo.getResType() == 3) {
//                                            LqResViewHelper.playBaseRes(resVo.getResType(), activity, resVo.getVuid().trim(), resVo.getName());
                                            playMedia(resVo, VodVideoSettingUtil.VIDEO_TYPE);
                                        } else {
                                            *//*LqResViewHelper.playWeike(activity,
                                                    UserHelper.getUserId(),
                                                    UserHelper.getUserName(),
                                                    resVo.getResourceUrl().trim(),
                                                    resVo.getOriginName(),
                                                    1,
                                                    Utils.getCacheDir(),
                                                    resVo.getScreenType(),
                                                    resVo.getResType());*//*
                                            if (TaskSliderHelper.onTaskSliderListener != null) {
                                                TaskSliderHelper.onTaskSliderListener.viewCourse(getActivity(),
                                                        resVo.getResId(), resVo.getResType(),
                                                        getActivity().getIntent().getStringExtra("schoolId"),
                                                        getActivity().getIntent().getBooleanExtra("isPublic", false),
                                                        getActivity().getIntent().getBooleanExtra(MyCourseDetailsActivity
                                                                .KEY_IS_FROM_MY_COURSE, false)
                                                                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                                            }
                                        }
                                    }
                                });
                        builder.setNegativeButton(UIUtil.getResources().getString(R.string.cancel),
                                new android.content.DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.create().show();
                    }
                }
                break;
            default:
                break;
        }
    }

    *//**
     * 图片浏览
     *
     * @param resVo
     *//*
    private void showPic(SectionResListVo resVo) {
        List<ImageInfo> resourceInfoList = new ArrayList<>();
        ImageInfo newResourceInfo = new ImageInfo();
        newResourceInfo.setTitle(resVo.getName());
        newResourceInfo.setResourceUrl(resVo.getResourceUrl().trim());
        newResourceInfo.setResourceId(resVo.getResId() + "-" + resVo.getResType());
        newResourceInfo.setAuthorId(resVo.getCreateId());
        newResourceInfo.setResourceType(resVo.getResType());
        resourceInfoList.add(newResourceInfo);


        Intent intent = new Intent();
        intent.setClassName(MainApplication.getInstance().getPackageName(), "com.galaxyschool.app.wawaschool.imagebrowser.GalleryActivity");
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) resourceInfoList);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, 0);
        intent.putExtra(ImageBrowserActivity.ISPDF, false);

        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, false);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, true);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, false);//隐藏收藏功能
        startActivity(intent);
    }

    *//**
     * 音视频播放
     *
     * @param resVo
     * @param type
     *//*
    private void playMedia(SectionResListVo resVo, int type) {
        new LetvVodHelperNew.VodVideoBuilder(getActivity())
                .setNewUI(true)//使用自定义UI
                .setTitle(resVo.getName())//视频标题
                .setAuthorId(resVo.getCreateId())
                .setResId(resVo.getResId() + "-" + resVo.getResType())
                .setResourceType(resVo.getResType())
                .setVuid(resVo.getVuid())
                .setUrl(resVo.getResourceUrl())
                .setMediaType(type)//设置媒体类型
                .setPackageName(MainApplication.getInstance().getPackageName())
                .setClassName("com.galaxyschool.app.wawaschool.medias.activity.VodPlayActivity")
                .setHideBtnMore(true)
                .setLeStatus(resVo.getLeStatus())
                .setIsPublic(getActivity().getIntent().getBooleanExtra("isPublic", false))
                .create();
    }*/

    @NonNull
    private String getTaskName(int i) {
        String taskName = "";
        int taskType = mSectionDetailsVo.getTaskList().get(i).getTaskType();
        if (taskType == 1) {//看课件
            taskName = getString(R.string.lq_watch_course);
        } else if (taskType == 2) {//复述课件
            taskName = getResources().getString(R.string.retell_course);
        } else if (taskType == 3) {//任务单
            taskName = getResources().getString(R.string.coursetask);
        }
        return taskName;
    }



    /**
     * 注册广播事件,接收事件刷新
     */
    protected void registerBroadcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE);//读写单
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    /**
     * 数据刷新广播的处理
     */
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CourseDetailsItemFragment.LQWAWA_ACTION_READ_WRITE_SINGLE)) {// 读写单
                getData();
            }
        }
    };

    /**
     * 标志任务已读
     * @param vo 任务实体
     * @param position 位置
     */
    private void flagRead(final SectionResListVo vo, final int position) {

        LessonHelper.requestAddSourceFlag(vo.getTaskType(), vo.getId(), vo.getResId(), new DataSource.SucceedCallback<Void>() {
            @Override
            public void onDataLoaded(Void aVoid) {
                SectionResListVo item = (SectionResListVo) mCourseResListAdapter.getItem(position);
                item.setStatus(1);
                mCourseResListAdapter.notifyDataSetChanged();
                // 发送广播,课程大纲刷新UI
                CourseDetails.courseDetailsTriggerStudyTask(getActivity().getApplicationContext(), CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE);
            }
        });

        /*RequestVo requestVo = new RequestVo();
        requestVo.addParams("cwareId", vo.getId());
        if(vo.getTaskType() != 1){
            // 1是看课件 除了看课件，其它都需要传resId
            requestVo.addParams("resId", vo.getResId());
        }
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.setReaded + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            // TODO 还是刚刚那个接口的问题，课程是国际课程测试，接口返回flagRead是正确的。但是在加载的时候，isRead还是false
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    ((SectionResListVo) mCourseResListAdapter.getItem(position)).setStatus(1);
                    mCourseResListAdapter.notifyDataSetChanged();
                    // 发送广播
                    CourseDetails.courseDetailsTriggerStudyTask(getActivity().getApplicationContext(), CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE);
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                UIUtil.showToastSafe(R.string.net_error_tip);
            }

            @Override
            public void onFinished() {
            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EmptyUtil.isNotEmpty(mReadWeikeHelper)){
            mReadWeikeHelper.release();
        }


        if(EmptyUtil.isNotEmpty(getActivity())){
            getActivity().unregisterReceiver(mBroadcastReceiver);
        }
    }
}
