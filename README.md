# KRest

## 介绍
krest是一款基于RESTful规则的轻量级框架套装，旨在整合无状态服务中的常见基础模块，从而帮助开发人员降低工作难度和学习成本。功能包含身份验证、权限控制、通信加密等。具有耦合低、侵入性小、配置灵活、普适性强等特点。


## 产品特性
* 核心功能由shiro+jwt实现，通用性好，规则成熟可靠。
* 配置方式简洁轻便。本产品采用代理模式，将原本繁琐的配置工作整合到一个配置文件中来，最大程度地帮助用户精简配置工作。用户只需实现少量最必要配置接口，即可完成一整套功能完善的RESTful服务框架的搭建。
* 耦合低，侵入性小，普适性好。本框架不会接管任何数据库或远程缓存配置，用户可以完全保留原本的数据存储获取方式。只需确保权限数据的设计符合RBAC规范，即可无障碍接入本框架。
* 可扩展性好，使用自由灵活。本框架采用SpringBoot的自动装配机制实现对shiro和jwt的封装，原本shiro框架的功能依然可以完全不受影响地自定义和扩展。
* 继承了shiro+jwt框架的所有优点。诸如更节约服务器资源、更便于分布式扩展及更易于适配移动app、小程序、网页开发等多种客户端平台共同使用等，不再赘述。
* 本框架另行包含一个通信加密模块，配置简单功能强大。

## 版本要求

* JDK1.8以上

## 快速开始

1.  导入包
    ```xml
    <dependency>
       <groupId>com.chenkaiwei.krest</groupId>
       <artifactId>krest-core</artifactId>
        <version>${最新是啥就写啥}</version>
    </dependency>
    ```
    
2.  新建一个config类（或在您原有的config类上）实现KrestConfigurer接口
    ```java
        @Configuration
        public class DemoConfig implements KrestConfigurer {  
            //按下图方式配置角色-权限映射，返回值中key为角色（Role）名称，value为该角色所拥有的所有权限（Permission）
            
        @Override
        public Map<String, Collection<String>> configRolePermissionsMap() {
            Map<String, Collection<String>> res=new HashMap<String, Collection<String>>();
            res.put("admin", Arrays.asList("p1","p2","p3","p4"));
            res.put("user", Arrays.asList("p3","p4"));
            return res;
        }
            //返回jwt Token的加密策略，字符串部分为秘钥
        @Override
        public Algorithm configJwtAlgorithm() {
            return Algorithm.HMAC256("mydemosecretkey");
        }
        }
    ```
    
3.  （可选）实现登录方法。如果您的客户端已经拥有了token，则可跳过本步骤。

    在您的登录方法中加入以下代码：
    ```java
        //if(您自己的登录逻辑，验证通过后：)
        JwtUser jwtUser=new JwtUser("zhang3", Arrays.asList("admin"));
        //↑ 您确认已登陆的用户及其角色信息。
        resultBody.put("token",KrestUtil.createJwtTokenByUser(jwtUser));
    ```
    
    本段代码的功能为以 该用户名和该用户的角色（Role）列表生成一个token（按照jwt规则实现）并返回给客户端。基于jwt验证机制，只需将token返回给客户端即视为已经登录完成。
    
    本例中zhang3为用户名，admin为其角色。该角色名必须与步骤2中的key对应。
    
4. 客户端的操作(即Jwt令牌使用规则)：
    客户端在获取到token后，应将其加上"Bearer "前缀使用。在后续的请求中，只须将该"Bearer "+token的字符串以"Authorization"为属性名加到请求头中，即可自动实现身份验证。
    
    完成后的效果类似下表
    
   | KEY           |VALUE   |   
   | --------------| -------- | 
   | Authorization |Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJhZG1pbiJdLCJleHAiOjE2NDY3OTcyMjEsInVzZXJuYW1lIjoiemhhbmczIn0.HroVIdxf5qmpjWJlOs0QGW7OtaTcjirD9aMViK4oDdI|   
   
   注意：Bearer和令牌字符串之间有且仅有一个半角空格。
      
