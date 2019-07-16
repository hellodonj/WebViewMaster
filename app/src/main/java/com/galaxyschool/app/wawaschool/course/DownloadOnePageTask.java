package com.galaxyschool.app.wawaschool.course;

import android.app.Activity;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.common.CheckReplaceIPAddressHelper;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.lecloud.xutils.cache.MD5FileNameGenerator;
import com.lqwawa.lqbaselib.net.FileApi;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.lqwawa.tools.FileZipHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author: wangchao
 * @date: 2017/08/29 12:57
 */

public class DownloadOnePageTask extends MyAsyncTask<LocalCourseDTO> {
    Activity activity;
    String resUrl;
    String title;
    int screenType;
    String destPath;
    String jsonString;
    boolean isDownloadEvalZip;

    public DownloadOnePageTask(Activity activity, String resUrl, String title, int
            screenType, String destPath, String jsonString) {
        super(activity);
        this.activity = activity;
        this.resUrl = resUrl;
        this.title = Utils.removeFileNameSuffix(title);
        this.screenType = screenType;
        this.destPath = destPath;
        this.jsonString = jsonString;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected LocalCourseDTO doInBackground(Void... params) {
        Utils.createLocalDiskPath(destPath);
        String filename = new MD5FileNameGenerator().generate(resUrl);
        File destFile = new File(destPath, filename);
        String filePath = destFile.getAbsolutePath();

        judgeFileIntegrity(destFile, filePath);

        File file = new File(filePath);
        if (file != null && file.exists() && file.canRead()) {
            Utils.createLocalDiskPath(Utils.TEMP_FOLDER);
            String urlPath = String.valueOf(System.currentTimeMillis());
            File tempDirFile = new File(Utils.TEMP_FOLDER, urlPath);
            String tempPath = tempDirFile.getAbsolutePath() + File.separator;
            if (!tempDirFile.exists()) {
                FileZipHelper.unzipMyZip(filePath, tempPath);
            }
            if (isDownloadEvalZip) {
                String evalRootPath = findEvalRootPath(tempPath);
                if (!TextUtils.isEmpty(evalRootPath)){
                    LocalCourseDTO dto = new LocalCourseDTO();
                    dto.setmPath(evalRootPath);
                    dto.setmParentPath(tempPath);
                    return  dto;
                }
            } else {
                String draftRootPath = findWeikeFolder(tempPath);
                if (!TextUtils.isEmpty(draftRootPath)) {
                    File oldFile = new File(draftRootPath);
                    if (activity == null) {
                        return null;
                    }
                    String memberId = ((MyApplication) activity.getApplication()).getMemberId();
                    String courseRootPath = Utils.getUserCourseRootPath(memberId, CourseType
                            .COURSE_TYPE_LOCAL, false);
                    File newFile = new File(courseRootPath, DateUtils.millSecToDateStr(System
                            .currentTimeMillis()));
                    oldFile.renameTo(newFile);
                    Utils.safeDeleteDirectory(tempPath);
                    LocalCourseDTO localCourseDTO = new LocalCourseDTO(newFile.getAbsolutePath(),
                            courseRootPath, "", "", System.currentTimeMillis(), 0, 0, 0, CourseType.COURSE_TYPE_LOCAL);
                    localCourseDTO.setmTitle(title);
                    localCourseDTO.setmOrientation(screenType);
                    return localCourseDTO;
                }
            }
        }
        return null;
    }

    /**
     * 校验文件是否完整,如果不完整重新下载
     *
     * @param destFile
     * @param filePath
     */
    private void judgeFileIntegrity(File destFile, String filePath) {
        if (!destFile.exists()) {
            FileApi.getFile(resUrl, filePath);
        } else {
            URL newurl = null;
            HttpURLConnection conn = null;
            int fileSize = -1;
            FileInputStream fis = null;
            try {
                newurl = new URL(resUrl);
                conn = (HttpURLConnection) newurl.openConnection();
                conn.setRequestProperty("Accept-Encoding", "identity");
                conn.setConnectTimeout(60 * 1000);
                conn.connect();
                if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                    newurl = new URL(conn.getHeaderField("Location"));
                    conn = (HttpURLConnection) newurl.openConnection();
                    conn.setRequestProperty("Connection", "close");
                    conn.setConnectTimeout(30 * 1000);
                    conn.setReadTimeout(30 * 1000);
                    conn.connect();
                }
                fileSize = conn.getContentLength();

                fis = new FileInputStream(destFile);

                int size = fis.available();
                if (size != fileSize || fileSize == -1) {
                    FileApi.getFile(resUrl, filePath);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onPostExecute(LocalCourseDTO localCourseDTO) {
        super.onPostExecute(localCourseDTO);
    }

    private String findWeikeFolder(String folder) {
        String result = null;
        if (!TextUtils.isEmpty(folder)) {
            File file = new File(folder);
            if (file.exists() && file.isDirectory()) {
                if (new File(folder, "head.jpg").exists() || new File(folder, "page_index.xml").exists()) {
                    return folder;
                } else {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        result = findWeikeFolder(files[i].getPath());
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    private String findEvalRootPath(String folder) {
        String result = null;
        if (!TextUtils.isEmpty(folder)) {
            File file = new File(folder);
            if (file.exists() && file.isDirectory()) {
                if (new File(folder, "page_index.json").exists()) {
                    return folder;
                } else {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        result = findEvalRootPath(files[i].getPath());
                        if (result != null) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 检测是否需要替换内网的ip
     *
     * @param resId
     * @param resType
     */
    public void checkCanReplaceIPAddress(int resId,
                                         int resType,
                                         int fileSize,
                                         final DownloadOnePageTask task) {
        final CheckReplaceIPAddressHelper helper = new CheckReplaceIPAddressHelper(activity);
        helper.setResId(resId)
                .setResType(resType)
                .setFileSize(fileSize)
                .setCallBackListener(new CallbackListener() {
                    @Override
                    public void onBack(Object result) {
                        boolean flag = (boolean) result;
                        if (flag) {
                            resUrl = helper.getChangeIPUrl(resUrl);
                        }
                        task.execute();
                    }
                })
                .checkIP();
    }

    public void setDownloadEvalZip(boolean isDownloadEvalZip) {
        this.isDownloadEvalZip = isDownloadEvalZip;
    }
}
