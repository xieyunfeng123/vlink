package com.vomont.vlink.bean;

import java.io.Serializable;

public class User implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String name;
    
    private String password;
    
    private String url;
    
    

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
    
    
}
