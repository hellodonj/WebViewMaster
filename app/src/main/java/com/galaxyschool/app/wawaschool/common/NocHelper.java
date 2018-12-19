package com.galaxyschool.app.wawaschool.common;

import android.app.Activity;
import android.os.Bundle;

import com.galaxyschool.app.wawaschool.PictureBooksDetailActivity;
import com.galaxyschool.app.wawaschool.pojo.MediaInfo;
import com.galaxyschool.app.wawaschool.pojo.NewResourceInfo;
import com.galaxyschool.app.wawaschool.pojo.NocEnterDetailArguments;
import com.galaxyschool.app.wawaschool.pojo.NocLePlayHelper;
import com.galaxyschool.app.wawaschool.pojo.ResType;

import org.json.JSONObject;

/**
 * Created by E450 on 2017/7/26.
 */

public class NocHelper {
    public static NewResourceInfo pase2NewResourceInfo(JSONObject object){
        if(object==null){
            return null;
        }
        NewResourceInfo item = new NewResourceInfo();
        item.setTitle(object.optString("name"));
        item.setThumbnail(object.optString("resThumbnailUrl"));
        item.setResourceType(object.optInt("resType"));
        item.setResourceUrl(object.optString("resourceUrl"));
        item.setVuid(object.optString("vuid"));
        item.setLeStatus(object.optInt("leStatus"));
        item.setId(object.opt("id").toString());
        item.setMicroId(object.opt("resId").toString());
        item.setNocCreateTime(object.optString("createTime"));
        item.setNocEntryNum(object.optString("entryNum"));
        item.setNocOrgName(object.optString("orgName"));
        item.setNocRemark(object.optString("remark"));
//        item.setAuthorName(object.optString("createName"));
        item.setAuthorName(object.optString("realName"));
        item.setNocNameForType(object.optInt("type"));
        item.setNocPraiseNum(object.optInt("praiseNum"));
        item.setResourceUrl(object.optString("resourceUrl"));
        return item;
    }

    public static MediaInfo pase2MediaInfo(JSONObject object){
        if(object==null){
            return null;
        }
        MediaInfo item=new MediaInfo();
        item.setTitle(object.optString("name"));
        item.setThumbnail(object.optString("resThumbnailUrl"));
        item.setResourceType(object.optInt("resType"));
        item.setResourceUrl(object.optString("resourceUrl"));
        item.setVuid(object.optString("vuid"));
        item.setLeStatus(object.optInt("leStatus"));
        item.setId(object.opt("id").toString());
        item.setMicroId(object.opt("resId").toString());
        item.setNocCreateTime(object.optString("createTime"));
        item.setNocEntryNum(object.optString("entryNum"));
        item.setNocOrgName(object.optString("orgName"));
        item.setNocRemark(object.optString("remark"));
//        item.setAuthor(object.optString("createName"));
        item.setAuthor(object.optString("realName"));
        item.setNocNameForType(object.optInt("type"));
        item.setNocPraiseNum(object.optInt("praiseNum"));
        item.setResourceUrl(object.optString("resourceUrl"));
        return item;
    }

    public static void prepareEnterNocDetail( NewResourceInfo data,Activity activity ){
        NocEnterDetailArguments nocArgs=new NocEnterDetailArguments();
        nocArgs.setCreateTime(data.getNocCreateTime());
        nocArgs.setEntryNum(data.getNocEntryNum());
        nocArgs.setOrgName(data.getNocOrgName());
        nocArgs.setRemark(data.getNocRemark());
        nocArgs.setAuthor(data.getAuthorName());
        nocArgs.setTitle(data.getTitle());
        nocArgs.setCourseId(data.getMicroId());
        nocArgs.setNocNameForType(data.getNocNameForType());
        nocArgs.setNocPraiseNum(data.getNocPraiseNum());
        nocArgs.setResourceUrl(data.getResourceUrl());
        nocArgs.setLeStatus(data.getLeStatus());
        if(data.isMicroCourse()||data.isOnePage()){
            ActivityUtils.openCourseDetail(activity,
                    PictureBooksDetailActivity.FROM_OTHRE,data.getMicroId(),data
                            .getResourceType(),nocArgs);
        } else if (data.getResourceType() == ResType.RES_TYPE_VIDEO ) {
            NocLePlayHelper.startLeVideoPlay(activity, data.getVuid(), "b68e945493",
                    nocArgs);
        }
    }

    public static void prepareEnterNocDetail2( MediaInfo data,Activity activity ){
        NocEnterDetailArguments nocArgs=new NocEnterDetailArguments();
        nocArgs.setCreateTime(data.getNocCreateTime());
        nocArgs.setEntryNum(data.getNocEntryNum());
        nocArgs.setOrgName(data.getNocOrgName());
        nocArgs.setRemark(data.getNocRemark());
        nocArgs.setAuthor(data.getAuthor());
        nocArgs.setTitle(data.getTitle());
        nocArgs.setCourseId(data.getMicroId());
        nocArgs.setNocNameForType(data.getNocNameForType());
        nocArgs.setNocPraiseNum(data.getNocPraiseNum());
        nocArgs.setResourceUrl(data.getResourceUrl());
        nocArgs.setLeStatus(data.getLeStatus());
        int resType = data.getResourceType() % ResType.RES_TYPE_BASE;
        if(resType == ResType.RES_TYPE_ONEPAGE||resType == ResType.RES_TYPE_COURSE_SPEAKER){
            ActivityUtils.openCourseDetail(activity, PictureBooksDetailActivity.FROM_OTHRE,data.getMicroId(),data
                            .getResourceType(),nocArgs);
        } else if (data.getResourceType() == ResType.RES_TYPE_VIDEO ) {
            NocLePlayHelper.startLeVideoPlay(activity, data.getVuid(), "b68e945493",
                    nocArgs);
        }
    }
}
