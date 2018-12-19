package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;

public class ContactsNewFriendRequestCountResult
        extends ModelResult<ContactsNewFriendRequestCountResult.NewFriendRequestCountModel> {

    public static class NewFriendRequestCountModel extends Model {
        private int ApprovalCount;

        public int getApprovalCount() {
            return ApprovalCount;
        }

        public void setApprovalCount(int approvalCount) {
            ApprovalCount = approvalCount;
        }
    }

}
