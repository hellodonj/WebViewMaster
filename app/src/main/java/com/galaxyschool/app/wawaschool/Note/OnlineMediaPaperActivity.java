package com.galaxyschool.app.wawaschool.Note;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.CollectionHelper;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.ShareUtils;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.UIUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.common.WawaCourseUtils;
import com.galaxyschool.app.wawaschool.common.ZipFileUtil;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.db.NoteDao;
import com.galaxyschool.app.wawaschool.db.dto.NoteDTO;
import com.galaxyschool.app.wawaschool.fragment.NoteCommentFragment;
import com.galaxyschool.app.wawaschool.fragment.library.TipsHelper;
import com.lqwawa.lqbaselib.net.FileApi;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.galaxyschool.app.wawaschool.pojo.RoleType;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfoResult;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.NoteOpenParams;
import com.lqwawa.libs.mediapaper.MediaPaper;
import com.lqwawa.libs.mediapaper.PaperUtils;
import com.lqwawa.libs.mediapaper.io.Saver;
import com.lqwawa.libs.mediapaper.player.CustomizeParams;
import com.oosic.apps.share.ShareInfo;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pp on 15/11/25.
 */
public class OnlineMediaPaperActivity extends MediaPaperActivity
        implements MediaPaperActivity.MediaPaperCallback, MediaPaper.PlayerVideoHandler {


    private View rootView;
    private EditText commentEdit;
    private boolean isPraise;
    NoteCommentFragment noteCommentFragment;
    private UserInfo otherUserInfo;
    private NoteDao noteDao;
    private String mOnlinePaperPath = null;
    private static boolean hasMicroCourseCollected;//收藏
    private static boolean hasContentChanged;//包含了点赞、收藏、编辑、评论。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView commentSend = (TextView)findViewById(R.id.comment_send_btn);
        commentSend.setText(R.string.send);
        commentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEdit = (EditText) findViewById(R.id.comment_send_content);
                String comment = commentEdit.getEditableText().toString();
                sendComment(comment);
            }
        });

        if (courseInfo != null && courseInfo.getId() > 0) {
            mMediaPaper.setupSubTitle(getSubTitle(courseInfo));
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(OnlineMediaPaperActivity.this);
            wawaCourseUtils.loadCourseDetail(String.valueOf(courseInfo.getId()));
            wawaCourseUtils.setOnCourseDetailFinishListener(new WawaCourseUtils.OnCourseDetailFinishListener() {
                @Override
                public void onCourseDetailFinish(CourseData courseData) {
                    if (courseData != null) {
                        courseInfo.setPraisenum(courseData.praisenum);
                        courseInfo.setCommentnum(courseData.commentnum);
                        courseInfo.setShareAddress(courseData.shareurl);
                        mMediaPaper.updatePraiseNumber(courseData.praisenum);
                    }
                }
            });
            mMediaPaper.setChatBtnVisible(View.GONE);
            mMediaPaper.setCollectBtnVisible(View.VISIBLE);
            if (userInfo != null) {
                commitHelper.setIsTeacher(userInfo.isTeacher());
            }
            loadUserInfo(courseInfo.getCode());
            //控制按钮可见性
            controlButtonVisibility();
            if (!isCloudBar){
                //不是云贴吧要隐藏编辑按钮
                mMediaPaper.setEditBtnVisible(View.GONE);
            }
        }
        updateCommentList();

        noteDao = new NoteDao(OnlineMediaPaperActivity.this);

        setMediaPaperCallback(this);

        mOnlinePaperPath = mPaperPath;
    }

    private String getSubTitle(CourseInfo courseInfo) {
        if (courseInfo == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(courseInfo.getUpdateTime())) {
            builder.append(courseInfo.getCreatetime() + " ");
        } else {
            builder.append(courseInfo.getUpdateTime() + " ");
        }

        if (!TextUtils.isEmpty(courseInfo.getCreatename())) {
            builder.append(getString(R.string.author) + " " + courseInfo.getCreatename() + " ");
        }
        if (!TextUtils.isEmpty(courseInfo.getSchoolName())) {
            builder.append(courseInfo.getSchoolName());
        }
        return builder.toString();
    }

    /**
     * 控制按钮的可见性
     */
    private void controlButtonVisibility(){
        boolean isMyself = isMySelf(courseInfo.getCode());
        if (!isMyself) {
            if (mMediaPaper != null) {
                mMediaPaper.setFollowBtnVisible(View.VISIBLE);
                mMediaPaper.setPraiseBtnVisible(View.VISIBLE);
                mMediaPaper.setEditBtnVisible(View.GONE);
            }
        } else {
            if (mMediaPaper != null) {
                mMediaPaper.setFollowBtnVisible(View.GONE);
                mMediaPaper.setPraiseBtnVisible(View.GONE);
                if (!courseInfo.isCampusPatrolTag) {
                    mMediaPaper.setEditBtnVisible(!isStudyTask ? View.VISIBLE : View.GONE);
                }else {
                    //校园巡查需要隐藏编辑按钮
                    mMediaPaper.setEditBtnVisible(View.GONE);
                }
            }
            noteOpenParams = new NoteOpenParams();
            noteOpenParams.sourceType = SourceType.EDIT_NOTE;
        }
    }

    @Override
    protected MediaPaper mySetContentView() {
        setContentView(R.layout.activity_online_mediapaper);
        MediaPaper mediaPaper = (MediaPaper) findViewById(R.id.mediapaper);
        mediaPaper.setmPlayerVideoHandler(this);
        rootView = findViewById(R.id.root_layout);
        return mediaPaper;
    }

    @Override
    protected void paperInitialize(MediaPaper mediaPaper) {
//        if (mPaperPath.startsWith("http")) {
//            mPaperPath = prepareOnlinePaper(mPaperPath);
//            mOpenType = MediaPaper.LOAD_HISTORY;
//            mediaPaper.onlinePaperInitialize(this, mPaperPath, CACHE_FOLDER, mResourceOpenHandler, bPad, mFeedbackHandler);
//            mediaPaper.setMediasPreviewListener(mMediasPreviewLsn);
//        } else {
//            super.paperInitialize(mediaPaper);
//        }
        if (mPaperPath.startsWith("http")) {
            mediaPaper.setmPaperMode(paperMode);//PaperUtils.HOMEWORK_MODE);
            mPaperPath = prepareOnlinePaper(mPaperPath);
            mOpenType = MediaPaper.LOAD_HISTORY;
            CustomizeParams obj = packageCustomizeParams(false);
            mediaPaper.onlinePaperInitialize(this, mPaperPath, CACHE_FOLDER,
                    mResourceOpenHandler,mFeedbackHandler,obj);
            mediaPaper.setMediasPreviewListener(mMediasPreviewLsn);
        } else {
            mMediaPaper.setmPaperMode(paperMode);
            super.paperInitialize(mediaPaper);
        }
    }

    private String prepareOnlinePaper(String resourcePath) {
        if (!resourcePath.startsWith("http")) {
            return null;
        }
        if (resourcePath.endsWith(".zip")) {
            resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("."));
        }

        File cacheFolder = new File(CACHE_FOLDER);
        if (cacheFolder.exists()) {
            PaperUtils.deleteDirectory(CACHE_FOLDER);
        }
        cacheFolder.mkdirs();

        return resourcePath;
    }

    private MediaPaper.FeedbackHandler mFeedbackHandler = new MediaPaper.FeedbackHandler() {

        @Override
        public void chat() {

        }

        @Override
        public void follow() {
            followMe();
        }

        @Override
        public void share() {
            if (courseInfo == null) {
                return;
            }
            boolean isTeacher=false;
            String roles=userInfo.getRoles();
            if (!TextUtils.isEmpty(roles)){
                int roleType=-1;
                if (roles.contains(",")){
                    String [] rolesData=roles.split(",");
                    for (int i=0;i<rolesData.length;i++){
                        roleType=Integer.valueOf(rolesData[i]);
                        if (roleType==RoleType.ROLE_TYPE_TEACHER){
                            isTeacher=true;
                        }
                    }
                }else {
                    roleType=Integer.valueOf(roles);
                    if (roleType==RoleType.ROLE_TYPE_TEACHER){
                        isTeacher=true;
                    }
                }
            }
            //不是自己制作的课件
            if (!isMySelf(courseInfo.getCode())) {
                if (courseInfo.isTeacher()&&isTeacher&&courseInfo.isFromShowShow()||(courseInfo
                        .isCampusPatrolTag&&courseInfo.isFromShowShow())) {
                    commitHelper.setIsFromShowShow(true);
                    commitHelper.commit(mMediaPaper, null);
                    isTempData = true;
                }else {
                    ShareInfo shareInfo = courseInfo.getShareInfo(OnlineMediaPaperActivity.this);
                    if (shareInfo != null) {
                        shareInfo.setSharedResource(courseInfo.getSharedResource());
                    }
                    new ShareUtils(OnlineMediaPaperActivity.this).share(rootView, shareInfo);
                }
            } else {
                //自己制作的课件
                if (isTeacher&&courseInfo.isFromShowShow()||(courseInfo
                        .isCampusPatrolTag&&courseInfo.isFromShowShow())) {
                    commitHelper.setIsFromShowShow(true);
//                    isTempData=true;
                }
                commitHelper.setIsCloudBar(isCloudBar);
                commitHelper.commit(mMediaPaper, null);
            }
        }

        @Override
        public void praise() {
            if (courseInfo == null) {
                return;
            }

            if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                TipMsgHelper.ShowLMsg(OnlineMediaPaperActivity.this, R.string.pls_login);
                return;
            }

            if (isPraise) {
                TipMsgHelper.ShowLMsg(OnlineMediaPaperActivity.this, R.string.have_praised);
                return;
            } else {
                isPraise = true;
            }
            WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(OnlineMediaPaperActivity.this);
            wawaCourseUtils.praiseCourse(String.valueOf(courseInfo.getId()), 0, courseInfo.getResourceType());
//            wawaCourseUtils.setOnCoursePraiseFinishListener(new WawaCourseUtils.OnCoursePraiseFinishListener() {
//                @Override
//                public void onCoursePraiseFinish(String courseId, int code, int praiseNum) {
//                    if(code == 0) {
//                        mMediaPaper.updatePraiseNumber(praiseNum);
//                    }
//                }
//            });
        }

        @Override
        public void edit() {
            editNote();
        }

        @Override
        public void collect() {
            colectResource();
        }
    };

    private void followMe() {
        if (courseInfo == null || TextUtils.isEmpty(courseInfo.getCode())) {
            return;
        }

        if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
            ActivityUtils.enterLogin(OnlineMediaPaperActivity.this);
            return;
        }
        Map<String, Object> params = new HashMap();
        params.put("FAttentionId", userInfo.getMemberId());
        params.put("TAttentionId", courseInfo.getCode());
        RequestHelper.RequestDataResultListener<DataModelResult> listener =
            new RequestHelper.RequestDataResultListener<DataModelResult>(OnlineMediaPaperActivity.this,
                DataModelResult.class) {
                @Override
                public void onSuccess(String jsonString) {
                    super.onSuccess(jsonString);
                    DataModelResult result = getResult();
                    if (result != null){
                        if (result.isSuccess()){
                            TipsHelper.showToast(OnlineMediaPaperActivity.this, R.string.subscribe_success);
                        }else {
                            String errorMessage = result.getErrorMessage();
                            if (result.getErrorCode() == -1){
                                //已收藏
                                errorMessage = getString(R.string.subscribed);
                            }
                            TipMsgHelper.ShowLMsg(OnlineMediaPaperActivity.this, errorMessage);
                        }
                    }
                }
            };
        String serverUrl = ServerUrl.SUBSCRIBE_ADD_PERSON_URL;
        listener.setShowErrorTips(false);
        RequestHelper.sendPostRequest(OnlineMediaPaperActivity.this, serverUrl, params, listener);
    }

    private void editNote() {
        if (courseInfo != null) {
            if (courseInfo.getId() > 0) {
                try {
                    NoteDTO noteDTO = noteDao.getNoteDTOByNoteId(courseInfo.getId(), MediaPaper.PAPER_TYPE_TIEBA);
                    if (noteDTO != null) {
                        if (noteDTO.getDateTime() > 0) {
                            File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(noteDTO.getDateTime()));
                            if (noteFile != null && noteFile.exists()) {
                                openLocalNote(noteFile.getPath());
                            } else {
                                DownloadNoteTask downloadNoteTask = new DownloadNoteTask(OnlineMediaPaperActivity.this, courseInfo.getResourceurl());
                                downloadNoteTask.execute();
                            }
                        }
                    } else {
                        DownloadNoteTask downloadNoteTask = new DownloadNoteTask(OnlineMediaPaperActivity.this, courseInfo.getResourceurl());
                        downloadNoteTask.execute();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void colectResource() {
        if (courseInfo != null) {
            CollectionHelper helper = new CollectionHelper(OnlineMediaPaperActivity.this);
            helper.collectResource(courseInfo.getIdType(), courseInfo.getNickname(), courseInfo.getCode());
        }
    }

    private void sendComment(String commentStr) {
        if (TextUtils.isEmpty(commentStr)) {
            TipMsgHelper.ShowLMsg(OnlineMediaPaperActivity.this, R.string.pls_input_comment_content);
            return;
        }
        UIUtils.hideSoftKeyboard(OnlineMediaPaperActivity.this);
        if (courseInfo != null) {
            sendComment(courseInfo.getId(), commentStr);
        }
    }

    private void sendComment(long resId, String content) {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(OnlineMediaPaperActivity.this);
        wawaCourseUtils.sendComment(resId, content);
        wawaCourseUtils.setOnCommentSendFinishListener(new WawaCourseUtils.OnCommentSendFinishListener() {
            @Override
            public void onCommentSendFinish(int code) {
                if (code == 0) {
                    //设置已经评论过
                    setHasContentChanged(true);
                    noteCommentFragment.updateComments();
                    if (commentEdit != null) {
                        commentEdit.setText("");
                    }
                }
            }
        });
    }

    private void updateCommentList() {
        if (courseInfo != null) {
            LinearLayout attachLayout = (LinearLayout) findViewById(R.id.attach_layout);
            if(attachLayout != null) {
                attachLayout.removeAllViews();
            }
            Bundle args = new Bundle();
            args.putParcelable(CourseInfo.class.getSimpleName(), courseInfo);
            noteCommentFragment = new NoteCommentFragment();
            noteCommentFragment.setArguments(args);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.attach_layout, noteCommentFragment, NoteCommentFragment.TAG);
            ft.commit();
        }
    }

    private void loadUserInfo(String userId) {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", userInfo.getMemberId());
        params.put("UserId", userId);
        RequestHelper.sendPostRequest(OnlineMediaPaperActivity.this, ServerUrl.LOAD_USERINFO_URL,
            params, new Listener<String>() {
                @Override
                public void onSuccess(String jsonString) {
                    UserInfoResult result = JSON.parseObject(jsonString, UserInfoResult.class);
                    if (result == null || !result.isSuccess()) {
                        return;
                    }
                    otherUserInfo = result.getModel();
                    updateUserInfoViews();
//                    if (otherUserInfo != null) {
//                        commitHelper.setIsTeacher(otherUserInfo.isTeacher());
//                    }
                }
            });
    }

    private void updateUserInfoViews() {
        boolean isMySelef = isMySelf(courseInfo.getCode());
        if (isMySelef) {
            mMediaPaper.setFollowBtnVisible(View.GONE);
        } else {
//            if (otherUserInfo != null && mMediaPaper != null) {
//                mMediaPaper.setFollowBtnVisible(otherUserInfo.hasSubscribed() ? View.GONE : View.VISIBLE);
//            }
        }
    }

    private boolean isMySelf(String userId) {
        if (!TextUtils.isEmpty(userId) && userInfo != null
            && !TextUtils.isEmpty(userInfo.getMemberId())
            && (userId.equals(userInfo.getMemberId()))) {
            return true;
        }
        return false;
    }

    private void openLocalNote(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        String title = Utils.getFileNameFromPath(path);
        long dateTime = Long.parseLong(title);
        NoteInfo noteInfo = null;
        try {
            NoteDTO noteDTO = noteDao.getNoteDTOByDateTime(dateTime, MediaPaper.PAPER_TYPE_TIEBA);
            if (noteDTO != null) {
                noteInfo = noteDTO.toNoteInfo();
                noteInfo.setNoteType(MediaPaper.PAPER_TYPE_TIEBA);
                noteInfo.setIsUpdate(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
        String dateTimeStr = DateUtils.transferLongToDate("yyyy-MM-dd HH:mm:ss", dateTime);
        NoteOpenParams openParams = new NoteOpenParams(noteFile.getPath(), dateTimeStr,
            MediaPaperActivity.OPEN_TYPE_EDIT, MediaPaper.PAPER_TYPE_TIEBA, null,
            SourceType.EDIT_NOTE, false);
        openParams.noteInfo = noteInfo;

        mPaperPath = noteFile.getPath();
        isEditable = true;

        // 编辑只有文本内容的帖子时，需要手动创建子目录images, audio , thumb, video
        PaperUtils.createNew(OnlineMediaPaperActivity.this, mPaperPath, true);

        View commentGrp = findViewById(R.id.comment_grp);
        if (commentGrp != null) {
            commentGrp.setVisibility(View.GONE);
        }
        paperInitialize(mMediaPaper);
        OnlineMediaPaperActivity.this.noteInfo = noteInfo;
        mMediaPaper.setEditMode(true);
        if (noteInfo != null && courseInfo != null) {
            noteInfo.setResourceUrl(courseInfo.getResourceurl());
        }
//        ActivityUtils.openNote(OnlineMediaPaperActivity.this, openParams);
//        OnlineMediaPaperActivity.this.finish();


    }

    private void allowComment(boolean isAllow) {
        View commonLayout  = findViewById(R.id.comment_grp);
        commonLayout.setVisibility(isAllow ? View.VISIBLE : View.INVISIBLE);
    }

    private View getCommentList() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (noteCommentFragment != null) {
            noteCommentFragment.updateComments();
        }
    }

    private MediaPaper.MediasPreviewListener mMediasPreviewLsn = new MediaPaper.MediasPreviewListener() {
        @Override
        public void onStart() {
            View commentGrp = findViewById(R.id.comment_grp);
            if (commentGrp != null) {
                commentGrp.setVisibility(View.GONE);
            }
        }

        @Override
        public void onStop() {
            View commentGrp = findViewById(R.id.comment_grp);
            if (commentGrp != null) {
                commentGrp.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onUpdateFinish() {
        //编辑过
        setHasContentChanged(true);
        mMediaPaper.setOnline(true);
        mMediaPaper.switchCustomizeTitle();
        View commentGrp = findViewById(R.id.comment_grp);
        if (commentGrp != null) {
            commentGrp.setVisibility(View.VISIBLE);
        }

        //更新帖子副标题日期
        mMediaPaper.setupSubTitle(getSubTitle(courseInfo));

    }

    @Override
    public void closeEditFinish() {
        if (mOnlinePaperPath != null) {
            mPaperPath = mOnlinePaperPath;
        }
        View commentGrp = findViewById(R.id.comment_grp);
        if (commentGrp != null) {
            commentGrp.setVisibility(View.VISIBLE);
        }
        paperInitialize(mMediaPaper);
    }

    @Override
    public void startPlayVideo() {
        allowComment(false);
    }

    @Override
    public void stopPlayVideo() {
        allowComment(true);
    }


    private class DownloadNoteTask extends AsyncTask<Void, Void, String> {

        Context context;
        String noteUrl;
        Dialog loadingDlg = null;

        public DownloadNoteTask(Context context, String noteUrl) {
            this.context = context;
            this.noteUrl = noteUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDlg = showLoadingDialog();
        }

        @Override
        protected String doInBackground(Void... params) {
            String cacheFilePath = null;
            if (!TextUtils.isEmpty(noteUrl)) {
                String tempFilePath = Utils.TEMP_FOLDER + Utils.getFileNameFromPath(noteUrl);
                if (new File(tempFilePath).exists()) {
                    new File(tempFilePath).delete();
                }

                FileApi.getFile(noteUrl, tempFilePath);
                if (!TextUtils.isEmpty(tempFilePath) && new File(tempFilePath).exists()) {
                    try {
                        long dateTime = System.currentTimeMillis();
                        File noteFile = new File(Utils.NOTE_FOLDER, String.valueOf(dateTime));
                        cacheFilePath = noteFile.getPath();
//                        Utils.unzip(tempFilePath, cacheFilePath);
                        ZipFileUtil.unZip(tempFilePath, cacheFilePath);
                        Utils.deleteFile(tempFilePath);

                        NoteInfo noteInfo = new NoteInfo();
                        noteInfo.setDateTime(dateTime);
                        noteInfo.setNoteId(courseInfo.getId());
                        noteInfo.setTitle(courseInfo.getNickname());
                        noteInfo.setNoteType(MediaPaper.PAPER_TYPE_TIEBA);
                        noteInfo.setCreateTime(dateTime);
                        if (new File(cacheFilePath, Saver.HEAD_FILE).exists()) {
                            noteInfo.setThumbnail(new File(cacheFilePath, Saver.HEAD_FILE).getPath());
                        } else {
                            noteInfo.setThumbnail(null);
                        }
                        NoteDTO noteDTO = noteInfo.toNoteDTO(noteInfo);
                        noteDTO.setIsUpdate(true);
                        NoteDao noteDao = new NoteDao(context);
                        noteDao.addOrUpdateNoteDTO(noteDTO);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return cacheFilePath;
        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            dismissLoadingDialog(loadingDlg);
            openLocalNote(path);
        }
    }


    public static void setHasMicroCourseCollected(boolean hasMicroCourseCollected) {
        OnlineMediaPaperActivity.hasMicroCourseCollected = hasMicroCourseCollected;
    }

    public static boolean hasMicroCourseCollected() {
        return hasMicroCourseCollected;
    }

    public static void setHasContentChanged(boolean hasContentChanged) {
        OnlineMediaPaperActivity.hasContentChanged = hasContentChanged;
    }

    public static boolean hasContentChanged() {
        return hasContentChanged;
    }

}
