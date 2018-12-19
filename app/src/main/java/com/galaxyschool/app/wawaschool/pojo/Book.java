package com.galaxyschool.app.wawaschool.pojo;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.io.Serializable;
@DatabaseTable(tableName = "book")
public class Book implements Serializable{
    @DatabaseField(id = true)
    private String Id;
    @DatabaseField()
    private String VersionId;
    @DatabaseField()
    private String LevelId;
    @DatabaseField()
    private String GradeId;
    @DatabaseField()
    private String SubjectId;
    @DatabaseField()
    private String VolumeId;
    @DatabaseField()
    private String PublisherId;
    @DatabaseField()
    private String LanguageId;
    @DatabaseField()
    private String BookName;
    @DatabaseField()
    private String ResType;
    @DatabaseField()
    private String CoverUrl;
    @DatabaseField()
    private int CourseType;
    @DatabaseField()
    private int FromType;
    @DatabaseField()
    private long CreatedOn;
    @DatabaseField()
    private String MemberId;
    @DatabaseField()
    private String SchoolId;

    public interface FromType{
        int FROM_PLATFORM = 1;//平台大纲
        int FROM_QUOTE_PLATFORM = 2; //应用平台大纲
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
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

    public String getPublisherId() {
        return PublisherId;
    }

    public void setPublisherId(String publisherId) {
        PublisherId = publisherId;
    }

    public String getLanguageId() {
        return LanguageId;
    }

    public void setLanguageId(String languageId) {
        LanguageId = languageId;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getResType() {
        return ResType;
    }

    public void setResType(String resType) {
        ResType = resType;
    }

    public String getCoverUrl() {
        return CoverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        CoverUrl = coverUrl;
    }

    public int getCourseType() {
        return CourseType;
    }

    public void setCourseType(int courseType) {
        CourseType = courseType;
    }

    public int getFromType() {
        return FromType;
    }

    public void setFromType(int fromType) {
        FromType = fromType;
    }

    public long getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(long createdOn) {
        CreatedOn = createdOn;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getSchoolId() {
        return SchoolId;
    }

    public void setSchoolId(String schoolId) {
        SchoolId = schoolId;
    }
}
