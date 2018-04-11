### 一、背景
> 基于Spring Boot定制一些简单易用的自动配置工具


### 二、当前已内置开箱即用的配置
1. 【**必选**】jersey、jersey-swagger以及jersey-client
2. elasticsearch client
3. jwt

### 三、内置一个公用的基础模型
1. 【**建议继承**】BaseMapper
2. 【**建议实现**】BaseService
3. CacheUtils
4. Mybatis相关自动生成器MybatisGenUtil

### 三、内置部分Boot未集成的框架版本
1. Jdk1.8
2. jersey 2.25.1
3. commons-lang3 3.7
4. guava 23.0
5. jwt 3.1.0
6. elasticsearch 5.5.3
7. mybatis-generator-core 1.3.6
8. mybatis-spring-boot-starter 1.3.2

```
Spring Boot自身继承的可直接查看其pom.xml
```

### 四、案例
1. 以[meta-boot](https://github.com/kse-music/meta-boot)为例
2. 引入mcn-projects作为父依赖

```
<parent>
    <groupId>com.hiekn.boot</groupId>
    <artifactId>mcn-projects</artifactId>
    <version>2.2.3</version>
</parent>
```
3. 以jersey配置为例
- jersey.swagger.init：是否初始化swagger，默认true
- jersey.swagger.base-package：项目基础package
- jersey.swagger.version：项目API版本
- jersey.swagger.title：API标题，默认API
- jersey.swagger.ip：服务器地址，默认127.0.0.1
- jersey.swagger.port：服务器端口，默认8080
- jersey.swagger.base-path：拦截基础路径，默认/
- jersey.swagger.resource-package:rest接口所在package
```
例如：
jersey.swagger.base-package=com.hiekn.metaboot
jersey.swagger.version=@project.version@
jersey.swagger.title=${spring.application.name} API
jersey.swagger.port=${server.port}
jersey.swagger.base-path=${spring.jersey.application-path}
jersey.swagger.resource-package=${jersey.swagger.base-package}.rest

说明：值中变量是Spring boot内置变量，强烈建议必须配置
spring.application.name：boot应用名称，建议与项目名称一致
server.port：服务器端口号，默认使用tomcat
spring.jersey.application-path：servlet拦截基础路径
```

4. 完整pom配置
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
        <version>2.2.3</version>
    </parent>


    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <dependencies>

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
            <version>1.3.2</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
            <version>5.5.3</version>
        </dependency>
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch</artifactId>
            <version>5.5.3</version>
        </dependency>

        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.generator</groupId>
            <artifactId>mybatis-generator-core</artifactId>
            <version>1.3.6</version>
            <scope>provided</scope>
        </dependency>
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

5. 完整application配置
```
spring.application.name=meta-boot
jersey.swagger.base-package=com.hiekn.metaboot
server.port=8888
spring.jersey.application-path=api

mybatis.type-aliases-package=${jersey.swagger.base-package}.bean
mybatis.mapper-locations=classpath:mapper/*.xml

spring.profiles.active=dev

spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://192.168.1.7:3306/console?characterEncoding=utf8&amp;autoReconnect=true&amp;failOverReadOnly=false
spring.datasource.username=root
spring.datasource.password=root
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

spring.redis.host=192.168.1.7

spring.data.mongodb.host=192.168.1.7
spring.data.mongodb.database=db

elasticsearch.host=192.168.1.7:9300
elasticsearch.cluster-name=docker-es

server.tomcat.max-threads=800
server.tomcat.accept-count=1000
server.tomcat.uri-encoding=UTF-8
server.tomcat.basedir=/work/tomcat
server.tomcat.access-log-enabled=true

spring.http.encoding.force=true

logging.path=${server.tomcat.basedir}/project_logs/${spring.application.name}

filter.request=false

jersey.swagger.version=@project.version@
jersey.swagger.title=${spring.application.name} API
jersey.swagger.port=${server.port}
jersey.swagger.base-path=${spring.jersey.application-path}
jersey.swagger.resource-package=${jersey.swagger.base-package}.rest
```

6. 与spring cloud集成
- 去掉pom和application.properties注释部分即可，别忘记修改注册中心地址