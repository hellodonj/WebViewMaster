package com.lqwawa.intleducation.module.user.ui.all;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;

import java.util.List;

/**
 * 描述: 我的订单all界面的presenter
 * 作者|时间: djj on 2019/6/6 0006 下午 4:05
 */
public class OrderAllPresenter extends BasePresenter<OrderAllContract.View>
        implements OrderAllContract.Presenter {

    public OrderAllPresenter(OrderAllContract.View view) {
        super(view);
    }

    @Override
    public void requestOrderList(int pageIndex, int pageSize, int tabType, String memberId) {
        UserHelper.requestLQMyOrderList(pageIndex, pageSize, tabType, memberId, new DataSource.Callback<List<MyOrderVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OrderAllContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<MyOrderVo> orderVos) {
                final OrderAllContract.View view = getView();
                if (EmptyUtil.isNotEmpty(view)) {
                    if (pageIndex == 0) {
                        view.updateOrderList(orderVos);
                    } else {
                        view.updateMoreOrderList(orderVos);
                    }

                }
            }
        });
    }
}
