package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by renbao on 16-3-23.
 */
public class BookDetail {
    private String Id;//����id
    private String BookName;//����
    private String CoverUrl;//���棨ͼƬ��
    private String Status;//״̬(1�����С�2�����)
    private String CourseType;//�γ�����(1������2ʦѵ��3У��)
    private String CourseTypeName;//�γ��������(ʦѵ�γ̡�������У��)
    private int MicroCount;//΢����
    private int ColCount;//�ղ���
    private String Brief;//���
    private boolean ColStatus;//�ղ�״̬
    private int ReadNumber;//�Ķ����ȷ�ϣ�
    private String ShareAddress;//�����ַ
    private String SchoolName;//ѧУ���
    private String SchoolId;//ѧУid
    // BookDetail bookDetail = JSONObject.parseObject(jsonString, BookDetail.class);

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCourseType() {
        return CourseType;
    }

    public void setCourseType(String courseType) {
        CourseType = courseType;
    }

    public String getCourseTypeName() {
        return CourseTypeName;
    }

    public void setCourseTypeName(String courseTypeName) {
        CourseTypeName = courseTypeName;
    }

    public int getMicroCount() {
        return MicroCount;
    }

    public void setMicroCount(int microCount) {
        MicroCount = microCount;
    }

    public int getColCount() {
        return ColCount;
    }

    public void setColCount(int colCount) {
        ColCount = colCount;
    }

    public String getBrief() {
        return Brief;
    }

    public void setBrief(String brief) {
        Brief = brief;
    }

    public boolean isColStatus() {
        return ColStatus;
    }

    public void setColStatus(boolean colStatus) {
        ColStatus = colStatus;
    }

    public int getReadNumber() {
        return ReadNumber;
    }

    public void setReadNumber(int readNumber) {
        ReadNumber = readNumber;
    }

    public String getShareAddress() {
        return ShareAddress;
    }

    public void setShareAddress(String shareAddress) {
        ShareAddress = shareAddress;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }
}
