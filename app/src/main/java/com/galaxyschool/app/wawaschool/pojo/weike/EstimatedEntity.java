package com.galaxyschool.app.wawaschool.pojo.weike;

import com.lqwawa.intleducation.base.vo.BaseVo;

public class EstimatedEntity extends BaseVo {

    private String message;
    private boolean estimated;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isEstimated() {
        return estimated;
    }

    public void setEstimated(boolean estimated) {
        this.estimated = estimated;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
