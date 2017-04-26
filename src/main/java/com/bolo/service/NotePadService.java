package com.bolo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bolo.entity.NotePad;
import com.bolo.entity.User;
import com.bolo.mybatis.MyBatisDao;
/**
 * 业务层
 * @author 王越
 * 2016-8-31
 */
@Service
public class NotePadService {
	
	@Autowired
	private MyBatisDao myBatisDao;
	
	/**
	 * 所有留言
	 * @return
	 */
	public List<NotePad> getNotes(){
		return myBatisDao.getList1("notePad.selectAll");
	}
	/**
	 * 某个用户的留言
	 * @param id
	 * @return
	 */
	public List<NotePad> getNotesById(String id){
		return myBatisDao.getList2("notePad.selectById",id);
	}
	/**
	 * 模糊查询
	 * @param str
	 * @return
	 */
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
	public String  insert(NotePad notePad){
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
	public String delete(int noteid){
		myBatisDao.deleteNote("notePad.delete", noteid);
		return "true";
	}
}
