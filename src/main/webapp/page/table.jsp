<%@ page  isELIgnored="false" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户信息</title>

<style type="text/css">

body {
	line-height: 1;
	text-align: center;

}
table{
	z-index: 1;
	text-align: center;
	font-size: 18px;
}
a{
	font-size: 24px;
	font:  'Lucida Grande', Verdana, sans-serif;
}

</style>
<script type="text/javascript" src="jquery"></script>
</head>
<body>

<div id="formbackground" style="position:absolute; width:100%; height:100%; z-index:-1">  
<img src="3" height="100%" width="100%"/>  
</div>
<script type="text/javascript">
$(document).ready(function(){
	
	var len = $('table tr').length;	   
	for(var i = 1;i<=len;i++){  	 
		$('table tr:eq('+i+') td:first').text(i);
    }
	for(var i = 2;i<$(':button ').length;i+=3){
		var per = $(':button:eq('+i+')').val();
		if(per==="0"){
			$(':button:eq('+i+')').val("禁言");
		}else if(per==="-1"){
			$(':button:eq('+i+')').val("解禁");
		}else{
			$(':button:eq('+i+')').val("备注");
			var j =i-1;
			$(':button:eq('+j+')').val("管理员");
			//$(':button:eq('+j+')').remove();
			
		}
	}
});

 function va(){
	 var name = document.getElementById("a1").value;
	 document.setElementById("name").value=name;
	 return true;
 }
</script>


<br></br><br></br><br></br><br>
<h1  style="color:#C06; face="华文彩云,琥珀体,隶书,华文行楷,黑体"">用户信息表</h1>
<center>
<table width="572" border="1" bordercolor="#0000CC" bordercolorlight="#CCFFCC" bordercolordark="#3300FF" id="用户信息"  >

 <tr><th width="92" >序号</th><th width="170" height="32">用户名</th> <th width="172">密码</th><th width="190">操作</th></tr>
 <c:forEach items="${list}"  var="user" >
 <tr>
 <td></td>
 <td height="30">${user.id}</td>
 <td>${user.password} </td>
 <td>
	<a href="edit/${user.id}"><input type="button" value="修改"></input></a>
	<a href="delete/${user.id}"><input type="button" value="删除"></input></a>
	<a href="setper/${user.id}"><input type="button" value="${user.permission}" ></input></a>
</td>
 </tr> 
 </c:forEach>
</table>
</center>



<br></br><br></br>

<form name='form1' action='return' method='post'>  
<input name="name" id="name" value="wangyue" type="hidden" >
</form>
<a href="javascript:document.form1.submit();"><-返回</a>
<br></br><br></br><br></br>
<div id="time";style="text-align: center; width: 100%">   
        <script>  
        document.getElementById('time').innerHTML=new Date().toLocaleString();     
        setInterval("document.getElementById('time').innerHTML=new Date().toLocaleString();",1000);  
        </script>    
</div>


</body>
</html>