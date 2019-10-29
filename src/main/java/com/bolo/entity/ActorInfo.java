package com.bolo.entity;

import lombok.Data;
import lombok.Setter;

import java.io.Serializable;

@Data
public class ActorInfo implements Serializable{

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public double getHot() {
        return hot;
    }

    public void setHot(double hot) {
        this.hot = hot;
    }

    public double getAudience_rating() {
        return audience_rating;
    }

    public void setAudience_rating(float audience_rating) {
        this.audience_rating = audience_rating;
    }

    public int getTicket_office() {
        return ticket_office;
    }

    public void setTicket_office(int ticket_office) {
        this.ticket_office = ticket_office;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLemmaNumber() {
        return lemmaNumber;
    }

    public void setLemmaNumber(int lemmaNumber) {
        this.lemmaNumber = lemmaNumber;
    }
    public double getIndex() {
        return index;
    }

    public void setIndex(double index) {
        this.index = index;
    }

    public int getUserPoint() {
        return userPoint;
    }

    public void setUserPoint(int userPoint) {
        this.userPoint = userPoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * ID
     */
    String id;
    /**
     *  艺人名字
     */
    String name;
    /**
     * 粉丝数
     */
    int fans;
    /**
     * 艺人头像
     */
    String userImage;
    /**
     * 影响力值
     */
    float force;
    /**
     * 类型
     */
    String[] type;
    /**
     * 热度
     */
    double hot;
    /**
     * 电影电视收视率
     */
    double audience_rating;
    /**
     * 票房
     */
    int ticket_office;
    /**
     * 星座
     */
    String constellation;
    /**
     * 生日
     */
    String date;
    /**
     * 词条数量
     */
    int lemmaNumber;

    /**
     * 搜索指数
     */
    double index;

    /**
     * 用户打榜积分
     */
    int userPoint;



    public String[] getLastweek() {
        return lastweek;
    }

    public void setLastweek(String[] lastweek) {
        this.lastweek = lastweek;
    }

    String[] lastweek;

    public String[] getPortrait() {
        return portrait;
    }

    public void setPortrait(String[] portrait) {
        this.portrait = portrait;
    }

    String[] portrait;

    public float[] getForces() {
        return last_force;
    }

    public void setForces(float[] forces) {
        this.last_force = forces;
    }

    float[] last_force;

    public void setFans(Fans fanInfo) {
        this.fanInfo = fanInfo;
    }

    Fans fanInfo;





}
