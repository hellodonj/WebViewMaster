package com.lqwawa.intleducation.module.tutorial.schools;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.AppConfig;
import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.MemberSchoolEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;
import com.lqwawa.intleducation.module.tutorial.courses.TutorialCoursesContract;

import java.util.List;

/**
 * @authr mrmedici
 * @desc 我的帮辅机构Presenter
 */
public class TutorialSchoolsPresenter extends BasePresenter<TutorialSchoolsContract.View>
    implements TutorialSchoolsContract.Presenter{

    public TutorialSchoolsPresenter(TutorialSchoolsContract.View view) {
        super(view);
    }

    @Override
    public void requestTutorialSchoolsData(@NonNull String memberId, @NonNull String schoolName, int pageIndex) {
        SchoolHelper.requestLoadMemberSchoolData(memberId, schoolName, "3", new DataSource.Callback<List<MemberSchoolEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final TutorialSchoolsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<MemberSchoolEntity> entities) {
                final TutorialSchoolsContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.updateTutorialSchoolsView(entities);
                }
            }
        });
    }
}
