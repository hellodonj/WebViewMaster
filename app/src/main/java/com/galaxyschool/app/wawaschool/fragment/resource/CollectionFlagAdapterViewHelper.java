package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ScreenUtils;
import com.galaxyschool.app.wawaschool.config.AppSettings;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.views.HalfRoundedImageView;

/**
 * 显示收藏标识
 * @param <T>
 */
public abstract class CollectionFlagAdapterViewHelper<T>
        extends AdapterViewHelper<T> {

    private Activity activity;
    private int maxColumn = 2;//列数
    private int minPadding = (int) (5 * MyApplication.getDensity()); //padding

    public CollectionFlagAdapterViewHelper(Activity activity,
                                           AdapterView adapterView,
                                           int maxColumn,
                                           int minPadding) {
        this(activity, adapterView, R.layout.item_common_lq_course,maxColumn,minPadding);
    }

    public CollectionFlagAdapterViewHelper(Activity activity,
                                           AdapterView adapterView,
                                           int itemViewLayout,
                                           int maxColumn,
                                           int minPadding) {
        super(activity, adapterView, itemViewLayout);
        this.activity = activity;
        if (maxColumn > 0) {
            this.maxColumn = maxColumn;
        }
        if (minPadding > 0) {
            this.minPadding = minPadding;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        NewResourceInfo data = (NewResourceInfo) getDataAdapter().getItem(position);
        if (data == null) {
            return view;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder();
        }
        holder.data = data;

        //设置布局参数，限制最大宽高。
        int itemSize = (ScreenUtils.getScreenWidth(activity) -
                minPadding * (maxColumn + 1)) / maxColumn;
        FrameLayout frameLayout = (FrameLayout) view.findViewById(
                R.id.resource_frameLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout
                .getLayoutParams();
        if (frameLayout != null && params != null) {
            params.width = itemSize;
            params.height = params.width * 9 / 16;
            frameLayout.setLayoutParams(params);
        }

        //缩略图
        ImageView imageView = (HalfRoundedImageView) view.
                findViewById(R.id.resource_thumbnail);
        if (imageView != null) {
            MyApplication.getThumbnailManager(activity).
                    displayThumbnailWithDefault(AppSettings.getFileUrl(
                            data.getThumbnail()), imageView, R.drawable.default_cover);
        }

        //收藏标识
        imageView = (ImageView) view.findViewById(R.id.icon_collect);
        if (imageView != null){
            //默认隐藏
            imageView.setVisibility(View.GONE);
        }

        //标题
        TextView textView = (TextView) view.findViewById(R.id.resource_title);
        if (textView != null) {
            textView.setText(data.getTitle());
        }

        //选择状态
        imageView = (ImageView) view.findViewById(R.id.resource_selector);
        if (imageView != null){
            //默认隐藏
            imageView.setVisibility(View.GONE);
        }

        view.setTag(holder);
        return view;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            return;
        }
        NewResourceInfo data = (NewResourceInfo) holder.data;
    }
}
