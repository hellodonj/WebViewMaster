/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lqwawa.libs.videorecorder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.util.Formatter;
import java.util.Locale;

/**
 * A view containing controls for a MediaPlayer. Typically contains the
 * buttons like "Play/Pause", "Rewind", "Fast Forward" and a progress
 * slider. It takes care of synchronizing the controls with the state
 * of the MediaPlayer.
 */
public class MediaController extends FrameLayout implements IMediaController {

    private static final int sDefaultTimeout = 3000;
    private static final int MSG_FADE_OUT = 1;
    private static final int MSG_SHOW_PROGRESS = 2;

    private Context mContext;

    private View mViewRoot;
    private View mAnchorView;
    private ImageView mPlayButton;
    private ImageView mFfwdButton;
    private ImageView mRewButton;
    private ProgressBar mProgress;
    private TextView mEndTime, mCurrentTime;

    private MediaPlayerControl mPlayer;
    private boolean mShowing;
    private boolean mDragging;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    public MediaController(Context context) {
        super(context);
        init(context);
    }

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        makeControllerView();
    }

    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        mViewRoot = inflate.inflate(R.layout.vr_media_controller, this, true);
        mViewRoot.setOnTouchListener(mTouchListener);

        initControllerView(mViewRoot);

        return mViewRoot;
    }

    @SuppressLint("WrongViewCast")
    private void initControllerView(View v) {
        mPlayButton = (ImageView) v.findViewById(R.id.vr_media_play_btn);
        if (mPlayButton != null) {
            mPlayButton.requestFocus();
            mPlayButton.setOnClickListener(mPauseListener);
        }

//        mFfwdButton = (ImageView) v.findViewById();
        if (mFfwdButton != null) {
            mFfwdButton.setOnClickListener(mFfwdListener);
            mFfwdButton.setVisibility(View.VISIBLE);
        }

//        mRewButton = (ImageButton) v.findViewById();
        if (mRewButton != null) {
            mRewButton.setOnClickListener(mRewListener);
            mRewButton.setVisibility(View.VISIBLE);
        }

        mProgress = (ProgressBar) v.findViewById(R.id.vr_media_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(1000);
        }

        mEndTime = (TextView) v.findViewById(R.id.vr_media_end_time);
        mCurrentTime = (TextView) v.findViewById(R.id.vr_media_curr_time);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    /**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * When VideoView calls this method, it will use the VideoView's parent
     * as the anchor.
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(View view) {
        mAnchorView = view;

//        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//        removeAllViews();
//        View v = makeControllerView();
//        addView(v, frameParams);
    }

    public View getTopBar() {
        return findViewById(R.id.vr_media_top_bar);
    }

    public View getBottomBar() {
        return findViewById(R.id.vr_media_bottom_bar);
    }

    public TextView getTitleView()  {
        return (TextView) findViewById(R.id.vr_media_title);
    }

    public ImageView getBackButton()  {
        return (ImageView) findViewById(R.id.vr_media_back_btn);
    }

    /**
     * Disable pause or seek buttons if the stream cannot be paused or seeked.
     * This requires the control interface to be a MediaPlayerControlExt
     */
    private void disableUnsupportedButtons() {
        try {
            if (mPlayButton != null && !mPlayer.canPause()) {
                mPlayButton.setEnabled(false);
            }
            if (mRewButton != null && !mPlayer.canSeekBackward()) {
                mRewButton.setEnabled(false);
            }
            if (mFfwdButton != null && !mPlayer.canSeekForward()) {
                mFfwdButton.setEnabled(false);
            }
            // TODO What we really should do is add a canSeek to the MediaPlayerControl interface;
            // this scheme can break the case when applications want to allow seek through the
            // progress bar but disable forward/backward buttons.
            //
            // However, currently the flags SEEK_BACKWARD_AVAILABLE, SEEK_FORWARD_AVAILABLE,
            // and SEEK_AVAILABLE are all (un)set together; as such the aforementioned issue
            // shouldn't arise in existing applications.
            if (mProgress != null && !mPlayer.canSeekBackward() && !mPlayer.canSeekForward()) {
                mProgress.setEnabled(false);
            }
        } catch (IncompatibleClassChangeError ex) {
            // We were given an old version of the interface, that doesn't have
            // the canPause/canSeekXYZ methods. This is OK, it just means we
            // assume the media can be paused and seeked, and so we don't disable
            // the buttons.
        }
    }

    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    private final OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mShowing) {
                    hide();
                }
            }
            return false;
        }
    };

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {
            case MSG_FADE_OUT:
                hide();
                break;
            case MSG_SHOW_PROGRESS:
                pos = setProgress();
                if (!mDragging && mShowing && mPlayer.isPlaying()) {
                    msg = obtainMessage(MSG_SHOW_PROGRESS);
                    sendMessageDelayed(msg, 1000 - (pos % 1000));
                }
                break;
            }
        }
    };

    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 3 seconds of inactivity.
     */
    public void show() {
        show(sDefaultTimeout);
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchorView != null) {
            setProgress();
            if (mPlayButton != null) {
                mPlayButton.requestFocus();
            }
            disableUnsupportedButtons();
            mShowing = true;

            setVisibility(View.VISIBLE);
        }
        updatePausePlay();

        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);

        if (timeout != 0) {
            mHandler.removeMessages(MSG_FADE_OUT);
            Message msg = mHandler.obtainMessage(MSG_FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchorView == null)
            return;

        if (mShowing) {
            try {
                mHandler.removeMessages(MSG_SHOW_PROGRESS);

                setVisibility(View.GONE);
            } catch (IllegalArgumentException ex) {
                Log.w("MediaController", "already removed");
            }
            mShowing = false;
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            mShowing = true;
            getTopBar().setVisibility(getTopBar().isEnabled() ? View.VISIBLE : View.GONE);
            getBottomBar().setVisibility(visibility);
        } else {
            mShowing = false;
            getTopBar().setVisibility(visibility);
            getBottomBar().setVisibility(visibility);
        }
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress( (int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));

        return position;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            show(0); // show until hide is called
            break;
        case MotionEvent.ACTION_UP:
            show(sDefaultTimeout); // start timeout
            break;
        case MotionEvent.ACTION_CANCEL:
            hide();
            break;
        default:
            break;
        }
        return true;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        show(sDefaultTimeout);
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        final boolean uniqueDown = event.getRepeatCount() == 0
                && event.getAction() == KeyEvent.ACTION_DOWN;
        if (keyCode ==  KeyEvent.KEYCODE_HEADSETHOOK
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_SPACE) {
            if (uniqueDown) {
                doPauseResume();
                show(sDefaultTimeout);
                if (mPlayButton != null) {
                    mPlayButton.requestFocus();
                }
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer.isPlaying()) {
                mPlayer.start();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if (uniqueDown && mPlayer.isPlaying()) {
                mPlayer.pause();
                updatePausePlay();
                show(sDefaultTimeout);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE
                || keyCode == KeyEvent.KEYCODE_CAMERA) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event);
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide();
            }
            return true;
        }

        show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mPlayButton != null) {
            mPlayButton.setEnabled(enabled);
        }
        if (mFfwdButton != null) {
            mFfwdButton.setEnabled(enabled);
        }
        if (mRewButton != null) {
            mRewButton.setEnabled(enabled);
        }
        if (mProgress != null) {
            mProgress.setEnabled(enabled);
        }
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    private void updatePausePlay() {
        if (mViewRoot == null || mPlayButton == null)
            return;

        if (mPlayer.isPlaying()) {
            mPlayButton.setImageResource(R.drawable.vr_media_pause);
        } else {
            mPlayButton.setImageResource(R.drawable.vr_media_play);
        }
    }

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(MSG_SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mPlayer.seekTo( (int) newPosition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime( (int) newPosition));
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);
        }
    };

    private final OnClickListener mPauseListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    private final OnClickListener mRewListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = mPlayer.getCurrentPosition();
            position -= 5000; // milliseconds
            mPlayer.seekTo(position);
            setProgress();

            show(sDefaultTimeout);
        }
    };

    private final OnClickListener mFfwdListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = mPlayer.getCurrentPosition();
            position += 15000; // milliseconds
            mPlayer.seekTo(position);
            setProgress();

            show(sDefaultTimeout);
        }
    };

}
