package com.galaxyschool.app.wawaschool;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.chat.library.ConversationHelper;
import com.galaxyschool.app.wawaschool.common.CheckInsideWiFiUtil;
import com.galaxyschool.app.wawaschool.common.DataMigrationUtils;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.NoteDao;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;
import com.galaxyschool.app.wawaschool.fragment.BookStoreFragment;
import com.galaxyschool.app.wawaschool.fragment.MyPersonalSpaceFragment;
import com.galaxyschool.app.wawaschool.fragment.MySchoolSpaceFragment;
import com.galaxyschool.app.wawaschool.fragment.account.AccountListener;
import com.galaxyschool.app.wawaschool.fragment.library.MyFragmentPagerAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.jpush.PushUtils;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.views.MyViewPager;
import com.galaxyschool.app.wawaschool.views.ToolbarBottomView;
import com.lecloud.skin.init.InitResultListener;
import com.lecloud.skin.init.LqInit;
import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.common.utils.ActivityUtil;
import com.lqwawa.intleducation.module.discovery.ui.mycourse.TabCourseFragment;
import com.lqwawa.intleducation.module.discovery.ui.myonline.MyOnlinePagerFragment;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyFragment;
import com.lqwawa.intleducation.module.learn.ui.MyCourseListPagerFragment;
import com.lqwawa.intleducation.module.organcourse.OrganCourseClassifyActivity;
import com.lqwawa.libs.appupdater.UpdateService;
import com.lqwawa.libs.mediapaper.MediaPaper;
import com.lqwawa.mooc.common.MOOCHelper;
import com.oosic.apps.iemaker.base.ooshare.MyShareManager;
import com.osastudio.apps.Config;
import com.osastudio.common.utils.PermissionsUtils;
import com.osastudio.common.utils.ValidationUtils;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends BaseCompatActivity
        implements ToolbarBottomView.BottomViewClickListener, AccountListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    public static final String ACTION_ACCOUNT_LOGOUT = "com.galaxyschool.app.wawaschool.ACTION_LOGOUT";
    public static final String ACTION_CHANGE_LQCOURSE_TAB = "action_change_lqCourse_tab";
    private static final int TAB_LQ_COURSE = 0;//lq学程、学程馆
    private static final int TAB_ONLINE_STUDY = 1;//在线学习
    private static final int TAB_MY_COURSE = 2;//我的课程
    private static final int TAB_SCHOOL_SPACE = 3;//校园空间、空中学校
    private static final int TAB_PERSONAL_SPACE = 4;//个人空间
//    private static final int TAB_MORE = 3;

    public static final int MAX_LOCAL_NOTE_COUNT = 10;

    public static final int REQUEST_CODE_EXIT = 100;

    /**
     * 应用程序需要申请的权限集合
     */
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CALL_PHONE
    };

    /**
     * APP申请的所有权限，申请新的权限同步更新
     */
    public static final int[] PERMISSION_GROUP_IDS = new int[]{
            PermissionsUtils.WRITE_CONTACTS,
            PermissionsUtils.CAMERA,
            PermissionsUtils.PHONE,
            PermissionsUtils.LOCATION,
            PermissionsUtils.STORAGE,
            PermissionsUtils.RECORD_AUDIO
    };

    private MyApplication app;
    private Handler handler = new Handler();

    private HomeReceiver homeReceiver = new HomeReceiver();
//	private ConversationHelper conversationHelper;

    private MyViewPager viewPager;
    private MyFragmentPagerAdapter pagerAdapter;
    private List<Fragment> fragments;
    private int curIndex = -1;
    private int nextIndex = -1;

    private ToolbarBottomView bottomBar;
    private MyShareManager mShareManager;
    private ImageView myCourseImage;

    Map<String, Integer> hashMap = new HashMap<String, Integer>();

    static {
        try {
            System.loadLibrary("sdspv3_jni");
        } catch (UnsatisfiedLinkError ule) {
            System.err.println("WARNING: Could not load so");
        }
    }

    private boolean exitAllowed;
    private Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            exitAllowed = false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //request all authority one time
            hashMap.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
            hashMap.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            hashMap.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
            hashMap.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            hashMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            hashMap.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
            // 去除不需要的权限
