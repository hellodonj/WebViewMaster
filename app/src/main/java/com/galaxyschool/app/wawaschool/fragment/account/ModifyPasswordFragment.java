package com.galaxyschool.app.wawaschool.fragment.account;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;

import java.util.HashMap;
import java.util.Map;

public class ModifyPasswordFragment extends BaseFragment implements View.OnClickListener, TextView
    .OnEditorActionListener {
    public static final String TAG = ModifyPasswordFragment.class.getSimpleName();

    private ToolbarTopView toolbarTopView;
    private EditText oldPasswordTxt, newPasswordTxt, confirmPasswordTxt;

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_modify_password, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        View rootView = getView();
        if (rootView != null) {
            toolbarTopView = (ToolbarTopView) rootView.findViewById(R.id.toolbartopview);
            toolbarTopView.getBackView().setVisibility(View.VISIBLE);
            toolbarTopView.getTitleView().setText(R.string.modify_password);
            toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
            toolbarTopView.getCommitView().setText(R.string.save);
            toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
            toolbarTopView.getBackView().setOnClickListener(this);
            toolbarTopView.getCommitView().setOnClickListener(this);

            oldPasswordTxt = (EditText)rootView.findViewById(R.id.old_password_edittext);
            newPasswordTxt = (EditText)rootView.findViewById(R.id.new_password_edittext);
            confirmPasswordTxt = (EditText)rootView.findViewById(R.id.confirm_new_password_edittext);
            confirmPasswordTxt.setOnEditorActionListener(this);

        }
    }

    private void modifyPassword() {
        String oldPassword = oldPasswordTxt.getText().toString().trim();
        String newPassword = newPasswordTxt.getText().toString().trim();
        String confirmPassword = confirmPasswordTxt.getText().toString().trim();

        if(TextUtils.isEmpty(oldPassword)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.input_old_password));
            oldPasswordTxt.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(newPassword)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.input_new_password));
            newPasswordTxt.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(confirmPassword)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.confirm_new_password));
            confirmPasswordTxt.requestFocus();
            return;
        }

        if(!newPassword.equals(confirmPassword)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.different_password));
            newPasswordTxt.requestFocus();
            return;
        }
        String memberId = ((MyApplication)getActivity().getApplication()).getMemberId();
        if(!TextUtils.isEmpty(memberId)) {
            modifyPassword(memberId, oldPassword, newPassword);
        }
    }

    private void modifyPassword(String memberId, String oldPassword, String newPassword) {
        String url = ServerUrl.MODIFY_PASSWORD_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("MemberID", memberId);
            mParams.put("OldPassword", oldPassword);
            mParams.put("NewPassword", newPassword);
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    if (getActivity() == null) {
                        return;
                    }
                    dismissLoadingDialog();
                    try {
                        Log.i("", "Login:onSuccess " + json);
                        UserInfo userInfo = JSON.parseObject(json, UserInfo.class);
                        if (userInfo != null) {
                            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.modify_password_success));
//                            FragmentManager fragmentManager = getFragmentManager();
//                            if(fragmentManager != null) {
//                                fragmentManager.popBackStack();
//                            }
                            if(!getActivity().isFinishing()){
                                getActivity().finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    dismissLoadingDialog();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(getActivity(), result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(getActivity());
            showLoadingDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
//                FragmentManager fragmentManager = getFragmentManager();
//                if(fragmentManager != null) {
//                    fragmentManager.popBackStack();
//                }
                getActivity().finish();
                break;
            case R.id.toolbar_top_commit_btn:
                modifyPassword();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                modifyPassword();
                break;
        }
        return true;
    }
}
