package com.galaxyschool.app.wawaschool.common;

import android.content.Intent;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.fragment.CampusPatrolPickerFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by E450 on 2016/12/13.
 * 校园巡查工具类
 */
public class CampusPatrolUtils {

    public static boolean hasStudyTaskAssigned;
    public static boolean isAutoUpload = true;//设置自动上传

    public static final boolean SHOW_PRINCIPAL_ASSISTANT = true;//是否显示校长助手

    public interface Constants {

        String EXTRA_IS_PICK = "is_pick";
        String EXTRA_MEDIA_NAME = "media_name";
        String EXTRA_CHOICE_MODE = "choice_mode";
        String EXTRA_SHOW_CHOICE_BOTTOM_LAYOUT = "show_choice_bottom_layout";
    }

    public interface ViewMode {
        int NORMAL = 0;
        int EDIT = 1;
    }

    public interface EditMode {
        int CANCEL = -1;
        int UPLOAD = 0;
        int DELETE = 1;
        int RENAME = 2;
    }

    public static List<String> getScreeningDate(Intent intent){
        if (intent == null){
            return null;
        }
        String startDate = intent.getStringExtra(CampusPatrolPickerFragment.START_DATE);
        String endDate = intent.getStringExtra(CampusPatrolPickerFragment.END_DATE);
        List<String> dateList = new ArrayList<>();
        dateList.add(startDate);
        dateList.add(endDate);
        return dateList;
    }

    public static String getStartDate (Intent intent){
        List<String> list = getScreeningDate(intent);
        if (list == null){
            return null;
        }else {
            return list.get(0);
        }
    }

    public static String getEndDate (Intent intent){
        List<String> list = getScreeningDate(intent);
        if (list == null){
            return null;
        }else {
            return list.get(1);
        }
    }

    /**
     * 比较日期大小
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    public static boolean isValidDate(String startDateStr, String endDateStr) {
        boolean isOk = true;
        if (TextUtils.isEmpty(startDateStr) || TextUtils.isEmpty(endDateStr)){
            isOk = false;
        }
        int result = DateUtils.compareDate(endDateStr, startDateStr);
        if (result < 0) {
            isOk = false;
        }
        return isOk;
    }

    public static void setHasStudyTaskAssigned(boolean hasStudyTaskAssigned) {
        CampusPatrolUtils.hasStudyTaskAssigned = hasStudyTaskAssigned;
    }

    public static boolean hasStudyTaskAssigned() {
        return hasStudyTaskAssigned;
    }
}
