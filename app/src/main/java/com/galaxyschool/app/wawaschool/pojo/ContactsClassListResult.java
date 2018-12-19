package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsClassListResult
        extends ModelResult<ContactsClassListResult.ContactsClassListModel> {

    public static class ContactsClassListModel extends Model {
        private List<ContactsClassInfo> ClassMailListList;

        public List<ContactsClassInfo> getClassMailListList() {
            return ClassMailListList;
        }

        public void setClassMailListList(List<ContactsClassInfo> classMailListList) {
            ClassMailListList = classMailListList;
        }
    }

}
