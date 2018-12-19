package com.galaxyschool.app.wawaschool.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wangchao
 * @date: 2017/06/02 11:15
 */

public class CourseSectionData {

    /**
     * progress : 10
     * weekCount : 10
     * price : 0
     * examList : []
     * progressStatus : 2
     * thumbnailUrl : http://lqwwres2.lqwawa.com/image/2017/03/15/04d50a95-884d-4f6a-846e-8bc70f3ee718.jpg
     * chapList : [{"id":2236,"sectionList":[{"id":2240,"resList":[{"id":2245,"name":"外研社小学英语一年级上册_1.lqdx","weekNum":1,"type":3,"resId":231227}],"name":"How are you?","weekNum":1,"type":2}],"name":"第一单元","weekNum":1,"taskList":[{"id":1254,"paperId":231227,"paperName":"外研社小学英语一年级上册_1"}],"type":1}]
     * payType : 0
     * courseId : 349
     * teachersName : 张珂
     * organName : 青岛两栖蛙蛙信息技术有限公司
     * courseName : 外研社小学英语一年级上册
     */

    private int progress;
    private int weekCount;
    private int price;
    private int progressStatus;
    private String thumbnailUrl;
    private int payType;
    private int courseId;
    private String teachersName;
    private String organName;
    private String courseName;
    private List<?> examList;
    private List<ChapList> chapList;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getWeekCount() {
        return weekCount;
    }

    public void setWeekCount(int weekCount) {
        this.weekCount = weekCount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(int progressStatus) {
        this.progressStatus = progressStatus;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getTeachersName() {
        return teachersName;
    }

    public void setTeachersName(String teachersName) {
        this.teachersName = teachersName;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<?> getExamList() {
        return examList;
    }

    public void setExamList(List<?> examList) {
        this.examList = examList;
    }

    public List<ChapList> getChapList() {
        return chapList;
    }

    public void setChapList(List<ChapList> chapList) {
        this.chapList = chapList;
    }

    public static class ChapList {
        /**
         * id : 2236
         * sectionList : [{"id":2240,"resList":[{"id":2245,"name":"外研社小学英语一年级上册_1.lqdx","weekNum":1,"type":3,"resId":231227}],"name":"How are you?","weekNum":1,"type":2}]
         * name : 第一单元
         * weekNum : 1
         * taskList : [{"id":1254,"paperId":231227,"paperName":"外研社小学英语一年级上册_1"}]
         * type : 1
         */

        private int id;
        private String name;
        private int weekNum;
        private int type;
        private List<SectionList> sectionList;
        private List<TaskList> taskList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWeekNum() {
            return weekNum;
        }

        public void setWeekNum(int weekNum) {
            this.weekNum = weekNum;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<SectionList> getSectionList() {
            return sectionList;
        }

        public void setSectionList(List<SectionList> sectionList) {
            this.sectionList = sectionList;
        }

        public List<TaskList> getTaskList() {
            return taskList;
        }

        public void setTaskList(List<TaskList> taskList) {
            this.taskList = taskList;
        }

        public static class SectionList {
            /**
             * id : 2240
             * resList : [{"id":2245,"name":"外研社小学英语一年级上册_1.lqdx","weekNum":1,"type":3,"resId":231227}]
             * name : How are you?
             * weekNum : 1
             * type : 2
             */

            private int id;
            private String name;
            private int weekNum;
            private int type;
            private List<ResListBean> resList;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getWeekNum() {
                return weekNum;
            }

            public void setWeekNum(int weekNum) {
                this.weekNum = weekNum;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public List<ResListBean> getResList() {
                return resList;
            }

            public void setResList(List<ResListBean> resList) {
                this.resList = resList;
            }

            public static class ResListBean {
                /**
                 * id : 2245
                 * name : 外研社小学英语一年级上册_1.lqdx
                 * weekNum : 1
                 * type : 3
                 * resId : 231227
                 */

                private int id;
                private String name;
                private int weekNum;
                private int type;
                private int resId;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getWeekNum() {
                    return weekNum;
                }

                public void setWeekNum(int weekNum) {
                    this.weekNum = weekNum;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public int getResId() {
                    return resId;
                }

                public void setResId(int resId) {
                    this.resId = resId;
                }
            }
        }

        public static class TaskList {
            /**
             * id : 1254
             * paperId : 231227
             * paperName : 外研社小学英语一年级上册_1
             */

            private int id;
            private int paperId;
            private String paperName;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPaperId() {
                return paperId;
            }

            public void setPaperId(int paperId) {
                this.paperId = paperId;
            }

            public String getPaperName() {
                return paperName;
            }

            public void setPaperName(String paperName) {
                this.paperName = paperName;
            }
        }
    }
}
