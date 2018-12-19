package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class SchoolTeacherListResult
        extends ModelResult<SchoolTeacherListResult.SchoolTeacherListModel> {

    public static class SchoolTeacherListModel extends Model {
        private List<SchoolTeacherInfo> TeacherList;

        public List<SchoolTeacherInfo> getTeacherList() {
            return TeacherList;
        }

        public void setTeacherList(List<SchoolTeacherInfo> teacherList) {
            TeacherList = teacherList;
        }
    }

}
