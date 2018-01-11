package com.vomont.vlink.home.fragment;

import com.vomont.vlinkersdk.WMChannelInfo;
import com.vomont.vlinkersdk.WMDeviceInfo;
import com.vomont.vlinkersdk.WMMapNodeInfo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

public class DevInfo implements Serializable {

    private WMChannelInfo channelInfo;


    private WMMapNodeInfo mapNodeInfo;

    private WMDeviceInfo deviceInfo;

    public WMChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(WMChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    public WMMapNodeInfo getMapNodeInfo() {
        return mapNodeInfo;
    }

    public void setMapNodeInfo(WMMapNodeInfo mapNodeInfo) {
        this.mapNodeInfo = mapNodeInfo;
    }

    public WMDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(WMDeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }
}
