package com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

public interface PayCourseContract {

    interface Presenter extends BaseContract.Presenter{
        void requestParentChildData();
        void requestUserInfoWithMembers(@NonNull List<UserModel> members);
        void requestUserInfoWithUserId(@NonNull String userId);
        void requestCheckCourseBuy(int courseId, @NonNull String memberId, int type);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateParentChildView(@NonNull List<ChildrenListVo> vos);
        void updateUserInfoWithMembersView(@NonNull List<UserEntity> entities);
        void updateUserInfoWithUserIdView(@NonNull UserEntity entity);
        void updateCheckCourseBuy(@NonNull String memberId, boolean result);
    }

}
