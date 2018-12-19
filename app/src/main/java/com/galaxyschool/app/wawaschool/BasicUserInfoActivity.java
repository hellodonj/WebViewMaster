package com.galaxyschool.app.wawaschool;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.text.method.TextKeyListener;
import android.text.method.TextKeyListener.Capitalize;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.DefaultRetryPolicy;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.bitmapmanager.ThumbnailManager;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadImageTask;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.account.RegisterFragment;
import com.lqwawa.lqbaselib.net.NetErrorResult;
import com.lqwawa.lqbaselib.net.PostByMapParamsModelRequest;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.ImagePopupView;
import com.galaxyschool.app.wawaschool.views.ToolbarTopView;
import com.galaxyschool.app.wawaschool.views.wheelview.CalendarView;
import com.lqwawa.mooc.common.MOOCHelper;
import com.osastudio.common.utils.PhotoUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicUserInfoActivity extends BaseActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int ACCOUNT_MAX_LEN = 20;
    public static final int ACCOUNT_MIN_LEN = 3;

    public static final String DIGITS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private ToolbarTopView toolbarTopView;
    private ListView basicInfoListView;
    private EditText contentTxt;
    private LinearLayout sexLayout, maleLayout, femaleLayout;
    private CalendarView calendarView;

    private FrameLayout basicInfoEditLayout;

    private DialogHelper.LoadingDialog loadingDialog;
    private BasicInfoAdapter basicInfoAdapter;

    private UserInfo userInfo;
    private UserInfoChangeField mChangeField;
    private String origin;
    private int position = -1;
    private int sexType = 0;
    private int roleType;
    private boolean isFirstPage = true;
    private List<BasicInfo> basicInfoList = new ArrayList<BasicInfo>();
    //新增头像
    final int AVATAR = 0;
    final int ACCOUNT = AVATAR + 1;
    final int BIND_PHONE = ACCOUNT + 1;
    final int REALNAME = BIND_PHONE + 1;
    final int SEX = REALNAME + 1;
    final int BIRTHDAY = SEX + 1;
    final int LOCATION = BIRTHDAY + 1;
    final int INTRO = LOCATION + 1;
    //新增修改密码
