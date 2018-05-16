<%@ page  isELIgnored="false" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html> 
<head>  
<link rel="stylesheet" href="http://static.runoob.com/assets/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="http://static.runoob.com/assets/jquery/2.0.3/jquery.min.js"></script>
<script src="http://static.runoob.com/assets/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<script src="bootstrap-paginator"></script>
<style type="text/css">

body {
	/*background-image: url();url为空时，浏览器会把其解析为当前页面地址，再次请求当前页面，导致重复请求问题*/
}
.img-responsive1 {
    display: inline-block;
    height: auto;
    max-width: 30%;
}
.img-responsive {
    height: auto;
    max-width: 70%;
}
</style>
</head>  
<body> 

<form name='form1' action='table' method='post'> <input type="hidden" value="${id}" name="id"> </form>
<nav class="navbar navbar-inverse" role="navigation"> 
     <div class="container-fluid"> 
     <div class="navbar-header"> 
         <a class="navbar-brand" href="#">我的论坛</a> 
     </div> 
     <div> 
         <ul class="nav navbar-nav"> 
             <li class="active"><a href="#">留言板</a></li> 
             <li><a href="javascript:document.form1.submit();">所有用户信息</a></li> 
             <li class="dropdown"> 
                 <a href="#" class="dropdown-toggle" data-toggle="dropdown"> 
                                                         个人中心  <b class="caret"></b> 
                 </a> 
                 <ul class="dropdown-menu"> 
                     <li><a href="selectmine">我的留言</a></li> 
                     <li><a href="edituser/${id}">修改密码</a></li> 
                 </ul> 
             </li> 
         </ul> 
     </div> 
     <div>
		<form class="navbar-form navbar-left" role="search" action="search" id="searchform">
			<div class="form-group">
				<input type="text" class="form-control" placeholder="Search" name="search">
			</div>
			<button type="submit" class="btn btn-default">搜索</button>
		</form>
	</div>
	 <ul class="nav navbar-nav navbar-right">
      <li><a href="javascript:delCookie()"><span class="glyphicon glyphicon-log-in"></span> 退出</a></li>
    </ul>
     </div> 
 </nav>

 <h2 class="text-center">
	 Welcome
	 <small> 留下你的足迹ba</small>
	 <button type="button"class="btn btn-large btn-success"data-toggle="modal" data-target="#myModal1" >留下我的足迹</button>

 </h2>
 <br>
 <script type="text/javascript">


     function delCookie()
     {
         window.location.href = "exit";
         var exp = new Date();
         exp.setTime(exp.getTime() - 1);
         var cval=getCookie("sd");
         if(cval!=null)
             document.cookie= name + "="+cval+";expires="+exp.toGMTString();
     }
 function toopen(noteid){
	 $.ajax({
   	  url:"selectreply",
   	  type:"GET",
      data:{
    	 "noteid" : noteid 
      },
      async:false,
   	  success:function(data){ 
   		$("#replyby"+noteid).text("");
   		$("#replyby"+noteid).append(data);
   	  }
     });
 }
 
function reply1(noteid,title){
	 var date = new Date().toLocaleString(); 
     $("#date1").val(date);
     $("#noteid1").val(noteid);
     $("#title1").val(title);       
}
 
function replyon(){

     $.ajax({
  	  cache: true,
  	  url:"insertreply",
  	  type:"POST",
       data:$('#formreply').serialize(),
  	  async:false,
  	  success:function(data){
  		  if(data=="true"){
  			  $('#myModal').modal('hide'); //设置myModal1关闭
  			  alert("发表成功");
  		  }else{
  			alert("未登录用户，请先登录");
	    	location.href="lode";
  		  }
  	      result = true;
  	  }
    });
	
}

 </script>
 
        <div>
           <c:forEach items="${notelist}"  var="notePad" >
           <div class="panel panel-primary">
	       <div class="panel-heading">
		<h3 class="panel-title">  
		标题：${notePad.title} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;  
		作者：${notePad.id} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; 
		 时间：${notePad.date}</h3>
	    </div>
	       <div class="panel panel-info">

	    <div class="panel-body">
		<p>message：${notePad.content}
		
		</p>
            <%--<img src="upload/${notePad.filename}" class="img-responsive" >--%>
           <img alt="-----来自上帝之手 :)"   class="img-responsive1" onmouseover="this.style.cursor='pointer';this.style.cursor='hand'" onmouseout="this.style.cursor='default'"  src="/uploadfile/upload/${notePad.filename}" onclick="javascript:showimage('/uploadfile/upload/${notePad.filename}');"/>
        </div>
	     <div class="panel-footer">
	     
	     <div class="panel panel-info">
		<div class="panel-heading">
		<a href="#collapseThree${notePad.noteid}"  data-toggle="collapse" ><button type="button" class="btn btn-primary btn-sm" onclick="toopen(${notePad.noteid})">查看</button></a>
	    <button type="button" class="btn btn-primary btn-sm"data-toggle="modal" data-target="#myModal"  onclick="reply1(${notePad.noteid},'${notePad.title}')">回复</button>
	     <a><input type="button" value="删除" class="btn btn-primary btn-sm" onclick="del(${notePad.noteid})"></input></a>
		</div>
		<div id="collapseThree${notePad.noteid}"class="panel-collapse collapse">
			<div class="panel-body" id="replyby${notePad.noteid}">
				
			</div>
		</div>
	</div>
	     
	    </div> 
       </div>
       </div>
       </c:forEach>
        </div> 

