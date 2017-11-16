package com.bolo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户对象
 * @author 王越
 * 2016-8-30
 */
@Data
public class User implements Serializable {
	
	private String id;  //用户名
	private String password;  //密码
	private String permission; //权限
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	public String getPermission() {
		return permission;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	   
}
