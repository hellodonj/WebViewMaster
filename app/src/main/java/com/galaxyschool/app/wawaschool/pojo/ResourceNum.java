package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by wangchao on 1/4/16.
 */
public class ResourceNum {
    String MType;
    String MNumber;

    public ResourceNum() {

    }

    public ResourceNum(String MType, String MNumber) {
        this.MType = MType;
        this.MNumber = MNumber;
    }

    public String getMType() {
        return MType;
    }

    public void setMType(String MType) {
        this.MType = MType;
    }

    public String getMNumber() {
        return MNumber;
    }

    public void setMNumber(String MNumber) {
        this.MNumber = MNumber;
    }
}
