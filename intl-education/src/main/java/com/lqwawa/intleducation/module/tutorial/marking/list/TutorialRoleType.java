package com.lqwawa.intleducation.module.tutorial.marking.list;

import android.support.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface TutorialRoleType {
    String TUTORIAL_TYPE_TUTOR = "0";
    String TUTORIAL_TYPE_STUDENT = "1";
    String TUTORIAL_TYPE_PARENT = "2";

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TUTORIAL_TYPE_TUTOR,TUTORIAL_TYPE_STUDENT, TUTORIAL_TYPE_PARENT})
    public @interface TutorialRoleRes{

    }

}