5. 服务端的操作，在所有业务请求的返回值中，加入以下代码，即可按照步骤6中配置的过期时间和刷新时间实现Token的自动刷新。
    ```java
    resBody.put("token",KrestUtil.createNewJwtTokenIfNeeded());
    ```
   一般建议使用ResponseBodyAdvice等方式统一封装在返回结果中。
   
6. （可选）自定义token过期时间和自动刷新时间。

   在application.yml文件中增加如下配置：
   ```yaml
    krest:
      jwt:
        expire-time: 1m
        #↑token过期时间，默认20分钟
        refresh-time-before-expire: 40s
        #↑过期前多久更新token。默认10分钟。若设为与expire-time一致则每次请求都会刷新。
   ```
   若您使用application.properties则自行更改对应配置，不再赘述。
   
   至此，配置完成。
   
7. 运行测试
    
    在controller中加入如下代码
    ```java
    @GetMapping("/permissionDemo")
    @RequiresPermissions("p1")//当用户拥有"p1"权限时才被许可访问该方法。以此便捷地实现粗粒度的权限控制。
    public Map permissionDemo(){
        Map<String,String> res=new HashMap<>();
        res.put("result","you have got the permission [permissionDemo]");
        res.put("token",KrestUtil.createNewJwtTokenIfNeeded());


        return res;
    }
   ```

## 进阶使用

   #### 启用更完善的用户名-密码登录功能
   本框架通过实现shiro的realm，自定义了一套用户名密码登陆机制，可在配置文件中一键开启。配置如下。
   
   ```yaml
    krest:
      enable-username-password-realm: true
            
  ```
   您仅需在config文件中配置一些最必要的设置即可实现。其具体规则参考krest-demo-1源码。
     
   #### 自定义异常返回
   
   1. 继承KrestErrorController并覆盖getErrorResponseBody方法来自定义返回错误时的数据结构。
   
   2. 通过定义全局ExceptionHandler来捕获异常。具体规则参考demo中的GlobalExceptionController文件。
   
   #### 通信加密模块
   
   本框架还包含一个通信加密模块。使用规范如下：
   
   1. 由客户端生成一个临时秘钥(tempSecretKey)，使用 对称加密 策略将整条消息体加密（也可只加密您需要的字段）。加密策略由客户端服务端约定。
   
   2. 与服务端约定一个不对称加密策略，仅用以加密解密临时秘钥（tempSecretKey）。公钥由客户端维护，私钥由服务端维护。客户端在访问服务端的加密接口前，将临时秘钥用不对称加密的公钥加密后放入头信息的Cryption字段中。伴随经临时秘钥加密的消息体一并发送到服务端对应的接口。
   
   3. 以下是服务端部分：需要先在配置文件中开启接口加密功能：
   ```yml
        cryption.enable-cryption = true
   ```
   
   4. 需要实现的配置为 configTempSecretKeyCryptoAlgorithm（即步骤2中的加解密临时秘钥的不对称加密策略）和createMessageBodyCryptoAlgorithm（即步骤1中使用临时秘钥加解密消息体的对称加解密策略）两处
   
   5. 服务端对应的加密接口由@Cryption注解标签修饰。该注解包含四种策略：请求时的消息体全加密、返回时的消息体全加密、以上二种叠加、自定义的局部消息加密。
   
   6. 前三种加解密模式由框架自动完成。自定义局部信息加解密，通过调用KrestUtil.decryptMessageBody和KrestUtil.encryptMessageBody来加/解密您与客户端所约定的相应密文字段来实现。
   
   7. 在服务端加密配置和具体实现的语法参考krest-demo-1。在客户端将KrestDemo.postman_collection.json导入postman，参考这个。

## 后续开发计划
  * 完善javadoc注解
   
## 联系作者
欢迎试用并留下宝贵意见，帮助本产品逐步完善和成熟。如您在本产品的使用中有任何疑问或交流建议，请随时联系作者。
* Email:ckw1988@163.com
* QQ群：818464800（推荐）