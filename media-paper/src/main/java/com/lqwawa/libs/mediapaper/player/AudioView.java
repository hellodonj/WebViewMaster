
package com.lqwawa.libs.mediapaper.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.lqwawa.libs.mediapaper.BaseChild;
import com.lqwawa.libs.mediapaper.PaperManger;
import com.lqwawa.libs.mediapaper.PaperUtils;
import com.lqwawa.libs.mediapaper.PaperUtils.childViewData;
import com.lqwawa.libs.mediapaper.R;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioView extends BaseChild {
    private LayoutInflater mInflater;

    private ImageView mRecord = null;

    private ImageView mPlay = null;

    private ImageView mPause = null;

    private ImageView mStop = null;

    private SeekBar mSeek = null;

    private Chronometer mRecDuration = null;

    private TextView mPlayDuration = null;
    private TextView mTotalDuration = null;
    private String mPath = null;

    Handler mHandler = new Handler();;

    MediaRecorder mMediaRecorder01;

    MediaPlayer mPlayer;

//    private File myRecAudioFile;

    private Context mContext;

    public boolean isRecord;

    private childViewData mchildViewData = null;

    private PaperManger mManager;

    private int mPlayedTime = 0;

    private AudioBarTrackingTouchListener mTrackingTouchListener = null;
    
    
    Timer timer = null;
    TimerTask task = new TimerTask() {
       public void run() {
          myHandler.sendEmptyMessage(TIMER_END);
       }
    };

    public AudioView(Context context, childViewData childData, PaperManger manager, int width) {
        super(context);
        mInflater = LayoutInflater.from(context);
        try {
           mInflater.inflate(R.layout.audio, this);
        } catch (OutOfMemoryError e) {
           return;
        }
        mPath = childData.mViewData;
        mchildViewData = childData;
        mContext = context;
        mManager = manager;
        if (mPath == null) {
            isRecord = true;
            mPath = PaperUtils.SDCARD_PATH + "/wmtest/";
        }
        if (mPath.lastIndexOf(File.separator) == (mPath.length() - 1)) {
            isRecord = true;
        } else {
            isRecord = false;
        }
        findViews();
//        View baseLayout = findViewById(R.id.base_layout);
//        if (baseLayout != null) {
//            ViewGroup.LayoutParams lp = baseLayout.getLayoutParams();
//            if (lp != null) {
//                lp.width = width;
//                baseLayout.setLayoutParams(lp);
//            }
//        }
        setDeleteMode(false);
    }


    private void findViews() {
        mRecord = (ImageView) findViewById(R.id.recordbtn);
        mRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecord();
            }
        });
        mPlay = (ImageView) findViewById(R.id.recplaybtn);
        mPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    playRecord();
                } catch(Exception e) {
//                     Utils.ShowDialog(mContext, R.string.error_audio_msg);
                    PaperUtils.showMessage(mContext, mContext.getString(R.string.error_audio_msg));
                    stopPlayAndRecord();
                    mPlayDuration.setVisibility(View.INVISIBLE);
                    mTotalDuration.setVisibility(View.INVISIBLE);
                }
            }
        });
        mPause = (ImageView) findViewById(R.id.recpausebtn);
        mPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mPlayer != null) {
                        mPlayedTime = mPlayer.getCurrentPosition();
                        mPlayer.pause();
                    }
                    mPlay.setVisibility(View.VISIBLE);
                    mPause.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mStop = (ImageView) findViewById(R.id.recstopbtn);
        mStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlayAndRecord();
            }
        });
        mSeek = (SeekBar) findViewById(R.id.audioseek);
        mSeek.setOnSeekBarChangeListener(seekListener);
        mSeek.setVisibility(View.VISIBLE);
        mRecDuration = (Chronometer) findViewById(R.id.recduration);
        mPlayDuration = (TextView) findViewById(R.id.playduration);
        mTotalDuration = (TextView) findViewById(R.id.playTotal);
        
        mPlayDuration.setVisibility(View.INVISIBLE);
        mTotalDuration.setVisibility(View.INVISIBLE);
        if (isRecord) {
            showRecord();
        } else {
            hideRecord();
        }
    }

    private OnSeekBarChangeListener seekListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
            // TODO Auto-generated method stub
            try {
                if (fromUser && (mPlayer != null)) {
                    // // if(!isOnline){
                    mPlayer.seekTo(progress);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
           if (mTrackingTouchListener != null) {
              mTrackingTouchListener.onTouchChange(AudioView.this, true);
           }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
           if (mTrackingTouchListener != null) {
              mTrackingTouchListener.onTouchChange(AudioView.this, false);
           }
        }
    };

    @Override
    public void setDeleteMode(boolean bDelMode) {
        mbDelMode = bDelMode;
        View deleteBtn = findViewById(R.id.dele);
        if (deleteBtn != null) {
            if (bDelMode) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }
        }
    }

    public interface AudioBarTrackingTouchListener {
        public void onTouchChange(View v, boolean startOrStop); // true is start, false is stop
    }
    
    public void setSeekbarTrackingTouchListener(AudioBarTrackingTouchListener trackingTouchListener) {
       mTrackingTouchListener = trackingTouchListener;
    }

    private OnPreparedListener prepareListener = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer arg0) {
            // TODO Auto-generated method stub
            try {
                if (mPlayer != null) {
                    int i = mPlayer.getDuration();
                    mSeek.setMax(i);
                    i /= 1000;
                    int minute = i / 60;
                    int hour = minute / 60;
                    int second = i % 60;
                    minute %= 60;
                    minute = minute + hour * 60;
//                    if(minute < 0 || second < 0)
//                       mTotalDuration.setText("00:00");
//                    mTotalDuration.setText(String.format("%02d:%02d", minute, second));
                    //added by rmpan
                    if(minute < 0 || second < 0)
                        mTotalDuration.setText("00:00:00");
                     mTotalDuration.setText(String.format("%02d:%02d:%02d", hour,minute, second));
                     
                    mPlayDuration.setVisibility(View.VISIBLE);
                    mTotalDuration.setVisibility(View.VISIBLE);
                    myHandler.sendEmptyMessage(PROGRESS_CHANGED);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private OnCompletionListener completeListener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer arg0) {
            try {
                if (mPlayer != null) {
                    mPlayer.stop();// .stopPlayback();
                    mPlayer.release();
                    mPlayer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    myHandler.removeMessages(PROGRESS_CHANGED);
                    showPlayFinished();
                }
            }, 200);
        }
    };

    private OnErrorListener recordErr = new OnErrorListener() {

        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            // TODO Auto-generated method stub

        }

    };

    private MediaPlayer.OnErrorListener playErr = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
