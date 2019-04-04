package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyDownloadListActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SplitCourseListActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckReplaceIPAddressHelper;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.CommitCourseHelper;
import com.galaxyschool.app.wawaschool.common.CommitHelper;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.NetworkHelper;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.course.CacheCourseImagesTask;
import com.galaxyschool.app.wawaschool.course.CopyCourseTask;
import com.galaxyschool.app.wawaschool.db.DownloadCourseDao;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.DownloadCourseDTO;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.resource.ResourceBaseFragment;
import com.galaxyschool.app.wawaschool.helper.UserInfoHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.CircleImageView;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.ModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfo;
import com.galaxyschool.app.wawaschool.pojo.AuthorizationInfoResult;
import com.galaxyschool.app.wawaschool.pojo.CourseImageListResult;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NocEnterDetailArguments;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.SchoolInfoResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.libs.filedownloader.DownloadService;
import com.lqwawa.libs.filedownloader.FileInfo;
import com.lqwawa.tools.DensityUtils;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.ooshare.MyShareManager;
import com.oosic.apps.iemaker.base.ooshare.SharePlayControler;
import com.oosic.apps.iemaker.base.playback.xml.Loader;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareType;
import com.osastudio.common.library.ActivityStack;
import com.osastudio.common.library.Md5FileNameGenerator;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.galaxyschool.app.wawaschool.fragment.CompletedHomeworkListFragment.ACTION_MARK_SCORE;

public class PictureBooksDetailFragment extends ResourceBaseFragment
        implements CommitHelper.NoteCommitListener {

    //    private TaskMarkParam mTaskMarkParam;
    private PopupMenu popupMenu;
    private CustomPopWindow mPopWindow;

    public interface Constants {
        int FROM_MY_WORK = 1;//來自我的作品
        int FROM_MY_BOOK_SHELF = 2;//來自我的书架
        int FROM_CLOUD_SPACE = 3;//來自云空间
        int FROM_OTHRE = 4;//其它（我的收藏、班级作品、绘本屋、发现）
        int FROM_MY_DOWNLOAD = 5;
        //来自读写单
        int FROM_TASK_ORDER = 6;

        String FROM_SOURCE_TYPE = "FROM_SOURCE_TYPE";
        String NEW_RESOURCE_INFO = "NewResourceInfo";
        String EXTRA_IS_FROM_CATALOG = "IsFromCataLog";
        //下面两个字段用于资源开通
        String EXTRA_SCHOOL_ID = "schoolId"; //用户所在学校ID
        String EXTRA_FEE_SCHOOL_ID = "feeSchoolId"; //购买课程所在学校ID
        //课件来自哪个模块
        String EXTRA_COURSE_MODE_FROM = "course_mode_from";
        String EXTRA_COURSE_ID = "courseId";
        String EXTRA_COURSE_RESTYPE = "resType";
        int OPERATION_TYPE_SHARESCEEN = 0;
        int OPERATION_TYPE_MAKEPICBOOK = 1;
        int OPERATION_TYPE_EDITCOURSE = 2;
        int OPERATION_TYPE_PLAYCOURSE = 3;
        int OPERATION_TYPE_GET_THUMBNAIL = 4;
        int MAX_BOOKS_PER_ROW = 3;

        String ACTION_TASKMARKPARAM = "TaskMarkParam";//打分批阅
    }

    public interface CourseModelFrom {
        int fromNocModel = 100;
    }

    public static final String TAG = PictureBooksDetailFragment.class.getSimpleName();
    private int fromType = Constants.FROM_OTHRE;

    private ImageView picBookImageView;
    private NewResourceInfo newResourceInfo;
    private TextView authorTextView;
    private TextView sourceTextView;
    private TextView readContTextView;
    private TextView headTitletextView;
    private TextView introductionTextView;
    private ImageView introArrowImageView;

    private LocalCourseInfo localCourseInfo;
    protected CommitCourseHelper commitCourseHelper;
    private int shareType;
    private UploadParameter uploadParameter;
    private MyShareManager shareManager;
    private SharePlayControler sharePlayControler;
    private TextView commentCountTextview;
    private TextView authorizationInfoView;
    private TextView openConsultionView;
    private View authorizationInfoLayout;
    private CircleImageView authorIconImageView;
    private boolean isSplitCourse;
    private boolean isFromCatalog;
    private String schoolId; //用户所在学校Id
    private String feeSchoolId; //购买的课程所在学校Id
    private boolean isPlayCourse = false; //课程未授权或用户未加入学校均不允许播放
    private AuthorizationInfo authorizationInfo; //保存授权信息
    private SchoolInfo schoolInfo; //我所在学校信息
    private int screenType = 0;
    private boolean isIntroduction;
    private static boolean hasCommented;
    private String mCourseParentId;
    private String courseId;
    private int resType = 0;
    private NocEnterDetailArguments nocArgs;
    private LinearLayout nocNumTimeView;
    //noc大赛区分的作者
    private String mNocAuthorName;
    private int mCourseModelFrom = -1;
    //区分个人资源库中的课件作者是不是自己的
    private boolean isMySelf = false;
    //是否显示下载的按钮
    private boolean isHideDownLoadBtn = true;
    //原来的头布局
    private View topLayout;
    private StudyTaskInfo info;
    private UserInfo userInfo;
    private UserInfo stuUserInfo;
    private int taskType;

    private boolean isOnePage;

    //根据boolean来确定来是否需要加密
    private boolean isPublicResource = true;

    //判断是否收藏的
    private boolean isCollection;
    //收藏的暂时保存的schoolId
    private String collectionSchoolId;
    //是否来自精品资源库
    private boolean isFromChoiceLib;
    private PassParamhelper mParam;
    private boolean isHideCollectBtn = true;
    private boolean isVipSchool = false;
    private PictureBooksDetailItemFragment mCommentFragment;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BaseUtils.MSG_SHAREPLAY_STATUS:
                    int status = msg.arg1;
                    shareplayControlSyncWithShareplayStatus(status);
                    break;
                default:
                    break;
            }
        }
    };

    private DownloadService downloadService;
    private ServiceConnection downloadServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.DownloadBinder) service).getService();
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getMyApplication().bindDownloadService(getActivity(), downloadServiceConn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_picture_books_detail, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
        loadUserInfoData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fromType == Constants.FROM_MY_WORK || fromType == Constants.FROM_MY_DOWNLOAD) {
            updatePicDetailView();
        } else {
            if (!isSplitCourse) {
                loadPictureBookDetails();

            } else {
                loadSplitCourseDetails();
            }
        }
//        if(fromType!=Constants.FROM_MY_BOOK_SHELF&&isFromCatalog){
//            checkAuthorization();
//        }else {
//            isPlayCourse=true;
//
//        }

        if (VipConfig.isVip(getActivity()) || isVipSchool) {
            isPlayCourse = true;
        } else {
            checkAuthorization();
        }

