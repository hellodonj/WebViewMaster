package com.lqwawa.intleducation.module.user.ui.review;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.module.user.ui.all.OrderAllContract;
import com.lqwawa.intleducation.module.user.vo.MyOrderVo;

import java.util.List;

/**
 * 描述: 待批阅
 * 作者|时间: djj on 2019/6/10 0010 上午 9:34
 */
public class PendingReviewFragment extends PresenterFragment<OrderAllContract.Presenter>
        implements OrderAllContract.View{
    @Override
    protected OrderAllContract.Presenter initPresenter() {
        return null;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_order_all;
    }

    @Override
    public void updateOrderList(@NonNull List<MyOrderVo> orderVos) {

    }

    @Override
    public void updateMoreOrderList(@NonNull List<MyOrderVo> orderVos) {

    }
}
