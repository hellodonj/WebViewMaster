package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 */

public class PersonalContactVo extends BaseVo{
    int friendRequestCount;
    List<ContactVo> friendList;

    public List<ContactVo> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<ContactVo> friendList) {
        this.friendList = friendList;
    }

    public int getFriendRequestCount() {
        return friendRequestCount;
    }

    public void setFriendRequestCount(int friendRequestCount) {
        this.friendRequestCount = friendRequestCount;
    }
}
