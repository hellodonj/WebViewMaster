package com.galaxyschool.app.wawaschool.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.CustomerServiceActivity;
import com.galaxyschool.app.wawaschool.HomeActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SplitCourseListActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.CommitCourseHelper;
import com.galaxyschool.app.wawaschool.common.CommitHelper;
import com.galaxyschool.app.wawaschool.common.DoCourseHelper;
import com.galaxyschool.app.wawaschool.common.PassParamhelper;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.config.VipConfig;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.NewResourcePadAdapterViewHelper;
import com.galaxyschool.app.wawaschool.helper.UserInfoHelper;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.views.CircleImageView;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.TaskOrderNewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.views.PopupMenu;
import com.galaxyschool.app.wawaschool.views.PullToRefreshView;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.lqwawa.tools.DensityUtils;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.ooshare.MyShareManager;
import com.oosic.apps.iemaker.base.ooshare.SharePlayControler;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareType;
import com.osastudio.common.library.ActivityStack;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by E450 on 2016/12/23.
 */

public class TaskOrderDetailFragment extends ContactsListFragment
        implements PictureBooksDetailFragment.Constants, CommitHelper.NoteCommitListener {
    public static final String TAG = TaskOrderDetailFragment.class.getSimpleName();
    private static final int MAX_BUTTON_PER_ROW = 3;
    private static final int MAX_TASK_PER_ROW = 2;
    private TextView readContTextView;
    private String gridViewTag;
    private NewResourceInfo newResourceInfo;
    private boolean isFromLqTask, isFromSchoolResource, isFromAirClass;
    private int fromType = FROM_OTHRE;
    private CommitCourseHelper commitCourseHelper;
    private MyShareManager shareManager;
    private SharePlayControler sharePlayControler;
    private CircleImageView authorIconImageView;

    private boolean isFromScanTask;
    private boolean isSplitCourse;
    private boolean isHasPermission;

    //判断是否收藏的
    private boolean isCollection;
    //收藏的暂时保存的schoolId
    private String collectionSchoolId;
    //是否来自精品资源库
    private boolean isFromChoiceLib;
    //获取参数的来源
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
    private PopupMenu popupMenu;
    private CustomPopWindow mPopWindow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_order_detail, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntent();
        initViews();
        initCommentData();
        handleHeadViewVisible();
        loadUserInfoData();
    }

    private void loadIntent() {
        Intent intent = getActivity().getIntent();
        fromType = intent.getIntExtra(FROM_SOURCE_TYPE, FROM_OTHRE);
        newResourceInfo = intent.getParcelableExtra(NewResourceInfo.class.getSimpleName());
        mParam = (PassParamhelper) intent.getSerializableExtra(PassParamhelper.class.getSimpleName());
        if (newResourceInfo != null) {
            isVipSchool = newResourceInfo.isVipSchool();
            isFromLqTask = newResourceInfo.isFromLqTask();
            isFromSchoolResource = newResourceInfo.isFromSchoolResource();//从SchoolResourceContainerFragment中的任务单进来
            //空中课堂进来不需要返回主页的按钮
            isFromAirClass = newResourceInfo.isFromAirClass();
            isFromScanTask = newResourceInfo.isScanTask();
            if (newResourceInfo.getResourceType() > ResType.RES_TYPE_BASE) {
                isSplitCourse = true;
            }
            isHasPermission = newResourceInfo.isHasPermission();
            if (newResourceInfo.isMyCollection()) {
                //这里备份收藏的数据
                isCollection = true;
                collectionSchoolId = newResourceInfo.getCollectionOrigin();
            }
            //针对添加收藏时用到字段
            isFromChoiceLib = newResourceInfo.isQualityCourse();
            collectionSchoolId = newResourceInfo.getCollectionOrigin();
            isHideCollectBtn = newResourceInfo.isHideCollectBtn();
            if (isFromChoiceLib && !VipConfig.isVip(getActivity())){
                isHideCollectBtn = true;
            }
        }
        //获取传过来的bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (isHideCollectBtn) {
                isHideCollectBtn = bundle.getBoolean(ActivityUtils.EXTRA_IS_NEED_HIDE_COLLECT_BTN, true);
            }
        }
        if (mParam != null){
            if (mParam.isFromLQMOOC && mParam.isAudition){
                isFromChoiceLib = true;
                if (!VipConfig.isVip(getActivity())){
                    isHideCollectBtn = true;
                }
            }
        }
        initCommitCourseHelper();
    }

    private void initCommitCourseHelper() {
        commitCourseHelper = new CommitCourseHelper(getActivity());
        commitCourseHelper.setHiddenGenESchool(true);
        commitCourseHelper.setNoteCommitListener(TaskOrderDetailFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSoftKeyboard(getActivity());
        loadDatas();
    }

    private void initViews() {
        TextView textView = (TextView) findViewById(R.id.tv_course_title);
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            String title = "";
            if (newResourceInfo != null) {
                title = newResourceInfo.getTitle();
            }
            textView.setText(title);
        }
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.my_back_0);
            imageView.setOnClickListener(this);
        }
        ImageView playBtnImageView = (ImageView) findViewById(R.id.iv_play_course);
        if (playBtnImageView != null) {
            playBtnImageView.setOnClickListener(this);
        }

        imageView = (ImageView) findViewById(R.id.pic_book_imageview);
        if (imageView != null) {
//            imageView.setOnClickListener(this);
            imageView.setVisibility(View.VISIBLE);
            if (newResourceInfo != null) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(newResourceInfo.getThumbnail()), imageView,
                        R.drawable.default_cover);
            } else {
                imageView.setImageResource(R.drawable.default_book_cover);
            }
        }

        readContTextView = (TextView) findViewById(R.id.pic_book_read_count_textview);
        //作者的头像
        authorIconImageView = (CircleImageView) findViewById(R.id.iv_user_icon);
