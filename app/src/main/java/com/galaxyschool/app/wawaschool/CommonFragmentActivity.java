package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.io.Serializable;

/**
 * Fragment外包Activity通用类
 *
 * @author: wangchao
 * @date: 2017/11/20
 */

public class CommonFragmentActivity extends BaseFragmentActivity {

    public static final String EXTRA_CLASS_OBJECT = "class_object";
    public static final String EXTRA_ORIENTAION = "orientation";

    protected Fragment fragment;
    private Class clazz;

    /**
     * 启动Activity
     *
     * @param object       Fragment或Activity对象
     * @param serializable 传入xxxFragment.class
     */
    public static void start(Object object, Serializable serializable) {
        start(object, serializable, 0);
    }

    /**
     * 启动Activity
     *
     * @param object       Fragment或Activity对象
     * @param serializable 传入xxxFragment.class
     * @param requestCode  大于0需要返回结果, 0不返回
     */
    public static void start(Object object, Serializable serializable, int requestCode) {
        start(object, serializable, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, requestCode);
    }

    /**
     * 启动Activity
     *
     * @param object       Fragment或Activity对象
     * @param serializable 传入xxxFragment.class
     * @param orientation  1竖屏， 0横屏
     * @param requestCode  大于0需要返回结果, 0不返回
     */
    public static void start(Object object, Serializable serializable, int orientation, int
            requestCode) {
        MethodObject methodObject = new MethodObject(object).invoke();
        if (!methodObject.isValid()) {
            return;
        }
        Activity activity = methodObject.getActivity();
        Fragment tempFragment = methodObject.getFragment();
        Intent starter = new Intent(activity, CommonFragmentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CLASS_OBJECT, serializable);
        bundle.putInt(EXTRA_ORIENTAION, orientation);
        starter.putExtras(bundle);
        start(activity, tempFragment, starter, requestCode);
    }

    protected static void start(Activity activity, Fragment fragment, Intent starter,
                                int requestCode) {
        if (activity == null) {
            return;
        }

        if (requestCode > 0) {
            if (fragment != null) {
                fragment.startActivityForResult(starter, requestCode);
            } else {
                activity.startActivityForResult(starter, requestCode);
            }
        } else {
            if (fragment != null) {
                fragment.startActivity(starter);
            } else {
                activity.startActivity(starter);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            int orientation = args.getInt(EXTRA_ORIENTAION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        setContentView(R.layout.activity_common);

        if (args == null) {
            return;
        }
        clazz = (Class) args.getSerializable(EXTRA_CLASS_OBJECT);
        if (clazz == null) {
            return;
        }
        try {
            fragment = (Fragment) clazz.newInstance();
            if (fragment != null) {
                fragment.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.activity_body, fragment, clazz.getSimpleName());
                ft.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected static class MethodObject {
        private Object object;
        private Activity activity;
        private Fragment fragment;
        private boolean result;

        public MethodObject(Object object) {
            this.object = object;
        }

        boolean isValid() {
            return result;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public Activity getActivity() {
            return activity;
        }

        public MethodObject invoke() {
            if (object == null) {
                result = false;
                return this;
            }

            fragment = null;
            activity = null;
            if (object instanceof Fragment) {
                fragment = (Fragment) object;
                activity = fragment.getActivity();
            } else if (object instanceof Activity) {
                activity = (Activity) object;
            } else {
                result = false;
                return this;
            }
            result = true;
            return this;
        }
    }
}
