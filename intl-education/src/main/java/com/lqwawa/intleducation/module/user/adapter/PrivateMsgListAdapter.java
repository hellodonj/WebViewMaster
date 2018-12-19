package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
//import com.hyphenate.chat.EMConversation;
//import com.hyphenate.chat.EMMessage;
//import com.hyphenate.easeui.utils.EaseCommonUtils;
//import com.hyphenate.easeui.utils.EaseSmileUtils;
//import com.hyphenate.util.DateUtils;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.module.user.vo.HXConversationVo;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class PrivateMsgListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<HXConversationVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;
    private ConversationComparator conversationComparator = new ConversationComparator();

    public PrivateMsgListAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<HXConversationVo>();

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
        HXConversationVo hxConversation = (HXConversationVo) getItem(position);
//        EMConversation conversation = hxConversation.getConversation();
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_private_msg_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
//
//        if (hxConversation.isIsGroup()){
//            holder.name_tv.setText(hxConversation.getGroup().getName());
//            holder.user_head_iv.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
//        }else{
//            x.image().bind(holder.user_head_iv,
//                    hxConversation.getPersonal().getThumbnail(),
//                    imageOptions);
//            holder.name_tv.setText(hxConversation.getPersonal().getName());
//        }
//
//        if (conversation.getAllMsgCount() != 0) {
//            if (conversation.getUnreadMsgCount() > 0) {
//                holder.new_msg_count_tv.setText(conversation.getUnreadMsgCount() + "");
//                holder.new_msg_count_tv.setVisibility(View.VISIBLE);
//            }else{
//                holder.new_msg_count_tv.setVisibility(View.GONE);
//            }
//            EMMessage lastMessage = conversation.getLastMessage();
//
//            holder.msg_tv.setText(EaseSmileUtils.getSmiledText(activity,
//                    EaseCommonUtils.getMessageDigest(lastMessage, activity)),
//                    TextView.BufferType.SPANNABLE);
//            holder.time_tv.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
//        }
        return convertView;
    }

    protected class ViewHolder {
        private ImageView user_head_iv;
        private TextView new_msg_count_tv;
        private TextView name_tv;
        private TextView time_tv;
        private TextView msg_tv;

        public ViewHolder(View view) {
            user_head_iv = (ImageView) view.findViewById(R.id.user_head_iv);
            new_msg_count_tv = (TextView) view.findViewById(R.id.new_msg_count_tv);
            name_tv = (TextView) view.findViewById(R.id.name_tv);
            time_tv = (TextView) view.findViewById(R.id.time_tv);
            msg_tv = (TextView) view.findViewById(R.id.msg_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<HXConversationVo> list) {
        if (list != null) {
            this.list = new ArrayList<HXConversationVo>(list);
            Collections.sort(this.list, conversationComparator);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<HXConversationVo> list) {
        this.list.addAll(list);
    }

    public class ConversationComparator implements Comparator {
        public int compare(Object arg0,Object arg1){
//            HXConversationVo obj0 = (HXConversationVo)arg0;
//            HXConversationVo obj1 = (HXConversationVo)arg1;
//            int flag = (obj1.getConversation().getLastMessage().getMsgTime() + "")
//                    .compareTo((obj0.getConversation().getLastMessage().getMsgTime() + ""));
//            return flag;
            return 0;
        }
    }
}
