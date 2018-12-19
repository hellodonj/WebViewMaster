package com.lqwawa.intleducation.module.discovery.ui.classcourse;

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
    private int requestCode;

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

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }
}
