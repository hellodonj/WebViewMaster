package com.galaxyschool.app.wawaschool.views.categoryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.views.MyGridView;

import java.util.List;


/**
 * Created by Administrator on 2016/8/2.
 */
class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> categorys;

    CategoryAdapter(Context context, List<Category> categorys) {
        this.context = context;
        this.categorys = categorys;
    }

    public final class ViewHolder {
        public ImageView slideView;
        public TextView valueView;
        public LinearLayout slideLayout;
        public MyGridView gridView;
    }

    @Override
    public int getCount() {
        return categorys != null ? categorys.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return categorys != null ? categorys.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_expand_gridview_group_popwindow, null);
            holder.valueView = (TextView) convertView.findViewById(R.id.value_view);
            holder.slideView = (ImageView) convertView.findViewById(R.id.slide_view);
            holder.slideLayout = (LinearLayout) convertView.findViewById(R.id.slide_layout);
            holder.gridView = (MyGridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Category item = (Category) getItem(position);
        holder.valueView.setText(item.getTypeName());
        CategoryValueAdapter valueAdapter = new CategoryValueAdapter(context, item);
        holder.gridView.setAdapter(valueAdapter);
        holder.gridView.setNumColumns(1);
        if (item.isExpanded()) {
            holder.slideView.setImageResource(R.drawable.arrow_up_ico);
            holder.gridView.setVisibility(View.VISIBLE);
        } else {
            holder.slideView.setImageResource(R.drawable.arrow_down_ico);
            holder.gridView.setVisibility(View.GONE);
        }
        holder.slideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setExpanded(!item.isExpanded());
                CategoryAdapter.this.notifyDataSetChanged();
            }
        });
        return convertView;
    }


    public List<Category> getCategorys() {
        return categorys;
    }

    public void setCategorys(List<Category> categorys) {
        this.categorys = categorys;
        this.notifyDataSetChanged();
    }
}
