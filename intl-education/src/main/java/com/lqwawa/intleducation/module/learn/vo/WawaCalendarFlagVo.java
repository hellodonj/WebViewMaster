package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2018/1/15.
 * email:man0fchina@foxmail.com
 */

public class WawaCalendarFlagVo extends BaseVo {
    String Date;
    boolean bolHaveLive;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public boolean isBolHaveLive() {
        return bolHaveLive;
    }

    public void setBolHaveLive(boolean bolHaveLive) {
        this.bolHaveLive = bolHaveLive;
    }
}
