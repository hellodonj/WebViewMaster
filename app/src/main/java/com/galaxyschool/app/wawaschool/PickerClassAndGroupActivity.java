package com.galaxyschool.app.wawaschool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.galaxyschool.app.wawaschool.fragment.PickerClassAndGroupFragment;

/**
  * ================================================
  * 作    者：Blizzard-liu
  * 版    本：1.0
  * 创建日期：2017/11/24 9:11
  * 描    述：选择班级 和 小组
  * 修订历史：
  * ================================================
  */
public class PickerClassAndGroupActivity extends CommonFragmentActivity
        implements PickerClassAndGroupFragment.Constatnts{


    public static void start(Object object, Bundle bundle) {
        MethodObject methodObject = new MethodObject(object).invoke();
        if (!methodObject.isValid()) {
            return;
        }
        Activity activity = methodObject.getActivity();
        Fragment tempFragment = methodObject.getFragment();
        Intent starter = new Intent(activity, PickerClassAndGroupActivity.class);
        bundle.putSerializable(EXTRA_CLASS_OBJECT, PickerClassAndGroupFragment.class);
        starter.putExtras(bundle);
        start(activity, tempFragment, starter, 0);
    }
}
