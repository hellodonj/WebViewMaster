package com.galaxyschool.app.wawaschool.pojo;

public class CollectionBook {

    private String Id;
    private String MemberId;
    private String OutlineId;
    private String SchoolId;
    private String SchoolName;
    private String BookName;
    private String CoverUrl;
    private String ShareAddress;
    private int CourseType;

    //校本资源库放入我的书架的学校id
    private String CollectionOrigin;
    //判断是不是来自精品资源库
    private boolean IsQualityCourse;

    public boolean isQualityCourse() {
        return IsQualityCourse;
    }

    public void setIsQualityCourse(boolean qualityCourse) {
        IsQualityCourse = qualityCourse;
    }

    public String getCollectionOrigin() {
        return CollectionOrigin;
    }

    public void setCollectionOrigin(String collectionOrigin) {
        CollectionOrigin = collectionOrigin;
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

    public String getOutlineId() {
        return OutlineId;
    }

    public void setOutlineId(String outlineId) {
        OutlineId = outlineId;
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

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        CoverUrl = coverUrl;
    }

    public String getShareAddress() {
        return ShareAddress;
    }

    public void setShareAddress(String shareAddress) {
        ShareAddress = shareAddress;
    }

    public int getCourseType() {
        return CourseType;
    }

    public void setCourseType(int courseType) {
        CourseType = courseType;
    }
}
