package com.lqwawa.intleducation.module.discovery.ui.coin

import com.lqwawa.intleducation.factory.presenter.BasePresenter

/**
 * 货币转让Dialog的Presenter
 */
class CoinTransferPresenter : BasePresenter<CoinTransferContract.View>,CoinTransferContract.Presenter{

    constructor(view: CoinTransferContract.View) : super(view)

}