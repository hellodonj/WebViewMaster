package com.lqwawa.intleducation.common.utils;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.LinearLayout;

import com.lqwawa.intleducation.base.utils.DisplayUtil;

import java.lang.reflect.Field;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function TabLayout indicator长短的工具类工具类
 * @date 2018/07/17 12:03
 * @history v1.0
 * **********************************
 */
public class TabLayoutUtil {

    public static void setIndicatorMargin(Context context, TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout mTabLinearLayout = null;
        try {
            mTabLinearLayout = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = DisplayUtil.dip2px(UIUtil.getContext(),leftDip);
        int right = DisplayUtil.dip2px(UIUtil.getContext(),rightDip);

        for (int i = 0; i < mTabLinearLayout.getChildCount(); i++) {
            View child = mTabLinearLayout.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
