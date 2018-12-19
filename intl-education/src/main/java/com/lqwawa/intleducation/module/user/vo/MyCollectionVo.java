package com.lqwawa.intleducation.module.user.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/12/5.
 * email:man0fchina@foxmail.com
 */

public class MyCollectionVo extends BaseVo{


    /**
     * weekCount : 8
     * progress : 4
     * id : 12
     * createTime : 2016-11-15 09:54:33
     * thumbnailUrl : http://192.168.99.181:8080/interCourse/img/12.png
     * name : 德语语法
     * teachersName : 李老师,陈老师
     * organName : 合肥大学
     */

    private int weekCount;
    private int progress;
    private long id;
    private String createTime;
    private String thumbnailUrl;
    private String name;
    private String teachersName;
    private String organName;
    private String chapterName;
    private String sectionName;


    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
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
