package com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay;

import android.support.annotation.NonNull;

/**
 * @author mrmedici
 * @desc 支付弹窗dialog回调定义
 */
public interface PayDialogNavigator {

    void onChoiceConfirm(@NonNull String curMemberId);
}
