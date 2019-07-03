package com.lqwawa.intleducation.module.organcourse.filtrate;

import android.os.Bundle;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;

import java.io.Serializable;

/**
 * @author: wangchao
 * @date: 2019/07/02
 * @desc:
 */
public class OrganCourseFiltrateParams implements Serializable {
    private LQCourseConfigEntity lqCourseConfigEntity;
    private int libraryType;
    private boolean isClassCourseEnter;
    private boolean isSelectResource;
    private boolean isAuthorized;
    private boolean isReallyAuthorized;
    private ShopResourceData shopResourceData;
    private boolean isHostEnter;
    private String roles;
    private String keyString;
    private boolean isHideTopBar;
    private Bundle bundle;

    public LQCourseConfigEntity getLqCourseConfigEntity() {
        return lqCourseConfigEntity;
    }

    public OrganCourseFiltrateParams setLqCourseConfigEntity(LQCourseConfigEntity lqCourseConfigEntity) {
        this.lqCourseConfigEntity = lqCourseConfigEntity;
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

    public OrganCourseFiltrateParams setClassCourseEnter(boolean classCourseEnter) {
        isClassCourseEnter = classCourseEnter;
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

    public boolean isHostEnter() {
        return isHostEnter;
    }

    public OrganCourseFiltrateParams setHostEnter(boolean hostEnter) {
        isHostEnter = hostEnter;
        return this;
    }

    public String getRoles() {
        return roles;
    }

    public OrganCourseFiltrateParams setRoles(String roles) {
        this.roles = roles;
        return this;
    }

    public String getKeyString() {
        return keyString;
    }

    public OrganCourseFiltrateParams setKeyString(String keyString) {
        this.keyString = keyString;
        return this;
    }

    public boolean isHideTopBar() {
        return isHideTopBar;
    }

    public OrganCourseFiltrateParams setHideTopBar(boolean hideTopBar) {
        isHideTopBar = hideTopBar;
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
