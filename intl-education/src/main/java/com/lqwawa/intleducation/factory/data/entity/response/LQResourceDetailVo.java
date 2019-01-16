package com.lqwawa.intleducation.factory.data.entity.response;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.io.Serializable;
import java.util.List;

/**
 * 做任务单，拿答题卡信息的网络请求返回接收数据实体
 */
public class LQResourceDetailVo extends BaseVo {
    public static final int SUCCEED = 0;

    /**
     * code : 0
     * data : [{"bookconcern":0,"catalog":"","catalogname":"","clientversion":"","code":"37985aee-a85a-47d9-84df-adb5ebb3320a","collectioncount":0,"commentnum":0,"courseporperty":0,"coursetype":23,"createaccount":"biu","createid":0,"createname":"biubiu","createtime":"2018-12-05 11:25:38","description":"","downloadtimes":0,"fascicule":0,"grade":0,"groupscode":"","id":711281,"imgurl":"http://resop.lqwawa.com/d5/course/daoxueka/2018/12/05/16111435-8e4e-417f-be34-09f571d43dd7/head.jpg?1543980337639s","isdelete":false,"isdownloadsuccess":false,"isexcellentcourse":false,"isverifyed":false,"language":0,"level":"","nickname":"biu试卷300","originname":"biu试卷300.lqdx","parentid":0,"point":"300","praisenum":0,"resourceurl":"http://resop.lqwawa.com/d5/course/daoxueka/2018/12/05/16111435-8e4e-417f-be34-09f571d43dd7.zip","resproperties":"","savename":"16111435-8e4e-417f-be34-09f571d43dd7.zip","screentype":0,"setexcellentcoursedate":"2018-12-05 11:25:22","setexcellentcourseid":0,"setexcellentcoursename":"","shareurl":"http://resop.lqwawa.com/weike/play?vId=711281&pType=23","size":1195026,"splitflag":1,"splitnum":0,"status":3,"subject":0,"syccomtime":"","taoxi":0,"textbooksversion":0,"thumbnailurl":"http://resop.lqwawa.com/d5/course/daoxueka/2018/12/05/16111435-8e4e-417f-be34-09f571d43dd7/head.jpg?1543980337639s","totaltime":0,"tvremoteurl":"","type":23,"unit":"","verifyname":"","verifyremark":"memberId:37985aee-a85a-47d9-84df-adb5ebb3320a,createName:biubiu,datetime:2018-12-05 11:25:37; ","verifytime":"2018-12-05 11:25:38","viewcount":33,"xueduan":0}]
     * exercise : [{"create_id":"37985aee-a85a-47d9-84df-adb5ebb3320a","create_name":"biubiu","create_time":"2018-12-5 11:25:23","exercise_item_list":[{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"42.7231968810916","page_index":"0","top":"68.8965517241379","width":"82.6900584795322"}],"index":"1","item_count":"4","name":"1","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.9","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"1","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"34.4541910331384","page_index":"0","top":"260.428965517241","width":"82.6900584795322"}],"index":"2","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.1","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"1","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"24.8070175438597","page_index":"0","top":"435.426206896552","width":"82.6900584795322"}],"index":"3","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"1,2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.9","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"2","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"30.3196881091618","page_index":"0","top":"629.714482758621","width":"82.6900584795322"}],"index":"4","item_count":"4","name":"4","right_answer":"2,3","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.1","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"2","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"1309.25925925926","page_index":"0","top":"267.318620689655","width":"82.6900584795322"}],"index":"5","item_count":"2","name":"5","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"3","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"1299.61208576998","page_index":"0","top":"409.245517241379","width":"82.6900584795322"}],"index":"6","item_count":"2","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"3","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"1058.43274853801","page_index":"0","top":"552.550344827586","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1284.4522417154","page_index":"0","top":"551.172413793103","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1070.83625730994","page_index":"0","top":"738.571034482759","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1299.61208576998","page_index":"0","top":"731.681379310345","width":"82.6900584795322"}],"index":"7","item_count":"4","name":"7","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.1,2.9,2.4,2.6","type":"4","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"6.89083820662768","page_index":"1","top":"107.478620689655","width":"82.6900584795322"},{"height":"82.6758620689655","left":"195.699805068226","page_index":"1","top":"117.124137931034","width":"82.6900584795322"},{"height":"82.6758620689655","left":"402.424951267057","page_index":"1","top":"124.013793103448","width":"84.0682261208577"},{"height":"82.6758620689655","left":"603.637426900585","page_index":"1","top":"136.415172413793","width":"82.6900584795322"}],"index":"8","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.2,2.3,2.7,2.8","type":"4","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"9.64717348927875","page_index":"1","top":"318.302068965517","width":"82.6900584795322"}],"index":"9","item_count":"4","name":"1","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"5.3","src_res_id":"13591","src_res_name":"120单选.pdf","src_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/e72cc918-4042-49fe-94ce-0956772e5b1b.pdf","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"5","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"208.103313840156","page_index":"1","top":"315.546206896552","width":"82.6900584795322"}],"index":"10","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"4.7","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"5","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"398.29044834308","page_index":"1","top":"289.365517241379","width":"68.9083820662768"}],"index":"11","item_count":"2","name":"3","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"4.3","src_res_id":"13592","src_res_name":"120单选.pdf","src_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/1c9688b8-9a28-41fc-8318-3f6a1e50c69a.pdf","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"6","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"596.746588693957","page_index":"1","top":"316.924137931034","width":"82.6900584795322"}],"index":"12","item_count":"2","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"5.7","src_res_id":"13593","src_res_name":"120单选.pdf","src_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/3c314279-8399-48f6-80f2-3035fcea5f83.pdf","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"6","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"42.7231968810916","page_index":"1","top":"566.329655172414","width":"82.6900584795322"},{"height":"82.6758620689655","left":"228.775828460039","page_index":"1","top":"566.329655172414","width":"82.6900584795322"},{"height":"82.6758620689655","left":"391.399610136452","page_index":"1","top":"562.195862068966","width":"82.6900584795322"},{"height":"82.6758620689655","left":"574.695906432749","page_index":"1","top":"566.329655172414","width":"82.6900584795322"}],"index":"13","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.8,2.2,2.3,2.7","type":"7","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"930.263157894737","page_index":"1","top":"533.259310344828","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1068.07992202729","page_index":"1","top":"537.393103448276","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1212.78752436647","page_index":"1","top":"538.771034482759","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1313.39376218324","page_index":"1","top":"540.148965517241","width":"82.6900584795322"}],"index":"14","item_count":"4","name":"6","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.1,2.2,2.8,2.9","type":"7","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"934.397660818713","page_index":"1","top":"655.895172413793","width":"82.6900584795322"}],"index":"15","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":{"answer_text":"a","item_index":"1"},"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"8","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"1216.92202729045","page_index":"1","top":"651.761379310345","width":"82.6900584795322"}],"index":"16","item_count":"4","name":"8","right_answer":{"answer_text":"b","item_index":"2"},"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"8","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"150.194482758621","left":"337.651072124756","page_index":"1","top":"775.775172413793","width":"289.415204678363"}],"index":"17","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.I believe that a returning customer is ________ important than a new customer.","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"120单选.pdf","right_answer_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/cc7dd6ae-b12a-4f8b-9529-6a1aa6989cc5.pdf","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"9","type_name":"zhuguan"},{"analysis":"","areaItemList":[{"height":"148.816551724138","left":"785.555555555556","page_index":"1","top":"784.04275862069","width":"330.760233918129"}],"index":"18","item_count":"4","name":"1","right_answer":"","right_answer_res_id":"","right_answer_res_name":"120单选.pdf","right_answer_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/7b19a369-63f9-4f47-8edf-d1b6453f78eb.pdf","score":"150","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"9","type_name":"zhuguan"}],"need_read_answer_area":"True","student_commit_time":"","student_id":"","student_name":"","student_score":"","total_score":"300"}]
     */

