package com.lqwawa.intleducation.module.discovery.vo;

import com.google.gson.annotations.SerializedName;
import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

import java.util.List;

public class SxExamDetailVo extends BaseVo {


        @SerializedName("id")
        public int id;
        @SerializedName("status")
        public int status;
        @SerializedName("sectionName")
        public String sectionName;
        @SerializedName("introduction")
        public String introduction;
        @SerializedName("isOpen")
        public boolean isOpen;
        @SerializedName("taskList")
        public List<TaskListVO> taskList;

        public class TaskListVO extends BaseVo{

            @SerializedName("taskType")
            public int taskType;
            @SerializedName("taskName")
            public String taskName;
            @SerializedName("data")
            public List<SectionResListVo> data;

            public int getTaskType() {
                return taskType;
            }

            public void setTaskType(int taskType) {
                this.taskType = taskType;
            }

            public String getTaskName() {
                return taskName;
            }

            public void setTaskName(String taskName) {
                this.taskName = taskName;
            }

            public List<SectionResListVo> getData() {
                return data;
            }

            public void setData(List<SectionResListVo> data) {
                this.data = data;
            }

            //            public class DetailVo {
//
//                @SerializedName("taskId")
//                public int taskId;
//                @SerializedName("isShield")
//                public boolean isShield;
//                @SerializedName("status")
//                public int status;
//                @SerializedName("assigned")
//                public boolean assigned;
//                @SerializedName("leStatus")
//                public int leStatus;
//                @SerializedName("resourceUrl")
//                public String resourceUrl;
//                @SerializedName("screenType")
//                public int screenType;
//                @SerializedName("resId")
//                public int resId;
//                @SerializedName("vuid")
//                public String vuid;
//                @SerializedName("resProperties")
//                public String resProperties;
//                @SerializedName("id")
//                public int id;
//                @SerializedName("point")
//                public String point;
//                @SerializedName("resPropType")
//                public int resPropType;
//                @SerializedName("thumbnail")
//                public String thumbnail;
//                @SerializedName("name")
//                public String name;
//                @SerializedName("viewCount")
//                public int viewCount;
//                @SerializedName("resType")
//                public int resType;
//                @SerializedName("originName")
//                public String originName;
//                @SerializedName("createId")
//                public String createId;
//                @SerializedName("linkCourseId")
//                public int linkCourseId;
//                @SerializedName("linkCourseName")
//                public String linkCourseName;
//                @SerializedName("score")
//                public String score;
//                @SerializedName("isRead")
//                private boolean isRead;
//                @SerializedName("taskType")
//                public int taskType;
//
//                public boolean isIsRead() {
//                    return isRead || status == 1;
//                }
//            }
        }

    @Override
    public String toString() {
        return "SxExamDetailVo{" +
                "id=" + id +
                ", status=" + status +
                ", sectionName='" + sectionName + '\'' +
                ", introduction='" + introduction + '\'' +
                ", isOpen=" + isOpen +
                ", taskList=" + taskList +
                '}';
    }
}
