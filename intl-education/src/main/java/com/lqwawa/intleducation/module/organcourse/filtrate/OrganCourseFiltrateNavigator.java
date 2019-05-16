package com.lqwawa.intleducation.module.organcourse.filtrate;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

import java.util.List;

/**
 * @author: wangchao
 * @date: 2019/05/15
 * @desc:
 */
public interface OrganCourseFiltrateNavigator {
    boolean triggerUpdateData(@NonNull OrganCourseFiltrateParams params);
    List<CourseVo> getCourseVoList();
}
