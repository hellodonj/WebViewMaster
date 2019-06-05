package com.lqwawa.mooc.modle.tutorial;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.data.entity.TutorStarLevelEntity;

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

        // 查询是否关注过该帮辅老师
        void requestQueryAddedTutorState(@NonNull String memberId, @NonNull String tutorMemberId,
                                         String classId);

        // 对帮辅(班级帮辅)老师进行关注
        void requestAddTutor(@NonNull String memberId, @NonNull String tutorMemberId,
                             @NonNull String tutorName, String classId);

        //获取帮辅老师星级
        void requestTutorStarLevel(@NonNull String tutorMemberId);
    }

    interface View extends BaseContract.View<Presenter> {
        void updateUserInfoView(@NonNull UserEntity entity);

        void updateTutorSubjectView(List<String> subjectList);

        void updateQueryAddedTutorStateView(boolean added);

        void updateAddTutorView(boolean result);

        void updateTutorStarLevel(TutorStarLevelEntity entity);
    }
}
