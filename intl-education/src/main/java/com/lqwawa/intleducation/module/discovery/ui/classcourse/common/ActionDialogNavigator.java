package com.lqwawa.intleducation.module.discovery.ui.classcourse.common;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * @author mrmedici
 * @desc 公共dialog回调定义
 */
public interface ActionDialogNavigator {
    void onAction(@NonNull View button,ActionDialogFragment.Tag tag);
}
