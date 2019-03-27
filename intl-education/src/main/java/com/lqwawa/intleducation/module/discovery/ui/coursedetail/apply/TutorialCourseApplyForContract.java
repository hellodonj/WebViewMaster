package com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.response.CourseTutorResponseVo;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 课程帮辅申请的契约类
 */
public interface TutorialCourseApplyForContract {

    interface Presenter extends BaseContract.Presenter{

        // 查询省市区数据
        void requestLocationWithParams(@LocationType.LocationTypeRes int locationType, @Nullable String parentLocationId);

        void requestIsTutorCourseByCourseId(@NonNull String memberId,@NonNull String courseId);

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

        void updateCountries(@NonNull List<LocationEntity.LocationBean> countries);
        // 回调指定国家的省份
        void updateProvincesWithChina(@NonNull List<LocationEntity.LocationBean> provinces);
        // 回调指定省份的城市
        void updateCityWithProvince(@NonNull List<LocationEntity.LocationBean> cities);
        // 回调指定城市的区
        void updateDistrictWithCity(@NonNull List<LocationEntity.LocationBean> districts);

        void updateIsCourseTutorByCourseIdView(@NonNull CourseTutorResponseVo.CourseTutorEntity entity);

        void updateApplyForCourseTutor(boolean result);
    }

}
