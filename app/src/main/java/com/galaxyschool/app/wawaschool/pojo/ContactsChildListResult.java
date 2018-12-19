package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsChildListResult
        extends ModelResult<ContactsChildListResult.ContactsChildListModel> {

    public static class ContactsChildListModel extends Model {
        private List<ContactsChildInfo> StuList;

        public List<ContactsChildInfo> getStuList() {
            return StuList;
        }

        public void setStuList(List<ContactsChildInfo> stuList) {
            StuList = stuList;
        }
    }

}
