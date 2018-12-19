package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.learn.vo.MyCourseChapterVo;
import org.xutils.image.ImageOptions;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/14.
 * email:man0fchina@foxmail.com
 */

public class MyCourseChapterAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<MyCourseChapterVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    private static final int[] introductionStringResId = new int[]{
            R.string.introduction_str0,
            R.string.introduction_str1,
            R.string.introduction_str2};
    private static final int[] introductionLogoResId = new int[]{
            R.drawable.ic_introduction,
            R.drawable.ic_suit_obj,
            R.drawable.ic_study_goal};

    public MyCourseChapterAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MyCourseChapterVo>();

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
        ViewHolder holder = null;
        MyCourseChapterVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }
        if (convertView == null ||
                (convertView != null && holder.isChildren != vo.isIsChildren())) {
            convertView = inflater.inflate(vo.isIsChildren() ?
                    R.layout.mod_course_lesson_list_item
                    : R.layout.mod_course_chapter_list_item, null);
            holder = new ViewHolder(convertView);
            holder.isChildren = vo.isIsChildren();
            convertView.setTag(holder);
        }
        if (!vo.isIsChildren()) {
            holder.chapter_name_tv.setText(vo.getName());
            holder.chapter_title_tv.setText(vo.getCourseName());
            holder.chapter_spl_view.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        } else {
            holder.lesson_name_tv.setText(vo.getName());
        }

        return convertView;
    }


    private class ViewHolder {
        boolean isChildren;
        View chapter_spl_view;
        LinearLayout chapter_root_layout;
        TextView chapter_name_tv;
        TextView chapter_title_tv;
        TextView lesson_name_tv;

        public ViewHolder(View parent) {
            chapter_spl_view = parent.findViewById(R.id.chapter_spl_view);
            chapter_root_layout = (LinearLayout) parent.findViewById(R.id.chapter_root_layout);
            chapter_name_tv = (TextView) parent.findViewById(R.id.chapter_name_tv);
            chapter_title_tv = (TextView) parent.findViewById(R.id.chapter_title_tv);
            lesson_name_tv = (TextView) parent.findViewById(R.id.lesson_name_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<MyCourseChapterVo> list) {
        if (list != null) {
            if (this.list != null && this.list.size() > 0) {
                this.list.clear();
            } else if (this.list == null) {
                this.list = new ArrayList<MyCourseChapterVo>();
            }
            for (int i = 0; i < list.size(); i++) {
                MyCourseChapterVo vo = list.get(i);
                vo.setIsChildren(false);
                this.list.add(vo);
                if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                    for (int j = 0; j < vo.getChildren().size(); j++) {
                        MyCourseChapterVo lessonVo = vo.getChildren().get(j);
                        lessonVo.setIsChildren(true);
                        this.list.add(lessonVo);
                    }
                }
            }
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<MyCourseChapterVo> list) {
        if (this.list == null) {
            this.list = new ArrayList<MyCourseChapterVo>();
        }
        for (int i = 0; i < list.size(); i++) {
            MyCourseChapterVo vo = list.get(i);
            vo.setIsChildren(false);
            this.list.add(vo);
            if (vo.getChildren() != null && vo.getChildren().size() > 0) {
                for (int j = 0; j < vo.getChildren().size(); j++) {
                    MyCourseChapterVo lessonVo = vo.getChildren().get(j);
                    lessonVo.setIsChildren(true);
                    this.list.add(lessonVo);
                }
            }
        }
    }
}
