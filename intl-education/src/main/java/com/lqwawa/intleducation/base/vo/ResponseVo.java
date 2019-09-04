package com.lqwawa.intleducation.base.vo;

import java.io.Serializable;

/**
 * Created by taoshiyuan on 15/10/8.
 */
public class ResponseVo<T> implements Serializable {

    public static final int SUCCEED = 0;

    public static final int ERROR_UNKNOWN = -1;

    private int total;
    private int code;
    private T data;
    private String message;
    private boolean exist;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
