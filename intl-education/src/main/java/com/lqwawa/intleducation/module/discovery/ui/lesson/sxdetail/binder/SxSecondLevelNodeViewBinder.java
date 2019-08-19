package com.lqwawa.intleducation.module.discovery.ui.lesson.sxdetail.binder;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.ResIconUtils;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.tool.CourseDetails;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.ReadWeikeHelper;
import com.lqwawa.intleducation.module.discovery.ui.task.detail.SectionTaskParams;
import com.lqwawa.intleducation.module.discovery.ui.videodetail.VideoDetailActivity;
import com.lqwawa.intleducation.module.discovery.vo.ExamsAndTestExtrasVo;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;


/**
 * 描述: 第二级别
 * 作者|时间: djj on 2019/7/25 0025 下午 12:01
 */

public class SxSecondLevelNodeViewBinder extends CheckableNodeViewBinder {

    private CheckBox checkbox;
    TextView resName, mTvAutoMask, mViewCount;
    ImageView resIcon, mIvPlayIcon, mIvNeedCommit;
    LinearLayout itemRootLay;
    private int mTaskType = 9;
    // 可以选择的最大条目
    private int maxSelect = 1;

    private SparseArray<ResIconUtils.ResIcon> resIconSparseArray = ResIconUtils.resIconSparseArray;
    //    private boolean mChoiceMode;
    private String TAG = getClass().getSimpleName();


    public SxSecondLevelNodeViewBinder(View itemView) {
        super(itemView);
        resName = (TextView) itemView.findViewById(R.id.tv_res_name);
        resIcon = (ImageView) itemView.findViewById(R.id.iv_res_icon);
        itemRootLay = (LinearLayout) itemView.findViewById(R.id.item_root_lay);
        mIvPlayIcon = (ImageView) itemView.findViewById(R.id.iv_res_play);
        mIvNeedCommit = (ImageView) itemView.findViewById(R.id.iv_need_commit);
        mTvAutoMask = (TextView) itemView.findViewById(R.id.tv_auto_mark);
        mViewCount = (TextView) itemView.findViewById(R.id.tv_view_count);
        checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.checkbox;
    }

    @Override
    public int getToggleTriggerViewId() {
        return R.id.item_root_lay;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_course_lesson_detail_source_layout;
    }

