package com.vomont.vlinkersdk;

public interface RealPlayCallBack
{	
	public abstract void fLiveStreamCallBack(int playerId, int dataType, byte[] pDataBuffer, int iDataSize);
};
