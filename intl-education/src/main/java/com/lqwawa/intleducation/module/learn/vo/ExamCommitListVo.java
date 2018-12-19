package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2018/04/14
 * @desc:
 */
public class ExamCommitListVo extends BaseVo {
    private int code;
    private String message;
    private int total;
    List<ExamCommitVo> data;

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

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ExamCommitVo> getData() {
        return data;
    }

    public void setData(List<ExamCommitVo> data) {
        this.data = data;
    }
}
