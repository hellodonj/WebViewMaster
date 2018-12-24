package com.lqwawa.intleducation.factory.data.entity.user;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * 接收用户金钱信息的实体
 */
public class CoinEntity extends BaseVo {


    private String memberId;
    private String realName;
    private int amount;
    private String updateDate;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
