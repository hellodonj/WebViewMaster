package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by KnIghT on 16-5-18.
 */
public class TempShowNewResourceInfo {
    private String UpdateTime;
    private int Id;
    private int ScreenType;
    private String shareurl;
    private String Memo;
    private String ProductTitle;
    private String MemberId;
    private String RealName;
    private String ResourceUrl;
    private String Thumb;
    private String CreateTime;
    private String ProductId;
    private String SchoolId;
    private String SchoolName;
    private String ClassId;
    private String ClassName;

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getScreenType() {
        return ScreenType;
    }

    public void setScreenType(int screenType) {
        ScreenType = screenType;
    }

    public String getShareurl() {
        return shareurl;
    }

    public void setShareurl(String shareurl) {
        this.shareurl = shareurl;
    }

    public String getMemo() {
        return Memo;
    }

    public void setMemo(String memo) {
        Memo = memo;
    }

    public String getProductTitle() {
        return ProductTitle;
    }

    public void setProductTitle(String productTitle) {
        ProductTitle = productTitle;
    }

    public String getMemberId() {
        return MemberId;
    }

    public void setMemberId(String memberId) {
        MemberId = memberId;
    }

    public String getRealName() {
        return RealName;
    }

    public void setRealName(String realName) {
        RealName = realName;
    }

    public String getResourceUrl() {
        return ResourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        ResourceUrl = resourceUrl;
    }

    public String getThumb() {
        return Thumb;
    }

    public void setThumb(String thumb) {
        Thumb = thumb;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
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

    public String getClassId() {
        return ClassId;
    }

    public void setClassId(String classId) {
        ClassId = classId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public static NewResourceInfo pase2NewResourceInfo(TempShowNewResourceInfo tempInfo) {
        if (tempInfo == null) {
            return null;
        }
        NewResourceInfo info = new NewResourceInfo();
        info.setResourceType(tempInfo.getScreenType());
        info.setDescription(tempInfo.getMemo());
        info.setTitle(tempInfo.getProductTitle());
        info.setAuthorId(tempInfo.getMemberId());
        if (tempInfo.getProductId() != null && tempInfo.getProductId().split("-").length > 0) {
            String[] str = tempInfo.getProductId().split("-");
            info.setMicroId(str[0]);
            info.setResourceType(Integer.parseInt(str[1]!=null?str[1]:"0"));
        }
        info.setUpdatedTime(tempInfo.getUpdateTime());
        info.setResourceUrl(tempInfo.getResourceUrl());
        info.setThumbnail(tempInfo.getThumb());
        info.setScreenType(tempInfo.getScreenType());
        info.setAuthorName(tempInfo.getRealName());
        info.setSchoolName(tempInfo.getSchoolName());
        info.setClassName(tempInfo.getClassName());
        return info;
    }

}
