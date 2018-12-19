package com.galaxyschool.app.wawaschool.pojo.weike;

import java.util.List;

/**
 * Created by wangchao on 1/21/16.
 */
public class SplitCourseInfoListResult {
    private int code;
    private String message;
    List<SplitCourseInfo> data;

    public SplitCourseInfoListResult() {
    }

    public SplitCourseInfoListResult(int code, String message, List<SplitCourseInfo> data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SplitCourseInfo> getData() {
        return data;
    }

    public void setData(List<SplitCourseInfo> data) {
        this.data = data;
    }
}
