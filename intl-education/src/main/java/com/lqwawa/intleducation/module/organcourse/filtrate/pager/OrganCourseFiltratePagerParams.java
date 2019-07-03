package com.lqwawa.intleducation.module.organcourse.filtrate.pager;


import android.os.Bundle;

import com.lqwawa.intleducation.module.organcourse.ShopResourceData;

import java.io.Serializable;

/**
 * @author: wangchao
 * @date: 2019/05/15
 * @desc:
 */
public class OrganCourseFiltratePagerParams implements Serializable, Cloneable {
    private String organId;
    private String level;
    private int sort;
    private String keyString;
    private int paramOneId;
    private int paramTwoId;
    private int paramThreeId;
    private int libraryType;
    private boolean isClassCourseEnter;
    private boolean isSelectResource;
    private boolean isAuthorized;
    private boolean isReallyAuthorized;
    private ShopResourceData shopResourceData;
    private Bundle bundle;

    public OrganCourseFiltratePagerParams(String organId, int libraryType,
                                          int sort, boolean isClassCourseEnter) {
        this.organId = organId;
        this.libraryType = libraryType;
        this.sort = sort;
        this.isClassCourseEnter = isClassCourseEnter;
    }

    public String getOrganId() {
        return organId;
    }

    public OrganCourseFiltratePagerParams setOrganId(String organId) {
        this.organId = organId;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public OrganCourseFiltratePagerParams setLevel(String level) {
        this.level = level;
        return this;
    }

    public int getSort() {
        return sort;
    }

    public OrganCourseFiltratePagerParams setSort(int sort) {
        this.sort = sort;
        return this;
    }

    public String getKeyString() {
        return keyString;
    }

    public OrganCourseFiltratePagerParams setKeyString(String keyString) {
        this.keyString = keyString;
        return this;
    }

    public int getParamOneId() {
        return paramOneId;
    }

    public OrganCourseFiltratePagerParams setParamOneId(int paramOneId) {
        this.paramOneId = paramOneId;
        return this;
    }

    public int getParamTwoId() {
        return paramTwoId;
    }

    public OrganCourseFiltratePagerParams setParamTwoId(int paramTwoId) {
        this.paramTwoId = paramTwoId;
        return this;
    }

    public int getParamThreeId() {
        return paramThreeId;
    }

    public OrganCourseFiltratePagerParams setParamThreeId(int paramThreeId) {
        this.paramThreeId = paramThreeId;
        return this;
    }

    public int getLibraryType() {
        return libraryType;
    }

    public OrganCourseFiltratePagerParams setLibraryType(int libraryType) {
        this.libraryType = libraryType;
        return this;
    }

    public boolean isClassCourseEnter() {
        return isClassCourseEnter;
    }

    public OrganCourseFiltratePagerParams setIsClassCourseEnter(boolean isClassCourseEnter) {
        this.isClassCourseEnter = isClassCourseEnter;
        return this;
    }

    public boolean isSelectResource() {
        return isSelectResource;
    }

    public OrganCourseFiltratePagerParams setSelectResource(boolean selectResource) {
        isSelectResource = selectResource;
        return this;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public OrganCourseFiltratePagerParams setAuthorized(boolean authorized) {
        isAuthorized = authorized;
        return this;
    }

    public boolean isReallyAuthorized() {
        return isReallyAuthorized;
    }

    public OrganCourseFiltratePagerParams setReallyAuthorized(boolean reallyAuthorized) {
        isReallyAuthorized = reallyAuthorized;
        return this;
    }

    public ShopResourceData getShopResourceData() {
        return shopResourceData;
    }

    public OrganCourseFiltratePagerParams setShopResourceData(ShopResourceData shopResourceData) {
        this.shopResourceData = shopResourceData;
        return this;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public OrganCourseFiltratePagerParams setBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }
}
