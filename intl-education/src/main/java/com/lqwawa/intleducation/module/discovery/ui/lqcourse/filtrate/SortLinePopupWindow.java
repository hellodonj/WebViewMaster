package com.lqwawa.intleducation.module.discovery.ui.lqcourse.filtrate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 显示筛选条件列表
 * @date 2018/05/03 20:49
 * @history v1.0
 * **********************************
 */
public class SortLinePopupWindow extends PopupWindow{

    private Activity mActivity;
    private List<Tab> mTabs;
    private PopupWindowListener mListener;
    private LayoutInflater mLayoutInflater;
    private boolean duckBottom;

    private View mRootView;
    private ListView mListView;
    private View mButtonView;
    private View mBackgroundView;

    public SortLinePopupWindow(@NonNull Activity activity,
                               @NonNull View buttonView,
                               @NonNull List<Tab> tabs,
                               @NonNull PopupWindowListener listener){
        this.mActivity = activity;
        this.mButtonView = buttonView;
        this.mTabs = tabs;
        this.mListener = listener;


        final Context context = UIUtil.getContext();
        mLayoutInflater = LayoutInflater.from(context);
        final View contentView = mRootView = mLayoutInflater.inflate(R.layout.widgets_line_pop_window,null);
        setContentView(contentView);
        // 设置宽高
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        mBackgroundView = (View) contentView.findViewById(R.id.pop_bottom_bg);
        mBackgroundView.setBackgroundColor(UIUtil.getColor(R.color.colorDarkHighAlpha));
        mBackgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(true);

        // 给筛选布局设置Adapter
        final ListAdapter adapter = new ListAdapter();
        //添加Item到网格中
        mListView.setAdapter(adapter);
        //添加点击事件
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SortLinePopupWindow.this.dismiss();
                Tab tab = mTabs.get(position);
                if(!EmptyUtil.isEmpty(mListener)){
                    mListener.onItemClickListener(mButtonView,position,tab);
                }
            }
        });
        setBackgroundDrawable(new ColorDrawable(UIUtil.getColor(android.R.color.transparent)));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(mActivity, 1.0f);
                if(!EmptyUtil.isEmpty(mListener)){
                    mListener.onDismissListener();
                }
            }
        });

    }

    /**
     * 显示popupWindow
     * @param parent
     */
    public void showPopupWindow(View parent, boolean duckBottom) {
        if (!this.isShowing()) {
            // 以下拉方式显示popup
            this.duckBottom = duckBottom;
            this.mBackgroundView.setVisibility(duckBottom ? View.VISIBLE : View.GONE);
            darkenBackground(mActivity, 0.6f);
            this.showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        // @date   :2018/5/4 0004 下午 6:18
        // @func   :不知道原来的人为什么这样写
        /*if(Build.VERSION.SDK_INT >= 24){
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
        }*/
        Rect visibleFrame = new Rect();
        anchor.getGlobalVisibleRect(visibleFrame);
        int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
        setHeight(height);
        super.showAtLocation(anchor, Gravity.BOTTOM,0,0);
    }

    /**
     * 控制弹出popupView屏幕变暗与恢复
     *
     * @param bgColor
     */
    private void darkenBackground(Activity activity, Float bgColor) {
        if (!duckBottom) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgColor;
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            activity.getWindow().setAttributes(lp);
        }
    }

    private class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Object getItem(int position) {
            return mTabs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Tab tab = mTabs.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.com_sort_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.contentTv.setText(tab.getConfigValue());
            if (tab.isSelected()) {
                holder.contentTv.setTextColor(UIUtil.getColor(R.color.textAccent));
                holder.selFlagIv.setVisibility(View.VISIBLE);
            } else {
                holder.contentTv.setTextColor(UIUtil.getColor(R.color.textSecond));
                holder.selFlagIv.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        protected class ViewHolder {
            private ImageView selFlagIv;
            private TextView contentTv;

            public ViewHolder(View view) {
                selFlagIv = (ImageView) view.findViewById(R.id.sel_flag_iv);
                contentTv = (TextView) view.findViewById(R.id.content_tv);
            }
        }
    }

    public interface PopupWindowListener {
        void onItemClickListener(View buttonView, int position, Tab tab);

        void onDismissListener();
    }
}
