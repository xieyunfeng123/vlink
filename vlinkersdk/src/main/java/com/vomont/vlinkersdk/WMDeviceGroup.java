package com.vomont.vlinkersdk;

import java.io.Serializable;
import java.util.List;

public class WMDeviceGroup implements Serializable
{
    private int groupId;
    private int fatherGroupId;
    private String groupName;

    private List<WMDeviceInfo>  wmDeviceInfoList;

    public List<WMDeviceInfo> getWmDeviceInfoList() {
        return wmDeviceInfoList;
    }

    public void setWmDeviceInfoList(List<WMDeviceInfo> wmDeviceInfoList) {
        this.wmDeviceInfoList = wmDeviceInfoList;
    }

    public int getGroupId()
    {
        return groupId;
    }


    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }


    public int getFatherGroupId()
    {
        return fatherGroupId;
    }


    public void setFatherGroupId(int fatherGroupId)
    {
        this.fatherGroupId = fatherGroupId;
    }


    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
}
