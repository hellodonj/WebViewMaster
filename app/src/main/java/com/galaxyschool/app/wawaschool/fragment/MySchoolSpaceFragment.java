package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.ActClassroomActivity;
import com.galaxyschool.app.wawaschool.AirClassroomActivity;
import com.galaxyschool.app.wawaschool.BasicUserInfoActivity;
import com.galaxyschool.app.wawaschool.CampusPatrolMainActivity;
import com.galaxyschool.app.wawaschool.ClassResourceListActivity;
import com.galaxyschool.app.wawaschool.ClassSpaceActivity;
import com.galaxyschool.app.wawaschool.CommentStatisticActivity;
import com.galaxyschool.app.wawaschool.ContactsActivity;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolSpaceActivity;
import com.galaxyschool.app.wawaschool.SubscribeSearchActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.AnimationUtil;
import com.galaxyschool.app.wawaschool.common.CampusPatrolUtils;
import com.galaxyschool.app.wawaschool.common.PrefsManager;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WebUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.ClassInfoListResult;
import com.galaxyschool.app.wawaschool.pojo.ClassMessageStatistics;
import com.galaxyschool.app.wawaschool.pojo.ClassMessageStatisticsListResult;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeSchoolListResult;
import com.galaxyschool.app.wawaschool.pojo.TabEntityPOJO;
import com.galaxyschool.app.wawaschool.views.ChannelView;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolStarEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.ui.ClassifyIndexActivity;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.HQCCourseListActivity;
import com.lqwawa.intleducation.module.spanceschool.SchoolFunctionStateType;
import com.lqwawa.intleducation.module.spanceschool.SpaceSchoolHolderFragment;
import com.lqwawa.intleducation.module.spanceschool.pager.SchoolFunctionPagerNavigator;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySchoolSpaceFragment extends SchoolSpaceBaseFragment implements SchoolSpaceBaseFragment.Constants {

    public static final String TAG = MySchoolSpaceFragment.class.getSimpleName();

    public static final String[] validSchoolIds = new String[]{
            "18733931-C586-45EE-B9AF-DEFBAD5ACE10",//1 私立青岛银河学校幼儿园
            "45BCD99F-9E4C-4857-AB28-C187170933EB",//2 私立青岛银河学校小学部
            "D3FFA5C0-775F-4B05-9B41-97CA88C05FCF",//3 私立青岛银河学校初中部
            "24B12C5C-5868-4C11-85C1-BCA46E5CD6F1",//4 私立青岛银河学校高中部
            "1e7af14b-c771-4022-9fc6-28f3768f1f83",//5 私立青岛银河学校创学部
            "328ee53a-f96d-420f-b5ec-349b64181599",//6 私立青岛银河学校高中韩国部
            "0F6C708B-40DC-469C-8D3C-D2396DB82F83",//7 银河假日学校
            "CF2BBF92-D844-44D0-B2A4-FB2185644F01",//8 创意绘本屋
            "bfbba4e6-c98a-4160-bca4-540087fb1d89",//9 两栖蛙蛙体验学校
            "D8FE8280-FB40-4B61-9936-08819AA7E611",//10聊城银河学校
            "16746338-CA55-4D67-B4D1-D88CFDD280AF" //11两栖晋城崇实学校
    };

    private View subscribeTipsView;

    private List<SchoolInfo> schoolInfoList;


    private List<SubscribeClassInfo> classInfoList;//加入的学校所有班级集合

    private TextView classTextView;
    private TextView moreClassTextView;

    LinearLayout resourceLayout;
    LinearLayout noClassTipLayout;
    private TextView addToClassTextview;
    private TextView moreResourceTextView;
    private PopupMenu popupMenuSchools;
    private PopupMenu popupMenuClasses;
    private GridView schoolGridView;
    private GridView classGridView;
    private String schoolGridViewTag;
    // 切换机构
    private TextView moreSchoolTextView;
    private TextView mTvToggleSchool;
    // 机构名称与机构头像
    private ImageView mSchoolIcon;
    private TextView mSchoolName;
    // 关注人数
    private TextView mSchoolAttentionCount;
    // 机构星级
    private RatingBar mSchoolStarBar;
    private TextView mTvIntro;

    private HashMap<String, TabEntityPOJO> entryInfoHashMap = new HashMap<String, TabEntityPOJO>();
    private boolean isCampusPatrol;
    private String oldUserId;
    private MyBroadCastReceiver receiver;

    private GridView newlyClassGridView;// 调整后的GridView
    private SpaceSchoolHolderFragment mSpaceHolderFragment;
    private String newlyClassGridViewTag;
    private ChannelView channelView;//可滑动的布局
    private List<ChannelView.ChannelItem> channelItemList = new ArrayList<>();
    private boolean showChannelView = false; //控制是否显示滑动布局
    private boolean isChangeLqCourseTab;
    private String lqCourseSchoolId;
    private String lqCourseClassId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_school_space, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initBroadCastReceiver();
        registerReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDatas();
    }

    private void loadDatas() {
        if (getUserVisibleHint()) {
            if (!isLogin()) {
                return;
            }
            String nowUserId = getUserInfo().getMemberId();
            if (oldUserId != null && oldUserId.equals(nowUserId)) {
                //不切换账户，不重新加载数据
                if (subscribeTipsView != null && subscribeTipsView.getVisibility() == View.VISIBLE) {
                    loadSchools();
                }
            } else {
                //切换账户，重新加载数据
                oldUserId = nowUserId;
                classInfo = null;
                loadSchools();
            }
//            String nowUserId=getUserInfo().getMemberId();
//            if(oldUserId!=null&&oldUserId.equals(nowUserId)){
//                //不切换账户，判断缓存时间是否到，判断是否重新加载数据
//                long timeNow=System.currentTimeMillis();
//                if(DateUtils.isUp3Minites(timeNow,timeHistory)){
//                    timeHistory=timeNow;
//                    try {
//                        Date date=DateUtils.longToDate(timeHistory,"yyyy-MM-dd HH:mm:ss");
//                        String updateTime = getActivity().getString(R.string.cs_update_time);
//                        pullToRefreshView.setLastUpdated(updateTime + date.toLocaleString());
//                        loadSchools();
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    if (!isHasSchool){
//                        loadSchools();
//                    }
//                }
//            }else{
//                //切换账户，重新加载数据
//                oldUserId=nowUserId;
//                timeHistory=System.currentTimeMillis();
//                loadSchools();
//            }
        }
    }

    @Override
    protected void loadSchoolInfo() {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        if (schoolInfo != null) {
            params.put("SchoolId", schoolInfo.getSchoolId());
        }
        params.put("VersionCode", 1);

        // 获取机构星级
        if (schoolInfo != null) {
            SchoolHelper.requestSchoolStar(schoolInfo.getSchoolId(), new DataSource.Callback<SchoolStarEntity>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(SchoolStarEntity schoolStarEntity) {
                    if (EmptyUtil.isNotEmpty(schoolStarEntity)) {
                        float starLevel = schoolStarEntity.getStarLevel();
                        mSchoolStarBar.setRating(starLevel);
                    }
                }
            });
        }


        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL, params, new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()) {
                    return;
                }
                schoolInfo = getResult().getModel();
                if (schoolInfo != null) {

                    RelativeLayout userHeader = (RelativeLayout) findViewById(R.id.user_head_without_tab);
                    RelativeLayout mRootLayout = (RelativeLayout) findViewById(R.id.root_layout);
                    if (schoolInfo.isOnlineSchool()) {
                        // 在线课堂机构
                        mRootLayout.setVisibility(View.VISIBLE);
                        userHeader.setVisibility(View.GONE);
                    } else {
                        // 实体机构
                        mRootLayout.setVisibility(View.GONE);
                        userHeader.setVisibility(View.VISIBLE);
                    }

                    // 开放校园巡查功能
                    // 查询到SchoolInfo，判断显示片段
                    if (schoolInfo.isOnlineSchool()) {
                        // 展示在线课堂功能菜单
                        if (showChannelView) {
                            channelView.setVisibility(View.GONE);
                        } else {
                            if (schoolGridView != null) {
                                schoolGridView.setVisibility(View.GONE);
                            }
                        }
                        final String schoolId = schoolInfo.getSchoolId();
                        String schoolName = schoolInfo.getSchoolName();
                        String roles = schoolInfo.getRoles();
                        String role = UserHelper.getOnlineRoleWithUserRoles(roles);

                        // 是否显示校园助手和校园巡查
                        // 校园巡查/校长助手
                        int state = SchoolFunctionStateType.TYPE_GONE;
                        if ((schoolInfo != null && schoolInfo.isSchoolInspector()) || VipConfig.isVip(getActivity())) {
                            if (CampusPatrolUtils.SHOW_PRINCIPAL_ASSISTANT) {
                                state = SchoolFunctionStateType.TYPE_PRINCIPAL_ASSISTANT;
                            } else {
                                state = SchoolFunctionStateType.TYPE_CAMPUS_PATROL;
                            }
                        }

                        mSpaceHolderFragment = SpaceSchoolHolderFragment.newInstance(schoolId, schoolName, schoolInfo.getSchoolLogo(), role, state);
                        mSpaceHolderFragment.setNavigator(new SchoolFunctionPagerNavigator() {
                            @Override
                            public void onClickBookLibrary() {
                                if (EmptyUtil.isEmpty(schoolInfo)) return;
                                UIUtils.currentSourceFromType = SourceFromType.CHOICE_LIBRARY;
                                ActivityUtils.enterBookStoreListActivity(getActivity(), schoolInfo, true);
                            }

                            @Override
                            public void onClickCampus(@NonNull int state) {
                                if (CampusPatrolUtils.SHOW_PRINCIPAL_ASSISTANT) {
                                    //校长助手
                                    Utils.openPrincipalAssistantPage(getActivity(), getUserInfo(), schoolInfo);
                                } else {
                                    //校园巡查
                                    enterCampusPatrol();
                                }
                            }

                            @Override
                            public void onClickForum() {
                                ActivityUtils.enterSchoolMessageActivity(getActivity(), schoolInfo);
                            }
                        });
                        FragmentManager fragmentManager = getChildFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.function_layout, mSpaceHolderFragment).commitAllowingStateLoss();

                    } else {
                        // 展示在线课堂功能菜单
                        if (showChannelView) {
                            channelView.setVisibility(View.VISIBLE);
                        } else {
                            if (schoolGridView != null) {
                                schoolGridView.setVisibility(View.VISIBLE);
                            }
                        }

                        if (EmptyUtil.isNotEmpty(mSpaceHolderFragment)) {
                            // 取消替换,换成普通的布局
                            FragmentManager fragmentManager = getChildFragmentManager();
                            fragmentManager.beginTransaction().detach(mSpaceHolderFragment).commitAllowingStateLoss();

                            mSpaceHolderFragment = null;
                        }
                    }

                    if (schoolInfo.isSchoolInspector()) {
                        isCampusPatrol = true;
                    } else {
                        isCampusPatrol = false;
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                loadData();
            }
        });
    }

    protected void updateSchoolSpaceViewCount() {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("CategoryId", schoolInfo.getSchoolId());
        params.put("BType", "2");
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.UPDATE_BROWSE_NUM_URL, params, new DefaultListener<ModelResult>(ModelResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()) {
                    return;
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                schoolInfo.setBrowseNum(schoolInfo.getBrowseNum() + 1);
                updateSchoolInfoViews();
                if (schoolInfo != null) {
                    getThumbnailManager().displayUserIconWithDefault(
                            AppSettings.getFileUrl(schoolInfo.getSchoolLogo()),
                            mSchoolIcon, R.drawable.default_school_icon);
                    mSchoolName.setText(schoolInfo.getSchoolName());
                    mSchoolAttentionCount.setText(getString(R.string.str_follow_people, Utils.transferNumberData(schoolInfo.getAttentionNumber())));
                }
            }
        });

    }


    private void initViews() {
        moreSchoolTextView = (TextView) findViewById(R.id.user_more);
        mTvToggleSchool = (TextView) findViewById(R.id.tv_toggle_school);

        mSchoolIcon = (ImageView) findViewById(R.id.iv_avatar);
        mSchoolName = (TextView) findViewById(R.id.tv_school_name);

        mSchoolAttentionCount = (TextView) findViewById(R.id.tv_attention);
        mSchoolStarBar = (RatingBar) findViewById(R.id.grade_rating_bar);
        mTvIntro = (TextView) findViewById(R.id.tv_intro);
        mTvIntro.setOnClickListener(this);

        TextView shareView = (TextView) findViewById(R.id.share_btn);
        TextView tvMenu = (TextView) findViewById(R.id.btn_menu);
        if (shareView != null) {
            setShareView(shareView);
        }

        if (tvMenu != null) {
            setShareView(tvMenu);
        }

        //隐藏一些元素
        countLayout.setVisibility(View.GONE);
        schoolQrCodeView.setVisibility(View.GONE);
        if (schoolIconView != null) {
            AnimationUtil.setAnimation(schoolIconView);
            //点击头像跳转到机构详情页面
            schoolIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterSchoolSpace();
                }
            });
        }

        if (mSchoolIcon != null) {
            AnimationUtil.setAnimation(mSchoolIcon);
            //点击头像跳转到机构详情页面
            mSchoolIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterSchoolSpace();
                }
            });
        }

        View view = findViewById(R.id.subscribe_school_tips_layout);
        if (view != null) {
            view.setOnClickListener(this);
            findViewById(R.id.subscribe_tips_btn).setOnClickListener(this);
        }
        subscribeTipsView = view;

        if (moreSchoolTextView != null) {
            moreSchoolTextView.setText(R.string.toggle_school);
            moreSchoolTextView.setOnClickListener(this);
            moreSchoolTextView.setVisibility(View.GONE);
        }

        if (mTvToggleSchool != null) {
            mTvToggleSchool.setText(R.string.toggle_school);
            mTvToggleSchool.setOnClickListener(this);
            mTvToggleSchool.setVisibility(View.GONE);
        }

        classTextView = (TextView) findViewById(R.id.class_textview);
        moreClassTextView = (TextView) findViewById(R.id.more_class_textview);
        moreClassTextView.setOnClickListener(this);
        moreClassTextView.setVisibility(View.GONE);
        resourceLayout = (LinearLayout) findViewById(R.id.resource_layout);
        noClassTipLayout = (LinearLayout) findViewById(R.id.no_class_tip_layout);
        addToClassTextview = (TextView) findViewById(R.id.add_to_class_textview);
        addToClassTextview.setOnClickListener(this);
        moreResourceTextView = (TextView) findViewById(R.id.more_resource_textview);
        moreResourceTextView.setVisibility(View.GONE);
        moreResourceTextView.setOnClickListener(this);
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
        initGridView();
    }

    private void setShareView(TextView shareView) {
        shareView.setText("");
        Drawable plusDrawable = getResources().getDrawable(R.drawable.icon_plus_white);
        plusDrawable.setBounds(0, 0, plusDrawable.getMinimumWidth(), plusDrawable.getMinimumHeight());
        shareView.setCompoundDrawables(null, null, plusDrawable, null);
        //替换为“＋”，变为下拉菜单。
        shareView.setVisibility(View.VISIBLE);
        shareView.setOnClickListener(v -> showMoreMenu(v));
    }

    private void enterSchoolSpace() {
        if (schoolInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_ID, schoolInfo.getSchoolId());
        args.putString(SchoolSpaceActivity.EXTRA_SCHOOL_NAME, schoolInfo.getSchoolName());
        Intent intent = new Intent(getActivity(), SchoolSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void initGridView() {
        initSchoolGridViewHelper();
        initChannelView();
        initClassGridViewHelper();
        initNewlyClassGridViewHelper();
    }

    /**
     * 初始化滑动布局
     */
    private void initChannelView() {
        channelView = (ChannelView) findViewById(R.id.layout_channel_view);
        if (channelView != null) {
            if (showChannelView) {
                if (schoolGridView != null) {
                    schoolGridView.setVisibility(View.GONE);
                }
                channelView.setVisibility(View.VISIBLE);
            } else {
                if (schoolGridView != null) {
                    schoolGridView.setVisibility(View.VISIBLE);
                }
                channelView.setVisibility(View.GONE);
            }
            //设置item的内边距,可配置left、top、right、bottom的padding
            int itemPadding = (int) (10 * MyApplication.getDensity());
            channelView.setItemTopPadding(2 * itemPadding);
            channelView.setItemBottomPadding(2 * itemPadding);
//            //设置图片大小为：40 * 40 dp
            int imageSize = (int) (40 * MyApplication.getDensity());
            channelView.setImgSize(imageSize);
            //设置图片是否包裹内容显示，true的话，imageSize属性失效。
//            channelView.setImageSizeWrapContent(true);
//            //设置每页显示多少个条目,默认4个
//            channelView.setPageSize(4);
//            //设置小圆点背景，默认绿色。
//            channelView.setSmallCirclePointDrawable(R.drawable.btn_green_indicator_bg);
            //处理item点击事件
            channelView.setOnChannelClickListener(new ChannelView.OnChannelClickListener() {
                @Override
                public void onChannelClick(int index, ChannelView.ChannelItem channelItem) {
                    controlEvent(channelItem.channelId);
                }
            });
        }
    }

    private void loadData() {
        if (!isAdded()) {
            return;
        }
        loadSchoolEntityData();
        loadClassEntityData();
        loadNewlyClassEntityData();
    }

    /**
     * 调整后的布局数据
     */
    private void loadNewlyClassEntityData() {
        if (schoolInfo == null) {
            return;
        }
        entryInfoHashMap.clear();
        List<TabEntityPOJO> itemList = new ArrayList<TabEntityPOJO>();
        TabEntityPOJO item = null;
        //点评统计(老师身份才显示)
        if (classInfo != null && classInfo.isTeacherByRoles()) {
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_COMMENT_STATISTIC;
            item.title = getString(R.string.str_comment_statistic);
            item.resId = R.drawable.icon_comment_statistic;
            itemList.add(item);
            entryInfoHashMap.put(item.type + "", item);
        }
        //通知
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_NOTICE;
        item.title = getString(R.string.notices);
        item.resId = R.drawable.tongzhi;
        itemList.add(item);
        entryInfoHashMap.put(item.type + "", item);

        if (schoolInfo.isOnlineSchool()) {
            //关联课程
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_RELEVANCE_COURSE;
            item.title = getString(R.string.label_space_school_relevance_course);
            item.resId = R.drawable.ic_relevance_course;
            itemList.add(item);
            entryInfoHashMap.put(item.type + "", item);

            //班级论坛
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_WAWA_SHOW;
            item.title = getString(R.string.str_online_class_message);
            item.resId = R.drawable.icon_online_class_discuss;
            itemList.add(item);
            entryInfoHashMap.put(item.type + "", item);
        } else {
            //秀秀
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_WAWA_SHOW;
            item.title = getString(R.string.shows);
            item.resId = R.drawable.xiuxiu;
            itemList.add(item);
            entryInfoHashMap.put(item.type + "", item);
        }

        //创e学堂
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_GEN_E_SCHOOL;
        item.title = getString(R.string.lectures);
        item.resId = R.drawable.chuangexuetang;
        itemList.add(item);
        entryInfoHashMap.put(item.type + "", item);

        //班级信息
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLASS_INFORMATION;
        item.title = getString(R.string.class_detail);
        item.resId = R.drawable.banjixiangqing;
        itemList.add(item);

        getAdapterViewHelper(this.newlyClassGridViewTag).setData(itemList);
    }

    /**
     * 调整后的GridView布局
     */
    private void initNewlyClassGridViewHelper() {
        newlyClassGridView = (GridView) findViewById(R.id.newly_class_grid_view);
        if (newlyClassGridView != null) {
            newlyClassGridView.setNumColumns(1);
            AdapterViewHelper newlyClassGridViewHelper = new AdapterViewHelper(getActivity(), newlyClassGridView, R.layout.item_common_personal_space_entity) {
                @Override
                public void loadData() {
                    loadSchools();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TabEntityPOJO data = (TabEntityPOJO) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.item_icon);
                    if (imageView != null) {
                        imageView.setImageResource(data.getResId());
                        int itemSize = (int) (30 * MyApplication.getDensity());
                        //原图展示
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                        lp.width = itemSize;
                        lp.height = itemSize;
                        imageView.setLayoutParams(lp);
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getTitle());
                    }

                    //红点
                    textView = (TextView) view.findViewById(R.id.item_tips);
                    if (textView != null) {
                        textView.setVisibility(data.messageCount > 0 ? View.VISIBLE : View.GONE);
                    }

                    //顶部分隔线
                    View topLine = view.findViewById(R.id.item_top_line);
                    if (topLine != null) {
                        if (position == 0) {
                            topLine.setVisibility(View.VISIBLE);
                        } else {
                            topLine.setVisibility(View.GONE);
                        }
                    }

                    //底部footer
                    View footerLine = view.findViewById(R.id.item_footer);
                    if (footerLine != null) {
                        if (position == dataAdapter.getCount() - 1) {
                            footerLine.setVisibility(View.GONE);
                        } else {
                            footerLine.setVisibility(View.VISIBLE);
                        }
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }

                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    TabEntityPOJO data = (TabEntityPOJO) holder.data;
                    if (data != null) {
                        controlEvent(data.getType());
                    }
                }
            };
            //根据tag来区分不同的数据源
            this.newlyClassGridViewTag = String.valueOf(newlyClassGridView.getId());
            addAdapterViewHelper(this.newlyClassGridViewTag, newlyClassGridViewHelper);
        }
    }

    private void initSchoolGridViewHelper() {
        schoolGridView = (GridView) findViewById(R.id.school_grid_view);
        if (schoolGridView != null) {
            AdapterViewHelper schoolGridViewHelper = new AdapterViewHelper(getActivity(), schoolGridView, R.layout.item_tab_entity_gridview) {
                @Override
                public void loadData() {
                    loadSchoolEntityData();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TabEntityPOJO data = (TabEntityPOJO) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                    if (imageView != null) {
                        imageView.setImageResource(data.getResId());
                        //原图展示
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
                        lp.width = FrameLayout.LayoutParams.WRAP_CONTENT;
                        lp.height = FrameLayout.LayoutParams.WRAP_CONTENT;
                        imageView.setLayoutParams(lp);
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getTitle());
                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
                        textView.setVisibility(View.INVISIBLE);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    TabEntityPOJO data = (TabEntityPOJO) holder.data;
                    if (data != null) {
                        controlEvent(data.getType());
                    }
                }
            };
            //根据tag来区分不同的数据源
            this.schoolGridViewTag = String.valueOf(schoolGridView.getId());
            addAdapterViewHelper(this.schoolGridViewTag, schoolGridViewHelper);
        }

    }

    private void enterClassResourceByChannel(int channelType) {
        if (classInfo == null) {
            return;
        }
        if (channelType == ClassResourceListActivity.CHANNEL_TYPE_SHOW) {
            classInfo.setIsTempData(true);
        } else {
            classInfo.setIsTempData(false);
        }
        Bundle args = new Bundle();
        args.putString(ClassResourceListActivity.EXTRA_CLASS_ID, classInfo.getClassId());
        args.putString(ClassResourceListActivity.EXTRA_SCHOOL_ID, classInfo.getSchoolId());
        args.putInt(ClassResourceListActivity.EXTRA_CHANNEL_TYPE, channelType);
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putInt(ClassResourceListActivity.EXTRA_ROLE_TYPE, classInfo.getRoleType());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HEAD_MASTER, classInfo.isHeadMaster());
        args.putBoolean(ClassResourceListActivity.EXTRA_IS_HISTORY, classInfo.isHistory());
        args.putBoolean(ClassResourceListActivity.EXTRA_CLASSINFO_TEMP_TYPE_DATA, classInfo.isTempData());
        if (schoolInfo != null) {
            args.putBoolean(ClassResourceListActivity.EXTRA_IS_ONLINE_SCHOOL_CLASS, schoolInfo.isOnlineSchool());
        }
        Intent intent = new Intent(getActivity(), ClassResourceListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterGroupMembers() {
        if (classInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putInt(ContactsActivity.EXTRA_CONTACTS_TYPE, classInfo.getType());
        args.putString(ContactsActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_SCHOOL_NAME, classInfo.getSchoolName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_GRADE_ID, classInfo.getGradeId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_GRADE_NAME, classInfo.getGradeName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putString(ContactsActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putString(ContactsActivity.EXTRA_CONTACTS_HXGROUP_ID, classInfo.getGroupId());
        args.putString("from", GroupExpandListFragment.TAG);
        if (classInfo.isClass()) {
            args.putInt(ClassContactsDetailsFragment.Constants.EXTRA_CLASS_STATUS, classInfo.getIsHistory());
        }
        if (schoolInfo.isOnlineSchool()) {
            args.putBoolean(ClassContactsDetailsFragment.Constants.IS_ONLINE_SCHOOL, true);
        }
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        intent.putExtras(args);
        if (classInfo.isClass()) {
            startActivityForResult(intent, ClassContactsDetailsFragment.Constants.REQUEST_CODE_CLASS_DETAILS);
        } else {
            startActivity(intent);
        }
    }

    private void controlEvent(int type) {
        if (type < 0) {
            return;
        }
        UIUtils.currentSourceFromType = SourceFromType.OTHER;
        switch (type) {

            //学校介绍
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_INTRODUCTION:
                enterSchoolIntroduction();
                break;

            //校园动态
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CAMPUS_DYNAMICS:
                enterMoreSchoolMessage();
                break;

            //学校班级
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CLASS:
                enterSchoolClasses();
                break;

            //校本课程
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_BASED_CURRICULUM:
                UIUtils.currentSourceFromType = SourceFromType.PUBLIC_LIBRARY;
                ActivityUtils.enterBookStoreListActivity(getActivity(), schoolInfo);
                break;

            //学习任务
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LEARNING_TASKS:
                UIUtils.currentSourceFromType = SourceFromType.STUDY_TASK;
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_HOMEWORK);
                break;

            //创e学堂
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_GEN_E_SCHOOL:
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_LECTURE);
                break;
            //通知
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_NOTICE:
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_NOTICE);
                break;
            //秀秀
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_WAWA_SHOW:
                enterClassResourceByChannel(ClassResourceListActivity.CHANNEL_TYPE_SHOW);
                break;
            //班级信息
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLASS_INFORMATION:
                enterGroupMembers();
                break;
            // 关联学程
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_RELEVANCE_COURSE:
                if (EmptyUtil.isEmpty(classInfo)) return;
                OnlineCourseHelper.requestOnlineCourseWithClassId(classInfo.getClassId(), new DataSource.Callback<String>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(R.string.tip_class_not_relevance_course);
                    }
                    
                    @Override
                    public void onDataLoaded(String courseId) {
                        // 进入课程详情
                        String roles = classInfo.getRoles();
                        boolean isTeacher = UserHelper.isTeacher(roles);
                        CourseDetailsActivity.start(getActivity(), courseId, true, UserHelper.getUserId(), isTeacher);
                    }
                });
                break;
            //精品资源库
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CHOICE_BOOKS:
                UIUtils.currentSourceFromType = SourceFromType.CHOICE_LIBRARY;
                ActivityUtils.enterBookStoreListActivity(getActivity(), schoolInfo, true);
                break;
            //习课程馆, 视频馆，图书馆, 练测馆
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LQCOURSE_SHOP:
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_VIDEO_LIBRARY:
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LIBRARY:
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_PRACTICE_LIBRARY:
                enterLqCourseShop(getActivity(), schoolInfo, type);
                break;
            //校园巡查
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_PATROL:
                if (CampusPatrolUtils.SHOW_PRINCIPAL_ASSISTANT) {
                    //校长助手
                    Utils.openPrincipalAssistantPage(getActivity(), getUserInfo(), schoolInfo);
                } else {
                    //校园巡查
                    enterCampusPatrol();
                }
                break;
            //校园电视台
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_DIRECT:
                ActivityUtils.enterCampusDirect(getActivity(), schoolInfo);
                break;
            //班级学程
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLASS_LESSON:
                ActivityUtils.enterClassCourseDetailActivity(getActivity(), schoolInfo, classInfo);
                break;
            //空中课堂
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_AIR_CLASSROOM:
                UIUtils.currentSourceFromType = SourceFromType.AIRCLASS_ONLINE;
                enterAirClassroom();
                break;
            //表演课堂
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_ACT_CLASSROOM:
                enterActClassroom();
                break;
            //点评统计
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_COMMENT_STATISTIC:
                CommentStatisticActivity.start(getActivity(), classInfo);
                break;
            default:
                break;
        }
    }


    private void enterCampusPatrol() {

        if (schoolInfo == null) {
            return;
        }
        Intent intent = new Intent(getActivity(), CampusPatrolMainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SchoolInfo.class.getSimpleName(), schoolInfo);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void enterSchoolIntroduction() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("SchoolId", schoolId);
        WebUtils.openWebView(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INTRODUCTION, params, schoolName);
    }

    /**
     * 进入空中课堂的详情界面
     */
    private void enterAirClassroom() {
        if (classInfo == null) return;
        Intent intent = new Intent(getActivity(), AirClassroomActivity.class);
        Bundle args = new Bundle();
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_SCHOOL_NAME, classInfo.getSchoolName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_GRADE_ID, classInfo.getGradeId());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_GRADE_NAME, classInfo.getGradeName());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putBoolean(AirClassroomActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putBoolean(AirClassroomActivity.EXTRA_IS_HEADMASTER, classInfo.isHeadMaster());
        args.putString(AirClassroomActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putSerializable(AirClassroomActivity.EXTRA_IS_SCHOOLINFO, schoolInfo);
        args.putSerializable(AirClassroomActivity.ExTRA_CLASS_INFO, classInfo);
        args.putInt(AirClassroomActivity.EXTRA_ROLE_TYPE, classInfo.getRoleType());
        if (schoolInfo != null) {
            args.putBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS, schoolInfo.isOnlineSchool());
        }
        intent.putExtras(args);
        startActivity(intent);
    }

    /**
     * 进入表演课堂的列表详情
     */
    private void enterActClassroom() {
        if (classInfo == null) return;
        Intent intent = new Intent(getActivity(), ActClassroomActivity.class);
        Bundle args = new Bundle();
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_ID, classInfo.getClassMailListId());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_NAME, classInfo.getClassName());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_SCHOOL_ID, classInfo.getSchoolId());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_SCHOOL_NAME, classInfo.getSchoolName());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_GRADE_ID, classInfo.getGradeId());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_GRADE_NAME, classInfo.getGradeName());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_CLASS_ID, classInfo.getClassId());
        args.putBoolean(ActClassroomActivity.EXTRA_IS_TEACHER, classInfo.isTeacherByRoles());
        args.putBoolean(ActClassroomActivity.EXTRA_IS_HEADMASTER, classInfo.isHeadMaster());
        args.putString(ActClassroomActivity.EXTRA_CONTACTS_CLASS_NAME, classInfo.getClassName());
        args.putSerializable(ActClassroomActivity.EXTRA_IS_SCHOOLINFO, schoolInfo);
        intent.putExtras(args);
        startActivity(intent);

    }


    private void loadSchoolEntityData() {

        if (!isAdded()) {
            return;
        }

        List<TabEntityPOJO> itemList = new ArrayList<TabEntityPOJO>();
        TabEntityPOJO item = null;
        //学校介绍
//        TabEntityPOJO item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_INTRODUCTION;
//        item.title = getString(R.string.subs_school_introduction);
//        item.resId = R.drawable.icon_school_introduction;
//        itemList.add(item);
//        if(schoolInfo != null && (schoolInfo.isTeacher() ||  VipConfig.isVip(getActivity()))){
        if (schoolInfo != null && schoolInfo.isTeacher()) {
            //校本资源库
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_BASED_CURRICULUM;
            item.title = getString(R.string.public_course);
            item.resId = R.drawable.xiaobenziyuanku;
            itemList.add(item);
        }

        //习课程馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LQCOURSE_SHOP;
        item.title = getString(R.string.common_course_shop);
        item.resId = R.drawable.ic_lqcourse_shop;
        itemList.add(item);

        //视频馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_VIDEO_LIBRARY;
        item.title = getString(R.string.common_video_library);
        item.resId = R.drawable.ic_video_library;
        itemList.add(item);
        
        //图书馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LIBRARY;
        item.title = getString(R.string.common_library);
        item.resId = R.drawable.ic_library;
        itemList.add(item);

        //练测馆
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_PRACTICE_LIBRARY;
        item.title = getString(R.string.common_practice_library);
        item.resId = R.drawable.ic_practice_library;
        itemList.add(item);

        //学校班级
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CLASS;
        item.title = getString(R.string.school_and_class);
        item.resId = R.drawable.xuexiaobanji;
        itemList.add(item);

        if (schoolInfo != null && schoolInfo.isTeacher()) {
            //新版的精品资源库
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CHOICE_BOOKS;
            item.title = getString(R.string.choice_books);
            item.resId = R.drawable.lqyinheguoji;
            itemList.add(item);
        }


        //校园电视台
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_DIRECT;
        item.title = getString(R.string.campus_now_direct);
        item.resId = R.drawable.xiaoyuandianshitai;
        itemList.add(item);

        //校园动态
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SCHOOL_CAMPUS_DYNAMICS;
        item.title = getString(R.string.school_message);
        item.resId = R.drawable.xuexiaodongtai;
        itemList.add(item);

        //校园巡查/校长助手
        if (isCampusPatrol || VipConfig.isVip(getActivity())) {
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CAMPUS_PATROL;

            if (CampusPatrolUtils.SHOW_PRINCIPAL_ASSISTANT) {
                //显示校长助手
                item.title = getString(R.string.principal_assistant);
                item.resId = R.drawable.xiaozhangzhushou;
            } else {
                //显示校园巡查
                item.title = getString(R.string.campus_patrol);
                item.resId = R.drawable.icon_campus_patrol;
            }
            itemList.add(item);
        }

        if (getAdapterViewHelper(schoolGridViewTag).hasData()) {
            getAdapterViewHelper(schoolGridViewTag).clearData();
        }
        getAdapterViewHelper(schoolGridViewTag).setData(itemList);
        //组装ViewPager页卡
        toChannelList(itemList);
    }

    /**
     * 转换数据
     *
     * @param itemList
     */
    private void toChannelList(List<TabEntityPOJO> itemList) {
        if (channelView != null) {
            if (itemList != null && itemList.size() > 0) {
                channelItemList.clear();
                for (TabEntityPOJO entity : itemList) {
                    if (entity != null) {
                        ChannelView.ChannelItem item = new ChannelView.ChannelItem();
                        item.channelId = entity.type;
                        item.channelTitle = entity.title;
                        item.channelIcon = entity.resId;
                        channelItemList.add(item);
                    }
                }
                channelView.setData(channelItemList);
            }
        }
    }

    private void loadClassEntityData() {
        entryInfoHashMap.clear();
        List<TabEntityPOJO> itemList = new ArrayList<TabEntityPOJO>();

        //学习任务
        TabEntityPOJO item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LEARNING_TASKS;
        item.title = getString(R.string.learning_tasks);
        item.resId = R.drawable.icon_learning_tasks;
        itemList.add(item);
        entryInfoHashMap.put(item.type + "", item);

        //班级学程
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLASS_LESSON;
        item.title = getString(R.string.str_class_lesson);
        item.resId = R.drawable.icon_class_lesson;
        itemList.add(item);

        //空中课堂
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_AIR_CLASSROOM;
        item.title = getString(R.string.air_classroom);
        item.resId = R.drawable.airclass_icon;
        itemList.add(item);

        //表演课堂
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_ACT_CLASSROOM;
        item.title = getString(R.string.act_classroom);
        item.resId = R.drawable.act_classroom_icon;
        itemList.add(item);

        getCurrAdapterViewHelper().setData(itemList);
    }

    private void initClassGridViewHelper() {

        classGridView = (GridView) findViewById(R.id.class_grid_view);
        if (classGridView != null) {
            classGridView.setNumColumns(4);
            AdapterViewHelper classGridViewHelper = new AdapterViewHelper(getActivity(), classGridView, R.layout.item_tab_entity_gridview) {
                @Override
                public void loadData() {
                    loadSchools();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TabEntityPOJO data = (TabEntityPOJO) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    //头像
                    ImageView imageView = (ImageView) view.findViewById(R.id.icon_head);
                    if (imageView != null) {
                        imageView.setImageResource(data.getResId());
                        imageView.setPadding(0, 0, 0, 0);
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getTitle());
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                        lp.topMargin = 0;
                        textView.setLayoutParams(lp);
                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
                        textView.setVisibility(View.GONE);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    //红点
                    imageView = (ImageView) view.findViewById(R.id.icon_selector);
                    if (imageView != null) {
//                        if (data.messageCount > 99) {
//                            textView.setText("99+");
//                        } else {
//                            textView.setText(String.valueOf(data.messageCount));
//                        }
                        imageView.setVisibility(data.messageCount > 0 ? View.VISIBLE : View.GONE);

                    }
                    holder.data = data;
                    //item的padding
                    int padding = (int) (20 * MyApplication.getDensity());
                    view.setPadding(0, padding, 0, padding);
                    view.setTag(holder);
                    return view;
                }

                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null || holder.data == null) {
                        return;
                    }
                    TabEntityPOJO data = (TabEntityPOJO) holder.data;
                    if (data != null) {
                        controlEvent(data.getType());
                    }
                }
            };
            setCurrAdapterViewHelper(classGridView, classGridViewHelper);
        }
    }


    private void loadSchools() {
        if (!isLogin()) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_LIST_URL, params, new DefaultPullToRefreshListener<SubscribeSchoolListResult>(SubscribeSchoolListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                SubscribeSchoolListResult result = getResult();
                if (result == null || !result.isSuccess() || result.getModel() == null) {
                    subscribeTipsView.setVisibility(View.VISIBLE);
                    return;
                }
                updateSchools(result);
            }
        });
    }


    private void updateSchools(SubscribeSchoolListResult result) {
        List<SchoolInfo> list = result.getModel().getSubscribeNoList();
        Utils.removeOnlineSchoolInfo(list);
        if (list == null || list.size() <= 0) {
            subscribeTipsView.setVisibility(View.VISIBLE);
            if (schoolInfoList != null) {
                schoolInfoList.clear();
            }
            schoolInfo = null;
            return;
        }
        subscribeTipsView.setVisibility(View.GONE);
        schoolInfoList = list;
        if (schoolInfoList.size() > 1) {
            moreSchoolTextView.setVisibility(View.VISIBLE);
            mTvToggleSchool.setVisibility(View.VISIBLE);
        } else {
            moreSchoolTextView.setVisibility(View.GONE);
            mTvToggleSchool.setVisibility(View.GONE);
        }
        boolean tag = false;
        if (!TextUtils.isEmpty(getLatestSchool(getMemeberId()))) {
            for (SchoolInfo sc : schoolInfoList) {
                if (isChangeLqCourseTab && !TextUtils.isEmpty(lqCourseSchoolId)) {
                    if (TextUtils.equals(sc.getSchoolId(), lqCourseSchoolId)) {
                        schoolInfo = sc;
                        tag = true;
                        break;
                    }
                } else {
                    if (sc.getSchoolId().equals(getLatestSchool(getMemeberId()))) {
                        schoolInfo = sc;
                        tag = true;
                        break;
                    }
                }
            }
        }
        if (!tag) {
            schoolInfo = schoolInfoList.get(0);
        }
        loadSchoolInfo();
        saveLatestSchool(getUserInfo().getMemberId(), schoolInfo.getSchoolId());
        //保存当前所有学校的集合
        DemoApplication.getInstance().getPrefsManager().setDataList(getUserInfo().getMemberId() + PrefsManager.PrefsItems.ALL_SCHOOLINFO_LIST, schoolInfoList);
        loadClass();
        if (oldUserId != null && !oldUserId.equals(getUserInfo().getMemberId())) {
            updateSchoolSpaceViewCount();
        } else {
            updateSchoolInfoViews();
            if (schoolInfo != null) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(schoolInfo.getSchoolLogo()),
                        mSchoolIcon, R.drawable.default_school_icon);
                mSchoolName.setText(schoolInfo.getSchoolName());
                mSchoolAttentionCount.setText(getString(R.string.str_follow_people, Utils.transferNumberData(schoolInfo.getAttentionNumber())));
            }
        }
    }

    private void saveLatestClass(String memberId, String classId) {
        SharedPreferencesHelper.setString(getActivity(), memberId + "_ClassId_MySchoolSpaceFragment", classId);
    }

    private String getLatestClass(String memberId) {
        return SharedPreferencesHelper.getString(getActivity(), memberId + "_ClassId_MySchoolSpaceFragment");
    }

    private void saveLatestSchool(String memberId, String schoolId) {
        SharedPreferencesHelper.setString(getActivity(), memberId + "_SchoolId_MySchoolSpaceFragment", schoolId);
    }

    private String getLatestSchool(String memberId) {
        return SharedPreferencesHelper.getString(getActivity(), memberId + "_SchoolId_MySchoolSpaceFragment");
    }

    private void loadClass() {
        if (!isLogin()) {
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolInfo.getSchoolId());
        params.put("IsHistory", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PICBOOKS_GET_CLASS_MAIL_URL, params, new RequestHelper.RequestDataResultListener<ClassInfoListResult>(getActivity(), ClassInfoListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ClassInfoListResult result = getResult();
                if (result == null || !result.isSuccess() || result.getModel() == null) {
                    return;
                }
                List<SubscribeClassInfo> list = result.getModel().getData();
                List<SubscribeClassInfo> dataList = new ArrayList<SubscribeClassInfo>();
                for (SubscribeClassInfo item : list) {
                    if (item.getType() != 1) {
                        if (item.getIsHeader() == 1) {
                            item.setHeadMaster(true);
                        } else {
                            item.setHeadMaster(false);
                        }
                        dataList.add(item);
                    }
                }
                classInfoList = dataList;
                if (classInfoList != null && classInfoList.size() > 0) {
                    if (classInfoList.size() > 1) {
                        moreClassTextView.setVisibility(View.VISIBLE);
                    } else {
                        moreClassTextView.setVisibility(View.GONE);
                    }
                    boolean tag = false;
                    if (!TextUtils.isEmpty(getLatestClass(getMemeberId())) && !TextUtils.isEmpty(getLatestSchool(getMemeberId()))) {
                        for (SubscribeClassInfo sc : classInfoList) {
                            if (sc.getSchoolId() == null || sc.getClassId() == null) {
                                continue;
                            }
                            if (isChangeLqCourseTab && !TextUtils.isEmpty(lqCourseClassId) && !TextUtils.isEmpty(lqCourseSchoolId)) {
                                if (TextUtils.equals(sc.getSchoolId(), lqCourseSchoolId)
                                        && TextUtils.equals(sc.getClassId(), lqCourseClassId)) {
                                    classInfo = sc;
                                    tag = true;
                                    break;
                                }
                            } else {
                                if (sc.getSchoolId().equals(getLatestSchool(getMemeberId())) && sc.getClassId().equals(getLatestClass(getMemeberId()))) {
                                    classInfo = sc;
                                    tag = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!tag) {
                        classInfo = classInfoList.get(0);
                    }
                    saveLatestClass(getUserInfo().getMemberId(), classInfo.getClassId());
                    updateClassView(true);
                } else {
                    updateClassView(false);
                }
            }
        });
    }

    private void updateClassView(boolean tag) {
        if (tag) {
            noClassTipLayout.setVisibility(View.GONE);
            if (classInfo != null) {
                classTextView.setText(classInfo.getClassName());
            }
            resourceLayout.setVisibility(View.VISIBLE);
            loadClassMessageStatistics();
            loadNewlyClassEntityData();
        } else {
            noClassTipLayout.setVisibility(View.VISIBLE);
            resourceLayout.setVisibility(View.INVISIBLE);
        }
        isChangeLqCourseTab = false;
    }


    private void loadClassMessageStatistics() {
        if (classInfo == null) {
            return;
        }
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("ClassId", classInfo.getClassId());
        DefaultDataListener listener = new DefaultDataListener<ClassMessageStatisticsListResult>(ClassMessageStatisticsListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                ClassMessageStatisticsListResult result = getResult();
                if (result == null || !result.isSuccess() || result.getModel() == null) {
                    return;
                }
                updateClassMessageStatistics(result);
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_CLASS_MESSAGE_STATISTICS_LIST_URL, params, listener);
    }

    private void updateClassMessageStatistics(ClassMessageStatisticsListResult result) {
        List<ClassMessageStatistics> list = result.getModel().getData();
        if (list == null || list.size() <= 0) {
            return;
        }

        for (ClassMessageStatistics msg : list) {
            switch (msg.getTypeCode()) {
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_STUDY_TASK:
                    TabEntityPOJO entryInfo = entryInfoHashMap.
                            get(ITabEntityTypeInfo.TAB_ENTITY_TYPE_LEARNING_TASKS + "");
                    if (entryInfo != null) {
                        entryInfo.messageCount = msg.getUnReadNumber();
                    }
                    break;
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_NOTICE:
                    entryInfo = entryInfoHashMap.
                            get(ITabEntityTypeInfo.TAB_ENTITY_TYPE_NOTICE + "");
                    if (entryInfo != null) {
                        entryInfo.messageCount = msg.getUnReadNumber();
                    }
                    break;
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_SHOW:
                    entryInfo = entryInfoHashMap.
                            get(ITabEntityTypeInfo.TAB_ENTITY_TYPE_WAWA_SHOW + "");
                    if (entryInfo != null) {
                        entryInfo.messageCount = msg.getUnReadNumber();
                    }
                case ClassMessageStatistics.CLASS_MESSAGE_TYPE_LECTURE:
                    entryInfo = entryInfoHashMap.
                            get(ITabEntityTypeInfo.TAB_ENTITY_TYPE_GEN_E_SCHOOL + "");
                    if (entryInfo != null) {
                        entryInfo.messageCount = msg.getUnReadNumber();
                    }
                    break;
            }
        }
        getCurrAdapterViewHelper().update();
    }

    private void showSchoolList(View view, AdapterView.OnItemClickListener itemClickListener) {
        if (schoolInfoList == null || schoolInfoList.size() == 0) {
            return;
        }
        List<PopupMenu.PopupMenuData> items = new ArrayList<>();
        PopupMenu.PopupMenuData data = null;
        for (SchoolInfo obj : schoolInfoList) {
            data = new PopupMenu.PopupMenuData();
            data.setText(obj.getSchoolName());
            data.setIsOnlineSchool(obj.isOnlineSchool());
            items.add(data);
        }
        if (popupMenuSchools == null) {
            int width = ScreenUtils.getScreenWidth(getActivity());
            popupMenuSchools = new PopupMenu(getActivity(), itemClickListener, items, width * 2 / 3);
            popupMenuSchools.setChangeTextTitleAttr(true);
        } else {
            popupMenuSchools.setData(items);
        }
        popupMenuSchools.showAsDropDown(view, 0, (int) (10 * MyApplication.getDensity()));
    }

    private AdapterView.OnItemClickListener toggleSchoolListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectSchool(schoolInfoList.get(position));
        }
    };

    private void selectSchool(SchoolInfo schoolInfo) {
        if (schoolInfo == this.schoolInfo) {
            return;
        }
        if (getUserInfo() != null || !TextUtils.isEmpty(getUserInfo().getMemberId())) {
            saveLatestSchool(getUserInfo().getMemberId(), schoolInfo.getSchoolId());
        }
        this.schoolInfo = schoolInfo;
        updateSchoolSpaceViewCount();
        loadClass();
        loadSchoolInfo();
    }

    private void enterSubscribeSearch() {
        if (TextUtils.isEmpty(getUserInfo().getRealName())) {
            showDialog();
            return;
        }
        Intent intent = new Intent(getActivity(), SubscribeSearchActivity.class);
        intent.putExtra(SubscribeSearchActivity.EXTRA_SUBSCRIPE_SEARCH_TYPE, SubscribeSearchActivity.SUBSCRIPE_SEARCH_SCHOOL);
        intent.putExtra(SubscribeSearchActivity.IS_NEED_SHOW_JOIN_STATE, true);
        startActivity(intent);
    }

    private void showDialog() {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(getActivity(), null,
                getString(R.string.pls_input_real_name)
                , getString(R.string.cancel),
                null, getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterUserDetail();
            }
        });
        messageDialog.show();
    }

    private void enterUserDetail() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), BasicUserInfoActivity.class);
        intent.putExtra("origin", MySchoolSpaceFragment.class.getSimpleName());
        intent.putExtra("userInfo", getUserInfo());
        startActivity(intent);
    }

    private void enterMoreSchoolMessage() {
        ActivityUtils.enterSchoolMessageActivity(getActivity(), schoolInfo);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.subscribe_tips_btn) {
            enterSubscribeSearch();
        } else if (v.getId() == R.id.user_more) {
            showSchoolList(v, toggleSchoolListener);
        } else if (v.getId() == R.id.tv_toggle_school) {
            showSchoolList(v, toggleSchoolListener);
        } else if (v.getId() == R.id.more_class_textview) {
            showMoreClasses(getActivity(), ToggleClassListener, v);
        } else if (v.getId() == R.id.add_to_class_textview) {
            enterSchoolClasses();
        } else if (v.getId() == R.id.more_resource_textview) {
            if (classInfo != null) {
                enterClassSpace(classInfo);
            }
        } else if (v.getId() == R.id.tv_intro) {
            enterSchoolIntroduction();
        } else {
            super.onClick(v);
        }
    }

    private void enterClassSpace(SubscribeClassInfo classInfo) {
        classInfo.setSchoolName(schoolName);
        Bundle args = new Bundle();
        if (classInfo != null) {
            args.putString(ClassSpaceActivity.EXTRA_CLASS_ID, classInfo.getClassId());
        }
        Intent intent = new Intent(getActivity(), ClassSpaceActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent, ClassSpaceActivity.REQUEST_CODE_CLASS_SPACE);
    }

    //展示班级列表
    private void showMoreClasses(Activity activity, AdapterView.OnItemClickListener itemClickListener, View AnchorView) {
        if (classInfoList == null || classInfoList.size() == 0) {
            return;
        }
        List<PopupMenu.PopupMenuData> itemDatas = new ArrayList<>();
        for (int i = 0; i < classInfoList.size(); i++) {
            PopupMenu.PopupMenuData data = new PopupMenu.PopupMenuData();
            if (classInfoList.get(i) != null && !TextUtils.isEmpty(classInfoList.get(i).getClassName())) {
                data.setText(classInfoList.get(i).getClassName());
                itemDatas.add(data);
            }
        }
        if (popupMenuClasses == null) {
            popupMenuClasses = new PopupMenu(activity, itemClickListener, itemDatas);
        } else {
            popupMenuClasses.setData(itemDatas);
        }
        popupMenuClasses.showAsDropDown(AnchorView);
    }

    //切换班级
    private AdapterView.OnItemClickListener ToggleClassListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SubscribeClassInfo classInfo1 = classInfoList.get(position);
            if (classInfo1 == null || classInfo1.getClassId() == null) {
                return;
            }
            if (classInfo == null) {
                return;
            }
            if (classInfo1.getClassId().equals(classInfo.getClassId()) && classInfo1.getSchoolId().equals(classInfo.getSchoolId())) {
                return;
            } else {
                if (getUserInfo() != null || !TextUtils.isEmpty(getUserInfo().getMemberId())) {
                    saveLatestSchool(getUserInfo().getMemberId(), classInfo1.getSchoolId());
                    saveLatestClass(getUserInfo().getMemberId(), classInfo1.getClassId());
                }
                classInfo = classInfo1;
            }
            //根据切换的班级去加载图书信息
            if (classInfo != null) {
                updateClassView(true);
            }
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        if (popupMenuSchools != null) {
            popupMenuSchools.dismiss();
        }
        if (popupMenuClasses != null) {
            popupMenuClasses.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }


    public static final String ACTION_LOAD_DATA = TAG + "_action_load_data";

    private final class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ACTION_LOAD_DATA)) {
                loadSchools();
            } else if (TextUtils.equals(HomeActivity.ACTION_CHANGE_LQCOURSE_TAB, intent.getAction())) {
                //接收学程过来的进入机构详情页
                isChangeLqCourseTab = true;
                lqCourseSchoolId = intent.getStringExtra("schoolId");
                lqCourseClassId = intent.getStringExtra("classId");
                loadSchools();
            }
        }
    }

    private void initBroadCastReceiver() {
        receiver = new MyBroadCastReceiver();

    }

    private void registerReceiver() {
        if (receiver != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_LOAD_DATA);
            filter.addAction(HomeActivity.ACTION_CHANGE_LQCOURSE_TAB);
            getActivity().registerReceiver(receiver, filter);
        }
    }

    private void unregisterReceiver() {
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    public static void sendBrocast(Activity activity) {
        Intent broadIntent = new Intent();
        broadIntent.setAction(MySchoolSpaceFragment.ACTION_LOAD_DATA);
        activity.sendBroadcast(broadIntent);
    }


}
