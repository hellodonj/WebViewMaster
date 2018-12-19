package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.module.learn.ui.ExamDetailActivity;
import com.lqwawa.intleducation.module.learn.vo.ExamCommitVo;
import com.lqwawa.intleducation.module.learn.vo.ExamListVo;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class ExamCommitListAdapter extends ExamListBaseAdapter {
    private Activity activity;
    private List<ExamCommitVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;
    private ExamListVo examListVo;

    public ExamCommitListAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ExamCommitVo>();

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
        final ExamCommitVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_exam_commit_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (vo != null) {
            x.image().bind(holder.resIcon,
                    vo.getHeadPic().trim(), imageOptions);
            holder.resName.setText(vo.getCreateName());
            String time = DateUtils.getFormatByStringDate(vo.getCreateTime() + "",
                    DateUtils.YYYYMMDDHHMMSS);
            holder.resTime.setText(activity.getString(R.string.commit_time, time));
            if (!vo.isIsFinish()) {
                updateButtonState(holder.resAction, true);
                holder.resAction.setText(R.string.mooc_mark);
            } else {
                updateButtonState(holder.resAction, false);
                holder.resAction.setText(vo.getScore() + activity.getResources().getString(R.string.points));
                holder.resAction.setTextColor(activity.getResources().getColor(R.color.com_bg_sky_blue));
            }
            holder.resRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (vo != null && examListVo != null && examListVo.getCexam() != null)
                    ExamDetailActivity.start(activity, examListVo.getCexam().getId(), vo.getType(),
                            activity.getIntent().getStringExtra("memberId"), courseVo, String
                                    .valueOf(vo.getId()), vo.getCreateId());
                }
            });
        }
        return convertView;
    }


    private class ViewHolder {
        View resRoot;
        ImageView resIcon;
        TextView resName;
        TextView resTime;
        TextView resAction;

        public ViewHolder(View view) {
            resRoot = view.findViewById(R.id.res_root_ll);
            resIcon = (ImageView) view.findViewById(R.id.res_icon_iv);
            resName = (TextView) view.findViewById(R.id.res_name_tv);
            resTime = (TextView) view.findViewById(R.id.res_time_tv);
            resAction = (TextView) view.findViewById(R.id.res_action_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ExamCommitVo> list) {
        if (list != null) {
            this.list = new ArrayList<ExamCommitVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ExamCommitVo> list) {
        this.list.addAll(list);
    }

    public void setExamListVo(ExamListVo examListVo) {
        this.examListVo = examListVo;
    }
}
