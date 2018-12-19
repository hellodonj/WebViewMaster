package com.lqwawa.intleducation.module.onclass;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 在线课堂角色定义
 * @date 2018/06/04 09:42
 * @history v1.0
 * **********************************
 */
public interface OnlineClassRole {

    String ROLE_TEACHER = "0";
    String ROLE_STUDENT = "1";
    String ROLE_PARENT = "2";

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ROLE_TEACHER,ROLE_STUDENT,ROLE_PARENT})
    public @interface RoleRes{

    }

}
