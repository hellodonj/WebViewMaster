package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * 描述: 创建班级付费订单的实体
 * 作者|时间: djj on 2019/7/12 0012 上午 11:54
 */
public class ClassOrderEntity extends BaseVo {
    /**
     * {"code":0，"message":"","id":36}
     */
    private int code;
    private int id;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
