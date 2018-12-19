package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/4/13.
 * email:man0fchina@foxmail.com
 */

public class SectionTaskCommitListVo extends BaseVo {

    /**
     * id : 249426
     * thumbnail : http://lqwwres2.lqwawa.com/course/weilaba/2017/03/18/a03c8a89-2633-4e7c-8fc7-3234ddd82ba0/head.jpg?1489831831221ss
     * name : 小学英语一年级上册 Module 1 Unit 1 横版-1
     * creatTime : 1489831831000
     */

    private String id;
    private String thumbnail;
    private String name;
    private String creatTime;
    private String headerPic;
    private String creatName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getHeaderPic() {
        return headerPic;
    }

    public void setHeaderPic(String headerPic) {
        this.headerPic = headerPic;
    }

    public String getCreatName() {
        return creatName;
    }

    public void setCreatName(String createName) {
        this.creatName = createName;
    }
}
