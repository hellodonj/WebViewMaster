package com.lqwawa.intleducation.module.tutorial.regist;

import android.support.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface LocationType {
    int LOCATION_TYPE_COUNTRY = 0;
    int LOCATION_TYPE_PROVINCE = 1;
    int LOCATION_TYPE_CITY = 2;
    int LOCATION_TYPE_DISTRICT = 3;

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCATION_TYPE_COUNTRY,LOCATION_TYPE_PROVINCE,LOCATION_TYPE_CITY,LOCATION_TYPE_DISTRICT})
    public @interface LocationTypeRes{

    }

}
