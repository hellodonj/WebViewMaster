package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.NocResourceInfoActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SelectSchoolActivity;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.NocUserInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.lqwawa.apps.views.ContainsEmojiEditText;

import java.util.HashMap;
import java.util.Map;

public class NocUserInfoFragment extends ContactsListFragment {


    public static final String TAG = NocUserInfoFragment.class.getSimpleName();
    public static final int REQUESE_CODE_SCHOOL = 2000;
    private TextView shcoolNameTextView;
    private ContainsEmojiEditText phoneView;
    private ContainsEmojiEditText usernameView;
    private ContainsEmojiEditText mailView;
    private TextView schoolPhoneView;
    private TextView addressView;
    private TextView areaContentView;
    private boolean namePerson=true;//是否一个人名义
    private ImageView personNameView;
    private ImageView schoolNameView;
    private NocUserInfo nocUserInfo   = new NocUserInfo();
    private SchoolInfo schoolInfo;//选择来的学校
    private boolean hasSelectData=false;
    private ImageView selectResourceBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_user_info, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        initViews();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        textView.setText(R.string.join_game);
        selectResourceBtn= (ImageView) findViewById(R.id.add_school_btn);
        selectResourceBtn.setOnClickListener(this);
        shcoolNameTextView= (TextView) findViewById(R.id.school_name_view);
        findViewById(R.id.sure_view).setOnClickListener(this);
        findViewById(R.id.cancel_view).setOnClickListener(this);
        phoneView= (ContainsEmojiEditText) findViewById(R.id.phone_view);
        usernameView= (ContainsEmojiEditText) findViewById(R.id.username_view);
        schoolPhoneView= (TextView) findViewById(R.id.school_phone_view);
        mailView= (ContainsEmojiEditText) findViewById(R.id.mail_view);
        addressView= (TextView) findViewById(R.id.address_view);

        //获取焦点：
        usernameView.setFocusable(true);
        usernameView.setFocusableInTouchMode(true);
        usernameView.requestFocus();
        usernameView.requestFocusFromTouch();
        areaContentView= (TextView) findViewById(R.id.area_content_view);
        findViewById(R.id.person_namme_layout).setOnClickListener(this);
        findViewById(R.id.school_namme_layout).setOnClickListener(this);
        personNameView = (ImageView) findViewById(R.id.person_namme_view);
        schoolNameView = (ImageView) findViewById(R.id.school_namme_view);
        intSelectView();
    }
    private void intSelectView(){
        personNameView.setSelected(namePerson);
        schoolNameView.setSelected(!namePerson);
    }
   @Override
   public void onClick(View v) {
      super.onClick(v);
       switch (v.getId()){
           case R.id.add_school_btn:
               addSchool();
               break;
           case R.id.sure_view:
                sure();
               break;
           case R.id.cancel_view:
               finish();
               break;
           case R.id.person_namme_layout:
               namePerson=!namePerson;
               intSelectView();
               break;
           case R.id.school_namme_layout:
               namePerson=!namePerson;
               intSelectView();
               break;

       }
   }
    private void sure(){
        if(TextUtils.isEmpty(usernameView.getText().toString().trim())){
            TipsHelper.showToast(getActivity(),getString(R.string.please_finish_info));
            return;
        }else  if(TextUtils.isEmpty(phoneView.getText().toString().trim())){
            TipsHelper.showToast(getActivity(),getString(R.string.please_finish_info));
            return;
        }else   if (!Utils.isPhoneNumber(phoneView.getText().toString().trim())) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.contact_phone_wrong));
            return;
        }  else  if (!TextUtils.isEmpty(mailView.getText().toString().trim()) && !Utils
                .isEmailAddress(mailView.getText().toString().trim())) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.wrong_email_format));
            return;
        } else  if(!namePerson&&TextUtils.isEmpty(shcoolNameTextView.getText().toString().trim())){
            TipsHelper.showToast(getActivity(),getString(R.string.please_finish_school));
            return;
        } else{

            nocUserInfo.setUsername(usernameView.getText().toString().trim());
            nocUserInfo.setUserPhone(phoneView.getText().toString().trim());
            nocUserInfo.setUserMail(mailView.getText().toString().trim());
            nocUserInfo.setSchoolName(shcoolNameTextView.getText().toString().trim());
            nocUserInfo.setSchoolPhone(schoolPhoneView.getText().toString().trim());
            nocUserInfo.setDistrict(areaContentView.getText().toString().trim());
            nocUserInfo.setAddress(addressView.getText().toString().trim());
            nocUserInfo.setJoinNameFor(namePerson?NocUserInfo.JOIN_NAME_FOR_PERSONAL:NocUserInfo.JOIN_NAME_FOR_SCHOOL);
            Intent intent = new Intent(getActivity(),NocResourceInfoActivity.class);
            intent.putExtra(NocUserInfo.class.getSimpleName(),nocUserInfo);
            startActivity(intent);
        }
    }


    private void addSchool(){
        if(hasSelectData){
            hasSelectData=false;
            selectResourceBtn.setSelected(hasSelectData);
            schoolInfo=null;
            shcoolNameTextView.setText("");
            schoolPhoneView.setText("");
            addressView.setText("");
            areaContentView.setText("");
        }else{
            Intent intent=new Intent(getActivity(), SelectSchoolActivity.class);
            startActivityForResult(intent,REQUESE_CODE_SCHOOL);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESE_CODE_SCHOOL&&resultCode== Activity.RESULT_OK){
          schoolInfo= (SchoolInfo)data.getSerializableExtra(SchoolInfo.class.getSimpleName());
            if(schoolInfo!=null){
                nocUserInfo.setSchoolId(schoolInfo.getSchoolId());
                shcoolNameTextView.setText(schoolInfo.getSchoolName());
                loadSchool(schoolInfo.getSchoolId());
                hasSelectData=true;
                selectResourceBtn.setSelected(hasSelectData);
            }
        }
    }
    /**
     * 获取学校详情 地址、电话
     * @param id
     */
    private void loadSchool(final String id ) {
        if (getUserInfo() == null){
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", id);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
                new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()) {
                            return;
                        }
                        schoolInfo = getResult().getModel();
                        schoolPhoneView.setText(schoolInfo.getSchoolPhone());
                        addressView.setText(schoolInfo.getSchoolAddress());
                        String areaName="";
                        if(!TextUtils.isEmpty(schoolInfo.getPName())){
                            areaName+=schoolInfo.getPName();
                        }if(!TextUtils.isEmpty(schoolInfo.getCName())){
                            areaName+=schoolInfo.getCName();
                        }if(!TextUtils.isEmpty(schoolInfo.getDName())){
                            areaName+=schoolInfo.getDName();
                        }
                        areaContentView.setText(areaName);
                    }
                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }

                    @Override
                    public void onError(NetroidError error) {
                        super.onError(error);
                    }
                });
    }
}
