package com.lqwawa.intleducation.factory.helper;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.MemberSchoolEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xutils.x;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class TutorialHelperInstrumentedTest{

    @Test
    public void requestWorkDataWithIdentityId() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestWorkDataWithIdentityId("", "", "1", "", "", "", 0, 0, 10, new DataSource.Callback<List<TaskEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<TaskEntity> taskEntities) {
                System.out.println(taskEntities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestDateFlagForAssist() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestDateFlagForAssist("283220D6-7D77-4B7B-8E74-CA5B86739F7D", "0", "2018-01-01","2018-01-12",0, new DataSource.Callback<List<DateFlagEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<DateFlagEntity> flagEntities) {
                System.out.println(flagEntities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestLoadMemberSchoolData() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        SchoolHelper.requestLoadMemberSchoolData("bc4d2af9-33a9-4d75-a0c1-41499e69b7be", "", "3", new DataSource.Callback<List<MemberSchoolEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<MemberSchoolEntity> entities) {
                System.out.println(entities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }
}
