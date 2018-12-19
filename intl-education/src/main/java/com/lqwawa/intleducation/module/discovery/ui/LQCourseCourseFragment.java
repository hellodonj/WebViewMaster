package com.lqwawa.intleducation.module.discovery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by XChen on 2017/8/9.
 * email:man0fchina@foxmail.com
 */

public class LQCourseCourseFragment extends MyBaseFragment {
    //头部
    private TopBar topBar;
    private RadioGroup radioGroupTopFilter;
    private RadioButton radioButtonTopFilter1;
    private RadioButton radioButtonTopFilter2;
    private RadioButton radioButtonTopFilter3;
    private RadioButton radioButtonTopFilter4;
    private LQCourseCourseListFragment fragment1;
    private LQCourseCourseListFragment fragment2;
    private LQCourseCourseListFragment fragment3;
    private LQCourseCourseListFragment fragment4;

    private static int selectPosition = 0;//学段选择的索引值
    private int topPosition = 0;//分类顶层选中的索引值

    private List<ClassifyVo> inClassifyList;
    LQCourseCourseListFragment.OnCourseSelListener onCourseSelListener = null;
    private boolean hideSort;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_lq_course_course_list, container, false);
        topBar = (TopBar)view.findViewById(R.id.top_bar);
        radioGroupTopFilter = (RadioGroup) view.findViewById(R.id.rg_top_filter);
        radioButtonTopFilter1 = (RadioButton) view.findViewById(R.id.rb_top_filter1);
        radioButtonTopFilter2 = (RadioButton) view.findViewById(R.id.rb_top_filter2);
        radioButtonTopFilter3 = (RadioButton) view.findViewById(R.id.rb_top_filter3);
        radioButtonTopFilter4 = (RadioButton) view.findViewById(R.id.rb_top_filter4);

        selectPosition = SharedPreferencesHelper.getInt(activity, "LQCourseTopPosition", 0);

        hideSort = activity.getIntent().getBooleanExtra("hideSort",false);
        if (hideSort) {
            view.findViewById(R.id.rg_top_filter).setVisibility(View.GONE);
            view.findViewById(R.id.line_view).setVisibility(View.GONE);
        }
        topBar.setBack(true);
        inClassifyList = (List<ClassifyVo>) (activity.getIntent().getSerializableExtra("classifyList"));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(inClassifyList != null) {
            String paramCourseName = activity.getIntent().getStringExtra("CourseName");
            if (!StringUtils.isValidString(paramCourseName)) {
                topBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(activity.getIntent().getBooleanExtra("isForSelRes", false)){
                            if(onCourseSelListener != null){
                                onCourseSelListener.onSearch();
                            }
                        }else {
                            startActivity(new Intent(activity, SearchActivity.class)
                                    .putExtra("classifyList", new ArrayList<>(inClassifyList))
                                    .putExtra("LevelName", activity.getIntent().getStringExtra("LevelName"))
                                    .putExtra("position", activity.getIntent().getIntExtra("position", 0))
                                    .putExtra("hideSort", hideSort)
                                    .putExtra("Sort", activity.getIntent().getStringExtra("Sort"))
                                    .putExtra("isForSelRes",
                                            activity.getIntent().getBooleanExtra("isForSelRes", false)));
                        }
                    }
                });
            }

            initViews();
        }
    }

    public void setOnCourseListener(LQCourseCourseListFragment.OnCourseSelListener listener){
        onCourseSelListener = listener;
    }

    public void updateForSearch(String keyWord) {
        if(fragment1 != null){
            fragment1.updateForSearch(keyWord);
        }
        if(fragment2 != null){
            fragment2.updateForSearch(keyWord);
        }
        if(fragment3 != null){
            fragment3.updateForSearch(keyWord);
        }
        if(fragment4 != null){
            fragment4.updateForSearch(keyWord);
        }
    }

    private void initViews() {
        if(inClassifyList != null && inClassifyList.size() == 4){
            topPosition = activity.getIntent().getIntExtra("position", 0);
            if(topPosition < 0){
                topPosition = 0;
            }
            radioGroupTopFilter.setVisibility(hideSort ? View.GONE : View.VISIBLE);
            radioButtonTopFilter1.setText(inClassifyList.get(topPosition).getChildList().get(0).getConfigValue());
            radioButtonTopFilter2.setText(inClassifyList.get(topPosition).getChildList().get(1).getConfigValue());
            radioButtonTopFilter3.setText(inClassifyList.get(topPosition).getChildList().get(2).getConfigValue());
            radioButtonTopFilter4.setText(inClassifyList.get(topPosition).getChildList().get(3).getConfigValue());
            radioGroupTopFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                    FragmentTransaction fragmentTransaction =
                            ((FragmentActivity)activity)
                                    .getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.hide(fragment1);
                    fragmentTransaction.hide(fragment2);
                    fragmentTransaction.hide(fragment3);
                    fragmentTransaction.hide(fragment4);
                    if(checkedId == R.id.rb_top_filter1){
                        fragmentTransaction.show(fragment1);
                        selectPosition = 0;
                        SharedPreferencesHelper.setInt(activity,
                                "LQCourseTopPosition", selectPosition);
                    }else if(checkedId == R.id.rb_top_filter2){
                        fragmentTransaction.show(fragment2);
                        selectPosition = 1;
                        SharedPreferencesHelper.setInt(activity,
                                "LQCourseTopPosition", selectPosition);
                    }else if(checkedId == R.id.rb_top_filter3){
                        fragmentTransaction.show(fragment3);
                        selectPosition = 2;
                        SharedPreferencesHelper.setInt(activity,
                                "LQCourseTopPosition", selectPosition);
                    }else if(checkedId == R.id.rb_top_filter4){
                        fragmentTransaction.show(fragment4);
                        selectPosition = 3;
                        SharedPreferencesHelper.setInt(activity,
                                "LQCourseTopPosition", selectPosition);
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                }
            });

            fragment1 = new LQCourseCourseListFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("classify", inClassifyList.get(topPosition));
            bundle1.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));

            if (hideSort) {
                String levelName = activity.getIntent().getStringExtra("LevelName");
                if (StringUtils.isValidString(levelName)) {
                    topBar.setTitle(levelName);
                }
            }

            bundle1.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle1.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle1.putString("Level", getSelectLevel(topPosition, 0));
            fragment1.setArguments(bundle1);
            fragment1.setOnCourseSelListener(this.onCourseSelListener);

            fragment2 = new LQCourseCourseListFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("classify", inClassifyList.get(topPosition));
            bundle2.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));
            if(activity.getIntent().getStringExtra("LevelName") == null) {
                activity.getIntent().putExtra("LevelName", getSelectLevelName(1, selectPosition));
            }
            bundle2.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle2.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle2.putString("Level", getSelectLevel(topPosition, 1));
            fragment2.setArguments(bundle2);
            fragment2.setOnCourseSelListener(this.onCourseSelListener);

            fragment3 = new LQCourseCourseListFragment();
            Bundle bundle3 = new Bundle();
            bundle3.putSerializable("classify", inClassifyList.get(topPosition));
            bundle3.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));
            if(activity.getIntent().getStringExtra("LevelName") == null) {
                activity.getIntent().putExtra("LevelName", getSelectLevelName(2, selectPosition));
            }
            bundle3.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle3.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle3.putString("Level", getSelectLevel(topPosition, 2));
            fragment3.setArguments(bundle3);
            fragment3.setOnCourseSelListener(this.onCourseSelListener);

            fragment4 = new LQCourseCourseListFragment();
            Bundle bundle4 = new Bundle();
            bundle4.putSerializable("classify", inClassifyList.get(topPosition));
            bundle4.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));
            if(activity.getIntent().getStringExtra("LevelName") == null) {
                activity.getIntent().putExtra("LevelName", getSelectLevelName(3, selectPosition));
            }
            bundle4.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle4.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle4.putString("Level", getSelectLevel(topPosition, 3));
            fragment4.setArguments(bundle4);
            fragment4.setOnCourseSelListener(this.onCourseSelListener);

            FragmentTransaction fragmentTransaction = ((FragmentActivity)activity)
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, fragment1);
            fragmentTransaction.add(R.id.fragment_container, fragment2);
            fragmentTransaction.add(R.id.fragment_container, fragment3);
            fragmentTransaction.add(R.id.fragment_container, fragment4);
            fragmentTransaction.show(fragment1);
            fragmentTransaction.hide(fragment2);
            fragmentTransaction.hide(fragment3);
            fragmentTransaction.hide(fragment4);
            fragmentTransaction.commit();
            switch (selectPosition){
                default:
                case 0:
                    radioGroupTopFilter.check(R.id.rb_top_filter1);
                    break;
                case 1:
                    radioGroupTopFilter.check(R.id.rb_top_filter2);
                    break;
                case 2:
                    radioGroupTopFilter.check(R.id.rb_top_filter3);
                    break;
                case 3:
                    radioGroupTopFilter.check(R.id.rb_top_filter4);
                    break;
            }
            if(activity.getIntent().getIntExtra("position", 0) < 0){
                String levelName = activity.getIntent().getStringExtra("LevelName");
                if (StringUtils.isValidString(levelName)) {
                    topBar.setTitle(levelName);
                }
            }else{
                topBar.setTitle(inClassifyList.get(topPosition).getConfigValue());
            }
        }else{
            radioGroupTopFilter.setVisibility(View.GONE);
        }
    }

    private String getSelectLevel(int index, int position){
        if (position < 0){
            return null;
        }
        if(inClassifyList == null || inClassifyList.size() != 4){
            return null;
        }else{
            ClassifyVo vo = inClassifyList.get(index);
            if(vo == null || vo.getChildList() == null){
                return null;
            }
            int count = 0;
            for(int i = 0; i < vo.getChildList().size(); i++){
                if("2".equals(vo.getChildList().get(i).getConfigType())){
                    if(count == position){
                        return vo.getChildList().get(i).getLevel();
                    }
                    count ++;
                }
            }
            return null;
        }
    }

    private String getSelectLevelName(int index, int position){
        if (position < 0){
            return null;
        }
        if(inClassifyList == null || inClassifyList.size() != 4){
            return null;
        }else{
            ClassifyVo vo = inClassifyList.get(index);
            if(vo == null || vo.getChildList() == null){
                return null;
            }
            int count = 0;
            for(int i = 0; i < vo.getChildList().size(); i++){
                if("2".equals(vo.getChildList().get(i).getConfigType())){
                    if(count == position){
                        return vo.getChildList().get(i).getConfigValue();
                    }
                    count ++;
                }
            }
            return null;
        }
    }
}
