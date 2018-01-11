package com.vomont.vlinkersdk;

/**
 * Created by Administrator on 2017/12/28 0028.
 */

public interface VodStreamCallBack {

    void fVodStreamCallBack(int streamHandle, int dataType, byte[] pDataBuffer, int iDataSize);
}
