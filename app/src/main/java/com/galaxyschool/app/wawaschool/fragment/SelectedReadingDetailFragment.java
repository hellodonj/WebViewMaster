package com.galaxyschool.app.wawaschool.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Request;
import com.galaxyschool.app.wawaschool.MediaMainActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.MyDownloadListActivity;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SplitCourseListActivity;
import com.galaxyschool.app.wawaschool.WawaCourseChoiceActivity;
import com.galaxyschool.app.wawaschool.bitmapmanager.Md5FileNameGenerator;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckReplaceIPAddressHelper;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.ColorUtil;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.LogUtils;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.UploadUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.course.CacheCourseImagesTask;
import com.galaxyschool.app.wawaschool.course.CopyCourseTask;
import com.galaxyschool.app.wawaschool.course.DownloadOnePageTask;
import com.galaxyschool.app.wawaschool.course.SlideActivityNew;
import com.galaxyschool.app.wawaschool.course.library.ImportImage;
import com.galaxyschool.app.wawaschool.db.DownloadCourseDao;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.DownloadCourseDTO;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandDataAdapter;
import com.galaxyschool.app.wawaschool.fragment.library.ExpandListViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.fragment.resource.ImportImageTask;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.lqbaselib.net.ThisStringRequest;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseImageListResult;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.HomeworkChildListResult;
import com.galaxyschool.app.wawaschool.pojo.HomeworkCommitObjectResult;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.StudentMemberInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTask;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskCommentDiscussPersonResult;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.StudytaskComment;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.MaterialType;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.CreateSlideHelper;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.MyGridView;
import com.galaxyschool.app.wawaschool.views.NoDoubleClickListener;
import com.lqwawa.libs.filedownloader.DownloadService;
import com.lqwawa.libs.filedownloader.FileInfo;
import com.lqwawa.tools.FileZipHelper;
import com.oosic.apps.iemaker.base.BaseSlideManager;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.SlideManager;
import com.oosic.apps.iemaker.base.data.NormalProperty;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInputParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;
import com.oosic.apps.iemaker.base.ooshare.MyShareManager;
import com.oosic.apps.share.ShareInfo;
import com.osastudio.common.library.ActivityStack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by E450 on 2016/12/23.
 */

