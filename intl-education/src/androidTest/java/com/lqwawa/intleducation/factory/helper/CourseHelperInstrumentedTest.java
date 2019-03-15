package com.lqwawa.intleducation.factory.helper;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.course.TutorialGroupEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorChoiceEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xutils.x;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class CourseHelperInstrumentedTest {

    @Test
    public void requestTutorDataByCourseId() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        CourseHelper.requestTutorDataByCourseId("1838","212",1,0,10, new DataSource.Callback<List<TutorialGroupEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<TutorialGroupEntity> taskEntities) {
                System.out.println(taskEntities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }


    @Test
    public void requestTutorsByCourseId() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        CourseHelper.requestTutorsByCourseId("e479c488-305b-466c-b81a-d1ee35345486","819","",1,10, new DataSource.Callback<List<TutorChoiceEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<TutorChoiceEntity> taskEntities) {
                System.out.println(taskEntities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void isTutorCourseByCourseId() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        CourseHelper.isTutorCourseBycourseId("e479c488-305b-466c-b81a-d1ee35345486", "819", new DataSource.SucceedCallback<Boolean>() {
            @Override
            public void onDataLoaded(Boolean aBoolean) {
                System.out.println();
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

}
