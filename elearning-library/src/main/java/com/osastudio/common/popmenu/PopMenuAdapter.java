package com.osastudio.common.popmenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.apps.R;

import java.util.List;


/**
 * ================================================
 * 作    者：Blizzard-liu
 * 版    本：1.0
 * 创建日期：2017/6/3 9:35
 * 描    述：
 * 修订历史：
 * ================================================
 */

public class PopMenuAdapter extends BaseAdapter {
    private List<EntryBean> mList ;
    private Context mContext;
    private boolean titleLeftShow;
    public PopMenuAdapter(Context context, List<EntryBean> list) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.pop_menu_item, viewGroup,false);
            holder.title = (TextView) view.findViewById(R.id.pop_menu_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        int iconId = mList.get(i).iconId;
        if (iconId > 0) {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                 drawable = mContext.getDrawable(iconId);
            } else {
                drawable = mContext.getResources().getDrawable(iconId);
            }
            holder.title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            holder.title.setCompoundDrawablePadding(20);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.title.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
            holder.title.setLayoutParams(layoutParams);
            holder.title.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
            holder.title.setText(mList.get(i).value);
        } else {
            if (titleLeftShow){
                holder.title.setGravity(Gravity.START|Gravity.CENTER);
            }
            holder.title.setText(mList.get(i).value);
        }
        return view;
    }

    static class ViewHolder {
        public TextView title;
    }

    public void setTitleLeftShow(boolean isTitleLeftShow){
        this.titleLeftShow = isTitleLeftShow;
        this.notifyDataSetChanged();
    }
}
