package com.vomont.vlink.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/14 0014.
 */

public class WifiBean implements Serializable {

    private  int  deviceId;

    private  String  des;
    private String time;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
