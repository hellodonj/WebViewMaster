package com.lqwawa.intleducation.factory.data.entity.response;

import com.lqwawa.intleducation.base.vo.ResponseVo;

/**
 * @author mrmedici
 * @desc V5.12版本首页标签数据的接受类
 */
public class LQConfigResponseVo<T,B> extends ResponseVo<T> {

    private B basicConfig;

    public B getBasicConfig() {
        return basicConfig;
    }

    public void setBasicConfig(B basicConfig) {
        this.basicConfig = basicConfig;
    }
}
