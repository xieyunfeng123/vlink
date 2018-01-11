package com.vomont.vlink.manager.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/16 0016.
 */

public class ItemFile implements Serializable {

    private boolean isChoose;

    private String  name;

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
