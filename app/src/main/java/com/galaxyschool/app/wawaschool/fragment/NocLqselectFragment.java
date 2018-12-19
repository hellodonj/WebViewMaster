package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KnIghT on 16-5-10.
 */
public class NocLqselectFragment extends ContactsListFragment {
    public static final String TAG = NocLqselectFragment.class.getSimpleName();
    public static final int DATA_TYPE_LOCAL =0;
    public static final int DATA_TYPE_REMOTE =1;
    private MyViewPager mViewPager;
    List<Fragment> mFragments;
    MyFragmentPagerAdapter fragmentAdapter;
    LocalCourseSelectFragment localCourseFragment;
    NocCoursePickerFragment nocCoursePickerFragment;
    private TextView leftTab, rightTab;
    public static LocalCourseInfo selectData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noc_lq_select, null);
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


    private void initFragment() {

        mViewPager = (MyViewPager) getView().findViewById(R.id.viewpager);
        this.mFragments = new ArrayList<Fragment>();
        localCourseFragment = new LocalCourseSelectFragment();
        Bundle args = new Bundle();
        localCourseFragment.setArguments(args);
        this.mFragments.add(localCourseFragment);
        nocCoursePickerFragment = new NocCoursePickerFragment();
        this.mFragments.add(nocCoursePickerFragment);
        this.fragmentAdapter = new MyFragmentPagerAdapter(
                getChildFragmentManager(), mFragments);
        mViewPager.setAdapter(this.fragmentAdapter);
    }



    private void initViews() {

        ImageView    rightImageView = ((ImageView) findViewById(R.id.course_picker_back_btn));
        if (rightImageView != null) {
            //取消返回键
            rightImageView.setVisibility(View.GONE);
            rightImageView.setOnClickListener(this);
        }
        TextView  textView = (TextView) findViewById(R.id.tab_left);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.leftTab = textView;

        textView = (TextView) findViewById(R.id.tab_right);
        if (textView != null) {
            textView.setOnClickListener(this);
        }
        this.rightTab = textView;
        initFragment();
        this.mViewPager.setCurrentItem(0);
        this.leftTab.setEnabled(false);
        this.rightTab.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.course_picker_back_btn:
                if(getActivity() != null) {
                    getActivity().finish();
                }
                break;
            case R.id.tab_left:
                this.rightTab.setEnabled(true);
                this.leftTab.setEnabled(false);
                this.mViewPager.setCurrentItem(0);
                break;
            case R.id.tab_right:
                this.rightTab.setEnabled(false);
                this.leftTab.setEnabled(true);
                this.mViewPager.setCurrentItem(1);
                break;
            case R.id.course_picker_commit_btn:
//                if(LqCourseSelectFragment.selectData!=null&& LqCourseSelectFragment.selectData.isSelect()){
//                    if(lqCourseSelectFragment!=null){
//                        lqCourseSelectFragment.chooseCourseToSend();
//                    }
//                }else if(LocalCourseFragment.selectData!=null&& LocalCourseFragment.selectData.mIsCheck){
//                    if(localCourseFragment!=null){
//                        localCourseFragment.chooseCourseToSend();
//                    }
//                }else{
//                      TipMsgHelper.ShowLMsg(getActivity(), R.string.no_file_select);
//                }
                break;

        }
    }

}
