package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by E450 on 2017/3/23.
 */

public class NocUserInfo implements Serializable {
    private String username;//用户名
    private String userPhone;//用户电话
    private String userMail;//用户邮箱
    private String schoolName;//学校名字
    private String schoolId;//学校Id
    private String schoolPhone;//学校电话
    private String district;//省市区
    private String address;//详细地址
    private int joinNameFor=JOIN_NAME_FOR_SCHOOL;//参赛名义
    public static int JOIN_NAME_FOR_PERSONAL=1;
    public static int JOIN_NAME_FOR_SCHOOL=2;

    public int getJoinNameFor() {
        return joinNameFor;
    }

    public void setJoinNameFor(int joinNameFor) {
        this.joinNameFor = joinNameFor;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchoolPhone() {
        return schoolPhone;
    }

    public void setSchoolPhone(String schoolPhone) {
        this.schoolPhone = schoolPhone;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    public String toString() {
        return "NocUserInfo{" +
                "username='" + username + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userMail='" + userMail + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", schoolPhone='" + schoolPhone + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", joinNameFor=" + joinNameFor +
                '}';
    }
}