    @Override
    public void bindView(final TreeNode treeNode, Context context) {
        //SectionTaskListVo TaskListVO //SectionDetailsVo SxExamDetailVo
        SectionResListVo vo = (SectionResListVo) treeNode.getValue();
        ExamsAndTestExtrasVo examsAndTestExtrasVo = (ExamsAndTestExtrasVo) treeNode.getExtras();
        if (examsAndTestExtrasVo == null) examsAndTestExtrasVo = new ExamsAndTestExtrasVo();
        boolean mChoiceMode = examsAndTestExtrasVo.ismChoiceMode();
        itemRootLay.setVisibility(View.VISIBLE);
        int resType = vo.getResType();
        if (resType > 10000) resType -= 10000;
        if (examsAndTestExtrasVo.isVideoCourse()) {
            resIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LQwawaImageUtil.loadCourseThumbnail(UIUtil.getContext(), resIcon, vo.getThumbnail());
        } else {
            resIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            setResIcon(resIcon, vo, resType, examsAndTestExtrasVo);
        }
        setResIconLayout(resIcon, examsAndTestExtrasVo.isVideoCourse());
        mIvPlayIcon.setVisibility(examsAndTestExtrasVo.isVideoCourse() ? View.VISIBLE : View.GONE);

        if (examsAndTestExtrasVo.isNeedFlagRead() && vo.isIsRead()) {
            mIvNeedCommit.setImageResource(R.drawable.ic_task_completed);
        } else {
            int resId = examsAndTestExtrasVo.isVideoCourse() ? 0 : R.drawable.ic_need_to_commit;
            mIvNeedCommit.setImageResource(resId);
        }

        if (!mChoiceMode) {
            int taskType = vo.getTaskType();
            if (taskType == 1 || taskType == 4) {
                // 看课件 视频课
                mIvNeedCommit.setVisibility(examsAndTestExtrasVo.isVideoCourse() ? View.VISIBLE : View.GONE);
            } else {
                // 听读课,读写单
                mIvNeedCommit.setVisibility(!examsAndTestExtrasVo.isCourseSelect() ? View.VISIBLE : View.GONE);
            }
        } else {
            mIvNeedCommit.setVisibility(View.GONE);
        }

        checkbox.setVisibility(mChoiceMode ? View.VISIBLE : View.GONE);

        String assigned = context.getString(R.string.label_assigned);
        if (examsAndTestExtrasVo.ismClassTeacher() && vo.isAssigned()) {
            SpannableString spannableString = new SpannableString(assigned + vo.getName().trim());
            ForegroundColorSpan colorSpan =
                    new ForegroundColorSpan(context.getResources().getColor(R.color.textAccent));
            spannableString.setSpan(colorSpan, 0, assigned.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            resName.setText(spannableString);
        } else {
            resName.setText(("" + vo.getName()).trim());
        }
        if (((mTaskType == CourseSelectItemFragment.KEY_RELL_COURSE || vo.getTaskType() == 2) ||
                (mTaskType == CourseSelectItemFragment.KEY_LECTURE_COURSE
                        || vo.getTaskType() == 5 && (examsAndTestExtrasVo.isCourseSelect() || examsAndTestExtrasVo.ismChoiceMode())) || vo.getTaskType() == 1) &&
                SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(vo.getResProperties())) {
            // 只有听说课,才显示语音评测  参考视屏也显示
            mTvAutoMask.setVisibility(View.VISIBLE);
            mTvAutoMask.setText(R.string.label_voice_evaluating);
        } else if ((mTaskType == CourseSelectItemFragment.KEY_TASK_ORDER || vo.getTaskType() == 3) &&
                EmptyUtil.isNotEmpty(vo.getPoint())) {
            // 只有读写单,才显示自动批阅
            mTvAutoMask.setVisibility(View.VISIBLE);
            mTvAutoMask.setText(R.string.label_auto_mark);
        } else {
            mTvAutoMask.setVisibility(View.INVISIBLE);
        }

        int color = UIUtil.getColor(R.color.alertImportant);
        int radius = DisplayUtil.dip2px(UIUtil.getContext(), 10);
        mTvAutoMask.setBackground(DrawableUtil.createDrawable(color, color, radius));
        mViewCount.setText(context.getString(R.string.some_study,
                String.valueOf(vo.getViewCount())));
    }

    //item的点击事件
    @Override
    public void onNodeToggled(int position, TreeNode treeNode, boolean expand, Context context) {
        SectionResListVo resVo = (SectionResListVo) treeNode.getValue();
        ExamsAndTestExtrasVo examsAndTestExtrasVo = (ExamsAndTestExtrasVo) treeNode.getExtras();
        ReadWeikeHelper mReadWeikeHelper = (ReadWeikeHelper) treeView.getExtras();
        boolean mChoiceMode = examsAndTestExtrasVo.ismChoiceMode();
        LessonSourceParams mSourceParams = examsAndTestExtrasVo.getLessonSourceParams();

        if (ButtonUtils.isFastDoubleClick()) return;
        if (resVo.isIsShield()) {
            UIUtil.showToastSafe(R.string.res_has_shield);
            return;
        }
        Activity activity = (Activity) context;
        if (mChoiceMode) {
            if (resVo.getTaskType() == 1 || resVo.getTaskType() == 4
                    || resVo.getTaskType() == 6) {
                // 看课件类型
                if (EmptyUtil.isNotEmpty(mReadWeikeHelper)) {
                    mReadWeikeHelper.readWeike(resVo);
                }
            } else {
                if (TaskSliderHelper.onTaskSliderListener != null &&
                        resVo != null) {
                    TaskSliderHelper.onTaskSliderListener.viewCourse(
                            activity, resVo.getResId(),
                            resVo.getResType(),
                            (activity).getIntent().getStringExtra("schoolId"),
                            SourceFromType.LQ_COURSE);
                }
            }
            return;
        }
        boolean needFlag = examsAndTestExtrasVo.isNeedFlagRead();
        if (resVo.getTaskType() == 1 || resVo.getTaskType() == 4) {
            if (examsAndTestExtrasVo.isVideoCourse()) {
                VideoDetailActivity.start(context, resVo, null);
//                VideoDetailActivity.start(context, resVo, mSourceParams);
            } else {
                // 看课件
                // V5.14 换成看课本,视频课
                mReadWeikeHelper.readWeike(resVo);
            }

            if ((needFlag && !mSourceParams.isParentRole())) {
                // 是已经加入的课程, 并且不是家长身份
                if (mSourceParams.getRole() != UserHelper.MoocRoleType.EDITOR &&
                        mSourceParams.getRole() != UserHelper.MoocRoleType.TEACHER) {
                    // 如果不是主编或者小编
                    // 看课件才FlagRead
                    flagRead(resVo, position, context);
                }
            }
        } else if (resVo.getTaskType() == 2 || resVo.getTaskType() == 5) {
            //复述微课
            if ((needFlag || !mSourceParams.isParentRole()) || mSourceParams.isAudition()) {
                // 是已经加入的课程
                // 或者是试听身份
                enterSectionTaskDetail(activity, resVo, mSourceParams, examsAndTestExtrasVo);
            } else {
                // 其它情况，只能看微课
                mReadWeikeHelper.readWeike(resVo);
            }
        } else if (resVo.getTaskType() == 3 || resVo.getTaskType() == 6) {
            // 6 Q配音
            if ((needFlag || !mSourceParams.isParentRole()) || mSourceParams.isAudition()) {
                // 是已经加入的课程
                // 或者是试听身份
                enterSectionTaskDetail(activity, resVo, mSourceParams, examsAndTestExtrasVo);
            } else {
                if (TaskSliderHelper.onTaskSliderListener != null) {
                    TaskSliderHelper.onTaskSliderListener.viewCourse(activity,
                            resVo.getResId(), resVo.getResType(),
                            examsAndTestExtrasVo.getSchoolId(),
                            activity.getIntent().getBooleanExtra("isPublic", false),
                            activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
                                    .KEY_IS_FROM_MY_COURSE, false)
                                    ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
                }
            }
        }
    }

//    @Override
//    public void onNodeSelectedChanged(Context context, TreeNode treeNode, boolean selected) {
//        super.onNodeSelectedChanged(context, treeNode, selected);
//        SectionResListVo resListVo = (SectionResListVo) treeNode.getValue();
//        ExamsAndTestExtrasVo extrasVo = (ExamsAndTestExtrasVo) treeNode.getExtras();
//        if (resListVo.getTaskType() == 9) {
//            maxSelect = 1;
//        } else {
//            maxSelect = extrasVo.getMultipleChoiceCount();
//        }
//        // 获取已经选中的作业库
//        List<TreeNode> data = treeView.getSelectedNodes();
//        List<TreeNode> choiceArray = new ArrayList<>();
//        if (EmptyUtil.isNotEmpty(data)) {
//            for (TreeNode vo : data) {
//                if (vo.isSelected()) {
//                    choiceArray.add(vo);
//                    if (resListVo.getTaskType() == 9) {
//                        if (choiceArray.size() > 1) {
//                            ToastUtil.showToast(context, context.getString(R.string.str_select_count_tips, maxSelect));
//                            return;
//                        }
//                    } else {
//                        if (choiceArray.size() > extrasVo.getMultipleChoiceCount()) {
//                            ToastUtil.showToast(context, context.getString(R.string.str_select_count_tips, maxSelect));
//                            return;
//                        }
//                    }
//                }
//            }
//        }
//
//    }

    private void setResIcon(ImageView imageView, SectionResListVo vo, int resType, ExamsAndTestExtrasVo examsAndTestExtrasVo) {
        if (vo.isIsShield()) {
            imageView.setImageResource(resIconSparseArray.get(resType).shieldResId);
        } else {
            if (examsAndTestExtrasVo.isNeedFlagRead() && vo.isIsRead()) {
                imageView.setImageResource(resIconSparseArray.get(resType).readResId);
            } else if (examsAndTestExtrasVo.isNeedFlagRead() && !vo.isIsRead() && examsAndTestExtrasVo.getLessonStatus() == 1 && (resType != 24
                    || resType != 25)) {
                // txt word 不设置
                imageView.setImageResource(resIconSparseArray.get(resType).newResId);
            } else {
                imageView.setImageResource(resIconSparseArray.get(resType).resId);
            }
        }
    }

    private void setResIconLayout(ImageView resIconIv, boolean isVideoLibrary) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) resIconIv.getLayoutParams();
        int iconWidth = DisplayUtil.dip2px(UIUtil.getContext(), 48);
        int iconHeight = iconWidth;
        if (isVideoLibrary) {
            iconHeight = DisplayUtil.dip2px(UIUtil.getContext(), 72);
            iconWidth = iconHeight * 16 / 9;
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        } else {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        }
        layoutParams.width = iconWidth;
        layoutParams.height = iconHeight;
        resIconIv.setLayoutParams(layoutParams);
    }

    private void flagRead(final SectionResListVo vo, final int position, Context context) {

        LessonHelper.requestAddSourceFlag(vo.getTaskType(), vo.getId(), vo.getResId(), new DataSource.SucceedCallback<Void>() {
            @Override
            public void onDataLoaded(Void aVoid) {
//                SectionResListVo item = (SectionResListVo) mCourseResListAdapter.getItem(position);
                vo.setStatus(1);
                treeView.notifychanged();
                // 发送广播,课程大纲刷新UI
                CourseDetails.courseDetailsTriggerStudyTask(context.getApplicationContext(), CourseDetailsItemFragment.LQWAWA_ACTION_CAN_COURSEWARE);
            }
        });
    }

    protected void enterSectionTaskDetail(Activity context, SectionResListVo vo, LessonSourceParams lessonSourceParams, ExamsAndTestExtrasVo examsAndTestExtrasVo) {
        String curMemberId = lessonSourceParams.getMemberId();
        int originalRole = lessonSourceParams.getRole();
        if (lessonSourceParams.isTeacherVisitor()) {
            // 这才是真实的角色身份
            originalRole = lessonSourceParams.getRealRole();
        }


        final String taskId = vo.getTaskId();
        if (originalRole == UserHelper.MoocRoleType.STUDENT && !TextUtils.isEmpty(taskId)) {
            // 分发任务
            LessonHelper.DispatchTask(taskId, curMemberId, null);
        }

        // 处理需要转换的角色
        int handleRole = originalRole;
        boolean isAudition = lessonSourceParams.isAudition();
        if (lessonSourceParams.isCounselor() || lessonSourceParams.isAudition()) {
            // 如果是辅导老师身份 或者试听
            // 角色按照浏览者，家长身份处理
            handleRole = UserHelper.MoocRoleType.PARENT;
        }

        if (handleRole != UserHelper.MoocRoleType.PARENT) {
            // 家长身份优先
            if (TextUtils.equals(UserHelper.getUserId(), vo.getCreateId())) {
                // 任务的创建者 小编
                handleRole = UserHelper.MoocRoleType.TEACHER;
            }

            if (lessonSourceParams.isLecturer()) {
                // 如果是讲师身份 主编身份处理
                handleRole = UserHelper.MoocRoleType.EDITOR;
            }
        }


        // 生成任务详情参数
        SectionTaskParams params = new SectionTaskParams(originalRole, handleRole);
        params.fillParams(lessonSourceParams);

        SectionTaskDetailsActivity.startForResultEx(context, vo, curMemberId, examsAndTestExtrasVo.getSchoolId(), context.getIntent().getBooleanExtra
                        (MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false),
                null, originalRole, handleRole, null, isAudition, examsAndTestExtrasVo.getLibraryType(), params);
    }
}
