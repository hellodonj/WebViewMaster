package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/3/31.
 * email:man0fchina@foxmail.com
 */

public class CourseExamDataVo extends BaseVo {
    private List<ExamListVo> data;
    private String chapterName;
    private String sectionName;
    private int code;

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public List<ExamListVo> getData() {
        return data;
    }

    public void setData(List<ExamListVo> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