    private int code;
    private String message;
    private List<DataBean> data;
    private List<ExerciseBean> exercise;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public List<ExerciseBean> getExercise() {
        return exercise;
    }

    public void setExercise(List<ExerciseBean> exercise) {
        this.exercise = exercise;
    }

    /**
     * 网络请求是否成功
     * @return true 请求成功 false 请求失败
     */
    public boolean isSucceed(){
        return code == SUCCEED;
    }

    public static class DataBean implements Serializable {
        /**
         * bookconcern : 0
         * catalog :
         * catalogname :
         * clientversion :
         * code : 37985aee-a85a-47d9-84df-adb5ebb3320a
         * collectioncount : 0
         * commentnum : 0
         * courseporperty : 0
         * coursetype : 23
         * createaccount : biu
         * createid : 0
         * createname : biubiu
         * createtime : 2018-12-05 11:25:38
         * description :
         * downloadtimes : 0
         * fascicule : 0
         * grade : 0
         * groupscode :
         * id : 711281
         * imgurl : http://resop.lqwawa.com/d5/course/daoxueka/2018/12/05/16111435-8e4e-417f-be34-09f571d43dd7/head.jpg?1543980337639s
         * isdelete : false
         * isdownloadsuccess : false
         * isexcellentcourse : false
         * isverifyed : false
         * language : 0
         * level :
         * nickname : biu试卷300
         * originname : biu试卷300.lqdx
         * parentid : 0
         * point : 300
         * praisenum : 0
         * resourceurl : http://resop.lqwawa.com/d5/course/daoxueka/2018/12/05/16111435-8e4e-417f-be34-09f571d43dd7.zip
         * resproperties :
         * savename : 16111435-8e4e-417f-be34-09f571d43dd7.zip
         * screentype : 0
         * setexcellentcoursedate : 2018-12-05 11:25:22
         * setexcellentcourseid : 0
         * setexcellentcoursename :
         * shareurl : http://resop.lqwawa.com/weike/play?vId=711281&pType=23
         * size : 1195026
         * splitflag : 1
         * splitnum : 0
         * status : 3
         * subject : 0
         * syccomtime :
         * taoxi : 0
         * textbooksversion : 0
         * thumbnailurl : http://resop.lqwawa.com/d5/course/daoxueka/2018/12/05/16111435-8e4e-417f-be34-09f571d43dd7/head.jpg?1543980337639s
         * totaltime : 0
         * tvremoteurl :
         * type : 23
         * unit :
         * verifyname :
         * verifyremark : memberId:37985aee-a85a-47d9-84df-adb5ebb3320a,createName:biubiu,datetime:2018-12-05 11:25:37;
         * verifytime : 2018-12-05 11:25:38
         * viewcount : 33
         * xueduan : 0
         */

