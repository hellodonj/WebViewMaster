package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/7/10.
 * email:man0fchina@foxmail.com
 */

public class LqTaskCommitListVo extends BaseVo {
    // @date   :2018/4/13 0013 下午 4:18
    // @func   :v5.5新添加的属性
    String AirClassId;
    LqTaskInfoVo TaskInfo;
    // 班级学程中返回
    int StudentCount;
    List<LqTaskCommitVo> ListCommitTaskOnline;

    public String getAirClassId() {
        return AirClassId;
    }

    public void setAirClassId(String airClassId) {
        AirClassId = airClassId;
    }

    public LqTaskInfoVo getTaskInfo() {
        return TaskInfo;
    }

    public void setTaskInfo(LqTaskInfoVo taskInfo) {
        TaskInfo = taskInfo;
    }

    public int getStudentCount() {
        return StudentCount;
    }

    public void setStudentCount(int studentCount) {
        StudentCount = studentCount;
    }

    public List<LqTaskCommitVo> getListCommitTaskOnline() {
        return ListCommitTaskOnline;
    }

    public void setListCommitTaskOnline(List<LqTaskCommitVo> listCommitTaskOnline) {
        ListCommitTaskOnline = listCommitTaskOnline;
    }
}
