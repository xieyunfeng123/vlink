package com.vomont.vlink.bean;


import com.vomont.vlinkersdk.WMDeviceInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class DevBean implements Serializable {

    private String name;

    private List<WMDeviceInfo> mlist;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WMDeviceInfo> getMlist() {
        return mlist;
    }

    public void setMlist(List<WMDeviceInfo> mlist) {
        this.mlist = mlist;
    }
}
