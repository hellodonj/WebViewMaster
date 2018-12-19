package com.lqwawa.libs.videorecorder;

import android.view.View;

public interface IMediaController {

    void setMediaPlayer(MediaPlayerControl player);

    void show();

    void show(int timeout);

    void hide();

    boolean isShowing();

    void setEnabled(boolean enable);

    void setAnchorView(View view);

    public interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(int milliseconds);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();
    }

    public static final class Proxy {
        private IMediaController mMediaController;

        private Proxy() {
        }

        public Proxy(IMediaController controller) {
            this.mMediaController = controller;
        }

        public void show() {
            this.mMediaController.show();
        }

        public void show(int timeout) {
            this.mMediaController.show(timeout);
        }

        public void hide() {
            this.mMediaController.hide();
        }

        public void setEnabled(boolean enable) {
            this.mMediaController.setEnabled(enable);
        }

        public void setAnchorView(View view) {
            this.mMediaController.setAnchorView(view);
        }

        public boolean isShowing() {
            return this.mMediaController.isShowing();
        }

        public void setMediaPlayer(MediaPlayerControl player) {
            this.mMediaController.setMediaPlayer(player);
        }
    }

}

