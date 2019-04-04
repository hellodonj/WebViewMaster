package com.lqwawa.intleducation.common;

import com.osastudio.apps.Config;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 配置一些版本周期内不会改变的参数
 * @date 2018/04/21 14:55
 * @history v1.0
 * **********************************
 */
public class Common {

    /**
     * 一些不可变的永恒的参数
     * 通常用于一些配置
     */
    public interface Constance {
        String MOOC_MONEY_MARK = "¥";

        String WAWACHAT_PHONENUMBER = "4001077727";

        // 最近更新，热门推荐加载最多条目
        int LQMOOC_COURSE_MAX_COUNT = 30;
        // UE_TOOL 开关
        boolean UE_TOOL_FLAG = true;

        // 是否过滤测试课程 1或者不传 过滤
        // 0 不过滤
        int isAppStore = Config.UPLOAD_BUGLY_EXCEPTION ? 1 : 0;

        boolean isAssistant = false;
    }

}
