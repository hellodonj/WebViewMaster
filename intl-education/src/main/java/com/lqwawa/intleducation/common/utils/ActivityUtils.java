package com.lqwawa.intleducation.common.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.lqwawa.intleducation.MainApplication;
import com.lqwawa.intleducation.base.utils.BaseUtils;
import com.lqwawa.intleducation.common.Common;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;
import com.oosic.apps.aidl.CollectParams;
import com.oosic.apps.iemaker.base.PlaybackActivity;
import com.oosic.apps.iemaker.base.data.CourseShareData;
import com.oosic.apps.iemaker.base.interactionlayer.data.SlideInPlaybackParam;
import com.oosic.apps.iemaker.base.interactionlayer.data.User;

import java.io.File;

/**
 * Created by XChen on 2016/11/30.
 * email:man0fchina@foxmail.com
 */

public class ActivityUtils {
    public static final int REQUEST_CODE_TAKE_PHOTO = 1;
    public static final int REQUEST_CODE_FETCH_PHOTO = 2;
    public static final int REQUEST_CODE_ZOOM_PHOTO = 3;

    public static void gotoTelephone(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + Common.Constance.WAWACHAT_PHONENUMBER));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void startTakePhoto(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        BaseUtils.createLocalDiskPath(Utils.IMAGE_FOLDER);
        intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Utils.IMAGE_FOLDER
                        + Utils.TEMP_IMAGE_NAME)));
        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    public static void startTakeIconPhoto(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        BaseUtils.createLocalDiskPath(Utils.ICON_FOLDER);
        intent.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Utils.ICON_FOLDER + Utils.ICON_NAME)));
        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    public static void startFetchPhoto(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        activity.startActivityForResult(intent, REQUEST_CODE_FETCH_PHOTO);
    }

    public static void startZoomPhoto(Activity activity, Uri uri, int cropSize) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", cropSize);
        intent.putExtra("outputY", cropSize);
        intent.putExtra("scaleUpIfNeeded", true);//去黑边
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                Utils.ICON_FOLDER + Utils.ZOOM_ICON_NAME)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", false);
        activity.startActivityForResult(
                intent,
                ActivityUtils.REQUEST_CODE_ZOOM_PHOTO);
    }
}
