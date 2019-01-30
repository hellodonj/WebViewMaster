package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.galaxyschool.app.wawaschool.R;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.Md5FileNameGenerator;
import com.osastudio.common.utils.LQImageLoader;
import com.osastudio.common.utils.TipMsgHelper;

import java.io.File;

/**
 * Created by pp on 15/10/12.
 */
public class ARCodeDialog extends Dialog{
    Context mContext = null;

    Bitmap mQRBitmap = null;
    ShareHandler mShareHandler = null;
    SaveQRCodeListener mSaveQRCodeListener = null;
    String mUrl = null;
    String mTitle = null;
    String mId = null;

    private String mDialogTitle;
    private TextView mDialogTitleTextView;
    private ImageView mQRCodeImage;
    private TextView mOtherDscpTv;
    private View mSaveBtn;
    private TextView mCourseTitle;

    public ARCodeDialog(Context context) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arcode_view);

        //对话框标题
        mDialogTitleTextView = (TextView) findViewById(R.id.dialog_title);
        mQRCodeImage = (ImageView)findViewById(R.id.qrcode_img);

        mCourseTitle = (TextView) findViewById(R.id.course_title);

        mOtherDscpTv = (TextView)findViewById(R.id.other_dscp);

        mSaveBtn = findViewById(R.id.save_btn);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qrCodeImage = saveQRCodeToLocal();
                if (qrCodeImage != null ) {
                    if (mSaveQRCodeListener != null) {
                        mSaveQRCodeListener.onSaveQRCodeImage(qrCodeImage);
                    }
                    TipMsgHelper.ShowLMsg(mContext,mContext.getString(R.string.image_saved_to, qrCodeImage));
                } else {
                    TipMsgHelper.ShowLMsg(mContext,mContext.getString(R.string.save_failed));
                }
                ARCodeDialog.this.dismiss();
            }
        });

        //外部点击消失
        setCanceledOnTouchOutside(true);
        resizeDialog(0.9f);
    }

    public void resizeDialog(float resize){
        if (resize <= 0){
            return;
        }
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager windowManager = ((Activity)mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int)(display.getWidth() * resize);
        window.setAttributes(lp);
    }

    public void setup(String url, String dialogTitle,String courseTitle,ShareHandler shareHandler, SaveQRCodeListener saveQRCodeListener) {
        mUrl = url;

        mDialogTitle = dialogTitle;

        mShareHandler = shareHandler;
        mSaveQRCodeListener = saveQRCodeListener;
        if (mDialogTitleTextView != null){
            mDialogTitleTextView.setText(dialogTitle);
        }

        if (mCourseTitle != null){
            mCourseTitle.setText(courseTitle);
        }


        if (!TextUtils.isEmpty(url)){
            LQImageLoader.displayImage(url,mQRCodeImage);
        }

    }

    public String getQRCodeImageSavePath() {
        if (mUrl != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(mTitle);
            builder.append("_");
            if(!TextUtils.isEmpty(mId)) {
                builder.append(mId);
            } else {
                builder.append(Md5FileNameGenerator.generate(mUrl+mTitle));
            }
            builder.append(".jpg");
            String filename  = builder.toString();
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Pictures", filename).getAbsolutePath();
        } else {
            return null;
        }
    }

    private void updateGallery(String filename)//filename是我们的文件全名，包括后缀哦
    {
        MediaScannerConnection.scanFile(
                mContext,
                new String[] { filename }, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    private String saveQRCodeToLocal() {
        boolean bSave = false;
        String savePath = getQRCodeImageSavePath();
        if (!new File(savePath).exists()) {

            mQRCodeImage.setDrawingCacheEnabled(true);
            Bitmap bmp = Bitmap.createBitmap(mQRCodeImage.getDrawingCache());
            mQRCodeImage.setDrawingCacheEnabled(false);
            if (bmp != null) {
                bSave = BaseUtils.writeToCacheJPEG(bmp, savePath, 100);
            }

        } else {
            bSave = true;
        }
        if (bSave) {
            Uri uri = Uri.fromFile(new File(savePath));
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            updateGallery(savePath);
            return savePath;
        } else {
            return null;
        }


    }

    private void shareQRCode() {
        String QRCodeImage = saveQRCodeToLocal();
        ARCodeDialog.this.dismiss();
        if (mShareHandler != null && QRCodeImage != null) {
            mShareHandler.shareQRCode(mTitle, QRCodeImage);
        }
    }

    public interface ShareHandler {
        public void shareQRCode(String title, String qrCodePath);
    }

    public interface SaveQRCodeListener {
        public String onSaveQRCodeImage(String QRCodeImagePath);
    }
}
