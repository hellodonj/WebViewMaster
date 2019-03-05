package com.lqwawa.intleducation.module.tutorial.teacher.schools;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.tutorial.MemberSchoolEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 我的帮辅机构契约类
 */
public interface TutorialSchoolsContract {

    interface Presenter extends BaseContract.Presenter {
        // 请求我的帮辅机构列表
        void requestTutorialSchoolsData(@NonNull String memberId, @NonNull String schoolName, int pageIndex);

    }

    interface View extends BaseContract.View<Presenter>{
        void updateTutorialSchoolsView(List<MemberSchoolEntity> courseVos);
        void updateMoreTutorialSchoolsView(List<MemberSchoolEntity> courseVos);
    }

}
