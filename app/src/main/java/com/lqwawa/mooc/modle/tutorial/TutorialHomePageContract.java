package com.lqwawa.mooc.modle.tutorial;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页的契约类
 */
public interface TutorialHomePageContract {

    interface Presenter extends BaseContract.Presenter {
        // 请求个人信息
        void requestUserInfoWithUserId(@NonNull String userId);
        void requestTutorSubjectList(@NonNull String tutorMemberId);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateUserInfoView(@NonNull UserEntity entity);
        void updateTutorSubjectView(List<String> subjectList);
    }
}
