<%@ page  isELIgnored="false" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>修改密码</title>
<style type="text/css">
body{
	text-align: center;
	line-height: 1;	
	font:20px/30px  Verdana, 'Lucida Grande', sans-serif;
	font-size: 19px;
	background-image: url(3);
}
input, textarea, select, label {
  font-family: inherit;
  font-size: 15px;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}
.login{

  margin: 50px auto;
  padding: 40px 60px;
  width: 400px;
}
.login > h1 {
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: bold;
  color: white;
  text-align: center;
  text-shadow: 0 -1px rgba(0, 0, 0, 0.4);
}
.login-input {
  color: white;
  text-shadow: 0 1px black;
  background:#666;
  border: 1px solid #15243b;
  border-top-color: #0d1827;
  border-radius: 4px;

  

}
.login-input:focus {
	outline: 0;
	background-color: #006699;
	-webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.3), 0 0 4px 1px rgba(255, 255, 255, 0.6);
	box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.3), 0 0 4px 1px rgba(255, 255, 255, 0.6);
}
.login-submit {
	
	width: 100px;
	height: 30px;
	margin-bottom: 0px;
	font-size: 14px;
	font-weight: bold;
	color: #294779;
	text-align: center;
	text-shadow: 0px 1px rgba(255, 255, 255, 0.3);
	background: #adcbfa;
	background-clip: padding-box;
	border: 1px solid #284473;
	border-bottom-color: #223b66;
	border-radius: 4px;
	cursor: pointer;
	background-image: -webkit-linear-gradient(top, #d0e1fe, #96b8ed);
	background-image: -moz-linear-gradient(top, #d0e1fe, #96b8ed);
	background-image: -o-linear-gradient(top, #d0e1fe, #96b8ed);
	background-image: linear-gradient(to bottom, #d0e1fe, #96b8ed);
	-webkit-box-shadow: inset 0 1px rgba(255, 255, 255, 0.5), inset 0 0 7px rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.15);
	box-shadow: inset 0 1px rgba(255, 255, 255, 0.5), inset 0 0 7px rgba(255, 255, 255, 0.4), 0 1px 1px rgba(0, 0, 0, 0.15);
}
.login-submit:active {
  background: #a4c2f3;
  -webkit-box-shadow: inset 0 1px 5px rgba(0, 0, 0, 0.4), 0 1px rgba(255, 255, 255, 0.1);
  box-shadow: inset 0 1px 5px rgba(0, 0, 0, 0.4), 0 1px rgba(255, 255, 255, 0.1);
}

</style>
 <script language=javascript>
    function toopen(){
    var password=document.getElementById("password").value;
    var name=document.getElementById("name").value;
    if(name!="" && name!=NaN){
       if(password!=""){
           return true;
       }else{
           alert("密码不能为空!");
           return false;
       }
       }
    else{
    	alert("用户名不能为空！");
    	return false;
    }
       }
</script>
</head>
<body>

<form action="update" class="login" method="post">
  <ul style="line-height:3">
  <b>修改密码</b>
  <li>姓&nbsp;&nbsp;&nbsp;&nbsp;名:<input name="id" id="name"class="login-input" value="${user.id}" type="text" readonly="true">
  <li>新密码:<input name="password" id="password"type="password"class="login-input"  value=""}>
  <li><input type="submit" class="login-submit" value="保存" "  onclick="return toopen()">
 </ul>
</form>
</body>
</html>