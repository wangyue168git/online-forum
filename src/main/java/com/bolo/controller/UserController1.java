package com.bolo.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bolo.entity.User;
import com.bolo.service.NotePadService;
import com.bolo.service.UserService;


/**
 * 用户控制层
 * @author 王越
 */
@Controller
//@RequestMapping("/user")
public class UserController1 {
	
	@Autowired
	private UserService service;
	
	@Autowired
	private NotePadService noteService;
	
	/**
	 * 登录界面
	 * @return
	 */
	@RequestMapping(value="lode",method=RequestMethod.GET)
	public String lode(){
		return "page/lode.jsp";
	}
	/**
	 * 验证用户名是否存在
	 * @param id
	 * @return
	 */
	@RequestMapping(value="getUser",method=RequestMethod.GET)
	@ResponseBody
	public String getUser(@RequestParam("id") String id){
		User user = service.getUser(id);
		if(user != null){
		    return "false";
		}else{
			return "true";
		}
	}
	
	/**
	 * 删除用户
	 * @param id 用户名
	 * @return  跳转到表单界面
	 */
	@RequestMapping(value="delete/{name}",method=RequestMethod.GET)
	public String delete(@PathVariable("name") String id){
		if(service.getUser(id).getPermission().equals("1")){
			return "redirect:/retable";
		}else{
		service.delete(id);
		return "redirect:/retable";
		}
	}
	@RequestMapping(value="setper/{id}",method=RequestMethod.GET)
	public String setper(@PathVariable("id") String id){
		User user  = service.getUser(id);
		if(user.getPermission().equals("-1")){
			user.setPermission("0");
			service.edit(user);
			return "redirect:/retable";
		}else if(user.getPermission().equals("0")){
		   user.setPermission("-1");
		   service.edit(user);
		   return "redirect:/retable";
		}else
		   return "redirect:/retable";
	}
	
	/**
	 * 跳转修改密码界面
	 * @param name1  用户名
	 * @param model 模块，传参
	 * @return
	 */
	@RequestMapping(value="edit/{id}",method=RequestMethod.GET)
	public String edit(@PathVariable("id") String id,ModelMap model){
		model.addAttribute("user", service.getUser(id));       //可以用来在页面上下文之间传递参数
		return "forward:/page/edit.jsp";
	}
	
	@RequestMapping(value="edituser/{id}",method=RequestMethod.GET)
	public String editUser(@PathVariable("id") String id,ModelMap model){
		model.addAttribute("user", service.getUser(id));       //可以用来在页面上下文之间传递参数
		return "forward:/page/edituser.jsp";
	}
	
	
	/**
	 * 登录用户判断
	 * @param name  用户名
	 * @param password  密码
	 * @param model 
	 * @return  正确则跳转到显示页面，否则跳转到erro界面
	 */
	@RequestMapping(value="show",method = RequestMethod.POST)
	public String lode(@RequestParam("name") String id,@RequestParam("password") String password,HttpServletRequest req,HttpServletResponse resp){
		if(service.getUser(id)!=null){
			String password1 = service.getUser(id).getPassword();
			String permission = service.getUser(id).getPermission();
		if(password.equals(password1)){
		    Cookie cookie = new Cookie("sd",id);
		    //Cookie cookie1 = new Cookie("JSESSIONID",id);
		    //cookie1.setMaxAge(60*60);
		    cookie.setMaxAge(60*60);
		    resp.addCookie(cookie);
		    //resp.addCookie(cookie1);
		    //resp.setStatus(200);

			HttpSession session = req.getSession();
			session.setAttribute("id", id);
			if(permission.equals("1")){
				return "page/admin.jsp";
			}else{
			    return "redirect:/shownotepad";
			}
		}
		else{
			return "page/erro.jsp";
		}
		}else{
			return "page/erro.jsp";
		}
	}
	
