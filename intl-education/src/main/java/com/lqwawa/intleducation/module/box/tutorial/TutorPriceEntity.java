package com.lqwawa.intleducation.module.box.tutorial;

import java.io.Serializable;

public class TutorPriceEntity implements Serializable {

    private int code;
    private int price;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
