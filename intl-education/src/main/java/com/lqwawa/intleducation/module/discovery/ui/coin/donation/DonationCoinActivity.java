package com.lqwawa.intleducation.module.discovery.ui.coin.donation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;

/**
 * 转赠蛙蛙币页面
 */
public class DonationCoinActivity extends PresenterActivity<DonationCoinContract.Presenter>
    implements DonationCoinContract.View{

    @Override
    protected DonationCoinContract.Presenter initPresenter() {
        return new DonationCoinPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_donation_coin;
    }

    public static void show(@NonNull Context context){
        Intent intent = new Intent(context,DonationCoinActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
