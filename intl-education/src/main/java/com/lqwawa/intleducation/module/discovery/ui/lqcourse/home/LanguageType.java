package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 中英文的定义
 * @date 2018/04/27 16:09
 * @history v1.0
 * **********************************
 */
public interface LanguageType {

    int LANGUAGE_CHINESE = 0;
    int LANGUAGE_OTHER = 1;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LANGUAGE_CHINESE,LANGUAGE_OTHER})
    public @interface LanguageRes{

    }

}
