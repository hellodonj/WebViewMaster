package com.lqwawa.intleducation.module.tutorial.marking.choice;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 提问资源
 */
public class QuestionResourceModel implements Serializable {

    private String AssMemberId;
    private String StuMemberId;
    private String Title;
    private String ResId;
    private String ResUrl;
    private int T_TaskId;
    private int T_TaskType;
    private int T_CommitTaskId;
    private int T_CommitTaskOnlineId;
    private String T_EQId;
    private int T_AirClassId;
    private String T_ClassId;
    private String T_ClassName;
    private String T_CourseId;
    private String T_CourseName;
    private int T_ResCourseId;

    public String getAssMemberId() {
        return AssMemberId;
    }

    public void setAssMemberId(String assMemberId) {
        AssMemberId = assMemberId;
    }

    public String getStuMemberId() {
        return StuMemberId;
    }

    public void setStuMemberId(String stuMemberId) {
        StuMemberId = stuMemberId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String resId) {
        ResId = resId;
    }

    public String getResUrl() {
        return ResUrl;
    }

    public void setResUrl(String resUrl) {
        ResUrl = resUrl;
    }

    public int getT_TaskId() {
        return T_TaskId;
    }

    public void setT_TaskId(int t_TaskId) {
        T_TaskId = t_TaskId;
    }

    public int getT_TaskType() {
        return T_TaskType;
    }

    public void setT_TaskType(int t_TaskType) {
        T_TaskType = t_TaskType;
    }

    public int getT_CommitTaskId() {
        return T_CommitTaskId;
    }

    public void setT_CommitTaskId(int t_CommitTaskId) {
        T_CommitTaskId = t_CommitTaskId;
    }

    public int getT_CommitTaskOnlineId() {
        return T_CommitTaskOnlineId;
    }

    public void setT_CommitTaskOnlineId(int t_CommitTaskOnlineId) {
        T_CommitTaskOnlineId = t_CommitTaskOnlineId;
    }

    public String getT_EQId() {
        return T_EQId;
    }

    public void setT_EQId(String t_EQId) {
        T_EQId = t_EQId;
    }

    public int getT_AirClassId() {
        return T_AirClassId;
    }

    public void setT_AirClassId(int t_AirClassId) {
        T_AirClassId = t_AirClassId;
    }

    public String getT_ClassId() {
        return T_ClassId;
    }

    public void setT_ClassId(String t_ClassId) {
        T_ClassId = t_ClassId;
    }

    public String getT_ClassName() {
        return T_ClassName;
    }

    public void setT_ClassName(String t_ClassName) {
        T_ClassName = t_ClassName;
    }

    public String getT_CourseId() {
        return T_CourseId;
    }

    public void setT_CourseId(String t_CourseId) {
        T_CourseId = t_CourseId;
    }

    public String getT_CourseName() {
        return T_CourseName;
    }

    public void setT_CourseName(String t_CourseName) {
        T_CourseName = t_CourseName;
    }

    public int getT_ResCourseId() {
        return T_ResCourseId;
    }

    public void setT_ResCourseId(int t_ResCourseId) {
        T_ResCourseId = t_ResCourseId;
    }
}
