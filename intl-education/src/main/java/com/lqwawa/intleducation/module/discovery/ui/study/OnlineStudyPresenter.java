package com.lqwawa.intleducation.module.discovery.ui.study;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.JoinClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.NewOnlineConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.OnlineClassEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyEntity;
import com.lqwawa.intleducation.factory.data.entity.online.OnlineStudyOrganEntity;
import com.lqwawa.intleducation.factory.data.entity.school.SchoolInfoEntity;
import com.lqwawa.intleducation.factory.helper.OnlineCourseHelper;
import com.lqwawa.intleducation.factory.helper.SchoolHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.user.tool.UserHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 在线学习的Presenter
 */
public class OnlineStudyPresenter extends BasePresenter<OnlineStudyContract.View>
    implements OnlineStudyContract.Presenter{

    public OnlineStudyPresenter(OnlineStudyContract.View view) {
        super(view);
    }

    @Override
    public void requestOnlineStudyLabelData() {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        OnlineCourseHelper.requestNewOnlineStudyLabelData(languageRes,new DataSource.Callback<List<NewOnlineConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineStudyContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<NewOnlineConfigEntity> entities) {
                final OnlineStudyContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateOnlineStudyLabelView(entities);
                }
            }
        });
    }

    @Override
    public void requestOnlineStudyOrganData() {
        OnlineCourseHelper.requestOnlineStudyOrganData(new DataSource.Callback<OnlineStudyEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineStudyContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(OnlineStudyEntity entity) {
                final OnlineStudyContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){

                    // 热门数据
                    List<OnlineClassEntity> rmList = entity.getRmList();
                    if(EmptyUtil.isNotEmpty(rmList)){
                        if(rmList.size() > 4){
                            // 返回的数据大于两个
                            rmList = new ArrayList<>(rmList.subList(0,4));
                        }
                        view.updateOnlineStudyHotView(rmList);
                    }

                    // 最新数据
                    List<OnlineClassEntity> zxList = entity.getZxList();
                    if(EmptyUtil.isNotEmpty(zxList)){
                        if(zxList.size() > 4){
                            // 返回的数据大于两个
                            zxList = new ArrayList<>(zxList.subList(0,4));
                        }
                        view.updateOnlineStudyLatestView(zxList);
                    }

                    // 机构信息
                    List<OnlineStudyOrganEntity> organList = entity.getOrganList();
                    if(EmptyUtil.isNotEmpty(organList)){
                        if(organList.size() > 4){
                            // 返回的数据大于四个
                            organList = new ArrayList<>(organList.subList(0,4));
                        }
                        view.updateOnlineStudyOrganView(organList);
                    }
                }
            }
        });
    }

    @Override
    public void requestLoadClassInfo(@NonNull String classId) {
        String memberId = UserHelper.getUserId();
        // 获取班级详情信息时候,弹出Dialog
        start();
        // 发送获取班级详情细信息的请求
        OnlineCourseHelper.loadOnlineClassInfo(memberId, classId, new DataSource.Callback<JoinClassEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineStudyContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(JoinClassEntity joinClassEntity) {
                // 进行验证
                final OnlineStudyContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(joinClassEntity)){
                    view.onClassCheckSucceed(joinClassEntity);
                }
            }
        });
    }

    @Override
    public void requestSchoolInfo(@NonNull String schoolId,@NonNull final OnlineStudyOrganEntity organEntity) {
        String userId = UserHelper.getUserId();
        SchoolHelper.requestSchoolInfo(userId, schoolId, new DataSource.Callback<SchoolInfoEntity>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final OnlineStudyContract.View view = getView();
                if(!EmptyUtil.isEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(SchoolInfoEntity entity) {
                final OnlineStudyContract.View view = getView();
                if(!EmptyUtil.isEmpty(view) && !EmptyUtil.isEmpty(entity)){
                    view.updateSchoolInfoView(entity,organEntity);
                }
            }
        });
    }
}
