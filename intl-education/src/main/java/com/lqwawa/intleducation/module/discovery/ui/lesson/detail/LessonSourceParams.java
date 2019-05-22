package com.lqwawa.intleducation.module.discovery.ui.lesson.detail;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.chapter.CourseChapterParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * @author mrmedici
 * @desc 节资源显示列表片段参数
 */
public class LessonSourceParams extends BaseVo{

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

    private LessonSourceParams(){}

    public LessonSourceParams(String memberId, int role, boolean isAudition) {
        this.memberId = memberId;
        this.role = role;
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

    public int getRealRole() {
        return realRole;
    }

    public void setRealRole(int realRole) {
        this.realRole = realRole;
    }

    public boolean isTeacherVisitor() {
        return teacherVisitor;
    }

    public void setTeacherVisitor(boolean teacherVisitor) {
        this.teacherVisitor = teacherVisitor;
    }

    /**
     * 返回是否是讲师
     * @return true 讲师
     */
    public boolean isLecturer(){
        return teacherType == UserHelper.TeacherType.TEACHER_LECTURER;
    }

    /**
     * 返回是否是助教
     * @return true 助教
     */
    public boolean isAutor(){
        return teacherType == UserHelper.TeacherType.TEACHER_TUTOR;
    }

    /**
     * 返回是否是辅导老师
     * @return true 辅导老师
     */
    public boolean isCounselor(){
        return teacherType == UserHelper.TeacherType.TEACHER_COUNSELOR;
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

    public LessonSourceParams setTeacherTutorIds(String teacherTutorIds) {
        this.teacherTutorIds = teacherTutorIds;
        return this;
    }

    /**
     * 通过课程大纲传到节详情的参数来build出节资源详情列表数据的参数
     * @param params 节详情的参数
     */
    public static LessonSourceParams buildParams(@NonNull CourseChapterParams params){
        LessonSourceParams newParams = new LessonSourceParams();
        newParams.isAudition = params.isAudition();
        newParams.memberId = params.getMemberId();
        newParams.role = params.getRole();
        newParams.teacherType = params.getTeacherType();
        newParams.courseParams = params.getCourseParams();
        newParams.teacherVisitor = params.isTeacherVisitor();
        newParams.realRole = params.getRealRole();
        newParams.setChoiceMode(params.isChoiceMode(),params.isInitiativeTrigger());
        newParams.setTeacherTutorIds(params.getTeacherTutorIds());
        return newParams;
    }

}
