package com.vomont.vlinkersdk;

public interface BackPlayCallBack {
	public abstract void fBackPlayDataCallBack(int playerId, int dataType, byte[] pDataBuffer, int iDataSize);
}
