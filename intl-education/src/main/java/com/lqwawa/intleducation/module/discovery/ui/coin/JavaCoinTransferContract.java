package com.lqwawa.intleducation.module.discovery.ui.coin;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * 货币赠送的契约类
 */
public interface JavaCoinTransferContract{
    interface Presenter extends BaseContract.Presenter{
        void requestParentChildData();
        void requestUserInfoWithMembers(@NonNull List<UserModel> members);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateParentChildView(@NonNull List<ChildrenListVo> vos);
        void updateUserInfoWithMembersView(@NonNull List<UserEntity> entities);
    }
}