package com.lqwawa.intleducation.module.organcourse.base;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.onclass.detail.base.BaseClassDetailContract;
import com.lqwawa.intleducation.module.organcourse.OrganCourseClassifyContract;

/**
 * @desc 实体机构学程馆获取授权相关Presenter
 * @author medici
 */
public class SchoolPermissionPresenter<T extends SchoolPermissionContract.View> extends BasePresenter<T>
    implements SchoolPermissionContract.Presenter{


    public SchoolPermissionPresenter(T view) {
        super(view);
    }

    @Override
    public void requestCheckSchoolPermission(@NonNull String schoolId, @NonNull int type,final boolean autoRequest) {
        SchoolHelper.requestCheckSchoolPermission(schoolId, type, new DataSource.Callback<CheckSchoolPermissionEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final SchoolPermissionContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(CheckSchoolPermissionEntity entity) {
                final SchoolPermissionContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) || EmptyUtil.isNotEmpty(entity)){
                    view.updateCheckPermissionView(entity,autoRequest);
                }
            }
        });
    }

    @Override
    public void requestSaveAuthorization(@NonNull String schoolId, @NonNull int type, @NonNull String code) {
        SchoolHelper.requestSavePermission(schoolId, type,code, new DataSource.Callback<CheckPermissionResponseVo<Void>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final SchoolPermissionContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(CheckPermissionResponseVo<Void> vo) {
                final SchoolPermissionContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) || EmptyUtil.isNotEmpty(vo)){
                    view.updateRequestPermissionView(vo);
                }
            }
        });
    }
}
