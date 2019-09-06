package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;

public class ViewBaseBean implements Serializable {
    private int drawableId;
    private String title;

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
