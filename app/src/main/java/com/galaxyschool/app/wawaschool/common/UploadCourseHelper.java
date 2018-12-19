package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;

import com.galaxyschool.app.wawaschool.net.course.UploadCourseManager;
import com.galaxyschool.app.wawaschool.pojo.UploadParameter;

public class UploadCourseHelper {

    private Activity context;

    public UploadCourseHelper(Activity context) {
        this.context = context;
    }

    public void uploadResource(UploadParameter uploadParameter, int uploadType) {
        if (uploadParameter == null) {
            return;
        }
        UploadCourseManager uploadManager = UploadCourseManager
            .getDefault(context);
        uploadManager.upload(uploadParameter, uploadType);
    }
}
