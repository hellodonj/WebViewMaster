package com.libs.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lqwawa.apps.R;
import com.lqwawa.tools.DensityUtils;
import com.lqwawa.tools.ScreenUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.osastudio.apps.BaseActivity;
import com.osastudio.common.popmenu.CustomPopWindow;
import com.osastudio.common.popmenu.EntryBean;
import com.osastudio.common.popmenu.PopMenuAdapter;
import com.osastudio.common.utils.LQImageLoader;
import com.osastudio.common.utils.Utils;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ImageBrowserActivity extends BaseActivity {
    private static final String TAG = "ImageBrowserActivity";

    public static final String EXTRA_CURRENT_INDEX = "current_index";
    public static final String EXTRA_IMAGE_INFOS = "image_infos";
    public static final String ISPDF = "isPdf";
    public static final String EXTRA_ISSHOWINDEX = "isShowIndex";
    public static final String KEY_ISHIDEMOREBTN = "isHideMoreBtn";//是否隐藏更多按钮
    public static final String KEY_ISSHOWCOLLECT = "isShowCollect";//
    public static final String KEY_ISSHOWCOURSEANDREADING = "isShowCourseAndReading";
    public static final String KEY_BUNDLE_VALUE_TOO_BIG = "key_bundle_value_too_big";
    public static final String KEY_IS_LOAD_LOCAL_IMAGE = "key_is_load_local_image";
    public static final String KEY_ROOM_ID = "room_id";
    public static final String KEY_TITLE = "title";

    private ExtendedViewPager mGallery;

    private ArrayList<ImageInfo> mImgsInfo;

    private TextView mTitle;
    private RelativeLayout mTopbar;
    private Timer mTimer;
    private TitlebarTimerTask mTimerTask;
    private ImageView mCloseBtn;
    private TextView mImageToolbarNum;
    private LinearLayout placeHolderLayout;
    private int mIndex;
    private boolean isPDF,//是否为PDF,PPT
            isHideMoreBtn,//是否隐藏更多按钮
            isShowCourseAndReading,//是否显示“我要做课件”，“我要加点读”
            isShowCollect,//是否显示收藏
            isShowIndex;//是否显示当前页码
    protected CustomPopWindow mPopWindow;
    protected ImageView mIvMore;
    public static final int KEY_COLLECT = 0;
    public static final int KEY_DO_COURSE = 1;
    public static final int KEY_DO_READING = 2;
    //    private DisplayImageOptions options;
    private LQImageLoader.DIOptBuiderParam param;
    private boolean isShowTopBar = true;
    private boolean isBigBundle;
    private boolean isLoadLocalImage;
    protected String title;
    protected String roomId;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        setContentView(R.layout.image_browser);

//        setStatusBarColor();

        getFiles();
        initView();

        mCloseBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        ImageLoader.getInstance().clearMemoryCache();
    }

    /**
     * 透明状态栏,5.0以上才能看到效果
     */
    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }


    private void getFiles() {
        mIndex = getIntent().getIntExtra(EXTRA_CURRENT_INDEX, 0);
        isPDF = getIntent().getBooleanExtra(ISPDF, false);
        isShowIndex = getIntent().getBooleanExtra(EXTRA_ISSHOWINDEX, false);
        isHideMoreBtn = getIntent().getBooleanExtra(KEY_ISHIDEMOREBTN, true);
        isShowCollect = getIntent().getBooleanExtra(KEY_ISSHOWCOLLECT, true);
        mImgsInfo = getIntent().getParcelableArrayListExtra(EXTRA_IMAGE_INFOS);
        isShowCourseAndReading = getIntent().getBooleanExtra(KEY_ISSHOWCOURSEANDREADING, false);
        isBigBundle = getIntent().getBooleanExtra(KEY_BUNDLE_VALUE_TOO_BIG, false);
        isLoadLocalImage = getIntent().getBooleanExtra(KEY_IS_LOAD_LOCAL_IMAGE, false);
        roomId = getIntent().getStringExtra(KEY_ROOM_ID);
        title = getIntent().getStringExtra(KEY_TITLE);
        if (isBigBundle) {
            List<ImageInfo> info = getPreferencesData();
            if (info != null && info.size() > 0) {
                mImgsInfo = (ArrayList<ImageInfo>) info;
            }
        }
        if (!isShowCollect && !isShowCourseAndReading) {
            isHideMoreBtn = true;
        }
    }

    private void showTitlebar() {
        if (TextUtils.isEmpty(roomId)) {
            changeTopBarStatus();
        } else {
            if (mImgsInfo == null || mImgsInfo.size() == 0){
                mTopbar.setBackgroundColor(getResources().getColor(R.color.uvv_black));
                mTopbar.setVisibility(View.VISIBLE);
            } else {
                changeTopBarStatus();
            }
        }
    }

    private void changeTopBarStatus(){
        if (isShowTopBar) {
            mTopbar.animate().alpha(1).y(0).setDuration(250);
        } else {
            mTopbar.animate().alpha(0).y(-mTopbar.getHeight()).setDuration(250);
        }
        isShowTopBar = !isShowTopBar;
    }

    private void startTimerOfTitlebar() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer(true);
        mTimerTask = new TitlebarTimerTask();
        mTimer.schedule(mTimerTask, 3000, 3000);
    }

    private void restartTimerOfTitlebar() {
        startTimerOfTitlebar();
    }

    private void cancelTimerOfTitlebar() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = null;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        cancelTimerOfTitlebar();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        restartTimerOfTitlebar();

    }

    private void initView() {
        mCloseBtn = (ImageView) findViewById(R.id.close_btn);
        mTitle = (TextView) findViewById(R.id.image_toolbar_title);
        if (mImgsInfo != null && mImgsInfo.size() > 0){
            String title = mImgsInfo.get(mIndex).getTitle();
            mTitle.setText(Utils.removeFileNameSuffix(title));
        } else if (!TextUtils.isEmpty(roomId)){
            mTitle.setText(title);
        }
        mTopbar = (RelativeLayout) findViewById(R.id.image_toolbar);
        mGallery = (ExtendedViewPager) findViewById(R.id.gallery);
        mImageToolbarNum = (TextView) findViewById(R.id.image_toolbar_num);
        placeHolderLayout = (LinearLayout) findViewById(R.id.layout_place_holder);
        if (mImgsInfo != null && mImgsInfo.size() > 0){
            if (isPDF || isShowIndex) {
                String str = (mIndex + 1) + "/" + mImgsInfo.size();
                mImageToolbarNum.setVisibility(View.VISIBLE);
                mImageToolbarNum.setText(str);
            }
        }
        mIvMore = (ImageView) findViewById(R.id.iv_more);
        mIvMore.setVisibility(isHideMoreBtn ? View.INVISIBLE : View.VISIBLE);
        mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopwindow(view);
            }
        });
        mIvMore.setEnabled(!isHideMoreBtn);

        mGallery.setOffscreenPageLimit(0);
