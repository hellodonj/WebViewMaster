package com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * @author mrmedici
 * @desc 课程大纲参数
 */
public class CourseChapterParams extends BaseVo{
    // 课程大纲已确定的角色信息
    private int role;
    // 老师看孩子的本人角色信息
    private int realRole;
    // 是否是老师看学生
    private boolean teacherVisitor;
    // 如果是老师，老师的角色类型
    private int teacherType;
    // 当前学生的memberId,家长传孩子的，学生就是自己的
    private String memberId;
    // 是否是试听
    private boolean isAudition;
    // 是否是选择模式
    private boolean choiceMode;
    // 是否是主动触发
    private boolean initiativeTrigger;
    // 讲师助教ID拼接
    private String teacherTutorIds;

    // 课程详情的参数
    private CourseDetailParams courseParams;


    public CourseChapterParams(String memberId, int role,int teacherType, boolean isAudition) {
        this.memberId = memberId;
        this.role = role;
        this.teacherType = teacherType;
        this.isAudition = isAudition;
    }

    public int getRole() {
        return role;
    }

    public int getTeacherType() {
        return teacherType;
    }

    public String getMemberId() {
        return memberId;
    }

    public boolean isAudition() {
        return isAudition;
    }

    public boolean isParentRole(){
        return role == UserHelper.MoocRoleType.PARENT;
    }

    public CourseDetailParams getCourseParams() {
        return courseParams;
    }

    public void setCourseParams(CourseDetailParams courseParams) {
        this.courseParams = courseParams;
    }

    public int getRealRole() {
        return realRole;
    }

    public boolean isTeacherVisitor() {
        return teacherVisitor;
    }

    public void fillVisitorInfo(boolean teacherVisitor,int realRole){
        this.teacherVisitor = teacherVisitor;
        this.realRole = realRole;
    }

    public void setChoiceMode(boolean choiceMode,boolean initiativeTrigger){
        this.choiceMode = choiceMode;
        this.initiativeTrigger = initiativeTrigger;
    }

    public boolean isChoiceMode() {
        return choiceMode;
    }

    public boolean isInitiativeTrigger() {
        return initiativeTrigger;
    }

    public String getTeacherTutorIds() {
        return teacherTutorIds;
    }

    public CourseChapterParams setTeacherTutorIds(String teacherTutorIds) {
        this.teacherTutorIds = teacherTutorIds;
        return this;
    }
}
