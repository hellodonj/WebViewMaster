package com.galaxyschool.app.wawaschool.pojo;

import android.text.TextUtils;
import com.lqwawa.lqbaselib.net.library.Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangchao on 4/2/15.
 */
public class UserInfo extends Model implements Serializable {

    public static final int USER_STATE_STRANGE = 0;
    public static final int USER_STATE_SUBSCRIBED = 1;
    public static final int USER_STATE_FRIEND = 2;

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
    private List<SchoolInfo> SchoolList;
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
    private boolean IsAssistant;
    private boolean IsNewCreateUser;//是否新创建的用户

    public boolean isNewCreateUser() {
        return IsNewCreateUser;
    }

    public void setIsNewCreateUser(boolean newCreateUser) {
        IsNewCreateUser = newCreateUser;
    }

    public boolean isAssistant() {
        return IsAssistant;
    }

    public void setAssistant(boolean assistant) {
        IsAssistant = assistant;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public void setNoteName(String noteName) {
        NoteName = noteName;
    }

    public String getNoteName() {
        return NoteName;
    }

    public boolean isHaveFamily() {
        return HaveFamily;
    }

    public void setHaveFamily(boolean haveFamily) {
        HaveFamily = haveFamily;
    }

    public String getBindMobile() {
		return BindMobile;
	}

	public void setBindMobile(String bindMobile) {
		BindMobile = bindMobile;
	}
	
    public boolean isTeacher() {
        if (!TextUtils.isEmpty(Roles)
                && Roles.contains(String.valueOf(RoleType.ROLE_TYPE_TEACHER))) {
            return true;
        }
        return false;
    }

    public boolean isStudent() {
        if (!TextUtils.isEmpty(Roles)
                && Roles.contains(String.valueOf(RoleType.ROLE_TYPE_STUDENT))) {
            return true;
        }
        return false;
    }

    public boolean isParent(){
        if (!TextUtils.isEmpty(Roles) && Roles.contains(String.valueOf(RoleType.ROLE_TYPE_PARENT))) {
            return true;
        }
        return false;
    }

    public boolean isOnlyStudent() {
        if (!TextUtils.isEmpty(Roles)
                && Roles.contains(String.valueOf(RoleType.ROLE_TYPE_STUDENT))
                && !Roles.contains(String.valueOf(RoleType.ROLE_TYPE_TEACHER))
                && !Roles.contains(String.valueOf(RoleType.ROLE_TYPE_PARENT))
                && !Roles.contains(String.valueOf(RoleType.ROLE_TYPE_VISITOR))) {
            return true;
        }
        return false;
    }

    public boolean isHeaderTeacher() {
        return isTeacher() && HeaderTeacher == 1;
    }

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

    public List<SchoolInfo> getSchoolList() {
        return SchoolList;
    }

    public void setSchoolList(List<SchoolInfo> schoolList) {
        SchoolList = schoolList;
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

//    public boolean hasSubscribed() {
//        return RelationState == 1 || RelationState == 2;
//    }
//
//    public boolean isFriend() {
//        return RelationState == 2;
//    }

    public boolean hasSubscribed() {
        return RelationState == USER_STATE_SUBSCRIBED || RelationState == USER_STATE_FRIEND;
    }

    public void setSubscribed(boolean subscribed) {
        RelationState = subscribed ? USER_STATE_SUBSCRIBED : USER_STATE_STRANGE;
    }

    public boolean isFriend() {
        return RelationState == USER_STATE_FRIEND;
    }

    public void setFriend(boolean isFriend) {
        if (isFriend) {
            State = USER_STATE_FRIEND;
        }
    }

    public boolean isOnlySubscribed(){
        return RelationState == USER_STATE_SUBSCRIBED;
    }

    public String getFamilyName() {
        if(!TextUtils.isEmpty(RealName) && RealName.length() > 0) {
            return RealName.substring(0, 1);
        }
        return null;
    }

    public String getYeyptid() {
        return yeyptid;
    }

    public void setYeyptid(String yeyptid) {
        this.yeyptid = yeyptid;
    }
}
