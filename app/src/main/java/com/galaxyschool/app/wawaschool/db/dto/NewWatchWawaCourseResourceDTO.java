package com.galaxyschool.app.wawaschool.db.dto;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by E450 on 2017/06/21.
 * 新版看课件资源
 */
@DatabaseTable(tableName = "new_watch_wawa_course_resource_dto")
public class NewWatchWawaCourseResourceDTO implements Serializable{

    @DatabaseField(id = true)
    private String taskId; // 任务主键

    @DatabaseField
    private String ids;  //条目ids

    @DatabaseField
    private boolean readAll; //是否全部阅读

    @DatabaseField
    private String studentId; // 区分学生

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public boolean isReadAll() {
        return readAll;
    }

    public void setReadAll(boolean readAll) {
        this.readAll = readAll;
    }
}
