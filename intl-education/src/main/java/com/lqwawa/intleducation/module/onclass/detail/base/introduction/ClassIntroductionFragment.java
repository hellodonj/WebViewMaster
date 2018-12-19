package com.lqwawa.intleducation.module.onclass.detail.base.introduction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.module.discovery.ui.CourseDetailsActivity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.course.CommonCourseAdapter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.base.OnlineTabParams;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailNavigator;
import com.lqwawa.intleducation.module.onclass.related.RelatedCourseListActivity;
import com.lqwawa.intleducation.module.onclass.related.RelatedCourseParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级详情 课堂简介Fragment
 * @date 2018/06/01 16:03
 * @history v1.0
 * **********************************
 */
public class ClassIntroductionFragment extends PresenterFragment<ClassIntroductionContract.Presenter>
    implements ClassIntroductionContract.View,View.OnClickListener{

    private static final String KEY_EXTRA_ENTITY = "KEY_EXTRA_ENTITY";
    private static final String KEY_EXTRA_IS_COURSE_ENTER = "KEY_EXTRA_IS_COURSE_ENTER";

    private NestedScrollView mNestedView;
    private TextView mTvTimeTable;
    // 课堂简介WebView
    private WebView mWVIntroduction;
    // 适用对象
    private TextView mSuitObjectContent;
    // 授课目标
    private TextView mTeachingGoalContent;
    // 授课老师列表
    private LinearLayout mTeacherLayout;
    private View mTeacherLine;
    private RecyclerView mTeacherRecycler;
    // 关联课程列表
    private LinearLayout mCourseLayout;
    private RecyclerView mCourseRecycler;
    // 推荐课程列表
    private LinearLayout mRecommendCourseLayout;
    private RecyclerView mRecommendRecycler;

    // 授课老师数据集合
    private TeacherAdapter mTeacherAdapter;
    // 关联课程Adapter
    private CommonCourseAdapter mRelatedCourseAdapter;
    // 推荐课程Adapter
    private RelatedCourseAdapter mRecommendAdapter;

    private OnlineTabParams mTabParams;
    // 数据实体
    private ClassDetailEntity mClassDetailEntity;
    private JoinClassEntity mClassEntity;
    // 原本角色信息
    private String mRole;
    private boolean isParent;
    // 是否是已经加入的
    private boolean isJoin;
    private String schoolId;

    // 是否从学程入口进来
    private boolean isCourseEnter;

    public static Fragment newInstance(@NonNull OnlineTabParams params){
        ClassIntroductionFragment fragment = new ClassIntroductionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FRAGMENT_BUNDLE_OBJECT,params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected ClassIntroductionContract.Presenter initPresenter() {
        return new ClassIntroductionPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_class_introduction;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mTabParams = (OnlineTabParams) bundle.getSerializable(FRAGMENT_BUNDLE_OBJECT);
        if(EmptyUtil.isEmpty(mTabParams)) return false;
        schoolId = mTabParams.getSchoolId();
        mClassDetailEntity = mTabParams.getDetailEntity();
        mRole = mTabParams.getRole();
        isParent = mTabParams.isParent();
        isJoin = mTabParams.isJoin();
        if(EmptyUtil.isEmpty(mClassDetailEntity)){
            return false;
        }
        return super.initArgs(bundle);

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mNestedView = (NestedScrollView) mRootView.findViewById(R.id.nested_view);
        mTvTimeTable = (TextView) mRootView.findViewById(R.id.tv_timetable);
        mWVIntroduction = (WebView) mRootView.findViewById(R.id.wv_introduction);
        mSuitObjectContent = (TextView) mRootView.findViewById(R.id.tv_target_content);
        mTeachingGoalContent = (TextView) mRootView.findViewById(R.id.tv_goal_content);
        mTeacherLayout = (LinearLayout) mRootView.findViewById(R.id.teacher_layout);
        mTeacherRecycler = (RecyclerView) mRootView.findViewById(R.id.teachers_recycler);
        mTeacherLine = mRootView.findViewById(R.id.teacher_line);

        mCourseLayout = (LinearLayout) mRootView.findViewById(R.id.course_layout);
        mCourseLayout.setOnClickListener(this);
        mCourseRecycler = (RecyclerView) mRootView.findViewById(R.id.course_recycler);
        // 推荐课程
        mRecommendCourseLayout = (LinearLayout) mRootView.findViewById(R.id.recommend_course_layout);
        mRecommendRecycler = (RecyclerView) mRootView.findViewById(R.id.recommend_recycler);


        mTvTimeTable.setOnClickListener(this);
        if(isJoin){
            // V5.11隐藏课程表功能
            mTvTimeTable.setVisibility(View.GONE);
        }else{
            mTvTimeTable.setVisibility(View.GONE);
        }

        // 填充适用对象
        if(!EmptyUtil.isEmpty(mClassDetailEntity.getData())){
            mSuitObjectContent.setText(mClassDetailEntity.getData().get(0).getSuitObj());
            // 填充授课目标
            mTeachingGoalContent.setText(mClassDetailEntity.getData().get(0).getLearnGoal());

            // 获取图文混排WebView加载的Url
            String url = AppConfig.ServerUrl.WebViewOnlineClassDetailIntroduction;
            url = url.replace("{id}",Integer.toString(mClassDetailEntity.getData().get(0).getId()));
            // WebView加载地址
            mWVIntroduction.loadUrl(url);
        }

        // 填充授课老师
        if(EmptyUtil.isEmpty(mClassDetailEntity.getTeacher())){
            // 取消显示授课老师布局
            mTeacherLayout.setVisibility(View.GONE);
            mTeacherLine.setVisibility(View.GONE);
        }else{
            mTeacherAdapter = new TeacherAdapter(mClassDetailEntity.getTeacher());
            mTeacherRecycler.setNestedScrollingEnabled(false);
            mTeacherRecycler.setLayoutManager(new GridLayoutManager(getContext(),4){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            });
            mTeacherRecycler.setAdapter(mTeacherAdapter);
        }


        // 填充关联课程
        if(EmptyUtil.isEmpty(mClassDetailEntity.getRelatedCourse()) || isCourseEnter || true){
            // 取消显示关联课程布局
            mCourseLayout.setVisibility(View.GONE);
        }else{
            List<CourseVo> relatedArray = mClassDetailEntity.getRelatedCourse();
            if(EmptyUtil.isNotEmpty(relatedArray) && relatedArray.size() > 3){
                relatedArray = new ArrayList<>(relatedArray.subList(0,3));
            }
            mRelatedCourseAdapter = new CommonCourseAdapter(relatedArray);
            GridLayoutManager mLayoutManger = new GridLayoutManager(getContext(),3){
                @Override
                public boolean canScrollVertically() {
                    return super.canScrollVertically();
                }
            };
            mCourseRecycler.setLayoutManager(mLayoutManger);
            mCourseRecycler.setAdapter(mRelatedCourseAdapter);
            mRelatedCourseAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<CourseVo>() {
                @Override
                public void onItemClick(RecyclerAdapter.ViewHolder holder, CourseVo courseVo) {
                    super.onItemClick(holder, courseVo);
                    // 进入课程详情
                    /*String roles = classInfo.getRoles();
                    boolean isTeacher = UserHelper.isTeacher(roles);
                    CourseDetailsActivity.start(getActivity(),courseId, true, UserHelper.getUserId(),isTeacher);*/
                    // 进入课程详情 从在线课堂进入
                    CourseDetailsActivity.start(getActivity(),true,true,courseVo.getId(), true, UserHelper.getUserId());
                }
            });
        }


        // 填充推荐课程
        if(EmptyUtil.isEmpty(mClassDetailEntity.getTjCourse()) || isCourseEnter || true){
            // 不显示推荐课程
            // 取消显示推荐课程布局
            mRecommendCourseLayout.setVisibility(View.GONE);
        }else{
            List<ClassDetailEntity.RelatedCourseBean> tjArray = mClassDetailEntity.getTjCourse();
            if(EmptyUtil.isNotEmpty(tjArray) && tjArray.size() > 1){
                tjArray = new ArrayList<>(tjArray.subList(0,1));
            }
            mRecommendAdapter = new RelatedCourseAdapter(tjArray);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext()){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            mRecommendRecycler.setLayoutManager(mLayoutManager);
            mRecommendRecycler.setAdapter(mRecommendAdapter);
            mRecommendAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<ClassDetailEntity.RelatedCourseBean>() {
                @Override
                public void onItemClick(RecyclerAdapter.ViewHolder holder, ClassDetailEntity.RelatedCourseBean relatedCourseBean) {
                    super.onItemClick(holder, relatedCourseBean);
                    /*String roles = classInfo.getRoles();
                    boolean isTeacher = UserHelper.isTeacher(roles);
                    CourseDetailsActivity.start(getActivity(),courseId, true, UserHelper.getUserId(),isTeacher);*/
                    // 进入课程详情 从在线课堂进入
                    CourseDetailsActivity.start(getActivity(),true,true,Integer.toString(relatedCourseBean.getId()), true, UserHelper.getUserId());
                }
            });
        }
    }

    @Override
    protected void initData() {
        super.initData();
        // 获取到班级信息
        if(EmptyUtil.isNotEmpty(mClassDetailEntity.getData())){
            String classId = mClassDetailEntity.getData().get(0).getClassId();
            mPresenter.requestLoadClassInfo(classId);
        }
    }

    @Override
    public void onClassCheckSucceed(@NonNull JoinClassEntity entity) {
        mClassEntity = entity;
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.course_layout){
            // 点击关联课程
            RelatedCourseParams params = new RelatedCourseParams(mClassDetailEntity.getParam(),mClassDetailEntity.getRelatedCourse());
            RelatedCourseListActivity.show(getActivity(),params);
        }else if(viewId == R.id.tv_timetable){
            // 课程表
            if(EmptyUtil.isEmpty(mClassDetailEntity) ||
                    EmptyUtil.isEmpty(mClassDetailEntity.getData()) ||
                    EmptyUtil.isEmpty(mClassEntity)){
                return;
            }

            ClassDetailEntity.DataBean dataBean = mClassDetailEntity.getData().get(0);

            boolean isTeacher = OnlineClassRole.ROLE_TEACHER.equals(mRole);
            boolean isHeadMaster = UserHelper.getUserId().equals(dataBean.getCreateId());
            if(isParent){
                mRole = OnlineClassRole.ROLE_PARENT;
                isHeadMaster = false;
                isTeacher = false;
            }

            Intent intent = new Intent();
            intent.putExtra("schoolId",schoolId);
            intent.putExtra("classId",dataBean.getClassId());
            intent.putExtra("role_type",Integer.parseInt(mRole));
            intent.putExtra("isHeadMaster",isHeadMaster);
            intent.putExtra("isTeacher",isTeacher);
            intent.putExtra("isOpenAirClassLiveTable",true);
            intent.putExtra("id",mClassEntity.getClassMailListId());
            intent.putExtra("className",dataBean.getName());
            intent.putExtra("schoolName",dataBean.getOrganName());
            // 是否是历史班
            intent.putExtra("is_histroy_class",mTabParams.isGiveHistory());
            // 传参是否是结束授课
            intent.putExtra("is_finish_lecture",mTabParams.isGiveFinish());
            intent.setClassName(getContext().getPackageName(),
                    "com.galaxyschool.app.wawaschool.OpenCourseHelpActivity");
            startActivity(intent);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 课堂简介显示
        Activity activity = getActivity();
        if(activity instanceof ClassDetailNavigator){
            ClassDetailNavigator navigator = (ClassDetailNavigator) activity;
            navigator.onCommentChanged(getUserVisibleHint());
        }

        if(getUserVisibleHint()){
            // 显示了课堂简介
            if(getActivity() instanceof BaseClassDetailActivity){
                BaseClassDetailActivity parentActivity = (BaseClassDetailActivity) getActivity();
                parentActivity.addRefreshView(mNestedView);
                parentActivity.getRefreshLayout().setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        parentActivity.refreshData();
                    }
                });
            }
        }
    }
}
