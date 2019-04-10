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

    private long createTime;
    // vtype == 0时, 0：自充 1代充 2 他人赠送
    private int rechargeType;
    // vtype == 1时, 0:购买课程;1:购买直播;2:学程馆借买书籍, 3购买在线课堂 4 赠送给他人
    private int consumeType;
    // 赠送给别人的id，此时realName，userName 是toId的姓名
    private String toId;
    private int vtype; // 0 充值 1消费
    private int courseId;
    // 别人赠送给我或者别人为我代充的id此时realName, userName是buyerId的姓名
    private String buyerId;
    private String typeC;
    private int amount;
    private int id;
    private int payType;
    private String memberId;
    private String courseName;
    private String mobile;
    private String realName;
    private String userName;
    // 积分明细-余额
    private String remainder;
    // 积分明细-消耗类型：0-自己购买，1-帮别人买
    private int type;
    // 积分明细-1表示赠送 2消耗
    private int recordType;
    // 积分明细-创建时间
    private String createDate;


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(int rechargeType) {
        this.rechargeType = rechargeType;
    }

    public int getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(int consumeType) {
        this.consumeType = consumeType;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public int getVtype() {
        return vtype;
    }

    public void setVtype(int vtype) {
        this.vtype = vtype;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getTypeC() {
        return typeC;
    }

    public void setTypeC(String typeC) {
        this.typeC = typeC;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRemainder() {
        return remainder;
    }

    public void setRemainder(String remainder) {
        this.remainder = remainder;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
