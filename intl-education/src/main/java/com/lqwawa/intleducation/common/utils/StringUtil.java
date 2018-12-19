package com.lqwawa.intleducation.common.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 两栖蛙蛙Mooc模块字符串工具类
 * @date 2018/06/06 12:03
 * @history v1.0
 * **********************************
 */
public final class StringUtil {

    /**
     * 将Text内容设置到文本控件中
     * @param tvContent 文本控件
     * @param charSequence 字符串资源
     */
    public static void fillSafeTextView(@NonNull TextView tvContent, @Nullable CharSequence charSequence){
        tvContent.setText(EmptyUtil.isEmpty(charSequence) ? "" : charSequence);
    }

}
