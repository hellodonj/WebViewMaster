package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by XChen on 2016/11/7.
 * email:man0fchina@foxmail.com
 */

@Table(name = "UserInfoVo")
public class UserInfoVo extends BaseVo {
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "userId", property = "NOT NULL")
    private String userId;
    @Column(name = "userOrganName")
    private String userOrganName;
    @Column(name = "userOrganId")
    private String userOrganId;
    @Column(name = "userName")
    private String userName;
    @Column(name = "userEmail")
    private String userEmail;
    @Column(name = "mobile")
    private String mobile;
    @Column(name = "userType")
    private String userType;
    @Column(name = "account")
    private String account;
    @Column(name = "token")
    private String token;
    @Column(name = "thumbnail")
    private String thumbnail;
    @Column(name = "hxAccount")
    private String hxAccount;
    @Column(name = "hxPassword")
    private String hxPassword;
    @Column(name = "roles")
    private String roles;
    @Column(name = "schoolIds")
    private String schoolIds;


    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.token = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserOrganId() {
        return userOrganId;
    }

    public void setUserOrganId(String userOrganId) {
        this.userOrganId = userOrganId;
    }

    public String getUserOrganName() {
        return userOrganName;
    }

    public void setUserOrganName(String userOrganName) {
        this.userOrganName = userOrganName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String lastAccount) {
        this.account = lastAccount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getHxAccount() {
        return hxAccount;
    }

    public void setHxAccount(String hxAccount) {
        this.hxAccount = hxAccount;
    }

    public String getHxPassword() {
        return hxPassword;
    }

    public void setHxPassword(String hxPassword) {
        this.hxPassword = hxPassword;
    }

    @Deprecated
    /**
     * 该角色只是登录用户的角色，包括在线机构和实体机构角色的信息，慎用
     */
    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getSchoolIds() {
        return schoolIds;
    }

    public void setSchoolIds(String schoolIds) {
        this.schoolIds = schoolIds;
    }
}
