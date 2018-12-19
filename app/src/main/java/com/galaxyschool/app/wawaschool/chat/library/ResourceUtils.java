package com.galaxyschool.app.wawaschool.chat.library;

import android.text.TextUtils;
import com.galaxyschool.app.wawaschool.pojo.CourseInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.ResType;
import com.galaxyschool.app.wawaschool.pojo.ResourceType;
import com.galaxyschool.app.wawaschool.pojo.weike.CourseSourceType;
import com.oosic.apps.aidl.CollectParams;
import com.oosic.apps.share.SharedResource;

public class ResourceUtils {

    public static int getResourceType(SharedResource resource) {
        if (SharedResource.RESOURCE_TYPE_STREAM.equals(resource.getType())) {
            return ResourceType.VIDEO;
        } else if (SharedResource.RESOURCE_TYPE_FILE.equals(resource.getType())) {
            return ResourceType.CHW;
        } else if (SharedResource.RESOURCE_TYPE_HTML.equals(resource.getType())) {
            return ResourceType.WEB;
        }
        return 0;
    }

    public static CourseInfo toCourseInfo(SharedResource resource) {
        CourseInfo result = new CourseInfo();
        result.setNickname(resource.getTitle());
        try {
            result.setId(Integer.valueOf(resource.getId()));
        } catch (NumberFormatException e) {

        }
        result.setDescription(resource.getDescription());
        result.setImgurl(resource.getThumbnailUrl());
        result.setCreatename(resource.getAuthorName());
        result.setResourceurl(resource.getUrl());
//        result.setResourceType(getResourceType(resource));
        result.setResourceType(resource.getResourceType());
        result.setType(resource.getResourceType());
        result.setPrimaryKey(resource.getPrimaryKey());
        result.setSourceType(resource.getSourceType());
        result.setScreenType(resource.getScreenType());
        result.setCode(resource.getAuthorId());
        result.setCreatetime(resource.getCreateTime());
        result.setUpdateTime(resource.getUpdateTime());
        if(!TextUtils.isEmpty(resource.getShareUrl())) {
            result.setShareAddress(resource.getShareUrl());
        }
        result.setIsSlide(false);
        result.setCourseSourceType(CourseSourceType.CHAT);
        int resType = resource.getResourceType() % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            result.setIsSlide(true);
        }
        if(resource.getResourceType() > ResType.RES_TYPE_BASE) {
            result.setIsSplitCourse(true);
        }
        return result;
    }

    public static CollectParams toCollectParams(SharedResource resource) {
        CollectParams result = new CollectParams();
        result.microId = resource.getId();
        result.thumbnail = resource.getThumbnailUrl();
        result.title = resource.getTitle();
        result.author = resource.getAuthorId();
        result.description = resource.getDescription();
        result.knowledge = resource.getKnowledge();
        result.resourceUrl = resource.getUrl();
        result.author = resource.getAuthorId();
        result.resourceType = getResourceType(resource);
        result.primaryKey = resource.getPrimaryKey();
        result.sourceType = resource.getSourceType();
        return result;
    }

    public static NewResourceInfo toNewResourceInfo(SharedResource resource) {
        NewResourceInfo result = new NewResourceInfo();
        result.setTitle(resource.getTitle());
        result.setMicroId(resource.getId());
        result.setDescription(resource.getDescription());
        result.setThumbnail(resource.getThumbnailUrl());
        result.setAuthorName(resource.getAuthorName());
        result.setResourceUrl(resource.getUrl());
        result.setResourceType(resource.getResourceType());
        result.setType(resource.getResourceType());
        result.setScreenType(resource.getScreenType());
        result.setAuthorId(resource.getAuthorId());
        result.setCreatedTime(resource.getCreateTime());
        result.setUpdatedTime(resource.getUpdateTime());
        if(!TextUtils.isEmpty(resource.getShareUrl())) {
            result.setShareAddress(resource.getShareUrl());
        }
        resource.setFileSize(resource.getFileSize());
        return result;
    }


}
