package com.vomont.vlinkersdk;


/**
 * Created by Administrator on 2017/12/6 0006.
 */

public class ClientEngineer {

    public native int init(int nLogLevel) ;
    public native int uninit();

    public native int login(String userName, String password, String svrIp, int svrPort);
    public native int logout();

    public native int UpdatePassword(String oldPassword, String newPassword, Object callback);

    public native WMDeviceGroup[] GetDeviceGroupList();
    public native WMDeviceInfo[] GetDeviceList();
    public native WMMapNodeInfo[] GetMapNodeList();

    public  native  int  SetDevStatusCallBack( Object callBack);


    public native int StartRealPlay(int deviceId, int devChannelId, int streamType, Object resultCallback, Object resultStreamCallback) ;
    public native int StopRealPlay(int playerId);

    public native int StartRecord(int playerId, String filePath);
    public native  int StopRecord(int playerId);

    public native  int PTZControl(int deviceId, int devChannelId,int ptzCommand, int nStop, int nSpeed);
    public native int PTZPresetControl(int deviceId, int devChannelId, int presetCommand, int presetIdx);

    public native int SetTransparentDataCallBack(Object callback);

    public native int FrontEndSearch(WMFileSearchCondition fileSearchCondition, Object callback) ;
    public native int FrontEndPlayStart(WMFrontEndFile fileInfo, int playPos, Object resultCallBack,Object vodStreamCallBack);
    public native int FrontEndPlayStop(int playerId);
    public native int FrontEndPlaySetPos(int playerId, int playPos, Object callback);
    public native int FrontEndPlayGetPos(int playerId);

    public native int SetEventMsgCallBack(Object callback);
    public  native int GetEventConfig(int deviceId, int devChannelId, Object callback);
    public native int SetEventConfig(WMClientEventCfg eventCfg, Object callback);
    public native int HistoryEventMsgSearch(WMHistoryEventSearchCondition searchCondition, Object callback);

    public native int  StartVoiceTalk(int deviceId,int devChannelId,int playerId);
    public native int StopVoiceTalk(int deviceId,int devChannelId,int playerId);
    public native int SendVoiceTalkData(int deviceId,int devChannelId,byte[] dataArr,int dataLen);
    static
    {
        System.loadLibrary("vlinkersdk");
    }
}