//    final int MODIFY_PWD = INTRO +1;
    final int MODIFY_PWD = LOCATION +1;
    final int CONTACT_PHONE = MODIFY_PWD + 1;
    final int MAIL = CONTACT_PHONE + 1;
    final int ROLE = MAIL + 1;

    TextWatcher textWatcher;
    //头像信息
    private boolean isUploading;
    private static final int MSG_UPLOAD_AVATAR_START = 1;
    private static final int MSG_UPLOAD_AVATAR_END = 2;
    private ThumbnailManager thumbnailManager;
    private String userId;


    private int[] mBasicInfoNames = {
            R.string.avatar,
            R.string.user_account,
            R.string.bind_phone_number,
            R.string.real_name,
            R.string.sex,
            R.string.birthday,
            R.string.user_location,
//            R.string.user_introduction,
            R.string.modify_password,
            R.string.contact_phone,
            R.string.mailbox,
            R.string.role_info
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_userinfo);
        thumbnailManager = MyApplication.getThumbnailManager(this);
        initViews();
        initData();
    }

    private void initViews() {
        toolbarTopView = (ToolbarTopView) findViewById(R.id.toolbartopview);
        toolbarTopView.getBackView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        toolbarTopView.getCommitView().setText(R.string.save);
        toolbarTopView.getCommitView().setBackgroundResource(R.drawable.sel_nav_button_bg);
        toolbarTopView.getBackView().setOnClickListener(this);
        toolbarTopView.getCommitView().setOnClickListener(this);

        basicInfoListView = (ListView) findViewById(R.id.userinfo_basic_info_listview);
        basicInfoEditLayout = (FrameLayout) findViewById(R.id.basic_userinfo_edit_layout);
        contentTxt = (EditText) findViewById(R.id.basic_userinfo_content_edittext);
        sexLayout = (LinearLayout) findViewById(R.id.basic_userinfo_sex_layout);
        maleLayout = (LinearLayout) findViewById(R.id.basic_userinfo_male_layout);
        femaleLayout = (LinearLayout) findViewById(R.id.basic_userinfo_female_layout);
        calendarView = (CalendarView) findViewById(R.id.basic_userinfo_calendarview);
        basicInfoListView.setOnItemClickListener(this);
        maleLayout.setOnClickListener(this);
        femaleLayout.setOnClickListener(this);

        textWatcher = new MaxLengthWatcher(ACCOUNT_MAX_LEN, contentTxt);

        initBasicInfoLayout();
    }

    private void initBasicInfoLayout() {
        basicInfoList.clear();
        for (int i = 0; i < mBasicInfoNames.length; i++) {
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.resId = mBasicInfoNames[i];
            basicInfo.isNull = (i == REALNAME) ? false : true;
            basicInfoList.add(basicInfo);
        }
        basicInfoAdapter = new BasicInfoAdapter(BasicUserInfoActivity.this);
        basicInfoListView.setAdapter(basicInfoAdapter);
    }

    private void updateUserInfo() {
        if (userInfo != null) {
            basicInfoList.get(ACCOUNT).info = userInfo.getNickName();
            basicInfoList.get(REALNAME).info = userInfo.getRealName();
            basicInfoList.get(BIND_PHONE).info = userInfo.getBindMobile();
            basicInfoList.get(CONTACT_PHONE).info = userInfo.getMobile();
            basicInfoList.get(MAIL).info = userInfo.getEmail();
            basicInfoList.get(SEX).info = userInfo.getSex();
            basicInfoList.get(BIRTHDAY).info = userInfo.getBirthday();
//            basicInfoList.get(INTRO).info = userInfo.getPIntroduces();
            //修改密码
            basicInfoList.get(MODIFY_PWD).info ="********";
            basicInfoList.get(LOCATION).info = userInfo.getLocation();
            basicInfoAdapter.notifyDataSetChanged();
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userInfo = ((MyApplication) getApplication()).getUserInfo();
            origin = bundle.getString("origin");
            loadUserInfo();
        }
    }

    protected void loadUserInfo() {
        if(userInfo!=null){
            userId=userInfo.getMemberId();
        }
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("UserId", userId);

        RequestHelper.sendPostRequest(this, ServerUrl.LOAD_USERINFO_URL,
                params,
                new RequestHelper.RequestModelResultListener<UserInfoResult>(this,UserInfoResult
                        .class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (this == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        userInfo = getResult().getModel();
                        userInfo.setMemberId(userId);
                        ((MyApplication) getApplication()).setUserInfo(userInfo);
                    }
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        enterBasicLayout();
                    }
                });
    }

    private void enterEditLayout() {
        toolbarTopView.getCommitView().setVisibility(View.VISIBLE);
        basicInfoListView.setVisibility(View.GONE);
        basicInfoEditLayout.setVisibility(View.VISIBLE);
        if (userInfo != null && position >= 0) {
            toolbarTopView.getTitleView().setText(mBasicInfoNames[position]);
            contentTxt.setVisibility(View.INVISIBLE);
            contentTxt.setHint("");
            sexLayout.setVisibility(View.INVISIBLE);
            calendarView.setVisibility(View.INVISIBLE);
            contentTxt.setEnabled(true);
            if(textWatcher != null) {
                contentTxt.removeTextChangedListener(textWatcher);
            }
            setEditTextInputLimit(false);
            switch (position) {
                case ACCOUNT:
                    contentTxt.setVisibility(View.VISIBLE);
                    contentTxt.setText(userInfo.getNickName());
                    contentTxt.setHint(R.string.user_account_hint);
                    setAcceptCharsAndNum(CHAR_AND_NUM);
                    //用户名最多20个字符
                    ((MaxLengthWatcher)textWatcher).setMaxLen(20);
                    contentTxt.addTextChangedListener(textWatcher);
                    break;
                case REALNAME:
                    contentTxt.setVisibility(View.VISIBLE);
                    contentTxt.setText(userInfo.getRealName());
                    //真实姓名至多20个字符
                    ((MaxLengthWatcher)textWatcher).setMaxLen(20);
                    contentTxt.addTextChangedListener(textWatcher);
                    setAcceptCharsAndNum(ALL);
                    setEditTextInputLimit(true);
                    break;
                case BIND_PHONE:
                    contentTxt.setVisibility(View.VISIBLE);
                    contentTxt.setText(userInfo.getBindMobile());
                    //手机号11位
                    ((MaxLengthWatcher)textWatcher).setMaxLen(11);
                    contentTxt.addTextChangedListener(textWatcher);
                    setAcceptCharsAndNum(MOBILE_NUM);
                    break;
                case CONTACT_PHONE:
                    contentTxt.setVisibility(View.VISIBLE);
                    contentTxt.setText(userInfo.getMobile());
                    //手机号11位
                    ((MaxLengthWatcher)textWatcher).setMaxLen(11);
                    contentTxt.addTextChangedListener(textWatcher);
                    setAcceptCharsAndNum(PHONE_NUM);
                    break;
                case MAIL:
                    contentTxt.setVisibility(View.VISIBLE);
                    contentTxt.setText(userInfo.getEmail());
                    setAcceptCharsAndNum(ALL);
//                    contentTxt.setEnabled(false);
//                    toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
                    break;
                case SEX:
                    sexLayout.setVisibility(View.VISIBLE);
                    sexType = 0;
                    if (!TextUtils.isEmpty(userInfo.getSex()) && userInfo.getSex().equals("女")) {
                        sexType = 1;
                    }
                    setSexLayout(sexType);
                    break;
                case BIRTHDAY:
                    calendarView.setVisibility(View.VISIBLE);
                    if (userInfo != null) {
                        String birthday = userInfo.getBirthday();
                        if (!TextUtils.isEmpty(birthday)) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date date = dateFormat.parse(userInfo.getBirthday());
                                int year = date.getYear() + 1900;
                                int month = date.getMonth();
                                int day = date.getDate();
                                calendarView.setCurrentYearMonthDay(year, month, day);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Calendar calendar = Calendar.getInstance();
                            int year = calendar.get(Calendar.YEAR);
                            int month = calendar.get(Calendar.MONTH);
                            int day = calendar.get(Calendar.DAY_OF_MONTH);
                            calendarView.setCurrentYearMonthDay(year, month, day);
                        }
                    }
                    break;
                case INTRO:
                case LOCATION:
                    contentTxt.setVisibility(View.VISIBLE);
                    if (position == INTRO) {
                        contentTxt.setText(userInfo.getPIntroduces());
                        //限制30个字符
                        ((MaxLengthWatcher)textWatcher).setMaxLen(30);
                        contentTxt.addTextChangedListener(textWatcher);
                    } else {
                        contentTxt.setText(userInfo.getLocation());
                    }
                    setAcceptCharsAndNum(ALL);
                    break;
                default:
                    break;
            }
        }
        isFirstPage = false;
    }

    private void enterModifyPasswordActivity() {

        Intent intent = new Intent(BasicUserInfoActivity.this,ModifyPasswordActivity.class);
        startActivity(intent);
    }

    private void enterBasicLayout() {
        UIUtils.hideSoftKeyboard(this);
        toolbarTopView.getCommitView().setVisibility(View.INVISIBLE);
        toolbarTopView.getTitleView().setText(R.string.personal_details);
        basicInfoListView.setVisibility(View.VISIBLE);
        basicInfoEditLayout.setVisibility(View.GONE);
        updateUserInfo();
        isFirstPage = true;
    }

    @Override
    public void onBackPressed() {
        if (isFirstPage) {
//            if (!origin.equals(UserInfoFragment.class.getSimpleName())) {
            if (origin.equals(RegisterFragment.class.getSimpleName())) {
                if (TextUtils.isEmpty(userInfo.getRealName())) {
                    showUserInfoDialog();
                    return;
                }
            }
//                else {
//                    if (TextUtils.isEmpty(userInfo.getRealName()) || TextUtils
//                        .isEmpty(userInfo.getEmail())) {
//                        showUserInfoDialog2();
//                        return;
//
//                    }
//                }
//            }
            Intent intent = new Intent();
            intent.putExtra("userInfo", userInfo);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            isFirstPage = true;
            enterBasicLayout();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_top_back_btn:
                if (isFirstPage) {
//                    if (!origin.equals(UserInfoFragment.class.getSimpleName())) {
                    if (origin.equals(RegisterFragment.class.getSimpleName())) {
                        if (TextUtils.isEmpty(userInfo.getRealName())) {
                            showUserInfoDialog();
                            return;
                        }
                    }
//                        else {
//                            if (TextUtils.isEmpty(userInfo.getRealName()) || TextUtils
//                                .isEmpty(userInfo.getEmail())) {
//                                showUserInfoDialog2();
//                                return;
//
//                            }
//                        }
//                    }
                    Intent intent = new Intent();
                    intent.putExtra("userInfo", userInfo);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    isFirstPage = true;
                    enterBasicLayout();
                }
                break;
            case R.id.toolbar_top_commit_btn:
                if (userInfo != null) {
                    UIUtils.hideSoftKeyboard(BasicUserInfoActivity.this);

                    saveBasicUserInfo(position);
                }
                break;
            case R.id.basic_userinfo_male_layout:
                sexType = 0;
                setSexLayout(0);
                break;
            case R.id.basic_userinfo_female_layout:
                sexType = 1;
                setSexLayout(1);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        if (position == AVATAR){
            //修改头像
            modifyAvatar();

        }else if (position == MODIFY_PWD){
            //修改密码
            enterModifyPasswordActivity();

        }else if (position == ROLE){
            //角色信息
            Intent intent = new Intent();
            intent.setClass(BasicUserInfoActivity.this, RoleBindActivity.class);
            startActivity(intent);

        }else {
            //其他情况
            enterEditLayout();
        }
    }

    private void modifyAvatar() {
        if (!new File(Utils.ICON_FOLDER).exists()) {
            new File(Utils.ICON_FOLDER).mkdirs();
        }

        showImagePopupView();

    }

    private void showImagePopupView() {
        ImagePopupView imagePopupView = new ImagePopupView(this, false);
        imagePopupView.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content)
                , Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }



    private void processUploadAvatarResult(String iconUrl) {

        if (!TextUtils.isEmpty(iconUrl)) {
            //更新头像信息
            userInfo.setHeaderPic(iconUrl);
            basicInfoAdapter.notifyDataSetChanged();
            thumbnailManager.displayThumbnailWithDefault(
                    AppSettings.getFileUrl(iconUrl), basicInfoAdapter.getAvatarView(),
                    R.drawable.default_user_icon);
        }

    }



    private void setSexLayout(int sexType) {
        if (sexType == 0) {
            maleLayout.getChildAt(1).setVisibility(View.VISIBLE);
            femaleLayout.getChildAt(1).setVisibility(View.INVISIBLE);
        } else {
            maleLayout.getChildAt(1).setVisibility(View.INVISIBLE);
            femaleLayout.getChildAt(1).setVisibility(View.VISIBLE);
        }

    }

    private void showUserInfoDialog() {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
            BasicUserInfoActivity.this,
            null, getString(R.string.pls_input_user_info),
            getString(R.string.no),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            },
            getString(R.string.yes),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        dialog.show();
    }

    private void showUserInfoDialog2() {
        ContactsMessageDialog dialog = new ContactsMessageDialog(
            BasicUserInfoActivity.this,
            null, getString(R.string.pls_input_name_email),
            getString(R.string.confirm),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            },
            null, null);
        dialog.show();
    }

    private void saveBasicUserInfo(int position) {
        if (position < 0) {
            return;
        }
        boolean isSave = false;
        String content = contentTxt.getText().toString().trim();
        mChangeField = new UserInfoChangeField();
        mChangeField.mFieldId = position;
        switch (position) {
            case ACCOUNT:
                if (TextUtils.isEmpty(content)) {
                    TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.pls_input_username));
                    return;
                } else {
                    //过滤特殊字符和表情
                    if (!Utils.checkEditTextValid(BasicUserInfoActivity.this,content)){
                        return;
                    }
                    int length = content.length();
                    if(length < ACCOUNT_MIN_LEN || length > ACCOUNT_MAX_LEN || TextUtils.isDigitsOnly(content)) {
                        TipMsgHelper.ShowLMsg(BasicUserInfoActivity.this, R.string.user_account_hint);
                        return;
                    }
                    if (TextUtils.isEmpty(userInfo.getNickName()) || !content.equals(userInfo.getNickName())) {
                        isSave = true;
                        userInfo.setNickName(content);
                    }

                }
                break;
            case REALNAME:
                if (TextUtils.isEmpty(content)) {
                    TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.pls_input_realname));
                    return;
                } else {
                    //过滤特殊字符和表情
                    if (!Utils.checkEditTextValid(BasicUserInfoActivity.this,content)){
                        return;
                    }
                    //用户真实姓名<=20个字符
                    int length = content.length();
                    if (length > 20){
                        TipMsgHelper.ShowMsg(BasicUserInfoActivity.this,
                                getString(R.string.real_name_length_is_not_legal));
                        return;
                    }
                    if (TextUtils.isEmpty(userInfo.getRealName()) || !userInfo.getRealName().equals(content)) {
                        isSave = true;
                        userInfo.setRealName(content);
                    }
                }
                break;
            case BIND_PHONE:
                if (TextUtils.isEmpty(content)) {
//                    TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.pls_input_mobile));
//                    return;
                    if (!TextUtils.isEmpty(userInfo.getBindMobile())) {
                        isSave = true;
                        userInfo.setBindMobile(null);
                    }
                } else {
                    if (!Utils.isCellularPhoneNumber2(content)) {
                        TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.wrong_phone_number));
                        return;
                    } else {
                        if (!content.equals(userInfo.getBindMobile())) {
                            isSave = true;
                            userInfo.setBindMobile(content);
                        }
                    }
                }
                break;
            case CONTACT_PHONE:
                if (TextUtils.isEmpty(content)) {
//                    TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.pls_input_contact_phone));
//                    return;
                    if (!TextUtils.isEmpty(userInfo.getMobile())) {
                        isSave = true;
                        userInfo.setMobile(null);
                    }
                } else {
                    if (!Utils.isPhoneNumber(content)) {
                        TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.contact_phone_wrong));
                        return;
                    } else {
                        if (!content.equals(userInfo.getMobile())) {
                            isSave = true;
                            userInfo.setMobile(content);
                        }
                    }
                }
                break;
            case MAIL:
                if (TextUtils.isEmpty(content)) {
//                    TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.pls_input_email));
//                    return;
                    if (!TextUtils.isEmpty(userInfo.getEmail())) {
                        isSave = true;
                        userInfo.setEmail(null);
                    }
                } else {
                    if (!Utils.isEmail(content)) {
                        TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.wrong_email_format));
                        return;
                    } else {
                        if (TextUtils.isEmpty(userInfo.getEmail()) || !userInfo.getEmail().equals(content)) {
                            isSave = true;
                            userInfo.setEmail(content);
                        }
                    }
                }
                break;
            case SEX:
