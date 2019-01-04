package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.common.utils.StringUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.CourseHolderAdapter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.EnglishCourseInternationalHolder;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LQCourseNavigator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc V5.12新版本习课程首页公共的Holder
 */
public class NewCommonHolder extends FrameLayout implements View.OnClickListener {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    private LinearLayout mTitleLayout;
    private TextView mTitleContent;

    private ViewStub mHeaderStub;
    private RecyclerView mRecycler;
    private CourseHolderAdapter mHolderAdapter;

    // 显示数据源
    private LQCourseConfigEntity mEntity;
    private int mViewerType;
    // 点击事件回调接口
    private LQCourseNavigator mNavigator;

    public NewCommonHolder(Context context) {
        this(context,null);
    }

    public NewCommonHolder(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NewCommonHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }


    /**
     * 初始化View相关信息
     */
    private void initView(){
        final View view = mRootView = mLayoutInflater.inflate(R.layout.item_new_common_course_layout,this);
        // 标题布局,标题文本
        mTitleLayout = (LinearLayout) view.findViewById(R.id.title_layout);
        mTitleContent = (TextView) view.findViewById(R.id.title_name);

        mHeaderStub = (ViewStub) view.findViewById(R.id.header_stub);
        mRecycler = (RecyclerView) view.findViewById(R.id.recycler);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8,false));

        mTitleLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!EmptyUtil.isEmpty(mEntity) && !EmptyUtil.isEmpty(mNavigator)){
                    // 进入相关列表页
                    mNavigator.onClickConfigTitleLayout(mEntity);
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
     * 显示列表控件
     * @param sources 列表控件数据源
     */
    private void updateRecyclerView(@NonNull List<LQCourseConfigEntity> sources, @ViewerType.ViewerRes int viewerType){
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRecycler.setLayoutManager(mLayoutManager);
        if(mViewerType > ViewerType.VIEWER_TYPE_EIGHT){
            // 当前有很多Item 切除多余的Item
            sources = new ArrayList<>(sources.subList(0,8));
        }

        mHolderAdapter = new CourseHolderAdapter(sources,null);
        mRecycler.setAdapter(mHolderAdapter);

        mHolderAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<LQCourseConfigEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, LQCourseConfigEntity entity) {
                super.onItemClick(holder, entity);
                // 点击回调
                if(EmptyUtil.isNotEmpty(mNavigator)){
                    mNavigator.onClickClassify(entity);
                }
            }
        });
    }

    /**
     * 根据不同类型显示不同的View
     * @param entities 实体数据
     * @param viewerType <p>{@link ViewerType}</p>
     */
    private void updateView(@NonNull final List<LQCourseConfigEntity> entities, @ViewerType.ViewerRes int viewerType){
        if(viewerType == ViewerType.VIEWER_TYPE_THREE ||
                viewerType == ViewerType.VIEWER_TYPE_FIVE ||
                viewerType == ViewerType.VIEWER_TYPE_SEVEN){
            // 3,5
            ViewStub stub = mHeaderStub;
            stub.inflate();
            View headerLayout = mRootView.findViewById(R.id.header_layout);
            ImageView course1 = (ImageView) headerLayout.findViewById(R.id.course1);
            ImageView course2 = (ImageView) headerLayout.findViewById(R.id.course2);
            ImageView course3 = (ImageView) headerLayout.findViewById(R.id.course3);

            try {
                // 获取到第一个显示数据
                LQCourseConfigEntity entity1 = entities.get(0);
                course1.setTag(entity1);
                ImageUtil.fillDefaultView(course1,entity1.getThumbnail());

                // 获取到第二个显示数据
                LQCourseConfigEntity entity2 = entities.get(1);
                course2.setTag(entity2);
                ImageUtil.fillDefaultView(course2,entity2.getThumbnail());

                // 获取到第一个显示数据
                LQCourseConfigEntity entity3 = entities.get(2);
                course3.setTag(entity3);
                ImageUtil.fillDefaultView(course3,entity3.getThumbnail());

                course1.postInvalidate();
                course2.postInvalidate();
                course3.postInvalidate();
                course1.setOnClickListener(this);
                course2.setOnClickListener(this);
                course3.setOnClickListener(this);



                if(viewerType == ViewerType.VIEWER_TYPE_FIVE ||
                        viewerType == ViewerType.VIEWER_TYPE_SEVEN){
                    // 还有两个或者四个没有显示
                    mRecycler.setVisibility(View.VISIBLE);
                    List<LQCourseConfigEntity> sources = new ArrayList<>(entities.subList(3,entities.size()));
                    updateRecyclerView(sources,viewerType);
                }else if(viewerType == ViewerType.VIEWER_TYPE_THREE){
                    // 隐藏列表控件
                    mRecycler.setVisibility(View.GONE);
                }

            }catch (Exception e){
                LogUtil.e(EnglishCourseInternationalHolder.class,String.format("LQ学程首页分类数据,数据异常：%s",e.getMessage()));
            }
        }else{
            updateRecyclerView(entities,viewerType);
        }
    }

    /**
     * 获取到数据,刷新数据
     * @param entity 英语国际课程数据
     */
    public void updateView(@NonNull final LQCourseConfigEntity entity){
        if(!EmptyUtil.isEmpty(entity)){
            mEntity = entity;
            // 显示标题
            String configValue = entity.getConfigValue();
            StringUtil.fillSafeTextView(mTitleContent,configValue);

            List<LQCourseConfigEntity> childList = entity.getChildList();
            if(EmptyUtil.isEmpty(childList)) return;

            int childCount = childList.size();
            int viewerType = ViewerType.VIEWER_TYPE_MORE;
            if(childCount <= 31){
                viewerType = 1 << childCount;
            }

            this.mViewerType = viewerType;
            updateView(childList,viewerType);
        }

    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        // 回调进入筛选页面
        if(viewId == R.id.course1){
            LQCourseConfigEntity entity = (LQCourseConfigEntity) v.getTag();
            mNavigator.onClickClassify(entity);
        }else if(viewId == R.id.course2){
            LQCourseConfigEntity entity = (LQCourseConfigEntity) v.getTag();
            mNavigator.onClickClassify(entity);
        }else if(viewId == R.id.course3){
            LQCourseConfigEntity entity = (LQCourseConfigEntity) v.getTag();
            mNavigator.onClickClassify(entity);
        }
    }

    /**
     * 设置点击事件的监听
     * @param navigator 回调接口对象
     */
    public void setCourseNavigator(LQCourseNavigator navigator){
        this.mNavigator = navigator;
    }

}
