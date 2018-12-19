package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.utils.StringUtils;
import com.lqwawa.intleducation.module.discovery.ui.coursedetail.CourseDetailParams;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.vo.ExamListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.utils.TipMsgHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by XChen on 2016/11/29.
 * email:man0fchina@foxmail.com
 */

public class ExamListAdapter extends ExamListBaseAdapter {
    private Activity activity;
    private List<ExamListVo> list;
    private LayoutInflater inflater;
    private ExamComparator comparator = new ExamComparator();
    private boolean canEdit = false;
    private boolean isOnlineTeacher;

    public ExamListAdapter(Activity activity,boolean isOnlineTeacher){
        this(activity);
        this.isOnlineTeacher = isOnlineTeacher;
    }


    public ExamListAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ExamListVo>();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final ExamListVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_learn_test_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        boolean isTeacher = UserHelper.checkCourseAuthor(courseVo,isOnlineTeacher);
        if(vo.isBuyed() || isTeacher){
            // 已经购买或者是老师身份 显示考试
            convertView.setVisibility(View.VISIBLE);
        }else{
            convertView.setVisibility(View.GONE);
        }
        // 1、考试，是否需要学习完所有章节,才能学习。 是
        // 2、考试Tab下,考试和测试的区分是什么？Cexam.WeekNum != 0? 考试的weekNum == 0
        // 3、家长查看孩子的课程，孩子已经全部购买章节，是否可以进入考试？ 孩子需要判断有无学习所有章节，家长不需要
        if(StringUtils.isValidString(vo.getCexam().getWeekNum())
                && !TextUtils.equals(vo.getCexam().getWeekNum(),"0")) {
            // @date   :2018/4/25 0025 上午 11:29
            // @func   :V5.5.41 将考试显示为章节标题名称
            holder.examTitleTv.setText(vo.getChapterName());
            /*holder.examTitleTv.setText(
                    StringUtils.getChapterNumString(activity, vo.getCexam().getChapterName(),
                            NumberTool.numberToChinese(vo.getCexam().getWeekNum()))*/
        }else{
            holder.examTitleTv.setText(activity.getResources().getString(R.string.exam));
        }

        if (vo.getCexam().getType() == 1 || vo.getCexam().getType() == 2){
            if(position == 0 ||
                    !TextUtils.equals(vo.getCexam().getWeekNum(),
                            list.get(position - 1).getCexam().getWeekNum())){
                holder.examTitleTv.setVisibility(View.VISIBLE);
                holder.chapterSegLine.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            }else{
                holder.examTitleTv.setVisibility(View.GONE);
                holder.chapterSegLine.setVisibility(View.GONE);
            }

            holder.examContentTv.setText(activity.getResources().getString(R.string.exam_paper) + "："
                    + vo.getCexam().getPaperName());
            holder.startIv.setText(activity.getResources().getString(R.string.into_homework));
        }else if(vo.getCexam().getType() == 3 || vo.getCexam().getType() == 4){
            if(position == 0 ||
                    !TextUtils.equals(vo.getCexam().getWeekNum(),
                            list.get(position - 1).getCexam().getWeekNum())){
                holder.examTitleTv.setVisibility(View.VISIBLE);
                holder.chapterSegLine.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            }else{
                holder.examTitleTv.setVisibility(View.GONE);
                holder.chapterSegLine.setVisibility(View.GONE);
            }
            holder.examContentTv.setText(activity.getResources().getString(R.string.coursetask) + "："
                    + vo.getCexam().getPaperName());
            holder.startIv.setText(activity.getResources().getString(R.string.into_task));
        }

