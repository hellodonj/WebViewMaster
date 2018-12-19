package com.galaxyschool.app.wawaschool.pojo;

/**
 * Author: wangchao
 * Time: 2015/11/06 23:48
 */
public class MaterialInfo {
    String MaterialId;
    String Title;
    String ThumbUrl;
    String ResUrl;
    String CreateDate;

    public MaterialInfo() {

    }

    public MaterialInfo(String materialId, String title, String thumbUrl, String resUrl, String createDate) {
        MaterialId = materialId;
        Title = title;
        ThumbUrl = thumbUrl;
        ResUrl = resUrl;
        CreateDate = createDate;
    }

    public String getMaterialId() {
        return MaterialId;
    }

    public void setMaterialId(String materialId) {
        MaterialId = materialId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getThumbUrl() {
        return ThumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        ThumbUrl = thumbUrl;
    }

    public String getResUrl() {
        return ResUrl;
    }

    public void setResUrl(String resUrl) {
        ResUrl = resUrl;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }
}
