package com.lqwawa.intleducation.module.user.ui.all;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;

import java.util.List;

/**
 * 描述: 我的订单全部的契约类
 * 作者|时间: djj on 2019/6/10 0010 上午 10:19
 */
public interface OrderAllContract {

    interface Presenter extends BaseContract.Presenter {
        /**
         * @param pageIndex 页索引
         * @param pageSize  条数
         * @param memberId
         */
        void requestOrderList(int pageIndex, int pageSize, String memberId);
    }

    interface View extends BaseContract.View<OrderAllContract.Presenter> {
        //回调订单列表
        void updateOrderList(@NonNull List<MyOrderVo> orderVos);

        //加载更多
        void updateMoreOrderList(@NonNull List<MyOrderVo> orderVos);
    }
}
