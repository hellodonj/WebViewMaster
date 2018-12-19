package com.lqwawa.intleducation.module.learn.vo;

import java.io.Serializable;


public class PublishClassVo implements Serializable {
    private int Id;
    private int ExtId;
    private String SchoolId;
    private String SchoolName;
    private String ClassId;
    private String ClassName;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;
    private String UpdateTime;
    private boolean Deleted;
    private boolean IsBelong;
    private boolean isCanDelete;
    private int Role;

    public PublishClassVo() {
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public boolean isCanDelete() {
        return isCanDelete;
    }

    public void setIsCanDelete(boolean canDelete) {
        isCanDelete = canDelete;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getExtId() {
        return ExtId;
    }

    public void setExtId(int extId) {
        ExtId = extId;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String createId) {
        CreateId = createId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String createName) {
        CreateName = createName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String updateId) {
        UpdateId = updateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String updateName) {
        UpdateName = updateName;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setIsDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public boolean isBelong() {
        return IsBelong;
    }

    public void setIsBelong(boolean belong) {
        IsBelong = belong;
    }
}
