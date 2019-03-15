package com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;

import java.util.List;

/**
 * @author mrmedici
 * @desc 课程帮辅申请的契约类
 */
public interface TutorialCourseApplyForContract {

    interface Presenter extends BaseContract.Presenter{
        void requestApplyForCourseTutor(@NonNull String memberId,
                                        @NonNull int courseId,
                                        int type,int isOrganTutorStatus,
                                        @NonNull String realName,
                                        @NonNull String markingPrice,
                                        @NonNull String provinceId,
                                        @NonNull String provinceName,
                                        @NonNull String cityId,
                                        @NonNull String cityName,
                                        @NonNull String countyId,
                                        @NonNull String countyName,
                                        boolean isLqOrganTutor);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateApplyForCourseTutor(boolean result);
    }

}
