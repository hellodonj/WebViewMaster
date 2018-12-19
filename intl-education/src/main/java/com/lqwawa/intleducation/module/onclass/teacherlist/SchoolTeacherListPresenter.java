package com.lqwawa.intleducation.module.onclass.teacherlist;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQTeacherEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.onclass.school.SchoolInfoContract;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂老师列表的Presenter
 * @date 2018/06/05 18:04
 * @history v1.0
 * **********************************
 */
public class SchoolTeacherListPresenter extends BasePresenter<SchoolTeacherListContract.View>
    implements SchoolTeacherListContract.Presenter{

    public SchoolTeacherListPresenter(SchoolTeacherListContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineSchoolTeacherData(@NonNull String schoolId,int pageIndex) {
        OnlineCourseHelper.requestOnlineSchoolTeacherData(schoolId, pageIndex, AppConfig.PAGE_SIZE, new DataSource.Callback<List<LQTeacherEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final SchoolTeacherListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQTeacherEntity> lqTeacherEntities) {
                final SchoolTeacherListContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    if(pageIndex == 0){
                        view.updateSchoolTeacherView(lqTeacherEntities);
                    }else{
                        view.updateSchoolMoreTeacherView(lqTeacherEntities);
                    }
                }
            }
        });
    }
}
