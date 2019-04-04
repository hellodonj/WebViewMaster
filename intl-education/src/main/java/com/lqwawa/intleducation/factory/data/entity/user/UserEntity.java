package com.lqwawa.intleducation.factory.data.entity.user;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 用来接收用户的基本信息返回
 */
public class UserEntity extends BaseVo {


    private String MemberId;
    private String NickName;
    private String UserName;
    private String Password;
    private String RealName;
    private String Sex;
    private String Birthday;
    private String Email;
    private String BindMobile;
    private String Mobile;
    private String HeaderPic;
    private int HeaderTeacher;
    private int State;
    private String QRCode;
    private String Roles; // 0老师 1学生 2家长 3游客
    private String Yeid;
    private String yeyptid;//判断营养膳食
    private String PIntroduces;
    private int AttentionNumber;
    private int BrowseNum;
    private String Location;
    private int RelationState; // 0未关注 1关注 2好友
    private boolean HaveFamily;//返回是否有家庭通讯录
    private String NoteName;
    private String UserId;
    private boolean IsExist;

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBindMobile() {
        return BindMobile;
    }

    public void setBindMobile(String bindMobile) {
        BindMobile = bindMobile;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getHeaderPic() {
        return HeaderPic;
    }

    public void setHeaderPic(String headerPic) {
        HeaderPic = headerPic;
    }

    public int getHeaderTeacher() {
        return HeaderTeacher;
    }

    public void setHeaderTeacher(int headerTeacher) {
        HeaderTeacher = headerTeacher;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public String getQRCode() {
        return QRCode;
    }

    public void setQRCode(String QRCode) {
        this.QRCode = QRCode;
    }

    public String getRoles() {
        return Roles;
    }

    public void setRoles(String roles) {
        Roles = roles;
    }

    public String getYeid() {
        return Yeid;
    }

    public void setYeid(String yeid) {
        Yeid = yeid;
    }

    public String getYeyptid() {
        return yeyptid;
    }

    public void setYeyptid(String yeyptid) {
        this.yeyptid = yeyptid;
    }

    public String getPIntroduces() {
        return PIntroduces;
    }

    public void setPIntroduces(String PIntroduces) {
        this.PIntroduces = PIntroduces;
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

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getRelationState() {
        return RelationState;
    }

    public void setRelationState(int relationState) {
        RelationState = relationState;
    }

    public boolean isHaveFamily() {
        return HaveFamily;
    }

    public void setHaveFamily(boolean haveFamily) {
        HaveFamily = haveFamily;
    }

    public String getNoteName() {
        return NoteName;
    }

    public void setNoteName(String noteName) {
        NoteName = noteName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public boolean isIsExist() {
        return IsExist;
    }

    public void setIsExist(boolean IsExist) {
        this.IsExist = IsExist;
    }
}
