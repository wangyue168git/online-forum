<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>锁定ip</title>


    <style type="text/css">
        body {
            line-height: 1;
            text-align: center;

        }
    </style>
</head>

<body>
<div id="formbackground" style="position:absolute; width:100%; height:100%; z-index:-1">
    <img src="3" height="100%" width="100%"/>
</div>
<br></br><br></br><br></br>
<h1>您的ip已经被锁定，请2小时后再来吧</h1>   <br>
<br></br><!--
 现在时间是：<%= new java.util.Date().toLocaleString()%><br>-->

<br></br><br></br>
<div id="time";style="text-align: center; width: 100%">
    <script>
        document.getElementById('time').innerHTML=new Date().toLocaleString();
        setInterval("document.getElementById('time').innerHTML=new Date().toLocaleString();",1000);
    </script>
</div>


</body>
</html>