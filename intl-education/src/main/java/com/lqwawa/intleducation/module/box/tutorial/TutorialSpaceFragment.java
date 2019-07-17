package com.lqwawa.intleducation.module.box.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.base.PresenterFragment;
import com.lqwawa.intleducation.base.utils.ToastUtil;
import com.lqwawa.intleducation.base.vo.RequestVo;
import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.base.widgets.recycler.RecyclerAdapter;
import com.lqwawa.intleducation.common.ui.ContactsInputDialog;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.LogUtil;
import com.lqwawa.intleducation.factory.data.StringCallback;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.module.box.FunctionAdapter;
import com.lqwawa.intleducation.module.box.FunctionEntity;
import com.lqwawa.intleducation.module.box.common.CommonMarkingListFragment;
import com.lqwawa.intleducation.module.box.common.CommonMarkingParams;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingListActivity;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialMarkingParams;
import com.lqwawa.intleducation.module.tutorial.marking.list.TutorialRoleType;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesParams;
import com.lqwawa.intleducation.module.tutorial.teacher.schools.TutorialSchoolsActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.schools.TutorialSchoolsParams;
import com.lqwawa.intleducation.module.tutorial.teacher.students.TutorialStudentActivity;
import com.lqwawa.intleducation.module.tutorial.teacher.students.TutorialStudentParams;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * @author medici
 * @desc 帮辅空间的页面
 */
