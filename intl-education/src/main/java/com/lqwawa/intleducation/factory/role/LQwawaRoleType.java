package com.lqwawa.intleducation.factory.role;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 两栖蛙蛙角色定义
 */
public interface LQwawaRoleType {

    int ROLE_TYPE_TEACHER = 0;
    int ROLE_TYPE_STUDENT = 1;
    int ROLE_TYPE_PARENT = 2;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ROLE_TYPE_TEACHER,ROLE_TYPE_STUDENT,ROLE_TYPE_PARENT})
    public @interface LQwawaRoleTypeRes{

    }

}
