package com.lqwawa.intleducation.module.discovery.lessontask;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.helper.LessonHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.learn.vo.LqTaskCommitListVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 听说课读写单Presenter
 * @date 2018/04/12 17:00
 * @history v1.0
 * **********************************
 */
public class LessonDetailTaskPresenter extends BasePresenter<LessonDetailTaskContract.View>
    implements LessonDetailTaskContract.Presenter{

    public LessonDetailTaskPresenter(LessonDetailTaskContract.View view) {
        super(view);
    }

    @Override
    public void getCommittedTaskByTaskId(@NonNull SectionResListVo vo, boolean canEdit, String memberId) {
        String studentId = UserHelper.getUserId();
        if(!canEdit){
            // 如果是家长身份
            studentId = "";
        }
        LessonHelper.getCommittedTaskByTaskId(vo.getTaskId(), studentId, new DataSource.Callback<LqTaskCommitListVo>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                UIUtil.showToastSafe(strRes);
            }

            @Override
            public void onDataLoaded(LqTaskCommitListVo commitTaskVo) {
                LessonDetailTaskContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                   view.refreshView(commitTaskVo);
                }
            }
        });
    }
}
