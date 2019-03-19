package com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 课程帮辅申请的Presenter
 */
public class TutorialCourseApplyForPresenter extends BasePresenter<TutorialCourseApplyForContract.View>
        implements TutorialCourseApplyForContract.Presenter {


    public TutorialCourseApplyForPresenter(TutorialCourseApplyForContract.View view) {
        super(view);
    }

    @Override
    public void requestLocationWithParams(@LocationType.LocationTypeRes int locationType, @Nullable String parentLocationId) {
        TutorialHelper.requestLocationData(parentLocationId, locationType, new DataSource.Callback<LocationEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialCourseApplyForContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(LocationEntity entity) {
                final TutorialCourseApplyForContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entity)){
                    int parentLocationType = entity.getParentLocationType();
                    List<LocationEntity.LocationBean> locationList = entity.getLocationList();
                    switch (parentLocationType){
                        case LocationType.LOCATION_TYPE_COUNTRY:
                            // 当前请求的是国家数据
                            view.updateCountries(locationList);
                            break;
                        case LocationType.LOCATION_TYPE_PROVINCE:
                            // 当前请求的是省份数据
                            view.updateProvincesWithChina(locationList);
                            break;
                        case LocationType.LOCATION_TYPE_CITY:
                            // 当前请求的是城市数据
                            view.updateCityWithProvince(locationList);
                            break;
                        case LocationType.LOCATION_TYPE_DISTRICT:
                            // 当前请求的是区数据
                            view.updateDistrictWithCity(locationList);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void requestIsTutorCourseByCourseId(@NonNull String memberId, @NonNull String courseId) {
        CourseHelper.isTutorCourseBycourseId(memberId, courseId, new DataSource.SucceedCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorialCourseApplyForContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateIsCourseTutorByCourseIdView(aBoolean);
                }
            }
        });
    }

    @Override
    public void requestApplyForCourseTutor(@NonNull String memberId,
                                           @NonNull int courseId,
                                           int type, int isOrganTutorStatus,
                                           @NonNull String realName, @NonNull String markingPrice,
                                           @NonNull String provinceId, @NonNull String provinceName,
                                           @NonNull String cityId, @NonNull String cityName,
                                           @NonNull String countyId, @NonNull String countyName,
                                           boolean isLqOrganTutor) {

        final TutorialCourseApplyForContract.View view = getView();

        if(EmptyUtil.isEmpty(provinceId) ||
                EmptyUtil.isEmpty(provinceName)){
            view.showError(R.string.label_input_province_tip);
            return;
        }

        if(EmptyUtil.isEmpty(cityId) ||
                EmptyUtil.isEmpty(cityName)){
            view.showError(R.string.label_input_city_tip);
            return;
        }

        if(EmptyUtil.isEmpty(countyId) ||
                EmptyUtil.isEmpty(countyName)){
            view.showError(R.string.label_input_district_tip);
            return;
        }

        if(EmptyUtil.isEmpty(markingPrice)){
            view.showError(R.string.label_mark_price_tip);
            return;
        }

        CourseHelper.requestApplyForCourseTutor(memberId, courseId,
                type, isOrganTutorStatus,
                realName, markingPrice,
                provinceId, provinceName,
                cityId, cityName,
                countyId, countyName,
                isLqOrganTutor, new DataSource.Callback<Boolean>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        final TutorialCourseApplyForContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.showError(strRes);
                        }
                    }

                    @Override
                    public void onDataLoaded(Boolean aBoolean) {
                        final TutorialCourseApplyForContract.View view = getView();
                        if(EmptyUtil.isNotEmpty(view)){
                            view.updateApplyForCourseTutor(aBoolean);
                        }
                    }
                });
    }
}
