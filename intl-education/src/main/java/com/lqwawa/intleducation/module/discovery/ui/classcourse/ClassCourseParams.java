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
    // 角色信息
    private String roles;

    public ClassCourseParams(boolean isHeadMaster,String schoolId, String classId) {
        this.isHeadMaster = isHeadMaster;
        this.schoolId = schoolId;
        this.classId = classId;
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
