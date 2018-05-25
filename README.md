# online-forum (网上论坛)
独立开发的一个个人网站的web应用，用户发表博客留言。<br>
后端服务器：mySQL（数据库）redis，spring mvc+mybatis jsp  ，中间件：Tomcat  反向代理服务器 nginx<br>
前端：jquery,ajax，使用bootstrap框架插件。<br>
通过这个项目可以熟悉spring mvc框架的使用，对于spring源码进行改进学习，整个项目设计从后端数据库服务器的设计，到中间件的使用，再到前端页面的设计（尤其对对jquery的学习，及ajax这个前端处理机制的设计，类似前后端异步交互的实现）,对于服务器的搭建，Jsp+servlet的理解都很有帮助。
java Web一个完整的开发与部署流程尽收眼底。

目前已将该应用部署到aws服务器上，链接附上->
[我的论坛](http://18.221.27.170/1.03)

关于远程Linux服务器的环境搭建，与应用的自动化部署，在我的博客可以看到详细教程
[web应用服务器端的自动化部署](https://blog.csdn.net/qq_32231495/article/details/80304296)

增加爬虫框架，采用reids做消息中间件，多线程爬取。<br>

