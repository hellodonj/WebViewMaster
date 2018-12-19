package com.lqwawa.intleducation.module.discovery.ui.study;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.module.onclass.OnlineClassAdapter;

import java.util.List;

/**
 * @author mrmedici
 * @desc 在线学习，机构列表
 */
public class OnlineStudyOrganHolder extends FrameLayout{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    private LinearLayout mTitleLayout;
    private TextView mTitleContent;
    private RecyclerView mOrganRecycler;
    private OrganAdapter mOrganAdapter;
    private OnlineStudyNavigator mNavigator;

    private int mSort;

    public OnlineStudyOrganHolder(Context context) {
        this(context,null);
    }

    public OnlineStudyOrganHolder(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OnlineStudyOrganHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    /**
     * 初始化View相关信息
     */
    private void initView(){
        final View view = mRootView = mLayoutInflater.inflate(R.layout.holder_online_study_organ_layout,this);
        // 标题文本
        mTitleContent = (TextView) view.findViewById(R.id.title_content);
        // mTitleContent.setText(UIUtil.getString(R.string.label_english_international_course));
        // 标题布局
        mTitleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
        mOrganRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mOrganRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),4){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mOrganRecycler.setLayoutManager(mLayoutManager);
        // mOrganRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8,false));
        mOrganAdapter = new OrganAdapter();
        mOrganRecycler.setAdapter(mOrganAdapter);

        mOrganAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineStudyOrganEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineStudyOrganEntity entity) {
                super.onItemClick(holder, entity);
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onClickOrgan(entity);
                }
            }
        });

        // 机构列表不需要显示
        mTitleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onClickTitleLayout(mSort);
                }
            }
        });
    }

    /**
     * 获取根布局
     * @return 根布局View
     */
    public View getRootView(){
        return mRootView;
    }

    /**
     * 获取到数据,刷新数据
     * @param sort 规则
     * @param entities 班级数据
     */
    public void updateView(@NonNull @OnlineStudyType.OnlineStudyRes int sort, @NonNull List<OnlineStudyOrganEntity> entities){
        mSort = sort;
        if(sort == OnlineStudyType.SORT_HOT){
            // 热门数据
            mTitleContent.setText(UIUtil.getString(R.string.label_online_study_hot_data));
        }else if(sort == OnlineStudyType.SORT_LATEST){
            // 最新数据
            mTitleContent.setText(UIUtil.getString(R.string.label_online_study_latest_data));
        }else if(sort == OnlineStudyType.SORT_ORGAN){
            mTitleContent.setText(UIUtil.getString(R.string.label_online_study_organ_data));
        }
        mOrganAdapter.replace(entities);
    }

    /**
     * 设置点击事件的监听
     * @param navigator 回调接口对象
     */
    public void setOnlineStudyNavigator(OnlineStudyNavigator navigator){
        this.mNavigator = navigator;
    }

}
