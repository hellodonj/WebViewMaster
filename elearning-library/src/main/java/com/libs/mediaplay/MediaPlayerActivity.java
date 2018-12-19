package com.libs.mediaplay;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.lqwawa.apps.R;
import com.lqwawa.tools.DensityUtils;
import com.osastudio.apps.BaseActivity;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;
import com.osastudio.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import static com.libs.gallery.ImageBrowserActivity.KEY_COLLECT;

/**
 * <br/>================================================
 * <br/> 作    者：Blizzard-liu
 * <br/> 版    本：1.1
 * <br/> 创建日期：2017/12/8 10:32
 * <br/> 描    述：视频播放 使用url
 * <br/> 修订历史：调整平板布局 逻辑
 * <br/>================================================
 */

public class MediaPlayerActivity extends BaseActivity implements UniversalVideoView.VideoViewCallback{

    private static final String TAG = "MediaPlayerActivity";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";
    public final static String DATA = "data";
    public final static String EXTRA_ISHIDEBTNMORE = "ishideBtnMore";
    public static final String EXTRA_VIDEO_PATH = "video_path";
    public static final String EXTRA_VIDEO_TITLE = "video_title";

    UniversalVideoView mVideoView;
    UniversalMediaController mMediaController;

    View mVideoLayout;
    protected String VIDEO_URL;
    protected String VIDEO_TITLE;
    protected boolean isHideBtnMore = true;
    protected boolean isTablet = false;
    private int mSeekPosition;
    private int cachedHeight;
    private boolean isFullscreen;
    private CustomPopWindow mPopWindow;
    private View mBtnMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplay);

        initData();

        if(TextUtils.isEmpty(VIDEO_URL)) {
            finish();
            return;
        }

        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView = (UniversalVideoView) findViewById(R.id.videoView);
        mMediaController = (UniversalMediaController) findViewById(R.id.media_controller);
        mVideoView.setMediaController(mMediaController);
        if (isTablet) {
            mVideoView.setFullscreen(true);
            mVideoView.setAutoRotation(false);
            mMediaController.setIsTablet(true);
        }
        setVideoAreaSize();
        mVideoView.setVideoViewCallback(this);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                LogUtils.logd(TAG, "onCompletion ");
            }
        });

        if (mSeekPosition > 0) {
            mVideoView.seekTo(mSeekPosition);
        }
        mVideoView.start();
        mMediaController.setTitle(VIDEO_TITLE);
        mMediaController.setBtnMoreVisible(!isHideBtnMore);
        mBtnMore = mMediaController.getBtnMore();
        mBtnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopMenu();
            }
        });

    }

    protected void initData() {

    }

    protected void showPopMenu() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu,null);
        //处理popWindow 显示内容
        handleLogic(contentView);

        mPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)//显示的布局，还可以通过设置一个View
                //                .size(260,80) //设置显示的大小，不设置就默认包裹内容
                .setFocusable(true)//是否获取焦点，默认为ture
                .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                .create()//创建PopupWindow
                .showAsDropDown(mBtnMore,-DensityUtils.dp2px(getApplicationContext(), 100),
                        DensityUtils.dp2px(getApplicationContext(), 10));
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param contentView
     */
    private void handleLogic(View contentView){
        EntryBean entryBean = new EntryBean();
        final List<EntryBean> list = new ArrayList<>();

            entryBean.value = getString(R.string.collection);
            entryBean.id = KEY_COLLECT;
            list.add(entryBean);


        ListView listView = (ListView) contentView.findViewById(R.id.pop_menu_list);
        PopMenuAdapter adapter = new PopMenuAdapter(this,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mPopWindow!=null){
                    mPopWindow.dissmiss();
                }
                EntryBean bean = list.get(i);
                if (bean.id == KEY_COLLECT) {
                    //收藏
                    handleCollectLogic();
                }
            }


        });

    }

    /**
     * 点击收藏
     */
    protected void handleCollectLogic() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.logd(TAG, "onPause ");
        if (mVideoView != null && mVideoView.isPlaying()) {
            mSeekPosition = mVideoView.getCurrentPosition();
            LogUtils.logd(TAG, "onPause mSeekPosition=" + mSeekPosition);
            mVideoView.pause();
        }
    }

    /**
     * 置视频区域大小
     */
    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
               /* int width = mVideoLayout.getWidth();
//                cachedHeight = (int) (width * 405f / 720f);
                                cachedHeight = (int) (width * 3f / 4f);
                //                cachedHeight = (int) (width * 9f / 16f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoView.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoView.setLayoutParams(videoLayoutParams);*/
                mVideoView.setVideoPath(VIDEO_URL);
                mVideoView.requestFocus();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.logd(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        LogUtils.logd(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
     /*   if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = this.cachedHeight;
            mVideoLayout.setLayoutParams(layoutParams);
        }*/

    }



    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        LogUtils.logd(TAG, "onPause UniversalVideoView callback");
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        LogUtils.logd(TAG, "onStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        LogUtils.logd(TAG, "onBufferingStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        LogUtils.logd(TAG, "onBufferingEnd UniversalVideoView callback");
    }

    @Override
    public void onBackPressed() {
        if (isTablet) {
            super.onBackPressed();
        } else {
            if (this.isFullscreen) {
                mVideoView.setFullscreen(false);
            } else {
                super.onBackPressed();
            }
        }

    }

}
