package com.lqwawa.intleducation.module.discovery.lessontask;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.LQwawaBaseEntity;
import com.lqwawa.intleducation.factory.presenter.BaseContract;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionDetailsVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 听说课,读写单 契约类
 * @date 2018/04/12 16:58
 * @history v1.0
 * **********************************
 */
public interface LessonDetailTaskContract{

    interface Presenter extends BaseContract.Presenter{
        /**
         * 根据taskId加载已经提交的任务(作业)
         * @param vo 听说课或读写单资源对象
         * @param canEdit false 家长身份
         * @param memberId 孩子的memberId
         */
        void getCommittedTaskByTaskId(@NonNull SectionResListVo vo, boolean canEdit, String memberId);
    }

    interface View extends BaseContract.View<Presenter>{
        /**
         * 获取到任务信息后刷新View
         * @param commitTaskVo 拉取到的听说课 or 读写单任务数据
         */
        void refreshView(LqTaskCommitListVo commitTaskVo);
    }

}
