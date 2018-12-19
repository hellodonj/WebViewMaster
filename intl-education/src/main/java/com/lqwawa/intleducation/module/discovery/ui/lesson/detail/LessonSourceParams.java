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
    // 如果是老师，老师的角色类型
    private int teacherType;
    // 当前学生的memberId,家长传孩子的，学生就是自己的
    private String memberId;
    // 是否是试听
    private boolean isAudition;

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
        return newParams;
    }

}
