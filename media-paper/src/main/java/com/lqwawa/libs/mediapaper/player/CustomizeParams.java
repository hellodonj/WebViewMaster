package com.lqwawa.libs.mediapaper.player;

import com.lqwawa.libs.mediapaper.PaperUtils;

/**
 * Created by rmpan on 2016/7/19.
 */
public class CustomizeParams {
    int paperType;
    boolean bTableDevice;
    boolean bEdit;
    int supportCreateMaterialType = PaperUtils.NOT_SUPPORT_TYPE;//default

    public int getPaperType() {
        return paperType;
    }

    public void setPaperType(int paperType) {
        this.paperType = paperType;
    }

    public boolean isbTableDevice() {
        return bTableDevice;
    }

    public void setbTableDevice(boolean bTableDevice) {
        this.bTableDevice = bTableDevice;
    }

    public boolean isbEdit() {
        return bEdit;
    }

    public void setbEdit(boolean bEdit) {
        this.bEdit = bEdit;
    }

    public boolean isSupportCreateMaterialType(int type) {
        return (supportCreateMaterialType & type) > PaperUtils.NOT_SUPPORT_TYPE;
    }

    public void setSupportCreateMaterialType(int supportCreateMaterialType) {
        this.supportCreateMaterialType = this.supportCreateMaterialType ^ supportCreateMaterialType;
    }
}
