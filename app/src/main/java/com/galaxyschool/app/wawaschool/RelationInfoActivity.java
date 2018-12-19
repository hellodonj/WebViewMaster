package com.galaxyschool.app.wawaschool;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.pojo.RelationInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RelationInfoActivity extends BaseActivity implements View.OnClickListener {

    private ToolbarTopView toolbarTopView;
    private EditText accountTxt, nameTxt;
    private DialogHelper.LoadingDialog loadingDialog;

    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationinfo);

        initViews();
        userInfo = ((MyApplication)getApplication()).getUserInfo();
    }

    private void initViews() {
        toolbarTopView = (ToolbarTopView)findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setText(R.string.bind);
        toolbarTopView.getTitleView().setText(R.string.bind_parent);
        toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);

        accountTxt = (EditText)findViewById(R.id.parent_account_edittext);
        nameTxt = (EditText)findViewById(R.id.parent_name_edittext);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                finish();
                break;
            case R.id.toolbar_top_commit_btn:
                saveParentInfo();
                break;
        }
    }

    private void saveParentInfo() {
        String account = accountTxt.getText().toString().trim();
        String name = nameTxt.getText().toString().trim();

        if(TextUtils.isEmpty(account)) {
            TipMsgHelper.ShowMsg(RelationInfoActivity.this, getString(R.string.pls_input_parent_account));
            return;
        }

        if(TextUtils.isEmpty(name)) {
            TipMsgHelper.ShowMsg(RelationInfoActivity.this, getString(R.string.pls_input_parent_name));
            return;
        }

        if(userInfo != null) {
            saveParentInfo(userInfo.getMemberId(), account, name);
        }
    }

    private void saveParentInfo(String memberId, String account, String name){
        if(TextUtils.isEmpty(memberId)) {
            return;
        }

        String url = ServerUrl.SAVE_RELATIONINFO_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("MemberId", memberId);
            mParams.put("ParentNickName", account);
            mParams.put("ParentRealName", name);
            mParams.put("ChildId", memberId);
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    loadingDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if(jsonObject != null) {
                            String parentName = jsonObject.optString("ParentRealName");
                            int state = jsonObject.optInt("CheckState");
                            String childId = jsonObject.optString("ChildId");
                            RelationInfo relationInfo = new RelationInfo();
                            relationInfo.setParentRealName(parentName);
                            relationInfo.setState(state);
                            relationInfo.setChildId(childId);

                            Intent intent = new Intent();
                            intent.putExtra("relationInfo", relationInfo);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(RelationInfoActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(RelationInfoActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(RelationInfoActivity.this);
            loadingDialog = DialogHelper.getIt(RelationInfoActivity.this).GetLoadingDialog(0);
        }

    }
}
