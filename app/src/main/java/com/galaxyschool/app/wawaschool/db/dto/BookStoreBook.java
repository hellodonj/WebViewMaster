package com.galaxyschool.app.wawaschool.db.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by remnao on 16-3-23.
 */
@DatabaseTable(tableName = "book_store_book")
public class BookStoreBook implements Serializable{
    @DatabaseField(id = true)
    private String Id;//����id
    @DatabaseField()
    private String BookName;//����
    @DatabaseField()
    private String CoverUrl;//���棨ͼƬ��
    @DatabaseField()
    private int Status; //״̬(1�����С�2�����)
    @DatabaseField()
    private int CourseType;//�γ�����(1������2ʦѵ��3У��)
    @DatabaseField()
    private String SchoolId;//ѧУID
    @DatabaseField()
    private int Count;//�����
    @DatabaseField()
    private String MemberId;//�û�ID
    @DatabaseField()
    private long CreatedOn;//����ʱ��
    private String VersionId;//版本Id
    private String LevelId;//学段id
    private String GradeId;//年级ID
    private String SubjectId;//学科id
    private String VolumeId;//套系id
    private String LanguageId;//语言id
    private String PublisherId;//出版社id

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

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getCourseType() {
        return CourseType;
    }

    public void setCourseType(int courseType) {
        CourseType = courseType;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public long getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(long createdOn) {
        this.CreatedOn = createdOn;
    }

    public String getVersionId() {
        return VersionId;
    }

    public void setVersionId(String versionId) {
        VersionId = versionId;
    }

    public String getLevelId() {
        return LevelId;
    }

    public void setLevelId(String levelId) {
        LevelId = levelId;
    }

    public String getGradeId() {
        return GradeId;
    }

    public void setGradeId(String gradeId) {
        GradeId = gradeId;
    }

    public String getSubjectId() {
        return SubjectId;
    }

    public void setSubjectId(String subjectId) {
        SubjectId = subjectId;
    }

    public String getVolumeId() {
        return VolumeId;
    }

    public void setVolumeId(String volumeId) {
        VolumeId = volumeId;
    }

    public String getLanguageId() {
        return LanguageId;
    }

    public void setLanguageId(String languageId) {
        LanguageId = languageId;
    }

    public String getPublisherId() {
        return PublisherId;
    }

    public void setPublisherId(String publisherId) {
        PublisherId = publisherId;
    }
}
