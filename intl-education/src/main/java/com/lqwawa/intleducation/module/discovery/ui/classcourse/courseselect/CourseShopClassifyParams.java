package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect;

import android.support.annotation.Nullable;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;

/**
 * @author mrmedici
 * @desc
 */
public class CourseShopClassifyParams extends BaseVo{

    private String organId;

    private String classId;

    private boolean selectResource;

    private ShopResourceData data;
    // 是否主动选择作业库资源
    private boolean initiativeTrigger;

    private int libraryType;

    //是否添加班级学程
    private boolean isAddClassCourse;

    public CourseShopClassifyParams(String organId, @Nullable String classId) {
        this.organId = organId;
        this.classId = classId;
    }

    public CourseShopClassifyParams(String organId,boolean selectResource, ShopResourceData data) {
        this(organId,null,selectResource,data);
    }

    public CourseShopClassifyParams(String organId, @Nullable String classId,boolean selectResource, ShopResourceData data) {
        this(organId,classId);
        this.selectResource = selectResource;
        this.data = data;
    }

    public String getOrganId() {
        return organId;
    }

    public String getClassId() {
        return classId;
    }

    public boolean isSelectResource() {
        return selectResource;
    }

    public ShopResourceData getData() {
        return data;
    }

    public boolean isInitiativeTrigger() {
        return initiativeTrigger;
    }

    public void setInitiativeTrigger(boolean initiativeTrigger) {
        this.initiativeTrigger = initiativeTrigger;
    }

    public int getLibraryType() {
        return libraryType;
    }

    public CourseShopClassifyParams setLibraryType(int libraryType) {
        this.libraryType = libraryType;
        return this;
    }

    public boolean isAddClassCourse() {
        return isAddClassCourse;
    }

    public CourseShopClassifyParams setIsAddCourseClass(boolean isAddCourseClass) {
        this.isAddClassCourse = isAddCourseClass;
        return this;
    }
}
