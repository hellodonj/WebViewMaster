package com.lqwawa.intleducation.module.discovery.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.helper.SharedPreferencesHelper;
import com.lqwawa.intleducation.base.ui.MyBaseFragment;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.common.ui.SortLinePopupWindow;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import com.lqwawa.intleducation.module.discovery.vo.HQCPermissionsVo;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by XChen on 2017/8/9.
 * email:man0fchina@foxmail.com
 */

public class HQCCourseFragment extends MyBaseFragment implements View.OnClickListener{
    //头部
    private TopBar topBar;
    private TextView textViewTopSection;
    private RadioGroup radioGroupTopFilter;
    private RadioButton radioButtonTopFilter1;
    private RadioButton radioButtonTopFilter2;
    private RadioButton radioButtonTopFilter3;
    private RadioButton radioButtonTopFilter4;
    private HQCCourseListFragment fragment1;
    private HQCCourseListFragment fragment2;
    private HQCCourseListFragment fragment3;
    private HQCCourseListFragment fragment4;
    private TextView vip_tv1;
    private TextView vip_tv2;
    private TextView vip_tv3;
    private TextView vip_tv4;
    private LinearLayout searchKeywordLay;
    private TextView searchKeywordTv;
    private ImageView clearKeywordIv;

    private ClassifyVo selectClassVo = null;
    private List<ClassifyVo> inClassifyList;
    private List<HQCPermissionsVo> permissionsVos;
    private static int selectPosition = 0;
    private VipPermissionsComparator vipPermissionsComparator = new VipPermissionsComparator();

