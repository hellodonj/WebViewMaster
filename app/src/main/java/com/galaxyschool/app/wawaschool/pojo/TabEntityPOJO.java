package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by Administrator on 2016.09.19.
 */
public class TabEntityPOJO {

    public int type;
    public String title;
    public int resId;
    public int messageCount;
    public boolean hasHeader;//头部分割线
    public boolean hasFooter;//底部分割线

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public boolean isHasFooter() {
        return hasFooter;
    }

    public void setHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TabEntityPOJO that = (TabEntityPOJO) o;

        if (type != that.type) return false;
        if (resId != that.resId) return false;
        return title != null ? title.equals(that.title) : that.title == null;
    }
}