        private int bookconcern;
        private String catalog;
        private String catalogname;
        private String clientversion;
        private String code;
        private int collectioncount;
        private int commentnum;
        private int courseporperty;
        private int coursetype;
        private String createaccount;
        private int createid;
        private String createname;
        private String createtime;
        private String description;
        private int downloadtimes;
        private int fascicule;
        private int grade;
        private String groupscode;
        private int id;
        private String imgurl;
        private boolean isdelete;
        private boolean isdownloadsuccess;
        private boolean isexcellentcourse;
        private boolean isverifyed;
        private int language;
        private String level;
        private String nickname;
        private String originname;
        private int parentid;
        private String point;
        private int praisenum;
        private String resourceurl;
        private String resproperties;
        private String savename;
        private int screentype;
        private String setexcellentcoursedate;
        private int setexcellentcourseid;
        private String setexcellentcoursename;
        private String shareurl;
        private int size;
        private int splitflag;
        private int splitnum;
        private int status;
        private int subject;
        private String syccomtime;
        private int taoxi;
        private int textbooksversion;
        private String thumbnailurl;
        private int totaltime;
        private String tvremoteurl;
        private int type;
        private String unit;
        private String verifyname;
        private String verifyremark;
        private String verifytime;
        private int viewcount;
        private int xueduan;

        public int getBookconcern() {
            return bookconcern;
        }

        public void setBookconcern(int bookconcern) {
            this.bookconcern = bookconcern;
        }

