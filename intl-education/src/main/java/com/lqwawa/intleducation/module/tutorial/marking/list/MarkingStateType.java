package com.lqwawa.intleducation.module.tutorial.marking.list;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface MarkingStateType {
    int MARKING_STATE_NORMAL = -1;
    int MARKING_STATE_NOT = 0;
    int MARKING_STATE_HAVE = 1;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MARKING_STATE_NORMAL,MARKING_STATE_NOT,MARKING_STATE_HAVE})
    public @interface TutorialRoleRes{

    }

}
