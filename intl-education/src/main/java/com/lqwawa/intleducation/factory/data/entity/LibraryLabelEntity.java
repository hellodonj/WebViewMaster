package com.lqwawa.intleducation.factory.data.entity;

import com.lqwawa.intleducation.module.organcourse.OrganLibraryType;

import java.util.ArrayList;
import java.util.List;

public class LibraryLabelEntity extends BaseEntity {

    private List<LibraryLabelEntity> data;

    private String thumbnail;
    private String name;
    private int type;
    private int id;
    private String configValue;
    private int configType;
    private List<LibraryLabelEntity> list;
    private boolean isAuthorized;

    public int getId() {
        return id;
    }

    public List<LibraryLabelEntity> getData() {
        return data;
    }

    public void setData(List<LibraryLabelEntity> data) {
        this.data = data;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public int getConfigType() {
        return configType;
    }

    public void setConfigType(int configType) {
        this.configType = configType;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<LibraryLabelEntity> getList() {
        return list;
    }

    public void setList(List<LibraryLabelEntity> list) {
        this.list = list;
    }


    public static List<LibraryLabelEntity> generateData(boolean isSuperTaskType,int taskType, List<LibraryLabelEntity> libraryLabelEntities) {
        ArrayList<LibraryLabelEntity> entities = new ArrayList<>();
        for (LibraryLabelEntity entity : libraryLabelEntities) {
            int type = entity.getType();
            if (taskType == StudyTaskType.Q_DUBBING) {
                if (type == OrganLibraryType.TYPE_LIBRARY || type == OrganLibraryType.TYPE_TEACHING_PLAN) {
                    entities.add(entity);
                }
                if (entities.size() >= 2) return entities;
            }else if (taskType == StudyTaskType.TASK_ORDER || taskType == StudyTaskType.RETELL_WAWA_COURSE){
                if (type == OrganLibraryType.TYPE_LQCOURSE_SHOP || type == OrganLibraryType.TYPE_PRACTICE_LIBRARY|| type == OrganLibraryType.TYPE_LIBRARY
                        || type == OrganLibraryType.TYPE_BRAIN_LIBRARY|| type == OrganLibraryType.TYPE_TEACHING_PLAN) {
                    entities.add(entity);
                }
                if (entities.size() >= 5) return entities;
            } else if (taskType == StudyTaskType.WATCH_WAWA_COURSE){
                if (type == OrganLibraryType.TYPE_LQCOURSE_SHOP || type == OrganLibraryType.TYPE_PRACTICE_LIBRARY|| type == OrganLibraryType.TYPE_LIBRARY
                        || type == OrganLibraryType.TYPE_BRAIN_LIBRARY|| type == OrganLibraryType.TYPE_TEACHING_PLAN|| type == OrganLibraryType.TYPE_VIDEO_LIBRARY) {
                    entities.add(entity);
                }
                if (entities.size() >= 6) return entities;
            }
        }
        return entities;
    }



    public interface StudyTaskType {
        int WATCH_WAWA_COURSE = 0;
        int WATCH_RESOURCE = 1;
        int WATCH_HOMEWORK = 2;
        int SUBMIT_HOMEWORK = 3;
        int TOPIC_DISCUSSION = 4;
        int RETELL_WAWA_COURSE = 5;
        int INTRODUCTION_WAWA_COURSE = 6;
        int ENGLISH_WRITING = 7;
        int TASK_ORDER = 8;
        //新版看课件
        int NEW_WATACH_WAWA_COURSE = 9;
        //听说+读写
        int LISTEN_READ_AND_WRITE = 10;
        //综合任务
        int SUPER_TASK = 11;
        //听说课多选
        int MULTIPLE_RETELL_COURSE = 12;
        //读写单多选
        int MULTIPLE_TASK_ORDER = 13;
        //q配音
        int Q_DUBBING = 14;
        //多选配音
        int MULTIPLE_Q_DUBBING = 15;
        //其他多选
        int MULTIPLE_OTHER = 17;
        //其他多选(需提交)
        int MULTIPLE_OTHER_SUBMIT = 18;
    }
}
