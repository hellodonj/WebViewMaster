package com.lqwawa.intleducation.module.watchcourse.list;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.ArrayList;

/**
 * @author mrmedici
 * @desc 关联多个学程参数
 */
public class CourseResourceParams extends BaseVo {

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

    public CourseResourceParams(String parentName, String courseIds,int taskType,int multipleChoiceCount) {
        this.parentName = parentName;
        this.courseIds = courseIds;
        this.taskType = taskType;
        this.multipleChoiceCount = multipleChoiceCount;
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
}