//        final PullToRefreshView pullToRefreshView = (PullToRefreshView) findViewById(
//                R.id.contacts_pull_to_refresh);
//        setStopPullUpState(true);
//        setPullToRefreshView(pullToRefreshView);
//        initTaskGridview();
        setImageLayout();
//        //单页的任务单隐藏底部的显示
//        if (isSplitCourse) {
//            hideSinglePageLayout();
//        }
    }

    /**
     * 控制布局比例
     */
    private void setImageLayout() {

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.rl_pic_imageview);
        // //设置布局为A4比例
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        int screenWidth = com.osastudio.common.utils.Utils.getScreenWidth(getActivity());
//        int width = (screenWidth * 2/5) - 20;
//        layoutParams.width = width;
//        layoutParams.height = width * 210/297;
        layoutParams.width = screenWidth;
        layoutParams.height = screenWidth * 9 / 16;
        frameLayout.setLayoutParams(layoutParams);
//        View rightView = findViewById(R.id.layout_right_content);
//        LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) rightView.getLayoutParams();
//        layoutParams1.height = width * 210/297;
//        layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        rightView.setLayoutParams(layoutParams1);

    }

    @Override
    public void noteCommit(int shareType) {
        switch (shareType) {
            case ShareType.SHARE_TYPE_PUBLIC_COURSE:
                sendCourseToSchoolSpace(shareType);
                break;
            case ShareType.SHARE_TYPE_WECHAT:
            case ShareType.SHARE_TYPE_WECHATMOMENTS:
            case ShareType.SHARE_TYPE_QQ:
            case ShareType.SHARE_TYPE_QZONE:
            case ShareType.SHARE_TYPE_CONTACTS:
                //其实就是分享
                if (newResourceInfo != null) {
                    CourseInfo courseInfo = newResourceInfo.getCourseInfo();
                    if (courseInfo != null) {
                        ShareInfo shareInfo = courseInfo.getShareInfo(getActivity());
                        if (shareInfo != null && commitCourseHelper != null) {
                            commitCourseHelper.shareTo(shareType, shareInfo);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    public class BtnEntity {
        private String name;
        private int type;
        public static final int TYPE_CHECK_COURSE = 1;//查看
        public static final int TYPE_SHARE_COURSE = 2;//分享
        public static final int TYPE_SEND_COURSE = 3;//发送
        public static final int TYPE_VIEW_QRCODE = 4;//查看二维码
        public static final int TYPE_COLLECT = 5;//收藏

        public static final int TYPE_MAKE_ORDER = 6;//我要做任务单
        public static final int TYPE_SHARE_SCREEN = 7;//投屏
        //返回主页
        public static final int TYPE_BACK_HOME = 8;
        //单页展示
        public static final int TYPE_SPLIT_COURSE = 9;

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

    private void loadBottons() {
        View doCourseView = findViewById(R.id.rl_do_course);
        doCourseView.setOnClickListener(this);
        if (isFromChoiceLib && !VipConfig.isVip(getActivity())){
            doCourseView.setVisibility(View.GONE);
        } else {
            doCourseView.setVisibility(View.VISIBLE);
        }
        findViewById(R.id.rl_add_reading).setVisibility(View.GONE);
        TextView textView = (TextView) findViewById(R.id.tv_make_pic_book);
        //我要做任务单
        textView.setText(R.string.I_want_to_make_order);
    }

    /**
     * 发送
     */
    private void send() {
        if (commitCourseHelper != null) {

            commitCourseHelper.commit(TaskOrderDetailFragment.this.getView(), null);
        }
    }

    private void shareplayControlSyncWithShareplayStatus(int status) {
        if (sharePlayControler != null) {
            sharePlayControler.syncShareplayStatus(status);
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

    private void enterShareScreenEvent() {
        if (shareManager == null) {
            shareManager = MyShareManager.getInstance(getActivity(), handler);
        }
        if (shareManager != null) {
            if (shareManager.getSharedDevices() != null) {
                ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, true, null);
            } else {
                TipMsgHelper.ShowMsg(getActivity(), R.string.no_share_play);
            }
        }
    }

    /**
     * 我要做任务单
     */
    private void makeOrderCourse() {
        if (newResourceInfo == null) return;
        String orderId = null;
        if (isSplitCourse) {
            //当前拉取任务单分页id类型为id-type
            orderId = newResourceInfo.getResourceId();
        } else {
            orderId = newResourceInfo.getMicroId();
            if (orderId.contains("-")) {
                orderId = orderId.substring(0, orderId.indexOf('-'));
            }
        }
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        DoCourseHelper courseHelper = new DoCourseHelper(getActivity());
        courseHelper.setFromScanTask(isFromScanTask);
        courseHelper.makeOrderCourse(orderId);
    }

    /**
     * 收藏
     */
    private void doCollect() {
        if (newResourceInfo == null) {
            return;
        }
        CollectionHelper collectionHelper = new CollectionHelper(getActivity());
        String microId = newResourceInfo.getMicroId();
        if (!microId.contains("-")) {
            microId = microId + "-" + newResourceInfo.getResourceType();
        }
        collectionHelper.setIsPublicRes(newResourceInfo.isPublicResource());
        collectionHelper.setCollectSchoolId(collectionSchoolId);
        collectionHelper.setFromChoiceLib(isFromChoiceLib);
        collectionHelper.collectDifferentResource(
                microId,
                newResourceInfo.getTitle(),
                newResourceInfo.getAuthorId(),
                getString(R.string.make_task)
        );
    }

    /**
     * 任务单发送到校本资源库
     */
    private void sendCourseToSchoolSpace(int shareType) {
        CourseData courseData = null;
        UserInfo userInfo = getUserInfo();
        if (userInfo == null) return;
        courseData = new CourseData();
        if (!TextUtils.isEmpty(newResourceInfo.getMicroId())) {
            String microId = newResourceInfo.getMicroId();
            int type, index, id;
            if (microId.contains("-")) {
                index = microId.indexOf("-");
                id = Integer.parseInt(microId.substring(0, index));
                type = Integer.parseInt(microId.substring(index + 1, microId.length()));
            } else {
                id = Integer.parseInt(microId);
                type = newResourceInfo.getResourceType();
            }
            courseData.id = id;
            courseData.type = type;
            courseData.nickname = newResourceInfo.getTitle();
            courseData.resourceurl = newResourceInfo.getResourceUrl();
            courseData.code = newResourceInfo.getAuthorId();
            courseData.description = newResourceInfo.getDescription();
            courseData.createname = newResourceInfo.getAuthorName();
            courseData.thumbnailurl = newResourceInfo.getThumbnail();
            courseData.screentype = newResourceInfo.getScreenType();
            courseData.size = newResourceInfo.getFileSize();
        }
        String courseName = null;
        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setTempData(true);
        courseName = courseData.nickname;
        uploadParameter.setType(courseData.type);
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCourseData(courseData);
        uploadParameter.setType(shareType);
        uploadParameter.setCourseData(courseData);
        CommitCourseHelper commitCourseHelper = new CommitCourseHelper(getActivity());
        commitCourseHelper.enterContactsPicker(uploadParameter, courseName, null, shareType);
    }

    private void initTaskGridview() {
        GridView gridView = (GridView) findViewById(R.id.book_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_TASK_PER_ROW);
            AdapterViewHelper adapterViewHelper = new NewResourcePadAdapterViewHelper(
                    getActivity(), gridView) {
                @Override
                public void loadData() {
                    loadTaskDatas();
                }


                @Override
                public void onItemClick(AdapterView parent, View view, int position, long id) {
                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (holder == null) {
                        return;
                    }
                    NewResourceInfo data = (NewResourceInfo) holder.data;
                    if (data == null) return;
                    ActivityUtils.openOnlineOnePage(getActivity(), data, true, null);
                }
            };
            setCurrAdapterViewHelper(gridView, adapterViewHelper);
        }
    }

    private void loadTaskDatas() {
        if (newResourceInfo == null) return;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pid", newResourceInfo.getMicroId() + "-23");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, ServerUrl.GET_GUIDANCE_CARD_SPLIT_URL + builder.toString(), new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                parseData(jsonString);
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowMsg(getActivity(), getString(R.string.network_error));
            }

            @Override
            public void onFinish() {
                if (getActivity() == null) {
                    return;
                }
                super.onFinish();
                dismissLoadingDialog();
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    private void parseData(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject != null) {
                    int code = jsonObject.optInt("code");
                    if (code == 0) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray != null) {
                            List<TaskOrderNewResourceInfo> datas = JSON.parseArray(
                                    jsonArray.toString(), TaskOrderNewResourceInfo.class);
                            if (datas != null && datas.size() > 0) {
                                List<NewResourceInfo> items = new ArrayList<NewResourceInfo>();
                                for (TaskOrderNewResourceInfo tempInfo : datas) {
                                    NewResourceInfo item = TaskOrderNewResourceInfo.pase2NewResourceInfo(tempInfo);
                                    items.add(item);
                                }
                                getCurrAdapterViewHelper().setData(items);
                            } else {
                                if (getCurrAdapterViewHelper().hasData()) {
                                    getCurrAdapterViewHelper().clearData();
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void loadDatas() {
        loadBottons();
        if (isSplitCourse) {
            loadSplitCourseDetail();
        } else {
            loadCourseDetail();
//            loadTaskDatas();
        }
    }

    /**
     * 拉取任务详情
     */
    public void loadCourseDetail() {

        if (newResourceInfo == null) {
            return;
        }
        final JSONObject jsonObject = new JSONObject();
        try {
            String resourceId = newResourceInfo.getResourceId();
            if (!TextUtils.isEmpty(resourceId)) {
                jsonObject.put("resId", resourceId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("?j=" + jsonObject.toString());
        String url = ServerUrl.WAWATV_COURSE_DETAIL_URL + builder.toString();
        ThisStringRequest request = new ThisStringRequest(
                Request.Method.GET, url, new Listener<String>() {
            @Override
            public void onSuccess(String jsonString) {
                if (getActivity() == null) {
                    return;
                }
                if (jsonString != null) {
                    CourseUploadResult uploadResult = JSON.parseObject(
                            jsonString,
                            CourseUploadResult.class);
                    if (uploadResult != null && uploadResult.code == 0) {
                        CourseData courseData = uploadResult.getData().get(0);
                        if (courseData != null) {
                            NewResourceInfo info = courseData.getNewResourceInfo();
                            if (info != null) {
                                //重新赋值
                                newResourceInfo = info;
                                updateImageView();
                            }
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
                    }
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }

    /**
     * 加载拆分的数据
     */
    private void loadSplitCourseDetail() {
        if (newResourceInfo == null) return;
        String splitId = newResourceInfo.getMicroId();
        if (TextUtils.isEmpty(splitId)) {
            return;
        }
        if (splitId.contains("-")) {
            splitId = splitId.substring(0, splitId.indexOf('-'));
        }
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(getActivity());
        wawaCourseUtils.loadSplitCourseDetail(Long.valueOf(splitId));
        wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    CourseData courseData = info.getCourseData();
                    if (courseData != null) {
                        NewResourceInfo newInfo = courseData.getNewResourceInfo();
                        if (info != null) {
                            //重新赋值
                            newResourceInfo = newInfo;
                            updateImageView();
                        }
                    }
                }
            }
        });
    }

    private void checkResPermission() {
        //如果是空中课堂和校本资源库进来权限保护
        boolean flag = isCollection && !TextUtils.isEmpty(collectionSchoolId);
        if (!isHasPermission) {
            if (isFromAirClass || isFromSchoolResource || flag) {
                newResourceInfo.setIsPublicResource(isVipSchool);
            }
        }
    }

    private void updateImageView() {
        loadAuthorUserInfo();
        initRightButton();
        setCommentNum(newResourceInfo.getCommentNumber());
        checkResPermission();
        ImageView imageView = (ImageView) findViewById(R.id.pic_book_imageview);
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            if (newResourceInfo != null) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(newResourceInfo.getThumbnail()), imageView,
                        R.drawable.default_cover);
            } else {
                imageView.setImageResource(R.drawable.default_book_cover);
            }
        }

        TextView textView = (TextView) findViewById(R.id.pic_book_author_textview);
        if (textView != null) {
            if (newResourceInfo != null) {
                textView.setText(newResourceInfo.getAuthorName());
            }
        }
    }

    private void updateReadNum() {
        String commentCount = getString(R.string.comment_person,
                String.valueOf(newResourceInfo.getCommentNumber()));
        readContTextView.setText(new StringBuilder().append(newResourceInfo.getReadNumber())
                .append(getString(R.string.read_person))
                .append('·').append(commentCount));
    }

    public void setCommentNum(int commentNum) {
        if (isSplitCourse) {
            return;
        }
        newResourceInfo.setCommentNumber(commentNum);
        if (mCommentFragment != null && mCommentFragment.getCurrentCommentNum() < 5){
            mCommentFragment.loadComments();
        }
        updateReadNum();
    }

    @Override
    public void
    onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                finish();
                break;
            case R.id.pic_book_imageview:
            case R.id.iv_play_course:
                if (newResourceInfo == null) return;
                ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, false, null);
                break;
            case R.id.contacts_header_right_ico:
                // TODO: 2017/12/25  弹窗
                showPopwindow();
                break;
            case R.id.open_consultion_view:
                CustomerServiceActivity.start(getActivity(), CustomerServiceActivity.SOURCE_TYPE_OPEN_CONSULTION);
                break;
            case R.id.iv_qr_code:
                //查看二维码
                Utils.showViewQrCodeDialog(getActivity(), newResourceInfo,
                        null);
                break;
            case R.id.rl_do_course:
                //做课件
                makeOrderCourse();
                break;
            case R.id.iv_share:
                //分享 或者 发送
                shareDifferentCourse();
                break;

            case R.id.iv_collect:
                //收藏
                doCollect();
                break;
            case R.id.ll_message:
                //进入更多评论的界面
                enterCommentDetail();
                break;
            default:
                break;
        }
    }

    /**
     * 进入评论的详情页
     */
    private void enterCommentDetail() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        CommentDetailFragment commentDetailFragment = CommentDetailFragment.newInstance("",
                newResourceInfo);
        ft.add(R.id.activity_body, commentDetailFragment, commentDetailFragment.getTag());
        ft.hide(this);
        ft.show(commentDetailFragment);
        ft.commit();
        ft.addToBackStack(null);
    }

    private void showPopwindow() {
        View view = findViewById(R.id.contacts_header_right_ico);
        if (mPopWindow == null) {
            View contentView = LayoutInflater.from(getContext()).inflate(R.layout.pop_menu, null);
            contentView.setBackgroundResource(R.drawable.pop_menu_bg);
            //处理popWindow 显示内容
            handleLogic(contentView, true);

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
    private boolean handleLogic(View contentView, boolean isShowPop) {
        EntryBean data = null;
        final List<EntryBean> items = new ArrayList<>();


        //只有来自个人是发送，其余均是分享。二者是互斥的。
//        if (isFromLqTask) {
//            UserInfo userInfo = getUserInfo();
//            if (userInfo != null && newResourceInfo != null) {
//                if (getMemeberId().equals(newResourceInfo.getAuthorId())) {
//                    //当前版本修改为只要当作者自己 显示发送按钮
//                    data = new EntryBean(R.drawable.icon_share,
//                            getString(R.string.button_send), BtnEntity.TYPE_SEND_COURSE);
//                    items.add(data);
//                } else {
//                    //其余身份为“分享”。
//                    data = new EntryBean(R.drawable.icon_share,
//                            getString(R.string.share), BtnEntity.TYPE_SHARE_COURSE);
//                    items.add(data);
//                }
//            }
//        } else {
//            //来自其余地方均是分享
//            data = new EntryBean(R.drawable.icon_share,
//                    getString(R.string.share), BtnEntity.TYPE_SHARE_COURSE);
//            items.add(data);
//        }

//        if (!(fromType == FROM_MY_WORK || fromType == FROM_MY_DOWNLOAD)) {
//            boolean flag = (mParam != null && mParam.isFromLQMOOC) || isHideCollectBtn;
//            if (!flag) {
//                if (isFromSchoolResource && !isSplitCourse) {
//                    //收藏
//                    data = new EntryBean(R.drawable.icon_collect,
//                            getString(R.string.collection), BtnEntity.TYPE_COLLECT);
//                    items.add(data);
//                }
//            }
//        }

        //投屏
//            data = new EntryBean(R.drawable.icon_share_screen,
//                    getString(R.string.sharescreen), BtnEntity.TYPE_SHARE_SCREEN);
//            items.add(data);

        if (!isSplitCourse) {
            //单页展示
            data = new EntryBean(R.drawable.icon_show_splict_course,
                    getString(R.string.str_single_page_show), BtnEntity.TYPE_SPLIT_COURSE);
            items.add(data);
        }

        if (isFromSchoolResource && !isFromAirClass) {
            //返回主页
            data = new EntryBean(R.drawable.icon_back_home,
                    getString(R.string.back_home), BtnEntity.TYPE_BACK_HOME);
            items.add(data);
        }
        if (items.size() <= 0) {
            return false;
        }
        if (!isShowPop) {
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
                switch (data.id) {
                    //投屏
                    case BtnEntity.TYPE_SHARE_SCREEN:
                        enterShareScreenEvent();
                        break;
                    //分享
                    case BtnEntity.TYPE_SHARE_COURSE:
                        shareCourse();
                        break;
                    //发送
                    case BtnEntity.TYPE_SEND_COURSE:
                        send();
                        break;

                    //收藏
                    case BtnEntity.TYPE_COLLECT:
                        doCollect();
                        break;

                    //返回主页
                    case BtnEntity.TYPE_BACK_HOME:
                        backHome();
                        break;
                    //单页展示
                    case BtnEntity.TYPE_SPLIT_COURSE:
                        enterSplitCourseDetail();
                        break;
                    default:
                        break;
                }
            }
        });
        return true;
    }

    /**
     * 进入单页展示的详情页
     */
    private void enterSplitCourseDetail(){
        if (mParam != null && mParam.isFromLQMOOC && mParam.isAudition){
            TipMsgHelper.ShowMsg(getActivity(),R.string.buy_course_please);
            return;
        }
        if (newResourceInfo != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), SplitCourseListActivity.class);
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_ID, newResourceInfo.getMicroId());
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_NAME, newResourceInfo.getTitle());
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_IS_TASKORDER_SPLIT_DETAIL,true);
            startActivity(intent);
        }
    }

    private void handleHeadViewVisible() {
        LinearLayout messageLayout = (LinearLayout) findViewById(R.id.ll_message);
        LinearLayout courseUseWay = (LinearLayout) findViewById(R.id.ll_course_use_way);
        LinearLayout readDetailLayout = (LinearLayout) findViewById(R.id.ll_course_detail);
        if (isSplitCourse) {
            readDetailLayout.setVisibility(View.GONE);
            messageLayout.setVisibility(View.GONE);
        } else {
            readDetailLayout.setVisibility(View.VISIBLE);
            messageLayout.setVisibility(View.VISIBLE);
            messageLayout.setOnClickListener(this);
        }
        if (fromType == FROM_MY_WORK || fromType == FROM_MY_DOWNLOAD) {
            courseUseWay.setVisibility(View.GONE);
            return;
        } else {
            courseUseWay.setVisibility(View.VISIBLE);
        }
        ImageView collectImage = (ImageView) findViewById(R.id.iv_collect);
        ImageView shareImage = (ImageView) findViewById(R.id.iv_share);
        ImageView qrImageView = (ImageView) findViewById(R.id.iv_qr_code);
        //收藏
        boolean flag = (mParam != null && mParam.isFromLQMOOC) || isHideCollectBtn;
        if (!flag) {
            if (isFromSchoolResource && !isSplitCourse) {
                collectImage.setVisibility(View.VISIBLE);
                collectImage.setOnClickListener(this);
            }
        }

        if (!(fromType == FROM_MY_WORK || fromType == FROM_MY_DOWNLOAD)) {
             if (isFromChoiceLib && !VipConfig.isVip(getActivity())){
                qrImageView.setVisibility(View.GONE);
            } else {
                qrImageView.setOnClickListener(this);
                qrImageView.setVisibility(View.VISIBLE);
            }
        } else {
            qrImageView.setVisibility(View.GONE);
        }
        shareImage.setVisibility(View.VISIBLE);
        shareImage.setOnClickListener(this);
    }

    private void initCommentData() {
        if (isSplitCourse) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        mCommentFragment = PictureBooksDetailItemFragment
                .newInstance(true, "", newResourceInfo);
        ft.add(R.id.frame_layout, mCommentFragment, mCommentFragment.getTag());
        ft.commit();
    }

    private void initRightButton(){
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_right_ico);
        if (imageView != null) {
            imageView.setBackgroundResource(R.drawable.icon_plus_white);
            if (handleLogic(null, false)) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
            imageView.setOnClickListener(this);
        }
    }

    private void loadAuthorUserInfo() {
        if (fromType == FROM_MY_WORK || fromType == FROM_MY_DOWNLOAD) {
            return;
        }
        if (newResourceInfo == null || TextUtils.isEmpty(newResourceInfo.getAuthorId())) {
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
                        if (authorInfo != null && authorIconImageView != null) {
                            MyApplication.getThumbnailManager(getActivity()).displayUserIcon
                                    (AppSettings.getFileUrl(authorInfo.getHeaderPic()),
                                            authorIconImageView);
                        }
                    }
                });
    }

    private void loadUserInfoData(){
        UserInfoHelper userInfoHelper = new UserInfoHelper(getActivity());
        userInfoHelper.setCallBackListener((obj) ->{
            if (obj != null && obj instanceof UserInfo){
                UserInfo info = (UserInfo) obj;
                if (commitCourseHelper != null){
                    commitCourseHelper.setIsTeacher(Utils.isRealTeacher(info.getSchoolList()));
                }
            }
        });
        userInfoHelper.check();
    }

    //返回主页
    private void backHome() {
        if (getActivity() != null) {
            ActivityStack activityStack = ((MyApplication) (getActivity().getApplication())).getActivityStack();
            if (activityStack != null) {
                activityStack.finishUtil(HomeActivity.class);
            }
        }
    }

    private void shareDifferentCourse(){
        if (isFromLqTask) {
            UserInfo userInfo = getUserInfo();
            if (userInfo != null && newResourceInfo != null) {
                if (getMemeberId().equals(newResourceInfo.getAuthorId())) {
                    //当前版本修改为只要当作者自己 显示发送按钮
                    send();
                } else {
                    //其余身份为“分享”。
                    shareCourse();
                }
            }
        } else {
            shareCourse();
        }
    }

    protected void shareCourse() {
        if (newResourceInfo != null) {
            CourseInfo courseInfo = newResourceInfo.getCourseInfo();
            if (courseInfo != null) {
                ShareInfo shareInfo = courseInfo.getShareInfo(getActivity());
                shareInfo.setSharedResource(courseInfo.getSharedResource());
                if (shareInfo != null) {
                    new ShareUtils(getActivity()).share(TaskOrderDetailFragment.this.getView(), shareInfo);
                }
            }
        }
    }

    /**
     * 如果是任务单的单页课件 隐藏单页展示的模块
     */
    private void hideSinglePageLayout() {
        findViewById(R.id.contacts_pull_to_refresh).setVisibility(View.GONE);
        findViewById(R.id.single_page_text_layout).setVisibility(View.GONE);
        findViewById(R.id.view_space).setVisibility(View.GONE);
    }
}
