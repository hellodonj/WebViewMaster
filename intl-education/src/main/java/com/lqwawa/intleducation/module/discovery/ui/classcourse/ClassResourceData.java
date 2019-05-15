package com.lqwawa.intleducation.module.discovery.ui.classcourse;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.module.watchcourse.WatchResourceType;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @desc 班级学程选取资源的数据定义
 * @author medici
 */
public class ClassResourceData implements Serializable {

    private int taskType;
    private int multipleChoiceCount;
    private ArrayList<Integer> filterArray;
    // 是否主动选择作业库资源
    private boolean initiativeTrigger;
    private int requestCode;
    // 是否直接进入班级学程
    private boolean isDirectToClassCourse;

    public ClassResourceData(){

    }

    public ClassResourceData(@WatchResourceType.WatchResourceRes int taskType,
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

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public boolean isDirectToClassCourse() {
        return isDirectToClassCourse;
    }

    public ClassResourceData setIsDirectToClassCourse(boolean isDirectToClassCourse) {
        this.isDirectToClassCourse = isDirectToClassCourse;
        return this;
    }
}
