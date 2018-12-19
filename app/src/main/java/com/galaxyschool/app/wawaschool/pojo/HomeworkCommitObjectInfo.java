package com.galaxyschool.app.wawaschool.pojo;

import com.lqwawa.lqbaselib.net.library.Model;

import java.io.Serializable;
import java.util.List;

/**
 * ��ҵ����Model���
 * Created by Administrator on 2016.06.17.
 */
public class HomeworkCommitObjectInfo{

    private StudyTask TaskInfo;
    private List<CommitTask> ListCommitTask;
    private int AirClassId;//空中课堂直播的id

    public int getAirClassId() {
        return AirClassId;
    }

    public void setAirClassId(int airClassId) {
        this.AirClassId = airClassId;
    }

    public void setTaskInfo(StudyTask taskInfo) {
        TaskInfo = taskInfo;
    }

    public StudyTask getTaskInfo() {
        return TaskInfo;
    }

    public void setListCommitTask(List<CommitTask> listCommitTask) {
        ListCommitTask = listCommitTask;
    }

    public List<CommitTask> getListCommitTask() {
        return ListCommitTask;
    }
}
