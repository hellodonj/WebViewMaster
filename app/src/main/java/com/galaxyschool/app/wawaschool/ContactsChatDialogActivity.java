package com.galaxyschool.app.wawaschool;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.fragment.account.AccountListener;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;

public class ContactsChatDialogActivity extends BaseActivity {

    public static final String EXTRA_DIALOG_TYPE = "dialogType";

    public static final int DIALOG_TYPE_ACCOUNT_CONFLICT = 1;
    public static final int DIALOG_TYPE_ACCOUNT_REMOVED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            if (args.getInt(EXTRA_DIALOG_TYPE) == DIALOG_TYPE_ACCOUNT_CONFLICT) {
                showConflictDialog();
            } else if (args.getInt(EXTRA_DIALOG_TYPE) == DIALOG_TYPE_ACCOUNT_REMOVED) {
                showAccountRemovedDialog();
            }
        }
    }

    /**
     * 帐号被移除的dialog
     */
    void showAccountRemovedDialog() {
        DemoApplication.getInstance().logout(null);
        if (isFinishing()) {
            return;
        }
        ContactsMessageDialog dialog = new ContactsMessageDialog(this,
                R.style.Theme_ContactsDialog,
                null, //getString(R.string.Remove_the_notification),
                getString(R.string.em_user_remove),
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                finish();
                getActivityStack().finishAll();
                enterLogin();
            }
        }, null, null);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 显示帐号在别处登录dialog
     */
    void showConflictDialog() {
        DemoApplication.getInstance().logout(null);
        if (isFinishing()) {
            return;
        }
        ContactsMessageDialog dialog = new ContactsMessageDialog(this,
                R.style.Theme_ContactsDialog,
                null, //getString(R.string.Logoff_notification),
                getString(R.string.connect_conflict),
                getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                finish();
//                getActivityStack().finishAll();
                getActivityStack().finishUtilRoot();
                //清空mooc的栈Activity
                DemoApplication.getInstance().finishActivitysWithoutHome();

                AccountListener listener = DemoApplication.getInstance().getAccountListener();
                if (listener != null) {
                    listener.onAccountLogout(DemoApplication.getInstance().getMemberId());
                }
                enterLogin();
            }
        }, null, null);
        dialog.setCancelable(false);
        dialog.show();
    }

    void enterLogin() {
        Intent intent = new Intent(this, AccountActivity.class);
        Bundle args = new Bundle();
//        args.putBoolean(AccountActivity.EXTRA_EXIT_APPLICATION_ON_CANCEL, true);
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        intent.putExtras(args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {

        }
    }

}