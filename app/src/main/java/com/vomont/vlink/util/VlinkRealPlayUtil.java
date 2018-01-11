package com.vomont.vlink.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.vomont.vlink.bean.FileInfo;
import com.vomont.vlinkersdk.Constants;
import com.vomont.vlinkersdk.DevStatusCallBack;
import com.vomont.vlinkersdk.PlayResultCallBack;
import com.vomont.vlinkersdk.StreamPlayer;
import com.vomont.vlinkersdk.WMDeviceGroup;
import com.vomont.vlinkersdk.WMDeviceInfo;
import com.vomont.vlinkersdk.WMVlinkerSDK;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author      谢云峰
 * @date        17/10/30
 * @email       773675907@qq.com
 */
public class VlinkRealPlayUtil implements SurfaceHolder.Callback
{
    private Activity context;
    

    private StreamPlayer streamPlayer = null;
    
    public String path= Environment.getExternalStorageDirectory().getAbsolutePath() + "/vlinker/img";;
    
    private RelativeLayout relativeLayout;
    
    private SurfaceView surfaceView;
    

    private PlayListener playListener;
    
    private boolean isStoped = true;
    
    private PlayRunnable pendingPlayRunnable;
    
    private int DeviceId;
    
    private int DevChannelId;

    private int type;

    private  int vedioType;

    private  int playid;


    public interface PlayListener
    {
        void onPlaySuccess(int playid);
        void onPlayFailed();
        void onPlayConnectTimeOut();
    }
    
    public void addPlayListener(PlayListener l)
    {
        this.playListener = l;
    }
    
    public VlinkRealPlayUtil(Activity context)
    {
        this.context = context;
    }


    public void  getDevList(List<WMDeviceInfo> mlist, List<WMDeviceGroup> groups)
    {
        WMVlinkerSDK.getInstance().getDevInfoList(mlist);
        WMVlinkerSDK.getInstance().getDevGroupList(groups);
    }

    public  void setDevStatusCallBack(DevStatusCallBack devStatusCallBack)
    {
            WMVlinkerSDK.getInstance().setDevStatusCallBack(devStatusCallBack);
    }

    /**
     * 建立媒体播放器
     * 
     * @param type
     *            播放器类型
     * @param relativeLayout
     *            播放器的容器
     */
    public void startPlay(int type, RelativeLayout relativeLayout, int deviceId, int devChannelId, int vedioType)
    {
        this.relativeLayout = relativeLayout;
        this.type=type;
        if (surfaceView != null)
        {
            relativeLayout.removeView(surfaceView);
            surfaceView = null;
        }
            surfaceView = new SurfaceView(context);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            surfaceView.setLayoutParams(layoutParams);
            surfaceView.getHolder().addCallback(this);
            relativeLayout.addView(surfaceView, 0);
            streamPlayer = WMVlinkerSDK.getInstance().CreatePlayer(type, surfaceView.getTag(), Constants.WMStreamType_RealTime);
        if (!isStoped)
        {
            pendingPlayRunnable = new PlayRunnable(deviceId, devChannelId, vedioType);
            return;
        }
        DeviceId=deviceId;
        DevChannelId=devChannelId;
        this.vedioType=vedioType;
        new Thread(pendingPlayRunnable).start();
//
    }

    class PlayRunnable implements Runnable
    {
        int deviceId;
        
        int devChannelId;
        
        int vedioType;
        
