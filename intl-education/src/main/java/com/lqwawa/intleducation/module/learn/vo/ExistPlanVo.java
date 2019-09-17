package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

public class ExistPlanVo extends BaseVo {

    //{ "code": 0,"exist": true,"containStandard":true}
    private int code;
    private boolean exist;
    private boolean containStandard;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isContainStandard() {
        return containStandard;
    }

    public void setContainStandard(boolean containStandard) {
        this.containStandard = containStandard;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
