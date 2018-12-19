package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state;

import android.support.annotation.NonNull;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 没有筛选选择,直接显示课程的状态
 * @date 2018/05/03 12:54
 * @history v1.0
 * **********************************
 */
public class DefaultFiltrateState extends AbstractFiltrateState{

    public DefaultFiltrateState(@NonNull String levelName) {
        this.mLevelName = levelName;
    }

    @Override
    public String generateTitle() {
        return mLevelName;
    }

    public String getLevelName() {
        return mLevelName;
    }

    public void setLevelName(String mLevelName) {
        this.mLevelName = mLevelName;
    }
}
