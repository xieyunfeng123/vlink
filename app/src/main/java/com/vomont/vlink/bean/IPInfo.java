package com.vomont.vlink.bean;

import java.io.Serializable;

public class IPInfo implements Serializable {

	private String vfilesvrip;
	
	private int vfilesvrport;
//09-28 11:28:46.629: E/insert(16060):       "vfilesvrid" : 1,

	private int vfilesvrid;
	
	

    public String getVfilesvrip()
    {
        return vfilesvrip;
    }
    
    public void setVfilesvrip(String vfilesvrip)
    {
        this.vfilesvrip = vfilesvrip;
    }
    
    public int getVfilesvrport()
    {
        return vfilesvrport;
    }
    
    public void setVfilesvrport(int vfilesvrport)
    {
        this.vfilesvrport = vfilesvrport;
    }

    public int getVfilesvrid()
    {
        return vfilesvrid;
    }

    public void setVfilesvrid(int vfilesvrid)
    {
        this.vfilesvrid = vfilesvrid;
    }

	
	
}
