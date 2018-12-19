package com.galaxyschool.app.wawaschool.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.CampusPatrolPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.views.MyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 校园巡查主界面
 */
public class CampusPatrolMainFragment extends ContactsListFragment {

    public static final String TAG = CampusPatrolMainFragment.class.getSimpleName();
    private TextView leftTab,rightTab ;
    private Fragment leftFragment,rightFragment;
    private MyViewPager viewPager;
    private static final int INDEX_LEFT_TAB = 0;
    private static final int INDEX_RIGHT_TAB = 1;
    private int tabIndex = INDEX_LEFT_TAB;
    private String startDate,endDate;
    public static final String IS_CAMPUS_PATROL_TAG = "is_campus_patrol_tag";
    public static final String CAMPUS_PATROL_RESOURCE_NAME = "campus_patrol_resource_name";
    public static final String CAMPUS_PATROL_RESOURCE_ID = "campus_patrol_resource_id";
    public static final String CAMPUS_PATROL_RESOURCE_COUNT_STR = "campus_patrol_resource_count_str";
    public static final String CAMPUS_PATROL_RESOURCE_DATA = "campus_patrol_resource_data";
    public static final String CAMPUS_PATROL_SCREENING_START_DATE = "campus_patrol_screening_start_date";
    public static final String CAMPUS_PATROL_SCREENING_END_DATE = "campus_patrol_screening_end_date";
    public static final String CAMPUS_PATROL_SEARCH_TYPE = "campus_patrol_search_type";
    public static final int CAMPUS_PATROL_SEARCH_TYPE_TEACHER = 3;
    public static final int CAMPUS_PATROL_SEARCH_TYPE_CLASS = 4;
    public static final String CAMPUS_PATROL_RESOURCE_TYPE = "campus_patrol_resource_type";
    public static final int CAMPUS_PATROL_RESOURCE_TYPE_CLASS = 1;
    public static final int CAMPUS_PATROL_RESOURCE_TYPE_USER = 2;
    public static final String CAMPUS_PATROL_TASK_TYPE = "campus_patrol_task_type";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.campus_patrol_main, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        switchTabByEnableState();
    }

    private void initViews() {

        ImageView imageView = (ImageView) findViewById(R.id.header_left_btn);
        if (imageView != null) {
            imageView.setOnClickListener(this);
        }

        TextView textView = (TextView) findViewById(R.id.header_left_tab);
        if (textView != null) {
            textView.setText(getString(R.string.according_to_teacher));
            textView.setOnClickListener(this);
        }
        this.leftTab = textView;

        textView = (TextView) findViewById(R.id.header_right_tab);
        if (textView != null) {
            textView.setText(getString(R.string.according_to_class));
            textView.setOnClickListener(this);
        }
        this.rightTab = textView;

        textView = ((TextView) findViewById(R.id.header_right_btn));
        if (textView != null) {
            textView.setText(getString(R.string.screening));
            textView.setOnClickListener(this);
        }

        this.leftTab.setEnabled(false);
        this.rightTab.setEnabled(true);
        initFragments();
    }

    private void initFragments() {
        this.viewPager = (MyViewPager) getView().findViewById(R.id.contacts_view_pager);
        List<Fragment> fragments = new ArrayList<Fragment>();
        Bundle bundle = getArguments();

        this.leftFragment = new CampusPatrolTeacherListFragment();
        this.leftFragment.setArguments(bundle);
        fragments.add(this.leftFragment);

        this.rightFragment = new CampusPatrolClassListFragment();
        this.rightFragment.setArguments(bundle);
        fragments.add(this.rightFragment);

        FragmentPagerAdapter fragmentAdapter = new MyFragmentPagerAdapter(
                getChildFragmentManager(), fragments);
        this.viewPager.setAdapter(fragmentAdapter);
    }

    private void switchLeftTab() {
        this.viewPager.setCurrentItem(0);
    }

    private void switchRightTab() {
        this.viewPager.setCurrentItem(1);
    }

    private void switchTabByEnableState(){
        if (!this.leftTab.isEnabled()){
            switchLeftTab();
        }else {
            switchRightTab();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_left_btn) {
            if (getActivity() != null){
                getActivity().finish();
            }
        }else if (v.getId() == R.id.header_left_tab) {
            tabIndex = INDEX_LEFT_TAB;
            this.leftTab.setEnabled(false);
            this.rightTab.setEnabled(true);
            switchTabByEnableState();
        } else if (v.getId() == R.id.header_right_tab) {
            tabIndex = INDEX_RIGHT_TAB;
            this.rightTab.setEnabled(false);
            this.leftTab.setEnabled(true);
            switchTabByEnableState();
        }else if (v.getId() == R.id.header_right_btn){
            //筛选
            enterCampusPatrolPickerActivity(tabIndex);
        }
    }

    private void enterCampusPatrolPickerActivity(int tabIndex) {
        Intent intent = new Intent(getActivity(), CampusPatrolPickerActivity.class);
        startActivityForResult(intent,tabIndex);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null){
                if (resultCode == CampusPatrolPickerFragment.RESULT_CODE){
                    //筛选结果
                    startDate = CampusPatrolUtils.getStartDate(data);
                    endDate = CampusPatrolUtils.getEndDate(data);
                    if (requestCode == INDEX_LEFT_TAB){
                        //按老师筛选刷新
                        ((CampusPatrolTeacherListFragment)this.leftFragment).
                                refreshData(startDate,endDate);

                    }else if (requestCode == INDEX_RIGHT_TAB){
                        //按班级筛选刷新
                        ((CampusPatrolClassListFragment)this.rightFragment).
                                refreshData(startDate,endDate);
                    }
                }
        }
    }
}
