package com.lqwawa.mooc.modle.tutorial.regist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.tutorial.regist.IDType;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 申请成为帮辅，注册信息页面的契约类
 */
public interface TutorialRegisterContract {

    interface Presenter extends BaseContract.Presenter {
        // 查询省市区数据
        void requestLocationWithParams(@LocationType.LocationTypeRes int locationType, @Nullable String parentLocationId);
        // 查询线下机构数据
        void requestTutorialOrgan(boolean onlyIncludeOnline, int pageIndex);

        /**
         * 学生申请成为机构助教
         * @param name 姓名
         * @param phoneNumber 手机号码
         * @param verificationCode 验证码
         * @param IDType 证件类型 1 身份证 2 护照
         * @param IDNumber 身份证或者护照号码
         * @param memberId 助教memberId
         * @param tutorName 助教姓名
         * @param tutorOrganId 选择的机构Id
         * @param tutorOrganName 选择的机构名称
         * @param markingPrice 批阅价格
         * @param provinceId 省Id
         * @param provinceName 省名称
         * @param cityId 市Id
         * @param cityName 市名称
         * @param countyId 区Id
         * @param countyName 区名称
         * @param workingLife 工作年限
         * @param educationUrl 学历认证
         * @param seniorityUrl 资历认证
         */
        void requestApplyForTutor(@NonNull String name,
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
                                  @NonNull String seniorityUrl);
    }

    interface View extends BaseContract.View<Presenter>{
        void updateCountries(@NonNull List<LocationEntity.LocationBean> countries);
        // 回调指定国家的省份
        void updateProvincesWithChina(@NonNull List<LocationEntity.LocationBean> provinces);
        // 回调指定省份的城市
        void updateCityWithProvince(@NonNull List<LocationEntity.LocationBean> cities);
        // 回调指定城市的区
        void updateDistrictWithCity(@NonNull List<LocationEntity.LocationBean> districts);
        void updateTutorialOrganView(@NonNull List<SchoolInfoEntity> entities);
        // 回调申请结果
        void updateApplyForResult(boolean result);
    }
}
