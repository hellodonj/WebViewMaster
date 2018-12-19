package com.lqwawa.intleducation.module.discovery.ui.lesson.select;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.module.learn.vo.SectionResListVo;

public interface ResourceSelectListener {
    // 资源选择的信息回调
    // true 返回选择已超过最大数目
    boolean onSelect(@NonNull SectionResListVo vo);

}
