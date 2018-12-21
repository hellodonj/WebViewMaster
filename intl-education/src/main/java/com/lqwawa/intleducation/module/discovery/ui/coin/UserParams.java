package com.lqwawa.intleducation.module.discovery.ui.coin;

import android.support.annotation.NonNull;

import com.lqwawa.intleducation.factory.data.entity.user.UserEntity;
import com.lqwawa.intleducation.module.learn.vo.ChildrenListVo;
import com.lqwawa.intleducation.module.user.vo.UserInfoVo;

import java.io.Serializable;

/**
 * 用来传送数据
 */
public class UserParams implements Serializable {

    private String memberId;
    private String realName;
    private String nickName;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 根据用户实体创建一个传递的User基本对象
     * @param entity 用户实体
     * @return UserParams
     */
    public static UserParams buildUser(@NonNull UserEntity entity){
        UserParams user = new UserParams();
        user.memberId = entity.getMemberId();
        user.realName = entity.getRealName();
        user.nickName = entity.getNickName();
        return user;
    }

    /**
     * 根据用户实体创建一个传递的User基本对象
     * @param vo 用户实体
     * @return UserParams
     */
    public static UserParams buildUser(@NonNull UserInfoVo vo){
        UserParams user = new UserParams();
        user.memberId = vo.getUserId();
        user.realName = vo.getUserName();
        user.nickName = vo.getAccount();
        return user;
    }

    /**
     * 根据用户实体创建一个传递的User基本对象
     * @param vo 用户实体
     * @return UserParams
     */
    public static UserParams buildUser(@NonNull ChildrenListVo vo){
        UserParams user = new UserParams();
        user.memberId = vo.getMemberId();
        user.realName = vo.getRealName();
        user.nickName = vo.getNickname();
        return user;
    }
}
