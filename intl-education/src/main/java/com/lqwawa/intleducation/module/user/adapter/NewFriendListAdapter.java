package com.lqwawa.intleducation.module.user.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.ui.MyBaseAdapter;
import com.lqwawa.intleducation.base.utils.LogUtil;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.module.user.vo.NewFriendVo;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2016/11/12.
 * email:man0fchina@foxmail.com
 */

public class NewFriendListAdapter extends MyBaseAdapter {
    private Activity activity;
    private List<NewFriendVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;
    private OnContentChangedListener onContentChangedListener;

    public NewFriendListAdapter(Activity activity, OnContentChangedListener listener) {
        this.activity = activity;
        this.onContentChangedListener = listener;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<NewFriendVo>();

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
        final NewFriendVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_new_friend_list_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        x.image().bind(holder.user_head_iv, ("" + vo.getThumbnail()).trim(), imageOptions);
        holder.name_tv.setText("" + vo.getComeName());
        holder.msg_tv.setText("" + vo.getContent());
        if (vo.getDealFlag() == 0) {//未处理
            holder.agreed_tv.setVisibility(View.VISIBLE);
            holder.ignore_tv.setVisibility(View.VISIBLE);
            holder.has_friend_tv.setVisibility(View.GONE);
            holder.agreed_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doAgreed(vo, 1);
                }
            });
            holder.ignore_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doAgreed(vo, 0);
                }
            });
        }else if (vo.getDealFlag() == 1){//已同意
            holder.agreed_tv.setVisibility(View.GONE);
            holder.ignore_tv.setVisibility(View.GONE);
            holder.has_friend_tv.setVisibility(View.VISIBLE);
            holder.has_friend_tv.setText(activity.getResources().getString(R.string.has_agreed));
        }else if(vo.getDealFlag() == 2){//已忽略
            holder.agreed_tv.setVisibility(View.GONE);
            holder.ignore_tv.setVisibility(View.GONE);
            holder.has_friend_tv.setVisibility(View.VISIBLE);
            holder.has_friend_tv.setText(activity.getResources().getString(R.string.has_ignore));
        }

        return convertView;
    }

    private void doAgreed(final NewFriendVo vo, int isAgree){
        final RequestVo requestVo = new RequestVo();
        requestVo.addParams("id", vo.getId());
        requestVo.addParams("isAgree", isAgree);
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.DealFriendRequest + requestVo.getParams());

        params.setConnectTimeout(10000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ResponseVo<String> result = JSON.parseObject(s,
                        new TypeReference<ResponseVo<String>>() {
                        });
                if (result.getCode() == 0) {
                    if (onContentChangedListener != null){
                        onContentChangedListener.OnContentChanged();
                    }
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.deal_friend_request)
                    + activity.getResources().getString(R.string.success));
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d("Test", "处理好友请求失败:" + throwable.getMessage());


                ToastUtil.showToast(activity, activity.getResources().getString(R.string.net_error_tip));
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private class ViewHolder {
        ImageView user_head_iv;
        TextView name_tv;
        TextView msg_tv;
        TextView agreed_tv;
        TextView ignore_tv;
        TextView has_friend_tv;

        public ViewHolder(View parentView) {
            user_head_iv = (ImageView) parentView.findViewById(R.id.user_head_iv);
            name_tv = (TextView) parentView.findViewById(R.id.name_tv);
            msg_tv = (TextView) parentView.findViewById(R.id.msg_tv);
            agreed_tv = (TextView) parentView.findViewById(R.id.agreed_tv);
            ignore_tv = (TextView) parentView.findViewById(R.id.ignore_tv);
            has_friend_tv = (TextView) parentView.findViewById(R.id.has_friend_tv);
        }
    }

    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<NewFriendVo> list) {
        if (list != null) {
            this.list = new ArrayList<NewFriendVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<NewFriendVo> list) {
        this.list.addAll(list);
    }
}
