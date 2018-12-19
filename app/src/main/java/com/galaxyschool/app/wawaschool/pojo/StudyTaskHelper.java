package com.galaxyschool.app.wawaschool.pojo;

import android.content.Context;

import com.galaxyschool.app.wawaschool.R;

/**
 * Created by E450 on 2016/11/17.
 */
public class StudyTaskHelper {

    private Context context;

    public   StudyTaskHelper(Context context) {
        this.context = context;
    }

    public  interface Constants {
        int WATCH_WAWA_COURSE = 0;
        int WATCH_RESOURCE = 1;
        int WATCH_HOMEWORK = 2;
        int SUBMIT_HOMEWORK = 3;
        int TOPIC_DISCUSSION = 4;
        int RETELL_WAWA_COURSE = 5;
    }

    public String getTypeName(int type) {
        String typeName = "";
        switch (type) {
            case Constants.WATCH_WAWA_COURSE:
                typeName = context.getString(R.string.look_through_courseware);
                break;
            case Constants.WATCH_RESOURCE:
                typeName = context.getString(R.string.look_through_courseware);
                break;
            case Constants.WATCH_HOMEWORK:
                typeName = context.getString(R.string.homeworks);
                break;
            case Constants.SUBMIT_HOMEWORK:
//                typeName = context.getString(R.string.submit_homework);
                typeName = context.getString(R.string.homeworks);
                break;
            case Constants.TOPIC_DISCUSSION:
                typeName = context.getString(R.string.discuss_topic);
                break;
            case Constants.RETELL_WAWA_COURSE:
                typeName = context.getString(R.string.retell_course);
                break;
        }
        return typeName;
    }
}
