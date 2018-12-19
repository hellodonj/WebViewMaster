package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.views.HalfRoundedImageView;
import com.galaxyschool.app.wawaschool.R;
public abstract class NewResourcePadAdapterViewHelper<T> extends AdapterViewHelper<T> {

    private Activity activity;
    private int count = 2;//item个数
    private int spacecount = 3;//间隔数

    public NewResourcePadAdapterViewHelper(Activity activity,
                                           AdapterView adapterView) {
        this(activity, adapterView, R.layout.resource_item_pad);
    }

    public NewResourcePadAdapterViewHelper(Activity activity,
                                           AdapterView adapterView, int itemViewLayout) {
        super(activity, adapterView, itemViewLayout);
        this.activity = activity;
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

        TextView textView = (TextView) view.findViewById(R.id.resource_title);
        if (textView != null) {
            textView.setSingleLine(false);
            textView.setPadding(5,5,5,5);
            textView.setTextSize(12);
            textView.setLines(2);
            textView.setText(data.getTitle());
        }
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.resource_frameLayout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        int windowWith = wm.getDefaultDisplay().getWidth();//屏幕宽度
        int itemWidth = (windowWith - activity.getResources().getDimensionPixelSize(R.dimen.separate_20dp) * spacecount) / count;
        params.width = itemWidth;
        params.height = params.width * 9 / 16;
        frameLayout.setLayoutParams(params);
        params = (LinearLayout.LayoutParams)textView.getLayoutParams();
        params.width = itemWidth;
        textView.setLayoutParams(params);
        ImageView imageView = (HalfRoundedImageView) view.findViewById(R.id.resource_thumbnail);
        if (imageView != null) {
            MyApplication.getThumbnailManager(this.activity).displayThumbnailWithDefault(
                    data.getThumbnail(), imageView, R.drawable.default_cover);
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
