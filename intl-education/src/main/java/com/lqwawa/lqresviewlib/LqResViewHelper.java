package com.lqwawa.lqresviewlib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import com.lecloud.sdk.constant.PlayerParams;
import com.lqwawa.lqresviewlib.leplay.LePlayHelper;
import com.lqwawa.lqresviewlib.leplay.ui.PlayActivity;
import com.lqwawa.lqresviewlib.office365.WebActivity;
import com.lqwawa.lqresviewlib.weike.SimplePlayBackActivity;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInPlaybackParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;

/**
 * Created by XChen on 2016/12/29.
 * email:man0fchina@foxmail.com
 */

public class LqResViewHelper {
    public static void init(Application application) {
        LePlayHelper.init(application);
    }

    public static void startLeVideoPlay(Activity activity, String vuid, String uuid, String title) {
        Intent intent = new Intent(activity, PlayActivity.class);
        Bundle mBundle = new Bundle();

        mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
        mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
        mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
        mBundle.putString(PlayerParams.KEY_PLAY_CHECK_CODE, "");
        mBundle.putString(PlayerParams.KEY_PLAY_PAYNAME, "0");
        mBundle.putString(PlayerParams.KEY_PLAY_USERKEY, "151398");
        mBundle.putString(PlayerParams.KEY_PLAY_PU, "0");
        mBundle.putString("title", title);
        intent.putExtra(PlayActivity.DATA, mBundle);
        activity.startActivity(intent);
    }

    public static void playWeike(Activity activity,
                                 String userId,
                                 String userName,
                                 String courseUrl,
                                 String title,
                                 int userType,
                                 String cacheDirPath,
                                 int orientation,
                                 int resTyp) {
        if (courseUrl.endsWith(".zip")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf('.'));
        } else if (courseUrl.contains(".zip?")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf(".zip?"));
        }
        resTyp = (resTyp == 23) ? 18 : resTyp;
        activity.startActivity(getIntentForPlayCourse(activity,
                userId,
                userName,
                courseUrl,
                title,
                userType,
                cacheDirPath,
                orientation,
                resTyp
                ,false));
    }

    public static void shareScreen(Activity activity,
                                 String userId,
                                 String userName,
                                 String courseUrl,
                                 String title,
                                 int userType,
                                 String cacheDirPath,
                                 int orientation,
                                 int resTyp,
                                 boolean shareScreen) {
        if (courseUrl.endsWith(".zip")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf('.'));
        } else if (courseUrl.contains(".zip?")) {
            courseUrl = courseUrl.substring(0, courseUrl.lastIndexOf(".zip?"));
        }
        resTyp = (resTyp == 23) ? 18 : resTyp;
        activity.startActivity(getIntentForPlayCourse(activity,
                userId,
                userName,
                courseUrl,
                title,
                userType,
                cacheDirPath,
                orientation,
                resTyp,
                shareScreen));
    }

    public static Intent getIntentForPlayCourse(Context context,
                                                String userId,
                                                String userName,
                                                String courseUrl,
                                                String title,
                                                int userType,
                                                String cacheDirPath,
                                                int orientation,
                                                int resType,
                                                boolean shareScreen) {
        Intent intent = new Intent(context, SimplePlayBackActivity.class);
        Bundle extras = new Bundle();
        extras.putString(SimplePlayBackActivity.FILE_PATH, courseUrl);
        extras.putString(SimplePlayBackActivity.FILE_NAME, title);
        extras.putInt(SimplePlayBackActivity.USER_ID, userType);
        extras.putString(SimplePlayBackActivity.CACHE_ROOT, cacheDirPath);
        extras.putInt(SimplePlayBackActivity.ORIENTATION, orientation);
        extras.putInt(SimplePlayBackActivity.PLAYBACK_TYPE, 18);
        extras.putParcelable(SlideInPlaybackParam.class.getSimpleName(),
                getSlideInPlaybackParam(userId, userName, shareScreen, orientation));
        intent.putExtras(extras);
        return intent;
    }

    public static void playBaseRes(int resType, Activity activity, String resUrl, String title) {
        switch (resType) {
            case 2:
                activity.startActivity(new Intent("android.intent.action.MUSIC_PLAYER")
                        .setAction(Intent.ACTION_VIEW)
                        .setDataAndType(Uri.parse(resUrl)
                                , "audio/mp3"));
                break;
            case 3:
                LqResViewHelper.startLeVideoPlay(activity, resUrl, "b68e945493", title);
                break;
            case 6:
            case 20:
            case 24:
            case 25:
                WebActivity.start(activity, "http://ow365.cn/?i=11261&furl=" + resUrl.trim(), title);
                break;
            default:
                break;
        }
    }


    public static Intent getIntentForPlayCourse(Activity activity,
                                                String courseUrl,
                                                String title,
                                                int userType,
                                                String cacheDirPath,
                                                int orientation,
                                                int resType,
                                                SlideInPlaybackParam params) {
        Intent intent = new Intent(activity, SimplePlayBackActivity.class);
        Bundle extras = new Bundle();
        extras.putString(SimplePlayBackActivity.FILE_PATH, courseUrl);
        extras.putString(SimplePlayBackActivity.FILE_NAME, title);
        extras.putInt(SimplePlayBackActivity.USER_ID, userType);
        extras.putString(SimplePlayBackActivity.CACHE_ROOT, cacheDirPath);
        extras.putInt(SimplePlayBackActivity.ORIENTATION, orientation);
        extras.putInt(SimplePlayBackActivity.PLAYBACK_TYPE, resType);
        extras.putParcelable(SlideInPlaybackParam.class.getSimpleName(), params);
        intent.putExtras(extras);
        return intent;
    }

    public static SlideInPlaybackParam getSlideInPlaybackParam(String userId,
                                                               String userName,
                                                               boolean isShareScreen,
                                                               int orientation) {
        SlideInPlaybackParam param = new SlideInPlaybackParam();
        param.mCurUser = new User();
        param.mCurUser.mId = userId;
        param.mCurUser.mName = userName;
//        param.mIsShareScreen = isShareScreen;
        //隐藏投屏
        param.mIsShareScreen = false;
        //support A4 paper ratio
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            param.mRatioScreenWToH = 297.0f / 210.0f;
        } else {
            param.mRatioScreenWToH = 210.0f / 297.0f;
        }
        return param;
    }

}
