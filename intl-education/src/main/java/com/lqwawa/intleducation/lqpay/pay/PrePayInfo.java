package com.lqwawa.intleducation.lqpay.pay;

import com.google.gson.annotations.SerializedName;
import com.lqwawa.intleducation.base.vo.BaseVo;

/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/17 16:10
  * 描    述：用于微信支付.// TODO 集成时请按照自身需求修改此类
  * 修订历史：
  * ================================================
  */

public class PrePayInfo extends BaseVo {
    public String appid;
    public String partnerid;
    public String prepayid;
    @SerializedName("package")
    public String packageValue;
    public String noncestr;
    public String timestamp;
    public String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


     @Override
     public String toString() {
         return "PrePayInfo{" +
                 "appid='" + appid + '\'' +
                 ", partnerid='" + partnerid + '\'' +
                 ", prepayid='" + prepayid + '\'' +
                 ", packageValue='" + packageValue + '\'' +
                 ", noncestr='" + noncestr + '\'' +
                 ", timestamp='" + timestamp + '\'' +
                 ", sign='" + sign + '\'' +
                 '}';
     }
 }
