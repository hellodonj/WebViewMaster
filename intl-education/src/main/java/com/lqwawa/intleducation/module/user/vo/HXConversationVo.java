package com.lqwawa.intleducation.module.user.vo;

//import com.hyphenate.chat.EMConversation;
import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2016/12/16.
 * email:man0fchina@foxmail.com
 */

public class HXConversationVo extends BaseVo {
    private boolean isGroup;
//    private EMConversation conversation;
    private MyClassVo group;
    private PersonalInfo personal;

    public boolean isIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

//    public EMConversation getConversation() {
//        return conversation;
//    }
//
//    public void setConversation(EMConversation conversation) {
//        this.conversation = conversation;
//    }

    public MyClassVo getGroup() {
        return group;
    }

    public void setGroup(MyClassVo group) {
        this.group = group;
    }

    public PersonalInfo getPersonal() {
        return personal;
    }

    public void setPersonal(PersonalInfo personal) {
        this.personal = personal;
    }
}
