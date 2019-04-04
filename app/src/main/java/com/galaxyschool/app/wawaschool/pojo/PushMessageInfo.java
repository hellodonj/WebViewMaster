package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Describe:极光推送实体类
 * ======================================================
 */
public class PushMessageInfo implements Serializable {
    private String SchoolId;
    private String ClassId;
    private int AirClassId;
    private int Role;
    private int PushModuleType;
    private boolean IsOnlineSchool;
    private boolean IsEmcee;
    private boolean IsStudent;
    private String StudentId;
    private String TeacherId;
    private int AssistTask_Id;

    public String getStudentId() {
        return StudentId;
    }

    public void setStudentId(String studentId) {
        StudentId = studentId;
    }

    public String getTeacherId() {
        return TeacherId;
    }

    public void setTeacherId(String teacherId) {
        TeacherId = teacherId;
    }

    public int getAssistTask_Id() {
        return AssistTask_Id;
    }

    public void setAssistTask_Id(int assistTask_Id) {
        AssistTask_Id = assistTask_Id;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public int getAirClassId() {
        return AirClassId;
    }

    public void setAirClassId(int airClassId) {
        AirClassId = airClassId;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public int getPushModuleType() {
        return PushModuleType;
    }

    public void setPushModuleType(int pushModuleType) {
        PushModuleType = pushModuleType;
    }

    public boolean isOnlineSchool() {
        return IsOnlineSchool;
    }

    public void setIsOnlineSchool(boolean onlineSchool) {
        IsOnlineSchool = onlineSchool;
    }

    public boolean isEmcee() {
        return IsEmcee;
    }

    public void setIsEmcee(boolean emcee) {
        IsEmcee = emcee;
    }

    public boolean isStudent() {
        return IsStudent;
    }

    public void setIstudent(boolean student) {
        IsStudent = student;
    }
}
