package com.lqwawa.mooc.modle.tutorial.regist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.ImagePopupView;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.module.tutorial.regist.IDType;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.mooc.factory.data.entity.MediaEntity;
import com.lqwawa.mooc.modle.tutorial.audit.TutorialAuditActivity;
import com.osastudio.common.utils.PhotoUtils;

import org.angmarch.views.NiceSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 申请成为帮辅，注册信息页面的页面
 */
public class TutorialRegisterActivity extends PresenterActivity<TutorialRegisterContract.Presenter>
        implements TutorialRegisterContract.View, View.OnClickListener {

    private static final String CHINA_TEXT = "中国"; //变量
    private int mediaType = MediaType.PICTURE; //图片类型

    private TopBar mTopBar;
    private TextView mTvLabelName;
    private TextView mTvLabelIdenitifier;
    private TextView mTvLabelIdenitifierNumber;
    private TextView mTvLabelAddress;
    private TextView mTvLabelChoiceOrgan;
    private TextView mTvLabelMarkingPrice;

    // 姓名
    private EditText mEtName;
    // 手机号码
    private EditText mEtPhoneNumber;
    // 验证码
    private EditText mEtVerificationCode;
    // 证件类型容器
    private FrameLayout mIdentifyLayout;
    private LinearLayout mIdentifyContentLayout;
    private TextView mTvTypeName;
    // 证件号码
    private EditText mEtIdentifyNumber;
    // 批阅价格
    private EditText mEtMarkPrice;
    // 工作年限
    private EditText mEtWorkLimit;
    // 机构
    private NiceSpinner mOrganSpinner;
    // 省
    private NiceSpinner mProvinceSpinner;
    // 市
    private NiceSpinner mCitySpinner;
    // 区
    private NiceSpinner mDistrictSpinner;
    // 个人学历证书
    private TextView mBtnCertificateUpload;
    // 行业资历认证证书
    private TextView mBtnBusinessUpload;

    private ImageView mIvThumbnailImage1;
    private ImageView mIvThumbnailImage2;

    // 提交
    private Button mBtnSubmit;

    // 当前选择的机构
    private SchoolInfoEntity mCurrentOrganEntity;
    // 当前省份
    private LocationEntity.LocationBean mCurrentProviceBean;
    // 当前市区
    private LocationEntity.LocationBean mCurrentCityBean;
    private LocationEntity.LocationBean mCurrentDistrictBean;

    // 记录提交的文件地址
    private SparseArray<String> mUrlArray = new SparseArray<>();

    @Override
    protected TutorialRegisterContract.Presenter initPresenter() {
        return new TutorialRegisterPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_tutorial_register;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mTopBar.setTitle(R.string.title_fill_information);

        mTvLabelName = (TextView) findViewById(R.id.tv_label_name);
        mTvLabelIdenitifier = (TextView) findViewById(R.id.tv_label_identifier);
        mTvLabelIdenitifierNumber = (TextView) findViewById(R.id.tv_label_identifier_number);
        mTvLabelAddress = (TextView) findViewById(R.id.tv_label_address);
        mTvLabelChoiceOrgan = (TextView) findViewById(R.id.tv_label_choice_organ);
        mTvLabelMarkingPrice = (TextView) findViewById(R.id.tv_label_marking_price);
        fillLabelWarning(mTvLabelName,"*",UIUtil.getColor(R.color.colorDarkRed));
        fillLabelWarning(mTvLabelIdenitifier,"*",UIUtil.getColor(R.color.colorDarkRed));
        fillLabelWarning(mTvLabelIdenitifierNumber,"*",UIUtil.getColor(R.color.colorDarkRed));
        fillLabelWarning(mTvLabelAddress,"*",UIUtil.getColor(R.color.colorDarkRed));
        fillLabelWarning(mTvLabelChoiceOrgan,"*",UIUtil.getColor(R.color.colorDarkRed));
        fillLabelWarning(mTvLabelMarkingPrice,"*",UIUtil.getColor(R.color.colorDarkRed));

        mEtName = (EditText) findViewById(R.id.et_nick_name);
        mEtPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        mEtVerificationCode = (EditText) findViewById(R.id.et_verification_code);
        mIdentifyLayout = (FrameLayout) findViewById(R.id.identify_layout);
        mIdentifyContentLayout = (LinearLayout) findViewById(R.id.identify_content_layout);
        mTvTypeName = (TextView) findViewById(R.id.tv_type_name);
        mIdentifyLayout.setOnClickListener(this);
        // 默认身份证
        mIdentifyLayout.setTag(IDType.ID_TYPE_IDENTITY_CARD);
        mEtIdentifyNumber = (EditText) findViewById(R.id.et_identify_number);
        mEtMarkPrice = (EditText) findViewById(R.id.et_mark_price);
        mEtWorkLimit = (EditText) findViewById(R.id.et_work_limit);
        mEtWorkLimit.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (EmptyUtil.isEmpty(s) || s.toString().startsWith("0")) {
                    // 0开头
                    mEtWorkLimit.getText().clear();
                }
            }
        });
        mOrganSpinner = (NiceSpinner) findViewById(R.id.organ_spinner);
        mProvinceSpinner = (NiceSpinner) findViewById(R.id.province_spinner);
        mCitySpinner = (NiceSpinner) findViewById(R.id.city_spinner);
        mDistrictSpinner = (NiceSpinner) findViewById(R.id.district_spinner);
        mBtnCertificateUpload = (TextView) findViewById(R.id.btn_certificate_upload);
        mBtnBusinessUpload = (TextView) findViewById(R.id.tv_business_upload);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mIvThumbnailImage1 = (ImageView) findViewById(R.id.iv_thumbnail_image1);
        mIvThumbnailImage2 = (ImageView) findViewById(R.id.iv_thumbnail_image2);
        mBtnCertificateUpload.setOnClickListener(this);
        mBtnBusinessUpload.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
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
    protected void initData() {
        super.initData();
        mPresenter.requestTutorialOrgan(true, 0);
        mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_COUNTRY, "");
    }

    @Override
    public void updateTutorialOrganView(@NonNull List<SchoolInfoEntity> entities) {
        mOrganSpinner.attachDataSource(entities);
        mOrganSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentOrganEntity = entities.get(position);
            }
        });
        fillSpinnerBottom(mOrganSpinner);
        if (EmptyUtil.isNotEmpty(entities)) {
            mOrganSpinner.setSelectedIndex(0);
            int position = mOrganSpinner.getSelectedIndex();
            mCurrentOrganEntity = entities.get(position);
        }
    }

    @Override
    public void updateCountries(@NonNull List<LocationEntity.LocationBean> countries) {
        if (EmptyUtil.isNotEmpty(countries)) {
            for (LocationEntity.LocationBean country : countries) {
                if (CHINA_TEXT.equals(country.getText())) {
                    String chinaValue = country.getValue();
                    mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_PROVINCE, chinaValue);
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
                mCurrentProviceBean = bean;
                mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_CITY, bean.getValue());
            }
        });

        fillSpinnerBottom(mProvinceSpinner);
        if (EmptyUtil.isNotEmpty(provinces)) {
            mProvinceSpinner.setSelectedIndex(0);

            int position = mProvinceSpinner.getSelectedIndex();
            LocationEntity.LocationBean bean = provinces.get(position);
            mCurrentProviceBean = bean;
            mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_CITY, bean.getValue());
        }
    }

    @Override
    public void updateCityWithProvince(@NonNull List<LocationEntity.LocationBean> cities) {
        if(EmptyUtil.isNotEmpty(cities)){
            mCitySpinner.setEnabled(true);
            mDistrictSpinner.setEnabled(true);
            mCitySpinner.attachDataSource(cities);
            mCitySpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LocationEntity.LocationBean bean = cities.get(position);
                    mCurrentCityBean = bean;
                    mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_DISTRICT, bean.getValue());
                }
            });
            fillSpinnerBottom(mCitySpinner);
            if (EmptyUtil.isNotEmpty(cities)) {
                mCitySpinner.setSelectedIndex(0);

                int position = mCitySpinner.getSelectedIndex();
                LocationEntity.LocationBean bean = cities.get(position);
                mCurrentCityBean = bean;
                mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_DISTRICT, bean.getValue());
            }
        }else{
            mCitySpinner.setEnabled(false);
            mCitySpinner.setText("");
            mCurrentCityBean = null;
            mDistrictSpinner.setEnabled(false);
            mDistrictSpinner.setText("");
            mCurrentDistrictBean = null;
        }
    }

    @Override
    public void updateDistrictWithCity(@NonNull List<LocationEntity.LocationBean> districts) {
        if(EmptyUtil.isNotEmpty(districts)){
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
        }
    }

    @Override
    public void updateApplyForResult(boolean result) {
        // 不管成功与否都隐藏对话框
        hideLoading();
        if (result) {
            TutorialAuditActivity.show(this, result);
            finish();
        }
    }


    /**
     * 对NiceSpinner高度计算错误的补偿
     *
     * @param spinner Spinner容器
     */
    private void fillSpinnerBottom(@NonNull NiceSpinner spinner) {
        int[] locationOnScreen = new int[2];
        spinner.getLocationOnScreen(locationOnScreen);
        int parentVerticalOffset = locationOnScreen[NiceSpinner.VERTICAL_OFFSET];
        // 应该设置的最大高度
        int maxSpinnerHeight = DisplayUtil.getMobileHeight(this) - parentVerticalOffset - spinner.getMeasuredHeight();
        // 实际设置的高度
        int spinnerHeight = Math.max(maxSpinnerHeight, parentVerticalOffset);
        // 补偿策略
        if (spinnerHeight > maxSpinnerHeight) {
            spinner.setDropDownListPaddingBottom(spinnerHeight - maxSpinnerHeight);
        }
    }

    private IdentifyPopupWindow mPopupWindow;

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.btn_certificate_upload) {
            // 个人学历证书选择上传
            mBtnBusinessUpload.setActivated(false);
            mBtnCertificateUpload.setActivated(true);
            showImagePopupView();
        } else if (viewId == R.id.tv_business_upload) {
            // 行业资历认证证书上传
            mBtnBusinessUpload.setActivated(true);
            mBtnCertificateUpload.setActivated(false);
            showImagePopupView();
        } else if (viewId == R.id.btn_submit) {
            // 提交申请
            commitApplyFor();
        } else if (viewId == R.id.identify_layout) {
            if (EmptyUtil.isNotEmpty(mPopupWindow)) {
                if (!mPopupWindow.isShowing()) {
                    mPopupWindow.showAsDropDown(mTvTypeName);
                }
            } else {
                // 弹出证件类型选择
                int width = ViewGroup.LayoutParams.WRAP_CONTENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (mTvTypeName.getMeasuredWidth() > 0) {
                    width = mTvTypeName.getMeasuredWidth();
                }
                final IdentifyPopupWindow popupWindow = new IdentifyPopupWindow(width, height, new IdentifyPopupWindow.OnChoiceListener() {
                    @Override
                    public void onChoiceMenu(@NonNull IdentifyPopupWindow.IdentifyEntity entity) {
                        mIdentifyLayout.setTag(entity.getIdentifyId());
                        mTvTypeName.setText(entity.getIdentifyName());
                    }
                });
                popupWindow.showAsDropDown(mTvTypeName);
                mPopupWindow = popupWindow;
            }
        }
    }

    private void commitApplyFor() {
        String name = mEtName.getText().toString().trim();
        String phoneNumber = mEtPhoneNumber.getText().toString().trim();
        String verificationCode = mEtVerificationCode.getText().toString().trim();
        int idType = (int) mIdentifyLayout.getTag();
        String identifyNumber = mEtIdentifyNumber.getText().toString().trim();
        String organId = "";
        String organName = "";
        if (EmptyUtil.isNotEmpty(mCurrentOrganEntity)) {
            organId = mCurrentOrganEntity.getId();
            organName = mCurrentOrganEntity.getSName();
        }

        String provinceId = "";
        String provinceName = "";
        if (EmptyUtil.isNotEmpty(mCurrentProviceBean)) {
            provinceId = mCurrentProviceBean.getValue();
            provinceName = mCurrentProviceBean.getText();
        }

        String cityId = "";
        String cityName = "";
        if (EmptyUtil.isNotEmpty(mCurrentCityBean)) {
            cityId = mCurrentCityBean.getValue();
            cityName = mCurrentCityBean.getText();
        }

        String districtId = "";
        String districtName = "";
        if (EmptyUtil.isNotEmpty(mCurrentDistrictBean)) {
            districtId = mCurrentDistrictBean.getValue();
            districtName = mCurrentDistrictBean.getText();
        }

        String markPrice = mEtMarkPrice.getText().toString().trim();
        String workLife = mEtWorkLimit.getText().toString().trim();

        String certificateUrl = mUrlArray.get(mBtnCertificateUpload.getId(), "");
        String businessUrl = mUrlArray.get(mBtnBusinessUpload.getId(), "");

        showLoading();
        mPresenter.requestApplyForTutor(name, phoneNumber, verificationCode,
                idType, identifyNumber,
                UserHelper.getUserId(), UserHelper.getUserName(),
                organId, organName, markPrice,
                provinceId, provinceName,
                cityId, cityName,
                districtId, districtName,
                workLife, certificateUrl, businessUrl);
    }

    private void showImagePopupView() {
        ImagePopupView imagePopupView = new ImagePopupView(this, true);
        View view = getLayoutInflater().inflate(R.layout.activity_tutorial_register, null);
        imagePopupView.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String userId = UserHelper.getUserId();

        switch (requestCode) {
            case ActivityUtils.REQUEST_CODE_TAKE_PHOTO:
                if (this == null) {
                    return;
                }

                String urlFolder = com.galaxyschool.app.wawaschool.common.Utils.IMAGE_FOLDER;
                String url = urlFolder + com.galaxyschool.app.wawaschool.common.Utils.TEMP_IMAGE_NAME;

                File fileFolder = new File(urlFolder);
                if (!fileFolder.exists()) {
                    fileFolder.mkdirs();
                }

                uploadAvatar(userId, url);
                break;
            case ActivityUtils.REQUEST_CODE_FETCH_PHOTO:
                if (data != null) {
                    String photoPath = PhotoUtils.getImageAbsolutePath(this, data.getData());

                    if (TextUtils.isEmpty(photoPath)) {
                        return;
                    }

                    uploadAvatar(userId, photoPath);
                }
                break;
            default:
                break;

        }
    }

    /**
     * 上传文件
     *
     * @param memberId 当前的用户Id
     * @param filePath 文件路径
     */
    private void uploadAvatar(String memberId, String filePath) {
        if (TextUtils.isEmpty(memberId) || TextUtils.isEmpty(filePath)) {
            return;
        }

        // 上传
        UserInfo userInfo = getUserInfo();

        if (userInfo == null) {
            return;
        }


        UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, mediaType, 1);
        List<String> paths = new ArrayList<>();
        StringBuilder names = new StringBuilder();
        paths.add(filePath);
        uploadParameter.setPaths(paths);
        if (filePath.contains("/")) {
            names.append(filePath.substring(filePath.lastIndexOf("/") + 1) + ";");
            uploadParameter.setFileName(names.toString());
        }

        showLoading();
        UploadUtils.uploadMedia(this, uploadParameter, new CallbackListener() {
            @Override
            public void onBack(Object result) {
                hideLoading();
                TypeReference<ResponseVo<List<MediaEntity>>> typeReference = new TypeReference<ResponseVo<List<MediaEntity>>>() {
                };
                ResponseVo<List<MediaEntity>> responseVo = JSON.parseObject(JSON.toJSONString(result), typeReference);
                if (responseVo.isSucceed()) {
                    List<MediaEntity> data = responseVo.getData();
                    if (EmptyUtil.isNotEmpty(data)) {
                        MediaEntity entity = data.get(0);
                        String resourceUrl = entity.getResourceurl();
                        runOnUiThread(new UploadImageCallback(resourceUrl));
                    }
                }else{
                    // 上传失败
                    if(mBtnBusinessUpload.isActivated()){
                        mIvThumbnailImage2.setVisibility(View.GONE);
                    }else if (mBtnCertificateUpload.isActivated()) {
                        mIvThumbnailImage1.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private class UploadImageCallback implements Runnable {

        private String url;

        public UploadImageCallback(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            if (!isFinishing()) {
                UIUtil.showToastSafe(R.string.label_upload_completed);
                if (mBtnBusinessUpload.isActivated()) {
                    mBtnBusinessUpload.setText(R.string.label_upload_again);
                    int viewId = mBtnBusinessUpload.getId();
                    mIvThumbnailImage2.setVisibility(View.VISIBLE);
                    LQwawaImageUtil.loadCommonIcon(TutorialRegisterActivity.this,mIvThumbnailImage2,url);
                    mUrlArray.put(viewId, url);
                } else if (mBtnCertificateUpload.isActivated()) {
                    mBtnCertificateUpload.setText(R.string.label_upload_again);
                    int viewId = mBtnCertificateUpload.getId();
                    mIvThumbnailImage1.setVisibility(View.VISIBLE);
                    LQwawaImageUtil.loadCommonIcon(TutorialRegisterActivity.this,mIvThumbnailImage1,url);
                    mUrlArray.put(viewId, url);
                }
            }
        }
    }

    public UserInfo getUserInfo() {
        if (getApplication() != null) {
            return ((MyApplication) getApplication()).getUserInfo();
        }
        return null;
    }

    /**
     * 申请成为帮辅，注册信息的入口
     *
     * @param context
     */
    public static void show(@NonNull final Context context) {
        Intent intent = new Intent(context, TutorialRegisterActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
