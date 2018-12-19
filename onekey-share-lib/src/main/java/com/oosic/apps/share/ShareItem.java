package com.oosic.apps.share;

/**
 * Author: wangchao
 * Time: 2015/10/22 10:51
 */
public class ShareItem {
    int titleId;
    int iconId;
    int shareType;

    public ShareItem(int titleId, int iconId, int shareType) {
        this.titleId = titleId;
        this.iconId = iconId;
        this.shareType = shareType;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }
}
