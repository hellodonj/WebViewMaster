package com.galaxyschool.app.wawaschool;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.*;
import com.galaxyschool.app.wawaschool.common.DialogHelper.LoadingDialog;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.BaseFragment;
import com.galaxyschool.app.wawaschool.fragment.ClassDetailsFragment;
import com.galaxyschool.app.wawaschool.fragment.ContactsPickerFragment;
import com.galaxyschool.app.wawaschool.fragment.MySchoolSpaceFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.ui.PayActivity;
import com.lqwawa.intleducation.module.discovery.vo.DiscoveryItemVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.*;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.WheelPopupView;
import com.galaxyschool.app.wawaschool.views.SelectBindChildPopupView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QrcodeProcessActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, WheelPopupView.OnSelectChangeListener {


    public interface QrcodeProcessType {
        int TEACHER_ADDTO_CLASS = 0;
        int TEACHER_ADDTO_SCHOOL = 1;
        int FIND_MY_FRIEND = 2;
        int ATTEND_SCHOOL = 3;
    }

    private View rootView;
    private ToolbarTopView toolbarTopView;
    private ImageView thumbnail;
    private TextView name, info, addBtn, classInfo, classExtra;
    private EditText verifyTxt, mRealnameEditText;
    private View classLayout;
    private RadioGroup radioGroup;
    private RadioButton teacherRadioBtn, studentRadioBtn, parentRadioBtn;
    private ViewGroup mRealnameLayout, mSubjectLayout, mRelationLayout;
    private LoadingDialog loadingDialog;
    private WheelPopupView wheelPopupView;
    private LinearLayout chargeDetailLayout;
    private TextView wawaPayNumTextV;
    private String[] qrcodeAddInfos;
    private String qrcodeStr;
    private int qrcode_process_type;
    private String id;
    private int type;
    private int checkPosition = 0;
    private List<QrcodeSchoolInfo> qrcodeSchoolInfoList;
    private List<QrcodeClassInfo> qrcodeClassInfoList;
    private List<QrcodeMemberInfo> qrcodeMemberInfoList;
    private ImageLoader imageLoader = new ImageLoader();
    private QrcodeSchoolInfo qrcodeSchoolInfo;
    private QrcodeClassInfo qrcodeClassInfo;
    private String[] qrcode_titles;

    private UserInfo userInfo;
    private String userName;
    private int relationIndex = -1;
    private ViewGroup mSelectStudentAccountLayout;
    private TextView qrcode_class_selected_student_account;
    private String[] childNameArray;
    private String[] childIdArray;
    private int position = 0;
    private String studentId = null;
    private boolean isFromMooc;
    private SubscribeClassInfo classDetailInfo;
    private boolean needPayJoinClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_process);

        userInfo = ((MyApplication) getApplication()).getUserInfo();
        qrcode_titles = getResources().getStringArray(R.array.qrcode_titles);
        userName = getString(R.string.who_am_i, "");
        if (userInfo != null) {
            userName = getString(R.string.who_am_i, userInfo.getRealName());
        }
        initData();
        initViews();
        registerEventBus();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            qrcodeStr = bundle.getString(ActivityUtils.KEY_QRCODE_STRING);