        public PlayRunnable(int deviceId, int devChannelId, int vedioType)
        {
            this.deviceId = deviceId;
            this.devChannelId = devChannelId;
            this.vedioType = vedioType;
        }
        @Override
        public void run()
        {
            isStoped = false;
             WMVlinkerSDK.getInstance().startRealPlay(deviceId, devChannelId, vedioType, streamPlayer, new PlayResultCallBack() {
                @Override
                public void onPlayConnectTimeOut() {
                    if (playListener != null)
                    {
                        playListener.onPlayConnectTimeOut();
                    }
                    isStoped = true;
                }
                @Override
                public void onFail(int result) {
                    if (playListener != null)
                    {
                        playListener.onPlayFailed();
                    }
                    isStoped = true;
                }

                @Override
                public void onSucess(int nStreamHandle) {
                   playid=nStreamHandle;
                    if (playListener != null)
                    {
                        playListener.onPlaySuccess(nStreamHandle);
                    }

                }
            });
            synchronized (VlinkRealPlayUtil.this)
            {
                try
                {
                    VlinkRealPlayUtil.this.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            stopPlay();
            destoryStreamPlayer();
            if (pendingPlayRunnable != null)
            {
                new Thread(pendingPlayRunnable).start();
                pendingPlayRunnable = null;
            }
            isStoped = true;
        }
    }

    public void stopIng()
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


    public void openVoice(boolean isOpen)
    {
        if(isOpen)
        {
            WMVlinkerSDK.getInstance().openSound(playid);
        }
        else
        {
            WMVlinkerSDK.getInstance().closeSound(playid);
        }
    }
    
    /**
     * 
     * @return 图片存储路径
     */
    public String getPath()
    {
        return path;
    }

    public void  setPath(String path)
    {
        this.path=path;
    }
    
    public List<String> getImagPath()
    {
        File file = new File(path);
        if (!file.exists() && !file.mkdir())
        {
            return null;
        }
        List<String> mlist = new ArrayList<String>();
        File[] fs = file.listFiles();
        for (int i = 0; i < fs.length; i++)
        {
            mlist.add(fs[i].getAbsolutePath());
        }
        return mlist;
    }
    

    
   
    
    @SuppressLint("DefaultLocale")
    public FileFilter fileFilter = new FileFilter()
    {
        public boolean accept(File file)
        {
            String tmp = file.getName().toLowerCase();
            if (tmp.endsWith(".mov") || tmp.endsWith(".jpg"))
            {
                return true;
            }
            return false;
        }
    };
    
    /**
     * 截图
     * 
     * @param
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public String saveSnapshot(int groupid, int deviceid, int channelid)
    {
        File directory = new File(path);
        if (!directory.exists() && !directory.mkdirs())
        {
            Toast.makeText(context, "抓拍失败！", Toast.LENGTH_SHORT).show();
            return null;
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = sDateFormat.format(new java.util.Date()) + "-" + groupid + "-" + deviceid + "-" + channelid;
        String fileName = path + File.separator + name;
        if (Constants.success == WMVlinkerSDK.getInstance().saveSnapshot(playid, fileName))
        {
            Toast.makeText(context, "抓拍成功！", Toast.LENGTH_SHORT).show();
            return name;
        }
        Toast.makeText(context, "抓拍失败！", Toast.LENGTH_SHORT).show();
        return null;
    }
    /**
     *
     * @return 所有截图的文件名称
     */
    public List<String> getImageName()
    {
        File file = new File(path);
        if (!file.exists() && !file.mkdir())
        {
            return null;
        }
        List<String> mlist = new ArrayList<String>();
        File[] str = file.listFiles();
        if(str==null)
        {
            return null;
        }

        ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();// 将需要的子文件信息存入到FileInfo里面
        for (int i = 0; i < str.length; i++)
        {
            File f = str[i];
            FileInfo fileInfo = new FileInfo();
            fileInfo.name = f.getName();
            fileInfo.path = f.getPath();
            fileInfo.lastModified = f.lastModified();
            fileList.add(fileInfo);
        }
        Collections.sort(fileList, new FileComparator());// 通过重写Comparator的实现类
        Collections.reverse(fileList);
        for (int i = 0; i < fileList.size(); i++)
        {
            mlist.add(fileList.get(i).getPath());
        }
        List<String> send=new ArrayList<>();
        send.addAll(mlist);
        for (int i = 0; i < send.size(); i++)
        {
            if(send.get(i).substring(send.get(i).length()-3,send.get(i).length()).equals("mp4"))
            {
                mlist.remove(send.get(i).replace("mp4","jpg"));
            }
        }
        return mlist;
    }

    public class FileComparator implements Comparator<FileInfo>
    {
        public int compare(FileInfo file1, FileInfo file2)
        {
            if (file1.lastModified < file2.lastModified)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }



    public String startRecord(int groupid, int deviceid, int channelid) {
        File directory = new File(path);
        if (!directory.exists() && !directory.mkdirs()) {
            Toast.makeText(context, "录制失败！", Toast.LENGTH_SHORT).show();
            return null;
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = sDateFormat.format(new java.util.Date()) + "-" + groupid + "-" + deviceid + "-" + channelid;
        String fileName = path + File.separator + name;
//        WMClientSdk.getInstance().saveSnapshot(playid, fileName);
//        if (Constants.success == WMClientSdk.getInstance().startRecord(playid, fileName)) {
//            return name;
//        }
//        Toast.makeText(context, "录制失败！", Toast.LENGTH_SHORT).show();
        return null;
    }


    public  void  stopRecord() {
//        WMClientSdk.getInstance().stopRecord(playid);
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
    public void ptzControlend(int size)
    {
//       WMClientSdk.getInstance().ptzControl(DeviceId, DevChannelId, size, 1, 4);
    }
    
    
    public void ptzControlStart(final int size)
    {
     int i=  WMVlinkerSDK.getInstance().pTZControl(DeviceId, DevChannelId, size, 0, 4);
     if(i==0)
     {
         new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(50);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                context.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        WMVlinkerSDK.getInstance().pTZControl(DeviceId, DevChannelId, size, 1, 4);
                    }
                });
            }
        }).start();
     }
    }
    
    /**
     * 关闭播放器
     */
    private void stopPlay()
    {
        if (playid != 0)
        {
            WMVlinkerSDK.getInstance().stopRealPlay(playid);
        }
    }
    
    /**
     * 退出播放器权限
     */
    public void unAuthenticate()
    {
//        WMClientSdk.getInstance().unAuthenticate();
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        streamPlayer = WMVlinkerSDK.getInstance().CreatePlayer(type, holder, Constants.WMStreamType_RealTime);
        new Thread(new PlayRunnable(DeviceId, DevChannelId, vedioType)).start();
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        
    }
    
}
