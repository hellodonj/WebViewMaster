package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.Note.MediaPaperActivity;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.fragment.PublishResourceFragment;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.oosic.apps.share.ShareHelper;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.ShareItem;
import com.oosic.apps.share.SharePopupView;
import com.oosic.apps.share.ShareType;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommitHelper implements PopupWindow.OnDismissListener {
    protected Activity mContext;
    protected ShareHelper shareHelper;
    private SharePopupView sharePopupView;
    private ShareInfo shareInfo;
    protected List<ShareItem> shareItems;
    private Map<String, Object> contactsPickerParams;
    private NoteCommitListener listener;
    private PopWindowDismissListener dismissListener;
    protected boolean isTeacher;
    private boolean isFinish;
    private boolean isStudyTask;
    private boolean isCloudBar;//云贴吧
    private boolean isSaveCloudBarBtn;//云贴吧图标
    //是否是老师角色来自于校园空间的班级秀秀显示校园动态
    private boolean isFromShowShow;

    private boolean isLocalCourseShare;
    private String backFilePath ="";
    private boolean isOnlineSchool;

    public void setIsOnlineSchool(boolean onlineSchool) {
        isOnlineSchool = onlineSchool;
    }

    public void setBackFilePath(String backFilePath) {
        this.backFilePath = backFilePath;
    }

    public void setLocalCourseShare(boolean localCourseShare) {
        isLocalCourseShare = localCourseShare;
    }

    public void setIsFromShowShow(boolean isFromShowShow){
        this.isFromShowShow=isFromShowShow;
    }
    public boolean isFromShowShow(){
        return  this.isFromShowShow;
    }
    public interface NoteCommitListener {
        void noteCommit(int shareType);
    }

    public interface PopWindowDismissListener {
        void popWindowDismiss();
    }


    public CommitHelper(Activity context) {
        mContext = context;
        shareHelper = new ShareHelper(context);
    }

    public void setIsTeacher(boolean isTeacher) {
        this.isTeacher = isTeacher;
    }

    public void setIsFinish(boolean isFinish) {
        this.isFinish = isFinish;
    }
    public void setIsStudyTask(boolean isStudyTask){
        this.isStudyTask=isStudyTask;
    }

    public void setIsCloudBar(boolean isCloudBar){
        this.isCloudBar = isCloudBar;
    }
    public void setIsCloudBar(boolean isCloudBar,boolean isSaveCloudBarBtn){
        this.isCloudBar = isCloudBar;
        this.isSaveCloudBarBtn = isSaveCloudBarBtn;
    }


    protected void initShareItems() {
        shareItems = new ArrayList<ShareItem>();

        //是云贴吧
        if (isCloudBar) {
            if (isSaveCloudBarBtn) {
                shareItems.add(new ShareItem(R.string.cloud_post_bar, R.drawable.pub_post_bar_ico, ShareType.SHARE_TYPE_POSTBAR));
            }
            if(isTeacher) {
                shareItems.add(new ShareItem(R.string.school_movement, R.drawable.pub_school_movement_ico, ShareType.SHARE_TYPE_SCHOOL_MOVEMENT));
                //            shareItems.add(new ShareItem(R.string.school_course, R.drawable.pub_school_course_ico, ShareType.SHARE_TYPE_SCHOOL_COURSE));
                if (!isFromShowShow) {
                    shareItems.add(new ShareItem(R.string.notices, R.drawable.pub_notice_ico, ShareType.SHARE_TYPE_NOTICE));
                }
            }
            if (!isFromShowShow) {
                shareItems.add(new ShareItem(R.string.comments, R.drawable.pub_comment_ico, ShareType.SHARE_TYPE_COMMENT));
            }
        } else if (isFromShowShow) {
            if (isTeacher && !isOnlineSchool){
                shareItems.add(new ShareItem(R.string.school_movement, R.drawable.pub_school_movement_ico, ShareType.SHARE_TYPE_SCHOOL_MOVEMENT));
            }
        }

//        if(isTeacher) {
////            shareItems.add(new ShareItem(R.string.courses, R.drawable.pub_course_ico, ShareType.SHARE_TYPE_COURSE));
//            shareItems.add(new ShareItem(R.string.learning_tasks, R.drawable.pub_homework_ico, ShareType
//                    .SHARE_TYPE_HOMEWORK));
//        }

        shareItems.add(new ShareItem(R.string.wechat_friends, R.drawable.umeng_share_wechat_btn, ShareType.SHARE_TYPE_WECHAT));
        shareItems.add(new ShareItem(R.string.wxcircle, R.drawable.umeng_share_wxcircle_btn, ShareType
            .SHARE_TYPE_WECHATMOMENTS));
        shareItems.add(new ShareItem(R.string.qq_friends, R.drawable.umeng_share_qq_btn, ShareType.SHARE_TYPE_QQ));
        shareItems.add(new ShareItem(R.string.qzone, R.drawable.umeng_share_qzone_btn, ShareType.SHARE_TYPE_QZONE));
//        shareItems.add(new ShareItem(R.string.wawachat, R.drawable.umeng_share_wawachat_btn, ShareType
//                .SHARE_TYPE_CONTACTS));
    }

    private AdapterView.OnItemClickListener shareItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(
                AdapterView<?> parent, View view, int position,
                long id) {
            if (sharePopupView != null) {
                sharePopupView.dismiss();
            }
            if (position < shareItems.size()) {
//                switchTo(shareItems.get(position).getShareType());
                if(listener != null) {
                    listener.noteCommit(shareItems.get(position).getShareType());
                }
            }
        }
    };

    public void commit(View parent, ShareInfo shareInfo) {
        commit(parent, shareInfo, null);
    }

    public void commit(View parent, ShareInfo shareInfo, Map<String, Object> contactsPickerParams) {
        if (parent == null) {
            return;
        }
        this.contactsPickerParams = contactsPickerParams;
        this.shareInfo = shareInfo;
        sharePopupView = new SharePopupView(mContext);
        sharePopupView.setIsNew(true);
        if (shareItems == null) {
            initShareItems();
        }
        sharePopupView.setShareItems(shareItems);
        sharePopupView.setOnItemClickListener(shareItemClickListener);
        sharePopupView.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        sharePopupView.setOnDismissListener(this);
    }
    UMImage thumb ;
    //share to wechat, wechat circle, qq, qq zone, contacts
    public void shareTo(int shareType, ShareInfo shareInfo) {
        switch (shareType) {
            case ShareType.SHARE_TYPE_WECHAT:
            case ShareType.SHARE_TYPE_WECHATMOMENTS:
            case ShareType.SHARE_TYPE_QQ:
            case ShareType.SHARE_TYPE_QZONE:
                if(shareInfo != null) {
                    UMImage umImage = (UMImage) shareInfo.getuMediaObject();
                    final String imgUrl = umImage.asUrlImage();

                    if (!TextUtils.isEmpty(imgUrl)) {
                        ThumbTask task = new ThumbTask(shareInfo, shareType);
                        task.execute(imgUrl);
                    } else {
//                        thumb = new UMImage(mContext, R.drawable.icon_default_image);
//                        shareInfo.setuMediaObject(thumb);
                        doShare(shareInfo, shareType);

                    }
                }
                break;
            case ShareType.SHARE_TYPE_CONTACTS:
                UserInfo userInfo = ((MyApplication) mContext.getApplication()).getUserInfo();
                if (userInfo == null || TextUtils.isEmpty(userInfo.getMemberId())) {
                    ActivityUtils.enterLogin(mContext);
                    return;
                }
                if (shareInfo != null && shareInfo.getSharedResource() != null) {
                    PublishResourceFragment.enterContactsPicker(mContext,
                            shareInfo.getSharedResource(), contactsPickerParams);
                    if(isFinish && mContext != null) {
                        //设置发送成功标志位
                        MediaPaperActivity.setHasResourceSended(true);
                        mContext.finish();
                    }

                    if (mContext != null && isLocalCourseShare){
                        Intent intent = new Intent();
                        intent.putExtra(SlideManagerHornForPhone.SAVE_PATH, backFilePath);
                        mContext.setResult(Activity.RESULT_OK, intent);
                        mContext.finish();
                    }
                }
                break;
        }
    }

    @Override
    public void onDismiss() {
        if (this.dismissListener != null) {
            this.dismissListener.popWindowDismiss();
        }
    }

    public void setNoteCommitListener(NoteCommitListener listener) {
        this.listener = listener;
    }

    public void setPopWindowDissmissListener(PopWindowDismissListener listener) {
        this.dismissListener = listener;
    }

    private void doShare(ShareInfo shareInfo, int shareType) {
        shareHelper.shareTo(shareType, shareInfo, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                if(mContext != null && isFinish) {
                    //设置发送成功标志位
                    MediaPaperActivity.setHasResourceSended(true);
                    mContext.finish();
                }
                if (mContext!=null&&isStudyTask){
                    mContext.finish();
                }
                if (mContext != null && isLocalCourseShare){
                    Intent intent = new Intent();
                    intent.putExtra(SlideManagerHornForPhone.SAVE_PATH, backFilePath);
                    mContext.setResult(Activity.RESULT_OK, intent);
                    mContext.finish();
                }
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {

            }
        });
    }

    private class ThumbTask extends AsyncTask<String,Void,Boolean> {
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
                thumb  = new UMImage(mContext, R.drawable.icon_default_image);
                shareInfo.setuMediaObject(thumb);
            }
            doShare(shareInfo, shareType);
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
            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                mURL = new URL(conn.getHeaderField("Location"));
                conn = (HttpURLConnection) mURL.openConnection();
                conn.setRequestProperty("Connection", "close");
                conn.setConnectTimeout(30 * 1000);
                conn.setReadTimeout(30 * 1000);
                conn.connect();
            }
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
}
