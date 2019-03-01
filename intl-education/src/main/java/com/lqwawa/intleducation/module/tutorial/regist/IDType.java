package com.lqwawa.intleducation.module.tutorial.regist;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IDType {
    int ID_TYPE_IDENTITY_CARD = 0;
    int ID_TYPE_PASSPORT = 0;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ID_TYPE_IDENTITY_CARD,ID_TYPE_PASSPORT})
    public @interface IDTypeRes{

    }
}
