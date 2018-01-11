package com.vomont.vlink.manager.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/11/16 0016.
 */

public class ManageFile  implements Serializable{

    private String  date;

    private List<ItemFile>  itemFiles;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ItemFile> getItemFiles() {
        return itemFiles;
    }

    public void setItemFiles(List<ItemFile> itemFiles) {
        this.itemFiles = itemFiles;
    }
}
