package com.lqwawa.intleducation.lqpay.widget;

 /**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/7/27 16:56
  * 描    述：
  * 修订历史：
  * ================================================
  */
interface PasswordView {

    //void setError(String error);

    String getPassWord();

    void clearPassword();

    void setPassword(String password);

    void setPasswordVisibility(boolean visible);

    void togglePasswordVisibility();

    void setOnPasswordChangedListener(GridPasswordView.OnPasswordChangedListener listener);

    void setPasswordType(PasswordType passwordType);
}
