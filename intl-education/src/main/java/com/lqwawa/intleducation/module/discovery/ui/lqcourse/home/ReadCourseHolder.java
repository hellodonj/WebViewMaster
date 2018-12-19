package com.lqwawa.intleducation.module.discovery.ui.lqcourse.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.ImageUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 阅读课程
 * @date 2018/05/02 10:03
 * @history v1.0
 * **********************************
 */
public class ReadCourseHolder extends FrameLayout
    implements View.OnClickListener{

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private View mRootView;
    private LinearLayout mTitleLayout;
    private TextView mTitleContent;
    // 显示数据源
    private LQCourseConfigEntity mEntity;
    // 点击事件回调接口
    private LQCourseNavigator mNavigator;

    private ImageView mCourse1,mCourse2,mCourse3;

    public ReadCourseHolder(Context context) {
        this(context,null);
    }

    public ReadCourseHolder(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ReadCourseHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        initView();
    }

    /**
     * 初始化View相关信息
     */
    private void initView(){
        final View view = mRootView = mLayoutInflater.inflate(R.layout.item_lq_read_course_layout,this);
        // 标题文本
        mTitleContent = (TextView) view.findViewById(R.id.title_name);
        // mTitleContent.setText(UIUtil.getString(R.string.label_english_international_course));
        // 标题布局
        mTitleLayout = (LinearLayout) view.findViewById(R.id.title_layout);

        mCourse1 = (ImageView) view.findViewById(R.id.course1);
        mCourse2 = (ImageView) view.findViewById(R.id.course2);
        mCourse3 = (ImageView) view.findViewById(R.id.course3);

        mCourse1 = (ImageView) view.findViewById(R.id.course1);
        mCourse2 = (ImageView) view.findViewById(R.id.course2);
        mCourse3 = (ImageView) view.findViewById(R.id.course3);

        mCourse1.setOnClickListener(this);
        mCourse2.setOnClickListener(this);
        mCourse3.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(EmptyUtil.isEmpty(mEntity)) return;
        final List<LQCourseConfigEntity> entities = mEntity.getChildList();
        if(EmptyUtil.isEmpty(entities) || EmptyUtil.isEmpty(mNavigator)){
            return;
        }

        // 回调进入筛选页面
        if(viewId == R.id.course1){
            mNavigator.onClickClassify(entities.get(0));
        }else if(viewId == R.id.course2){
            mNavigator.onClickClassify(entities.get(1));
        }else if(viewId == R.id.course3){
            mNavigator.onClickClassify(entities.get(2));
        }
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
     * @param entity 英语国际课程数据
     */
    public void updateView(@NonNull LQCourseConfigEntity entity){
        if(!EmptyUtil.isEmpty(entity)){
            mEntity = entity;
            mTitleContent.setText(entity.getConfigValue());

            List<LQCourseConfigEntity> childList = entity.getChildList();
            if(EmptyUtil.isEmpty(childList)) return;
            try {
                // 获取到第一个显示数据
                LQCourseConfigEntity entity1 = childList.get(0);
                ImageUtil.fillDefaultView(mCourse1,entity1.getThumbnail());

                // 获取到第二个显示数据
                LQCourseConfigEntity entity2 = childList.get(1);
                ImageUtil.fillDefaultView(mCourse2,entity2.getThumbnail());

                // 获取到第一个显示数据
                LQCourseConfigEntity entity3 = childList.get(2);
                ImageUtil.fillDefaultView(mCourse3,entity3.getThumbnail());

            }catch (Exception e){
                LogUtil.e(EnglishCourseInternationalHolder.class,String.format("LQ学程首页分类数据,数据异常：%s",e.getMessage()));
            }
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
