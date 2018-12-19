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
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.learn.adapter.ChildrensListAdapter;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyLiveListActivity extends MyBaseFragmentActivity {
    public static void start(Activity activity){
        if(!ButtonUtils.isFastDoubleClick()) {
            activity.startActivity(new Intent(activity, MyLiveListActivity.class));
        }
    }

    private static final String TAG = "MyLiveListActivity";
    // private TopBar topBar;
    private RelativeLayout mTopBar;
    private TextView mTitle;
    private TabLayout mTabLayout;
    private ImageButton mBack;
    // private HorizontalListView childListView;
    private boolean isMultiFragment = false;
    protected ViewPager viewPager;
    protected boolean isTeacher;
    // private ChildrensListAdapter childrensListAdapter;
    private List<MyLiveListPagerFragment> pagerFragments =
            new ArrayList<>();
    protected SlidePagerAdapter pagerAdapter;
    MyLiveListPagerFragment fragment0 = new MyLiveListPagerFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_live_list);
        mTopBar = (RelativeLayout) findViewById(R.id.top_bar);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTabLayout = (TabLayout) mTopBar.findViewById(R.id.tab_layout);
        mBack = (ImageButton)mTopBar.findViewById(R.id.left_function1_image);
        viewPager = (ViewPager) findViewById(R.id.view_paper);
        viewPager.setCurrentItem(0);
        /*viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }
            @Override
            public void onPageSelected(int i) {
                if (childListView != null && childListView.getVisibility() == View.VISIBLE) {
                    if (childrensListAdapter != null
                            && childrensListAdapter.getSelectIndex() != i) {
                        childrensListAdapter.setSelect(i);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {
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
        mTitle.setText(getText(R.string.my_learning_process));

        registerBoradcastReceiver();


        mTitleVos = new ArrayList<>();
        // @date   :2018/6/27 0027 上午 11:57
        // @func   :我主持的直播 V5.8 第一个Tab是该直播列表
        boolean haveMoreTable = false;
        if (StringUtils.isValidString(UserHelper.getUserInfo().getRoles()) &&
                UserHelper.getUserInfo().getRoles().contains("0")){//老师的身份
            isTeacher = true;
            haveMoreTable = true;
            ChildrenListVo vo = new ChildrenListVo();
            vo.setMemberId(UserHelper.getUserId());
            vo.setRealName(activity.getResources().getString(R.string.label_my_establish_live));
            mTitleVos.add(vo);
            MyLiveListPagerFragment fragment = new MyLiveListPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("MemberId", UserHelper.getUserId());
            bundle.putString("SchoolId", "");
            bundle.putInt("type", 1);
            fragment.setArguments(bundle);
            pagerFragments.add(fragment);
        }


        // 添加我参加的直播
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("MemberId", UserHelper.getUserId());
        bundle1.putInt("type", 0);
        fragment0.setArguments(bundle1);
        pagerFragments.add(fragment0);
        ChildrenListVo vo1 = new ChildrenListVo();
        vo1.setRealName(activity.getResources().getString(R.string.my_join_live_prefix));
        vo1.setNickname(activity.getResources().getString(R.string.my_join_live_prefix));
        mTitleVos.add(vo1);


        if (StringUtils.isValidString(UserHelper.getUserInfo().getRoles()) &&
                UserHelper.getUserInfo().getRoles().contains("2")){//家长
            haveMoreTable = true;
            getChildList();
        }
        if(haveMoreTable) {
            isMultiFragment = true;
            /*childListView = (HorizontalListView) getLayoutInflater()
                    .inflate(R.layout.com_horizontal_list_view, null);*/

            mTitle.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.VISIBLE);

            /*topBar.setTitleContentView(childListView, 0);

            childrensListAdapter = new ChildrensListAdapter(activity,
                    onSelectItemChangeListener);
            childListView.setAdapter(childrensListAdapter);
            childListView.mCenterHorizontal = true;
            childrensListAdapter.setData(list, true);
            childrensListAdapter.notifyDataSetChanged();*/
        }else{
            mTitle.setText(activity.getResources().getString(R.string.my_live));
        }
        mTabLayout.setupWithViewPager(viewPager);
        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), mTitleVos.size());
        viewPager.setAdapter(pagerAdapter);

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
                if(!result.isHasError()){
                    List<ChildrenListVo> list = result.getModel().getData();
                    if(list != null && list.size() > 0){
                        for(int i = 0; i < list.size(); i++){
                            MyLiveListPagerFragment fragment = new MyLiveListPagerFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("MemberId", list.get(i).getMemberId());
                            bundle.putString("SchoolId", list.get(i).getSchoolId());
                            bundle.putInt("type", 0);
                            bundle.putBoolean("IsChildren", true);
                            bundle.putString("childName",
                                    StringUtils.isValidString(list.get(i).getRealName()) ?
                                            list.get(i).getRealName() : list.get(0).getNickname());
                            fragment.setArguments(bundle);
                            pagerFragments.add(fragment);
                        }
                        mTitleVos.addAll(list);
                        mTabLayout.setupWithViewPager(viewPager);
                        pagerAdapter.setPageCount(pagerAdapter.getCount() + list.size());
                    }

                    /*childrensListAdapter.addData(list);
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

    private List<ChildrenListVo> mTitleVos;

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
        fragment0.getData();
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

        public void setPageCount(int count){
            this.pageCount = count;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            if(isMultiFragment){
                return pagerFragments.get(position);
            }else{
                return fragment0;
            }
        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(!EmptyUtil.isEmpty(mTitleVos)){
                ChildrenListVo vo = mTitleVos.get(position);
                if(!EmptyUtil.isEmpty(vo)){
                    String language = Locale.getDefault().getLanguage();
                    if(language.endsWith("en")){

                        if(isTeacher && position == 0){
                            // 第一个就是我主持的直播
                            return vo.getRealName() + " " + UIUtil.getString(R.string.x_joined_live);
                        }

                        if(isTeacher){
                            if(position == 1){
                                // 第二个就是我参加的直播
                                return vo.getRealName() + " " + UIUtil.getString(R.string.x_joined_live);
                            }
                        }else{
                            if(position == 0){
                                // 第一个就是我参加的直播
                                return vo.getRealName() + " " + UIUtil.getString(R.string.x_joined_live);
                            }
                        }
                    }

                    return vo.getRealName() + UIUtil.getString(R.string.xx_joined_live);

                    /*if(position == 0){
                        return vo.getRealName();
                    }*/
                    /*return vo.getRealName() + (TextUtils.equals(vo.getMemberId(), UserHelper.getUserId()) ?
                            "" : UIUtil.getString(R.string.xx_joined_live));*/
                }
            }
            return super.getPageTitle(position);
        }
    }
}
