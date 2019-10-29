package com.bolo.entity;

import java.util.List;

public class BillBoardMap{
    public List<ActorInfo> getData() {
        return data;
    }

    public void setData(List<ActorInfo> data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * 艺人
     */
    List<ActorInfo> data;
    /**
     * 时间
     */
    String date;
}
