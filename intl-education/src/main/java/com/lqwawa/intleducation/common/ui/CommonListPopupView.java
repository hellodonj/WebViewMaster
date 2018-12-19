package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqwawa.intleducation.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by XChen on 2016/12/1.
 * email:man0fchina@foxmail.com
 */

public class CommonListPopupView extends PopupWindow implements PopupWindow.OnDismissListener,
        View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener{
    protected List<String> list;
    protected ListView listView;
    protected Activity parentActivity;
    protected View contentView;
    protected View topView;
    protected View bottomView;
    protected TextView textViewTitle;
    protected TextView textViewBottomButton;
    protected BlackType blackType = BlackType.BLACK_ALL;
    protected ListAdapter adapter;

    public enum BlackType {
        BLACK_ALL, BLACK_TOP, BLACK_BOTTOM
    }

    PopupWindowListener popupWindowListener;

    public CommonListPopupView(Activity activity,
                               List<String> data,
                               BlackType type,
                               PopupWindowListener listener) {
        this.parentActivity = activity;
        this.list = data;
        this.blackType = type;
        this.popupWindowListener = listener;

        LayoutInflater inflater = activity.getLayoutInflater();
        contentView = inflater.inflate(R.layout.widgets_common_list_pop_window, null);
        this.setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        listView = (ListView) contentView.findViewById(R.id.listView);
        topView = (View) contentView.findViewById(R.id.pop_top_bg);
        bottomView = (View) contentView.findViewById(R.id.pop_bottom_bg);
        textViewTitle = (TextView) contentView.findViewById(R.id.pop_title_tv);
        textViewBottomButton = (TextView) contentView.findViewById(R.id.bottom_btn);

        if (blackType == BlackType.BLACK_ALL) {
            topView.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
        } else if (blackType == BlackType.BLACK_TOP) {
            topView.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.GONE);
        } else if (blackType == BlackType.BLACK_BOTTOM) {
            topView.setVisibility(View.GONE);
            bottomView.setVisibility(View.VISIBLE);
        }
        textViewBottomButton.setOnClickListener(this);
        topView.setOnClickListener(this);
        bottomView.setOnClickListener(this);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setTouchable(true);
        setTouchInterceptor(this);
        adapter = new ListAdapter(parentActivity);
        adapter.setData(list);
        //添加Item到网格中
        listView.setAdapter(adapter);
        //添加点击事件
        listView.setOnItemClickListener(this);
        adapter.setData(list);
        adapter.notifyDataSetChanged();
        setOnDismissListener(this);
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.pop_top_bg
            || view.getId()==R.id.pop_bottom_bg) {
            dismiss();
        }else if(view.getId()== R.id.bottom_btn) {
            if (this.popupWindowListener != null) {
                popupWindowListener.onBottomButtonClickListener();
            }
            this.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (this.popupWindowListener != null){
            this.popupWindowListener.onItemClickListener(adapter.getItem(arg2));
        }
        this.dismiss();
    }

    @Override
    public void onDismiss() {
        darkenBackground(parentActivity, 1.0f);
        if (this.popupWindowListener != null) {
            this.popupWindowListener.onDismissListener();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
        // 这里如果返回true的话，touch事件将被拦截
        // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
    }


    /**
     * 控制弹出popupView屏幕变暗与恢复
     *
     * @param bgcolor
     */
    protected void darkenBackground(Activity activity, Float bgcolor) {
        if (blackType == BlackType.BLACK_ALL) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgcolor;
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            activity.getWindow().setAttributes(lp);
        }
    }

    protected class ListAdapter extends BaseAdapter {
        protected List<String> list;
        Activity parentActivity;

        public ListAdapter(Activity activity) {
            list = new ArrayList<String>();
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

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String name = list.get(position);
            if (convertView == null) {
                convertView = new TextView(parentActivity);
                ((TextView)convertView).setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        parentActivity.getResources().getDimension(R.dimen.com_font_size_5));
                ((TextView)convertView).setTextColor(
                        parentActivity.getResources().getColorStateList(R.color.com_select_text));
                convertView.setPadding(30, 40, 30, 40);
            }
            ((TextView) convertView).setText(name);
            return convertView;
        }

        /**
         * 下拉刷新设置数据
         *
         * @param list
         */
        public void setData(List<String> list) {
            if (list != null) {
                this.list = new ArrayList<String>(list);
            } else {
                this.list.clear();
            }
        }

        /**
         * 上拉加载更多，新增数据
         *
         * @param list
         */
        public void addData(List<String> list) {
            this.list.addAll(list);
        }
    }

    public interface PopupWindowListener {
        public void onItemClickListener(Object object);
        public void onBottomButtonClickListener();
        public void onDismissListener();
    }
}