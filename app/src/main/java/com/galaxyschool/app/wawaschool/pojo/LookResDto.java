package com.galaxyschool.app.wawaschool.pojo;

import android.text.TextUtils;
import com.lqwawa.client.pojo.ResourceInfo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by E450 on 2017/06/16.
 * 看课件资源
 */
public class LookResDto implements Serializable{

    private int Id;
    private int TaskId;
    private String ResId;  //资源id
    private String ResUrl; //资源url
    private String ResTitle; //资源标题
    private String CreateId;
    private String CreateName;
    private String CreateTime;
    private String UpdateId;
    private String UpdateName;
    private boolean Deleted;
    private String Author; //资源作者id
    private String imgPath;
    private List<ResourceInfo> SplitInfoList;
    private String authorName;
    private String ResProperties;
    private int ResCourseId;
    private boolean isSelect;
    private String Point;
    private int ResPropType;
    private int completionMode;//0 复述课件 1 复述课件+语音评测

    public int getCompletionMode() {
        return completionMode;
    }

    public void setCompletionMode(int completionMode) {
        this.completionMode = completionMode;
    }

    public int getResPropType() {
        return ResPropType;
    }

    public void setResPropType(int resPropType) {
        ResPropType = resPropType;
    }

    public String getPoint() {
        return Point;
    }

    public void setPoint(String point) {
        Point = point;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean select) {
        isSelect = select;
    }

    public int getResCourseId() {
        return ResCourseId;
    }

    public void setResCourseId(int resCourseId) {
        ResCourseId = resCourseId;
    }

    public String getResProperties() {
        return ResProperties;
    }

    public void setResProperties(String resProperties) {
        ResProperties = resProperties;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public List<ResourceInfo> getSplitInfoList() {
        return SplitInfoList;
    }

    public void setSplitInfoList(List<ResourceInfo> splitInfoList) {
        SplitInfoList = splitInfoList;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getAuthor() {
        return Author;
    }

    /**
     * 转换为看课件资源对象
     * @return
     */
    public ResourceInfoTag toResourceInfoTag() {
        int resourceType = getResourceType();
        if (resourceType >= 0) {
            if (isPictureSet(resourceType)) {
             //处理图片集
                return getPictureSetResourceInfoTag(resourceType);
            } else {
                //处理单个对象
                return getSingleResourceInfoTag(resourceType);
            }
        }
        return null;
    }

    public boolean isPictureSet(int resourceType){
        boolean isPictureSet = false;
        if (resourceType == MaterialResourceType.PICTURE){
            isPictureSet = true;
        }
        return isPictureSet;
    }

    /**
     * 单个对象
     * @param resourceType
     * @return
     */
    private ResourceInfoTag getSingleResourceInfoTag(int resourceType) {
        ResourceInfoTag tag = new ResourceInfoTag();
        tag.setTitle(ResTitle);
        tag.setResId(ResId);
        tag.setImgPath(ResUrl);
        tag.setResourcePath(ResUrl);
        tag.setTaskId(String.valueOf(TaskId));
        tag.setId(String.valueOf(Id));
        //资源类型
        tag.setResourceType(resourceType);
        //阅读标识
        tag.setHasRead(false);
        //资源作者id
        tag.setAuthorId(Author);
        tag.setResCourseId(ResCourseId);
        return tag;
    }

    /**
     * 图片集
     * @param resourceType
     * @return
     */
    private ResourceInfoTag getPictureSetResourceInfoTag(int resourceType) {
        //ResId、ResUrl、ResTitle都是逗号分割的,上传的时候都是同步的，数量相等的。
        ResourceInfoTag tag = new ResourceInfoTag();
        tag.setTaskId(String.valueOf(TaskId));
        tag.setId(String.valueOf(Id));
        //阅读标识
        tag.setHasRead(false);
        tag.setResourceType(resourceType);
        tag.setResCourseId(ResCourseId);
        List<String> resIdList = splitPictureSetData(ResId);
        List<String> resUrlList = splitPictureSetData(ResUrl);
        List<String> resTitleList = splitPictureSetData(ResTitle);
        List<String> authorIdList = splitPictureSetData(Author);

        //判断是否是有效的上传资源list
        boolean validUploadedList = (resIdList != null && resIdList.size() > 0)
                && (resUrlList != null && resUrlList.size() > 0)
                && (resTitleList != null && resTitleList.size() > 0)
                && (authorIdList != null && authorIdList.size() > 0);
        if (validUploadedList){
            //图集
            List<ResourceInfo> resourceInfoList = new ArrayList<>();
            int size = resIdList.size();
            for (int i = 0 ; i < size ; i++) {
                String resId = resIdList.get(i);
                String resUrl = resUrlList.get(i);
                String resTitle = resTitleList.get(i);
                String authorId = authorIdList.get(i);
                //resId
                if (i == 0){
                    //取第一张数据作为封面数据
                    tag.setResId(resId);
                    tag.setImgPath(resUrl);
                    tag.setResourcePath(resUrl);
                    tag.setTitle(resTitle);
                    //资源作者id
                    tag.setAuthorId(authorId);
                }
                ResourceInfo resourceInfo = new ResourceInfo();
                //资源类型
                resourceInfo.setResourceType(resourceType);
                resourceInfo.setResId(resId);
                resourceInfo.setImgPath(resUrl);
                resourceInfo.setResourcePath(resUrl);
                resourceInfo.setTitle(resTitle);
                //资源作者id
                resourceInfo.setAuthorId(authorId);
                resourceInfoList.add(resourceInfo);
            }
            //装载图片集
            tag.setSplitInfoList(resourceInfoList);
        }
        return tag;
    }

    /**
     * 分割图片集数据
     * @param target
     * @return
     */
    private List<String> splitPictureSetData(String target){
        if (!TextUtils.isEmpty(target)){
            if (target.contains(",")){
                String[] splitArray = target.split(",");
                if (splitArray != null && splitArray.length > 0){
                    List <String> resultList = Arrays.asList(splitArray);
                    return resultList;
                }
            }else {
                //单张图片
                List <String> resultList = new ArrayList<>();
                resultList.add(target);
                return resultList;
            }
        }
        return null;
    }


    /**
     * 查找是否是图片集
     * @return
     */
    private int getResourceType() {
        int type = -1;
        if (!TextUtils.isEmpty(ResId)){
            if (ResId.contains("-")){
                type = Integer.parseInt(ResId.substring(ResId.lastIndexOf("-") + 1));
            }
        }
        return type;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public String getCreateId() {
        return CreateId;
    }

    public void setCreateId(String createId) {
        CreateId = createId;
    }

    public String getCreateName() {
        return CreateName;
    }

    public void setCreateName(String createName) {
        CreateName = createName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getUpdateId() {
        return UpdateId;
    }

    public void setUpdateId(String updateId) {
        UpdateId = updateId;
    }

    public String getUpdateName() {
        return UpdateName;
    }

    public void setUpdateName(String updateName) {
        UpdateName = updateName;
    }

    public boolean isDeleted() {
        return Deleted;
    }

    public void setDeleted(boolean deleted) {
        Deleted = deleted;
    }

    public void setResTitle(String resTitle) {
        ResTitle = resTitle;
    }

    public String getResTitle() {
        return ResTitle;
    }

    public void setResId(String resId) {
        ResId = resId;
    }

    public String getResId() {
        return ResId;
    }

    public void setResUrl(String resUrl) {
        ResUrl = resUrl;
    }

    public String getResUrl() {
        return ResUrl;
    }
}
