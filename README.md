### 一、背景
> 基于Spring Boot定制一些简单易用的自动配置工具


### 二、当前已内置开箱即用的配置
1. jersey、jersey-swagger以及jersey-client
```
须引入以下jar：
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jersey</artifactId>
    </dependency>
    <dependency>
        <groupId>io.swagger</groupId>
        <artifactId>swagger-jersey2-jaxrs</artifactId>
    </dependency>
    <dependency>
        <groupId>com.hiekn.boot</groupId>
        <artifactId>mcn-spring-boot-starter</artifactId>
    </dependency>
```
2. elasticsearch client
```
须引入以下jar：
    <dependency>
        <groupId>org.elasticsearch.client</groupId>
        <artifactId>transport</artifactId>
    </dependency>
    <dependency>
        <groupId>org.elasticsearch</groupId>
        <artifactId>elasticsearch</artifactId>
    </dependency>
    <dependency>
        <groupId>com.hiekn.boot</groupId>
        <artifactId>mcn-spring-boot-starter</artifactId>
    </dependency>
```
3. jwt
```
须引入以下jar：
    <dependency>
        <groupId>com.auth0</groupId>
        <artifactId>java-jwt</artifactId>
    </dependency>
    <dependency>
        <groupId>com.hiekn.boot</groupId>
        <artifactId>mcn-spring-boot-starter</artifactId>
    </dependency>
```
4. 七牛上传
```
须引入以下jar：
      <dependency>
        <groupId>com.qiniu</groupId>
        <artifactId>qiniu-java-sdk</artifactId>
    </dependency>
    <dependency>
        <groupId>com.hiekn.boot</groupId>
        <artifactId>mcn-spring-boot-starter</artifactId>
    </dependency>
```
5. 多数据源配置

### 三、内置一个公用的基础模型
1. 【**建议继承**】BaseMapper
- 定义通用实体增删改查
2. 【**建议实现**】BaseService
- 定义通用实体增删改查
3. 【**建议继承**】BaseServiceImpl
- 实现通用实体增删改查
4. CacheUtils
5. Mybatis相关自动生成器MybatisGenUtil
- 配置好generator.properties和generatorConfig.xml相关项
- 执行MybatisGenUtil.genMapperAndXml()

### 三、内置部分Boot未集成的框架版本
1. Jdk1.8
2. jersey 2.25.1
3. commons-lang3 3.7
4. guava 23.0
5. jwt 3.1.0
6. elasticsearch 5.5.3
7. mybatis-generator-core 1.3.6
8. mybatis-spring-boot-starter 1.3.2
9. commons-io 2.6
10. fastjson 1.2.47
11. qiniu-java-sdk 7.0.7

```
Spring Boot自身继承的可直接查看其pom.xml
```
### 四、MCN内置默认配置(包括：server/tomcat、servlet、mybatis、tomcat-jdbc、log-path、jersey-swagger)
```
#servlet拦截路径默认api
spring.jersey.application-path=api
#server默认端口
server.port=8080
#mybatis数据模型默认别名包
mybatis.type-aliases-package=${jersey.swagger.base-package}.bean
#mybatis，mapper文件默认路径
mybatis.mapper-locations=classpath:mapper/*.xml
#mybatis，handlers默认包
mybatis.type-handlers-package=${jersey.swagger.base-package}.dao.handler
#mybatis，默认使用驼峰
mybatis.configuration.map-underscore-to-camel-case=true

spring.datasource.tomcat.test-while-idle=true
spring.datasource.tomcat.test-on-borrow=false
spring.datasource.tomcat.test-on-return=false
spring.datasource.tomcat.validation-query=SELECT 1 FROM DUAL
spring.datasource.tomcat.time-between-eviction-runs-millis=300000
spring.datasource.tomcat.min-evictable-idle-time-millis=1800000
spring.datasource.tomcat.initial-size=5
spring.datasource.tomcat.max-active=50
spring.datasource.tomcat.max-wait=60000
spring.datasource.tomcat.min-idle=5
spring.datasource.tomcat.max-idle=20

server.tomcat.max-threads=800
server.tomcat.accept-count=1000
server.tomcat.uri-encoding=UTF-8
#默认服务器路径
server.tomcat.basedir=/work/tomcat
server.tomcat.access-log-enabled=true

spring.http.encoding.force=true

#log，默认日志路径
logging.path=${server.tomcat.basedir}/project_logs/${spring.application.name}

#默认api版本，读取pom version
jersey.swagger.version=@project.version@
#默认title
jersey.swagger.title=${spring.application.name} API
jersey.swagger.port=${server.port}
jersey.swagger.base-path=${spring.jersey.application-path}
#默认jersey资源包
jersey.swagger.resource-package=${jersey.swagger.base-package}.rest
```


### 五、使用注意事项
1. 必须设置spring.application.name的值，建议使用项目名

### 六、案例
1. 以[meta-boot](https://github.com/kse-music/meta-boot)为例
2. 引入mcn-projects作为父依赖

```
<parent>
    <groupId>com.hiekn.boot</groupId>
    <artifactId>mcn-projects</artifactId>
    <version>最新稳定版</version>
</parent>
```
3. 完整pom配置
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>com.hiekn</groupId>
	<artifactId>meta-boot</artifactId>
	<version>1.0-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>meta-boot</name>
	<description>meta-boot project for Spring Boot</description>

    <parent>
        <groupId>com.hiekn.boot</groupId>
        <artifactId>mcn-projects</artifactId>
        <version>2.7.5</version>
    </parent>

 <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jersey</artifactId>
        </dependency>
        <dependency>
            <groupId>io.swagger</groupId>
            <artifactId>swagger-jersey2-jaxrs</artifactId>
        </dependency>

        <dependency>
            <groupId>com.hiekn.boot</groupId>
            <artifactId>mcn-spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
        </dependency>
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
        </dependency>

        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-core</artifactId>
            <scope>provided</scope>
        </dependency>

        <!--与cloud集成，请引用此包-->
        <!--<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>-->
        <!--开启boot应用监控，请引用此包-->
        <!--<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>-->

    </dependencies>

    <build>
        <finalName>meta-boot</finalName>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <skipTests>true</skipTests>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>

```

4. 完整application配置
```
spring.application.name=meta-boot
server.port=8888

spring.profiles.active=dev

#服务注册地址
#eureka.client.serviceUrl.defaultZone=http://192.168.1.159:9000/eureka/
#关闭安全控制，1.5.x版本以上默认开启
#management.security.enabled=false
#eureka client显示ip
#eureka.instance.preferIpAddress=true

spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://192.168.1.7:3306/console?characterEncoding=utf8&amp;autoReconnect=true&amp;failOverReadOnly=false
spring.datasource.username=root
spring.datasource.password=root

spring.redis.host=192.168.1.7

spring.data.mongodb.host=192.168.1.7
spring.data.mongodb.database=db

elasticsearch.host=192.168.1.7:9300
elasticsearch.cluster-name=docker-es
```

5. 与spring cloud集成
- 去掉pom和application.properties注释部分即可，别忘记修改注册中心地址