package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by KnIghT on 16-6-15.
 */
public class StudyTaskClassInfo {
    private String ClassId;//班级ID
    private String ClassName ;//班级名称
    private List<StudyTaskInfo> TaskInfoList;

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public List<StudyTaskInfo> getTaskInfoList() {
        return TaskInfoList;
    }

    public void setTaskInfoList(List<StudyTaskInfo> taskInfoList) {
        TaskInfoList = taskInfoList;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }
}