        View.OnClickListener clickListtener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int role = UserHelper.getCourseAuthorRole(memberId, courseVo);
                if (role == UserHelper.MoocRoleType.STUDENT || role == UserHelper.MoocRoleType.PARENT) {

                    String weekNum = vo.getCexam().getWeekNum();
                    if (!TextUtils.isEmpty(weekNum) && weekNum.equals("0")) {
                        if (courseVo != null && courseVo.getProgressStatus() != 2) {
                            TipMsgHelper.ShowLMsg(activity, R.string.course_is_not_published);
                            return;
                        }
                    }

                    // 是否是考试 weekNum == 0 就是考试 不等于0就是测试
                    // boolean isExam = EmptyUtil.isEmpty(vo.getCexam()) && TextUtils.equals(vo.getCexam().getWeekNum(),"0");

                    if (vo.isExam() && vo.getChapterIsFinish() != 1) {
                        // 考试判断之前章节有没有学习完
                        TipMsgHelper.ShowLMsg(activity, R.string.exam_chapter_first);
                        return;
                    }
                }
                if(vo.getCexam().getType() == 1 || vo.getCexam().getType() == 2) {
//                    String memberId = activity.getIntent().getStringExtra("memberId");
//                    ExamDetailActivity.start(activity, vo.getCexam().getId(), vo.getCexam().getType(),
//                            memberId, courseVo, null);
                    doExam(vo);
                }else{
//                    if (canEdit && vo.getScore() == -2) {
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
            }
        };

//        String sortString = "";
//        if (vo.getScore() == -2){//未提交
//            holder.sortTv.setVisibility(View.GONE);
//            holder.startIv.setVisibility(View.VISIBLE);
//            holder.startIv.setOnClickListener(clickListtener);
//        }else if(vo.getScore() == -1){//未批阅
//            sortString = activity.getResources().getString(R.string.not_mark);
//            holder.sortTv.setText(sortString);
//            holder.sortTv.setVisibility(View.VISIBLE);
//            holder.sortTv.setTextColor(activity.getResources().getColor(R.color.com_text_red));
//            holder.startIv.setVisibility(View.GONE);
//            holder.sortTv.setOnClickListener(clickListtener);
//            holder.examContentTv.setOnClickListener(clickListtener);
//        }else{//成绩已公布
//            sortString = vo.getScore() + activity.getResources().getString(R.string.points);
//            holder.sortTv.setText(sortString);
//            holder.sortTv.setTextColor(activity.getResources().getColor(R.color.com_bg_sky_blue));
//            holder.sortTv.setVisibility(View.VISIBLE);
//            holder.startIv.setVisibility(View.GONE);
//            holder.sortTv.setOnClickListener(clickListtener);
//            holder.examContentTv.setOnClickListener(clickListtener);
//        }
        holder.examContentLayout.setOnClickListener(clickListtener);
        updateActionButton(holder.startIv, vo.getCexam().getType(), vo.getScore());
        return convertView;
    }


    protected class ViewHolder {
        private LinearLayout examContentLayout;
        private TextView examTitleTv;
        private TextView examContentTv;
        private TextView sortTv;
        private TextView startIv;
        private View chapterSegLine;

        public ViewHolder(View view) {
            examContentLayout = (LinearLayout) view.findViewById(R.id.exam_content_ll);
            examTitleTv = (TextView) view.findViewById(R.id.exam_title_tv);
            examContentTv = (TextView) view.findViewById(R.id.exam_content_tv);
            sortTv = (TextView) view.findViewById(R.id.sort_tv);
            startIv = (TextView) view.findViewById(R.id.start_iv);
            chapterSegLine = view.findViewById(R.id.exam_chapter_seg_line);
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
            Collections.sort(this.list, comparator);
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
        Collections.sort(this.list, comparator);
    }


    public class ExamComparator implements Comparator<ExamListVo> {
        public int compare(ExamListVo o1, ExamListVo o2) {
            int o1Week = StringUtils.getInt(o1.getCexam().getWeekNum());
            if (o1Week == 0){
                o1Week = Integer.MAX_VALUE;
            }
            int o2Week = StringUtils.getInt(o2.getCexam().getWeekNum());
            if (o2Week == 0){
                o2Week = Integer.MAX_VALUE;
            }
            if (o1Week >= o2Week){
                return 1;
            }else{
                return -1;
            }
        }

    }

    public void setCourseVo(CourseVo courseVo) {
        this.courseVo = courseVo;
    }

    public void setCourseDetailParams(CourseDetailParams params) {
        this.courseDetailParams = params;
        if (courseDetailParams != null && courseVo != null) {
            courseDetailParams.setBindSchoolId(courseVo.getBindSchoolId());
            courseDetailParams.setBindClassId(courseVo.getBindClassId());
        }
    }

    @Override
    protected boolean getOnlineTeacher() {
        return UserHelper.isCourseCounselor(courseVo,isOnlineTeacher);
    }
}
