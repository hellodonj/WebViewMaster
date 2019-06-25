package com.galaxyschool.app.wawaschool.pojo.weike;


import android.app.Activity;
import android.text.TextUtils;

import com.galaxyschool.app.wawaschool.R;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.StudyTaskInfo;
import com.lqwawa.intleducation.module.learn.vo.TaskUploadBackVo;
import com.oosic.apps.share.ShareInfo;
import com.oosic.apps.share.SharedResource;
import com.umeng.socialize.media.UMImage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pp on 15/7/30.
 */
public class CourseData implements Serializable {
    public int subject;
    public int collectioncount;
    public int courseporperty;
    public int parentid;
    public int type;
    public int id;
    public String createtime;
    public String level;
    public String thumbnailurl;
    public String resourceurl;
    public String setexcellentcoursename;
    public String description;
    public String savename;
    public int grade;
    public int totaltime;
    public int coursetype;
    public String imgurl;
    public String setexcellentcoursedate;
    public boolean isexcellentcourse;
    public int status;
    public String nickname;
    public boolean isdelete;
    public int viewcount;
    public String originname;
    public String code;
    public int downloadtimes;
    public int size;
    public String unit;
    public String point;
    public String createid;
    public int textbooksversion;
    public int setexcellentcourseid;
    public String createname;
    public String groupscode;
    public int fascicule;
    public int commentnum;
    public int praisenum;
    public String shareurl;
    public int screentype;

    public String resproperties;//1表示自动批阅

    public String levelId;
    public String gradeId;
    public String subjectId;
    public String createaccount;

    public String resId;
    //设置学习任务
    private StudyTaskInfo studyTaskInfo;

    //乐视视频的leValue
    private String leValue;
    private int leStatus;
    private boolean isPublicRes = true;
    //收藏过来的原始schoolId
    private String CollectionOrigin;
    private int splitFlag;//判断是否需要拆分 0 (不需要) 1(需要)
    private int sourceFromType;//资源的来自模块
    //教辅材料（任务单类型的值为1）
    public int guidanceCardSendFlag;
    private List<String> mp3List;

    public List<String> getMp3List() {
        return mp3List;
    }

    public void setMp3List(List<String> mp3List) {
        this.mp3List = mp3List;
    }

    public String getResproperties() {
        return resproperties;
    }

    public void setResproperties(String resproperties) {
        this.resproperties = resproperties;
    }

    public int getGuidanceCardSendFlag() {
        return guidanceCardSendFlag;
    }

    public void setGuidanceCardSendFlag(int guidanceCardSendFlag) {
        this.guidanceCardSendFlag = guidanceCardSendFlag;
    }

    public int getSourceFromType() {
        return sourceFromType;
    }

    public void setSourceFromType(int sourceFromType) {
        this.sourceFromType = sourceFromType;
    }


    public int getSplitFlag() {
        return splitFlag;
    }

    public void setSplitFlag(int splitFlag) {
        this.splitFlag = splitFlag;
    }

    public int getLeStatus() {
        return leStatus;
    }

    public void setLeStatus(int leStatus) {
        this.leStatus = leStatus;
    }

    public boolean isPublicRes() {
        return isPublicRes;
    }

    public String getCollectionOrigin() {
        return CollectionOrigin;
    }

    public void setCollectionOrigin(String collectionOrigin) {
        CollectionOrigin = collectionOrigin;
    }

    public void setIsPublicRes(boolean publicRes) {
        isPublicRes = publicRes;
    }

    public String getResId() {
        return resId;
    }

    public String getLeValue() {
        return leValue;
    }

    public void setLeValue(String leValue) {
        this.leValue = leValue;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public void setStudyTaskInfo(StudyTaskInfo studyTaskInfo) {
        this.studyTaskInfo = studyTaskInfo;
    }

    public StudyTaskInfo getStudyTaskInfo() {
        return studyTaskInfo;
    }

    public ShareInfo getShareInfo(Activity activity) {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle(nickname);
        if(!TextUtils.isEmpty(createname)) {
            shareInfo.setContent(createname);
        } else {
            shareInfo.setContent(" ");
        }

//        int resType = type % ResType.RES_TYPE_BASE;
//         if(resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER) {
//            UMVideo umVideo = new UMVideo(shareurl);
//            umVideo.setThumb(imgurl);
//            shareInfo.setuMediaObject(umVideo);
//        } else {
//             shareInfo.setTargetUrl(shareurl);
//             UMImage umImage;
//             if(!TextUtils.isEmpty(imgurl)) {
//                 umImage = new UMImage(activity, imgurl);
//             } else {
//                 umImage = new UMImage(activity, R.drawable.ic_launcher);
//             }
//             shareInfo.setuMediaObject(umImage);
//         }
        shareInfo.setTargetUrl(shareurl);
        UMImage umImage;
        if(!TextUtils.isEmpty(imgurl)) {
            umImage = new UMImage(activity, imgurl);
        } else {
            umImage = new UMImage(activity, R.drawable.ic_launcher);
        }
        shareInfo.setuMediaObject(umImage);
        shareInfo.setSharedResource(getSharedResource());
        if (type == ResType.RES_TYPE_ONEPAGE
                || type == ResType.RES_TYPE_COURSE_SPEAKER){
            String typeName = activity.getString(R.string.retell_course);
            nickname = activity.getString(R.string.str_resources_tag,typeName,nickname);
            shareInfo.setTitle(nickname);
            if (!TextUtils.isEmpty(createname)) {
                String content = createname;
                if (content.length() > 10){
                    content = content.substring(0,10);
                    content = content + "...";
                }
//                content = content + "\n" + activity.getString(R.string
//                        .Str_view_people, String.valueOf(viewcount));
                shareInfo.setContent(content);
            }
        }
        return shareInfo;
    }

    public SharedResource getSharedResource() {
        SharedResource resource = new SharedResource();
        resource.setId(String.valueOf(id));
        int resType = type % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_COURSE || resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            resource.setType(SharedResource.RESOURCE_TYPE_STREAM);
        } else if(resType == ResType.RES_TYPE_NOTE) {
            resource.setType(SharedResource.RESOURCE_TYPE_NOTE);
        } else if(resType == ResType.RES_TYPE_ONEPAGE) {
            resource.setType(SharedResource.RESOURCE_TYPE_FILE);
        }
        resource.setTitle(nickname);
        resource.setDescription(description);
        resource.setThumbnailUrl(imgurl);
        resource.setUrl(resourceurl);
        resource.setShareUrl(shareurl);
        resource.setAuthorId(code);
        resource.setAuthorName(createname);
        resource.setResourceType(type);
        resource.setCreateTime(createtime);
        resource.setScreenType(screentype);
        resource.setFileSize(size);
        resource.setUpdateTime(createtime);
        resource.setParentId(String.valueOf(parentid));
        resource.setIsPublicRescourse(isPublicRes);
        return resource;
    }

