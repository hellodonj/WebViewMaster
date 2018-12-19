package com.lqwawa.intleducation.base.vo;

import java.io.Serializable;

/**
 * Created by XChen on 2017/7/10.
 * email:man0fchina@foxmail.com
 */

public class LqDataVo<T> implements Serializable{
    T Data;
    private PagerArgs Pager;

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public PagerArgs getPager() {
        return Pager;
    }

    public void setPager(PagerArgs pager) {
        Pager = pager;
    }
}
