package com.lqwawa.intleducation.factory.data.entity.tutorial;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * 描述: 帮辅订单的实体
 * 作者|时间: djj on 2019/7/12 0012 上午 11:54
 */
public class TutorOrderEntity extends BaseVo {
    /**
     * "code": 0,
     * "orderId": 5386,
     * "message": ""
     */
    private int code;
    private int orderId;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
