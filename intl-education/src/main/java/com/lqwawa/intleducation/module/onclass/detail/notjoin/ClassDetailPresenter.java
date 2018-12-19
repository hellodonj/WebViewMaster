package com.lqwawa.intleducation.module.onclass.detail.notjoin;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.onclass.OnlineClassContract;
import com.lqwawa.intleducation.module.onclass.OnlineClassRole;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailContract;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailPresenter;
import com.lqwawa.intleducation.module.user.tool.UserHelper;
import com.osastudio.common.popmenu.EntryBean;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 未加入班级详情的Presenter
 * @date 2018/06/01 12:02
 * @history v1.0
 * **********************************
 */
public class ClassDetailPresenter extends BaseClassDetailPresenter<ClassDetailContract.View>
    implements ClassDetailContract.Presenter{

    public ClassDetailPresenter(ClassDetailContract.View view) {
        super(view);
    }

    @Override
    public void joinInOnlineGratisClass(@NonNull int classId) {
        // 参加免费班级
        OnlineCourseHelper.joinInOnlineGratisClass(classId, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.updateJoinOnlineGratisClass(aBoolean);
                }
            }
        });
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
                final ClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final ClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }
}
