<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"import="java.util.Date,java.text.SimpleDateFormat"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<!-- 每秒钟刷新一次页面 -->
<!--<meta http-equiv="refresh" content="1">
--></head>
<body>
        本地时间是：<%= new Date().toLocaleString() %>    
   <% response.setHeader("Refresh","1"); %>
</body>

</html>