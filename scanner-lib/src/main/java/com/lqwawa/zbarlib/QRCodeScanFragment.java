package com.lqwawa.zbarlib;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.osastudio.common.utils.PhotoUtils;
import com.osastudio.common.utils.TipMsgHelper;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * ================================================
 * author：xu_wenliang
 * time：2018/4/25 18:11
 * desp: 描 述： 扫描二维码界面
 * ================================================
 */

public class QRCodeScanFragment extends Fragment implements ZXingScannerView.ResultHandler {
    public static final String TAG = QRCodeScanFragment.class.getSimpleName();

    private ZXingScannerView scannerView;
    private ProgressDialog progressDialog;

    public interface ScanFinishListener {
        /**
         * 扫描结束回调
         *
         * @param result
         */
        void onScanFinish(Result result);
    }

    public ScanFinishListener listener;


    public void setOnScanFinishListener(ScanFinishListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode_scan, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        if (getView() != null) {
            ImageView imageView = (ImageView) getView().findViewById(R.id.iv_close);
            if (imageView != null) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                });
            }

            TextView textView = (TextView)getView().findViewById(R.id.tv_title);
            if (textView != null) {
                textView.setText(R.string.qrcode_scanning);
            }

            textView = (TextView) getView().findViewById(R.id.tv_photo);
            if (textView != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PhotoUtils.startFetchPhoto(getActivity());
                    }
                });
            }

            ViewGroup contentFrame = (ViewGroup) getView().findViewById(R.id.content_frame);
            scannerView = new ZXingScannerView(getActivity());
            contentFrame.addView(scannerView);
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK && requestCode == PhotoUtils.REQUEST_CODE_FETCH_PHOTO) {
            if (intent == null) {
                return;
            }
            String photoPath = PhotoUtils.getImageAbsolutePath(getActivity(), intent
                    .getData());

            if (TextUtils.isEmpty(photoPath)) {
                return;
            }

            scanImage(photoPath);
        }
    }

    @SuppressLint("NewApi")
    private void scanImage(String photoPath) {
        ScanImageTask scanImageTask = new ScanImageTask();
        scanImageTask.execute(photoPath);
    }


    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        if (!new File(path).exists()) {
            return null;
        }
        
        return QRCodeDecoder.syncDecodeQRCodeForResult(getActivity(), path);
    }

    @Override
    public void handleResult(Result rawResult) {
        if (listener != null) {
            listener.onScanFinish(rawResult);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @SuppressLint({"NewApi", "StaticFieldLeak"})
    private class ScanImageTask extends AsyncTask<String, Void, Result> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProcessDialog();
        }

        @Override
        protected Result doInBackground(String... strings) {
            return scanningImage(strings[0]);
        }

        @Override
        protected void onPostExecute(Result result) {
            super.onPostExecute(result);
            dismissProcessDialog();
            if (result != null) {
                if (listener != null) {
                    listener.onScanFinish(result);
                }
            } else {
                TipMsgHelper.ShowMsg(getActivity(), R.string.scan_failed);
            }
        }
    }


    public void showProcessDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.scanning));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProcessDialog() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        } finally {
            if (progressDialog != null) {
                progressDialog = null;
            }
        }
    }
}
