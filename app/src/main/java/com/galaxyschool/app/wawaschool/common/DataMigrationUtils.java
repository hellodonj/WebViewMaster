package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.course.MyAsyncTask;
import com.galaxyschool.app.wawaschool.db.DraftData;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.lqwawa.lqbaselib.views.ContactsLoadingDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 用于本地数据迁移
 *
 * @author: wangchao
 * @date: 2017/08/25 17:07
 */

public class DataMigrationUtils {

    public static void loadDraftData(Activity activity) {
        CopyDraftDataTask copyDraftDataTask = new CopyDraftDataTask(activity);
        copyDraftDataTask.execute();
    }

    private static class CopyDraftDataTask extends AsyncTask<Void, Void, Object> {

        private Activity activity;
        private String memberId;
        private String courseRootPath;
        private ContactsLoadingDialog loadingDialog;

        public CopyDraftDataTask(Activity activity) {
            this.activity = activity;
            memberId = ((MyApplication) activity.getApplication()).getMemberId();
            courseRootPath = Utils.getUserCourseRootPath(memberId, CourseType.COURSE_TYPE_LOCAL,
                    false);
            loadingDialog = new ContactsLoadingDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog.show();
        }

        @Override
        protected Object doInBackground(Void... params) {
            List<DraftData> draftDatas = DraftData.getAllDraftsInType(activity, Common.TYPE_NOTE,
                    memberId);
            processDraftData(draftDatas);
            return null;
        }

        @Override
        protected void onPostExecute(Object data) {
            super.onPostExecute(data);
            loadingDialog.dismiss();
        }

        private void processDraftData(List<DraftData> draftDatas) {
            if (draftDatas != null && draftDatas.size() > 0) {
                for (DraftData data : draftDatas) {
                    if (data != null && !TextUtils.isEmpty(data.chw) && new File(data.chw).exists
                            ()) {
                        File draftFile = new File(data.chw);
                        File courseFile = new File(courseRootPath, data.title);
                        if (courseFile.exists() && courseFile.canRead()) {
                            String newTitle = Utils.getFileTitle(data.title, courseRootPath, null);
                            courseFile = new File(courseRootPath, newTitle);
                        }
                        File courseRootFile = new File(courseRootPath);
                        if (courseRootFile == null || !courseRootFile.exists()) {
                            courseRootFile.mkdirs();
                        }
                        boolean isOk = false;
                        if (draftFile != null && courseFile != null) {
                            isOk = draftFile.renameTo(courseFile);
                        }
                        if (isOk) {
                            LocalCourseDTO dto = new LocalCourseDTO();
                            dto.setmPath(courseFile.getAbsolutePath());
                            if (!TextUtils.isEmpty(courseRootPath) && courseRootPath.endsWith(File
                                    .separator)) {
                                courseRootPath = courseRootPath.substring(0, courseRootPath
                                        .length() - 1);
                            }
                            dto.setmParentPath(courseRootPath);
                            dto.setmDescription(data.content);
                            dto.setmLastModifiedTime(data.editTime);
                            dto.setmType(CourseType.COURSE_TYPE_LOCAL);
                            dto.setmMemberId(memberId);
                            LocalCourseDTO.saveLocalCourse(activity, memberId, dto);

                            //删除旧数据
                            DraftData.deleteDraftByChwPath(activity, data.chw, memberId);
                        }
                    }
                }
            }
        }

    }


    private static class CopyLocalCourseDataTask extends MyAsyncTask<Object> {

        private Activity activity;
        private String memberId;

        public CopyLocalCourseDataTask(Activity activity, String memberId) {
            super(activity);
            this.activity = activity;
            this.memberId = memberId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Void... params) {
            processLocalCourseData(activity, memberId, CourseType.COURSE_TYPE_IMPORT);
            processLocalCourseData(activity, memberId, CourseType.COURSE_TYPE_LOCAL);
            return null;
        }

        @Override
        protected void onPostExecute(Object data) {
            super.onPostExecute(data);
        }
    }

    /**
     * 同步迁移数据
     * @param activity
     * @param memberId
     */

    public static void processLocalCourseData(Activity activity, String memberId) {
        processLocalCourseData(activity, memberId, CourseType.COURSE_TYPE_IMPORT);
        processLocalCourseData(activity, memberId, CourseType.COURSE_TYPE_LOCAL);
    }

    /**
     * 异步迁移数据, 如需重命名文件夹，需打开renameCourses方法
     * @param activity
     * @param memberId
     * @param listener
     */
    public static void processLocalCourseData(Activity activity, String memberId,
                                              CallbackListener listener) {
        CopyLocalCourseDataTask copyLocalCourseDataTask = new CopyLocalCourseDataTask(activity, memberId);
        copyLocalCourseDataTask.setCallbackListener(listener);
        copyLocalCourseDataTask.execute();
    }

    private static void processLocalCourseData(Activity activity, String memberId, int type) {
        List<LocalCourseDTO> localCourseDTOs = LocalCourseDTO.getAllLocalCourses(activity, memberId,
                type);
        updateCourseTitle(activity, memberId, type, localCourseDTOs);
    }

