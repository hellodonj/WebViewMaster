package com.lqwawa.intleducation.module.discovery.ui.subject.add;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.common.utils.EmptyUtil;
import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.helper.LQConfigHelper;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SetupConfigType;
import com.lqwawa.intleducation.module.discovery.ui.subject.SubjectContract;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * 添加科目的Presenter
 */
public class AddSubjectPresenter extends BasePresenter<AddSubjectContract.View>
    implements AddSubjectContract.Presenter{

    private List<Integer> ids;

    public AddSubjectPresenter(AddSubjectContract.View view) {
        super(view);
        ids = new ArrayList<>();
    }


    @Override
    public void requestAssignConfigData(@NonNull String memberId) {
        // 获取中英文数据
        int languageRes = Utils.isZh(UIUtil.getContext()) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        LQConfigHelper.requestAssignConfigData(memberId, languageRes, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                final AddSubjectContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view)){
                    view.showError(strRes);
                }
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                final AddSubjectContract.View view = getView();
                if(EmptyUtil.isNotEmpty(view) && EmptyUtil.isNotEmpty(entities)){
                    view.updateAssignConfigView(entities);
                }
            }
        });
    }

    @Override
    public String getSelectedIds(@NonNull List<LQCourseConfigEntity> entities) {
        ids.clear();
        checkEntityTag(entities);
        ListIterator<Integer> integerIterator = ids.listIterator();
        StringBuilder idBuilder = new StringBuilder();
        while(integerIterator.hasNext()){
            String idStr = integerIterator.next().toString();
            idBuilder.append(idStr);
            if(integerIterator.hasNext()){
                idBuilder.append(",");
            }
        }
        return idBuilder.toString();
    }




    private void checkEntityTag(@NonNull List<LQCourseConfigEntity> entities){
        if(EmptyUtil.isEmpty(entities)) return;
        for (LQCourseConfigEntity entity:entities) {
            if(entity.isSelected()){
                ids.add(entity.getId());
                continue;
            }

            List<LQCourseConfigEntity> childList = entity.getChildList();
            checkEntityTag(childList);
        }
    }
}
