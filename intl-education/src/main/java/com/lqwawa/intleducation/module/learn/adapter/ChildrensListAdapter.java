package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/3/24.
 * email:man0fchina@foxmail.com
 */

public class ChildrensListAdapter extends BaseAdapter {
    private List<ChildrenListVo> list;
    Activity parentActivity;
    private int selectIndex = 0;
    private OnSelectItemChangeListener onSelectItemChangeListener;
    private boolean isLive = false;

    public ChildrensListAdapter(Activity activity, OnSelectItemChangeListener listener) {
        list = new ArrayList<ChildrenListVo>();
        this.onSelectItemChangeListener = listener;
        parentActivity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public ChildrenListVo getSelect() {
        if (list != null && selectIndex >= 0 && selectIndex < list.size()) {
            return list.get(selectIndex);
        } else {
            return null;
        }
    }

    public void setSelect(int select) {
        if (this.list != null) {
            if(select != selectIndex) {
                selectIndex = select;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ChildrenListVo vo = list.get(position);
        if (convertView == null) {
            convertView = parentActivity.getLayoutInflater()
                    .inflate(R.layout.mod_common_filter_text, null, false);
            TextView textViewItem = (TextView) convertView.findViewById(R.id.item_tv);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0,0,0,0);
            textViewItem.setGravity(Gravity.CENTER);
            textViewItem.setLayoutParams(lp);
            textViewItem.setMaxLines(1);
            textViewItem.setEllipsize(TextUtils.TruncateAt.END);
        }
        TextView textViewItem = (TextView) convertView.findViewById(R.id.item_tv);
        if (position == selectIndex) {
            textViewItem.setTextColor(parentActivity.getResources().getColor(R.color.com_text_black));
            convertView.setBackgroundResource(R.drawable.com_green_line_white_bg);
            textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    parentActivity.getResources().getDimension(R.dimen.com_font_size_7));
        } else {
            textViewItem.setTextColor(parentActivity.getResources().getColor(R.color.com_text_gray));
            convertView.setBackgroundColor(parentActivity.getResources().getColor(R.color.translation));
            textViewItem.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    parentActivity.getResources().getDimension(R.dimen.com_font_size_6));
        }
        if(StringUtils.isValidString(vo.getMemberId())) {
            textViewItem.setText("" + vo.getRealName() +
                    (TextUtils.equals(vo.getMemberId(), UserHelper.getUserId()) ?
                            "" : (isLive ?
                            parentActivity.getResources().getString(R.string.xx_joined_live) :
                            parentActivity.getResources().getString(R.string.xx_learning_process))));
        }else{
            textViewItem.setText("" + vo.getNickname());
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != selectIndex) {
                    selectIndex = position;
                    notifyDataSetChanged();
                    if (onSelectItemChangeListener != null) {
                        onSelectItemChangeListener.OnSelectItemChanged(vo, position);
                    }
                }
            }
        });
        return convertView;
    }

    public void setData(List<ChildrenListVo> list, boolean isLive) {
        this.isLive = isLive;
        if (list != null) {
            this.list = new ArrayList<>(list);
            if(!isLive) {
                ChildrenListVo vo = new ChildrenListVo();
                vo.setMemberId(UserHelper.getUserId());
                vo.setRealName(parentActivity.getResources().getString(R.string.my_learning_process));
                this.list.add(0, vo);
            }
        } else {
            this.list.clear();
        }
    }

    public void addData(List<ChildrenListVo> list) {
        if(this.list == null){
            this.list = new ArrayList<>();
        }
        if (list != null) {
            this.list.addAll(list);
        }
    }

    public interface OnSelectItemChangeListener {
        void OnSelectItemChanged(Object obj, int position);
    }

    public List<ChildrenListVo> getList() {
        return this.list;
    }

    public int getSelectIndex(){
        return selectIndex;
    }
}