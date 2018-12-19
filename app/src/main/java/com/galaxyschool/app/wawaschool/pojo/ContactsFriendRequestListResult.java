package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

import java.util.List;

public class ContactsFriendRequestListResult
        extends ModelResult<ContactsFriendRequestListResult.ContactsFriendRequestListModel> {

    public static class ContactsFriendRequestListModel extends Model {
        private List<ContactsFriendRequestInfo> ApplyJoinPersonalList;

        public List<ContactsFriendRequestInfo> getApplyJoinPersonalList() {
            return ApplyJoinPersonalList;
        }

        public void setApplyJoinPersonalList(List<ContactsFriendRequestInfo> applyJoinPersonalList) {
            ApplyJoinPersonalList = applyJoinPersonalList;
        }
    }

}
