package com.lqwawa.intleducation.factory.data.entity;

import com.lecloud.skin.ui.base.BaseVodMediaController;
import com.lqwawa.intleducation.base.vo.BaseVo;

import java.io.Serializable;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 记得写注释喲
 * @date 2018/06/12 18:37
 * @history v1.0
 * **********************************
 */
public class BaseModelDataEntity<T> extends BaseVo{

    private static final String ERROR_CODE = "-1";

    private boolean HasError;
    private String ErrorMessage;
    /**
     * 保存server返回的错误码
     */
    private String ErrorCode;

    private T Model;

    public boolean isHasError() {
        return HasError;
    }

    public void setHasError(boolean hasError) {
        HasError = hasError;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
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
        return ErrorCode != ERROR_CODE;
    }
}
