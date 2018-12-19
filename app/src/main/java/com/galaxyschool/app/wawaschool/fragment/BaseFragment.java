package com.galaxyschool.app.wawaschool.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.duowan.mobile.netroid.Listener;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.bitmapmanager.ThumbnailManager;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.DialogHelper.LoadingDialog;
import com.galaxyschool.app.wawaschool.common.RefreshUtil;
import com.galaxyschool.app.wawaschool.fragment.library.FragmentListener;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.MapParamsStringRequest;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.ResourceResult;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.osastudio.apps.Config;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

public class BaseFragment extends Fragment implements View.OnKeyListener,
		FragmentManager.OnBackStackChangedListener,
		FragmentListener.FragmentResultListener {

	private static final String TAG = "TEST";
	private static final boolean DEBUG = Config.DEBUG;

	private String className = getClass().getSimpleName();
	private FragmentListener.FragmentResultListener resultListener;
	private int requestCode;
	private int resultCode = Activity.RESULT_CANCELED;
	private Intent resultData = new Intent();

	private LoadingDialog loadingDialog;
	private ThumbnailManager thumbnailManager;
	protected boolean isLoadingData;
	private boolean isRegisterEventBus;

	public Dialog showLoadingDialog() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			return loadingDialog;
		}
		loadingDialog = DialogHelper.getIt(getActivity()).GetLoadingDialog(0);
		return loadingDialog;
	}
	public Dialog showLoadingDialog(String content, boolean cancelable) {
		Dialog dialog = showLoadingDialog();
		((LoadingDialog) dialog).setContent(content);
		dialog.setCancelable(cancelable);
		return dialog;
	}

	public void dismissLoadingDialog() {
		try {
			if (this.loadingDialog != null && this.loadingDialog.isShowing()) {
				this.loadingDialog.dismiss();
			}
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			if (this.loadingDialog != null) {
				this.loadingDialog = null;
			}
		}
	}
	
	public View findViewById(int viewId) {
		if (getView() != null) {
			return getView().findViewById(viewId);
		}
		return null;
	}

	public void requestFocus() {
		View view = getView();
		view.setOnKeyListener(this);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
	}

	public void popStack() {
		if(getFragmentManager() != null) {
			getFragmentManager().popBackStack();
		}
	}

	protected void finishActivity() {
		if (getActivity() != null) {
			getActivity().finish();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		if (DEBUG)
            Log.i(TAG, className + ">>>>>>>>>>onAttach");
		super.onAttach(activity);

//		getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
//		getChildFragmentManager().addOnBackStackChangedListener(this);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onCreate");
		super.onCreate(savedInstanceState);
		thumbnailManager = MyApplication.getThumbnailManager(getActivity());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onViewStateStored");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (DEBUG)
            Log.i(TAG, className + ">>>>>>>>>>onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		requestFocus();
	}

	@Override
	public void onStart() {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		if (DEBUG)
            Log.i(TAG, className + ">>>>>>>>>>onResume " + getUserVisibleHint());
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onPause");
	}
	
	@Override
	public void onStop() {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		RefreshUtil.getInstance().clear();
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onDestroy");
		unRegisterEventBus();
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onDetach");
		super.onDetach();

//		getActivity().getSupportFragmentManager().removeOnBackStackChangedListener(this);
//		getChildFragmentManager().removeOnBackStackChangedListener(this);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onHiddenChanged: " + hidden);
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onBackStackChanged() {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onBackStackChanged "
					+ " isVisible: " + getUserVisibleHint());
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>setUserVisibleHint: " + isVisibleToUser);
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
//		if (DEBUG)
//			Log.i(TAG, className + ">>>>>>>>>>onKey: " + keyCode + "-" + event.getAction());
		if (event.getAction() == KeyEvent.ACTION_UP
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			return onBackPressed();
		}
		return false;
	}

	public boolean onBackPressed() {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onBackPressed");
		return false;
	}

	@Override
	public void onFragmentResult(int requestCode, int resultCode, Intent data) {
		if (DEBUG)
			Log.i(TAG, className + ">>>>>>>>>>onFragmentResult");
	}
	
	public boolean isDestroyed() {
		return getActivity() == null;
	}

	public void setResultListener(FragmentListener.FragmentResultListener listener) {
		this.resultListener = listener;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public void setResult(int resultCode) {
		this.resultCode = resultCode;
	}

	public void setResult(int resultCode, Intent resultData) {
		this.resultCode = resultCode;
        this.resultData = resultData;
	}

	public int getResultCode() {
		return resultCode;
	}

	public Intent getResultData() {
		return resultData;
	}

	public void finish() {
		if (this.resultListener != null) {
			this.resultListener.onFragmentResult(this.requestCode,
					this.resultCode, this.resultData);
		}
	}

	public void clearData() {

	}

	public void clearViews() {

	}

	public void clear() {
		clearData();
		clearViews();
	}

	public MyApplication getMyApplication() {
		if(getActivity() == null) {
			return null;
		}
		return (MyApplication) getActivity().getApplication();
	}

	public UserInfo getUserInfo() {
		if (getActivity() != null && getActivity().getApplication() != null) {
			return ((MyApplication) getActivity().getApplication()).getUserInfo();
		}
		return null;
	}

	public String getMemeberId() {
		if (getUserInfo() != null) {
			return getUserInfo().getMemberId();
		}
		return null;
	}
	
	public boolean isLogin() {
		if (TextUtils.isEmpty(getMemeberId())) {
			return false;
		}
		return true;
	}
	
	public void hideSoftKeyboard(Activity activity) {
		try {
			((InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(activity.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {

		}
	}
	
	public ThumbnailManager getThumbnailManager() {
		return thumbnailManager;
	}

	protected MapParamsStringRequest postRequest(String url, Map<String,
            Object> params, Listener listener) {
		return RequestHelper.sendPostRequest(getActivity(), url, params, listener);
	}

	protected class DefaultListener<T extends ModelResult>
			extends DefaultModelListener<T> {
		public DefaultListener(Class resultClass) {
			super(resultClass);
		}
	}

	protected class DefaultModelListener<T extends ModelResult>
			extends RequestHelper.RequestModelResultListener<T> {
		public DefaultModelListener(Class resultClass) {
			super(getActivity(), resultClass);
		}
	}

	protected class DefaultDataListener<T extends DataModelResult>
			extends RequestHelper.RequestDataResultListener<T> {
		public DefaultDataListener(Class resultClass) {
			super(getActivity(), resultClass);
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();
			isLoadingData = true;
		}


		@Override
		public void onFinish() {
			super.onFinish();
			isLoadingData = false;
		}
	}

	protected class DefaultResourceListener<T extends ResourceResult>
			extends RequestHelper.RequestResourceResultListener<T> {
		public DefaultResourceListener(Class resultClass) {
			super(getActivity(), resultClass);
		}
	}

	protected void addEventBusReceiver(){
		isRegisterEventBus = true;
		registerEventBus();
	}

	private void registerEventBus(){
		if (!EventBus.getDefault().isRegistered(this)) {
			if (DEBUG) {
				Log.i(TAG, className + ">>>>>>>>>>注册EventBus");
			}
			EventBus.getDefault().register(this);
		}
	}

	private void unRegisterEventBus(){
		if (isRegisterEventBus && EventBus.getDefault().isRegistered(this)) {
			if (DEBUG) {
				Log.i(TAG, className + ">>>>>>>>>>销毁EventBus");
			}
			EventBus.getDefault().unregister(this);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEvent(MessageEvent messageEvent){

	}

}
