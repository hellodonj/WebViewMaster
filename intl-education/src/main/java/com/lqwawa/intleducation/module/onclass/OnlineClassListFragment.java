package com.lqwawa.intleducation.module.onclass;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.CourseEmptyView;
import com.lqwawa.intleducation.base.PlaceHolderView;
import com.lqwawa.intleducation.base.PresenterActivity;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.widgets.PullRefreshView.PullToRefreshView;
import com.lqwawa.intleducation.base.widgets.adapter.TextWatcherAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerSpaceItemDecoration;
import com.lqwawa.intleducation.common.interfaces.OnLoadStatusChangeListener;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.online.ParamResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.CourseHelper;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.module.discovery.tool.LoginHelper;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.study.OnlineStudyContract;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.ui.study.filtrate.NewOnlineStudyFiltrateParams;
import com.lqwawa.intleducation.module.discovery.ui.study.newfiltrate.NewOnlineClassifyFiltrateActivity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.learn.ui.MyCourseDetailsActivity;
import com.lqwawa.intleducation.module.onclass.detail.join.JoinClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassDetailActivity;
import com.lqwawa.intleducation.module.onclass.detail.notjoin.ClassInfoParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂列表的Fragment 课程关联的在线课堂
 * @date 2018/06/07 23:56
 * @history v1.0
 * **********************************
 */
