<%@ page  isELIgnored="false" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<head>
<link href="style" rel="stylesheet" type="text/css" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="jquery"></script>
<title>注册界面</title>

 <script language=javascript>
    function toopen(){
    var password=document.getElementById("password").value;
    var password1=document.getElementById("password1").value; 
    var name=document.getElementById("name").value;
    if(name!="" && name!=NaN){
       if(password==password1){
    	   if(password==""){
    		   alert("密码不能为空");
    		   return false;
    	   }else
             return true;
       }else{
           alert("两次输入密码不同，重新输入!");
           return false;
       }
       }
    else{
    	alert("用户名不能为空！");
    	return false;
    }
       }
    function toopen1(){
    	var id=$("#name").val();
	    if(id==""){
		   alert("用户名不能为空");
		   return false;      
	    }else{
	       var result=true;
	       $.ajax({  
	    	  url:"getUser",
	    	  type:"GET",
	    	  data : {
					"id":id
			  },
	    	  success:function(data){
	    		  if(data=='false'){
	    			  alert("该用户名已存在，请重新输入用户名...");
		    		  $('#name').val('');
		    	      result = false;
	    		  }else{
	    			  result = true;
	    		  }
	    	  }
	      });
	    return result;
	  }     
 } 
</script>
</head>
<body>
<div id="formbackground" style="position:absolute; width:100%; height:100%; z-index:-1">  
<img src="3" height="100%" width="100%"/>  
</div>
<script type="text/javascript">
$(function(){
    $('#formbackground').height($(window).height());
    $('#formbackground').width($(window).width());
});
</script>
<br></br>
<form  action="register"class="login" method="post">
  <ul style="line-height:3">
  <b>注册界面</b>
  <li>姓&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;名:<input name="id" id="name"class="login-input" type="text" onblur="toopen1()" >
  <li>登录密码:<input name="password" id="password"type="password"class="login-input" }>
  <li>确定密码:<input name="password1" id="password1"type="password"class="login-input" }>
  <li><input type="submit" class="login-submit" value="注册"  onclick="return toopen()"  >
 </ul>
</form>






</body>
</html>
