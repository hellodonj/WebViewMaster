package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.AccountActivity;
import com.galaxyschool.app.wawaschool.ActClassroomActivity;
import com.galaxyschool.app.wawaschool.AssociateAccountActivity;
import com.galaxyschool.app.wawaschool.CaptureActivity;
import com.galaxyschool.app.wawaschool.ClassContactsActivity;
import com.galaxyschool.app.wawaschool.DeviceManagementActivity;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.MediaMainActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyCollectionListActivity;
import com.galaxyschool.app.wawaschool.MyDownloadListActivity;
import com.galaxyschool.app.wawaschool.MyMessageListActivity;
import com.galaxyschool.app.wawaschool.MyStudyTaskActivity;
import com.galaxyschool.app.wawaschool.NocMyWorkActivity;
import com.galaxyschool.app.wawaschool.PersonalContactsActivity;
import com.galaxyschool.app.wawaschool.PersonalPostBarListActivity;
import com.galaxyschool.app.wawaschool.PersonalSpaceActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.ShareScreenActivity;
import com.galaxyschool.app.wawaschool.SubscribeMainActivity;
import com.galaxyschool.app.wawaschool.TalentCipherActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.AnimationUtil;
import com.galaxyschool.app.wawaschool.common.DensityUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.SwitchButton;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WebUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.account.SettingFragment;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.helper.ThirdPartyLoginHelper;
import com.galaxyschool.app.wawaschool.pojo.BindThirdParty;
import com.galaxyschool.app.wawaschool.pojo.BindThirdPartyResult;
import com.galaxyschool.app.wawaschool.pojo.CheckMarkInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.NutritionRecipeObject;
import com.galaxyschool.app.wawaschool.pojo.NutritionRecipeObjectResult;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolYkStateInfo;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.TabEntityPOJO;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.google.gson.Gson;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.common.utils.SPUtil;
import com.lqwawa.intleducation.factory.constant.SharedConstant;
import com.lqwawa.intleducation.factory.event.EventConstant;
import com.lqwawa.intleducation.factory.event.EventWrapper;
import com.lqwawa.intleducation.module.box.TutorialSpaceBoxFragment;
import com.lqwawa.intleducation.module.discovery.ui.LQCourseActivity;
import com.lqwawa.intleducation.module.discovery.ui.UserCoinActivity;
import com.lqwawa.intleducation.module.discovery.ui.person.mygive.MyGiveInstructionActivity;
import com.lqwawa.intleducation.module.discovery.ui.subject.SubjectActivity;
import com.lqwawa.intleducation.module.learn.ui.MyLiveListActivity;
import com.lqwawa.intleducation.module.user.ui.MyOrderListActivity;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.lqbaselib.net.library.ResourceResult;
import com.lqwawa.lqbaselib.pojo.MessageEvent;
import com.lqwawa.mooc.common.MOOCHelper;
import com.lqwawa.mooc.modle.tutorial.TutorialHomePageActivity;
import com.lqwawa.mooc.modle.tutorial.TutorialParams;
import com.lqwawa.mooc.modle.tutorial.regist.TutorialRegisterActivity;
import com.oosic.apps.share.ShareHelper;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.osastudio.common.utils.TipMsgHelper;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyPersonalSpaceFragment extends ContactsListFragment {

    public static final String TAG = MyPersonalSpaceFragment.class.getSimpleName();

    private static final int MENU_ID_SCAN = 0;
    private static final int MENU_ID_SHARE = 1;
    private static final int MENU_ID_SCREEN_PROJECTION = 2;
    private GridView topGridView;
    private GridView bottomGridView;
    private ImageView userIconView;
    private TextView userNameTxt;
    private TextView authLoginWxTxt;
    private TextView authLoginQqTxt;
    private LinearLayout openAssistanceLayout;
    private LinearLayout assistanceModeLayout;
    private SwitchButton switchButton;

    private String topGridViewTag;
    private Map<Integer, TabEntityPOJO> entryMap = new HashMap();
    private List<SchoolInfo> schoolList;
    private UserInfo userInfo;
    private String userId;
    private String qqBindUnionid;
    private String wxBindUnionid;
    private boolean isBindQQ = false;
    private boolean isBindWx = false;

    public interface ITabEntityTypeInfo {

        int TAB_ENTITY_TYPE_CREATE_MICRO_LESSON = 0;
        int TAB_ENTITY_TYPE_LEARNING_TASKS = 1;
        int TAB_ENTITY_TYPE_INTERACTIVE_Q_AND_A = 2;
        int TAB_ENTITY_TYPE_MY_LIVE_CLASS = 3;
        int TAB_ENTITY_TYPE_CLOUD_SPACE = 4;
        int TAB_ENTITY_TYPE_MY_BOOK_SHELF = 5;
        int TAB_ENTITY_TYPE_MY_COLLECTION = 6;
        int TAB_ENTITY_TYPE_MY_DOWNLOAD = 7;

        int TAB_ENTITY_TYPE_MY_MESSAGE = 8;
        int TAB_ENTITY_TYPE_CLASS_ADDRESS_BOOK = 9;
        int TAB_ENTITY_TYPE_PERSONAL_ADDRESS_BOOK = 10;

        //营养膳食
        int TAB_ENTITY_TYPE_NUTRITION_RECIPES = 11;//营养食谱
        int TAB_ENTITY_TYPE_GROWTH_RECORD = 12;//成长记录
        int TAB_ENTITY_TYPE_VACCINATION = 13;//疫苗接种

        int TAB_ENTITY_TYPE_NOC_COMPETITION = 14;//NOC大赛

        //天赋密码
        int TAB_ENTITY_TYPE_TALENT_CIPHER = 15;

        //学习记录
        int TAB_ENTITY_TYPE_LEARNING_RECORD = 16;

        int TAB_ENTITY_TYPE_LQ_GALAXY_INTL = 100;
        int TAB_ENTITY_TYPE_MY_COURSE = 101;
        //我的订单
        int TAB_ENTITY_TYPE_MY_ORDER = 102;
        //关注
        int TAB_ENTITY_TYPE_SUBSCRIBER = 103;
        //设备管理
        int TAB_ENTITY_TYPE_DEVICE_MANAGEMENT = 104;
        //设置
        int TAB_ENTITY_TYPE_SETTINGS = 105;
        int TAB_ENTITY_TYPE_MY_LIVE = 106;
        //我的余额
        int TAB_ENTITY_TYPE_USER_COIN = 107;
        //关联账号
        int TAB_ENTITY_TYPE_ASSOCIATED_ACCOUNT = 108;
        //我的表演
        int TAB_ENTITY_TYPE_MY_PERFORMANCE = 109;
        //科目设置
        int TAB_ENTITY_TYPE_SUBJECT_SETTING = 110;

    }

    private String oldUserId;
    // private long timeHistory= 0L;
    // private boolean requestMessageByPerson=false;//区分手动请消息还是非手动，如果是不是人为
    private boolean showNutritionRecipesWithWebViewStyle = true; //控制营养膳食是否用WebView加载
    private boolean shouldShowNutritionRecipe = false;//是否需要显示营养膳食
    /**
     * 是否显示天赋密码
     */
    private boolean shouldShowTalentipher = false;
    /**
     * 天赋密码 家长是否有小孩
     */
    private boolean isHasChild = false;
    private boolean isLoadingTalentChildEnd = false;
    private boolean isLoadingTalentDataEnd = false;
    private List<StudentMemberInfo> childMemberData;
    /**
     * 天赋密码数据集合
     */
    private ArrayList<CheckMarkInfo.ModelBean> talentList = new ArrayList<CheckMarkInfo.ModelBean>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_personal_space, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    private void prepareLoadPushMessage() {
        if (userInfo == null) {
            return;
        }
        String newUserId = getUserInfo().getMemberId();
        if (oldUserId != null && TextUtils.equals(oldUserId, newUserId)) {
            //不切换账户，不加载数据
        } else {
            //切换账户，重新加载数据
            oldUserId = newUserId;
            //加载营养膳食信息
            loadNutritionRecipeData();
            //天赋密码
            loadTalentData();
            loadBindData();
        }
        //  String nowUserId=getUserInfo().getMemberId();
//        if(oldUserId!=null&&oldUserId.equals(nowUserId)){
//            //不切换账户，判断缓存时间是否到，判断是否重新加载数据
//            loadData();
//            long timeNow=System.currentTimeMillis();
//            if(DateUtils.isUp3Minites(timeNow,timeHistory)){
//                timeHistory=timeNow;
//                try {
//                    Date date=DateUtils.longToDate(timeHistory,"yyyy-MM-dd HH:mm:ss");
//                    String updateTime = getActivity().getString(R.string.cs_update_time);
//                    pullToRefreshView.setLastUpdated(updateTime + date.toLocaleString());
//                    loadPushMessages();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        }else{
//            //切换账户，重新加载数据
//            oldUserId=nowUserId;
//            timeHistory=System.currentTimeMillis();
//            loadData();
//            loadPushMessages();
//        }
    }

    private void loadCommonData() {
        if (getUserVisibleHint()) {
            if (isLogin()) {
                loadUserInfo();
                userInfo = getUserInfo();
                loadBindData();
                prepareLoadPushMessage();
            }
        }
    }

    /**
     * 判断是否需要展示营养膳食模块
     *
     * @return
     */
    private boolean shouldShowNutritionModule() {
        if (AppSettings.LOGIN_NUTRITION) {
            return shouldShowNutritionRecipe;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCommonData();
    }

    private void loadEntityData() {
        loadTopGridViewEntityData();
        loadBottomGridViewEntityData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        deleteFiles();
        initViews();
    }


    private void initViews() {
        if (getView() == null) {
            return;
        }
        userIconView = (ImageView) findViewById(R.id.im_user_icon);
        if (userIconView != null) {
            //头像呼吸效果
            AnimationUtil.setAnimation(userIconView);
            userIconView.setOnClickListener(this);
        }
        userNameTxt = (TextView) findViewById(R.id.tv_user_name);
        authLoginWxTxt = (TextView) findViewById(R.id.tv_auth_login_wx);
        authLoginQqTxt = (TextView) findViewById(R.id.tv_auth_login_qq);
        //关闭qq关联
        authLoginWxTxt.setOnClickListener(v -> handlerBindShareMediaData(SHARE_MEDIA.WEIXIN));
        authLoginQqTxt.setOnClickListener(v -> handlerBindShareMediaData(SHARE_MEDIA.QQ));

        TextView shareView = (TextView) findViewById(R.id.btn_user_add);
        if (shareView != null) {
            shareView.setOnClickListener(v -> showMoreMenu(v));
        }

        initAssistanceView();
        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
                R.id.contacts_pull_to_refresh);
        setStopPullUpState(true);
        setPullToRefreshView(pullToRefreshView);
        initGridView();
    }

    private void initAssistanceView(){
        assistanceModeLayout = (LinearLayout) findViewById(R.id.ll_assistance_mode);
        openAssistanceLayout = (LinearLayout) findViewById(R.id.ll_open_assistance);
        openAssistanceLayout.setOnClickListener(v -> {
            //申请开通帮辅
            TutorialRegisterActivity.show(getActivity());
        });
        switchButton = (SwitchButton) findViewById(R.id.sb_btn);
        switchButton.setOnCheckedChangeListener((view,check) -> {
            //切换模式
            HomeActivity activity = null;
            if (getActivity() instanceof HomeActivity){
                activity = (HomeActivity) getActivity();
            }
            if (check) {
                //帮辅模式
                SPUtil.getInstance().put(SharedConstant.KEY_APPLICATION_MODE,true);
                EventBus.getDefault().post(new EventWrapper(TutorialSpaceBoxFragment.KEY_TUTORIAL_MODE_ID,
                        EventConstant.TRIGGER_SWITCH_APPLICATION_MODE));
            } else {
                SPUtil.getInstance().put(SharedConstant.KEY_APPLICATION_MODE,false);
                EventBus.getDefault().post(new EventWrapper(TutorialSpaceBoxFragment.KEY_COURSE_MODE_ID,
                        EventConstant.TRIGGER_SWITCH_APPLICATION_MODE));
            }
            if (activity != null) {
                activity.updateBottomViewText();
            }
        });
    }

    private void initGridView() {
        initTopGridViewHelper();
        initBottomGridViewHelper();
    }

    private void initTopGridViewHelper() {
        topGridView = (GridView) findViewById(R.id.top_grid_view);
        if (topGridView != null) {
            AdapterViewHelper topGridViewHelper = new AdapterViewHelper(getActivity(),
                    topGridView, R.layout.item_tab_entity_gridview) {
                @Override
                public void loadData() {
                    loadTopGridViewEntityData();
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
                        int itemSize = (int) (40 * MyApplication.getDensity());
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)
                                imageView.getLayoutParams();
                        lp.width = itemSize;
                        lp.height = itemSize;
                        imageView.setLayoutParams(lp);
                    }

                    //红点
                    ImageView tips = (ImageView) view.findViewById(R.id.icon_selector);
                    if (tips != null) {
                        if (data.messageCount > 0) {
                            tips.setVisibility(View.GONE);
                        } else {
                            tips.setVisibility(View.GONE);
                        }
                    }
                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.title);
                    if (textView != null) {
                        textView.setText(data.getTitle());
                        if (!com.lqwawa.intleducation.common.utils.Utils.isZh(getContext())) {
                            //解决英文状态下显示不全问题
                            textView.setLines(2);
                        }

                    }

                    //时间
                    textView = (TextView) view.findViewById(R.id.time);
                    if (textView != null) {
                        //确保标题是两行文字时完整显示出来
                        textView.setVisibility(View.GONE);
                    }

                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new ViewHolder();
                    }
                    holder.data = data;
                    //item的padding
                    int padding = (int) (10 * MyApplication.getDensity());
                    view.setPadding(0, 0, 0, padding);
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
            this.topGridViewTag = String.valueOf(topGridView.getId());
            addAdapterViewHelper(this.topGridViewTag, topGridViewHelper);
        }

    }

    private void controlEvent(int type) {

        if (type < 0) {
            return;
        }
        UIUtils.currentSourceFromType = SourceFromType.OTHER;
        Intent intent;
        switch (type) {

            //LQ云板
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CREATE_MICRO_LESSON:
                enterMediaMainActivity();
                break;

            //学习任务
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LEARNING_TASKS:
                UIUtils.currentSourceFromType = SourceFromType.STUDY_TASK;
                enterStudyTaskList();
                break;

            //互动答疑
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_INTERACTIVE_Q_AND_A:
//                InteractionHelper.initInteraction();
//                InteractionHelper.setInteractionParams();
//                InteractionHelper.startUpInteraction();
                break;

            //我的直播课
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_LIVE_CLASS:
                break;

            //云空间
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLOUD_SPACE:
                enterMoreResource();
                break;

            //个人资源库
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_BOOK_SHELF:
                ActivityUtils.gotoMediaTypeList(getActivity(), schoolList);
                break;

            //我的收藏
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_COLLECTION:
                enterMyCollectionList();
                break;

            //我的下载
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_DOWNLOAD:
                intent = new Intent(getActivity(), MyDownloadListActivity.class);
                startActivity(intent);
                break;
            //NOC大赛
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_NOC_COMPETITION:
                enterNocCompetition();
                break;
            //天赋密码
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_TALENT_CIPHER:
                if (!isLoadingTalentDataEnd) {
                    return;
                }
                TalentCipherActivity.start(getActivity(), talentList);
                break;

            //LQ银河国际
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LQ_GALAXY_INTL: {
                //LqGalaxyIntlActivity.start(getActivity());
                MOOCHelper.init(getUserInfo());
                //"93"对应LQ银河国际分类 "40"对应"英语标签"
                LQCourseActivity.start(getActivity());
                break;
            }
            //我的课程
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_COURSE: {
                MOOCHelper.init(getUserInfo());
                //"93"对应LQ银河国际分类 "40"对应"英语标签"
                // MyCourseListActivity.start(getActivity());
                // com.lqwawa.intleducation.module.discovery.ui.person.mycourse.MyCourseListActivity.start(getActivity());
                MyGiveInstructionActivity.show(getActivity());
                break;
            }
            //我的直播
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_LIVE: {
                MOOCHelper.init(getUserInfo());
                MyLiveListActivity.start(getActivity());
                break;
            }
            //我的消息
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_MESSAGE:
                enterMyMessageList();
                // timeHistory= 0L;
                break;

            //班级通讯录
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLASS_ADDRESS_BOOK:
                intent = new Intent(getActivity(), ClassContactsActivity.class);
                startActivity(intent);
                // timeHistory= 0L;
                break;

            //个人通讯录
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_PERSONAL_ADDRESS_BOOK:
                intent = new Intent(getActivity(), PersonalContactsActivity.class);
                startActivity(intent);
                //timeHistory= 0L;
                break;
            //营养食谱
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_NUTRITION_RECIPES:
                //打开营养膳食
                if (showNutritionRecipesWithWebViewStyle) {
                    //网页加载
                    Utils.openNutritionRecipesWebPage(getActivity(), userInfo);
                } else {

                }
                break;
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_LEARNING_RECORD:
                //学生记录
                HashMap<String, String> params = new HashMap<>();
                params.put("memberid", userInfo.getMemberId());
                List<SchoolInfo> list = new ArrayList<>();
                if (schoolList != null && schoolList.size() > 0) {
                    for (int i = 0; i < schoolList.size(); i++) {
                        SchoolInfo schoolInfo = schoolList.get(i);
                        if (schoolInfo.isStudent()) {
                            list.add(schoolInfo);
                        }
                    }
                    if (list.size() > 0) {
                        String json = new Gson().toJson(list);
                        params.put("SchoolList", json);
                    } else {
                        params.put("SchoolList", "");
                    }
                } else {
                    params.put("SchoolList", "");
                }
                params.put("realname", userInfo.getRealName());
                params.put("form", "lqapp");
                WebUtils.openCommonWebView(getActivity(), ServerUrl.STUDY_RECORD_BASE_URL, params, getString(R.string
                        .learning_record), false);
                break;
            //成长记录
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_GROWTH_RECORD:
                break;
            //疫苗接种
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_VACCINATION:
                break;

            //---------------------//
            //我的余额
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_USER_COIN:

                intent = new Intent(getActivity(), UserCoinActivity.class);
                intent.putExtra("memberId", getMemeberId());
                startActivity(intent);
                break;
            //我的订单
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_ORDER:
                MOOCHelper.init(getUserInfo());
                MyOrderListActivity.newInstance(getActivity());
                break;

            //关注
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SUBSCRIBER:
                intent = new Intent(getActivity(), SubscribeMainActivity.class);
                startActivity(intent);
                break;

            //设备管理
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_DEVICE_MANAGEMENT:
                intent = new Intent(getActivity(), DeviceManagementActivity.class);
                startActivity(intent);
                break;

            //设置
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SETTINGS:
                intent = new Intent(getActivity(), AccountActivity.class);
                intent.putExtra("fragmentTag", SettingFragment.TAG);
                intent.putExtra("isLogin", false);
                startActivity(intent);
                break;

            //关联账号
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_ASSOCIATED_ACCOUNT:
                AssociateAccountActivity.start(getActivity());
                break;

            //我的表演
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_PERFORMANCE:
                loadChildInfo();
                break;

            //科目设置
            case ITabEntityTypeInfo.TAB_ENTITY_TYPE_SUBJECT_SETTING:
                enterSubjectSettingDetail();
                break;

            default:
                break;
        }
    }

    /**
     * 进入科目设置界面
     */
    private void enterSubjectSettingDetail(){
        SubjectActivity.show(getActivity());
    }

    private void loadChildInfo() {
        childMemberData = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", getMemeberId());
        RequestHelper.RequestDataResultListener listener = new RequestHelper
                .RequestDataResultListener<HomeworkChildListResult>(getContext(),
                HomeworkChildListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                if (getResult() == null || !getResult().isSuccess()
                        || getResult().getModel() == null) {
                    return;
                }
                childMemberData = getResult().getModel().getData();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                ActClassroomActivity.start(getActivity(), true, childMemberData);
            }
        };
        listener.setShowLoading(true);
        RequestHelper.sendPostRequest(getContext(), ServerUrl.GET_STUDENT_BY_PARENT_URL, params, listener);
    }


    private void enterNocCompetition() {
        Intent intent = new Intent(getActivity(), NocMyWorkActivity.class);
        startActivity(intent);
    }

    private void enterStudyTaskList() {
        Intent intent = new Intent(getActivity(), MyStudyTaskActivity.class);
        startActivity(intent);
    }

    protected void enterMoreResource() {
        if (userInfo == null) {
            return;
        }
        Bundle args = new Bundle();
        args.putString(PersonalPostBarListActivity.EXTRA_MEMBER_ID, userInfo.getMemberId());
        args.putBoolean(PersonalPostBarListActivity.EXTRA_IS_TEACHER, userInfo.isTeacher());
        Intent intent = new Intent(getActivity(), PersonalPostBarListActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void enterMediaMainActivity() {

        Intent intent = new Intent(getActivity(), MediaMainActivity.class);
        Bundle args = new Bundle();
        args.putInt(MediaListFragment.EXTRA_MEDIA_TYPE, MediaType.ONE_PAGE);
        args.putString(MediaListFragment.EXTRA_MEDIA_NAME, getString(R.string.createspace));
        args.putBoolean(MediaListFragment.EXTRA_IS_REMOTE, false);
        args.putBoolean(MediaListFragment.EXTRA_IS_SHOW_LQ_TOOLS, true);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void initBottomGridViewHelper() {

        bottomGridView = (GridView) findViewById(R.id.bottom_grid_view);
        if (bottomGridView != null) {
            bottomGridView.setNumColumns(1);
            AdapterViewHelper bottomGridViewHelper = new AdapterViewHelper(getActivity(),
                    bottomGridView, R.layout.item_common_personal_space_entity) {
                @Override
                public void loadData() {
                    //刷新营养膳食信息
                    loadNutritionRecipeData();
                    loadTalentData();
                    loadUserInfo();
//                    if(isLogin()){
//                        oldUserId=getUserInfo().getMemberId();
//                        timeHistory=System.currentTimeMillis();
//                    }
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
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)
                                imageView.getLayoutParams();
                        lp.width = itemSize;
                        lp.height = itemSize;
                        imageView.setLayoutParams(lp);
                    }
                    //红点
                    TextView tips = (TextView) view.findViewById(R.id.item_tips);
                    if (tips != null) {
                        if (data.messageCount > 0) {
                            tips.setVisibility(View.GONE);
                        } else {
                            tips.setVisibility(View.GONE);
                        }
                    }

                    //标题
                    TextView textView = (TextView) view.findViewById(R.id.item_title);
                    if (textView != null) {
                        textView.setText(data.getTitle());
                    }
                    //底部footer
                    View footerLine = view.findViewById(R.id.item_footer);
                    if (footerLine != null) {
                        if (data.hasFooter || position == dataAdapter.getCount() - 1) {
                            footerLine.setVisibility(View.INVISIBLE);
                        } else {
                            footerLine.setVisibility(View.VISIBLE);
                        }
                    }

                    //底部10dp分割线
                    View footerDivider = view.findViewById(R.id.item_divider_footer);
                    if (footerDivider != null) {
                        footerDivider.setVisibility(data.isHasFooter() ? View.VISIBLE : View.GONE);
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
            setCurrAdapterViewHelper(bottomGridView, bottomGridViewHelper);
        }
    }

    private void loadTopGridViewEntityData() {
        if (!isAdded()) {
            return;
        }
        List<TabEntityPOJO> itemList = new ArrayList<TabEntityPOJO>();
        //LQ云板
        TabEntityPOJO item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CREATE_MICRO_LESSON;
        item.title = getString(R.string.createspace);
        item.resId = R.drawable.lqyunban;
        itemList.add(item);
        entryMap.put(item.type, item);

//        //创作微课
//        TabEntityPOJO item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CREATE_MICRO_LESSON;
//        item.title = getString(R.string.create_micro_lesson);
//        item.resId = R.drawable.icon_create_micro_lesson;
//        itemList.add(item);
//        entryMap.put(item.type, item);

        //个人资源库
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_BOOK_SHELF;
        item.title = getString(R.string.my_book_shelf);
        item.resId = R.drawable.gerenziyuanku;
        itemList.add(item);
        entryMap.put(item.type, item);

//        学习任务
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            if (isRealTeacher()) {
                item = new TabEntityPOJO();
                item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LEARNING_TASKS;
                item.title = getString(R.string.assign_task_line);
                item.resId = R.drawable.buzhirenwu;
                itemList.add(item);
                entryMap.put(item.type, item);
            }
        }

//        //LQ英语
//        item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LQ_GALAXY_INTL;
//        item.title = getString(R.string.lq_course);
//        item.resId = R.drawable.lqyingyu;
//        itemList.add(item);
//        entryMap.put(item.type, item);

        // 有在线机构老师身份才显示我的授课
        if (!TextUtils.isEmpty(getMemeberId()) && isOnlineTeacher()) {
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_COURSE;
            item.title = getString(R.string.str_my_Lecture);
            item.resId = R.drawable.icon_my_course;
            itemList.add(item);
            entryMap.put(item.type, item);
        }

        //我的直播
//        item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_LIVE;
//        item.title = getString(R.string.my_live);
//        item.resId = R.drawable.ic_my_live;
//        itemList.add(item);
//        entryMap.put(item.type, item);

        //云贴吧
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLOUD_SPACE;
        item.title = getString(R.string.cloud_post_bar);
        item.resId = R.drawable.yuntieba;
        itemList.add(item);
        entryMap.put(item.type, item);

        //学习记录
//        if (!TextUtils.isEmpty(getMemeberId()) && getUserInfo().isStudent()) {
//            item = new TabEntityPOJO();
//            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_LEARNING_RECORD;
//            item.title = getString(R.string.learning_record);
//            item.resId = R.drawable.learning_record;
//            itemList.add(item);
//            entryMap.put(item.type, item);
//        }

       /* //NOC大赛
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_NOC_COMPETITION;
        item.title = getString(R.string.noc_competition);
        item.resId = R.drawable.nocdasai;
        itemList.add(item);
        entryMap.put(item.type, item);*/


        //天赋密码
        if (shouldShowTalentipher) {
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_TALENT_CIPHER;
            item.title = getString(R.string.str_talent_cipher);
            item.resId = R.drawable.talent_cipher;
            itemList.add(item);
            entryMap.put(item.type, item);
        }


//        //我的课程
//        item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_COURSE;
//        item.title = getString(R.string.my_learning_process);
//        item.resId = R.drawable.ic_my_course;
//        itemList.add(item);
//        entryMap.put(item.type, item);

        //营养食谱
        if (shouldShowNutritionModule()) {
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_NUTRITION_RECIPES;
            item.title = getString(R.string.nutrition_recipes);
            item.resId = R.drawable.yingyangshipu;
            itemList.add(item);
            entryMap.put(item.type, item);
        }

        if (isEntityStudentOrParent()) {
            //学生和家长身份显示
            //我的表演
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_PERFORMANCE;
            item.title = getString(R.string.str_my_performance);
            item.resId = R.drawable.icon_my_performance;
            itemList.add(item);
            entryMap.put(item.type, item);
        }
        //互动答疑
//        item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_INTERACTIVE_Q_AND_A;
//        item.title = getString(R.string.interactive_q_and_a);
//        item.resId = R.drawable.icon_interactive_q_and_a;
//        itemList.add(item);
//        entryMap.put(item.type, item);

//        //我的收藏
//        item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_COLLECTION;
//        item.title = getString(R.string.cs_collect);
//        item.resId = R.drawable.icon_my_collection;
//        itemList.add(item);
//        entryMap.put(item.type, item);


//        //我的直播课
//        item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_LIVE_CLASS;
//        item.title = getString(R.string.my_live_class);
//        item.resId = R.drawable.icon_my_live_class;
//        itemList.add(item);
//        entryMap.put(item.type, item);

        getAdapterViewHelper(topGridViewTag).setData(itemList);
    }

    /**
     * 是否是在线机构的老师
     *
     * @param
     */
    private boolean isOnlineTeacher() {
        if (schoolList != null && schoolList.size() > 0) {
            for (SchoolInfo info : schoolList) {
                if (!info.isOnlineSchool()) continue;
                if (info.isTeacher()) return true;
            }
        }
        return false;
    }


    /**
     * 是否是实体机构的老师
     *
     * @param
     */
    private boolean isRealTeacher() {
        if (schoolList != null && schoolList.size() > 0) {
            for (SchoolInfo info : schoolList) {
                if (info.isOnlineSchool()) continue;
                if (info.isTeacher()) return true;
            }
        }
        return false;
    }

    /**
     * 实体机构的家长和学生
     *
     * @return
     */
    private boolean isEntityStudentOrParent() {
        if (schoolList != null && schoolList.size() > 0) {
            for (SchoolInfo info : schoolList) {
                if (info.isOnlineSchool()) continue;
                if (info.isStudent() || info.isParent()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadBottomGridViewEntityData() {

        if (!isAdded()) {
            return;
        }

        List<TabEntityPOJO> itemList = new ArrayList<TabEntityPOJO>();

        TabEntityPOJO item;

        //我的余额
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_USER_COIN;
        item.title = getString(R.string.my_coins);
        item.resId = R.drawable.user_coin;
        itemList.add(item);
        entryMap.put(item.type, item);

        //我的订单
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_ORDER;
        item.title = getString(R.string.my_orders);
        item.resId = R.drawable.wodedingdan;
        itemList.add(item);
        entryMap.put(item.type, item);

        //我的下载
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_DOWNLOAD;
        item.title = getString(R.string.my_download_courses);
        item.resId = R.drawable.xiazai;
        itemList.add(item);
        entryMap.put(item.type, item);

        //我的消息
//        item = new TabEntityPOJO();
//        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_MY_MESSAGE;
//        item.title = getString(R.string.my_message);
//        item.resId = R.drawable.xiaoxi;
//        itemList.add(item);
//        entryMap.put(item.type, item);

        //班级通讯录
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_CLASS_ADDRESS_BOOK;
        item.title = getString(R.string.class_contacts);
        item.resId = R.drawable.banjitongxunlu;
        itemList.add(item);
        entryMap.put(item.type, item);

        //个人通讯录
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_PERSONAL_ADDRESS_BOOK;
        item.title = getString(R.string.personal_contacts);
        item.resId = R.drawable.gerentongxunlu;
        itemList.add(item);
        entryMap.put(item.type, item);

        //关注
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SUBSCRIBER;
        item.title = getString(R.string.subscribe);
        item.resId = R.drawable.guanzhu;
        item.hasFooter = true;
        itemList.add(item);
        entryMap.put(item.type, item);

        //关联账号
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_ASSOCIATED_ACCOUNT;
        item.title = getString(R.string.str_associated_account);
        item.resId = R.drawable.icon_associated_account;
        itemList.add(item);
        entryMap.put(item.type, item);

        //科目设置
        if (isRealTeacher()) {
            item = new TabEntityPOJO();
            item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SUBJECT_SETTING;
            item.title = getString(R.string.str_subject_setting);
            item.resId = R.drawable.icon_subject_setting;
            itemList.add(item);
            entryMap.put(item.type, item);
        }

        //设备管理
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_DEVICE_MANAGEMENT;
        item.title = getString(R.string.device_manager);
        item.resId = R.drawable.shebeiguanli;
        itemList.add(item);
        entryMap.put(item.type, item);

        //设置
        item = new TabEntityPOJO();
        item.type = ITabEntityTypeInfo.TAB_ENTITY_TYPE_SETTINGS;
        item.title = getString(R.string.settings);
        item.resId = R.drawable.shezhi;
        itemList.add(item);
        entryMap.put(item.type, item);

        getCurrAdapterViewHelper().setData(itemList);
        bottomGridView.setPadding(0, 0, 0, DensityUtils.dp2px(getActivity(), 10));
    }


    private void showMoreMenu(View view) {

        List<PopupMenu.PopupMenuData> items = new ArrayList();
        PopupMenu.PopupMenuData data = null;

        //投屏
//        data = new PopupMenu.PopupMenuData(0,
//                R.string.sharescreen, MENU_ID_SCREEN_PROJECTION);
//        items.add(data);

        //扫一扫
        data = new PopupMenu.PopupMenuData(0,
                R.string.scan_me, MENU_ID_SCAN);
        items.add(data);

        //分享
        data = new PopupMenu.PopupMenuData(0, R.string.share,
                MENU_ID_SHARE);
        items.add(data);
        if (items.size() <= 0) {
            return;
        }

        AdapterView.OnItemClickListener itemClickListener =
                (parent, view1, position, id) -> {
                    if (view1.getTag() == null) {
                        return;
                    }
                    PopupMenu.PopupMenuData data1 = (PopupMenu.PopupMenuData) view1.getTag();
                    if (data1.getId() == MENU_ID_SCAN) {
                        enterCaptureActivity();
                    } else if (data1.getId() == MENU_ID_SHARE) {
                        sharePersonalSpace();
                    } else if (data1.getId() == MENU_ID_SCREEN_PROJECTION) {
                        enterShareScreen();
                    }
                };
        PopupMenu popupMenu = new PopupMenu(getActivity(), itemClickListener, items);
        popupMenu.showAsDropDown(view, view.getWidth(), 0);
    }

    private void updateAssistanceViewData(){
        if (userInfo != null) {
            if (userInfo.isTeacher() || userInfo.isAssistant()) {
                //助教和老师身份
                openAssistanceLayout.setVisibility(View.GONE);
                assistanceModeLayout.setVisibility(View.VISIBLE);
            } else {
                assistanceModeLayout.setVisibility(View.GONE);
                openAssistanceLayout.setVisibility(View.VISIBLE);
            }
            switchButton.setChecked(SPUtil.getInstance().getBoolean(SharedConstant.KEY_APPLICATION_MODE));
        }
    }

    private void enterCaptureActivity() {

        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }

    private void enterShareScreen() {
        Intent intent = new Intent(getActivity(), ShareScreenActivity.class);
        startActivity(intent);
    }

    protected void sharePersonalSpace() {
        if (userInfo == null) {
            return;
        }
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(userInfo.getNickName());
        if (!TextUtils.isEmpty(userInfo.getRealName())) {
            shareInfo.setTitle(userInfo.getRealName());
        }
        shareInfo.setContent(" ");
        String serverUrl = ServerUrl.SHARE_PERSONAL_SPACE_URL;
        String url = serverUrl + String.format(
                ServerUrl.SHARE_PERSONAL_SPACE_PARAMS, userInfo.getMemberId());
        shareInfo.setTargetUrl(url);
        UMImage umImage = null;
        if (!TextUtils.isEmpty(userInfo.getHeaderPic())) {
            umImage = new UMImage(getActivity(), AppSettings.getFileUrl(userInfo.getHeaderPic()));
        } else {
            umImage = new UMImage(getActivity(), R.drawable.ic_launcher);
        }
        shareInfo.setuMediaObject(umImage);
        SharedResource resource = new SharedResource();
        resource.setId(userInfo.getMemberId());
        resource.setTitle(userInfo.getNickName());
        if (!TextUtils.isEmpty(userInfo.getRealName())) {
            resource.setTitle(userInfo.getRealName());
        }
        resource.setDescription("");
        resource.setShareUrl(serverUrl);
        if (!TextUtils.isEmpty(userInfo.getHeaderPic())) {
            resource.setThumbnailUrl(AppSettings.getFileUrl(userInfo.getHeaderPic()));
        }
        resource.setType(SharedResource.RESOURCE_TYPE_HTML);
        resource.setFieldPatches(SharedResource.FIELD_PATCHES_PERSON_SHARE_URL);
        shareInfo.setSharedResource(resource);
        ShareUtils shareUtils = new ShareUtils(getActivity());
        shareUtils.share(getView(), shareInfo);
    }

    protected void loadUserInfo() {
        userId = getMemeberId();
        if (TextUtils.isEmpty(userId)) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("UserId", userId);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.LOAD_USERINFO_URL,
                params,
                new DefaultListener<UserInfoResult>(UserInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess()
                                || getResult().getModel() == null) {
                            return;
                        }
                        userInfo = getResult().getModel();
                        schoolList = userInfo.getSchoolList();
                        String memberId = userInfo.getUserId();
                        if (TextUtils.isEmpty(memberId)) {
                            userInfo.setMemberId(userId);
                        } else {
                            userInfo.setMemberId(memberId);
                        }
                        getThumbnailManager().displayUserIconWithDefault(AppSettings.getFileUrl(
                                userInfo.getHeaderPic()), userIconView, R.drawable.default_user_icon);
                        String userName = userInfo.getRealName();
                        if (TextUtils.isEmpty(userName)) {
                            userName = userInfo.getNickName();
                        }
                        userNameTxt.setText(userName);
                        getMyApplication().setUserInfo(userInfo);
                        loadEntityData();
                        updateAssistanceViewData();
                    }
                });
        prepareLoadPushMessage();
    }

    private void loadBindData() {
        if (userInfo == null) {
            return;
        }
        isBindWx = false;
        isBindQQ = false;
        Map<String, Object> param = new HashMap<>();
        param.put("MemberId", DemoApplication.getInstance().getMemberId());
        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener<BindThirdPartyResult>(getActivity(), BindThirdPartyResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                super.onSuccess(jsonString);
                BindThirdPartyResult result = getResult();
                if (result != null && result.isSuccess()) {
                    String bindString = result.getModel().toString();
                    if (!TextUtils.isEmpty(bindString)) {
                        try {
                            List<BindThirdParty> bindThirdPartyList = result.getModel().getDataList();
                            if (bindThirdPartyList != null && bindThirdPartyList.size() > 0) {
                                for (int i = 0; i < bindThirdPartyList.size(); i++) {
                                    BindThirdParty data = bindThirdPartyList.get(i);
                                    if (data.getIdentityType() == 1) {
                                        isBindWx = true;
                                        wxBindUnionid = data.getUnionid();
                                    } else if (data.getIdentityType() == 2) {
                                        isBindQQ = true;
                                        qqBindUnionid = data.getUnionid();
                                    }
                                }
                            }
                            changeAssociateText(isBindQQ, isBindWx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl
                .GET_LOAD_THIRDPARTY_ASSOCIATED_ACCOUNT, param, listener);
    }

    private void changeAssociateText(boolean qqAssociate, boolean wxAssociate) {
        int qqResId = qqAssociate ? R.string.str_already_associated : R.string.str_not_associated;
        int wxResId = wxAssociate ? R.string.str_already_associated : R.string.str_not_associated;
        int qqIconId = qqAssociate ? R.drawable.ic_auth_login_qq_enable : R.drawable
                .ic_auth_login_qq_disable;
        int wxIconId = wxAssociate ? R.drawable.ic_auth_login_wx_enable : R.drawable
                .ic_auth_login_wx_disable;
        authLoginQqTxt.setCompoundDrawablesWithIntrinsicBounds(qqIconId, 0, 0, 0);
        authLoginWxTxt.setCompoundDrawablesWithIntrinsicBounds(wxIconId, 0, 0, 0);
        authLoginQqTxt.setCompoundDrawablePadding(Utils.dip2px(getActivity(), 5));
        authLoginWxTxt.setCompoundDrawablePadding(Utils.dip2px(getActivity(), 5));
        authLoginQqTxt.setText(String.format("%s >", getString(qqResId)));
        authLoginWxTxt.setText(String.format("%s >", getString(wxResId)));
    }

    private void handlerBindShareMediaData(SHARE_MEDIA shareMedia) {
        boolean isAppInstall = ShareHelper.isAppInstall(getActivity(), shareMedia);
        if (shareMedia == SHARE_MEDIA.QQ) {
            if (!isBindQQ) {
                if (!isAppInstall) {
                    showUnInstallShareMediaMessage(shareMedia);
                    return;
                }
                //未关联
                applyOrDeleteShareMediaAuthorization(shareMedia, true);
            } else {
                //已关联
//                popUnbindAuthDialog(shareMedia, false);
                AssociateAccountActivity.start(getActivity());
            }
        } else if (shareMedia == SHARE_MEDIA.WEIXIN) {
            if (!isBindWx) {
                if (!isAppInstall) {
                    showUnInstallShareMediaMessage(shareMedia);
                    return;
                }
                //未关联
                applyOrDeleteShareMediaAuthorization(shareMedia, true);
            } else {
                //已关联
//                popUnbindAuthDialog(shareMedia, false);
                AssociateAccountActivity.start(getActivity());
            }
        }
    }

    private void showUnInstallShareMediaMessage(SHARE_MEDIA shareMedia) {
        if (shareMedia == SHARE_MEDIA.QQ) {
            TipMsgHelper.ShowMsg(getActivity(), R.string.install_qq);
        } else if (shareMedia == SHARE_MEDIA.WEIXIN) {
            TipMsgHelper.ShowMsg(getActivity(), R.string.install_wechat);
        }
    }

    private void popUnbindAuthDialog(final SHARE_MEDIA share_media, final boolean isUnbindSuccess) {
        String cancelText = getString(R.string.cancel);
        String confirmText = getString(R.string.str_remove_associated);
        if (isUnbindSuccess) {
            cancelText = "";
            confirmText = getString(R.string.str_i_know);
            if (share_media == SHARE_MEDIA.WEIXIN) {
                isBindWx = false;
            } else if (share_media == SHARE_MEDIA.QQ) {
                isBindQQ = false;
            }
            changeAssociateText(isBindQQ, isBindWx);
        }
        String messageTip = "";
        if (share_media == SHARE_MEDIA.QQ) {
            messageTip = getString(R.string.str_remove_associated_tips, getString(R.string.str_qq));
        } else if (share_media == SHARE_MEDIA.WEIXIN) {
            messageTip = getString(R.string.str_remove_associated_tips, getString(R.string.str_weixin));
        }
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                getActivity(),
                null,
                R.layout.layout_change_unbind_auth_dialog,
                cancelText,
                (dialog, which) -> dialog.dismiss(),
                confirmText,
                (dialog, which) -> {
                    if (!isUnbindSuccess) {
                        applyOrDeleteShareMediaAuthorization(share_media, false);
                    }
                });
        ImageView unBindImage = (ImageView) messageDialog.getContentView().findViewById(R.id.iv_unbind_tip);
        TextView unBindTitle = (TextView) messageDialog.getContentView().findViewById(R.id.tv_unbind_message_title);
        TextView unBindTip = (TextView) messageDialog.getContentView().findViewById(R.id.tv_unbind_tip);
        View seperator = messageDialog.getContentView().findViewById(R.id.contacts_dialog_button_seperator);
        Button mBtnLeft = (Button) messageDialog.getContentView().findViewById(R.id.contacts_dialog_left_button);
        if (mBtnLeft != null) {
            mBtnLeft.setText(cancelText);
            if (TextUtils.isEmpty(cancelText)) {
                mBtnLeft.setVisibility(View.GONE);
                seperator.setVisibility(View.GONE);
            } else {
                mBtnLeft.setVisibility(View.VISIBLE);
                seperator.setVisibility(View.VISIBLE);
            }
        }
        Button button = (Button) messageDialog.getContentView().findViewById(R.id.contacts_dialog_right_button);
        button.setText(confirmText);
        if (isUnbindSuccess) {
            unBindImage.setImageResource(R.drawable.icon_success);
            unBindTitle.setText(R.string.str_remove_associated_success);
        } else {
            unBindImage.setImageResource(R.drawable.icon_remove_tip);
            unBindTitle.setText(R.string.str_confirm_remove_associated);
        }
        unBindTip.setText(messageTip);
        messageDialog.show();
    }

    private void applyOrDeleteShareMediaAuthorization(final SHARE_MEDIA share_media, final boolean isApplyAuth) {
        ThirdPartyLoginHelper helper = new ThirdPartyLoginHelper(getActivity());
        helper.setShareMediaType(share_media)
                .setFunctionType(isApplyAuth ? ThirdPartyLoginHelper.FUNCTION_TYPE.BIND_AUTH :
                        ThirdPartyLoginHelper.FUNCTION_TYPE.DELETE_AUTH)
                .setCallBackListener(result -> {
                    boolean bindSuccess = (boolean) result;
                    if (bindSuccess) {
                        if (isApplyAuth) {
                            TipMsgHelper.ShowMsg(getActivity(), R.string.str_bind_auth_success);
                        } else {
                            popUnbindAuthDialog(share_media, true);
                        }
                        loadBindData();
                    }
                });
        if (!isApplyAuth) {
            if (share_media == SHARE_MEDIA.QQ) {
                helper.setUnionid(qqBindUnionid);
            } else if (share_media == SHARE_MEDIA.WEIXIN) {
                helper.setUnionid(wxBindUnionid);
            }
        }
        helper.start();
    }

    /**
     * 加载营养膳食信息
     */
    private void loadNutritionRecipeData() {
        loadEntityData();
//        Map<String, Object> params = new HashMap();
//        //学校Id，必填
//        params.put("MemberId", getMemeberId());
//
//        RequestHelper.sendPostRequest(getActivity(),
//                ServerUrl.NUTRITION_RECIPES_LOGIN_URL, params,
//                new DefaultPullToRefreshListener<NutritionRecipeObjectResult>(
//                        NutritionRecipeObjectResult.class) {
//                    @Override
//                    public void onSuccess(String jsonString) {
//                        if (getActivity() == null) {
//                            return;
//                        }
//                        super.onSuccess(jsonString);
//                        if (getResult() == null || !getResult().isSuccess()
//                                || getResult().getModel() == null) {
//                            return;
//                        }
//                        updateNutritionRecipeInfo(getResult());
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        //更新图标显示
//                        loadEntityData();
//                        super.onFinish();
//                    }
//                });
    }

    /**
     * 天赋密码
     */
    private void loadTalentData() {
        shouldShowTalentipher = false;
        isHasChild = false;
        isLoadingTalentDataEnd = false;
        isLoadingTalentChildEnd = false;
        if (getUserInfo() == null) {
            return;
        }
        talentList.clear();
        UserInfo userInfo = getUserInfo();

        //先判断自己是否有天赋密码
        List<CheckMarkInfo.ModelBean> model = new ArrayList<>();
        CheckMarkInfo.ModelBean bean = new CheckMarkInfo.ModelBean();
        bean.setChildId(userInfo.getMemberId());
        bean.setRealName(userInfo.getRealName());
        bean.setNickName(userInfo.getNickName());
        model.add(bean);

        loadTalentStudentData(userInfo.getMemberId(), model);

    }

    /**
     * 通过ParentId查询孩子列表
     */
    private void loadChildList(UserInfo userInfo) {

        Map<String, Object> params = new ArrayMap<>();
        params.put("ParentId", userInfo.getMemberId());
        String requestUrl = ServerUrl.GET_LOADCHILDRENBYPARENTID;
        DefaultModelListener listener =
                new DefaultModelListener(ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        CheckMarkInfo info = com.alibaba.fastjson.JSONObject.parseObject(jsonString, CheckMarkInfo.class);
                        if (!info.isHasError() && info.getModel() != null) {
                            List<CheckMarkInfo.ModelBean> model = info.getModel();

                            StringBuilder builder = new StringBuilder();
                            for (int i = 0; i < model.size(); i++) {
                                CheckMarkInfo.ModelBean modelBean = model.get(i);
                                if (i == model.size() - 1) {
                                    builder.append(modelBean.getChildId());
                                } else {
                                    builder.append(modelBean.getChildId()).append(",");
                                }
                            }
                            isHasChild = true;
                            //根据孩子Id 查询孩子是否有天赋密码
                            loadTalentStudentData(builder.toString(), model);

                        }

                    }

                    @Override
                    public void onFinish() {
                        isLoadingTalentChildEnd = true;
                        if (!isHasChild) {
                            isLoadingTalentDataEnd = true;
                            loadTopGridViewEntityData();
                        }
                    }
                };
        listener.setShowLoading(false);
        postRequest(requestUrl, params, listener);

    }

    /**
     * 查看是否有天赋密码
     */
    private void loadTalentStudentData(String memIds, final List<CheckMarkInfo.ModelBean> model) {
        Map<String, Object> params = new ArrayMap<>();

        params.put("memberIds", memIds);

        DefaultResourceListener listener =
                new DefaultResourceListener(ResourceResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(jsonString);
                            JSONArray data = jsonObject.optJSONArray("memberIds");
                            if (data != null && !TextUtils.isEmpty(data.toString())) {
                                List<String> memIds = JSON.parseArray(data.toString(), String.class);
                                if (memIds != null && memIds.size() > 0) {
                                    //显示天赋密码
                                    shouldShowTalentipher = true;

                                    //封装要传递的数据
                                    for (CheckMarkInfo.ModelBean modelBean : model) {
                                        boolean flag = false;

                                        if (talentList != null && talentList.size() > 0) {
                                            for (CheckMarkInfo.ModelBean bean : talentList) {
                                                String childId = bean.getChildId();
                                                if (childId.equalsIgnoreCase(modelBean.getChildId())) {
                                                    flag = true;
                                                    break;
                                                }
                                            }
                                        }

                                        if (flag) {
                                            continue;
                                        }

                                        if (memIds.contains(modelBean.getChildId())) {
                                            if (getMemeberId().equalsIgnoreCase(modelBean.getChildId())) {
                                                //自己
//                                            modelBean.setRealName(getString(R.string.me));
                                                talentList.add(0, modelBean);
                                            } else {
                                                talentList.add(modelBean);
                                            }

                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFinish() {
                        if (getUserInfo() == null) {
                            return;
                        }
                        if (getUserInfo().isParent()) {
                            //判断是否是家长
                            if (!isLoadingTalentChildEnd) {

                                loadChildList(getUserInfo());
                            } else {
                                isLoadingTalentDataEnd = true;
                                loadTopGridViewEntityData();
                            }

                        } else {
                            isLoadingTalentDataEnd = true;
                            loadTopGridViewEntityData();
                        }

                    }
                };
        listener.setShowLoading(false);
        RequestHelper.sendGetRequest(getActivity(), ServerUrl.GET_TESTHAVEREPORT, params, listener);

    }


    /**
     * 更新营养膳食信息
     *
     * @param result
     */
    private void updateNutritionRecipeInfo(NutritionRecipeObjectResult result) {
        shouldShowNutritionRecipe = showNutritionRecipeByResult(result);
    }


    /**
     * 判断是否需要显示营养膳食
     *
     * @param result
     * @return
     */
    private boolean showNutritionRecipeByResult(NutritionRecipeObjectResult result) {
        if (result != null) {
            NutritionRecipeObject obj = result.getModel();
            if (obj != null) {
                //先判断yeyidOfMember是否有值
                int yeyidOfMember = obj.getYeyidOfMember();
                if (yeyidOfMember != 0) {
                    return true;
                }
                //再判断学校列表yeyidOfSchool是否有值
                List<SchoolYkStateInfo> list = obj.getSchoolList();
                if (list != null && list.size() > 0) {
                    //先判断yeidOfSchool是否有值
                    for (SchoolYkStateInfo info : list) {
                        if (info != null) {
                            int yeyidOfSchool = info.getYeyidOfSchool();
                            if (yeyidOfSchool != 0) {
                                //只要找到一个就说明开通了
                                return true;
                            }
                        }
                    }
                    //最后判断是否开通了弋康服务
                    for (SchoolYkStateInfo info : list) {
                        if (info != null) {
                            boolean isOpenYkServices = info.getIsOpenYkServices();
                            if (isOpenYkServices) {
                                //只要找到一个就说明开通了
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    @Override
    public void clearViews() {
        userInfo = null;
        userId = null;
    }

    private void enterPersonalSpace() {
        Bundle args = new Bundle();
        args.putString(PersonalSpaceActivity.EXTRA_USER_ID, userInfo.getMemberId());
        args.putString(PersonalSpaceActivity.EXTRA_USER_REAL_NAME, userInfo.getRealName());
        //表示从个人空间进入的个人详情页
        args.putInt(PersonalSpaceActivity.EXTRA_FROM_WHERE_COMEIN,
                PersonalSpaceBaseFragment.FromWhereEnter.fromPersonSpace);
        Intent intent = new Intent(getActivity(), PersonalSpaceActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.im_user_icon) {
            if (TextUtils.isEmpty(getMemeberId())) {
                return;
            }
            if (MainApplication.isTutorialMode()) {
                TutorialHomePageActivity.show(getActivity(), new TutorialParams(getMemeberId()));
            } else {
                enterPersonalSpace();
            }
        } else {
            super.onClick(v);
        }
    }

    private void enterMyMessageList() {
        Intent intent = new Intent(getActivity(), MyMessageListActivity.class);
        startActivity(intent);
    }

    private void enterMyCollectionList() {
        Intent intent = new Intent(getActivity(), MyCollectionListActivity.class);
        startActivity(intent);
    }

    private void deleteFiles() {
        String iconPath = Utils.ICON_FOLDER + Utils.ICON_NAME;
        if (new File(iconPath).exists()) {
            Utils.deleteFile(iconPath);
        }

        String zoomIconPath = Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME;
        if (new File(zoomIconPath).exists()) {
            Utils.deleteFile(zoomIconPath);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityUtils.REQUEST_CODE_BASIC_USER_INFO:
                if (data != null) {
                    UserInfo basicUserInfo = (UserInfo) data.getSerializableExtra("userInfo");
                    if (basicUserInfo != null) {
                        userInfo.setRealName(basicUserInfo.getRealName());
                        userInfo.setMobile(basicUserInfo.getMobile());
                        userInfo.setEmail(basicUserInfo.getEmail());
                        userInfo.setSex(basicUserInfo.getSex());
                        userInfo.setBirthday(basicUserInfo.getBirthday());
                        userInfo.setPIntroduces(basicUserInfo.getPIntroduces());
                        userInfo.setLocation(basicUserInfo.getLocation());
                        UserInfo info = getMyApplication().getUserInfo();
                        info.setRealName(basicUserInfo.getRealName());
                        info.setMobile(basicUserInfo.getMobile());
                        info.setEmail(basicUserInfo.getEmail());
                        info.setSex(basicUserInfo.getSex());
                        info.setBirthday(basicUserInfo.getBirthday());
                        info.setPIntroduces(basicUserInfo.getPIntroduces());
                        info.setLocation(basicUserInfo.getLocation());
                        getMyApplication().setUserInfo(info);
                    }
                }
                break;
            default:
                break;
        }
    }
}
