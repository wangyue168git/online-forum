package com.bolo.entity;
/**
 * 留言对象
 * @author 王越
 * 2016-8-30
 */
public class NotePad {
	
	private int noteid;
	private String title;//note标题
	private String id; //用户id
	private String content;//留言内容
	private String date;//时间
	
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
