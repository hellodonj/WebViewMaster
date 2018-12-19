package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.config.ServerUrl;
import com.galaxyschool.app.wawaschool.fragment.LocalCourseFragment;
import com.galaxyschool.app.wawaschool.net.course.UserApis;
import com.galaxyschool.app.wawaschool.pojo.FileSuffixType;
import com.galaxyschool.app.wawaschool.pojo.NoteInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;
import com.galaxyschool.app.wawaschool.pojo.UploadSchoolInfo;
import com.galaxyschool.app.wawaschool.pojo.UserInfo;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseUploadResult;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;
import com.oosic.apps.iemaker.base.BaseUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Author: wangchao
 * Time: 2015/10/30 16:31
 */
public class UploadReourceHelper {

    public static void uploadResource(Activity activity, final UploadParameter uploadParameter,
                                      CallbackListener listener) {
        UploadResourceTask uploadResourceTask = new UploadResourceTask(activity, uploadParameter, listener);
        uploadResourceTask.execute();
    }


    private static class UploadResourceTask extends AsyncTask<Void, Void, CourseUploadResult> {
        Activity mActivity = null;
        UploadParameter mUploadParameter;
        CallbackListener mListener;

        public UploadResourceTask(
            Activity activity, UploadParameter uploadParameter,
            CallbackListener listener) {
            mActivity = activity;
            mUploadParameter = uploadParameter;
            mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected CourseUploadResult doInBackground(Void... arg0) {
            CourseUploadResult upload_result = null;
            if (mActivity == null || mUploadParameter == null) {
                return null;
            }
            File rsc = new File(mUploadParameter.getFilePath());
            try {
                String fileName = mUploadParameter.getFileName();
                if(mUploadParameter != null && mUploadParameter.getResType() == ResType
                        .RES_TYPE_NOTE) {
                    fileName = Utils.getFileNameFromPath(mUploadParameter.getFilePath());
                }
                File zipFile = zipFile(rsc, fileName, mUploadParameter.getResType());
                if (zipFile.exists()) {
                    String path = zipFile.getPath();
                    long size = new File(path).length();
                    mUploadParameter.setZipFilePath(path);
                    mUploadParameter.setSize(size);
                    upload_result = UserApis.uploadResource(mActivity, mUploadParameter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return upload_result;
        }

        @Override
        protected void onPostExecute(CourseUploadResult result) {
            super.onPostExecute(result);
            if (mListener != null) {
                mListener.onBack(result);
            }
        }
    }

    private static File zipFile(File rsc, String fileName, int resType) {
        boolean rtn = false;
        File zipFile;

        String suffix = Utils.COURSE_SUFFIX;
        int type = resType % ResType.RES_TYPE_BASE;
        if (type == ResType.RES_TYPE_COURSE) {
            suffix = FileSuffixType.SUFFIX_TYPE_CMC;
        } else if (type == ResType.RES_TYPE_NOTE) {
            suffix = FileSuffixType.SUFFIX_TYPE_CMP;
        }

        String zipFileName = Utils.getFileTitle(fileName + Utils.COURSE_SUFFIX, Utils.TEMP_FOLDER, suffix);
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

    public static UploadParameter getUploadParameter(UserInfo userInfo, LocalCourseInfo localCourseInfo, CourseData courseData,
                                                     UploadSchoolInfo uploadSchoolInfo, int type) {
        if (userInfo == null) {
            return null;
        }
        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCreateName(userInfo.getRealName());
        uploadParameter.setAccount(userInfo.getNickName());

        if(localCourseInfo != null) {
            String thumbPath = null;
            if (localCourseInfo.mPath != null && !localCourseInfo.mPath.endsWith(File.separator)) {
                localCourseInfo.mPath = localCourseInfo.mPath + File.separator;
            }
            String courseName = localCourseInfo.mTitle;
            if (!TextUtils.isEmpty(localCourseInfo.mPath)) {
                if(localCourseInfo.mType == CourseType.COURSE_TYPE_IMPORT) {
                    thumbPath = getLocalResourceThumbPath(localCourseInfo);
                } else {
                    thumbPath = localCourseInfo.mPath + Utils.RECORD_HEAD_IMAGE_NAME;
                }
            }
            uploadParameter.setFilePath(localCourseInfo.mPath);
            uploadParameter.setThumbPath(thumbPath);
            uploadParameter.setFileName(courseName);
            uploadParameter.setTotalTime(localCourseInfo.mDuration);
            uploadParameter.setKnowledge(localCourseInfo.mPoints);
            uploadParameter.setDescription(localCourseInfo.mDescription);
            uploadParameter.setTotalTime(localCourseInfo.mDuration);
            if(localCourseInfo.mType == CourseType.COURSE_TYPE_IMPORT) {
                uploadParameter.setResType(ResType.RES_TYPE_RESOURCE);
            } else {
                int courseType = BaseUtils.getCoursetType(localCourseInfo.mPath);
                if (courseType > 0) {
                    uploadParameter.setResType(courseType);
                }
            }
            uploadParameter.setScreenType(localCourseInfo.mOrientation);
            uploadParameter.setColType(1);  //upload resource
            uploadParameter.setUploadSchoolInfo(uploadSchoolInfo);
            if (uploadSchoolInfo != null) {
                uploadParameter.setSchoolIds(uploadSchoolInfo.SchoolId);
            }
            String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
            uploadParameter.setUploadUrl(uploadUrl);
        }

        if(courseData != null) {
            uploadParameter.setCourseData(courseData);
            uploadParameter.setFileName(courseData.nickname);
        }

        uploadParameter.setType(type);
        return uploadParameter;
    }

    public static UploadParameter getUploadParameter(UserInfo userInfo, NoteInfo noteInfo,
                                                     UploadSchoolInfo uploadSchoolInfo, int type) {

        if (userInfo == null || noteInfo == null) {
            return null;
        }

        File file = new File(Utils.NOTE_FOLDER, String.valueOf(noteInfo.getDateTime()));
        String notePath = file.getPath();
        if (notePath != null && !notePath.endsWith(File.separator)) {
            notePath = notePath + File.separator;
        }

        UploadParameter uploadParameter = new UploadParameter();
        uploadParameter.setMemberId(userInfo.getMemberId());
        uploadParameter.setCreateName(userInfo.getRealName());
        uploadParameter.setAccount(userInfo.getNickName());
        uploadParameter.setFilePath(notePath);
        uploadParameter.setThumbPath(noteInfo.getThumbnail());
        uploadParameter.setFileName(noteInfo.getTitle());
        uploadParameter.setTotalTime(0);
        uploadParameter.setKnowledge("");
        uploadParameter.setDescription("");
        uploadParameter.setResId(noteInfo.getNoteId());
        uploadParameter.setResType(ResType.RES_TYPE_NOTE);

        uploadParameter.setUploadSchoolInfo(uploadSchoolInfo);
        if (uploadSchoolInfo != null) {
            uploadParameter.setSchoolIds(uploadSchoolInfo.SchoolId);
        }
        uploadParameter.setColType(1);
        uploadParameter.setType(type);
        uploadParameter.setScreenType(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        String uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, ServerUrl.WEIKE_UPLOAD_BASE_SERVER);
        if (noteInfo.getNoteId() > 0 && !TextUtils.isEmpty(noteInfo.getResourceUrl())) {
            String baseUrl = getBaseUploadUrl(noteInfo.getResourceUrl());
            if (!TextUtils.isEmpty(baseUrl)) {
                uploadUrl = String.format(ServerUrl.UPLOAD_RESOURCE_URL, baseUrl);
            }
        }
        uploadParameter.setUploadUrl(uploadUrl);
        return uploadParameter;
    }

    public static String getBaseUploadUrl(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        int index = path.indexOf("/", 8); //find "/" after "http://"
        if (index > 0 && index < path.length()) {
            return path.substring(0, index);
        }

        return null;
    }

    private static String getLocalResourceThumbPath(LocalCourseInfo info) {
        String thumbPath = null;
        if (info != null) {
            thumbPath = LocalCourseFragment.ThumbnailsHelper.getInstance().getThumbnailPath(info.mPath);
        }
        return thumbPath;
    }

    public static Comparator<String> sTitleComparator = new Comparator<String>() {
        public int compare(String obj1, String obj2) {
            ArrayList<Long> obj1NumList = getNumList(getNumStr(obj1));
            ArrayList<Long> obj2NumList = getNumList(getNumStr(obj2));
            int rtn = 0;
            int size = Math.max(obj1NumList.size(), obj2NumList.size());
            for (int i = 0; i < size; i++) {
                long num1 = 0;
                long num2 = 0;

                if (i < obj1NumList.size()) {
                    num1 = obj1NumList.get(i);
                }
                if (i < obj2NumList.size()) {
                    num2 = obj2NumList.get(i);
                }

                if (num1 < num2) {
                    rtn = -1;
                    break;
                } else if (num1 > num2) {
                    rtn = 1;
                    break;
                } else {
                    rtn = 0;
                }
            }
            return rtn;// obj1.compareTo(obj2);
        }
        private String getNumStr(String srcPath) {
            int firstIndex = srcPath.lastIndexOf('/');
            int lastIndex = srcPath.lastIndexOf('.');
            if (lastIndex <= firstIndex)
                lastIndex = srcPath.length();
            String temp = null;
            try {
                temp = srcPath.subSequence(firstIndex + 1, lastIndex)
                    .toString();

                if (temp != null) {
                    firstIndex = temp.indexOf(Utils.PDF_PAGE_NAME)
                        + Utils.PDF_PAGE_NAME.length();
                    if (firstIndex < temp.length()) {
                        temp = temp.substring(firstIndex);
                    }
                }
            } catch (Exception e) {
                temp = null;
                e.printStackTrace();
            }
            return temp;
        }

        private ArrayList<Long> getNumList(String numStr) {
            ArrayList<Long> list = null;
            long num = -1;
            do {
                num = getNum(numStr);
                if (num >= 0) {
                    if (list == null) {
                        list = new ArrayList<Long>();
                    }
                    list.add(num);
                    int index = numStr.indexOf("_");
                    if (index > 0) {
                        numStr = numStr.substring(index);
                    } else {
                        break;
                    }
                }
            } while (num >= 0);

            return list;
        }

        private long getNum(String numStr) {
            while (numStr.startsWith("_")) {
                numStr = numStr.substring(1);
            }
            long num = -1;
            String temp = numStr;
            int index = numStr.indexOf('_');
            if (index > 0) {
                temp = numStr.substring(0, index);

            }
            if (temp != null) {
                try {
                    num = Long.valueOf(temp);
                } catch (NumberFormatException e) {
                    num = -1;
                }
            }
            return num;
        }
    };
}
