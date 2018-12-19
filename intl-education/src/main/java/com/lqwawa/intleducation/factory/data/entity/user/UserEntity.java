package com.lqwawa.intleducation.factory.data.entity.user;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 用来接收用户的基本信息返回
 */
public class UserEntity extends BaseVo {


    private String RealName;
    private String NickName;
    private boolean IsExist;
    private String UserId;
    private String MemberId;
    private String BindMobile;

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String RealName) {
        this.RealName = RealName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public boolean isIsExist() {
        return IsExist;
    }

    public void setIsExist(boolean IsExist) {
        this.IsExist = IsExist;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String MemberId) {
        this.MemberId = MemberId;
    }

    public String getBindMobile() {
        return BindMobile;
    }

    public void setBindMobile(String BindMobile) {
        this.BindMobile = BindMobile;
    }
}
