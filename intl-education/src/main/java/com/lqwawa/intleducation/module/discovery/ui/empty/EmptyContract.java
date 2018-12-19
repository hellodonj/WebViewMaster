package com.lqwawa.intleducation.module.discovery.ui.empty;

import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空布局测试契约类
 * @date 2018/05/16 11:21
 * @history v1.0
 * **********************************
 */
public class EmptyContract {

    interface Presenter extends BaseContract.Presenter{
        void getData(int pageIndex, int pageSize);
    }

    interface View extends BaseContract.RecyclerView<Presenter,LQCourseConfigEntity>{

    }

}
