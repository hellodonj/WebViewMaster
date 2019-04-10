package com.lqwawa.intleducation.base.ui;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ly
 * @date : 2018/3/28
 * @desc :
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> mList;
    protected Context mContext;
    protected LayoutInflater mLInflater;
    protected int[] mLayoutId;
    protected ViewGroup mParent;
    protected int mHeadSize = 0;

    protected OnItemClickListener mOnItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * @param data     数据源
     * @param context  上下文
     * @param layoutId 布局Id
     */
    public BaseRecyclerAdapter(List<T> data, Context context, @LayoutRes int... layoutId) {
        this.mList = data == null ? new ArrayList<T>() : data;
        this.mLayoutId = layoutId;
        this.mContext = context;
        this.mLInflater = LayoutInflater.from(mContext);
    }

    /**
     * @param context  上下文
     * @param layoutId 布局Id
     */
    public BaseRecyclerAdapter(Context context, @LayoutRes int... layoutId) {
        this(null, context, layoutId);
    }

    @Override
    @SuppressWarnings("all")
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0 || viewType > mLayoutId.length) {
            throw new ArrayIndexOutOfBoundsException("checkLayoutIndex > LayoutId.length ：" + viewType + ">" +
                    mLayoutId.length);
        }
        if (mLayoutId.length == 0) {
            throw new IllegalArgumentException("not layoutId");
        }
        mParent = parent;
        int layoutId = mLayoutId[viewType];
        View view = inflaterView(layoutId);
        BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
        if (viewHolder == null || viewHolder.getLayoutId() != layoutId) {
            viewHolder = new BaseViewHolder(mContext, layoutId, view);
        }
        if (mOnItemClickListener != null) {
            final BaseViewHolder finalViewHolder = viewHolder;
            viewHolder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = finalViewHolder.getAdapterPosition();
                    position -= getHeadSize();
                    // TODO: 2018/10/11 列表刷新过程中触发事件会发生position获取到值为-1，暂时先在这里控制
                    if (position < 0) {
                        return;
                    }
                    mOnItemClickListener.onItemClick(finalViewHolder, position, mList.get(position));
                }
            });
        }
        return viewHolder;
    }

    @Override
    public final void onBindViewHolder(BaseViewHolder holder, int position) {
        onBindViewHolder(holder, position, null);
    }


    @Override
    public final void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
        final T item = mList.get(position);
        if (payloads == null || payloads.isEmpty()) {
            // 绑定数据
            bindData(holder, position, item);
        } else {
            // 绑定数据
            bindData(holder, position, item, payloads);
        }
    }

    /**
     * 带有payloads的处理数据回调方法
     */
    protected void bindData(BaseViewHolder viewHolder, int position, T item, List<Object> payloads) {

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return checkLayoutIndex(mList.get(position), position);
    }

    protected int checkLayoutIndex(T item, int position) {
        return 0;
    }

    /**
     * 绑定数据到Item View上
     *
     * @param viewHolder vieholder
     * @param position   数据的位置
     * @param item       数据项
     */
    protected abstract void bindData(BaseViewHolder viewHolder, int position, T item);

    /**
     * 实例化view,用于要添加到RecyclerView的View,不会导致设置的宽高不铺满
     */
    public View inflaterView(@LayoutRes int layoutId) {
        return mLInflater.inflate(layoutId, mParent, false);
    }

    public ViewGroup getParent() {
        return mParent;
    }

    /**
     * 绑定相关事件,例如点击长按等,默认空实现
     *
     * @param viewHolder viewHolder
     * @param position   数据的位置
     * @param item       数据项
     */
    @SuppressWarnings("unchecked")
    protected void setListener(final BaseViewHolder viewHolder, final int position, T item) {

    }

    /**
     * 返回headView数量
     */
    protected int getHeadSize() {
        return mHeadSize;
    }

    public void setHeadSize(int headSize) {
        mHeadSize = headSize;
    }

    public void addData(T data) {
        if (data == null) {
            return;
        }
        mList.add(data);
        notifyDataSetChanged();
    }

    public void addData(List<T> dataList) {
        if (dataList == null) {
            return;
        }
        mList.addAll(dataList);
        notifyDataSetChanged();
    }

    public void setNewData(List<T> dataList) {
        mList.clear();
        addData(dataList);
        notifyDataSetChanged();
    }

    public void setNoData() {
        mList.clear();
        notifyDataSetChanged();
    }

    public interface OnItemClickListener<T> {
        void onItemClick(BaseViewHolder holder, int position, T item);
    }

}