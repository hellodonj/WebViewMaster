package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsSearchPersonListResult
        extends ModelResult<ContactsSearchPersonListResult.ContactsSearchPersonListModel> {

    public static class ContactsSearchPersonListModel extends Model {
        private List<ContactsSearchPersonInfo> NewFriendList;

        public List<ContactsSearchPersonInfo> getNewFriendList() {
            return NewFriendList;
        }

        public void setNewFriendList(List<ContactsSearchPersonInfo> newFriendList) {
            NewFriendList = newFriendList;
        }
    }

}
