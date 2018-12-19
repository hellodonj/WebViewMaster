package com.galaxyschool.app.wawaschool.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;

import java.util.ArrayList;
import java.util.List;

public class ApplicationModelSettingFragment extends ContactsListFragment {
    public static final String TAG = ApplicationModelSettingFragment.class.getSimpleName();
    private int currentModel = ApplicationModel.GENERAL_MODEL;
    private View rootView;
    private PrefsManager prefsManager;
    private TextView headTitle;
    private ImageView mBack,generalSelect,campustSelect;
    private LinearLayout generalLayout,campusLayout,campusIPLayout;
    private EditText et1,et2,et3,et4;
    private String campusIP;
    private boolean isFirstIn = true;
    private List<EditText> etList = new ArrayList<>();

    public interface ApplicationModel{
        int GENERAL_MODEL = 0;
        int CAMPUS_MODEL = 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_app_model_setting,null);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initListener();
        initData();
    }
    @Override
    public void onResume() {
        super.onResume();
        isFirstIn =false;
    }

    private void initView(){
        headTitle = (TextView) rootView.findViewById(R.id.contacts_header_title);
        headTitle.setText(getResources().getString(R.string.application_model));
        mBack = (ImageView) rootView.findViewById(R.id.contacts_header_left_btn);
        mBack.setOnClickListener(this);
        generalSelect = (ImageView) rootView.findViewById(R.id.general_icon);
        campustSelect = (ImageView) rootView.findViewById(R.id.campus_icon);
        generalLayout = (LinearLayout) rootView.findViewById(R.id.layout_application);
        generalLayout.setOnClickListener(this);
        campusLayout = (LinearLayout) rootView.findViewById(R.id.layout_campus);
        campusLayout.setOnClickListener(this);
        campusIPLayout = (LinearLayout) rootView.findViewById(R.id.layout_campus_ip);
        et1 = (EditText) rootView.findViewById(R.id.et1);
        et2 = (EditText) rootView.findViewById(R.id.et2);
        et3 = (EditText) rootView.findViewById(R.id.et3);
        et4 = (EditText) rootView.findViewById(R.id.et4);
        etList.add(et1);
        etList.add(et2);
        etList.add(et3);
        etList.add(et4);
    }
    private void initData(){
        prefsManager = DemoApplication.getInstance().getPrefsManager();
        if (prefsManager != null){
            String tempData = prefsManager.getCurrentApplicationModel(getActivity(),getMemeberId());
            if (!TextUtils.isEmpty(tempData)){
                currentModel = Integer.valueOf(tempData);
            }
            campusIP = prefsManager.getCampusModelIp(getActivity(),getMemeberId());
            if (TextUtils.isEmpty(campusIP)){
                //如果campusIP为空自动检测校内网有没有备份
                campusIP = prefsManager.getSchoolInsideWiFiHeadUrl(getActivity(),getMemeberId());
            }
        }
        changeModel(currentModel,true);
        showAlreadyCampusIP();
    }
    private void initListener(){
        for (int i = 0,len = etList.size();i < len;i++){
            final EditText editText = etList.get(i);
            EditText nextText = null;
            if ((i+1) < len){
                nextText = etList.get(i+1);
            }
            final EditText finalNextText = nextText;
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isFirstIn) {
                        String textContent = s.toString().trim();
                        if (!TextUtils.isEmpty(textContent) && textContent.length() > 2) {
                            int data = Integer.valueOf(textContent);
                            if (data >= 0 && data <= 255) {
                                if (finalNextText != null) {
                                    changeEditTextFocus(finalNextText);
                                }
                            } else {
                                popIPInputLegalError(editText);
                            }
                        }
                    }
                }
            });
        }
    }

    private void changeEditTextFocus(EditText nextFocus){
        nextFocus.setFocusable(true);
        nextFocus.setFocusableInTouchMode(true);
        nextFocus.requestFocus();
        nextFocus.findFocus();
        String text = nextFocus.getText().toString();
        if (!TextUtils.isEmpty(text)){
            nextFocus.setSelection(text.length());
        }
    }
    private void popIPInputLegalError(final EditText editText){
        String content = getString(R.string.check_ip_input_legal,editText.getText().toString());
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", content,
                "",null, getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                editText.setText("");
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();
    }
    private void changeModel(int model,boolean isFirstIn){
        if (currentModel == model && !isFirstIn) return;
        if (model == ApplicationModel.GENERAL_MODEL){
            generalSelect.setImageResource(R.drawable.noc_select_logo);
            campustSelect.setImageResource(R.drawable.noc_unselect_logo);
            campusIPLayout.setVisibility(View.GONE);
            hideSoftKeyboard();
        }else if (model == ApplicationModel.CAMPUS_MODEL){
            generalSelect.setImageResource(R.drawable.noc_unselect_logo);
            campustSelect.setImageResource(R.drawable.noc_select_logo);
            campusIPLayout.setVisibility(View.VISIBLE);
        }
        currentModel = model;
    }
    private void showAlreadyCampusIP(){
        if (!TextUtils.isEmpty(campusIP)){
            String [] splitArrray = campusIP.split("\\.");
            if (splitArrray != null && splitArrray.length != 0){
                for (int i = 0,len = splitArrray.length;i < len;i++){
                    String ipDetail = splitArrray[i];
                    etList.get(i).setText(ipDetail);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.contacts_header_left_btn){
            //结束当前的view
            finishCurrentView();
        }else if (v.getId() == R.id.layout_application){
            //通用模式
            changeModel(ApplicationModel.GENERAL_MODEL,false);
        }else if (v.getId() == R.id.layout_campus){
            //校本模式
            changeModel(ApplicationModel.CAMPUS_MODEL,false);
        }
    }
    /**
     * 结束当前的view
     */
    public void finishCurrentView(){
        //返回和保存 当前选择的数据
        if (currentModel == ApplicationModel.GENERAL_MODEL) {
            prefsManager.saveCurrentApplicationModel(getActivity(), getMemeberId(), String.valueOf
                    (currentModel));
            getActivity().finish();
        } else {
            saveCampusData();
        }
    }

    private void saveCampusData(){
        String ipConfig = getCampusModelIP();
        if (!TextUtils.isEmpty(ipConfig)){
            prefsManager.saveCampusModelIp(getActivity(),getMemeberId(),ipConfig);
            prefsManager.saveCurrentApplicationModel(getActivity(),getMemeberId(),String.valueOf
                    (currentModel));
            getActivity().finish();
        }
    }
    private String getCampusModelIP(){
        StringBuilder builder = new StringBuilder();
        for (int i = 0,len = etList.size();i < len;i++){
            EditText editText = etList.get(i);
            String etContent = editText.getText().toString();
            if (TextUtils.isEmpty(etContent)){
                showCampusIPErrorDialog();
                return null;
            }else {
                if (i == 0){
                    builder.append(etContent);
                }else {
                    builder.append(".").append(etContent);
                }
            }
        }
        return builder.toString();
    }

    private void showCampusIPErrorDialog() {
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", getString(R
                .string.pls_input_complete_ip),
                "",null, getString(R.string.str_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.dismiss();
            }
        });
        dialog.setIsAutoDismiss(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            boolean flag = false;
            for (int i = 0, len = etList.size(); i < len; i++) {
                EditText editText = etList.get(i);
                if (editText.hasFocus()) {
                    flag = true;
                    imm.hideSoftInputFromWindow((editText.getWindowToken()), InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
                }
            }
            if (!flag) {
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
            }
        }
    }
}
