package com.vomont.vlinkersdk;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/7 0007.
 */

public class WMFileSearchCondition implements Serializable {

    private int devId;

    private int channelId;

    private  int beginTime;

    private int  endTime;

    /**
     *  0-中心计划存储 1-前端文件
     */
    private  int fileType;

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }
}
