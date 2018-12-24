package com.lqwawa.intleducation.module.discovery.ui.coin.donation;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.user.CoinEntity;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * 转赠页面的契约类
 */
public interface DonationCoinContract {

    interface Presenter extends BaseContract.Presenter{
        void requestParentChildData();
        void requestUserCoinCount();
        void requestUserInfoWithMembers(@NonNull List<UserModel> members);
        void requestBalanceDonation(@NonNull String memberId,@NonNull String beneficiaryId,int amount);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateParentChildView(@NonNull List<ChildrenListVo> vos);
        void updateUserCoinCountView(@NonNull CoinEntity entity);
        void updateUserInfoWithMembersView(@NonNull List<UserEntity> entities);
        void updateBalanceDonationView(boolean result);
    }

}