    public String getIdType() {
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        builder.append("-");
        builder.append(type);
        return builder.toString();
    }

    public CourseInfo getCourseInfo() {
        CourseInfo courseInfo = new CourseInfo();
        courseInfo.setNickname(nickname);
        courseInfo.setId(Integer.valueOf(id));
        courseInfo.setImgurl(imgurl);
        courseInfo.setCreatename(createname);
        courseInfo.setCode(code);
        courseInfo.setResourceurl(resourceurl);
        courseInfo.setPrimaryKey(String.valueOf(id));
        courseInfo.setResourceType(type);
        courseInfo.setType(type);
        courseInfo.setCreatetime(createtime);
        courseInfo.setUpdateTime(createtime);
        courseInfo.setShareAddress(shareurl);
        courseInfo.setIsSlide(false);
        int resType = type % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            courseInfo.setIsSlide(true);
        }
        if (type > ResType.RES_TYPE_BASE && resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            courseInfo.setIsSplitCourse(true);
        } else {
            courseInfo.setIsSplitCourse(false);
        }
        courseInfo.setScreenType(screentype);
        courseInfo.setIsPublicRescourse(isPublicRes);
        courseInfo.setParentId(String.valueOf(parentid));
        courseInfo.setCollectionOrigin(CollectionOrigin);
        courseInfo.setTotaltime(totaltime);//资源的时长
        courseInfo.setSourceFromType(sourceFromType);
        return courseInfo;
    }

    public NewResourceInfo getNewResourceInfo() {
        NewResourceInfo info = new NewResourceInfo();
        info.setTitle(nickname);
        info.setMicroId(String.valueOf(id));
        info.setThumbnail(thumbnailurl);
        info.setResourceType(type);
        info.setResourceUrl(resourceurl);
        info.setShareAddress(shareurl);
        info.setReadNumber(viewcount);
        info.setCommentNumber(commentnum);
        info.setPointNumber(praisenum);
        info.setAuthorId(code);
        info.setAuthorName(TextUtils.isEmpty(createname) ? createaccount : createname);
        info.setCreatedTime(createtime);
        info.setUpdatedTime(createtime);
        info.setFileSize(size);
        info.setScreenType(screentype);
        info.setDescription(description);
        info.setSplitFlag(splitFlag);
        info.setLeValue(leValue);
        info.setLeStatus(leStatus);

        //设置学习任务
        info.setStudyTaskInfo(studyTaskInfo);
        info.setIsPublicResource(isPublicRes);
        info.setParentId(String.valueOf(parentid));
        info.setCollectionOrigin(CollectionOrigin);
        info.setTotalTime(totaltime);//资源的时长
        info.setSourceFromType(sourceFromType);
        info.setResProperties(resproperties);
        return info;
    }

    public List<ShortCourseInfo> getShortCourseInfoList() {
        List<ShortCourseInfo> shortCourseInfos = new ArrayList<ShortCourseInfo>();
        ShortCourseInfo info = new ShortCourseInfo();
        info.setMicroId(getIdType());
        info.setTitle(getNickname());
        shortCourseInfos.add(info);
        return shortCourseInfos;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public static CourseData fromTaskUploadBackVo(TaskUploadBackVo taskUploadBackVo){
        CourseData data = new CourseData();
        data.type = taskUploadBackVo.getType();
        data.id = taskUploadBackVo.getId();
        data.thumbnailurl = taskUploadBackVo.getThumbnailurl();
        data.nickname = taskUploadBackVo.getNickname();
        data.originname = taskUploadBackVo.getOriginname();
        data.totaltime = taskUploadBackVo.getTotaltime();
        data.description = taskUploadBackVo.getDescription();
        data.createaccount = taskUploadBackVo.getCreateaccount();
        data.createname = taskUploadBackVo.getCreatename();
        data.createtime = taskUploadBackVo.getCreatetime();
        data.commentnum = taskUploadBackVo.getCommentnum();
        data.courseporperty = taskUploadBackVo.getCourseporperty();
        data.imgurl = taskUploadBackVo.getImgurl();
        data.shareurl = taskUploadBackVo.getShareurl();
        data.viewcount = taskUploadBackVo.getViewcount();
        data.level = taskUploadBackVo.getLevel();
        data.isdelete = taskUploadBackVo.isIsdelete();
        return data;
    }
}
