# zooweb
 
 - zooweb 是 zookeeper的一个客户端，基于淘宝的web客户端改的。

 原地址`http://172.16.1.13:8080/zkWeb-1.0/`的文件是一个war包，需要web容器依赖
 改造成Springboot的前后端分离模式，响应速度更快，更像是一个随用随启的软件了。
 
- 功能：能正常看zonode结构、数据、添加、修改等。
 
- 启动方式：
 
 ```
    mvn clean && mvn package
 ```
 然后运行：`java -jar target/zooweb-1.0.jar`
 通过页面 [http://127.0.0.1:9345](http://127.0.0.1:9345) 访问。


