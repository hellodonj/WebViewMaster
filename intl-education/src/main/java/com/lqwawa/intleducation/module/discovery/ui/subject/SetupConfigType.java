package com.lqwawa.intleducation.module.discovery.ui.subject;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface SetupConfigType {

    int TYPE_TEACHER = 1;
    int TYPE_STUDENT = 2;
    int TYPE_CLASS = 3;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_TEACHER,TYPE_STUDENT,TYPE_CLASS})
    public @interface SetupConfigRes{

    }

}