//           mPlayDuration.setVisibility(View.INVISIBLE);
//           Utils.ShowDialog(mContext, R.string.error_audio_msg);
           PaperUtils.showMessage(mContext, mContext.getString(R.string.error_audio_msg));
           stopPlayAndRecord();
           mTotalDuration.setText("00:00:00");
            return true;
        }

    };

    private final static int PROGRESS_CHANGED = 0;
    private final static int TIMER_END = 100;
    Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub

            switch (msg.what) {

                case PROGRESS_CHANGED:
                    try {

                        if (mPlayer != null) {
                            int i = mPlayer.getCurrentPosition();
                            int duration = mPlayer.getDuration();
                            if (i < duration)
                                mSeek.setProgress(i);
//                            mPlayDuration.setText(String.format("%02d:%02d", minute,
//                                    second));
                            //changed by rmpan
                            mPlayDuration.setText(getFormatTime(i));
                            sendEmptyMessageDelayed(PROGRESS_CHANGED, 100);
                        } else {
                            mSeek.setProgress(mSeek.getMax());
                            mPlayDuration.setText(mSeek.getMax());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case TIMER_END:
                   stopPlayAndRecord();
                    PaperUtils.showMessage(mContext, mContext.getString(R.string.recordend));
                   break;
            }

            super.handleMessage(msg);
        }
    };

    private String getFormatTime(int currentPosition) {
        int position = currentPosition;
        position /= 1000;
        int minute = position / 60;
        int hour = minute / 60;
        int second = position % 60;
        minute %= 60;
        minute = minute + hour * 60;

        return String.format("%02d:%02d:%02d", hour, minute,
                second);
    }

    private void startRecord() {
        try {
           if (!mManager.isEditMode()) {
               PaperUtils.showMessage(mContext, mContext.getString(R.string.record_in_preview_alart));
           } else {
               mManager.stopAll();
               long dateTaken = System.currentTimeMillis();
               String strTempFile = Long.toString(dateTaken);
               File myRecAudioFile = new File(mPath + strTempFile + ".m4a");
               mMediaRecorder01 = new MediaRecorder();
               mMediaRecorder01.setOnErrorListener(recordErr);
               mMediaRecorder01.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
               mMediaRecorder01.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
               mMediaRecorder01.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
               mMediaRecorder01.setOutputFile(myRecAudioFile.getAbsolutePath());
               mMediaRecorder01.prepare();
               mRecDuration.setFormat("Recording %s");
               
               mRecDuration.setBase(SystemClock.elapsedRealtime());
              
               mRecDuration.start();
               mMediaRecorder01.start();
               mPath = mPath + strTempFile + ".m4a";
               mchildViewData.mViewData = mPath;
               mRecord.setEnabled(false);
               
               if (timer == null) {
                  timer = new Timer();
               }
               timer.schedule(task, PaperUtils.RECORD_MAX * 1000);

           }
        } catch (Exception e) {
            // TODO Auto-generated catch block
//           Utils.ShowDialog(mContext, R.string.error_record_msg);
            PaperUtils.showMessage(mContext, mContext.getString(R.string.error_record_msg));
           stopPlayAndRecord();
            e.printStackTrace();
        }
    }

    private void playRecord() throws IllegalStateException, IOException {
//      if (!isRecord)
//         myRecAudioFile = new File(mPath);
      mManager.stopOthersExceptId(this.getId());
      if (mPath != null && !isRecord) {
         mPlay.setVisibility(View.GONE);
         mPause.setVisibility(View.VISIBLE);
         if (mPlayer == null) {
            mPlayer = new MediaPlayer();

            Uri media = Uri.parse(mPath);
            mPlayer = MediaPlayer.create(mContext, media);
         }
         if (mPlayer != null) {

            mPlayer.setOnErrorListener(playErr);
//            mPlayer.prepare();
            mPlayer.setOnPreparedListener(prepareListener);
            mPlayer.setOnCompletionListener(completeListener);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mPlayer.seekTo(mPlayedTime); // resume play
            mPlayer.start();
         } else {
//            Utils.ShowDialog(mContext, R.string.error_audio_msg);
             PaperUtils.showMessage(mContext, mContext.getString(R.string.error_audio_msg));
            showPlayFinished();
            mPlayDuration.setVisibility(View.INVISIBLE);
            mTotalDuration.setVisibility(View.INVISIBLE);
         }
      } else {
         String msg = mContext.getResources().getText(R.string.play_error)
               .toString();
          PaperUtils.showMessage(mContext, msg);
      }
    }

    public void stopPlayAndRecord() {
        try {
           if(timer != null) {
              timer.cancel();
              timer = null;
           }
            if (mMediaRecorder01 != null) {
                mMediaRecorder01.stop();
                mMediaRecorder01.release();
                mMediaRecorder01 = null;

                mRecDuration.stop();
                
                hideRecord();
            }
            if (mPlayer != null) {
                mPlayer.stop();
                myHandler.removeMessages(PROGRESS_CHANGED);
                mPlayer.release();
                mPlayer = null;
                
                showPlayFinished();
            }
        } catch (Exception e) {
//           if (mMediaRecorder01 != null) {
//              mRecDuration.stop();
//              hideRecord();
//          }
//          if (mPlayer != null) {
//               showPlayFinished();
//          }
            e.printStackTrace();
        }
    }

