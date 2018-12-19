package com.galaxyschool.app.wawaschool.views.categoryview;

/**
 * Created by Administrator on 2016/7/23.
 */
public class CategoryValue {
    private String Id;
    private String Name;
    private boolean isSelect;
    private boolean tempSelect;//临时选中，因为用户点确认前都不算数的。
    //算法：进来将isSelect赋值给tempSelect，然后确认后再赋值回去

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isTempSelect() {
        return tempSelect;
    }

    public void setTempSelect(boolean tempSelect) {
        this.tempSelect = tempSelect;
    }
}
