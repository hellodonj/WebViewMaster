package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.helper.ThirdPartyLoginHelper;
import com.galaxyschool.app.wawaschool.pojo.BindThirdParty;
import com.galaxyschool.app.wawaschool.pojo.BindThirdPartyResult;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.lqbaselib.net.library.Model;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.osastudio.common.utils.TipMsgHelper;
import com.tencent.mm.opensdk.utils.Log;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ======================================================
 * Created by : Brave_Qu on 2018/9/8 0008 11:30
 * Describe:关联账号
 * ======================================================
 */
public class AssociateAccountActivity extends BaseFragmentActivity implements View.OnClickListener{
    private TextView wxAuthorizeTextV;
    private TextView qqAuthorizeTextV;
    private String qqBindUnionid;
    private String wxBindUnionid;
    private String qqNickName;
    private String wxNickName;
    public static void start(Activity activity){
        Intent intent = new Intent(activity,AssociateAccountActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associate_account);
        initView();
        loadBindData();
    }

    private void initView(){
        ToolbarTopView toolbarTopView = (ToolbarTopView)findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getTitleView().setText(R.string.str_associated_account);
        LinearLayout qqLayout = (LinearLayout) findViewById(R.id.ll_qq);
        qqLayout.setOnClickListener(this);
        LinearLayout wxLayout = (LinearLayout) findViewById(R.id.ll_weixin);
        wxLayout.setOnClickListener(this);
        wxAuthorizeTextV = (TextView) findViewById(R.id.tv_wx_status);
        qqAuthorizeTextV = (TextView) findViewById(R.id.tv_qq_status);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.ll_qq){
            handlerBindShareMediaData(SHARE_MEDIA.QQ);
        } else if (viewId == R.id.ll_weixin){
            handlerBindShareMediaData(SHARE_MEDIA.WEIXIN);
        } else if (viewId == R.id.toolbar_top_back_btn){
            finish();
        }
    }

    private void loadBindData(){
        Map<String,Object> param = new HashMap<>();
        param.put("MemberId", DemoApplication.getInstance().getMemberId());
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener<BindThirdPartyResult>(AssociateAccountActivity.this,BindThirdPartyResult.class){
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                BindThirdPartyResult result = getResult();
                if (result != null && result.isSuccess()){
                    String bindString = result.getModel().toString();
                    if (!TextUtils.isEmpty(bindString)){
                        try {
                            List<BindThirdParty> bindThirdPartyList = result.getModel().getDataList();
                            if (bindThirdPartyList != null && bindThirdPartyList.size() > 0){
                                boolean isBindQQ = false;
                                boolean isBindWx = false;
                                for (int i = 0; i < bindThirdPartyList.size(); i++){
                                    BindThirdParty data = bindThirdPartyList.get(i);
                                    if (data.getIdentityType() == 1){
                                        isBindWx = true;
                                        wxBindUnionid = data.getUnionid();
                                        wxNickName = data.getNickName();
                                    } else if (data.getIdentityType() == 2){
                                        isBindQQ = true;
                                        qqBindUnionid = data.getUnionid();
                                        qqNickName = data.getNickName();
                                    }
                                }
                                changeAssociateText(isBindQQ,isBindWx);
                            } else {
                                changeAssociateText(false,false);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        RequestHelper.sendPostRequest(AssociateAccountActivity.this, ServerUrl
                        .GET_LOAD_THIRDPARTY_ASSOCIATED_ACCOUNT, param, listener);
    }

    private void changeAssociateText(boolean qqAssociate,boolean wxAssociate){
        if (qqAssociate){
            qqAuthorizeTextV.setText(qqNickName);
            qqAuthorizeTextV.setTextColor(ContextCompat.getColor(this,R.color.text_black));
        } else {
            qqAuthorizeTextV.setText(getString(R.string.str_not_associated));
            qqAuthorizeTextV.setTextColor(ContextCompat.getColor(this,R.color.text_green));
        }

        if (wxAssociate){
            wxAuthorizeTextV.setText(wxNickName);
            wxAuthorizeTextV.setTextColor(ContextCompat.getColor(this,R.color.text_black));
        } else {
            wxAuthorizeTextV.setText(getString(R.string.str_not_associated));
            wxAuthorizeTextV.setTextColor(ContextCompat.getColor(this,R.color.text_green));
        }
    }

    private void handlerBindShareMediaData(SHARE_MEDIA shareMedia){
        if (shareMedia == SHARE_MEDIA.QQ){
            if (TextUtils.equals(getString(R.string.str_not_associated),qqAuthorizeTextV.getText())){
                //未关联
                applyOrDeleteShareMediaAuthorization(shareMedia,true);
            } else {
                //已关联
                popUnbindAuthDialog(shareMedia,false);
            }
        } else if (shareMedia == SHARE_MEDIA.WEIXIN){
            if (TextUtils.equals(getString(R.string.str_not_associated),wxAuthorizeTextV.getText())){
                //未关联
                applyOrDeleteShareMediaAuthorization(shareMedia,true);
            } else {
                //已关联
                popUnbindAuthDialog(shareMedia,false);
            }
        }
    }

    private void popUnbindAuthDialog(final SHARE_MEDIA share_media, final boolean isUnbindSuccess){
        String cancelText = getString(R.string.cancel);
        String confirmText = getString(R.string.str_remove_associated);
        if (isUnbindSuccess){
            cancelText = "";
            confirmText = getString(R.string.str_i_know);
        }
        String messageTip = "";
        if (share_media == SHARE_MEDIA.QQ){
            messageTip = getString(R.string.str_remove_associated_tips,getString(R.string.str_qq));
        } else if (share_media == SHARE_MEDIA.WEIXIN){
            messageTip = getString(R.string.str_remove_associated_tips,getString(R.string.str_weixin));
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                this,
                null,
                R.layout.layout_change_unbind_auth_dialog,
                cancelText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                confirmText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isUnbindSuccess) {
                            applyOrDeleteShareMediaAuthorization(share_media, false);
                        }
                    }
                });
        ImageView unBindImage = (ImageView) messageDialog.getContentView().findViewById(R.id.iv_unbind_tip);
        TextView unBindTitle = (TextView) messageDialog.getContentView().findViewById(R.id.tv_unbind_message_title);
        TextView unBindTip = (TextView) messageDialog.getContentView().findViewById(R.id.tv_unbind_tip);
        View seperator = messageDialog.getContentView().findViewById(R.id.contacts_dialog_button_seperator);
        Button mBtnLeft = (Button) messageDialog.getContentView().findViewById(R.id.contacts_dialog_left_button);
        if (mBtnLeft != null) {
            mBtnLeft.setText(cancelText);
            if (TextUtils.isEmpty(cancelText)) {
                mBtnLeft.setVisibility(View.GONE);
                seperator.setVisibility(View.GONE);
            } else {
                mBtnLeft.setVisibility(View.VISIBLE);
                seperator.setVisibility(View.VISIBLE);
            }
        }
        Button button = (Button) messageDialog.getContentView().findViewById(R.id.contacts_dialog_right_button);
        button.setText(confirmText);
        if (isUnbindSuccess){
            unBindImage.setImageResource(R.drawable.icon_success);
            unBindTitle.setText(R.string.str_remove_associated_success);
        } else {
            unBindImage.setImageResource(R.drawable.icon_remove_tip);
            unBindTitle.setText(R.string.str_confirm_remove_associated);
        }
        unBindTip.setText(messageTip);
        messageDialog.show();
    }

    private void applyOrDeleteShareMediaAuthorization(final SHARE_MEDIA share_media, final boolean isApplyAuth){
        ThirdPartyLoginHelper helper = new ThirdPartyLoginHelper(AssociateAccountActivity.this);
        helper.setShareMediaType(share_media)
                .setFunctionType(isApplyAuth ? ThirdPartyLoginHelper.FUNCTION_TYPE.BIND_AUTH :
                        ThirdPartyLoginHelper.FUNCTION_TYPE.DELETE_AUTH)
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean bindSuccess = (boolean) result;
                        if (bindSuccess){
                            if (isApplyAuth) {
                                TipMsgHelper.ShowMsg(AssociateAccountActivity.this, R.string.str_bind_auth_success);
                            } else {
                                popUnbindAuthDialog(share_media,true);
                            }
                            loadBindData();
                        }
                    }
                });
        if (!isApplyAuth){
            if (share_media == SHARE_MEDIA.QQ){
                helper.setUnionid(qqBindUnionid);
            } else if (share_media == SHARE_MEDIA.WEIXIN){
                helper.setUnionid(wxBindUnionid);
            }
        }
        helper.start();
    }
}
