package com.lqwawa.intleducation.factory.data.entity.tutorial;

import java.io.Serializable;
import java.util.List;

/**
 * @author mrmedici
 * @desc 用来接收Server返回的国家，省，市，区数据
 */
public class LocationEntity implements Serializable {

    private boolean HasAppliedSchool;
    private String Id;
    private String MId;
    private int ParentLocationType;
    private int RegisteredCapital;
    private boolean ReturnResult;
    private String SId;
    private List<LocationBean> LocationList;

    public boolean isHasAppliedSchool() {
        return HasAppliedSchool;
    }

    public void setHasAppliedSchool(boolean HasAppliedSchool) {
        this.HasAppliedSchool = HasAppliedSchool;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getMId() {
        return MId;
    }

    public void setMId(String MId) {
        this.MId = MId;
    }

    public int getParentLocationType() {
        return ParentLocationType;
    }

    public void setParentLocationType(int ParentLocationType) {
        this.ParentLocationType = ParentLocationType;
    }

    public int getRegisteredCapital() {
        return RegisteredCapital;
    }

    public void setRegisteredCapital(int RegisteredCapital) {
        this.RegisteredCapital = RegisteredCapital;
    }

    public boolean isReturnResult() {
        return ReturnResult;
    }

    public void setReturnResult(boolean ReturnResult) {
        this.ReturnResult = ReturnResult;
    }

    public String getSId() {
        return SId;
    }

    public void setSId(String SId) {
        this.SId = SId;
    }

    public List<LocationBean> getLocationList() {
        return LocationList;
    }

    public void setLocationList(List<LocationBean> LocationList) {
        this.LocationList = LocationList;
    }

    public static class LocationBean {

        private String Text;
        private String Value;

        public String getText() {
            return Text;
        }

        public void setText(String Text) {
            this.Text = Text;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String Value) {
            this.Value = Value;
        }
    }
}
