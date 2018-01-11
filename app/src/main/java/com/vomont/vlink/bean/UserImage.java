package com.vomont.vlink.bean;

import java.io.Serializable;

public class UserImage implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8088224859888867309L;

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public String getIconurl()
    {
        return iconurl;
    }

    public void setIconurl(String iconurl)
    {
        this.iconurl = iconurl;
    }

    private int result;
    
    private String iconurl;
    
    
    
}
