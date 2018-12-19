package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.NocUserInfoActivity;
import com.galaxyschool.app.wawaschool.R;

public class NocEntryInfoFragment extends ContactsListFragment {

    public static final String TAG = NocEntryInfoFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_entry_info, null);
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
        findViewById(R.id.agree_btn).setOnClickListener(this);
    }
   @Override
   public void onClick(View v) {
             super.onClick(v);
       if(v.getId()==R.id.agree_btn){
           enterNocUserInfo();
       }
   }
    private void enterNocUserInfo(){
        Intent intent=new Intent(getActivity(), NocUserInfoActivity.class);
        startActivity(intent);
    }
 }
