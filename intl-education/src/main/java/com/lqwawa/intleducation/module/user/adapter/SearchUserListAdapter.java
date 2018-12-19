package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.user.ui.SendFriendRequestActivity;
import com.lqwawa.intleducation.module.user.vo.SearchUserVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class SearchUserListAdapter extends MyBaseAdapter{
    private Activity activity;
    private List<SearchUserVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;

    public SearchUserListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<SearchUserVo>();

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setRadius(16)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.contact_head_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.contact_head_def)//加载失败后默认显示图片
                .build();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SearchUserVo vo = list.get(position);
        final long id = vo.getId();
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_search_user_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        x.image().bind(holder.user_head_iv, ("" + vo.getThumbnail()).trim(), imageOptions);
        holder.name_tv.setText("" + vo.getName());
        if (!vo.isFriend()){
            holder.add_friend_tv.setVisibility(View.VISIBLE);
            holder.has_friend_tv.setVisibility(View.GONE);
            holder.add_friend_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendFriendRequestActivity.startForResult(activity, id + "");
                }
            });
        }else{
            holder.add_friend_tv.setVisibility(View.GONE);
            holder.has_friend_tv.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView user_head_iv;
        TextView name_tv;
        TextView add_friend_tv;
        TextView has_friend_tv;
        public ViewHolder(View parentView) {
            user_head_iv = (ImageView)parentView.findViewById(R.id.user_head_iv);
            name_tv = (TextView)parentView.findViewById(R.id.name_tv);
            add_friend_tv = (TextView)parentView.findViewById(R.id.add_friend_tv);
            has_friend_tv = (TextView)parentView.findViewById(R.id.has_friend_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<SearchUserVo> list) {
        if (list != null) {
            this.list = new ArrayList<SearchUserVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<SearchUserVo> list) {
        this.list.addAll(list);
    }
}
