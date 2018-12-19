package com.galaxyschool.app.wawaschool.fragment.account;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 作者 shouyi
 * @version 创建时间：Oct 21, 2015 11:51:16 AM 类说明
 */
public class RetrievePWByPhoneFragment extends GetSmsVerCodeBase {
	private EditText mPhoneNumTxt, mVerCodeTxt, mPasswordTxt, mconFirmPwTxt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_retrieve_pw_by_phone, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initViews();
		initContactServer();
	}

	private void initViews() {
		mPhoneNumTxt = (EditText) getView().findViewById(R.id.phone_edittext);
		mVerCodeTxt = (EditText) getView().findViewById(
				R.id.verification_code_edittext);
		mPasswordTxt = (EditText) getView()
				.findViewById(R.id.password_edittext);
		mconFirmPwTxt = (EditText) getView().findViewById(
				R.id.confirm_password_edittext);
		mGetVerCodeBtn = (TextView) getView().findViewById(
				R.id.get_ver_code_btn);
		mGetVerCodeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phoneNum = mPhoneNumTxt.getText().toString().trim();
				checkGetVerCode(phoneNum, GetSmsVerCodeType.RETRIEVE_PW);
			}
		});
		getView().findViewById(R.id.confirm_btn).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						retrievePw();
					}
				});
	}

	private void initContactServer() {
		TextView telephoneBtn = (TextView) getView().findViewById(
				R.id.register_telephone_btn);
		telephoneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomerServiceActivity.start(getActivity());
			}
		});
		telephoneBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	private void retrievePw() {
		String phoneNum = mPhoneNumTxt.getText().toString().trim();
		String vercode = mVerCodeTxt.getText().toString().trim();
		String password = mPasswordTxt.getText().toString().trim();
		String confirmPassword = mconFirmPwTxt.getText().toString().trim();

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
		if (TextUtils.isEmpty(vercode.toString())) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.pls_input_verification_code));
			return;
		}
		if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.pls_input_password));
			return;
		}
		if (!password.equals(confirmPassword)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.password_not_same));
			return;
		}

		UIUtils.hideSoftKeyboard(getActivity());
		toRetrieve(phoneNum, vercode, password);
	}

	private void toRetrieve(String phoneNum, String code, String pw) {
		String url = ServerUrl.SMS_CHANGE_PW_URL;
		Map<String, String> mParams = new HashMap<String, String>();
		PrefsManager prefsManager = ((MyApplication) getActivity().getApplication()).getPrefsManager();
		mMemberId = prefsManager.getStringValue(prefsManager.getAppDataPrefs(),PrefsManager.PrefsItems.MEMBERID,"");
		if (!TextUtils.isEmpty(mMemberId)) {
			mParams.put("MemberId", mMemberId);
		}
		mParams.put("Mobile", phoneNum);
		mParams.put("Code", code);
		mParams.put("NewPassword", pw);
		PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
				url, mParams, new Listener<String>() {
					@Override
					public void onSuccess(String json) {
						if (getActivity() == null) {
							return;
						}
						TipMsgHelper.ShowLMsg(getActivity(),
								R.string.change_password_success);
						stopTimerCountDown();
						if (getActivity() != null) {
							getActivity().finish();
						}
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
		showLoadingDialog();
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		stopTimerCountDown();
	}
}
