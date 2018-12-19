package com.oosic.apps.share;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SharePopupView extends PopupWindow implements OnTouchListener {

    private LayoutInflater layoutInflater;
    private Context context;
    private View rootView;
    private GridView gridView;
    private RelativeLayout cancelButton;
    private ShareAdapter shareAdapter;
    private List<ShareItem> shareItems;
    private SimpleAdapter saImageItems;
    private int[] image = {
            R.drawable.umeng_socialize_wechat,
            R.drawable.umeng_socialize_wxcircle,
            R.drawable.umeng_socialize_qq_on,
            R.drawable.umeng_socialize_qzone_on
//            ,
//            R.drawable.umeng_socialize_wawachat_on
    };
    private int[] name = {
            R.string.wechat_friends,
            R.string.wxcircle,
            R.string.qq_friends,
            R.string.qzone
//            ,
//            R.string.wawachat
    };

    public SharePopupView(Context context) {
        super(context);
        this.context = context;

        layoutInflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        initViews();
    }


    private void initViews() {
        rootView = layoutInflater.inflate(R.layout.share_popup_view, null);
        gridView = (GridView) rootView.findViewById(R.id.share_gridView);
        cancelButton = (RelativeLayout) rootView.findViewById(R.id.share_cancel);

        rootView.setOnTouchListener(this);
        setContentView(rootView);
        setFocusable(true);
        setAnimationStyle(R.style.AnimBottom);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        int color = context.getResources().getColor(R.color.popup_root_view_bg);
        ColorDrawable colorDrawable = new ColorDrawable(color);
        setBackgroundDrawable(colorDrawable);

        cancelButton.setOnClickListener(
            new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

    }

    public void setIsNew(boolean isNew) {
        if(!isNew) {
            List<HashMap<String, Object>> shareList = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < image.length; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("ItemImage", image[i]);//添加图像资源的ID
                map.put("ItemText", context.getString(name[i]));//按序号做ItemText
                shareList.add(map);
            }

            saImageItems = new SimpleAdapter(
                context, shareList, R.layout.share_item, new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.imageView1, R.id.textView1});
            gridView.setAdapter(saImageItems);
        } else {
            shareAdapter = new ShareAdapter();
            gridView.setAdapter(shareAdapter);
        }
    }

    public void setShareItems(List<ShareItem> items) {
        this.shareItems = items;
        shareAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = rootView.findViewById(R.id.share_pop_layout).getTop();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (y < height) {
                dismiss();
            }
        }
        return true;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        gridView.setOnItemClickListener(listener);
    }

    private class ShareAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return shareItems == null ? 0 : shareItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(
                R.layout.share_item, null);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            ImageView imageView = (ImageView)view.findViewById(R.id.imageView1);

            textView.setText(shareItems.get(position).getTitleId());
            imageView.setImageResource(shareItems.get(position).getIconId());

            return view;
        }
    }
}
