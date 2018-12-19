package com.oosic.apps.share;

import com.osastudio.apps.Config;

public class ShareSettings {

    public static String WAWAWEIKE_SHARE_URL;
    public static String WAWAWEIKE_DIARY_SHARE_URL;
    public static String WAWACHAT_SHARE_URL;

    static {
        if(Config.DEBUG) {
            WAWAWEIKE_SHARE_URL = "http://resop.lqwawa.com/weike/play?vId=";
            WAWAWEIKE_DIARY_SHARE_URL = "http://resop.lqwawa.com/weike/diaryplay?vId=";
            WAWACHAT_SHARE_URL = "http://filetestop.lqwawa.com/HomeworkPlugin/TaskShare.aspx?Id=";
        } else {
            WAWAWEIKE_SHARE_URL = "http://mcourse.lqwawa.com/weike/play?vId=";
            WAWAWEIKE_DIARY_SHARE_URL = "http://mcourse.lqwawa.com/weike/diaryplay?vId=";
            WAWACHAT_SHARE_URL = "http://file.lqwawa.com/HomeworkPlugin/TaskShare.aspx?Id=";
        }
    }
}