//            qrcode_process_type = bundle.getInt(ActivityUtils.KEY_QRCODE_PROCESS_TYPE);
//            qrcodeTitle = bundle.getString(ActivityUtils.KEY_QRCODE_TITLE);
            qrcodeSchoolInfo = (QrcodeSchoolInfo) bundle.getSerializable(ActivityUtils.KEY_QRCODE_SCHOOL_INFO);
            qrcodeClassInfo = (QrcodeClassInfo) bundle.getSerializable(ActivityUtils.KEY_QRCODE_CLASS_INFO);
            isFromMooc = bundle.getBoolean("isFromMooc");
            if(isFromMooc){
                String id = bundle.getString("id");
                String name = bundle.getString("name");
                String logoUrl = bundle.getString("logoUrl");
                qrcodeSchoolInfo = new QrcodeSchoolInfo();
                qrcodeSchoolInfo.setId(id);
                qrcodeSchoolInfo.setSname(name);
                qrcodeSchoolInfo.setLogoUrl(logoUrl);
            }
        }
        qrcodeAddInfos = getResources().getStringArray(R.array.qrcode_add_infos);
    }

    private void initViews() {
        rootView = findViewById(R.id.qrcode_process_root_layout);
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getBackView().setOnClickListener(this);

        thumbnail = (ImageView) findViewById(R.id.qrcode_thumbnail);
        name = (TextView) findViewById(R.id.qrcode_name);
        info = (TextView) findViewById(R.id.qrcode_info);
        verifyTxt = (EditText) findViewById(R.id.qrcode_verify_edittext);

        radioGroup = (RadioGroup) findViewById(R.id.qrcode_radio_group);
        teacherRadioBtn = (RadioButton) findViewById(R.id.qrcode_radio_btn_teacher);
        studentRadioBtn = (RadioButton) findViewById(R.id.qrcode_radio_btn_student);
        parentRadioBtn = (RadioButton) findViewById(R.id.qrcode_radio_btn_parent);

        mRealnameLayout = (ViewGroup) findViewById(R.id.qrcode_realname_layout);
        mSubjectLayout = (ViewGroup) findViewById(R.id.qrcode_subject_layout);
        mRelationLayout = (ViewGroup) findViewById(R.id.qrcode_relation_layout);

        mSelectStudentAccountLayout = (ViewGroup) findViewById(R.id.qrcode_account_layout);

        classInfo = (TextView) mRealnameLayout.findViewById(R.id.name_edit_name);
        classExtra = (TextView) findViewById(R.id.qrcode_class_verify_extra);
        mRealnameEditText = (EditText) mRealnameLayout.findViewById(R.id.name_edit_edittext);
        classLayout = findViewById(R.id.qrcode_class_layout);

        qrcode_class_selected_student_account = (TextView) findViewById(R.id
                .qrcode_class_selected_student_account);
        qrcode_class_selected_student_account.setOnClickListener(this);

        ((TextView) mSubjectLayout.findViewById(R.id.name_edit_name)).setText(R.string.subject);
        ((TextView) mSubjectLayout.findViewById(R.id.name_edit_edittext)).setHint(R.string.pls_input_subject);
        ((TextView) mRelationLayout.findViewById(R.id.name_edit_name)).setText(R.string.relation);

        addBtn = (TextView) findViewById(R.id.qrcode_add_btn);
        addBtn.setOnClickListener(this);
        teacherRadioBtn.setChecked(true);
        classExtra.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);

        chargeDetailLayout = (LinearLayout) findViewById(R.id.ll_charge_detail);
        wawaPayNumTextV = (TextView) findViewById(R.id.tv_charge_count);
        if (qrcodeClassInfo == null && qrcodeSchoolInfo == null) {
            getQrcodeInfo();
        } else {
            if (qrcodeClassInfo != null) {
                toolbarTopView.getTitleView().setText(R.string.join_class);
                qrcode_process_type = QrcodeProcessType.TEACHER_ADDTO_CLASS;
                name.setText(qrcodeClassInfo.getCname());
                info.setText(qrcodeClassInfo.getSname());
                thumbnail.setImageResource(R.drawable.ic_launcher);
                imageLoader.loadImage(
                        AppSettings.getFileUrl(qrcodeClassInfo.getHeadPicUrl()),
                        null, thumbnail);

                if (userInfo != null) {
//                    classExtra.setText(userInfo.getRealName());
                    classExtra.setCompoundDrawablesWithIntrinsicBounds(
                            null, null, null, null);
                    classExtra.setEnabled(false);
                }
                loadClassInfo();
            }

            if (qrcodeSchoolInfo != null) {
//                toolbarTopView.getTitleView().setText(R.string.join_school);
//                name.setText(qrcodeSchoolInfo.getSname());
//                thumbnail.setImageResource(R.drawable.ic_launcher);
//                imageLoader.setContent(AppSettings.getFileUrl(qrcodeSchoolInfo.getLogoUrl()), null, thumbnail);
                qrcode_process_type = QrcodeProcessType.TEACHER_ADDTO_SCHOOL;
                name.setText(userName);
                toolbarTopView.getTitleView().setText(getString(R.string.str_i_am_teacher));
                String thumbnailUrl = null;
                if (userInfo != null){
                    thumbnailUrl = userInfo.getHeaderPic();
                }
                if (userInfo != null) {
                    MyApplication.getThumbnailManager(this).displayUserIcon(AppSettings.getFileUrl(
                            thumbnailUrl), thumbnail);
                }
            }
        }

        checkPosition = 0;
        initLayout(qrcode_process_type);
        addBtn.setText(qrcodeAddInfos[qrcode_process_type]);
    }

    private void initLayout(int type) {
        if (type < QrcodeProcessType.TEACHER_ADDTO_CLASS || type > QrcodeProcessType.ATTEND_SCHOOL) {
            return;
        }

        switch (type) {
            case QrcodeProcessType.TEACHER_ADDTO_CLASS:
                classExtra.setHint(R.string.pls_input_relationship);
                switch (checkPosition) {
                    case 0:
                        mRealnameLayout.setVisibility(View.VISIBLE);
                        mSubjectLayout.setVisibility(View.VISIBLE);
                        mRelationLayout.setVisibility(View.GONE);
                        mSelectStudentAccountLayout.setVisibility(View.GONE);
                        classInfo.setText(R.string.name);
                        mRealnameEditText.setHint(R.string.pls_input_name);
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getRealName())) {
                            mRealnameEditText.setText(userInfo.getRealName());
                            mRealnameEditText.setEnabled(false);
                        } else {
                            mRealnameEditText.setText("");
                        }
                        classExtra.setEnabled(false);
                        if (userInfo != null) {
                            classExtra.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                        break;
                    case 1:
                        mRealnameLayout.setVisibility(View.VISIBLE);
                        mSubjectLayout.setVisibility(View.GONE);
                        mRelationLayout.setVisibility(View.GONE);
                        mSelectStudentAccountLayout.setVisibility(View.GONE);
                        classInfo.setText(R.string.name);
                        mRealnameEditText.setHint(R.string.pls_input_name);
                        if (userInfo != null && !TextUtils.isEmpty(userInfo.getRealName())) {
                            mRealnameEditText.setText(userInfo.getRealName());
                            mRealnameEditText.setEnabled(false);
                        } else {
                            mRealnameEditText.setText("");
                        }
                        classExtra.setEnabled(false);
                        if (userInfo != null) {
                            mRealnameEditText.setText(userInfo.getRealName());
                        }
                        break;
                    case 2:
                        mRealnameLayout.setVisibility(View.VISIBLE);
                        mSubjectLayout.setVisibility(View.GONE);
                        mRelationLayout.setVisibility(View.VISIBLE);
                        //如果存在学生重名的情况下才显示选择账户布局
                        classInfo.setText(R.string.stu_name);
                        mRealnameEditText.setHint(R.string.pls_input_student_name);
                        mRealnameEditText.setText("");
                        classExtra.setEnabled(true);
                        Drawable drawable = getResources().getDrawable(R.drawable.arrow_down_ico);
                        classExtra.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                        classExtra.setCompoundDrawablePadding(10);
                        break;
                }
                verifyTxt.setVisibility(View.GONE);
                classLayout.setVisibility(View.VISIBLE);
                classInfo.setText(R.string.i_am);
//                mRealnameEditText.setHint(R.string.qrcode_subject_hint);
                info.setVisibility(View.VISIBLE);
//                if (userInfo != null) {
//                    classExtra.setText(userInfo.getRealName());
//                }
                break;

            case QrcodeProcessType.TEACHER_ADDTO_SCHOOL:
            case QrcodeProcessType.ATTEND_SCHOOL:
                if (name != null) {
                    name.setText(userName);
                }
                verifyTxt.setVisibility(View.GONE);
                classLayout.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                break;
//            case QrcodeProcessType.STUDENT_BIND_PARENT:
//                verifyTxt.setVisibility(View.VISIBLE);
//                verifyTxt.setHint(R.string.qrcode_verify_hint);
//                classLayout.setVisibility(View.GONE);
//                info.setVisibility(View.VISIBLE);
//                if (userInfo != null) {
//                    info.setText(userInfo.getRealName());
//                }
//                break;
            case QrcodeProcessType.FIND_MY_FRIEND:
                verifyTxt.setVisibility(View.VISIBLE);
                verifyTxt.setHint(R.string.qrcode_verify_hint);
                classLayout.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                break;
        }

    }

    private void getQrcodeInfo() {
        if (!TextUtils.isEmpty(qrcodeStr)) {
            String[] strs = qrcodeStr.split("&");
            if (strs != null && strs.length == 2) {
                if (!TextUtils.isEmpty(strs[0])) {
                    id = strs[0].substring(strs[0].indexOf("=") + 1, strs[0].length());
                }
                if (!TextUtils.isEmpty(strs[1])) {
                    String tempStr = strs[1].substring(strs[1].indexOf("=") + 1, strs[1].length());
                    if (tempStr != null && TextUtils.isDigitsOnly(tempStr)) {
                        type = Integer.parseInt(tempStr);
                    }
                }
            }

            qrcode_process_type = QrcodeProcessType.TEACHER_ADDTO_CLASS;
            if (!TextUtils.isEmpty(id) && type >= 0) {
                getQrcodeInfo(id, type);
                qrcode_process_type = type;
            }
            toolbarTopView.getTitleView().setText(qrcode_titles[qrcode_process_type]);
        }
    }

    private void getQrcodeInfo(String id, int type) {
        String url = ServerUrl.QRCODE_SCANNING_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("id", id);
            mParams.put("input_type", String.valueOf(type));
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                    url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    try {
                        if (json != null) {
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject != null) {
                                JSONArray schoolInfoJsonArray = jsonObject.optJSONArray("schoolInfo");
                                if (schoolInfoJsonArray != null) {
                                    qrcodeSchoolInfoList = JSON.parseArray(
                                            schoolInfoJsonArray.toString(), QrcodeSchoolInfo.class);
                                    if (qrcodeSchoolInfoList != null && qrcodeSchoolInfoList.size() > 0) {
                                        qrcodeSchoolInfo = qrcodeSchoolInfoList.get(0);
                                        if (qrcodeSchoolInfo != null) {
                                            name.setText(qrcodeSchoolInfo.getSname());
                                            imageLoader.loadImage(
                                                    AppSettings.getFileUrl(qrcodeSchoolInfo.getLogoUrl()),
                                                    null, thumbnail);
                                        }
                                    }
                                }

                                JSONArray classInfoJsonArray = jsonObject.optJSONArray("classInfo");
                                if (classInfoJsonArray != null) {
                                    qrcodeClassInfoList = JSON.parseArray(
                                            classInfoJsonArray.toString(), QrcodeClassInfo.class);
                                    if (qrcodeClassInfoList != null && qrcodeClassInfoList.size() > 0) {
                                        qrcodeClassInfo = qrcodeClassInfoList.get(0);
                                        if (qrcodeClassInfo != null) {
                                            name.setText(qrcodeClassInfo.getCname());
                                            info.setText(qrcodeClassInfo.getSname());
                                            imageLoader.loadImage(
                                                    AppSettings.getFileUrl(qrcodeClassInfo.getHeadPicUrl()),
                                                    null, thumbnail);

                                            if (userInfo != null) {
//                                                classExtra.setText(userInfo.getRealName());
                                                classExtra.setCompoundDrawablesWithIntrinsicBounds(
                                                        null, null, null, null);
                                                classExtra.setEnabled(false);
                                            }
                                        }
                                    }
                                }

                                JSONArray memberInfoJsonArray = jsonObject.optJSONArray("memberInfo");
                                if (memberInfoJsonArray != null) {
                                    qrcodeMemberInfoList = JSON.parseArray(
                                            memberInfoJsonArray.toString(), QrcodeMemberInfo.class);
                                    if (qrcodeMemberInfoList != null && qrcodeMemberInfoList.size() > 0) {
                                        QrcodeMemberInfo qrcodeMemberInfo = qrcodeMemberInfoList.get(0);
                                        if (qrcodeMemberInfo != null) {
                                            name.setText(qrcodeMemberInfo.getNickName());
//                                            if (qrcode_process_type == QrcodeProcessType.STUDENT_BIND_PARENT) {
//                                                info.setText(qrcodeMemberInfo.getRealName());
//                                            }
                                            imageLoader.loadImage(
                                                    AppSettings.getFileUrl(qrcodeMemberInfo.getHeadPicUrl()),
                                                    null, thumbnail);
                                            verifyTxt.setText(userName);
                                            if (!TextUtils.isEmpty(userName)) {
                                                verifyTxt.setSelection(verifyTxt.length());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    loadClassInfo();
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(QrcodeProcessActivity.this);
        }
    }

    private void addTo(int position) {
        switch (position) {
            case QrcodeProcessType.TEACHER_ADDTO_SCHOOL:
                if (userInfo != null) {
                    if (TextUtils.isEmpty(userInfo.getRealName())) {
                        showonsummateDialog();
                        return;
                    }

//                    if (TextUtils.isEmpty(userInfo.getEmail())) {
//                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.pls_input_mailbox));
//                        return;
//                    }

                    addTeacherToSchool();
                }
                break;
            case QrcodeProcessType.TEACHER_ADDTO_CLASS:
                if (checkPosition >= 0) {
                    switch (checkPosition) {
                        case 0:
                            addTeacherToClass();
                            break;
                        case 1:
                            addStudentToClass();
                            break;
                        case 2:
                            addParentToClass();
                            break;
                    }
                }
                break;
//            case QrcodeProcessType.STUDENT_BIND_PARENT:
//                bindParent();
//                break;
            case QrcodeProcessType.FIND_MY_FRIEND:
                findFriend();
                break;
            case QrcodeProcessType.ATTEND_SCHOOL:
                attendSchool();
                break;
        }
    }

    private void addTeacherToSchool() {
//        if (qrcodeSchoolInfoList == null || qrcodeSchoolInfoList.size() == 0) {
//            return;
//        }
//        QrcodeSchoolInfo qrcodeSchoolInfo = qrcodeSchoolInfoList.get(0);
        if (qrcodeSchoolInfo == null) {
            return;
        }
        if (userInfo == null) {
            return;
        }
        String url = ServerUrl.SAVE_TEACHER_ROLEINFO_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("MemberId", userInfo.getMemberId());
            mParams.put("SchoolId", qrcodeSchoolInfo.getId());
            mParams.put("SchoolName", qrcodeSchoolInfo.getSname());
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                    url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    loadingDialog.dismiss();
                    try {
//                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.add_school_success));
                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.apply_success));
//                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(QrcodeProcessActivity.this);
            loadingDialog = DialogHelper.getIt(QrcodeProcessActivity.this).GetLoadingDialog(0);
        }
    }


    private void addTeacherToClass() {
        String verifyStr = ((EditText) mSubjectLayout.findViewById(R.id.name_edit_edittext)).getText()
                .toString().trim();
        if (TextUtils.isEmpty(verifyStr)) {
            TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.pls_input_subject));
            return;
        }
//        addToClass(RoleType.ROLE_TYPE_TEACHER, verifyStr + classExtra.getText().toString().trim());
        addToClass(RoleType.ROLE_TYPE_TEACHER, verifyStr);
    }

    private void addStudentToClass() {
        if (needPayJoinClass){
            applyPayJoinClass();
        } else {
            String verifyStr = mRealnameEditText.getText().toString().trim();
            addToClass(RoleType.ROLE_TYPE_STUDENT, verifyStr);
        }
    }

    private void addParentToClass() {
        String verifyStr = mRealnameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(verifyStr)) {
            TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.pls_input_student_name));
            return;
        }
        if (relationIndex < 0) {
            TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.pls_select_relation));
            return;
        }
        addToClass(
                RoleType.ROLE_TYPE_PARENT, getString(
                        R.string.whose_parent, verifyStr, classExtra
                                .getText()
                                .toString().trim
                                        ()));
    }


