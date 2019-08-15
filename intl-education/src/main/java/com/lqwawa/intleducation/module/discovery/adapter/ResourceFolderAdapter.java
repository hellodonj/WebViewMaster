package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.discovery.vo.ResourceFolderVo;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/16.
 * email:man0fchina@foxmail.com
 */

public class ResourceFolderAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<ResourceFolderVo> list;
    private LayoutInflater inflater;

    public ResourceFolderAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ResourceFolderVo>();
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
        ResourceFolderVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_teacher_resource_grid_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.resourceNameTv.setText(vo.name);
        holder.resourceCoverIv.setImageDrawable(activity.getResources().getDrawable(vo.icon));

        return convertView;
    }

    protected class ViewHolder {
        private LinearLayout resourceRoot;
        private ImageView resourceCoverIv;
        private TextView resourceNameTv;

        public ViewHolder(View view) {
            resourceRoot = (LinearLayout) view.findViewById(R.id.resource_root);
            resourceCoverIv = (ImageView) view.findViewById(R.id.resource_cover_iv);
            resourceNameTv = (TextView) view.findViewById(R.id.resource_name_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ResourceFolderVo> list) {
        if (list != null) {
            this.list = new ArrayList<ResourceFolderVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ResourceFolderVo> list) {
        this.list.addAll(list);
    }
}
