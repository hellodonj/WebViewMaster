package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 日期标记的实体
 */
public class DateFlagEntity implements Serializable {

    private String Date;
    private boolean bolHaveData;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public boolean isBolHaveData() {
        return bolHaveData;
    }

    public void setBolHaveData(boolean bolHaveData) {
        this.bolHaveData = bolHaveData;
    }
}