        public String getCatalog() {
            return catalog;
        }

        public void setCatalog(String catalog) {
            this.catalog = catalog;
        }

        public String getCatalogname() {
            return catalogname;
        }

        public void setCatalogname(String catalogname) {
            this.catalogname = catalogname;
        }

        public String getClientversion() {
            return clientversion;
        }

        public void setClientversion(String clientversion) {
            this.clientversion = clientversion;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getCollectioncount() {
            return collectioncount;
        }

        public void setCollectioncount(int collectioncount) {
            this.collectioncount = collectioncount;
        }

        public int getCommentnum() {
            return commentnum;
        }

        public void setCommentnum(int commentnum) {
            this.commentnum = commentnum;
        }

        public int getCourseporperty() {
            return courseporperty;
        }

        public void setCourseporperty(int courseporperty) {
            this.courseporperty = courseporperty;
        }

        public int getCoursetype() {
            return coursetype;
        }

        public void setCoursetype(int coursetype) {
            this.coursetype = coursetype;
        }

        public String getCreateaccount() {
            return createaccount;
        }

        public void setCreateaccount(String createaccount) {
            this.createaccount = createaccount;
        }

        public int getCreateid() {
            return createid;
        }

        public void setCreateid(int createid) {
            this.createid = createid;
        }

        public String getCreatename() {
            return createname;
        }

        public void setCreatename(String createname) {
            this.createname = createname;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getDownloadtimes() {
            return downloadtimes;
        }

        public void setDownloadtimes(int downloadtimes) {
            this.downloadtimes = downloadtimes;
        }

        public int getFascicule() {
            return fascicule;
        }

        public void setFascicule(int fascicule) {
            this.fascicule = fascicule;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public String getGroupscode() {
            return groupscode;
        }

        public void setGroupscode(String groupscode) {
            this.groupscode = groupscode;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public boolean isIsdelete() {
            return isdelete;
        }

        public void setIsdelete(boolean isdelete) {
            this.isdelete = isdelete;
        }

        public boolean isIsdownloadsuccess() {
            return isdownloadsuccess;
        }

        public void setIsdownloadsuccess(boolean isdownloadsuccess) {
            this.isdownloadsuccess = isdownloadsuccess;
        }

        public boolean isIsexcellentcourse() {
            return isexcellentcourse;
        }

        public void setIsexcellentcourse(boolean isexcellentcourse) {
            this.isexcellentcourse = isexcellentcourse;
        }

        public boolean isIsverifyed() {
            return isverifyed;
        }

        public void setIsverifyed(boolean isverifyed) {
            this.isverifyed = isverifyed;
        }

        public int getLanguage() {
            return language;
        }

        public void setLanguage(int language) {
            this.language = language;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getOriginname() {
            return originname;
        }

        public void setOriginname(String originname) {
            this.originname = originname;
        }

        public int getParentid() {
            return parentid;
        }

        public void setParentid(int parentid) {
            this.parentid = parentid;
        }

        public String getPoint() {
            return point;
        }

        public void setPoint(String point) {
            this.point = point;
        }

        public int getPraisenum() {
            return praisenum;
        }

        public void setPraisenum(int praisenum) {
            this.praisenum = praisenum;
        }

        public String getResourceurl() {
            return resourceurl;
        }

        public void setResourceurl(String resourceurl) {
            this.resourceurl = resourceurl;
        }

        public String getResproperties() {
            return resproperties;
        }

        public void setResproperties(String resproperties) {
            this.resproperties = resproperties;
        }

        public String getSavename() {
            return savename;
        }

        public void setSavename(String savename) {
            this.savename = savename;
        }

        public int getScreentype() {
            return screentype;
        }

        public void setScreentype(int screentype) {
            this.screentype = screentype;
        }

        public String getSetexcellentcoursedate() {
            return setexcellentcoursedate;
        }

        public void setSetexcellentcoursedate(String setexcellentcoursedate) {
            this.setexcellentcoursedate = setexcellentcoursedate;
        }

        public int getSetexcellentcourseid() {
            return setexcellentcourseid;
        }

        public void setSetexcellentcourseid(int setexcellentcourseid) {
            this.setexcellentcourseid = setexcellentcourseid;
        }

        public String getSetexcellentcoursename() {
            return setexcellentcoursename;
        }

        public void setSetexcellentcoursename(String setexcellentcoursename) {
            this.setexcellentcoursename = setexcellentcoursename;
        }

        public String getShareurl() {
            return shareurl;
        }

        public void setShareurl(String shareurl) {
            this.shareurl = shareurl;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getSplitflag() {
            return splitflag;
        }

        public void setSplitflag(int splitflag) {
            this.splitflag = splitflag;
        }

        public int getSplitnum() {
            return splitnum;
        }

        public void setSplitnum(int splitnum) {
            this.splitnum = splitnum;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getSubject() {
            return subject;
        }

        public void setSubject(int subject) {
            this.subject = subject;
        }

        public String getSyccomtime() {
            return syccomtime;
        }

        public void setSyccomtime(String syccomtime) {
            this.syccomtime = syccomtime;
        }

        public int getTaoxi() {
            return taoxi;
        }

        public void setTaoxi(int taoxi) {
            this.taoxi = taoxi;
        }

        public int getTextbooksversion() {
            return textbooksversion;
        }

        public void setTextbooksversion(int textbooksversion) {
            this.textbooksversion = textbooksversion;
        }

        public String getThumbnailurl() {
            return thumbnailurl;
        }

        public void setThumbnailurl(String thumbnailurl) {
            this.thumbnailurl = thumbnailurl;
        }

        public int getTotaltime() {
            return totaltime;
        }

        public void setTotaltime(int totaltime) {
            this.totaltime = totaltime;
        }

        public String getTvremoteurl() {
            return tvremoteurl;
        }

        public void setTvremoteurl(String tvremoteurl) {
            this.tvremoteurl = tvremoteurl;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }

        public String getVerifyname() {
            return verifyname;
        }

        public void setVerifyname(String verifyname) {
            this.verifyname = verifyname;
        }

        public String getVerifyremark() {
            return verifyremark;
        }

        public void setVerifyremark(String verifyremark) {
            this.verifyremark = verifyremark;
        }

        public String getVerifytime() {
            return verifytime;
        }

        public void setVerifytime(String verifytime) {
            this.verifytime = verifytime;
        }

        public int getViewcount() {
            return viewcount;
        }

        public void setViewcount(int viewcount) {
            this.viewcount = viewcount;
        }

        public int getXueduan() {
            return xueduan;
        }

        public void setXueduan(int xueduan) {
            this.xueduan = xueduan;
        }
    }

    public static class ExerciseBean implements Serializable{
        /**
         * create_id : 37985aee-a85a-47d9-84df-adb5ebb3320a
         * create_name : biubiu
         * create_time : 2018-12-5 11:25:23
         * exercise_item_list : [{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"42.7231968810916","page_index":"0","top":"68.8965517241379","width":"82.6900584795322"}],"index":"1","item_count":"4","name":"1","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.9","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"1","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"34.4541910331384","page_index":"0","top":"260.428965517241","width":"82.6900584795322"}],"index":"2","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.1","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"1","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"24.8070175438597","page_index":"0","top":"435.426206896552","width":"82.6900584795322"}],"index":"3","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"1,2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.9","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"2","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"30.3196881091618","page_index":"0","top":"629.714482758621","width":"82.6900584795322"}],"index":"4","item_count":"4","name":"4","right_answer":"2,3","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.1","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"2","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"1309.25925925926","page_index":"0","top":"267.318620689655","width":"82.6900584795322"}],"index":"5","item_count":"2","name":"5","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"3","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"1299.61208576998","page_index":"0","top":"409.245517241379","width":"82.6900584795322"}],"index":"6","item_count":"2","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"3","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"1058.43274853801","page_index":"0","top":"552.550344827586","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1284.4522417154","page_index":"0","top":"551.172413793103","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1070.83625730994","page_index":"0","top":"738.571034482759","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1299.61208576998","page_index":"0","top":"731.681379310345","width":"82.6900584795322"}],"index":"7","item_count":"4","name":"7","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.1,2.9,2.4,2.6","type":"4","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"6.89083820662768","page_index":"1","top":"107.478620689655","width":"82.6900584795322"},{"height":"82.6758620689655","left":"195.699805068226","page_index":"1","top":"117.124137931034","width":"82.6900584795322"},{"height":"82.6758620689655","left":"402.424951267057","page_index":"1","top":"124.013793103448","width":"84.0682261208577"},{"height":"82.6758620689655","left":"603.637426900585","page_index":"1","top":"136.415172413793","width":"82.6900584795322"}],"index":"8","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.2,2.3,2.7,2.8","type":"4","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"9.64717348927875","page_index":"1","top":"318.302068965517","width":"82.6900584795322"}],"index":"9","item_count":"4","name":"1","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"5.3","src_res_id":"13591","src_res_name":"120单选.pdf","src_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/e72cc918-4042-49fe-94ce-0956772e5b1b.pdf","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"5","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"208.103313840156","page_index":"1","top":"315.546206896552","width":"82.6900584795322"}],"index":"10","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"4.7","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"5","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"398.29044834308","page_index":"1","top":"289.365517241379","width":"68.9083820662768"}],"index":"11","item_count":"2","name":"3","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"4.3","src_res_id":"13592","src_res_name":"120单选.pdf","src_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/1c9688b8-9a28-41fc-8318-3f6a1e50c69a.pdf","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"6","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"596.746588693957","page_index":"1","top":"316.924137931034","width":"82.6900584795322"}],"index":"12","item_count":"2","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":"2","right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"5.7","src_res_id":"13593","src_res_name":"120单选.pdf","src_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/3c314279-8399-48f6-80f2-3035fcea5f83.pdf","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"6","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"42.7231968810916","page_index":"1","top":"566.329655172414","width":"82.6900584795322"},{"height":"82.6758620689655","left":"228.775828460039","page_index":"1","top":"566.329655172414","width":"82.6900584795322"},{"height":"82.6758620689655","left":"391.399610136452","page_index":"1","top":"562.195862068966","width":"82.6900584795322"},{"height":"82.6758620689655","left":"574.695906432749","page_index":"1","top":"566.329655172414","width":"82.6900584795322"}],"index":"13","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.8,2.2,2.3,2.7","type":"7","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"930.263157894737","page_index":"1","top":"533.259310344828","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1068.07992202729","page_index":"1","top":"537.393103448276","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1212.78752436647","page_index":"1","top":"538.771034482759","width":"82.6900584795322"},{"height":"82.6758620689655","left":"1313.39376218324","page_index":"1","top":"540.148965517241","width":"82.6900584795322"}],"index":"14","item_count":"4","name":"6","right_answer":["a","b","c","d"],"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","student_answer":"","student_score":"0","student_subscore":"","subscore":"2.1,2.2,2.8,2.9","type":"7","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"82.6758620689655","left":"934.397660818713","page_index":"1","top":"655.895172413793","width":"82.6900584795322"}],"index":"15","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.","right_answer":{"answer_text":"a","item_index":"1"},"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"10.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"8","type_name":""},{"analysis":"","areaItemList":[{"height":"82.6758620689655","left":"1216.92202729045","page_index":"1","top":"651.761379310345","width":"82.6900584795322"}],"index":"16","item_count":"4","name":"8","right_answer":{"answer_text":"b","item_index":"2"},"right_answer_res_id":"","right_answer_res_name":"","right_answer_res_url":"","score":"9.5","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"8","type_name":""},{"analysis":"I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many ","areaItemList":[{"height":"150.194482758621","left":"337.651072124756","page_index":"1","top":"775.775172413793","width":"289.415204678363"}],"index":"17","item_count":"4","name":"I believe that a returning customer is ________ important than a new customer.I believe that a returning customer is ________ important than a new customer.","right_answer":"1","right_answer_res_id":"","right_answer_res_name":"120单选.pdf","right_answer_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/cc7dd6ae-b12a-4f8b-9529-6a1aa6989cc5.pdf","score":"10","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"9","type_name":"zhuguan"},{"analysis":"","areaItemList":[{"height":"148.816551724138","left":"785.555555555556","page_index":"1","top":"784.04275862069","width":"330.760233918129"}],"index":"18","item_count":"4","name":"1","right_answer":"","right_answer_res_id":"","right_answer_res_name":"120单选.pdf","right_answer_res_url":"http://resop.lqwawa.com/d5/pdf/2018/12/05/7b19a369-63f9-4f47-8edf-d1b6453f78eb.pdf","score":"150","src_res_id":"","src_res_name":"","src_res_url":"","src_text":"","student_answer":"","student_score":"0","student_subscore":"","subscore":"","type":"9","type_name":"zhuguan"}]
         * need_read_answer_area : True
         * student_commit_time :
         * student_id :
         * student_name :
         * student_score :
         * total_score : 300
         */

        private String create_id;
        private String create_name;
        private String create_time;
        private String need_read_answer_area;
        private String student_commit_time;
        private String student_id;
        private String student_name;
        private String student_score;
        private String total_score;
        private List<ExerciseItemListBean> exercise_item_list;

        public String getCreate_id() {
            return create_id;
        }

        public void setCreate_id(String create_id) {
            this.create_id = create_id;
        }

        public String getCreate_name() {
            return create_name;
        }

        public void setCreate_name(String create_name) {
            this.create_name = create_name;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getNeed_read_answer_area() {
            return need_read_answer_area;
        }

        public void setNeed_read_answer_area(String need_read_answer_area) {
            this.need_read_answer_area = need_read_answer_area;
        }

        public String getStudent_commit_time() {
            return student_commit_time;
        }

        public void setStudent_commit_time(String student_commit_time) {
            this.student_commit_time = student_commit_time;
        }

        public String getStudent_id() {
            return student_id;
        }

        public void setStudent_id(String student_id) {
            this.student_id = student_id;
        }

        public String getStudent_name() {
            return student_name;
        }

        public void setStudent_name(String student_name) {
            this.student_name = student_name;
        }

        public String getStudent_score() {
            return student_score;
        }

        public void setStudent_score(String student_score) {
            this.student_score = student_score;
        }

        public String getTotal_score() {
            return total_score;
        }

        public void setTotal_score(String total_score) {
            this.total_score = total_score;
        }

        public List<ExerciseItemListBean> getExercise_item_list() {
            return exercise_item_list;
        }

        public void setExercise_item_list(List<ExerciseItemListBean> exercise_item_list) {
            this.exercise_item_list = exercise_item_list;
        }

        public static class ExerciseItemListBean implements Serializable{
            /**
             * analysis : I believe that a returning customer is ________ important than a new customer. ○ much ○ most ○ more ○ many
             * areaItemList : [{"height":"82.6758620689655","left":"42.7231968810916","page_index":"0","top":"68.8965517241379","width":"82.6900584795322"}]
             * index : 1
             * item_count : 4
             * name : 1
             * right_answer : 1
             * right_answer_res_id :
             * right_answer_res_name :
             * right_answer_res_url :
             * score : 9.9
             * src_res_id :
             * src_res_name :
             * src_res_url :
             * src_text :
             * student_answer :
             * student_score : 0
             * student_subscore :
             * subscore :
             * type : 1
             * type_name :
             */

            private String analysis;
            //答案解析的资源id
            private String analysis_res_id;
            //答案解析的资源路径
            private String analysis_res_url;
            //答案解析的资源名字
            private String analysis_res_name;
            private String index;
            private String item_count;
            private String name;
            private String right_answer;
            private String right_answer_res_id;
            private String right_answer_res_name;
            private String right_answer_res_url;
            private String score;
            private String src_res_id;
            private String src_res_name;
            private String src_res_url;
            private String src_text;
            private String student_answer;
            private String student_score;
            private String student_subscore;
            private String subscore;
            private String type;
            private String type_name;
            private List<AreaItemListBean> areaItemList;

            public String getAnalysis() {
                return analysis;
            }

            public void setAnalysis(String analysis) {
                this.analysis = analysis;
            }

            public String getAnalysis_res_id() {
                return analysis_res_id;
            }

            public void setAnalysis_res_id(String analysis_res_id) {
                this.analysis_res_id = analysis_res_id;
            }

            public String getAnalysis_res_url() {
                return analysis_res_url;
            }

            public void setAnalysis_res_url(String analysis_res_url) {
                this.analysis_res_url = analysis_res_url;
            }

            public String getAnalysis_res_name() {
                return analysis_res_name;
            }

            public void setAnalysis_res_name(String analysis_res_name) {
                this.analysis_res_name = analysis_res_name;
            }

            public String getIndex() {
                return index;
            }

            public void setIndex(String index) {
                this.index = index;
            }

            public String getItem_count() {
                return item_count;
            }

            public void setItem_count(String item_count) {
                this.item_count = item_count;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getRight_answer() {
                return right_answer;
            }

            public void setRight_answer(String right_answer) {
                this.right_answer = right_answer;
            }

            public String getRight_answer_res_id() {
                return right_answer_res_id;
            }

            public void setRight_answer_res_id(String right_answer_res_id) {
                this.right_answer_res_id = right_answer_res_id;
            }

            public String getRight_answer_res_name() {
                return right_answer_res_name;
            }

            public void setRight_answer_res_name(String right_answer_res_name) {
                this.right_answer_res_name = right_answer_res_name;
            }

            public String getRight_answer_res_url() {
                return right_answer_res_url;
            }

            public void setRight_answer_res_url(String right_answer_res_url) {
                this.right_answer_res_url = right_answer_res_url;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getSrc_res_id() {
                return src_res_id;
            }

            public void setSrc_res_id(String src_res_id) {
                this.src_res_id = src_res_id;
            }

            public String getSrc_res_name() {
                return src_res_name;
            }

            public void setSrc_res_name(String src_res_name) {
                this.src_res_name = src_res_name;
            }

            public String getSrc_res_url() {
                return src_res_url;
            }

            public void setSrc_res_url(String src_res_url) {
                this.src_res_url = src_res_url;
            }

            public String getSrc_text() {
                return src_text;
            }

            public void setSrc_text(String src_text) {
                this.src_text = src_text;
            }

            public String getStudent_answer() {
                return student_answer;
            }

            public void setStudent_answer(String student_answer) {
                this.student_answer = student_answer;
            }

            public String getStudent_score() {
                return student_score;
            }

            public void setStudent_score(String student_score) {
                this.student_score = student_score;
            }

            public String getStudent_subscore() {
                return student_subscore;
            }

            public void setStudent_subscore(String student_subscore) {
                this.student_subscore = student_subscore;
            }

            public String getSubscore() {
                return subscore;
            }

            public void setSubscore(String subscore) {
                this.subscore = subscore;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getType_name() {
                return type_name;
            }

            public void setType_name(String type_name) {
                this.type_name = type_name;
            }

            public List<AreaItemListBean> getAreaItemList() {
                return areaItemList;
            }

            public void setAreaItemList(List<AreaItemListBean> areaItemList) {
                this.areaItemList = areaItemList;
            }

            public static class AreaItemListBean implements Serializable{
                /**
                 * height : 82.6758620689655
                 * left : 42.7231968810916
                 * page_index : 0
                 * top : 68.8965517241379
                 * width : 82.6900584795322
                 */

                private String height;
                private String left;
                private String page_index;
                private String top;
                private String width;

                public String getHeight() {
                    return height;
                }

                public void setHeight(String height) {
                    this.height = height;
                }

                public String getLeft() {
                    return left;
                }

                public void setLeft(String left) {
                    this.left = left;
                }

                public String getPage_index() {
                    return page_index;
                }

                public void setPage_index(String page_index) {
                    this.page_index = page_index;
                }

                public String getTop() {
                    return top;
                }

                public void setTop(String top) {
                    this.top = top;
                }

                public String getWidth() {
                    return width;
                }

                public void setWidth(String width) {
                    this.width = width;
                }
            }
        }
    }
}
