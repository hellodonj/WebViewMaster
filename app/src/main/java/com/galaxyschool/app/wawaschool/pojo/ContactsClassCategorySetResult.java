package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsClassCategorySetResult
        extends ModelResult<ContactsClassCategorySetResult.ContactsClassCategorySetModel> {

    public static class ContactsClassCategorySetModel extends Model {
        private List<SchoolStage> LevelList;
        private List<SchoolGrade> GradeList;
        private List<SchoolClass> ClassList;

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

        public List<SchoolClass> getClassList() {
            return ClassList;
        }

        public void setClassList(List<SchoolClass> classList) {
            ClassList = classList;
        }
    }

}
