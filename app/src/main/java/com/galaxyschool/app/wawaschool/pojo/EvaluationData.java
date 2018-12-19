package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/13 0013 13:57
 * Describe:语音评测的信息
 * ======================================================
 */
public class EvaluationData implements Serializable {
    private List<TextContent> textContent;
    private String courseId;

    public List<TextContent> getTextContent() {
        return textContent;
    }

    public void setTextContent(List<TextContent> textContent) {
        this.textContent = textContent;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public class TextContent {
        private String text;
        private int pageIndex;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }
    }
}
