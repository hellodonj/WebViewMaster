package com.lqwawa.intleducation.module.organcourse.base;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.base.vo.ResponseVo;
import com.lqwawa.intleducation.factory.data.entity.response.CheckPermissionResponseVo;
import com.lqwawa.intleducation.factory.data.entity.school.CheckSchoolPermissionEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.organcourse.OrganCourseClassifyContract;

/**
 * @desc 实体机构学程馆获取授权相关契约类
 * @author medici
 */
public interface SchoolPermissionContract{

    interface Presenter extends BaseContract.Presenter{
        /**
         * 检查是否有机构课程授权
         * @param schoolId 机构Id
         * @param type 检查授权类型
         * @param autoRequest 是否自动申请授权
         */
        void requestCheckSchoolPermission(@NonNull String schoolId, @NonNull int type, boolean autoRequest);
        // 申请授权
        void requestSaveAuthorization(@NonNull String schoolId, @NonNull int type, @NonNull String code);
    }

    interface View<T extends Presenter> extends BaseContract.View<T>{
        /**
         * 检查机构授权的回调
         * @param entity 回调的数据
         * @param autoRequest 是否自动申请授权
         */
        void updateCheckPermissionView(@NonNull CheckSchoolPermissionEntity entity, boolean autoRequest);
        void updateRequestPermissionView(@NonNull CheckPermissionResponseVo<Void> vo);
    }

}
