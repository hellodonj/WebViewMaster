package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.common.CharacterParser;
import com.lqwawa.intleducation.module.user.vo.ContactVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class ContactListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<ContactVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;
    private PinyinComparator pinyinComparator = new PinyinComparator();
    private CharacterParser characterParser;

    public ContactListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<ContactVo>();

        characterParser = CharacterParser.getInstance();

        imageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setRadius(16)
                .setCrop(false)
                .setLoadingDrawableId(R.drawable.contact_head_def)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.contact_head_def)//加载失败后默认显示图片
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
        ContactVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_person_contact_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        x.image().bind(holder.user_head_iv, ("" + vo.getThumbnail()).trim(), imageOptions);
        holder.new_msg_count_tv.setVisibility(View.GONE);
        holder.name_tv.setText("" + vo.getName());
        if (vo.getUserType() == 1) {
            holder.user_type_tv.setVisibility(View.VISIBLE);
            holder.user_type_tv.setText(activity.getResources().getString(R.string.teacher));
        } else {
            holder.user_type_tv.setVisibility(View.GONE);
        }

        //根据position获取分类的首字母的char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(vo.getFirstLetter());
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
        return convertView;
    }


    private class ViewHolder {
        TextView alpha;
        ImageView user_head_iv;
        TextView new_msg_count_tv;
        TextView name_tv;
        TextView user_type_tv;

        public ViewHolder(View parentView) {
            alpha = (TextView) parentView.findViewById(R.id.alpha);
            user_head_iv = (ImageView) parentView.findViewById(R.id.user_head_iv);
            new_msg_count_tv = (TextView) parentView.findViewById(R.id.new_msg_count_tv);
            name_tv = (TextView) parentView.findViewById(R.id.name_tv);
            user_type_tv = (TextView) parentView.findViewById(R.id.user_type_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<ContactVo> list) {
        if (list != null) {
            this.list = new ArrayList<ContactVo>(list);
            Collections.sort(this.list, pinyinComparator);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<ContactVo> list) {
        this.list.addAll(list);
        Collections.sort(this.list, pinyinComparator);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return list.get(position).getFirstLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getFirstLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    public class PinyinComparator implements Comparator<ContactVo> {
        public int compare(ContactVo o1, ContactVo o2) {
            if (o1.getFirstLetter().equals("@")
                    || o2.getFirstLetter().equals("#")) {
                return -1;
            } else if (o1.getFirstLetter().equals("#")
                    || o2.getFirstLetter().equals("@")) {
                return 1;
            } else {
                return o1.getFirstLetter().compareTo(o2.getFirstLetter());
            }
        }

    }
}
