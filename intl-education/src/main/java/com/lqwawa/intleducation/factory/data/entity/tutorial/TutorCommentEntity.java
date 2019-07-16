package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 帮辅老师评论列表的单个实体
 */
public class TutorCommentEntity implements Serializable {

    public static final int STATUS_SHOWING = 1;
    public static final int STATUS_HIDE = 0;

    /**
     * HeadPicUrl : http://filetestop.lqwawa.com/UploadFiles/2018/06/07/37985aee-a85a-47d9-84df-adb5ebb3320a/ed7d03f2-27b6-42cd-aa8b-594e921520aa.jpg
     * content : 这个很牛
     * createName : biubiu
     * createTime : 2019-02-27 15:40:40
     * id : 1
     * memberId : 37985aee-a85a-47d9-84df-adb5ebb3320a
     * praiseNum : 4
     * status : 1
     */

    private String HeadPicUrl;
    private String content;
    private String createName;
    private String createTime;
    private int id;
    private String memberId;
    private int praiseNum;
    private int status;
    private float starLevel; //星级等级

    public String getHeadPicUrl() {
        return HeadPicUrl;
    }

    public void setHeadPicUrl(String HeadPicUrl) {
        this.HeadPicUrl = HeadPicUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

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

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getPraiseNum() {
        return praiseNum;
    }

    public void setPraiseNum(int praiseNum) {
        this.praiseNum = praiseNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isShowing() {
        return this.status == STATUS_SHOWING;
    }

    public boolean isHide() {
        return this.status == STATUS_HIDE;
    }

    public float getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(float starLevel) {
        this.starLevel = starLevel;
    }

}
