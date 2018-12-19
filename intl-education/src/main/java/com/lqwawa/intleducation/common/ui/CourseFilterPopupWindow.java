package com.lqwawa.intleducation.common.ui;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.NoScrollGridView;
import com.lqwawa.intleducation.module.discovery.vo.FilterVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */
public class CourseFilterPopupWindow extends PopupWindow {
    private List<FilterVo> dataList;
    private List<FilterVo> segmentList;
    private List<FilterVo> ageList;
    private Activity parentActivity;
    private View conentView;
    private NoScrollGridView gridViewSegment;
    private NoScrollGridView gridViewAge;
    private Button btnAllReset;
    private Button btnConfirm;
    PopupWindowListener popupWindowListener;

    public CourseFilterPopupWindow(Activity activity,
                                   List<FilterVo> dataList,
                                   FilterVo segmentSelect,
                                   FilterVo ageSelect,
                                   PopupWindowListener listener) {
        this.parentActivity = activity;
        this.popupWindowListener = listener;
        this.dataList = dataList;
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widgets_course_filter_pop_window, null);
        this.setContentView(conentView);
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        gridViewSegment = (NoScrollGridView) conentView.findViewById(R.id.segment_grid_view);
        gridViewAge = (NoScrollGridView) conentView.findViewById(R.id.age_grid_view);
        btnAllReset = (Button)conentView.findViewById(R.id.all_reset_bt) ;
        btnConfirm = (Button)conentView.findViewById(R.id.confirm_bt) ;
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

        segmentList = new ArrayList<>();
        ageList = new ArrayList<>();
        int segmentSelectIndex = -1;
        int ageSelectIndex = -1;
        for (int i = 0; i < this.dataList.size(); i++) {
            if (this.dataList.get(i).getConfigType() == 4) {
                segmentList.add(this.dataList.get(i));
                if (segmentSelect!=null && segmentSelect.getId().equals(this.dataList.get(i).getId())){
                    segmentSelectIndex = segmentList.size() - 1;
                }
            } else if (this.dataList.get(i).getConfigType() == 5) {
                ageList.add(this.dataList.get(i));
                if (ageSelect!=null && ageSelect.getId().equals(this.dataList.get(i).getId())){
                    ageSelectIndex = ageList.size() - 1;
                }
            }
        }

        final FilterAdapter segmentAdapter = new FilterAdapter(parentActivity, segmentSelectIndex);
        segmentAdapter.setData(segmentList);
        //添加Item到网格中
        gridViewSegment.setAdapter(segmentAdapter);

        final FilterAdapter ageAdapter = new FilterAdapter(parentActivity, ageSelectIndex);
        ageAdapter.setData(ageList);
        //添加Item到网格中
        gridViewAge.setAdapter(ageAdapter);

        btnAllReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindowListener != null){
                    segmentAdapter.changeSelect(-1);
                    ageAdapter.changeSelect(-1);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindowListener != null){
                    popupWindowListener.onConfirm(
                            segmentAdapter.getSelectItem(),
                            ageAdapter.getSelectItem());
                    dismiss();
                }
            }
        });

        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(parentActivity, 1.0f);
                CourseFilterPopupWindow.this.popupWindowListener.onDismissListener();
            }
        });
    }


    /**
     * 控制弹出popupView屏幕变暗与恢复
     *
     * @param bgcolor
     */
    private void darkenBackground(Activity activity, Float bgcolor) {
        /*WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgcolor;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
        */
    }


    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent);
            darkenBackground(parentActivity, 0.6f);
        } else {
            this.dismiss();
        }
    }

    private class FilterAdapter extends BaseAdapter {
        private List<FilterVo> list;
        Activity parentActivity;
        private int selectIndex = -1;


        public FilterAdapter(Activity activity, int selectIndex) {
            list = new ArrayList<FilterVo>();
            this.selectIndex = selectIndex;
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

        public void changeSelect(int select){
            this.selectIndex = select;
            notifyDataSetChanged();
        }

        public Object getSelectItem(){
            if (selectIndex >=0 && selectIndex < list.size()){
                return getItem(selectIndex);
            }else{
                return null;
            }
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            FilterVo vo = list.get(position);
            if (convertView == null) {
                convertView = parentActivity.getLayoutInflater()
                        .inflate(R.layout.mod_common_filter_text, null, false);
            }
            TextView textViewItem = (TextView)convertView.findViewById(R.id.item_tv);
            if (position == selectIndex){
                textViewItem.setTextColor(parentActivity.getResources().getColor(R.color.com_text_white));
                textViewItem.setBackgroundResource(R.drawable.com_green_radio_bt_normal);
            }else{
                textViewItem.setTextColor(parentActivity.getResources().getColor(R.color.com_text_dark_gray));
                textViewItem.setBackgroundResource(R.drawable.com_white_radio_bt_normal);
            }
            textViewItem.setText(vo.getConfigValue());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != selectIndex){
                        selectIndex = position;
                    }else{
                        selectIndex = -1;
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        /**
         * 下拉刷新设置数据
         *
         * @param list
         */
        public void setData(List<FilterVo> list) {
            if (list != null) {
                this.list = new ArrayList<FilterVo>(list);
            } else {
                this.list.clear();
            }
        }

        /**
         * 上拉加载更多，新增数据
         *
         * @param list
         */
        public void addData(List<FilterVo> list) {
            this.list.addAll(list);
        }
    }

    public interface PopupWindowListener {
        public void onConfirm(Object segmentVo, Object ageVo);
        public void onAllReset();
        public void onDismissListener();
    }
}
