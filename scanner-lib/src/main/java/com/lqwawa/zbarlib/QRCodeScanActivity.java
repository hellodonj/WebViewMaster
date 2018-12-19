package com.lqwawa.zbarlib;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.google.zxing.Result;

/**
 * @author: wangchao
 * @date: 2018/05/23
 * @desc:
 */
public class QRCodeScanActivity extends BaseScanActivity implements QRCodeScanFragment.ScanFinishListener{

    private QRCodeScanFragment qrCodeScanFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                initQRScan();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void initQRScan() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        qrCodeScanFragment = new QRCodeScanFragment();
        qrCodeScanFragment.setOnScanFinishListener(this);

        Bundle bundle = new Bundle();
        if (getIntent() != null) {
            bundle = getIntent().getExtras();
        }
        qrCodeScanFragment.setArguments(bundle);
        ft.replace(R.id.content_frame, qrCodeScanFragment, QRCodeScanFragment.TAG);
        ft.commit();
    }

    @Override
    public void onScanFinish(Result result) {
        handleResult(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (qrCodeScanFragment != null) {
            qrCodeScanFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
