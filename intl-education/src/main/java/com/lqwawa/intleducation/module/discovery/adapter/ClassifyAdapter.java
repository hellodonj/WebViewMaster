package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.ResourceUtils;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.discovery.vo.ClassifyVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/8.
 * email:man0fchina@foxmail.com
 */

public class ClassifyAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<ClassifyVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;

    public ClassifyAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<>();

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setConfig(Bitmap.Config.ARGB_8888)
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
        ViewHolder holder;
        ClassifyVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_discovery_classify_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.classifyName.setText("" + vo.getConfigValue());
        if (vo.getType() == 1) {
            holder.classifyImg.setImageDrawable(
                    activity.getResources().getDrawable(R.drawable.all_classify));
        } else {
            String thumbnail = vo.getThumbnail();
            if (StringUtils.isValidString(thumbnail)) {
                if (StringUtils.isValidWebResString(thumbnail)) {
                    x.image().bind(holder.classifyImg,
                            vo.getThumbnail().trim(),
                            imageOptions);
                } else {
                    try {
                        holder.classifyImg.setImageDrawable(
                                activity.getResources().getDrawable(
                                        ResourceUtils.getDrawableId(activity, thumbnail)));
                    } catch (Exception e) {

                    }
                }
            }
        }
        holder.classifyRoot.setBackgroundColor(AppConfig.BaseConfig.ClassifybackColors[position % 6]);
        return convertView;
    }


    private class ViewHolder {
        LinearLayout classifyRoot;
        ImageView classifyImg;
        TextView classifyName;

        public ViewHolder(View parent) {
            classifyRoot = (LinearLayout) parent.findViewById(R.id.classify_root);
            classifyImg = (ImageView) parent.findViewById(R.id.classify_img);
            classifyName = (TextView) parent.findViewById(R.id.classify_name);
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

}
