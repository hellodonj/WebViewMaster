package com.lqwawa.intleducation.module.onclass.related;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.factory.data.entity.ClassDetailEntity;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mrmedici
 * @desc 关联课程传递参数
 */
public class RelatedCourseParams extends BaseVo{

    private ClassDetailEntity.ParamBean param;

    private ArrayList<CourseVo> relatedCourse;

    public RelatedCourseParams(ClassDetailEntity.ParamBean param, List<CourseVo> relatedCourse) {
        this.param = param;
        this.relatedCourse = (ArrayList<CourseVo>) relatedCourse;
    }

    public ClassDetailEntity.ParamBean getParam() {
        return param;
    }

    public ArrayList<CourseVo> getRelatedCourse() {
        return relatedCourse;
    }
}
