package com.lqwawa.intleducation.factory.data.entity.course;

import com.lqwawa.intleducation.base.vo.BaseVo;
import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

/**
 * @author: wangchao
 * @date: 2019/05/07
 * @desc:
 */
public class VideoResourceEntity extends BaseVo {

    /**
     * createTime : Wed Apr 24 09:58:14 BRT 2019
     * courseId : 1086
     * type : 1
     * resourceUrl : http://resop.lqwawa.com/d5/ppt/2018/12/08/9ebaad93-a15c-4259-88bf-095895d6f9dd.pptx
     * resId : 14159
     * id : 132
     * isDelete : false
     * chapterId : 34489
     * saveName : lqwawa 学习任务-家长端听说课
     * thumbnailUrl :
     * name : lqwawa 学习任务-家长端听说课
     * createId : 24f26eb8-adcf-431b-92fa-a782211cf648
     * resType : 5
     * originName : lqwawa 学习任务-家长端听说课
     */

    private String createTime;
    private int courseId;
    private int type;
    private String resourceUrl;
    private int resId;
    private int id;
    private boolean isDelete;
    private int chapterId;
    private String saveName;
    private String thumbnailUrl;
    private String name;
    private String createId;
    private int resType;
    private String originName;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public int getResType() {
        return resType;
    }

    public void setResType(int resType) {
        this.resType = resType;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public SectionResListVo buildSectionResListVo() {
        SectionResListVo vo = new SectionResListVo();
        vo.setName(name);
        vo.setCreateId(createId);
        vo.setResId(String.valueOf(resId));
        vo.setResType(resType);
        vo.setResourceUrl(resourceUrl);
        vo.setOriginName(originName);
        return vo;
    }
}
