package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.oosic.apps.share.ShareInfo;

public class ShareCommitUtils {

    public static void shareCommitData(Activity activity, String resId) {
        WawaCourseUtils wawaCourseUtils = new WawaCourseUtils(activity);
        wawaCourseUtils.loadCourseDetail(resId);
        wawaCourseUtils.setOnCourseDetailFinishListener(courseData -> {
            if (courseData != null) {
                NewResourceInfo newResourceInfo = courseData.getNewResourceInfo();
                if (newResourceInfo != null) {
                    CourseInfo courseInfo = newResourceInfo.getCourseInfo();
                    if (courseInfo != null) {
                        ShareInfo shareInfo = courseInfo.getShareInfo(activity);
                        shareInfo.setSharedResource(courseInfo.getSharedResource());
                        new ShareUtils(activity).share(activity.getWindow().getDecorView(), shareInfo);
                    }
                }
            }
        });
    }
}
