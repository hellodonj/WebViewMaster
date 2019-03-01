package com.lqwawa.intleducation.module.tutorial.regist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;

import java.util.List;

/**
 * @author mrmedici
 * @desc 申请成为帮辅，注册信息页面的页面
 */
public class TutorialRegisterActivity extends PresenterActivity<TutorialRegisterContract.Presenter>
    implements TutorialRegisterContract.View{

    private static final String CHINA_TEXT = "中国";

    @Override
    protected TutorialRegisterContract.Presenter initPresenter() {
        return new TutorialRegisterPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_register;
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestTutorialOrgan(true,0);
        mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_COUNTRY,"");
    }

    @Override
    public void updateTutorialOrganView(@NonNull List<SchoolInfoEntity> entities) {
        System.out.print(entities);
    }

    @Override
    public void updateCountries(@NonNull List<LocationEntity.LocationBean> countries) {
        if(EmptyUtil.isNotEmpty(countries)){
            for (LocationEntity.LocationBean country : countries) {
                if(CHINA_TEXT.equals(country.getText())){
                    String chinaValue = country.getValue();
                    mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_PROVINCE,chinaValue);
                    break;
                }
            }
        }
    }

    @Override
    public void updateProvincesWithChina(@NonNull List<LocationEntity.LocationBean> provinces) {
        System.out.print(provinces);
    }

    @Override
    public void updateCityWithProvince(@NonNull List<LocationEntity.LocationBean> cities) {

    }

    @Override
    public void updateDistrictWithCity(@NonNull List<LocationEntity.LocationBean> districts) {

    }

    @Override
    public void updateApplyForResult(boolean result) {

    }

    /**
     * 申请成为帮辅，注册信息的入口
     * @param context
     */
    public static void show(@NonNull final Context context){
        Intent intent = new Intent(context,TutorialRegisterActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
