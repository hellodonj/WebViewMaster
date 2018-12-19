package com.lqwawa.intleducation.module.discovery.vo;
import com.lqwawa.intleducation.base.vo.BaseVo;

/**
 * Created by XChen on 2017/9/7.
 * email:man0fchina@foxmail.com
 */

public class AuthorizationVo extends BaseVo {

    /**
     * code : 0
     *  isAuthorized : true
     * isExist : true
     * authorizeCode :
     */

    private int code;
    private String message;
    private boolean isAuthorized;
    private boolean isExist;
    private String authorizeCode;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String messaage) {
        this.message = messaage;
    }

    public boolean isIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public boolean isIsExist() {
        return isExist;
    }

    public void setIsExist(boolean exist) {
        isExist = exist;
    }

    public String getAuthorizeCode() {
        return authorizeCode;
    }

    public void setAuthorizeCode(String authorizeCode) {
        this.authorizeCode = authorizeCode;
    }
}
