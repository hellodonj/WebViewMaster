package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/11/27.
 * email:man0fchina@foxmail.com
 */

public class MyLiveListVo extends BaseVo {
    private int total;
    private List<LiveVo> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<LiveVo> getRows() {
        return rows;
    }

    public void setRows(List<LiveVo> rows) {
        this.rows = rows;
    }
}
