package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */
public class SortLinePopupWindow extends PopupWindow {
    private List<ClassifyVo> list;
    private Activity parentActivity;
    private View conentView;
    private ListView listView;
    private View bottomBg;
    private boolean duckBottom = false;
    PopupWindowListener popupWindowListener;
    private boolean needAddAll = false;

    public SortLinePopupWindow(Activity activity,
                               List<ClassifyVo> data,
                               String selectLevel,
                               PopupWindowListener listener,
                               boolean needAddAll) {
        this.parentActivity = activity;
        this.popupWindowListener = listener;
        this.list = data;
        this.needAddAll = needAddAll;
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widgets_line_pop_window, null);
        this.setContentView(conentView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        listView = (ListView) conentView.findViewById(R.id.listView);
        bottomBg = (View) conentView.findViewById(R.id.pop_bottom_bg);
        bottomBg.setBackgroundColor(parentActivity.getResources().getColor(R.color.com_bg_dark_trans_black));
        bottomBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortLinePopupWindow.this.dismiss();
            }
        });
        setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("mengdd", "onTouch : ");
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        final ListAdapter adapter = new ListAdapter(parentActivity, selectLevel);
        for (int i = 0; i < list.size(); i++) {
            adapter.addData(list.get(i));
        }
        //添加Item到网格中
        listView.setAdapter(adapter);
        //添加点击事件
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        ;
                        SortLinePopupWindow.this.popupWindowListener
                                .onItemClickListener(arg2, adapter.getItem(arg2));
                        SortLinePopupWindow.this.dismiss();
                    }
                }
        );
        adapter.notifyDataSetChanged();
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(parentActivity, 1.0f);
                SortLinePopupWindow.this.popupWindowListener.onDismissListener();
            }
        });

    }


    /**
     * 控制弹出popupView屏幕变暗与恢复
     *
     * @param bgcolor
     */
    private void darkenBackground(Activity activity, Float bgcolor) {
        if (!duckBottom) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgcolor;
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            activity.getWindow().setAttributes(lp);
        }
    }


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent, boolean duckBottom) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.duckBottom = duckBottom;
            bottomBg.setVisibility(duckBottom ? View.VISIBLE : View.GONE);
            this.showAsDropDown(parent);
            darkenBackground(parentActivity, 0.6f);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        if(Build.VERSION.SDK_INT >= 24){
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
        }

        super.showAsDropDown(anchor);
    }

    private class ListAdapter extends BaseAdapter {
        private List<ClassifyVo> list;
        Activity parentActivity;
        private String selectLevel;

        public ListAdapter(Activity activity, String selectLevel) {
            list = new ArrayList<>();
            this.selectLevel = selectLevel;
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
            ClassifyVo vo = list.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parentActivity).inflate(R.layout.com_sort_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.contentTv.setText((needAddAll && position == 0 ?
                    parentActivity.getResources().getString(R.string.all) : "")
                    + vo.getConfigValue());
            if (selectLevel.equals(vo.getLevel()) || selectLevel.equals("" + vo.getLabelId())) {
                holder.contentTv.setTextColor(
                        parentActivity.getResources().getColor(R.color.com_text_green));
                holder.selFlagIv.setVisibility(View.VISIBLE);
            } else {
                holder.contentTv.setTextColor(
                        parentActivity.getResources().getColor(R.color.com_text_gray));
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

        /**
         * 下拉刷新设置数据
         *
         * @param list
         */
        public void setData(List<ClassifyVo> list) {
            if (list != null) {
                this.list = new ArrayList<>(list);
            } else {
                this.list.clear();
            }
        }

        /**
         * 上拉加载更多，新增数据
         *
         * @param list
         */
        public void addData(List<ClassifyVo> list) {
            this.list.addAll(list);
        }

        public void addData(ClassifyVo classifyVo) {
            if (this.list == null) {
                this.list = new ArrayList<>();
            }
            if (classifyVo != null) {
                if (classifyVo.getLabelId() != 0) {
                    for (ClassifyVo vo : this.list) {
                        if (vo.getLabelId() == classifyVo.getLabelId()) {
                            return;
                        }
                    }
                }
                this.list.add(classifyVo);
            }
        }
    }

    public interface PopupWindowListener {
        void onItemClickListener(int position, Object object);

        void onDismissListener();
    }
}
