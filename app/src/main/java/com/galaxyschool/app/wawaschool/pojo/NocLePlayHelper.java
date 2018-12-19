package com.galaxyschool.app.wawaschool.pojo;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxyschool.app.wawaschool.NocLePlayActivity;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.NocVideoDetailFragment;
import com.lecloud.sdk.constant.PlayerParams;

/**
 * Created by XChen on 2016/12/29.
 * email:man0fchina@foxmail.com
 */

public class NocLePlayHelper {

    public static void startLeVideoPlay(Activity activity, String vuid, String uuid,
                                        NocEnterDetailArguments nocArgs) {
        Intent intent = new Intent(activity, NocLePlayActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
        mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
        mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
        mBundle.putString(PlayerParams.KEY_PLAY_CHECK_CODE, "");
        mBundle.putString(PlayerParams.KEY_PLAY_PAYNAME, "0");
        mBundle.putString(PlayerParams.KEY_PLAY_USERKEY, "151398");
        mBundle.putString(PlayerParams.KEY_PLAY_PU, "0");
        mBundle.putSerializable(NocEnterDetailArguments.class.getSimpleName(), nocArgs);
        intent.putExtra(NocVideoDetailFragment.DATA, mBundle);
        activity.startActivity(intent);
    }

    public static void startLeVideoPlay(Activity activity, String vuid, String uuid,
                                        NocEnterDetailArguments nocArgs, Fragment fragment) {
        Intent intent = new Intent(activity, NocLePlayActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putInt(PlayerParams.KEY_PLAY_MODE, PlayerParams.VALUE_PLAYER_VOD);
        mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
        mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
        mBundle.putString(PlayerParams.KEY_PLAY_CHECK_CODE, "");
        mBundle.putString(PlayerParams.KEY_PLAY_PAYNAME, "0");
        mBundle.putString(PlayerParams.KEY_PLAY_USERKEY, "151398");
        mBundle.putString(PlayerParams.KEY_PLAY_PU, "0");
        mBundle.putSerializable(NocEnterDetailArguments.class.getSimpleName(), nocArgs);
        intent.putExtra(NocVideoDetailFragment.DATA, mBundle);
        fragment.startActivityForResult(intent, ActivityUtils.REQUEST_CODE_RETURN_REFRESH);
    }

}
