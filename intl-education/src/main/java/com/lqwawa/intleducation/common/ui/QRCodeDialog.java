package com.lqwawa.intleducation.common.ui;

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

import com.lqwawa.intleducation.R;
import com.oosic.apps.iemaker.base.BaseUtils;
import com.oosic.apps.iemaker.base.Md5FileNameGenerator;

import java.io.File;

/**
 * Created by LQwawa on 15/10/12.
 */
public class QRCodeDialog extends Dialog{
    Context mContext = null;
    ImageView mQRCodeImage = null;
    TextView mTitleTV = null;
    TextView mOtherDscpTv = null;
    View mCancelBtn = null;
    View mSaveBtn = null;
    View mShareBtn = null;
    Bitmap mQRBitmap = null;
    ShareHandler mShareHandler = null;
    SaveQRCodeListener mSaveQRCodeListener = null;
    String mUrl = null;
    String mTitle = null;
    String mId = null;
    private ImageView mLeftCancelButton;//左边的X按钮
    private TextView mDialogTitleTextView;//对话框的标题
    private String mDialogTitle;
    private TextView mPhotoDesc;

    public QRCodeDialog(Context context) {
        super(context, R.style.Theme_Public_Dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qrcode);
        //X号点击消失
        mLeftCancelButton = (ImageView) findViewById(R.id.contacts_dialog_left_button);
        mLeftCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCodeDialog.this.dismiss();
            }
        });
        //对话框标题
        mDialogTitleTextView = (TextView) findViewById(R.id.contacts_dialog_title);
        mQRCodeImage = (ImageView)findViewById(R.id.qrcode_img);

        mPhotoDesc = (TextView) findViewById(R.id.qr_photo_desc);

        //标题
        mTitleTV = (TextView)findViewById(R.id.course_title);
        mOtherDscpTv = (TextView)findViewById(R.id.other_dscp);
        mCancelBtn = findViewById(R.id.cancel_btn);
        mSaveBtn = findViewById(R.id.save_btn);
        mShareBtn = findViewById(R.id.share_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeDialog.this.dismiss();
            }
        });
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qrCodeImage = saveQRCodeToLocal();
                if (qrCodeImage != null ) {
                    if (mSaveQRCodeListener != null) {
                        mSaveQRCodeListener.onSaveQRCodeImage(qrCodeImage);
                    }
                    Toast.makeText(mContext, mContext.getString(R.string.label_image_saved_to, qrCodeImage),
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.label_image_save_failed), Toast.LENGTH_LONG).show();
                }
                QRCodeDialog.this.dismiss();
            }
        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
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

    /**
     * 在线课堂之前传图片显示扫描二维码信息
     * @param bitmap
     * @param id
     * @param dialogTitle
     * @param courseTile
     * @param otherDescription
     * @param shareHandler
     * @param saveQRCodeListener
     */
    public void setup(String url,Bitmap bitmap, String id, String dialogTitle,String courseTile, String
            otherDescription,
                      ShareHandler shareHandler, SaveQRCodeListener saveQRCodeListener) {
        mUrl = url;
        mId = id;
        mDialogTitle = dialogTitle;
        mTitle = courseTile;
        mShareHandler = shareHandler;
        mSaveQRCodeListener = saveQRCodeListener;
        if (mDialogTitleTextView != null){
            mDialogTitleTextView.setText(dialogTitle);
        }
        if (mTitleTV != null) {
            mTitleTV.setText(courseTile);
        }
        if (otherDescription != null && mOtherDscpTv != null) {
            mOtherDscpTv.setVisibility(View.VISIBLE);
            mOtherDscpTv.setText(otherDescription);
        }

        if (null == otherDescription){
            mPhotoDesc.setVisibility(View.VISIBLE);
        }else {
            mPhotoDesc.setVisibility(View.GONE);
        }

        mQRCodeImage.setImageBitmap(bitmap);

        /*if (url != null) {
            QRCodeCreator.QRCodeCreateThread QRCodeCreateThread = new QRCodeCreator.QRCodeCreateThread(
                    mContext, url, null,
                    new QRCodeCreator.QRCodeCreateFinishHandler(){
                        @Override
                        public void onQRCodeCreate(Bitmap QRCodeBmp, String filePath) {
                            if (QRCodeBmp != null) {
                                mQRCodeImage.setImageBitmap(QRCodeBmp);
                            }
                        }
                    }
            );
            QRCodeCreateThread.execute();
        }*/

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
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    private String saveQRCodeToLocal() {
        boolean bSave = false;
        String savePath = getQRCodeImageSavePath();
        if (!new File(savePath).exists()) {
            View QRCodeGrp = findViewById(R.id.qrcode_grp);
            QRCodeGrp.setDrawingCacheEnabled(true);
            Bitmap bmp = Bitmap.createBitmap(QRCodeGrp.getDrawingCache());
            QRCodeGrp.setDrawingCacheEnabled(false);
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
        QRCodeDialog.this.dismiss();
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
