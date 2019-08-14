package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.response.LQResourceDetailVo;
import com.lqwawa.intleducation.module.discovery.ui.task.list.TaskCommitParams;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.TaskCommitListFragment;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 课时任务提交列表
 */

public class CommittedTasksAdapter extends MyBaseAdapter {

    private Activity activity;

    private List<LqTaskCommitVo> mCommittedTaskVo;

    private ImageOptions userAvatarImageOptions;
    private ImageOptions courseImageOptions;
    private ImageOptions autoMarkImageOptions;

    private SectionResListVo mSectionResListVo;
    private TaskCommitParams mTaskCommitParams;
    private boolean isAudition;

    private TaskCommitListFragment.DoWorkListener mDoWorkListener;
    private LQResourceDetailVo mResourceDetailVo;
    private CommittedNavigator mNavigator;
    // 是否有主观题
    private boolean mHaveSubjectivity;

    public void setAnswerData(@Nullable LQResourceDetailVo vo){
        this.mResourceDetailVo = vo;
        if(EmptyUtil.isNotEmpty(vo)){
            List<LQResourceDetailVo.ExerciseBean> exercise = vo.getExercise();
            if(EmptyUtil.isNotEmpty(exercise)){
                for (LQResourceDetailVo.ExerciseBean bean:exercise) {
                    List<LQResourceDetailVo.ExerciseBean.ExerciseItemListBean> exercise_item_list = bean.getExercise_item_list();
                    if(EmptyUtil.isNotEmpty(exercise_item_list)){
                        for (LQResourceDetailVo.ExerciseBean.ExerciseItemListBean item:exercise_item_list) {
                            if(EmptyUtil.isNotEmpty(item) && TextUtils.equals(item.getType(),"9")){
                                // 有主观题
                                mHaveSubjectivity = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void setDoWorkListener(TaskCommitListFragment.DoWorkListener mDoWorkListener) {
        this.mDoWorkListener = mDoWorkListener;
    }

    public void setCommittedNavigator(@NonNull CommittedNavigator navigator){
        this.mNavigator = navigator;
    }

    public interface CommittedNavigator{
        void onDeleteTask(@NonNull LqTaskCommitVo vo);
        // 刷新状态
        void onRefreshState(boolean state);
    }

    public CommittedTasksAdapter(@NonNull Activity activity,
                                 @NonNull List<LqTaskCommitVo> taskVo,
                                 @NonNull TaskCommitParams params,
                                 @NonNull SectionResListVo sectionResListVo) {
        this(activity, taskVo);
        this.mTaskCommitParams = params;
        this.mSectionResListVo = sectionResListVo;
        this.isAudition = params.isAudition();
    }

    public CommittedTasksAdapter(@NonNull Activity activity, @NonNull List<LqTaskCommitVo> taskVo) {
        this.activity = activity;
        this.mCommittedTaskVo = taskVo;
        if (EmptyUtil.isEmpty(taskVo)) {
            this.mCommittedTaskVo = new ArrayList<>();
        }

        // 用户头像的ImageOptions
        userAvatarImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY,
                        R.drawable.user_header_def, false, true, null);


        // 课程图片显示的ImageOptions
        courseImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY,
                R.drawable.img_def, false, false, null);

        // 课程图片显示的ImageOptions
        autoMarkImageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.FIT_XY,
                R.drawable.ic_auto_mark_task_order, false, false, null);
    }

    public CommittedTasksAdapter(@NonNull Activity activity) {
        this(activity, null);
    }

    @Override
    public int getCount() {
        return mCommittedTaskVo.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommittedTaskVo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final LqTaskCommitVo vo = mCommittedTaskVo.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = UIUtil.inflate(R.layout.item_lesson_committed_task_layout);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (vo.isSpeechEvaluation() || vo.isVideoType()) {
            // 26是语音评测
            // 语音评测
            // 加载语音评测缩略图和显示分数
            // 加载语音评测的头像
            LQwawaImageUtil.loadCommonIcon(holder.mCourseIcon.getContext(), holder.mCourseIcon, vo.getStudentResThumbnailUrl(), R.drawable.ic_speech_evaluation_placeholder);
            holder.mVoiceScores.setVisibility(View.VISIBLE);
            holder.mVoiceScores.setVisibility(View.VISIBLE);
            if(EmptyUtil.isEmpty(vo.getTaskScore())){
                vo.setTaskScore("0");
                holder.mVoiceScores.setText(String.format(UIUtil.getString(R.string.label_voice_scores_format), "0"));
            }else{
                holder.mVoiceScores.setText(String.format(UIUtil.getString(R.string.label_voice_scores_format), vo.getTaskScore()));
            }
            if (!TextUtils.isEmpty(vo.getTaskScore()) &&
                    (mTaskCommitParams.getHandleRole() == UserHelper.MoocRoleType.TEACHER ||
                        mTaskCommitParams.getHandleRole() == UserHelper.MoocRoleType.EDITOR ||
                            mTaskCommitParams.getHandleRole() == UserHelper.MoocRoleType.PARENT ||
                            TextUtils.equals(UserHelper.getUserId(),mSectionResListVo.getCreateId()))) {
                // 只有主编小编，任务的创建者才显示警告 and 分数小于等于60分 不等于
                // 家长身份也需要查看警告
                // TaskScore 自动测评的分数，老师点评过后,会自动覆盖该分数
                // 家长和老师身份,都显示不及格，因为班级学程入口，家长身份，辅导老师处理？
                try{
                    if(Double.parseDouble(vo.getTaskScore()) < 60){
                        if(isAudition){
                            // 试听不显示警告
                            holder.mVoiceWarning.setVisibility(View.GONE);
                        }else{
                            holder.mVoiceWarning.setVisibility(View.VISIBLE);
                        }
                    }else{
                        holder.mVoiceWarning.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                holder.mVoiceWarning.setVisibility(View.GONE);
            }

            // 语音评测状态显示
            holder.mCheckMark.setActivated(vo.isHasVoiceReview());
            if (vo.isHasVoiceReview()) {
                // 老师点评
                holder.mCheckMark.setTextColor(UIUtil.getColor(R.color.colorAccent));
                if (vo.isVideoType()){
                    //q配音显示查看点评
                    holder.mCheckMark.setText(UIUtil.getString(R.string.label_look_review));
                } else {
                    holder.mCheckMark.setText(UIUtil.getString(R.string.label_teacher_mark));
                }
                holder.mCheckMark.setVisibility(View.VISIBLE);
            } else {
                // 立即点评
                holder.mCheckMark.setTextColor(UIUtil.getColor(android.R.color.holo_red_light));
                holder.mCheckMark.setText(UIUtil.getString(R.string.label_immediate_review));
                // TODO 批阅没有做身份判断，语音评测身份需要判断？
                // TODO 主编,小编,任务的创建者,有点评的权限
                // 是否有点评的权限
                // 主编小编,任务的创建者
                int handleRole = mTaskCommitParams.getHandleRole();
                boolean isReviewPermission =
                        handleRole == UserHelper.MoocRoleType.TEACHER
                        || handleRole == UserHelper.MoocRoleType.EDITOR
                        || TextUtils.equals(mSectionResListVo.getCreateId(),UserHelper.getUserId());
                if (isReviewPermission) {
                    holder.mCheckMark.setVisibility(View.VISIBLE);
                } else {
                    holder.mCheckMark.setVisibility(View.GONE);
                }
            }
        } else {
            // 做任务cell
            // 做读写单有自动批阅的功能
            // 已经批阅过
            if(EmptyUtil.isNotEmpty(vo.getTaskScore())){
                holder.mVoiceScores.setText(String.format(UIUtil.getString(R.string.label_voice_scores_format), vo.getTaskScore()));
                holder.mVoiceScores.setVisibility(View.VISIBLE);
            }else{
                holder.mVoiceScores.setVisibility(View.GONE);
            }
            /*if(vo.isHasCommitTaskReview()){

            }else{
                // holder.mVoiceScores.setText(String.format(UIUtil.getString(R.string.label_voice_scores_format), vo.getTaskScore()));
                holder.mVoiceScores.setVisibility(View.GONE);
            }*/
            if (!TextUtils.isEmpty(vo.getTaskScore()) &&
                    // 自动批阅的读写单 或者正常的读写单已经批阅过
                    (vo.isAutoMark() || vo.isHasCommitTaskReview()) &&
                    (mTaskCommitParams.getHandleRole() == UserHelper.MoocRoleType.TEACHER ||
                            mTaskCommitParams.getHandleRole() == UserHelper.MoocRoleType.EDITOR ||
                            mTaskCommitParams.getHandleRole() == UserHelper.MoocRoleType.PARENT ||
                            TextUtils.equals(UserHelper.getUserId(),mSectionResListVo.getCreateId()))) {
                // 批阅列表已经批阅过才显示警告
                // 只有主编小编，任务的创建者才显示警告 and 分数小于等于60分  不等于
                // 家长身份也需要查看警告
                // TaskScore 自动测评的分数，老师点评过后,会自动覆盖该分数
                // 家长和老师身份,都显示不及格，因为班级学程入口，家长身份，辅导老师处理？
                try{

                    int passingGradle = 60;
                    if(vo.isAutoMark()){
                        passingGradle = (int)(Integer.parseInt(mSectionResListVo.getPoint()) * 0.6);
                    }

                    if(Double.parseDouble(vo.getTaskScore()) < passingGradle){
                        if(isAudition){
                            // 试听不显示警告
                            holder.mVoiceWarning.setVisibility(View.GONE);
                        }else{
                            holder.mVoiceWarning.setVisibility(View.VISIBLE);
                        }
                    }else{
                        holder.mVoiceWarning.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                holder.mVoiceWarning.setVisibility(View.GONE);
            }

            // 显示用户提交课程缩略图
            if(vo.isAutoMark()){
                // 自动批阅的读写单
                XImageLoader.loadImage(holder.mCourseIcon, vo.getStudentResThumbnailUrl(), autoMarkImageOptions);
            }else{
                XImageLoader.loadImage(holder.mCourseIcon, vo.getStudentResThumbnailUrl(), courseImageOptions);
            }

            /* 更改,自动批阅的读写单,显示批阅
            if(mSectionResListVo.isAutoMark() && !mHaveSubjectivity){
                // 没有主观题 隐藏批阅
                holder.mCheckMark.setVisibility(View.GONE);
            }else{
                holder.mCheckMark.setVisibility(View.VISIBLE);
            }*/

            // 批阅状态显示
            holder.mCheckMark.setActivated(vo.isHasCommitTaskReview());
            if (vo.isHasCommitTaskReview()) {
                // 已经批阅
                holder.mCheckMark.setTextColor(UIUtil.getColor(R.color.colorAccent));
                holder.mCheckMark.setText(UIUtil.getString(R.string.label_check_the_marking));
            } else {
                holder.mCheckMark.setTextColor(UIUtil.getColor(android.R.color.holo_red_light));
                holder.mCheckMark.setText(UIUtil.getString(R.string.label_check_the_marking));
            }
        }

        // 显示用户头像
        XImageLoader.loadImage(holder.mStudentAvatar, vo.getHeadPicUrl(), userAvatarImageOptions);
        // 显示用户姓名
        holder.mStudentName.setText(vo.getStudentName());
        if(EmptyUtil.isNotEmpty(vo.getStudentResTitle())){
            // 显示用户提交课程名称
            holder.mCourseName.setText(vo.getStudentResTitle());
        }else{
            if(vo.isAutoMark()){
                holder.mCourseName.setText(mSectionResListVo.getName());
            }
        }
        // 显示提交时间，保留到分
        // 优先使用更新时间
        String updateTime = vo.getUpdateTime();
        String commitTime = vo.getCommitTime();
        if(EmptyUtil.isNotEmpty(updateTime)){
            if (updateTime.contains(":")) {
                updateTime = updateTime.substring(0, commitTime.lastIndexOf(":"));
            }
            holder.mCourseCreateTime.setText(updateTime);
        }else if(EmptyUtil.isNotEmpty(commitTime)){
            if (commitTime.contains(":")) {
                commitTime = commitTime.substring(0, commitTime.lastIndexOf(":"));
            }
            holder.mCourseCreateTime.setText(commitTime);
        }

        // 点击查看批阅
        holder.mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EmptyUtil.isEmpty(mDoWorkListener)) {
                    if(EmptyUtil.isNotEmpty(mNavigator)){
                        mNavigator.onRefreshState(false);
                    }
                    mDoWorkListener.onItemClick(vo, true, getSourceType(),false);
                }
            }
        });

        // 点击删除
        boolean isShowDelete = vo.isDeleteTag();
        if(isShowDelete){
            holder.mIvDelete.setVisibility(View.VISIBLE);
        }else{
            holder.mIvDelete.setVisibility(View.GONE);
        }

        holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除按钮
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onDeleteTask(vo);
                }
            }
        });
        // 点击课件详情的跳转拦截
        holder.mIvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EmptyUtil.isEmpty(mDoWorkListener)) {
                    if(EmptyUtil.isNotEmpty(mNavigator)){
                        mNavigator.onRefreshState(false);
                    }

                    if(vo.isSpeechEvaluation()){
                        // 语音评测
                        mDoWorkListener.openCourseWareDetails(vo);
                    }else{
                        // 批阅列表
                        String studentResId = vo.getStudentResId();
                        int commitTaskId = vo.getCommitTaskId();
                        // 新版本用Id
                        commitTaskId = vo.getId();
                        if (!EmptyUtil.isEmpty(studentResId)) {
                            String[] strings = studentResId.split("-");
                            String resId = EmptyUtil.isEmpty(strings) ? "" : strings[0];
                            int resType = strings.length < 2 ? -1 : Integer.parseInt(strings[1]);
                            String title = vo.getStudentResTitle();
                            String resourceUrl = vo.getStudentResUrl();
                            String resourceThumbnailUrl = vo.getStudentResThumbnailUrl();
                            mDoWorkListener.openCourseWareDetails(
                                    resId,
                                    resType,
                                    title,
                                    0,
                                    resourceUrl,
                                    resourceThumbnailUrl,
                                    commitTaskId);
                        }
                    }
                }
            }
        });

        holder.mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoWorkListener.onShareCourseWare(vo);
            }
        });

        if(vo.isAutoMark() || vo.isVideoType()){
            holder.mDetailLayout.setVisibility(View.GONE);
            if(EmptyUtil.isNotEmpty(vo.getStudentResId())) {
                if (vo.getCommitType() == 6) {
                    holder.mDetailLayout.setVisibility(View.VISIBLE);
                }
                holder.mCourseIconLayout.setEnabled(true);
                holder.mCourseIconLayout.setClickable(true);
                holder.mCourseIconLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!EmptyUtil.isEmpty(mDoWorkListener)) {
                            mDoWorkListener.onItemClick(vo, false, getSourceType(), true);
                        }
                    }
                });
            } else{
                holder.mCourseIconLayout.setEnabled(false);
                holder.mCourseIconLayout.setClickable(false);
                holder.mCourseIconLayout.setOnClickListener(null);
            }
        }else{
            holder.mDetailLayout.setVisibility(View.VISIBLE);
            holder.mCourseIconLayout.setOnClickListener(null);
            holder.mCourseIconLayout.setEnabled(false);
            holder.mCourseIconLayout.setClickable(false);
            // 评测列表不显示分享
            holder.mIvShare.setVisibility(vo.getCommitType() == 5 ? View.GONE : View.VISIBLE);
        }


        return convertView;
    }

    /**
     * 获取资源类型
     *
     * @return
     */
    private int getSourceType() {
        return activity.getIntent().getBooleanExtra("isLive", false) ?
                (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity.KEY_IS_FROM_MY, false) ?
                        SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE)
                : (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity.KEY_IS_FROM_MY, false) ?
                SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
    }


    /**
     * 任务提交列表Item ViewHolder
     */
    public static class ViewHolder {

        // 课件详情容器
        private FrameLayout mCourseWareDetailsLayout;
        // 用户头像容器
        private FrameLayout mIconLayout;
        // 用户学生头像
        private ImageView mStudentAvatar;
        // 红点显示
        private ImageView mRedPoint;
        // 学生姓名
        private TextView mStudentName;
        private LinearLayout mDetailLayout;
        // 课件详情
        private ImageView mIvDetail;
        // 分享
        private ImageView mIvShare;
        // 删除按钮
        private ImageView mIvDelete;
        // 课程图片Icon容器
        private FrameLayout mCourseIconLayout;
        // 课程图片显示
        private ImageView mCourseIcon;
        // 语音评测分数
        private TextView mVoiceScores;
        // 语音评测分数警告
        private ImageView mVoiceWarning;
        // 课程名称
        private TextView mCourseName;
        // 课程创建时间
        private TextView mCourseCreateTime;
        // 批阅按钮
        private TextView mCheckMark;

        public ViewHolder(View itemView) {
            mCourseWareDetailsLayout = (FrameLayout) itemView.findViewById(R.id.courseware_details_layout);
            mIconLayout = (FrameLayout) itemView.findViewById(R.id.icon_layout);
            mStudentAvatar = (ImageView) itemView.findViewById(R.id.iv_student_avatar);
            mRedPoint = (ImageView) itemView.findViewById(R.id.red_point);
            mStudentName = (TextView) itemView.findViewById(R.id.tv_student_name);
            mDetailLayout = (LinearLayout)itemView.findViewById(R.id.ll_course_detail);
            mIvDetail = (ImageView) itemView.findViewById(R.id.iv_access_details);
            mIvShare = (ImageView) itemView.findViewById(R.id.iv_share);
            mIvDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            mCourseIconLayout = (FrameLayout) itemView.findViewById(R.id.course_icon_layout);
            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mVoiceScores = (TextView) itemView.findViewById(R.id.tv_voice_scores);
            mVoiceWarning = (ImageView) itemView.findViewById(R.id.iv_voice_warning);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mCourseCreateTime = (TextView) itemView.findViewById(R.id.tv_course_time);
            mCheckMark = (TextView) itemView.findViewById(R.id.tv_check_mark);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<LqTaskCommitVo> list) {
        if (list != null) {
            this.mCommittedTaskVo.clear();
            this.mCommittedTaskVo.addAll(list);
        } else {
            this.mCommittedTaskVo.clear();
        }
    }

    /**
     * 获取当前显示的提交列表
     * @return 集合数据
     */
    public List<LqTaskCommitVo> getData(){
        return mCommittedTaskVo;
    }
}
