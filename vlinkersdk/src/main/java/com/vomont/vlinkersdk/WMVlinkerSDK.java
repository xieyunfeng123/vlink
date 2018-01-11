package com.vomont.vlinkersdk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 谢
 * @desp vlinkerSdk 接口
 * @date 2017/12/7
 */
public class WMVlinkerSDK {

    private static WMVlinkerSDK instance;
    private ClientEngineer clientEngineer = new ClientEngineer();
    private Map<Integer, StreamPlayer> m_streamPlayerMap = new HashMap();

    public static WMVlinkerSDK getInstance() {
        if (instance == null)
            instance = new WMVlinkerSDK();
        return instance;
    }

    public interface OnHasRealDataCallBackListener {
        public void OnHasRealDataCallBack();
    }

    public interface OnEncodeDataCallBack {
        boolean OnEncodeDataCallBack(byte[] data, int nSize, long nPts);
    }

    /**
     * 初始化
     *
     * @param nLogLevel
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int init(int nLogLevel) {
        return clientEngineer.init(nLogLevel);
    }

    /**
     * 结束sdk
     *
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int uninit() {
        return clientEngineer.uninit();
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param ip       ip地址
     * @param port     端口号
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int login(String username, String password, String ip, int port) {
        return clientEngineer.login(username, password, ip, port);
    }

    /**
     * 登出
     *
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int logout() {
        return clientEngineer.logout();
    }

    /**
     * 更新密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int updataPassword(String oldPassword, String newPassword, ResultCallBack callBack) {
        return clientEngineer.UpdatePassword(oldPassword, newPassword, callBack);
    }

    /**
     * 获取设备组信息
     *
     * @param mlist
     */
    public void getDevGroupList(List<WMDeviceGroup> mlist) {
        WMDeviceGroup[] groups = clientEngineer.GetDeviceGroupList();
        if (groups != null) {
            for (WMDeviceGroup group : groups) {
                mlist.add(group);
            }
        }
    }

    /**
     * 获取设备信息
     *
     * @param mlist
     */
    public void getDevInfoList(List<WMDeviceInfo> mlist) {
        WMDeviceInfo[] deviceInfos = clientEngineer.GetDeviceList();
        if (deviceInfos != null) {
            for (WMDeviceInfo wmDeviceInfo : deviceInfos) {
                mlist.add(wmDeviceInfo);
            }
        }
    }

    public int setDevStatusCallBack(DevStatusCallBack devStatusCallBack) {
        return clientEngineer.SetDevStatusCallBack(devStatusCallBack);
    }

    /**
     * 获取地图节点
     *
     * @param mlist
     */
    public void getMapList(List<WMMapNodeInfo> mlist) {
        WMMapNodeInfo[] infos = clientEngineer.GetMapNodeList();
        if (infos != null) {
            for (WMMapNodeInfo wmMapNodeInfo : infos) {
                mlist.add(wmMapNodeInfo);
            }
        }
    }


    /**
     * 创建流媒体播放器
     *
     * @param deviceType 设备类型（当前支持雄迈或者海康）
     * @param showObj    视频显示的窗口对象
     * @return 流媒体播放器对象
     */
    public StreamPlayer CreatePlayer(int deviceType, Object showObj, int streamType) {
        StreamPlayer streamPlayer = new StreamPlayer(deviceType);
        streamPlayer.setShowObj(showObj, streamType);
        return streamPlayer;
    }

    /**
     * 销毁流媒体播放器
     *
     * @param player 流媒体播放器对象
     * @return 0表示成功，其它表示失败原因
     */
    public int DestroyPlayer(StreamPlayer player) {
        player.stopPlay();
        player.setShowObj(null, 0);
        return Constants.success;
    }

    /**
     * 实时预览
     *
     * @param deviceId     设备id
     * @param devChannelId 设备通道id
     * @param streamType   流类型（StreamType_Main：主码流，StreamType_Sub：子码流）
     * @return 0-失败,其它-实时播放ID
     */
    public int startRealPlay(int deviceId, int devChannelId, int streamType, final StreamPlayer streamPlayer, final PlayResultCallBack playResultCallBack) {

        int deviceType = streamPlayer.getDeviceType();
        if (Constants.DEVICE_TYPE_HK_DEV != deviceType
                && Constants.DEVICE_TYPE_HK_PUSHDEV != deviceType
                && Constants.DEVICE_TYPE_VEYE_DEV != deviceType
                && Constants.DEVICE_TYPE_RTSP_DEV != deviceType
                && Constants.DEVICE_TYPE_ONVIF_DEV != deviceType) {
            //播放失败
            if (playResultCallBack != null)
                playResultCallBack.onFail(0);
            return Constants.WMPLAYERID_INVALID;
        }
        int nStreamHandle = clientEngineer.StartRealPlay(deviceId, devChannelId, streamType, new RealPlayResultCallBack() {
            @Override
            public void onRealPlayResultCallBack(int nStreamHandle, int nResult) {
                if (Constants.WMPLAYERID_INVALID != nStreamHandle && nResult == 0) {
                    //播放成功 只有nStreamHandle不为0 且 nResult为0的时候才是成功的
                    streamPlayer.setStartTime(System.currentTimeMillis());
                    m_streamPlayerMap.put(nStreamHandle, streamPlayer);
                    if (playResultCallBack != null) {
                        playResultCallBack.onSucess(nStreamHandle);
                    }
                } else if (playResultCallBack != null && 23 == nResult) {
                    playResultCallBack.onPlayConnectTimeOut();
                } else if (playResultCallBack != null) {
                    playResultCallBack.onFail(nResult);
                }
            }
        }, new RealPlayCallBack() {
            @Override
            public void fLiveStreamCallBack(int nStreamHandle, int dataType, byte[] pDataBuffer, int iDataSize) {
                //实时数据的更新
                streamPlayer.fRealDataCallBack(nStreamHandle, dataType, pDataBuffer, iDataSize);
            }
        });
        if (playResultCallBack != null && nStreamHandle == 0) {
            playResultCallBack.onFail(0);
        }
        return nStreamHandle;
    }

