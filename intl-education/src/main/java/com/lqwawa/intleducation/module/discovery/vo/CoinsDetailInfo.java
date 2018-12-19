package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/10 17:32
 * desp: 描 述：
 * ================================================
 */

public class CoinsDetailInfo extends BaseVo{


    private int amount;
    private int courseId;
    private String courseName;
    private long createTime;
    private int id;
    private String memberId;
    private int payType;
    private int vtype; //0充值  1消费
    private int consumeType;  //0购买课程   1购买直播  2学程馆借买书籍


    public int getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
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

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getVtype() {
        return vtype;
    }

    public void setVtype(int vtype) {
        this.vtype = vtype;
    }
}
