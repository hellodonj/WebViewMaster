package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/2/2 10:46
 * E-Mail Address:863378689@qq.com
 * Describe:
 * ======================================================
 */

public class HeadMasterClassMailInfo implements Serializable {
    private List<HeadMasterInfoList> HeadMasterClassMailList;
    private boolean IsHeadMaster;
    private boolean HasInspectAuth;

    public boolean isHasInspectAuth() {
        return HasInspectAuth;
    }

    public void setHasInspectAuth(boolean hasInspectAuth) {
        HasInspectAuth = hasInspectAuth;
    }

    public List<HeadMasterInfoList> getHeadMasterClassMailList() {
        return HeadMasterClassMailList;
    }

    public void setHeadMasterClassMailList(List<HeadMasterInfoList> headMasterClassMailList) {
        HeadMasterClassMailList = headMasterClassMailList;
    }

    public boolean isHeadMaster() {
        return IsHeadMaster;
    }

    public void setIsHeadMaster(boolean headMaster) {
        IsHeadMaster = headMaster;
    }
}
