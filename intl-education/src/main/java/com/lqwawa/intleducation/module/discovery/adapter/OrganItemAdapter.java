package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.module.discovery.vo.OrganItemVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/10.
 * email:man0fchina@foxmail.com
 */

public class OrganItemAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<OrganItemVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    int type = 1;
    int layoutId;

    public OrganItemAdapter(Activity activity, int type) {
        this.type = type;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<OrganItemVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        switch (type) {
            case 1:
            default:
                layoutId = R.layout.mod_discovery_course_item;
                img_width = p_width / 2 - 20;
                img_height = img_width * 9 / 16;
                break;
            case 2:
                layoutId = R.layout.mod_discovery_teacher_item;
                img_width = p_width / 3 - 20;
                img_height = img_width;
                break;
            case 3:
                layoutId = R.layout.mod_discovery_teacher_item;
                img_width = p_width / 2 - 20;
                img_height = img_width * 3 / 4;
                break;
        }

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setSize(img_width, img_height)
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
        OrganItemVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(layoutId, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            int sp4dp = DisplayUtil.dip2px(activity, 4);
            holder.courseRoot.setPadding(sp4dp, sp4dp, sp4dp, sp4dp);
        }

        x.image().bind(holder.courseImg,
                (type == 1 ? vo.getThumbnailUrl() : vo.getThumbnail() + "").trim(),
                imageOptions);
        holder.courseImg.setLayoutParams(
                new FrameLayout.LayoutParams(img_width, img_height));

        switch (type) {
            default:
            case 1:
                holder.courseName.setText(vo.getName());
                holder.organName.setVisibility(View.GONE);
                holder.teacherName.setText(vo.getTeachersName());
                break;
            case 2:
                holder.organName.setVisibility(View.GONE);
                holder.teacherName.setText(vo.getName());
                break;
            case 3:
                holder.organName.setText(vo.getName());
                holder.teacherName.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }



    protected class ViewHolder {
        private LinearLayout itemRoot;
        private LinearLayout courseRoot;
        private ImageView courseImg;
        private TextView organName;
        private TextView courseName;
        private TextView teacherName;

        public ViewHolder(View view) {
            itemRoot = (LinearLayout) view.findViewById(R.id.item_root);
            courseRoot = (LinearLayout) view.findViewById(R.id.course_root);
            courseImg = (ImageView) view.findViewById(R.id.course_img);
            organName = (TextView) view.findViewById(R.id.organ_name);
            courseName = (TextView) view.findViewById(R.id.course_name);
            teacherName = (TextView) view.findViewById(R.id.teacher_name);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<OrganItemVo> list) {
        if (list != null) {
            this.list = new ArrayList<OrganItemVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<OrganItemVo> list) {
        this.list.addAll(list);
    }
}

