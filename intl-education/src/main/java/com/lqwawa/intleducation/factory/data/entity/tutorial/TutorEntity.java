package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 我的帮辅老师实体
 */
public class TutorEntity implements Serializable {

    /**
     * createTime : Thu Dec 27 00:00:00 CST 2018
     * id : 2
     * isDelete : false
     * stuMemberId : 0018356f-ad4b-439f-88fa-e4cdbf4de32b
     * tutorMemberId : 4b0727b7-26b6-4aed-bd33-06dcee904ae1
     * tutorName : 陸老师
     */

    private String createTime;
    private int id;
    private boolean isDelete;
    private String stuMemberId;
    private String tutorMemberId;
    private String tutorName;
    private String HeadPicUrl;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getStuMemberId() {
        return stuMemberId;
    }

    public void setStuMemberId(String stuMemberId) {
        this.stuMemberId = stuMemberId;
    }

    public String getTutorMemberId() {
        return tutorMemberId;
    }

    public void setTutorMemberId(String tutorMemberId) {
        this.tutorMemberId = tutorMemberId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String headPicUrl) {
        HeadPicUrl = headPicUrl;
    }
}
