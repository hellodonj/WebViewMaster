package com.lqwawa.intleducation.module.onclass.detail.join;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailPresenter;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 已加入在线课堂班级的Presenter
 * @date 2018/06/04 13:49
 * @history v1.0
 * **********************************
 */
public class JoinClassDetailPresenter extends BaseClassDetailPresenter<JoinClassDetailContract.View>
    implements JoinClassDetailContract.Presenter{

    public JoinClassDetailPresenter(JoinClassDetailContract.View view) {
        super(view);
    }

    @Override
    public void requestNotificationData(@NonNull String classId, int pageIndex) {
        OnlineCourseHelper.requestNotificationData(classId,pageIndex, 1, new DataSource.Callback<List<ClassNotificationEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final JoinClassDetailContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ClassNotificationEntity> entities) {
                final JoinClassDetailContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateNotificationView(entities);
                }
            }
        });
    }

    @Override
    public void requestSettingHistory(@NonNull int id) {
        OnlineCourseHelper.requestCompleteGiveOrHistory(id, 3,new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final JoinClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final JoinClassDetailContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.updateSettingHistory(aBoolean);
                }
            }
        });
    }
}
