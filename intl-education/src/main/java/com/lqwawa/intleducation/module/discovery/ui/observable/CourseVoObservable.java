package com.lqwawa.intleducation.module.discovery.ui.observable;

import java.util.Observable;

/**
 * **********************************
 *
 * @author MrMedici
 * @email mr.medici@foxmail.com
 * @function 课程信息被观察者
 * @date 2018/04/11 16:00
 * @history v1.0
 * **********************************
 */
public class CourseVoObservable extends Observable{

    /**
     * 分发数据
     * @param arg 数据实体
     */
    public void triggerObservers(Object arg){
        setChanged();
        notifyObservers(arg);
    }

    @Override
    @Deprecated
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Override
    @Deprecated
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }
}
