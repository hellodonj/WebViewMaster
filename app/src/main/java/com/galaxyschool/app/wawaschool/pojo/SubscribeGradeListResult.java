package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class SubscribeGradeListResult
        extends ModelResult<SubscribeGradeListResult.SubscribeGradeListModel> {

    public static class SubscribeGradeListModel extends Model {
        private String SchoolId;
        private SubscribeClassInfo TeacherBook;
        private List<SubscribeGradeInfo> LevelGList;
        private List<SubscribeClassInfo> HbaddedClassList;
        private List<SubscribeGradeInfo> NaddedClassList;

        public String getSchoolId() {
            return SchoolId;
        }

        public void setSchoolId(String schoolId) {
            SchoolId = schoolId;
        }

        public SubscribeClassInfo getTeacherBook() {
            return TeacherBook;
        }

        public void setTeacherBook(SubscribeClassInfo teacherBook) {
            TeacherBook = teacherBook;
        }

        public List<SubscribeGradeInfo> getLevelGList() {
            return LevelGList;
        }

        public void setLevelGList(List<SubscribeGradeInfo> levelGList) {
            LevelGList = levelGList;
        }

        public List<SubscribeClassInfo> getHbaddedClassList() {
            return HbaddedClassList;
        }

        public void setHbaddedClassList(List<SubscribeClassInfo> hbaddedClassList) {
            HbaddedClassList = hbaddedClassList;
        }

        public List<SubscribeGradeInfo> getNaddedClassList() {
            return NaddedClassList;
        }

        public void setNaddedClassList(List<SubscribeGradeInfo> naddedClassList) {
            NaddedClassList = naddedClassList;
        }
    }

}
