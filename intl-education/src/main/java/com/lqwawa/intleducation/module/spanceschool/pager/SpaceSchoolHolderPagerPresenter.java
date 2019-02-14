package com.lqwawa.intleducation.module.spanceschool.pager;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.factory.data.entity.SchoolFunctionEntity;
import com.lqwawa.intleducation.factory.presenter.BasePresenter;
import com.lqwawa.intleducation.module.spanceschool.SchoolFunctionStateType;

import java.util.ArrayList;
import java.util.List;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 空中学校空中课堂功能列表的Presenter
 * @date 2018/06/25 10:12
 * @history v1.0
 * **********************************
 */
public class SpaceSchoolHolderPagerPresenter extends BasePresenter<SpaceSchoolHolderPagerContract.View>
    implements SpaceSchoolHolderPagerContract.Presenter{

    public SpaceSchoolHolderPagerPresenter(SpaceSchoolHolderPagerContract.View view) {
        super(view);
    }

    @Override
    public List<SchoolFunctionEntity> getFunctionEntities(@NonNull @SchoolFunctionStateType.FunctionStateRes int state,int pageNumber) {
        List<SchoolFunctionEntity> entities = new ArrayList<>();
        if(pageNumber == 0){
            // 精品课程
            SchoolFunctionEntity entity = new SchoolFunctionEntity(SchoolFunctionEntity.TYPE_FUNCTION_COURSE, R.string.label_space_school_function_shop,R.drawable.ic_course_shop);
            entities.add(entity);
            // 开课班
            entity = new SchoolFunctionEntity(SchoolFunctionEntity.TYPE_FUNCTION_CLASS, R.string.label_space_school_function_class,R.drawable.ic_online_course);
            entities.add(entity);
            // 新版的精品资源库 V5.9隐藏精品资源库
            // entity = new SchoolFunctionEntity(SchoolFunctionEntity.TYPE_FUNCTION_CHOICE_BOOKS,R.string.label_space_school_function_choice_books,R.drawable.ic_choice_books);
            // entities.add(entity);
            // 名师堂
            entity = new SchoolFunctionEntity(SchoolFunctionEntity.TYPE_FUNCTION_TEACHER, R.string.label_space_school_function_teacher,R.drawable.ic_online_teacher);
            entities.add(entity);

            // 学校论坛
            entity = new SchoolFunctionEntity(SchoolFunctionEntity.TYPE_FUNCTION_SCHOOL_FORUM, R.string.label_space_school_function_SCHOOL_FORUM,R.drawable.ic_school_forum);
            entities.add(entity);
        }else if(pageNumber == 1){
            if(state != SchoolFunctionStateType.TYPE_GONE){
                SchoolFunctionEntity entity = null;
                if(state == SchoolFunctionStateType.TYPE_PRINCIPAL_ASSISTANT){
                    // 校园助手
                    entity = new SchoolFunctionEntity(SchoolFunctionEntity.TYPE_FUNCTION_CAMPUS,R.string.label_space_school_function_principal_assistant,R.drawable.ic_principal_assistant);
                }else if(state == SchoolFunctionStateType.TYPE_CAMPUS_PATROL){
                    // 校园巡查
                    entity = new SchoolFunctionEntity(SchoolFunctionEntity.TYPE_FUNCTION_CAMPUS,R.string.label_space_school_function_campus_patrol,R.drawable.ic_campus_patrol);
                }
                entities.add(entity);
            }
        }
        return entities;
    }
}
