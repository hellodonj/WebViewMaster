package com.lqwawa.intleducation.module.tutorial.teacher.courses.record;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface AuditType {
    int AUDITING = 0;
    int AUDITED_PASS = 1;
    int AUDITED_REJECT = 2;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AUDITING,AUDITED_PASS,AUDITED_REJECT})
    public @interface AuditTypeRes{

    }

}