package com.galaxyschool.app.wawaschool.pojo;


/**
 * Created by Administrator on 2016/9/28.
 */

public class SchoolCourseInfo{

    /**
     * Id : 36
     * SchoolId : bfbba4e6-c98a-4160-bca4-540087fb1d89
     * SchoolName : 两栖蛙蛙体验学校
     * CourseId : 45bcd99f-9e4c-4857-ab28-c187170933eb
     * CourseName : 私立青岛银河学校小学部
     * CourseLogoUrl :
     * OpenTime : 2016-09-28 10:16:58
     * AuthorizeCount : 0
     * AuthorizeTotal : 5
     * RestAuthorizeNum : 5
     * Remark : 张东亚添加
     * CreateId : af09096d-9c98-42ab-828f-538930e3688e
     * CreateName : 回翠敏
     * CreateTime : 2016-09-28 10:16:58
     * IsHistory : false
     * Deleted : false
     * SchoolLogoUrl : 20160714104014/000000000000000/b60ce190-eb8d-4fc5-8428-3a25ef8021bd.jpg
     * IsPictureBook : false
     * DeletedTime : 1900-01-01 00:00:00
     */

    private int Id;
    private String SchoolId;
    private String SchoolName;
    private String CourseId;
    private String CourseName;
    private String CourseLogoUrl;
    private String OpenTime;
    private int AuthorizeCount;
    private int AuthorizeTotal;
    private int RestAuthorizeNum;
    private String Remark;
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private boolean IsHistory;
    private boolean Deleted;
    private String SchoolLogoUrl;
    private boolean IsPictureBook;
    private String DeletedTime;
    public boolean IsOpenCourse;//是否是已开通的课程

    public boolean isOpenCourse() {
        return IsOpenCourse;
    }

    public void setOpenCourse(boolean openCourse) {
        IsOpenCourse = openCourse;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String SchoolId) {
        this.SchoolId = SchoolId;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String SchoolName) {
        this.SchoolName = SchoolName;
    }

    public String getCourseId() {
        return CourseId;
    }

    public void setCourseId(String CourseId) {
        this.CourseId = CourseId;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String CourseName) {
        this.CourseName = CourseName;
    }

    public String getCourseLogoUrl() {
        return CourseLogoUrl;
    }

    public void setCourseLogoUrl(String courseLogoUrl) {
        CourseLogoUrl = courseLogoUrl;
    }

    public String getOpenTime() {
        return OpenTime;
    }

    public void setOpenTime(String OpenTime) {
        this.OpenTime = OpenTime;
    }

    public int getAuthorizeCount() {
        return AuthorizeCount;
    }

    public void setAuthorizeCount(int AuthorizeCount) {
        this.AuthorizeCount = AuthorizeCount;
    }

    public int getAuthorizeTotal() {
        return AuthorizeTotal;
    }

    public void setAuthorizeTotal(int AuthorizeTotal) {
        this.AuthorizeTotal = AuthorizeTotal;
    }

    public int getRestAuthorizeNum() {
        return RestAuthorizeNum;
    }

    public void setRestAuthorizeNum(int RestAuthorizeNum) {
        this.RestAuthorizeNum = RestAuthorizeNum;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String CreateId) {
        this.CreateId = CreateId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String CreateName) {
        this.CreateName = CreateName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String CreateTime) {
        this.CreateTime = CreateTime;
    }

    public boolean isIsHistory() {
        return IsHistory;
    }

    public void setIsHistory(boolean IsHistory) {
        this.IsHistory = IsHistory;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean Deleted) {
        this.Deleted = Deleted;
    }

    public String getSchoolLogoUrl() {
        return SchoolLogoUrl;
    }

    public void setSchoolLogoUrl(String SchoolLogoUrl) {
        this.SchoolLogoUrl = SchoolLogoUrl;
    }

    public boolean isIsPictureBook() {
        return IsPictureBook;
    }

    public void setIsPictureBook(boolean IsPictureBook) {
        this.IsPictureBook = IsPictureBook;
    }

    public String getDeletedTime() {
        return DeletedTime;
    }

    public void setDeletedTime(String DeletedTime) {
        this.DeletedTime = DeletedTime;
    }
}
