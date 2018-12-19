package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/1/7.
 * email:man0fchina@foxmail.com
 */

public class MyCredentialCourseListVo extends BaseVo {
    MyCredentialCourseVo course;
    private List<MyCourseChapterVo> chapters;//
    /**
     * isAdd : false
     * isBuy : false
     */

    private boolean isAdd;
    private boolean isBuy;
    private String chapterName;
    private String sectionName;


    public List<MyCourseChapterVo> getChapters() {
        return chapters;
    }

    public void setChapters(List<MyCourseChapterVo> chapters) {
        this.chapters = chapters;
    }

    public MyCredentialCourseVo getCourse() {
        return course;
    }

    public void setCourse(MyCredentialCourseVo course) {
        this.course = course;
    }

    public boolean isIsAdd() {
        return isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public boolean isIsBuy() {
        return isBuy;
    }

    public void setIsBuy(boolean isBuy) {
        this.isBuy = isBuy;
    }

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
}
