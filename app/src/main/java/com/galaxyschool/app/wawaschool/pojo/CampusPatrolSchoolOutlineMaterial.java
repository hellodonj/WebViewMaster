package com.galaxyschool.app.wawaschool.pojo;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by E450 on 2016/12/19.
 * 校园巡查---校本课程。
 */
public class CampusPatrolSchoolOutlineMaterial implements Serializable {

    /// Sc_Outline_Material表的主键
    public String Id ;

    /// 屏幕类型
    public int ScreenType ;

    /// 创建人
    public String Creator ;

    /// 创建人
    public String NickName ;

    /// 创建人名称
    public String CreatorName ;

    /// 资源类型
    /// PPT = 1  PDF = 2 Image = 3
    /// Video = 4 Audio = 5 WaWaWeike = 6
    public String SchoolMaterialType ;

    /// 序号
    public String Num ;

    /// 合肥的资源Id
    public String ResId ;

    /// 所属的大纲的章/节Id
    public String Sc_OutlineCatlogId ;

    /// 所属的学校大纲Id
    public String Sc_OutlineId ;

    /// 资源名称
    public String Title ;

    /// 缩略图地址
    public String ConvertUrl ;

    /// 资源下载地址
    public String DownloadUrl ;

    /// 合肥的资源类型
    /// 图1 音频2 视频2 pdf6 ppt20
    public String HeFeiResType ;

    /// 创建时间
    public String CreateTime ;

    /// 知识点
    public String Knowledge ;

    public void setId(String id) {
        Id = id;
    }

    public String getId() {
        return Id;
    }

    public int getScreenType() {
        return ScreenType;
    }

    public void setScreenType(int screenType) {
        ScreenType = screenType;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getCreatorName() {
        return CreatorName;
    }

    public void setCreatorName(String creatorName) {
        CreatorName = creatorName;
    }

    public String getSchoolMaterialType() {
        return SchoolMaterialType;
    }

    public void setSchoolMaterialType(String schoolMaterialType) {
        SchoolMaterialType = schoolMaterialType;
    }

    public String getNum() {
        return Num;
    }

    public void setNum(String num) {
        Num = num;
    }

    public String getResId() {
        return ResId;
    }

    public void setResId(String resId) {
        ResId = resId;
    }

    public String getSc_OutlineCatlogId() {
        return Sc_OutlineCatlogId;
    }

    public void setSc_OutlineCatlogId(String sc_OutlineCatlogId) {
        Sc_OutlineCatlogId = sc_OutlineCatlogId;
    }

    public String getSc_OutlineId() {
        return Sc_OutlineId;
    }

    public void setSc_OutlineId(String sc_OutlineId) {
        Sc_OutlineId = sc_OutlineId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getConvertUrl() {
        return ConvertUrl;
    }

    public void setConvertUrl(String convertUrl) {
        ConvertUrl = convertUrl;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public String getHeFeiResType() {
        return HeFeiResType;
    }

    public void setHeFeiResType(String heFeiResType) {
        HeFeiResType = heFeiResType;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getKnowledge() {
        return Knowledge;
    }

    public void setKnowledge(String knowledge) {
        Knowledge = knowledge;
    }


    public NewResourceInfo getNewResourceInfo(){
        NewResourceInfo resource = new NewResourceInfo();
        if (!TextUtils.isEmpty(ResId)){
            String microId = ResId;
            if(ResId.contains("-")) {
               microId = ResId.substring(0, ResId.indexOf("-"));
            }
            resource.setMicroId(microId);
            resource.setResourceId(ResId);
        }
        resource.setResourceType(Integer.parseInt(HeFeiResType));
        resource.setTitle(Title);
        resource.setThumbnail(ConvertUrl);
        resource.setResourceUrl(DownloadUrl);
        resource.setAuthorName(CreatorName);
        resource.setScreenType(ScreenType);
        resource.setCreatedTime(CreateTime);
        return resource;
    }

    /**
     * 装载分页信息
     * @return
     */
    public NewResourceInfoTag getNewResourceTag(){
        NewResourceInfo newResourceInfo = getNewResourceInfo();
        if (newResourceInfo != null){
            List<NewResourceInfo> newResourceInfoList = new ArrayList<>();
            newResourceInfoList.add(newResourceInfo);
            NewResourceInfoTag newResourceInfoTag = new NewResourceInfoTag();
            newResourceInfoTag.setSplitInfoList(newResourceInfoList);
            return newResourceInfoTag;
        }
        return null;
    }

    /**
     * 是否需要显示拆分页
     * @return
     */
    public boolean isNeedToShowSplitPage() {
        int resType = Integer.parseInt(HeFeiResType);
        if(resType == ResType.RES_TYPE_COURSE_SPEAKER) {
            //老微课不显示详情，打开的时候也不进入详情。10019是单页的微课，不显示拆分。
            return  true;
        }
        return false;
    }
}
