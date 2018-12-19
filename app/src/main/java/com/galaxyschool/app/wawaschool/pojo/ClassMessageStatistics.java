package com.galaxyschool.app.wawaschool.pojo;

public class ClassMessageStatistics {

    public static final int CLASS_MESSAGE_TYPE_HOMEWORK = NewResourceInfo.TYPE_CLASS_HOMEWORK;
    public static final int CLASS_MESSAGE_TYPE_NOTICE = NewResourceInfo.TYPE_CLASS_NOTICE;
    public static final int CLASS_MESSAGE_TYPE_SHOW = NewResourceInfo.TYPE_CLASS_SHOW;
    public static final int CLASS_MESSAGE_TYPE_COURSE = NewResourceInfo.TYPE_CLASS_COURSE;
    public static final int CLASS_MESSAGE_TYPE_LECTURE = NewResourceInfo.TYPE_CLASS_LECTURE;
    public static final int CLASS_MESSAGE_TYPE_STUDY_TASK = NewResourceInfo.TYPE_CLASS_STUDY_TASK;

    private String ClassId;
    private int TypeCode;
    private String TypeName;
    private int UnReadNumber;

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public int getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(int typeCode) {
        TypeCode = typeCode;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public int getUnReadNumber() {
        return UnReadNumber;
    }

    public void setUnReadNumber(int unReadNumber) {
        UnReadNumber = unReadNumber;
    }

}