//                String sex = sexType == 0 ? getString(R.string.male) : getString(R.string.female);
                String sex = sexType == 0 ? "男" : "女";
                if (TextUtils.isEmpty(userInfo.getSex()) || !userInfo.getSex().equals(sex)) {
                    isSave = true;
                    userInfo.setSex(sex);
                    mChangeField.mStrValue = sex;
                }
                break;
            case BIRTHDAY:
                String birthday = calendarView.GetCurrentTime();
                if (!TextUtils.isEmpty(birthday)) {
                    int result = DateUtils.compareDate(birthday, DateUtils.format(DateUtils.getCurDate(), DateUtils.DATE_PATTERN_yyyy_MM_dd).toString(), DateUtils.DATE_PATTERN_yyyy_MM_dd);
                    if (result == 1 || result == 0) {
                        //出生日期不能大于当期的日期
                        TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, R.string.str_birthday_need_greater_than_today);
                        return;
                    }
                }
                if (TextUtils.isEmpty(userInfo.getBirthday()) || !userInfo.getBirthday().equals(birthday)) {
                    isSave = true;
                    userInfo.setBirthday(birthday);
                    mChangeField.mStrValue = birthday;
                }
                break;
            case INTRO:
            case LOCATION:
                if (TextUtils.isEmpty(content)) {
//                    if (position == INTRO) {
//                        TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.pls_input_intro));
//                    } else {
//                        TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, getString(R.string.pls_input_location));
//                    }
//                    return;
                    if (position == INTRO) {
                        if (!TextUtils.isEmpty(userInfo.getPIntroduces())) {
                            isSave = true;
                            userInfo.setPIntroduces(null);
                            mChangeField.mStrValue = null;
                        }
                    } else {
                        if (!TextUtils.isEmpty(userInfo.getLocation())) {
                            isSave = true;
                            userInfo.setLocation(null);
                            mChangeField.mStrValue = null;
                        }
                    }

                } else {
                    if (position == INTRO) {
                        if (TextUtils.isEmpty(userInfo.getPIntroduces()) || !content.equals(userInfo.getPIntroduces())) {
                            isSave = true;
                            userInfo.setPIntroduces(content);
                            mChangeField.mStrValue = content;
                        }
                    } else {
                        if (TextUtils.isEmpty(userInfo.getLocation()) || !content.equals(userInfo.getLocation())) {
                            isSave = true;
                            userInfo.setLocation(content);
                            mChangeField.mStrValue = content;
                        }
                    }
                }
                break;
        }
        if (isSave) {
            saveBasicUserInfo(userInfo);
            userInfo = ((MyApplication) getApplication()).getUserInfo();
            if (position != SEX && position != BIRTHDAY) {
                mChangeField.mStrValue = content;
            }
        } else {
            if (isFirstPage) {
                Intent intent = new Intent();
                intent.putExtra("userInfo", userInfo);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                isFirstPage = true;
                enterBasicLayout();
            }
        }
    }

    final int CHAR_AND_NUM = 1;
    final int PHONE_NUM = 2;
    final int MOBILE_NUM = 3;
    final int ALL = 4;

    private void setAcceptCharsAndNum(int acceptType) {
        if (ALL == acceptType) {
            contentTxt.setKeyListener(new TextKeyListener(Capitalize.CHARACTERS, false) {
                @Override
                public int getInputType() {
                    // TODO Auto-generated method stub
                    return EditorInfo.TYPE_CLASS_TEXT;
                }
            });
        } else {
            final char[] acceptChars;
            final int inputType;
            if (CHAR_AND_NUM == acceptType) {
                acceptChars = Utils.getAcceptedCharNumChars();
                inputType = EditorInfo.TYPE_CLASS_TEXT;
            } else if (PHONE_NUM == acceptType) {
                acceptChars = "0123456789-".toCharArray();
                inputType = EditorInfo.TYPE_CLASS_PHONE;
            } else {
                acceptChars = "0123456789".toCharArray();
                inputType = EditorInfo.TYPE_CLASS_PHONE;
            }
            contentTxt.setKeyListener(new NumberKeyListener() {

                @Override
                public int getInputType() {
                    // TODO Auto-generated method stub
                    return inputType;
                }

                @Override
                protected char[] getAcceptedChars() {
                    // TODO Auto-generated method stub
                    return acceptChars;
                }
            });
        }
    }

    private void setEditTextInputLimit(final boolean limit){
        if (contentTxt == null){
            return;
        }
        contentTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && limit){
                    return true;
                }
                return false;
            }
        });
    }

    private void saveBasicUserInfo(final UserInfo userInfo) {
        String url = ServerUrl.SAVE_USERINFO_URL;
        if (!TextUtils.isEmpty(url)) {
            showLoadingDialog();
            Map<String, String> mParams = new HashMap<String, String>();
            mParams.put("UserId", userInfo.getMemberId());
            mParams.put("NickName", userInfo.getNickName());
            if (!TextUtils.isEmpty(userInfo.getRealName())) {
                mParams.put("RealName", userInfo.getRealName());
            } else {
                mParams.put("RealName", "");
            }
            if (!TextUtils.isEmpty(userInfo.getSex())) {
                mParams.put("Sex", userInfo.getSex());
            } else {
                mParams.put("Sex", "");
            }
            if (!TextUtils.isEmpty(userInfo.getBindMobile())) {
                mParams.put("BindMobile", userInfo.getBindMobile());
            }
            if (!TextUtils.isEmpty(userInfo.getMobile())) {
                mParams.put("Mobile", userInfo.getMobile());
            }
            if (!TextUtils.isEmpty(userInfo.getEmail())) {
                mParams.put("Email", userInfo.getEmail());
            }
            if (!TextUtils.isEmpty(userInfo.getBirthday())) {
                mParams.put("Birthday", userInfo.getBirthday());
            }
            if (!TextUtils.isEmpty(userInfo.getPIntroduces())) {
                mParams.put("PIntroduces", userInfo.getPIntroduces());
            }
            if (!TextUtils.isEmpty(userInfo.getLocation())) {
                mParams.put("Location", userInfo.getLocation());
            }
            PostByMapParamsModelRequest request = new PostByMapParamsModelRequest(
                url, mParams, new Listener<String>() {
                @Override
                public void onSuccess(String json) {
                    try {
                        Log.i("", "Login:onSuccess " + json);
                        UserInfo basicUserInfo = JSON.parseObject(json, UserInfo.class);

                        if (basicUserInfo != null) {
                            // 更改信息成功后，更新Mooc的用户信息
                            // 用自己生成的UserInfo ，不用后台返回的
                            MOOCHelper.init(userInfo);
                            onChangeFieldSuccess();
                            if (isFirstPage) {
                                Intent intent = new Intent();
                                intent.putExtra("userInfo", basicUserInfo);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                isFirstPage = true;
                                enterBasicLayout();
                            }
                            TipMsgHelper.ShowLMsg(BasicUserInfoActivity.this, R.string.modify_success);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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
                                TipMsgHelper.ShowMsg(BasicUserInfoActivity.this, result.getErrorMessage());
                            }
                        }
                    } catch (Exception e) {
                        TipMsgHelper.ShowLMsg(BasicUserInfoActivity.this, getString(R.string.network_error));
                    }
                }

                @Override
                public void onFinish() {
                    // TODO Auto-generated method stub
                    super.onFinish();
                    dismissLoadingDialog();
                }
            });
            request.addHeader("Accept-Encoding", "*");
            request.setRetryPolicy(new DefaultRetryPolicy(60000, 0, DefaultRetryPolicy
                    .DEFAULT_BACKOFF_MULT));
            request.start(BasicUserInfoActivity.this);
        }
    }

    private void onChangeFieldSuccess() {
        if (mChangeField != null && mChangeField.mStrValue != null) {
            switch (mChangeField.mFieldId) {
                case ACCOUNT:
                    userInfo.setNickName(mChangeField.mStrValue);
                    break;
                case REALNAME:
                    userInfo.setRealName(mChangeField.mStrValue);
                    break;
                case BIND_PHONE:
                    userInfo.setBindMobile(mChangeField.mStrValue);
                    break;
                case CONTACT_PHONE:
                    userInfo.setMobile(mChangeField.mStrValue);
                    break;
                case MAIL:
                    userInfo.setEmail(mChangeField.mStrValue);
                    break;
                case SEX:
                    userInfo.setSex(mChangeField.mStrValue);
                    break;
                case BIRTHDAY:
                    userInfo.setBirthday(mChangeField.mStrValue);
                    break;
                case INTRO:
                    userInfo.setPIntroduces(mChangeField.mStrValue);
                    break;
                case LOCATION:
                    userInfo.setLocation(mChangeField.mStrValue);
                    break;

                default:
                    break;
            }
            MyApplication myApp = (MyApplication) getApplication();
            myApp.setUserInfo(this.userInfo);
        }
    }

    private class BasicInfo {
        int resId;
        String info;
        boolean isNull;
        String hint;
    }

    private class BasicInfoAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public BasicInfoAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return basicInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.userinfo_basic_info_item, null);
            }
            TextView nameTxt = (TextView) convertView.findViewById(R.id.basic_info_name_txt);
            TextView flagTxt = (TextView) convertView.findViewById(R.id.basic_info_flag_txt);
            TextView hintTxt = (TextView) convertView.findViewById(R.id.basic_info_hint_txt);
            TextView infoTxt = (TextView) convertView.findViewById(R.id.basic_info_txt);
            //头像
            ImageView avatar = (ImageView) convertView.findViewById(R.id.basic_info_avatar);

            hintTxt.setVisibility(position == MAIL ? View.VISIBLE : View.GONE);
            if (basicInfoList != null && basicInfoList.size() > 0) {
                BasicInfo basicInfo = basicInfoList.get(position);
                if (basicInfo != null) {
                    nameTxt.setText(basicInfo.resId);
                    infoTxt.setText(basicInfo.info);
                    flagTxt.setVisibility(!basicInfo.isNull ? View.VISIBLE : View.INVISIBLE);
                    //头像
                    if (position == 0){
                        avatar.setVisibility(View.VISIBLE);
                        thumbnailManager.displayUserIconWithDefault(
                                AppSettings.getFileUrl(userInfo.getHeaderPic()),avatar,R.drawable
                                        .default_user_icon);
                    }else {
                        avatar.setVisibility(View.GONE);
                    }
                }
            }
            return convertView;
        }

        public ImageView getAvatarView(){
            View rootView = mInflater.inflate(R.layout.userinfo_basic_info_item, null);
            return (ImageView) rootView.findViewById(R.id.basic_info_avatar);
        }
    }

    public static class UserInfoChangeField {
        int mFieldId;
        String mStrValue;
    }

    /*
 * 监听输入内容是否超出最大长度，并设置光标位置
 * */
    public class MaxLengthWatcher implements TextWatcher {

        private int maxLen = 0;
        private EditText editText = null;

        public MaxLengthWatcher(int maxLen, EditText editText) {
            this.maxLen = maxLen;
            this.editText = editText;
        }

        public void setMaxLen(int maxLen) {
            this.maxLen = maxLen;
        }

        public int getMaxLen() {
            return maxLen;
        }

        public void afterTextChanged(Editable arg0) {

        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            Editable editable = editText.getText();
            int len = editable.length();

            if (maxLen > 0 & len > maxLen) {
                int selEndIndex = Selection.getSelectionEnd(editable);
                String str = editable.toString();
                //截取新字符串
                String newStr = str.substring(0, maxLen);
                editText.setText(newStr);
                editable = editText.getText();

                //新字符串的长度
                int newLen = editable.length();
                //旧光标位置超过字符串长度
                if (selEndIndex > newLen) {
                    selEndIndex = editable.length();
                }
                //设置新光标所在的位置
                Selection.setSelection(editable, selEndIndex);
                TipMsgHelper.ShowLMsg(BasicUserInfoActivity.this,
                        getString(R.string.max_input_character_length,maxLen));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ActivityUtils.REQUEST_CODE_TAKE_PHOTO:
                File iconFile = new File(Utils.ICON_FOLDER + Utils.ICON_NAME);
                if (iconFile != null && iconFile.exists()) {
                    if (BasicUserInfoActivity.this == null) {
                        return;
                    }
                    File file = Utils.getZoomFile();
                    PhotoUtils.startZoomPhoto(BasicUserInfoActivity.this,iconFile,file,1,1,
                            Utils.USER_ICON_SIZE,Utils.USER_ICON_SIZE,PhotoUtils.REQUEST_CODE_ZOOM_PHOTO);
                }
                break;
            case ActivityUtils.REQUEST_CODE_FETCH_PHOTO:
                if (data != null) {
                    if (BasicUserInfoActivity.this == null) {
                        return;
                    }
                    String photo_path = null;

                    photo_path = PhotoUtils.getImageAbsolutePath(BasicUserInfoActivity.this, data
                            .getData());

                    if (TextUtils.isEmpty(photo_path)) {
                        return;
                    }
                    File file = Utils.getZoomFile();
                    PhotoUtils.startZoomPhoto(BasicUserInfoActivity.this,new File(photo_path),file,1,1,
                            Utils.USER_ICON_SIZE,Utils.USER_ICON_SIZE,PhotoUtils.REQUEST_CODE_ZOOM_PHOTO);
                }
                break;
            case ActivityUtils.REQUEST_CODE_ZOOM_PHOTO:
                String zoomIconPath = Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME;
                if (!TextUtils.isEmpty(zoomIconPath)) {
                    File iconZoomFile = new File(zoomIconPath);
                    if (iconZoomFile != null && iconZoomFile.length() > 0) {
                        String memberId = userInfo.getMemberId();
                        if (!TextUtils.isEmpty(memberId)) {

                            //上传头像
                            UploadImageTask uploadImageTask = new UploadImageTask(
                                    BasicUserInfoActivity.this,memberId, zoomIconPath);
                            uploadImageTask.setCallbackListener(new CallbackListener() {
                                @Override
                                public void onBack(Object result) {
                                    processUploadAvatarResult((String) result);
                                }
                            });
                            uploadImageTask.execute();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}
