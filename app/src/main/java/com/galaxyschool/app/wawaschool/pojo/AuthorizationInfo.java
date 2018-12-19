package com.galaxyschool.app.wawaschool.pojo;

/**
 * Created by Administrator on 2016/9/28.
 */

public class AuthorizationInfo {

    /**
     * IsMemberAuthorized : true
     * IsCanAuthorize : true
     */

    private boolean IsMemberAuthorized;
    private boolean IsCanAuthorize;

    public boolean isIsMemberAuthorized() {
        return IsMemberAuthorized;
    }

    public void setIsMemberAuthorized(boolean IsMemberAuthorized) {
        this.IsMemberAuthorized = IsMemberAuthorized;
    }

    public boolean isIsCanAuthorize() {
        return IsCanAuthorize;
    }

    public void setIsCanAuthorize(boolean IsCanAuthorize) {
        this.IsCanAuthorize = IsCanAuthorize;
    }
}