<script type="text/javascript">
 function del(noteid){
	 $.ajax({
   	  url:"deletenote",
   	  type:"GET",
      data:{
    	  "noteid":noteid
      },
   	  success:function(data){ 
   		if(data==="true"){
   		alert("删除成功");
   	    location.reload();
   		}else{
   		 location.href="lode";
   		}
   	  }
     });
	 
 }
 function add(){
	    var title = $("#title").val();
	    var id = $("#id11").val();

	    if(id==""){
	    	alert("未登录用户，请先登录");
	    	location.href="lode";
			return false;     
	    }
	    else if(title==""){
		   alert("标题不能为空");
		   return false;      
	    }else{

            var form = new FormData(document.getElementById("upload"));
            $.ajax({
                cache: true,
                url:"/uploadfile/upload.do",
                type:"POST",
                data:form,
                async:false,
                processData:false,
                contentType:false,
                success:function(data){
                    if(data=="true"){
                    }

                }
            });

	       var result=false;
	       var date = new Date().toLocaleString();
	       $("#date").val(date);
	       //$("#filename").val($("#file").value);
	       $.ajax({
	    	  cache: true,
	    	  url:"insertnote",
	    	  type:"POST",
              data:$('#formid').serialize(),
	    	  async:false,
	    	  success:function(data){
	    		  if(data=="true"){
                      //_s();
	    			  $('#myModal1').modal('hide'); //设置myModal1关闭
	    			  alert("发表成功");
	    			  location.reload();
	    		  }else{
	    			  alert("发表失败");
	    		  }
	    	      result = true;
	    	  }
	      });
	    return result;
	    }     
 }
 function _s() {
             var f = document.getElementsByName("file").files;
             //上次修改时间
            alert(f[0].lastModifiedDate);
            //名称
             alert(f[0].name);
             //大小 字节
             alert(f[0].size);
             //类型
             alert(f[0].type);
          }
 function showimage(source)
 {
     $("#ShowImage_Form").find("#img_show").html("<image src='"+source+"' class='carousel-inner img-responsive img-rounded center-block' />");
     $("#ShowImage_Form").modal();
 }

</script>       
      
            
<!-- 回复框（Modal） -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title" id="myModalLabel">
					回复留言
				</h4>
			</div>
			<div class="input-group"> 
             <span class="input-group-addon">回复：</span> 
             <form class="form-horizontal" role="form" action="insertreply" method="post" id="formreply">
             <input type="hidden" class="form-control" id="noteid1" name="noteid1"> 
             <input type="hidden" class="form-control"   id="title1" name="title"> 
             <input type="hidden" class="form-control" value="${id}"  id="id1" name="id"> 
             <input type="hidden" class="form-control"  id="date1" name="date"> 
             <input type="text" class="form-control" placeholder="Reply" id="replycontent"  name="replycontent">   
             </form>
            </div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭
				</button>
				<button type="button" class="btn btn-primary" onclick="replyon()">
					提交
				</button>
			</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
	
		
</div>

<!-- 留言框（Modal） -->
 <div class="modal fade" id="myModal1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">
					&times;
				</button>
				<h4 class="modal-title" id="myModalLabel">
					发表留言
				</h4>
			</div>
	 		<form class="form-horizontal" role="form" action="insertnote" method="post" id="formid">
	     <div class="form-group">
		<label for="firstname" class="col-sm-2 control-label">标题</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" id="title"  name="title"
				   placeholder="title">
	 </div>
	 </div>
	 <div class="form-group">
		<label for="lastname" class="col-sm-2 control-label">作者</label>
		<div class="col-sm-10">
			<input type="text" class="form-control" id="id11"  name="id" value="${id}" readonly="true"
				   placeholder="id">
		</div>
	</div>
	<div class="form-group">
	   <label for="lastname"  class="col-sm-2 control-label">留言内容</label>
	   <div class="col-sm-10">
		<textarea class="form-control" rows="3" id="content"  name="content" placeholder="content"></textarea>
		<input type="hidden" class="form-control" id="date" value="" name="date">
           <input type="hidden" class="form-control" id="filename" value="" name="filename">
		</div>	
	</div>
</form>
            <div class="form-group">
                <label for="lastname"  class="col-sm-2 control-label">上传图片</label>
                <form action="upload.do" method="post" enctype="multipart/form-data" id="upload">
                    <input type="file" name="file" id = "file"/>
                </form>
            </div>
	<div class="modal-footer">
			<button type="button" class="btn btn-default" data-dismiss="modal">关闭
			</button>
			<button type="button" class="btn btn-primary" id="add" onclick="return add()">
				提交
			</button>
	</div>
		</div><!-- /.modal-content -->
	</div><!-- /.modal -->
</div>




﻿<div id="ShowImage_Form" class="modal">
    <div class="modal-header">
        <button data-dismiss="modal" class="close" type="button"></button>
    </div>
    <div class="modal-body">
        <div id="img_show">
        </div>
    </div>
</div>


   <div id="example" style="text-align: center"> <ul id="pageLimit"></ul> </div>
<script type="text/javascript">
$('#pageLimit').bootstrapPaginator({    
    currentPage: 1,    
    totalPages: 10,    
    size:"normal",    
    bootstrapMajorVersion: 3,    
    alignment:"right",    
    numberOfPages:5,    
    itemTexts: function (type, page, current) {        
        switch (type) {            
        case "first": return "首页";            
        case "prev": return "上一页";            
        case "next": return "下一页";            
        case "last": return "末页";            
        case "page": return page;
        }
    },
    onPageClicked: function (event, originalEvent, type, page) { //异步换页
        $.post("/Exam/GetExamLogs",{page:page,take:2},function(data) {
            $("#logtable").html(data);
        });
    }

});
</script>
    <center>       
    <p>
      <button type="button"class="btn btn-large btn-success"data-toggle="modal" data-target="#myModal1" >留下足迹</button>
    </p>
    </center>
    <br>    

 
 
</body>    
</html>