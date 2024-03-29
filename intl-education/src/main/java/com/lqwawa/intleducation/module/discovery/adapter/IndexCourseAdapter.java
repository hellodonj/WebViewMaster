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
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.tools.DensityUtils;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/8.
 * email:man0fchina@foxmail.com
 */

public class IndexCourseAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<CourseVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    private boolean needPadding = false;

    public IndexCourseAdapter(Activity activity, boolean needPadding) {
        this.activity = activity;
        this.needPadding = needPadding;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<CourseVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        p_width = p_width - DisplayUtil.dip2px(activity,48);
        img_width = p_width / 3;
        img_height = img_width * 297 / 210;

        imageOptions =  XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.default_cover_h, false, false, null, img_width, img_height);

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
        CourseVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_discovery_course_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.courseName.setText(vo.getName());
        holder.organName.setText(vo.getOrganName());
        holder.teacherName.setText(vo.getTeachersName());
        XImageLoader.loadImage(holder.courseImg,
                vo.getThumbnailUrl().trim(),
                imageOptions);
        holder.courseImg.setLayoutParams(new FrameLayout.LayoutParams(img_width, img_height));
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
    public void setData(List<CourseVo> list) {
        if (list != null) {
            this.list = new ArrayList<CourseVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<CourseVo> list) {
        this.list.addAll(list);
    }
}