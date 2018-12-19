package com.lqwawa.intleducation.module.discovery.lessontask.committedtask;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 记得写注释喲
 * @date 2018/04/13 10:47
 * @history v1.0
 * **********************************
 */
public class CommittedTaskAdapter extends RecyclerAdapter<LqTaskCommitVo>{

    private ImageOptions userAvatarImageOptions;
    private ImageOptions courseImageOptions;

    public CommittedTaskAdapter() {

        // 用户头像的ImageOptions
        userAvatarImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCircular(true)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.user_header_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.user_header_def)//加载失败后默认显示图片
                .build();


        // 课程图片显示的ImageOptions
        courseImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();

    }

    @Override
    protected int getItemViewType(int position, LqTaskCommitVo lqTaskCommitVo) {
        return R.layout.item_lesson_committed_task_layout;
    }

    @Override
    protected RecyclerAdapter.ViewHolder<LqTaskCommitVo> onCreateViewHolder(View root, int viewType) {
        return new ViewHolder(root);
    }

    /**
     * 任务提交列表Item ViewHolder
     */
    public static class ViewHolder extends RecyclerAdapter.ViewHolder<LqTaskCommitVo>{

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
        // 课程图片Icon容器
        private FrameLayout mCourseIconLayout;
        // 课程图片显示
        private ImageView mCourseIcon;
        // 课程名称
        private TextView mCourseName;
        // 课程创建时间
        private TextView mCourseCreateName;
        // 批阅按钮
        private TextView mCheckMark;

        public ViewHolder(View itemView) {
            super(itemView);
            mCourseWareDetailsLayout = (FrameLayout)itemView.findViewById(R.id.courseware_details_layout);
            mIconLayout = (FrameLayout)itemView.findViewById(R.id.icon_layout);
            mStudentAvatar = (ImageView) itemView.findViewById(R.id.iv_student_avatar);
            mRedPoint = (ImageView) itemView.findViewById(R.id.red_point);
            mStudentName = (TextView) itemView.findViewById(R.id.tv_student_name);
            mCourseIconLayout = (FrameLayout) itemView.findViewById(R.id.course_icon_layout);
            mCourseIcon = (ImageView) itemView.findViewById(R.id.iv_course_icon);
            mCourseName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mCourseCreateName = (TextView) itemView.findViewById(R.id.tv_course_name);
            mCheckMark = (TextView) itemView.findViewById(R.id.tv_check_mark);
        }

        @Override
        protected void onBind(LqTaskCommitVo lqTaskCommitVo) {
            UIUtil.showToastSafe(lqTaskCommitVo.toString());
        }
    }
}
