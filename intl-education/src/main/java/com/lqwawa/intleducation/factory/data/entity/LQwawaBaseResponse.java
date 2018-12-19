package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function LQwawa数据请求返回解析实体基类
 * @date 2018/04/12 20:13
 * @history v1.0
 * **********************************
 */
public class LQwawaBaseResponse<T> extends BaseVo{

    private static final int SUCCEED = 0;

    private int ErrorCode;

    private boolean HasError;

    private String ErrorMessage;

    private T Model;

    public int getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(int errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public boolean isHasError() {
        return HasError;
    }

    public void setHasError(boolean hasError) {
        HasError = hasError;
    }

    public T getModel() {
        return Model;
    }

    public void setModel(T model) {
        Model = model;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return !HasError && ErrorCode == SUCCEED;
    }}
