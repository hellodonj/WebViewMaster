package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.task.detail.SectionTaskParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.ui.ExamCommitListActivity;
import com.lqwawa.intleducation.module.learn.ui.ExamDetailActivity;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.ExamListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * 考试基础类
 * @author: wangchao
 * @date: 2018/04/13
 * @desc:
 */
public class ExamListBaseAdapter extends MyBaseAdapter {

    protected String memberId;
    protected CourseVo courseVo;
    protected CourseDetailParams courseDetailParams;
    protected Activity activity;

    public ExamListBaseAdapter(Activity activity) {
        this.activity = activity;
        memberId = activity.getIntent().getStringExtra("memberId");
        courseVo = (CourseVo) activity.getIntent().getSerializableExtra(CourseVo.class.getSimpleName());
        courseDetailParams = (CourseDetailParams) activity.getIntent().getSerializableExtra
                (CourseDetailParams.class.getSimpleName());
    }


    /**
     * 根据不同身份信息显示不同的按钮状态
     * @param textView 按钮
     * @param type 类型，读写单还是考试
     * @param score 考试分数
     */
    protected void updateActionButton(TextView textView, int type, int score) {
        // 根据memberId courseVo 获取角色信息
        int role = UserHelper.getCourseAuthorRole(memberId, courseVo);
        if (role == UserHelper.MoocRoleType.TEACHER) {
            // 老师身份,可以批阅
            updateButtonState(textView, true);
            textView.setText(R.string.mooc_mark);
        } else {
            //家长,孩子身份
            if (score == -2) {
                updateButtonState(textView, true);
                textView.setText(R.string.into_task);
            } else if (score == -1) {
                // 未批阅
                updateButtonState(textView, false);
                textView.setText(R.string.mooc_unmark);
            } else {
                // score大于0 type=1 or type=2 考试类型,显示分数
                // type=3 or type = 4 任务单类型，显示已经批阅
                updateButtonState(textView, false);
                if (type == 1 || type == 2) {
                    // 试卷，蓝色显示分数
                    textView.setText(score + activity.getResources().getString(R.string.points));
                    textView.setTextColor(activity.getResources().getColor(R.color.com_bg_sky_blue));
                } else if (type == 3 || type == 4) { //任务单
                    // 显示任务单，红色已批阅
                    textView.setText(R.string.mooc_has_marked);
                }
            }
        }

    }

    protected void updateButtonState(TextView textView, boolean isEnable) {
        if (isEnable) {
            textView.setBackgroundResource(R.drawable.com_white_green_edge_radio_bt_bg);
            int textColor = activity.getResources().getColor(R.color.com_text_green);
            textView.setTextColor(textColor);
        } else {
            textView.setBackground(null);
            int textColor = activity.getResources().getColor(R.color.com_text_red);
            textView.setTextColor(textColor);
        }
    }

    /**
     * 点击考试
     * @param vo 考试试题信息
     */
    protected void doExam(ExamListVo vo) {
        // 根据memberId courseVo 获取角色信息
        int role = UserHelper.getCourseAuthorRole(memberId, courseVo);
        if(UserHelper.isCourseCounselor(courseVo,getOnlineTeacher())){
            role = UserHelper.MoocRoleType.TEACHER;
        }

        if (role == UserHelper.MoocRoleType.TEACHER) {
            // 如果是老师十分,打开考试提交列表
            ExamCommitListActivity.start(activity, vo, activity.getIntent().getStringExtra("memberId")
                    ,courseVo);
        } else {
            // 其它身份,进入考试详情
            ExamDetailActivity.start(activity, vo.getCexam().getId(), vo.getCexam().getType(),
                    activity.getIntent().getStringExtra("memberId"), courseVo, null, null);
        }
    }

    /**
     * 点击任务单
     * @param vo 任务单信息
     */
    protected void enterSectionTaskDetail(ExamListVo vo, CourseDetailParams courseDetailParams) {
        if (vo == null || vo.getCexam() == null) {
            return;
        }
        // 根据memberId courseVo 获取角色信息
        int originRole = UserHelper.getCourseAuthorRole(memberId, courseVo);
        final String taskId = vo.getCexam().getStudyTaskId();
        if (originRole == UserHelper.MoocRoleType.STUDENT && !TextUtils.isEmpty(taskId)) {
            // 只有学生角色,才发布任务
            LessonHelper.DispatchTask(taskId, memberId, null);
        }

        // 辅导老师的身份等同家长处理 已经角色处理过的 role
        // 助教相当于小编
        int role = originRole;
        if (originRole == UserHelper.MoocRoleType.TEACHER) {
            // 是老师身份,才有可能是辅导老师身份
            if (UserHelper.isCourseCounselor(courseVo)) {
                // 辅导老师身份等同家长处理
                role = UserHelper.MoocRoleType.PARENT;
            }
            if (UserHelper.isCourseTeacher(courseVo)) {
                // 讲师就是主编
                role = UserHelper.MoocRoleType.EDITOR;
            }
        }

        SectionTaskParams params = new SectionTaskParams(originRole,role);
        params.setCourseParams(courseDetailParams);
        params.setMemberId(memberId);

        // 进入听说课,读写单页面
        SectionTaskDetailsActivity.startForResultEx(activity, null, memberId, activity.getIntent
                ().getStringExtra("schoolId"), activity.getIntent().getBooleanExtra
                (MyCourseDetailsActivity.KEY_IS_FROM_MY_COURSE, false), taskId, originRole,role, vo.getCexam()
                .getId(),5,params);
    }


    protected boolean getOnlineTeacher(){
        return false;
    }
}
