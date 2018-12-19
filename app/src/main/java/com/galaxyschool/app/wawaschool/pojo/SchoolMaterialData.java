package com.galaxyschool.app.wawaschool.pojo;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/4/27 15:46
 * E-Mail Address:863378689@qq.com
 * Describe: 校本资源书本章节下的类型以及数量
 * ======================================================
 */
public class SchoolMaterialData {
    private int SchoolMaterialType;
    private int SchoolMaterialTypeCount;

    public int getSchoolMaterialType() {
        return SchoolMaterialType;
    }

    public void setSchoolMaterialType(int schoolMaterialType) {
        SchoolMaterialType = schoolMaterialType;
    }

    public int getSchoolMaterialTypeCount() {
        return SchoolMaterialTypeCount;
    }

    public void setSchoolMaterialTypeCount(int schoolMaterialTypeCount) {
        SchoolMaterialTypeCount = schoolMaterialTypeCount;
    }
}
