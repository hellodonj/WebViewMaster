package com.lqwawa.intleducation.module.discovery.ui.coin;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.helper.UserHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * 货币转让Dialog的Presenter
 */
public class JavaCoinTransferPresenter extends BasePresenter<JavaCoinTransferContract.View>
        implements JavaCoinTransferContract.Presenter {

    public JavaCoinTransferPresenter(JavaCoinTransferContract.View view) {
        super(view);
    }

    @Override
    public void requestParentChildData() {
        MyCourseHelper.requestParentChildData(new DataSource.Callback<List<ChildrenListVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final JavaCoinTransferContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ChildrenListVo> childrenListVos) {
                final JavaCoinTransferContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateParentChildView(childrenListVos);
                }
            }
        });
    }

    @Override
    public void requestUserInfoWithMembers(@NonNull List<UserModel> members) {
        UserHelper.requestRealNameWithNick(false, members, new DataSource.Callback<List<UserEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final JavaCoinTransferContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }


            @Override
            public void onDataLoaded(List<UserEntity> entities) {
                final JavaCoinTransferContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateUserInfoWithMembersView(entities);
                }
            }
        });
    }
}