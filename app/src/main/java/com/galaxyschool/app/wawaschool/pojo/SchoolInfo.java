package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;

import java.io.Serializable;

public class SchoolInfo extends Model implements Serializable {

    public static final int USER_STATE_STRANGE = 0;
    public static final int USER_STATE_SUBSCRIBED = 1;
    public static final int USER_STATE_JOINED = 2;

    private String SchoolId;
    private String SchoolName;
    private String SchoolLogo;
    private String QRCode;
    private String GQRCode;
    private String SchoolIntro;
    private String SchoolAddress;
    private String SchoolPhone;
    private boolean IsAttention;
    private boolean IsinSchool;
    private boolean IsOpenCourse;
    private int State; // 0未关注/未加入 1已关注 2已加入
    private int AttentionNumber;
    private int BrowseNum;
    private String Roles;  // 0老师 1学生 2家长 3/空为游客
    private boolean isSelect;
    private String LiveShowUrl;//校园直播台URL
    public boolean IsSchoolInspector;//是否有校园巡查权限
    private boolean IsLiveShowMgr;//是否是直播管理员
    private boolean IsOnlineSchool;//是不是在线课堂的机构
    private String SchoolIntranetIP;//校园内网的ip

    private String PName;//省
    private String CName;//市
    private String DName;//区

    public String getSchoolIntranetIP() {
        return SchoolIntranetIP;
    }

    public void setSchoolIntranetIP(String schoolIntranetIP) {
        SchoolIntranetIP = schoolIntranetIP;
    }

    public boolean isLiveShowMgr() {
        return IsLiveShowMgr;
    }

    public void setIsLiveShowMgr(boolean liveShowMgr) {
        IsLiveShowMgr = liveShowMgr;
    }

    public void setSchoolInspector(boolean schoolInspector) {
        IsSchoolInspector = schoolInspector;
    }

    public boolean isSchoolInspector() {
        return IsSchoolInspector;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getSchoolLogo() {
        return SchoolLogo;
    }

    public void setSchoolLogo(String schoolLogo) {
        SchoolLogo = schoolLogo;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getGQRCode() {
        return GQRCode;
    }

    public void setGQRCode(String GQRCode) {
        this.GQRCode = GQRCode;
    }

    public String getSchoolIntro() {
        return SchoolIntro;
    }

    public void setSchoolIntro(String schoolIntro) {
        SchoolIntro = schoolIntro;
    }

    public String getSchoolAddress() {
        return SchoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        SchoolAddress = schoolAddress;
    }

    public String getSchoolPhone() {
        return SchoolPhone;
    }

    public void setSchoolPhone(String schoolPhone) {
        SchoolPhone = schoolPhone;
    }

    public boolean isAttention() {
        return IsAttention;
    }

    public void setIsAttention(boolean isAttention) {
        IsAttention = isAttention;
    }

    public boolean isinSchool() {
        return IsinSchool;
    }

    public void setIsinSchool(boolean isinSchool) {
        IsinSchool = isinSchool;
    }

    public boolean isOpenCourse() {
        return IsOpenCourse;
    }

    public void setIsOpenCourse(boolean isOpenCourse) {
        IsOpenCourse = isOpenCourse;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public boolean hasSubscribed() {
        return State == USER_STATE_SUBSCRIBED || State == USER_STATE_JOINED;
    }

    public void setSubscribed(boolean subscribed) {
        State = subscribed ? USER_STATE_SUBSCRIBED : USER_STATE_STRANGE;
    }

    public boolean hasJoinedSchool() {
        return State == USER_STATE_JOINED;
    }

    public void setJoinedSchool(boolean joined) {
        if (joined) {
            State = USER_STATE_JOINED;
        }
    }

    public int getAttentionNumber() {
        return AttentionNumber;
    }

    public void setAttentionNumber(int attentionNumber) {
        AttentionNumber = attentionNumber;
    }

    public int getBrowseNum() {
        return BrowseNum;
    }

    public void setBrowseNum(int browseNum) {
        BrowseNum = browseNum;
    }

    public String getRoles() {
        return Roles;
    }

    public void setRoles(String roles) {
        Roles = roles;
    }

    public boolean isTeacher() {
        return Roles != null && Roles.contains("0");
    }

    public boolean isParent() {
        return Roles != null && Roles.contains("2");
    }

    public boolean isStudent() {
        return Roles != null && Roles.contains("1");
    }

    public boolean isTourist() {
        return Roles != null && (Roles.contains("3")||Roles.trim().equals(""));
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getLiveShowUrl() {
        return LiveShowUrl;
    }

    public void setLiveShowUrl(String liveShowUrl) {
        LiveShowUrl = liveShowUrl;
    }

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getCName() {
        return CName;
    }

    public void setCName(String CName) {
        this.CName = CName;
    }

    public String getDName() {
        return DName;
    }

    public void setDName(String DName) {
        this.DName = DName;
    }

    public boolean isOnlineSchool() {
        return IsOnlineSchool;
    }

    public void setIsOnlineSchool(boolean onlineSchool) {
        IsOnlineSchool = onlineSchool;
    }
}
