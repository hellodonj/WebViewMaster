package com.galaxyschool.app.wawaschool.pojo;

import android.app.Activity;

import com.galaxyschool.app.wawaschool.common.SharedPreferencesHelper;

/**
 * Created by KnIghT on 16-6-27.
 */
public class StudyTaskFragmentCache {
    private Activity activity;
    private static final String SCHOOL_ID_POSTFIX="_SchoolId_StudyTaskFragment";
    private static final String DATE_POSTFIX="_Date_StudyTaskFragment";
    public StudyTaskFragmentCache(Activity activity){
        this.activity=activity;
    }

    public void saveLatestSchool(String memberId, String schoolId) {
        SharedPreferencesHelper.setString(activity, memberId + SCHOOL_ID_POSTFIX, schoolId);
    }

    public String getLatestSchool(String memberId) {
        return SharedPreferencesHelper.getString(activity, memberId + SCHOOL_ID_POSTFIX);
    }

    public void saveLatestDate(String memberId, String dateStr) {
        SharedPreferencesHelper.setString(activity, memberId + DATE_POSTFIX, dateStr);
    }

    public String getLatestDate(String memberId) {
        return SharedPreferencesHelper.getString(activity, memberId + DATE_POSTFIX);
    }
}
