package com.galaxyschool.app.wawaschool.fragment.resource;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.MyApplication;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.common.ActivityUtils;
import com.galaxyschool.app.wawaschool.common.DateUtils;
import com.galaxyschool.app.wawaschool.fragment.library.AdapterViewHelper;
import com.galaxyschool.app.wawaschool.fragment.library.ViewHolder;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;

public abstract class NewResourceAdapterViewHelper<T> extends AdapterViewHelper<T> {

    private Activity activity;

    public NewResourceAdapterViewHelper(Activity activity,
                                        AdapterView adapterView) {
        this(activity, adapterView, R.layout.resource_item);
    }

    public NewResourceAdapterViewHelper(Activity activity,
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
            textView.setText(data.getTitle());
        }
        textView = (TextView) view.findViewById(R.id.resource_author);
        if (textView != null) {
            textView.setText(data.getAuthorName());
        }
        textView = (TextView) view.findViewById(R.id.resource_time);
        if (textView != null) {
//            if(data.getType()==NewResourceInfo.TYPE_SCHOOL_NEWS){
            textView.setText(DateUtils.getStringToString(data.getCreatedTime(),DateUtils.DATE_PATTERN_yyyy_MM_dd_HH_MM));
//            }else{
//                textView.setText(data.getUpdatedTime());
//            }
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.resource_thumbnail);
        if (imageView != null) {
            MyApplication.getThumbnailManager(this.activity).displayThumbnailWithDefault(
                    data.getThumbnail(), imageView, R.drawable.default_cover);
        }

        imageView = (ImageView) view.findViewById(R.id.resource_delete);
        if (imageView != null) {
            imageView.setTag(data);
        }
        textView = (TextView) view.findViewById(R.id.resource_read_count);
        if (textView != null) {
            textView.setText(activity.getString(R.string.n_read, data.getReadNumber()));
        }
        textView = (TextView) view.findViewById(R.id.resource_comment_count);
        if (textView != null) {
            textView.setText(activity.getString(R.string.n_comment, data.getCommentNumber()));
        }
        textView = (TextView) view.findViewById(R.id.resource_applaud_count);
        if (textView != null) {
            textView.setText(activity.getString(R.string.n_praise, data.getPointNumber()));
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
        if (data != null) {
            ActivityUtils.openOnlineNote(activity, data.getCourseInfo(), false, false);
        }
    }

}
