package com.lqwawa.intleducation.factory.data.model.user;

import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * @author mrmedici
 * @desc 用户请求根据nickName查询用户信息的请求Model
 */
public class UserModel extends BaseVo {

    private String NickName;

    public UserModel(String nickName) {
        NickName = nickName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }
}