//        registResultBroadcast();
    }

    private void initButtons() {

        View doCourseView = findViewById(R.id.rl_do_course);
        View addReadingView = findViewById(R.id.rl_add_reading);
        if (doCourseView != null) {
            doCourseView.setOnClickListener(this);
        }

        if (addReadingView != null) {
            addReadingView.setOnClickListener(this);
        }

        //我要做课件 和  我要加点读
        if (newResourceInfo != null) {
            if (fromType == Constants.FROM_CLOUD_SPACE
                    || fromType == Constants.FROM_OTHRE
                    || fromType == Constants.FROM_MY_BOOK_SHELF
                    || fromType == Constants.FROM_MY_DOWNLOAD) {
                if(isFromChoiceLib && !VipConfig.isVip(getActivity())){
                    setVisibility(doCourseView, View.GONE);
                    setVisibility(addReadingView, View.GONE);
                } else {
                    setVisibility(doCourseView, View.VISIBLE);
                    setVisibility(addReadingView, View.VISIBLE);
                }
            } else {
                setVisibility(doCourseView, View.GONE);
                setVisibility(addReadingView, View.GONE);
            }
        }
    }

    private void setVisibility(View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }


    public class BtnEntity {
        private String name;
        private int type;
        public static final int TYPE_PLAY_COURSE = 1;
        public static final int TYPE_SHARE_SCREEN = 2;
        public static final int TYPE_SEND_COURSE = 3;
        public static final int TYPE_MAKE_PIC_BOOK = 4;
        public static final int TYPE_SHARE_COURSE = 5;
        public static final int TYPE_EDIT_COURSE = 6;
        public static final int TYPE_DOWNLOAD_COURSE = 7;
        public static final int TYPE_SPLIT_COURSE = 8;
        public static final int TYPE_MAKE_PIC_BOOK_REMOTE = 9;
        //查看二维码
        public static final int TYPE_VIEW_QRCODE = 10;
        //收藏
        public static final int TYPE_COLLECT = 11;
        //返回主页
        public static final int TYPE_BACK_HOME = 12;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    /**
     * 收藏
     */
    private void doCollect() {
        if (newResourceInfo == null) {
            return;
        }
        CollectionHelper collectionHelper = new CollectionHelper(getActivity());
        collectionHelper.setIsPublicRes(newResourceInfo.isPublicResource());
        collectionHelper.setCollectSchoolId(collectionSchoolId);
        collectionHelper.setFromChoiceLib(isFromChoiceLib);
        collectionHelper.collectDifferentResource(
                newResourceInfo.getMicroId() + "-" + newResourceInfo.getResourceType(),
                newResourceInfo.getTitle(),
                newResourceInfo.getAuthorId(),
                getString(R.string.microcourse)
        );
    }

    private void makeCourse() {
        if (!islogin()) {
            return;
        }
        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree != 0) {
            return;
        }
        final HashMap<String, Object> params = new HashMap<String, Object>();
        if (newResourceInfo != null) {
            String resId = newResourceInfo.getResourceId();
            if (!TextUtils.isEmpty(resId)) {
//                if (resId.contains("-")) {
//                    resId = resId.substring(0, resId.lastIndexOf("-"));
//                }
                //传参microId-resourceType
                params.put("courseId", resId);
            }
        }
        RequestHelper.RequestResourceResultListener listener = new RequestHelper
                .RequestResourceResultListener(getActivity(), CourseImageListResult.class) {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                super.onSuccess(jsonString);
                CourseImageListResult result = (CourseImageListResult) getResult();
                if (result == null || result.getCode() != 0) {
                    TipsHelper.showToast(getActivity(), R.string.no_course_images);
                    return;
                }
                String savePath = null;
                List<CourseData> courseDatas = result.getCourse();
                if (courseDatas != null && courseDatas.size() > 0) {
                    CourseData courseData = courseDatas.get(0);
                    if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                        screenType = courseData.screentype;
                        savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator().generate
                                (courseData.resourceurl);
                        List<String> paths = result.getData();
                        if (paths == null || paths.size() == 0) {
                            String originVoicePath = null;
                            if (info != null) {
                                originVoicePath = info.getResUrl();
                                if (!TextUtils.isEmpty(originVoicePath)
                                        && originVoicePath.contains(".zip")) {
                                    //截取字符串
                                    originVoicePath = originVoicePath.substring(0,
                                            originVoicePath.indexOf(".zip"));
                                }
                            }

                            createNewRetellCourseSlidePage(screenType);

                        } else {
//                            downloadCourseImages(savePath, result.getData(), courseData.nickname);
                            checkCanReplaceIPAddress(savePath, result.getData(), courseData);
                        }
                    }
                }
            }
        };
        listener.setShowLoading(false);
        RequestHelper.sendGetRequest(getActivity(), ServerUrl.COURSE_IMAGES_URL, params, listener);
    }

    /**
     * 校验是否用内网的IP进行下载
     */
    private void checkCanReplaceIPAddress(final String savePath, final List<String> paths, final CourseData courseData) {
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper(getActivity());
        helper.setResId(courseData.id)
                .setResType(courseData.type)
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        List<String> imageUrl = paths;
                        if (flag) {
                            imageUrl = helper.getChangeIPUrlArray(paths);
                        }
                        downloadCourseImages(savePath, imageUrl, courseData.nickname);
                    }
                })
                .checkIP();
    }

    private void downloadCourseImages(String savePath, final List<String> paths, String title) {
        if (paths == null || paths.size() == 0) {
            return;
        }
        //空判断
        if (TextUtils.isEmpty(title)) {
            if (newResourceInfo != null) {
                title = newResourceInfo.getTitle();
            }
        }
        CacheCourseImagesTask task = new CacheCourseImagesTask(getActivity(), paths, savePath,
                title);
        final String resourceTitle = title;
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    List<String> paths = (List<String>) result;
                    importLocalPicResourcesCheck(paths, resourceTitle);
                }
            }
        });
        task.execute();
    }

    private void loadPictureBookDetails() {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        String resId;
        if (TextUtils.isEmpty(courseId)) {
            resId = newResourceInfo.getMicroId();
        } else {
            resId = courseId;
        }
        wawaCourseUtils.loadCourseDetail(resId);
//        wawaCourseUtils.loadCourseDetail(newResourceInfo.getMicroId());
        wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
            @Override
            public void onCourseDetailFinish(CourseData courseData) {
                if (getActivity() == null) {
                    return;
                }
                if (courseData != null) {
                    String tempThumbnail = null;
                    //如果courseId不为空表示来自任务单的关联课件
                    if (TextUtils.isEmpty(courseId)) {
                        tempThumbnail = newResourceInfo.getThumbnail();
                    } else {
                        tempThumbnail = courseData.imgurl;
                    }
                    //设置缩略图
                    if (TextUtils.isEmpty(tempThumbnail)) {
                        tempThumbnail = courseData.imgurl;
                    }
                    newResourceInfo = courseData.getNewResourceInfo();
                    if (TextUtils.isEmpty(newResourceInfo.getThumbnail()) && !TextUtils.isEmpty
                            (tempThumbnail)) {
                        newResourceInfo.setThumbnail(tempThumbnail);
                    }

                    //如果来noc大赛 作者保持不变
                    if (nocArgs != null) {
                        newResourceInfo.setAuthorName(nocArgs.getAuthor());
                    } else if (!TextUtils.isEmpty(mNocAuthorName) && mCourseModelFrom ==
                            CourseModelFrom.fromNocModel) {
                        newResourceInfo.setAuthorName(mNocAuthorName);
                    }
                    updatePicDetailView();
                    //如果是任务的关联课件
//                    if (!TextUtils.isEmpty(courseId)) {
//                        initButtons();
//                    }
                }
            }
        });
    }

    private void loadSplitCourseDetails() {
        long resId = 0;
        if (newResourceInfo != null) {
            String microId = newResourceInfo.getMicroId();
            if (!TextUtils.isEmpty(microId)) {
                if (TextUtils.isDigitsOnly(microId)) {
                    //纯数字
                    resId = Long.parseLong(microId);
                } else if (microId.contains("-")) {
                    //带"-"的
                    microId = microId.substring(0, microId.indexOf("-"));
                    if (!TextUtils.isEmpty(microId)) {
                        resId = Long.parseLong(microId);
                    }
                }
            }
        } else {
            if (!TextUtils.isEmpty(courseId)) {
                resId = Long.parseLong(courseId);
            }
        }
        if (resId <= 0) {
            return;
        }
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        wawaCourseUtils.loadSplitCourseDetail(resId);
        wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {

            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (getActivity() == null) {
                    return;
                }
                if (info != null) {
                    newResourceInfo = info.getNewResourceInfo();
                    if (newResourceInfo.getFileSize() <= 0) {
                        newResourceInfo.setFileSize(info.getFileSize());
                    }
                    if (TextUtils.isEmpty(newResourceInfo.getShareAddress())) {
                        newResourceInfo.setShareAddress(info.getShareUrl());
                    }

                    //如果来noc大赛 作者保持不变
                    if (nocArgs != null) {
                        newResourceInfo.setAuthorName(nocArgs.getAuthor());
                    } else if (!TextUtils.isEmpty(mNocAuthorName) && mCourseModelFrom ==
                            CourseModelFrom.fromNocModel) {
                        newResourceInfo.setAuthorName(mNocAuthorName);
                    }
                    updatePicDetailView();

                    //如果是任务的关联课件
//                    if (!TextUtils.isEmpty(courseId)) {
//                        initButtons();
//                    }
                }
            }
        });
    }

    private void loadUserInfoData(){
        UserInfoHelper userInfoHelper = new UserInfoHelper(getActivity());
        userInfoHelper.setCallBackListener((obj) ->{
            if (obj != null && obj instanceof UserInfo){
                UserInfo info = (UserInfo) obj;
                if (commitCourseHelper != null) {
                    commitCourseHelper.setIsTeacher(Utils.isRealTeacher(info.getSchoolList()));
                }
            }
        });
        userInfoHelper.check();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadService != null) {
            getMyApplication().unbindDownloadService(downloadServiceConn);
        }
//        unRegistResultBroadcast();
    }

    private void loadIntentData() {
        Intent intent = getActivity().getIntent();
        courseId = intent.getStringExtra(Constants.EXTRA_COURSE_ID);
        resType = intent.getIntExtra(Constants.EXTRA_COURSE_RESTYPE, 0);
        isIntroduction = intent.getBooleanExtra(ActivityUtils.EXTRA_TASK_TYPE, false);
        fromType = intent.getIntExtra(Constants.FROM_SOURCE_TYPE, Constants.FROM_OTHRE);
        newResourceInfo = intent.getParcelableExtra(Constants.NEW_RESOURCE_INFO);

        if (newResourceInfo != null) {
            //获取 并且临时保存传递过来值的状态
            isPublicResource = newResourceInfo.isPublicResource();
            isVipSchool = newResourceInfo.isVipSchool();
            if (!isPublicResource) {
                mCourseParentId = newResourceInfo.getParentId();
            }

            if (newResourceInfo.getType() == 1) {
                //这里备份收藏的数据
                isCollection = true;
                collectionSchoolId = newResourceInfo.getCollectionOrigin();
            }

            //针对添加收藏时用到字段
            isFromChoiceLib = newResourceInfo.isQualityCourse();
            collectionSchoolId = newResourceInfo.getCollectionOrigin();
            //是否隐藏收藏按钮
            isHideCollectBtn = newResourceInfo.isHideCollectBtn();
            if (isFromChoiceLib && !VipConfig.isVip(getActivity())){
                isHideCollectBtn = true;
            }
        }

        mCourseModelFrom = intent.getIntExtra(Constants.EXTRA_COURSE_MODE_FROM, -1);
        if (mCourseModelFrom == CourseModelFrom.fromNocModel) {
            //来自noc大赛
            if (newResourceInfo != null) {
                mNocAuthorName = newResourceInfo.getAuthorName();
            }
        }
        mParam = (PassParamhelper) intent.getSerializableExtra(PassParamhelper.class.getSimpleName());
        //获取传过来的bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (isHideCollectBtn) {
                isHideCollectBtn = bundle.getBoolean(ActivityUtils.EXTRA_IS_NEED_HIDE_COLLECT_BTN, true);
            }
            if (isHideCollectBtn && mParam == null) {
                mParam = new PassParamhelper();
                mParam.isFromLQMOOC = true;
            } else {
                if (mParam == null) {
                    mParam = new PassParamhelper();
                } else if (mParam.isFromLQMOOC && mParam.isAudition){
                    isFromChoiceLib = true;
                    if (!VipConfig.isVip(getActivity())){
                        isHideCollectBtn = true;
                    }
                }
            }
        }
        getStudyTaskInfo();
        localCourseInfo = (LocalCourseInfo) intent.getSerializableExtra(LocalCourseInfo.class.getSimpleName());
        isFromCatalog = intent.getBooleanExtra(Constants.EXTRA_IS_FROM_CATALOG, false);
        schoolId = intent.getStringExtra(Constants.EXTRA_SCHOOL_ID);
        feeSchoolId = intent.getStringExtra(Constants.EXTRA_FEE_SCHOOL_ID);
        commitCourseHelper = new CommitCourseHelper(getActivity());
        commitCourseHelper.setIsLocal(fromType == Constants.FROM_MY_WORK ? true : false);

        UserInfo userInfo = getUserInfo();
        if (userInfo != null && userInfo.isStudent()) {
            commitCourseHelper.setIsStudent(true);
        } else {
            commitCourseHelper.setIsStudent(false);
        }
        commitCourseHelper.setNoteCommitListener(PictureBooksDetailFragment.this);
        isSplitCourse = false;
        if (newResourceInfo != null) {
            resType = newResourceInfo.getResourceType();
        }
        if (resType > ResType.RES_TYPE_BASE) {
            isSplitCourse = true;
        }
        nocArgs = (NocEnterDetailArguments) intent.getSerializableExtra(NocEnterDetailArguments
                .class.getSimpleName());

        //用来去区分我的下载是否我要做课件
        if (newResourceInfo != null && fromType == Constants.FROM_MY_DOWNLOAD) {
            String resId = newResourceInfo.getResourceId();
            int resType = Integer.valueOf(resId.split("-")[1]);
            if (resType == ResType.RES_TYPE_ONEPAGE) {
                isOnePage = true;
            }
        }
        analysisActionButtonCondition();
    }

    /**
     * 判断actionButton显示的条件
     */
    private void analysisActionButtonCondition() {
        if (newResourceInfo == null) {
            return;
        }
        if (VipConfig.isVip(getActivity())) {
            isHideDownLoadBtn = false;
            return;
        }
        if (fromType == Constants.FROM_CLOUD_SPACE) {
            //个人资源库
            if (TextUtils.equals(getMemeberId(), newResourceInfo.getAuthorId())) {
                isHideDownLoadBtn = false;
            }
        } else if (!newResourceInfo.isHideDownLoadBtn()) {
            //校本资源库
            isHideDownLoadBtn = false;
        }
    }


    private void getStudyTaskInfo() {
        if (newResourceInfo != null) {
            info = newResourceInfo.getStudyTaskInfo();
            if (info != null) {
                //获得任务类型
                taskType = info.getTaskType();
                stuUserInfo = info.getUserInfo();
                if (info.getIsFromStudyTask()) {
                    collectionSchoolId = info.getCollectSchoolId();
                }
            }
        }
    }

    private void initSomeViews() {
        //原先的头布局
        topLayout = findViewById(R.id.pic_book_top_layout);
        if (topLayout != null) {
            topLayout.setVisibility(View.GONE);
        }
        headTitletextView = (TextView) findViewById(R.id.tv_course_title);
        ImageView imageView = ((ImageView) findViewById(R.id.contacts_header_left_btn));
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.my_back_0);
        ImageView playBtnImageView = (ImageView) findViewById(R.id.iv_play_course);
        if (playBtnImageView != null){
            playBtnImageView.setOnClickListener(this);
        }
        picBookImageView = (ImageView) findViewById(R.id.pic_book_imageview);
        authorTextView = (TextView) findViewById(R.id.pic_book_author_textview);
        sourceTextView = (TextView) findViewById(R.id.pic_book_source_textview);
        readContTextView = (TextView) findViewById(R.id.pic_book_read_count_textview);
        authorizationInfoView = (TextView) findViewById(R.id.authorization_info_view);
        authorizationInfoLayout = findViewById(R.id.authorization_info_layout);
        openConsultionView = (TextView) findViewById(R.id.open_consultion_view);
        openConsultionView.setOnClickListener(this);
        // //设置布局为A4比例
        FrameLayout thumbnailLayout = (FrameLayout) findViewById(R.id.rl_pic_imageview);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) thumbnailLayout
                .getLayoutParams();
        int screenWidth = com.osastudio.common.utils.Utils.getScreenWidth(getActivity());
