package com.osastudio.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by rmpan on 2016/10/23.
 * customized for android6.0(sdk>=23) or higher android edition
 */

public class PermissionsUtils {
    public static final int WRITE_CONTACTS = 1;
    public static final int READ_CALENDAR = 2;
    public static final int BODY_SENSORS = 3;
    public static final int CAMERA = 4;
    public static final int PHONE = 5;
    public static final int LOCATION = 6;
    public static final int STORAGE = 7;
    public static final int RECORD_AUDIO = 8;
    public static final int READ_SMS = 9;

    public static final int ALL = 10;

    /**
     * nine dangerous authority for android6.0 or higher edition
     */
    public static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 2;
    public static final int MY_PERMISSIONS_REQUEST_BODY_SENSORS = 3;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 4;
    public static final int MY_PERMISSIONS_REQUEST_PHONE = 5;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 6;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 7;
    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 8;
    public static final int MY_PERMISSIONS_REQUEST_READ_SMS = 9;

    public static final int MY_PERMISSIONS_REQUEST_ALL_PERMISSION = 10;
    /**
     * 每一组权限中，申请的权限以下表为准，mainifest文件中对应添加
     */
    public static  String[] mAllGroupPermission = {Manifest.permission.WRITE_CONTACTS
            , Manifest.permission.READ_CALENDAR, Manifest.permission.BODY_SENSORS, Manifest.permission.CAMERA
            , Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_SMS};


    private static boolean judgePermission(String[] groupPermission, String permission){
        boolean ret = false;
        for(int i = 0; i < groupPermission.length; i++){
            if(groupPermission[i].equals(permission)){
                ret = true;
                break;
            }
        }
        return ret;
    }


    /**
     * request all necessary authority one time
     * @param context
     * @param groupPermission
     */
    public static void requestAllPermissions(Context context, String[] groupPermission) {
        //only request necessary permission begin
        if(judgePermission(groupPermission, Manifest.permission.WRITE_CONTACTS)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.READ_CALENDAR)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_CALENDAR)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.BODY_SENSORS)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.BODY_SENSORS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.CAMERA)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.READ_PHONE_STATE)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.ACCESS_FINE_LOCATION)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.READ_EXTERNAL_STORAGE)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.RECORD_AUDIO)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        if(judgePermission(groupPermission, Manifest.permission.READ_SMS)){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
                return;
            }
        }
        //only request necessary permission end

//        if ((ContextCompat.checkSelfPermission(context,
//                Manifest.permission.WRITE_CONTACTS)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.READ_CALENDAR)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.BODY_SENSORS)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED)
//                || (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.READ_SMS)
//                != PackageManager.PERMISSION_GRANTED)) {
//
//            ActivityCompat.requestPermissions((Activity) context,
//                    groupPermission, MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
//        }
    }

    /**
     * Request custom group permission
     * @param context
     * @param groupPermission
     */
    public static void requestPermissions(Context context, String[] groupPermission) {
        if(groupPermission == null || groupPermission.length <= 0) {
            return;
        }

        ArrayList<String> permissionList = new ArrayList<String>();
        for(String permission : groupPermission) {
            if(!TextUtils.isEmpty(permission)) {
                if(ContextCompat.checkSelfPermission(context,
                        permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
        }
        if(permissionList != null && permissionList.size() > 0) {
            ActivityCompat.requestPermissions((Activity) context,
                    permissionList.toArray(new String[0]),
                    MY_PERMISSIONS_REQUEST_ALL_PERMISSION);
        }
    }

    /**
     * only request one authority one time
     * @param context
     * @param groupId
     */
    public static void requestPermissions(Context context, int groupId) {
        switch (groupId) {
            case WRITE_CONTACTS:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.WRITE_CONTACTS},
                            MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
                }
                break;

            case READ_CALENDAR:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_CALENDAR)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_CALENDAR},
                            MY_PERMISSIONS_REQUEST_READ_CALENDAR);
                }
                break;

            case BODY_SENSORS:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.BODY_SENSORS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.BODY_SENSORS},
                            MY_PERMISSIONS_REQUEST_BODY_SENSORS);
                }
                break;

            case CAMERA:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
                break;

            case PHONE:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_PHONE);
                }
                break;

            case LOCATION:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                break;

            case STORAGE:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);
                }
                break;

            case RECORD_AUDIO:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                }
                break;

            case READ_SMS:
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.READ_SMS},
                            MY_PERMISSIONS_REQUEST_READ_SMS);
                }
                break;

            default:
                break;
        }

    }

    public static boolean isAllNeccessaryAuthorityAvailable(Context context, int[] groupId) {
        boolean ret = true;
        if (context != null && groupId != null && groupId.length > 0) {
            for (int i = 0; i < groupId.length; i++) {
                switch (groupId[i]) {
                    case WRITE_CONTACTS:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.WRITE_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    case READ_CALENDAR:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_CALENDAR)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    case BODY_SENSORS:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.BODY_SENSORS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    case CAMERA:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    case PHONE:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ret = false;
                            return ret;
                        }
                        break;

                    case LOCATION:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    case STORAGE:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    case RECORD_AUDIO:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    case READ_SMS:
                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.READ_SMS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ret = false;
                            return ret;
                        }
                        break;

                    default:
                        break;
                }
            }
        }
        return ret;
    }
}
