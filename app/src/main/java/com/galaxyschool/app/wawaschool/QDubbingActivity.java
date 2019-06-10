package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.pojo.CommitTask;
import com.galaxyschool.app.wawaschool.views.ContactsMessageDialog;
import com.galaxyschool.app.wawaschool.views.OnlineIntroPopwindow;
import com.icedcap.dubbing.DubbingActivity;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.lqbaselib.net.FileApi;
import com.lqwawa.lqbaselib.pojo.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

/**
 * ======================================================
 * Describe: 配音辅助类
 * ======================================================
 */
public class QDubbingActivity extends DubbingActivity {
    public static int COMMIT_Q_DUBBING_TASK_SUCCESS = 0x1000;
    private CommitTask commitTask;

    /**
     * 打开学生详情 commitTask 不能传null  学生做作业(开始配音) commitTask 传null
     *
     * @param context
     * @param resourceUrl         mp4Url
     * @param level               level==3 表示有背景音乐
     * @param commitTask          (mooc过来对象转化一下->
     *                            需要转化的字段 （
     *                            studentResUrl,
     *                            AutoEvalContent,
     *                            commitTaskId(id),
     *                            HasVoiceReview,
     *                            TaskScore,
     *                            TaskScoreRemark））
     * @param hasReviewPermission 点评的权限
     * @param resPropertyValue    配音的类型 按句配音(2) 通篇配音(3)
     */
    public static void start(Activity context,
                             String resourceUrl,
                             String level,
                             CommitTask commitTask,
                             boolean hasReviewPermission,
                             int resPropertyValue) {
        Intent intent = new Intent(context, QDubbingActivity.class);
        if (TextUtils.isEmpty(resourceUrl)) {
            return;
        }
        if (!TextUtils.isEmpty(level) && TextUtils.equals(level, "3")) {
            if (resourceUrl.endsWith(".mp4")) {
                String bgAudioUrl = resourceUrl.replace(".mp4", ".mp3");
                intent.putExtra(Constant.VIDEO_BACKGROUND_VOICE, bgAudioUrl);
            }
        }
        intent.putExtra(Constant.VIDEO_RESOURCE_URL_PATH, resourceUrl);
        if (resourceUrl.endsWith(".mp4")) {
            String srtText = resourceUrl.replace(".mp4", ".srt");
            intent.putExtra(Constant.VIDEO_SRT_TEXT, srtText);
        }
        if (commitTask != null) {
            intent.putExtra(CommitTask.class.getSimpleName(), (Serializable) commitTask);
        }
        intent.putExtra(Constant.HAS_REVIEW_COMMENT_PERMISSION, hasReviewPermission);
        intent.putExtra(Constant.VIDEO_RES_PROPERTIES_VALUE, resPropertyValue);
        context.startActivityForResult(intent, COMMIT_Q_DUBBING_TASK_SUCCESS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void loadIntentData() {
        Bundle args = getIntent().getExtras();
        if (args != null) {
            videoFilePath = args.getString(Constant.VIDEO_RESOURCE_URL_PATH);
            backgroundFilePath = args.getString(Constant.VIDEO_BACKGROUND_VOICE);
            srtTextUrl = args.getString(Constant.VIDEO_SRT_TEXT);
            hasReviewPermission = args.getBoolean(Constant.HAS_REVIEW_COMMENT_PERMISSION);
            resPropertyValue = args.getInt(Constant.VIDEO_RES_PROPERTIES_VALUE);
            CommitTask data = (CommitTask) args.getSerializable(CommitTask.class.getSimpleName());
            if (data != null) {
                handleOnlinePageData(data);
            }
        }
    }

    private void handleOnlinePageData(CommitTask data) {
        isOnlineOpen = true;
        studentCommitFilePath = data.getStudentResUrl();
        commitTask = data;
        String pageScore = data.getAutoEvalContent();
        if (!TextUtils.isEmpty(pageScore)) {
            pageScoreArray = JSONArray.parseArray(pageScore);
            for (int i = 0; i < pageScoreArray.size(); i++) {
                systemScore = systemScore + Integer.valueOf(pageScoreArray.get(i).toString());
            }
            systemScore = systemScore / pageScoreArray.size();
            hasVideoReview = data.isHasVoiceReview();
            if (hasVideoReview) {
                reviewComment = data.getTaskScoreRemark();
            }
            if (!TextUtils.isEmpty(data.getTaskScore())) {
                teacherReviewScore = Integer.valueOf(data.getTaskScore());
            }
        }
    }

    @Override
    protected void enterTeacherReviewActivity() {
        TeacherReviewDetailActivity.start(
                this,
                "",
                String.valueOf(commitTask.getCommitTaskId()),
                2,
                commitTask.getTaskScore());
    }

    @Override
    public String downloadFile(String srtUrl) {
        if (TextUtils.isEmpty(srtUrl)) {
            return null;
        }
        String filename = new MD5FileNameGenerator().generate(srtUrl);
        File destFile = new File(Utils.TEMP_FOLDER, filename);
        String filePath = destFile.getAbsolutePath();
        if (!destFile.exists()) {
            FileApi.getFile(srtUrl, filePath);
        } else {
            URL newurl = null;
            URLConnection conn = null;
            int fileSize = -1;
            FileInputStream fis = null;
            try {
                newurl = new URL(srtUrl);
                conn = newurl.openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.setConnectTimeout(60 * 1000);
                fileSize = conn.getContentLength();
                fis = new FileInputStream(destFile);
                int size = fis.available();
                if (size != fileSize || fileSize == -1) {
                    FileApi.getFile(srtUrl, filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filePath;
    }

    @Override
    public void backDubbing() {
        ContactsMessageDialog messageDialog = new ContactsMessageDialog(
                this, null,
                getString(R.string.str_unfinish_dubbing_back),
                getString(R.string.discard_save),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isRecording) {
                            isDubbing = false;
                            dubbing(false);
                        }
                        finish();
                    }
                },
                getString(R.string.str_continue_dubbing),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        messageDialog.show();
    }

    @Override
    protected void showTeacherReviewPopWindow(int height) {
        OnlineIntroPopwindow popwindow = new OnlineIntroPopwindow(this, height, reviewComment);
        popwindow.showPopupMenu();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        if (messageEvent != null) {
            Bundle args = messageEvent.getBundle();
            if (args != null) {

                String score = args.getString("evalScore");
                String comment = args.getString("evalComment");
                hasVideoReview = true;
                if (!TextUtils.isEmpty(score)) {
                    teacherReviewScore = Integer.valueOf(score);
                }
                reviewComment = comment;
                showReviewViewData();
            }
        }
    }
}