public class TutorialSpaceFragment extends PresenterFragment<TutorialSpaceContract.Presenter>
        implements TutorialSpaceContract.View, View.OnClickListener {

    private RecyclerView mRecycler;
    private FunctionAdapter mAdapter;
    private TextView mTvReviewPrice;
    private ContactsInputDialog inputBoxDialog;


    @Override
    protected TutorialSpaceContract.Presenter initPresenter() {
        return new TutorialSpacePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_tutorial_space;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mRecycler = (RecyclerView) mRootView.findViewById(R.id.recycler);
        mRecycler.setNestedScrollingEnabled(false);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        mRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new FunctionAdapter();
        mRecycler.setAdapter(mAdapter);
        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<FunctionEntity>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, FunctionEntity functionEntity) {
                super.onItemClick(holder, functionEntity);
                int titleId = functionEntity.getTitleId();
                if (titleId == R.string.label_tutorial_work) {
                    skipTutorialWork();
                } else if (titleId == R.string.label_tutorial_student) {
                    skipTutorialStudents();
                } else if (titleId == R.string.label_tutorial_course) {
                    skipTutorialCourse();
                } else if (titleId == R.string.label_tutorial_organ) {
                    skipTutorialOrgan();
                }
            }
        });

        mTvReviewPrice = (TextView) mRootView.findViewById(R.id.tv_review_price);
        mTvReviewPrice.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        List<FunctionEntity> entities = new ArrayList<>();
        entities.add(new FunctionEntity(R.string.label_tutorial_work, R.drawable.ic_tutorial_work));
        entities.add(new FunctionEntity(R.string.label_tutorial_student, R.drawable.ic_tutorial_student));
        entities.add(new FunctionEntity(R.string.label_tutorial_course, R.drawable.ic_tutorial_course));
        entities.add(new FunctionEntity(R.string.label_tutorial_organ, R.drawable.ic_tutorial_organ));
        mAdapter.replace(entities);

        String memberId = UserHelper.getUserId();
        CommonMarkingParams params = new CommonMarkingParams(true, memberId);
        Fragment fragment = CommonMarkingListFragment.newInstance(params);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.lay_content, fragment)
                .commit();
    }

    // 去作业列表页面
    private void skipTutorialWork() {
        String memberId = UserHelper.getUserId();
        TutorialMarkingParams params = new TutorialMarkingParams(memberId, TutorialRoleType.TUTORIAL_TYPE_TUTOR);
        TutorialMarkingListActivity.show(getActivity(), params);
    }

    // 去我帮辅的学生页面
    private void skipTutorialStudents() {
        String memberId = UserHelper.getUserId();
        TutorialStudentParams params = new TutorialStudentParams(memberId, getString(R.string.label_tutorial_student));
        TutorialStudentActivity.show(getActivity(), params);
    }

    // 去我帮辅的课程页面
    private void skipTutorialCourse() {
        String memberId = UserHelper.getUserId();
        String configValue = getString(R.string.label_tutorial_course);
        TutorialCoursesParams params = new TutorialCoursesParams(memberId, configValue);
        TutorialCoursesActivity.show(getActivity(), params);
    }

    // 去我的帮辅机构页面
    private void skipTutorialOrgan() {
        String memberId = UserHelper.getUserId();
        String configValue = getString(R.string.label_tutorial_organ);
        TutorialSchoolsParams params = new TutorialSchoolsParams(memberId, configValue);
        TutorialSchoolsActivity.show(getActivity(), params);
    }

    /**
     * 帮辅空间入口
     *
     * @return TutorialSpaceFragment
     */
    public static TutorialSpaceFragment newInstance() {
        TutorialSpaceFragment fragment = new TutorialSpaceFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    //批阅价格点击事件
    @Override
    public void onClick(View v) {
        String memberId = UserHelper.getUserId();
        RequestVo requestVo = new RequestVo();
        requestVo.addParams("memberId", memberId);
        RequestParams params = new RequestParams(AppConfig.ServerUrl.PostGetTutorPrice);
        params.setAsJsonContent(true);
        params.setBodyContent(requestVo.getParamsWithoutToken());
        params.setConnectTimeout(10000);
        LogUtil.i(TutorialSpaceFragment.class, "send request ==== " + params.getUri());
        x.http().post(params, new StringCallback<String>() {
            @Override
            public void onSuccess(String str) {
                LogUtil.i(TutorialSpaceFragment.class, "request " + params.getUri() + " result :" + str);
                TypeReference<TutorPriceEntity> typeReference = new TypeReference<TutorPriceEntity>() {
                };
                TutorPriceEntity priceEntity = JSON.parseObject(str, typeReference);
                if (priceEntity.getCode() == 0) {
                    String tutorPrice = priceEntity.getPrice() + "";
                    showEditReviewPriceDialog(tutorPrice);
                }
            }
        });
    }

    //设置批阅价格弹框
    private void showEditReviewPriceDialog(String price) {
        inputBoxDialog = new ContactsInputDialog(
                getActivity(),
                getString(R.string.review_price_dialog_title),
                null,
                price,
                getString(R.string.cancel),
                (dialog, which) -> dialog.dismiss(),
                getString(R.string.confirm), (dialog, which) -> {
            confirmReviewPrice();
        });
        inputBoxDialog.setInputLimitNumber(6);
        inputBoxDialog.setInputLimit();
        inputBoxDialog.setUnitDisplay(true);
        inputBoxDialog.setIsAutoDismiss(false);
        inputBoxDialog.show();
    }

    //确认设置的批阅价格
    private void confirmReviewPrice() {
        //IntroductionSuperTaskFragment 845
        String etPrice = inputBoxDialog.getInputText();
        String memberId = UserHelper.getUserId();
        if (EmptyUtil.isEmpty(etPrice) || etPrice.equals("")|| etPrice.length() > 6) {
            ToastUtil.showToast(getContext(), R.string.label_price_tip);
        } else {
            RequestVo requestVo = new RequestVo();
            requestVo.addParams("memberId", memberId);
            requestVo.addParams("markingPrice", etPrice);
            RequestParams params = new RequestParams(AppConfig.ServerUrl.UpdateTutorInfo + requestVo.getParams());
            params.setConnectTimeout(10000);
            LogUtil.i(TutorialSpaceFragment.class, "send request ==== " + params.getUri());
            x.http().get(params, new StringCallback<String>() {
                @Override
                public void onSuccess(String str) {
                    LogUtil.i(TutorialHelper.class, "request " + params.getUri() + " result :" + str);
                    TypeReference<ResponseVo> typeReference = new TypeReference<ResponseVo>() {
                    };
                    ResponseVo responseVo = JSON.parseObject(str, typeReference);
                    if (responseVo.isSucceed()) {
                        inputBoxDialog.dismiss();
                        ToastUtil.showToast(getActivity(), R.string.modify_success_tip);
                    }
                }

                @Override
                public void onError(Throwable throwable, boolean b) {
                    super.onError(throwable, b);
                    ToastUtil.showToast(getActivity(), R.string.net_error_tip);
                }
            });
        }
    }
}
