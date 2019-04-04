package com.lqwawa.intleducation.module.tutorial.marking.choice;


import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;

import java.io.Serializable;

public interface TutorChoiceNavigator extends Serializable{
    void onChoice(@NonNull TutorChoiceEntity entity);
}