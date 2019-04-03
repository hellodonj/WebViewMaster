package com.lqwawa.mooc.modle.tutorial.regist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RegexUtil;
import com.lqwawa.intleducation.common.utils.constant.RegexConstants;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.regist.IDType;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;

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
    public void requestApplyForTutor(@NonNull String name,
                                     @NonNull String phoneNumber,
                                     @NonNull String verificationCode,
                                     @IDType.IDTypeRes int IDType,
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
        final TutorialRegisterContract.View view = getView();
        if(EmptyUtil.isEmpty(name)){
            view.showError(com.lqwawa.intleducation.R.string.label_user_name_hint);
            return;
        }

        /*if(EmptyUtil.isEmpty(phoneNumber)){
            view.showError(com.lqwawa.intleducation.R.string.label_phone_number_hint);
            return;
        }

        if(EmptyUtil.isEmpty(verificationCode)){
            view.showError(com.lqwawa.intleducation.R.string.label_verification_code_hint);
            return;
        }*/

        if(IDType != com.lqwawa.intleducation.module.tutorial.regist.IDType.ID_TYPE_IDENTITY_CARD &&
                IDType != com.lqwawa.intleducation.module.tutorial.regist.IDType.ID_TYPE_PASSPORT){
            return;
        }

        if(EmptyUtil.isEmpty(IDNumber)){
            view.showError(com.lqwawa.intleducation.R.string.label_identify_number_hint);
            return;
        }

        if(IDType == com.lqwawa.intleducation.module.tutorial.regist.IDType.ID_TYPE_IDENTITY_CARD){
            if(!RegexUtil.isIDCard15(IDNumber) && !RegexUtil.isIDCard18(IDNumber)){
                view.showError(com.lqwawa.intleducation.R.string.label_please_input_valid_card_hint);
                return;
            }
        }else if(IDType == com.lqwawa.intleducation.module.tutorial.regist.IDType.ID_TYPE_PASSPORT){
            if(!RegexUtil.isMatch(RegexConstants.REGEX_ID_PASSPORT,IDNumber)){
                view.showError(com.lqwawa.intleducation.R.string.label_please_input_valid_passport_hint);
                return;
            }
        }

        /*if(EmptyUtil.isEmpty(tutorName) ||
                EmptyUtil.isEmpty(tutorOrganId) ||
                EmptyUtil.isEmpty(tutorOrganName)){
            view.showError(com.lqwawa.intleducation.R.string.label_choice_organ_hint);
            return;
        }*/

        if(EmptyUtil.isEmpty(provinceId) ||
                EmptyUtil.isEmpty(provinceName)){
            view.showError(com.lqwawa.intleducation.R.string.label_input_province_tip);
            return;
        }

        /*if(EmptyUtil.isEmpty(cityId) ||
                EmptyUtil.isEmpty(cityName)){
            view.showError(com.lqwawa.intleducation.R.string.label_input_city_tip);
            return;
        }*/

        /*if(EmptyUtil.isEmpty(countyId) ||
                EmptyUtil.isEmpty(countyName)){
            view.showError(com.lqwawa.intleducation.R.string.label_input_district_tip);
            return;
        }*/

        if(EmptyUtil.isEmpty(markingPrice)){
            view.showError(com.lqwawa.intleducation.R.string.label_mark_price_tip);
            return;
        }

        try {
            if (Double.parseDouble(markingPrice) <= 0 || Double.parseDouble(markingPrice) > 100000) {
                view.showError(com.lqwawa.intleducation.R.string.label_please_update_marking_price_range);
                return;
            }
        }catch (Exception ignore){
            view.showError(com.lqwawa.intleducation.R.string.label_please_update_marking_price_range);
            return;
        }

        /*if(EmptyUtil.isEmpty(workingLife)){
            view.showError(com.lqwawa.intleducation.R.string.label_work_period_hint);
            return;
        }

        if(EmptyUtil.isEmpty(educationUrl)){
            view.showError(com.lqwawa.intleducation.R.string.label_upload_academic_certificate_tip);
            return;
        }

        if(EmptyUtil.isEmpty(seniorityUrl)){
            view.showError(com.lqwawa.intleducation.R.string.label_upload_business_certificate_tip);
            return;
        }*/

        TutorialHelper.requestApplyForTutor(
                name,
                phoneNumber,
                verificationCode,
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
