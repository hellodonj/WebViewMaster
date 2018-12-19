package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;

import java.util.List;

public class ConversationInfoList extends Model {

    List<UserBasicInfo> MemberList;
    List<GroupBasicInfo> GroupList;

    public List<UserBasicInfo> getMemberList() {
        return MemberList;
    }

    public void setMemberList(List<UserBasicInfo> memberList) {
        MemberList = memberList;
    }

    public List<GroupBasicInfo> getGroupList() {
        return GroupList;
    }

    public void setGroupList(List<GroupBasicInfo> groupList) {
        GroupList = groupList;
    }
}
