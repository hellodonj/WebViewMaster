package com.icedcap.dubbing.entity;

import android.os.Parcel;
import android.text.SpannableString;

/**
 * @desc:
 * @author: wangchao
 * @date: 2019/03/01
 */
public class DubbingEntity extends SrtEntity {
    private int score;
    private int progressMax;
    private int progress;
    private int videoTime;
    private boolean isRecord;
    private boolean isSelect;
    private boolean isRecording;
    private boolean isRecordPlaying;
    private SpannableString evalResult;

    public DubbingEntity() {

    }

    public DubbingEntity(SrtEntity srtEntity) {
        this.content = srtEntity.content;
        this.startTime = srtEntity.startTime;
        this.endTime = srtEntity.endTime;
        this.role = srtEntity.role;
    }

    public SpannableString getEvalResult() {
        return evalResult;
    }

    public void setEvalResult(SpannableString evalResult) {
        this.evalResult = evalResult;
    }

    public int getScore() {
        return score;
    }

    public DubbingEntity setScore(int score) {
        this.score = score;
        return this;
    }

    public int getProgressMax() {
        return progressMax;
    }

    public DubbingEntity setProgressMax(int progressMax) {
        this.progressMax = progressMax;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public DubbingEntity setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public int getVideoTime() {
        return videoTime;
    }

    public DubbingEntity setVideoTime(int videoTime) {
        this.videoTime = videoTime;
        return this;
    }

    public boolean isRecord() {
        return isRecord;
    }

    public DubbingEntity setRecord(boolean record) {
        isRecord = record;
        return this;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public DubbingEntity setSelect(boolean select) {
        isSelect = select;
        return this;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setIsRecording(boolean recording) {
        isRecording = recording;
    }

    public boolean isRecordPlaying() {
        return isRecordPlaying;
    }

    public void setIsRecordPlaying(boolean recordPlaying) {
        isRecordPlaying = recordPlaying;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.score);
        dest.writeInt(this.progressMax);
        dest.writeInt(this.progress);
        dest.writeInt(this.videoTime);
        dest.writeByte(this.isRecord ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRecording ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isRecordPlaying ? (byte) 1 : (byte) 0);
    }

    protected DubbingEntity(Parcel in) {
        this.score = in.readInt();
        this.progressMax = in.readInt();
        this.progress = in.readInt();
        this.videoTime = in.readInt();
        this.isRecord = in.readByte() != 0;
        this.isSelect = in.readByte() != 0;
        this.isRecording = in.readByte() != 0;
        this.isRecordPlaying = in.readByte() != 0;
    }

    public static final Creator<DubbingEntity> CREATOR = new Creator<DubbingEntity>() {
        @Override
        public DubbingEntity createFromParcel(Parcel source) {
            return new DubbingEntity(source);
        }

        @Override
        public DubbingEntity[] newArray(int size) {
            return new DubbingEntity[size];
        }
    };
}
