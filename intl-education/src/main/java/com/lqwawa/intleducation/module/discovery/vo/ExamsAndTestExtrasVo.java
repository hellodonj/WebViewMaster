package com.lqwawa.intleducation.module.discovery.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.discovery.ui.lesson.detail.LessonSourceParams;

public class ExamsAndTestExtrasVo extends BaseVo {

    private boolean needFlagRead;
    private int lessonStatus;
    private boolean isVideoCourse;
    private boolean mClassTeacher;
    private boolean isCourseSelect;
    private boolean mChoiceMode;
    private LessonSourceParams lessonSourceParams;
    private String schoolId;
    private int libraryType;
    private int multipleChoiceCount;

    public ExamsAndTestExtrasVo() {
    }

    public ExamsAndTestExtrasVo(String schoolId, LessonSourceParams lessonSourceParams, boolean needFlagRead, int lessonStatus,
                                boolean isVideoCourse, boolean mClassTeacher, boolean isCourseSelect, boolean mChoiceMode,
                                int libraryType,int multipleChoiceCount) {
        this.schoolId =schoolId;
        this.needFlagRead = needFlagRead;
        this.lessonStatus = lessonStatus;
        this.isVideoCourse = isVideoCourse;
        this.mClassTeacher = mClassTeacher;
        this.isCourseSelect = isCourseSelect;
        this.mChoiceMode = mChoiceMode;
        this.lessonSourceParams = lessonSourceParams;
        this.libraryType =libraryType;
        this.multipleChoiceCount = multipleChoiceCount;
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

    public LessonSourceParams getLessonSourceParams() {
        return lessonSourceParams;
    }

    public void setLessonSourceParams(LessonSourceParams lessonSourceParams) {
        this.lessonSourceParams = lessonSourceParams;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public int getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(int libraryType) {
        this.libraryType = libraryType;
    }

    public int getMultipleChoiceCount() {
        return multipleChoiceCount;
    }

    public void setMultipleChoiceCount(int multipleChoiceCount) {
        this.multipleChoiceCount = multipleChoiceCount;
    }
}
