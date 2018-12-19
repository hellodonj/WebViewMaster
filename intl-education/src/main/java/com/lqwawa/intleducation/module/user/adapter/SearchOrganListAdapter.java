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
import com.lqwawa.intleducation.module.discovery.vo.OrganVo;
import com.lqwawa.intleducation.module.login.ui.LoginActivity;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
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

public class SearchOrganListAdapter extends MyBaseAdapter {
    private static String TAG = "SearchOrganListAdapter";
    private Activity activity;
    private List<OrganVo> list;
    private LayoutInflater inflater;
    private ImageOptions imageOptions;
    private OnContentChangedListener onContentChangedListener;

    public SearchOrganListAdapter(Activity activity, OnContentChangedListener listener) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.onContentChangedListener = listener;
        list = new ArrayList<OrganVo>();

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
        final OrganVo vo = list.get(position);
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.mod_user_search_organ_list_item, null);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        }

        x.image().bind(holder.user_head_iv, ("" + vo.getThumbnail()).trim(), imageOptions);
        holder.name_tv.setText("" + vo.getName());
        if (!vo.isAttented()){
            holder.add_attention_tv.setVisibility(View.VISIBLE);
            holder.has_attention_tv.setVisibility(View.GONE);
            holder.add_attention_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAttention(vo.getId());
                }
            });
        }else{
            holder.add_attention_tv.setVisibility(View.GONE);
            holder.has_attention_tv.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void addAttention(String id){
        if (!UserHelper.isLogin()) {//未登录状态 跳转到登陆界面
            LoginActivity.login(activity);
            return;
        }
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("organId", id);
        LogUtil.d(TAG, requestVo.getParams());
        RequestParams params =
                new RequestParams(AppConfig.ServerUrl.AddAttention + requestVo.getParams());
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
                    activity.setResult(Activity.RESULT_OK);
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.has)
                                    + activity.getResources().getString(R.string.attention)
                                    + "!");
                } else {
                    ToastUtil.showToast(activity,
                            activity.getResources().getString(R.string.attention)
                                    + activity.getResources().getString(R.string.failed)
                                    + result.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.d(TAG, "关注失败:" + throwable.getMessage());
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
        TextView add_attention_tv;
        TextView has_attention_tv;
        public ViewHolder(View parentView) {
            user_head_iv= (ImageView)parentView.findViewById(R.id.user_head_iv);
            name_tv= (TextView)parentView.findViewById(R.id.name_tv);
            add_attention_tv= (TextView)parentView.findViewById(R.id.add_attention_tv);
            has_attention_tv= (TextView)parentView.findViewById(R.id.has_attention_tv);
        }
    }
    /**
     * 下拉刷新设置数据
     *
     * @param list
     */
    public void setData(List<OrganVo> list) {
        if (list != null) {
            this.list = new ArrayList<OrganVo>(list);
        } else {
            this.list.clear();
        }
    }

    /**
     * 上拉加载更多，新增数据
     *
     * @param list
     */
    public void addData(List<OrganVo> list) {
        this.list.addAll(list);
    }
}
