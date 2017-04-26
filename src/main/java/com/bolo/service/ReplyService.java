package com.bolo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.bolo.entity.NotePad;
import com.bolo.entity.Reply;
import com.bolo.mybatis.MyBatisDao;

/**
 * 业务层
 * @author 王越
 * 2016-8-31
 */
@Service
public class ReplyService {

	@Autowired
	private MyBatisDao myBatisDao;
	
	/**
	 * 查询回复
	 * @param noteid
	 * @return
	 */
	public List<Reply> getReply(int noteid){
		return myBatisDao.getList3("reply.selectBynoteid",noteid);
	}
	/**
	 * 添加回复
	 * @param reply
	 * @return
	 */
	public String  insert(Reply reply){
		if(reply.getReplycontent()!= null){
			myBatisDao.insert("reply.insert", reply);
			return "true";
		}
		else
			return "false";
	}
}
