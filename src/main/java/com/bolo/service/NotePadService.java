package com.bolo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.bolo.entity.NotePad;
import com.bolo.mybatis.MyBatisDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务层
 * @author 王越
 * 2016-8-31
 */
@Service
public class NotePadService {

    private static final Logger logger = LoggerFactory.getLogger(NotePadService.class);
	
	@Autowired
	private MyBatisDao myBatisDao;
	
	/**
	 * 所有留言
	 * @return
	 */
	@Cacheable(value = "userCache",keyGenerator = "customKeyGenerator")
	public List<NotePad> getNotes(){
	    logger.info("我是从数据库取的数据。。。。");
		return myBatisDao.getList1("notePad.selectAll");
	}
	/**
	 * 某个用户的留言
	 * @param id
	 * @return
	 */
    @Cacheable(value = "noteCache",key = "#id")
	public List<NotePad> getNotesById(String id){
		return myBatisDao.getList2("notePad.selectById",id);
	}
	/**
	 * 模糊查询
	 * @param str
	 * @return
	 */
    @Cacheable(value = "noteCache1",key = "#str")
	public List<NotePad> getNotesBystr(String str){
		return myBatisDao.getList2("notePad.selectByStr",str);
	}
	/**
	 * 某个留言信息
	 * @param noteid
	 * @return
	 */
	public List<NotePad> getNote(int noteid){
		return myBatisDao.getUser("notePad.selectByPrimary",noteid);
	}
	/**
	 * 添加留言
	 * @param notePad
	 * @return
	 */
	//@CachePut
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(value ={"userCache","noteCache1","noteCache"}, allEntries=true)
    public String insert(NotePad notePad){
		if(notePad.getTitle()!= null){
			myBatisDao.insert("notePad.insert", notePad);
			return "true";
		}
		else
			return "false";
	}
	/**
	 * 修改留言信息
	 * @param notePad
	 * @return
	 */
    @CacheEvict(value ={"userCache","noteCache1","noteCache"}, allEntries=true)
	public String  edit(NotePad notePad){
		if(notePad.getTitle()!= null){
			myBatisDao.insert("notePad.update", notePad);
			return "true";
		}
		else
			return "false";
	}
	/**
	 * 删除留言
	 * @param noteid
	 * @return
	 */
    @CacheEvict(value ={"userCache","noteCache1","noteCache"}, allEntries=true)
	public String delete(int noteid){
		myBatisDao.deleteNote("notePad.delete", noteid);
		return "true";
	}

}
