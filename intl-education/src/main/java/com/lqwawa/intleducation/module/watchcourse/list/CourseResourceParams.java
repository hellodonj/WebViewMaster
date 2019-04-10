package com.lqwawa.intleducation.module.watchcourse.list;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.ArrayList;

/**
 * @author mrmedici
 * @desc 关联多个学程参数
 */
public class CourseResourceParams extends BaseVo {

    private String schoolId;
    private String classId;

    private String parentName;
    private String courseIds;
    // 查看类型
    private int taskType;
    // 选择条目个数
    private int multipleChoiceCount;
    // 请求码
    private int requestCode;
    // 听读课 限制显示的资源类型集合
    private ArrayList<Integer> filterArray;
    // 是否主动选择作业库资源
    private boolean initiativeTrigger;

    public CourseResourceParams(String parentName, String courseIds,int taskType,int multipleChoiceCount) {
        this.parentName = parentName;
        this.courseIds = courseIds;
        this.taskType = taskType;
        this.multipleChoiceCount = multipleChoiceCount;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getParentName() {
        return parentName;
    }

    public String getCourseIds() {
        return courseIds;
    }

    public int getTaskType() {
        return taskType;
    }

    public int getMultipleChoiceCount() {
        return multipleChoiceCount;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public ArrayList<Integer> getFilterArray() {
        return filterArray;
    }

    public void setFilterArray(ArrayList<Integer> filterArray) {
        this.filterArray = filterArray;
    }

    public boolean isInitiativeTrigger() {
        return initiativeTrigger;
    }

    public void setInitiativeTrigger(boolean initiativeTrigger) {
        this.initiativeTrigger = initiativeTrigger;
    }
}
