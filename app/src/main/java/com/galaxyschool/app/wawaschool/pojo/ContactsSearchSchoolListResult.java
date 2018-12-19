package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsSearchSchoolListResult
        extends ModelResult<ContactsSearchSchoolListResult.ContactsSearchSchoolListModel> {

    public static class ContactsSearchSchoolListModel extends Model {
        private List<ContactsSearchSchoolInfo> SchoolList;

        public List<ContactsSearchSchoolInfo> getSchoolList() {
            return SchoolList;
        }

        public void setSchoolList(List<ContactsSearchSchoolInfo> schoolList) {
            SchoolList = schoolList;
        }
    }

}
