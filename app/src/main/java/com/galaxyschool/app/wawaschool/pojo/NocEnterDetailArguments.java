package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

/**
 * Created by E450 on 2017/3/29.
 */

public class NocEnterDetailArguments implements Serializable{
   private String  createTime;//时间
    private String       remark;//简介
    private String   entryNum;//编号
    private String       orgName;//来源
    private String  author;//作者
    private String title;//标题
    private String courseId;//资源id
    private int nocNameForType;//参赛名义
    private int nocPraiseNum;//noc点赞数

    private String resourceUrl;
    public static int JOIN_NAME_FOR_PERSONAL=1;
    public static int JOIN_NAME_FOR_SCHOOL=2;
    private int LeStatus;
    public String getCreateTime() {
        return createTime;
    }

    public int getNocPraiseNum() {
        return nocPraiseNum;
    }

    public void setNocPraiseNum(int nocPraiseNum) {
        this.nocPraiseNum = nocPraiseNum;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getEntryNum() {
        return entryNum;
    }

    public void setEntryNum(String entryNum) {
        this.entryNum = entryNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getNocNameForType() {
        return nocNameForType;
    }

    public void setNocNameForType(int nocNameForType) {
        this.nocNameForType = nocNameForType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public int getLeStatus() {
        return LeStatus;
    }

    public void setLeStatus(int leStatus) {
        LeStatus = leStatus;
    }
}
