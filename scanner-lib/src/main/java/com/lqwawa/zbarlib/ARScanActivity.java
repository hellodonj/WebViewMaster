package com.lqwawa.zbarlib;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

/**
 * @author: wangchao
 * @date: 2018/05/23
 * @desc:
 */
public class ARScanActivity extends BaseScanActivity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                initARScan();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void initARScan() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ARScanFragment();
        ft.replace(R.id.content_frame, fragment, ARScanFragment.TAG);
        ft.commit();
    }
}


