package com.lqwawa.intleducation.module.tutorial.marking.list;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface OrderByType {
    int MARKING_ASC = 1;
    int MARKING_DESC = 2;
    int MARKING_ASC_TIME_DESC = 3;
    int MARKING_DESC_TIME_DESC = 4;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MARKING_ASC,MARKING_DESC,MARKING_ASC_TIME_DESC,MARKING_DESC_TIME_DESC})
    public @interface OrderByTypeRes{

    }

}
