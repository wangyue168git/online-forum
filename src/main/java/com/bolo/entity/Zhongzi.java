package com.bolo.entity;

import javax.persistence.*;

@Table(name = "zhongzi")
public class Zhongzi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String title;

    private String torrent;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return torrent
     */
    public String getTorrent() {
        return torrent;
    }

    /**
     * @param torrent
     */
    public void setTorrent(String torrent) {
        this.torrent = torrent;
    }
}