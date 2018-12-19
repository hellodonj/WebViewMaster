package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;
import java.util.prefs.BackingStoreException;

/**
 * Created by XChen on 2017/11/23.
 * email:man0fchina@foxmail.com
 */

public class LiveDetailsResVo extends BaseVo{
    private String message;
    private int total;
    private int code;
    private List<SectionTaskListVo> data;

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

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<SectionTaskListVo> getData() {
        return data;
    }

    public void setData(List<SectionTaskListVo> data) {
        this.data = data;
    }
}
