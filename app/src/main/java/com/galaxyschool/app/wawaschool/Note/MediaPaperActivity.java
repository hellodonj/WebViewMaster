package com.galaxyschool.app.wawaschool.Note;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.BaseFragmentActivity;
import com.galaxyschool.app.wawaschool.HomeworkSendPickerActivity;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.SchoolCourseCategorySelectorActivity;
import com.galaxyschool.app.wawaschool.ShellActivity;
import com.galaxyschool.app.wawaschool.chat.DemoApplication;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CommitHelper;
import com.galaxyschool.app.wawaschool.common.Common;
import com.galaxyschool.app.wawaschool.common.DialogHelper;
import com.galaxyschool.app.wawaschool.common.NoteHelper;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UploadReourceHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.db.NoteDao;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;
import com.galaxyschool.app.wawaschool.fragment.MediaListFragment;
import com.galaxyschool.app.wawaschool.fragment.MediaTypeListFragment;
import com.galaxyschool.app.wawaschool.fragment.PublishResourceFragment;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.lqwawa.client.pojo.MediaType;
import com.lqwawa.lqbaselib.net.FileApi;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ShortSchoolClassInfo;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.galaxyschool.app.wawaschool.pojo.weike.PlaybackParam;
import com.galaxyschool.app.wawaschool.pojo.weike.SplitCourseInfo;
import com.galaxyschool.app.wawaschool.slide.SlideUtils;
import com.lqwawa.client.pojo.ResourceInfo;
import com.lqwawa.libs.mediapaper.AudioPopwindow;
import com.lqwawa.libs.mediapaper.MediaPaper;
import com.lqwawa.libs.mediapaper.PaperUtils;
import com.lqwawa.libs.mediapaper.io.Saver;
import com.lqwawa.libs.mediapaper.player.CustomizeParams;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareType;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MediaPaperActivity extends BaseFragmentActivity implements CommitHelper.NoteCommitListener {

    final static public int REQUEST_CLOUD_RESOURCE = 101;
    final static public int REQUEST_PERSONAL_CLOUD_RESOURCE = 102;

    final static public String KEY_PAPER_PATH = "path";
    final static public String KEY_EDITABLE = "editable";
    final static public String KEY_OPEN_TYPE = "open_type";
    final static public String KEY_IS_PAD = "is_pad";
    final static public String KEY_COURSE_INFO = "course_info";
    final static public String KEY_CREATE_TIME = "create_time";
    final static public String KEY_PAPER_TYPE = "paper_type";
    final static public String KEY_NOTE_INFO = "note_info";
    final static public String KEY_NOTE_OPEN_PARAMS = "note_open_params";
    final static public String KEY_ORIENTATION = "orientation";
    final static public String KEY_IS_STUDY_TASK = "is_study_task";
    final static public String KEY_IS_CLOUD_BAR = "is_cloud_bar";//云贴吧
    final static public String KEY_IS_NULL_UPLOADPARAMETER = "key_is_null_uploadparameter";//布置任务判断uploadparameter是否为空

    final static private int PERSONAL_CLOUD_RESOURCE_TYPE_CMC = 1;
    final static private int PERSONAL_CLOUD_RESOURCE_TYPE_IMG = 2;
    final static private int PERSONAL_CLOUD_RESOURCE_TYPE_ONEPAGE = 10;
//    final static private int PERSONAL_CLOUD_RESOURCE_TYPE_AUDIO = 3;
//    final static private int PERSONAL_CLOUD_RESOURCE_TYPE_VIDEO = 4;

    //    final static private int RES_TYPE_COURSE = 16;
    final static private int RES_TYPE_NOTE = 17;
    final static private int RES_TYPE_ONEPAGE = 18;
//    final static private int RES_TYPE_COURSE_SPEAKER = 19;

    final static public String CACHE_FOLDER = Utils.NOTE_FOLDER + "/TempCache";
    public final static int OPEN_TYPE_PREVIEW = MediaPaper.LOAD_HISTORY;

    public static final String EXTRA_ORIENTATION = "orientation";
    public final static int OPEN_TYPE_EDIT = MediaPaper.CREATE_NEW;


    final static int SEND_SAVE_STATE_NONE = 0;
    final static int SEND_SAVE_STATE_SEND = 1;
    final static int SEND_SAVE_STATE_UPDATE = 2;

    public interface SourceType {
        int CLOUD_POST_BAR = 0;
        int SCHOOL_MESSAGE = 1;
        int SCHOOL_SPACE = 2;
        int CLASS_SPACE = 3;
        int EDIT_NOTE = 4;
        int CLASS_SPACE_HOMEWORK = 5;
        int STUDY_TASK = 6;
    }

    MediaPaper mMediaPaper;
    int mOpenType = MediaPaper.CREATE_NEW;
    String mPaperPath = null;
    String mLoadFilePath;
    boolean isEditable = true;
    boolean bPad = false;
    CourseInfo courseInfo;
    NoteInfo noteInfo;
    UploadParameter uploadParameter;
    String createTime;
    DialogHelper.LoadingDialog mLoadingDialog;
    private int mPaperType = MediaPaper.PAPER_TYPE_TIEBA;
    private int mSendSaveState = SEND_SAVE_STATE_NONE;

    protected CommitHelper commitHelper;
    protected UserInfo userInfo;
    private int shareType;
    NoteOpenParams noteOpenParams;
    boolean isTeacher;
    private MediaPaperCallback mMediaPaperCallback = null;
    protected MediaPaper.MediaPaperExitHandler mMediaPaperExitHandler = new BasePaperExitHandler();
    protected boolean isStudyTask;
    protected boolean isCloudBar;//云贴吧
    protected int paperMode;
    protected int sourceType;
    private static boolean hasResourceSended;
    //用来控制一些需要的暂时数据
    protected boolean isTempData;

    //是否检查帖子缩略图（升级友盟分享SDK后，如果链接对应的缩略图不存在，那么就不能分享，故作此检查；
    //如果返回链接对应的缩略图存在或为空，可忽略该检查）
    private boolean isCheckNoteThumbnail = true;
    private RadioGroup onlineMessageTypeGroup;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MediaPaper.SAVE_EDIT_COMPLETE:
                    switch (mSendSaveState) {
                        case SEND_SAVE_STATE_NONE:
                            if (mMediaPaperCallback != null) {
                                mMediaPaperCallback.closeEditFinish();
                            } else {
                                MediaPaperActivity.this.finish();
                            }
                            break;
                        case SEND_SAVE_STATE_SEND:
                            updateNoteInfo();
                            if (mPaperType == MediaPaper.PAPER_TYPE_TIEBA) {
                                if (noteOpenParams != null) {
                                    if (noteOpenParams.sourceType == SourceType.CLOUD_POST_BAR) {
                                        switchTo(shareType);
                                    } else if (noteOpenParams.sourceType == SourceType.SCHOOL_SPACE
                                            || noteOpenParams.sourceType == SourceType.SCHOOL_MESSAGE) {
                                        commitNoteToSchoolSpace();
                                    } else if (noteOpenParams.sourceType == SourceType.CLASS_SPACE) {
                                        commitNoteToClassSpace();
                                    } else if (noteOpenParams.sourceType == SourceType.CLASS_SPACE_HOMEWORK) {
                                        enterHomeworkSendPickerActivity();
                                    } else if (noteOpenParams.sourceType == SourceType.STUDY_TASK) {
                                        commitHomework(noteOpenParams.taskType);
                                    }
                                }
                            }
                            break;
                        case SEND_SAVE_STATE_UPDATE:
                            if (noteInfo == null || noteInfo.getNoteId() <= 0) {
                                return;
                            }
                            NoteInfo info = getNoteInfoByDataTime(noteInfo.getDateTime());
                            if (info != null) {
                                noteInfo.setTitle(info.getTitle());
                                noteInfo.setThumbnail(info.getThumbnail());
                            }
                            noteInfo.setIsUpdate(true);
                            updateNote(noteInfo);
                            break;
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            int orientation = extras.getInt(KEY_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            if(orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            } else {
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
            mPaperPath = extras.getString(KEY_PAPER_PATH);
            bPad = extras.getBoolean(KEY_IS_PAD, false);
            courseInfo = extras.getParcelable(KEY_COURSE_INFO);
            isEditable = extras.getBoolean(KEY_EDITABLE, true);
            isStudyTask = extras.getBoolean(KEY_IS_STUDY_TASK, false);
            isCloudBar = extras.getBoolean(KEY_IS_CLOUD_BAR, false);
            paperMode = isStudyTask ? PaperUtils.HOMEWORK_MODE : PaperUtils.COMMON_MODE;
            noteOpenParams = extras.getParcelable(KEY_NOTE_OPEN_PARAMS);
            if (noteOpenParams != null) {
                mPaperPath = noteOpenParams.path;
                bPad = noteOpenParams.isPad;
                mOpenType = noteOpenParams.openType;
                mPaperType = noteOpenParams.noteType;
                noteInfo = noteOpenParams.noteInfo;
                isTeacher = noteOpenParams.isTeacher;
                createTime = noteOpenParams.createTime;
                sourceType = noteOpenParams.sourceType;
                if (sourceType == SourceType.STUDY_TASK) {
                    paperMode = PaperUtils.HOMEWORK_MODE;
                }
            }
        }


        File file = new File(Common.TempPath, "note_temp");
        SlideUtils.createLocalDiskPath(file.getPath());
        mLoadFilePath = file.getPath();


//        mPaperPath = "http://121.42.49.131:8080/kukewebservice/course/20151113/22577bf4-119d-4776-aa80-0abf9e220acc.zip";
        if (mPaperPath != null) {
            mMediaPaper = mySetContentView();
            mMediaPaper.setmPaperMode(paperMode);
            paperInitialize(mMediaPaper);


            if (mMediaPaper != null && !TextUtils.isEmpty(createTime)) {
                mMediaPaper.setupSubTitle(createTime);
                mMediaPaper.fillUserTitle(mMediaPaper.getmTitleStr());
            }

        }
        if (mMediaPaper != null && noteOpenParams != null) {
            if (noteOpenParams.sourceType == SourceType.STUDY_TASK) {
                mMediaPaper.setTopBarTitle(getString(R.string.homeworks));
            }
        }
        commitHelper = new CommitHelper(MediaPaperActivity.this);
        commitHelper.setIsTeacher(isTeacher);
        //courseInfo不为空时，在线打开帖子，分享成功后不需要退出页面；
        //courseInfo为空时，新建帖子，分享成功后需要退出当前的编辑页面
        boolean isFinish = courseInfo != null ? false : true;
        commitHelper.setIsFinish(isFinish);
        if (courseInfo != null){
            commitHelper.setIsOnlineSchool(courseInfo.isOnlineSchool());
        }
        commitHelper.setNoteCommitListener(MediaPaperActivity.this);
        userInfo = ((MyApplication) getApplication()).getUserInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPaper != null) {
            mMediaPaper.clearPaper();
        }
    }

    protected MediaPaper mySetContentView() {
        MediaPaper mediaPaper = new MediaPaper(this);
        setContentView(mediaPaper);
        return mediaPaper;
    }

    protected void paperInitialize(MediaPaper mediaPaper) {
        if (mediaPaper != null) {
            CustomizeParams customizeParams = packageCustomizeParams(isEditable);
            mediaPaper.paperInitialize(this, mHandler, mPaperPath, mOpenType, mSelectCloudResourceHandler,
                    mPaperSaveListener, mResourceOpenHandler, mMediaPaperExitHandler, customizeParams);
        }

    }

    private MediaPaper.PaperSaveListener mPaperSaveListener = new MediaPaper.PaperSaveListener() {

        @Override
        public void onSaveFinish(String paperPath, String userTitle) {
            NoteDao noteDao = new NoteDao(MediaPaperActivity.this);
            String title = new File(paperPath).getName();
            long createTime = System.currentTimeMillis();
            long dateTime = Long.parseLong(title);
            try {
                NoteDTO noteDTO = noteDao.getNoteDTOByDateTime(dateTime, mPaperType);
                if (noteDTO != null) {
                    noteDTO.setTitle(userTitle);
                    noteDTO.setCreateTime(createTime);
                    if (new File(paperPath, Saver.HEAD_FILE).exists()) {
                        noteDTO.setThumbnail(new File(paperPath, Saver.HEAD_FILE).getPath());
                    } else {
                        noteDTO.setThumbnail(null);
                    }
                    noteDTO.setIsUpdate(true);
                    noteDao.updateNoteDTO(dateTime, mPaperType, noteDTO);
                } else {
                    NoteInfo noteInfo = new NoteInfo();
                    noteInfo.setDateTime(Long.parseLong(title));
                    noteInfo.setTitle(userTitle);
                    noteInfo.setNoteType(mPaperType);
                    noteInfo.setCreateTime(createTime);
                    if (new File(paperPath, Saver.HEAD_FILE).exists()) {
                        noteInfo.setThumbnail(new File(paperPath, Saver.HEAD_FILE).getPath());
                    } else {
                        noteInfo.setThumbnail(null);
                    }
                    noteDTO = noteInfo.toNoteDTO(noteInfo);
                    noteDTO.setIsUpdate(true);
                    noteDao.addOrUpdateNoteDTO(noteDTO);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    };

    private void openUnzipThread(String zipPath, String saveFolder) {
        File saveImportedFolder = new File(saveFolder);
        String savePath = saveImportedFolder.getPath();
        if (saveImportedFolder.exists() && saveImportedFolder.list().length > 0) {
            SlideUtils.safeDeleteDirectory(savePath);
        }
        SlideUtils.createLocalDiskPath(savePath);
        Thread unZipThread = new Thread(new UnzipThread(zipPath, savePath));
        unZipThread.start();
    }

    @Override
    public void noteCommit(int shareType) {
        this.shareType = shareType;
        if (courseInfo == null) {
            if (uploadParameter != null) {
                updateNoteInfo();
                if (mPaperType == MediaPaper.PAPER_TYPE_TIEBA) {
                    if (noteOpenParams != null) {
                        if (noteOpenParams.sourceType == SourceType.CLOUD_POST_BAR) {
                            switchTo(shareType);
                        } else {

                        }
                    }
                }
            } else {
                mSendSaveState = SEND_SAVE_STATE_SEND;
                mMediaPaper.saveEdit();
            }
        } else {
            switchTo(shareType);
        }
    }

    private void switchTo(int shareType) {
        switch (shareType) {
            case ShareType.SHARE_TYPE_POSTBAR:
                if (courseInfo == null) {
                    commitNoteToCloudPostBar();
                } else {
                    CourseData courseData = new CourseData();
                    courseData.id = courseInfo.getId();
                    courseData.type = courseInfo.getResourceType();
                    courseData.nickname = courseInfo.getNickname();
                    NoteHelper.updateNoteToPostBar(MediaPaperActivity.this, userInfo, courseData, false);
                }
                break;
            case ShareType.SHARE_TYPE_SCHOOL_MOVEMENT:
            case ShareType.SHARE_TYPE_SCHOOL_COURSE:
                if (courseInfo == null) {
                    commitNoteToSchoolSpace();
                } else {
                    int type = PublishResourceFragment.Constants.TYPE_SCHOOL_COURSE;
                    if (shareType == ShareType.SHARE_TYPE_SCHOOL_MOVEMENT) {
                        type = PublishResourceFragment.Constants.TYPE_SCHOOL_MOVEMENT;
                    }
                    uploadParameter = new UploadParameter();
                    uploadParameter.setShareType(shareType);
                    CourseData courseData = new CourseData();
                    courseData.id = courseInfo.getId();
                    courseData.type = courseInfo.getResourceType();
                    courseData.nickname = courseInfo.getNickname();
                    uploadParameter.setCourseData(courseData);
                    //如果是校园空间以及校园巡查里面的秀秀 直接发送到当前所在的学校
                    if (courseInfo.isOnlineSchool()){
                        //在线课堂的班级论坛直接发送
                        handleOnlineSchoolMessageType(true,courseData);
                        return;
                    }
                    if (isTempData) {
                        //更新到到校园动态
                        shareToSchoolMessageSpace(courseData);
                        isTempData = false;
                    } else {
                        NoteHelper.enterContactsPicker(MediaPaperActivity.this, uploadParameter,
                                null, courseInfo.getNickname(), null, type, false);
                    }
                    finish();
                }
                break;
            case ShareType.SHARE_TYPE_NOTICE:
            case ShareType.SHARE_TYPE_HOMEWORK:
            case ShareType.SHARE_TYPE_COMMENT:
            case ShareType.SHARE_TYPE_COURSE:
                if (courseInfo == null) {
                    commitNoteToClassSpace();
                } else {
                    uploadParameter = new UploadParameter();
                    uploadParameter.setShareType(shareType);
                    CourseData courseData = new CourseData();
                    courseData.id = courseInfo.getId();
                    courseData.type = courseInfo.getResourceType();
                    courseData.nickname = courseInfo.getNickname();
                    uploadParameter.setCourseData(courseData);
                    if (uploadParameter.getShareType() == ShareType.SHARE_TYPE_HOMEWORK) {
                        uploadParameter.setFileName(courseInfo.getNickname());
                        UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
                        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                            ActivityUtils.enterLogin(MediaPaperActivity.this);
                            return;
                        }
                        uploadParameter.setMemberId(userInfo.getMemberId());
                        uploadParameter.setCreateName(userInfo.getRealName());
                        enterHomeworkSendPickerActivity();
                    } else {
                        uploadParameter.setSourceType(noteOpenParams.sourceType);
                        NoteHelper.enterContactsPicker(MediaPaperActivity.this, uploadParameter,
                                null, courseInfo.getNickname(), null, PublishResourceFragment.Constants.TYPE_CLASS_SPACE, false);
                    }
                    finish();
                }
                break;
            case ShareType.SHARE_TYPE_WECHAT:
            case ShareType.SHARE_TYPE_WECHATMOMENTS:
            case ShareType.SHARE_TYPE_QQ:
            case ShareType.SHARE_TYPE_QZONE:
            case ShareType.SHARE_TYPE_CONTACTS:
                if (courseInfo == null) {
                    shareNote(shareType);
                } else {
                    ShareInfo shareInfo = courseInfo.getShareInfo(MediaPaperActivity.this);
                    if (shareInfo != null) {
                        shareInfo.setSharedResource(courseInfo.getSharedResource());
                    }
                    if (shareInfo != null) {
                        commitHelper.shareTo(shareType, shareInfo);
//                        shareNote(shareType, shareInfo);
                    }
                }
                break;

        }
    }

    /**
     * 分享到校园动态
     * @param courseData
     */
    private void shareToSchoolMessageSpace(CourseData courseData) {
        String schoolId = courseInfo.getSchoolId();
        if (!TextUtils.isEmpty(schoolId)) {
            //原课件作者的id
            String courseAuthorId = courseInfo.getCode();
            if (!TextUtils.isEmpty(courseAuthorId)) {
                courseData.code = courseAuthorId;
            }
            NoteHelper.updateNoteToSchoolMovement(
                    MediaPaperActivity.this,
                    DemoApplication.getInstance().getUserInfo(),
                    courseData,
                    schoolId,
                    courseInfo.isOnlineSchool(),
                    getSelectTypeId(),
                    courseInfo.isTeacher());
        }
    }


    private class UnzipThread implements Runnable {
        String mZipFilePath = null;
        String mUnzipOutputPath = null;

        public UnzipThread(String zipFilePath, String outputPath) {
            mZipFilePath = zipFilePath;
            mUnzipOutputPath = outputPath;
        }


        @Override
        public void run() {
            if (!TextUtils.isEmpty(mZipFilePath)
                    && new File(mZipFilePath).exists()) {

                try {
                    SlideUtils.unzip(mZipFilePath, mUnzipOutputPath);
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mOpenType = MediaPaper.LOAD_HISTORY;
                                    setContentView(mMediaPaper);
                                    mMediaPaper.setViewMode(false);
                                    mMediaPaper.paperInitialize(MediaPaperActivity.this, mHandler, mUnzipOutputPath, mOpenType,
                                            mSelectCloudResourceHandler, null/*mPaperSaveListener*/, mResourceOpenHandler,
                                            mMediaPaperExitHandler, mPaperType, bPad, isEditable);
                                }
                            });
                } catch (Exception e) {
                    SlideUtils.outLog("unzip Exception", e.getMessage());
                }

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CLOUD_RESOURCE) {
            if (data != null) {
                List<ResourceInfo> resourceInfos = data.getParcelableArrayListExtra("resourseInfoList");
                if (resourceInfos != null && resourceInfos.size() > 0) {
                    addPersonalCloudResource(resourceInfos);
                }
            }
        } else if (requestCode == REQUEST_PERSONAL_CLOUD_RESOURCE) {
            if (resultCode == RESULT_OK && data != null) {
//                Bundle bundle = data.getExtras();
//                ArrayList<CSEntry> CSDatas = (ArrayList<CSEntry>) bundle.getSerializable("data");
//                if (CSDatas != null) {
//                    addCloudResource(CSDatas);
//                }
                List<ResourceInfo> resourceInfos = data.getParcelableArrayListExtra("resourseInfoList");
                if (resourceInfos != null && resourceInfos.size() > 0) {
                    //add resources for my personal resources
                    addPersonalCloudResource(resourceInfos);
                }
            }
        } else if (mMediaPaper != null) {
            mMediaPaper.backToPaper(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //	public String getTimeString(Long millSec) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
//		Date date = new Date(millSec);
//		return sdf.format(date);
//	}


    @Override
    public void onBackPressed() {
        boolean rtn = false;
        if (mMediaPaper != null) {
            rtn = mMediaPaper.onBackPressed();
        }
        if (!rtn) {
            super.onBackPressed();
        }
    }

    private MediaPaper.SelectCloudResourceHandler mSelectCloudResourceHandler = new MediaPaper.SelectCloudResourceHandler() {

        @Override
        public void selectCloudResource(Handler mpHandler) {
            Intent intent = new Intent(MediaPaperActivity.this, ShellActivity.class);
            intent.putExtra("Window", "resourceTypeList");
            startActivityForResult(intent, REQUEST_CLOUD_RESOURCE);
        }

        @Override
        public void selectPersonlCloudResource(Handler mpHandler) {
            UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
            if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                ActivityUtils.enterLogin(MediaPaperActivity.this);
                return;
            }

//            Intent it = new Intent();
//            it.setClass(MediaPaperActivity.this, CreateSpaceActivity.class);
//            startActivityForResult(it, REQUEST_PERSONAL_CLOUD_RESOURCE);

            Intent intent = new Intent();
            intent.setClass(MediaPaperActivity.this, ShellActivity.class);
            intent.putExtra("Window", "media_type_list");
            intent.putExtra(MediaTypeListFragment.EXTRA_IS_REMOTE, true);
            intent.putExtra(MediaListFragment.EXTRA_IS_PICK, true);
            startActivityForResult(intent, REQUEST_PERSONAL_CLOUD_RESOURCE);

        }
    };


//    private void addCloudResource(ArrayList<CSEntry> datas) {
//        int insertPos = mMediaPaper.getInsertPos();
//
//        if (datas != null && datas.size() > 0) {
//            for (int i = 0; i < datas.size(); i++) {
//                if (insertPos >= 0) {
//                    insertPos += i;
//                }
//                CSEntry data = datas.get(i);
//                addCloudResource(data, insertPos);
//            }
//        }
//    }

//    private void addCloudResource(CSEntry data, int insertPos) {
//        String viewName = null;
//        String shareAddress = data.getShareAddress();
//        if (data.ResourceType == ResourceType.CHW) {
//            viewName = PaperUtils.HWPAGEVIEW;
//
//        } else if (data.ResourceType == ResourceType.VIDEO) {
//            viewName = PaperUtils.COURSEVIEW;
//            if (TextUtils.isEmpty(shareAddress)) {
//                shareAddress = ShareSettings.WAWAWEIKE_SHARE_URL + data.getMicroID();
//            }
//        }
//        if (mMediaPaper != null) {
//            CacheCloudImageTask task = new CacheCloudImageTask(viewName, data.getThumbnail(),
//                data.getTitle(), data.getResourceurl(), shareAddress, insertPos);
//            task.execute();
//        }
//    }

    private void addPersonalCloudResource(List<ResourceInfo> resourceInfos) {
        int insertPos = mMediaPaper.getInsertPos();

        if (resourceInfos != null && resourceInfos.size() > 0) {
            for (int i = 0; i < resourceInfos.size(); i++) {
                if (insertPos >= 0) {
                    insertPos += i;
                }
                ResourceInfo data = resourceInfos.get(i);

                addPersonalCloudResource(data, insertPos);
            }
        }
    }

    private void addPersonalCloudResource(ResourceInfo data, int insertPos) {
        String viewName = null;
        if (data != null) {
            switch (data.getType()) {
                case MediaType.PPT:
                case MediaType.PDF:
                case MediaType.DOC:
                case MediaType.PICTURE:
                    viewName = PaperUtils.IMAGEVIEW;
                    if (!TextUtils.isEmpty(data.getImgPath())) {
                        data.setImgPath(AppSettings.getFileUrl(data.getImgPath()));
                    } else {
                        data.setImgPath(AppSettings.getFileUrl(data.getResourcePath()));
                    }
                    break;
                case MediaType.AUDIO:
                    viewName = PaperUtils.RECORDVIEW;
                    break;
                case MediaType.VIDEO:
                    viewName = PaperUtils.VIDEOVIEW;
                    break;
                case MediaType.MICROCOURSE:
                case MediaType.ONE_PAGE:
                    if (data.getType() == MediaType.MICROCOURSE) {
                        int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
                        if (resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_OLD_COURSE) {
                            viewName = PaperUtils.COURSEVIEW;
                        } else if (resType == ResType.RES_TYPE_COURSE_SPEAKER) {
                            viewName = PaperUtils.COURSEVIEW2;
                        }
                    } else if (data.getType() == MediaType.ONE_PAGE) {
                        viewName = PaperUtils.HWPAGEVIEW;
                    }
                    break;
            }

//            if (data.getType() == MediaType.PICTURE) {
//                viewName = PaperUtils.IMAGEVIEW;
//                data.setImgPath(AppSettings.getFileUrl(data.getImgPath()));
//            } else if (data.getType() == PERSONAL_CLOUD_RESOURCE_TYPE_CMC) {
//                if (data.getResourceType() == RES_TYPE_COURSE ) {
//                    viewName = PaperUtils.COURSEVIEW;
//                } else if (data.getResourceType() == RES_TYPE_COURSE_SPEAKER ) {
//                    viewName = PaperUtils.COURSEVIEW2;
//                } else if (data.getResourceType() == RES_TYPE_ONEPAGE ) {
//                    viewName = PaperUtils.HWPAGEVIEW;
//                }
//            } else if (data.getType() == PERSONAL_CLOUD_RESOURCE_TYPE_ONEPAGE) {
//                viewName = PaperUtils.HWPAGEVIEW;
//            }

            if (mMediaPaper != null && viewName != null) {
                CacheCloudImageTask task = new CacheCloudImageTask(viewName, data.getImgPath(),
                        data.getTitle(), data.getResourcePath(), data.getShareAddress(), data.getScreenType(), insertPos);
                task.execute();
            }
        }
//        String shareAddress = data.getShareAddress();
//        if (data.ResourceType == ResourceType.CHW) {
//            viewName = PaperUtils.HWPAGEVIEW;
//
//        } else if (data.ResourceType == ResourceType.VIDEO) {
//            viewName = PaperUtils.COURSEVIEW;
//            if (TextUtils.isEmpty(shareAddress)) {
//                shareAddress = ShareSettings.WAWAWEIKE_SHARE_URL + data.getMicroID();
//            }
//        }
//        if (mMediaPaper != null) {
//            CacheCloudImageTask task = new CacheCloudImageTask(viewName, data.getThumbnail(),
//                    data.getTitle(), data.getResourceurl(), shareAddress, insertPos);
//            task.execute();
//        }
    }


    protected MediaPaper.ResourceOpenHandler mResourceOpenHandler = new MediaPaper.ResourceOpenHandler() {

        @Override
        public void openResource(int type, String resourcePath, String resourceTitle, int screentype, String webPlayUrl) {
            switch (type) {
                case MediaPaper.RESOURCE_TYPE_IMAGE:
                    break;
                case MediaPaper.RESOURCE_TYPE_VIDEO:
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    File f = new File(resourcePath);
                    String mineType = "video/*";
                    intent.setDataAndType(Uri.fromFile(f), mineType);
                    final Intent chooser = Intent.createChooser(intent, f.getName());
                    try {
                        startActivity(chooser);
                    } catch (ActivityNotFoundException e) {

                    }
                    break;
                case MediaPaper.RESOURCE_TYPE_COURSE:
                case MediaPaper.RESOURCE_TYPE_COURSE2:
                    if (!TextUtils.isEmpty(webPlayUrl)) {
                        String resId = getResIdFromShareUrl(webPlayUrl);
                        if (!TextUtils.isEmpty(resId) && TextUtils.isDigitsOnly(resId)) {
                            boolean isSplit = isSplitCourse(webPlayUrl);
                            if (!isSplit) {
                                final Dialog loadingDlg = showLoadingDialog();
                                WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(MediaPaperActivity.this);
                                wawaCourseUtils.loadCourseDetail(resId);
                                wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                                    @Override
                                    public void onCourseDetailFinish(CourseData courseData) {
                                        dismissLoadingDialog(loadingDlg);
                                        if (courseData != null) {
                                            if (mMediaPaper.isEditMode()) {
                                                PlaybackParam playbackParam = new PlaybackParam();
                                                playbackParam.mIsHideCollectTip = true;
                                                ActivityUtils.playOnlineCourse(MediaPaperActivity
                                                                .this, courseData.getCourseInfo(), false,
                                                        playbackParam);
                                            } else {
                                                ActivityUtils.openCourseDetail(MediaPaperActivity
                                                                .this, courseData.getNewResourceInfo(),
                                                        PictureBooksDetailActivity.FROM_OTHRE);
                                            }
                                        }
                                    }
                                });
                            } else {
                                final Dialog loadingDlg = showLoadingDialog();
                                WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(MediaPaperActivity.this);
                                wawaCourseUtils.loadSplitCourseDetail(Long.parseLong(resId));
                                wawaCourseUtils.setOnSplitCourseDetailFinishListener(new WawaCourseUtils.OnSplitCourseDetailFinishListener() {
                                    @Override
                                    public void onSplitCourseDetailFinish(SplitCourseInfo info) {
                                        dismissLoadingDialog(loadingDlg);
                                        if (info != null) {
                                            if (mMediaPaper.isEditMode()) {
                                                PlaybackParam playbackParam = new PlaybackParam();
                                                playbackParam.mIsHideCollectTip = true;
                                                ActivityUtils.playOnlineCourse(MediaPaperActivity
                                                        .this, info.getCourseInfo(), false, playbackParam);
                                            } else {
                                                ActivityUtils.openCourseDetail(MediaPaperActivity.this,
                                                        info.getNewResourceInfo(),
                                                        PictureBooksDetailActivity.FROM_OTHRE);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                    break;
                case MediaPaper.RESOURCE_TYPE_CHW:
                    if (!TextUtils.isEmpty(webPlayUrl)) {
                        String resId = getResIdFromShareUrl(webPlayUrl);
                        if (!TextUtils.isEmpty(resId) && TextUtils.isDigitsOnly(resId)) {
                            final Dialog loadingDlg = showLoadingDialog();
                            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(MediaPaperActivity.this);
                            wawaCourseUtils.loadCourseDetail(resId);
                            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                                @Override
                                public void onCourseDetailFinish(CourseData courseData) {
                                    dismissLoadingDialog(loadingDlg);
                                    if (courseData != null) {
                                        if (mMediaPaper.isEditMode()) {
                                            PlaybackParam playbackParam = new PlaybackParam();
                                            playbackParam.mIsHideCollectTip = true;
                                            ActivityUtils.openOnlineOnePage(MediaPaperActivity.this,
                                                    courseData.getNewResourceInfo(), false, playbackParam);
                                        } else {
                                            ActivityUtils.openCourseDetail(MediaPaperActivity.this,
                                                    courseData.getNewResourceInfo(),
                                                    PictureBooksDetailActivity.FROM_OTHRE);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    break;
            }
        }
    };


    private class CacheCloudImageTask extends AsyncTask<Void, Void, String> {
        String mImageUrl = null;
        String mViewName = null;
        String mTitle = null;
        String mResourceUrl = null;
        String mSharePlayUrl = null;
        int mOrientation = 0;
        int mInsertPos = -1;
        Dialog mLoadingDlg = null;


        CacheCloudImageTask(String viewName, String imageUrl, String title, String resourceUrl, String sharePlayUrl,
                            int orientation, int insertPos) {
            mImageUrl = imageUrl;
            if (imageUrl.contains("?")) {
                int index = imageUrl.indexOf("?");
                if (index > 0) {
                    mImageUrl = imageUrl.substring(0, index);
                }
            }
            mViewName = viewName;
            mTitle = title;
            mResourceUrl = resourceUrl;
            mSharePlayUrl = sharePlayUrl;
            mOrientation = orientation;
            mInsertPos = insertPos;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingDlg = showLoadingDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            String cacheFilePath = null;
            if (mImageUrl != null) {
                String ext = BaseUtils.getFileExt(mImageUrl);
                long time = System.currentTimeMillis();
                String filename = String.valueOf(time) + ext;
                cacheFilePath = mMediaPaper.getPathName()
                        + PaperUtils.SUB_IMAGE + File.separator + filename;
                if (new File(cacheFilePath).exists()) {
                    new File(cacheFilePath).delete();
                }

                FileApi.getFile(mImageUrl, cacheFilePath);
                if (cacheFilePath != null && new File(cacheFilePath).exists()) {
                    BitmapFactory.Options options = PaperUtils.loadBitmapOptions(cacheFilePath);
                    if (options == null ||
                            options.outWidth > PaperUtils.IMAGE_LONG_SIZE ||
                            options.outHeight > PaperUtils.IMAGE_LONG_SIZE) {
                        String newCache = mMediaPaper.writeImageToCache(cacheFilePath);
                        if (newCache != null && new File(newCache).exists()) {
                            new File(cacheFilePath).delete();
                            cacheFilePath = newCache;
                        }
                    } else if (options != null) {
                        time = System.currentTimeMillis();
                        String newCacheName = String.format("%d_%dx%d%s", System.currentTimeMillis(),
                                options.outWidth, options.outHeight, ext);
                        //String.valueOf(time) + "_"+options.outWidth+"x"+options.outHeight+ext;
                        String newCachePath = mMediaPaper.getPathName()
                                + PaperUtils.SUB_IMAGE + File.separator + newCacheName;
                        new File(cacheFilePath).renameTo(new File(newCachePath));
                        cacheFilePath = newCachePath;


                    }


                }
            }
            return cacheFilePath;
        }

        @Override
        protected void onPostExecute(String thumbPath) {
            super.onPostExecute(thumbPath);
            dismissLoadingDialog(mLoadingDlg);
            mMediaPaper.addCloudResouce(mViewName, thumbPath, mTitle,
                    AppSettings.getFileUrl(mResourceUrl), mSharePlayUrl, mOrientation, mInsertPos);
        }

    }

    public Dialog showLoadingDialog() {
        Dialog dlg = DialogHelper.getIt(this).GetLoadingDialog(0);
        return dlg;
    }

//    public void dismissLoadingDialog() {
//        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
//            mLoadingDialog.dismiss();
//        }
//    }

    public void dismissLoadingDialog(Dialog loadingDialog) {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private String getResIdFromShareUrl(String shareUrl) {
        if (TextUtils.isEmpty(shareUrl)) {
            return null;
        }
        String tagId = "play?vId=";
        int startIndex = shareUrl.indexOf(tagId);
        int endIndex = shareUrl.indexOf("&");
        if (endIndex == -1) {
            endIndex = shareUrl.length();
        }
        String id = null;
        if (startIndex < endIndex) {
            id = shareUrl.substring(startIndex + tagId.length(), endIndex);
        }
        return id;
    }

    private boolean isSplitCourse(String shareUrl) {
        boolean isSplit = false;

        if (!TextUtils.isEmpty(shareUrl)) {
            isSplit = shareUrl.contains("&subFlag=1");
        }

        return isSplit;
    }


    ////////////////////////////////////////////// online
//    private boolean mbOnlinePaper = false;


//    private String prepareOnlinePaper(String resourcePath) {
//        if (!resourcePath.startsWith("http")) {
//            return null;
//        }
////        mbOnlinePaper = true;
//        if (resourcePath.endsWith(".zip")) {
//            resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("."));
//        }
//
//        File cacheFolder = new File(CACHE_FOLDER);
//        if (cacheFolder.exists()) {
//            PaperUtils.deleteDirectory(CACHE_FOLDER);
//        }
//        cacheFolder.mkdirs();
//
//        return resourcePath;
//    }


    protected class BasePaperExitHandler implements MediaPaper.MediaPaperExitHandler {

        @Override
        public void back(boolean paperIsEdit) {
            ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                    MediaPaperActivity.this, null,
                    getString(R.string.back_or_not),
                    getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    },
                    getString(R.string.confirm),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (noteOpenParams != null && noteOpenParams.sourceType ==
                                    SourceType.STUDY_TASK) {
                                finish();
                            } else {
                                mSendSaveState = SEND_SAVE_STATE_NONE;
                                mMediaPaper.closeAndDeletePaper();
                            }
                        }
                    });
            messageDialog.show();
        }

        /**
         * 鐐瑰嚮鍙戦€佺殑鐩戝惉鏂规硶
         */
        @Override
        public void backAndSend(boolean paperIsEdit) {
            if (noteOpenParams.sourceType == SourceType.CLOUD_POST_BAR) {
                commitHelper.setIsCloudBar(true, true);
                commitHelper.commit(mMediaPaper, null);
            } else if (noteOpenParams.sourceType == SourceType.SCHOOL_SPACE
                    || noteOpenParams.sourceType == SourceType.SCHOOL_MESSAGE) {
                if (uploadParameter != null) {
                    commitNoteToSchoolSpace();
                } else {
                    if (noteOpenParams.isOnlineSchool && noteOpenParams.sourceType == SourceType.SCHOOL_MESSAGE){
                        //空中在线机构选择框
                        handleOnlineSchoolMessageType(false,null);
                    } else {
                        mSendSaveState = SEND_SAVE_STATE_SEND;
                        mMediaPaper.saveEdit();
                    }
                }
            } else if (noteOpenParams.sourceType == SourceType.CLASS_SPACE) {
                if (uploadParameter != null) {
                    commitNoteToClassSpace();
                } else {
                    mSendSaveState = SEND_SAVE_STATE_SEND;
                    mMediaPaper.saveEdit();
                }
            } else if (noteOpenParams.sourceType == SourceType.EDIT_NOTE) {
                boolean bShowUpdate = checkoutUpdateShowOrNot(paperIsEdit, mPaperPath);
                if (bShowUpdate) {
//                    if(uploadParameter != null) {
//                        if(noteInfo != null) {
//                            updateNote(noteInfo.getDateTime(), uploadParameter);
//                        }
//                    } else {
                    mSendSaveState = SEND_SAVE_STATE_UPDATE;
                    mMediaPaper.saveEdit();
//                    }
                } else {
                    mMediaPaper.setEditMode(false);
//                    if (mMediaPaperCallback != null) {
//                        mMediaPaperCallback.onUpdateFinish();
//                    }
                }
            } else if (noteOpenParams.sourceType == SourceType.CLASS_SPACE_HOMEWORK) {
                if (uploadParameter != null) {
                    enterHomeworkSendPickerActivity();
                } else {
                    mSendSaveState = SEND_SAVE_STATE_SEND;
                    mMediaPaper.saveEdit();
                }
            } else if (noteOpenParams.sourceType == SourceType.STUDY_TASK) {

                if (noteOpenParams.taskType == StudyTaskType.WATCH_HOMEWORK) {
                    if (uploadParameter != null) {
                        commitHomework(noteOpenParams.taskType);
                    } else {
                        mSendSaveState = SEND_SAVE_STATE_SEND;
                        mMediaPaper.saveEdit();
                    }
                } else if (noteOpenParams.taskType == StudyTaskType.SUBMIT_HOMEWORK) {
                    if (uploadParameter != null) {
                        commitHomework(noteOpenParams.taskType);
                    } else {
                        mSendSaveState = SEND_SAVE_STATE_SEND;
                        mMediaPaper.saveEdit();
                    }
                }
            }
        }
    }

    private boolean checkoutUpdateShowOrNot(boolean paperIsEdit, String paperPath) {
//        Toast.makeText(this, "bEdit="+paperIsEdit, Toast.LENGTH_LONG).show();
        if (paperIsEdit && noteInfo != null && noteInfo.getNoteId() > 0) {
            return true;
        }
        return false;
    }

    private void updateNote(NoteInfo noteInfo) {
        if (noteInfo == null || !noteInfo.isUpdate()) {
            TipMsgHelper.ShowLMsg(MediaPaperActivity.this, R.string.no_note_update);
            return;
        }
        UserInfo userInfo = ((MyApplication) getApplication()).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(MediaPaperActivity.this);
            return;
        }
        final long dateTime = noteInfo.getDateTime();
        uploadParameter = NoteHelper.getUploadParameter(
                userInfo, noteInfo, null, 0, mPaperType);
        uploadParameter.setNoteType(mPaperType);
        updateNote(dateTime, uploadParameter);
    }

    private void updateNote(long dateTime, final UploadParameter uploadParameter) {
        if (uploadParameter != null) {
            final Dialog loadingDlg = showLoadingDialog();
            NoteHelper.uploadNote(
                    MediaPaperActivity.this, uploadParameter, dateTime, new CallbackListener() {
                        @Override
                        public void onBack(Object result) {
                            dismissLoadingDialog(loadingDlg);
                            CourseUploadResult uploadResult = (CourseUploadResult) result;
                            if (uploadResult != null && uploadResult.getCode() == 0) {
                                if (uploadResult.data == null || uploadResult.data.size() == 0) {
                                    TipMsgHelper.ShowLMsg(MediaPaperActivity.this, R.string.update_failure);
                                } else {
                                    CourseData data = null;
                                    if (uploadResult.data.size() > 0) {
                                        data = uploadResult.data.get(0);
                                        if (data != null) {
                                            if (!TextUtils.isEmpty(data.nickname)) {
                                                courseInfo.setNickname(data.nickname);
                                            }
                                            if (!TextUtils.isEmpty(data.thumbnailurl)) {
                                                courseInfo.setImgurl(data.thumbnailurl);
                                            }
                                            if (!TextUtils.isEmpty(data.createtime)) {
                                                courseInfo.setUpdateTime(data.createtime);
                                            }
                                        }
                                    }
                                    TipMsgHelper.ShowLMsg(MediaPaperActivity.this, R.string.update_success);
                                    NoteHelper.updateBroadcast(MediaPaperActivity.this, data.getIdType(),
                                            courseInfo.getCode() + "");
                                    com.nostra13.universalimageloader.core.ImageLoader.getInstance().clearMemoryCache();
                                    if (mMediaPaperCallback != null) {
                                        mMediaPaperCallback.onUpdateFinish();
                                    } else {
                                        finish();
                                    }
                                }
                            } else {
                                mMediaPaper.setEditMode(true);
                                MediaPaperActivity.this.uploadParameter = null;
                            }
                        }
                    });
        }
    }

    private NoteInfo getNoteInfoByDataTime(long dataTime) {
        NoteDao noteDao = new NoteDao(MediaPaperActivity.this);
        try {
            NoteDTO noteDTO = noteDao.getNoteDTOByDateTime(dataTime, mPaperType);
            if (noteDTO != null) {
                return noteDTO.toNoteInfo();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateNoteInfo() {
        if (!TextUtils.isEmpty(mPaperPath)) {
            String title = new File(mPaperPath).getName();
            long dateTime = Long.parseLong(title);
            NoteInfo info = getNoteInfoByDataTime(dateTime);
            if (info != null) {
                if (noteInfo == null) {
                    noteInfo = info;
                } else {
                    noteInfo.setTitle(info.getTitle());
                    noteInfo.setIsUpdate(info.isUpdate());
                    noteInfo.setThumbnail(info.getThumbnail());
                }
            }
        }
    }

    private void commitNoteToCloudPostBar() {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(MediaPaperActivity.this);
            return;
        }
        final long dateTime = noteInfo.getDateTime();
        uploadParameter = NoteHelper.getUploadParameter(
                userInfo, noteInfo, null, 0, mPaperType);
        uploadParameter.setNoteType(mPaperType);
        uploadParameter.setColType(1);
        if (uploadParameter != null) {
            final Dialog loadingDlg = showLoadingDialog();
            NoteHelper.uploadNote(MediaPaperActivity.this, uploadParameter, dateTime, new CallbackListener() {
                @Override
                public void onBack(Object result) {
                    dismissLoadingDialog(loadingDlg);
                    CourseUploadResult uploadResult = (CourseUploadResult) result;
                    if (uploadResult != null && uploadResult.getCode() == 0) {
                        if (uploadResult.data == null || uploadResult.data.size() == 0) {
                            return;
                        }
                        CourseData courseData = uploadResult.data.get(0);
                        if (courseData != null) {
                            NoteHelper.updateNoteToPostBar(MediaPaperActivity.this, userInfo, courseData, false);
                        }
                    } else {
                        mMediaPaper.setEditMode(true);
                        MediaPaperActivity.this.uploadParameter = null;
                    }
                }
            });
        }
    }

    private void commitNoteToSchoolSpace() {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(MediaPaperActivity.this);
            return;
        }
        final long dateTime = noteInfo.getDateTime();
        uploadParameter = NoteHelper.getUploadParameter(
                userInfo, noteInfo, null, 0, mPaperType);
        uploadParameter.setNoteType(mPaperType);
        uploadParameter.setColType(1);
        uploadParameter.setSourceType(noteOpenParams.sourceType);
        uploadParameter.setShareType(shareType);
        if (noteOpenParams != null) {
            uploadParameter.setSchoolIds(noteOpenParams.schoolId);
        }
        if (uploadParameter != null) {
            if (noteOpenParams.sourceType == SourceType.SCHOOL_SPACE) {
                enterSchoolCourseCategorySelector(noteOpenParams.schoolId);
            } else if (noteOpenParams.sourceType == SourceType.SCHOOL_MESSAGE) {
                final Dialog loadingDlg = showLoadingDialog();
                NoteHelper.uploadNote(MediaPaperActivity.this, uploadParameter, dateTime, new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        dismissLoadingDialog(loadingDlg);
                        CourseUploadResult uploadResult = (CourseUploadResult) result;
                        if (uploadResult != null && uploadResult.getCode() == 0) {
                            if (uploadResult.data == null || uploadResult.data.size() == 0) {
                                return;
                            }
                            CourseData courseData = uploadResult.data.get(0);
                            if (courseData != null) {
                                if (noteOpenParams.sourceType == SourceType.SCHOOL_SPACE) {
                                    NoteHelper.updateNoteToSchoolCourse(MediaPaperActivity.this, userInfo, courseData, noteOpenParams.schoolId);
                                } else if (noteOpenParams.sourceType == SourceType.SCHOOL_MESSAGE) {
                                    NoteHelper.updateNoteToSchoolMovement(
                                            MediaPaperActivity.this,
                                            userInfo,
                                            courseData,
                                            noteOpenParams.schoolId,
                                            noteOpenParams.isOnlineSchool,
                                            getSelectTypeId(),
                                            noteOpenParams.isTeacher);
                                }
                            }
                        } else {
                            mMediaPaper.setEditMode(true);
                            MediaPaperActivity.this.uploadParameter = null;
                        }
                    }
                });
            } else if (noteOpenParams.sourceType == SourceType.CLOUD_POST_BAR) {
                NoteHelper.enterContactsPicker(MediaPaperActivity.this, uploadParameter,
                        noteInfo, noteInfo.getTitle(), null, PublishResourceFragment.Constants.TYPE_SCHOOL_COURSE, false);
                finish();
            }
        }
    }

    private void handleOnlineSchoolMessageType(final boolean isShare, final CourseData courseData){
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                MediaPaperActivity.this,
                "",
                R.layout.layout_change_selected_icon,
                getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                },
                getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isShare){
                            //分享到校园动态
                            shareToSchoolMessageSpace(courseData);
                        } else {
                            mSendSaveState = SEND_SAVE_STATE_SEND;
                            mMediaPaper.saveEdit();
                        }
                    }
                });
        handlePopMessageDialogData(messageDialog);
        messageDialog.show();
    }

    private void handlePopMessageDialogData(ContactsMessageDialog messageDialog){
        TextView messageTitle = (TextView) messageDialog.getContentView().findViewById(R.id.contacts_dialog_content_title);
        messageTitle.setText(R.string.str_choose_online_message_content_type);
        onlineMessageTypeGroup = (RadioGroup) messageDialog.getContentView().findViewById(R.id.radio_group);
        onlineMessageTypeGroup.removeAllViews();
        String [] messageTypeArray = getResources().getStringArray(R.array.str_online_school_message_type);
        for (int i = 0; i < 4;i++){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutParams(new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT,RadioGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setTextSize(18);
            radioButton.setPadding(10,10,10,10);
            radioButton.setText(messageTypeArray[i]);
            radioButton.setId(5 + i);
            radioButton.setChecked(false);
            radioButton.setButtonDrawable(getResources().getDrawable(R.drawable.com_checkbox));
            onlineMessageTypeGroup.addView(radioButton);
        }
        onlineMessageTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0;i < 4;i++){
                    RadioButton  button = (RadioButton) group.getChildAt(i);
                    if (checkedId == button.getId()){
                        button.setChecked(true);
                        button.setTextColor(getResources().getColor(R.color.text_green));
                    } else {
                        button.setChecked(false);
                        button.setTextColor(getResources().getColor(R.color.text_black));
                    }
                }
            }
        });
        onlineMessageTypeGroup.check(5);
    }

    private int getSelectTypeId(){
        int messageTypeId = 0;
        if (onlineMessageTypeGroup != null){
            for (int i = 0;i < 4;i++){
                RadioButton  button = (RadioButton) onlineMessageTypeGroup.getChildAt(i);
                if (button.isChecked()){
                    messageTypeId = button.getId();
                    break;
                }
            }
        }
        return messageTypeId;
    }


    private void enterSchoolCourseCategorySelector(String schoolId) {
        Intent intent = new Intent();
        intent.setClass(MediaPaperActivity.this, SchoolCourseCategorySelectorActivity.class);
        intent.putExtra(SchoolCourseCategorySelectorActivity.EXTRA_MODE, SchoolCourseCategorySelectorActivity.UPLOAD_MODE);
        intent.putExtra(SchoolCourseCategorySelectorActivity.EXTRA_SCHOOL_ID, schoolId);
        intent.putExtra(UploadParameter.class.getSimpleName(), uploadParameter);
        intent.putExtra(NoteInfo.class.getSimpleName(), noteInfo);
        startActivity(intent);
        finish();
    }

    private void commitNoteToClassSpace() {
        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(MediaPaperActivity.this);
            return;
        }
        final long dateTime = noteInfo.getDateTime();
        uploadParameter = NoteHelper.getUploadParameter(
                userInfo, noteInfo, null, 0, mPaperType);

        if (uploadParameter != null) {
            uploadParameter.setNoteType(mPaperType);
            uploadParameter.setColType(1);
            uploadParameter.setShareType(shareType);
            if (noteOpenParams == null) {
                return;
            }
            uploadParameter.setSourceType(noteOpenParams.sourceType);
            if (noteOpenParams.sourceType == SourceType.CLASS_SPACE) {
                final Dialog loadingDlg = showLoadingDialog();
                NoteHelper.uploadNote(MediaPaperActivity.this, uploadParameter, dateTime, new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        dismissLoadingDialog(loadingDlg);
                        CourseUploadResult uploadResult = (CourseUploadResult) result;
                        if (uploadResult != null && uploadResult.getCode() == 0) {
                            if (uploadResult.data == null || uploadResult.data.size() == 0) {
                                return;
                            }
                            CourseData courseData = uploadResult.data.get(0);
                            if (courseData != null) {
                                NoteHelper.updateNoteToClassSpace(MediaPaperActivity.this, userInfo, courseData,
                                        noteOpenParams.schoolId, noteOpenParams.classId, transferType(noteOpenParams.shareType));
                            }
                        } else {
                            mMediaPaper.setEditMode(true);
                            MediaPaperActivity.this.uploadParameter = null;
                        }
                    }
                });
            } else if (noteOpenParams.sourceType == SourceType.CLOUD_POST_BAR) {
                if (uploadParameter != null) {
                    if (uploadParameter.getShareType() == ShareType.SHARE_TYPE_HOMEWORK) {
                        enterHomeworkSendPickerActivity();
                    } else {
                        NoteHelper.enterContactsPicker(MediaPaperActivity.this, uploadParameter,
                                noteInfo, noteInfo.getTitle(), null, PublishResourceFragment.Constants.TYPE_CLASS_SPACE, false);
                    }
                    finish();
                }
            }
        }
    }

    private int transferType(int shareType) {
        int type = -1;
        switch (shareType) {
            case ShareType.SHARE_TYPE_NOTICE:
                type = 2;
                break;
            case ShareType.SHARE_TYPE_HOMEWORK:
                type = 1;
                break;
            case ShareType.SHARE_TYPE_COMMENT:
                type = 3;
                break;
            case ShareType.SHARE_TYPE_COURSE:
                type = 4;
                break;
        }
        return type;
    }

    private void shareNote(final int shareType) {
        if (noteInfo != null) {
            if (noteInfo.getNoteId() > 0 && !noteInfo.isUpdate()) {
                final Dialog loadingDlg = showLoadingDialog();
                WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(MediaPaperActivity.this);
                wawaCourseUtils.loadCourseDetail(String.valueOf(noteInfo.getNoteId()));
                wawaCourseUtils.setOnCourseDetailFinishListener(
                        new WawaCourseUtils.OnCourseDetailFinishListener() {
                            @Override
                            public void onCourseDetailFinish(CourseData data) {
                                dismissLoadingDialog(loadingDlg);
                                if (data != null) {
                                    ShareInfo shareInfo = data.getShareInfo(MediaPaperActivity.this);
                                    if (shareInfo != null) {
                                        commitHelper.shareTo(shareType, shareInfo);
//                                        shareNote(shareType, shareInfo);
                                    }
                                }
                            }
                        });
            } else {
                if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                    ActivityUtils.enterLogin(MediaPaperActivity.this);
                    return;
                }
                final long dateTime = noteInfo.getDateTime();
                uploadParameter = NoteHelper.getUploadParameter(
                        userInfo, noteInfo, null, 0, mPaperType);
                uploadParameter.setNoteType(mPaperType);
                uploadParameter.setColType(1);
                if (uploadParameter != null) {
                    final Dialog loadingDlg = showLoadingDialog();
                    NoteHelper.uploadNote(MediaPaperActivity.this, uploadParameter, dateTime, new CallbackListener() {
                        @Override
                        public void onBack(Object result) {
                            dismissLoadingDialog(loadingDlg);
                            CourseUploadResult uploadResult = (CourseUploadResult) result;
                            if (uploadResult != null && uploadResult.getCode() == 0) {
                                if (uploadResult.data == null || uploadResult.data.size() == 0) {
                                    return;
                                }
                                CourseData courseData = uploadResult.data.get(0);
                                if (courseData != null) {
                                    NoteHelper.updateNoteToPostBar(MediaPaperActivity.this, userInfo, courseData, true);
                                    ShareInfo shareInfo = courseData.getShareInfo(MediaPaperActivity.this);
                                    commitHelper.shareTo(shareType, shareInfo);
//                                    shareNote(shareType, shareInfo);
                                }
                            } else {
                                mMediaPaper.setEditMode(true);
                                MediaPaperActivity.this.uploadParameter = null;
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 升级友盟分享SDK后，如果链接对应的缩略图不存在，那么就不能分享，故作此检查；
     * 如果返回链接对应的缩略图存在或为空，可忽略该检查。
     *
     * @param shareType
     * @param shareInfo
     */
    UMImage thumb;

    private void shareNote(int shareType, ShareInfo shareInfo) {
        if (!isCheckNoteThumbnail) {
            commitHelper.shareTo(shareType, shareInfo);
            return;
        }
        UMImage umImage = (UMImage) shareInfo.getuMediaObject();
        final String imgUrl = umImage.asUrlImage();

        if (!TextUtils.isEmpty(imgUrl)) {
            ThumbTask task = new ThumbTask(shareInfo, shareType);
            task.execute(imgUrl);

        } else {
//            thumb  = new UMImage(this, R.drawable.ic_launcher);
//            shareInfo.setuMediaObject(thumb);
            commitHelper.shareTo(shareType, shareInfo);
        }

    }


    private class ThumbTask extends AsyncTask<String, Void, Boolean> {
        ShareInfo shareInfo;
        int shareType;

        ThumbTask(ShareInfo shareInfo, int shareType) {
            this.shareInfo = shareInfo;
            this.shareType = shareType;
        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Boolean doInBackground(String... params) {

            return getImageFromNet(params[0]);
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Boolean b) {
            if (!b) {
                thumb = new UMImage(MediaPaperActivity.this, R.drawable.ic_launcher);
                shareInfo.setuMediaObject(thumb);
            }
            commitHelper.shareTo(shareType, shareInfo);
        }


    }

    private boolean getImageFromNet(String url) {
        HttpURLConnection conn = null;
        try {
            URL mURL = new URL(url);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET"); //设置请求方法
            conn.setConnectTimeout(10000); //设置连接服务器超时时间
            conn.setReadTimeout(5000);  //设置读取数据超时时间
            conn.connect(); //开始连接

            int responseCode = conn.getResponseCode(); //得到服务器的响应码
            if (responseCode == 200) {
                //访问成功
                return true;

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect(); //断开连接
            }
        }
        return false;
    }

    private void enterHomeworkSendPickerActivity() {
        if (noteInfo != null) {
            uploadParameter = NoteHelper.getUploadParameter(
                    userInfo, noteInfo, null, 0, mPaperType);
            if (uploadParameter != null) {
                uploadParameter.setNoteType(mPaperType);
                uploadParameter.setSourceType(noteOpenParams.sourceType);
                uploadParameter.setShareType(ShareType.SHARE_TYPE_HOMEWORK);
                uploadParameter.setColType(1);
                if (noteOpenParams != null) {
                    List<ShortSchoolClassInfo> shortSchoolClassInfos = new ArrayList<ShortSchoolClassInfo>();
                    ShortSchoolClassInfo shortSchoolClassInfo = new ShortSchoolClassInfo();
                    shortSchoolClassInfo.setSchoolId(noteOpenParams.schoolId);
                    shortSchoolClassInfo.setSchoolName("");
                    shortSchoolClassInfo.setClassId(noteOpenParams.classId);
                    shortSchoolClassInfo.setClassName("");
                    shortSchoolClassInfos.add(shortSchoolClassInfo);
                    uploadParameter.setShortSchoolClassInfos(shortSchoolClassInfos);

                }
            }
        }
        Intent intent = new Intent(MediaPaperActivity.this, HomeworkSendPickerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
        if (noteInfo != null) {
            bundle.putParcelable(NoteInfo.class.getSimpleName(), noteInfo);
        }
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    protected CustomizeParams packageCustomizeParams(boolean bEditMode) {
        CustomizeParams obj = new CustomizeParams();
        if (bEditMode) {
            obj.setPaperType(mPaperType);
        }
        obj.setbTableDevice(bPad);
        obj.setbEdit(bEditMode);
        obj.setSupportCreateMaterialType(PaperUtils.SUPPORT_PERSONAL_MATERIAL_TYPE);//SUPPORT_PERSONAL_MATERIAL_TYPE
        return obj;
    }

    public void setMediaPaperCallback(MediaPaperCallback callback) {
        mMediaPaperCallback = callback;
    }

    public interface MediaPaperCallback {
        public void onUpdateFinish();

        public void closeEditFinish();
    }

    private void commitHomework(int taskType) {
        if (noteInfo != null) {
            UploadParameter uploadParameter = UploadReourceHelper.getUploadParameter(userInfo, noteInfo,
                    null, 5);
            if (uploadParameter != null) {
                uploadParameter.setTaskType(taskType);
                String headerTitle = null;
                if (taskType == StudyTaskType.WATCH_HOMEWORK) {
                    //“看作业”和“交作业”统一为“作业”
                    headerTitle = getString(R.string.n_create_task, getString(R.string.homeworks));
                } else if (taskType == StudyTaskType.SUBMIT_HOMEWORK) {
                    headerTitle = getString(R.string.n_create_task, getString(R.string.homeworks));
                }

//                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Bundle args = getIntent().getExtras();
                if (args == null) {
                    args = new Bundle();
                }
                args.putSerializable(UploadParameter.class.getSimpleName(), uploadParameter);
                args.putSerializable(ActivityUtils.EXTRA_HEADER_TITLE, headerTitle);
                args.putParcelable(NoteInfo.class.getSimpleName(), noteInfo);
//                PublishStudyTaskFragment fragment = new PublishStudyTaskFragment();
//                fragment.setArguments(args);
//                ft.replace(R.id.activity_body, fragment, PublishStudyTaskFragment.TAG);
//                ft.addToBackStack(null);
//                ft.commit();
//                if(getActivity() != null) {
//                    getActivity().finish();
//                }
                Intent intent = new Intent();
                intent.putExtras(args);
                if (this != null) {
                    this.setResult(Activity.RESULT_OK, intent);
                    this.finish();
                }
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecordingAudio();
    }

    private void stopRecordingAudio() {
        if (mMediaPaper != null) {
            AudioPopwindow audioPopwindow = mMediaPaper.getAudioPopwindow();
            if (audioPopwindow != null && audioPopwindow.isShowing()) {
                audioPopwindow.stopRecordingAudio();
            }
        }
    }

    public UserInfo getUserInfo() {
        if ((MyApplication) this.getApplication() != null) {
            return ((MyApplication) this.getApplication()).getUserInfo();
        }
        return null;
    }

    public static void setHasResourceSended(boolean hasResourceSended) {
        MediaPaperActivity.hasResourceSended = hasResourceSended;
    }

    public static boolean hasResourceSended() {
        return hasResourceSended;
    }
}
