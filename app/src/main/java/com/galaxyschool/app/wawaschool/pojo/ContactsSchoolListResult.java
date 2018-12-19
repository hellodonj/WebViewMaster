package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsSchoolListResult
        extends ModelResult<ContactsSchoolListResult.ContactsSchoolListModel> {

    public static class ContactsSchoolListModel extends Model {
        private List<ContactsSchoolInfo> SchoolList;

        public List<ContactsSchoolInfo> getSchoolList() {
            return SchoolList;
        }

        public void setSchoolList(List<ContactsSchoolInfo> schoolList) {
            SchoolList = schoolList;
        }
    }

}
