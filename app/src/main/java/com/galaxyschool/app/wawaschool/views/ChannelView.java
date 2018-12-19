package com.galaxyschool.app.wawaschool.views;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.osastudio.common.utils.LQImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ChannelView extends LinearLayout implements ViewPager.OnPageChangeListener {

    public interface OnChannelClickListener {
        void onChannelClick(int index, ChannelItem channelItem);
    }

    private int pageSize = 4;

    private Context context;
    private ViewGroup layout;
    private WrapContentHeightViewPager viewPager;
    private MyPageAdapter pageAdapter;
    private RadioGroup radioGroup;
    private List<View> listViews = new ArrayList<View>();

    private int channelItemSize = 11;
    private List<ChannelItem> channelItems;
    private int itemWidth, imgSize;
    private OnChannelClickListener listener;
    private int itemLeftPadding,itemTopPadding,itemRightPadding,itemBottomPadding; //控制item内边距
    private int smallCirclePointDrawable = 0; //小圆点的背景
    private boolean imageSizeWrapContent = false; // 设置图片是否包裹内容显示。

    public ChannelView(Context context) {
        super(context);
    }

    public ChannelView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (ViewGroup) inflater.inflate(R.layout.channel_view, this, true);
        int screenWidth = ScreenUtils.getScreenWidth(context);
        itemWidth = screenWidth / pageSize;
        imgSize = (int)getResources().getDimension(R.dimen.channel_view_item_size);

        initViews();
    }

    private void initViews() {
        viewPager = (WrapContentHeightViewPager) layout.findViewById(R.id.view_pager);
        radioGroup = (RadioGroup) layout.findViewById(R.id.radio_group);
        pageAdapter = new MyPageAdapter();
        viewPager.setAdapter(pageAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    public void setOnChannelClickListener(OnChannelClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<ChannelItem> channelItems) {
        radioGroup.removeAllViews();
        listViews.clear();
        this.channelItems  = channelItems;
        if(channelItems != null && channelItems.size() > 0) {
            channelItemSize = channelItems.size();
            if (channelItemSize > 0) {
                int pageSize = channelItemSize / this.pageSize;
                if(channelItemSize % this.pageSize != 0) {
                    pageSize = pageSize + 1;
                }
                for (int i = 0; i < pageSize; i++) {
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(HORIZONTAL);
                    layout.setGravity(Gravity.CENTER_VERTICAL);
                    for (int j = i * this.pageSize; j < channelItemSize; j++) {
                        if (j >= (i + 1) * this.pageSize) {
                            break;
                        }
                        ImageTextButton imageTextButton = new ImageTextButton(context);
                        //设置padding
                        imageTextButton.setPadding(itemLeftPadding,itemTopPadding,itemRightPadding,
                                itemBottomPadding);
                        imageTextButton.setLayoutParams(
                            new LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        imageTextButton.setText(channelItems.get(j).channelTitle);
                        imageTextButton.setTextColor(Color.BLACK);
                        imageTextButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        imageTextButton.setTag(j);
                        imageTextButton.setOnClickListener(itemClickListener);
                        //图片包裹内容显示
                        if (imageSizeWrapContent){
                            imageTextButton.getImageView().setLayoutParams(
                                    new LayoutParams(LayoutParams.WRAP_CONTENT,
                                            LayoutParams.WRAP_CONTENT));
                        }else {
                            //指定大小显示
                            imageTextButton.getImageView().setLayoutParams(
                                    new LayoutParams(imgSize, imgSize));
                        }
                        imageTextButton.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
                        //网络资源
                        if (!TextUtils.isEmpty(channelItems.get(j).channelIconUrl)) {
                            LQImageLoader.displayImage(channelItems.get(j).channelIconUrl,
                                    imageTextButton.getImageView());
                        }else {
                            //用本地资源
                            imageTextButton.getImageView().setImageResource(
                                    channelItems.get(j).channelIcon);
                        }
                        layout.addView(imageTextButton);
                    }
                    listViews.add(layout);

                    final RadioButton dotButton = new RadioButton(context);
                    dotButton.setLayoutParams(
                        new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT));
                    dotButton.setGravity(Gravity.CENTER);
                    dotButton.setBackgroundDrawable(null);
                    dotButton.setPadding(12, 0, 12, 0);
                    //小圆点默认背景
                    if (smallCirclePointDrawable <= 0) {
                        dotButton.setButtonDrawable(R.drawable.btn_green_indicator_bg);
                    }else {
                        dotButton.setButtonDrawable(smallCirclePointDrawable);
                    }
                    radioGroup.addView(dotButton);
                }
                if(pageSize > 1) {
                    radioGroup.setVisibility(View.VISIBLE);
                    radioGroup.clearCheck();
                    ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
                } else {
                    radioGroup.setVisibility(View.GONE);
                }
                pageAdapter.notifyDataSetChanged();
                //重置页面
                viewPager.setCurrentItem(0);
            }
        }
    }

    public void setImageSizeWrapContent(boolean imageSizeWrapContent) {
        this.imageSizeWrapContent = imageSizeWrapContent;
    }

    public boolean isImageSizeWrapContent() {
        return imageSizeWrapContent;
    }

    /**
     * 设置条目图片的大小
     * @param imgSize
     */
    public void setImgSize(int imgSize) {
        if (imgSize > 0) {
            this.imgSize = imgSize;
        }
    }

    public int getImgSize() {
        return imgSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize >= 1) {
            this.pageSize = pageSize;
        }
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getItemLeftPadding() {
        return itemLeftPadding;
    }

    public void setItemLeftPadding(int itemLeftPadding) {
        this.itemLeftPadding = itemLeftPadding;
    }

    public int getItemTopPadding() {
        return itemTopPadding;
    }

    public void setItemTopPadding(int itemTopPadding) {
        this.itemTopPadding = itemTopPadding;
    }

    public int getItemRightPadding() {
        return itemRightPadding;
    }

    public void setItemRightPadding(int itemRightPadding) {
        this.itemRightPadding = itemRightPadding;
    }

    public int getItemBottomPadding() {
        return itemBottomPadding;
    }

    public void setItemBottomPadding(int itemBottomPadding) {
        this.itemBottomPadding = itemBottomPadding;
    }

    public void setSmallCirclePointDrawable(int smallCirclePointDrawable) {
        this.smallCirclePointDrawable = smallCirclePointDrawable;
    }

    public int getSmallCirclePointDrawable() {
        return smallCirclePointDrawable;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private class MyPageAdapter extends PagerAdapter {
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((WrapContentHeightViewPager) container).removeView((View)object);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((WrapContentHeightViewPager) container).addView(listViews.get(position));
            return listViews.get(position);
        }

        @Override
        public int getCount() {
            return listViews == null ? 0 : listViews.size();
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private OnClickListener itemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer)v.getTag();
            if(channelItems!= null && channelItems.size() > 0) {
                if(index < channelItems.size()) {
                    ChannelItem channelItem = channelItems.get(index);
                    if (listener != null) {
                        listener.onChannelClick(index, channelItem);
                    }
                }
            }
        }
    };

    public static class ChannelItem {
        public int channelId;
        public String channelTitle;
        public int channelIcon;
        public String channelIconUrl;
        public boolean haveItem;
    }

}
