package com.lqwawa.zbarlib;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Result;

/**
 * ================================================
 * author：xu_wenliang
 * time：2018/5/4 11:41
 * desp: 描 述：扫描界面
 * ================================================
 */

public class ScanActivity extends BaseScanActivity implements View.OnClickListener, QRCodeScanFragment
        .ScanFinishListener {

    private TextView tvQRcode;
    private TextView tvImage;

    private QRCodeScanFragment qrCodeScanFragment;

    protected int scanMode;
    private int viewId;

    public interface ScanMode {
        int QR = 0;
        int AR = 1;
        int QR_AR = 2;
    }

    public static void start(Context context) {
        start(context, ScanMode.QR);
    }
    /**
     * 扫描界面的入口
     *
     * @param context  上下文对象
     * @param scanMode 扫描方式 0 QR, 1 AR, 2 QR&AR
     */
    public static void start(Context context, int scanMode) {
        Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra("scanMode", scanMode);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        
        initViews();

        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                if (tvQRcode != null) {
                    init();
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void initData() {
        scanMode = getIntent().getIntExtra("scanMode", ScanMode.QR_AR);
    }

    private void initViews() {
        tvQRcode = (TextView) findViewById(R.id.tv_scan_qrcode);
        tvQRcode.setOnClickListener(this);
        tvImage = (TextView) findViewById(R.id.tv_scan_image);
        tvImage.setOnClickListener(this);

        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        bottomLayout.setVisibility(scanMode == ScanMode.QR_AR ? View.VISIBLE : View.GONE);
    }

    private void init() {
        switch (scanMode) {
            case ScanMode.QR:
                initQRScan();
                break;
            case ScanMode.AR:
                initARScan();
                break;
            case ScanMode.QR_AR:
                if (tvQRcode != null) {
                    changeScanMode(tvQRcode.getId());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_scan_qrcode || v.getId() == R.id.tv_scan_image) {
            if (viewId > 0 && viewId == v.getId()) {
                return;
            }
            changeScanMode(v.getId());
        }
    }

    private void changeScanMode(int viewId) {
        this.viewId = viewId;
        if (viewId == R.id.tv_scan_qrcode) {
            updateView(tvQRcode, R.color.color_code, R.drawable.qr_green);
            updateView(tvImage, R.color.white, R.drawable.ar_white);

            initQRScan();
        } else if (viewId == R.id.tv_scan_image) {
            updateView(tvQRcode, R.color.white, R.drawable.qr_white);
            updateView(tvImage, R.color.color_code, R.drawable.ar_green);

            initARScan();
        }

    }

    private void initARScan() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ARScanFragment();
        ft.replace(R.id.content_frame, fragment, ARScanFragment.TAG);
        ft.commit();
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

    private void updateView(TextView textView, int color, int drawableId) {
        textView.setTextColor(getResources().getColor(color));
        Drawable drawable = getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null, drawable, null, null);
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
