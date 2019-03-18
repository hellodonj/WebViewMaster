package com.lqwawa.mooc.modle.tutorial.regist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
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
    implements TutorialRegisterContract.View,View.OnClickListener{

    private static final String CHINA_TEXT = "中国"; //变量
    private int mediaType = MediaType.PICTURE; //图片类型

    private TopBar mTopBar;
    // 姓名
    private EditText mEtName;
    // 手机号码
    private EditText mEtPhoneNumber;
    // 验证码
    private EditText mEtVerificationCode;
    // 证件类型容器
    private FrameLayout mIdentifyLayout;
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

        mEtName = (EditText) findViewById(R.id.et_nick_name);
        mEtPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        mEtVerificationCode = (EditText) findViewById(R.id.et_verification_code);
        mIdentifyLayout = (FrameLayout) findViewById(R.id.identify_layout);
        // 默认身份证
        mIdentifyLayout.setTag(IDType.ID_TYPE_IDENTITY_CARD);
        mEtIdentifyNumber = (EditText) findViewById(R.id.et_identify_number);
        mEtMarkPrice = (EditText) findViewById(R.id.et_mark_price);
        mEtWorkLimit = (EditText) findViewById(R.id.et_work_limit);
        mOrganSpinner = (NiceSpinner) findViewById(R.id.organ_spinner);
        mProvinceSpinner = (NiceSpinner) findViewById(R.id.province_spinner);
        mCitySpinner = (NiceSpinner) findViewById(R.id.city_spinner);
        mDistrictSpinner = (NiceSpinner) findViewById(R.id.district_spinner);
        mBtnCertificateUpload = (TextView) findViewById(R.id.btn_certificate_upload);
        mBtnBusinessUpload = (TextView) findViewById(R.id.tv_business_upload);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mBtnCertificateUpload.setOnClickListener(this);
        mBtnBusinessUpload.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.requestTutorialOrgan(true,0);
        mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_COUNTRY,"");
    }

    @Override
    public void updateTutorialOrganView(@NonNull List<SchoolInfoEntity> entities) {
        mOrganSpinner.attachDataSource(entities);
        fillSpinnerBottom(mOrganSpinner);
        mOrganSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentOrganEntity = entities.get(position);
            }
        });
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
                mCurrentProviceBean = bean;
                mPresenter.requestLocationWithParams(LocationType.LOCATION_TYPE_CITY,bean.getValue());
            }
        });
        fillSpinnerBottom(mProvinceSpinner);
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
    }

    @Override
    public void updateApplyForResult(boolean result) {
        if(result){
            hideLoading();
            TutorialAuditActivity.show(this,result);
            finish();
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
        int maxSpinnerHeight = DisplayUtil.getMobileHeight(this) - parentVerticalOffset - spinner.getMeasuredHeight();
        // 实际设置的高度
        int spinnerHeight = Math.max(maxSpinnerHeight,parentVerticalOffset);
        // 补偿策略
        if(spinnerHeight > maxSpinnerHeight){
            spinner.setDropDownListPaddingBottom(spinnerHeight - maxSpinnerHeight);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_certificate_upload){
            // 个人学历证书选择上传
            mBtnBusinessUpload.setActivated(false);
            mBtnCertificateUpload.setActivated(true);
            showImagePopupView();
        }else if(viewId == R.id.tv_business_upload){
            // 行业资历认证证书上传
            mBtnBusinessUpload.setActivated(true);
            mBtnCertificateUpload.setActivated(false);
            showImagePopupView();
        }else if(viewId == R.id.btn_submit){
            // 提交申请
            commitApplyFor();
        }
    }

    private void commitApplyFor(){
        String name = mEtName.getText().toString().trim();
        String phoneNumber = mEtPhoneNumber.getText().toString().trim();
        String verificationCode = mEtVerificationCode.getText().toString().trim();
        int idType = (int) mIdentifyLayout.getTag();
        String identifyNumber = mEtIdentifyNumber.getText().toString().trim();
        String organId = "";
        String organName = "";
        if(EmptyUtil.isNotEmpty(mCurrentOrganEntity)){
            organId = mCurrentOrganEntity.getId();
            organName = mCurrentOrganEntity.getSName();
        }

        String provinceId = "";
        String provinceName = "";
        if(EmptyUtil.isNotEmpty(mCurrentProviceBean)){
            provinceId = mCurrentProviceBean.getValue();
            provinceName = mCurrentProviceBean.getText();
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
        String workLife = mEtWorkLimit.getText().toString().trim();

        String certificateUrl = mUrlArray.get(mBtnCertificateUpload.getId());
        String businessUrl = mUrlArray.get(mBtnBusinessUpload.getId());

        showLoading();
        mPresenter.requestApplyForTutor(name,phoneNumber,verificationCode,
                idType,identifyNumber,
                UserHelper.getUserId(),UserHelper.getUserName(),
                organId,organName,markPrice,
                provinceId,provinceName,
                cityId,cityName,
                districtId,districtName,
                workLife,certificateUrl,businessUrl);
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

                String urlFolder = Utils.IMAGE_FOLDER;
                String url = urlFolder + Utils.TEMP_IMAGE_NAME;

                File fileFolder = new File(urlFolder);
                if (!fileFolder.exists()) {
                    fileFolder.mkdirs();
                }

                uploadAvatar(userId,url);
                break;
            case ActivityUtils.REQUEST_CODE_FETCH_PHOTO:
                if (data != null) {
                    String photoPath = PhotoUtils.getImageAbsolutePath(this, data.getData());

                    if (TextUtils.isEmpty(photoPath)) {
                        return;
                    }

                    uploadAvatar(userId,photoPath);
                }
                break;
            default:
                break;

        }
    }

    /**
     * 上传文件
     * @param memberId 当前的用户Id
     * @param filePath 文件路径
     */
    private void uploadAvatar(String memberId, String filePath) {
        if (TextUtils.isEmpty(memberId) || TextUtils.isEmpty(filePath)) {
            return;
        }

        // 上传
        UserInfo userInfo = getUserInfo();

        if(userInfo == null){
            return;
        }


        UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, mediaType, 1);
        List<String> paths = new ArrayList<>();
        StringBuilder names = new StringBuilder();
        paths.add(filePath);
        uploadParameter.setPaths(paths);
        if(filePath.contains("/")){
            names.append(filePath.substring(filePath.lastIndexOf("/") + 1) + ";");
            uploadParameter.setFileName(names.toString());
        }

        showLoading();
        UploadUtils.uploadMedia(this, uploadParameter, new CallbackListener() {
            @Override
            public void onBack(Object result) {
                hideLoading();
                TypeReference<ResponseVo<List<MediaEntity>>> typeReference = new TypeReference<ResponseVo<List<MediaEntity>>>(){};
                ResponseVo<List<MediaEntity>> responseVo = JSON.parseObject(JSON.toJSONString(result), typeReference);
                if(responseVo.isSucceed()){
                    List<MediaEntity> data = responseVo.getData();
                    if(EmptyUtil.isNotEmpty(data)){
                        MediaEntity entity = data.get(0);
                        String resourceUrl = entity.getResourceurl();
                        runOnUiThread(new UploadImageCallback(resourceUrl));
                    }
                }
            }
        });
    }

    private class UploadImageCallback implements Runnable{

        private String url;

        public UploadImageCallback(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            if(!isFinishing()) {
                UIUtil.showToastSafe(R.string.label_upload_completed);
                if (mBtnBusinessUpload.isActivated()) {
                    int viewId = mBtnBusinessUpload.getId();
                    mUrlArray.put(viewId, url);
                } else if (mBtnCertificateUpload.isActivated()) {
                    int viewId = mBtnCertificateUpload.getId();
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
     * @param context
     */
    public static void show(@NonNull final Context context){
        Intent intent = new Intent(context, TutorialRegisterActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
