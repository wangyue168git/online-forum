<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登陆界面</title>
</head>
<body>
<%
  String welcome ="第一次访问";
  String[] info = new String[]{"","",""};
  Cookie[] cook = request.getCookies();
  if(cook!=null){
	  for(int i=0;i<cook.length;i++){
		  if(cook[i].getName().equals("mrCookInfo")){
			  info = cook[i].getValue().split("#");
			  welcome =",欢迎回来";
		  }
	  }
  }
  String password = request.getParameter("mail");
  if(password!=null){
	  if(password.equals("123456")){
		  %>
		  <jsp:forward page="show.jsp"></jsp:forward>
		  <% 
	  }
	  else{
		  %>
		  <script type="text/javascript"language="javascript">
		     alert("密码错误");
		  </script>
		  <%
	  }
  }
%>
<%=info[0]+welcome%>
<form  method="post">
  <ul style="line-height:3">
  <li>姓&nbsp;&nbsp;&nbsp;&nbsp;名:<input name="name" type="text" value="<%=info[0]%>">
  <li>出生日期:<input name="birthday" type="text" value="<%=info[1]%>">
  <li>登录密码:<input name="mail" type="password" value="<%=info[2]%>">
  <li><input type="submit" value="提交">
 </ul>
</form>
</body>
</html>