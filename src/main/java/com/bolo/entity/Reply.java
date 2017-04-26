package com.bolo.entity;
/**
 * 留言回复
 * @author 王越
 * 2016-8-30
 */
public class Reply {
	
	private int noteid;
	private String title;
	private String id;
	private String replycontent;
	private String date;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getReplycontent() {
		return replycontent;
	}
	public void setReplycontent(String replycontent) {
		this.replycontent = replycontent;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setNoteid(int noteid) {
		this.noteid = noteid;
	}
	public int getNoteid() {
		return noteid;
	}

}
