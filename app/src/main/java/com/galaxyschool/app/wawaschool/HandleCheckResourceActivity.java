package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.fragment.HandleCheckResourceFragment;

public class HandleCheckResourceActivity extends BaseFragmentActivity {
    private Fragment fragment = null;

    public static void start(Activity activity,
                             String schoolId,
                             String classId,
                             boolean isOnlineClass){
        Intent intent = new Intent(activity,HandleCheckResourceActivity.class);
        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(schoolId)){
            args.putString(ActivityUtils.EXTRA_SCHOOL_ID, schoolId);
        }
        if (!TextUtils.isEmpty(classId)){
            args.putString(ActivityUtils.EXTRA_CLASS_ID, classId);
        }
        args.putBoolean(ActivityUtils.EXTRA_IS_ONLINE_CLASS, isOnlineClass);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        Bundle args = getIntent().getExtras();
        fragment = new HandleCheckResourceFragment();
        fragment.setArguments(args);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.activity_body, fragment, HandleCheckResourceFragment.TAG);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
