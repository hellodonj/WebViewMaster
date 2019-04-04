package com.lqwawa.mooc.modle.tutorial;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.helper.UserHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.regist.IDType;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页的Presenter
 */
public class TutorialHomePagePresenter extends BasePresenter<TutorialHomePageContract.View>
    implements TutorialHomePageContract.Presenter{

    public TutorialHomePagePresenter(TutorialHomePageContract.View view) {
        super(view);
    }

    @Override
    public void requestUserInfoWithUserId(@NonNull String userId) {
        UserHelper.requestUserInfoWithUserId(userId, new DataSource.Callback<UserEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(UserEntity entity) {
                final TutorialHomePageContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) &&
                        EmptyUtil.isNotEmpty(entity)){
                    view.updateUserInfoView(entity);
                }
            }
        });
    }
}
