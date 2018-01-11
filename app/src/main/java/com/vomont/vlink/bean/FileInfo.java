package com.vomont.vlink.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/13 0013.
 */

public class FileInfo implements Serializable{

    public  String name;

    public String path;

    public long lastModified;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}
