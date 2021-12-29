# 教务系统对接模块

## 一. 简介
+ 本项目是微校园项目的子项目，用于获取GDUPT教务系统中的成绩、课表以及素拓分数据。
+ 涉及到技术：SpringCloud | Consul | Mybatis | MySql | Redis

## 二. 部署
>+ 执行mvn package之后，在docker中找到镜像即可部署。

>**提示**：因微校园项目要求，本模块需配合Docker和Consul使用，如需在Tomcat下运行，请按下列步骤修改：
>1. 找到并打开`src/main/resources/application.yml`
>2. 将`consul`中的内容全部删除

## 三.模块暴露的接口
+ [API](API.md)

## 四. 问题记录
1. 执行maven package之后，运行jar，提示  
`Could not resolve placeholder 'spring.datasource.url'`  
* **解决方法：**  
  SpringBoot在没有正常读取配置文件时就会报这个错误。  
  原因是maven在package过程中默认忽略了.yml后缀的配置文件，需要在pom.xml文件中加入  
  ```xml
  <resource>
    <directory>src/main/resources</directory>
    <filtering>true</filtering>
    <includes>
      <include>**/*.yml</include>
    </includes>
  </resource>
  ```
修改完pom.xml后执行`mvn clean`再`mvn package`即可。

2. 将项目编译成Docker镜像并运行，提示：
```shell
Caused by: org.apache.http.conn.HttpHostConnectExcepion:
Connect to localhost:8500 [localhost/127.0.0.1] failed:Donetion refused(Connection refused)
```
* **解决方法：**  
  Docker容器相当于一个小型虚拟机（大概），连接localhost是必然连不上的。   
  - 首先，使用以下命令启动Consul  
  `consul agent -dev -client 0.0.0.0 -ui -bind 192.168.1.11`  
  其中，`-client`用来绑定Consul客户端，只有绑定的客户端才可以连接，而`-client 0.0.0.0` 则是所有客户端都可以连接
  `-bind`指令用来绑定服务器的ip，这里绑定的是本机ip。  
  - 其次，需要将配置文件中的
```yml
spring.cloud.consul.host = localhost
```
　　修改成
```yml
spring.cloud.consul.host = host.docker.internal
```
> `host.docker.internal` 指向的是本机ip

3. 项目成功部署到docker容器后，并成功连接至Consul后，Consul提示:  
`All service check failing`，点进节点后提示:   
`Get 'http://URL:8080/actuator/health" : context deadline exceeded(Client.Trimeout exceeded while awaiting headera)`

* **解决方法：**  
SpringCloud中Consul的心跳协议默认是关闭的，在SpringCloud的配置文件中添加：    
```yml
spring.cloud.consul.discovery.heartbeat.enabled = true
```


