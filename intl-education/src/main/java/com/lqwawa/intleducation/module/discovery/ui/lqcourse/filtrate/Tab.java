package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate;

import android.text.TextUtils;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 自定义筛选Tab
 * @date 2018/05/03 19:30
 * @history v1.0
 * **********************************
 */
public class Tab {

    private int configType;
    private String configValue;
    private int id;
    private int labelId;
    private String level;
    private boolean isAll;
    private boolean selected;
    private List<LQCourseConfigEntity> childList;

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public List<LQCourseConfigEntity> getChildList() {
        return childList;
    }

    public void setChildList(List<LQCourseConfigEntity> childList) {
        this.childList = childList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tab tab = (Tab) o;

        if (configType != tab.configType) return false;
        if (labelId != tab.labelId) return false;
        return configValue != null ? configValue.equals(tab.configValue) : tab.configValue == null;
    }

    /**
     * 根据分类实体,生成一个Tab
     *
     * @param entity 分类实体
     * @return 筛选Tab
     */
    public static Tab build(LQCourseConfigEntity entity) {
        if (entity == null) throw new RuntimeException();
        Tab tab = new Tab();
        tab.id = entity.getId();
        tab.configType = entity.getConfigType();
        tab.configValue = entity.getConfigValue();
        tab.labelId = entity.getLabelId();
        tab.level = entity.getLevel();
        return tab;
    }

    /**
     * 生成一个特殊的 全部 Tab
     *
     * @param all 文本
     * @return 特殊的Tab
     */
    public static Tab buildAll(String all) {
        if (TextUtils.isEmpty(all)) throw new RuntimeException();
        Tab tab = new Tab();
        tab.setConfigValue(all);
        tab.isAll = true;
        tab.labelId = 0;
        return tab;
    }
}
