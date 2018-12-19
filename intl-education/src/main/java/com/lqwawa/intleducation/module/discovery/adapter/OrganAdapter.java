package com.lqwawa.intleducation.module.discovery.adapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/8.
 * email:man0fchina@foxmail.com
 */

public class OrganAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<OrganVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;

    public OrganAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<OrganVo>();

        int p_width  = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width/8;
        img_height = img_width;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCircular(true)
                .setCrop(false)
                .setSize(img_width,img_height)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();
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
        final ViewHolder holder;
        final OrganVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_discovery_organ_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.organName.setText(vo.getName());

        holder.organImg.setLayoutParams(
                new LinearLayout.LayoutParams(img_width,img_height));
        x.image().bind(holder.organImg,
                vo.getThumbnail().trim(),
                imageOptions);
        return convertView;
    }


    protected class ViewHolder {
        private LinearLayout organRoot;
        private ImageView organImg;
        private TextView organName;

        public ViewHolder(View view) {
            organRoot = (LinearLayout) view.findViewById(R.id.organ_root);
            organImg = (ImageView) view.findViewById(R.id.organ_img);
            organName = (TextView) view.findViewById(R.id.organ_name);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<OrganVo> list) {
        if (list != null) {
            this.list = new ArrayList<OrganVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<OrganVo> list) {
        this.list.addAll(list);
    }
}
