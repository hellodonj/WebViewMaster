package com.lqwawa.libs.mediapaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by pp on 15/10/30.
 */
public class MediaFrame extends BaseChild implements View.OnClickListener{
    private LayoutInflater mInflater;
    private Bitmap mBitmap = null;
    private ImageView mImageContent;
    private View mBaseLayout = null;
    private String mVideoPath = null;
    private Context mContext = null;
    private View mPlayButton = null;
    private String mResourceType = null;
//    private String mSharePlayUrl = null;
    private String mTitle = null;
    private VideoViewResourceOpenHandler mResourceOpenHandler = null;
    private int mDispLongSize = 320;
    private int mDispShortSize = 240;
    private String mThumbPath = null;
    private String mThumbCacheFolder = null;
    private String mThumbCachePath = null;
    private Bitmap mDispBitmap = null;
    private int mScreenType = 0;
    private String mWebPlayUrl = null;

    public MediaFrame(Context context) {
        this(context, null);
    }

    public MediaFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.media_frame, this);
        mImageContent = (ImageView)findViewById(R.id.thumb);
        mBaseLayout = findViewById(R.id.base_layout);
        mPlayButton = findViewById(R.id.videoPlay);
//        if (mPlayButton != null) {
//            mPlayButton.setOnClickListener(this);
//        }
        setDeleteMode(false);
    }

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

    public void setVideoData(String resourceType, String thumbPath, String thumbCacheFolder, String resourcePath,
                             String title, int dispLongSize, int dispShortSize,
                             VideoViewResourceOpenHandler openHander, boolean bShowPlayBtn, String webPlayUrl) {
        setVideoData(resourceType, thumbPath, thumbCacheFolder, resourcePath, title, dispLongSize, dispShortSize, 0, openHander, webPlayUrl);
        if (mPlayButton != null && !bShowPlayBtn) {
            mPlayButton.setVisibility(View.GONE);
        }
    }

    public void setVideoData(String resourceType, String thumbPath, String thumbCacheFolder, String resourcePath,
                             String title, int dispLongSize, int dispShortSize, int screenType,
                             VideoViewResourceOpenHandler openHander, boolean bShowPlayBtn, String webPlayUrl) {
        setVideoData(resourceType, thumbPath, thumbCacheFolder, resourcePath, title, dispLongSize, dispShortSize, screenType, openHander, webPlayUrl);
        if (mPlayButton != null && !bShowPlayBtn) {
            mPlayButton.setVisibility(View.GONE);
        }
    }

    public void setVideoData(String resourceType, String thumbPath, String thumbCacheFolder, String resourcePath,
                             String title, int dispLongSize, int dispShortSize, int screenType,
                             VideoViewResourceOpenHandler openHander, String webPlayUrl) {
        mResourceType = resourceType;
        mThumbPath = thumbPath;
        mThumbCacheFolder = thumbCacheFolder;
        mVideoPath = resourcePath;
        mTitle = title;
        mDispLongSize = dispLongSize;
        mDispShortSize = dispShortSize;
        mScreenType = screenType;
        mResourceOpenHandler = openHander;
        mWebPlayUrl = webPlayUrl;

        View baseLayout = getBaseLayout();
            if (baseLayout != null ) {
                ViewGroup.LayoutParams lp = setupLayoutFromResourceName(PaperUtils.getFileNameFromPaperPath(mThumbPath), baseLayout);
                if (lp != null) {
                    baseLayout.setLayoutParams(lp);
                }
            }

        LoadThumbTask task = new LoadThumbTask(mThumbPath);
        task.execute();

    }


    public Bitmap getVideoThumb() {
        return mBitmap;
    }

    public View getBaseLayout() {
        return mBaseLayout;
    }

    private ViewGroup.LayoutParams setupLayoutFromResourceName(String fileName, View baseLayout) {
        if (baseLayout == null) {
            return null;
        }
        ViewGroup.LayoutParams lp = null;
        if (baseLayout != null ) {
            lp = baseLayout.getLayoutParams();
            lp.width = mDispLongSize;
            lp.height = mDispShortSize;
        }
        if (TextUtils.isEmpty(fileName)) {
            return lp;
        }
        int index = fileName.lastIndexOf("_");
        if (index <= 0 || index >= fileName.length()-1) {
            return lp;
        }
        String formatStr = fileName.substring(index+1);
        index = formatStr.lastIndexOf(".");
        if (index <= 0 || index >= fileName.length()) {
            return lp;
        }
        formatStr = formatStr.substring(0, index);
        String[] pos = formatStr.split("x");
        if (pos != null && pos.length == 2) {
            try {
                int w = Integer.valueOf(pos[0]);
                int h = Integer.valueOf(pos[1]);
                if (w <= 0 || h <= 0) {
                    return lp;
                }
                if (lp != null) {
                    if (w >= h) {
                        lp.width = mDispLongSize;
                        lp.height = mDispShortSize;
                    } else {
                        lp.width = mDispShortSize;
                        lp.height = mDispLongSize;
                    }
                }
            } catch (NumberFormatException e) {

            }
        }
        return lp;
    }





    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.videoPlay) {
            if (mResourceOpenHandler != null) {
                mResourceOpenHandler.openResource(mVideoPath, mTitle);
            }
        }
    }

    public class LoadThumbTask extends AsyncTask<Void, Void, Bitmap> {
        private String mThumbPath = null;

        public LoadThumbTask(String thumbPath) {
            mThumbPath = thumbPath;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bmp = null;
            if (mThumbCachePath == null) {
                if (!TextUtils.isEmpty(mThumbPath)) {
                    if (mThumbPath.startsWith("http")) {
                        File file = new File(mThumbCacheFolder, String.valueOf(System.currentTimeMillis()));
                        mThumbCachePath = PaperUtils.getFile(mThumbPath, file.getPath());
                    } else {
                        mThumbCachePath = mThumbPath;
                    }
                }
            }
            bmp = PaperUtils.loadBitmap(mThumbCachePath, mDispLongSize, mDispShortSize);
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && !bitmap.isRecycled()) {
                mDispBitmap = bitmap;
                if (mImageContent != null) {
                    mImageContent.setImageBitmap(mDispBitmap);
                }

                View baseLayout = getBaseLayout();
                if (mDispBitmap.getWidth() < mDispBitmap.getHeight()) {
                    if (baseLayout != null && baseLayout.getWidth() > baseLayout.getHeight()) {
                        ViewGroup.LayoutParams lp = baseLayout.getLayoutParams();
                        int temp = lp.height;
                        lp.height = lp.width;
                        lp.width = temp;
                        baseLayout.setLayoutParams(lp);
                    }
                } else {
                    if (baseLayout != null && baseLayout.getWidth() < baseLayout.getHeight()) {
                        ViewGroup.LayoutParams lp = baseLayout.getLayoutParams();
                        int temp = lp.height;
                        lp.height = lp.width;
                        lp.width = temp;
                        baseLayout.setLayoutParams(lp);
                    }
                }

            }
        }
    }

    public void releaseBitmap() {
        if (mDispBitmap != null && !mDispBitmap.isRecycled()) {
            if (mImageContent != null) {
                mImageContent.setImageBitmap(null);
            }
            mDispBitmap.recycle();
            mDispBitmap = null;
        }
    }

    public void restoreBitmap() {
        if (mThumbPath != null) {
            LoadThumbTask task = new LoadThumbTask(mThumbPath);
            task.execute();
        }
    }

    public String getResourceType () {
        return mResourceType;
    }

    public String getThumbCachePath() {
        return mThumbCachePath;
    }

    public String getResourcePath() {
        return mVideoPath;
    }

    public int getScreenType() {
        return mScreenType;
    }

    public String getResourceTitle() {
        return mTitle;
    }

    public String getWebPlayUrl() {
        return mWebPlayUrl;
    }

    public interface VideoViewResourceOpenHandler {
        public void openResource(String resourceUrl, String title);
    }
}
