package com.lqwawa.intleducation.module.organcourse;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailType;
import com.lqwawa.intleducation.module.watchcourse.WatchResourceType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @desc 学程馆选取资源的数据定义
 * @author medici
 */
public class ShopResourceData implements Serializable{

    private int taskType;
    private int multipleChoiceCount;
    private ArrayList<Integer> filterArray;
    // 是否主动选择作业库资源
    private boolean initiativeTrigger;
    private int requestCode;

    private String schoolId;
    private String classId;

    private int enterType;

    public ShopResourceData() {
    }

    public ShopResourceData(@WatchResourceType.WatchResourceRes int taskType,
                            @IntRange(from = 1,to = Integer.MAX_VALUE) int multipleChoiceCount,
                            @NonNull ArrayList<Integer> filterArray,
                            int requestCode) {
        this.taskType = taskType;
        this.multipleChoiceCount = multipleChoiceCount;
        this.filterArray = filterArray;
        this.requestCode = requestCode;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getMultipleChoiceCount() {
        return multipleChoiceCount;
    }

    public void setMultipleChoiceCount(int multipleChoiceCount) {
        this.multipleChoiceCount = multipleChoiceCount;
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

    public int getEnterType() {
        return enterType;
    }

    public void setEnterType(@NonNull @CourseDetailType.CourseDetailRes int enterType) {
        this.enterType = enterType;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
