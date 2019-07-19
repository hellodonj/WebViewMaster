package com.lqwawa.intleducation.common.ui.treeview.binder;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.ResIconUtils;
import com.lqwawa.intleducation.common.ui.treeview.TreeNode;
import com.lqwawa.intleducation.common.ui.treeview.base.CheckableNodeViewBinder;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.vo.ExamsAndTestExtrasVo;
import com.lqwawa.intleducation.module.discovery.vo.SxExamDetailVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;


/**
 * Created by zxy on 17/4/23.
 */

public class SecondLevelNodeViewBinder extends CheckableNodeViewBinder {

    private CheckBox checkbox;
    TextView resName, mTvAutoMask, mViewCount;
    ImageView resIcon, mIvPlayIcon, mIvNeedCommit;
    LinearLayout itemRootLay;
    private int mTaskType = 9;

    private SparseArray<ResIconUtils.ResIcon> resIconSparseArray = ResIconUtils.resIconSparseArray;
    //    private boolean mChoiceMode;
    private String TAG = getClass().getSimpleName();

    public SecondLevelNodeViewBinder(View itemView) {
        super(itemView);
        resName = (TextView) itemView.findViewById(R.id.tv_res_name);
        resIcon = (ImageView) itemView.findViewById(R.id.iv_res_icon);
        itemRootLay = (LinearLayout) itemView.findViewById(R.id.item_root_lay);
        mIvPlayIcon = (ImageView) itemView.findViewById(R.id.iv_res_play);
        mIvNeedCommit = (ImageView) itemView.findViewById(R.id.iv_need_commit);
        mTvAutoMask = (TextView) itemView.findViewById(R.id.tv_auto_mark);
        mViewCount = (TextView) itemView.findViewById(R.id.tv_view_count);
        checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
//        mIvNeedCommit = (ImageView) itemView.findViewById(R.id.iv_need_commit);
    }

    @Override
    public int getCheckableViewId() {
        return R.id.checkbox;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_course_lesson_detail_source_layout;
    }

    @Override
    public void bindView(final TreeNode treeNode, Context context) {
        SxExamDetailVo.TaskListVO.DetailVo vo = (SxExamDetailVo.TaskListVO.DetailVo) treeNode.getValue();
        ExamsAndTestExtrasVo extras = (ExamsAndTestExtrasVo) treeNode.getExtras();
        boolean mChoiceMode = treeNode.isShowCheckBox();
        itemRootLay.setVisibility(View.VISIBLE);
        int resType = vo.resType;
        if (resType > 10000) resType -= 10000;
        if (extras.isVideoCourse()) {
            resIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LQwawaImageUtil.loadCourseThumbnail(UIUtil.getContext(), resIcon, vo.thumbnail);
        } else {
            resIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            setResIcon(resIcon, vo, resType, extras);
        }
        setResIconLayout(resIcon, extras.isVideoCourse());
        mIvPlayIcon.setVisibility(extras.isVideoCourse() ? View.VISIBLE : View.GONE);

        if (extras.isNeedFlagRead() && vo.isIsRead()) {
            mIvNeedCommit.setImageResource(R.drawable.ic_task_completed);
        } else {
            int resId = extras.isVideoCourse() ? 0 : R.drawable.ic_need_to_commit;
            mIvNeedCommit.setImageResource(resId);
        }
//        Log.e(TAG, "bindView: " +  vo.name  + mChoiceMode);

        if (!mChoiceMode) {
            int taskType = vo.taskType;
            if (taskType == 1 || taskType == 4) {
                // 看课件 视频课
                mIvNeedCommit.setVisibility(extras.isVideoCourse() ? View.VISIBLE : View.GONE);
            } else {
                // 听读课,读写单
                mIvNeedCommit.setVisibility(!extras.isCourseSelect() ? View.VISIBLE : View.GONE);
            }
        } else {
            mIvNeedCommit.setVisibility(View.GONE);
        }

        checkbox.setVisibility(mChoiceMode ? View.VISIBLE : View.GONE);

        String assigned = context.getString(R.string.label_assigned);
        if (extras.ismClassTeacher() && vo.assigned) {
            SpannableString spannableString = new SpannableString(assigned + vo.name.trim());
            ForegroundColorSpan colorSpan =
                    new ForegroundColorSpan(context.getResources().getColor(R.color.textAccent));
            spannableString.setSpan(colorSpan, 0, assigned.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            resName.setText(spannableString);
        } else {
            resName.setText(("" + vo.name).trim());
        }
        if (((mTaskType == CourseSelectItemFragment.KEY_RELL_COURSE || vo.taskType == 2) ||
                (mTaskType == CourseSelectItemFragment.KEY_LECTURE_COURSE
                        || vo.taskType == 5 && (extras.isCourseSelect() || extras.ismChoiceMode())) || vo.taskType == 1) &&
                SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(vo.resProperties)) {
            // 只有听说课,才显示语音评测  参考视屏也显示
            mTvAutoMask.setVisibility(View.VISIBLE);
            mTvAutoMask.setText(R.string.label_voice_evaluating);
        } else if ((mTaskType == CourseSelectItemFragment.KEY_TASK_ORDER || vo.taskType == 3) &&
                EmptyUtil.isNotEmpty(vo.point)) {
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
                String.valueOf(vo.viewCount)));
    }

    @Override
    public void onNodeToggled(TreeNode treeNode, boolean expand) {

    }

    private void setResIcon(ImageView imageView, SxExamDetailVo.TaskListVO.DetailVo vo, int resType, ExamsAndTestExtrasVo extras) {
        if (vo.isShield) {
            imageView.setImageResource(resIconSparseArray.get(resType).shieldResId);
        } else {
            if (extras.isNeedFlagRead() && vo.isIsRead()) {
                imageView.setImageResource(resIconSparseArray.get(resType).readResId);
            } else if (extras.isNeedFlagRead() && !vo.isIsRead() && extras.getLessonStatus() == 1 && (resType != 24
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

}
