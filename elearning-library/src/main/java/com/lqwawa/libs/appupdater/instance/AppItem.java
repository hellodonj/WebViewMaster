package com.lqwawa.libs.appupdater.instance;

import com.lqwawa.libs.appupdater.AppInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AppItem {

    private int id;
    private int lauResType;
    private String lauResName;
    private String lauIconUrl;
    private String packageName;
    private int versionCode;
    private String versionName;
    private String description;
    private boolean isForce;
    private String lauResUrl;
    private long lauResSize;
    private String apkMd5;
    private String createTime;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLauResType() {
        return lauResType;
    }

    public void setLauResType(int lauResType) {
        this.lauResType = lauResType;
    }

    public String getLauResName() {
        return lauResName;
    }

    public void setLauResName(String lauResName) {
        this.lauResName = lauResName;
    }

    public String getLauIconUrl() {
        return lauIconUrl;
    }

    public void setLauIconUrl(String lauIconUrl) {
        this.lauIconUrl = lauIconUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setIsForce(boolean isForce) {
        this.isForce = isForce;
    }

    public String getLauResUrl() {
        return lauResUrl;
    }

    public void setLauResUrl(String lauResUrl) {
        this.lauResUrl = lauResUrl;
    }

    public long getLauResSize() {
        return lauResSize;
    }

    public void setLauResSize(long lauResSize) {
        this.lauResSize = lauResSize;
    }

    public String getApkMd5() {
        return apkMd5;
    }

    public void setApkMd5(String apkMd5) {
        this.apkMd5 = apkMd5;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AppInfo toAppInfo() {
        AppInfo appInfo = new AppInfo();
        appInfo.setId(String.valueOf(this.id));
        appInfo.setAppName(this.lauResName);
        appInfo.setAppIcon(this.lauIconUrl);
        appInfo.setPackageName(this.packageName);
        appInfo.setVersionName(this.versionName);
        appInfo.setVersionCode(this.versionCode);
        appInfo.setDescription(this.description);
        appInfo.setForcedUpdate(this.isForce);
        appInfo.setFileUrl(this.lauResUrl);
        appInfo.setFileSize(this.lauResSize);
        appInfo.setFileChecksum(this.apkMd5);
        try {
            appInfo.setModifiedTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(this.createTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return appInfo;
    }

}
