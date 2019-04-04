package com.galaxyschool.app.wawaschool.net.course;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.widget.RemoteViews;
import com.alibaba.fastjson.JSON;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.TipMsgHelper;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.lqwawa.lqbaselib.net.library.DataModelResult;
import com.lqwawa.lqbaselib.net.library.DataResult;
import com.lqwawa.lqbaselib.net.library.RequestHelper;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfo;
import com.galaxyschool.app.wawaschool.pojo.SubscribeClassInfoResult;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UploadSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.ShortCourseInfo;
import com.galaxyschool.app.wawaschool.slide.SlideManagerHornForPhone;
import com.lqwawa.client.pojo.MediaType;
import com.oosic.apps.share.ShareType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadCourseManager {

    private final static String UPLOAD_COURSE = "Upload course";
    private final static String UPLOAD_THUMB = "Upload thumbnail";

    private final static String TAG = "CourseUpload";

    private static UploadCourseManager sInstance = null;
    private Activity mContext;
    private int mUploadType = 0;
    private Map<String, Thread> mThreadList = new HashMap<String, Thread>();

    public static String oriFilePath="";
    // private Semaphore mUploadSemaphore;

    private Handler handler = new Handler();

    public static UploadCourseManager getDefault(Activity context) {
        if (sInstance == null) {
            sInstance = new UploadCourseManager(context);
        }
        return sInstance;
    }

    private UploadCourseManager(Activity context) {
        mContext = context;
        // mUploadSemaphore = new Semaphore(1);
    }

    public boolean isUploading(String filePath) {
        boolean result = false;
        if (!TextUtils.isEmpty(filePath) && mThreadList.size() > 0) {
            if (mThreadList.containsKey(filePath)) {
                result = true;
            }
        }
        return result;
    }

    public boolean upload(UploadParameter uploadParameter, int type) {
        mUploadType = type;
        if (uploadParameter == null) {
            return false;
        }

        if (uploadParameter.getCourseData() != null) {
            commitCourseToWawaCourse(uploadParameter.getCourseData(), uploadParameter, type);
            return true;
        }

        if (TextUtils.isEmpty(uploadParameter.getFilePath())) {
            return false;
        }
        uploadToHKServer thread = new uploadToHKServer(uploadParameter);

        if (mThreadList.get(uploadParameter.getFilePath()) != null) {
            return false;
        } else {
            mThreadList.put(uploadParameter.getFilePath(), thread);
            thread.start();
        }
        return true;

    }

    private CourseUploadResult uploadToHKServer(UploadParameter uploadParameter) {
        synchronized (sInstance) {
            CourseUploadResult upload_result = null;
            File rsc = new File(uploadParameter.getFilePath());
            try {
                File zipFile = zipFile(rsc, uploadParameter.getFileName());
                if (zipFile.exists()) {
                    String path = zipFile.getPath();
                    long size = new File(path).length();
                    uploadParameter.setZipFilePath(path);
                    uploadParameter.setSize(size);
                    upload_result = UserApis.uploadResource(mContext, uploadParameter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return upload_result;
        }
    }

    private File zipFile(File rsc, String fileName) {
        boolean rtn = false;
        File zipFile = null;

        String zipFileName = Utils.getFileTitle(fileName + Utils.COURSE_SUFFIX, Utils.TEMP_FOLDER, Utils.COURSE_SUFFIX);
        zipFile = new File(Utils.TEMP_FOLDER, zipFileName);
        File parent = new File(Utils.TEMP_FOLDER);
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (zipFile.exists()) {
            zipFile.delete();
        }
        try {
            zipFile.createNewFile();
            rtn = Utils.zip(rsc, zipFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rtn) {
            return zipFile;
        } else {
            if (zipFile.exists()) {
                zipFile.delete();
            }
            return null;
        }
    }

    private class uploadToHKServer extends Thread {
        private UploadParameter mUploadParameter;
        private String mSrcPath = null;
        private String mUploadTitle = null;
        private long mFileSize = 0;
        private long mTmpFileSize = 0;
        private Notification notify = null;
        private RemoteViews notifyView = null;

        public uploadToHKServer(UploadParameter uploadParameter) {
            mUploadParameter = uploadParameter;
            mSrcPath = uploadParameter.getFilePath();
            mUploadTitle = uploadParameter.getFileName();
        }

        @Override
        public void run() {
            CourseUploadResult uplaodResult = null;

            refreshDownloadPeriod(mSrcPath);
            uplaodResult = uploadToHKServer(mUploadParameter);

            if (uplaodResult != null) {
                List<CourseData> result = uplaodResult.getData();
                if (result != null && result.size() > 0) {
                    commitCourseToWawaCourse(result.get(0), mUploadParameter, mUploadType);
                    onResult(mSrcPath, true);
                } else {
                    onResult(mSrcPath, false);
                }
            } else {
                if(!TextUtils.isEmpty(mSrcPath)) {
                    mThreadList.remove(mSrcPath);
                }
            }
        }

        private void onResult(String path, boolean done) {
            if (done) {
                cancelNotify(path);
            } else {
                notifyError(path);
            }
            if (mUploadParameter != null && !TextUtils.isEmpty(mUploadParameter.getZipFilePath())) {
                File zipFile = new File(mUploadParameter.getZipFilePath());
                if (zipFile != null && zipFile.exists()) {
                    zipFile.delete();
                }
            }
            mThreadList.remove(mSrcPath);
        }

        private String getPercentString(long currentBytes, long totalBytes) {
            if (totalBytes <= 0 || currentBytes < 0) {
                return "";
            }
            long progress = (long) currentBytes * 100 / totalBytes;
            StringBuilder sb = new StringBuilder();
            sb.append(progress);
            sb.append('%');
            return sb.toString();
        }

        private String getProgressDetailString(long currentBytes, long totalBytes) {
            if (currentBytes <= 0) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            sb.append(Formatter.formatFileSize(mContext, currentBytes));
            if (totalBytes > 0) {
                sb.append(" / ");
                sb.append(Formatter.formatFileSize(mContext, totalBytes));
            }

            return sb.toString();
        }

        private void refreshDownloadPeriod(String key) {
            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notify == null) {
                notify = new Notification();
            }
            notify.flags |= Notification.FLAG_ONGOING_EVENT;
            notify.icon = android.R.drawable.stat_sys_upload;
            notify.tickerText = mUploadTitle;
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
            notify.contentIntent = pi;

            if (notifyView == null) {
                notifyView = new RemoteViews(mContext.getPackageName(), R.layout.upload_notification);
            }
            notifyView.setTextViewText(R.id.title, mUploadTitle);
            notifyView.setTextViewText(R.id.progress_text, getPercentString(mTmpFileSize, mFileSize));
            notifyView.setTextViewText(R.id.progress_detail_text, getProgressDetailString(mTmpFileSize, mFileSize));
            notifyView.setProgressBar(R.id.progress_bar, (int) mFileSize, (int) mTmpFileSize, mFileSize == 0);
            notifyView.setImageViewResource(R.id.icon, android.R.drawable.stat_sys_upload);
            notify.contentView = notifyView;

            nm.notify(key, R.layout.upload_notification, notify);

        }

        private void notifyError(String key) {
            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (notify == null) {
//                notify = new Notification();
//            }
//            notify.icon = android.R.drawable.stat_notify_error;// android.R.drawable.stat_notify_sync_error;
//            notify.tickerText = mContext.getString(R.string.upload_failure, mUploadTitle);
//
//            notify.flags |= Notification.FLAG_AUTO_CANCEL;
//            Intent intent = new Intent();
//            notify.setLatestEventInfo(
//                    mContext, mContext.getString(R.string.upload_failure, mUploadTitle),
//                    mContext.getString(R.string.upload_failed_summary),
//                    PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
//
//            nm.notify(key, R.layout.upload_notification, notify);
            Intent intent = new Intent();
            PendingIntent pi = PendingIntent.getActivity(mContext, 0, intent, PendingIntent
                    .FLAG_UPDATE_CURRENT);
            String title = mContext.getString(R.string.upload_failure);
            String content = mContext.getString(R.string.upload_failed_summary);
            Notification.Builder builder = new Notification.Builder(mContext)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pi)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setTicker(title)
                    .setWhen(System.currentTimeMillis());
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                notify = builder.getNotification();
            } else {
                notify = builder.build();
            }
            if(notify != null) {
                notify.flags |= Notification.FLAG_AUTO_CANCEL;
                nm.notify(key, R.layout.upload_notification, notify);
            }

        }

        private void cancelNotify(String key) {
            NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(key, R.layout.upload_notification);
        }
    }

    private void commitCourseToWawaCourse(CourseData courseData, UploadParameter uploadParameter, int uploadType) {
        switch (uploadType) {
            case ShareType.SHARE_TYPE_CLOUD_COURSE:
                if (uploadParameter != null) {
                    commitCourseToPersonalResource(courseData, uploadParameter);
                }
                break;
            case ShareType.SHARE_TYPE_CLASSROOM:
                if (uploadParameter != null) {
                    commitCourseToClassSpace(mContext, courseData, uploadParameter, false);
                }
                break;

            case ShareType.SHARE_TYPE_PICTUREBOOK:
                if (uploadParameter != null ) {
                    commitCourseToPictureBook(courseData, uploadParameter);
                }
                break;

            case ShareType.SHARE_TYPE_PUBLIC_COURSE:
                if (uploadParameter != null ) {
                    commitCourseToSchoolSpace(courseData, uploadParameter);
                }
                break;
            default:
                break;

        }
    }

    private void commitCourseToPersonalResource(CourseData courseData, final UploadParameter uploadParameter) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", uploadParameter.getMemberId());
        int tempMediaType = MediaType.MICROCOURSE;
        if(courseData.type == ResType.RES_TYPE_ONEPAGE) {
            tempMediaType = MediaType.ONE_PAGE;
        }
        params.put("MType", String.valueOf(tempMediaType));
        List<ShortCourseInfo> shortCourseInfos = new ArrayList<ShortCourseInfo>();
        ShortCourseInfo info = new ShortCourseInfo();
        info.setMicroId(courseData.getIdType());
        info.setTitle(courseData.nickname);
        shortCourseInfos.add(info);
        params.put("MaterialList", shortCourseInfos);

        RequestHelper.sendPostRequest(mContext, ServerUrl.PR_UPLOAD_WAWAWEIKE_URL, params, new Listener<String>() {
            @Override
            public void onSuccess(String json) {
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
                    } else {
                        TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }
        });
    }

    public static void commitCourseToClassSpace(final  Activity activity, CourseData courseData,
                                                final UploadParameter uploadParameter, final boolean isFinish) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", uploadParameter.getMemberId());
        params.put("SchoolId", uploadParameter.getSchoolIds());
        params.put("MicroID", courseData.getIdType());
        params.put("ClassId", uploadParameter.getClassId());
        params.put("ActionType", String.valueOf(6));
        params.put("Title", courseData.nickname);
        final CourseData courseDataTemp=courseData;
        RequestHelper.sendPostRequest(activity, ServerUrl.UPLOAD_CLASS_SPACE_URL, params, new Listener<String>() {
            @Override
            public void onSuccess(String json) {
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        oriFilePath = uploadParameter.getFilePath();
                        loadClassInfo(activity, courseDataTemp, uploadParameter.getClassId(), isFinish);
                    } else {
                        TipMsgHelper.ShowLMsg(activity, R.string.upload_file_failed);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }
        });
    }

    private static void loadClassInfo(final Activity activity, final CourseData courseData, String
            classId, final boolean isFinish) {
        Map<String, Object> params = new HashMap();
        params.put("MemberId", courseData.code);
        params.put("ClassId", classId);
        RequestHelper.RequestDataResultListener listener =
                new RequestHelper.RequestDataResultListener<SubscribeClassInfoResult>(
                        activity, SubscribeClassInfoResult.class) {
                    @Override
                    public void onSuccess(String jsonString) {
                        super.onSuccess(jsonString);
                        SubscribeClassInfoResult result = getResult();
                        if (result == null || !result.isSuccess()
                                || result.getModel() == null) {
                            return;
                        }
                        upload2OrignalShow(activity, courseData, result.getModel().getData(), isFinish);
                    }
                };
        RequestHelper.sendPostRequest(activity,
                ServerUrl.CONTACTS_CLASS_INFO_URL, params, listener);
    }


    private static void upload2OrignalShow(final Activity activity, CourseData courseData,
                                           SubscribeClassInfo classInfo, final boolean isFinish) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Dept",1);
        params.put("JoinType", 1);
        params.put("MemberId", courseData.code);
        params.put("Memo", courseData.description);
        params.put("NickName", courseData.createaccount);
        params.put("ProductId",courseData.getIdType());
        params.put("RealName", courseData.createname);
        params.put("Thumb", courseData.thumbnailurl);
        params.put("ProductTitle", courseData.nickname);
        params.put("ResourceUrl",courseData.resourceurl);
        params.put("ScreenType", courseData.screentype);
        params.put("FileSize",courseData.size);
        if(classInfo != null) {
            params.put("SchoolId", classInfo.getSchoolId());
            params.put("SchoolName", classInfo.getSchoolName());
            params.put("ClassId", classInfo.getClassId());
            params.put("ClassName", classInfo.getClassName());
        }

        RequestHelper.sendPostRequest(activity, ServerUrl.UPLOAD_SHOW_LIST_URL, params, new Listener<String>() {
            @Override
            public void onSuccess(String json) {
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        TipMsgHelper.ShowLMsg(activity, R.string.upload_file_sucess);
                        if(isFinish && activity != null) {
                            if (!TextUtils.isEmpty(oriFilePath)){
                                Intent intent =new Intent();
                                intent.putExtra(SlideManagerHornForPhone.SAVE_PATH,oriFilePath);
                                activity.setResult(Activity.RESULT_OK, intent);
                            }
                            activity.finish();
                        }
                    } else {
                        TipMsgHelper.ShowLMsg(activity, R.string.upload_file_failed);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }
        });
    }

    private void commitCourseToPictureBook(CourseData courseData, UploadParameter uploadParameter) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", uploadParameter.getMemberId());
        params.put("SchoolId", uploadParameter.getSchoolIds());
        params.put("MicroID", courseData.getIdType());
        params.put("Title", courseData.nickname);
        List<String> picBookIds = uploadParameter.getPicBookIds();
        if(picBookIds != null && picBookIds.size() == 3) {
            params.put("AgeGroupIds", picBookIds.get(0));
            params.put("LanguageIds", picBookIds.get(1));
            params.put("TagsIds", picBookIds.get(2));
        }

        RequestHelper.sendPostRequest(mContext, ServerUrl.UPLOAD_PICTURE_BOOK_URL, params, new Listener<String>() {
            @Override
            public void onSuccess(String json) {
                try {
                    DataResult result = JSON.parseObject(json, DataResult.class);
                    if (result != null && result.isSuccess()) {
                        TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
                    } else {
                        TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }
        });
    }

    private void commitCourseToSchoolSpace(CourseData courseData, UploadParameter uploadParameter) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("MemberId", uploadParameter.getMemberId());
        params.put("MicroID", courseData.getIdType());
        params.put("Title", courseData.nickname);
        params.put("OutlineId", uploadParameter.getOutlineId());
        params.put("SectionId", uploadParameter.getSectionId());
        final UploadSchoolInfo uploadSchoolInfo = uploadParameter.getUploadSchoolInfo();
        if (uploadSchoolInfo != null) {
            params.put("SchoolId", uploadSchoolInfo.SchoolId);
        }
        params.put("IsPmaterial", uploadParameter.isPmaterial());
        if (uploadParameter.getSchoolMaterialType() > 0) {
            params.put("SchoolMaterialType", uploadParameter.getSchoolMaterialType());
        }
        int resType = courseData.type % ResType.RES_TYPE_BASE;
        if (resType == ResType.RES_TYPE_STUDY_CARD){
            //任务类型的教辅材料
            params.put("GuidanceCardSendFlag", courseData.guidanceCardSendFlag);
        }

        RequestHelper.sendPostRequest(mContext, ServerUrl.UPLOAD_COURSE_BOOK_STORE_URL, params, new
                RequestHelper.RequestDataResultListener(mContext, DataModelResult.class) {
            @Override
            public void onSuccess(String json) {
                try {
//                    DataModelResult result = JSON.parseObject(json, DataModelResult.class);
                    super.onSuccess(json);
                    DataModelResult result = (DataModelResult) getResult();
                    if (result == null || !result.isSuccess()){
                        return;
                    }
                    TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
//                    if (result != null &&result.isSuccess()) {
//                        TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_sucess);
//                    } else {
//                            TipMsgHelper.ShowLMsg(mContext, R.string.upload_file_failed);
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }
        });
    }


    private static String getMemberId(Activity activity) {
        String memberId = null;
        MyApplication app = (MyApplication) activity.getApplication();
        if (app != null) {
            memberId = app.getMemberId();
        }
        return memberId;
    }

}
