package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.learn.vo.ExamVo;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/2/27.
 * email:man0fchina@foxmail.com
 */

public class TaskChapterListAdapter extends BaseAdapter {
    private Activity activity;
    private List<ExamVo> list;
    private LayoutInflater inflater;
    private List<String> selectFlags;
    private List<String> hideFlags;
    private int img_width;
    private int img_height;
    private ImageOptions imageOptions;
    private SelectChangedListener selectChangedListener;

    public TaskChapterListAdapter(Activity activity, SelectChangedListener listener) {
        this.activity = activity;
        this.selectChangedListener = listener;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ExamVo>();
        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 4;
        img_height = img_width * 3 / 4;
        imageOptions = XImageLoader.buildImageOptions(ImageView.ScaleType.CENTER_CROP,
                R.drawable.img_def, false, false, null);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final ExamVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_task_chapter_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (neetShowCourse(position)) {
            holder.course_info_root_lay.setVisibility(View.VISIBLE);
            XImageLoader.loadImage(holder.course_iv,
                    vo.getCourseThumbnail().trim(),
                    imageOptions);
            holder.course_iv.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
            holder.course_name_tv.setText(vo.getCourseName());
        }
        holder.chapter_root_layout.setVisibility(
                hideFlags.get(position).equals("1") ? View.VISIBLE : View.GONE);
        holder.chapter_name_tv.setText(
                StringUtils.getChapterNumString(activity, vo.getChapterName(), "" + vo.getWeekNum())
                        + ": " + vo.getWeekName());
        holder.hide_lesson_iv.setImageDrawable(activity.getResources().getDrawable(
                hideFlags.get(position).equals("1") ? R.drawable.arrow_up_gray_ico : R.drawable.arrow_down_ico));
        holder.course_info_root_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = position; i < hideFlags.size(); i++){
                    if (i == position){
                        if (hideFlags.get(i).equals("1")) {
                            hideFlags.set(i, "0");
                        }else{
                            hideFlags.set(i, "1");
                        }
                    }else if(!neetShowCourse(i)){
                        if (hideFlags.get(i).equals("1")) {
                            hideFlags.set(i, "0");
                        }else{
                            hideFlags.set(i, "1");
                        }
                    }else{
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        });
        holder.select_rb.setChecked(this.selectFlags.get(position).equals("1"));
        holder.select_rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.select_rb.isChecked()){
                    select(position);
                    if (selectChangedListener != null){
                        selectChangedListener.onSelectOn(vo);
                    }
                }
            }
        });
        return convertView;
    }

    private void select(int index){
        clearSelect(false);
        selectFlags.set(index, "1");
        notifyDataSetChanged();
    }

    public void clearSelect(boolean needRefrash){
        for(int i = 0; i < selectFlags.size(); i++){
            selectFlags.set(i, "0");
        }
        if (needRefrash){
            notifyDataSetChanged();
        }
    }

    private boolean neetShowCourse(int position) {
        if (position >= 0 && position < this.getCount()) {
            if (position == 0) {
                return true;
            } else if (this.list.get(position - 1).getCourseId()
                    == this.list.get(position).getCourseId()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }


    private class ViewHolder {
        LinearLayout course_info_root_lay;
        ImageView course_iv;
        TextView course_name_tv;
        ImageView hide_lesson_iv;
        LinearLayout chapter_root_layout;
        TextView chapter_name_tv;
        RadioButton select_rb;

        public ViewHolder(View parentView) {
            course_info_root_lay = (LinearLayout) parentView.findViewById(R.id.course_info_root_lay);
            course_iv = (ImageView) parentView.findViewById(R.id.course_iv);
            course_name_tv = (TextView) parentView.findViewById(R.id.course_name_tv);
            hide_lesson_iv = (ImageView) parentView.findViewById(R.id.hide_lesson_iv);
            chapter_root_layout = (LinearLayout) parentView.findViewById(R.id.chapter_root_layout);
            chapter_name_tv = (TextView) parentView.findViewById(R.id.chapter_name_tv);
            select_rb = (RadioButton) parentView.findViewById(R.id.select_rb);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ExamVo> list) {
        if (list != null) {
            this.list = new ArrayList<ExamVo>(list);
            selectFlags = new ArrayList<>();
            hideFlags = new ArrayList<>();
            for (int i = 0; i < this.list.size(); i++) {
                selectFlags.add("0");
                hideFlags.add("1");
            }
        } else {
            this.list.clear();
        }
    }

    public interface SelectChangedListener{
        public void onSelectOn(ExamVo vo);
    }
}
