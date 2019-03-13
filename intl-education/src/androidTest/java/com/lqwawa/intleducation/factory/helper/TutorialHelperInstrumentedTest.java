package com.lqwawa.intleducation.factory.helper;

import android.app.Application;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.lqwawa.intleducation.common.utils.UIUtil;
import com.lqwawa.intleducation.common.utils.Utils;
import com.lqwawa.intleducation.factory.data.DataSource;
import com.lqwawa.intleducation.factory.data.entity.LQCourseConfigEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.DateFlagEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.MemberSchoolEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TaskEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorCommentEntity;
import com.lqwawa.intleducation.factory.data.entity.tutorial.TutorEntity;
import com.lqwawa.intleducation.module.discovery.ui.lqcourse.home.LanguageType;
import com.lqwawa.intleducation.module.discovery.vo.CourseVo;

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
        TutorialHelper.requestWorkDataWithIdentityId("", "", "1", "", "","","", "", 0, 0,0, 10, new DataSource.Callback<List<TaskEntity>>() {
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

    @Test
    public void requestTutorialCourses() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestTutorialCourses("0018356f-ad4b-439f-88fa-e4cdbf4de32b", "", 1,0,10, new DataSource.Callback<List<CourseVo>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<CourseVo> courseVos) {
                System.out.println(courseVos);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestMyTutorData() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestMyTutorData("0018356f-ad4b-439f-88fa-e4cdbf4de32b", "",0,10, new DataSource.Callback<List<TutorEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<TutorEntity> entities) {
                System.out.println(entities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestAddTutorByStudentId() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestAddTutorByStudentId("0018356f-ad4b-439f-88fa-e4cdbf4de32b","0018356f-ad4b-439f-88fa-e4cdbf4de32b" ,"", new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                System.out.println(aBoolean);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestQueryAddedTutorByTutorId() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestQueryAddedTutorByTutorId("0018356f-ad4b-439f-88fa-e4cdbf4de32b","0018356f-ad4b-439f-88fa-e4cdbf4de32b" , new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                System.out.println(aBoolean);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestTutorCommentData() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestTutorCommentData("95eab099-0cf8-4b69-866b-83c85dfad8a0","95eab099-0cf8-4b69-866b-83c85dfad8a0",0,10 , new DataSource.Callback<List<TutorCommentEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<TutorCommentEntity> entities) {
                System.out.println(entities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestTutorSingleCommentState() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestTutorSingleCommentState("95eab099-0cf8-4b69-866b-83c85dfad8a0",1,0, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                System.out.println(aBoolean);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestAddPraiseByCommentId() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestAddPraiseByCommentId("95eab099-0cf8-4b69-866b-83c85dfad8a0",1, new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                System.out.println(aBoolean);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestAddTutorialComment() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        TutorialHelper.requestAddTutorialComment("95eab099-0cf8-4b69-866b-83c85dfad8a0","95eab099-0cf8-4b69-866b-83c85dfad8a0","测试内容", new DataSource.Callback<Boolean>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(Boolean aBoolean) {
                System.out.println(aBoolean);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }

    @Test
    public void requestTutorialConfigData() throws Exception{
        Context appContext = InstrumentationRegistry.getTargetContext();
        x.Ext.init((Application) appContext.getApplicationContext());
        CountDownLatch downLatch = new CountDownLatch(1);
        int languageRes = Utils.isZh(appContext) ? LanguageType.LANGUAGE_CHINESE : LanguageType.LANGUAGE_OTHER;
        TutorialHelper.requestTutorialConfigData(languageRes,1,0, new DataSource.Callback<List<LQCourseConfigEntity>>() {
            @Override
            public void onDataNotAvailable(int strRes) {
                System.out.println(strRes);
            }

            @Override
            public void onDataLoaded(List<LQCourseConfigEntity> entities) {
                System.out.println(entities);
                downLatch.countDown();
            }
        });

        downLatch.await();
        System.out.println("End");
    }
}
