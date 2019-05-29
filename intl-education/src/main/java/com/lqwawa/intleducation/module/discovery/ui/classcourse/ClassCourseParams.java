package com.lqwawa.intleducation.module.discovery.ui.classcourse;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * @author mrmedici
 * @desc 班级学程的参数
 */
public class ClassCourseParams extends BaseVo{
    // 是否是班主任
    private boolean isHeadMaster;
    // 机构Id
    private String schoolId;
    // 班级Id
    private String classId;
    // 班级名称
    private String className;
    // 角色信息
    private String roles;
    // 是否老师
    private boolean isTeacher;

    public ClassCourseParams(boolean isHeadMaster,String schoolId, String classId,String className) {
        this.isHeadMaster = isHeadMaster;
        this.schoolId = schoolId;
        this.classId = classId;
        this.className = className;
    }

    /**
     * 班级学程学习任务选择资源的入口
     */
    public ClassCourseParams(String schoolId, String classId){
        this.schoolId = schoolId;
        this.classId = classId;
        // 学习任务选择，肯定是老师角色
        this.roles = "0";
    }

    public boolean isHeadMaster() {
        return isHeadMaster;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isTeacher() {
        return isTeacher;
    }

    public ClassCourseParams setTeacher(boolean teacher) {
        isTeacher = teacher;
        return this;
    }
}