//        int width = (screenWidth * 2 / 5) - 20;
//        layoutParams.width = width;
//        layoutParams.height = width * 210 / 297;
        layoutParams.width = screenWidth;
        layoutParams.height = screenWidth * 9 / 16;
        thumbnailLayout.setLayoutParams(layoutParams);
//        View rightView = findViewById(R.id.layout_right_content);
//        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) rightView.getLayoutParams();
//        layoutParams1.height = width * 210 / 297;
//        layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        rightView.setLayoutParams(layoutParams1);

//        picBookImageView.setOnClickListener(this);
        commentCountTextview = ((TextView) findViewById(R.id.comment_count_textview));
        if (isSplitCourse) {
            sourceTextView.setVisibility(View.GONE);
            readContTextView.setVisibility(View.GONE);
            commentCountTextview.setVisibility(View.GONE);
            if (newResourceInfo != null) {
                updateTitle();
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(newResourceInfo.getThumbnail()), picBookImageView,
                        R.drawable.default_book_cover);
                authorTextView.setText(newResourceInfo.getAuthorName());
            }
        } else {
            if (fromType == Constants.FROM_MY_WORK || fromType == Constants.FROM_MY_DOWNLOAD) {
                sourceTextView.setVisibility(View.GONE);
                readContTextView.setVisibility(View.GONE);
                commentCountTextview.setVisibility(View.GONE);
            } else {
                commentCountTextview.setVisibility(View.VISIBLE);

            }
        }

        nocNumTimeView = (LinearLayout) findViewById(R.id.noc_num_time_view);
        TextView nocNumView = (TextView) findViewById(R.id.noc_num_textview);
        if (nocArgs != null) {
            nocNumTimeView.setVisibility(View.VISIBLE);
            nocNumView.setText(getString(R.string.noc_num, nocArgs.getEntryNum()));
            if (nocArgs.getNocNameForType() == NocEnterDetailArguments.JOIN_NAME_FOR_SCHOOL
                    && !TextUtils.isEmpty(nocArgs.getOrgName())) {
                sourceTextView.setVisibility(View.VISIBLE);
                sourceTextView.setText(getString(R.string.n_source, nocArgs.getOrgName()));
            } else {
                sourceTextView.setVisibility(View.GONE);
            }
        } else {
            nocNumTimeView.setVisibility(View.GONE);
            //判断SchoolName和ClassName其中一个是否为空，来决定是否显示来源。这里主要为创意秀显示学校班级
            if (!TextUtils.isEmpty(courseId)) {
                sourceTextView.setVisibility(View.GONE);
                return;
            }
        }
        if (newResourceInfo != null) {
            if (TextUtils.isEmpty(newResourceInfo.getSchoolName()) && TextUtils.isEmpty
                    (newResourceInfo.getClassName())) {
                sourceTextView.setVisibility(View.GONE);
            } else {
                sourceTextView.setVisibility(View.VISIBLE);
                sourceTextView.setText(getString(R.string.n_source, newResourceInfo.getSchoolName()
                        + newResourceInfo.getClassName()));
            }
        }
        //作者的头像
        authorIconImageView = (CircleImageView) findViewById(R.id.iv_user_icon);
    }

    /**
     * 更新标题
     */
    private void updateTitle() {
        if (newResourceInfo != null) {
            headTitletextView.setText(newResourceInfo.getTitle());
        }
    }

    private void updatePicDetailView() {
        if (newResourceInfo != null) {
            loadAuthorUserInfo();
            initButtons();
            initRightButton();
            showIntroDescriptionData();
            newResourceInfo.setCollectionOrigin(collectionSchoolId);
            newResourceInfo.setIsQualityCourse(isFromChoiceLib);
            updateTitle();
            //检测资源保护状态
            checkResourceProtectionStatus();
            if (fromType == Constants.FROM_MY_WORK) {
                getThumbnailManager().displayImageWithDefault(
                        newResourceInfo.getThumbnail(), picBookImageView,
                        R.drawable.default_book_cover);
            } else if (fromType == Constants.FROM_MY_DOWNLOAD) {
                if (newResourceInfo != null) {
                    String localPath = getLocalCoursePath(newResourceInfo);
                    if (!TextUtils.isEmpty(localPath)) {
                        String courseRootPath = Utils.getCourseRootPath(localPath);
                        if (TextUtils.isEmpty(courseRootPath)) {
                            Utils.createLocalDiskPath(localPath);
                            unzip(localPath, newResourceInfo.getLocalZipPath(), newResourceInfo
                                    .getTitle(), newResourceInfo.getResourceId());
                        } else {
                            File headFile = new File(courseRootPath, "head.jpg");
                            if (headFile != null && headFile.canRead()) {
                                newResourceInfo.setThumbnail(headFile.getAbsolutePath());
                                getThumbnailManager().displayImageWithDefault(
                                        newResourceInfo.getThumbnail(), picBookImageView,
                                        R.drawable.default_book_cover);
                            }
                        }
                    }
                }
            } else {
                getThumbnailManager().displayImageWithDefault(
                        AppSettings.getFileUrl(newResourceInfo.getThumbnail()), picBookImageView,
                        R.drawable.default_book_cover);
            }
            authorTextView.setText(new StringBuilder().append(newResourceInfo.getAuthorName()));
            //设置评论的人数
            setCommentNum(newResourceInfo.getCommentNumber());
        }
    }

    private void updateReadNum() {
        String commentCount = getString(R.string.comment_person,
                String.valueOf(newResourceInfo.getCommentNumber()));
        if (newResourceInfo.isMicroCourse()) {
            getPicTextView().setText(new StringBuilder().append(newResourceInfo.getReadNumber())
                    .append(getString(R.string.read_person))
                    .append('·').append(commentCount)
                    .append('·').append(newResourceInfo.getPointNumber() + getString(R.string.support_person)));
        } else {
            getPicTextView().setText(new StringBuilder().append(newResourceInfo.getReadNumber())
                    .append(getString(R.string.read_person))
                    .append('·').append(commentCount));
        }
    }

    public void setCommentNum(int commentNum) {
        if (isSplitCourse){
            return;
        }
        newResourceInfo.setCommentNumber(commentNum);
        if (mCommentFragment != null && mCommentFragment.getCurrentCommentNum() < 5){
            mCommentFragment.loadComments();
        }
        updateReadNum();
    }

    /**
     * 检查资源保护状态
     */
    private void checkResourceProtectionStatus() {
        if (newResourceInfo != null) {
            //来自我的书架
            boolean fromMyBookshelf = (fromType == Constants.FROM_MY_BOOK_SHELF);
            //来自校本资源库
            boolean fromSchoolResource = isFromCatalog;
            //来自学习任务
            boolean fromStudyTask = fromStudyTask();
            //是否需要保护
            boolean needProtection = fromMyBookshelf || fromSchoolResource || fromStudyTask ||
                    !isPublicResource;
            if (needProtection) {
                if (isSplitCourse) {
                    newResourceInfo.setParentId(mCourseParentId);
                }
                newResourceInfo.setIsPublicResource(isVipSchool);
            }
        }
    }

    private void initViews() {
        initBottomData();
        initSomeViews();
        initButtons();
        handleHeadViewVisible();
//        setImageLayout();
    }


    private void checkAuthorization() {
        if (TextUtils.isEmpty(schoolId) && TextUtils.isEmpty(feeSchoolId)) {
            isPlayCourse = true;
            return;
        }
        if (!TextUtils.isEmpty(schoolId) && !TextUtils.isEmpty(feeSchoolId)) {
            if (schoolId.equalsIgnoreCase(feeSchoolId)) {
                loadSchool(schoolId, false);
            } else {
                loadSchool(schoolId, true);
            }
        } else if (!TextUtils.isEmpty(schoolId)) {
            loadSchool(schoolId, false);
        }
    }

    private boolean checkAuthorizationCondition(final String schoolId, final String feeSchoolId) {
        if (getUserInfo() == null) {
            return false;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        RequestHelper.RequestDataResultListener<AuthorizationInfoResult> listener =
                new RequestHelper.RequestDataResultListener<AuthorizationInfoResult>
                        (getActivity(), AuthorizationInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        AuthorizationInfoResult result = (AuthorizationInfoResult) getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            authorizationInfoLayout.setVisibility(View.VISIBLE);
                            authorizationInfoView.setText(R.string.course_is_not_authorize);
                            openConsultionView.setVisibility(View.VISIBLE);
                            return;
                        }
                        authorizationInfo = result.getModel().getData();
                        if (authorizationInfo != null) {
                            if (authorizationInfo.isIsMemberAuthorized()) {
                                authorizationInfoLayout.setVisibility(View.GONE);
                                isPlayCourse = true;
                            } else {
                                if (authorizationInfo.isIsCanAuthorize()) {
                                    authorizationInfoLayout.setVisibility(View.GONE);
                                    isPlayCourse = true;
                                } else {
                                    authorizationInfoLayout.setVisibility(View.VISIBLE);
                                    openConsultionView.setVisibility(View.VISIBLE);
                                    authorizationInfoView.setText(R.string.course_is_not_authorize);
                                }
                            }
                        }
                    }
                };
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.CHECK_AUTHONRIZE_CONDITION_URL,
                params, listener);
        return false;
    }

    private void authorizeToMembers(String schoolId, String feeSchoolId) {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("CourseId", feeSchoolId);
        if (schoolInfo != null) {
            params.put("RoleTypes", schoolInfo.getRoles());
        }
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.AUTHONRIZE_TO_MEMBER_URL,
                params, new RequestHelper.RequestModelResultListener<ModelResult>
                        (getActivity(), ModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        ModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            return;
                        }
                        if (result.isSuccess()) {
                            if (authorizationInfo != null) {
                                authorizationInfo.setIsMemberAuthorized(true);
                            }
                        }
                    }
                });
    }

    /**
     * 获取学校详情
     *
     * @param schoolId
     * @param isCheckAuthorization 该值为true执行授权检查
     */
    private void loadSchool(final String schoolId, final boolean isCheckAuthorization) {
        if (getUserInfo() == null) {
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("MemberId", getUserInfo().getMemberId());
        params.put("SchoolId", schoolId);
        params.put("VersionCode", 1);
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.SUBSCRIBE_SCHOOL_INFO_URL,
                params,
                new DefaultListener<SchoolInfoResult>(SchoolInfoResult.class) {
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
                        if (!isCheckAuthorization) {
                            isSchoolJoin();
                        } else {
                            checkAuthorizationCondition(schoolId, feeSchoolId);
                        }
                    }
                });
    }

    private void isSchoolJoin() {
        if (schoolInfo != null) {
            //如果来自精品资源库不需要检查授权
            if (schoolInfo.hasJoinedSchool() || isFromChoiceLib) {
                authorizationInfoLayout.setVisibility(View.GONE);
                isPlayCourse = true;
            } else {
                authorizationInfoLayout.setVisibility(View.VISIBLE);
                authorizationInfoView.setText(R.string.course_is_not_watch);
            }
        }
    }


    private void enterPlayEvent() {
        playCourse();
    }

    private void enterShareEvent() {
        shareCourse();
    }

    /**
     * 判断是否登陆，未登录提示登陆并跳转到登陆界面
     *
     * @return
     */
    private boolean islogin() {
        if (!isLogin() && getActivity() != null) {
            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.pls_login));
            ActivityUtils.enterLogin(getActivity(), false);
            return false;
        }
        return true;
    }

    /**
     * 检测是否需要替代ip作为内网来访问
     */
    private void checkCanReplaceIPAddress() {
        if (newResourceInfo == null) return;
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper(getActivity());
        helper.setResId(Integer.valueOf(newResourceInfo.getMicroId()))
                .setResType(newResourceInfo.getResourceType())
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        if (flag) {
                            //替换resUrl的ip
                            newResourceInfo.setResourceUrl(helper.getChangeIPUrl(newResourceInfo
                                    .getResourceUrl()));
                        }
                        enterDownLoadEvent();
                    }
                }).checkIP();
    }

    protected void enterDownLoadEvent() {
        boolean isHasNet = NetworkHelper.isNetworkConnected(getActivity());
        if (!isHasNet) {
            TipMsgHelper.ShowMsg(getActivity(), R.string.network_error);
            return;
        }
        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree != 0) {
            return;
        }
        if (!islogin()) {
            return;
        }
        if (newResourceInfo != null) {
            if (downloadService == null) {
                return;
            }
            //save CourseInfo to Local
            FileInfo fileInfo = downloadService.getFileInfo(getUserInfo().getMemberId(), newResourceInfo.getResourceId());
            if (fileInfo == null || fileInfo.isDownloadLapsed() || fileInfo.isDownloadFailed()
                    || fileInfo.isDownloadPaused() || fileInfo.isDownloadDeleted()) {
                if (fileInfo == null) {
                    fileInfo = newResourceInfo.toFileInfo(getUserInfo().getMemberId());
                }
                if (fileInfo != null && !TextUtils.isEmpty(fileInfo.getId())) {
                    DownloadCourseDTO dto = new DownloadCourseDTO(fileInfo.getId(),
                            newResourceInfo.getResourceId(), getMemeberId()
                            , newResourceInfo.getTitle(), newResourceInfo.getAuthorName(),
                            newResourceInfo.getDescription(), newResourceInfo.getThumbnail(),
                            newResourceInfo.getScreenType(),newResourceInfo.getAuthorId());
                    if (dto != null) {
                        DownloadCourseDao dao = new DownloadCourseDao(getActivity());
                        dao.addOrUpdateDownloadCourseDTO(dto);
                    }
                }

                // 未下载/下载失效/下载失败/下载暂停/删除下载 --> 开启下载
                downloadService.downloadFile(fileInfo);
                // TipsHelper.showToast(getActivity(), R.string.my_download_task_added);
                showMessageDialog(getString(R.string.add_to_download_queue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        enterMyCollectionBookList();
                    }
                });
            } else if (fileInfo.isDownloaded()) {
                // 已下载 --> 提示用户
                TipsHelper.showToast(getActivity(), R.string.my_downloaded);
            } else if (fileInfo.isDownloadWaiting() || fileInfo.isDownloadStarted()
                    || fileInfo.isDownloading()) {
                // 等待下载/下载已开始/正在下载 --> 提示用户
                TipsHelper.showToast(getActivity(), R.string.my_downloading);
            }
        }
    }


    private void enterPlayLocalCourseEvent() {
        if (fromType == Constants.FROM_MY_DOWNLOAD) {
            if (newResourceInfo != null) {
                String localPath = getLocalCoursePath(newResourceInfo);
                if (!TextUtils.isEmpty(localPath)) {
                    String courseRootPath = Utils.getCourseRootPath(localPath);
                    if (TextUtils.isEmpty(courseRootPath)) {
                        unzip(localPath, newResourceInfo.getTitle(), newResourceInfo.getScreenType(),
                                newResourceInfo.getDescription(), Constants.OPERATION_TYPE_PLAYCOURSE);
                    } else {
                        File file = new File(courseRootPath);
                        if (file.exists()) {
                            LocalCourseInfo info = new LocalCourseInfo();
                            info.mPath = courseRootPath;
                            info.mOrientation = newResourceInfo.getScreenType();
                            info.mTitle = newResourceInfo.getTitle();
                            playLocalCourse(info, false);
                        } else {
                            unzip(localPath, newResourceInfo.getTitle(), newResourceInfo.getScreenType(),
                                    newResourceInfo.getDescription(), Constants.OPERATION_TYPE_PLAYCOURSE);
                        }
                    }
                }
            }
        }
    }

    private void enterMakePicBookEvent() {
        if (fromType == Constants.FROM_MY_BOOK_SHELF || fromType == Constants.FROM_MY_DOWNLOAD) {
            if (newResourceInfo != null) {
                String localPath = getLocalCoursePath(newResourceInfo);
                if (!TextUtils.isEmpty(localPath)) {
                    String courseRootPath = Utils.getCourseRootPath(localPath);
                    if (TextUtils.isEmpty(courseRootPath)) {
                        unzip(localPath, newResourceInfo.getTitle(), 0, null, Constants.OPERATION_TYPE_MAKEPICBOOK);
                    } else {
                        File file = new File(courseRootPath);
                        if (file.exists()) {
                            importLocalPicResources(courseRootPath, newResourceInfo.getTitle());
                        } else {
                            unzip(localPath, newResourceInfo.getTitle(), 0, null, Constants.OPERATION_TYPE_MAKEPICBOOK);
                        }
                    }
                }
            }
        }
    }

    protected void enterSplitCourseList() {
        if (mParam != null && mParam.isFromLQMOOC && mParam.isAudition){
            TipMsgHelper.ShowMsg(getActivity(),R.string.buy_course_please);
            return;
        }
        if (newResourceInfo != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), SplitCourseListActivity.class);
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_ID, newResourceInfo.getMicroId());
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_NAME, newResourceInfo.getTitle());
            //是不是公开的资源
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_ISPUBLIC_RES, newResourceInfo
                    .isPublicResource());
            //是不是精品资源库资源
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_IS_CHOICE_LIB, newResourceInfo
                    .isQualityCourse());
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_IS_HIDEDOWNLOAD_BTN, isHideDownLoadBtn);
            intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE, fromType);
            if (mParam != null) {
                intent.putExtra(PassParamhelper.class.getSimpleName(), mParam);
            }
            startActivity(intent);
        }
    }

    private void enterShareScreenEvent() {
        if (fromType == Constants.FROM_MY_WORK) {
            if (localCourseInfo != null && !TextUtils.isEmpty(localCourseInfo.mPath)) {
                shareScreen(localCourseInfo);
            }
        } else if (fromType == Constants.FROM_MY_DOWNLOAD) {
            if (newResourceInfo != null) {
                String localPath = getLocalCoursePath(newResourceInfo);
                if (!TextUtils.isEmpty(localPath)) {
                    String courseRootPath = Utils.getCourseRootPath(localPath);
                    if (TextUtils.isEmpty(courseRootPath)) {
                        unzip(localPath, newResourceInfo.getTitle(), 0, null, Constants.OPERATION_TYPE_SHARESCEEN);
                    } else {
                        File file = new File(courseRootPath);
                        if (file.exists()) {
                            LocalCourseInfo info = new LocalCourseInfo();
                            info.mPath = courseRootPath;
                            info.mOrientation = newResourceInfo
                                    .getScreenType();
                            shareScreen(info);
                        } else {
                            unzip(localPath, newResourceInfo.getTitle(), 0, null, Constants.OPERATION_TYPE_SHARESCEEN);
                        }
                    }
                }
            }
        } else {
            if (newResourceInfo != null) {
                if (shareManager == null) {
                    shareManager = MyShareManager.getInstance(getActivity(), handler);
                }
                if (shareManager != null) {
                    if (shareManager.getSharedDevices() != null) {
                        if (newResourceInfo.getResourceType() == ResType.RES_TYPE_ONEPAGE) {
                            ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, true,
                                    null);
                        } else {
                            ActivityUtils.playOnlineCourse(getActivity(),
                                    newResourceInfo.getCourseInfo(), true,
                                    null);
                        }
                    } else {
                        TipMsgHelper.ShowMsg(getActivity(), R.string.no_share_play);
                    }
                }
            }
        }
    }

    private void playCourse() {
        if (fromType == Constants.FROM_MY_WORK) {
            if (localCourseInfo != null) {
                int resType = BaseUtils.getCoursetType(localCourseInfo.mPath);
                if (resType == ResType.RES_TYPE_ONEPAGE) {
                    playLocalOnePage(localCourseInfo);
                } else {
                    playLocalCourse(localCourseInfo, false);
                }
            }
        } else {
            if (newResourceInfo != null) {
                if (fromType == Constants.FROM_MY_DOWNLOAD) {
                    enterPlayLocalCourseEvent();
                } else {
                    Bundle bundle = getArguments();
                    PlaybackParam playbackParam = null;
                    boolean flag = (mParam != null && mParam.isFromLQMOOC);
                    if (bundle != null) {
//                        mTaskMarkParam = (TaskMarkParam) bundle.getSerializable(Constants.ACTION_TASKMARKPARAM);
//                        if (mTaskMarkParam != null) {
//                            playbackParam = new PlaybackParam();
//                            playbackParam.taskMarkParam = mTaskMarkParam;
//                        }
                    }
                    if (flag) {
                        if (playbackParam == null) {
                            playbackParam = new PlaybackParam();
                        }
                        playbackParam.mIsHideCollectTip = true;
                    }

                    if (newResourceInfo.isOnePage() || newResourceInfo.isStudyCard()) {
                        ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo,
                                false, playbackParam);
                    } else {
                        ActivityUtils.playOnlineCourse(getActivity(), newResourceInfo
                                .getCourseInfo(), false, playbackParam);
                    }
                }
            }
        }
    }

    private void playLocalCourse(LocalCourseInfo info, boolean isShareScreen) {
        String path = info.mPath;
        int resType = BaseUtils.getCoursetType(info.mPath);
        Intent it;
        if (fromType == Constants.FROM_MY_DOWNLOAD) {
            it = ActivityUtils.getIntentForPlayLocalCourse(
                    getActivity(), path,
                    info.mTitle, info.mDescription,
                    info.mOrientation, resType, false, isShareScreen);
        } else {
            it = ActivityUtils.getIntentForPlayLocalCourse(
                    getActivity(), path, info.mTitle, info.mDescription,
                    info.mOrientation, resType, true, isShareScreen);
        }
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(it, Common.ACTIVITY_REQUEST_ATTACHMENGT_EDIT);
    }

    private void playLocalOnePage(LocalCourseInfo info) {
        Fragment fragment = getParentFragment() != null ? getParentFragment() :
                PictureBooksDetailFragment.this;
        CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam(null,
                fragment, info.mPath, info.mTitle, info.mDescription, info.mOrientation);
        param.mEditable = false;
        param.fromType = SlideManagerHornForPhone.FromWhereData.FROM_LQCLOUD_COURSE;
        param.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true, true, true);
        CreateSlideHelper.startSlide(param, Common.ACTIVITY_REQUEST_ATTACHMENGT_EDIT);
    }

    protected void shareCourse() {
        if (fromType == Constants.FROM_MY_WORK || (fromType == Constants.FROM_CLOUD_SPACE
                && isMySelf)) {
            commitCourseHelper.commit(PictureBooksDetailFragment.this.getView(), null);
        } else {
            shareCourse2();
        }
    }

    private void shareCourse2() {
        if (newResourceInfo != null) {
            CourseInfo courseInfo = newResourceInfo.getCourseInfo();
            if (courseInfo != null) {
                ShareInfo shareInfo = courseInfo.getShareInfo(getActivity());
                shareInfo.setSharedResource(courseInfo.getSharedResource());
                if (shareInfo != null) {
                    new ShareUtils(getActivity()).share(PictureBooksDetailFragment.this.getView(), shareInfo);
                }
            }
        }
    }


    private void uploadCourse(LocalCourseInfo localCourseInfo, boolean isShare) {
        final UserInfo userInfo = getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(getActivity());
            return;
        }
        //更新MicroId
        String path = null;
        if (localCourseInfo != null) {
            path = localCourseInfo.mPath;
            if (!TextUtils.isEmpty(path) && path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }
            LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(), getMemeberId(),
                    path);
            if (dto != null && dto.getmMicroId() > 0) {
                localCourseInfo.mMicroId = dto.getmMicroId();
            }
        }
        if (!isShare) {
            CourseData courseData = null;
            if (newResourceInfo != null && fromType == Constants.FROM_CLOUD_SPACE) {
                courseData = new CourseData();
                if (!TextUtils.isEmpty(newResourceInfo.getMicroId())) {
                    courseData.id = Integer.parseInt(newResourceInfo.getMicroId());
                    courseData.type = newResourceInfo.getResourceType();
                    courseData.nickname = newResourceInfo.getTitle();
                    courseData.resourceurl = newResourceInfo.getResourceUrl();
                    courseData.code = newResourceInfo.getAuthorId();
                    courseData.description = newResourceInfo.getDescription();
                    courseData.createname = newResourceInfo.getAuthorName();
                    courseData.thumbnailurl = newResourceInfo.getThumbnail();
                    courseData.screentype = newResourceInfo.getScreenType();
                    courseData.size = newResourceInfo.getFileSize();
                }
            }
            commitCourseHelper.setIsLocal(true);
            commitCourseHelper.uploadCourse(userInfo, localCourseInfo, courseData, shareType);
            return;
        }
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo, localCourseInfo, null, 1);
        if (uploadParameter != null) {
            showLoadingDialog();
            UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                @Override
                public void onBack(Object result) {
                    final CourseUploadResult courseUploadResult = (CourseUploadResult) result;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoadingDialog();
                                if (courseUploadResult != null) {
                                    List<CourseData> courseDatas = courseUploadResult.data;
                                    if (courseDatas != null && courseDatas.size() > 0) {
                                        final CourseData data = courseDatas.get(0);
                                        if (data != null) {
                                            MediaListFragment.updateMedia(getActivity(), userInfo,
                                                    data.getShortCourseInfoList(),
                                                    MediaType.MICROCOURSE, new CallbackListener() {
                                                        @Override
                                                        public void onBack(Object result) {
                                                            commitCourseHelper.shareTo(shareType, data.getShareInfo(getActivity()));
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }


    private void commitStudentCourse(UserInfo userInfo, CourseData courseData, final String slidePath) {
        Map<String, Object> params = new HashMap<String, Object>();
        StudyTaskInfo task = newResourceInfo.getStudyTaskInfo();
        if (task != null) {
            int roleType = task.getRoleType();
            String studentId = task.getStudentId();
            String taskId = task.getTaskId();
            if (!TextUtils.isEmpty(taskId)) {
                params.put("TaskId", taskId);
            }
            if (roleType == RoleType.ROLE_TYPE_STUDENT) {
                params.put("StudentId", userInfo.getMemberId());
            } else if (roleType == RoleType.ROLE_TYPE_PARENT) {
                //家长复述微课需要传递孩子的Id
                if (!TextUtils.isEmpty(studentId)) {
                    params.put("StudentId", studentId);
                }
            }
        }
        if (courseData != null) {
            params.put("StudentResId", courseData.getIdType());
            params.put("StudentResUrl", courseData.resourceurl);
            params.put("StudentResTitle", courseData.nickname);
        }

        RequestHelper.RequestModelResultListener listener = new RequestHelper
                .RequestModelResultListener(getActivity(), DataResult.class) {
            @Override
            public void onSuccess(String json) {
                try {
                    if (getActivity() == null) {
                        return;
                    }
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        //通知前面刷新
                        setHasCommented(true);
                        //上传成功删除微课对应的素材
                        String temSlidePath = slidePath;
                        if (!TextUtils.isEmpty(temSlidePath)) {
                            if (temSlidePath.endsWith(File.separator)) {
                                temSlidePath = temSlidePath.substring(0, temSlidePath.length() - 1);
                            }
                            LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                    temSlidePath, true);
                        }
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.save_to_lq_cloud));
                        //刷新页面
                        setHasCommented(true);
                        //返回
                        getActivity().finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL, params,
                listener);
    }

    private void shareScreen(LocalCourseInfo info) {
        if (shareManager == null) {
            shareManager = MyShareManager.getInstance(getActivity(), handler);
        }
        if (shareManager != null) {
            if (shareManager.getSharedDevices() != null) {
                int resType = BaseUtils.getCoursetType(info.mPath);
                if (resType == ResType.RES_TYPE_ONEPAGE) {
                    playLocalOnePage(info);
                } else {
                    playLocalCourse(info, true);
                }
            } else {
                TipMsgHelper.ShowMsg(getActivity(), R.string.no_share_play);
            }
        }
    }

    public void showSharePlayControler(String title) {
        if (sharePlayControler == null) {
            sharePlayControler = new SharePlayControler(getActivity(), title, shareManager, null);
            sharePlayControler.setCancelable(false);
            sharePlayControler.show();
            sharePlayControler.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    sharePlayControler = null;
                }
            });
        }
    }

    private void shareplayControlSyncWithShareplayStatus(int status) {
        if (sharePlayControler != null) {
            sharePlayControler.syncShareplayStatus(status);
        }
    }

    private String getLocalCoursePath(NewResourceInfo info) {
        String path = null;

        if (info != null && !TextUtils.isEmpty(info.getResourceUrl())) {
            path = Common.DownloadWeike + Md5FileNameGenerator.generate(info.getResourceUrl()) + "/";
            if (!TextUtils.isEmpty(info.getResourceId())) {
                String[] ids = info.getResourceId().split("-");
                if (ids != null && ids.length == 2) {
                    if (!TextUtils.isEmpty(ids[1])) {
                        int resType = Integer.parseInt(ids[1]);
                        if (resType > ResType.RES_TYPE_BASE) {
                            path = path + info.getTitle() + "/";
                        }
                    }
                }
            }
        }
        return path;
    }

    private void unzip(final String destPath, String zipFilePath, String title, String resourceId) {
        if (TextUtils.isEmpty(destPath) || TextUtils.isEmpty(zipFilePath) || TextUtils.isEmpty(title)
                || TextUtils.isEmpty(resourceId)) {
            return;
        }
        Utils.createLocalDiskPath(destPath);

        if (!TextUtils.isEmpty(zipFilePath) && new File(zipFilePath).exists()) {
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(zipFilePath, destPath);
            showLoadingDialog();
            FileZipHelper.unzip(param, new FileZipHelper.ZipUnzipFileListener() {
                @Override
                public void onFinish(
                        FileZipHelper.ZipUnzipResult result) {
                    if (result.mIsOk && getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                dismissLoadingDialog();
                                if (!TextUtils.isEmpty(destPath) && new File(destPath).exists()) {
                                    String courseRootPath = Utils.getCourseRootPath(destPath);
                                    if (!TextUtils.isEmpty(courseRootPath)) {
                                        File headFile = new File(courseRootPath,
                                                "head.jpg");
                                        if (headFile != null && headFile.canRead()) {
                                            newResourceInfo.setThumbnail(headFile.getAbsolutePath());
                                            getThumbnailManager().displayImageWithDefault(
                                                    newResourceInfo.getThumbnail(), picBookImageView,
                                                    R.drawable.default_book_cover);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void unzip(final String destPath, final String title,
                       final int orientaion, final String description,
                       final int operationType) {
        if (newResourceInfo != null) {
            if (downloadService == null) {
                return;
            }
            FileInfo fileInfo = downloadService.getFileInfo(getUserInfo().getMemberId(),
                    newResourceInfo.getResourceId());
            if (fileInfo != null && fileInfo.isDownloaded()) {
                String zipFilePath = fileInfo.getFilePath();
                if (!TextUtils.isEmpty(zipFilePath) && new File(zipFilePath).exists()) {
                    FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(zipFilePath, destPath);
                    showLoadingDialog();
                    FileZipHelper.unzip(param, new FileZipHelper.ZipUnzipFileListener() {
                        @Override
                        public void onFinish(
                                FileZipHelper.ZipUnzipResult result) {
                            if (result.mIsOk && getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        dismissLoadingDialog();
                                        if (!TextUtils.isEmpty(destPath) && new File(destPath).exists()) {
                                            String courseRootPath = Utils.getCourseRootPath(destPath);
                                            if (!TextUtils.isEmpty(courseRootPath)) {
                                                if (operationType == Constants.OPERATION_TYPE_SHARESCEEN) {
                                                    LocalCourseInfo info = new LocalCourseInfo();
                                                    info.mPath = courseRootPath;
                                                    info.mOrientation = newResourceInfo
                                                            .getScreenType();
                                                    info.mTitle = newResourceInfo.getTitle();
                                                    playLocalCourse(info, true);
                                                } else if (operationType == Constants.OPERATION_TYPE_MAKEPICBOOK) {
                                                    importLocalPicResources(courseRootPath, title);
                                                } else if (operationType == Constants.OPERATION_TYPE_EDITCOURSE) {
                                                    copyLocalCourse(courseRootPath, orientaion, description);
                                                } else if (operationType == Constants
                                                        .OPERATION_TYPE_PLAYCOURSE) {
                                                    LocalCourseInfo info = new LocalCourseInfo();
                                                    info.mPath = courseRootPath;
                                                    info.mOrientation = newResourceInfo
                                                            .getScreenType();
                                                    info.mTitle = newResourceInfo.getTitle();
                                                    playLocalCourse(info, false);
                                                } else if (operationType == Constants
                                                        .OPERATION_TYPE_GET_THUMBNAIL) {
                                                    File headFile = new File(courseRootPath,
                                                            "head.jpg");
                                                    if (headFile != null && headFile.canRead()) {
                                                        newResourceInfo.setThumbnail(headFile.getAbsolutePath());
                                                        getThumbnailManager().displayImageWithDefault(
                                                                newResourceInfo.getThumbnail(), picBookImageView,
                                                                R.drawable.default_book_cover);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }


    private List<String> getPicPaths(String coursePath) {
        List<String> paths = new ArrayList<String>();
        if (!TextUtils.isEmpty(coursePath)) {
            File pdfFile = new File(coursePath, "pdf");
            File[] files = pdfFile.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0, len = files.length; i < len; i++) {
                    if (files[i] != null) {
                        String fileName = files[i].getName();
                        if (!TextUtils.isEmpty(fileName) && fileName.contains("pdf_page")) {
                            paths.add(files[i].getAbsolutePath());
                        }
                    }
                }
            }
        }
        return paths;
    }

    private void getPicPaths(final String coursePath, final String title) {
        if (!TextUtils.isEmpty(coursePath)) {
            File courseIndexFile = new File(coursePath, BaseUtils.RECORD_XML_NAME);
            if (courseIndexFile != null && courseIndexFile.exists() && courseIndexFile.canRead()) {
                Loader loader = new Loader(getActivity(), null);
                loader.startload(courseIndexFile.getPath(), new Loader.OnLoadFinishListener() {
                    @Override
                    public void onFinish(Loader loader, String path, boolean bSuccess) {
                        if (bSuccess) {
                            List<String> rawImages = loader.getRawImages();
                            if (rawImages == null || rawImages.size() == 0) {
                                createNewRetellCourseSlidePage(newResourceInfo.getScreenType());
                            } else {
                                List<String> paths = new ArrayList<String>();
                                File pdfFile = new File(coursePath, "pdf");
                                for (String item : rawImages) {
                                    if (!TextUtils.isEmpty(item)) {
                                        StringBuilder builder = new StringBuilder();
                                        builder.append(pdfFile.getPath());
                                        builder.append(File.separator);
                                        builder.append(item);
                                        paths.add(builder.toString());
                                    }
                                }
                                importLocalPicResources(paths, title);
                            }
                        }
                    }
                });
            }
        }
    }

    private void importLocalPicResources(String coursePath, final String title) {
        savePath = Utils.getUserCourseRootPath(getMemeberId(), CourseType.COURSE_TYPE_IMPORT, false);
        getPicPaths(coursePath, title);
//        final List<String> paths = getPicPaths(coursePath);
//        if (paths == null || paths.size() == 0){
//            createNewRetellCourseSlidePage(newResourceInfo.getScreenType());
//        }else {
//            importLocalPicResources(paths, title);
//        }
    }

    private void copyLocalCourse(final String coursePath, final int orientation, final String description) {
        if (TextUtils.isEmpty(coursePath)) {
            return;
        }
        savePath = Utils.getUserCourseRootPath(getMemeberId(), CourseType.COURSE_TYPE_LOCAL, false);

        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }
        String tempTitle = null;
        if (newResourceInfo != null) {
            tempTitle = newResourceInfo.getTitle();
        }
        if (TextUtils.isEmpty(tempTitle)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(savePath);
        builder.append(DateUtils.millSecToDateStr(System.currentTimeMillis()));
        builder.append(File.separator);
        final String destPath = builder.toString();

        copyLocalCourse(coursePath, destPath, orientation, tempTitle, description);
    }

    private void copyLocalCourse(String srcPath, String destPath, int orientation, String
            title, String description) {
        CopyCourseTask copyCourseTask = new CopyCourseTask(getActivity(), srcPath, destPath,
                orientation, title, description);
        copyCourseTask.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    LocalCourseInfo info = (LocalCourseInfo) result;
                    enterLocalCourse(info);
                }
            }
        });
        copyCourseTask.execute();
    }

    private void enterMyCollectionBookList() {
        Intent intent = new Intent(getActivity(), MyDownloadListActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                if (isIntroduction) {
                    Intent mIntent = new Intent();
                    mIntent.putExtra(IntroductionForReadCourseFragment.UPDATETHUMBIAL, newResourceInfo
                            .getThumbnail());
                    setResult(Activity.RESULT_OK);
                }
                getActivity().finish();
                break;
            case R.id.pic_book_imageview:
                //图片
            case R.id.iv_icon:
                //播放的按钮
            case R.id.iv_play_course:
                if (!isPlayCourse) {
                    return;
                }
                if (UIUtils.isFastClick()) {
                    return;
                }
                doAuthor();
                enterPlayEvent();
                break;

            case R.id.contacts_header_right_ico:
                // TODO: 2017/12/25  弹窗
                showPopwindow();
                break;
            case R.id.open_consultion_view:
                CustomerServiceActivity.start(getActivity(), CustomerServiceActivity.SOURCE_TYPE_OPEN_CONSULTION);
                break;
            case R.id.iv_qr_code:
                doAuthor();
                //二维码
                Utils.showViewQrCodeDialog(getActivity(), newResourceInfo, null);
                break;
            case R.id.rl_do_course:
                if (UIUtils.isFastClick()) {
                    return;
                }
                doAuthor();
                //做课件
                if (fromType == Constants.FROM_MY_DOWNLOAD) {
                    DoCourseHelper helper = new DoCourseHelper
                            (getActivity(), downloadService);
                    helper.doLocalLqCourse(newResourceInfo,
                            DoCourseHelper.FromType.DO_LQ_COURSE);

                } else {
                    makeCourse();
                }


                break;
            case R.id.rl_add_reading:
                if (UIUtils.isFastClick()) {
                    return;
                }
                doAuthor();
                //加点读
                //区分点读和录音课件的我要加点读
                DoCourseHelper doCourseHelper = new DoCourseHelper
                        (getActivity(), downloadService);
                if (fromType == Constants.FROM_MY_DOWNLOAD) {
                    doCourseHelper.doLocalLqCourse(newResourceInfo,
                            DoCourseHelper.FromType.Do_SLIDE_COURSE);
                } else {
                    doCourseHelper.doRemoteLqCourse(newResourceInfo,
                            DoCourseHelper.FromType.Do_SLIDE_COURSE);
                }
                break;
            case R.id.iv_share:
                //分享 或者 发送
                enterShareEvent();
                break;

            case R.id.iv_collect:
                //收藏
                doCollect();
                break;
            case R.id.ll_introduction:
                //简介
                handleIntroData(true,false);
                break;
            case R.id.ll_message:
                //进入更多评论的界面
                enterCommentDetail();
            default:
                break;
        }
    }

    private void doAuthor() {
        //如果是vipSchool省去校验权限
        if (isVipSchool) {
            return;
        }
        if (!VipConfig.isVip(getActivity()) && authorizationInfo != null && !authorizationInfo
                .isIsMemberAuthorized()) {
            authorizeToMembers(schoolId, feeSchoolId);
        }
    }

    private void showPopwindow() {
        View view = findViewById(R.id.contacts_header_right_ico);
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu, null);
            contentView.setBackgroundResource(R.drawable.pop_menu_bg);
            //处理popWindow 显示内容
            handleLogic(contentView,true);

            mPopWindow = new CustomPopWindow.PopupWindowBuilder(getContext())
                    //显示的布局，还可以通过设置一个View
                    .setView(contentView)
                    //创建PopupWindow
                    .create();
        }

        //水平偏移量
        int xOff = mPopWindow.getWidth() - view.getWidth() + DensityUtils.dp2px(getContext(), 0);
        mPopWindow.showAsDropDown(view, -xOff, 0);
    }


    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private boolean handleLogic(View contentView,boolean isShowPop) {
        EntryBean data = null;
        final List<EntryBean> items = new ArrayList<>();

        if (isSplitCourse) {
        } else {
            if (fromType == Constants.FROM_MY_WORK || fromType == Constants.FROM_MY_DOWNLOAD) {
            } else {
                if (newResourceInfo != null
                        && ((newResourceInfo.getResourceType() != ResType.RES_TYPE_ONEPAGE)
                        && (newResourceInfo.getResourceType() != ResType.RES_TYPE_COURSE)
                        && (newResourceInfo.getResourceType() != ResType.RES_TYPE_OLD_COURSE))
                        && (newResourceInfo.getSplitFlag() == 4)) {

                    data = new EntryBean(R.drawable.icon_show_splict_course,
                            getString(R.string.str_single_page_show), BtnEntity.TYPE_SPLIT_COURSE);
                    items.add(data);
                }
            }
        }

        //发送 或者 分享
//        if (fromType == Constants.FROM_MY_WORK) {
//
//            data = new EntryBean(R.drawable.icon_share,
//                    getString(R.string.send), BtnEntity.TYPE_SHARE_COURSE);
//            items.add(data);
//        }
//        else if (fromType == Constants.FROM_CLOUD_SPACE) {
//            if (newResourceInfo != null) {
//                int id;
//                if (getMemeberId().equals(newResourceInfo.getAuthorId())) {
//                    isMySelf = true;
//                    id = R.string.send;
//                } else {
//                    isMySelf = false;
//                    id = R.string.share_to;
//                }
//                data = new EntryBean(R.drawable.icon_share,
//                        getString(id), BtnEntity.TYPE_SHARE_COURSE);
//                items.add(data);
//            }
//        } else if (fromType == Constants.FROM_MY_DOWNLOAD) {
//            //我的下载里面不需要“发送”或者“分享”
//        } else {
//
//            data = new EntryBean(R.drawable.icon_share,
//                    getString(R.string.share_to), BtnEntity.TYPE_SHARE_COURSE);
//            items.add(data);
//        }

//        boolean flag = (mParam != null && mParam.isFromLQMOOC);
//        //收藏
//        if (!(fromType == Constants.FROM_MY_DOWNLOAD || fromType == Constants.FROM_MY_WORK ||
//                fromType == Constants.FROM_CLOUD_SPACE)) {
//            if (!flag) {
//
//                data = new EntryBean(R.drawable.icon_collect,
//                        getString(R.string.collection), BtnEntity.TYPE_COLLECT);
//                items.add(data);
//            }
//        }

        //下载
        if (fromType == Constants.FROM_MY_WORK || fromType == Constants.FROM_MY_DOWNLOAD) {
        } else {
            if (newResourceInfo != null
                    && ((newResourceInfo.getResourceType() != ResType.RES_TYPE_COURSE)
                    && (newResourceInfo.getResourceType() != ResType.RES_TYPE_OLD_COURSE))) {
                if (!isHideDownLoadBtn) {
                    data = new EntryBean(R.drawable.icon_download,
                            getString(R.string.download), BtnEntity.TYPE_DOWNLOAD_COURSE);
                    items.add(data);
                }
            }
        }

        //投屏
//        data = new EntryBean(R.drawable.icon_share_screen,
//                getString(R.string.sharescreen), BtnEntity.TYPE_SHARE_SCREEN);
//        items.add(data);

        if (isFromCatalog) {
            //返回主页
            data = new EntryBean(R.drawable.icon_back_home,
                    getString(R.string.back_home), BtnEntity.TYPE_BACK_HOME);
            items.add(data);
        }

        if (items.size() <= 0) {
            return false;
        }
        if (!isShowPop){
            return true;
        }
        ListView listView = (ListView) contentView.findViewById(R.id.pop_menu_list);
        PopMenuAdapter adapter = new PopMenuAdapter(getContext(), items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mPopWindow != null) {
                    mPopWindow.dissmiss();
                }
                EntryBean data = items.get(i);
                doAuthor();
                switch (data.id) {
                    //投屏
                    case BtnEntity.TYPE_SHARE_SCREEN:
                        enterShareScreenEvent();
                        break;
                    case BtnEntity.TYPE_SEND_COURSE:
                        enterShareEvent();
                        break;
                    //分享
                    case BtnEntity.TYPE_SHARE_COURSE:
                        enterShareEvent();
                        break;

                    //下载
                    case BtnEntity.TYPE_DOWNLOAD_COURSE:
                        //防止连续点击 目前控制2秒钟
                        if (UIUtils.isFastClick()) {
                            return;
                        }
                        //校验ip的有效性
                        checkCanReplaceIPAddress();
                        break;
                    //查看分页
                    case BtnEntity.TYPE_SPLIT_COURSE:
                        enterSplitCourseList();
                        break;

                    //收藏
                    case BtnEntity.TYPE_COLLECT:
                        doCollect();
                        break;
                    //返回主页
                    case BtnEntity.TYPE_BACK_HOME:
                        backHome();
                        break;
                    default:
                        break;
                }
            }
        });
        return true;
    }


    private void handleHeadViewVisible(){
        LinearLayout readDetailLayout = (LinearLayout) findViewById(R.id.ll_course_detail);
        LinearLayout courseUseWay = (LinearLayout) findViewById(R.id.ll_course_use_way);
        courseUseWay.setVisibility(View.VISIBLE);
        if (fromType == Constants.FROM_MY_WORK || fromType == Constants.FROM_MY_DOWNLOAD){
            readDetailLayout.setVisibility(View.GONE);
        } else {
            readDetailLayout.setVisibility(View.VISIBLE);
        }

        ImageView collectImage = (ImageView) findViewById(R.id.iv_collect);
        ImageView shareImage = (ImageView) findViewById(R.id.iv_share);
        ImageView qrImageView = (ImageView) findViewById(R.id.iv_qr_code);
        //收藏
        boolean flag = (mParam != null && mParam.isFromLQMOOC);
        if (!flag) {
            collectImage.setVisibility(View.VISIBLE);
            collectImage.setOnClickListener(this);
        }

        if (fromType != Constants.FROM_MY_DOWNLOAD){
            shareImage.setVisibility(View.VISIBLE);
            shareImage.setOnClickListener(this);
        }
        if (fromType != Constants.FROM_MY_DOWNLOAD && fromType != Constants.FROM_MY_WORK) {
            if (isFromChoiceLib && !VipConfig.isVip(getActivity())){
                //精品资源库不显示二维码
                qrImageView.setVisibility(View.GONE);
            } else {
                qrImageView.setOnClickListener(this);
                qrImageView.setVisibility(View.VISIBLE);
            }
        }

        if (fromType == Constants.FROM_CLOUD_SPACE) {
            if (newResourceInfo != null) {
                if (getMemeberId().equals(newResourceInfo.getAuthorId())) {
                    isMySelf = true;
                } else {
                    isMySelf = false;
                }
            }
        }
    }


    private void initRightButton(){
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setBackgroundResource(R.drawable.icon_plus_white);
            if (fromType == Constants.FROM_MY_DOWNLOAD){
                //我的下载隐藏右上角的加号
                imageView.setVisibility(View.GONE);
            } else {
                if (handleLogic(null,false)){
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
            imageView.setOnClickListener(this);
        }
    }
    private void loadAuthorUserInfo(){
        if (fromType == Constants.FROM_MY_WORK){
            UserInfo userInfo = getUserInfo();
            if (userInfo != null) {
                String studentName = userInfo.getRealName();
                if (TextUtils.isEmpty(studentName)) {
                    studentName = userInfo.getNickName();
                }
                newResourceInfo.setAuthorName(studentName);
                authorTextView.setText(studentName);
                MyApplication.getThumbnailManager(getActivity()).displayUserIcon
                        (AppSettings.getFileUrl(userInfo.getHeaderPic()),
                                authorIconImageView);
            }
            return;
        }
        if (newResourceInfo == null
                || TextUtils.isEmpty(newResourceInfo.getAuthorId())
                || TextUtils.equals(newResourceInfo.getAuthorId(),"00000000-0000-0000-0000-000000000000")){
            return;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("UserId", newResourceInfo.getAuthorId());
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
                        UserInfo authorInfo = getResult().getModel();
                        if (authorInfo != null && authorIconImageView != null){
                            MyApplication.getThumbnailManager(getActivity()).displayUserIcon
                                    (AppSettings.getFileUrl(authorInfo.getHeaderPic()),
                                            authorIconImageView);
                        }
                    }
                });
    }

    @Override
    public void importResource(String title, int type) {

    }

    @Override
    public void noteCommit(int shareType) {
        switch (shareType) {
            case ShareType.SHARE_TYPE_CLOUD_COURSE:
            case ShareType.SHARE_TYPE_STUDY_TASK:
            case ShareType.SHARE_TYPE_CLASSROOM:
            case ShareType.SHARE_TYPE_PICTUREBOOK:
            case ShareType.SHARE_TYPE_PUBLIC_COURSE:
                this.shareType = shareType;
                if (fromType == Constants.FROM_MY_WORK || fromType == Constants.FROM_CLOUD_SPACE) {
                    uploadCourse(localCourseInfo, false);
                }
                break;
            case ShareType.SHARE_TYPE_WECHAT:
            case ShareType.SHARE_TYPE_WECHATMOMENTS:
            case ShareType.SHARE_TYPE_QQ:
            case ShareType.SHARE_TYPE_QZONE:
            case ShareType.SHARE_TYPE_CONTACTS:
                this.shareType = shareType;
                if (fromType == Constants.FROM_CLOUD_SPACE) {
                    if (newResourceInfo != null) {
                        CourseInfo courseInfo = newResourceInfo.getCourseInfo();
                        if (courseInfo != null) {
                            ShareInfo shareInfo = courseInfo.getShareInfo(getActivity());
                            if (shareInfo != null && commitCourseHelper != null) {
                                commitCourseHelper.shareTo(shareType, shareInfo);
                            }
                        }
                    }
                } else {
                    uploadCourse(localCourseInfo, true);
                }
                break;
        }
    }


    public static void setHasCommented(boolean hasCommented) {
        PictureBooksDetailFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    private void backHome() {
        if (getActivity() != null) {
            ActivityStack activityStack = ((MyApplication) (getActivity().getApplication())).getActivityStack();
            if (activityStack != null) {
                activityStack.finishUtil(HomeActivity.class);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        //有声相册通用requestCode：105
        if (requestCode == 105 || requestCode == Common.ACTIVITY_REQUEST_ATTACHMENGT_EDIT) {
            if (data != null) {
                String savePath = data.getStringExtra(SlideManagerHornForPhone.SAVE_PATH);
                if (savePath != null) {
                    if (savePath.endsWith(File.separator)) {
                        savePath = savePath.substring(0, savePath.length() - 1);
                    }
                    LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(),
                            getMemeberId(), savePath);
                    if (dto != null) {
                        LocalCourseInfo info = dto.toLocalCourseInfo();
                        if (info != null && newResourceInfo != null) {
                            newResourceInfo.setResourceUrl(info.mPath);
                            newResourceInfo.setTitle(info.mTitle);
                            newResourceInfo.setThumbnail(info.mPath + File.separator
                                    + Utils.RECORD_HEAD_IMAGE_NAME);
                            newResourceInfo.setDescription(info.mDescription);
                            localCourseInfo = info;
                        }
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_EDITCOURSE) {
            TipMsgHelper.ShowMsg(getActivity(), getString(R.string.save_to_lq_cloud));

        } else if (requestCode == REQUEST_CODE_SLIDE) {
            //学习任务，家长和学生需要上传课件。
            //老版看课件也放开上传了

            //老师仅保存到本地
            if (data != null) {
                String slidePath = data.getStringExtra(SlideManager
                        .EXTRA_SLIDE_PATH);
                String coursePath = data.getStringExtra(SlideManager
                        .EXTRA_COURSE_PATH);
                LogUtils.logi("TEST", "SlidePath = " + slidePath);
                LogUtils.logi("TEST", "CoursePath = " + coursePath);
                if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                    TipMsgHelper.ShowMsg(getActivity(), getString(R.string.save_to_lq_cloud));
                    //已编辑
//
                } else if (!TextUtils.isEmpty(slidePath)) {
                    //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                    if (slidePath.endsWith(File.separator)) {
                        slidePath = slidePath.substring(0, slidePath.length() - 1);
                    }
                    LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                            slidePath, true);
                }
            }
        } else if (requestCode == REQUEST_CODE_DO_SLIDE_TOAST || requestCode == 105) {
            if (data != null) {
                //课件我要加点读的回来的提示
                String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                if (!TextUtils.isEmpty(slidePath)) {
                    TipMsgHelper.ShowMsg(getActivity(), getString(R.string.lqcourse_save_local));
                }
            }
        }
    }

    private LocalCourseInfo getLocalCourseInfo(String coursePath) {
        LocalCourseInfo result = null;
        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        try {
            LocalCourseDTO localCourseDTO = localCourseDao.getLocalCourseDTOByPath
                    (getMemeberId(), coursePath);
            if (localCourseDTO != null) {
                return localCourseDTO.toLocalCourseInfo();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断是否是老师身份
     *
     * @return
     */
    private boolean isTeacher() {
        if (info != null) {
            int roleType = info.getRoleType();
            if (roleType == RoleType.ROLE_TYPE_TEACHER) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void importLocalPicResourcesCheck(List<String> paths, String title) {
        savePath = Utils.getUserCourseRootPath(getMemeberId(), CourseType.COURSE_TYPE_IMPORT, false);
        importLocalPicResources(paths, title);
    }

    @Override
    protected void saveData(Message msg) {
        LocalCourseInfo localCourseInfo = (LocalCourseInfo) msg.obj;
        if (localCourseInfo != null) {
            if (newResourceInfo != null) {
                localCourseInfo.mOrientation = newResourceInfo.getScreenType();
            }
            saveData(localCourseInfo);
            if (info != null) {
                localCourseInfo.mOriginVoicePath = info.getResUrl();
                if (localCourseInfo.mOriginVoicePath != null
                        && localCourseInfo.mOriginVoicePath.contains(".zip")) {
                    //截取zip前面的字符串
                    localCourseInfo.mOriginVoicePath
                            = localCourseInfo.mOriginVoicePath.substring(0,
                            localCourseInfo.mOriginVoicePath.indexOf(".zip"));
                }
            }

            enterSlideNew(localCourseInfo, MaterialType.RECORD_BOOK,
                    REQUEST_CODE_SLIDE, false, -1, "");

        }
    }

    /**
     * 判断是否是学习任务的新/老版看课件
     *
     * @return
     */
    private boolean isWatchWawaCourse() {
        //首先判断类型
        boolean containsWatchWawaCourse = taskType == StudyTaskType.NEW_WATACH_WAWA_COURSE
                || taskType == StudyTaskType.WATCH_WAWA_COURSE;
        //其次判断是否从学习任务过来
        boolean fromStudyTask = fromStudyTask();
        boolean result = containsWatchWawaCourse && fromStudyTask;
        return result;
    }

    /**
     * 判断是否来自学习任务
     *
     * @return
     */
    private boolean fromStudyTask() {
        if (info != null) {
            //是否来自学习任务，如果只判断info是否为null是不行的。
            boolean fromStudyTask = info.getIsFromStudyTask();
            if (fromStudyTask) {
                return true;
            }
        }
        return false;
    }

    private LocalBroadcastManager mBroadcastManager;

    private void registResultBroadcast() {
        if (mBroadcastManager == null) {
            mBroadcastManager = LocalBroadcastManager.getInstance(getMyApplication());
            IntentFilter filter = new IntentFilter(ACTION_MARK_SCORE);
            mBroadcastManager.registerReceiver(mReceiver, filter);
        }

    }

    private void unRegistResultBroadcast() {
        if (mBroadcastManager != null && mReceiver != null) {
            mBroadcastManager.unregisterReceiver(mReceiver);
            mBroadcastManager = null;
            mReceiver = null;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//                if (mTaskMarkParam != null) {
//                    mTaskMarkParam.isMarked = true;
//                }
        }
    };

    public TextView getPicTextView() {
        return readContTextView;
    }

    private void initBottomData() {
        if (isSplitCourse) {
            return;
        }
        final LinearLayout introductionLayout = (LinearLayout) findViewById(R.id.ll_introduction);
        LinearLayout messageLayout = (LinearLayout) findViewById(R.id.ll_message);
        messageLayout.setOnClickListener(this);
        introductionLayout.setOnClickListener(this);
        introductionLayout.setVisibility(View.VISIBLE);
        introductionTextView = (TextView) findViewById(R.id.tv_introduction_content);
        introArrowImageView = (ImageView) findViewById(R.id.iv_arrow_icon);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //初始化标题
        if (fromType == Constants.FROM_MY_DOWNLOAD || fromType == Constants.FROM_MY_WORK){
            messageLayout.setVisibility(View.GONE);
            handleIntroData(false,true);
        } else {
            handleIntroData(true,true);
            messageLayout.setVisibility(View.VISIBLE);
            messageLayout.setOnClickListener(this);
            findViewById(R.id.ten_dp_view).setVisibility(View.VISIBLE);
            mCommentFragment = PictureBooksDetailItemFragment.newInstance(true, courseId, newResourceInfo);
            ft.add(R.id.frame_layout, mCommentFragment, mCommentFragment.getTag());
        }
        ft.commit();
    }

    private void handleIntroData(boolean isCouldCourse,boolean isFirstIn){
        if (isFirstIn){
            if (isCouldCourse) {
                introductionTextView.setVisibility(View.GONE);
                introArrowImageView.setImageResource(R.drawable.arrow_gray_down_icon);
            } else {
                introductionTextView.setVisibility(View.VISIBLE);
                introArrowImageView.setImageResource(R.drawable.arrow_gray_up_icon);
            }
            return;
        }
        if (introductionTextView.getVisibility() == View.VISIBLE){
            introductionTextView.setVisibility(View.GONE);
            introArrowImageView.setImageResource(R.drawable.arrow_gray_down_icon);
        } else {
            introductionTextView.setVisibility(View.VISIBLE);
            introArrowImageView.setImageResource(R.drawable.arrow_gray_up_icon);
        }
        showIntroDescriptionData();
    }

    private void showIntroDescriptionData(){
        if (newResourceInfo != null && introductionTextView != null){
            String content = newResourceInfo.getDescription();
            if (TextUtils.isEmpty(content)){
                introductionTextView.setText(getString(R.string.no_content));
            } else {
                introductionTextView.setText(content);
            }
        }
    }

    /**
     * 进入评论的详情页
     */
    private void enterCommentDetail(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        CommentDetailFragment commentDetailFragment = CommentDetailFragment.newInstance( courseId,
                newResourceInfo);
        ft.add(R.id.activity_body, commentDetailFragment, commentDetailFragment.getTag());
        ft.hide(this);
        ft.show(commentDetailFragment);
        ft.commit();
        ft.addToBackStack(null);
    }
}
