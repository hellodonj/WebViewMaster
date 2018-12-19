package com.lqwawa.intleducation.module.onclass.detail.base.notification;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.ClassNotificationEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂班级详情通知片段的Presenter
 * @date 2018/06/04 14:06
 * @history v1.0
 * **********************************
 */
public class ClassNotificationPresenter extends BasePresenter<ClassNotificationContract.View>
    implements ClassNotificationContract.Presenter{

    public ClassNotificationPresenter(ClassNotificationContract.View view) {
        super(view);
    }

    @Override
    public void requestNotificationData(@NonNull String classId,int pageIndex) {
        OnlineCourseHelper.requestNotificationData(classId,pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<ClassNotificationEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassNotificationContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<ClassNotificationEntity> entities) {
                final ClassNotificationContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) || EmptyUtil.isNotEmpty(entities)){
                    if(pageIndex == 0){
                        view.updateNotificationView(entities);
                    }else{
                        view.updateMoreNotificationView(entities);
                    }
                }
            }
        });
    }

    @Override
    public void requestDeleteNotification(@NonNull String id, int type) {
        OnlineCourseHelper.requestDeleteNotification(id,type,new DataSource.Callback<Boolean>(){
            @Override
            public void onDataLoaded(Boolean aBoolean) {
                final ClassNotificationContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateDeleteNotificationView(aBoolean);
                }
            }

            @Override
            public void onDataNotAvailable(int strRes) {
                final ClassNotificationContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }
        });
    }
}
