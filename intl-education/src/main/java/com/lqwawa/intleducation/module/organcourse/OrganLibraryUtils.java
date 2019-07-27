package com.lqwawa.intleducation.module.organcourse;

import android.content.Context;

import com.lqwawa.intleducation.R;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;

/**
 * @desc:
 * @author: wangchao
 * @date: 2019/06/10
 */
public class OrganLibraryUtils {

    // 全脑馆标签id
    public static final int BRAIN_LIBRARY_ID = 2351;

    // 全脑馆标签Level
    public static final String BRAIN_LIBRARY_LEVEL = "2351";


    // Q配音标签id
    public static final int LIBRARY_QDUBBING_ID = 1003;
    // Q配音标签level
    public static final String LIBRARY_QDUBBING_LEVEL = "1003";

    public static LQCourseConfigEntity getEntityForQDubbing(Context context, String schoolId) {
        LQCourseConfigEntity entity = new LQCourseConfigEntity();
        entity.setConfigValue(context.getString(R.string.dubbing));
        entity.setLibraryType(OrganLibraryType.TYPE_LIBRARY);
        entity.setId(LIBRARY_QDUBBING_ID);
        entity.setLevel(LIBRARY_QDUBBING_LEVEL);
        entity.setEntityOrganId(schoolId);
        return entity;
    }

    public static LQCourseConfigEntity getEntityForBrainLibrary(Context context, String schoolId) {
        LQCourseConfigEntity entity = new LQCourseConfigEntity();
        entity.setConfigValue(context.getString(R.string.common_brain_library));
        entity.setLibraryType(OrganLibraryType.TYPE_BRAIN_LIBRARY);
        entity.setId(BRAIN_LIBRARY_ID);
        entity.setLevel(BRAIN_LIBRARY_LEVEL);
        entity.setEntityOrganId(schoolId);
        return entity;
    }

}
