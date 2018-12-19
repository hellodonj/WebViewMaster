package com.lqwawa.intleducation.module.discovery.ui.person.mycourse;

/**
 * @author medici
 * @desc 学程标题
 */
public class CourseTitle {

    private String realName;
    private boolean child;

    public CourseTitle(String realName, boolean child) {
        this.realName = realName;
        this.child = child;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }
}
