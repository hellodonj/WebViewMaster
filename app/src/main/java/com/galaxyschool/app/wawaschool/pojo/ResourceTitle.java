package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by wangchao on 3/5/16.
 */
public class ResourceTitle {
    List<String> Title;
    String RepeatSign;

    public ResourceTitle() {
    }

    public ResourceTitle(List<String> title, String repeatSign) {
        Title = title;
        RepeatSign = repeatSign;
    }

    public List<String> getTitle() {
        return Title;
    }

    public void setTitle(List<String> title) {
        Title = title;
    }

    public String getRepeatSign() {
        return RepeatSign;
    }

    public void setRepeatSign(String repeatSign) {
        RepeatSign = repeatSign;
    }
}
