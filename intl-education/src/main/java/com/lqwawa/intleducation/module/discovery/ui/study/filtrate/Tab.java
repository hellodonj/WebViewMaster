package com.lqwawa.intleducation.module.discovery.ui.study.filtrate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;

import java.util.List;

/**
 * @author mrmedici
 * @desc 自定义筛选Tab
 */
public class Tab {

    private int configType;
    private String configValue;
    private int id;
    private int labelId;
    private String level;
    private boolean isAll;
    private boolean checked;
    private List<NewOnlineConfigEntity> childList;

    private Tab() {
    }

    public int getConfigType() {
        return configType;
    }

    public void setConfigType(int configType) {
        this.configType = configType;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<NewOnlineConfigEntity> getChildList() {
        return childList;
    }

    public void setChildList(List<NewOnlineConfigEntity> childList) {
        this.childList = childList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tab tab = (Tab) o;

        if (configType != tab.configType) return false;
        return configValue != null ? configValue.equals(tab.configValue) : tab.configValue == null;
    }

    /**
     * 根据分类实体,生成一个Tab
     *
     * @param entity 分类实体
     * @return 筛选Tab
     */
    public static Tab build(NewOnlineConfigEntity entity) {
        if (entity == null) throw new RuntimeException();
        Tab tab = new Tab();
        tab.id = entity.getId();
        tab.configType = entity.getConfigType();
        tab.configValue = entity.getConfigValue();
        tab.labelId = entity.getLabelId();
        tab.level = entity.getLevel();
        tab.childList = entity.getChildList();
        return tab;
    }

    /**
     * 生成一个特殊的 全部 Tab
     *
     * @param all 文本
     * @param childList 第一级别的有childList
     * @return 特殊的Tab
     */
    public static Tab buildAll(String all, @Nullable List<NewOnlineConfigEntity> childList) {
        if (TextUtils.isEmpty(all)) throw new RuntimeException();
        Tab tab = new Tab();
        tab.setConfigValue(all);
        tab.isAll = true;
        tab.labelId = 0;
        tab.id = 0;
        tab.childList = childList;
        return tab;
    }
}
