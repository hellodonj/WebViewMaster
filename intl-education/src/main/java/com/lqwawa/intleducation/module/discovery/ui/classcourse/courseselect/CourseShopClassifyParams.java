package com.lqwawa.intleducation.module.discovery.ui.classcourse.courseselect;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.organcourse.ShopResourceData;

/**
 * @author mrmedici
 * @desc
 */
public class CourseShopClassifyParams extends BaseVo{

    private String organId;

    private boolean selectResource;

    private ShopResourceData data;
    // 是否主动选择作业库资源
    private boolean initiativeTrigger;

    public CourseShopClassifyParams(String organId) {
        this.organId = organId;
    }

    public CourseShopClassifyParams(String organId, boolean selectResource, ShopResourceData data) {
        this.organId = organId;
        this.selectResource = selectResource;
        this.data = data;
    }

    public String getOrganId() {
        return organId;
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
}