//    private void bindParent() {
//        if (qrcodeMemberInfoList == null || qrcodeMemberInfoList.size() == 0) {
//            return;
//        }
//        QrcodeMemberInfo qrcodeMemberInfo = qrcodeMemberInfoList.get(0);
//        if (qrcodeMemberInfo == null) {
//            return;
//        }
//        if (userInfo == null) {
//            return;
//        }
//
//        String verifyStr = verifyTxt.getText().toString().trim();
//
//        String url = ServerUrl.SAVE_RELATIONINFO_URL;
//        if (!TextUtils.isEmpty(url)) {
//            Map<String, String> mParams = new HashMap<String, String>();
//            mParams.put("MemberId", userInfo.getMemberId());
//            mParams.put("ParentNickName", qrcodeMemberInfo.getNickName());
//            mParams.put("ParentRealName", qrcodeMemberInfo.getRealName());
//            mParams.put("ChildId", userInfo.getMemberId());
//            if (!TextUtils.isEmpty(verifyStr)) {
//                mParams.put("ValidationMessage", verifyStr);
//            }
//            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
//                url, mParams, new Listener<String>() {
//                @Override
//                public void onSuccess(String json) {
//                    loadingDialog.dismiss();
//                    try {
////                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.bind_parent_success));
//                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.apply_success));
////                        setResult(RESULT_OK);
//                        finish();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onError(NetroidError error) {
//                    super.onError(error);
//                    loadingDialog.dismiss();
//                    String es = error.getMessage();
//                    try {
//                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
//                        if (result != null) {
//                            if (result.isHasError()) {
//                                TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, result.getErrorMessage());
//                            }
//                        }
//                    } catch (Exception e) {
//                        TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, getString(R.string.network_error));
//                    }
//                }
//            });
//            request.addHeader("Accept-Encoding", "*");
//            request.start(QrcodeProcessActivity.this);
//            loadingDialog = DialogHelper.getIt(QrcodeProcessActivity.this).GetLoadingDialog(0);
//        }
//    }