	/**
	 * 保存修改后的密码
	 * @param name 用户名
	 * @param password  密码
	 * @param model 
	 * @return  返回表单页面
	 */
	@RequestMapping(value="edit/update",method = RequestMethod.POST)
	public String update(User user,Model model){
		service.edit(user);
		return "redirect:/retable";
	}
	
	@RequestMapping(value="edituser/update",method = RequestMethod.POST)
	public String updateUser(User user,Model model){
		service.edit(user);
		return "redirect:/notepad";
	}
	
	/**
	 * 处理跳转注册界面请求
	 * @return  跳转界面
	 */
	@RequestMapping(value="sign",method=RequestMethod.GET)
	public String sign(){
		return "page/register.jsp";
	}
	
	/**
	 * 用户注册
	 * @param name  用户名
	 * @param password  密码
	 * @param model
	 * @return  跳转到登陆界面
	 */
	@RequestMapping(value="register",method = RequestMethod.POST)
	public String sign(User user){
		user.setPermission("0");
		service.
		
		insert(user);
		return "page/lode.jsp";
	}
	
	/**
	 * css文件
	 * @return 向浏览器发送指定css文件
	 */
	@RequestMapping(value="/style",method = RequestMethod.GET)
	public String style(){
		return "page/css/style.css";
	}
	
	/**
	 * jquery库
	 * @return 发送库文件
	 */
	@RequestMapping(value="/jquery",method = RequestMethod.GET)
	public String jquery(){
		return "page/js/jquery-1.8.2.min.js";
	}
	
	@RequestMapping(value="bootstrap-paginator",method = RequestMethod.GET)
	public String bootstrap(){
		return "page/js/bootstrap-paginator.min.js";
	}
	
	/**
	 * 调用登录页面背景图片
	 * @return 发送背景图片
	 */
	@RequestMapping(value="/back",method = RequestMethod.GET)
	public String background(){
		return "page/image/back.jpg";
	}

	@RequestMapping(value="/3",method = RequestMethod.GET)
	public String background1(){
		return "page/image/3.jpg";
	}
	
	@RequestMapping(value="edit/3",method = RequestMethod.GET)
	public String backgroun(){
		return "page/image/3.jpg";
	}
	
	@RequestMapping(value="/2",method = RequestMethod.GET)
	public String background2(){
		return "page/image/2.jpg";
	}
	
	@RequestMapping(value="/1",method = RequestMethod.GET)
	public String background3(){
		return "page/image/1.jpg";
	}
	
	@RequestMapping(value="/1045",method = RequestMethod.GET)
	public String background4(){
		return "page/image/1045.jpg";
	}
	
	/**
	 * 将用户表单与request绑到一起
	 * @param req request请求
	 * @param resp repose响应
	 * @param model 存储表单List
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="table",method = RequestMethod.POST)
	public ModelAndView showDept(@RequestParam("id") String id,HttpServletRequest req,HttpServletResponse resp,ModelMap model){
		if(!id.equals("")){
		  String permission =  service.getUser(id).getPermission();
		  if(permission.equals("1")){
		     List list=service.getUsers();
		     model.addAttribute("list",list);
		     return new ModelAndView("page/table.jsp",model);
		  }else{
			 return  new ModelAndView("page/lode.jsp",model);
		  }
		}else
		   return  new ModelAndView("page/lode.jsp",model);
    }
	@RequestMapping(value="table",method = RequestMethod.GET)
	public String showGet(){
		return "page/lode.jsp";
	}
	
	/**
	 * 用户信息
	 * @param req
	 * @param resp
	 * @param model
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="retable",method = RequestMethod.GET)
	public ModelAndView show(HttpServletRequest req,HttpServletResponse resp,ModelMap model){
		List list=service.getUsers();
	    model.addAttribute("list",list);
	    return new ModelAndView("page/table.jsp",model);
	}
	
	
	@RequestMapping(value="return",method = RequestMethod.POST)
	public String ret(Model model,@RequestParam("name") String id){
		model.addAttribute("id", id);
		return "page/admin.jsp";
	}
	
	
	

}
