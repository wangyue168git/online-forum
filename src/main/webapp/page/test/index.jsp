<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.Date"%>
<!DOCTYPE html >
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>第一个JSP网站</title>
<!-- 5秒后自动跳转 -->
<meta http-equiv="refresh" content="5;1.jsp">
</head>
<body>
 你好，Hello World! <br>
               跳转界面 <br>
 现在时间是：<%= new Date().toLocaleString()%><br>
<a href="1.jsp?id=001">获取请求参数的值</a>
</body>
</html>