package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.views.switchbutton.SwitchButton;
import com.galaxyschool.app.wawaschool.R;

/**
 * Created by E450 on 2016/12/8.
 */

public class NoticeAvoidDisturbFragment extends ContactsListFragment implements  CompoundButton.OnCheckedChangeListener{
    private SwitchButton voiceSwitchButton;
    private SwitchButton shakeSwitchButton;
    private boolean avoidVoiceOPenState=false;
    private boolean avoidShakeOPenState=false;
    private PrefsManager prefsManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notice_avoid_disturb, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       initView();
    }

    @Override
    public void onResume() {
        super.onResume();
       // loadViews();
    }
    private void initView(){
        MyApplication myApp=null;
        if(getActivity()!=null){
            myApp = (MyApplication) getActivity().getApplication();
        }
        if(myApp!=null){
            prefsManager = myApp.getPrefsManager();
        }
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setOnClickListener(this);
            textView.setText(R.string.avoid_disturb);
        }

        ImageView imageView = (ImageView)findViewById(R.id.contacts_header_left_btn);
        if(imageView != null) {
            imageView.setOnClickListener(this);
        }
        //声音
        voiceSwitchButton = (SwitchButton)findViewById(R.id.voice_switch_btn);
        avoidVoiceOPenState = prefsManager.getNoticeAvoidDisturbVoiceOpenState();
        voiceSwitchButton.setChecked(avoidVoiceOPenState);
        voiceSwitchButton.setOnCheckedChangeListener(this);
        //震动
        shakeSwitchButton = (SwitchButton)findViewById(R.id.shake_switch_btn);
        avoidShakeOPenState = prefsManager.getNoticeAvoidDisturbShakeOpenState();
        shakeSwitchButton.setChecked(avoidShakeOPenState);
        shakeSwitchButton.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId()==R.id.voice_switch_btn){
            avoidVoiceOPenState = isChecked;
            if(prefsManager!=null){
                prefsManager.setNoticeAvoidDisturbVoiceOpenState(avoidVoiceOPenState);
            }
        }else if(buttonView.getId()==R.id.shake_switch_btn){
            avoidShakeOPenState = isChecked;
            if(prefsManager!=null){
                prefsManager.setNoticeAvoidDisturbShakeOpenState(avoidShakeOPenState);
            }
        }
    }
}
