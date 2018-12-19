package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class AreaListVo extends BaseVo{
    private List<AreaVo> areaList = null;
    private String message;
    private int code;

    public List<AreaVo> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<AreaVo> areaList) {
        this.areaList = areaList;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
