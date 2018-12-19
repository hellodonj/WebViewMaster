package com.galaxyschool.app.wawaschool.course;

import android.app.Activity;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.common.Utils;
import com.galaxyschool.app.wawaschool.db.LocalCourseDao;
import com.galaxyschool.app.wawaschool.db.dto.LocalCourseDTO;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseType;
import com.galaxyschool.app.wawaschool.pojo.weike.LocalCourseInfo;

import java.io.File;
import java.sql.SQLException;
import java.util.List;


/**
 * @author: wangchao
 * @date: 2017/08/29 15:33
 */

public class CopyCourseTask extends MyAsyncTask<LocalCourseInfo> {

    Activity activity;
    String srcPath;
    String destPath;
    int orientation;
    String title;
    String description;

    public CopyCourseTask(Activity activity, String srcPath, String destPath, int orientation,
                          String title, String description) {
        super(activity);
        this.activity = activity;
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.orientation = orientation;
        this.title = title;
        this.description = description;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected LocalCourseInfo doInBackground(Void... params) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        if (srcFile != null && destFile != null) {
            boolean isOk = Utils.copyDirectory(srcFile, destFile);
            if (isOk) {
                String memberId = ((MyApplication) activity.getApplication()).getMemberId();
                LocalCourseInfo info = saveCourseData(memberId, destPath, orientation, title,
                        description);
                return info;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(LocalCourseInfo info) {
        super.onPostExecute(info);
    }

    protected LocalCourseInfo saveCourseData(String memberId, String path, int orientation, String
            title, String description) {
        if (path == null) {
            return null;
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        String parent = new File(path).getParentFile().getPath();

        if (parent.endsWith(File.separator)) {
            parent = parent.substring(0, parent.length() - 1);
        }
        LocalCourseInfo info = new LocalCourseInfo(
                path, parent, 0,
                System.currentTimeMillis(), CourseType.COURSE_TYPE_LOCAL, "", description);
        info.mParentPath = parent;
        info.mOrientation = orientation;
        info.mMemberId = memberId;
        info.mTitle = title;
        LocalCourseDao localCourseDao = new LocalCourseDao(activity);
        try {
            List<LocalCourseDTO> localCourseDTOs = localCourseDao.getLocalCourseByPath(memberId, path);
            if(localCourseDTOs != null && localCourseDTOs.size() > 0) {
                localCourseDao.updateLocalCourse(memberId, path, info);
            } else {
                LocalCourseDTO dto = info.toLocalCourseDTO();
                dto.setmMemberId(memberId);
                localCourseDao.addOrUpdateLocalCourseDTO(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }
}
