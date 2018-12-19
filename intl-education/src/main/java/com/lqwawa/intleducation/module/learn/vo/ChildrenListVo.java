package com.lqwawa.intleducation.module.learn.vo;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/7/12.
 * email:man0fchina@foxmail.com
 */

public class ChildrenListVo extends BaseVo {
    private String MemberId;
    private String RealName;
    private String Nickname;
    private String HeadPicUrl;
    private String QRCode;
    private String Telephone;
    private String Email;
    private String Memo;
    private String ClassId;
    private String SchoolId;

    private boolean otherMember;
    private boolean choice;

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String MemberId) {
        this.MemberId = MemberId;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String RealName) {
        this.RealName = RealName;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String Nickname) {
        this.Nickname = Nickname;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String HeadPicUrl) {
        this.HeadPicUrl = HeadPicUrl;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String Telephone) {
        this.Telephone = Telephone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String Memo) {
        this.Memo = Memo;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String ClassId) {
        this.ClassId = ClassId;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String SchoolId) {
        this.SchoolId = SchoolId;
    }

    public boolean isOtherMember() {
        return otherMember;
    }

    public void setOtherMember(boolean otherMember) {
        this.otherMember = otherMember;
    }

    public boolean isChoice() {
        return choice;
    }

    public void setChoice(boolean choice) {
        this.choice = choice;
    }

    /**
     * 构建一个对象
     * @param name 用户名和真实姓名
     * @return ChildrenListVo
     */
    public static ChildrenListVo buildVo(@NonNull String name,boolean choice){
        ChildrenListVo vo = new ChildrenListVo();
        vo.setRealName(name);
        vo.setNickname(name);
        vo.setOtherMember(true);
        vo.setChoice(choice);
        return vo;
    }
}
