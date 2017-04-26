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
  String name=(String)session.getAttribute("name");
  String solution=request.getParameter("address");
%>
<form id="form1"name="form1" method="post"action="">
 <td clospan="2"><strong>显示答案</strong></td><br>
 <tr>
 <td> 您的名字是：</td>
 <%=name %>
 </tr>
 <tr>
 <td><label>
 您最喜欢去的地方：
 </label></td>
 <td><%=solution %></td>
 </tr>
</form>
</body>
</html>