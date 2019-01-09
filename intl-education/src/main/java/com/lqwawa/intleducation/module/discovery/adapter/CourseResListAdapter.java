package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.RefreshUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.module.discovery.ui.CourseSelectItemFragment;
import com.lqwawa.intleducation.module.discovery.ui.lesson.select.ResourceSelectListener;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import org.xutils.image.ImageOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/1/4.
 * email:man0fchina@foxmail.com
 */

public class CourseResListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<SectionResListVo> list;
    private List<SectionResListVo> selectList = new ArrayList<>();
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    ImageOptions imageOptions;
    boolean needFlagRead;
    private int lessonStatus;
    private OnItemClickListener onItemClickListener = null;
    private ResourceSelectListener mSelectListener;
    private boolean isCourseSelect;
    private int selectCount = 0;
    private int maxSelect = 1;
    private int mTaskType = 9;

    private boolean isLQCourse;
    private boolean lessonDetail;

    public CourseResListAdapter(Activity activity, boolean needFlagRead,boolean lessonDetail){
        this(activity,needFlagRead);
        this.lessonDetail = lessonDetail;
    }

    public CourseResListAdapter(Activity activity, boolean needFlagRead) {
        this.activity = activity;
        this.needFlagRead = needFlagRead;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<SectionResListVo>();
        lessonStatus = activity.getIntent().getIntExtra("status", 0);
        int p_width = Math.min(activity.getWindowManager().getDefaultDisplay().getWidth()
                ,activity.getWindowManager().getDefaultDisplay().getHeight());

        img_width = (p_width  - DisplayUtil.dip2px(activity, 16)) / 7;
        img_height = img_width;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                //.setSize(img_width,img_height)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.onItemClickListener = listener;
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

    public void setNeedFlagRead(boolean needFlagRead){
        this.needFlagRead = needFlagRead;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position,View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final SectionResListVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        }else {
            // convertView = inflater.inflate(R.layout.mod_discovery_course_res_list_item, null);
            convertView = inflater.inflate(R.layout.item_course_lesson_detail_source_layout, null);
            holder = new ViewHolder(convertView);
            holder.resIconIv.setLayoutParams(new LinearLayout.LayoutParams(img_width, img_height));
            convertView.setTag(holder);
        }

        final View _convertView = convertView;
        if (vo.isIsShow()) {
            holder.itemRootLay.setVisibility(View.VISIBLE);
            int resType = vo.getResType();
            if(resType > 10000){
                resType -= 10000;
            }
            switch (resType) {
                case 1:
                    if(vo.isIsShield()){
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_pic_shield));
                    }else {
                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_pic_read));
                        } else if(needFlagRead && !vo.isIsRead() && lessonStatus == 1){
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_pic_new));
                        } else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_pic));
                        }
                    }
                    break;
                case 2:
                    if(vo.isIsShield()){
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_audio_shield));
                    }else {
                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_audio_read));
                        } else if(needFlagRead && !vo.isIsRead() && lessonStatus == 1){
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_audio_new));
                        }else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_audio));
                        }
                    }
                    break;
                case 3:
                    if(vo.isIsShield()){
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_video_shiled));
                    }else {
                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_video_read));
                        } else if(needFlagRead && !vo.isIsRead() && lessonStatus == 1){
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_video_new));
                        }else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_video));
                        }
                    }
                    break;
                case 6:
                    if(vo.isIsShield()){
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_pdf_shield));
                    }else {
                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_pdf_read));
                        } else if(needFlagRead && !vo.isIsRead() && lessonStatus == 1){
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_pdf_new));
                        }else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_pdf));
                        }
                    }
                    break;
                case 20:
                    if(vo.isIsShield()){
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_ppt_shield));
                    }else {
                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_ppt_read));
                        } else if(needFlagRead && !vo.isIsRead() && lessonStatus == 1){
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_ppt_new));
                        }else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_ppt));
                        }
                    }
                    break;
                case 24:
                    if(vo.isIsShield()){
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_word_shield));
                    }else {
                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_word_read));
                        } else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_word));
                        }
                    }
                    break;
                case 25:
                    if (vo.isIsShield()) {
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_txt_shield));
                    } else {

                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_txt_read));
                        } else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_txt));
                        }
                    }
                    break;
                case 5:
                case 16:
                case 17:
                case 18:
                case 19:
                    if (vo.isIsShield()) {
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_lqc_shield));
                    } else {
                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_lqc_read));
                        } else if(needFlagRead && !vo.isIsRead() && lessonStatus == 1){
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_lqc_new));
                        }else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_lqc));
                        }
                    }
                    break;
                case 23:
                    if(vo.isIsShield()){
                        holder.resIconIv.setImageDrawable(
                                activity.getResources().getDrawable(R.drawable.ic_task_shield));
                    } else {

                        if (needFlagRead && vo.isIsRead()) {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_task_read));
                        } else if(needFlagRead && !vo.isIsRead() && lessonStatus == 1){
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_task_new));
                        }else {
                            holder.resIconIv.setImageDrawable(
                                    activity.getResources().getDrawable(R.drawable.ic_task_not_flag));
                        }
                    }
                    break;
                default:
                    break;
            }


            if(needFlagRead && vo.isIsRead()){
                holder.mIvNeedCommit.setImageResource(R.drawable.ic_task_completed);
            }else{
                holder.mIvNeedCommit.setImageResource(R.drawable.ic_need_to_commit);
            }

            holder.resNameTv.setText(("" + vo.getName()).trim());
            holder.checkbox.setVisibility(isCourseSelect ? View.VISIBLE : View.GONE);
            holder.checkbox.setChecked(vo.isChecked());

            if(isCourseSelect){
                holder.checkbox.setOnClickListener(new ItemClickListener(position,_convertView,vo));
            }else{
                holder.itemRootLay.setOnClickListener(new ItemClickListener(position,_convertView,vo));
            }
        }else{
            holder.itemRootLay.setVisibility(View.GONE);
        }
        if (vo.isIsTitle()){
            if(lessonDetail){
                holder.titleLay.setVisibility(View.GONE);
            }else{
                holder.titleLay.setVisibility(View.VISIBLE);
            }
            if (list.get(position).isIsShow()){
                holder.arrowIv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.arrow_up_gray_ico));
                holder.splitView.setVisibility(View.GONE);
            }else{
                holder.arrowIv.setImageDrawable(
                        activity.getResources().getDrawable(R.drawable.arrow_down_gray_ico));
                holder.splitView.setVisibility(View.VISIBLE);
            }
            holder.titleLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isShow = !list.get(position).isIsShow();
                    list.get(position).setIsShow(isShow);
                    for(int i = position + 1; i < getCount(); i++){
                        if (list.get(i).isIsTitle()){
                            break;
                        }else {
                            list.get(i).setIsShow(isShow);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
            holder.titleNameTv.setText(vo.getTaskName());
        }else{
            holder.titleLay.setVisibility(View.GONE);
        }

        if(isCourseSelect /*|| lessonDetail*/){
            // 是课件选取,添加自动批阅
            // v5.10 LQ学程浏览的时候也要添加语音评测,还有一个直播课前课后的调用
            // v5.10 LQ学程浏览的时候不需要添加语音评测
            if((mTaskType == CourseSelectItemFragment.KEY_RELL_COURSE || vo.getTaskType() == 2) &&
                    SectionResListVo.EXTRAS_AUTO_READ_OVER.equals(vo.getResProperties())){
                // 只有听说课,才显示语音评测
                holder.mTvAutoMask.setVisibility(View.VISIBLE);
                holder.mTvAutoMask.setText(R.string.label_voice_evaluating);
                // 如果是语音评测类型，设置最大宽度
                holder.resNameTv.setMaxWidth(DisplayUtil.dip2px(UIUtil.getContext(),160));
            }else if((mTaskType == CourseSelectItemFragment.KEY_TASK_ORDER || vo.getTaskType() == 3) &&
                    EmptyUtil.isNotEmpty(vo.getPoint())){
                // 只有读写单,才显示自动批阅
                holder.mTvAutoMask.setVisibility(View.VISIBLE);
                // 自动批阅
                holder.mTvAutoMask.setText(R.string.label_auto_mark);
                // 如果是自动批阅类型，设置最大宽度
                holder.resNameTv.setMaxWidth(DisplayUtil.dip2px(UIUtil.getContext(),160));
            }else{
                // 如果不是自动批阅类型，取消设置最大宽度
                holder.resNameTv.setMaxWidth(Integer.MAX_VALUE);
                holder.mTvAutoMask.setVisibility(View.GONE);
            }
        }else{
            // 不是课件选取,隐藏自动批阅
            // 取消设置最大宽度
            holder.resNameTv.setMaxWidth(Integer.MAX_VALUE);
            holder.mTvAutoMask.setVisibility(View.GONE);
        }

        if(lessonDetail){
            int taskType = vo.getTaskType();
            if(taskType == 1){
                // 看课件
                holder.mIvNeedCommit.setVisibility(View.GONE);
            }else {
                // 听读课,读写单
                holder.mIvNeedCommit.setVisibility(View.VISIBLE);
            }
        }else{
            holder.mIvNeedCommit.setVisibility(View.GONE);
        }

        return  convertView;
    }

    class ItemClickListener implements View.OnClickListener {

        private int position;
        private View _convertView;
        private SectionResListVo vo;

        ItemClickListener(int position,View convertView,SectionResListVo vo){
            this.position = position;
            this._convertView = convertView;
            this.vo = vo;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null){
                onItemClickListener.onItemClick(position,_convertView);
            }
            //课件选取
            if (isCourseSelect) {
                if (maxSelect == 1) {//单选模式
                    if(EmptyUtil.isNotEmpty(mSelectListener)){
                        boolean onSelect = mSelectListener.onSelect(vo);
                        if(!onSelect){
                            vo.setChecked(!vo.isChecked());
                            notifyDataSetChanged();
                            if (vo.isChecked()) {
                                RefreshUtil.getInstance().addId(vo.getId());
                            } else {
                                RefreshUtil.getInstance().removeId(vo.getId());
                            }
                        }
                    }
                            /*for (int i = 0; i < list.size(); i++) {
                                SectionResListVo sectionResListVo = list.get(i);
                                if (sectionResListVo.isChecked() && !TextUtils.equals(vo.getId(),sectionResListVo.getId())) {
                                   sectionResListVo.setChecked(false);
                                    RefreshUtil.getInstance().removeId(sectionResListVo.getId());
                                }
                            }
                            vo.setChecked(!vo.isChecked());
                            notifyDataSetChanged();
                            if (vo.isChecked()) {
                                RefreshUtil.getInstance().addId(vo.getId());
                            } else {
                                RefreshUtil.getInstance().removeId(vo.getId());
                            }*/


                } else {//多选模式
                    if(EmptyUtil.isNotEmpty(mSelectListener)){
                        boolean onSelect = mSelectListener.onSelect(vo);
                        if(!onSelect){
                            vo.setChecked(!vo.isChecked());
                            notifyDataSetChanged();
                            if (vo.isChecked()) {
                                selectCount++;
                                RefreshUtil.getInstance().addId(vo.getId());
                            } else {
                                selectCount--;
                                RefreshUtil.getInstance().removeId(vo.getId());
                            }
                        }else{
                            ToastUtil.showToast(activity,activity.getString(R.string.str_select_count_tips,maxSelect));
                        }
                    }
                            /*if (selectCount < maxSelect || RefreshUtil.getInstance().contains(vo.getId())) {
                                vo.setChecked(!vo.isChecked());
                                notifyDataSetChanged();
                                if (vo.isChecked()) {
                                    selectCount++;
                                    RefreshUtil.getInstance().addId(vo.getId());
                                } else {
                                    selectCount--;
                                    RefreshUtil.getInstance().removeId(vo.getId());
                                }

                            } else {
                                ToastUtil.showToast(activity,activity.getString(R.string.str_select_count_tips,maxSelect));
                            }*/
                }

            }
        }
    }