//    private void addToClass(final int roleType, String verifyStr) {
////        if (qrcodeClassInfoList == null || qrcodeClassInfoList.size() == 0) {
////            return;
////        }
////        QrcodeClassInfo qrcodeClassInfo = qrcodeClassInfoList.get(0);
//        if (qrcodeClassInfo == null || TextUtils.isEmpty(qrcodeClassInfo.getClassId())) {
//            TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, R.string.class_not_exist);
//            return;
//        }
//        if (userInfo == null) {
//            return;
//        }
//
//        if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_STUDENT) {
//            if (TextUtils.isEmpty(userInfo.getRealName()) && TextUtils.isEmpty(mRealnameEditText.getText().toString()))
//            {
//                TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.pls_input_realname2));
//                return;
//            }
//        }
//
////        if (TextUtils.isEmpty(userInfo.getEmail())) {
////            TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.pls_input_mailbox));
////            return;
////        }
//
//        String url = ServerUrl.JOIN_CLASS_URL;
//        if (!TextUtils.isEmpty(url)) {
//            Map<String, String> mParams = new HashMap<String, String>();
//            mParams.put("MemberId", userInfo.getMemberId());
//            mParams.put("ClassId", qrcodeClassInfo.getClassId());
//            mParams.put("role", String.valueOf(roleType));
//            mParams.put("VersionCode", "1");
//            if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_STUDENT) {
//                if (TextUtils.isEmpty(userInfo.getRealName())) {
//                    mParams.put("RealName", mRealnameEditText.getText().toString());
//                }
//            }
//            if (!TextUtils.isEmpty(verifyStr)) {
//                mParams.put("ValidationMessage", verifyStr);
//            }
//            if (roleType == RoleType.ROLE_TYPE_PARENT) {
////                String studentName = mRealnameEditText.getText().toString().trim();
////                mParams.put("StudentName", studentName);
//                //孩子重名的时候需要用studentId来区分
//                if (studentId != null){
//                    mParams.put("StudentName", studentId);
//                }
//                int pType = relationIndex == 0 ? 2 : (relationIndex == 1 ? 1 : 0);
//                mParams.put("ParentType", String.valueOf(pType));
//            }
//            //家长需要传入孩子的真实姓名
//            if (roleType == RoleType.ROLE_TYPE_PARENT){
//                mParams.put("RealName", mRealnameEditText.getText().toString());
//            }
//            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
//                    url, mParams, new Listener<String>() {
//                @Override
//                public void onSuccess(String json) {
//                    try {
////                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.add_class_success));
//                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.apply_success));
////                        setResult(RESULT_OK);
//                        if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_STUDENT) {
//                            if (TextUtils.isEmpty(userInfo.getRealName())) {
//                                userInfo.setRealName(mRealnameEditText.getText().toString().trim());
//                                ((MyApplication) getApplication()).setUserInfo(userInfo);
//                            }
//                        }
//                        finish();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onError(NetroidError error) {
//                    super.onError(error);
//                    String es = error.getMessage();
//                    try {
//                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
//                        if (result != null) {
//                            if (result.isHasError()) {
//                                TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, result.getErrorMessage());
//
//                                //如果重名的话，要注意重名的情况。
//
//                            }
//                        }
//                    } catch (Exception e) {
//                        TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, getString(R.string.network_error));
//                    }
//                }
//
//                @Override
//                public void onFinish() {
//                    // TODO Auto-generated method stub
//                    super.onFinish();
//                    loadingDialog.dismiss();
//                }
//            });
//            request.addHeader("Accept-Encoding", "*");
//            request.start(QrcodeProcessActivity.this);
//            loadingDialog = DialogHelper.getIt(QrcodeProcessActivity.this).GetLoadingDialog(0);
//        }
//    }

    private void addToClass(final int roleType, String verifyStr) {
//        if (qrcodeClassInfoList == null || qrcodeClassInfoList.size() == 0) {
//            return;
//        }
//        QrcodeClassInfo qrcodeClassInfo = qrcodeClassInfoList.get(0);
        if (qrcodeClassInfo == null || TextUtils.isEmpty(qrcodeClassInfo.getClassId())) {
            TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, R.string.class_not_exist);
            return;
        }
        if (userInfo == null) {
            return;
        }

        if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_STUDENT) {
            if (TextUtils.isEmpty(userInfo.getRealName()) && TextUtils.isEmpty(mRealnameEditText.getText().toString())) {
                showonsummateDialog();
                return;
            }
        }

