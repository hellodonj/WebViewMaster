package com.galaxyschool.app.wawaschool.fragment.account;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 作者 shouyi
 * @version 创建时间：Oct 21, 2015 11:51:16 AM 类说明
 */
public class RetrievePWByMailFragment extends BaseFragment implements
		OnClickListener, TextView.OnEditorActionListener {
	private EditText emailTxt;
	private TextView sendBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_retrieve_pw_by_mail, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		initViews();
		initContactServer();
	}

	private void initViews() {
		emailTxt = (EditText) getView().findViewById(
				R.id.retrieve_email_edittext);
		sendBtn = (TextView) getView().findViewById(R.id.confirm_btn);
		sendBtn.setOnClickListener(this);
		emailTxt.setOnEditorActionListener(this);
		emailTxt.requestFocus();
		UIUtils.showSoftKeyboard(getActivity());
		getView().findViewById(R.id.goto_wawaxiu).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.goto_wawaxiu) {
			ActivityUtils.gotoHome(getActivity());
		} else if (v.getId() == R.id.confirm_btn) {
			getBackPassword();
		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		switch (actionId) {
		case EditorInfo.IME_ACTION_DONE:
			getBackPassword();
			break;
		}
		return true;
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

	private void getBackPassword() {
		String email = emailTxt.getText().toString().trim();
		if (TextUtils.isEmpty(email)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.pls_input_email));
			return;
		}
		if (!Utils.isEmailAddress(email)) {
			TipMsgHelper.ShowMsg(getActivity(),
					getString(R.string.wrong_email_format));
			return;
		}

		UIUtils.hideSoftKeyboard(getActivity());
		getBackPassword(email);
	}

	private void getBackPassword(String email) {
		String url = ServerUrl.GETBACK_PASSWORD_URL;
		if (!TextUtils.isEmpty(url)) {
			Map<String, String> mParams = new HashMap<String, String>();
			mParams.put("EMail", email);
			PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
					url, mParams, new Listener<String>() {
						@Override
						public void onSuccess(String json) {
							if (getActivity() == null) {
								return;
							}
							try {
								Log.i("", "Login:onSuccess " + json);
								UserInfo userInfo = JSON.parseObject(json,
										UserInfo.class);
								if (userInfo != null) {
									TipMsgHelper
											.ShowMsg(
													getActivity(),
													getString(R.string.email_send_success));
									FragmentManager fragmentManager = getFragmentManager();
									if (fragmentManager != null) {
										fragmentManager.popBackStack();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
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
	}

}
