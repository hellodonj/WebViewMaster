package com.lqwawa.intleducation.module.discovery.ui.study.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 讲授课程类型筛选页面的核心参数
 */
public class NewOnlineStudyFiltrateParams implements Serializable {

    public static final int VIEW_MODE_FILTRATE = 0;
    public static final int VIEW_MODE_HIDE_TOP = 1;

    // 是否隐藏搜索入口
    private boolean hideTop;
    private String keyWord;
    private String configValue;
    private ParamResponseVo.Param param;
    private NewOnlineConfigEntity configEntity;
    private int mode;

    public NewOnlineStudyFiltrateParams(@NonNull String configValue,
                                        @NonNull NewOnlineConfigEntity configEntity) {
        this.configValue = configValue;
        this.configEntity = configEntity;
        this.mode = VIEW_MODE_FILTRATE;
    }

    public NewOnlineStudyFiltrateParams(@NonNull String configValue,
                                        @NonNull ParamResponseVo.Param param) {
        this.configValue = configValue;
        this.param = param;
        this.mode = VIEW_MODE_HIDE_TOP;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public NewOnlineConfigEntity getConfigEntity() {
        return configEntity;
    }

    public ParamResponseVo.Param getParam() {
        return param;
    }

    public boolean isHideTop() {
        return hideTop;
    }

    public void setHideTop(boolean hideTop) {
        this.hideTop = hideTop;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public int getMode() {
        return mode;
    }

    /**
     * 通过已知的参数返回一个是否隐藏搜索入口的核心参数
     * @param hideTop 是否隐藏搜索
     * @param keyWord 搜索的关键词
     * @param params 核心参数
     * @return 拷贝后的参数
     */
    public static NewOnlineStudyFiltrateParams copy(boolean hideTop, @NonNull String keyWord,@NonNull NewOnlineStudyFiltrateParams params){
        String configValue = params.getConfigValue();
        NewOnlineConfigEntity configEntity = params.getConfigEntity();
        NewOnlineStudyFiltrateParams copyParams = new NewOnlineStudyFiltrateParams(configValue,configEntity);
        copyParams.setHideTop(hideTop);
        copyParams.setKeyWord(keyWord);
        return copyParams;
    }
}
