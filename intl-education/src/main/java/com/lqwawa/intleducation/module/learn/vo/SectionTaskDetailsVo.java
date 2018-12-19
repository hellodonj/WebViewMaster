package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.List;

/**
 * Created by XChen on 2017/4/13.
 * email:man0fchina@foxmail.com
 */

public class SectionTaskDetailsVo extends BaseVo{
    SectionTaskOriginVo origin;
    List<SectionTaskCommitListVo> commitList;
    int code;
    SectionTaskDetailsVo data;

    public SectionTaskDetailsVo getData() {
        return data;
    }

    public void setData(SectionTaskDetailsVo data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SectionTaskOriginVo getOrigin() {
        return origin;
    }

    public void setOrigin(SectionTaskOriginVo origin) {
        this.origin = origin;
    }

    public List<SectionTaskCommitListVo> getCommitList() {
        return commitList;
    }

    public void setCommitList(List<SectionTaskCommitListVo> commitList) {
        this.commitList = commitList;
    }
}