//            hashMap.put(Manifest.permission.BODY_SENSORS, PackageManager.PERMISSION_GRANTED);
//            hashMap.put(Manifest.permission.READ_CALENDAR, PackageManager.PERMISSION_GRANTED);
//            hashMap.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);

//            PermissionsUtils.requestAllPermissions(HomeActivity.this, PermissionsUtils.mAllGroupPermission);
            PermissionsUtils.requestPermissions(HomeActivity.this, PERMISSIONS);
        }


        if (!Config.UPDATE_FOR_BUGLY) {

            bindUpdateService();
        }

        this.app = (MyApplication) getApplication();
        this.app.setAccountListener(this);

        initViews();

//        initChatModule();

        if (savedInstanceState != null) {
            curIndex = savedInstanceState.getInt("curIndex");
        }
        if (curIndex > -1) {
            setCurrPage(curIndex);
        } else {
            setCurrPage();
        }

        registerReceiver();

        clearLocalDatas();

        mShareManager = MyShareManager.getInstance(HomeActivity.this, handler);
        mShareManager.start();

        if (!Config.DEBUG) {
            ValidationUtils.validateApp(HomeActivity.this, getPackageName(), Config.DEBUG);
        }

        String memberId = ((MyApplication) getApplication()).getMemberId();
        if (!TextUtils.isEmpty(memberId)) {
            DataMigrationUtils.loadDraftData(HomeActivity.this);
            DataMigrationUtils.processLocalCourseData(HomeActivity.this, memberId);
            ((MyApplication) getApplication()).getPrefsManager().setVip(memberId);
            PushUtils.initUserDeviceData(this,memberId);
        }

        //6.0以上杀进程后APP获取到所有权限不会走onRequestPermissionsResult方法，需手动乐视初始化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && PermissionsUtils
                .isAllNeccessaryAuthorityAvailable(HomeActivity.this,
                        PERMISSION_GROUP_IDS)) {
            LqInit.initLetvSdk(this, new InitResultListener() {
                @Override
                public void onInitResult(boolean result) {
                    app.setCdeInitSuccess(result);
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //request all authority one time
                if (requestCode == PermissionsUtils.MY_PERMISSIONS_REQUEST_ALL_PERMISSION) {
                    // Fill with results
                    for (int i = 0; i < permissions.length; i++) {
                        hashMap.put(permissions[i], grantResults[i]);
                    }
                    // Check  condition
                    if (hashMap.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && hashMap.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                            && hashMap.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && hashMap.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && hashMap.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && hashMap.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        ((MyApplication) getApplication()).setMyUUID(HomeActivity.this);
                        if (!Config.UPDATE_FOR_BUGLY) {
                            ((MyApplication) getApplication()).startUpdateService();
                        }
                        ((MyApplication) getApplication()).startDownloadService();
                        LqInit.initLetvSdk(this, new InitResultListener() {
                            @Override
                            public void onInitResult(boolean result) {
                                app.setCdeInitSuccess(result);
                            }
                        });
                    } else {
                        // Permission Denie
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.nextIndex >= 0) {
            if (this.nextIndex == TAB_LQ_COURSE || this.app.hasLogined()) {
                setCurrPage(this.nextIndex);
            }
            this.nextIndex = -1;
        }
        initDistinctLayout();
//		loadConversations();
        mShareManager = MyShareManager.getInstance(this, handler);
        if (mShareManager != null) {
            mShareManager
                    .setOpenPackage("com.oosic.apps.kuke_receiver/com.oosic.apps.iemaker_receiver.ShareBox");
        }
    }

    /**
     * �����˺ű��������µĲ����ظ��������⡣
     */
    private void initDistinctLayout() {
        View view = getWindow().getDecorView().findViewById(R.id.home_root_layout);
        FrameLayout layout = (FrameLayout) view.getParent();
        if (layout != null) {
            int childCount = layout.getChildCount();
            if (childCount > 1) {
                for (int i = 0; i < childCount; i++) {
                    View childView = layout.getChildAt(i);
                    if (childView != view) {
                        layout.removeView(childView);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApplication.destroyThumbnailManager();
        unregisterReceiver();
        if (!Config.UPDATE_FOR_BUGLY) {

            unbindUpdateService();
        }
        if (mShareManager != null) {
            mShareManager.stop();
        }
    }

    @Override
    public void onBackPressed() {
        if (!this.exitAllowed
                && getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            this.exitAllowed = true;
            TipsHelper.showToast(this, R.string.exit_if_press_back_again);
            this.handler.postDelayed(this.exitRunnable, 1000);
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("curIndex", curIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initViews() {
        viewPager = (MyViewPager) findViewById(R.id.home_view_pager);
        bottomBar = (ToolbarBottomView) findViewById(R.id.home_bottom_bar);
        bottomBar.setBottomViewClickListener(this);
        myCourseImage = (ImageView) findViewById(R.id.iv_my_course);
        initFragments();
    }

    private void initFragments() {
        fragments = new ArrayList<Fragment>();
        Bundle args = new Bundle();
        args.putInt("rootLayoutId", R.id.home_root_layout);

        //LQ Course
        //初始化mooc用户信息
        MyApplication app = (MyApplication) this.getApplication();
        MOOCHelper.init(app.getUserInfo());
        com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseFragment lqCourseFragment = new com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseFragment();
        lqCourseFragment.setArguments(args);
        /*LQCourseFragment lqCourseFragment = new LQCourseFragment();
        lqCourseFragment.setArguments(args);*/
        fragments.add(lqCourseFragment);

        //��e����
//        BookStoreFragment bookStoreFragment = new BookStoreFragment();
//        bookStoreFragment.setArguments(args);
//        fragments.add(bookStoreFragment);

        //new eBook store

//        SchoolsBooksFragment schoolsBooksFragment = new SchoolsBooksFragment();
//        schoolsBooksFragment.setArguments(args);
//        fragments.add(schoolsBooksFragment);
        //新版快乐学习
//        HappyLearningFragment happyLearningFragment = new HappyLearningFragment();
//        happyLearningFragment.setArguments(args);
//        fragments.add(happyLearningFragment);

        //在线学习
        OnlineStudyFragment onlineStudyFragment = OnlineStudyFragment.newInstance();
        fragments.add(onlineStudyFragment);

        //我的课程
        TabCourseFragment tabCourseFragment = TabCourseFragment.newInstance();
        fragments.add(tabCourseFragment);

        //空中学校
        MySchoolSpaceFragment mySchoolSpaceFragment = new MySchoolSpaceFragment();
        mySchoolSpaceFragment.setArguments(args);
        fragments.add(mySchoolSpaceFragment);

        //个人空间
        MyPersonalSpaceFragment myPersonalSpaceFragment = new MyPersonalSpaceFragment();
        myPersonalSpaceFragment.setArguments(args);
        fragments.add(myPersonalSpaceFragment);

        //����
//        MoreFragment moreFragment = new MoreFragment();
//        moreFragment.setArguments(args);
//        fragments.add(moreFragment);

        pagerAdapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4);//切换时，除了當前页，还缓存3页
    }

    private void setCurrPage() {
        setCurrPage(TAB_LQ_COURSE);
    }

    private void setCurrPage(int tabIndex) {
        this.curIndex = tabIndex;
        viewPager.setCurrentItem(tabIndex);
        bottomBar.setSelectItemView(curIndex);
        setBottomImageViewDrawable(tabIndex);
    }

    @Override
    public void onBottomViewClick(int index) {
        if ((index != TAB_ONLINE_STUDY) && (index != TAB_LQ_COURSE) && !app.hasLogined()) {
            enterLogin();
            return;
        }
        UIUtils.currentSourceFromType = SourceFromType.OTHER;
        if (curIndex != index) {
            viewPager.setCurrentItem(index);
            curIndex = index;
            bottomBar.setSelectItemView(curIndex);
            setBottomImageViewDrawable(index);
        }
    }

    private void setBottomImageViewDrawable(int index) {
        if (myCourseImage == null) {
            return;
        }
        if (index == TAB_MY_COURSE) {
            myCourseImage.setImageResource(R.drawable.icon_my_course_detail);
        } else {
            myCourseImage.setImageResource(R.drawable.icon_my_course_detail_gray);
        }
    }

    private void clearLocalDatas() {
        clearLocalNotes();
//        clearAllLocalResources();
    }

    private void clearLocalNotes() {
        //only save the lastest 10 notes
        NoteDao noteDao = new NoteDao(HomeActivity.this);
        try {
            List<NoteDTO> noteDTOs = noteDao.getNoteDTOs(MediaPaper.PAPER_TYPE_TIEBA);
            if (noteDTOs != null && noteDTOs.size() > MAX_LOCAL_NOTE_COUNT) {
                for (int i = MAX_LOCAL_NOTE_COUNT, size = noteDTOs.size(); i < size; i++) {
                    NoteDTO noteDTO = noteDTOs.get(i);
                    if (noteDTO != null && noteDTO.getDateTime() > 0) {
                        noteDao.deleteNoteDTOByDateTime(noteDTO.getDateTime(), MediaPaper.PAPER_TYPE_TIEBA);
                        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(noteDTO.getDateTime()));
                        if (noteFile.exists()) {
                            Utils.safeDeleteDirectory(noteFile.getAbsolutePath());
                        }
                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除手机上保存的所有素材
     */
    private void clearAllLocalResources() {
        File file = new File(Utils.IMPORTED_FOLDER);
        if (file != null && file.exists()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0, len = files.length; i < len; i++) {
                    if (files[i] != null) {
                        if (files[i].isFile()) {
                            Utils.deleteFile(files[i].getPath());
                        } else if (files[i].isDirectory()) {
                            Utils.safeDeleteDirectory(files[i].getPath());
                        }
                    }
                }
            }
        }
    }

    private void initChatModule() {
        chatLogin();

//		conversationHelper = new ConversationHelper(this);
//		conversationHelper.registerConversationChangedListener(
//				new ConversationHelper.ConversationChangedListener() {
//					@Override
//					public void onConversationChanged() {
//						loadConversations();
//					}
//				});
    }

    private void chatLogin() {
        String memberId = this.app.getMemberId();
        if (!TextUtils.isEmpty(memberId)) {
            UserInfo userInfo = this.app.getUserInfo();
            if (userInfo == null) {
                return;
            }
            ConversationHelper.login(userInfo.getMemberId(),
                    this.app.getPrefsManager().getUserPassword());
        }
    }

    private void enterLogin() {
        Bundle args = new Bundle();
        args.putBoolean(AccountActivity.EXTRA_HAS_LOGINED, false);
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtras(args);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment currFragment = pagerAdapter.getCurrFragment();
        if (currFragment != null
                && (currFragment instanceof MyPersonalSpaceFragment || currFragment instanceof BookStoreFragment)) {
            currFragment.onActivityResult(requestCode, resultCode, data);
        }
//		CreateSlideHelper.processActivityResule(HomeActivity.this, null,
//				requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EXIT && resultCode == RESULT_OK) {
            HomeActivity.this.finish();
        }
    }

    @Override
    public void onAccountLogin(String userId) {
    }

    @Override
    public void onAccountLogout(String userId) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        setCurrPage();
    }

    public void onUserLogout(Object result) {
        NotificationManager nm =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
        setCurrPage();
    }

    private NewMessageListener newMessageListener = new NewMessageListener() {
        @Override
        public void onNewMessage(int tabIndex, int newCount) {
            bottomBar.showBadgeView(tabIndex, newCount);
        }
    };

    public interface NewMessageListener {
        public void onNewMessage(int tabIndex, int newCount);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(ACTION_ACCOUNT_LOGOUT);
        filter.addAction(ACTION_CHANGE_LQCOURSE_TAB);
        filter.addAction(OrganCourseClassifyActivity.ACTION_MORE_COURSE_ENTER);
        filter.addAction(MyOnlinePagerFragment.ACTION_GO_ONLINE_STUDY);
        filter.addAction(MyCourseListPagerFragment.ACTION_GO_COURSE_SHOP);
        //接收系统网络切换的广播
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(homeReceiver, filter);
    }

    private void unregisterReceiver() {
        unregisterReceiver(homeReceiver);
    }

    private class HomeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_LOCALE_CHANGED.equals(intent.getAction())) {
                HomeActivity.this.getActivityStack().finishAll();
            } else if (ACTION_ACCOUNT_LOGOUT.equals(intent.getAction())) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
//				if (conversationHelper != null) {
//					conversationHelper.clear();
//				}
                setCurrPage();
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                //接收系统网络切换的广播
//                Log.d("TTT","打印是否走了当前的广播通道");
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (wifiNetInfo != null && wifiNetInfo.isConnected() && wifiNetInfo.isAvailable()) {
                    //连接上wifi之后（包括切换无线wifi时都能监听到）
                    String memberId = DemoApplication.getInstance().getMemberId();
                    if (!TextUtils.isEmpty(memberId)) {
                        CheckInsideWiFiUtil.getInstance().
                                setMemberId(memberId).
                                setActivity(HomeActivity.this).
                                checkData();
                    }
                }
            } else if (TextUtils.equals(ACTION_CHANGE_LQCOURSE_TAB, intent.getAction())) {
                setCurrPage(TAB_SCHOOL_SPACE);
                // 结束HomeActivity之外的其它Activity
                ActivityUtil.finishToActivity(HomeActivity.this, false);
            } else if (TextUtils.equals(OrganCourseClassifyActivity.ACTION_MORE_COURSE_ENTER, intent.getAction())) {
                // 去LQ学程
                setCurrPage(TAB_LQ_COURSE);
                // 结束HomeActivity之外的其它Activity
                ActivityUtil.finishToActivity(HomeActivity.this, false);
            } else if (TextUtils.equals(MyOnlinePagerFragment.ACTION_GO_ONLINE_STUDY, intent.getAction())) {
                // 去在线学习
                setCurrPage(TAB_ONLINE_STUDY);
            } else if (TextUtils.equals(MyCourseListPagerFragment.ACTION_GO_COURSE_SHOP, intent.getAction())) {
                // 去学程馆
                setCurrPage(TAB_LQ_COURSE);
            }
        }
    }

//	private void loadConversations() {
//		conversationHelper.loadConversations(conversationListener);
//	}
//
//	private NetResultListener conversationListener = new NetResultListener() {
//		@Override
//		public void onSuccess(Object data) {
//
//		}
//
//		@Override
//		public void onError(String message) {
//
//		}
//
//		@Override
//		public void onFinish() {
//
//		}

    private void bindUpdateService() {
        ((MyApplication) getApplication()).bindUpdateService(this, updateServiceConn);
    }

    private void unbindUpdateService() {
        ((MyApplication) getApplication()).unbindUpdateService(updateServiceConn);
    }

    private UpdateService updateService;
    private ServiceConnection updateServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            updateService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            updateService = ((UpdateService.UpdateBinder) service).getService();
            updateService.checkUpdate();
        }
    };

}
