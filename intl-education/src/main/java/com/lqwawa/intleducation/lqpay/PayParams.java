package com.lqwawa.intleducation.lqpay;

import android.app.Activity;

import com.lqwawa.intleducation.lqpay.enums.HttpType;
import com.lqwawa.intleducation.lqpay.enums.PayWay;


/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/17 15:04
 * 描    述：支付相关参数.
 * 修订历史：
 * ================================================
 */

public class PayParams {
    private Activity mActivity;
    private String mWechatAppID;
    private PayWay mPayWay;
    private String mGoodsPrice;
    private String mGoodsName;
    private String mGoodsIntroduction;
    private HttpType mHttpType = HttpType.Get;
    private String mApiUrl;
    private String courseId;
    private String chapterIds;
    private String schoolId;
    private String orderId;
    private String memberId;
    // 购买人的memberId
    private String buyerMemberId;
    private String realName;
    private String activeCode;
    private boolean isActCode;//激活码支付
    private int type;
    private boolean isCharge; //充值
    private int recordId;


    @Override
    public String toString() {
        return "PayParams{" +
                "mActivity=" + mActivity +
                ", mWechatAppID='" + mWechatAppID + '\'' +
                ", mPayWay=" + mPayWay +
                ", mGoodsPrice='" + mGoodsPrice + '\'' +
                ", mGoodsName='" + mGoodsName + '\'' +
                ", mGoodsIntroduction='" + mGoodsIntroduction + '\'' +
                ", mHttpType=" + mHttpType +
                ", mApiUrl='" + mApiUrl + '\'' +
                ", courseId='" + courseId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", realName='" + realName + '\'' +
                ", activeCode='" + activeCode + '\'' +
                ", isActCode=" + isActCode +
                ", type=" + type +
                ", isCharge=" + isCharge +
                ", recordId=" + recordId +
                '}';
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public boolean isCharge() {
        return isCharge;
    }

    public void setCharge(boolean charge) {
        isCharge = charge;
    }

    public String getActiveCode() {
        return activeCode;
    }

    public void setActiveCode(String activeCode) {
        this.activeCode = activeCode;
    }

    public boolean isActCode() {
        return isActCode;
    }

    public void setActCode(boolean actCode) {
        isActCode = actCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getChapterIds() {
        return chapterIds;
    }

    public void setChapterIds(String chapterIds) {
        this.chapterIds = chapterIds;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getBuyerMemberId() {
        return buyerMemberId;
    }

    public void setBuyerMemberId(String buyerMemberId) {
        this.buyerMemberId = buyerMemberId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Activity getActivity() {
        return mActivity;
    }

    private void setActivity(Activity activity) {
        mActivity = activity;
    }

    public String getWeChatAppID() {
        return mWechatAppID;
    }

    private void setWechatAppID(String id) {
        mWechatAppID = id;
    }

    public PayWay getPayWay() {
        return mPayWay;
    }

    private void setPayWay(PayWay mPayWay) {
        this.mPayWay = mPayWay;
    }

    public String getGoodsPrice() {
        return mGoodsPrice;
    }

    private void setGoodsPrice(String mGoodsPrice) {
        this.mGoodsPrice = mGoodsPrice;
    }

    public String getGoodsName() {
        return mGoodsName;
    }

    private void setGoodsName(String mGoodsTitle) {
        this.mGoodsName = mGoodsTitle;
    }

    public String getGoodsIntroduction() {
        return mGoodsIntroduction;
    }

    private void setGoodsIntroduction(String mGoodsIntroduction) {
        this.mGoodsIntroduction = mGoodsIntroduction;
    }

    public HttpType getHttpType() {
        return mHttpType;
    }

    private void setHttpType(HttpType mHttpType) {
        this.mHttpType = mHttpType;
    }


    public String getApiUrl() {
        return mApiUrl;
    }

    private void setApiUrl(String mApiUrl) {
        this.mApiUrl = mApiUrl;
    }

    public static class Builder {
        Activity mActivity;
        String wechatAppId;
        PayWay payWay;
        String goodsPrice;
        String goodsName;
        String goodsIntroduction;
        HttpType httpType = HttpType.Get;
        String apiUrl;
        String courseId;
        String chapterIds;
        String schoolId;
        String orderId;
        String memberId;
        // 购买人的memberId
        String buyerMemberId;
        String realName;
        String activeCode;
        boolean isActCode;//激活码支付
        int type;
        boolean isCharge;
        int recordId;


        public Builder(Activity activity) {
            mActivity = activity;
        }


        public Builder wechatAppID(String appid) {
            wechatAppId = appid;
            return this;
        }

        public Builder setCharge(boolean b) {
            isCharge = b;
            return this;
        }

        public Builder payWay(PayWay way) {
            payWay = way;
            return this;
        }

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder chapterIds(String chapterIds) {
            this.chapterIds = chapterIds;
            return this;
        }

        public Builder schoolId(String schoolId){
            this.schoolId = schoolId;
            return this;
        }

        public Builder recordId(int recordId) {
            this.recordId = recordId;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder memberId(String memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder buyerMemberId(String buyerMemberId) {
            this.buyerMemberId = buyerMemberId;
            return this;
        }

        public Builder realName(String realName) {
            this.realName = realName;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder goodsPrice(String price) {
            goodsPrice = price;
            return this;
        }

        public Builder goodsName(String name) {
            goodsName = name;
            return this;
        }

        public Builder goodsIntroduction(String introduction) {
            goodsIntroduction = introduction;
            return this;
        }

        public Builder httpType(HttpType type) {
            httpType = type;
            return this;
        }


        public Builder requestBaseUrl(String url) {
            apiUrl = url;
            return this;
        }

        public Builder setActCode(boolean b) {
            isActCode = b;
            return this;
        }

        public Builder setActiveCode(String activeCode) {
            this.activeCode = activeCode;
            return this;
        }


        public PayParams build() {
            PayParams params = new PayParams();

            params.setActivity(mActivity);
            params.setWechatAppID(wechatAppId);
            params.setPayWay(payWay);
            params.setGoodsPrice(goodsPrice);
            params.setGoodsName(goodsName);
            params.setGoodsIntroduction(goodsIntroduction);
            params.setHttpType(httpType);
            params.setApiUrl(apiUrl);
            params.setCourseId(courseId);
            params.setChapterIds(chapterIds);
            params.setSchoolId(schoolId);
            params.setOrderId(orderId);
            params.setRealName(realName);
            params.setMemberId(memberId);
            params.setBuyerMemberId(buyerMemberId);
            params.setActCode(isActCode);
            params.setActiveCode(activeCode);
            params.setType(type);
            params.setCharge(isCharge);
            params.setRecordId(recordId);

            return params;
        }

    }

}
