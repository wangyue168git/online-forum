package com.bolo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.bolo.entity.User;
import com.bolo.mybatis.MyBatisDao;
/**
 * 业务层
 * @author 王越
 *
 */
@Service
public class UserService {
	
	@Autowired
	private MyBatisDao myBatisDao;
	
	/**
	 * 获得用户信息
	 * @param id
	 * @return
	 */
	public User getUser(String id){
		return myBatisDao.getUser("userMapper.selectByPrimary",id);
	}
	/**
	 * 获得用户
	 * @return
	 */
    @Cacheable(value = "userinfoCache",keyGenerator = "customKeyGenerator")
	public List<User> getUsers(){
		return myBatisDao.getList("userMapper.selectByEntity");
	}
	/**
	 * 根据ID获得用户密码
	 * @param id
	 * @return
	 */
	public String getPassword(String id){
		return myBatisDao.get("userMapper.selectByPrimaryKey",id);
	}
	/**
	 * 添加用户
	 * @param user
	 */
    @CacheEvict(value ="userinfoCache", allEntries=true)
	public void  insert(User user){
		if(user.getId()!= null)
			myBatisDao.insert("userMapper.insert", user);
	}
	/**
	 * 修改用户信息
	 * @param user
	 */
    @CacheEvict(value ="userinfoCache", allEntries=true)
	public void  edit(User user){
		myBatisDao.insert("userMapper.update", user);
	}
	/**
	 * 删除用户
	 * @param id
	 */
    @CacheEvict(value ="userinfoCache", allEntries=true)
	public void delete(String id){
		myBatisDao.delete("userMapper.delete", id);
	}
}