public class SelectedReadingDetailFragment extends ContactsExpandListFragment implements
        View.OnClickListener, View.OnLayoutChangeListener {
    public interface Constants {

        public static final String NEW_RESOURCE_INFO = "NewResourceInfo";
        public static final String STUDY_TASK_INFO = "StudyTaskInfo";
        public static final String ROLE_TYPE = "RoleType";
        public static final String  STUDENT_ID = "StudentId";
        public static final String INTORDUCTION_CREATE="introduction_create";
        public static final String STUDENT_USERINFO="stuentUserInfo";
        public static final int OPERATION_TYPE_SHARESCEEN = 0;
        public static final int OPERATION_TYPE_MAKEPICBOOK = 1;
        public static final int OPERATION_TYPE_EDITCOURSE = 2;
        public static final int OPERATION_TYPE_PLAYCOURSE = 3;
        public static final int OPERATION_TYPE_GET_THUMBNAIL = 4;
    }
    private int commitDataType;
    public static final int REQUEST_CODE_SLIDE = 0;
    public static final int REQUEST_CODE_EDITCOURSE = 1;
    public static final int REQUEST_CODE_RETELLCOURSE = 2;
    public static final int CREATE_NEW_COURSE_TASK_TYPE=10;
    public static final String TAG = SelectedReadingDetailFragment.class.getSimpleName();
    private static final int MAX_BUTTON_PER_ROW = 3;
    private static final int MAX_TASK_PER_ROW = 2;
    private String gridViewTag;
    private int roleType = 0;//0老师，其他学生、家长
    private ExpandableListView expandListView;
    private EditText commentEditText;
    private TextView studentTaskDiscuss;
    private int parentId = 0;
    private String commentToId = "";
    private String commentToName = "";
    private ImageView imageViewIcon;
    private TextView courseTitle;
    //屏幕高度
    private int screenHeight = 0;

    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    private StudyTaskInfo task;
    private StudyTask studyTask;
    private NewResourceInfo newResourceInfo;
    private int screenType = 0;
    protected String savePath;
    private UserInfo userInfo;
    private UserInfo stuUserInfo;
    private String studentId;
    private static CreateSlideHelper.CreateSlideParam mCreateSlideParam;
    // 1 复述课件 2 任务单 3 提问 4 创作
    public interface CommitType{
        String  RETELL_INTRODUCTION_COURSE="1";
        String TASK_ORDER="2";
        String ASK_QUESTION="3";
        String CREATE_NEW_COURSE="4";
    }
    private static boolean hasCommented;
    private static boolean hasHomeworkUploaded;//是否提交了作业
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ImportImage.MSG_IMPORT_FINISH:

                    LocalCourseInfo localCourseInfo = (LocalCourseInfo) msg.obj;
                    if (localCourseInfo != null) {
                        if (screenType >= 0) {
                            localCourseInfo.mOrientation = screenType;
                        }
                        saveData(localCourseInfo);
                        if(studyTask != null) {
                            localCourseInfo.mOriginVoicePath = studyTask.getResUrl();
                            if (localCourseInfo.mOriginVoicePath != null
                                    && localCourseInfo.mOriginVoicePath.contains(".zip")) {
                                //截取字符串
                                localCourseInfo.mOriginVoicePath
                                        = localCourseInfo.mOriginVoicePath.substring(0,
                                        localCourseInfo.mOriginVoicePath.indexOf(".zip"));
                            }
                        }
                        enterSlideNewRetellCourse(localCourseInfo);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private View commonSendLayout;//发送布局

    //头部布局
    private View headerView;
    private ImageView homeworkIcon;
    private ImageView mediaCover;//播放按钮
    private TextView homeworkTitle;
    private TextView assignTime;
    private TextView finishTime;
    private TextView finishStatus;
    private TextView accessDetails;//查阅详情
    private MyShareManager shareManager;
    private int taskType = -1;

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            replaySuccessOrGiveup();//收起键盘时，评论回复的参数要重置回去，commentEditText的text
            // 和hint也要重置回去
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selected_reading_detail, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadIntentData();
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDatas();

    }

    private void loadIntentData() {
        Intent intent = getActivity().getIntent();
        newResourceInfo = intent.getParcelableExtra(Constants.NEW_RESOURCE_INFO);
        task = (StudyTaskInfo) intent.getSerializableExtra(Constants.STUDY_TASK_INFO);
        if (task != null){
            taskType = task.getTaskType();
        }
        roleType = intent.getIntExtra(Constants.ROLE_TYPE, 0);
        studentId= intent.getStringExtra(Constants.STUDENT_ID);
        stuUserInfo= (UserInfo) intent.getSerializableExtra(Constants.STUDENT_USERINFO);
    }

    /**
     * 获得标题
     * @return
     */
    private String getTaskNameByType(){
        String title = "";
        if (task != null){
            int type = task.getTaskType();
            if (type == StudyTaskType.INTRODUCTION_WAWA_COURSE){
                title = getString(R.string.selected_reading);
            }else if (type == StudyTaskType.TASK_ORDER){
                title = getString(R.string.do_task);
            }
        }
        return title;
    }

    private void initViews() {
        //Bar条title
        TextView textView = (TextView) findViewById(R.id.contacts_header_title);
        if (textView != null) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getTaskNameByType());
        }
        //Bar条返回按钮
        ImageView imageView = (ImageView) findViewById(R.id.contacts_header_left_btn);
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(this);
        }
        //课件title
        courseTitle = (TextView) findViewById(R.id.course_title_view);
        //课件的缩略图
        imageViewIcon = (ImageView) findViewById(R.id.course_image_view);
        if (imageViewIcon != null) {
            imageViewIcon.setOnClickListener(this);
            if (newResourceInfo != null) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(newResourceInfo.getThumbnail()), imageViewIcon,
                        R.drawable.default_book_cover);
            } else {
                imageViewIcon.setImageResource(R.drawable.default_book_cover);
            }
        }
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.task_layout);
        if (layout != null) {
            LinearLayout.LayoutParams layoutParams=( LinearLayout.LayoutParams)layout.getLayoutParams();
            layoutParams.width= (ScreenUtils.getScreenWidth(getActivity())*1/2-getResources()
                    .getDimensionPixelSize(R.dimen.separate_20dp)*2);
            layoutParams.height=layoutParams.width*3/5;
            layout.setOnClickListener(this);
        }
        initHeaderView();
        //发送布局
        commonSendLayout = findViewById(R.id.layout_common_send);
        if (commonSendLayout != null){
            commonSendLayout.setVisibility(View.GONE);
        }
        commentEditText = (EditText) findViewById(R.id.edit_btn);
        TextView sendBtn = (TextView) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(this);
        //学生任务描述
        studentTaskDiscuss= (TextView) findViewById(R.id.student_task_content);
        initButtonridview();
        initExpandListView();
    }

    /**
     * 初始化头布局
     */
    private void initHeaderView() {
        //头布局
        headerView = findViewById(R.id.layout_assign_homework);
        //作业图片
        homeworkIcon = (ImageView) headerView.findViewById(R.id.iv_icon);
        if (homeworkIcon != null){
            homeworkIcon.setOnClickListener(this);
            if (newResourceInfo != null) {
                getThumbnailManager().displayUserIconWithDefault(
                        AppSettings.getFileUrl(newResourceInfo.getThumbnail()), homeworkIcon,
                        R.drawable.default_book_cover);
            } else {
                homeworkIcon.setImageResource(R.drawable.default_book_cover);
            }
        }
        //播放按钮
        mediaCover = (ImageView) headerView.findViewById(R.id.media_cover);
        if (mediaCover != null){
            //默认显示
            mediaCover.setVisibility(View.VISIBLE);
        }

        //作业标题
        homeworkTitle = (TextView) headerView.findViewById(R.id.tv_title);

        //布置时间
        assignTime = (TextView) headerView.findViewById(R.id.tv_start_time);

        //完成时间
        finishTime = (TextView) headerView.findViewById(R.id.tv_end_time);
        //查阅详情
        accessDetails = (TextView) headerView.findViewById(R.id.tv_access_details);
        if (accessDetails != null){
            accessDetails.setVisibility(View.GONE);
        }
        //完成状态仅对老师显示
        finishStatus = (TextView) headerView.findViewById(R.id.tv_finish_status);
        if (finishStatus != null) {
            finishStatus.setVisibility(View.GONE);
        }
    }

    public class BtnEntity {
        private String name;
        private int type;
        public static final int TYPE_CHECK_COURSE = 1;//看课件
        public static final int TYPE_RETELL_COURSE = 2;//复述课件
        public static final int TYPE_MAKE_TASK = 3;//做任务
        public static final int TYPE_QUESTION_COURSE = 4;//提问
        public static final int TYPE_CREATE_COURSE = 5;//创作
        public static final int TYPE_DOWNLOAD_COURSE = 6;//下载
        public static final int TYPE_SHARE_COURSE = 7;//分享
        public static final int TYPE_SPLIT_COURSE = 8;//查看分页
        public static final int TYPE_CHECK_TASK = 9;//看任务
        public static final int TYPE_VIEW_QRCODE = 10;//查看二维码
        public static final int TYPE_SHARE_SCREEN = 11 ; // 投屏
        public static final int TYPE_COLLECT = 12 ; // 收藏

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
     * 判断是否是单页
     * @return
     */
    private boolean isSplitCourse(){
        if (newResourceInfo != null){
            int resType = newResourceInfo.getResourceType();
            if (resType > ResType.RES_TYPE_BASE){
                //是单页
                return true;
            }
        }
        return false;
    }

    private void loadBottons() {
        //导读
        if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE) {
            if (getActivity() == null) {
                return;
            }
            List<BtnEntity> entities = new ArrayList<BtnEntity>();
            BtnEntity entity = null;
            if (roleType == 0) {
                //看任务
                entity = new BtnEntity();
                entity.setName(getString(R.string.watch_task)); //查看任务单
                entity.setType(BtnEntity.TYPE_CHECK_TASK);
                entities.add(entity);

                //看课件
//            entity = new BtnEntity();
//            entity.setName(getString(R.string.look_through_courseware));
//            entity.setType(BtnEntity.TYPE_CHECK_COURSE);
//            entities.add(entity);

                //分享
                entity = new BtnEntity();
                entity.setName(getString(R.string.share));
                entity.setType(BtnEntity.TYPE_SHARE_COURSE);
                entities.add(entity);

                //投屏
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.sharescreen));
//                entity.setType(BtnEntity.TYPE_SHARE_SCREEN);
//                entities.add(entity);

                //收藏
                entity = new BtnEntity();
                entity.setName(getString(R.string.collection));
                entity.setType(BtnEntity.TYPE_COLLECT);
                entities.add(entity);

                //下载
                entity = new BtnEntity();
                entity.setName(getString(R.string.download));
                entity.setType(BtnEntity.TYPE_DOWNLOAD_COURSE);
                entities.add(entity);

                if (!isSplitCourse()) {
                    //查看分页
                    entity = new BtnEntity();
                    entity.setName(getString(R.string.show_split_course));
                    entity.setType(BtnEntity.TYPE_SPLIT_COURSE);
                    entities.add(entity);
                }

                //查看二维码
                entity = new BtnEntity();
                entity.setName(getString(R.string.view_qrcode));
                entity.setType(BtnEntity.TYPE_VIEW_QRCODE);
                entities.add(entity);
            } else {
//            BtnEntity entity = null;
                //做任务
                entity = new BtnEntity();
                entity.setName(getString(R.string.do_task)); //做任务单
                entity.setType(BtnEntity.TYPE_MAKE_TASK);
                entities.add(entity);

                //复述课件
                entity = new BtnEntity();
                entity.setName(getString(R.string.retell_course));
                entity.setType(BtnEntity.TYPE_RETELL_COURSE);
                entities.add(entity);
                //提问
                entity = new BtnEntity();
                entity.setName(getString(R.string.question));
                entity.setType(BtnEntity.TYPE_QUESTION_COURSE);
                entities.add(entity);
                //创作
                entity = new BtnEntity();
                entity.setName(getString(R.string.create_sth));
                entity.setType(BtnEntity.TYPE_CREATE_COURSE);
                entities.add(entity);

//                //看课件
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.look_through_courseware));
//                entity.setType(BtnEntity.TYPE_CHECK_COURSE);
//                entities.add(entity);
                //分享
                entity = new BtnEntity();
                entity.setName(getString(R.string.share));
                entity.setType(BtnEntity.TYPE_SHARE_COURSE);
                entities.add(entity);

                //投屏
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.sharescreen));
//                entity.setType(BtnEntity.TYPE_SHARE_SCREEN);
//                entities.add(entity);

                //收藏
                entity = new BtnEntity();
                entity.setName(getString(R.string.collection));
                entity.setType(BtnEntity.TYPE_COLLECT);
                entities.add(entity);

                //下载
                entity = new BtnEntity();
                entity.setName(getString(R.string.download));
                entity.setType(BtnEntity.TYPE_DOWNLOAD_COURSE);
                entities.add(entity);

                if (!isSplitCourse()) {
                    //查看分页
                    entity = new BtnEntity();
                    entity.setName(getString(R.string.show_split_course));
                    entity.setType(BtnEntity.TYPE_SPLIT_COURSE);
                    entities.add(entity);
                }

                //查看二维码
                entity = new BtnEntity();
                entity.setName(getString(R.string.view_qrcode));
                entity.setType(BtnEntity.TYPE_VIEW_QRCODE);
                entities.add(entity);

            }

            getAdapterViewHelper(gridViewTag).setData(entities);
        }else {
            //其他类型
            loadOtherStudyTaskButtons();
        }
    }

    /**
     * 加载其他学习任务逻辑
     */
    private void loadOtherStudyTaskButtons(){

        if(getActivity()==null){
            return;
        }
        List<BtnEntity> entities = new ArrayList<BtnEntity>();
        BtnEntity entity = null;


        if (roleType == 0) {
            //看任务
            entity = new BtnEntity();
            entity.setName(getString(R.string.watch_task)); //查看任务单
            entity.setType(BtnEntity.TYPE_CHECK_TASK);
            entities.add(entity);

            //看课件
//            entity = new BtnEntity();
//            entity.setName(getString(R.string.look_through_courseware));
//            entity.setType(BtnEntity.TYPE_CHECK_COURSE);
//            entities.add(entity);
//                //下载
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.download));
//                entity.setType(BtnEntity.TYPE_DOWNLOAD_COURSE);
//                entities.add(entity);
//
//                //投屏
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.sharescreen));
//                entity.setType(BtnEntity.TYPE_SHARE_SCREEN);
//                entities.add(entity);

            //分享
            entity = new BtnEntity();
            entity.setName(getString(R.string.share));
            entity.setType(BtnEntity.TYPE_SHARE_COURSE);
            entities.add(entity);

            //投屏
//            entity = new BtnEntity();
//            entity.setName(getString(R.string.sharescreen));
//            entity.setType(BtnEntity.TYPE_SHARE_SCREEN);
//            entities.add(entity);

            //收藏
            entity = new BtnEntity();
            entity.setName(getString(R.string.collection));
            entity.setType(BtnEntity.TYPE_COLLECT);
            entities.add(entity);

//                if (!isSplitCourse()) {
//                    //查看分页
//                    entity = new BtnEntity();
//                    entity.setName(getString(R.string.show_split_course));
//                    entity.setType(BtnEntity.TYPE_SPLIT_COURSE);
//                    entities.add(entity);
//                }

            //查看二维码
            entity=new BtnEntity();
            entity.setName(getString(R.string.view_qrcode));
            entity.setType(BtnEntity.TYPE_VIEW_QRCODE);
            entities.add(entity);
        } else {
//            BtnEntity entity = null;
            //做任务
            entity = new BtnEntity();
            entity.setName(getString(R.string.do_task)); //做任务单
            entity.setType(BtnEntity.TYPE_MAKE_TASK);
            entities.add(entity);

//                //复述课件
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.retell_wawa_course));
//                entity.setType(BtnEntity.TYPE_RETELL_COURSE);
//                entities.add(entity);
//                //提问
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.question));
//                entity.setType(BtnEntity.TYPE_QUESTION_COURSE);
//                entities.add(entity);
//                //创作
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.create_sth));
//                entity.setType(BtnEntity.TYPE_CREATE_COURSE);
//                entities.add(entity);
//
//                //看课件
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.look_through_courseware));
//                entity.setType(BtnEntity.TYPE_CHECK_COURSE);
//                entities.add(entity);
//
//                //投屏
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.sharescreen));
//                entity.setType(BtnEntity.TYPE_SHARE_SCREEN);
//                entities.add(entity);
//
//                //下载
//                entity = new BtnEntity();
//                entity.setName(getString(R.string.download));
//                entity.setType(BtnEntity.TYPE_DOWNLOAD_COURSE);
//                entities.add(entity);
            //分享
            entity = new BtnEntity();
            entity.setName(getString(R.string.share));
            entity.setType(BtnEntity.TYPE_SHARE_COURSE);
            entities.add(entity);

            //投屏
