package com.lqwawa.intleducation.module.organcourse.filtrate;


import android.os.Bundle;

import com.lqwawa.intleducation.module.organcourse.ShopResourceData;

import java.io.Serializable;

/**
 * @author: wangchao
 * @date: 2019/05/15
 * @desc:
 */
public class OrganCourseFiltrateParams implements Serializable, Cloneable {
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

    public OrganCourseFiltrateParams(String organId, int libraryType,
                                     int sort, boolean isClassCourseEnter) {
        this.organId = organId;
        this.libraryType = libraryType;
        this.sort = sort;
        this.isClassCourseEnter = isClassCourseEnter;
    }

    public String getOrganId() {
        return organId;
    }

    public OrganCourseFiltrateParams setOrganId(String organId) {
        this.organId = organId;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public OrganCourseFiltrateParams setLevel(String level) {
        this.level = level;
        return this;
    }

    public int getSort() {
        return sort;
    }

    public OrganCourseFiltrateParams setSort(int sort) {
        this.sort = sort;
        return this;
    }

    public String getKeyString() {
        return keyString;
    }

    public OrganCourseFiltrateParams setKeyString(String keyString) {
        this.keyString = keyString;
        return this;
    }

    public int getParamOneId() {
        return paramOneId;
    }

    public OrganCourseFiltrateParams setParamOneId(int paramOneId) {
        this.paramOneId = paramOneId;
        return this;
    }

    public int getParamTwoId() {
        return paramTwoId;
    }

    public OrganCourseFiltrateParams setParamTwoId(int paramTwoId) {
        this.paramTwoId = paramTwoId;
        return this;
    }

    public int getParamThreeId() {
        return paramThreeId;
    }

    public OrganCourseFiltrateParams setParamThreeId(int paramThreeId) {
        this.paramThreeId = paramThreeId;
        return this;
    }

    public int getLibraryType() {
        return libraryType;
    }

    public OrganCourseFiltrateParams setLibraryType(int libraryType) {
        this.libraryType = libraryType;
        return this;
    }

    public boolean isClassCourseEnter() {
        return isClassCourseEnter;
    }

    public OrganCourseFiltrateParams setIsClassCourseEnter(boolean isClassCourseEnter) {
        this.isClassCourseEnter = isClassCourseEnter;
        return this;
    }

    public boolean isSelectResource() {
        return isSelectResource;
    }

    public OrganCourseFiltrateParams setSelectResource(boolean selectResource) {
        isSelectResource = selectResource;
        return this;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public OrganCourseFiltrateParams setAuthorized(boolean authorized) {
        isAuthorized = authorized;
        return this;
    }

    public boolean isReallyAuthorized() {
        return isReallyAuthorized;
    }

    public OrganCourseFiltrateParams setReallyAuthorized(boolean reallyAuthorized) {
        isReallyAuthorized = reallyAuthorized;
        return this;
    }

    public ShopResourceData getShopResourceData() {
        return shopResourceData;
    }

    public OrganCourseFiltrateParams setShopResourceData(ShopResourceData shopResourceData) {
        this.shopResourceData = shopResourceData;
        return this;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public OrganCourseFiltrateParams setBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }
}
