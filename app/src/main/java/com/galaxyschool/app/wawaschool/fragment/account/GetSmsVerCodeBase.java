package com.galaxyschool.app.wawaschool.fragment.account;

import android.os.Handler;
import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 作者 shouyi
 * @version 创建时间：Oct 30, 2015 2:23:28 PM
 * 类说明
 */
public class GetSmsVerCodeBase extends BaseFragment {
	protected String mMemberId;
	protected int mCount = 0;
	protected TextView mGetVerCodeBtn;
	
	interface GetSmsVerCodeType {
		public final int RETRIEVE_PW = 1;
		public final int REGISTER = 2;
	}
	
	protected Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			showGetVerCodeCountdown();
			mCount--;
			if (mCount < 0) {
				enableGetVerCodeBtn();
			} else {
				sendEmptyMessageDelayed(0, 1000);
			}
		};
	};
	
	protected void enableGetVerCodeBtn() {
		mCount = 0;
		mGetVerCodeBtn.setText(getString(R.string.refetch_vercode));
		mGetVerCodeBtn.setEnabled(true);
	}
	
	protected void showGetVerCodeCountdown() {
		mGetVerCodeBtn.setText(getString(R.string.refetch_vercode) + "("
				+ mCount + "s)");
	}

	protected void startTimerCountDown() {
		mGetVerCodeBtn.setEnabled(false);
		mCount = 60;
		if (getActivity() != null) {
			showGetVerCodeCountdown();
		}
		mHandler.sendEmptyMessageDelayed(0, 1000);
	}
	
	protected void stopTimerCountDown() {
		mCount = 0;
		mHandler.removeMessages(0);
	}
	
	protected void checkGetVerCode(String phoneNum, int type) {
		if (TextUtils.isEmpty(phoneNum)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.pls_input_phonenum));
			return;
		}
		if (!Utils.isCellularPhoneNumber2(phoneNum)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.wrong_phone_number));
			return;
		}
		startTimerCountDown();
		toGetVerCode(phoneNum, type);
	}

	protected void toGetVerCode(String phoneNum, int type) {
		String url = ServerUrl.SMS_GET_VER_CODE_URL;
		Map<String, String> mParams = new HashMap<String, String>();
		mParams.put("Mobile", phoneNum);
		mParams.put("Type", String.valueOf(type));
		PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
				url, mParams, new Listener<String>() {
					@Override
					public void onSuccess(String json) {
						if (getActivity() == null) {
							return;
						}
						try {
							JSONObject object = JSONObject.parseObject(json);
							mMemberId = object.getString("MemberId");
							PrefsManager prefsManager = ((MyApplication) getActivity().getApplication()).getPrefsManager();
							prefsManager.setStringValue(prefsManager.getAppDataPrefs(), PrefsManager.PrefsItems.MEMBERID,mMemberId);
						} catch (Exception e) {
							// TODO: handle exception
						}
						mGetVerCodeBtn.setEnabled(false);
					}

					@Override
					public void onError(NetroidError error) {
						super.onError(error);
						String es = error.getMessage();
						try {
							NetErrorResult result = JSON.parseObject(es,
									NetErrorResult.class);
							if (result != null) {
								if (result.isHasError()) {
									TipMsgHelper.ShowMsg(getActivity(),
											result.getErrorMessage());
								}
							}
						} catch (Exception e) {
							TipMsgHelper.ShowLMsg(getActivity(),
									getString(R.string.network_error));
						}
						enableGetVerCodeBtn();
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
						dismissLoadingDialog();
					}
				});
		request.addHeader("Accept-Encoding", "*");
		request.start(getActivity());
		// showLoadingDialog();
	}
}
