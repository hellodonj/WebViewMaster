package com.lqwawa.intleducation.module.discovery.ui.coin.donation;

import com.lqwawa.intleducation.factory.presenter.BasePresenter;

/**
 * 转赠页面的Presenter
 */
public class DonationCoinPresenter extends BasePresenter<DonationCoinContract.View>
    implements DonationCoinContract.Presenter{

    public DonationCoinPresenter(DonationCoinContract.View view) {
        super(view);
    }
}
