package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */

public class PPTAndPDFCourseInfoCode {
    private int code;
    private List<PPTAndPDFCourseInfo> data;
    public PPTAndPDFCourseInfoCode(){

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PPTAndPDFCourseInfo> getData() {
        return data;
    }

    public void setData(List<PPTAndPDFCourseInfo> data) {
        this.data = data;
    }
}
