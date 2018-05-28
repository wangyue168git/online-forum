package com.bolo.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bolo.redis.RedisCacheUtil;
import com.bolo.test.auther.AuthManage;


import com.bolo.test.crawler.ZhongZiCrawler;
import com.bolo.test.nettys.server.NettyTCPServer;
import com.bolo.test.reqlimit.RequestLimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;


import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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

    private static final Logger logger = LoggerFactory.getLogger(NotePadController.class);


    @Autowired
	private NotePadService noteService;
	@Autowired
	private ReplyService replyService;

	@Autowired
	private RedisCacheUtil redisCacheUtil;
    @Autowired
    private NettyTCPServer tcpServer;


    @RequestMapping(value="test",method = RequestMethod.GET)
    @ResponseBody
    public String hashset(HttpServletRequest req, HttpServletResponse resp,ModelMap model) throws Exception {

        tcpServer.startServer();
        NotePad notePad = new NotePad();
        notePad.setTitle("123");
        redisCacheUtil.hsetNotePad("123","1",notePad);
        return  redisCacheUtil.hgetNotePad("123","1").toString();
    }

	/**
	 * 主页
	 * @param model
	 * @return
	 */
    @AuthManage("manager")
	@RequestLimit(count = 5)
	@RequestMapping(value="notepad",method = RequestMethod.GET)
	public String notePad(HttpServletRequest req,HttpServletResponse resp,Model model){
        model.addAttribute("notelist", NoteListSort(noteService.getNotes()));
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
            file.transferTo(targetFile); //文件复制
        } catch (IOException e) {
            logger.info("file为空");
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
	@AuthManage
	@RequestMapping(value="search",method = RequestMethod.GET)
	public String searchnotePad(@RequestParam("search") String str,HttpServletRequest req, HttpServletResponse resp,ModelMap model){
		model.addAttribute("notelist", noteService.getNotesBystr("%"+str+"%"));
		return "page/notepad.jsp";
	}
	@AuthManage
	@RequestMapping(value="search1",method = RequestMethod.GET)
	public String searchnotePad1(@RequestParam("search") String str,HttpServletRequest req, HttpServletResponse resp,ModelMap model){
		model.addAttribute("notelist", noteService.getNotesBystr("%"+str+"%"));
		return "page/usernotepad.jsp";
	}
	/**
	 * 用户界面
	 * @param model
	 * @return
	 */
    @RequestLimit(count = 5)
	@RequestMapping(value="shownotepad",method = RequestMethod.GET)
	public String showPad(Model model,HttpServletRequest req,HttpServletResponse resp){
        SessionAddId(req);
		model.addAttribute("notelist", NoteListSort(noteService.getNotes()));
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

        //从文件绝对路径中取文件名
        String filename = notePad.getFilename();
        if (filename.contains("/")){
            notePad.setFilename(filename.substring(filename.lastIndexOf("/")+1));
        }else {
            notePad.setFilename(filename.substring(filename.lastIndexOf("\\")+1));
        }

//		notePad.setFilename((String)session.getAttribute("lastFileName"));
		if(redisCacheUtil.hgetUserAuth("userAuths",id).equals("-1")){
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
	@AuthManage
	@RequestMapping(value="updatenote",method = RequestMethod.POST)
	@ResponseBody
	public String updateNote(NotePad notePad,HttpServletRequest req, HttpServletResponse resp,ModelMap model){
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
	@AuthManage("manager")
	@RequestMapping(value="deletenote",method = RequestMethod.GET)
	@ResponseBody
	public String deleteNote(@RequestParam("noteid") int noteid,HttpServletRequest req,HttpServletResponse resp){
		HttpSession session = req.getSession();
		if((session.getAttribute("id"))!=null){
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
	@AuthManage
	@RequestMapping(value="deleteusernote",method = RequestMethod.GET)
	@ResponseBody
	public String deleteUserNote(@RequestParam("noteid") int noteid,HttpServletRequest req, HttpServletResponse resp,ModelMap model){
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

    /**
     * 统一异常处理机制，灵活性不高，所以暂时不用
     * @param name
     * @param req
     * @param resp
     * @param model
     * @return
     */
    @RequestMapping(value="error",method = RequestMethod.GET)
	public String error(@RequestParam("name") String name,HttpServletRequest req,HttpServletResponse resp,ModelMap model){
	    if(name.equals("RequestLimitException")){
            return "page/limit_erro.jsp";
        }else {
            return "page/ex_erro.jsp";
        }
    }

    @RequestMapping(value="zhongzi",method = RequestMethod.GET)
    public String getZZ(HttpServletRequest req,HttpServletResponse resp,ModelMap model){
	    StringBuilder stringBuilder = new StringBuilder();
	    for (Map.Entry<String,String> entry : ZhongZiCrawler.map.entrySet()){
	        stringBuilder.append("<pre>" + entry.getKey() + "&#9;" + "<a href=\"" + "https://www.zhongziso.net"
                    +entry.getValue() + "\">" +entry.getValue()+"</a>"+ "</pre>" + "<br>");
        }
        model.addAttribute("zhongzi",stringBuilder);
        return "page/zhongzi.jsp";
    }

	private void SessionAddId(HttpServletRequest req){
        HttpSession session = req.getSession();
        String id = null;
        if(req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals("sd")) {
                    id = cookie.getValue();
                    break;
                }
            }
        }
        session.setAttribute("id", id);
    }

    private  List<NotePad> NoteListSort(List<NotePad> list){
        Collections.sort(list, new Comparator<NotePad>() {
            @Override
            public int compare(NotePad o1, NotePad o2) {
                return o2.getNoteid() - o1.getNoteid();
            }
        });
        return list;
    }
	
}
