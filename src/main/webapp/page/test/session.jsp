<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
  String name=request.getParameter("sa");
%>
<div align="center">
<form id="form1"name="form1"method="post"action="result.jsp">
 <table width="28%",border="0">
 <tr>
  <td>您的名字是：</td>
  <td><%=name %></td>
 </tr>
 <tr>
  <td>您最喜欢去的地方是：</td>
  <td><label>
   <input type="text" name="address"/>
  </label></td>
  </tr>
  <tr>
   <td clospan="2"><label>
   <div align="center">
   <input type="submit" name="Submit "value="提交"/>
   </div>
   </label></td>
   </tr>

 </table>
</form>
</div>
</body>
</html>