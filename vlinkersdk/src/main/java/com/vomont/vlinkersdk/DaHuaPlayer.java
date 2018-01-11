package com.vomont.vlinkersdk;


public class DaHuaPlayer implements IPlayer
{
	private int m_hPlayer = 0;
	private static final int Max_volume = 100;
	
	@Override
	public boolean IsPlaying()
	{
		return (m_hPlayer != 0) ? true : false;
	}
	
	@Override
	public int StartPlay(byte[] pStreamHead, int nSize, int streamType, Object showObj)
	{	
		return Constants.success;
	}
	
	@Override
	public int StopPlay()
	{
		return Constants.success;
	}

	@Override
	public int InputData(byte[] pBuf, int nSize)
	{
		return Constants.success;
	}

	@Override
	public int PausePlay(int bPause)
	{
		return Constants.success;
	}

	@Override
	public int ControlFilePlay(int nControlCode, int nParam)
	{
		return Constants.success;
	}
	
	@Override
	public int GetPlaySpeed()
	{
		return Constants.success;
	}

	@Override
	public int GetPlayTime()
	{
		return Constants.success;
	}
	
	@Override
	public int SetPlayTime()
	{
		return Constants.success;
	}

	@Override
	public int OpenSound()
	{
		return Constants.success;
	}
	
	@Override
	public int CloseSound()
	{
		return Constants.success;
	}

	@Override
	public int SetVolume(int nVolume)
	{
		return Constants.success;
	}
	
	@Override
	public int GetVolume()
	{
		return Constants.success;
	}

	@Override
	public int SaveSnapshot(String fileName, int nFormat)
	{
		return Constants.success;
	}

	@Override
	public int ResetSourceBuffer()
	{
		return Constants.success;
	}
	
	@Override
	public int GetSourceBufferSize()
	{
		return Constants.success;
	}	
	
	@Override
	public int StartVoiceTalk(AudioCaptureCallBack callBack)
	{
		return Constants.success;
	}	
	
	@Override
	public int StopVoiceTalk()
	{
		return Constants.success;
	}		
	
	@Override
	public int GetLastError()
	{
		return 0;
	}
}
