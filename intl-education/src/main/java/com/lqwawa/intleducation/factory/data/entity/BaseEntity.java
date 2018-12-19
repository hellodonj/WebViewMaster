package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.io.Serializable;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 用来接收服务器返回的基类
 * @date 2018/04/09 13:55
 * @history v1.0
 * **********************************
 */
public class BaseEntity extends BaseVo {

    private static final int SUCCEED = 0;

    protected int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }
}
