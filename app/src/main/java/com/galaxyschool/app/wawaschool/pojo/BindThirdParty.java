package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/8 0008 14:21
 * Describe:关联账号的信息
 * ======================================================
 */
public class BindThirdParty implements Serializable {
    private String Unionid;
    private int IdentityType;
    private String NickName;

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getUnionid() {
        return Unionid;
    }

    public void setUnionid(String unionid) {
        Unionid = unionid;
    }

    public int getIdentityType() {
        return IdentityType;
    }

    public void setIdentityType(int identityType) {
        IdentityType = identityType;
    }

}