//        if (TextUtils.isEmpty(userInfo.getEmail())) {
//            TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.pls_input_mailbox));
//            return;
//        }

        String url = ServerUrl.JOIN_CLASS_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, Object> mParams = new HashMap<String, Object>();
            mParams.put("MemberId", userInfo.getMemberId());
            mParams.put("ClassId", qrcodeClassInfo.getClassId());
            mParams.put("role", String.valueOf(roleType));
            mParams.put("VersionCode", "1");
            if (roleType == RoleType.ROLE_TYPE_TEACHER || roleType == RoleType.ROLE_TYPE_STUDENT) {
                if (TextUtils.isEmpty(userInfo.getRealName())) {
                    mParams.put("RealName", mRealnameEditText.getText().toString());
                }
            }

            //家长需要传入孩子的真实姓名
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                mParams.put("StudentName", mRealnameEditText.getText().toString());
//                mParams.put("RealName", mRealnameEditText.getText().toString());
            }
            if (!TextUtils.isEmpty(verifyStr)) {
                mParams.put("ValidationMessage", verifyStr);
            }

            //重名的情况，需要传递学生账户名
            if (roleType == RoleType.ROLE_TYPE_PARENT) {
                //孩子重名的时候需要用studentId来区分
                if (studentId != null) {
                    mParams.put("StudentId", studentId);
                }
                int pType = relationIndex == 0 ? 2 : (relationIndex == 1 ? 1 : 0);
                mParams.put("ParentType", String.valueOf(pType));
            }

            final String myErrorCode = "00054";
            RequestHelper.sendPostRequest(QrcodeProcessActivity.this,
                    url, mParams,
                    new RequestHelper.RequestModelResultListener<BindStudentListModelResult>
                            (QrcodeProcessActivity.this, BindStudentListModelResult.class) {
                        @Override
                        public void onSuccess(String jsonString) {
                            if (getContext() == null) {
                                return;
                            }
                            if (TextUtils.isEmpty(jsonString)) {
                                return;
                            }
                            setResult((BindStudentListModelResult) com.alibaba.fastjson.
                                    JSONObject.parseObject(jsonString, getResultClass()));
                            if (getResult() == null || !getResult().isSuccess()) {
                                if (TextUtils.equals(getResult().getErrorCode(),myErrorCode)){
                                    if (getResult().getModel() != null) {
                                        updateViews(getResult());
                                    }
                                } else if (isShowErrorTips()){
                                    TipsHelper.showToast(getContext(), getResult().getErrorMessage());
                                }
                            }

                            if (getResult() == null || getResult().getModel() == null) {
                            } else {
                                //无重名，成功。
                                if (getResult().isSuccess()) {
                                    EventBus.getDefault().post(new MessageEvent("handle_class_relationship_success"));
                                    TipMsgHelper.ShowMsg(QrcodeProcessActivity.this,
                                            getString(R.string.apply_success));
                                    if (roleType == RoleType.ROLE_TYPE_TEACHER ||
                                            roleType == RoleType.ROLE_TYPE_STUDENT) {
                                        if (TextUtils.isEmpty(userInfo.getRealName())) {
                                            userInfo.setRealName(mRealnameEditText.getText().
                                                    toString().trim());
                                            ((MyApplication) getApplication()).setUserInfo(userInfo);
                                        }
                                    }
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onError(NetroidError error) {
                            super.onError(error);
                            String es = error.getMessage();
                            try {
                                NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                                if (result != null) {
                                    if (result.isHasError()) {
                                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this,
                                                result.getErrorMessage());
                                    }
                                }
                            } catch (Exception e) {
                                TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this,
                                        getString(R.string.network_error));
                            }
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            loadingDialog.dismiss();
                        }
                    });
            loadingDialog = DialogHelper.getIt(QrcodeProcessActivity.this).GetLoadingDialog(0);

        }
    }

    private void updateViews(BindStudentListModelResult result) {
        List<StudentListInfo> list = result.getModel().getStudentList();
        if (list == null || list.size() <= 0) {
            return;
        } else {
            //存在重名学生，要给家长提示。
            TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(
                    R.string.exists_multiple_student_with_the_same_name_tips));
            childNameArray = new String[list.size()];
            childIdArray = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                //只需要显示账号就行了
                childNameArray[i] = list.get(i).getNickname();
                //包含studentId
                childIdArray[i] = list.get(i).getMemberId();
            }

            //然后选择账户布局显示
            mSelectStudentAccountLayout.setVisibility(View.VISIBLE);
        }

    }

    private void showonsummateDialog() {

        ContactsMessageDialog dialog = new ContactsMessageDialog(
                QrcodeProcessActivity.this,
                "", getString(R.string.no_real_name_tip),
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.consummate_personal_info),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityUtils.enterUserDetails(QrcodeProcessActivity.this, userInfo, QrcodeProcessActivity.class.getSimpleName());
                    }
                });
        dialog.show();
    }

    private void findFriend() {
        if (qrcodeMemberInfoList == null || qrcodeMemberInfoList.size() == 0) {
            return;
        }
        QrcodeMemberInfo qrcodeMemberInfo = qrcodeMemberInfoList.get(0);
        if (qrcodeMemberInfo == null) {
            return;
        }
        if (userInfo == null) {
            return;
        }

        if (TextUtils.isEmpty(userInfo.getRealName())) {
            showonsummateDialog();
            return;
        }

        String verifyStr = verifyTxt.getText().toString().trim();

        String url = ServerUrl.JOIN_CONTACT_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("MemberId", userInfo.getMemberId());
            mParams.put("NewFriendId", qrcodeMemberInfo.getId());
            if (!TextUtils.isEmpty(verifyStr)) {
                mParams.put("ValidationMessage", verifyStr);
            }
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                    url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    loadingDialog.dismiss();
                    try {
//                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.add_contact_success));
                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.apply_success));
