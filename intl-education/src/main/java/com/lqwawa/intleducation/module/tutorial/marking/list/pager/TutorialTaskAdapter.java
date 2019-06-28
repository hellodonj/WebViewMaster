package com.lqwawa.intleducation.module.tutorial.marking.list.pager;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.LqServerHelper;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.module.tutorial.marking.list.MarkingStateType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mrmedici
 * @desc 帮辅空间作业提交列表
 */
public class TutorialTaskAdapter extends RecyclerAdapter<TaskEntity> {

    private boolean tutorialMode;
    private EntityCallback mCallback;

    public TutorialTaskAdapter(boolean tutorialMode) {
        this(tutorialMode, null);
    }

    public TutorialTaskAdapter(boolean tutorialMode, EntityCallback mCallback) {
        super();
        this.tutorialMode = tutorialMode;
        this.mCallback = mCallback;
    }

    public void setCallback(@NonNull EntityCallback callback) {
        this.mCallback = callback;
    }

    @Override
    protected int getItemViewType(int position, TaskEntity taskEntity) {
        return R.layout.item_tutorial_work_layout;
    }

    @Override
    protected ViewHolder<TaskEntity> onCreateViewHolder(View root, int viewType) {
        return new TaskHolder(root);
    }

    private class TaskHolder extends RecyclerAdapter.ViewHolder<TaskEntity> {

        private FrameLayout mRequireLayout;
        private FrameLayout mAvatarLayout;
        private ImageView mStudentAvatar;
        private ImageView mRedPoint;
        private TextView mStudentName;
        private TextView mTvRequire;

        private LinearLayout mBodyLayout;
        private ImageView mTaskAvatar;
        private TextView mTaskType;
        private TextView mTaskName;
        private TextView mTaskClass;
        private TextView mTaskChapter;

        private TextView mTaskTime;
        private TextView mCheckMark;
        //过期时长
        private TextView mExpiredTime;

        public TaskHolder(View itemView) {
            super(itemView);
            mRequireLayout = (FrameLayout) itemView.findViewById(R.id.require_layout);
            mAvatarLayout = (FrameLayout) itemView.findViewById(R.id.avatar_layout);
            mStudentAvatar = (ImageView) itemView.findViewById(R.id.iv_student_avatar);
            mRedPoint = (ImageView) itemView.findViewById(R.id.red_point);
            mStudentName = (TextView) itemView.findViewById(R.id.tv_student_name);
            mTvRequire = (TextView) itemView.findViewById(R.id.tv_require);
            mBodyLayout = (LinearLayout) itemView.findViewById(R.id.body_layout);
            mTaskAvatar = (ImageView) itemView.findViewById(R.id.iv_task_icon);
            mTaskType = (TextView) itemView.findViewById(R.id.tv_task_type);
            mTaskName = (TextView) itemView.findViewById(R.id.tv_task_name);
            mTaskClass = (TextView) itemView.findViewById(R.id.tv_task_class);
            mTaskChapter = (TextView) itemView.findViewById(R.id.tv_task_chapter);
            mTaskTime = (TextView) itemView.findViewById(R.id.tv_task_time);
            mCheckMark = (TextView) itemView.findViewById(R.id.tv_check_mark);
            mExpiredTime = (TextView) itemView.findViewById(R.id.tv_expired_time);
        }

