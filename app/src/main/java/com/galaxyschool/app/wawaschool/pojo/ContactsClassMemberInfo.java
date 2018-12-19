package com.galaxyschool.app.wawaschool.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.lqwawa.lqbaselib.net.library.Model;

import java.util.List;

public class ContactsClassMemberInfo extends Model implements Parcelable {

    private String Id;
    private String MemberId;
    private String NoteName;
    private String RealName;
    private String Nickname;
    private int Role;
    private String HeadPicUrl;
    private String Telephone;
    private String Email;
    private String Identity;
    private int WorkingState; //0不在职/不在读 1在职/在读
    private int HeadTeacherState; //班主任标示 0否 1是
    private boolean IsFriend;
    private String schoolId;
    private String classId;
    private int GagMark; //0未禁言 1已禁言
    private String FriendId;
    private int ParentType; // 0家长 1妈妈 2爸爸
    private String StudentName;
    private boolean isSelect;
    ///////////////////班级分组//////////////////////
    /**
     * 小组Id,小组名,老师,学生集合,创建者Id,是否班主任,班级通讯录详情ID
     */
    private String GroupId;
    private String CreateId;
    private String GroupName;
    private List<ContactsClassMemberInfo> TeacherList;
    private List<ContactsClassMemberInfo> StudentList;
    private String ClassMailListDetaileId;
    private boolean IsHeadMaster;

    public ContactsClassMemberInfo() {

    }

    public boolean isHeadMaster() {
        return IsHeadMaster;
    }

    public void setIsHeadMaster(boolean headMaster) {
        IsHeadMaster = headMaster;
        if (headMaster) {
            HeadTeacherState = 1;
        }
    }

    public String getClassMailListDetaileId() {
        return ClassMailListDetaileId;
    }

    public void setClassMailListDetaileId(String classMailListDetaileId) {
        ClassMailListDetaileId = classMailListDetaileId;
        Id = classMailListDetaileId;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String createId) {
        CreateId = createId;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public List<ContactsClassMemberInfo> getTeacherList() {
        return TeacherList;
    }

    public void setTeacherList(List<ContactsClassMemberInfo> teacherList) {
        TeacherList = teacherList;
    }

    public List<ContactsClassMemberInfo> getStudentList() {
        return StudentList;
    }

    public void setStudentList(List<ContactsClassMemberInfo> studentList) {
        StudentList = studentList;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public boolean getIsFriend() {
		return IsFriend;
	}

	public void setIsFriend(boolean isFriend) {
		IsFriend = isFriend;
	}

	public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getNoteName() {
        return NoteName;
    }

    public void setNoteName(String noteName) {
        NoteName = noteName;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int role) {
        Role = role;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
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

    public String getIdentity() {
        return Identity;
    }

    public void setIdentity(String identity) {
        Identity = identity;
    }

    public int getWorkingState() {
        return WorkingState;
    }

    public void setWorkingState(int workingState) {
        WorkingState = workingState;
    }

    public int getHeadTeacherState() {
        return HeadTeacherState;
    }

    public void setHeadTeacherState(int headTeacherState) {
        HeadTeacherState = headTeacherState;
    }

    public boolean isHeadTeacher() {
        return HeadTeacherState == 1;
    }

    public void setIsHeadTeacher(boolean isHeadTeacher) {
        HeadTeacherState = isHeadTeacher ? 1 : 0;
    }

    public int getGagMark() {
        return GagMark;
    }

    public void setGagMark(int gagMark) {
        GagMark = gagMark;
    }

    public boolean isChatForbidden() {
        return GagMark != 0;
    }

    public void setChatForbidden(boolean forbidden) {
        GagMark = forbidden ? 1 : 0;
    }

    public boolean isFriend() {
        return IsFriend;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public int getParentType() {
        return ParentType;
    }

    public void setParentType(int parentType) {
        ParentType = parentType;
    }

    public boolean isFather() {
        return ParentType == 2;
    }

    public boolean isMother() {
        return ParentType == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Id);
        dest.writeString(this.MemberId);
        dest.writeString(this.NoteName);
        dest.writeString(this.RealName);
        dest.writeString(this.Nickname);
        dest.writeInt(this.Role);
        dest.writeString(this.HeadPicUrl);
        dest.writeString(this.Telephone);
        dest.writeString(this.Email);
        dest.writeString(this.Identity);
        dest.writeInt(this.WorkingState);
        dest.writeInt(this.HeadTeacherState);
        dest.writeByte(this.IsFriend ? (byte) 1 : (byte) 0);
        dest.writeString(this.schoolId);
        dest.writeString(this.classId);
        dest.writeInt(this.GagMark);
        dest.writeString(this.FriendId);
        dest.writeInt(this.ParentType);
        dest.writeString(this.StudentName);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeString(this.GroupId);
        dest.writeString(this.CreateId);
        dest.writeString(this.GroupName);
        dest.writeTypedList(this.TeacherList);
        dest.writeTypedList(this.StudentList);
        dest.writeString(this.ClassMailListDetaileId);
        dest.writeByte(this.IsHeadMaster ? (byte) 1 : (byte) 0);
    }

    protected ContactsClassMemberInfo(Parcel in) {
        this.Id = in.readString();
        this.MemberId = in.readString();
        this.NoteName = in.readString();
        this.RealName = in.readString();
        this.Nickname = in.readString();
        this.Role = in.readInt();
        this.HeadPicUrl = in.readString();
        this.Telephone = in.readString();
        this.Email = in.readString();
        this.Identity = in.readString();
        this.WorkingState = in.readInt();
        this.HeadTeacherState = in.readInt();
        this.IsFriend = in.readByte() != 0;
        this.schoolId = in.readString();
        this.classId = in.readString();
        this.GagMark = in.readInt();
        this.FriendId = in.readString();
        this.ParentType = in.readInt();
        this.StudentName = in.readString();
        this.isSelect = in.readByte() != 0;
        this.GroupId = in.readString();
        this.CreateId = in.readString();
        this.GroupName = in.readString();
        this.TeacherList = in.createTypedArrayList(ContactsClassMemberInfo.CREATOR);
        this.StudentList = in.createTypedArrayList(ContactsClassMemberInfo.CREATOR);
        this.ClassMailListDetaileId = in.readString();
        this.IsHeadMaster = in.readByte() != 0;
    }

    public static final Creator<ContactsClassMemberInfo> CREATOR = new Creator<ContactsClassMemberInfo>() {
        @Override
        public ContactsClassMemberInfo createFromParcel(Parcel source) {
            return new ContactsClassMemberInfo(source);
        }

        @Override
        public ContactsClassMemberInfo[] newArray(int size) {
            return new ContactsClassMemberInfo[size];
        }
    };
}
