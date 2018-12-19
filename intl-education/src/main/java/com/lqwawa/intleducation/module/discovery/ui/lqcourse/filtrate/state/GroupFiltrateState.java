package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate.state;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function GroupButton 筛选按钮 进行筛选过滤
 * @date 2018/05/03 12:55
 * @history v1.0
 * **********************************
 */
public class GroupFiltrateState extends AbstractFiltrateState{

    public GroupFiltrateState(LQCourseConfigEntity entity) {
        this.mConfigEntity = entity;
    }

    @Override
    public String generateTitle() {
        return mConfigEntity.getConfigValue();
    }

    public LQCourseConfigEntity getConfigEntity() {
        return mConfigEntity;
    }

    public void setConfigEntity(LQCourseConfigEntity mConfigEntity) {
        this.mConfigEntity = mConfigEntity;
    }

}
