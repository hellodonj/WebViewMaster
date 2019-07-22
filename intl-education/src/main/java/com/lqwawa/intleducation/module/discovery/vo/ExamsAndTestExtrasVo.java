package com.lqwawa.intleducation.module.discovery.vo;

public class ExamsAndTestExtrasVo {

    private boolean needFlagRead;
    private int lessonStatus;
    private boolean isVideoCourse;
    private boolean mClassTeacher;
    private boolean isCourseSelect;
    private boolean mChoiceMode;

    public ExamsAndTestExtrasVo(boolean needFlagRead, int lessonStatus, boolean isVideoCourse, boolean mClassTeacher, boolean isCourseSelect, boolean mChoiceMode) {
        this.needFlagRead = needFlagRead;
        this.lessonStatus = lessonStatus;
        this.isVideoCourse = isVideoCourse;
        this.mClassTeacher = mClassTeacher;
        this.isCourseSelect = isCourseSelect;
        this.mChoiceMode = mChoiceMode;
    }

    public boolean isNeedFlagRead() {
        return needFlagRead;
    }

    public void setNeedFlagRead(boolean needFlagRead) {
        this.needFlagRead = needFlagRead;
    }

    public int getLessonStatus() {
        return lessonStatus;
    }

    public void setLessonStatus(int lessonStatus) {
        this.lessonStatus = lessonStatus;
    }

    public boolean isVideoCourse() {
        return isVideoCourse;
    }

    public void setVideoCourse(boolean videoCourse) {
        isVideoCourse = videoCourse;
    }

    public boolean ismClassTeacher() {
        return mClassTeacher;
    }

    public void setmClassTeacher(boolean mClassTeacher) {
        this.mClassTeacher = mClassTeacher;
    }

    public boolean isCourseSelect() {
        return isCourseSelect;
    }

    public void setCourseSelect(boolean courseSelect) {
        isCourseSelect = courseSelect;
    }

    public boolean ismChoiceMode() {
        return mChoiceMode;
    }

    public void setmChoiceMode(boolean mChoiceMode) {
        this.mChoiceMode = mChoiceMode;
    }
}
