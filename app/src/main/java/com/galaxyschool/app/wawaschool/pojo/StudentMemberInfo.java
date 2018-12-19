package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by Administrator on 2016.06.22.
 * �����б��У��ҳ��ĺ�����Ϣ
 */
public class StudentMemberInfo implements Serializable{
    private String MemberId;
    private String RealName;
    private String NickName;
    private String HeadPicUrl;
    private String QRCode;//��ά��
    private String Telephone;
    private String Email;
    private String Memo;//��ע
    private String ClassId;

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
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

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public UserInfo getUserInfo() {
        UserInfo userInfo = new UserInfo();
        userInfo.setMemberId(MemberId);
        userInfo.setRealName(RealName);
        userInfo.setNickName(NickName);
        userInfo.setHeaderPic(HeadPicUrl);
        userInfo.setQRCode(QRCode);
        userInfo.setMobile(Telephone);
        userInfo.setEmail(Email);
        return userInfo;
    }
}
