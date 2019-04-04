package com.lqwawa.intleducation.module.box;

import java.io.Serializable;

/**
 * @autor mrmedici
 * @desc 帮辅空间定义功能菜单的实体
 */
public class FunctionEntity implements Serializable {
    private int titleId;
    private int drawableId;

    public FunctionEntity(int titleId, int drawableId) {
        this.titleId = titleId;
        this.drawableId = drawableId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
