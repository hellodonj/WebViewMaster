package com.galaxyschool.app.wawaschool.fragment.account;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.Netroid;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FeedbackFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FeedbackFragment.class.getSimpleName();

    private ToolbarTopView toolbarTopView;
    private EditText contentTxt, emailTxt;
    private UserInfo userInfo;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        userInfo = ((MyApplication)activity.getApplication()).getUserInfo();
    }

    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feedback, container, false);
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
            toolbarTopView.getTitleView().setText(R.string.feedback);
            toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
            toolbarTopView.getCommitView().setText(R.string.send);
            toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
            toolbarTopView.getBackView().setOnClickListener(this);
            toolbarTopView.getCommitView().setOnClickListener(this);

            contentTxt = (EditText) rootView.findViewById(R.id.feedback_content_edittext);
            emailTxt = (EditText) rootView.findViewById(R.id.feedback_email_edittext);
            if(userInfo != null) {
                emailTxt.setText(userInfo.getEmail());
                if(!TextUtils.isEmpty(userInfo.getEmail())) {
                    emailTxt.setSelection(userInfo.getEmail().length());
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    getActivity().finish();
                }
                break;
            case R.id.toolbar_top_commit_btn:
                feedback();
                break;
        }
    }

    private void feedback() {
        String content = contentTxt.getText().toString().trim();
        String email = emailTxt.getText().toString().trim();

        if(TextUtils.isEmpty(content)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.pls_input_feedback));
            return;
        }

        if(TextUtils.isEmpty(email)) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.pls_input_email));
            return;
        }else {
            if (!Utils.isEmail(email)) {
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.wrong_email_format));
                return;
            }
        }

        feedback(content, email);
    }

    private void feedback(String content, String email) {
        String url = ServerUrl.SAVE_FEEDBACK_URL;
        if (userInfo != null) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("Context", content);
            mParams.put("EMail", email);
            mParams.put("MemberID", userInfo.getMemberId());
            mParams.put("IsFeedback", "0");
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
                        JSONObject jsonObject = new JSONObject(json);
                        if(jsonObject != null) {
                            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.feedback_send_success));
                            getActivity().finish();
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
}
