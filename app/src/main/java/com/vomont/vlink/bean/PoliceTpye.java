package com.vomont.vlink.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class PoliceTpye implements Serializable {

    private int id;

    private  boolean isChoose;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }
}
