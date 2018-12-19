package com.lqwawa.intleducation.common.utils;

import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;

/**
 * @author medici
 * @desc 语言工具类
 */
public final class LanguageUtil {

    private LanguageUtil() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * @author medici
     * @return <p>中英文{@link LanguageType}</p>
     */
    public static int isZh(){
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        return languageRes;
    }
}
