package com.lqwawa.intleducation.module.discovery.ui.lqcourse.basics.home;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 搜索参数
 */
public class SearchParams implements Serializable {

    private String level;
    private String configValue;

    public SearchParams(String level, String configValue) {
        this.level = level;
        this.configValue = configValue;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
