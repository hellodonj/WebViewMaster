package com.lqwawa.intleducation.module.learn.ui;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseFragmentActivity;
import com.lqwawa.intleducation.base.utils.ButtonUtils;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.base.vo.LqResponseDataVo;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.widgets.HorizontalListView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.PagerChangedAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.adapter.ChildrensListAdapter;
import com.lqwawa.intleducation.module.learn.adapter.MyCourseListAdapter;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyCourseListActivity extends MyBaseFragmentActivity {
    public static void start(Activity activity){
        if(!ButtonUtils.isFastDoubleClick()) {
            activity.startActivity(new Intent(activity, MyCourseListActivity.class));
        }
    }

    private static final String TAG = "MyCourseListActivity";
    // private TopBar topBar;
    private RelativeLayout mTopBar;
    private TextView mTitle;
    private TabLayout mTabLayout;
    private ImageButton mBack;
    private boolean isParent = false;
    private boolean isTeacher = false;
    protected ViewPager viewPager;
    // private ChildrensListAdapter childrensListAdapter;
    private List<MyCourseListPagerFragment> pagerFragments =
            new ArrayList<MyCourseListPagerFragment>();
    protected SlidePagerAdapter pagerAdapter;
    MyCourseListPagerFragment fragment0 = new MyCourseListPagerFragment();
    MyCourseListPagerFragment fragment1 = new MyCourseListPagerFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_list);
        mTopBar = (RelativeLayout) findViewById(R.id.top_bar);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTabLayout = (TabLayout) mTopBar.findViewById(R.id.tab_layout);
        mBack = (ImageButton) mTopBar.findViewById(R.id.left_function1_image);
        if (UserHelper.isLogin() && UserHelper.getUserInfo() != null
                && StringUtils.isValidString(UserHelper.getUserInfo().getRoles()) &&
                UserHelper.getUserInfo().getRoles().contains("2")){//以家长身份登录
            isParent = true;
        }


        if (UserHelper.isLogin() && UserHelper.getUserInfo() != null
                && StringUtils.isValidString(UserHelper.getUserInfo().getRoles()) &&
                UserHelper.getUserInfo().getRoles().contains("0")){//以老师身份登录
            isTeacher = true;
        }

        mTitleListVo = new ArrayList<>();

        if(isTeacher){
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("MemberId", UserHelper.getUserId());
            bundle1.putBoolean(MyCourseListPagerFragment.KEY_IS_TEACHER,true);
            fragment1.setArguments(bundle1);
            pagerFragments.add(fragment1);
            // 添加标题
            ChildrenListVo vo1 = new ChildrenListVo();
            vo1.setRealName(getString(R.string.label_my_establish_course));
            mTitleListVo.add(vo1);
        }

        Bundle bundle0 = new Bundle();
        bundle0.putSerializable("MemberId", UserHelper.getUserId());
        fragment0.setArguments(bundle0);
        pagerFragments.add(fragment0);

        // 添加标题
        ChildrenListVo vo1 = new ChildrenListVo();
        vo1.setRealName(getString(R.string.label_my_join_course));
        mTitleListVo.add(vo1);

        viewPager = (ViewPager) findViewById(R.id.view_paper);
        if(!isParent && !isTeacher) {
            pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), 1);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(0);
        }
        /*viewPager.addOnPageChangeListener(new PagerChangedAdapter(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mTabLayout != null && childListView.getVisibility() == View.VISIBLE) {
                    if (childrensListAdapter != null
                            && childrensListAdapter.getSelectIndex() != position) {
                        childrensListAdapter.setSelect(position);
                        childListView.setSelection(position);
                    }
                }
            }
        });*/
        initViews();
    }

    private void initViews() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(getResources().getString(R.string.my_learning_process));

        registerBoradcastReceiver();
        if(isParent){
            getChildList();
        }else if(isTeacher){
            mTitle.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setupWithViewPager(viewPager);
            pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), 2);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(0);
        }
    }

    private void getChildList(){
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("MemberId", UserHelper.getUserId());
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.LQWW_GET_STUDENT_BY_PARENT);
        params.setConnectTimeout(10000);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParams());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LqResponseDataVo<List<ChildrenListVo>> result = JSON.parseObject(s,
                        new TypeReference<LqResponseDataVo<List<ChildrenListVo>>>() {
                        });
                if(result.isHasError() == false){
                    List<ChildrenListVo> list = result.getModel().getData();
                    mTitleListVo.addAll(list);
                    int listCount = 0;
                    if(list != null && list.size() > 0){
                        listCount = list.size();
                        mTitle.setVisibility(View.GONE);
                        mTabLayout.setVisibility(View.VISIBLE);

                        /*childrensListAdapter = new ChildrensListAdapter(activity,
                                onSelectItemChangeListener);*/
                        mTabLayout.setupWithViewPager(viewPager);

                        for(int i = 0; i < list.size(); i++){
                            MyCourseListPagerFragment fragment = new MyCourseListPagerFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("MemberId", list.get(i).getMemberId());
                            bundle.putString("SchoolId", list.get(i).getSchoolId());
                            fragment.setArguments(bundle);
                            pagerFragments.add(fragment);
                        }
                    }
                    viewPager.removeAllViews();//涉及到实时刷新,所以要将之前的布局清空掉。
                    viewPager.removeAllViewsInLayout();//removeAllViews();//赋值之前先将Adapter中的
                    pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(),mTitleListVo.size());
                    viewPager.setAdapter(pagerAdapter);
                    viewPager.setCurrentItem(0);
                    pagerAdapter.notifyDataSetChanged();
                    /*childrensListAdapter.setData(list, false);
                    childrensListAdapter.notifyDataSetChanged();*/
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if(throwable != null) {
                    LogUtil.d(TAG, throwable.getMessage());
                }
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private List<ChildrenListVo> mTitleListVo;

    ChildrensListAdapter.OnSelectItemChangeListener onSelectItemChangeListener
            = new ChildrensListAdapter.OnSelectItemChangeListener() {
        @Override
        public void OnSelectItemChanged(Object obj, int position) {
            ChildrenListVo vo = (ChildrenListVo) obj;
            if(vo != null) {
                viewPager.setCurrentItem(position);
            }
        }
    };

    public void getData(){
        if(fragment0.isVisible()){
            fragment0.getData();
        }else if(fragment1.isVisible()){
            fragment1.getData();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.unregisterReceiver(mBroadcastReceiver);
    }

    /**BroadcastReceiver************************************************/
    protected BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AppConfig.ServerUrl.joinInCourse)//
                    || action.equals(AppConfig.ServerUrl.Login)) {//
                getData();
            }
        }
    };

    /**
     * 注册广播事件
     */
    protected void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        //要接收的类型
        myIntentFilter.addAction(AppConfig.ServerUrl.joinInCourse);//
        myIntentFilter.addAction(AppConfig.ServerUrl.Login);//登录成功
        //注册广播
        activity.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private class SlidePagerAdapter extends FragmentPagerAdapter {
        private int pageCount = 1;
        public SlidePagerAdapter(FragmentManager fm, int pageCount) {
            super(fm);
            this.pageCount = pageCount;
        }

        @Override
        public Fragment getItem(int position) {
            return pagerFragments.get(position);
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // @date   :2018/6/26 0026 下午 5:10
            // @func   :V5.8更改添加我开设的学程 === 我参加的学程 我孩子的学程

            if(!EmptyUtil.isEmpty(mTitleListVo)){
                ChildrenListVo vo = mTitleListVo.get(position);
                if(!EmptyUtil.isEmpty(vo)){
                    String language = Locale.getDefault().getLanguage();
                    if(language.endsWith("en")){
                        if(isTeacher && position == 0){
                            // 第一个就是我主持的课程
                            return vo.getRealName() + " " + UIUtil.getString(R.string.x_learning_process);
                        }

                        if(isTeacher){
                            if(position == 1){
                                // 第二个就是我参加的课程
                                return vo.getRealName() + " " + UIUtil.getString(R.string.x_learning_process);
                            }
                        }else{
                            if(position == 0){
                                // 第一个就是我参加的课程
                                return vo.getRealName() + " " + UIUtil.getString(R.string.x_learning_process);
                            }
                        }
                    }

                    return vo.getRealName() + UIUtil.getString(R.string.xx_learning_process);
                    /*String userId = UserHelper.getUserId();
                    String memberId = vo.getMemberId();
                    return vo.getRealName() + (TextUtils.equals(memberId, userId) ? "" : UIUtil.getString(R.string.xx_learning_process));*/
                }
            }
            return super.getPageTitle(position);
        }
    }
}
