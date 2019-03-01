package com.lqwawa.intleducation.module.tutorial.regist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 申请成为帮辅，注册信息页面的页面的Presenter
 */
public class TutorialRegisterPresenter extends BasePresenter<TutorialRegisterContract.View>
    implements TutorialRegisterContract.Presenter{

    public TutorialRegisterPresenter(TutorialRegisterContract.View view) {
        super(view);
    }

    @Override
    public void requestLocationWithParams(@LocationType.LocationTypeRes int locationType, @Nullable String parentLocationId) {
        TutorialHelper.requestLocationData(parentLocationId, locationType, new DataSource.Callback<LocationEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialRegisterContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(LocationEntity entity) {
                final TutorialRegisterContract.View view = getView();
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
    public void requestTutorialOrgan(boolean onlyIncludeOnline, int pageIndex) {
        SchoolHelper.requestFilterOrganData("", pageIndex, Integer.MAX_VALUE, onlyIncludeOnline, new DataSource.Callback<List<SchoolInfoEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialRegisterContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<SchoolInfoEntity> entities) {
                final TutorialRegisterContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateTutorialOrganView(entities);
                }
            }
        });
    }

    @Override
    public void requestApplyForTutor(@IDType.IDTypeRes int IDType,
                                     @NonNull String IDNumber,
                                     @NonNull String memberId,
                                     @NonNull String tutorName,
                                     @NonNull String tutorOrganId,
                                     @NonNull String tutorOrganName,
                                     @NonNull String markingPrice,
                                     @NonNull String provinceId,
                                     @NonNull String provinceName,
                                     @NonNull String cityId,
                                     @NonNull String cityName,
                                     @NonNull String countyId,
                                     @NonNull String countyName,
                                     @NonNull String workingLife,
                                     @NonNull String educationUrl,
                                     @NonNull String seniorityUrl) {
        TutorialHelper.requestApplyForTutor(
                IDType,
                IDNumber,
                memberId,
                tutorName,
                tutorOrganId,
                tutorOrganName,
                markingPrice,
                provinceId,
                provinceName,
                cityId,
                cityName,
                countyId,
                countyName,
                workingLife,
                educationUrl,
                seniorityUrl,
                new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialRegisterContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final TutorialRegisterContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateApplyForResult(aBoolean);
                }
            }
        });
    }
}
