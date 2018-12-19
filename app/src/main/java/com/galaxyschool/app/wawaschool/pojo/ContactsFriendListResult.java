package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsFriendListResult
        extends ModelResult<ContactsFriendListResult.ContactsFriendListModel> {

    public static class ContactsFriendListModel extends Model {
        private List<ContactsFriendInfo> PersonalMailListList;

        public List<ContactsFriendInfo> getPersonalMailListList() {
            return PersonalMailListList;
        }

        public void setPersonalMailListList(List<ContactsFriendInfo> personalMailListList) {
            PersonalMailListList = personalMailListList;
        }
    }

}
