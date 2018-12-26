package com.galaxyschool.app.wawaschool.fragment.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BasicUserInfoActivity;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper.LoadingDialog;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.VertificationCode;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.jpush.PushUtils;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.lqwawa.mooc.common.MOOCHelper;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends GetSmsVerCodeBase implements OnClickListener,
		TextView.OnEditorActionListener {

	public static final String TAG = RegisterFragment.class.getSimpleName();

	// private ToolbarTopView toolbarTopView;
	private EditText mPhoneNumTxt, userNameTxt, emailTxt, passwordTxt,
			confirmPasswordTxt, mVerCodeEditText, mSmsVerCodeEditText;
	private View registerBtn, mChangeVerBtn;
	private TextView mTab1, mTab2;
	private LoadingDialog loadingDialog;
	private int mCurTabIndex;
	private ImageView mVerCodeImg;
	private String mVerCode;
	private View mByPhoneContainer, mByNameContainer;
	private final int TAB1 = 0;
	private final int TAB2 = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_register, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initViews();
		if(getActivity() != null) {
			getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		}
	}

	private void initViews() {
		View rootView = getView();
		if (rootView != null) {
			rootView.findViewById(R.id.back_btn).setOnClickListener(this);
			mTab1 = ((TextView) rootView.findViewById(R.id.tab1));
			mTab2 = ((TextView) rootView.findViewById(R.id.tab2));
			mTab1.setText(R.string.register_tab1);
			mTab2.setText(R.string.register_tab2);
			mTab1.setOnClickListener(this);
			mTab2.setOnClickListener(this);
			// toolbarTopView = (ToolbarTopView)
			// rootView.findViewById(R.id.toolbartopview);
			// toolbarTopView.getBackView().setOnClickListener(this);
			// toolbarTopView.getTitleView().setText(R.string.register);

			mByPhoneContainer = rootView.findViewById(R.id.register_by_phone_container);
			mByNameContainer = rootView.findViewById(R.id.register_by_name_container);

			mPhoneNumTxt = (EditText) rootView
					.findViewById(R.id.register_phone_edittext);
			setPhoneNum();

			userNameTxt = (EditText) rootView
					.findViewById(R.id.register_username_edittext);
			emailTxt = (EditText) rootView
					.findViewById(R.id.register_email_edittext);

			passwordTxt = (EditText) rootView
					.findViewById(R.id.register_password_edittext);
			confirmPasswordTxt = (EditText) rootView
					.findViewById(R.id.register_confirm_password_edittext);
			registerBtn = rootView.findViewById(R.id.register_register_btn);
			mSmsVerCodeEditText = (EditText) rootView.findViewById(
					R.id.verification_code_edittext);
			mVerCodeEditText = (EditText) rootView
					.findViewById(R.id.register_verification_code_edittext);
			mGetVerCodeBtn = (TextView) rootView.findViewById(R.id.get_ver_code_btn);
			mGetVerCodeBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String phoneNum = mPhoneNumTxt.getText().toString().trim();
					checkGetVerCode(phoneNum, GetSmsVerCodeType.REGISTER);
				}
			});
			mVerCodeImg = (ImageView) rootView
					.findViewById(R.id.register_verification_code_image);
			setVerCode();
			mChangeVerBtn = rootView
					.findViewById(R.id.register_change_icon_btn);
			((TextView) mChangeVerBtn).getPaint().setFlags(
					Paint.UNDERLINE_TEXT_FLAG);

			confirmPasswordTxt.setOnEditorActionListener(this);
			registerBtn.setOnClickListener(this);
			mChangeVerBtn.setOnClickListener(this);

			userNameTxt.requestFocus();
			UIUtils.showSoftKeyboard(getActivity());

			TextView telephoneBtn = (TextView) rootView
					.findViewById(R.id.register_telephone_btn);
			telephoneBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CustomerServiceActivity.start(getActivity());
				}
			});
			telephoneBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
			switchTab(TAB1);
		}
	}

	private void switchTab(int tabIndex) {
		mCurTabIndex = tabIndex;
		if (TAB1 == tabIndex) {
			// mPhoneNumLayout.setVisibility(View.VISIBLE);
			// mUserNameLayout.setVisibility(View.GONE);
			// mGetVerCodeLayout.setVisibility(View.VISIBLE);
			// mEmailLayout.setVisibility(View.GONE);
			// mGetSmsVerCodeLayout.setVisibility(View.GONE);
			mByPhoneContainer.setVisibility(View.VISIBLE);
			mByNameContainer.setVisibility(View.GONE);

			mTab1.setEnabled(false);
			mTab2.setEnabled(true);
		} else {
			// mPhoneNumLayout.setVisibility(View.GONE);
			// mUserNameLayout.setVisibility(View.VISIBLE);
			// mGetVerCodeLayout.setVisibility(View.GONE);
			// mEmailLayout.setVisibility(View.VISIBLE);
			// mGetSmsVerCodeLayout.setVisibility(View.VISIBLE);
			mByPhoneContainer.setVisibility(View.GONE);
			mByNameContainer.setVisibility(View.VISIBLE);
			mTab1.setEnabled(true);
			mTab2.setEnabled(false);
		}
		findviews(tabIndex);
	}

	private void findviews(int tabIndex) {
		if (TAB1 == tabIndex) {
			passwordTxt = (EditText) getView().findViewById(R.id.register_phone_password_edittext);
			confirmPasswordTxt = (EditText) getView().findViewById(R.id.register_phone_confirm_password_edittext);
			registerBtn = getView().findViewById(R.id.register_phone_register_btn);
		} else if (TAB2 == tabIndex) {
			passwordTxt = (EditText) getView().findViewById(R.id.register_password_edittext);
			confirmPasswordTxt = (EditText) getView().findViewById(R.id.register_confirm_password_edittext);
			registerBtn = getView().findViewById(R.id.register_register_btn);
		}
		registerBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			FragmentManager fragmentManager = getFragmentManager();
			if (fragmentManager != null) {
				fragmentManager.popBackStack();
			}
			break;

		case R.id.tab1:
			switchTab(TAB1);
			break;

		case R.id.tab2:
			switchTab(TAB2);
			break;

		case R.id.register_phone_register_btn:
		case R.id.register_register_btn:
			register();
			break;
		case R.id.register_change_icon_btn:
			setVerCode();
			break;
		}
	}

	private void setVerCode() {
		mVerCodeImg.setImageBitmap(VertificationCode.getInstance()
				.createBitmap());
		mVerCode = VertificationCode.getInstance().getCode();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		switch (actionId) {
		case EditorInfo.IME_ACTION_DONE:
			register();
			break;
		}
		return true;
	}

	private void setPhoneNum() {
		if (mPhoneNumTxt != null) {
			TelephonyManager telephonyManager = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String phoneNumber=telephonyManager.getLine1Number();
			String prefix="+86";
			if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.startsWith(prefix)){
				phoneNumber=phoneNumber.substring(prefix.length());
			}
			mPhoneNumTxt.setText(phoneNumber);
		}
	}

	private void register() {
		String phoneNum = mPhoneNumTxt.getText().toString().trim();
		String userName = userNameTxt.getText().toString().trim();
		String email = emailTxt.getText().toString().toString();
		String password = passwordTxt.getText().toString().trim();
		String confirmPassword = confirmPasswordTxt.getText().toString().trim();

		if (mCurTabIndex == 0) {
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
			if (TextUtils.isEmpty(mSmsVerCodeEditText.getText().toString())) {
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
		} else {
			if (TextUtils.isEmpty(userName)) {
				TipMsgHelper.ShowMsg(getActivity(),
						getString(R.string.pls_input_username));
				return;
			}
			//用户名3—20个字符（仅限英文字母和数字）
			int length = userName.length();
			if (!(length >= 3 && length <= 20)){
				TipMsgHelper.ShowMsg(getActivity(),
						getString(R.string.user_name_length_is_not_legal));
				return;
			}
			if (!Utils.isContainEnglish(userName)) {
				TipMsgHelper.ShowMsg(getActivity(),
						getString(R.string.user_name_not_containe_char));
				return;
			}
			// if (TextUtils.isEmpty(email)) {
			// TipMsgHelper.ShowMsg(getActivity(),
			// getString(R.string.pls_input_email));
			// return;
			// }
			if (!TextUtils.isEmpty(email) && !Utils.isEmailAddress(email)) {
				TipMsgHelper.ShowMsg(getActivity(),
						getString(R.string.wrong_email_format));
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

			if (TextUtils.isEmpty(mVerCodeEditText.getText().toString())) {
				TipMsgHelper.ShowMsg(getActivity(),
						getString(R.string.pls_input_verification_code));
				return;
			}
			if (!mVerCode.toLowerCase().equals(
					mVerCodeEditText.getText().toString().toLowerCase())) {
				TipMsgHelper.ShowMsg(getActivity(),
						getString(R.string.verification_code_error));
				return;
			}
		}


		UIUtils.hideSoftKeyboard(getActivity());
		if (mCurTabIndex == TAB1) {
			registerByPhone(phoneNum, mSmsVerCodeEditText.getText().toString().trim(), password);
		} else {
			registerByName(userName, email, password);
		}
	}

	private void registerByPhone(String phoneNum, String smsCode, String password) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("Mobile", phoneNum);
		mParams.put("Password", password);
		mParams.put("Code", smsCode);
		mParams.put("Type", "1");
		toRegister(mParams);
	}

	private void registerByName(final String userName, String email, String password) {
		Map<String, Object> mParams = new HashMap<String, Object>();
		mParams.put("UserName", userName);
		mParams.put("EMail", TextUtils.isEmpty(email) ? "" : email);
		mParams.put("Password", password);
		mParams.put("Type", "0");
		toRegister(mParams);
	}

	private void toRegister(Map<String, Object> params) {
		RequestHelper.RequestModelResultListener listener = new RequestHelper.
				RequestModelResultListener<UserInfoResult>(getActivity(), UserInfoResult.class) {
			@Override
			public void onSuccess(String jsonString) {
				if (getActivity() == null) {
					return;
				}
				super.onSuccess(jsonString);
				stopTimerCountDown();
				UserInfoResult userInfoResult = getResult();
				if (userInfoResult != null && userInfoResult.isSuccess()) {
					UserInfo userInfo = userInfoResult.getModel();
					if (userInfo != null) {
						if (TextUtils.isEmpty(userInfo.getNickName())) {
							userInfo.setNickName(userInfo.getUserName());
						}
						if (mCurTabIndex == TAB1) {
							userInfo.setBindMobile(userInfo.getMobile());
						}
						TipMsgHelper.ShowMsg(getActivity(),
								getString(R.string.register_success));
//						ConversationHelper.login(
//								userInfo.getMemberId(),
//								userInfo.getPassword());
						getMyApplication().startDownloadService();
						userInfo.setRoles(String
								.valueOf(RoleType.ROLE_TYPE_VISITOR));
						getMyApplication().setUserInfo(userInfo);
						getMyApplication().getPrefsManager().setUserPassword(userInfo.getPassword());
						//初始化mooc用户信息
						MOOCHelper.init(userInfo);
						PushUtils.resumePush(getActivity());
						Intent intent = new Intent();
						// intent.setClass(getActivity(),
						// HomeActivity.class);
						intent.setClass(getActivity(),
								BasicUserInfoActivity.class);
						intent.putExtra("origin",
								RegisterFragment.class.getSimpleName());
						intent.putExtra("userInfo", userInfo);
						startActivity(intent);
						if (getActivity() != null) {
							getActivity().finish();
						}
					}
				}
			}
		};
		listener.setShowLoading(true);
		RequestHelper.sendPostRequest(getActivity(), ServerUrl.REGISTER_URL, params, listener);
//		String url = ServerUrl.REGISTER_URL;
//		PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
//				url, params, new Listener<String>() {
//					@Override
//					public void onSuccess(String json) {
//						stopTimerCountDown();
//						loadingDialog.dismiss();
//						try {
//							UserInfo userInfo = JSON.parseObject(json,
//									UserInfo.class);
//							if (userInfo != null) {
//								if (TextUtils.isEmpty(userInfo.getNickName())) {
//									userInfo.setNickName(userInfo.getUserName());
//								}
//								if (mCurTabIndex == TAB1) {
//									userInfo.setBindMobile(userInfo.getMobile());
//								}
//								TipMsgHelper.ShowMsg(getActivity(),
//										getString(R.string.register_success));
//								ConversationHelper.login(
//										userInfo.getMemberId(),
//										userInfo.getPassword());
//								userInfo.setRoles(String
//										.valueOf(RoleType.ROLE_TYPE_VISITOR));
//								((MyApplication) getActivity().getApplication())
//										.setUserInfo(userInfo);
//								Intent intent = new Intent();
//								// intent.setClass(getActivity(),
//								// HomeActivity.class);
//								intent.setClass(getActivity(),
//										BasicUserInfoActivity.class);
//								intent.putExtra("origin",
//										RegisterFragment.class.getSimpleName());
//								intent.putExtra("userInfo", userInfo);
//								startActivity(intent);
//								if (getActivity() != null) {
//									getActivity().finish();
//								}
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//
//					@Override
//					public void onError(NetroidError error) {
//						super.onError(error);
//						loadingDialog.dismiss();
//						String es = error.getMessage();
//						try {
//							NetErrorResult result = JSON.parseObject(es,
//									NetErrorResult.class);
//							if (result != null) {
//								if (result.isHasError()) {
//									TipMsgHelper.ShowMsg(getActivity(),
//											result.getErrorMessage());
//								}
//							}
//						} catch (Exception e) {
//							TipMsgHelper.ShowLMsg(getActivity(),
//									getString(R.string.network_error));
//						}
//					}
//				});
//		request.addHeader("Accept-Encoding", "*");
//		request.start(getActivity());
//		loadingDialog = DialogHelper.getIt(getActivity()).GetLoadingDialog(0);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		stopTimerCountDown();
	}

}
