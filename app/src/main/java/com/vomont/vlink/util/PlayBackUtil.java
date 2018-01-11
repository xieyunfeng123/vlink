package com.vomont.vlink.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.video.opengl.GLSurfaceView20;
import com.vomont.vlinkersdk.Constants;
import com.vomont.vlinkersdk.PlayResultCallBack;
import com.vomont.vlinkersdk.SearchFrontEndCallBack;
import com.vomont.vlinkersdk.SetFrontEndPlayPosResultCallBack;
import com.vomont.vlinkersdk.StreamPlayer;
import com.vomont.vlinkersdk.WMFileSearchCondition;
import com.vomont.vlinkersdk.WMFrontEndFile;
import com.vomont.vlinkersdk.WMVlinkerSDK;
import java.io.File;
import java.text.SimpleDateFormat;
/**
 * 
 * 回放的接口封装
 * 
 * @author 谢云峰
 * @email 7736907@qq.com
 * @version [V1.00, 2016-9-14]
 * @see [相关类/方法]
 * @since V1.00
 */
public class PlayBackUtil
{
    
    private int playid;
    
    private RelativeLayout relativeLayout;
    
    private SurfaceView surfaceView;
    
    private StreamPlayer streamPlayer;
    
    private Activity context;
    

    private boolean isStoped = true;
    
    private PlayBackListener playBackListener;
    
    private PlayBackRunnable playBackRunnable;

    public String path= Environment.getExternalStorageDirectory().getAbsolutePath() + "/vlinker/img";;
    
    public PlayBackUtil(Activity context)
    {
        this.context = context;
    }



    public  int searFile(WMFileSearchCondition fileSearchCondition,SearchFrontEndCallBack searchFrontEndCallBack)
    {
         int result= WMVlinkerSDK.getInstance().frontEndSearch(fileSearchCondition,searchFrontEndCallBack);
        return  result;

    }
    
    /**
     * 建立媒体播放器
     * 
     * @param type
     *            播放器类型
     * @param relativeLayout
     *            播放器的容器
     */
    public StreamPlayer getStreamPlayer(int type, RelativeLayout relativeLayout, int deviceId, int devChannelId)
    {
        this.relativeLayout = relativeLayout;
        if (surfaceView != null)
        {
            stopPlay();
        }
        if (type == Constants.DEVICE_TYPE_XM_DEV)
        {
            surfaceView = new GLSurfaceView20(context);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            streamPlayer = WMVlinkerSDK.getInstance().CreatePlayer(type, surfaceView.getTag(), Constants.WMStreamType_RealTime);
            relativeLayout.addView(surfaceView);
            return streamPlayer;
        }
        else
        {
            surfaceView = new SurfaceView(context);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            streamPlayer = WMVlinkerSDK.getInstance().CreatePlayer(type, surfaceView.getHolder(), Constants.WMStreamType_RealTime);
            relativeLayout.addView(surfaceView);
            return streamPlayer;
        }
    }
    

//    /**
//     * 开始前端文件播放
//     *
//     * @param nPos
//     *            播放进度 1-100
//     * @param info
//     *            文件信息
//     * @param player
//     *            流媒体播放器对象
//     */
    public void startFrontEndFilePlay(int nPos, WMFrontEndFile info, StreamPlayer player)
    {
        new Thread( new PlayBackRunnable(nPos, info, player)).start();
    }
    
    class PlayBackRunnable implements Runnable
    {
        int nPos;

        WMFrontEndFile info;

        StreamPlayer player;

        public PlayBackRunnable(int nPos, WMFrontEndFile info, StreamPlayer player)
        {
            this.nPos = nPos;
            this.info = info;
            this.player = player;
        }

