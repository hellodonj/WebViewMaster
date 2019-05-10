package com.lqwawa.intleducation.module.discovery.ui.task.list;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.base.vo.PagerArgs;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;
import com.lqwawa.intleducation.module.discovery.ui.task.detail.SectionTaskParams;

/**
 * @author mrmedici
 * @desc 任务提交列表的参数定义
 */
public class TaskCommitParams extends BaseVo implements Cloneable{
    // 复述列表
    public static final int TYPE_RETELL_COMMIT = 1;
    // 语音评测列表
    public static final int TYPE_SPEECH_EVALUATION = 5;

    public static final int TYPE_ALL = 0;

    // 原始的角色信息
    private int originalRole;
    // 已经处理的角色信息，用于传给两栖蛙蛙
    private int handleRole;
    // 当前学生的memberId,家长传孩子的，学生就是自己的
    private String memberId;
    // 是否是试听
    private boolean isAudition;
    // 提交类型
    private int commitType;
    // 是否是老师看学生
    private boolean teacherVisitor;

    // 课程详情的参数
    private CourseDetailParams courseParams;

    private PagerArgs pagerArgs;
    private int orderByType;


    public int getOriginalRole() {
        return originalRole;
    }

    public int getHandleRole() {
        return handleRole;
    }

    public String getMemberId() {
        return memberId;
    }

    public boolean isAudition() {
        return isAudition;
    }

    public CourseDetailParams getCourseParams() {
        return courseParams;
    }

    public int getCommitType() {
        return commitType;
    }

    public void setCommitType(int commitType) {
        this.commitType = commitType;
    }

    public boolean isTeacherVisitor() {
        return teacherVisitor;
    }

    public PagerArgs getPagerArgs() {
        return pagerArgs;
    }

    public TaskCommitParams setPagerArgs(PagerArgs pagerArgs) {
        this.pagerArgs = pagerArgs;
        return this;
    }

    public int getOrderByType() {
        return orderByType;
    }

    public TaskCommitParams setOrderByType(int orderByType) {
        this.orderByType = orderByType;
        return this;
    }

    @Override
    public Object clone() {
        try{
            return super.clone();
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 配置参数
     * @param params 节资源列表的参数实体
     */
    public static TaskCommitParams build(@NonNull SectionTaskParams params){
        TaskCommitParams newParams = new TaskCommitParams();
        newParams.originalRole = params.getOriginalRole();
        newParams.handleRole = params.getHandleRole();
        newParams.isAudition = params.isAudition();
        newParams.memberId = params.getMemberId();
        newParams.courseParams = params.getCourseParams();
        newParams.teacherVisitor = params.isTeacherVisitor();
        return newParams;
    }

}
