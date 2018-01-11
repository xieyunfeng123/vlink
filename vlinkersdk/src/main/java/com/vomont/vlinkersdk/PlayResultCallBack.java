package com.vomont.vlinkersdk;

/**
 * Created by Administrator on 2017/12/9 0009.
 */

public interface PlayResultCallBack {

    void  onPlayConnectTimeOut();

    void  onFail(int result);

    void onSucess(int nStreamHandle);
}
