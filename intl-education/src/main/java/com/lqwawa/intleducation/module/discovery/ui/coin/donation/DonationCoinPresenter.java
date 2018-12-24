package com.lqwawa.intleducation.module.discovery.ui.coin.donation;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.user.CoinEntity;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.helper.OrderHelper;
import com.lqwawa.intleducation.factory.helper.UserHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.coin.JavaCoinTransferContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * 转赠页面的Presenter
 */
public class DonationCoinPresenter extends BasePresenter<DonationCoinContract.View>
    implements DonationCoinContract.Presenter{

    public DonationCoinPresenter(DonationCoinContract.View view) {
        super(view);
    }


    @Override
    public void requestParentChildData() {
        MyCourseHelper.requestParentChildData(new DataSource.Callback<List<ChildrenListVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ChildrenListVo> childrenListVos) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateParentChildView(childrenListVos);
                }
            }
        });
    }

    @Override
    public void requestUserCoinCount() {
        UserHelper.requestUserCoinCount(new DataSource.Callback<CoinEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(CoinEntity coinEntity) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateUserCoinCountView(coinEntity);
                }
            }
        });
    }

    @Override
    public void requestUserInfoWithMembers(@NonNull List<UserModel> members) {
        UserHelper.requestRealNameWithNick(false, members, new DataSource.Callback<List<UserEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }


            @Override
            public void onDataLoaded(List<UserEntity> entities) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateUserInfoWithMembersView(entities);
                }
            }
        });
    }

    @Override
    public void requestBalanceDonation(@NonNull String memberId, @NonNull String beneficiaryId, int amount) {
        OrderHelper.requestUserBalanceDonation(memberId, beneficiaryId, 2, amount, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final DonationCoinContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateBalanceDonationView(aBoolean);
                }
            }
        });
    }
}
