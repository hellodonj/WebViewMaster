package com.galaxyschool.app.wawaschool.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.adapter.ResourcePlayListAdapter;
import com.galaxyschool.app.wawaschool.common.CallbackListener;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseData;

import java.util.List;

public class ResourcePlayListDialog extends Dialog {
    private Context mContext;
    private MyGridView gridView;
    private CallbackListener listener;
    private ResourcePlayListAdapter adapter;
    private List<CourseData> list;

    public ResourcePlayListDialog(Context context, List<CourseData> list, CallbackListener listener) {
        super(context, R.style.Theme_ContactsDialog);
        mContext = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_resource_play_list);
        initAdapter();
        setCancelable(false);
        resizeDialog(ResourcePlayListDialog.this, 0.8f);
    }

    private void initAdapter() {
        TextView cancelTextV = (TextView) findViewById(R.id.tv_cancel);
        if (cancelTextV != null){
            cancelTextV.setOnClickListener(v -> dismiss());
        }
        gridView = (MyGridView) findViewById(R.id.common_grid_view);
        if (gridView != null) {
            gridView.setNumColumns(1);
            adapter = new ResourcePlayListAdapter(mContext, list);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener((parent, view, position, id) -> {
                if (listener != null){
                    listener.onBack(position);
                }
            });
        }
    }

    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void resizeDialog(Dialog dialog, float ratio) {
        Window window = dialog.getWindow();
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        p.width = (int) (d.getWidth() * ratio);
        p.height = (int) (d.getHeight() * 0.5);
        window.setAttributes(p);
    }
}
