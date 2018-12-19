package com.lqwawa.intleducation.module.onclass.detail.base.introduction;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.onclass.detail.base.plan.ClassPlanContract;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程简介的Presenter
 * @date 2018/06/01 16:05
 * @history v1.0
 * **********************************
 */
public class ClassIntroductionPresenter extends BasePresenter<ClassIntroductionContract.View>
    implements ClassIntroductionContract.Presenter{

    public ClassIntroductionPresenter(ClassIntroductionContract.View view) {
        super(view);
    }

    @Override
    public void requestLoadClassInfo(@NonNull String classId) {
        String memberId = UserHelper.getUserId();
        // 获取班级详情信息时候,弹出Dialog
        start();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, classId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassIntroductionContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final ClassIntroductionContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }

}