//            entity = new BtnEntity();
//            entity.setName(getString(R.string.sharescreen));
//            entity.setType(BtnEntity.TYPE_SHARE_SCREEN);
//            entities.add(entity);

            //收藏
            entity = new BtnEntity();
            entity.setName(getString(R.string.collection));
            entity.setType(BtnEntity.TYPE_COLLECT);
            entities.add(entity);

//                if (!isSplitCourse()) {
//                    //查看分页
//                    entity = new BtnEntity();
//                    entity.setName(getString(R.string.show_split_course));
//                    entity.setType(BtnEntity.TYPE_SPLIT_COURSE);
//                    entities.add(entity);
//                }

            //查看二维码
            entity=new BtnEntity();
            entity.setName(getString(R.string.view_qrcode));
            entity.setType(BtnEntity.TYPE_VIEW_QRCODE);
            entities.add(entity);

        }

        getAdapterViewHelper(gridViewTag).setData(entities);
    }

    private void initButtonridview() {
        MyGridView gridView = (MyGridView) findViewById(R.id.botton_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(MAX_BUTTON_PER_ROW);
            AdapterViewHelper gridViewHelper = new AdapterViewHelper(getActivity(),
                    gridView, R.layout.item_pic_book_detail_button) {
                @Override
                public void loadData() {
                    loadBottons();
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    final BtnEntity data = (BtnEntity) getDataAdapter().getItem(position);
                    if (data == null) {
                        return view;
                    }
                    TextView textView = (TextView) view.findViewById(R.id.unkonw_btn);
                    if (textView != null) {
                        textView.setText(data.getName());
                        if(data.getType()==BtnEntity.TYPE_CHECK_TASK||data.getType()==BtnEntity.TYPE_MAKE_TASK ){
                            if (taskType == StudyTaskType.TASK_ORDER) {
                                textView.setBackgroundResource(R.drawable.green_10dp_white);
                                textView.setTextColor(getResources().getColor(R.color.text_green));
                            } else {
                                if(studyTask!=null&&!TextUtils.isEmpty(studyTask.getWorkOrderId())){
                                    textView.setBackgroundResource(R.drawable.green_10dp_white);
                                    textView.setTextColor(getResources().getColor(R.color.text_green));
                                }else{
                                    textView.setBackgroundResource(R.drawable.gray_10dp_gray);
                                    textView.setTextColor(getResources().getColor(R.color.gray));
                                }
                            }

                        }else{
                            textView.setBackgroundResource(R.drawable.green_10dp_white);
                            textView.setTextColor(getResources().getColor(R.color.text_green));
                        }
                        textView.setOnClickListener(new NoDoubleClickListener() {
                            @Override
                            public void onNoDoubleClick(View v) {
                                switch (data.getType()) {
                                    case BtnEntity.TYPE_CHECK_COURSE:
                                        //看课件
                                        if(newResourceInfo==null)return;
                                        ActivityUtils.playOnlineCourse(getActivity(),
                                                newResourceInfo.getCourseInfo(), false,
                                                null);
                                        break;
                                    case BtnEntity.TYPE_CHECK_TASK:
                                        //看任务
                                        if (taskType == StudyTaskType.TASK_ORDER) {
                                            takeTask(true);
                                        } else {
                                            if(studyTask!=null&&!TextUtils.isEmpty(studyTask.getWorkOrderId())){
                                                takeTask(false);
                                            }else{
                                                TipsHelper.showToast(getActivity(),getString(R.string
                                                        .no_task_order));
                                            }
                                        }

                                        break;
                                    case BtnEntity.TYPE_SHARE_COURSE:
                                        //分享
                                        shareCourse();
                                        break;
                                    case BtnEntity.TYPE_CREATE_COURSE:
                                        //創作
                                        createCourse();
                                        break;
                                    case BtnEntity.TYPE_DOWNLOAD_COURSE:
                                        //下载
                                        enterDownLoadEvent();
                                        break;
                                    case BtnEntity.TYPE_QUESTION_COURSE:
                                        //提问
                                        askQuestion();
                                        break;
                                    case BtnEntity.TYPE_RETELL_COURSE:
                                        //复述课件
                                        retellTaskCourse();
                                        break;
                                    case BtnEntity.TYPE_SPLIT_COURSE:
                                        //查看分页
                                        enterSplitCourseList();
                                        break;
                                    case BtnEntity.TYPE_MAKE_TASK:
                                        //做任务
                                        if (taskType == StudyTaskType.TASK_ORDER) {
                                            takeTask(true);
                                        } else {
                                            if (studyTask != null && !TextUtils.isEmpty(studyTask.getWorkOrderId())) {
                                                takeTask(false);
                                            } else {
                                                TipsHelper.showToast(getActivity(), getString(R.string
                                                        .no_task_order));
                                            }
                                        }
                                        break;

                                    case BtnEntity.TYPE_VIEW_QRCODE:
                                        //查看二维码
                                        Utils.showViewQrCodeDialog(getActivity(),newResourceInfo,
                                                null);
                                        break;

                                    case BtnEntity.TYPE_SHARE_SCREEN:
                                        //投屏
                                        enterShareScreenEvent();
                                        break;
                                    case BtnEntity.TYPE_COLLECT:
                                        //收藏
                                        doCollect();
                                        break;

                                }
                            }
                        });
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
                }
            };
            this.gridViewTag = String.valueOf(gridView.getId());
            addAdapterViewHelper(this.gridViewTag, gridViewHelper);
        }
    }

    /**
     * 收藏
     */
    private void doCollect() {
        if (newResourceInfo == null){
            return;
        }
        CollectionHelper collectionHelper = new CollectionHelper(getActivity());
        collectionHelper.setIsPublicRes(newResourceInfo.isPublicResource());
        collectionHelper.collectDifferentResource(
                newResourceInfo.getMicroId()+"-"+newResourceInfo.getResourceType(),
                newResourceInfo.getTitle(),
                newResourceInfo.getAuthorId(),
                ( taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE ?  getString(R.string.microcourse) : getString(R.string.task_order))
        );
    }

    /**
     * 投屏
     */
    private void enterShareScreenEvent() {
        if (newResourceInfo != null) {
            if (shareManager == null) {
                shareManager = MyShareManager.getInstance(getActivity(), handler);
            }
            if (shareManager != null) {
                if (shareManager.getSharedDevices() != null) {
                    if(newResourceInfo.getResourceType() == ResType.RES_TYPE_ONEPAGE) {
                        ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, true, null);
                    } else {
                        ActivityUtils.playOnlineCourse(getActivity(), newResourceInfo
                                        .getCourseInfo(), true, null);
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.no_share_play, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void loadDatas() {
        loadCommonData();
        loadTaskComments();
    }

    protected void initExpandListView() {
        expandListView = ((ExpandableListView) findViewById(R.id.catlog_expand_listview));
        if (expandListView != null) {
            expandListView.setGroupIndicator(null);
            expandListView.setDivider(null);
            //expandListView.setChildDivider(getResources().getDrawable(R.drawable.content_line));
            ExpandDataAdapter dataAdapter = new ExpandDataAdapter(getActivity(), null,
                    R.layout.item_expendlistview_group_comment,
                    R.layout.item_expendlistview_child_comment) {

                @Override
                public Object getChild(int groupPosition, int childPosition) {
                    return ((StudytaskComment) getData().get(groupPosition))
                            .getChildren().get(childPosition);
                }


                @Override
                public int getChildrenCount(int groupPosition) {
                    if (hasData() && groupPosition < getGroupCount()) {
                        StudytaskComment comment = (StudytaskComment)
                                getData().get(groupPosition);
                        if (comment != null && comment.getChildren() != null) {
                            return comment.getChildren().size();
                        }
                    }
                    return 0;
                }

                @Override
                public View getChildView(int groupPosition, int childPosition,
                                         boolean isLastChild, View convertView, ViewGroup parent) {
                    View view = super.getChildView(groupPosition, childPosition,
                            isLastChild, convertView, parent);
                    final StudytaskComment data = (StudytaskComment) getChild(groupPosition,
                            childPosition);
                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    holder.groupPosition = groupPosition;
                    holder.childPosition = childPosition;
                    holder.data = data;
                    TextView textView = (TextView) view.findViewById(R.id.who_comment_who);
                    if (textView != null) {
                        textView.setText(getString(R.string.who_reply_who, data.getCommentName(),
                                data.getCommentToName()));
                        ColorUtil.spannableGreenColor(getActivity(), textView, 0,
                                data.getCommentName().length(), textView.length() -
                                        data.getCommentToName().length(), textView.length());
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commentEditText.setHint(getString(R.string.reply_who,
                                        data.getCommentName()));
                                //打开软键盘
                                UIUtils.showSoftKeyboard1(getActivity());
                                commentEditText.requestFocus();
                                parentId = data.getParentId();
                                commentToId = data.getCommentId();
                                commentToName = data.getCommentName();
                            }
                        });
                    }
                    textView = (TextView) view.findViewById(R.id.comment_time);
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(data.getCommentTime(),
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                    }
                    textView = (TextView) view.findViewById(R.id.comment_cotent);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }
                    view.setTag(holder);
                    return view;
                }

                @Override
                public View getGroupView(int groupPosition, boolean isExpanded,
                                         View convertView, ViewGroup parent) {
                    View view = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                    final StudytaskComment data = (StudytaskComment) getGroup(groupPosition);
                    View devider = (View) view.findViewById(R.id.parent_children_devider);
                    if (data.getChildren() != null && data.getChildren().size() > 0) {
                        devider.setVisibility(View.VISIBLE);
                    } else {
                        devider.setVisibility(View.GONE);
                    }

                    //头部分隔线
                    View topLineView = view.findViewById(R.id.top_line);
                    if (topLineView != null) {
                        if (groupPosition == 0) {
                            topLineView.setVisibility(View.GONE);
                        } else {
                            topLineView.setVisibility(View.VISIBLE);
                        }
                    }
                    ImageView imageView = (ImageView) view.findViewById(R.id.head_img);
                    if (imageView != null) {
                        if (data.getCommentHeadPicUrl() != null) {
                            getThumbnailManager().displayUserIcon(AppSettings.getFileUrl(
                                    data.getCommentHeadPicUrl()), imageView);
                        }
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityUtils.enterPersonalSpace(getActivity(),
                                        data.getCommentId());
                            }
                        });
                    }
                    TextView textView = (TextView) view.findViewById(R.id.comment_name);
                    if (textView != null) {
                        textView.setText(data.getCommentName());
                    }
                    textView = (TextView) view.findViewById(R.id.comment_time);
                    if (textView != null) {
                        textView.setText(DateUtils.getDateStr(data.getCommentTime(),
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM_SS,
                                DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
                    }
                    textView = (TextView) view.findViewById(R.id.comment_cotent);
                    if (textView != null) {
                        textView.setText(data.getComments());
                    }
                    textView = (TextView) view.findViewById(R.id.reply_btn);
                    if (textView != null) {
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commentEditText.setHint(getString(R.string.reply_who,
                                        data.getCommentName()));
                                //打开软键盘
                                UIUtils.showSoftKeyboard1(getActivity());
                                commentEditText.requestFocus();
                                parentId = data.getId();
                                commentToId = data.getCommentId();
                                commentToName = data.getCommentName();
                            }
                        });
                    }
                    textView = (TextView) view.findViewById(R.id.praise_btn);
                    if (textView != null) {
                        int praiseCount = data.getPraiseCount();
                        //有评论
                        if (praiseCount > 0) {

                            textView.setText(String.valueOf(praiseCount));
                            Drawable leftDrawable = getResources().getDrawable
                                    (R.drawable.btn_comment_praise);
                            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable
                                    .getMinimumHeight());
                            textView.setCompoundDrawables(leftDrawable,
                                    null, null, null);

                        } else {
                            textView.setText("");
                            Drawable leftDrawable = getResources().getDrawable
                                    (R.drawable.comment_praise_pre_ico);
                            leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable
                                    .getMinimumHeight());
                            textView.setCompoundDrawables(leftDrawable,
                                    null, null, null);

                        }
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                praiseComment(data);
                            }
                        });
                    }


                    MyViewHolder holder = (MyViewHolder) view.getTag();
                    if (holder == null) {
                        holder = new MyViewHolder();
                    }
                    view.setTag(holder);
                    holder.data = data;
                    view.setClickable(true);
                    return view;
                }

            };

            ExpandListViewHelper listViewHelper = new ExpandListViewHelper(getActivity(),
                    expandListView, dataAdapter) {
                @Override
                public void loadData() {
                    loadTaskComments();
                }

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    MyViewHolder holder = (MyViewHolder) v.getTag();
                    if (holder == null || holder.data == null) {
                        return false;
                    }
                    return true;
                }

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    return false;
                }
            };
            listViewHelper.setData(null);
            setCurrListViewHelper(expandListView, listViewHelper);
        }
    }

    private class MyViewHolder extends ViewHolder {
        int groupPosition;
        int childPosition;
    }

    private void praiseComment(final StudytaskComment data) {
        if (data == null){
            return;
        }
        if(data.isHasPraised()){
            TipsHelper.showToast(getActivity(),getString(R.string.have_praised));
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskCommentId", data.getId());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.ADD_PRAISE_COUNT_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(getActivity(),
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            Toast.makeText(getActivity(), getString(R.string.praise_fail),
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getActivity(), getString(R.string.praise_success),
                                Toast.LENGTH_SHORT).show();
                        data.setHasPraised(true);
                        loadTaskComments();
                    }
                });
    }

    private void loadTaskComments() {
//        List<StudytaskComment> comments = new ArrayList<StudytaskComment>();
//        for(int i=0;i<10;i++){
//            StudytaskComment comment = new StudytaskComment ();
//            comment.setComments("sfsf");
//            comment.setCommentName("aha");
//            comment.setCommentToName("bala");
//            comments.add(comment);
//        }
//        getCurrListViewHelper().setData(comments);
//        expandAllView();
        if (task == null) return;
        Map<String, Object> params = new HashMap();
        params.put("TaskId", task.getTaskId());
        params.put("Pager", getPageHelper().getFetchingPagerArgs());
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_TASK_COMMENT_LIST_URL, params,
                new RequestHelper.RequestDataResultListener<StudyTaskCommentDiscussPersonResult>
                        (getActivity(), StudyTaskCommentDiscussPersonResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        StudyTaskCommentDiscussPersonResult result = getResult();
                        int discussCount = 0;
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            if (getCurrListViewHelper().hasData()) {
                                getCurrListViewHelper().getData().clear();
                                getCurrListViewHelper().update();
                            }
                            return;
                        }
                        List<StudytaskComment> data = result.getModel().getData().getCommentList();
                        if (data != null) {
                            discussCount += data.size();
                            for (StudytaskComment item : data) {
                                discussCount += item.getChildren().size();
                            }
                            //设置点赞标志位
                            List<StudytaskComment> oldData = getCurrListViewHelper().getData();
                            if(oldData != null && oldData.size() > 0){
                                for(StudytaskComment item : data){
                                    for(StudytaskComment oldItem : oldData){
                                        if(item.getId() == oldItem.getId()){
                                            item.setHasPraised(oldItem.isHasPraised());
                                        }
                                    }
                                }
                            }
                            getCurrListViewHelper().setData(data);
                            expandAllView();
                        }

                    }
                });
    }

    private void expandAllView() {
        int groupCount = expandListView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandListView.expandGroup(i);
        }
    }

    private void replaySuccessOrGiveup() {
        parentId = 0;
        commentToId = "";
        commentToName = "";
        commentEditText.setText("");
        commentEditText.setHint(getString(R.string.say_something));
    }

    private void setndComment() {
        if (task == null) return;
        String content = commentEditText.getText().toString();
        if (content.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.pls_input_comment_content),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String commentId = null;
        String commentName = null;
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            commentId = userInfo.getMemberId();
            if (!TextUtils.isEmpty(userInfo.getRealName())) {
                commentName = userInfo.getRealName();
            } else {
                commentName = userInfo.getNickName();
            }
        }
        Map<String, Object> params = new HashMap();
        params.put("TaskId", task.getTaskId());
        if (parentId != 0) {
            params.put("ParentId", parentId);
        }
        params.put("Comments", content);
        params.put("CommentId", commentId);
        params.put("CommentName", commentName);
        if (commentToId.length() > 0) {
            params.put("CommentToId", commentToId);
        }
        if (commentToName.length() > 0) {
            params.put("CommentToName", commentToName);
        }
        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.ADD_TASK_COMMENT_URL, params,
                new RequestHelper.RequestDataResultListener<DataModelResult>(getActivity(),
                        DataModelResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        DataModelResult result = getResult();
                        if (result == null || !result.isSuccess()) {
                            Toast.makeText(getActivity(), getString(R.string.upload_comment_error),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            return;
                        }
                        //设置已经评论过
                        setHasCommented(true);
                        Toast.makeText(getActivity(), getString(R.string.upload_comment_success),
                                Toast.LENGTH_SHORT)
                                .show();
                        UIUtils.hideSoftKeyboard1(getActivity(), commentEditText);
                        replaySuccessOrGiveup();
                        loadTaskComments();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contacts_header_left_btn:
                finish();
                break;
            case R.id.send_btn:
                setndComment();
                break;
            case R.id.course_image_view:
            case R.id.iv_icon://图片点击
                if(newResourceInfo==null)return;
                openImage();
                break;
        }
    }

    /**
     * 打开图片
     */
    private void openImage() {
        if (taskType == StudyTaskType.INTRODUCTION_WAWA_COURSE){
            ActivityUtils.playOnlineCourse(getActivity(), newResourceInfo.getCourseInfo(),
                    false, null);
        }else {
            ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, false,
                    null);
        }
    }

    /**
     * 复述课件
     */
    private void retellTaskCourse() {
        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree == 0) {
            commitDataType = BtnEntity.TYPE_RETELL_COURSE;
            retellCourseOrAskQuestion(commitDataType);
        }
    }

    /**
     * 做任务.任务单
     */
    private void takeTask(boolean flag) {
        commitDataType = BtnEntity.TYPE_MAKE_TASK;
        if (flag) {
            loadCourseDetail(studyTask.getResId(), false);
        } else {

            loadCourseDetail(studyTask.getWorkOrderId(),false);
        }
    }

    /**
     * 提问
     */
    private void askQuestion() {
        commitDataType = BtnEntity.TYPE_QUESTION_COURSE;
        retellCourseOrAskQuestion(commitDataType);
    }

    private void createNullImageCourse(int orientation,int slideType){
        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree == 0) {
            CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam();
            param.mFragment =SelectedReadingDetailFragment.this;
            param.mEntryType = Common.LIST_TYPE_SHARE;
            param.mEditable = true;
            param.mSlideType = slideType;
            param.mIsIntroducationTask=true;
            if (slideType == CreateSlideHelper.SLIDETYPE_IMAGE) {
                param.mIsPickOneImage = false;
            }
            UserInfo userInfo = getUserInfo();
            if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
                param.mMemberId = userInfo.getMemberId();
            }
            param.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true, true, true);
            param.mOrientation = orientation;
            param.courseMode= WawaCourseChoiceFragment.CourseMode.READ;
            CreateSlideHelper.createSlide(param);
        }
    }
    private void importAskQuesitonImage(List<String> imagePaths){
        mCreateSlideParam = new CreateSlideHelper.CreateSlideParam();
        mCreateSlideParam.mActivity = getActivity();
        mCreateSlideParam.mFragment = SelectedReadingDetailFragment.this;
        mCreateSlideParam.mEntryType = Common.LIST_TYPE_SHARE;
        mCreateSlideParam.mEditable = true;
        mCreateSlideParam.mIsPickOneImage = false;
        mCreateSlideParam.mSlideType = CreateSlideHelper.SLIDETYPE_IMAGE;
        mCreateSlideParam.mOrientation = screenType;
        mCreateSlideParam.mIsIntroducationTask = true;
        UserInfo userInfo = getUserInfo();
        if (userInfo != null && !TextUtils.isEmpty(userInfo.getMemberId())) {
            mCreateSlideParam.mMemberId = userInfo.getMemberId();
        }
        mCreateSlideParam.mSlideSaveBtnParam = new CreateSlideHelper.SlideSaveBtnParam(true,
                true, true);
        mCreateSlideParam.courseMode = WawaCourseChoiceFragment.CourseMode.READ;
        mCreateSlideParam.mAttachmentPath = SlideManager.INSERT_IMAGES;
        mCreateSlideParam.mIsCreateAndPassResParam = true;
        mCreateSlideParam.mSlideParam = CreateSlideHelper.getDefaultSlideParam();
        List<com.libs.yilib.pickimages.MediaInfo> imageInfos=new ArrayList<>();
        if (imagePaths != null && imagePaths.size() > 0) {
            imageInfos=getMediaInfos(imagePaths);
            mCreateSlideParam.mMediaInfos= (ArrayList<com.libs.yilib.pickimages.MediaInfo>) imageInfos;
            mCreateSlideParam.mMediaType = com.lqwawa.client.pojo.MediaType.PICTURE;
            Intent it = CreateSlideHelper.getSlideNewIntent(mCreateSlideParam);
            getActivity().startActivityForResult(it,REQUEST_CODE_SLIDE);
        }
    }
    private List<com.libs.yilib.pickimages.MediaInfo> getMediaInfos(List<String> imagePaths){
        List<com.libs.yilib.pickimages.MediaInfo> imageInfos=new ArrayList<>();
        if (imagePaths!=null&&imagePaths.size()>0){
            for (int i=0;i<imagePaths.size();i++){
                com.libs.yilib.pickimages.MediaInfo mediaInfo=new com.libs.yilib.pickimages.MediaInfo(imagePaths.get(i));
                imageInfos.add(mediaInfo);
            }
            return  imageInfos;
        }
        return null;
    }

    /**
     * 创作
     */
    private void createCourse() {
        Intent intent=new Intent(getActivity(),WawaCourseChoiceActivity.class);
        intent.putExtra(Constants.INTORDUCTION_CREATE,true);
        getActivity().startActivityForResult(intent,CREATE_NEW_COURSE_TASK_TYPE);
    }
    public void enterEditCourseEvent(LocalCourseDTO data){
        if (newResourceInfo != null) {
            String courseType = String.valueOf(ResType.RES_TYPE_COURSE_SPEAKER);
            String splitCourseType = String.valueOf(ResType.RES_TYPE_BASE + ResType.RES_TYPE_COURSE_SPEAKER);
            if (!TextUtils.isEmpty(newResourceInfo.getResourceId()) && (newResourceInfo
                    .getResourceId().contains(courseType) || newResourceInfo.getResourceId()
                    .contains(splitCourseType))) {
                String localPath = data.getmPath();
                if (!TextUtils.isEmpty(localPath)) {
                    String courseRootPath = getCourseRootPath(localPath);
                    if (TextUtils.isEmpty(courseRootPath)) {
                        unzip(localPath, newResourceInfo.getTitle(), newResourceInfo.getScreenType(),
                                "", Constants.OPERATION_TYPE_EDITCOURSE);
                    } else {
                        File file = new File(courseRootPath);
                        if (file.exists()) {
                            copyLocalCourse(courseRootPath, newResourceInfo.getScreenType(),"");
                        } else {
                            unzip(localPath, newResourceInfo.getTitle(), newResourceInfo.getScreenType(),
                                    "", Constants.OPERATION_TYPE_EDITCOURSE);
                        }
                    }
                }
            } else {
                TipsHelper.showToast(getActivity(), R.string.course_not_edit);
            }
        }
    }
    private void unzip(final String destPath, final String title, final int orientaion, final String description, final int operationType) {
        if (newResourceInfo != null) {
            if (downloadService == null) {
                return;
            }
            FileInfo fileInfo = downloadService.getFileInfo(getUserInfo().getMemberId(), newResourceInfo.getResourceId());
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
                                            String courseRootPath = getCourseRootPath(destPath);
                                            if (!TextUtils.isEmpty(courseRootPath)) {
                                                if (operationType == PictureBooksDetailFragment.Constants.OPERATION_TYPE_SHARESCEEN) {
                                                    LocalCourseInfo info = new LocalCourseInfo();
                                                    info.mPath = courseRootPath;
                                                    info.mOrientation = newResourceInfo
                                                            .getScreenType();
                                                    playLocalCourse(info, true);
                                                } else if (operationType == PictureBooksDetailFragment.Constants.OPERATION_TYPE_MAKEPICBOOK) {
                                                    importLocalPicResources(courseRootPath, title);
                                                } else if (operationType == PictureBooksDetailFragment.Constants.OPERATION_TYPE_EDITCOURSE) {
                                                    copyLocalCourse(courseRootPath, orientaion, description);
                                                } else if (operationType == PictureBooksDetailFragment.Constants
                                                        .OPERATION_TYPE_PLAYCOURSE) {
                                                    LocalCourseInfo info = new LocalCourseInfo();
                                                    info.mPath = courseRootPath;
                                                    info.mOrientation = newResourceInfo
                                                            .getScreenType();
                                                    playLocalCourse(info, false);
                                                } else if (operationType == PictureBooksDetailFragment.Constants
                                                        .OPERATION_TYPE_GET_THUMBNAIL) {
                                                    File headFile = new File(courseRootPath, "head.jpg");
                                                    if (headFile != null && headFile.canRead()) {
//                                                        newResourceInfo.setThumbnail(headFile.getAbsolutePath());
//                                                        getThumbnailManager().displayImageWithDefault(
//                                                                newResourceInfo.getThumbnail(), imageViewIcon,
//                                                                R.drawable.default_book_cover);
                                                        getThumbnailManager().displayImageWithDefault(
                                                                newResourceInfo.getThumbnail(), homeworkIcon,
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
    private void playLocalCourse(LocalCourseInfo info, boolean isShareScreen) {
        String path = info.mPath;
        int resType = BaseUtils.getCoursetType(info.mPath);
        Intent it = ActivityUtils.getIntentForPlayLocalCourse(
                getActivity(), path, info.mTitle, info.mDescription,
                info.mOrientation, resType, true, isShareScreen);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //temp 备注 requestCode 2
        startActivityForResult(it, REQUEST_CODE_RETELLCOURSE);
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
                    LocalCourseInfo info = (LocalCourseInfo)result;
                    enterLocalCourse(info);
                }
            }
        });
        copyCourseTask.execute();
    }

    private boolean isCourseFolder(String folder) {
        File headFile = new File(folder, "head.jpg");
        File pageIndexFile = new File(folder, "page_index.xml");
        File courseIndexFile = new File(folder, "course_index.xml");
        if (headFile == null || pageIndexFile == null || courseIndexFile == null) {
            return false;
        }
        if (headFile.exists() || pageIndexFile.exists() || courseIndexFile.exists()) {
            return true;
        }
        return false;
    }
    private String getCourseRootPath(String folder) {
        String result = null;
        if (!TextUtils.isEmpty(folder)) {
            File file = new File(folder);
            if (file.exists() && file.isDirectory()) {
                if (isCourseFolder(folder)) {
                    return folder;
                } else {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        result = getCourseRootPath(files[i].getPath());
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    protected void enterLocalCourse(LocalCourseInfo info) {
        String path = info.mPath;
        int resType = BaseUtils.getCoursetType(info.mPath);
        Intent intent = ActivityUtils.getIntentForPlayLocalCourse(
                getActivity(), path,
                info.mTitle, info.mDescription, info.mOrientation, resType, true, false);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_CODE_EDITCOURSE);
    }

    /**
     * 加载当前任务的数据
     */
    private void loadCommonData() {
        Map<String, Object> params = new HashMap();
        params.put("TaskId", task.getTaskId());
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.GET_COMMITTED_TASK_BY_TASK_ID_URL,
                params, new DefaultPullToRefreshDataListener<HomeworkCommitObjectResult>
                        (HomeworkCommitObjectResult.class) {
                    public void onSuccess(String jsonString) {
                        if (getActivity() == null) {
                            return;
                        }
                        super.onSuccess(jsonString);
                        if (getResult() == null || !getResult().isSuccess() || getResult().getModel() == null) {
                            return;
                        }
                        if (studyTask == null) {
                            studyTask = getResult().getModel().getData().getTaskInfo();
                            if (studyTask != null) {
                                updateHeaderView(studyTask);
                                studentTaskDiscuss.setText(studyTask.getDiscussContent());
                                //显示导读课件的标题
                                if (courseTitle!=null){
                                    courseTitle.setText(studyTask.getTaskTitle());
                                }
                                if (roleType==RoleType.ROLE_TYPE_PARENT){
                                    loadChildInfo();
                                }
                            }
                        }

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadBottons();
                    }
                });
    }

    /**
     * 更新头布局
     * @param task
     */
    private void updateHeaderView(StudyTask task) {
        if (task == null){
            return;
        }
        if (homeworkIcon != null) {
            getThumbnailManager().displayThumbnailWithDefault(AppSettings.getFileUrl(
                    task.getResThumbnailUrl()), homeworkIcon, R.drawable.default_book_cover);
        }
        //作业标题
        if (homeworkTitle != null) {
            homeworkTitle.setText(task.getTaskTitle());
        }

        //布置时间
        if (assignTime != null) {
            assignTime.setText(getString(R.string.assign_date) + "：" + DateUtils.getDateStr(task
                    .getStartTime(), 0) + "-" + DateUtils.getDateStr(task.getStartTime(), 1) + "-"
                    + DateUtils.getDateStr(task.getStartTime(), 2));
        }

        //完成时间
        if (finishTime != null) {
            finishTime.setText(getString(R.string.finish_date) + "：" + DateUtils.getDateStr(task
                    .getEndTime(), 0) + "-" + DateUtils.getDateStr(task.getEndTime(), 1) + "-"
                    + DateUtils.getDateStr(task.getEndTime(), 2));
        }
    }

    private void retellCourseOrAskQuestion(final int commitDataType) {
        final HashMap<String, Object> params = new HashMap<String, Object>();
        if (studyTask != null) {
            String resId = studyTask.getResId();
            if (!TextUtils.isEmpty(resId)) {
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
                List<CourseData> courseDatas = result.getCourse();
                if (courseDatas != null && courseDatas.size() > 0) {
                    CourseData courseData = courseDatas.get(0);
                    if (courseData != null && !TextUtils.isEmpty(courseData.resourceurl)) {
                        screenType = courseData.screentype;
                        String savePath = Utils.PIC_TEMP_FOLDER + new MD5FileNameGenerator()
                                .generate(courseData.resourceurl);
                        List<String> paths = result.getData();
                        if(paths == null || paths.size() == 0) {
//                            createNewRetellCourse(screenType);
                            String originVoicePath = null;
                            if (studyTask != null) {
                                originVoicePath = studyTask.getResUrl();
                                if (!TextUtils.isEmpty(originVoicePath)
                                        && originVoicePath.contains(".zip")) {
                                    //截取字符串
                                    originVoicePath = originVoicePath.substring(0,
                                            originVoicePath.indexOf(".zip"));
                                }
                            }
                            //复述课件类型
                            if (commitDataType == BtnEntity.TYPE_RETELL_COURSE) {
                                createNewRetellCourse(screenType, originVoicePath);
                            }
                            //提问
                            if (commitDataType == BtnEntity.TYPE_QUESTION_COURSE) {
                                createNullImageCourse(screenType,CreateSlideHelper.SLIDETYPE_WHITEBOARD);
                            }

                        } else {
//                            downloadCourseImages(savePath, result.getData(), courseData.nickname);
                            checkCanReplaceIPAddress(savePath,result.getData(),courseData);
                        }
                    }
                }
            }
        };
        listener.setShowLoading(false);
        RequestHelper.sendGetRequest(getActivity(), ServerUrl.COURSE_IMAGES_URL, params, listener);
    }

    protected void createNewRetellCourse(int orientation, String originVoicePath) {
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.ORIENTATION, orientation);
        //注意：加这句是没有根目录
        it.putExtra(SlideActivityNew.ISNEEDDIRECTORY,true);
        it.putExtra(SlideActivityNew.COURSETYPEFROM,SlideActivityNew.CourseTypeFrom.FROMSTUDYTASK);
        SlideInputParam slideInputParam = getSlideInputParam(true, true);
        slideInputParam.mOriginVoicePath = originVoicePath;
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        startActivityForResult(it, REQUEST_CODE_RETELLCOURSE);
    }
    private SlideInputParam getSlideInputParam(boolean isNew, boolean isRetellCourse) {
        SlideInputParam param = new SlideInputParam();
        param.mCurUser = new User();
        UserInfo userInfo = getUserInfo();
        if (userInfo != null) {
            param.mCurUser.mId = userInfo.getMemberId();
            if (TextUtils.isEmpty(userInfo.getRealName())) {
                param.mCurUser.mName = userInfo.getRealName();
            } else {
                param.mCurUser.mName = userInfo.getNickName();
            }
        }
        param.mNotShowShareBoxBtn = true;
        param.mIsCreateAndPassResParam = isNew;
        if(isRetellCourse) {
            int[] rayMenuV = {
                    BaseSlideManager.MENU_ID_CAMERA,
                    BaseSlideManager.MENU_ID_IMAGE,
                    BaseSlideManager.MENU_ID_WHITEBOARD,
                    BaseSlideManager.MENU_ID_AUDIO,
                    BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
            };
//            MyApplication application = (MyApplication) getActivity().getApplicationContext();
//            if (application != null){
//                UserInfo info = application.getUserInfo();
//                if (info != null && info.isTeacher()){
//                    rayMenuV= Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
//                    rayMenuV[rayMenuV.length - 1] =  BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
//                }
//            }
            param.mRayMenusV = rayMenuV;
        } else {
            int[] rayMenuV = {BaseSlideManager.MENU_ID_CAMERA,
                    BaseSlideManager.MENU_ID_IMAGE,
                    BaseSlideManager.MENU_ID_WHITEBOARD,
                    BaseSlideManager.MENU_ID_AUDIO,
                    BaseSlideManager.MENU_ID_PERSONAL_MATERIAL
            };
            MyApplication application = (MyApplication) getActivity().getApplicationContext();
            if (application != null){
                UserInfo info = application.getUserInfo();
                if (info != null && info.isTeacher()){
                    rayMenuV= Arrays.copyOf(rayMenuV, rayMenuV.length + 1);
                    rayMenuV[rayMenuV.length - 1] =  BaseSlideManager.MENU_ID_SCHOOL_MATERIAL;
                }
            }
            param.mRayMenusV = rayMenuV;
        }
        int[] rayMenuH = {
                BaseSlideManager.MENU_ID_CURVE,
                BaseSlideManager.MENU_ID_LASER,
                BaseSlideManager.MENU_ID_ERASER};
        param.mRayMenusH = rayMenuH;
        return param;
    }

    /**
     *校验是否用内网的IP进行下载
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
                        if (flag){
                            imageUrl = helper.getChangeIPUrlArray(paths);
                        }
                        downloadCourseImages(savePath,imageUrl,courseData.nickname);
                    }
                })
                .checkIP();
    }

    private void downloadCourseImages(String savePath, List<String> paths, String title) {
        if (paths == null || paths.size() == 0) {
            return;
        }
        CacheCourseImagesTask task = new CacheCourseImagesTask(getActivity(), paths,
                savePath, title);
        task.setCallbackListener(new CallbackListener() {
            @Override
            public void onBack(Object result) {
                if (result != null) {
                    List<String> imagePaths = (List<String>)result;
                    if (imagePaths != null && imagePaths.size() > 0) {
                        //复述课件类型
                        if (commitDataType==BtnEntity.TYPE_RETELL_COURSE){
                            importLocalPicResources(imagePaths);
                        }
                        //提问
                        if (commitDataType==BtnEntity.TYPE_QUESTION_COURSE){
                            importAskQuesitonImage(imagePaths);
                        }

                    }
                }
            }
        });
        task.execute();
    }

    private void importLocalPicResources(String coursePath, final String title) {
        final List<String> paths = getPicPaths(coursePath);
        importLocalPicResources(paths,title);
    }
    private void importLocalPicResources(final List<String> paths , final String title) {
        if (paths == null || paths.size() == 0) {
            TipMsgHelper.ShowLMsg(getActivity(), R.string.fetch_no_resources);
            return;
        }
        savePath = Utils.getUserCourseRootPath(getMemeberId(), CourseType.COURSE_TYPE_IMPORT, false);

        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }

        ImportImageTask importResourceTask = new ImportImageTask(getActivity(),
                getMemeberId(), paths, savePath, title, handler);
        importResourceTask.execute();

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
    private void importLocalPicResources(final List<String> paths) {
        if (savePath == null) {
            savePath = Utils.getUserCourseRootPath(getMemeberId(), CourseType.COURSE_TYPE_IMPORT, false);
        }
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }

        String title = studyTask.getTaskTitle();
        ImportImageTask importResourceTask = new ImportImageTask(getActivity(),
                getMemeberId(), paths, savePath, title, handler);
        importResourceTask.execute();
    }

    protected void showMessageDialog(final String message, DialogInterface.OnClickListener confirmButtonClickListener) {
        ContactsMessageDialog dialog = new ContactsMessageDialog(getActivity(), "", message, getString(R.string.cancel), null,
                getString(R.string.ok), confirmButtonClickListener);
        dialog.show();
//        resizeDialog(dialog, 0.9f);
    }

    protected void saveData(LocalCourseInfo courseInfo) {
        if (courseInfo != null) {
            courseInfo.mParentPath = Utils.removeFolderSeparator(courseInfo.mParentPath);
            courseInfo.mPath = Utils.removeFolderSeparator(courseInfo.mPath);
            LocalCourseDTO localCourseDTO = courseInfo.toLocalCourseDTO();
            localCourseDTO.setmType(CourseType.COURSE_TYPE_IMPORT);
            localCourseDTO.setmMemberId(getMemeberId());
            LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
            if (localCourseDTO != null) {
                localCourseDao.addOrUpdateLocalCourseDTO(localCourseDTO);
            }
        }
    }
    protected void enterSlideNewRetellCourse(LocalCourseInfo info) {
        enterSlideNew(info, MaterialType.RECORD_BOOK, REQUEST_CODE_RETELLCOURSE, true);
    }
    private void enterSlideNew(LocalCourseInfo info, int type, int requestCode, boolean
            isRetellCourse) {
        Intent it = new Intent(getActivity(), SlideActivityNew.class);
        it.putExtra(SlideActivityNew.LOAD_FILE_PATH, info.mPath);
        it.putExtra(SlideActivityNew.LOAD_FILE_PAGES, info.mPageCount);
        it.putExtra(SlideActivityNew.COUTSE_TYPE, type);
        it.putExtra(SlideActivityNew.ORIENTATION, info.mOrientation);
        if (isRetellCourse){
            it.putExtra(SlideActivityNew.COURSETYPEFROM,SlideActivityNew.CourseTypeFrom
                    .FROMSTUDYTASK);
        }else {
            it.putExtra(SlideActivityNew.COURSETYPEFROM,SlideActivityNew.CourseTypeFrom
                    .FROMLQCOURSE);
        }
        SlideInputParam slideInputParam = getSlideInputParam(false, isRetellCourse);
        slideInputParam.mOriginVoicePath = info.mOriginVoicePath;
        //support A4 paper ratio for course maker
        if(info.mOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            slideInputParam.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            slideInputParam.mRatioScreenWToH = 210.0f / 297.0f;
        }
        it.putExtra(SlideInputParam.class.getSimpleName(), slideInputParam);
        LocalCourseDao localCourseDao = new LocalCourseDao(getActivity());
        try {
            List<LocalCourseDTO> dtos = localCourseDao.getLocalCourseByPath(getMemeberId(), info.mPath);
            if (dtos != null && dtos.size() > 0) {
                it.putExtra(SlideActivityNew.ORIENTATION, dtos.get(0).getmOrientation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startActivityForResult(it, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 1 复述课件 2 任务单 3 提问 4 创作
        if (task.getTaskType()== StudyTaskType.SUBMIT_HOMEWORK) {
            CreateSlideHelper.processActivityResule(null, this, requestCode, resultCode, data);
        } else {
            //导读和任务单都有查看任务单、做任务单。
            //复述课件
            if (requestCode == REQUEST_CODE_RETELLCOURSE) {
                if (data != null) {
                    String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                    String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                    LogUtils.logi("TEST", "SlidePath = " + slidePath);
                    LogUtils.logi("TEST", "CoursePath = " + coursePath);
                    if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                        LocalCourseInfo info = getLocalCourseInfo(coursePath);
                        if (info != null) {
                            uploadCourse(info, slidePath,CommitType.RETELL_INTRODUCTION_COURSE);
                        }
                    } else if (!TextUtils.isEmpty(slidePath)) {
                        //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                        if (slidePath.endsWith(File.separator)) {
                            slidePath = slidePath.substring(0, slidePath.length() - 1);
                        }
                        LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                slidePath, true);
                    }
                }
                //提问
            } else if (requestCode == REQUEST_CODE_EDITCOURSE) {
                if (data != null) {
                    NormalProperty normalProperty = data.getParcelableExtra(PlaybackActivity.EXTRA_NAME_COURSE_PROPERTY);
                    if (normalProperty != null) {
                        String savePath = normalProperty.mPath;
                        if (savePath != null) {
                            LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(),
                                    getMemeberId(), savePath);
                            if (dto != null) {
                                LocalCourseInfo info = dto.toLocalCourseInfo();
                                if (info != null) {
                                    uploadCourse(info, null,CommitType.ASK_QUESTION);
                                }
                            }
                        }
                    }
                }
                //创作
            } else if (requestCode == CREATE_NEW_COURSE_TASK_TYPE) {
                if (data != null) {
                    String savePath = data.getStringExtra("path");
                    if (savePath != null) {
                        LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(), getMemeberId(), savePath);
                        if (dto != null) {
                            LocalCourseInfo info = dto.toLocalCourseInfo();
                            if (info != null) {
                                    uploadCourse(info, null,CommitType.CREATE_NEW_COURSE);
                            }
                        }
                    }
                }
                //任务单以及提问的整合
            } else if (requestCode == Common.ACTIVITY_REQUEST_EDITTEMPLATES_BASE + Common.LIST_TYPE_SHARE) {
                if (data != null) {
                    int saveType=data.getIntExtra(SlideManager.EXTRA_SLIDE_PATH_TYPE,0);
                    if (saveType==0){
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.lqcourse_save_local));
                        return;
                    }
                    String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                    String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                    if (slidePath != null) {
                        LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(), getMemeberId(), slidePath);
                        if (dto != null) {
                            LocalCourseInfo info = dto.toLocalCourseInfo();
                            if (info != null) {
                                if (commitDataType==BtnEntity.TYPE_QUESTION_COURSE){
                                    uploadCourse(info, null, CommitType.ASK_QUESTION);
                                }
                                if (commitDataType==BtnEntity.TYPE_MAKE_TASK){
                                    uploadCourse(info, null,CommitType.TASK_ORDER);
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                        //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                        if (slidePath.endsWith(File.separator)) {
                            slidePath = slidePath.substring(0, slidePath.length() - 1);
                        }
                        LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                slidePath, true);
                    }
                }
                //提问
            } else if (requestCode == REQUEST_CODE_SLIDE) {
                if (data != null) {
                    int saveType=data.getIntExtra(SlideManager.EXTRA_SLIDE_PATH_TYPE,0);
                    if (saveType==0){
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.lqcourse_save_local));
                        return;
                    }
                    String slidePath = data.getStringExtra(SlideManager.EXTRA_SLIDE_PATH);
                    String coursePath = data.getStringExtra(SlideManager.EXTRA_COURSE_PATH);
                    if (slidePath!=null){
                        LocalCourseDTO dto = LocalCourseDTO.getLocalCourse(getActivity(), getMemeberId(), slidePath);
                        if (dto != null) {
                            LocalCourseInfo info = dto.toLocalCourseInfo();
                            if (info != null) {
                                uploadCourse(info, null,CommitType.ASK_QUESTION);
                            }
                        }
                    }
                    LogUtils.logi("TEST", "SlidePath = " + slidePath);
                    LogUtils.logi("TEST", "CoursePath = " + coursePath);
                    if (!TextUtils.isEmpty(slidePath) && !TextUtils.isEmpty(coursePath)) {
                        //只打开素材没有录制微课，此时slidePath不空，coursePath空值，此时删除素材
                        if (slidePath.endsWith(File.separator)) {
                            slidePath = slidePath.substring(0, slidePath.length() - 1);
                        }
                        LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                slidePath, true);
                    }
                }
            }
        }
    }
    private void finishUtil(Class cls) {
        if(getActivity() != null) {
            MyApplication myApp = ((MyApplication) (getActivity().getApplication()));
            if(myApp != null) {
                ActivityStack activityStack = myApp.getActivityStack();
                if(activityStack != null) {
                    activityStack.finishUtil(cls);
                }
            }
        }
    }
    
    private void uploadCourse(final LocalCourseInfo localCourseInfo, final String slidePath, final String commitType) {
        userInfo = getUserInfo();
        if (roleType==RoleType.ROLE_TYPE_PARENT){
            if (stuUserInfo!=null){
                userInfo=stuUserInfo;
            }
        }
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(getActivity());
            return;
        }
        final UploadParameter uploadParameter = UploadUtils.getUploadParameter(userInfo,
                localCourseInfo, null, 1);
        if (uploadParameter != null) {
            showLoadingDialog();
            FileZipHelper.ZipUnzipParam param = new FileZipHelper.ZipUnzipParam(
                    localCourseInfo.mPath, Utils.TEMP_FOLDER + Utils.getFileNameFromPath
                    (localCourseInfo.mPath) + Utils.COURSE_SUFFIX);
            FileZipHelper.zip(param, new FileZipHelper.ZipUnzipFileListener() {
                            @Override
                            public void onFinish(FileZipHelper.ZipUnzipResult result) {
                            // TODO Auto-generated method stub
                            if (result != null && result.mIsOk) {
                                uploadParameter.setZipFilePath(result.mParam.mOutputPath);
                                UploadUtils.uploadResource(getActivity(), uploadParameter, new CallbackListener() {
                                    @Override
                                            public void onBack(final Object result) {
                                                if (getActivity()!=null){
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dismissLoadingDialog();
                                                            if (result != null) {
                                                                CourseUploadResult uploadResult = (CourseUploadResult) result;
                                                                if (uploadResult.code != 0) {
                                                                    TipMsgHelper.ShowLMsg(getActivity(), R.string.upload_file_failed);
                                                                    return;
                                                                }
                                                                if (uploadResult.data != null && uploadResult.data.size() > 0) {
                                                                    final CourseData courseData = uploadResult.data.get(0);
                                                                    if (courseData != null) {
                                                                        commitStudentCourse(userInfo, courseData, slidePath, commitType);
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
                    });
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

    private void openLocalOnePage(LocalCourseDTO data, int screenType) {
        if (data == null) {
            return;
        }
        CreateSlideHelper.CreateSlideParam param = new CreateSlideHelper.CreateSlideParam
                (getActivity(), null, data.getmPath(), data.getmTitle(), data.getmDescription(),
                        screenType);
        param.mIsScanTask = true;
        param.mIsIntroducationTask = true;
        param.mSchoolId = studyTask.getSchoolId();
        param.mClassId = studyTask.getClassId();
        CreateSlideHelper.startSlide(param, Common.ACTIVITY_REQUEST_ATTACHMENGT_EDIT);
    }

    private void commitStudentCourse(UserInfo userInfo, CourseData courseData, final String
            slidePath,String commitType) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (task != null) {
            params.put("TaskId", task.getTaskId());
        }
        if (roleType == RoleType.ROLE_TYPE_STUDENT) {
            params.put("StudentId", userInfo.getMemberId());
        }else if (roleType == RoleType.ROLE_TYPE_PARENT){
            //家长复述微课需要传递孩子的Id
            params.put("StudentId", studentId);
        }
        params.put("CommitType",commitType);
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
                        //上传成功删除微课对应的素材
                        String temSlidePath = slidePath;
                        if(!TextUtils.isEmpty(temSlidePath)) {
                            if(temSlidePath.endsWith(File.separator)) {
                                temSlidePath = temSlidePath.substring(0, temSlidePath.length() - 1);
                            }
                            LocalCourseDTO.deleteLocalCourseByPath(getActivity(), getMemeberId(),
                                    temSlidePath, true);
                        }
                        TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.save_to_lq_cloud));
                        //上传成功要设置刷新标志位
                        setHasHomeworkUploaded(true);
                        getActivity().finish();
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.publish_course_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        RequestHelper.sendPostRequest(getActivity(), ServerUrl.PUBLISH_STUDENT_HOMEWORK_URL, params, listener);
    }


    protected void shareCourse(){
        if (newResourceInfo != null) {
            CourseInfo courseInfo = newResourceInfo.getCourseInfo();
            if (courseInfo != null) {
                ShareInfo shareInfo = courseInfo.getShareInfo(getActivity());
                shareInfo.setSharedResource(courseInfo.getSharedResource());
                if (shareInfo != null) {
                    new ShareUtils(getActivity()).share(SelectedReadingDetailFragment.this.getView(), shareInfo);
                }
            }
        }
    }

    /**
     * 判断是否登陆，未登录提示登陆并跳转到登陆界面
     * @return
     */
    private boolean islogin(){
        if (!isLogin() && getActivity() != null) {
            TipMsgHelper.ShowLMsg(getActivity(), getString(R.string.pls_login));
            ActivityUtils.enterLogin(getActivity(), false);
            return false;
        }
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (downloadService != null) {
            getMyApplication().unbindDownloadService(downloadServiceConn);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getMyApplication().bindDownloadService(getActivity(), downloadServiceConn);
    }

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
    protected void enterDownLoadEvent() {
        int haveFree = Utils.checkStorageSpace(getActivity());
        if (haveFree != 0) {
            return;
        }
        if(!islogin()){
            return;
        }
        if (newResourceInfo != null) {
            if (downloadService == null) {
                return;
            }
            //save CourseInfo to Local
            FileInfo fileInfo = downloadService.getFileInfo(getUserInfo().getMemberId(),
                    newResourceInfo.getResourceId());
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

    private void enterMyCollectionBookList() {
        Intent intent = new Intent(getActivity(), MyDownloadListActivity.class);
        startActivity(intent);
    }

    protected void enterSplitCourseList() {
        if (newResourceInfo != null) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), SplitCourseListActivity.class);
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_ID, newResourceInfo.getMicroId());
            intent.putExtra(SplitCourseListActivity.EXTRA_COURSE_NAME, newResourceInfo.getTitle());
            intent.putExtra(PictureBooksDetailActivity.FROM_SOURCE_TYPE,
                    PictureBooksDetailActivity.FROM_OTHRE);
            startActivity(intent);
        }
    }
    public void loadCourseDetail(String resId, final boolean isFinish) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resId", resId);
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
                        CourseData courseData=uploadResult.getData().get(0);
                        if (courseData!=null){
                            prepareOpenCourse(23,String.valueOf(courseData.id));
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
                        if(isFinish && getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                }
            }
            @Override
            public void onError(NetroidError error) {
                if (getActivity() == null) {
                    return;
                }
                TipMsgHelper.ShowLMsg(getActivity(), R.string.resource_not_exist);
                if(isFinish && getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        request.addHeader("Accept-Encoding", "*");
        request.start(getActivity());
    }
    private void processData(CourseData courseData) {
        if (courseData != null) {
            int resType = courseData.type % ResType.RES_TYPE_BASE;
            if (resType == ResType.RES_TYPE_COURSE_SPEAKER ||
                    resType == ResType.RES_TYPE_COURSE ||
                    resType == ResType.RES_TYPE_OLD_COURSE) {
                ActivityUtils.playOnlineCourse(getActivity(), courseData.getCourseInfo(), false,
                        null);
                finish();
            } else if (resType == ResType.RES_TYPE_STUDY_CARD) {
                DownloadOnePageTask task = new DownloadOnePageTask(getActivity(), courseData
                        .resourceurl, courseData.nickname, courseData.screentype, Utils
                        .DOWNLOAD_TEMP_FOLDER, null);
                task.setCallbackListener(callbackListener);
                task.checkCanReplaceIPAddress(courseData.id,courseData.type,task);
            }else if(resType == ResType.RES_TYPE_ONEPAGE){
                ActivityUtils.openOnlineOnePage(getActivity(), courseData.getNewResourceInfo(), true,
                        null);
            }
        }
    }
    private void prepareOpenCourse(int type,String courseId) {
        WawaCourseUtils utils = new WawaCourseUtils(getActivity());
        utils.loadSplitLearnCardDetail(courseId, true);
        utils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
            @Override
            public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                if (info != null) {
                    CourseData courseData = info.getCourseData();
                    if(courseData==null)return;
                    if(roleType==0){//老师
                        NewResourceInfo newResourceInfo=  courseData.getNewResourceInfo();
                        if(newResourceInfo==null)return;
                        ActivityUtils.openOnlineOnePage(getActivity(), newResourceInfo, true, null);
                    }else{
                        processData(courseData);
                    }
                }
            }
        });
    }
    /**
     * 加载家长绑定的孩子信息
     */
    private void loadChildInfo() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ClassId",studyTask.getClassId());
        params.put("MemberId",getMemeberId());

        RequestHelper.sendPostRequest(getActivity(),
                ServerUrl.GET_STUDENT_BY_PARENT_URL, params,
                new DefaultDataListener<HomeworkChildListResult>(
                        HomeworkChildListResult.class) {
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
                        List<StudentMemberInfo> childList=getResult().getModel().getData();
                        if (childList!=null){
//                            studentId=createStudentIdsParam(childList);
                        }
                    }
                });

    }
    private String createStudentIdsParam(List<StudentMemberInfo> list) {
        StringBuilder sb = new StringBuilder();
        String result=null;
        if (list == null || list.size() <= 0) {
            result="";
        }else {
            for (int i = 0; i < list.size(); i++) {
                StudentMemberInfo info = list.get(i);
                if (info != null) {
                    if (i < list.size() - 1) {
                        sb.append(info.getMemberId()).append(",");
                    } else {
                        sb.append(info.getMemberId());
                    }
                }
            }
            result=sb.toString();
        }
        return result;
    }

    public static void setHasCommented(boolean hasCommented) {
        SelectedReadingDetailFragment.hasCommented = hasCommented;
    }

    public static boolean hasCommented() {
        return hasCommented;
    }

    public static void setHasHomeworkUploaded(boolean hasHomeworkUploaded) {
        SelectedReadingDetailFragment.hasHomeworkUploaded = hasHomeworkUploaded;
    }

    public static boolean hasHomeworkUploaded() {
        return hasHomeworkUploaded;
    }

    private CallbackListener callbackListener = new CallbackListener() {
        @Override
        public void onBack(Object result) {
            if (result != null) {
                LocalCourseDTO dto = (LocalCourseDTO)result;
                if (commitDataType == BtnEntity.TYPE_QUESTION_COURSE) {
                    enterEditCourseEvent(dto);
                } else if (commitDataType == BtnEntity.TYPE_MAKE_TASK) {
                    openLocalOnePage(dto, screenType);
                }
            }
        }
    };
}