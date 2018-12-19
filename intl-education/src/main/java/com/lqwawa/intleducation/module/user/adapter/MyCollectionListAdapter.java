package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.module.user.vo.MyCollectionVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class MyCollectionListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<MyCollectionVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    private static final int[] orderStatusResId = new int[]{
            R.string.order_status_0,
            R.string.order_status_1,
            R.string.order_status_2};

    public MyCollectionListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MyCollectionVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = 2 * p_width / 5;
        img_height = img_width * 10 / 16;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                //.setSize(img_width,img_height)
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
        MyCollectionVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_my_collection_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.course_date_tv.setText(DateUtils.getFormatByStringDate(vo.getCreateTime(),
                DateUtils.YYYYMMDDCH));
        holder.course_name.setText(vo.getName());
        holder.organ_name.setText(vo.getOrganName());
        holder.teacher_name.setText(vo.getTeachersName());
        /*holder.process_tv.setText(activity.getResources().getString(R.string.progress_on)
                + vo.getProgress()
                + vo.getChapterName() + ","
                + activity.getResources().getString(R.string.week_all)
                + vo.getWeekCount() +
                vo.getChapterName());*/
        holder.process_tv.setText(activity.getResources().getString(R.string.week_all)
                + vo.getWeekCount() +
                vo.getChapterName());

        x.image().bind(holder.course_iv,
                vo.getThumbnailUrl().trim(),
                imageOptions);
        holder.course_iv.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
        return convertView;
    }


    private class ViewHolder {
        ImageView course_iv;
        TextView course_name;
        TextView organ_name;
        TextView teacher_name;
        TextView process_tv;
        TextView course_date_tv;

        public ViewHolder(View parentView) {
            course_iv = (ImageView) parentView.findViewById(R.id.course_iv);
            course_name = (TextView) parentView.findViewById(R.id.course_name);
            organ_name = (TextView) parentView.findViewById(R.id.organ_name_tv);
            teacher_name = (TextView) parentView.findViewById(R.id.teacher_name_tv);
            process_tv = (TextView) parentView.findViewById(R.id.process_tv);
            course_date_tv = (TextView) parentView.findViewById(R.id.course_date_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<MyCollectionVo> list) {
        if (list != null) {
            this.list = new ArrayList<MyCollectionVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<MyCollectionVo> list) {
        this.list.addAll(list);
    }
}
