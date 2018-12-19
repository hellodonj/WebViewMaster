package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/12/5.
 * email:man0fchina@foxmail.com
 */

public class MyOrderVo extends BaseVo{


    /**
     * id : 79
     * organId : bfbba4e6-c98a-4160-bca4-540087fb1d89
     * price :
     * status : 1
     * thumbnailUrl :
     * teachersId :
     * courseId : 409
     * teachersName :
     * type : 1
     * organName : 两栖蛙蛙体验学校
     * courseName :
     */

    private int id;
    private String organId;
    private String price;
    private int status;
    private String thumbnailUrl;
    private String teachersId;
    private String courseId;
    private String classId;
    private String teachersName;
    private int type;
    private String organName;
    private String courseName;
    private boolean isJoin;
    private int payType;
    private boolean isExpire;//权限是否已过期
    private boolean deleted;
    // 版本5.9新添加的字段
    private boolean buyAll;
    private int buyChapterNum;

    // 受益人Id
    private String memberId;
    // 付款人Id
    private String buyerId;
    // 支付人Id
    private String payerId;

    // 受益人的姓名
    private String realName;
    // 付款人姓名
    private String buyerName;
    // 支付人姓名
    private String payerName;

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTeachersId() {
        return teachersId;
    }

    public void setTeachersId(String teachersId) {
        this.teachersId = teachersId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public boolean isIsJoin() {
        return isJoin;
    }

    public void setIsJoin(boolean join) {
        isJoin = join;
    }

    public boolean isIsExpire() {
        return isExpire;
    }

    public void setIsExpire(boolean expire) {
        isExpire = expire;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isBuyAll() {
        return buyAll;
    }

    public void setBuyAll(boolean buyAll) {
        this.buyAll = buyAll;
    }

    public int getBuyChapterNum() {
        return buyChapterNum;
    }

    public void setBuyChapterNum(int buyChapterNum) {
        this.buyChapterNum = buyChapterNum;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }
}
