package com.vomont.vlinkersdk;

/**
 * Created by Administrator on 2017/12/26 0026. 11 17
 */
public interface HistoryEventCallBack {
    void fSearchHistoryAlarmCallBack(int result,int total,int isFinish,WMUserEventMsg[] userEventMsgs);
}
