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
public class LQwawaBaseEntity<T> extends BaseVo{

    private static final int SUCCEED = 0;

    private int ErrorCode;

    private String ErrorMessage;

    private BaseModel<T> Mode;

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

    public BaseModel<T> getMode() {
        return Mode;
    }

    public void setMode(BaseModel<T> mode) {
        Mode = mode;
    }

    public class BaseModel<T>{
        private T Data;

        public T getData() {
            return Data;
        }

        public void setData(T data) {
            Data = data;
        }
    }


    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return ErrorCode == SUCCEED;
    }}
