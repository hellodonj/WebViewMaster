package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/3/27.
 * email:man0fchina@foxmail.com
 */

public class SectionTaskListVo extends BaseVo {
    private int taskType;
    private String taskName;
    private List<SectionResListVo> data;

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<SectionResListVo> getData() {
        return data;
    }

    public void setData(List<SectionResListVo> data) {
        this.data = data;
    }
}