    HQCCourseListFragment.OnCourseSelListener onCourseSelListener = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hqc_course_list, container, false);
        topBar = (TopBar)view.findViewById(R.id.top_bar);
        searchKeywordLay =
                (LinearLayout)view.findViewById(R.id.search_keyword_lay);
        searchKeywordTv = (TextView)view.findViewById(R.id.search_keyword_tv);
        clearKeywordIv = (ImageView) view.findViewById(R.id.clear_keyword_iv);
        radioGroupTopFilter = (RadioGroup) view.findViewById(R.id.rg_top_filter);
        radioButtonTopFilter1 = (RadioButton) view.findViewById(R.id.rb_top_filter1);
        radioButtonTopFilter2 = (RadioButton) view.findViewById(R.id.rb_top_filter2);
        radioButtonTopFilter3 = (RadioButton) view.findViewById(R.id.rb_top_filter3);
        radioButtonTopFilter4 = (RadioButton) view.findViewById(R.id.rb_top_filter4);
        vip_tv1 = (TextView) view.findViewById(R.id.vip_tv1);
        vip_tv2 = (TextView) view.findViewById(R.id.vip_tv2);
        vip_tv3 = (TextView) view.findViewById(R.id.vip_tv3);
        vip_tv4 = (TextView) view.findViewById(R.id.vip_tv4);

        vip_tv1.setOnClickListener(this);
        vip_tv2.setOnClickListener(this);
        vip_tv3.setOnClickListener(this);
        vip_tv4.setOnClickListener(this);

        selectPosition = SharedPreferencesHelper.getInt(activity, "HQCCourseTopPosition", 0);

        String levelName = activity.getIntent().getStringExtra("LevelName");

        topBar.setTitle(getString(R.string.all));
        if (StringUtils.isValidString(levelName)) {
            topBar.setTitle(levelName);
        }

        topBar.setBack(true);
        if(clearKeywordIv != null){
            clearKeywordIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateForSearch("");
                }
            });
        }
        if(searchKeywordTv != null){
            searchKeywordTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onCourseSelListener != null){
                        onCourseSelListener.onSearch();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getClassifyData();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.vip_tv1){
            radioGroupTopFilter.check(R.id.rb_top_filter1);
        }else if(view.getId() == R.id.vip_tv2){
            radioGroupTopFilter.check(R.id.rb_top_filter2);
        }else if(view.getId() == R.id.vip_tv3){
            radioGroupTopFilter.check(R.id.rb_top_filter3);
        }else if(view.getId() == R.id.vip_tv4){
            radioGroupTopFilter.check(R.id.rb_top_filter4);
        }
    }

    public void setOnCourseListener(HQCCourseListFragment.OnCourseSelListener listener){
        onCourseSelListener = listener;
    }

    public void updateForSearch(String keyWord) {
        if(StringUtils.isValidString(keyWord)) {
            if(searchKeywordLay != null && searchKeywordTv != null){
                searchKeywordLay.setVisibility(View.VISIBLE);
                searchKeywordTv.setText(keyWord);
            }
        }else{
            if(searchKeywordLay != null){
                searchKeywordLay.setVisibility(View.GONE);
            }
        }
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

    private void getClassifyData() {
        showProgressDialog(getResources().getString(R.string.loading));
        RequestVo requestVo = new RequestVo();

        requestVo.addParams("language", Utils.isZh(getActivity()) ? 0 : 1);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.GetClassList+requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<List<ClassifyVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<ClassifyVo>>>() {
                        });
                if (result.getCode() == 0) {
                    inClassifyList = result.getData();
                    getPermissionsList();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                try {
                    ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                }catch (Exception e){

                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void getPermissionsList() {
        showProgressDialog(getResources().getString(R.string.loading));
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("schoolId", activity.getIntent().getStringExtra("SchoolId"));
        RequestParams params = new RequestParams(
                AppConfig.ServerUrl.getHQCPermissionsListBySchoolId + requestVo.getParams());
        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                closeProgressDialog();
                ResponseVo<List<HQCPermissionsVo>> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<List<HQCPermissionsVo>>>() {
                        });
                if (result.getCode() == 0) {
                    permissionsVos = result.getData();
                    activity.getIntent().putExtra("PermissionsList",
                            new ArrayList<>(permissionsVos));
                    initTopFilter();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                closeProgressDialog();
                try {
                    ToastUtil.showToast(activity, getResources().getString(R.string.net_error_tip));
                }catch (Exception e){

                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void initTopFilter(){
        if (inClassifyList != null && inClassifyList.size() == 5) {
            inClassifyList.remove(0);
        }

        if (inClassifyList != null && inClassifyList.size() == 4) {
            if(permissionsVos != null && permissionsVos.size() > 0){
                for (HQCPermissionsVo permissionsVo : permissionsVos) {
                    for(int i = 0; i < inClassifyList.size(); i++) {
                        if (TextUtils.equals(permissionsVo.getFirstId(),
                                "" + inClassifyList.get(i).getId())){
                            inClassifyList.get(i).setHaveVipPermissions(true);
                        }
                    }
                }
            }

            Collections.sort(inClassifyList, vipPermissionsComparator);


            for (int i = 0; i < inClassifyList.size(); i++) {
                getVipTextByIndex(i).setVisibility(
                        inClassifyList.get(i).isHaveVipPermissions() ? View.GONE : View.VISIBLE);
            }

            activity.getIntent().putExtra("classifyList",new ArrayList<>(inClassifyList));
            String paramCourseName = activity.getIntent().getStringExtra("CourseName");
            if (!StringUtils.isValidString(paramCourseName)) {
                topBar.setRightFunctionImage1(R.drawable.search, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //if (activity.getIntent().getBooleanExtra("isForSelRes", false)) {
                            if (onCourseSelListener != null) {
                                onCourseSelListener.onSearch();
                            }
                        /*} else {
                            startActivity(new Intent(activity, SearchActivity.class)
                                    .putExtra("classifyList", new ArrayList<>(inClassifyList))
                                    .putExtra("LevelName", activity.getIntent().getStringExtra("LevelName"))
                                    .putExtra("position", activity.getIntent().getIntExtra("position", -1))
                                    .putExtra("Sort", activity.getIntent().getStringExtra("Sort"))
                                    .putExtra("isForSelRes",
                                            activity.getIntent().getBooleanExtra("isForSelRes", false)));
                        }*/
                    }
                });
            }
            textViewTopSection = new TextView(activity);
            textViewTopSection.setTextColor(activity.getResources().getColor(R.color.com_text_black));
            textViewTopSection.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    activity.getResources().getDimensionPixelSize(R.dimen.com_font_size_7));
            Drawable dra = getResources().getDrawable(R.drawable.arrow_down_gray_ico);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
            textViewTopSection.setCompoundDrawables(null, null, dra, null);
            textViewTopSection.setCompoundDrawablePadding(10);

            TextPaint tp = textViewTopSection .getPaint();
            tp.setFakeBoldText(true);
            topBar.setTitleContentView(textViewTopSection);
            int dp20 = (int)activity.getResources().getDimension(R.dimen.com_item_space_xxxl);
            textViewTopSection.setPadding(dp20, 0, dp20, 0);
            textViewTopSection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Drawable dra = getResources().getDrawable(R.drawable.arrow_up_gray_ico);
                    dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
                    textViewTopSection.setCompoundDrawables(null, null, dra, null);
                    List<ClassifyVo> sections = new ArrayList<ClassifyVo>();
                    for(int i = 0; i < selectClassVo.getChildList().size(); i++){
                        if ("2".equals(selectClassVo.getChildList().get(i).getConfigType())){
                            sections.add(selectClassVo.getChildList().get(i));
                        }
                    }
                    SortLinePopupWindow linePopupWindow =
                            new SortLinePopupWindow(activity, sections, "",
                                    new SortLinePopupWindow.PopupWindowListener() {
                                        @Override
                                        public void onItemClickListener(int position, Object object) {
                                            ClassifyVo vo = (ClassifyVo) object;
                                            if (vo != null) {
                                                textViewTopSection.setText(vo.getConfigValue());
                                                selectPosition = position;
                                                SharedPreferencesHelper.setInt(activity,
                                                        "HQCCourseTopPosition", selectPosition);
                                                fragment1.changeSection(getSelectLevel(0, selectPosition));
                                                fragment2.changeSection(getSelectLevel(1, selectPosition));
                                                fragment3.changeSection(getSelectLevel(2, selectPosition));
                                                fragment4.changeSection(getSelectLevel(3, selectPosition));
                                            }
                                        }

                                        @Override
                                        public void onDismissListener() {
                                            Drawable dra = getResources().getDrawable(R.drawable.arrow_down_gray_ico);
                                            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
                                            textViewTopSection.setCompoundDrawables(null, null, dra, null);
                                        }
                                    }, false);
                    linePopupWindow.showPopupWindow(view, true);
                }
            });
            initViews();
        }
    }

    private TextView getVipTextByIndex(int index){
        switch (index){
            default:
            case 0:
                return vip_tv1;
            case 1:
                return vip_tv2;
            case 2:
                return vip_tv3;
            case 3:
                return vip_tv4;
        }
    }

    private void initViews() {
        if(inClassifyList != null && inClassifyList.size() == 4){
            selectClassVo = inClassifyList.get(0);
            //selectPosition = activity.getIntent().getIntExtra("position", 0);
            radioGroupTopFilter.setVisibility(View.VISIBLE);
            radioButtonTopFilter1.setText(inClassifyList.get(0).getConfigValue());
            radioButtonTopFilter2.setText(inClassifyList.get(1).getConfigValue());
            radioButtonTopFilter3.setText(inClassifyList.get(2).getConfigValue());
            radioButtonTopFilter4.setText(inClassifyList.get(3).getConfigValue());
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
                        selectClassVo = inClassifyList.get(0);
                        SharedPreferencesHelper.setInt(activity, "HQCCourseTypePosition", 0);
                    }else if(checkedId == R.id.rb_top_filter2){
                        fragmentTransaction.show(fragment2);
                        selectClassVo = inClassifyList.get(1);
                        SharedPreferencesHelper.setInt(activity, "HQCCourseTypePosition", 1);
                    }else if(checkedId == R.id.rb_top_filter3){
                        fragmentTransaction.show(fragment3);
                        selectClassVo = inClassifyList.get(2);
                        SharedPreferencesHelper.setInt(activity, "HQCCourseTypePosition", 2);
                    }else if(checkedId == R.id.rb_top_filter4){
                        fragmentTransaction.show(fragment4);
                        selectClassVo = inClassifyList.get(3);
                        SharedPreferencesHelper.setInt(activity, "HQCCourseTypePosition", 3);
                    }
                    fragmentTransaction.commitAllowingStateLoss();
                }
            });

            fragment1 = new HQCCourseListFragment();
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("classify", inClassifyList.get(0));
            bundle1.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));
            if(activity.getIntent().getStringExtra("LevelName") == null) {
                String levelName = getSelectLevelName(0, selectPosition);
                activity.getIntent().putExtra("LevelName", levelName);
                if(textViewTopSection != null){
                    textViewTopSection.setText(levelName);
                }else{
                    topBar.setTitle(levelName);
                }
            }
            bundle1.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle1.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle1.putString("Level", getSelectLevel(0, selectPosition));
            fragment1.setArguments(bundle1);
            fragment1.setOnCourseSelListener(this.onCourseSelListener);

            fragment2 = new HQCCourseListFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("classify", inClassifyList.get(1));
            bundle2.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));
            if(activity.getIntent().getStringExtra("LevelName") == null) {
                activity.getIntent().putExtra("LevelName", getSelectLevelName(1, selectPosition));
            }
            bundle2.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle2.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle2.putString("Level", getSelectLevel(1, selectPosition));
            fragment2.setArguments(bundle2);
            fragment2.setOnCourseSelListener(this.onCourseSelListener);

            fragment3 = new HQCCourseListFragment();
            Bundle bundle3 = new Bundle();
            bundle3.putSerializable("classify", inClassifyList.get(2));
            bundle3.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));
            if(activity.getIntent().getStringExtra("LevelName") == null) {
                activity.getIntent().putExtra("LevelName", getSelectLevelName(2, selectPosition));
            }
            bundle3.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle3.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle3.putString("Level", getSelectLevel(2, selectPosition));
            fragment3.setArguments(bundle3);
            fragment3.setOnCourseSelListener(this.onCourseSelListener);

            fragment4 = new HQCCourseListFragment();
            Bundle bundle4 = new Bundle();
            bundle4.putSerializable("classify", inClassifyList.get(3));
            bundle4.putString("CourseName", activity.getIntent().getStringExtra("CourseName"));
            if(activity.getIntent().getStringExtra("LevelName") == null) {
                activity.getIntent().putExtra("LevelName", getSelectLevelName(3, selectPosition));
            }
            bundle4.putString("LevelName", activity.getIntent().getStringExtra("LevelName"));
            bundle4.putString("Sort", activity.getIntent().getStringExtra("Sort"));
            bundle4.putString("Level", getSelectLevel(3, selectPosition));
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

            int position = SharedPreferencesHelper.getInt(activity, "HQCCourseTypePosition", 0);
            switch (position){
                case 0:
                default:
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

    public class VipPermissionsComparator implements Comparator<ClassifyVo> {
        public int compare(ClassifyVo o1, ClassifyVo o2) {

            if (!o2.isHaveVipPermissions()){
                return -1;
            }else{
                return 1;
            }
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        closeProgressDialog();
    }

}
