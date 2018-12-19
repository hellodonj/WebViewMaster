package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsClassMemberListResult
        extends ModelResult<ContactsClassMemberListResult.ContactsClassMemberListModel> {

    public static class ContactsClassMemberListModel extends Model {
        private List<ContactsClassMemberInfo> ClassMailListDetailList;

        public List<ContactsClassMemberInfo> getClassMailListDetailList() {
            return ClassMailListDetailList;
        }

        public void setClassMailListDetailList(List<ContactsClassMemberInfo> classMailListDetailList) {
            ClassMailListDetailList = classMailListDetailList;
        }
    }

}