    /**
     * 关闭实时预览
     *
     * @param playerId 实时播放id
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int stopRealPlay(int playerId) {
        m_streamPlayerMap.remove(playerId);
        return clientEngineer.StopRealPlay(playerId);
    }

    /**
     * 视频截图
     *
     * @param playerId 流Id
     * @param fileName 存储文件路径
     * @return 0表示成功，其它表示失败原因
     */
    public int saveSnapshot(int playerId, String fileName) {
        StreamPlayer streamPlayer = (StreamPlayer) m_streamPlayerMap.get(playerId);
        if (null == streamPlayer) {
            return Constants.fail;
        }
        return streamPlayer.saveSnapshot(fileName);
    }


    /**
     * 打开声音
     *
     * @param playerId 流Id
     * @return 0表示成功，其它表示失败原因
     */
    public int openSound(int playerId) {
        StreamPlayer streamPlayer = (StreamPlayer) m_streamPlayerMap.get(playerId);
        if (null == streamPlayer) {
            return Constants.fail;
        }

        return streamPlayer.OpenSound();
    }

    /**
     * 关闭声音
     *
     * @param playerId 流Id
     * @return 0表示成功，其它表示失败原因
     */
    public int closeSound(int playerId) {
        StreamPlayer streamPlayer = (StreamPlayer) m_streamPlayerMap.get(playerId);
        if (null == streamPlayer) {
            return Constants.fail;
        }
        return streamPlayer.CloseSound();
    }


    /**
     * 开启手动录像(生成.mp4格式文件)
     *
     * @param playerId 播放器ID
     * @param filePath 录像文件存储路径（含文件名）
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int startRecord(int playerId, String filePath) {
        return clientEngineer.StartRecord(playerId, filePath);
    }

    /**
     * 关闭录像
     *
     * @param playerId 播放器id
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int stopRecord(int playerId) {
        return clientEngineer.StopRecord(playerId);
    }

    /**
     * 云台控制
     *
     * @param deviceId     设备ID
     * @param devChannelId 设备通道ID
     * @param ptzCommand   云台控制参数（com/vomont/vlinkersdk/WMPTZControl）
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int pTZControl(int deviceId, int devChannelId, int ptzCommand, int nStop, int nSpeed) {
        return clientEngineer.PTZControl(deviceId, devChannelId, ptzCommand, nStop, nSpeed);
    }

    /**
     * 预置点控制
     *
     * @param deviceId      设备ID
     * @param devChannelId  设备通道ID
     * @param presetCommand 预置点控制命令（com/vomont/vlinkersdk/WMPTZPresetCommand）
     * @param presetIdx     云台预置点参数
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int pTZPresetControl(int deviceId, int devChannelId, int presetCommand, int presetIdx) {
        return clientEngineer.PTZPresetControl(deviceId, devChannelId, presetCommand, presetIdx);
    }

    /**
     * 查找前端文件列表
     *
     * @param fileSearchCondition 文件检索条件（com/vomont/vlinkersdk/WMFileSearchCondition）
     * @return
     */
    public int frontEndSearch(WMFileSearchCondition fileSearchCondition, SearchFrontEndCallBack searchFrontEndCallBack) {
        return clientEngineer.FrontEndSearch(fileSearchCondition, searchFrontEndCallBack);
    }