        @Override
        protected void onBind(TaskEntity taskEntity) {
            if (tutorialMode) {
                mAvatarLayout.setVisibility(View.VISIBLE);
                // 显示用户头像
                String studentUrl = LqServerHelper.getFullImgUrl(taskEntity.getStuHeadPicUrl() + "").trim();
                ImageUtil.fillUserAvatar(mStudentAvatar, studentUrl, R.drawable.user_header_def);
                mTvRequire.setVisibility(View.VISIBLE);
                /*FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mStudentName.getLayoutParams();
                layoutParams.leftMargin = DisplayUtil.dip2px(UIUtil.getContext(),40);
                mStudentName.setLayoutParams(layoutParams);*/
            } else {
                mAvatarLayout.setVisibility(View.VISIBLE);
                // 显示用户头像
                String studentUrl = LqServerHelper.getFullImgUrl(taskEntity.getAssHeadPicUrl() + "").trim();
                ImageUtil.fillUserAvatar(mStudentAvatar, studentUrl, R.drawable.user_header_def);
                /*FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mStudentName.getLayoutParams();
                layoutParams.leftMargin = DisplayUtil.dip2px(UIUtil.getContext(),0);
                mStudentName.setLayoutParams(layoutParams);*/
            }

            mTvRequire.setVisibility(taskEntity.getT_TaskId() > 0 ? View.VISIBLE : View.GONE);

            if (tutorialMode) {
                // 显示用户姓名
                if (EmptyUtil.isNotEmpty(taskEntity.getStuRealName())) {
                    StringUtil.fillSafeTextView(mStudentName, taskEntity.getStuRealName());
                } else {
                    StringUtil.fillSafeTextView(mStudentName, taskEntity.getStuNickName());
                }
            } else {
                // 显示提交给某个老师
                String name = EmptyUtil.isEmpty(taskEntity.getAssRealName()) ? taskEntity.getAssNickName() : taskEntity.getAssRealName();
                StringUtil.fillSafeTextView(mStudentName, String.format(UIUtil.getString(R.string.label_commit_placeholder_teacher), name));
            }

            if (tutorialMode) {
                // 查看任务要求
                mTvRequire.setText(R.string.label_watch_task_requirement);
            } else {
                mTvRequire.setText(R.string.label_watch_task_requirement);
                // mTvRequire.setText(R.string.label_courseware_detail);
            }

            // 任务头像
            ImageUtil.fillNotificationView(mTaskAvatar, taskEntity.getResThumbnailUrl());
            // 任务标题
            StringUtil.fillSafeTextView(mTaskName, taskEntity.getTitle());
            // 任务类型
            if (taskEntity.getT_TaskType() == 5 || taskEntity.getT_TaskType() == 12) {
                // 听读课
                String typeName = String.format(UIUtil.getString(R.string.label_task_type_template), UIUtil.getString(R.string.label_tutorial_task_type_listen_read_course));
                StringUtil.fillSafeTextView(mTaskType, typeName);
            } else {
                // 做读写单
                String typeName = String.format(UIUtil.getString(R.string.label_task_type_template), UIUtil.getString(R.string.label_tutorial_task_type_do_task));
                StringUtil.fillSafeTextView(mTaskType, typeName);
            }

            if (EmptyUtil.isNotEmpty(taskEntity.getT_ClassName())) {
                // 任务班级
                StringUtil.fillSafeTextView(mTaskClass, taskEntity.getT_ClassName());
                mTaskClass.setVisibility(View.VISIBLE);
            } else {
                mTaskClass.setVisibility(View.INVISIBLE);
            }

            // 需求更改，不显示班级名
            mTaskClass.setVisibility(View.INVISIBLE);

            // 显示提交时间，保留到分
            // 优先使用更新时间
            String updateTime = taskEntity.getUpdateTime();
            String createTime = taskEntity.getCreateTime();
            if (EmptyUtil.isNotEmpty(updateTime)) {
                if (updateTime.contains(":")) {
                    updateTime = updateTime.substring(0, updateTime.lastIndexOf(":"));
                }
                mTaskTime.setText(updateTime);
            } else if (EmptyUtil.isNotEmpty(createTime)) {
                if (createTime.contains(":")) {
                    createTime = createTime.substring(0, createTime.lastIndexOf(":"));
                }
                mTaskTime.setText(createTime);
            }

            if (EmptyUtil.isNotEmpty(taskEntity.getT_CourseName())) {
                mTaskChapter.setVisibility(View.VISIBLE);
                StringUtil.fillSafeTextView(mTaskChapter, String.format(UIUtil.getString(R.string.label_placeholder_book), taskEntity.getT_CourseName()));
            } else {
                mTaskChapter.setVisibility(View.INVISIBLE);
            }

            // 设置批阅状态
            if (taskEntity.getReviewState() == MarkingStateType.MARKING_STATE_HAVE) {
                // 已批阅
                mCheckMark.setActivated(true);
                mCheckMark.setTextColor(UIUtil.getColor(R.color.colorAccent));
                mCheckMark.setText(R.string.label_have_mark);
            } else {
                // 未批阅
                mCheckMark.setActivated(false);
                mCheckMark.setTextColor(UIUtil.getColor(android.R.color.holo_red_light));
                mCheckMark.setText(R.string.label_un_mark);
            }

            mRequireLayout.setOnClickListener(v -> {
                // 点击查看任务要求
                if (EmptyUtil.isNotEmpty(mCallback)) {
                    int position = getAdapterPosition();
                    final TaskEntity entity = taskEntity;
                    if (entity.getT_TaskId() > 0) {
                        mCallback.onRequireClick(v, position, entity);
                    } else {
                        mCallback.onEntityClick(v, position, entity, taskEntity.getReviewState());
                    }
                }
            });

            mCheckMark.setOnClickListener(v -> {
                // 点击已批阅未批阅
                if (EmptyUtil.isNotEmpty(mCallback)) {
                    int position = getAdapterPosition();
                    final TaskEntity entity = taskEntity;
                    mCallback.onCheckMark(v, position, entity, taskEntity.getReviewState());
                }
            });

            // 实体点击
            mBodyLayout.setOnClickListener(v -> {
                // 点击实体
                if (EmptyUtil.isNotEmpty(mCallback)) {
                    int position = getAdapterPosition();
                    final TaskEntity entity = taskEntity;
                    mCallback.onEntityClick(v, position, entity, taskEntity.getReviewState());
                }
            });
            //过期时长
            // 有效期为24h，过期时间规则：
            //1、到12h，提示“12h后过期”
            //2、到6h，提示“6h后过期”
            //3、到2h以后，每个小时都提示，提示“2h后过期”、“1h后过期”
            //4、1h的时候，需要给帮辅推送信息
            // 未批阅状态
            if (taskEntity.getReviewState() == MarkingStateType.MARKING_STATE_NOT) {
                String time1 = taskEntity.getStuAssistTime();
                String time2 = taskEntity.getServerNowTime();
                long[] time = getDistanceTime(time1, time2);
                long day = time[0];
                long hour = time[1];
                //（6，12】，（2，6】 （1，2】 （0，1】
                if (day > 0) {
                    mExpiredTime.setText("已过期");
                    mExpiredTime.setBackgroundResource(R.drawable.expired_time_style_4);
                } else if (hour > 6 && hour <= 12) {
                    mExpiredTime.setText("12h内过期");
                    mExpiredTime.setBackgroundResource(R.drawable.expired_time_style_1);
                }else if (hour > 2 && hour <= 6) {
                    mExpiredTime.setText("6h内过期");
                    mExpiredTime.setBackgroundResource(R.drawable.expired_time_style_2);
                }else if (hour > 1 && hour <= 2) {
                    mExpiredTime.setText("2h内过期");
                    mExpiredTime.setBackgroundResource(R.drawable.expired_time_style_3);
                }else if (hour > 0 && hour <= 1) {
                    mExpiredTime.setText("1h内过期");
                    mExpiredTime.setBackgroundResource(R.drawable.expired_time_style_3);
                    //1h的时候，需要给帮辅推送信息
                }
            }
        }
    }

    private long[] getDistanceTime(String starttime, String endtime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        try {
            one = df.parse(starttime);
            two = df.parse(endtime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour};
        return times;
    }


    public interface EntityCallback {
        void onRequireClick(View it, int position, @NonNull TaskEntity entity);

        void onEntityClick(View it, int position, @NonNull TaskEntity entity, int state);

        void onCheckMark(View it, int position, @NonNull TaskEntity entity, int state);
    }
}
