package com.osastudio.common.popmenu;

/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/7/26 15:47
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class EntryBean {
    public EntryBean() {

    }
    public EntryBean(int iconId, String value, int id ) {
        this.value = value;
        this.id = id;
        this.iconId = iconId;
    }

    public String value;
    public int id;
    public int iconId;
}
