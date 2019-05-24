package com.lqwawa.intleducation.module.discovery.ui.coursedetail.apply;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterDialogFragment;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.response.CourseTutorResponseVo;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.factory.data.model.user.UserModel;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
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
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_ORGAN_TUTOR_STATUS = "KEY_EXTRA_ORGAN_TUTOR_STATUS";

    private View mRootView;
    private TextView mTvLabelMarkingPrice;
    private TextView mTvLabelAddress;
    // 批阅价格
    private EditText mEtMarkPrice;
    // 省
    private NiceSpinner mProvinceSpinner;
    // 市
    private NiceSpinner mCitySpinner;
    // 区
    private NiceSpinner mDistrictSpinner;

    private TextView mTvLabelAddress2;
    private Spinner mProvinceSpinner2;
    private Spinner mCitySpinner2;
    private Spinner mDistrictSpinner2;

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
    private String mSchoolId;
    private int mOrganTutorStatus;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(),R.style.AppTheme_Dialog_InputMode);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = mRootView = inflater.inflate(R.layout.dialog_tutorial_course_apply_for,null);
        mTvLabelMarkingPrice = (TextView) mRootView.findViewById(R.id.tv_label_marking_price);
        mTvLabelAddress = (TextView) mRootView.findViewById(R.id.tv_label_address);
        mTvLabelAddress2 = (TextView) mRootView.findViewById(R.id.tv_label_address_2);
        fillLabelWarning(mTvLabelAddress,"*",UIUtil.getColor(R.color.colorDarkRed));
        fillLabelWarning(mTvLabelMarkingPrice,"*",UIUtil.getColor(R.color.colorDarkRed));
        fillLabelWarning(mTvLabelAddress2,"*",UIUtil.getColor(R.color.colorDarkRed));
        mEtMarkPrice = (EditText) view.findViewById(R.id.et_mark_price);
        mProvinceSpinner = (NiceSpinner) view.findViewById(R.id.province_spinner);
        mCitySpinner = (NiceSpinner) view.findViewById(R.id.city_spinner);
        mDistrictSpinner = (NiceSpinner) view.findViewById(R.id.district_spinner);
        mProvinceSpinner2 = (Spinner) view.findViewById(R.id.province_spinner_2);
        mCitySpinner2 = (Spinner) view.findViewById(R.id.city_spinner_2);
        mDistrictSpinner2 = (Spinner) view.findViewById(R.id.district_spinner_2);
        mBtnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) view.findViewById(R.id.btn_cancel);
        return view;
    }

    /**
     * 设置必填项
     * @param view 标签
     * @param charSequence *
     * @param color 颜色值
     */
    private void fillLabelWarning(@NonNull TextView view, CharSequence charSequence,@ColorInt int color){
        String text = view.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        spannableString.setSpan(span,text.indexOf(charSequence.toString()),text.length() - 1,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        view.setText(spannableString);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        mCourseId = arguments.getString(KEY_EXTRA_COURSE_ID);
        mMemberId = arguments.getString(KEY_EXTRA_MEMBER_ID);
        mSchoolId = arguments.getString(KEY_EXTRA_SCHOOL_ID);
        mOrganTutorStatus = arguments.getInt(KEY_EXTRA_ORGAN_TUTOR_STATUS);
        mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_COUNTRY,"");

        mBtnConfirm.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProvinceSpinner2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mProvinceSpinner2.getMeasuredWidth();
                int height = mProvinceSpinner2.getMeasuredHeight();
                mProvinceSpinner2.setDropDownWidth(width);
                // mProvinceSpinner2.setDropDownVerticalOffset(height);

                mProvinceSpinner2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mCitySpinner2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mCitySpinner2.getMeasuredWidth();
                int height = mCitySpinner2.getMeasuredHeight();
                mCitySpinner2.setDropDownWidth(width);
                // mCitySpinner2.setDropDownVerticalOffset(height);

                mCitySpinner2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mDistrictSpinner2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = mDistrictSpinner2.getMeasuredWidth();
                int height = mDistrictSpinner2.getMeasuredHeight();
                mDistrictSpinner2.setDropDownWidth(width);
                // mDistrictSpinner2.setDropDownVerticalOffset(height);

                mDistrictSpinner2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        /*View provinceView = mProvinceSpinner2.getChildAt(0);
        if(provinceView instanceof TextView){
            ((TextView)provinceView).setHint(R.string.label_province);
        }

        View cityView = mCitySpinner2.getChildAt(0);
        if(cityView instanceof TextView){
            ((TextView)cityView).setHint(R.string.label_city);
        }

        View districtView = mDistrictSpinner2.getChildAt(0);
        if(districtView instanceof TextView){
            ((TextView)districtView).setHint(R.string.label_district);
        }*/

        // 设置自定义宽度必须在Dialog show之后
        Dialog dialog = getDialog();
        WindowManager windowManager = dialog.getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.width = (int)(display.getWidth() * 0.9);
        dialog.getWindow().setAttributes(attributes);
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
        /*mProvinceSpinner.attachDataSource(provinces);
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
        }*/


        ArrayAdapter<LocationEntity.LocationBean> adapter = new ArrayAdapter<>(getContext(),R.layout.spinner_select_layout,provinces);
        mProvinceSpinner2.setAdapter(adapter);
        mProvinceSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocationEntity.LocationBean bean = adapter.getItem(position);
                mCurrentProvinceBean = bean;
                mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_CITY,bean.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void updateCityWithProvince(@NonNull List<LocationEntity.LocationBean> cities) {
        /*if(EmptyUtil.isNotEmpty(cities)){
            mCitySpinner.setEnabled(true);
            mDistrictSpinner.setEnabled(true);
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
        }else{
            mCitySpinner.setEnabled(false);
            mCitySpinner.setText("");
            mCurrentCityBean = null;
            mDistrictSpinner.setEnabled(false);
            mDistrictSpinner.setText("");
            mCurrentDistrictBean = null;
        }*/

        if(EmptyUtil.isNotEmpty(cities)) {
            mCitySpinner2.setEnabled(true);
            mDistrictSpinner2.setEnabled(true);
            ArrayAdapter<LocationEntity.LocationBean> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_select_layout, cities);
            mCitySpinner2.setAdapter(adapter);
            mCitySpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    LocationEntity.LocationBean bean = adapter.getItem(position);
                    mCurrentCityBean = bean;
                    mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_DISTRICT, bean.getValue());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            List<LocationEntity.LocationBean> empties = new ArrayList<>();
            LocationEntity.LocationBean tip = new LocationEntity.LocationBean();
            tip.setText(getString(R.string.label_city));
            empties.add(tip);
            ArrayAdapter<LocationEntity.LocationBean> cityAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_select_layout, empties);
            mCitySpinner2.setAdapter(cityAdapter);
            mCitySpinner2.setOnItemSelectedListener(null);

            empties = new ArrayList<>();
            tip = new LocationEntity.LocationBean();
            tip.setText(getString(R.string.label_district));
            empties.add(tip);
            ArrayAdapter<LocationEntity.LocationBean> districtAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_select_layout, empties);
            mDistrictSpinner2.setAdapter(districtAdapter);
            mDistrictSpinner2.setOnItemSelectedListener(null);

            mCitySpinner2.setEnabled(false);
            mDistrictSpinner2.setEnabled(false);
            mCurrentCityBean = null;
            mCurrentDistrictBean = null;
        }
    }

    @Override
    public void updateDistrictWithCity(@NonNull List<LocationEntity.LocationBean> districts) {
        /*if(EmptyUtil.isNotEmpty(districts)) {
            mDistrictSpinner.setEnabled(true);
            mDistrictSpinner.attachDataSource(districts);
            mDistrictSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LocationEntity.LocationBean bean = districts.get(position);
                    mCurrentDistrictBean = bean;
                }
            });
            fillSpinnerBottom(mDistrictSpinner);
            if (EmptyUtil.isNotEmpty(districts)) {
                mDistrictSpinner.setSelectedIndex(0);
                int position = mDistrictSpinner.getSelectedIndex();
                LocationEntity.LocationBean bean = districts.get(position);
                mCurrentDistrictBean = bean;
            }
        }else{
            mDistrictSpinner.setEnabled(false);
            mDistrictSpinner.setText("");
            mCurrentDistrictBean = null;
        }*/

        if(EmptyUtil.isNotEmpty(districts)) {
            mDistrictSpinner2.setEnabled(true);
            ArrayAdapter<LocationEntity.LocationBean> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_select_layout, districts);
            mDistrictSpinner2.setAdapter(adapter);
            mDistrictSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    LocationEntity.LocationBean bean = adapter.getItem(position);
                    mCurrentDistrictBean = bean;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }else{
            List<LocationEntity.LocationBean> empties = new ArrayList<>();
            LocationEntity.LocationBean tip = new LocationEntity.LocationBean();
            tip.setText(getString(R.string.label_district));
            empties.add(tip);
            ArrayAdapter<LocationEntity.LocationBean> districtAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_select_layout, empties);
            mDistrictSpinner2.setAdapter(districtAdapter);
            mDistrictSpinner2.setOnItemSelectedListener(null);
            mDistrictSpinner2.setEnabled(false);
            mCurrentDistrictBean = null;
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
    public void updateIsCourseTutorByCourseIdView(@NonNull CourseTutorResponseVo.CourseTutorEntity entity) {
        boolean isCourseTutor = entity.isTutorCourse();
        int isOrganTutorStatus = entity.getIsOrganTutorStatus();
        if(isCourseTutor){
            // 申请帮辅成功,再次进入已加入帮辅
            if(EmptyUtil.isNotEmpty(mNavigator)){
                mNavigator.onCourseTutorEnter(isCourseTutor);
                dismiss();
            }
        }else{
            // 申请
            applyForTutor(isOrganTutorStatus,isCourseTutor);
        }
    }

    private void applyForTutor(int isOrganTutorStatus,boolean isCourseTutor){
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
                1,isOrganTutorStatus,
                userName,markPrice,
                provinceId,provinceName,
                cityId,cityName,
                districtId,districtName,
                MainApplication.isAssistant(mSchoolId));
    }

    @Override
    public void updateApplyForCourseTutor(boolean result) {
        // UIUtil.showToastSafe(R.string.label_course_tutor_apply_for);
        if(result) {
            if (EmptyUtil.isNotEmpty(mNavigator)) {
                mNavigator.onCourseTutorEnter(true);
            }
        }
        dismiss();
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
                            @NonNull String schoolId,
                            int isOrganTutorStatus,
                            @NonNull CourseApplyForNavigator navigator) {
        // 获取当前是否已经进入帮辅的状态
        CourseHelper.isTutorCourseBycourseId(memberId, courseId, new DataSource.SucceedCallback<CourseTutorResponseVo.CourseTutorEntity>() {
            @Override
            public void onDataLoaded(CourseTutorResponseVo.CourseTutorEntity entity) {
                int interfaceStatus = entity.getIsOrganTutorStatus();
                boolean isTutorCourse = entity.isTutorCourse();
                if(isTutorCourse){
                    if(EmptyUtil.isNotEmpty(navigator)){
                        navigator.onCourseTutorEnter(true);
                    }
                }else{
                    if(interfaceStatus == -1 && false) {
                        // 别的机构的课程
                        TutorialCourseApplyForFragment fragment = new TutorialCourseApplyForFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_EXTRA_MEMBER_ID, memberId);
                        bundle.putString(KEY_EXTRA_COURSE_ID, courseId);
                        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
                        bundle.putInt(KEY_EXTRA_ORGAN_TUTOR_STATUS, isOrganTutorStatus);
                        fragment.setArguments(bundle);
                        fragment.setNavigator(navigator);
                        fragment.show(manager, TutorialCourseApplyForFragment.class.getName());
                    }else{
                        // V5.14最后版本更改
                        String userName = UserHelper.getUserInfo().getUserName();
                        CourseHelper.requestApplyForCourseTutor(
                                memberId, Integer.parseInt(courseId),
                                1, isOrganTutorStatus,
                                userName, "",
                                "", "",
                                "", "",
                                "", "",
                                MainApplication.isAssistant(schoolId), new DataSource.Callback<Boolean>() {
                                    @Override
                                    public void onDataNotAvailable(int strRes) {

                                    }

                                    @Override
                                    public void onDataLoaded(Boolean aBoolean) {
                                        if(aBoolean){
                                            UIUtil.showToastSafe(R.string.label_apply_success);
                                            // 申请成功
                                            if(EmptyUtil.isNotEmpty(navigator)){
                                                navigator.onCourseTutorEnter(true);
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}
