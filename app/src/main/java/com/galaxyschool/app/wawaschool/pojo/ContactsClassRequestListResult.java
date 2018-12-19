package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsClassRequestListResult
        extends ModelResult<ContactsClassRequestListResult.ContactsClassRequestListModel> {

    public static class ContactsClassRequestListModel extends Model {
        private List<ContactsClassRequestInfo> ApplyJoinClassList;

        public List<ContactsClassRequestInfo> getApplyJoinClassList() {
            return ApplyJoinClassList;
        }

        public void setApplyJoinClassList(List<ContactsClassRequestInfo> applyJoinClassList) {
            ApplyJoinClassList = applyJoinClassList;
        }
    }

}
