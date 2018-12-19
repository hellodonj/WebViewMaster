package com.galaxyschool.app.wawaschool.pojo;

/**
 * Author: wangchao
 * Time: 2015/11/06 23:48
 */
public class MaterialType {
    String MaterialId;
    String Title;
    int TypeCode;

    public MaterialType() {

    }

    public MaterialType(String materialId, String title, int typeCode) {
        MaterialId = materialId;
        Title = title;
        TypeCode = typeCode;
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

    public int getTypeCode() {
        return TypeCode;
    }

    public void setTypeCode(int typeCode) {
        TypeCode = typeCode;
    }
}
