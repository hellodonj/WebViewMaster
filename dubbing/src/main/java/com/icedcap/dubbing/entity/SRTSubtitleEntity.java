package com.icedcap.dubbing.entity;

/**
 * Created by dsq on 2017/4/24.
 */

public class SRTSubtitleEntity extends SrtEntity {
    public static final int ENDTYPE = 3;
    public static final int SHOWLONGBREAK_TYPE = -2;
    public static final int SHOWNORMAL_TYPE = 1;
    public static final int SHOWROLE_TYPE = 2;
    private boolean isShowAnim = false;
    private boolean retracementFlag = false;
    private int type;

    public SRTSubtitleEntity(int type) {
        this.type = type;
    }

    public SRTSubtitleEntity(int type, SrtEntity srtEntity, boolean isShowAnim) {
        this.type = type;
        if (srtEntity != null) {
            setContent(srtEntity.getContent());
            setEndTime(srtEntity.getEndTime());
            setRole(srtEntity.getRole());
            setStartTime(srtEntity.getStartTime());
        }
        this.isShowAnim = isShowAnim;
    }

    public SRTSubtitleEntity(int type, String role, int starttime, int endtime, String content, boolean isShowAnim) {
        this.type = type;
        setContent(content);
        setEndTime(endtime);
        setRole(role);
        setStartTime(starttime);
        this.isShowAnim = isShowAnim;
    }

    public SRTSubtitleEntity(String role, int starttime, int endtime, String content) {
        super(role, starttime, endtime, content);
    }

    public int getType() {
        return this.type;
    }

    public boolean isRetracementFlag() {
        return this.retracementFlag;
    }

    public boolean isShowAnim() {
        return this.isShowAnim;
    }

    public void setIsShowAnim(boolean isShowAnim) {
        this.isShowAnim = isShowAnim;
    }

    public void setRetracementFlag(boolean retracementFlag) {
        this.retracementFlag = retracementFlag;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
