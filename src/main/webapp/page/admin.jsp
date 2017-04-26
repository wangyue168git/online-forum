
<%@page contentType="text/html; charset=UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登陆成功</title>
</head>


<style type="text/css">
body {
	line-height: 1;
	text-align: center;
    font:20px/30px  Verdana, 'Lucida Grande', sans-serif;
   
}
.lo{
   margin: 50px auto;
  padding: 40px 60px;
  width: 400px;
   font-size: 20px;
   text-align: left; 
}

</style>
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

<br></br><br></br> 

<h1>登陆成功，管理员你好</h1>
<form name='form1' action='table' method='post'>  
<input type="hidden" value="${id}" name="id">
</form>
<ul style="line-height:24px"class="lo">
  <li>当前用户：${id}
  <br></br>
  <li><a href="notepad">显示所有用户留言</a>
  <li><a href='javascript:document.form1.submit();' >显示所有用户信息</a>
  <li><a href="exit"><-退出</a>
</ul>	
<br></br><br>
<div id="time";style="text-align: center; width: 100%">   
        <script>  
        document.getElementById('time').innerHTML=new Date().toLocaleString();     
        setInterval("document.getElementById('time').innerHTML=new Date().toLocaleString();",1000);  
        </script>    
 </div> 
</body>
</html>