package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 日期标记的实体
 */
public class DateFlagEntity implements Serializable {

    private String Date;
    private boolean bolHaveData;

    public DateFlagEntity(String date, boolean bolHaveData) {
        Date = date;
        this.bolHaveData = bolHaveData;
    }
}
