package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by E450 on 2017/08/01.
 * 营养膳食学校信息
 */
public class SchoolYkStateInfo {

    public String SchoolId;//所在学校ID
    public String SchoolName;//所在学校名称
    public int yeyidOfSchool;//学校的弋康标识
    public boolean IsOpenYkServices; //是否开通了弋康服务

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

    public int getYeyidOfSchool() {
        return yeyidOfSchool;
    }

    public void setYeyidOfSchool(int yeyidOfSchool) {
        this.yeyidOfSchool = yeyidOfSchool;
    }

    public boolean getIsOpenYkServices() {
        return IsOpenYkServices;
    }

    public void setOpenYkServices(boolean openYkServices) {
        IsOpenYkServices = openYkServices;
    }
}
