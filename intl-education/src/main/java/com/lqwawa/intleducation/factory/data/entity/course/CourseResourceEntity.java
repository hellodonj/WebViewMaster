package com.lqwawa.intleducation.factory.data.entity.course;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * @author: wangchao
 * @date: 2019/07/08
 * @desc:
 */
public class CourseResourceEntity extends BaseVo {

    /**
     * resourceUrl : http://resop.lqwawa.com/d5/course/weilaba/2019/01/29/56e233eb-c2d0-4f87-b39a-8bafd4462a53.zip
     * resType : 19
     * nickName : 半角（中文）
     * id : 33789
     * studyTaskId : 56773
     * resId : 712813
     */

    private String resourceUrl;
    private int resType;
    private String nickName;
    private int id;
    private int studyTaskId;
    private int resId;
    private int screenType;
    private boolean selected;
    private int taskType;
    private boolean fromSXCourse;

    public boolean isFromSXCourse() {
        return fromSXCourse;
    }

    public void setFromSXCourse(boolean fromSXCourse) {
        this.fromSXCourse = fromSXCourse;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudyTaskId() {
        return studyTaskId;
    }

    public void setStudyTaskId(int studyTaskId) {
        this.studyTaskId = studyTaskId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
