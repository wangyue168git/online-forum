package com.bolo.mybatis;


import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.bolo.entity.NotePad;
import com.bolo.entity.Reply;
import com.bolo.entity.User;

/**
 * MyBatis的Dao基类
 * @author 王越
 */
public class MyBatisDao extends SqlSessionDaoSupport{
	
    /**
     * 删除某个用户
     * @param key 对数据库执行的操作
     * @param name 用户名
     */
	public void delete(String key, String id) {
		getSqlSession().delete(key, id);
	}
	/**
	 * 删除note
	 * @param key
	 * @param id noteid
	 */
	public void deleteNote(String key, int id) {
		getSqlSession().delete(key, id);
	}
	/**
	 * 获得某个用户密码
	 * @param key 对数据库执行的操作
	 * @return 返回用户的用户名
	 */
	public String get(String key,String id) {
		return (String) getSqlSession().selectOne(key,id);
	}
	/**
	 * 添加用户
	 * @param key 对数据库执行的操作
	 * @param object 用户
	 */
	public void insert(String key,Object object){
		getSqlSession().insert(key, object);
	}
	/**
	 * 获得用户表单
	 * @param key 对数据库执行的操作
	 * @return 所有用户信息
	 */
	@SuppressWarnings("unchecked")
	public List<User> getList(String key) {
		return getSqlSession().selectList(key);
	}
	
	@SuppressWarnings("unchecked")
	public List<Reply> getList3(String key,int noteid) {
		return getSqlSession().selectList(key,noteid);
	}
	
	@SuppressWarnings("unchecked")
	public List<NotePad> getList1(String key) {
		return getSqlSession().selectList(key);
	}
	
	@SuppressWarnings("unchecked")
	public List<NotePad> getList2(String key,String str) {
		return getSqlSession().selectList(key,str);
	}
	/**
	 * 查找某个用户信息
	 * @param key 对数据库执行的操作
	 * @param params 用户
	 * @return 返回用户对象
	 */
	@SuppressWarnings("unchecked")
	public <T> T getUser(String key, Object params) {
		return (T) getSqlSession().selectOne(key, params);
	}
	
}

