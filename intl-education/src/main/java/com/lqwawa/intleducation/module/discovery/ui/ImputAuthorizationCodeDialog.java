package com.lqwawa.intleducation.module.discovery.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.lqpay.widget.GridPasswordView;

/**
 * Created by XChen on 2017/09/07.
 * email:man0fchina@foxmail.com
 * 输入授权码对话框
 */
public class ImputAuthorizationCodeDialog extends Dialog {
    private Context context;
    private String tipInfo;
    private CommitCallBack callback;
    private GridPasswordView gridPasswordView;
    private Button positiveButton;
    private Button negativeButton;
    private boolean isCommited = false;
    private int codeType; //1 授权码 2激活码

    public ImputAuthorizationCodeDialog(Context context, String tipInfo,int codeType, CommitCallBack callback) {
        super(context);
        this.context = context;
        this.tipInfo = tipInfo;
        this.codeType = codeType;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widgets_imput_authorization_dialog);
        zoom();
        initUI();
    }

    public void setTipInfo(String tipInfo){
        this.tipInfo = tipInfo;
        try {
            ((TextView) findViewById(R.id.title)).setText(this.tipInfo);
            ((GridPasswordView) findViewById(R.id.password)).clearPassword();
        }catch (Exception ex){

        }
    }

    public void clearPassword(){
        try {
            ((GridPasswordView) findViewById(R.id.password)).clearPassword();
        }catch (Exception ex){

        }
    }

    public void setCommited(boolean value){
        this.isCommited = value;
    }

    /**
     * 界面缩放
     */
    private void zoom() {
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        LayoutParams p = getWindow().getAttributes();
        float widthRatio = 1.0f;
        float heightRatio = 0.5f;
        p.width = (int) (d.getWidth() * widthRatio);
        p.height = LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(p);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        this.setCanceledOnTouchOutside(false);
        gridPasswordView = (GridPasswordView) findViewById(R.id.password);
        ((TextView) findViewById(R.id.title)).setText(this.tipInfo);
        gridPasswordView.setPasswordVisibility(true);
        positiveButton = (Button) findViewById(R.id.positiveButton);
        negativeButton = (Button) findViewById(R.id.negativeButton);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = gridPasswordView.getPassWord().toString();
                if (!StringUtils.isValidString(code)
                        || code.length() != 6) {
                    if (codeType == 1) {
                        ToastUtil.showToast(context,
                                context.getResources().getString(
                                        R.string.imput_authorization_please));
                    } else if (codeType == 2) {
                        ToastUtil.showToast(context,
                                context.getResources().getString(
                                        R.string.input_activation_please));
                    }
                    return;
                }
                if (callback != null) {
                    callback.onCommit(code);
                }
                //ImputAuthorizationCodeDialog.this.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(callback != null){
                    callback.onCancel();
                }
            }
        });

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(callback != null && !isCommited){
                    callback.onCancel();
                }
            }
        });

        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
            }
        });
        getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public interface CommitCallBack{
        void onCommit(String code);
        void onCancel();
    }
}