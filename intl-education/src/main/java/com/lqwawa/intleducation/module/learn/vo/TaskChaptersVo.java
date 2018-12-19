package com.lqwawa.intleducation.module.learn.vo;

import com.lqwawa.intleducation.base.vo.BaseVo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XChen on 2017/2/24.
 * email:man0fchina@foxmail.com
 */

public class TaskChaptersVo extends BaseVo{
    List<ExamVo> list;
    public TaskChaptersVo(){

    }
    public TaskChaptersVo(List<ExamVo> list){
        this.list = new ArrayList<>(list);
    }
    public List<ExamVo> getList() {
        return list;
    }

    public void setList(List<ExamVo> list) {
        this.list = list;
    }
}
