package com.lqwawa.intleducation.module.discovery.ui.coin

import com.lqwawa.intleducation.factory.presenter.BaseContract

/**
 * 货币转让Dialog的契约类
 */
interface CoinTransferContract{
    interface Presenter : BaseContract.Presenter

    interface View : BaseContract.View<Presenter>
}