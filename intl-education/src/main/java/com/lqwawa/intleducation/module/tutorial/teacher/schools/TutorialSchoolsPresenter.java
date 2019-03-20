package com.lqwawa.intleducation.module.tutorial.teacher.schools;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.MemberSchoolEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.teacher.courses.TutorialCoursesContract;

import java.util.List;
import java.util.ListIterator;

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
                    if(EmptyUtil.isNotEmpty(entities)) {
                        ListIterator<MemberSchoolEntity> iterator = entities.listIterator();
                        while (iterator.hasNext()){
                            MemberSchoolEntity next = iterator.next();
                            if(TextUtils.equals(next.getSchoolId(),"00000000-0000-0000-0000-000000000000")){
                                // 过滤脏数据
                                iterator.remove();
                            }
                        }
                    }
                    view.updateTutorialSchoolsView(entities);
                }
            }
        });
    }
}
