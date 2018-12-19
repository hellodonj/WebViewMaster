package com.galaxyschool.app.wawaschool.views.categoryview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.galaxyschool.app.wawaschool.R;


/**
 * Created by Administrator on 2016/8/2.
 */
class CategoryValueAdapter extends BaseAdapter {
    private Context context;
    private Category category;
    private LayoutInflater inflater;
    CategoryValueAdapter(Context context , Category category){
        this.context=context;
        this.category=category;

        this.inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    public final class ViewHolder {
        public ImageView selectView;
        public TextView valueView;
    }

    @Override
    public int getCount() {
        return category != null && category.getDetailList() != null ? category
                .getDetailList().size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return category != null && category.getDetailList() != null ? category
                .getDetailList().get(position) : null;
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
            convertView = inflater.inflate(R.layout.item_listview_popwindow, null);
            holder.valueView = (TextView) convertView.findViewById(R.id.value_view);
            holder.selectView = (ImageView) convertView.findViewById(R.id.select_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
       final CategoryValue item = category != null && category.getDetailList() != null ?
                category.getDetailList().get(position) : null;
        holder.valueView.setText(item.getName());
        if (item.isTempSelect()) {
            holder.valueView.setTextColor(context.getResources().getColor(R.color.text_green));
            holder.selectView.setVisibility(View.VISIBLE);
        } else {
            holder.valueView.setTextColor(context.getResources().getColor(R.color.text_black));
            holder.selectView.setVisibility(View.INVISIBLE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category.getSelectMode()==Category.SELECT_MULTIPLIE_MODE){
                    item.setTempSelect(!item.isTempSelect());
                }else{
                    for(CategoryValue value : category.getDetailList()){
                        value.setTempSelect(false);
                    }
                    item.setTempSelect(!item.isTempSelect());
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        this.notifyDataSetChanged();
    }
}
