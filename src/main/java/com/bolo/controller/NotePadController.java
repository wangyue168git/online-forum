package com.bolo.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bolo.redis.RedisCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * 留言记录控制层
 * @author 王越
 * 2016-8-30
 */
@Controller
@Scope
public class NotePadController {
	
	@Autowired
	private NotePadService noteService;
	@Autowired
	private ReplyService replyService;
	@Autowired
	private UserService service;
	@Autowired
	private RedisCacheUtil redisCacheUtil;

	private int x;


    @RequestMapping(value="redis",method = RequestMethod.GET)
    @ResponseBody
    public String hashset(HttpServletRequest req, HttpServletResponse resp,ModelMap model){
        NotePad notePad = new NotePad();
        notePad.setTitle("123");
        redisCacheUtil.hsetNotePad("123","1",notePad);
        return  redisCacheUtil.hgetNotePad("123","1").toString();
    }

	
	/**
	 * 留言板主页
	 * @param model
	 * @return
	 */
	@RequestMapping(value="notepad",method = RequestMethod.GET)
	public String notePad(Model model,HttpServletRequest req,HttpServletResponse resp){
		HttpSession session = req.getSession();
		if(req.getCookies()[0].getName().equals("sd")){
			session.setAttribute("id", req.getCookies()[0].getValue());
		}
		model.addAttribute("notelist", noteService.getNotes());
		return "page/notepad.jsp";
	}

	@RequestMapping(value="upload.do",method = RequestMethod.POST)
    @ResponseBody
	public String upload(@RequestParam(value = "file", required = false) MultipartFile file, HttpServletRequest req, HttpServletResponse resp,ModelMap model){
		String savePath = req.getSession().getServletContext().getRealPath("upload");
		String fileName = file.getOriginalFilename();
//      String fileName = new Date().getTime()+".jpg";
        File targetFile = new File(savePath, fileName);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        if(file.getSize() > 1024 * 1000 ){
            return "false";
        }
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(fileName != null){
            HttpSession session = req.getSession();
            session.setAttribute("lastFileName",fileName);
        }
        model.addAttribute("fileUrl", req.getContextPath()+"/upload/"+fileName);
        return  "true";
    }

    @RequestMapping(value="upload/{filename}",method = RequestMethod.GET)
    public String getImage(@PathVariable("filename") String filename,HttpServletRequest req, HttpServletResponse resp,ModelMap model){
        return  filename;
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
	public String showPad(Model model,HttpServletRequest req,HttpServletResponse resp){
		HttpSession session = req.getSession();
		if(req.getCookies()[0].getName().equals("sd")){
			session.setAttribute("id", req.getCookies()[0].getValue());
		}
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
        String id = null;
        for (Cookie cookie : req.getCookies()) {
            if(cookie.getName().equals("sd")){
                id = cookie.getValue();
                break;
            }
        }
		session.setAttribute("lastNotePad",notePad);
		notePad.setFilename((String)session.getAttribute("lastFileName"));
		if(service.getUser(id).getPermission().equals("-1")){
			return "stop";
		}else{
		    return noteService.insert(notePad);
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
		Cookie cookie = new Cookie("sd","bye bye!");
		cookie.setMaxAge(0);
		resp.addCookie(cookie);
		return "page/lode.jsp";
	}
	
}
