package com.lqwawa.intleducation.module.discovery.ui.task.detail;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;

/**
 * @author mrmedici
 * @desc 小节任务详情参数
 */
public class SectionTaskParams extends BaseVo{

    // 原始的角色信息
    private int originalRole;
    // 已经处理的角色信息，用于传给两栖蛙蛙
    private int handleRole;
    // 当前学生的memberId,家长传孩子的，学生就是自己的
    private String memberId;
    // 是否是试听
    private boolean isAudition;

    // 课程详情的参数
    private CourseDetailParams courseParams;

    public SectionTaskParams(int originalRole,int handleRole){
        this.originalRole = originalRole;
        this.handleRole = handleRole;
    }

    public int getOriginalRole() {
        return originalRole;
    }

    public int getHandleRole() {
        return handleRole;
    }

    public String getMemberId() {
        return memberId;
    }

    public CourseDetailParams getCourseParams() {
        return courseParams;
    }

    public void setCourseParams(CourseDetailParams courseParams) {
        this.courseParams = courseParams;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public boolean isAudition() {
        return isAudition;
    }

    public void setAudition(boolean audition) {
        isAudition = audition;
    }

    /**
     * 配置参数
     * @param params 节资源列表的参数实体
     */
    public void fillParams(@NonNull LessonSourceParams params){
        this.isAudition = params.isAudition();
        this.memberId = params.getMemberId();
        this.courseParams = params.getCourseParams();
    }
}
