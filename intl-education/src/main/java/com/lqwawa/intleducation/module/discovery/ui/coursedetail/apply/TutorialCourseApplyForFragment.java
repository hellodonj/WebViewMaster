package com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterDialogFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.pay.PayDialogNavigator;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 课程帮辅申请的页面
 */
public class TutorialCourseApplyForFragment extends PresenterDialogFragment<TutorialCourseApplyForContract.Presenter>
    implements TutorialCourseApplyForContract.View,View.OnClickListener{

    private static final String CHINA_TEXT = "中国"; //变量

    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";
    private static final String KEY_EXTRA_MEMBER_ID = "KEY_EXTRA_MEMBER_ID";
    private static final String KEY_EXTRA_ORGAN_TUTOR_STATUS = "KEY_EXTRA_ORGAN_TUTOR_STATUS";

    private View mRootView;
    // 批阅价格
    private EditText mEtMarkPrice;
    // 省
    private NiceSpinner mProvinceSpinner;
    // 市
    private NiceSpinner mCitySpinner;
    // 区
    private NiceSpinner mDistrictSpinner;

    // 确定
    private Button mBtnConfirm,mBtnCancel;
    // 当前省份
    private LocationEntity.LocationBean mCurrentProvinceBean;
    // 当前市区
    private LocationEntity.LocationBean mCurrentCityBean;
    private LocationEntity.LocationBean mCurrentDistrictBean;

    private CourseApplyForNavigator mNavigator;

    private String mCourseId;
    private String mMemberId;
    private int mOrganTutorStatus;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(),R.style.AppTheme_Dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = mRootView = inflater.inflate(R.layout.dialog_tutorial_course_apply_for,null);
        mEtMarkPrice = (EditText) view.findViewById(R.id.et_mark_price);
        mProvinceSpinner = (NiceSpinner) view.findViewById(R.id.province_spinner);
        mCitySpinner = (NiceSpinner) view.findViewById(R.id.city_spinner);
        mDistrictSpinner = (NiceSpinner) view.findViewById(R.id.district_spinner);
        mBtnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        mCourseId = arguments.getString(KEY_EXTRA_COURSE_ID);
        mMemberId = arguments.getString(KEY_EXTRA_MEMBER_ID);
        mOrganTutorStatus = arguments.getInt(KEY_EXTRA_ORGAN_TUTOR_STATUS);
        mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_COUNTRY,"");

        mBtnConfirm.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);

    }

    @Override
    protected TutorialCourseApplyForContract.Presenter initPresenter() {
        return new TutorialCourseApplyForPresenter(this);
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
        mProvinceSpinner.attachDataSource(provinces);
        mProvinceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationEntity.LocationBean bean = provinces.get(position);
                mCurrentProvinceBean = bean;
                mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_CITY,bean.getValue());
            }
        });

        fillSpinnerBottom(mProvinceSpinner);
        if(EmptyUtil.isNotEmpty(provinces)) {
            mProvinceSpinner.setSelectedIndex(0);

            int position = mProvinceSpinner.getSelectedIndex();
            LocationEntity.LocationBean bean = provinces.get(position);
            mCurrentProvinceBean = bean;
            mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_CITY,bean.getValue());
        }
    }

    @Override
    public void updateCityWithProvince(@NonNull List<LocationEntity.LocationBean> cities) {
        mCitySpinner.attachDataSource(cities);
        mCitySpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationEntity.LocationBean bean = cities.get(position);
                mCurrentCityBean = bean;
                mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_DISTRICT,bean.getValue());
            }
        });
        fillSpinnerBottom(mCitySpinner);
        if(EmptyUtil.isNotEmpty(cities)) {
            mCitySpinner.setSelectedIndex(0);

            int position = mCitySpinner.getSelectedIndex();
            LocationEntity.LocationBean bean = cities.get(position);
            mCurrentCityBean = bean;
            mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_DISTRICT,bean.getValue());
        }
    }

    @Override
    public void updateDistrictWithCity(@NonNull List<LocationEntity.LocationBean> districts) {
        mDistrictSpinner.attachDataSource(districts);
        mDistrictSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationEntity.LocationBean bean = districts.get(position);
                mCurrentDistrictBean = bean;
            }
        });
        fillSpinnerBottom(mDistrictSpinner);
        if(EmptyUtil.isNotEmpty(districts)) {
            mDistrictSpinner.setSelectedIndex(0);
            int position = mDistrictSpinner.getSelectedIndex();
            LocationEntity.LocationBean bean = districts.get(position);
            mCurrentDistrictBean = bean;
        }
    }

    /**
     * 对NiceSpinner高度计算错误的补偿
     * @param spinner Spinner容器
     */
    private void fillSpinnerBottom(@NonNull NiceSpinner spinner){
        int[] locationOnScreen = new int[2];
        spinner.getLocationOnScreen(locationOnScreen);
        int parentVerticalOffset = locationOnScreen[NiceSpinner.VERTICAL_OFFSET];
        // 应该设置的最大高度
        Display display = getDialog().getWindow().getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        int maxSpinnerHeight = height - parentVerticalOffset - spinner.getMeasuredHeight();
        // 实际设置的高度
        int spinnerHeight = Math.max(maxSpinnerHeight,parentVerticalOffset);
        // 补偿策略
        if(spinnerHeight > maxSpinnerHeight){
            spinner.setDropDownListPaddingBottom(spinnerHeight - maxSpinnerHeight);
        }
    }

    @Override
    public void updateIsCourseTutorByCourseIdView(boolean isCourseTutor) {
        if(isCourseTutor){
            // 申请帮辅成功,再次进入已加入帮辅
            if(EmptyUtil.isNotEmpty(mNavigator)){
                mNavigator.onCourseTutorEnter(isCourseTutor);
                dismiss();
            }
        }else{
            // 申请
            applyForTutor(isCourseTutor);
        }
    }

    private void applyForTutor(boolean isCourseTutor){
        String provinceId = "";
        String provinceName = "";
        if(EmptyUtil.isNotEmpty(mCurrentProvinceBean)){
            provinceId = mCurrentProvinceBean.getValue();
            provinceName = mCurrentProvinceBean.getText();
        }

        String cityId = "";
        String cityName = "";
        if(EmptyUtil.isNotEmpty(mCurrentCityBean)){
            cityId = mCurrentCityBean.getValue();
            cityName = mCurrentCityBean.getText();
        }

        String districtId = "";
        String districtName = "";
        if(EmptyUtil.isNotEmpty(mCurrentDistrictBean)){
            districtId = mCurrentDistrictBean.getValue();
            districtName = mCurrentDistrictBean.getText();
        }

        String markPrice = mEtMarkPrice.getText().toString().trim();

        String userName = UserHelper.getUserInfo().getUserName();
        mPresenter.requestApplyForCourseTutor(
                mMemberId,Integer.parseInt(mCourseId),
                1,mOrganTutorStatus,
                userName,markPrice,
                provinceId,provinceName,
                cityId,cityName,
                districtId,districtName,
                isCourseTutor);
    }

    @Override
    public void updateApplyForCourseTutor(boolean result) {
        if(result){
            UIUtil.showToastSafe(R.string.label_course_tutor_apply_for);
            dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_confirm){
            mPresenter.requestIsTutorCourseByCourseId(mMemberId,mCourseId);
        }else if(viewId == R.id.btn_cancel){
            // 取消
            dismiss();
        }
    }

    /**
     * 设置确定回调的监听
     * @param navigator 监听对象
     */
    public void setNavigator(@NonNull CourseApplyForNavigator navigator){
        this.mNavigator = navigator;
    }

    /**
     * 私有的show方法
     * @param manager Fragment管理器
     */
    public static void show(@NonNull FragmentManager manager,
                            @NonNull String memberId,
                            @NonNull String courseId,
                            int isOrganTutorStatus,
                            @NonNull CourseApplyForNavigator navigator) {
        TutorialCourseApplyForFragment fragment = new TutorialCourseApplyForFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_MEMBER_ID,memberId);
        bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
        bundle.putInt(KEY_EXTRA_ORGAN_TUTOR_STATUS,isOrganTutorStatus);
        fragment.setArguments(bundle);
        fragment.setNavigator(navigator);
        fragment.show(manager,TutorialCourseApplyForFragment.class.getName());
    }
}