        @Override
        public void run()
        {
            isStoped = false;
            playid = WMVlinkerSDK.getInstance().frontEndPlayStart(info, nPos, player, new PlayResultCallBack() {
                @Override
                public void onPlayConnectTimeOut() {
                    if (playBackListener != null )
                    {
                        playBackListener.onConnectTimeOut();
                    }
                    isStoped = true;
                }
                @Override
                public void onFail(int result) {
                    if (playBackListener != null)
                    {
                        playBackListener.onFail();
                    }
                    isStoped = true;
                }

                @Override
                public void onSucess(int nStreamHandle) {
                    if (playBackListener != null)
                    {
                        playBackListener.onSucess();
                    }
                }
            });

            synchronized (PlayBackUtil.this)
            {
                try
                {
                    PlayBackUtil.this.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            stop();
            // destoryStreamPlayer();
            if (playBackRunnable != null)
            {
                new Thread(playBackRunnable).start();
                playBackRunnable = null;
            }
            isStoped = true;
        }
    }



    public  int  setFrontTime(int time,final StreamPlayer streamPlayer)
    {
        int result=  WMVlinkerSDK.getInstance().frontEndPlaySetPos(playid, time, new SetFrontEndPlayPosResultCallBack() {
            @Override
            public void fSetFrontEndPlayPosResultCallBack(int iResult, int streamHandle, int iPos) {
                if(iResult==0&&streamPlayer!=null)
                {
                    streamPlayer.clear();
                }
                Log.e("insert","========setFrontTime====="+iResult);
            }
        });
        return result;
    }

    public int  frontEndPlayGetPos()
    {
        int time=WMVlinkerSDK.getInstance().frontEndPlayGetPos(playid);
       return time;
    }


    public void setPlayBackListener(PlayBackListener playBackListener)
    {
        this.playBackListener = playBackListener;
    }
    
    public interface PlayBackListener
    {
        void onSucess();
        
        void onFail();
        
        void onConnectTimeOut();
        
    }
    
    /**
     * 停止前端文件播放
     * 
     *            播放流Id
     * @return 0表示成功，其它表示失败原因
     */
    public int stopFrontEndFilePlay()
    {
        int m;
        if (playid != -1)
        {
            m = WMVlinkerSDK.getInstance().frontEndPlayStop(playid);
            playid = -1;
            return 0;
        }
        
        return 1;
    }
    
//    /**
//     *
//     * @return 图片存储路径
//     */
//    public String getPath()
//    {
//        return path;
//    }
//
    public void stopPlay()
    {
        if (playid == Constants.WMPLAYERID_INVALID)
        {
            return;
        }
        synchronized (this)
        {
            notify();
        }
    }
    
    private void stop()
    {
        stopFrontEndFilePlay();
        destoryStreamPlayer();

    }
    
    /**
     * 销毁流媒体播放器
     */
    private void destoryStreamPlayer()
    {
        context.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (streamPlayer != null)
                {
                    WMVlinkerSDK.getInstance().DestroyPlayer(streamPlayer);
                }
                if (surfaceView != null)
                {
                    relativeLayout.removeView(surfaceView);
                    surfaceView = null;
                }
            }
        });
    }
    


    /**
     *
     * @return 图片存储路径
     */
    public String getPath()
    {
        return path;
    }

    public void  setPath(String  path)
    {
        this.path=path;
    }

    /**
     * 截图
     *
     *            保存的路径
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public String saveSnapshot(int factoryid, int subfactoryid, int deviceid)
    {
        File directory = new File(path);
        if (!directory.exists() && !directory.mkdirs())
        {
            return null;
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = sDateFormat.format(new java.util.Date()) + "-" + factoryid + "-" + subfactoryid + "-" + deviceid;
        String fileName = path + File.separator + name;
        if (Constants.success == WMVlinkerSDK.getInstance().saveSnapshot(playid, fileName))
        {
             Toast.makeText(context, "抓拍成功！", Toast.LENGTH_SHORT).show();
            return name;
        }
        Toast.makeText(context, "抓拍失败！", Toast.LENGTH_SHORT).show();
        return null;
    }
}