//        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
//        decodeOptions.outWidth = Utils.getScreenWidth(getApplicationContext());
//        decodeOptions.outHeight = Utils.getScreenHeight(getApplicationContext());
//        options = new DisplayImageOptions
//                .Builder()
//                .showImageOnLoading(R.drawable.default_photo)
//                .showImageOnFail(R.drawable.default_photo)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .cacheInMemory(false)
//                .cacheOnDisc(true)
//                .imageScaleType(ImageScaleType.NONE)
//                .resetViewBeforeLoading(true)
//                .decodingOptions(decodeOptions)
//                .build();
        if (mImgsInfo != null && mImgsInfo.size() > 0) {
            mGallery.setAdapter(new TouchImageAdapter());
            mGallery.setCurrentItem(mIndex);
            mGallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mIndex = position;
                    if (mImgsInfo != null) {
                        if (position < mImgsInfo.size()) {
                            cancelTimerOfTitlebar();
                            String title = mImgsInfo.get(position).getTitle();
                            mTitle.setText(Utils.removeFileNameSuffix(title));
                            String str = (position + 1) + "/" + mImgsInfo.size();

                            if (isPDF || isShowIndex) {
                                mImageToolbarNum.setText(str);
                            }
                            restartTimerOfTitlebar();
                            isShowTopBar = true;
                            showTitlebar();
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
            mGallery.setVisibility(View.GONE);
            placeHolderLayout.setVisibility(View.VISIBLE);
        }
        showTitlebar();
        param = new LQImageLoader.DIOptBuiderParam();
        param.mDefaultIcon = R.drawable.default_photo;
        param.mHighQuality = true;
        param.mIsCacheInMemory = false;
        param.mIsCacheOnDisc = true;
        param.mOutWidth = ScreenUtils.getScreenWidth(getApplicationContext());
        param.mOutHeight = ScreenUtils.getScreenHeight(getApplicationContext());
    }

    private void showPopwindow(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu, null);
        //处理popWindow 显示内容
        handleLogic(contentView);

        mPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)//显示的布局，还可以通过设置一个View