public class OnlineClassListFragment extends PresenterFragment<OnlineClassContract.Presenter>
    implements OnlineClassContract.View, View.OnClickListener {

    private static final String KEY_EXTRA_COURSE_ID = "KEY_EXTRA_COURSE_ID";

    private static OnlineClassListFragment INSTANCE = null;

    // 课程Id
    private String mCourseId;
    private int pageIndex;
    // 班级列表布局
    private RecyclerView mRecycler;
    private CourseEmptyView mNewEmptyLayout;
    private LinearLayout mEmptyLayout;
    private TextView mLoadingText;
    private Button mBtnStudy;
    private Button mBtnMoreCourse;
    // 班级Adapter
    private OnlineClassAdapter mClassAdapter;

    // 当前点击的在线课堂班级
    private OnlineClassEntity mCurrentClickEntity;
    // 课程信息
    private CourseVo mCourseVo;
    // 参数信息
    private ParamResponseVo.Param mParam;

    private OnLoadStatusChangeListener mListener;

    public static OnlineClassListFragment newInstance(@NonNull String courseId){
        if(INSTANCE == null || true){
            INSTANCE = new OnlineClassListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_EXTRA_COURSE_ID,courseId);
            INSTANCE.setArguments(bundle);
        }
        return  INSTANCE;
    }

    @Override
    protected OnlineClassContract.Presenter initPresenter() {
        return new OnlineClassPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_online_class_list;
    }

    @Override
    protected boolean initArgs(@NonNull Bundle bundle) {
        mCourseId = bundle.getString(KEY_EXTRA_COURSE_ID,null);
        if(EmptyUtil.isEmpty(mCourseId)){
            return false;
        }
        return super.initArgs(bundle);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mEmptyLayout = (LinearLayout) mRootView.findViewById(R.id.empty_layout);
        mNewEmptyLayout = (CourseEmptyView) mRootView.findViewById(R.id.new_empty_layout);
        mLoadingText = (TextView) mRootView.findViewById(R.id.loading_text);
        mBtnStudy = (Button) mRootView.findViewById(R.id.btn_study);
        mBtnMoreCourse = (Button) mRootView.findViewById(R.id.btn_more_course);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.addItemDecoration(new RecyclerSpaceItemDecoration(2,8,false));
        mClassAdapter = new OnlineClassAdapter();
        mRecycler.setAdapter(mClassAdapter);

        mBtnMoreCourse.setOnClickListener(this);

        mClassAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<OnlineClassEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, OnlineClassEntity entity) {
                super.onItemClick(holder, entity);
                // onClickClass(onlineClassEntity);
                ClassInfoParams params = new ClassInfoParams(entity,false,true);
                ClassDetailActivity.show(getActivity(),params);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

        // mSchoolId = "f899b66d-b7a9-4575-8b5f-2d6e47d03483";
        pageIndex = 0;
        mPresenter.requestOnlineClassByCourseId(mCourseId,pageIndex);
        // mRecycler.setVisibility(View.GONE);
        // mEmptyLayout.setVisibility(View.VISIBLE);

        /*mBtnStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EmptyUtil.isEmpty(mCourseVo)) return;
                if(!UserHelper.isLogin()){
                    LoginHelper.enterLogin(getActivity());
                    return;
                }
                requestSchoolInfo(mCourseVo.getOrganId());
            }
        });*/

        /*String token = UserHelper.getUserId();
        CourseHelper.getCourseDetailsData(token, 1, mCourseId, null, new DataSource.Callback<CourseVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(CourseVo courseVo) {
                if(EmptyUtil.isEmpty(courseVo) || EmptyUtil.isEmpty(courseVo.getOrganName())){
                    return;
                }

                OnlineClassListFragment.this.mCourseVo = courseVo;
                String organName = courseVo.getOrganName();
                *//*SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(UIUtil.getString(R.string.label_online_study_empty_first));*//*
                // organName += "-" + UIUtil.getString(R.string.label_space_school_function_class);
                String showText = String.format(UIUtil.getString(R.string.label_online_study_empty_text),organName);
                *//*SpannableString span = new SpannableString(showText);
                span.setSpan(new ForegroundColorSpan(UIUtil.getColor(R.color.textPrimary)), 0, organName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.append(span);*//*
                mLoadingText.setText(showText);
            }
        });*/
    }

    /**
     *查询机构信息
     * @param organId 机构Id
     */
    private void requestSchoolInfo(@NonNull String organId){
        String userId = UserHelper.getUserId();
        SchoolHelper.requestSchoolInfo(userId, organId, new DataSource.Callback<SchoolInfoEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(SchoolInfoEntity entity) {
                // 点击关注
                subscribeSchool(entity);
            }
        });
    }

    /**
     * 订阅机构
     * @param entity 机构实体
     */
    private void subscribeSchool(@NonNull SchoolInfoEntity entity){
        if(EmptyUtil.isEmpty(entity)){
            // 已经进入机构
            return;
        }

        if(entity.hasJoinedSchool() || entity.hasSubscribed()){
            // 已关注
            // 点击机构，进入机构开课班
            OnlineClassListActivity.show(getContext(),entity.getSchoolId(),entity.getSchoolName());
        }else{
            // 如果没有关注 +关注
            SchoolHelper.requestSubscribeSchool(entity.getSchoolId(), new DataSource.Callback<Object>() {
                @Override
                public void onDataNotAvailable(int strRes) {
                    UIUtil.showToastSafe(strRes);
                }

                @Override
                public void onDataLoaded(Object object) {
                    // 关注成功,发送广播,刷新UI
                    // 点击机构，进入机构开课班
                    OnlineClassListActivity.show(getContext(),entity.getSchoolId(),entity.getSchoolName());
                }
            });
        }
    }

    /**
     * 供Activity调用下拉刷新
     */
    public void onHeaderRefresh(){
        pageIndex = 0;
        mPresenter.requestOnlineClassByCourseId(mCourseId,pageIndex);
    }

    /**
     * 加载更多
     */
    public void getMore(){
        mPresenter.requestOnlineClassByCourseId(mCourseId,++pageIndex);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        if(EmptyUtil.isNotEmpty(mListener)){
            mListener.onLoadFlailed();
        }
    }

    /**
     * 点击在线课堂班级
     * @param onlineClassEntity 班级实体
     */
    private void onClickClass(OnlineClassEntity onlineClassEntity) {
        mCurrentClickEntity = onlineClassEntity;
        String classId = onlineClassEntity.getClassId();
        mPresenter.requestLoadClassInfo(classId);
    }

    /**
     * 设置下拉刷新回调的监听
     * @param l
     */
    public void setOnLoadStatusChangeListener(OnLoadStatusChangeListener l){
        this.mListener = l;
    }

    @Override
    public void updateClassListView(@NonNull ParamResponseVo<List<OnlineClassEntity>> vo) {
        mParam = vo.getParam();
        List<OnlineClassEntity> entities = vo.getData();
        mClassAdapter.replace(entities);
        updateClassListView(entities);
    }

    @Override
    public void updateClassMoreListView(@NonNull ParamResponseVo<List<OnlineClassEntity>> vo) {
        mParam = vo.getParam();
        List<OnlineClassEntity> entities = vo.getData();
        mClassAdapter.add(entities);
        updateClassListView(entities);
    }

    public void updateClassListView(@NonNull List<OnlineClassEntity> entities) {
        if(EmptyUtil.isEmpty(entities)){
            mNewEmptyLayout.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }else{
            mNewEmptyLayout.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        }

        // 回调给Activity,加载完毕
        if(EmptyUtil.isNotEmpty(mListener)){
            mListener.onLoadSuccess();
        }

        // 回调给Activity,是否可以加载更多
        if (EmptyUtil.isNotEmpty(mListener)) {
            mListener.onLoadFinish(EmptyUtil.isNotEmpty(entities)
                    && entities.size() >= AppConfig.PAGE_SIZE);
        }
    }

    @Override
    public void onClassCheckSucceed(@NonNull JoinClassEntity entity) {
        // 非空判断
        if(EmptyUtil.isEmpty(entity) || EmptyUtil.isEmpty(mCurrentClickEntity)){
            return;
        }

        boolean needToJoin = entity.isIsInClass();
        String role = getOnlineClassRoleInfo(entity);
        if(needToJoin || OnlineClassRole.ROLE_TEACHER.equals(role)){
            // 已经加入班级 或者是老师身份
            JoinClassDetailActivity.show(getContext(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),true,role);
        }else{
            // 未加入班级
            ClassDetailActivity.show(getContext(),entity.getClassId(),entity.getSchoolId(),mCurrentClickEntity.getId(),true,role);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btn_more_course){
            if(EmptyUtil.isEmpty(mParam)) return;
            String configValue = mParam.getName();

            if(mParam.getDataType() == NewOnlineClassifyFiltrateActivity.DataType.MINORITY_LANGUAGE.getIndex()){
                // 获取中英文数据
                int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
                OnlineCourseHelper.requestNewOnlineClassifyConfigData(NewOnlineClassifyFiltrateActivity.DataType.MINORITY_LANGUAGE.getIndex(), languageRes, new DataSource.Callback<List<NewOnlineConfigEntity>>() {
                    @Override
                    public void onDataNotAvailable(int strRes) {
                        UIUtil.showToastSafe(strRes);
                    }

                    @Override
                    public void onDataLoaded(List<NewOnlineConfigEntity> entities) {
                        String configValue = UIUtil.getString(R.string.label_minority_language_holder_title);
                        NewOnlineConfigEntity entity = new NewOnlineConfigEntity();
                        entity.setConfigValue(configValue);
                        entity.setId(NewOnlineStudyFiltrateActivity.MINORITY_LANGUGAE_ID);
                        entity.setChildList(entities);
                        NewOnlineStudyFiltrateParams params = new NewOnlineStudyFiltrateParams(entity.getConfigValue(),entity);
                        NewOnlineStudyFiltrateActivity.show(getActivity(),params);
                    }
                });
            }else if(mParam.getDataType() == NewOnlineClassifyFiltrateActivity.DataType.BASIC_COURSE.getIndex()){
                NewOnlineClassifyFiltrateActivity.show(getActivity(),NewOnlineClassifyFiltrateActivity.DataType.BASIC_COURSE);
            }else if(mParam.getDataType() == NewOnlineClassifyFiltrateActivity.DataType.INTERNATIONAL.getIndex()){
                NewOnlineClassifyFiltrateActivity.show(getActivity(),NewOnlineClassifyFiltrateActivity.DataType.INTERNATIONAL);
            }
            // mPresenter.requestOnlineStudyLabelData();
            // UIUtil.showToastSafe(R.string.label_watch_more_teach_course);
        }
    }

    @Override
    public void updateOnlineStudyLabelView(@NonNull List<NewOnlineConfigEntity> entities) {
        for (NewOnlineConfigEntity entity:entities) {
            if(entity.getId() == mParam.getFirstId()){
                NewOnlineStudyFiltrateParams params = new NewOnlineStudyFiltrateParams(entity.getConfigValue(),entity);
                NewOnlineStudyFiltrateActivity.show(getActivity(),params);
                break;
            }
        }
    }

    /**
     * 获取在线课堂角色信息
     * @param entity 数据实体
     * @return 判断顺序 老师->家长->学生
     */
    private String getOnlineClassRoleInfo(@NonNull JoinClassEntity entity){
        String roles = entity.getRoles();
        // 默认学生身份
        String roleType = OnlineClassRole.ROLE_STUDENT;
        if(UserHelper.isTeacher(roles)){
            // 老师身份
            roleType = OnlineClassRole.ROLE_TEACHER;
        }else if(UserHelper.isParent(roles)){
            // 家长身份
            roleType = OnlineClassRole.ROLE_PARENT;
        }else if(UserHelper.isStudent(roles)){
            // 学生身份
            roleType = OnlineClassRole.ROLE_STUDENT;
        }
        return roleType;
    }

}
