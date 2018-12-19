package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/3/27.
 * email:man0fchina@foxmail.com
 */

public class SectionDetailsVo extends BaseVo {
    private String sectionName;
    private String sectionTitle;
    private String status;
    private boolean isOpen;
    private int id;
    private String introduction;
    private List<SectionTaskListVo> taskList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<SectionTaskListVo> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<SectionTaskListVo> taskList) {
        this.taskList = taskList;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean open) {
        isOpen = open;
    }
}