//                .size(260,80) //设置显示的大小，不设置就默认包裹内容
                .setFocusable(true)//是否获取焦点，默认为ture
                .setOutsideTouchable(true)//是否PopupWindow 以外触摸dissmiss
                .create()//创建PopupWindow
                .showAsDropDown(mIvMore, -DensityUtils.dp2px(getApplicationContext(), 100),
                        DensityUtils.dp2px(getApplicationContext(), 10));

    }


    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    protected void handleLogic(View contentView) {
        EntryBean entryBean = new EntryBean();
        final List<EntryBean> list = new ArrayList<>();
        if (isShowCollect) {
            entryBean.value = getString(R.string.collection);
            entryBean.id = KEY_COLLECT;
            list.add(entryBean);
        }
        if (isShowCourseAndReading) {
            entryBean = new EntryBean();
            entryBean.value = getString(R.string.make_pic_book);
            entryBean.id = KEY_DO_COURSE;
            list.add(entryBean);

            entryBean = new EntryBean();
            entryBean.value = getString(R.string.edit_course);
            entryBean.id = KEY_DO_READING;
            list.add(entryBean);
        }
        ListView listView = (ListView) contentView.findViewById(R.id.pop_menu_list);
        PopMenuAdapter adapter = new PopMenuAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mPopWindow != null) {
                    mPopWindow.dissmiss();
                }
                EntryBean bean = list.get(i);
                if (bean.id == KEY_COLLECT) {//收藏
                    handleCollectLogic();
                } else if (bean.id == KEY_DO_COURSE) {
                    // TODO: 2017/6/9  我要做课件
                    selectOrientation(bean.id);
                } else if (bean.id == KEY_DO_READING) {
                    // TODO: 2017/6/14 我要加点读
                    selectOrientation(bean.id);
                }
            }


        });

    }

    protected void selectOrientation(final int flag) {

    }

    /**
     * 点击收藏
     */
    protected void handleCollectLogic() {

    }

    protected ImageInfo getNewResourceInfoTag() {
        ImageInfo newResourceInfoTag;
        newResourceInfoTag = mImgsInfo.get(mIndex);
        if (newResourceInfoTag == null) {
            newResourceInfoTag = new ImageInfo();
            newResourceInfoTag.setTitle(mImgsInfo.get(0).getTitle());
            newResourceInfoTag.setResourceUrl(mImgsInfo.get(0).getResourceUrl());
        }
        return newResourceInfoTag;
    }


    protected List<String> getPathList() {
        List<String> list = new ArrayList<>();

        if (isPDF) {
            for (ImageInfo imageItemInfo : mImgsInfo) {
                list.add(imageItemInfo.getResourceUrl());
            }
        } else {
            list.add(mImgsInfo.get(mIndex).getResourceUrl());
        }


        return list;
    }


    private class TitlebarTimerTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    isShowTopBar = false;
                    showTitlebar();
                }
            });
        }
    }


    public static void newInstance(Context context, List<ImageInfo> mediaInfos, boolean isPDF, int index, boolean isHideMoreBtn, boolean isShowCourseAndReading) {
        newInstance(context, mediaInfos, isPDF, index, isHideMoreBtn, isShowCourseAndReading, true);
    }

    /**
     * @param context
     * @param mediaInfos
     * @param isPDF
     * @param index
     * @param isHideMoreBtn          true:屏蔽右上角更多按钮
     * @param isShowCourseAndReading 是否显示“我要做课件”，“我要加点读”
     * @param isShowCollect          true:显示收藏条目
     */
    public static void newInstance(Context context, List<ImageInfo> mediaInfos, boolean isPDF, int index, boolean isHideMoreBtn, boolean isShowCourseAndReading, boolean isShowCollect) {
        if (mediaInfos == null || mediaInfos.size() == 0) {
            return;
        }
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.putParcelableArrayListExtra(ImageBrowserActivity.EXTRA_IMAGE_INFOS, (ArrayList<? extends Parcelable>) mediaInfos);
        intent.putExtra(ImageBrowserActivity.EXTRA_CURRENT_INDEX, index);
        intent.putExtra(ImageBrowserActivity.ISPDF, isPDF);
        intent.putExtra(ImageBrowserActivity.KEY_ISHIDEMOREBTN, isHideMoreBtn);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOLLECT, isShowCollect);
        intent.putExtra(ImageBrowserActivity.KEY_ISSHOWCOURSEANDREADING, isShowCourseAndReading);
        context.startActivity(intent);
    }

    protected List<ImageInfo> getPreferencesData() {
        return null;
    }


    private class TouchImageAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            if (mImgsInfo == null){
                return 0;
            }
            return mImgsInfo.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            TouchImageView img = new TouchImageView(container.getContext());
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restartTimerOfTitlebar();
                    showTitlebar();
                }
            });

            String resourceUrl = mImgsInfo.get(position).getResourceUrl();
            if (isLoadLocalImage) {
                //加载本地图片
                if (!TextUtils.isEmpty(resourceUrl)) {
                    if (!resourceUrl.startsWith("http") && !resourceUrl.startsWith("file://")) {
                        resourceUrl = "file://" + resourceUrl;
                    }
                }
                ImageOptions imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_INSIDE,
                        R.drawable.default_photo, false, false, null);
                XImageLoader.loadImage(img, resourceUrl, imageOptions);
            } else {
                LQImageLoader.displayImage(resourceUrl, img, param);
//                ImageLoader.getInstance().displayImage(resourceUrl, img, options);
            }
            container.addView(img);
            return img;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImgsInfo != null) {
            mImgsInfo.clear();
            mImgsInfo = null;
        }
    }
}