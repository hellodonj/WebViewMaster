package com.lqwawa.intleducation.module.onclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.transition.Transition;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.widgets.PriceArrowView;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.lqwawa.intleducation.base.widgets.adapter.TabSelectedAdapter;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.common.ui.PopupMenu;
import com.lqwawa.intleducation.common.ui.QRCodeDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaImageUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.KeyboardUtil;
import com.lqwawa.intleducation.common.utils.image.LQwawaSimpleTarget;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.module.onclass.pager.OnlineClassPagerFragment;
import com.oosic.apps.share.BaseShareUtils;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MrMedici
 * @desc 在线课堂列表
 */
public class OnlineClassListActivity extends PresenterActivity<OnlineClassListContract.Presenter>
        implements OnlineClassListContract.View, View.OnClickListener {

    private static final String KEY_EXTRA_SCHOOL_NAME = "KEY_EXTRA_SCHOOL_NAME";
    private static final String KEY_EXTRA_SCHOOL_ID = "KEY_EXTRA_SCHOOL_ID";
    private static final String KEY_EXTRA_IS_SCHOOL_ENTER = "KEY_EXTRA_IS_SCHOOL_ENTER";
    // 学校机构Id
    private String mSchoolId;
    // 学校机构名称
    private String mSchoolName;
    // 标题
    private TopBar mTopBar;
    // private TextView mToolbarTitle;
    // private ImageView mIvMoreMenu;

    // 搜索
    private EditText mSearchContent;
    private ImageView mSearchClear;
    private TextView mSearchFilter;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    // 标志是否从机构主页进入
    private boolean isSchoolEnter;

    private String[] mTabTitles;

    private List<SearchNavigator> mNavigatorList;

    private PriceArrowView mPriceArrowView;
    // 价格tab是否显示
    private boolean priceTabVisiale;
    // 学校信息
    private SchoolInfoEntity mSchoolEntity;


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_online_class_list;
    }

    @Override
    protected OnlineClassListContract.Presenter initPresenter() {
        return new OnlineClassListPresenter(this);
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mSchoolId = bundle.getString(KEY_EXTRA_SCHOOL_ID, null);
        mSchoolName = bundle.getString(KEY_EXTRA_SCHOOL_NAME, null);
        isSchoolEnter = bundle.getBoolean(KEY_EXTRA_IS_SCHOOL_ENTER);
        if (EmptyUtil.isEmpty(mSchoolId) || EmptyUtil.isEmpty(mSchoolName)) {
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        // mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        // mIvMoreMenu = (ImageView) findViewById(R.id.iv_more_menu);
        mSearchContent = (EditText) findViewById(R.id.et_search);
        mSearchClear = (ImageView) findViewById(R.id.iv_search_clear);
        mSearchFilter = (TextView) findViewById(R.id.tv_filter);

        // mToolbarTitle.setText(mSchoolName);
        mTopBar.setBack(true);
        mTopBar.setTitle(mSchoolName);
        mTopBar.setRightFunctionImage1(R.drawable.ic_vertical_more_green, v -> showMoreMenu(v));

        mSearchFilter.setVisibility(View.VISIBLE);
        mSearchClear.setOnClickListener(this);
        mSearchFilter.setOnClickListener(this);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        // @date   :2018/6/13 0013 上午 11:31
        // @func   :V5.7改用键盘搜索的方式
        mSearchContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                if (s.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                } else {
                    mSearchClear.setVisibility(View.INVISIBLE);
                }
                mSearchContent.setImeOptions(s.length() > 0
                        ? EditorInfo.IME_ACTION_SEARCH
                        : EditorInfo.IME_ACTION_DONE);
            }
        });

        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // 搜索，收起软件盘
                    KeyboardUtil.hideSoftInput(OnlineClassListActivity.this);
                    triggerSearch();
                }
                return true;
            }
        });

        // mIvMoreMenu.setOnClickListener(this);

        mTabLayout.addOnTabSelectedListener(new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                if (tab.getPosition() == mTabLayout.getTabCount() - 1) {
                    priceTabVisiale = true;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        int state = mPriceArrowView.triggerSwitch();
                        triggerPriceSwitch(state);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                if (tab.getPosition() == mTabLayout.getTabCount() - 1) {
                    priceTabVisiale = false;
                    // 价格被选中
                    if (EmptyUtil.isNotEmpty(mPriceArrowView)) {
                        // 状态重置
                        mPriceArrowView.reset();
                    }
                }
            }
        });

    }

    @Override
    protected void initData() {
        super.initData();

        mTabTitles = UIUtil.getStringArray(R.array.label_online_class_tabs);
        OnlineClassPagerFragment recentUpdateFragment = OnlineClassPagerFragment.newInstance(mSchoolId, isSchoolEnter, OnlineSortType.TYPE_SORT_ONLINE_CLASS_RECENT_UPDATE);
        OnlineClassPagerFragment hotFragment = OnlineClassPagerFragment.newInstance(mSchoolId, isSchoolEnter, OnlineSortType.TYPE_SORT_ONLINE_CLASS_HOT_RECOMMEND);
        OnlineClassPagerFragment priceFragment = OnlineClassPagerFragment.newInstance(mSchoolId, isSchoolEnter, OnlineSortType.TYPE_SORT_ONLINE_CLASS_PRICE_UP);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(recentUpdateFragment);
        fragments.add(hotFragment);
        fragments.add(priceFragment);

        mNavigatorList = new ArrayList<>();
        mNavigatorList.add(recentUpdateFragment);
        mNavigatorList.add(hotFragment);
        mNavigatorList.add(priceFragment);

        TabPagerAdapter mAdapter = new TabPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        // 设置TabLayout最后一个节点有upDown
        TabLayout.Tab tabAt = mTabLayout.getTabAt(mTabLayout.getTabCount() - 1);
        PriceArrowView view = new PriceArrowView(this);
        view.setTabTitle(mTabTitles[mTabLayout.getTabCount() - 1]);
        tabAt.setCustomView(view.getRootView());
        mPriceArrowView = view;

        mPriceArrowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (priceTabVisiale && event.getAction() == MotionEvent.ACTION_DOWN) {
                    int state = mPriceArrowView.triggerSwitch();
                    triggerPriceSwitch(state);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onResume的时候更新信息
        // 获取机构信息
        mPresenter.requestSchoolInfo(mSchoolId);
    }

    @Override
    public void updateSchoolInfoView(@NonNull SchoolInfoEntity infoEntity) {
        mSchoolEntity = infoEntity;
    }

    /**
     * 发生价格排序变化
     *
     * @param state 状态  1 UP 2 Down
     */
    private void triggerPriceSwitch(int state) {
        if (state == PriceArrowView.STATE_UP) {
            // 升序
            for (SearchNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(true);
                if (isVisible) break;
            }
        } else if (state == PriceArrowView.STATE_DOWN) {
            // 降序
            for (SearchNavigator navigator : mNavigatorList) {
                boolean isVisible = navigator.triggerPriceSwitch(false);
                if (isVisible) break;
            }
        }
    }

    /**
     * 弹出Menu菜单
     *
     * @param view 点击的菜单项
     */
    private void showMoreMenu(View view) {
        // 在showMenu之前应该获取到机构信息
        if (EmptyUtil.isEmpty(mSchoolEntity)) return;
        final List<PopupMenu.PopupMenuData> itemDatas = new ArrayList<>();
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.label_save_qrcode, PopupMenu.PopupMenuData.TYPE_SAVE_QRCODE));
        itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.label_subscription_recommend, PopupMenu.PopupMenuData.TYPE_RECOMMEND_FRIEND));
        if (EmptyUtil.isNotEmpty(mSchoolEntity) && mSchoolEntity.isOnlineSchool() && !mSchoolEntity.isTeacher()) {
            // 如果不是老师身份，才有成为机构老师入口
            itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.label_join_organ_teacher, PopupMenu.PopupMenuData.TYPE_JOIN_SCHOOL_TEACHER));
        }

        if(!mSchoolEntity.hasJoinedSchool()){
            if (!mSchoolEntity.hasSubscribed()) {
                // 关注
                itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.attention, PopupMenu.PopupMenuData.TYPE_ATTENTION));
            } else {
                // 取消关注
                itemDatas.add(new PopupMenu.PopupMenuData(0, R.string.cancel_attention, PopupMenu.PopupMenuData.TYPE_CANCEL_ATTENTION));
            }
        }


        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        PopupMenu.PopupMenuData popupMenuData = itemDatas.get(position);
                        int mId = popupMenuData.getId();
                        switch (mId) {
                            case PopupMenu.PopupMenuData.TYPE_SAVE_QRCODE:
                                // 二维码
                                requestQrCodeBitmap(mSchoolEntity);
                                break;
                            case PopupMenu.PopupMenuData.TYPE_RECOMMEND_FRIEND:
                                // 推荐给好友
                                shareSchoolSpace();
                                break;
                            case PopupMenu.PopupMenuData.TYPE_JOIN_SCHOOL_TEACHER:
                                // 成为机构老师
                                if (EmptyUtil.isNotEmpty(mSchoolEntity) && mSchoolEntity.isOnlineSchool() && !mSchoolEntity.isTeacher()) {
                                    enterJoinSchoolDetail();
                                }
                                break;
                            case PopupMenu.PopupMenuData.TYPE_ATTENTION:
                                subscribeSchool(true);
                                break;
                            case PopupMenu.PopupMenuData.TYPE_CANCEL_ATTENTION:
                                subscribeSchool(false);
                                break;
                        }
                    }
                };
        PopupMenu popupMenu = new PopupMenu(this, itemClickListener, itemDatas);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    /**
     * 获取二维码图片
     *
     * @param entity 学校信息
     */
    private void requestQrCodeBitmap(@NonNull final SchoolInfoEntity entity) {
        final String url = entity.getQRCode();
        if (TextUtils.isEmpty(url)) {
            // 如果图片地址为空 则return出去
            return;
        }

        // 需要加载多大的图片
        int width = DisplayUtil.dip2px(UIUtil.getContext(), 100);
        int height = DisplayUtil.dip2px(UIUtil.getContext(), 100);
        LQwawaImageUtil.loadSimpleTarget(this, url, new LQwawaSimpleTarget(width, height) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                // 获取到图片
                QRCodeDialog dialog = new QRCodeDialog(OnlineClassListActivity.this);
                dialog.show();
                String schoolId = entity.getSchoolId();
                //dialog的标题
                String dialogTitle = getString(R.string.label_qrcode);
                //课件标题
                final String courseTitle = entity.getSchoolName();
                if (dialog.isShowing()) {
                    // qrCodeImageView.setImageBitmap(BitmapFactory.decodeFile(qrCodeImagePath));
                    dialog.setup(url, resource, schoolId, dialogTitle, courseTitle, "", null, null);
                }
            }
        });
    }

    /**
     * 成为机构老师
     */
    private void enterJoinSchoolDetail(){
        Intent intent = new Intent();
        intent.setClassName(getPackageName(),"com.galaxyschool.app.wawaschool.QrcodeProcessActivity");
        intent.putExtra("id",mSchoolId);
        intent.putExtra("name",mSchoolName);
        intent.putExtra("logoUrl",mSchoolEntity.getSchoolLogo());
        intent.putExtra("isFromMooc",true);
        startActivity(intent);
    }

    /**
     * 关注已关注机构
     * @param subscribe true 点击关注 false 取消关注
     */
    private void subscribeSchool(boolean subscribe){
        mPresenter.requestSubscribeSchool(mSchoolId,subscribe);
    }

    @Override
    public void updateAttentionView(boolean state) {
        // 刷新状态
        mSchoolEntity.setSubscribed(state);
        mPresenter.requestSchoolInfo(mSchoolId);
        if(state){
            UIUtil.showToastSafe(R.string.tip_attention_succeed);
        }else{
            UIUtil.showToastSafe(R.string.tip_cancel_attention_succeed);
        }
    }

    /**
     * 推荐给好友
     */
    private void shareSchoolSpace() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(mSchoolEntity.getSchoolName());
        shareInfo.setContent(" ");
        String serverUrl = AppConfig.ServerUrl.QRCODE_SHARE_URL;
        String url = serverUrl.replace("{id}",mSchoolEntity.getSchoolId());
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(mSchoolEntity.getSchoolLogo())) {
            umImage = new UMImage(this, mSchoolEntity.getSchoolLogo());
        } else {
            umImage = new UMImage(this, R.drawable.ic_launcher);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setId(mSchoolEntity.getSchoolId());
        resource.setTitle(mSchoolEntity.getSchoolName());
        resource.setDescription("");
        resource.setShareUrl(serverUrl);
        if (!TextUtils.isEmpty(mSchoolEntity.getSchoolLogo())) {
            resource.setThumbnailUrl(mSchoolEntity.getSchoolLogo());
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        resource.setFieldPatches(SharedResource.FIELD_PATCHES_SCHOOL_SHARE_URL);
        shareInfo.setSharedResource(resource);
        BaseShareUtils utils = new BaseShareUtils(this);
        utils.share(this.getWindow().getDecorView(),shareInfo);
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.tv_filter) {
            // 搜索 兼容其它平板问题，收起软件盘
            KeyboardUtil.hideSoftInput(OnlineClassListActivity.this);
            triggerSearch();
        } else if (viewId == R.id.iv_search_clear) {
            // 删除关键字
            mSearchContent.getText().clear();
        } else if (viewId == R.id.et_search) {
            // 点击搜索框
        }
    }

    /**
     * 触发搜索
     */
    private void triggerSearch() {
        String searchKey = mSearchContent.getText().toString().trim();
        if (TextUtils.isEmpty(searchKey)) {
            return;
        }

        for (SearchNavigator navigator : mNavigatorList) {
            boolean isVisible = navigator.search(searchKey);
            if (isVisible) break;
        }
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }
    }

    /**
     * 在线课堂入口
     *
     * @param context    上下文对象
     * @param schoolId   学校机构Id
     * @param schoolName 学校机构名称
     */
    public static void show(@NonNull Context context,
                            @NonNull String schoolId,
                            @NonNull String schoolName) {
        Intent intent = new Intent(context, OnlineClassListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        bundle.putString(KEY_EXTRA_SCHOOL_NAME, schoolName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 在线课堂入口
     *
     * @param context       上下文对象
     * @param schoolId      学校机构Id
     * @param schoolName    学校机构名称
     * @param isSchoolEnter 是否从机构主页进入
     */
    public static void show(@NonNull Context context,
                            @NonNull String schoolId,
                            @NonNull String schoolName,
                            boolean isSchoolEnter) {
        Intent intent = new Intent(context, OnlineClassListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA_SCHOOL_ID, schoolId);
        bundle.putString(KEY_EXTRA_SCHOOL_NAME, schoolName);
        bundle.putBoolean(KEY_EXTRA_IS_SCHOOL_ENTER, isSchoolEnter);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
