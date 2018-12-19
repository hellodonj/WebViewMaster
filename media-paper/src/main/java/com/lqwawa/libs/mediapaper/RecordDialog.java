package com.lqwawa.libs.mediapaper;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Timer;
import java.util.logging.Handler;

/**
 * Created by pp on 15/12/16.
 */
public class RecordDialog extends Dialog {

    private int mWidth;

    ImageView mRecordImage = null,mDeleteImage=null;//added by rmpan for delete current recording
    Chronometer mRecDuration = null;
    TextView mRecordButton = null;

    MediaRecorder mMediaRecorder01 = null;
    String mPath = null;

    Context mContext = null;
    RecordFinishListener mRecordFinishListener = null;
    private  int mMiss = 0;
    public RecordDialog(Context context, int width, String saveFolderPath, RecordFinishListener recordFinishListener) {
        super(context, R.style.Theme_mpPageDialogFullScreen);
        mContext = context;
        mWidth = width;
        mPath = saveFolderPath;
        mRecordFinishListener = recordFinishListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_view);
        View baseLayout = findViewById(R.id.base_layout);
        ViewGroup.LayoutParams lp = baseLayout.getLayoutParams();
        if (lp != null) {
            lp.width = mWidth;
            baseLayout.setLayoutParams(lp);
        }
        mRecordImage = (ImageView)findViewById(R.id.record_img);
        mDeleteImage = (ImageView)findViewById(R.id.delete_img);
        mRecDuration = (Chronometer)findViewById(R.id.recduration);
        mRecordButton = (TextView)findViewById(R.id.record_btn);
        mRecDuration.setText(FormatMiss(0)); 
        mDeleteImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) { 
				stopRecord();
				//delete file 
				deleteRecordFile(); 
				arg0.postDelayed(new Runnable() {
                     @Override
                     public void run() {
                         RecordDialog.this.dismiss();
                     }
                 }, 500);
			}
		});
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaRecorder01 == null) {
                    startRecord();
                } else {
                    stopRecord();
                    if (mRecordFinishListener != null) {
                        mRecordFinishListener.onRecordFinish(mPath);
                    }
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecordDialog.this.dismiss();
                        }
                    }, 500);

                }
            }
        });
    }
    //below added by rmpan
    File myRecAudioFile= null;
    private void deleteRecordFile(){
    	if(myRecAudioFile != null){
			myRecAudioFile.delete();
			myRecAudioFile = null;
		}
    }
    private void startRecord() {
        try {
        	int miss = 0;
            long dateTaken = System.currentTimeMillis();
            String strTempFile = Long.toString(dateTaken);
            myRecAudioFile = new File(mPath + strTempFile + ".m4a");
            if (!myRecAudioFile.getParentFile().exists()) {
                myRecAudioFile.getParentFile().mkdirs();
            }
            mMediaRecorder01 = new MediaRecorder();
            mMediaRecorder01.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder01.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder01.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder01.setOutputFile(myRecAudioFile.getAbsolutePath());
            mMediaRecorder01.prepare();
//            mRecDuration.setFormat("Recording %s");
            //commented by rmpan
            //mRecDuration.setBase(SystemClock.elapsedRealtime()); 
            
            mRecDuration.start();
            mRecDuration.setText(FormatMiss(0));//added by rmpan
            //added by rmpan 
            mRecDuration.setOnChronometerTickListener(new OnChronometerTickListener() {
				
				@Override
				public void onChronometerTick(Chronometer arg0) {
					mMiss++;
					mRecDuration.setText(FormatMiss(mMiss));
				}
			});
            
            mMediaRecorder01.start();
            mPath = myRecAudioFile.getPath();
            if (mRecordImage != null) {
                mRecordImage.setImageResource(R.drawable.mp_recording);
            }
            if (mRecordButton != null) {
                mRecordButton.setText(R.string.stop_record);
            }

            RecordDialog.this.setCancelable(false);

        } catch (Exception e) {
            // TODO Auto-generated catch block
//           Utils.ShowDialog(mContext, R.string.error_record_msg);
            PaperUtils.showMessage(mContext, mContext.getString(R.string.error_record_msg));
            stopRecord();
            deleteRecordFile();
            e.printStackTrace();
        }
    }
 // 将秒转化成小时分钟秒
	public String FormatMiss(int miss){     
        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return hh+":"+mm+":"+ss;      
    }
  

    public void stopRecord() {
        try {
            if (mMediaRecorder01 != null) {

                //下面三个参数必须加，不加的话会奔溃，在mediarecorder.stop();
                //报错为：RuntimeException:stop failed
                mMediaRecorder01.setOnErrorListener(null);
                mMediaRecorder01.setOnInfoListener(null);
                mMediaRecorder01.setPreviewDisplay(null);

                mMediaRecorder01.stop();
                mMediaRecorder01.release();
                mMediaRecorder01 = null;

                mRecDuration.stop();

                if (mRecordImage != null) {
                    mRecordImage.setImageResource(R.drawable.mp_record);
                }
                if (mRecordButton != null) {
                    mRecordButton.setText(R.string.start_record);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface RecordFinishListener {
        public void onRecordFinish(String path); 
        
    }


}
