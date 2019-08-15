package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.module.learn.vo.ExamListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.XImageLoader;

import org.xutils.image.ImageOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 */

public class UnitExamListAdapter extends ExamListBaseAdapter{
    private Activity activity;
    private List<ExamListVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    private boolean canEdit = false;

    public UnitExamListAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);

        int p_width = Math.min(activity.getWindowManager().getDefaultDisplay().getWidth()
                ,activity.getWindowManager().getDefaultDisplay().getHeight());
        list = new ArrayList<ExamListVo>();
        img_width = (p_width  - DisplayUtil.dip2px(activity, 16)) / 7;
        img_height = img_width;
        canEdit = activity.getIntent().getBooleanExtra("canEdit", false);
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
        final ExamListVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            convertView = inflater.inflate(R.layout.mod_discovery_course_res_list_item, null);
            holder = new ViewHolder(convertView);
            holder.resIconIv.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
            convertView.setTag(holder);
        }
        holder.arrowIv.setVisibility(View.GONE);
        updateActionButton(holder.actionTv, vo.getCexam().getType(), vo.getScore());
        if (vo.getCexam().getType() == 1 || vo.getCexam().getType() == 2){
            if (position == 0){
                holder.titleLay.setVisibility(View.VISIBLE);
                holder.titleNameTv.setText(activity.getResources().getString(R.string.exam_paper));
            }else{
                holder.titleLay.setVisibility(View.GONE);
            }
            if((canEdit || !TextUtils.equals(activity.getIntent().getStringExtra("memberId"),
                    UserHelper.getUserId())) && vo.getScore() != -2) {
                holder.resIconIv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.ic_exam_flag));
            }else{
                holder.resIconIv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.ic_exam_not_flag));
            }
            holder.resNameTv.setText(vo.getCexam().getPaperName());
            holder.itemRootLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //这里canEdit始终为true!!!!!!!!!!!!!!
//                    if((canEdit)
//                            || !TextUtils.equals(UserHelper.getUserId(),
//                            activity.getIntent().getStringExtra("memberId"))){
//                        ExamDetailActivity.start(activity, vo.getCexam().getId(), vo.getCexam().getType(),
//                                activity.getIntent().getStringExtra("memberId"), courseVo);
//                    }else {
//                        DoExamActivity.startForResult(activity,
//                                vo.getCexam().getPaperName(), vo.getCexam().getId(),
//                                vo.getCexam().getType(),
//                                (canEdit
//                                        && TextUtils.equals(activity.getIntent().getStringExtra("memberId"),
//                                        UserHelper.getUserId()))? 2 : 0,
//                                activity.getIntent().getStringExtra("memberId"));
//                    }
                    doExam(vo);
                }
            });
        }else if(vo.getCexam().getType() == 3 || vo.getCexam().getType() == 4){
            boolean showTitle = false;
            if (position == 0){
                showTitle = true;
            }else if(list.get(position - 1).getCexam().getType() != 3){
                showTitle = true;
            }
            if (showTitle){
                holder.titleLay.setVisibility(View.VISIBLE);
                holder.titleNameTv.setText(activity.getResources().getString(R.string.coursetask));
            }else{
                holder.titleLay.setVisibility(View.GONE);
            }
            if((canEdit || !TextUtils.equals(activity.getIntent().getStringExtra("memberId"),
                    UserHelper.getUserId())) && vo.getScore() != -2) {
                holder.resIconIv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.ic_task_read));
            }else{
                holder.resIconIv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.ic_task_not_flag));
            }
            holder.resNameTv.setText(vo.getCexam().getPaperName());
            holder.itemRootLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(canEdit && vo.getScore() == -2){
//                        TaskSliderHelper.doTask(activity, "" + vo.getCexam().getPaperId(),
//                                activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
//                                        .KEY_IS_FROM_MY_COURSE, false)
//                                        ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
//                        activity.getIntent().putExtra("taskPaperId", vo.getCexam().getId());
//                    }else{
//                        if(TaskSliderHelper.onTaskSliderListener != null) {
//                            TaskSliderHelper.onTaskSliderListener.viewCourse(activity,
//                                    vo.getCexam().getPaperId(), 18,
//                                    activity.getIntent().getStringExtra("schoolId"),
//                                    activity.getIntent().getBooleanExtra(MyCourseDetailsActivity
//                                            .KEY_IS_FROM_MY_COURSE, false)
//                                            ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
//                        }
//                    }
                    enterSectionTaskDetail(vo, courseDetailParams);
                }
            });
        }

        return convertView;
    }


    protected class ViewHolder {
        private LinearLayout titleLay;
        private TextView titleNameTv;
        private ImageView arrowIv;
        private LinearLayout itemRootLay;
        private ImageView resIconIv;
        private TextView resNameTv;
        private TextView actionTv;
        private View splitView;

        public ViewHolder(View view) {
            titleLay = (LinearLayout) view.findViewById(R.id.title_lay);
            titleNameTv = (TextView) view.findViewById(R.id.title_name_tv);
            arrowIv = (ImageView) view.findViewById(R.id.arrow_iv);
            splitView = (View) view.findViewById(R.id.split_view);
            itemRootLay = (LinearLayout) view.findViewById(R.id.item_root_lay);
            resIconIv = (ImageView) view.findViewById(R.id.res_icon_iv);
            resNameTv = (TextView) view.findViewById(R.id.res_name_tv);
            actionTv = (TextView) view.findViewById(R.id.res_action_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ExamListVo> list) {
        if (list != null) {
            this.list = new ArrayList<ExamListVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ExamListVo> list) {
        this.list.addAll(list);
    }

}
