package com.galaxyschool.app.wawaschool.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.Emcee;
import com.galaxyschool.app.wawaschool.pojo.EmceeList;

import java.util.List;

public class ClassroomIntroductionFragment extends ContactsListFragment{
    private Emcee onlineRes;

    public ClassroomIntroductionFragment(Emcee onlineRes) {
        this.onlineRes = onlineRes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.classroom_introduction_item, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void initViews(){
        TextView tvHost= (TextView) findViewById(R.id.online_host_list);

        if (tvHost!=null){
            if (onlineRes != null) {
                StringBuilder builder = new StringBuilder();
                List<EmceeList> emceeLists=onlineRes.getEmceeList();
                if (emceeLists != null && emceeLists.size() > 0) {
                    for (int i = 0; i < emceeLists.size(); i++) {
                        String hostName = emceeLists.get(i).getRealName();
                        if (i == 0) {
                            builder.append(hostName);
                        } else {
                            builder.append("  ").append(hostName);
                        }
                    }
                }
                tvHost.setText(builder.toString());
                if (!TextUtils.isEmpty(onlineRes.getEmceeNames())) {
                    tvHost.setText(onlineRes.getEmceeNames());
                }
            }
        }
        TextView tvIntro= (TextView) findViewById(R.id.online_introduction);
        if (tvIntro!=null){
            if (onlineRes!=null){
                tvIntro.setText(onlineRes.getIntro());
            }
        }
    }
}