    /**
     * 开始文件回放
     *
     * @param fileInfo 前端文件信息（com/vomont/vlinkersdk/WMFrontEndFile）
     * @param playPos  播放偏移秒值
     * @return 播放器id
     */
    public int frontEndPlayStart(final WMFrontEndFile fileInfo, final int playPos, final StreamPlayer streamPlayer, final PlayResultCallBack playResultCallBack) {
        int deviceType = streamPlayer.getDeviceType();
        if (Constants.DEVICE_TYPE_HK_DEV != deviceType
                && Constants.DEVICE_TYPE_HK_PUSHDEV != deviceType
                && Constants.DEVICE_TYPE_VEYE_DEV != deviceType
                && Constants.DEVICE_TYPE_RTSP_DEV != deviceType
                && Constants.DEVICE_TYPE_ONVIF_DEV != deviceType) {
            //播放失败
            if (playResultCallBack != null)
                playResultCallBack.onFail(0);
            return Constants.WMPLAYERID_INVALID;
        }
        int streamHandle = clientEngineer.FrontEndPlayStart(fileInfo, playPos, new StartFrontEndResultCallBack() {
            @Override
            public void fStartFrontEndResultCallBack(int iResult, int streamHandle) {
                if (iResult == 0 && playResultCallBack != null) {
                    playResultCallBack.onSucess(streamHandle);
                } else if (playResultCallBack != null && 23 == iResult) {
                    playResultCallBack.onPlayConnectTimeOut();
                } else if (playResultCallBack != null) {
                    playResultCallBack.onFail(iResult);
                }
            }
        }, new VodStreamCallBack() {
            @Override
            public void fVodStreamCallBack(int streamHandle, int dataType, byte[] pDataBuffer, int iDataSize) {
                m_streamPlayerMap.get(streamHandle).fRealDataCallBack(streamHandle, dataType, pDataBuffer, iDataSize);
            }
        });
        if (playResultCallBack != null && streamHandle == 0) {
            playResultCallBack.onFail(0);
        } else {
            m_streamPlayerMap.put(streamHandle, streamPlayer);
        }
        return streamHandle;
    }

    /**
     * 结束文件回放
     *
     * @param streamHandle 播放器ID
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int frontEndPlayStop(int streamHandle) {
        return clientEngineer.FrontEndPlayStop(streamHandle);
    }

    /**
     * 设置进度
     *
     * @param streamHandle 播放器ID
     * @param playPos      偏移秒值
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int frontEndPlaySetPos(int streamHandle, int playPos, SetFrontEndPlayPosResultCallBack callBack) {
        return clientEngineer.FrontEndPlaySetPos(streamHandle, playPos, callBack);
    }

    /**
     * 获取回放文件播放进度
     *
     * @param streamHandle 播放器ID
     * @return
     */
    public int frontEndPlayGetPos(int streamHandle) {
        return clientEngineer.FrontEndPlayGetPos(streamHandle);
    }

    /**
     * 设置报警回调
     *
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int setEventMsgCallBack(PoliceCallBack policeCallBack) {
        return clientEngineer.SetEventMsgCallBack(policeCallBack);
    }

    /**
     * 获取报警参数
     *
     * @param deviceId     设备ID
     * @param devChannelId 通道ID
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int getEventConfig(int deviceId, int devChannelId) {
        return clientEngineer.GetEventConfig(deviceId, devChannelId, null);
    }

    /**
     * 设置报警参数
     *
     * @param eventCfg 报警设置参数（com/vomont/vlinkersdk/WMClientEventCfg）
     * @return 0-成功，1-失败, 其他-错误码
     */
    public int setEventConfig(WMClientEventCfg eventCfg) {
        return clientEngineer.SetEventConfig(eventCfg, null);
    }

    /**
     * 搜索历史报警记录
     *
     * @param searchCondition 历史报警记录搜索条件（com/vomont/vlinkersdk/WMHistoryEventSearchCondition）
     * @return
     */
    public int historyEventMsgSearch(WMHistoryEventSearchCondition searchCondition, HistoryEventCallBack historyEventCallBack) {
        return clientEngineer.HistoryEventMsgSearch(searchCondition, historyEventCallBack);
    }

    /**
     * 开启语音对讲
     *
     * @param deviceId     设备ID
     * @param devChannelId 设备通道ID
     * @param playerId     播放器ID
     * @return 0-成功, 1-失败, 其他-错误码
     */
    public int startVoiceTalk(int deviceId, int devChannelId, int playerId) {
        return clientEngineer.StartVoiceTalk(deviceId, devChannelId, playerId);
    }

    /**
     * 停止语音对讲
     *
     * @param deviceId     设备ID
     * @param devChannelId 设备通道ID
     * @param playerId     播放器ID
     * @return 0-成功, 1-失败, 其他-错误码
     */
    public int stopVoiceTalk(int deviceId, int devChannelId, int playerId) {
        return clientEngineer.StopVoiceTalk(deviceId, devChannelId, playerId);
    }

    /**
     * 发送语音对讲数据
     *
     * @param deviceId     设备ID
     * @param devChannelId 设备通道ID
     * @param dataArr      采集语音数据数组
     * @param dataLen      采集语音数据长度
     * @return 0-成功, 1-失败, 其他-错误码
     */
    public int sendVoiceTalkData(int deviceId, int devChannelId, byte[] dataArr, int dataLen) {
        return clientEngineer.SendVoiceTalkData(deviceId, devChannelId, dataArr, dataLen);
    }

    /**
     * 功能：设置透明数据通道回调
     * 参数：
     * callBack			[IN]: 透明数据回调函数, void fTransparentDataCallBack(int iDeviceId, byte[] pDataBuffer, int iDataSize)
     * 返回值： 0-成功，1-失败, 其他-错误码
     */
    public int setTransparentDataCallBack(TransparentCallBack transparentCallBack) {
        return clientEngineer.SetTransparentDataCallBack(transparentCallBack);
    }

}
