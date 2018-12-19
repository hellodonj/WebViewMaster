package com.lqwawa.intleducation.module.discovery.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.DisplayUtil;
import com.lqwawa.intleducation.module.discovery.vo.ResourceVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/16.
 * email:man0fchina@foxmail.com
 */

public class TeacherResListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<ResourceVo> list;
    private LayoutInflater inflater;
    private int img_width;
    private int img_height;
    private int img_widthPic;
    private int img_heighPict;
    ImageOptions imageOptions;
    ImageOptions imageOptionsPic;
    private int resType;

    public TeacherResListAdapter(Activity activity, int resType) {
        this.resType = resType;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ResourceVo>();

        int p_width = activity.getWindowManager().getDefaultDisplay().getWidth();
        img_width = p_width / 2 - 20;
        img_height = img_width * 9 / 16;

        img_widthPic = p_width / 3 - 30;
        img_heighPict = img_width * 9 / 16;

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setSize(img_width, img_height)
                .setLoadingDrawableId(R.drawable.img_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.img_def)//加载失败后默认显示图片
                .build();
        imageOptionsPic = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setCrop(false)
                .setSize(img_widthPic, img_heighPict)
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
        ResourceVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_discovery_res_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (vo.getType() <= 0) {//文件夹
            holder.courseRoot.setVisibility(View.GONE);
            holder.audioRoot.setVisibility(View.GONE);
            if (resType == 3) {
                int sp6dp = DisplayUtil.dip2px(activity, 8);
                if (position == 0) {
                    holder.folderRoot.setPadding(sp6dp, sp6dp, sp6dp, sp6dp);
                } else {
                    holder.folderRoot.setPadding(sp6dp, 0, sp6dp, sp6dp);
                }
            } else if (resType == 4){
                int sp6dp = DisplayUtil.dip2px(activity, 8);
                int sp3dp = DisplayUtil.dip2px(activity, 4);
                if (position % 3 == 0) {
                    if (position == 0) {
                        holder.folderRoot.setPadding(sp6dp, sp6dp, sp3dp, sp6dp);
                    } else {
                        holder.folderRoot.setPadding(sp6dp, 0, sp3dp, sp6dp);
                    }
                }else if(position % 3 == 1){
                    if (position == 1) {
                        holder.folderRoot.setPadding(sp3dp, sp6dp, sp3dp, sp6dp);
                    } else {
                        holder.folderRoot.setPadding(sp3dp, 0, sp3dp, sp6dp);
                    }
                } else {
                    if (position == 2) {
                        holder.folderRoot.setPadding(sp3dp, sp6dp, sp6dp, sp6dp);
                    } else {
                        holder.folderRoot.setPadding(sp3dp, 0, sp6dp, sp6dp);
                    }
                }
            }else {
                int sp6dp = DisplayUtil.dip2px(activity, 8);
                int sp3dp = DisplayUtil.dip2px(activity, 4);
                if (position % 2 == 0) {
                    if (position == 0) {
                        holder.folderRoot.setPadding(sp6dp, sp6dp, sp3dp, sp6dp);
                    } else {
                        holder.folderRoot.setPadding(sp6dp, 0, sp3dp, sp6dp);
                    }
                } else {
                    if (position == 1) {
                        holder.folderRoot.setPadding(sp3dp, sp6dp, sp6dp, sp6dp);
                    } else {
                        holder.folderRoot.setPadding(sp3dp, 0, sp6dp, sp6dp);
                    }
                }
            }
            if (vo.getType() == 0) {
                holder.folderRoot.setVisibility(View.VISIBLE);
                holder.folderNameTv.setText(vo.getOriginName().trim());
            } else {
                holder.folderRoot.setVisibility(View.INVISIBLE);
            }
        } else if (vo.getType() == 2) { //音频
            holder.courseRoot.setVisibility(View.GONE);
            holder.folderRoot.setVisibility(View.GONE);
            holder.audioRoot.setVisibility(View.VISIBLE);
            holder.audioNameTv.setText(vo.getOriginName().trim());
            holder.createTimeTv.setText(vo.getCreateTime().trim());
        } else {
            holder.audioRoot.setVisibility(View.GONE);
            holder.folderRoot.setVisibility(View.GONE);
            holder.courseRoot.setVisibility(View.VISIBLE);
            switch (vo.getType()) {
                case 1://图片
                    x.image().bind(holder.courseImg,
                            (vo.getResourceUrl() + "").trim(),
                            imageOptionsPic);
                    break;
                case 3://视频
                    holder.courseImg.setImageDrawable(
                            activity.getResources().getDrawable(R.drawable.img_video));
                    break;
                case 6://pdf
                    holder.courseImg.setImageDrawable(
                            activity.getResources().getDrawable(R.drawable.img_pdf));
                    break;
                case 20://ppt
                    holder.courseImg.setImageDrawable(
                            activity.getResources().getDrawable(R.drawable.img_ppt));
                    break;
                case 24://word
                    holder.courseImg.setImageDrawable(
                            activity.getResources().getDrawable(R.drawable.img_other));
                    break;
                case 25://text
                    holder.courseImg.setImageDrawable(
                            activity.getResources().getDrawable(R.drawable.img_other));
                    break;
                case 5:
                case 16:
                case 17:
                case 18:
                case 19:
                    x.image().bind(holder.courseImg,
                            (vo.getThumbnailUrl() + "").trim(),
                            imageOptions);
                    break;
                default:
                    holder.courseImg.setImageDrawable(
                            activity.getResources().getDrawable(R.drawable.img_other));
                    break;

            }
            holder.courseImg.setLayoutParams(
                    new LinearLayout.LayoutParams(img_width, img_height));
            holder.courseNameTv.setText(vo.getOriginName().trim());
        }

        return convertView;
    }



    protected class ViewHolder {
        private LinearLayout folderRoot;
        private TextView folderNameTv;
        private LinearLayout courseRoot;
        private ImageView courseImg;
        private TextView courseNameTv;
        private LinearLayout audioRoot;
        private TextView audioNameTv;
        private TextView createTimeTv;

        public ViewHolder(View view) {
            folderRoot = (LinearLayout) view.findViewById(R.id.folder_root);
            folderNameTv = (TextView) view.findViewById(R.id.folder_name_tv);
            courseRoot = (LinearLayout) view.findViewById(R.id.course_root);
            courseImg = (ImageView) view.findViewById(R.id.course_img);
            courseNameTv = (TextView) view.findViewById(R.id.course_name_tv);
            audioRoot = (LinearLayout) view.findViewById(R.id.audio_root);
            audioNameTv = (TextView) view.findViewById(R.id.audio_name_tv);
            createTimeTv = (TextView) view.findViewById(R.id.create_time_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ResourceVo> list) {
        if (list != null) {
            int numColumns = 2;
            if (resType == 4) {
                numColumns = 3;
            }
            if (resType != 3) {//不是音频 音频单列
                int folderCount = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getType() == 0) {
                        folderCount += 1;
                    } else {
                        break;
                    }
                }
                while (folderCount % numColumns != 0) {
                    ResourceVo vo = new ResourceVo();
                    vo.setType(-1);
                    list.add(folderCount, vo);
                    folderCount += 1;
                }
            }
            this.list = new ArrayList<ResourceVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ResourceVo> list) {
        this.list.addAll(list);
    }
}
