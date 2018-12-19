package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.common.utils.DrawableUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.LQBasicsOuterEntity;
import com.lqwawa.intleducation.module.user.adapter.ClassMemberAdapter;
import com.lqwawa.intleducation.module.user.vo.ContactVo;

import org.xutils.x;

import java.util.List;

/**
 * @author mrmedici
 * @desc 新基础课程科目cell的adapter
 */
public class NewBasicsInnerAdapter extends BaseAdapter{

    private int mBgColor;


    private List<LQBasicsOuterEntity.LQBasicsInnerEntity> mInnerEntities;

    public NewBasicsInnerAdapter(List<LQBasicsOuterEntity.LQBasicsInnerEntity> innerEntities,int bgColor) {
        this.mInnerEntities = innerEntities;
        this.mBgColor = bgColor;
    }

    @Override
    public int getCount() {
        return mInnerEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return mInnerEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = UIUtil.inflate(R.layout.item_new_basics_inner_layout);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        LogUtil.e(NewBasicsOuterAdapter.class,"position:"+position);
        LQBasicsOuterEntity.LQBasicsInnerEntity entity = mInnerEntities.get(position);
        StringUtil.fillSafeTextView(holder.mTvContent,entity.getConfigValue());
        // 设置底色
        int colorPosition = position % 4;
        GradientDrawable drawable = DrawableUtil.createDrawable(mBgColor,mBgColor, DisplayUtil.dip2px(UIUtil.getContext(),4));
        holder.mTvContent.setBackground(drawable);

        return convertView;
    }

    private class ViewHolder {

        TextView mTvContent;

        public ViewHolder(View parentView) {
            mTvContent = (TextView) parentView.findViewById(R.id.tv_content);
        }
    }

    /**
     * 重新设置数据源
     * @param entities 数据源
     */
    public void setData(@NonNull List<LQBasicsOuterEntity.LQBasicsInnerEntity> entities){
        this.mInnerEntities = entities;
        notifyDataSetChanged();
    }
}
