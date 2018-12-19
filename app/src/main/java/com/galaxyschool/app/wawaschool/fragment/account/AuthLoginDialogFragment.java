package com.galaxyschool.app.wawaschool.fragment.account;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.common.utils.DrawableUtil;

/**
 * @author: wangchao
 * @date: 2018/11/30
 * @desc:
 */
public class AuthLoginDialogFragment extends DialogFragment {

    public static final String TAG = AuthLoginDialogFragment.class.getSimpleName();

    private int loginType;
    private OnAuthLoginListener listener;

    public static AuthLoginDialogFragment newInstance(int loginType) {
        Bundle args = new Bundle();
        AuthLoginDialogFragment fragment = new AuthLoginDialogFragment();
        args.putInt("loginType", loginType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_fragment_auth_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        if (getArguments() != null) {
            loginType = getArguments().getInt("loginType", 0);
        }

        if (getView() != null && getActivity() != null) {
            TextView authLoginInfoTxt = (TextView) getView().findViewById(R.id.tv_auth_login_info);
            Button authLoginLqwawaBtn = (Button) getView().findViewById(R.id.btn_auth_login_lqwawa);
            Button authLoginOtherBtn = (Button) getView().findViewById(R.id.btn_auth_login_other);

            LinearLayout authLoginLqwawaLayout = (LinearLayout) getView().findViewById(R.id
                    .layout_auth_login_lqwawa);
            LinearLayout authLoginOtherLayout = (LinearLayout) getView().findViewById(R.id
                    .layout_auth_login_other);
            Drawable drawable = DrawableUtil.createDrawable(Color.parseColor("#e0f1e3"), Color
                    .parseColor("#e0f1e3"), dip2px(getActivity(), 10));
            authLoginLqwawaLayout.setBackground(drawable);
            authLoginOtherLayout.setBackground(drawable);


            Drawable qqDrawable = ContextCompat.getDrawable(getActivity(), R.drawable
                    .ic_auth_login_qq);
            Drawable wxDrawable = ContextCompat.getDrawable(getActivity(), R.drawable
                    .ic_auth_login_wx);
            Drawable arrowDrawable = ContextCompat.getDrawable(getActivity(), R.drawable
                    .ic_arrow_right_green);
            authLoginOtherBtn.setCompoundDrawablePadding(dip2px(getActivity(), 5));

            if (loginType == LoginType.WECHAT) {
                authLoginInfoTxt.setText(R.string.wx_auth_login_info);
                authLoginOtherBtn.setText(R.string.str_wx_login);
                authLoginOtherBtn.setCompoundDrawablesWithIntrinsicBounds(wxDrawable, null,
                        arrowDrawable, null);
            } else if (loginType == LoginType.QQ) {
                authLoginInfoTxt.setText(R.string.qq_auth_login_info);
                authLoginOtherBtn.setText(R.string.str_qq_login);
                authLoginOtherBtn.setCompoundDrawablesWithIntrinsicBounds(qqDrawable, null,
                        arrowDrawable, null);
            }
            authLoginLqwawaBtn.setOnClickListener(v -> {
                loginType = LoginType.LQWAWA;
                authLogin();
            });

            authLoginOtherBtn.setOnClickListener(v -> authLogin());
        }
    }

    private void authLogin() {
        dismiss();
        if (listener != null) {
            listener.onAuthLogin(loginType);
        }
    }

    public interface OnAuthLoginListener {
        void onAuthLogin(int loginType);
    }

    public void setOnAuthLoginListener(OnAuthLoginListener listener) {
        this.listener = listener;
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }
}
