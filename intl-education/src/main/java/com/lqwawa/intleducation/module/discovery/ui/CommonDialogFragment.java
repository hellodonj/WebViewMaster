package com.lqwawa.intleducation.module.discovery.ui;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.lqwawa.intleducation.R;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/10 10:23
 * desp: 描 述：
 * ================================================
 */

public class CommonDialogFragment extends DialogFragment {


    private TextView btn_ok;
    private TextView btn_cancel;

    private int currentAmount;
    private int needAmount;
    private TextView tvNeed;
    private TextView tvCurrent;
    private TextView tvTips;

    public static CommonDialogFragment newInstance(int current, int need) {
        CommonDialogFragment dialogFragment = new CommonDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("current", current);
        bundle.putInt("need", need);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.common_dialog_fragment, container, false);


        currentAmount = getArguments().getInt("current");
        needAmount = getArguments().getInt("need");

        btn_ok = (TextView) view.findViewById(R.id.tv_confirm);
        btn_cancel = (TextView) view.findViewById(R.id.tv_cancel);

        tvNeed = (TextView) view.findViewById(R.id.need_coins_tv);
        tvCurrent = (TextView) view.findViewById(R.id.current_coins_tv);
        tvTips = (TextView) view.findViewById(R.id.charge_tips);


        tvNeed.setText(String.format(getResources().getString(R.string.buy_course_need_coins), String.valueOf(needAmount)));
        tvCurrent.setText(String.format(getResources().getString(R.string.current_wawa_coins), String.valueOf(currentAmount)));


        boolean needCharge = false;
        if (currentAmount >= needAmount) {
            tvTips.setVisibility(View.GONE);
            btn_ok.setText(getResources().getString(R.string.pay_confirm));
            needCharge = false;
        } else {
            tvTips.setVisibility(View.VISIBLE);
            btn_ok.setText(getResources().getString(R.string.charge_coin));
            needCharge = true;
        }

        final boolean finalNeedCharge = needCharge;
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.confirm(finalNeedCharge);
                getDialog().dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    public interface WaWaPayListener {

        void confirm(boolean value);
    }

    public WaWaPayListener mListener;

    public void setOnWaWaPayListener(WaWaPayListener listener) {
        mListener = listener;
    }


}
