package com.galaxyschool.app.wawaschool.pojo.weike;

/**
 * Created by wangchao on 1/20/16.
 */
public class ShortCourseInfo {
    private String MicroId;
    private String Title;
    private String MaterialId;
    private String MicroID;
    private String CustomType;

    public ShortCourseInfo() {
    }

    public ShortCourseInfo(String microId, String title) {
        MicroId = microId;
        Title = title;
    }

    public String getMicroId() {
        return MicroId;
    }

    public void setMicroId(String microId) {
        MicroId = microId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMaterialId() {
        return MaterialId;
    }

    public void setMaterialId(String materialId) {
        MaterialId = materialId;
    }

    public String getMicroID() {
        return MicroID;
    }

    public void setMicroID(String microID) {
        MicroID = microID;
    }

    public String getCustomType() {
        return CustomType;
    }

    public void setCustomType(String customType) {
        CustomType = customType;
    }
}