//                        setResult(RESULT_OK);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(QrcodeProcessActivity.this);
            loadingDialog = DialogHelper.getIt(QrcodeProcessActivity.this).GetLoadingDialog(0);
        }
    }

    private void attendSchool() {
        if (qrcodeSchoolInfo == null) {
            return;
        }
        if (userInfo == null) {
            return;
        }
        String url = ServerUrl.SUBSCRIBE_ADD_SCHOOL_URL;
        if (!TextUtils.isEmpty(url)) {
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("MemberId", userInfo.getMemberId());
            mParams.put("SchoolId", qrcodeSchoolInfo.getId());
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                    url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    loadingDialog.dismiss();
                    try {
                        TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, getString(R.string.attend_success));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    super.onError(error);
                    loadingDialog.dismiss();
                    String es = error.getMessage();
                    try {
                        NetErrorResult result = JSON.parseObject(es, NetErrorResult.class);
                        if (result != null) {
                            if (result.isHasError()) {
                                TipMsgHelper.ShowMsg(QrcodeProcessActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(QrcodeProcessActivity.this, getString(R.string.network_error));
                    }
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.start(QrcodeProcessActivity.this);
            loadingDialog = DialogHelper.getIt(QrcodeProcessActivity.this).GetLoadingDialog(0);
        }
    }

    private void showUserInfoDialog() {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
                QrcodeProcessActivity.this,
                null, getString(R.string.pls_input_name_email),
                getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.input_user_info),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(QrcodeProcessActivity.this, BasicUserInfoActivity.class);
                        intent.putExtra("origin", QrcodeProcessActivity.class.getSimpleName());
                        intent.putExtra("userInfo", userInfo);
                        startActivityForResult(intent, ActivityUtils.REQUEST_CODE_BASIC_USER_INFO);
                    }
                });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                finish();
                break;
            case R.id.qrcode_add_btn:
                if (userInfo == null /*|| TextUtils.isEmpty(userInfo.getRealName()) || TextUtils.isEmpty(userInfo
                        .getEmail())*/) {
                    showUserInfoDialog();
                    return;
                }
                addTo(qrcode_process_type);
                break;
            case R.id.qrcode_class_verify_extra:
                UIUtils.hideSoftKeyboard(QrcodeProcessActivity.this);
                String[] strings = getResources().getStringArray(R.array.relation_types);
                wheelPopupView = new WheelPopupView(QrcodeProcessActivity.this,
                        relationIndex, this, strings);
                wheelPopupView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                break;

            //选择学生账户
            case R.id.qrcode_class_selected_student_account:
                UIUtils.hideSoftKeyboard(QrcodeProcessActivity.this);
                SelectBindChildPopupView popupView = new SelectBindChildPopupView(
                        QrcodeProcessActivity.this, position, new SelectBindChildPopupView.
                        OnRelationChangeListener() {
                    @Override
                    public void onRelationChange(int index, String relationType) {
                        if (!TextUtils.isEmpty(relationType)) {
                            qrcode_class_selected_student_account.setText(relationType);
                            studentId = childIdArray[index];
                            position = index;
                        }

                    }
                }, childNameArray);
                popupView.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                break;

        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.qrcode_radio_btn_teacher:
                mRealnameLayout.setVisibility(View.VISIBLE);
                mSubjectLayout.setVisibility(View.VISIBLE);
                mRelationLayout.setVisibility(View.GONE);
                mSelectStudentAccountLayout.setVisibility(View.GONE);
                chargeDetailLayout.setVisibility(View.GONE);
                addBtn.setText(qrcodeAddInfos[qrcode_process_type]);
                classInfo.setText(R.string.name);
                mRealnameEditText.setHint(R.string.pls_input_name);
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getRealName())) {
                    mRealnameEditText.setText(userInfo.getRealName());
                    mRealnameEditText.setEnabled(false);
                } else {
                    mRealnameEditText.setText("");
                }
                classExtra.setEnabled(false);
                if (userInfo != null) {
                    classExtra.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
                checkPosition = 0;
                //重置一下绑定孩子的数据
                resetBindChildData();
                break;
            case R.id.qrcode_radio_btn_student:
                mRealnameLayout.setVisibility(View.VISIBLE);
                mSubjectLayout.setVisibility(View.GONE);
                mRelationLayout.setVisibility(View.GONE);
                mSelectStudentAccountLayout.setVisibility(View.GONE);
                if (needPayJoinClass){
                    chargeDetailLayout.setVisibility(View.VISIBLE);
                    addBtn.setText(getString(R.string.str_go_to_pay));
                } else {
                    addBtn.setText(qrcodeAddInfos[qrcode_process_type]);
                }
                classInfo.setText(R.string.name);
                mRealnameEditText.setHint(R.string.pls_input_name);
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getRealName())) {
                    mRealnameEditText.setText(userInfo.getRealName());
                    mRealnameEditText.setEnabled(false);
                } else {
                    mRealnameEditText.setText("");
                }
                classExtra.setEnabled(false);
                if (userInfo != null) {
                    mRealnameEditText.setText(userInfo.getRealName());
                }
                checkPosition = 1;
                //重置一下绑定孩子的数据
                resetBindChildData();
                break;
            case R.id.qrcode_radio_btn_parent:
                mRealnameLayout.setVisibility(View.VISIBLE);
                mSubjectLayout.setVisibility(View.GONE);
                mRelationLayout.setVisibility(View.VISIBLE);
                chargeDetailLayout.setVisibility(View.GONE);
                addBtn.setText(qrcodeAddInfos[qrcode_process_type]);
                //如果存在重名学生才显示
                classInfo.setText(R.string.stu_name);
                mRealnameEditText.setHint(R.string.pls_input_student_name);
                mRealnameEditText.setEnabled(true);
                mRealnameEditText.setText("");
                classExtra.setEnabled(true);
                Drawable drawable = getResources().getDrawable(R.drawable.arrow_down_ico);
                classExtra.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                classExtra.setCompoundDrawablePadding(10);
                checkPosition = 2;
                break;
        }
    }

    private void resetBindChildData() {
        if (studentId != null) {
            studentId = null;
        }

        if (!TextUtils.isEmpty(classExtra.getText().toString().trim())) {
            classExtra.setText("");
        }

        if (!TextUtils.isEmpty(qrcode_class_selected_student_account.getText().toString().trim())) {
            qrcode_class_selected_student_account.setText("");
        }
    }

    @Override
    public void onSelectChange(int index, String relationType) {
        relationIndex = index;
        classExtra.setText(relationType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityUtils.REQUEST_CODE_BASIC_USER_INFO) {
            if (data != null) {
                userInfo = (UserInfo) data.getSerializableExtra("userInfo");
                if (userInfo != null) {
                    userName = getString(R.string.who_am_i, "");
                    if (!TextUtils.isEmpty(userInfo.getRealName())) {
                        userName = getString(R.string.who_am_i, userInfo.getRealName());
                    }
                }
                initLayout(qrcode_process_type);
            }
        }
    }

    private void loadClassInfo() {
        if (TextUtils.isEmpty(DemoApplication.getInstance().getMemberId()) || qrcodeClassInfo == null){
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", DemoApplication.getInstance().getMemberId());
        params.put("ClassId", qrcodeClassInfo.getClassId());
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<SubscribeClassInfoResult>(this,
                        SubscribeClassInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        SubscribeClassInfoResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        classDetailInfo = result.getModel().getData();
                        if (classDetailInfo != null && classDetailInfo.getPrice() > 0){
                            if (wawaPayNumTextV != null){
                                wawaPayNumTextV.setText(String.valueOf(classDetailInfo.getPrice()));
                            }
                            needPayJoinClass = true;
                        }
                    }
                };
        RequestHelper.sendPostRequest(this, ServerUrl.CONTACTS_CLASS_INFO_URL, params, listener);
    }

    private void applyPayJoinClass(){
        if (classDetailInfo == null || userInfo == null){
            return;
        }
        if (classDetailInfo.isStudentByRoles()){
            TipMsgHelper.ShowMsg(this,R.string.str_pay_join_class_tip);
            return;
        }
        if (classDetailInfo.isTeacherByRoles() || classDetailInfo.isParentByRoles()){
            TipMsgHelper.ShowMsg(this,R.string.str_repeat_role_info);
            return;
        }
        if (TextUtils.isEmpty(userInfo.getRealName())) {
            showonsummateDialog();
            return;
        }
        PayActivity.newInstance(this,
                true,
                DemoApplication.getInstance().getMemberId(),
                classDetailInfo.getClassId(),
                classDetailInfo.getClassName(),
                classDetailInfo.getHeadPicUrl(),
                classDetailInfo.getSchoolId(),
                classDetailInfo.getSchoolName(),
                String.valueOf(classDetailInfo.getPrice()));
    }

    private void registerEventBus(){
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void unRegisterEventBus(){
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventWrapper event){
        if (EventWrapper.isMatch(event,EventConstant.CREATE_CLASS_ORDER)){
            //支付成功加入班级
            String memberId = DemoApplication.getInstance().getMemberId();
            if (TextUtils.isEmpty(memberId)){
                return;
            }
            List<String> studentIds = new ArrayList<>();
            studentIds.add(memberId);
            addStudentToClass(studentIds);
        }
    }

    private void addStudentToClass(List<String> studentIds) {
        Map<String, Object> params = new HashMap<String, Object>();
        //班主任的memberId
        params.put("MemberId", classDetailInfo.getHeadMasterId());
        //班级的classId
        params.put("ClassId", classDetailInfo.getClassId());
        params.put("StudentList", studentIds);
        RequestHelper.RequestListener listener =
                new RequestHelper.RequestDataResultListener<DataModelResult>(
                        QrcodeProcessActivity.this, DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {

                        } else {
                            //刷新前面界面的数据
                            MySchoolSpaceFragment.sendBrocast(QrcodeProcessActivity.this);
                            EventBus.getDefault().post(new MessageEvent(MessageEventConstantUtils.JOIN_CHARGE_CLASS_SUCCESS));
                            TipsHelper.showToast(QrcodeProcessActivity.this, R.string.str_join_success);
                            finish();
                        }
                    }
                };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(QrcodeProcessActivity.this, ServerUrl.ADD_STUDENT_TO_CLASS_URL,
                params, listener);
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }
}
