package com.galaxyschool.app.wawaschool;

import android.app.Dialog;
import android.content.Intent;

import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.DialogHelper.LoadingDialog;
import com.umeng.socialize.UMShareAPI;

/**
 * @author 作者 shouyi
 * @version 创建时间：Oct 22, 2015 4:58:46 PM 类说明
 */
public class BaseActivity extends com.osastudio.apps.BaseActivity {

	private LoadingDialog mLoadingDialog;

	public Dialog showLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			return mLoadingDialog;
		}
		mLoadingDialog = DialogHelper.getIt(this).GetLoadingDialog(0);
		return mLoadingDialog;
	}

	public Dialog showLoadingDialog(String content, boolean cancelable) {
		Dialog dialog = showLoadingDialog();
		((LoadingDialog) dialog).setContent(content);
		dialog.setCancelable(cancelable);
		return dialog;
	}

	public void dismissLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			mLoadingDialog.dismiss();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

}
