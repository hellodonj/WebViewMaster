package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/12/9.
 * email:man0fchina@foxmail.com
 */

public class SomebodyInfo extends BaseVo{

    /**
     * isFriend : true
     */

    private boolean isFriend;
    private List<SomebodyInfoVo> user;

    public boolean isIsFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }

    public List<SomebodyInfoVo> getUser() {
        return user;
    }

    public void setUser(List<SomebodyInfoVo> user) {
        this.user = user;
    }
}
