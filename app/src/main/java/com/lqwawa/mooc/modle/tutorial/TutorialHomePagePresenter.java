package com.lqwawa.mooc.modle.tutorial;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.LocationEntity;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.helper.TutorialHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.tutorial.regist.IDType;
import com.lqwawa.intleducation.module.tutorial.regist.LocationType;

import java.util.List;

/**
 * @author mrmedici
 * @desc 帮辅模式助教个人主页的Presenter
 */
public class TutorialHomePagePresenter extends BasePresenter<TutorialHomePageContract.View>
    implements TutorialHomePageContract.Presenter{

    public TutorialHomePagePresenter(TutorialHomePageContract.View view) {
        super(view);
    }


}
