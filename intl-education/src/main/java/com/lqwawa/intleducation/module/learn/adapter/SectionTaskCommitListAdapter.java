package com.lqwawa.intleducation.module.learn.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.client.pojo.SourceFromType;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DateUtils;
import com.lqwawa.intleducation.common.CharacterParser;
import com.lqwawa.intleducation.module.learn.tool.TaskSliderHelper;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.learn.ui.SectionTaskDetailsActivity;
import com.lqwawa.intleducation.module.learn.vo.SectionTaskCommitListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class SectionTaskCommitListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<SectionTaskCommitListVo> list;
    private LayoutInflater inflater;
    private ImageOptions userHeadImageOptions;
    private int img_width;
    private int img_height;
    private ImageOptions coverImageOptions;
    private CharacterParser characterParser;

    public SectionTaskCommitListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<SectionTaskCommitListVo>();

        characterParser = CharacterParser.getInstance();

        userHeadImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCircular(true)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.user_header_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.user_header_def)//加载失败后默认显示图片
                .build();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 4;
        img_height = 3 * img_width / 4;

        coverImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setCrop(false)
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
        final SectionTaskCommitListVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.section_task_commit_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (UserHelper.isLogin()) {
            holder.nameTv.setText("" + vo.getCreatName());
            x.image().bind(holder.userHeadIv,
                    "" + vo.getHeaderPic(),
                    this.userHeadImageOptions);
        }

        x.image().bind(holder.coverIv,
                "" + vo.getThumbnail(),
                this.coverImageOptions);
        holder.coverIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TaskSliderHelper.onTaskSliderListener != null) {
                    TaskSliderHelper.onTaskSliderListener.viewCourse(activity, vo.getId(), 18,
                            activity.getIntent().getStringExtra("schoolId"), getSourceType());
                }
            }
        });
        holder.taskNameTv.setText("" + vo.getName());
        holder.createTimeTv.setText(DateUtils.getFormatByStringDate(vo.getCreatTime(),
                DateUtils.YYYYMMDDHHMM));
        return convertView;
    }


    protected class ViewHolder {
        private ImageView userHeadIv;
        private TextView nameTv;
        private ImageView coverIv;
        private TextView taskNameTv;
        private TextView createTimeTv;

        public ViewHolder(View view) {
            userHeadIv = (ImageView) view.findViewById(R.id.user_head_iv);
            nameTv = (TextView) view.findViewById(R.id.name_tv);
            coverIv = (ImageView) view.findViewById(R.id.cover_iv);
            taskNameTv = (TextView) view.findViewById(R.id.task_name_tv);
            createTimeTv = (TextView) view.findViewById(R.id.create_time_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<SectionTaskCommitListVo> list) {
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
    public void addData(List<SectionTaskCommitListVo> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(list);
    }

    private int getSourceType(){
        return activity.getIntent().getBooleanExtra("isLive", false) ?
                (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity
                        .KEY_IS_FROM_MY, false)
                        ? SourceFromType.MY_ONLINE_LIVE : SourceFromType.ONLINE_LIVE)
                : (activity.getIntent().getBooleanExtra(SectionTaskDetailsActivity
                .KEY_IS_FROM_MY, false)
                ? SourceFromType.LQ_MY_COURSE : SourceFromType.LQ_COURSE);
    }
}
