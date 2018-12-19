package com.galaxyschool.app.wawaschool.pojo;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/16.
 */
public class ShortSchoolClassInfo implements Serializable{
    String SchoolId;

    String SchoolName;

    String ClassId;

    String ClassName;

    String GroupId;

    public ShortSchoolClassInfo() {
    }

    public ShortSchoolClassInfo(String schoolId, String schoolName, String classId, String className,String GroupId) {
        SchoolId = schoolId;
        SchoolName = schoolName;
        ClassId = classId;
        ClassName = className;
        this.GroupId = GroupId;
    }
    @JSONField(name="GroupId")
    public String getGroupId() {
        return GroupId;
    }
    @JSONField(name="GroupId")
    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    @JSONField(name="SchoolId")
    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    @JSONField(name="SchoolName")
    public String getSchoolName() {
        return SchoolName;
    }

    @JSONField(name="SchoolName")
    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    @JSONField(name="ClassId")
    public String getClassId() {
        return ClassId;
    }

    @JSONField(name="ClassId")
    public void setClassId(String classId) {
        ClassId = classId;
    }

    @JSONField(name="ClassName")
    public String getClassName() {
        return ClassName;
    }

    @JSONField(name="ClassName")
    public void setClassName(String className) {
        ClassName = className;
    }
}
