package com.galaxyschool.app.wawaschool.pojo;

public class AutoMarkText {


    private int pageIndex;
    private String text;


    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "AutoMarkText{" +
                "pageIndex=" + pageIndex +
                ", text='" + text + '\'' +
                '}';
    }

}
