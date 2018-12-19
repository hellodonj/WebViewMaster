package com.galaxyschool.app.wawaschool.pojo;

public class RoleDetail {
    private String Id;
    private String SchoolId;
    private String SchoolName;
    private String GradeId;
    private String GradeName;
    private String ClassId;
    private String ClassName;
    private String ApplyMemo;
    private int CheckState;
    private String CheckMemo;
    private String MemberId;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getGradeId() {
        return GradeId;
    }

    public void setGradeId(String gradeId) {
        GradeId = gradeId;
    }

    public String getGradeName() {
        return GradeName;
    }

    public void setGradeName(String gradeName) {
        GradeName = gradeName;
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

    public String getApplyMemo() {
        return ApplyMemo;
    }

    public void setApplyMemo(String applyMemo) {
        ApplyMemo = applyMemo;
    }

    public int getCheckState() {
        return CheckState;
    }

    public void setCheckState(int checkState) {
        CheckState = checkState;
    }

    public String getCheckMemo() {
        return CheckMemo;
    }

    public void setCheckMemo(String checkMemo) {
        CheckMemo = checkMemo;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }
}
