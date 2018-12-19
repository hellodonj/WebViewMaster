package com.example.root.robot_pen_sdk;

import java.io.Serializable;

import cn.robotpen.model.entity.DeviceEntity;

/**
 * Created by XChen on 2017/2/28.
 * email:man0fchina@foxmail.com
 */

public class DeviceVo implements Serializable {
    private int type;
    private String address;
    private String name;
    private DeviceEntity deviceEntity;

    public DeviceVo(DeviceEntity device){
        type = 1;
        deviceEntity = device;
    }

    public DeviceVo(String address, String name){
        type = 0;
        this.address = address;
        this.name = name;
    }

    public String getAddress() {
        if (type == 0) {
            return address;
        }else{
            return deviceEntity.getAddress();
        }
    }

    public void setAddress(String address) {
        if (type == 0){
            this.address = address;
        }else{
            deviceEntity.setAddress(address);
        }
    }

    public DeviceEntity getDeviceEntity() {
        return deviceEntity;
    }

    public void setDeviceEntity(DeviceEntity deviceEntity) {
        this.deviceEntity = deviceEntity;
    }

    public String getName() {
        if (type == 0) {
            return name;
        }else{
            return deviceEntity.getName();
        }
    }

    public void setName(String name) {
        if (type == 0){
            this.name = name;
        }else{
            deviceEntity.setName(name);
        }
    }
}
