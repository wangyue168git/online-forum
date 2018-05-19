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


**爬虫部分介绍**

增加爬虫框架，采用reids做消息中间件，分布式爬取。<br>
具体介绍：<br>
spider有两个模块，一个单线程模块和一个多线程模块，多线程模块采用统一的线程池调度执行，任务分发由监听线程负责。<br>
具体为spider开启一个监听线程，去监听任务队列，当队列没有任务时，利用condition.await()将监听线程挂起，阻塞时间最大为30s，当有任务添加进入队列时，利用signal将监听线程唤醒，继续分发任务给线程池去调度执行。当监听线程阻塞时间达到30s，即30s内没有新任务加入队列，此时，就可判定为爬虫任务结束，退出监听，任务结束。<br>
公共线程池对newCachedThreadPool进行封装，利用线程间通信控制线程池中线程的最大存活数（0，N）。<br>
对于多线程情景下由于速度过快导致的问题，可以通过rndSleepTime参数设置，该值负责将多线程请求错开执行，<br>
具体实现为：<br>
sleep((int) (Math.random() * rndSleepTime + 1000));<br>
另一个参数sleepTime负责所有请求的统一休眠时间，通过这两个参数可以降低访问频率。<br>