/*    private boolean isContains(String id) {
        for (int i = 0; i < selectList.size(); i++) {
            if (TextUtils.equals(selectList.get(i).getId(), id)) {
                return true;
            }
        }
        return false;
    }*/

    protected class ViewHolder {
        private CheckBox checkbox;
        private LinearLayout titleLay;
        private TextView titleNameTv;
        private ImageView arrowIv;
        private LinearLayout itemRootLay;
        private ImageView resIconIv;
        private TextView resNameTv;
        private TextView mTvAutoMask;
        private View splitView;
        private ImageView mIvNeedCommit;

        public ViewHolder(View view) {
            titleLay = (LinearLayout) view.findViewById(R.id.title_lay);
            titleNameTv = (TextView) view.findViewById(R.id.title_name_tv);
            arrowIv = (ImageView) view.findViewById(R.id.arrow_iv);
            splitView = (View) view.findViewById(R.id.split_view);
            itemRootLay = (LinearLayout) view.findViewById(R.id.item_root_lay);
            resIconIv = (ImageView) view.findViewById(R.id.res_icon_iv);
            resNameTv = (TextView) view.findViewById(R.id.res_name_tv);
            checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            mIvNeedCommit = (ImageView) view.findViewById(R.id.iv_need_commit);
            mTvAutoMask = (TextView) view.findViewById(R.id.tv_auto_mark);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<SectionResListVo> list) {
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
    public void addData(List<SectionResListVo> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(list);
    }

    public interface OnItemClickListener{
        void onItemClick(int position, View convertView);
    }

    /**
     * 课件选取
     * @param b
     */
    public void setCourseSelect(boolean b,int tasktype) {
        isCourseSelect = b;
        mTaskType = tasktype;
        if (mTaskType == 9) {
            maxSelect = 9;
        } else {
            maxSelect = 1;
        }
    }

    public void setOnResourceSelectListener(@NonNull ResourceSelectListener listener){
        this.mSelectListener = listener;
    }

    /**
     * 设置多选条目
     * @param isLQCourse 是否是LQwawa调用，关联学程调用
     * @param multipleChoiceCount 条目个数
     * <p>需要在{@link CourseResListAdapter#setCourseSelect}后调用</p>
     */
    public void setMultipleChoiceCount(boolean isLQCourse,int multipleChoiceCount){
        this.isLQCourse = isLQCourse;
        maxSelect = multipleChoiceCount;
    }

    public List<SectionResListVo> getData(){
        return list;
    }

    public List<SectionResListVo> getSelectData() {
        selectList.clear();
        for (int i = 0; i < list.size(); i++) {
            SectionResListVo sectionResListVo = list.get(i);
            if (sectionResListVo.isChecked()) {
                selectList.add(sectionResListVo);
            }
        }
        return selectList;
    }
}