//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
////       mManager.setFocusChild(this);
//       if(mbEnable) {
//           switch (v.getId()) {
//               case R.id.recordbtn:
//                   startRecord();
//                   break;
//               case R.id.recplaybtn:
//                  try{
//                   playRecord();
//                  } catch(Exception e) {
////                     Utils.ShowDialog(mContext, R.string.error_audio_msg);
//                      PaperUtils.showMessage(mContext, mContext.getString(R.string.error_audio_msg));
//                     stopPlayAndRecord();
//                     mPlayDuration.setVisibility(View.INVISIBLE);
//                     mTotalDuration.setVisibility(View.INVISIBLE);
//                  }
//                   break;
//               case R.id.recpausebtn:
//                   try {
//                        if (mPlayer != null) {
//                          mPlayedTime = mPlayer.getCurrentPosition();
//                          mPlayer.pause();
//                       }
//                       mPlay.setVisibility(View.VISIBLE);
//                       mPause.setVisibility(View.GONE);
//                   } catch (Exception e) {
//                       e.printStackTrace();
//                   }
//                   break;
//               case R.id.recstopbtn:
//                   stopPlayAndRecord();
//                   break;
//
//           }
//       }
//    }
    
    private void showRecord() {
       mRecord.setVisibility(View.VISIBLE);
       mRecDuration.setVisibility(View.VISIBLE);

       mPlay.setVisibility(View.GONE);
       mPause.setVisibility(View.GONE);
       mSeek.setVisibility(View.GONE);
       mPlayDuration.setVisibility(View.INVISIBLE);
       mTotalDuration.setVisibility(View.INVISIBLE);
       mStop.setVisibility(View.VISIBLE);
    }
    private void hideRecord() {
       mRecord.setVisibility(View.GONE);
       mRecDuration.setVisibility(View.GONE);

       mPlay.setVisibility(View.VISIBLE);
       mPause.setVisibility(View.GONE);
       mSeek.setVisibility(View.VISIBLE);
//       mPlayDuration.setVisibility(View.VISIBLE);
//       mTotalDuration.setVisibility(View.VISIBLE);
//       mPlayDuration.setText("00:00");
//       mTotalDuration.setText("00:00:00");
       mStop.setVisibility(View.GONE);
        isRecord = false;
    }
    
    private void showPlayFinished() {
       mSeek.setProgress(0);
       mPlayedTime = 0;
       mPlay.setVisibility(View.VISIBLE);
       mPause.setVisibility(View.GONE);
//       mTotalDuration.setText("00:00:00");
       mPlayDuration.setText("00:00:00");
      
    }
    
    @Override
   protected void onWindowVisibilityChanged(int visibility) {
      // TODO Auto-generated method stub
       stopPlayAndRecord();
      super.onWindowVisibilityChanged(visibility);
   }
 
 
//   Chronometer.OnChronometerTickListener ticklistener = new OnChronometerTickListener() {
//      
//      @Override
//      public void onChronometerTick(Chronometer chronometer) {
//         // TODO Auto-generated method stub
//      }
//   };
    
    private boolean mbEnable = true;
    public void setEnable(boolean bEnable) {
       if (mbEnable != bEnable) {
          mbEnable = bEnable;
          if (mbEnable) {
             mRecord.setVisibility(View.VISIBLE);
             mPlay.setVisibility(View.VISIBLE);
             mPause.setVisibility(View.VISIBLE);
             mStop.setVisibility(View.VISIBLE);
          } else {
             mRecord.setVisibility(View.INVISIBLE);
             mPlay.setVisibility(View.INVISIBLE);
             mPause.setVisibility(View.INVISIBLE);
             mStop.setVisibility(View.INVISIBLE);
          }
       }
    }
  }