    private static void updateCourseTitle(Activity activity, String memberId, int type,
                                          List<LocalCourseDTO> localCourseDTOs) {
        HashMap<String, List<LocalCourseDTO>> hashMap = new HashMap<>();
        if (localCourseDTOs != null && localCourseDTOs.size() > 0) {
            for (LocalCourseDTO dto : localCourseDTOs) {
                if (dto != null) {
                    if (TextUtils.isEmpty(dto.getmTitle())) {
                        String title = Utils.getFileNameFromPath(dto.getmParentPath());
                        if (!TextUtils.isEmpty(dto.getmPath())) {
                            title = Utils.getFileNameFromPath(dto.getmPath());
                        }
                        dto.setmTitle(title);
                        if (!TextUtils.isEmpty(dto.getmPath())) {
                            LocalCourseDTO.updateLocalCourse(activity, memberId, dto
                                    .getmPath(), dto);
                        } else {
                            LocalCourseDTO.updateLocalCourse(activity, memberId, null,
                                    dto.getmParentPath(), dto);
                        }

                        List<LocalCourseDTO> paths = null;
                        String parent = dto.getmParentPath();
                        if (!TextUtils.isEmpty(parent)) {
                            if (!hashMap.containsKey(parent)) {
                                paths = new ArrayList<>();
                            } else {
                                paths = hashMap.get(parent);
                            }
                            paths.add(dto);
                        }
                        LocalCourseDTO foldDto = addFolderToDB(activity, memberId, type, dto);
                        if (foldDto != null && paths != null) {
                            paths.add(foldDto);
                        }
                        if (paths != null) {
                            hashMap.put(parent, paths);
                        }
                    }
                }
            }
        }
//        if (hashMap.size() > 0) {
//            renameCourses(activity, memberId, type, hashMap);
//        }
    }

    private static LocalCourseDTO addFolderToDB(Activity activity, String memberId, int type,
                                                LocalCourseDTO dto) {
        //创建文件夹
        LocalCourseDTO folderDto = null;
        String rootCourse = Utils.getUserCourseRootPath(memberId, type, false);
        rootCourse = Utils.removeFolderSeparator(rootCourse);
        String parentPath = dto.getmParentPath();
        if (!TextUtils.isEmpty(parentPath) && !parentPath.equals(rootCourse)) {
            folderDto = LocalCourseDTO.getLocalCourse(activity, memberId, null, parentPath);
            if (folderDto == null) {
                folderDto = new LocalCourseDTO(null, parentPath, null,
                        null, System.currentTimeMillis(), 0, 0, 0L, dto.getmType());
                folderDto.setmTitle(Utils.getFileNameFromPath(parentPath));
                LocalCourseDTO.saveLocalCourse(activity, memberId, folderDto);
            }
        }
        return folderDto;
    }

    private static void renameCourses(Activity activity, String memberId, int type, HashMap<String,
            List<LocalCourseDTO>> hashMap) {
        String rootCourse = Utils.getUserCourseRootPath(memberId, type, false);
        rootCourse = Utils.removeFolderSeparator(rootCourse);
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            List<LocalCourseDTO> value = (List<LocalCourseDTO>) entry.getValue();
            if (!TextUtils.isEmpty(key)) {
                if (value != null && value.size() > 0) {
                    if (key.equals(rootCourse)) {
                        for (LocalCourseDTO dto : value) {
                            if (dto != null && !TextUtils.isEmpty(dto.getmPath())) {
                                String newPath = new File(key, String.valueOf(System.currentTimeMillis())).getPath();
                                File oldFile = new File(dto.getmPath());
                                File newFile = new File(newPath);
                                oldFile.renameTo(newFile);
                                String oldFilePath = dto.getmPath();
                                dto.setmPath(newPath);
                                LocalCourseDTO.updateLocalCourse(activity, memberId, oldFilePath,
                                        dto);
                            }
                        }
                    } else {
                        File oldFile = new File(key);
                        File newFile = new File(oldFile.getParent(), String.valueOf(System
                                .currentTimeMillis()));
                        oldFile.renameTo(newFile);
                        for (LocalCourseDTO dto : value) {
                            if (dto != null) {
                                dto.setmParentPath(newFile.getPath());
                                if (TextUtils.isEmpty(dto.getmPath())) {
                                    LocalCourseDTO.updateLocalCourse(activity, memberId,
                                            null, key, dto);
                                } else {
                                    String newPath = new File(dto.getmParentPath(), String.valueOf(System
                                            .currentTimeMillis())).getPath();
                                    File oldFile2 = new File(dto.getmParentPath(), dto.getmTitle());
                                    File newFile2 = new File(newPath);
                                    oldFile2.renameTo(newFile2);
                                    String oldFilePath = dto.getmPath();
                                    dto.setmPath(newPath);
                                    LocalCourseDTO.updateLocalCourse(activity, memberId,
                                            oldFilePath, dto);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
