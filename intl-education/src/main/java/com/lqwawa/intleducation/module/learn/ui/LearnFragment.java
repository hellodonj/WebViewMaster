package com.lqwawa.intleducation.module.learn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.ui.SearchActivity;

/**
 * Created by XChen on 2016/11/2.
 * email:man0fchina@foxmail.com
 */

public class LearnFragment extends MyBaseFragment implements View.OnClickListener{
    private static final String TAG = "LearnFragment";


    private TopBar topBar;
    private RadioGroup rg_tab;


    MyCourseListFragment courseListFragment;
    MyCredentialListFragment credentialListFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_learn, container, false);
        topBar  = (TopBar)view.findViewById(R.id.top_bar);
        rg_tab = (RadioGroup)view.findViewById(R.id.rg_tab);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews(){
        topBar.setTitle(getResources().getString(R.string.learn));
        topBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, SearchActivity.class));
            }
        });

        courseListFragment = new MyCourseListFragment();
        credentialListFragment = new MyCredentialListFragment();

        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, courseListFragment);
        fragmentTransaction.add(R.id.fragment_container, credentialListFragment);
        fragmentTransaction.hide(credentialListFragment);
        fragmentTransaction.show(courseListFragment);
        courseListFragment.setUserVisibleHint(true);
        fragmentTransaction.commit();

        rg_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction fragmentTransaction =
                        getChildFragmentManager().beginTransaction();
                fragmentTransaction.hide(courseListFragment);
                fragmentTransaction.hide(credentialListFragment);
                courseListFragment.setUserVisibleHint(false);
                if (checkedId == R.id.rb_course) {
                    fragmentTransaction.show(courseListFragment);
                } else if (checkedId == R.id.rb_credential) {
                    fragmentTransaction.show(credentialListFragment);
                }
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
    }

    @Override
    public void onClick(View view){

    }
}
