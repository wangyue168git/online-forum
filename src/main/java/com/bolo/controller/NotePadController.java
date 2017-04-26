package com.bolo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bolo.entity.NotePad;
import com.bolo.entity.Reply;
import com.bolo.service.NotePadService;
import com.bolo.service.ReplyService;
import com.bolo.service.UserService;

/**
 * 留言记录控制层
 * @author 王越
 * 2016-8-30
 */
@Controller
public class NotePadController {
	
	@Autowired
	private NotePadService noteService;
	@Autowired
	private ReplyService replyService;
	@Autowired
	private UserService service;
	
	/**
	 * 留言板主页
	 * @param model
	 * @return
	 */
	@RequestMapping(value="notepad",method = RequestMethod.GET)
	public String notePad(Model model){
		model.addAttribute("notelist", noteService.getNotes());
		return "page/notepad.jsp";
	}
	
	/**
	 * 查询
	 * @param str
	 * @param model
	 * @return
	 */
	@RequestMapping(value="search",method = RequestMethod.GET)
	public String searchnotePad(@RequestParam("search") String str,Model model){
		model.addAttribute("notelist", noteService.getNotesBystr("%"+str+"%"));
		return "page/notepad.jsp";
	}
	
	@RequestMapping(value="search1",method = RequestMethod.GET)
	public String searchnotePad1(@RequestParam("search") String str,Model model){
		model.addAttribute("notelist", noteService.getNotesBystr("%"+str+"%"));
		return "page/usernotepad.jsp";
	}
	/**
	 * 用户留言界面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="shownotepad",method = RequestMethod.GET)
	public String showPad(Model model){
		model.addAttribute("notelist", noteService.getNotes());
		return "page/usernotepad.jsp";
	}
	
	@RequestMapping(value="bootstrap",method = RequestMethod.GET)
	public String jquery(){
		return "page/js/bootstrap.min.js";
	}
	/**
	 * 添加留言
	 * @param notePad 留言
	 * @param req  请求
	 * @param resp  响应
	 * @return
	 */
	@RequestMapping(value="insertnote",method = RequestMethod.POST)
	@ResponseBody
	public String insertNote(NotePad notePad,HttpServletRequest req,HttpServletResponse resp){
		HttpSession session = req.getSession();
		String id = (String) session.getAttribute("id");
		if(service.getUser(id).getPermission().equals("-1")){
			return "stop";
		}else{
			String result = noteService.insert(notePad);
		    return result;
		}
	}
	/**
	 * 修改留言
	 * @param notePad
	 * @return
	 */
	@RequestMapping(value="updatenote",method = RequestMethod.POST)
	@ResponseBody
	public String updateNote(NotePad notePad){
		 String result = noteService.edit(notePad);
		 return result;
	}
	/**
	 * 删除留言
	 * @param noteid
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value="deletenote",method = RequestMethod.GET)
	@ResponseBody
	public String deleteNote(@RequestParam("noteid") int noteid,HttpServletRequest req,HttpServletResponse resp){
		HttpSession session = req.getSession();
		if(((String) session.getAttribute("id"))!=null){
		  String result = noteService.delete(noteid);
		  return result;
		}else
			return "false";
	}
	/**
	 * 删除用户留言
	 * @param noteid
	 * @return
	 */
	@RequestMapping(value="deleteusernote",method = RequestMethod.GET)
	@ResponseBody
	public String deleteUserNote(@RequestParam("noteid") int noteid){
		String result = noteService.delete(noteid);
		return result;
	}
	
    /**
     * 查询回复
     * @param noteid
     * @return
     */
	@RequestMapping(value="selectreply",method = RequestMethod.GET)
	@ResponseBody
	public String selectRply(@RequestParam("noteid") int noteid){
		List<Reply> list = replyService.getReply(noteid);
		String result = "comment:<br>";
		for(int i=0;i<list.size();i++){
			String str = "<pre>"+list.get(i).getId()+": "+ list.get(i).getReplycontent()+"\t\t\t-------"+list.get(i).getDate()+"</pre><br>";
			result=result + str;
		}
        return  result;
	}
	
	/**
	 * 添加回复
	 * @param noteid1
	 * @param reply
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value="insertreply",method = RequestMethod.POST)
	@ResponseBody
	public String insertReply(@RequestParam("noteid1") String noteid1,Reply reply,HttpServletRequest req,HttpServletResponse resp){
		HttpSession session = req.getSession();
		if(((String) session.getAttribute("id"))!=null){
		    reply.setNoteid(Integer.valueOf(noteid1));
		    String result = replyService.insert(reply);
		    return result;
		}else{
			return "false";
		}
	}
	
	/**
	 * 查看我的留言
	 * @param req
	 * @param resp
	 * @param model
	 * @return
	 */
	@RequestMapping(value="selectmine",method = RequestMethod.GET)
	public String selectMine(HttpServletRequest req,HttpServletResponse resp,ModelMap model){
		HttpSession session = req.getSession();
		String id = (String) session.getAttribute("id");
		model.addAttribute("notelistmine", noteService.getNotesById(id));
		return "page/mynotepad.jsp";
	}
	@RequestMapping(value="userselectmine",method = RequestMethod.GET)
	public String selectUserMine(HttpServletRequest req,HttpServletResponse resp,ModelMap model){
		HttpSession session = req.getSession();
		String id = (String) session.getAttribute("id");
		model.addAttribute("notelistmine", noteService.getNotesById(id));
		return "page/usermynotepad.jsp";
	}
	/**
	 * 退出，注销账户
	 * @param req
	 * @param resp
	 * @param model
	 * @return
	 */
	@RequestMapping(value="exit",method = RequestMethod.GET)
	public String exit(HttpServletRequest req,HttpServletResponse resp,ModelMap model){
	    req.getSession().removeAttribute("id");
		return "page/lode.jsp";
	}
	
}
