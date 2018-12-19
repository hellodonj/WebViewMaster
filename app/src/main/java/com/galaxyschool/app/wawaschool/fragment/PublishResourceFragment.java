package com.galaxyschool.app.wawaschool.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.ContactsPickerActivity;
import com.galaxyschool.app.wawaschool.R;
import com.oosic.apps.share.ShareSettings;
import com.oosic.apps.share.SharedResource;

import java.util.Map;


public class PublishResourceFragment extends ContactsListFragment {

    public static final String TAG = PublishResourceFragment.class.getSimpleName();

    public interface Constants {
        public static final String EXTRA_RESOURE_FROM = "from";
        public static final String EXTRA_RESOURCE_MODIFIED = "modified";
        public static final String EXTRA_DATA_TITLE = "dataTitle";
        public static final String EXTRA_DATA_DESCRIPTION = "dataDesc";
        public static final String EXTRA_ENABLE_SCHOOL_OPTION = "schoolOption";

        public static final int RESOURCE_FROM_CLOUD = 0;
        public static final int RESOURCE_FROM_LOCAL = 1;
        public static final int NOTE_FROM_LOCAL = 2;
        public static final int NOTE_FROM_CLOUD = 3;

        static final int TYPE_SAVE = 0;
        static final int TYPE_SAVE_AS = 1;
        static final int TYPE_PERSONAL_WORKS = 2;
        static final int TYPE_MEDIA_ALBUM = 3;
        static final int TYPE_HOMEWORK = 4;
        static final int TYPE_COURSE = 5;
        static final int TYPE_NOTICE = 6;
        static final int TYPE_COMMENT = 7;
        static final int TYPE_CHAT_RESOURCE = 8;
        static final int TYPE_CLOUD_POST_BAR = 9;
        static final int TYPE_SCHOOL_MOVEMENT = 10;
        static final int TYPE_SCHOOL_COURSE = 11;
        static final int TYPE_CLASS_SPACE = 12;
    }

    public static void enterContactsPicker(Context context, SharedResource resource) {
        enterContactsPicker(context, resource, null);
    }

    public static void enterContactsPicker(
        Context context, SharedResource resource,
        Map<String, Object> pickerParams) {
        Bundle args = new Bundle();
        String key = ContactsPickerActivity.EXTRA_USE_EXTENDED_PICKER;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putBoolean(key, (Boolean) pickerParams.get(key));
        } else {
            args.putBoolean(key, true);
        }
        key = ContactsPickerActivity.EXTRA_PICKER_TYPE;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putInt(key, (Integer) pickerParams.get(key));
        } else {
            args.putInt(
                key,
                ContactsPickerActivity.PICKER_TYPE_PERSONAL | ContactsPickerActivity.PICKER_TYPE_GROUP);
        }
        key = ContactsPickerActivity.EXTRA_GROUP_TYPE;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putInt(key, (Integer) pickerParams.get(key));
        } else {
            args.putInt(
                key,
                ContactsPickerActivity.GROUP_TYPE_CLASS);
        }
        key = ContactsPickerActivity.EXTRA_MEMBER_TYPE;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putInt(key, (Integer) pickerParams.get(key));
        } else {
            args.putInt(
                key,
                ContactsPickerActivity.MEMBER_TYPE_ALL);
        }
        key = ContactsPickerActivity.EXTRA_PICKER_MODE;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putInt(key, (Integer) pickerParams.get(key));
        } else {
            args.putInt(
                key,
                ContactsPickerActivity.PICKER_MODE_SINGLE);
        }
        key = ContactsPickerActivity.EXTRA_PICKER_CONFIRM_BUTTON_TEXT;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putString(key, (String) pickerParams.get(key));
        } else {
            args.putString(
                key,
                context.getString(R.string.send));
        }
        key = ContactsPickerActivity.EXTRA_PUBLISH_CHAT_RESOURCE;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putBoolean(key, (Boolean) pickerParams.get(key));
        } else {
            args.putBoolean(key, true);
        }
        key = ContactsPickerActivity.EXTRA_PICKER_SUPERUSER;
        if (pickerParams != null && pickerParams.containsKey(key)) {
            args.putBoolean(key, (Boolean) pickerParams.get(key));
        } else {
            args.putBoolean(key, true);
        }

        if (resource != null) {
            if (resource.getType() == SharedResource.RESOURCE_TYPE_STREAM) {
                if(TextUtils.isEmpty(resource.getShareUrl())) {
                    resource.setShareUrl(ShareSettings.WAWAWEIKE_SHARE_URL + resource.getId());
                }
            } else if (resource.getType() == SharedResource.RESOURCE_TYPE_NOTE) {
                if(TextUtils.isEmpty(resource.getShareUrl())) {
                    resource.setShareUrl(ShareSettings.WAWAWEIKE_DIARY_SHARE_URL + resource.getId());
                }
            }
        }
        args.putSerializable(SharedResource.class.getSimpleName(), resource);
        Intent intent = new Intent(context, ContactsPickerActivity.class);
        intent.putExtras(args);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
