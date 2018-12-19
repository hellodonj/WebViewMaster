package com.lqwawa.intleducation.module.spanceschool.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.SchoolFunctionEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.spanceschool.SchoolFunctionStateType;

import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空中学校在线课堂功能列表的契约类
 * @date 2018/06/25 10:10
 * @history v1.0
 * **********************************
 */
public class SpaceSchoolHolderPagerContract {

    interface Presenter extends BaseContract.Presenter{
        /**
         * 生成Tab数据
         * @param state 校园助手,校园巡查显示类型
         * @param pageNumber 第几页的功能菜单
         * @return 功能列表实体集合
         */
        List<SchoolFunctionEntity> getFunctionEntities(@NonNull @SchoolFunctionStateType.FunctionStateRes int state, int pageNumber);
    }

    interface View extends BaseContract.View<Presenter>{

    }

}
