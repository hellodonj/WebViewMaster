package com.galaxyschool.app.wawaschool.pojo;

import java.util.List;

public class AutoMarkTextBean {

    private String courseId;
    private List<AutoMarkText> textContent;


    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<AutoMarkText> getTextContent() {
        return textContent;
    }

    public void setTextContent(List<AutoMarkText> textContent) {
        this.textContent = textContent;
    }
}
