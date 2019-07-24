package com.galaxyschool.app.wawaschool.pojo;

public class ContactsClassInfo {

    public static final int POS_HEADTEACHER = 0;
    public static final int POS_TEACHER = 1;
    public static final int POS_STUDENT = 2;
    public static final int POS_PARENT = 3;

    private String ClassId;
    private String ClassMailName;
    private String FirstLetter;
    private String GroupId;
    private String HeadPicUrl;
    private String Id;
    private String LQ_SchoolId;
    private int IsHistory;//0历史班 1开课班
    private int Type;
    private int IsHeader;
    private int GagMark; // 0未禁言 1已禁言
    private int Position; // 0班主任 1老师 2学生 3家长

    private boolean isSelected; //是否选中

    //班级小组
    private String smallGroup;
    private int CourseOnlineStatus;
    private int NewCourseOnlineStatus;
    private int Price;

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getNewCourseOnlineStatus() {
        return NewCourseOnlineStatus;
    }

    public void setNewCourseOnlineStatus(int newCourseOnlineStatus) {
        NewCourseOnlineStatus = newCourseOnlineStatus;
    }

    public int getCourseOnlineStatus() {
        return CourseOnlineStatus;
    }

    public void setCourseOnlineStatus(int courseOnlineStatus) {
        CourseOnlineStatus = courseOnlineStatus;
    }

    public String getSmallGroup() {
        return smallGroup;
    }

    public void setSmallGroup(String smallGroup) {
        this.smallGroup = smallGroup;
        this.Id = smallGroup;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassMailName() {
        return ClassMailName;
    }

    public void setClassMailName(String classMailName) {
        ClassMailName = classMailName;
    }

    public String getFirstLetter() {
        return FirstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        FirstLetter = firstLetter;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getLQ_SchoolId() {
        return LQ_SchoolId;
    }

    public void setLQ_SchoolId(String lQ_SchoolId) {
        LQ_SchoolId = lQ_SchoolId;
    }

    public boolean isHistory() {
        return IsHistory == 0;
    }

    public int getIsHistory() {
        return IsHistory;
    }

    public void setIsHistory(int isHistory) {
        IsHistory = isHistory;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public boolean isClass() {
        return Type == 0;
    }

    public boolean isSchool() {
        return Type == 1;
    }

    public boolean getIsHeader() {
        return IsHeader != 0;
    }

    public void setIsHeader(int isHeader) {
        IsHeader = isHeader;
    }

    public void setIsHeadTeacher(boolean isHeadTeacher) {
        IsHeader = isHeadTeacher ? 1 : 0;
    }

    public boolean getGagMark() {
        return GagMark != 0;
    }

    public void setGagMark(int gagMark) {
        GagMark = gagMark;
    }

    public boolean isChatForbidden() {
        return GagMark == 1;
    }

    public int getPosition() {
        return Position;
    }

    public void setPosition(int position) {
        Position = position;
    }

    public boolean isHeadTeacher() {
        return Position == 0;
    }

    public boolean isTeacher() {
        return Position == 0 || Position == 1;
    }

    public boolean isStudent() {
        return Position == 2;
    }

    public boolean isParent() {
        return Position == 3;
    }

}
