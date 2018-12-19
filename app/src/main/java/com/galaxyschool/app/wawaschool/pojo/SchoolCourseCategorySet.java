package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class SchoolCourseCategorySet {

    private List<SchoolStage> LevelList;
    private List<SchoolGrade> GradeList;
    private List<SchoolSubject> SubjectList;

    public List<SchoolStage> getLevelList() {
        return LevelList;
    }

    public void setLevelList(List<SchoolStage> levelList) {
        LevelList = levelList;
    }

    public List<SchoolGrade> getGradeList() {
        return GradeList;
    }

    public void setGradeList(List<SchoolGrade> gradeList) {
        GradeList = gradeList;
    }

    public List<SchoolSubject> getSubjectList() {
        return SubjectList;
    }

    public void setSubjectList(List<SchoolSubject> subjectList) {
        SubjectList = subjectList;
    }

}
