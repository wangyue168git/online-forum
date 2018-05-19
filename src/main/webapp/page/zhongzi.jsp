<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018\5\19 0019
  Time: 15:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>zhongzi</title>


    <style type="text/css">
        body {
            line-height: 1;
            text-align: center;
            background:#fff url("3");
            background-size:100%;
        }
    </style>
</head>

<body>
<%--<div id="formbackground" style="position:absolute; width:100%; height:100%; z-index:-1">--%>
    <%--<img src="3" height="100%" width="100%"/>--%>
<%--</div>--%>
<br><h1>种子分享</h1>   <br>

<div>
    <span>
        <h3>
            ${zhongzi}
        </h3>
    </span>

    <br>

</div>
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
