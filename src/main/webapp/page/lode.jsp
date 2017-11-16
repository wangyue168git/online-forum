<%@page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>


<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>登陆界面</title>
<script type="text/javascript" src="jquery"></script>
<style type="text/css">

html, body, div, span, applet, object, iframe,
h1, h2, h3, h4, h5, h6, p, blockquote, pre,
a, abbr, acronym, address, big, cite, code,
del, dfn, em, img, ins, kbd, q, s, samp,
small, strike, strong, sub, sup, tt, var,
b, u, i, center,
dl, dt, dd, ol, ul, li,
fieldset, form, label, legend,
table, caption, tbody, tfoot, thead, tr, th, td,
article, aside, canvas, details, embed,
figure, figcaption, footer, header, hgroup,
menu, nav, output, ruby, section, summary,
time, mark, audio, video {
  margin: 0;
  padding: 0;
  border: 0;
  font-size: 100%;
  font: inherit;
  vertical-align: baseline;
}

rticle, aside, details, figcaption, figure,
footer, header, hgroup, menu, nav, section {
  display: block;
}

body {
	line-height: 1;
	text-align: center;
}

ol, ul {
  list-style: none;
}

blockquote, q {
  quotes: none;
}

blockquote:before, blockquote:after,
q:before, q:after {
  content: '';
  content: none;
}

table {
  border-collapse: collapse;
  border-spacing: 0;
}





body {
	font: 12px/20px 'Lucida Grande', Verdana, sans-serif;
	color: #000000;
	background: #ebc9a2;
	background-image: url(back);
}

input, textarea, select, label {
  font-family: inherit;
  font-size: 12px;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  box-sizing: border-box;
}
.login {
  margin: 50px auto;
  padding: 45px 55px;
  width: 200px;
  background: #3f65b7;
  background-clip: padding-box;
  border: 1px solid #172b4e;
  border-bottom-color: #142647;
  border-radius: 5px;
  background-image: -webkit-radial-gradient(cover, #437dd6, #3960a6);
  background-image: -moz-radial-gradient(cover, #437dd6, #3960a6);
  background-image: -o-radial-gradient(cover, #437dd6, #3960a6);
  background-image: radial-gradient(cover, #437dd6, #3960a6);
  -webkit-box-shadow: inset 0 1px rgba(255, 255, 255, 0.3), inset 0 0 1px 1px rgba(255, 255, 255, 0.1), 0 2px 10px rgba(0, 0, 0, 0.5);
  box-shadow: inset 0 1px rgba(255, 255, 255, 0.3), inset 0 0 1px 1px rgba(255, 255, 255, 0.1), 0 2px 10px rgba(0, 0, 0, 0.5);
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
  display: block;
  width: 105%;
  height: 34px;
  margin-bottom: 20px;
  padding: 0 9px;
  color: white;
  text-shadow: 0 1px black;
  background: #2b3e5d;
  border: 1px solid #15243b;
  border-top-color: #0d1827;
  border-radius: 4px;
  
  background-image: -webkit-linear-gradient(top, rgba(0, 0, 0, 0.35), rgba(0, 0, 0, 0.2) 20%, rgba(0, 0, 0, 0));
  background-image: -moz-linear-gradient(top, rgba(0, 0, 0, 0.35), rgba(0, 0, 0, 0.2) 20%, rgba(0, 0, 0, 0));
  background-image: -o-linear-gradient(top, rgba(0, 0, 0, 0.35), rgba(0, 0, 0, 0.2) 20%, rgba(0, 0, 0, 0));
  background-image: linear-gradient(to bottom, rgba(0, 0, 0, 0.35), rgba(0, 0, 0, 0.2) 20%, rgba(0, 0, 0, 0));
  -webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.3), 0 1px rgba(255, 255, 255, 0.2);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.3), 0 1px rgba(255, 255, 255, 0.2);
}
.login-input:focus {
	outline: 0;
	background-color: #006699;
	-webkit-box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.3), 0 0 4px 1px rgba(255, 255, 255, 0.6);
	box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.3), 0 0 4px 1px rgba(255, 255, 255, 0.6);
}
.lt-ie9 .login-input {
  line-height: 35px;
}
.login-submit {
	display: block;
	width: 105%;
	height: 34px;
	margin-bottom: 15px;
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
.login-help {
	text-align: center;
	text-decoration: underline;
	font-size: 18px;
	color: #3333FF;
}
.classA{
   background-image: url(1);

}
.classB{
   background-image: url(2);

}
</style>
</head>

<body>

<form action="show" class="login" method="post">
    <h1>登陆界面</h1>
    <input type="text" name="name" class="login-input" placeholder="username">
    <input type="password" name="password" id="password" placeholder="password"class="login-input" >
    <input type="submit" value="登录" class="login-submit" >
     <p class="login-help"><a href="sign">注册帐号</a></p>
</form>

></body>
</html>
