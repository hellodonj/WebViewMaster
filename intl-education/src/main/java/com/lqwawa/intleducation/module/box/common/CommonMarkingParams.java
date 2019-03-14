package com.lqwawa.intleducation.module.box.common;

import android.text.TextUtils;

import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.io.Serializable;

/**
 * @author mrmedici
 * @desc 最新作业列表页面的参数
 */
public class CommonMarkingParams implements Serializable {

    private boolean mTutorialMode;
    private String mCurMemberId;

    public CommonMarkingParams(boolean tutorialMode, String mCurMemberId) {
        this.mTutorialMode = tutorialMode;
        this.mCurMemberId = mCurMemberId;
    }

    public boolean isTutorialMode() {
        return mTutorialMode;
    }

    public boolean isParent(){
        return !mTutorialMode &&
               !TextUtils.equals(UserHelper.getUserId(),mCurMemberId);
    }

    public String getCurMemberId() {
        return mCurMemberId;
    }
}
