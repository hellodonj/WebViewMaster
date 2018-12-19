package com.lqwawa.intleducation.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseActivity;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.net.UploadImg;
import com.lqwawa.intleducation.common.ui.CommonEditDialog;
import com.lqwawa.intleducation.common.ui.CommonListPopupView;
import com.lqwawa.intleducation.common.ui.GenderSelectPopupView;
import com.lqwawa.intleducation.common.ui.ImageSelectPopupView;
import com.lqwawa.intleducation.common.utils.ActivityUtils;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.module.user.vo.AreaVo;
import com.lqwawa.intleducation.module.user.vo.PersonalInfo;
import com.lqwawa.intleducation.module.user.vo.PersonalInfoVo;
import com.lqwawa.intleducation.module.user.vo.UploadImgBackVo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;

import static com.lqwawa.intleducation.common.ui.GenderSelectPopupView.GENDER_STR_IDS;

/**
 * Created by XChen on 2016/12/2.
 * email:man0fchina@foxmail.com
 * 个人信息
 */
public class PersonalInfoActivity extends MyBaseActivity implements View.OnClickListener {
    private static final String TAG = "PersonalInfoActivity";
    public static final int REQUEST_CODE_PERSONAL_INFO = 1002;

    private TopBar topBar;

    private LinearLayout layEditAvatar;//编辑头像
    private ImageView imageViewAvatar;//头像
    private LinearLayout layNickname;//昵称
    private TextView textViewNickname;
    private LinearLayout layGender;//性别
    private TextView textViewGender;
    private LinearLayout layName;//姓名
    private TextView textViewName;
    private LinearLayout layCity;//城市
    private TextView textViewCity;
    private LinearLayout layIntroduction;//介绍
    private TextView textViewIntroduction;

