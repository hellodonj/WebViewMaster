package com.lqwawa.mooc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.galaxyschool.app.wawaschool.BaseFragmentActivity;
import com.galaxyschool.app.wawaschool.R;
import com.lqwawa.intleducation.base.widgets.TopBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 描述: 图片查看
 * 作者|时间: djj on 2019/9/9 0009 下午 1:05
 */
public class ViewPagerImageActivity extends BaseFragmentActivity {

    private TopBar mTopBar;
    private ViewPager mViewPager;
    ImageView[] mImageViews;

    private ArrayList<String> listPhotos;
    private int selectPosition;
    private PagerAdapter adapter;

    public static void start(Activity activity,
                             ArrayList<String> urlList,
                            int position){
        Intent intent = new Intent(activity,ViewPagerImageActivity.class);
        Bundle args = new Bundle();
        args.putStringArrayList("url_list",urlList);
        args.putInt("position",position);
        intent.putExtras(args);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_layout);
        initView();
    }

    private void initView() {
        mTopBar = (TopBar) findViewById(R.id.top_bar);
        mTopBar.setBack(true);
        mViewPager = (ViewPager) findViewById(R.id.img_viewpager);
        Intent intent = getIntent();
        if (intent != null) {
            listPhotos = intent.getStringArrayListExtra("url_list");
            selectPosition = intent.getIntExtra("position", 0);
            mImageViews = new ImageView[listPhotos.size()];
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    selectPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


            adapter = new PagerAdapter() {
                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    //可以使用其他的ImageView 控件
                    ImageView tounChImageView = new ImageView(ViewPagerImageActivity.this);
                    mImageViews[position] = tounChImageView;
                    try {
                        ImageLoader.getInstance().displayImage(listPhotos.get(position), tounChImageView);
                    } catch (Exception e) {
                    }
                    container.addView(tounChImageView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    //单击返回
                    tounChImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    return tounChImageView;
                }

                @Override
                public int getCount() {
                    return listPhotos.size();
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(mImageViews[position]);
                }

                @Override
                public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == arg1;
                }
            };
            mViewPager.setAdapter(adapter);
            setDefaultItem(selectPosition);
        }
//        else {
//            ToastUtil.showS(this, "图片损坏哦");
//            return;
//        }
    }

    private void setDefaultItem(int selectPosition) {
        //我这里mViewpager是viewpager子类的实例。如果你是viewpager的实例，也可以这么干。
        try {
            Class c = Class.forName("android.support.v4.view.ViewPager");
            Field field =c.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(mViewPager, selectPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(selectPosition);
    }
}
