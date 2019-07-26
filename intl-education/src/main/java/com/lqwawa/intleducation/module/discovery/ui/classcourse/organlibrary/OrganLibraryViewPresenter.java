package com.lqwawa.intleducation.module.discovery.ui.classcourse.organlibrary;

import android.support.annotation.NonNull;
import android.util.Log;

import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;

public class OrganLibraryViewPresenter implements OrganLibraryTypeContract.View {

    private View view;
    protected OrganLibraryTypeContract.Presenter presenter;
    private String TAG = getClass().getSimpleName();

    public OrganLibraryViewPresenter(OrganLibraryViewPresenter.View view) {
        this.view = view;
        new OrganLibraryTypePresenter(this);
    }

    @Override
    public void showError(int str) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void setPresenter(OrganLibraryTypeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest) {
//        Log.e(TAG, "updateCheckPermissionView: " );
        if (view != null) view.updateCheckPermissionView(entity, autoRequest);
    }

    @Override
    public void updateRequestPermissionView(@NonNull CheckPermissionResponseVo<Void> vo) {
//        Log.e(TAG, "updateRequestPermissionView: " );
        if (view != null) view.updateRequestPermissionView(vo);
    }

    public interface View {
        void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest);

        void updateRequestPermissionView(@NonNull CheckPermissionResponseVo<Void> vo);
    }

    public void onDestory() {
        if (presenter != null) presenter.destroy();
    }

    public void requestCheckSchoolPermission(String organId, int type, boolean autoRequest) {
        if (presenter != null) presenter.requestCheckSchoolPermission(organId, type, autoRequest);
    }

    public void commitAuthorizationCode(String organId, String code) {
        if (presenter != null) presenter.requestSaveAuthorization(organId, 0, code);
    }
}
