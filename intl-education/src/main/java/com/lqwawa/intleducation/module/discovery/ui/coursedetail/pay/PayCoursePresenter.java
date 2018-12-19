package com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.MyCourseHelper;
import com.lqwawa.intleducation.factory.helper.UserHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

public class PayCoursePresenter extends BasePresenter<PayCourseContract.View>
    implements PayCourseContract.Presenter{

    public PayCoursePresenter(PayCourseContract.View view) {
        super(view);
    }

    @Override
    public void requestParentChildData() {
        MyCourseHelper.requestParentChildData(new DataSource.Callback<List<ChildrenListVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ChildrenListVo> childrenListVos) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateParentChildView(childrenListVos);
                }
            }
        });
    }

    @Override
    public void requestUserInfoWithUserId(@NonNull String userId) {
        UserHelper.requestUserInfoWithUserId(userId, new DataSource.Callback<UserEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(UserEntity entity) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entity)){
                    view.updateUserInfoWithUserIdView(entity);
                }
            }
        });
    }

    @Override
    public void requestUserInfoWithMembers(@NonNull List<UserModel> members) {
        UserHelper.requestRealNameWithNick(false, members, new DataSource.Callback<List<UserEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }


            @Override
            public void onDataLoaded(List<UserEntity> entities) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateUserInfoWithMembersView(entities);
                }
            }
        });
    }

    @Override
    public void requestCheckCourseBuy(int courseId, @NonNull final String memberId, int type) {
        CourseHelper.requestCheckCourseBuy(courseId, memberId, type, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    // view.showError(strRes);
                    view.updateCheckCourseBuy(memberId,false);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final PayCourseContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateCheckCourseBuy(memberId,aBoolean);
                }
            }
        });
    }
}