    private PersonalInfo personalInfo;
    private ImageOptions imageOptionsAvatar;
    private String updateAvatarUrl;
    private String updateName;
    private int updateGender = -1;
    private String updateCityLevel;
    private String updateCityLevelName;
    private String updateIntroduction;
    private boolean haveChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        topBar = (TopBar) findViewById(R.id.top_bar);
        layEditAvatar = (LinearLayout) findViewById(R.id.edit_avatar_layout);
        imageViewAvatar = (ImageView) findViewById(R.id.avatar_iv);
        layNickname = (LinearLayout) findViewById(R.id.nickname_layout);
        textViewNickname = (TextView) findViewById(R.id.nickname_tv);
        layGender = (LinearLayout) findViewById(R.id.gender_layout);
        textViewGender = (TextView) findViewById(R.id.gender_tv);
        layName = (LinearLayout) findViewById(R.id.name_layout);
        textViewName = (TextView) findViewById(R.id.name_tv);
        layCity = (LinearLayout) findViewById(R.id.city_layout);
        textViewCity = (TextView) findViewById(R.id.city_tv);
        layIntroduction = (LinearLayout) findViewById(R.id.introduction_layout);
        textViewIntroduction = (TextView) findViewById(R.id.introduction_tv);
        personalInfo = (PersonalInfo) getIntent().getSerializableExtra("personalInfo");
        initViews();
    }

    private void initViews() {
        topBar.setBack(true);
        topBar.setRightFunctionText1(getResources().getString(R.string.save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadChanges();
            }
        });
        topBar.setTitle(getResources().getString(R.string.personal_info));
        layEditAvatar.setOnClickListener(this);
        layNickname.setOnClickListener(this);
        layGender.setOnClickListener(this);
        layName.setOnClickListener(this);
        layCity.setOnClickListener(this);
        layIntroduction.setOnClickListener(this);
        topBar.setRightFunctionText1TextColor(getResources().getColorStateList(R.color.com_green_text));
        topBar.setRightFunctionText1Enabled(false);

        imageOptionsAvatar = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setCircular(true)
                .setLoadingDrawableId(R.drawable.ic_avatar_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.ic_avatar_def)//加载失败后默认显示图片
                .build();

        if (personalInfo != null) {
            x.image().bind(imageViewAvatar,
                    ("" + personalInfo.getThumbnail()).trim(),
                    imageOptionsAvatar);
            if (personalInfo.getName() != null) {
                textViewName.setText(personalInfo.getName());
            }
            if (StringUtils.isIntString(personalInfo.getGender())) {
                String genderString = activity.getResources().getString(
                        GENDER_STR_IDS[StringUtils.getInt(personalInfo.getGender())]);
                textViewGender.setText(genderString);
            }
            if (personalInfo.getLevelName() != null) {
                textViewCity.setText(personalInfo.getLevelName().replace("/", ""));
            }
            if (personalInfo.getIntroduction() != null) {
                textViewIntroduction.setText(personalInfo.getIntroduction());
            }
        }
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.edit_avatar_layout) {//修改头像
            ImageSelectPopupView.showView(activity, false, null);
        } else if (view.getId() == R.id.nickname_layout) {//修改昵称
        } else if (view.getId() == R.id.gender_layout) {//修改性别
            GenderSelectPopupView.showView(activity, new CommonListPopupView.PopupWindowListener() {
                @Override
                public void onItemClickListener(Object object) {
                    int index = (int) object;
                    String genderString = activity.getResources().getString(GENDER_STR_IDS[index]);
                    if (!textViewGender.getText().toString().equals(genderString)) {
                        topBar.setRightFunctionText1Enabled(true);
                    }
                    textViewGender.setText(genderString);
                    updateGender = index;
                }

                @Override
                public void onBottomButtonClickListener() {

                }

                @Override
                public void onDismissListener() {

                }
            });
        } else if (view.getId() == R.id.name_layout) {//修改姓名
            CommonEditDialog commonEditDialog
                    = new CommonEditDialog(activity,
                    getResources().getString(R.string.enter_name_please),
                    textViewName.getText().toString().trim(),
                    16,
                    new CommonEditDialog.CommitCallBack() {
                        @Override
                        public void OnDismiss(String content) {
                            if (!textViewName.getText().toString().equals(content)) {
                                updateName = content;
                                if (!textViewName.getText().toString().equals(updateName)) {
                                    topBar.setRightFunctionText1Enabled(true);
                                }
                                textViewName.setText(updateName);
                            }
                        }
                    });
            Window window = commonEditDialog.getWindow();
            commonEditDialog.show();
            window.setGravity(Gravity.BOTTOM);
        } else if (view.getId() == R.id.city_layout) {//更改城市
            startActivityForResult(new Intent(activity,
                            AreaSelectActivity.class),
                    AreaSelectActivity.Rc_AreaSelect);
        } else if (view.getId() == R.id.introduction_layout) {//编辑自我介绍
            CommonEditDialog dialog
                    = new CommonEditDialog(activity,
                    getResources().getString(R.string.enter_personal_intro_please),
                    textViewIntroduction.getText().toString().trim(),
                    300,
                    new CommonEditDialog.CommitCallBack() {
                        @Override
                        public void OnDismiss(String content) {
                            if (!textViewIntroduction.getText().toString().equals(content)) {
                                updateIntroduction = content;
                                if (!textViewIntroduction.getText().toString().equals(updateIntroduction)) {
                                    topBar.setRightFunctionText1Enabled(true);
                                }
                                textViewIntroduction.setText(updateIntroduction);
                            }
                        }
                    });
            Window wd = dialog.getWindow();
            dialog.show();
            wd.setGravity(Gravity.BOTTOM);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityUtils.REQUEST_CODE_TAKE_PHOTO:
                if (resultCode != Activity.RESULT_OK)
                    break;
                File iconFile = new File(Utils.ICON_FOLDER + Utils.ICON_NAME);
                if (iconFile != null && iconFile.exists()) {
                    if (PersonalInfoActivity.this == null) {
                        return;
                    }
                    ActivityUtils.startZoomPhoto(activity,
                            Uri.fromFile(iconFile), Utils.USER_ICON_SIZE);
                }
                break;
            case ActivityUtils.REQUEST_CODE_FETCH_PHOTO:
                if (resultCode != Activity.RESULT_OK)
                    break;
                if (data != null) {
                    if (PersonalInfoActivity.this == null) {
                        return;
                    }
                    ActivityUtils.startZoomPhoto(activity,
                            data.getData(), Utils.USER_ICON_SIZE);
                }
                break;
            case ActivityUtils.REQUEST_CODE_ZOOM_PHOTO:
                if (resultCode != Activity.RESULT_OK)
                    break;
                String zoomIconPath = Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME;
                if (!TextUtils.isEmpty(zoomIconPath)) {
                    File iconZoomFile = new File(zoomIconPath);
                    if (iconZoomFile != null && iconZoomFile.length() > 0) {
                        uploadAvatar(zoomIconPath);
                    }
                }
                break;
            case AreaSelectActivity.Rc_AreaSelect:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        AreaVo vo = (AreaVo) data.getSerializableExtra("areaVo");
                        if (vo != null) {
                            updateCityLevel = vo.getLevel();
                            updateCityLevelName = vo.getLevelName();
                            if (!textViewCity.getText().toString().equals(updateCityLevelName)) {
                                topBar.setRightFunctionText1Enabled(true);
                            }
                            textViewCity.setText(vo.getLevelName().replace("/", ""));
                        }
                    }
                }
            default:
                break;
        }
    }

    private void uploadAvatar(String imgPath) {
        showProgressDialog(getResources().getString(R.string.uploading));
        UploadImg.uploadImage(imgPath, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String s) {
                return false;
            }

            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ToastUtil.showToast(activity,
                        getResources().getString(R.string.upload)
                                + getResources().getString(R.string.success));
                UploadImgBackVo result = JSON.parseObject(s,
                        new TypeReference<UploadImgBackVo>() {
                        });
                if (result.getCode() == 0) {
                    if (result.getImgUrl() != null && !result.getImgUrl().isEmpty()) {
                        haveChanges = true;
                        topBar.setRightFunctionText1Enabled(true);
                        updateAvatarUrl = result.getImgUrl();
                        updateAvatarUrl = ("" + updateAvatarUrl).trim();
                        x.image().bind(imageViewAvatar,
                                ("" + updateAvatarUrl).trim(),
                                imageOptionsAvatar);
                        return;
                    }
                }
                LogUtil.d(TAG, result.getMessage());
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                closeProgressDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void uploadChanges() {
        final RequestVo requestVo = new RequestVo();
        if (StringUtils.isValidString(updateAvatarUrl)) {
            requestVo.addParams("thumbnail", updateAvatarUrl);
        }
        if (StringUtils.isValidString(updateName) || (updateName != null && updateName.equals(""))) {
            requestVo.addParams("name", updateName);
        }
        if (updateGender >= 0 && updateGender < 3) {
            requestVo.addParams("gender", updateGender);
        }
        if (StringUtils.isValidString(updateCityLevel)) {
            requestVo.addParams("level", updateCityLevel);
            requestVo.addParams("levelName", updateCityLevelName);
        }
        if (StringUtils.isValidString(updateIntroduction) || (updateIntroduction != null && updateIntroduction.equals(""))) {
            requestVo.addParams("introduction", updateIntroduction);
        }

        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.UpdatePersonalInfo + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                PersonalInfoVo result = JSON.parseObject(s,
                        new TypeReference<PersonalInfoVo>() {
                        });
                ToastUtil.showToast(activity,
                        getResources().getString(R.string.save)
                                + getResources().getString(R.string.success));
                setResult(Activity.RESULT_OK, new Intent().putExtra("personalInfo", result));
                finish();
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "更新个人信息失败:" + throwable.getMessage());

                ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }
}
