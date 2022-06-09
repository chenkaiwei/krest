# KRest

## 介绍
krest是一款基于SpringBoot的轻量级RESTful框架，整合了身份验证、权限控制、通信加密、跨域访问等REST服务中常见的基础功能，并提供极为精简的配置方式。用户只需完成最为必要的个性化配置即可直接使用（如加密策略、秘钥、跨域允许范围等）。最大程度地减小用户在搭建REST服务时的学习和工作成本。

## 产品特性
1. 本产品的核心功能能通过整合shiro+jwt+cryption（通信加密）三个模块实现，并通过委托模式（delegating）将各个模块的配置方式统一整合，最大程度地精简用户的配置和使用方式。同时充分利用SpringBoot的自动装配机制，保留了灵活强大的可扩展空间。
2. 本品不会参与任何数据持久层或远程缓存的读写方式，无论是加解密策略、秘钥，还是用户-权限信息，其存储配置方式均可完全自由地按照用户自行定义的方式实现。唯一需要遵守的是确保权限数据的结构设计符合RBAC规范。
3. 充分运用了shiro1.8版本的新机制，对jwt模块有了更为原生态的兼容。
5. 本项目的demos目录下包含三个功能完善的演示模块以及相应的客户端postman脚本和sql脚本。demo源码中有非常完善的注释说明帮助你快速上手本框架，也可以直接当做企业级项目的初始工程。

> 如果你对本项目所运用的shiro+jwt整合原理感兴趣，想单纯地学习实现两者的整合，可以出门左拐参考我另一个项目：[shiro-jwt-integration](https://gitee.com/ckw1988/shiro-jwt-integration) 我把KRest中所运用的jwt+shiro整合部分的知识单独提取出来做了这份教程和示例。

## 版本要求
* JDK1.8以上

## 快速开始

1. 在pom中导入包（本项目已发布到maven中央库，直接写配置即可。）
   ```xml
    <dependency>
       <groupId>com.chenkaiwei.krest</groupId>
       <artifactId>krest-core</artifactId>
        <version>${最新版本号}</version>
    </dependency>
   ```
    注：您可以在本项目的发布（release）页找到最新的版本号，也可在maven中央库中查看（https://search.maven.org/search?q=a:krest-core ）。

2. 新建一个config类（或在您原有的config类上）实现KrestConfigurer接口
    ```java
    @Configuration
    public class DemoConfig implements KrestConfigurer {  

        //1.按下图方式配置角色-权限映射，返回值中key为角色（Role）名称，value为该角色所拥有的所有权限（Permission）。
       //此处的硬编码仅作示例，您可以将这部分数据以任何您喜欢的方式存储（数据库、钥匙串、文本文件等，均可），只需在本方法中以同步方法取出并确保其按标准格式返回即可。
        @Override
        public Map<String, Collection<String>> configRolePermissionsMap() {
            Map<String, Collection<String>> res=new HashMap<String, Collection<String>>();
            res.put("admin", Arrays.asList("p1","p2","p3","p4"));
            res.put("user", Arrays.asList("p3","p4"));
            return res;
        }
            
        //2.配置jwt Token的加密策略，字符串部分为秘钥
        //同上，该加密策略中的加密方式和秘钥，也可自由实现存取方式，只需返回格式符合要求即可。
        @Override
        public Algorithm configJwtAlgorithm() {
            return Algorithm.HMAC256("mydemosecretkey");
        }

        //3.（可选）配置忽略jwt验证的路径规则，默认配置如下四条。本方法中的语法来自shiro，如果您对路径映射规则有更多的需求，也可一并在本方法中配置。
        @Override
        public void configFilterChainDefinitionMap(Map<String, String> filterRuleMap) {
            //配置不参与token验证的uri
            filterRuleMap.put("/static/*", "anon");
            filterRuleMap.put("/error", "anon");
            filterRuleMap.put("/register", "anon");
            filterRuleMap.put("/login", "anon");
        }
    }
    ```
    
3.  （可选）实现登录方法（即登录成功后发放初始新token）。如果您在本服务中仅须实现已有token的验证和更新发放功能（比如已从别的服务完成登录获取token），则可跳过本步骤。

    在controller中简单实现一个登陆方法，示例如下：
    ```java
    @PostMapping("/login")
    public Map login(@RequestBody User userInput) throws Exception {

        Map res=new HashMap();
        if(userInput.getUsername().equals("zhang3")&&userInput.getPassword().equals("12345")){
            //TODO ↑你自己的验证规则

            JwtUser jwtUser=new JwtUser("zhang3", Arrays.asList("admin"));
            res.put("token",KrestUtil.createJwtTokenByUser(jwtUser));
            //↑ 关键是这一步，将token返回给客户端以供后续请求时验证身份。
    
            res.put("message","login success");
        }else{
            res.put("message","login failed");
            // throw new KrestAuthenticationException("登录失败");

        }
        return res;
    }
    ```
    登陆成功后使用JwtUser封装用户，并以此生成token，返回给客户端。
    
    本例中zhang3为用户名，admin为其角色（role）。该角色名即为步骤2中角色-权限对照表中的的key，请保证两者的对应。
    
4. 客户端的操作(即客户端部分的Jwt令牌使用规则，已经懂的可以不看)：
    客户端在获取到token后，应将其加上"Bearer "前缀使用。在后续的请求中，只须将该"Bearer "+token的字符串以"Authorization"为属性名加到请求头中，即可自动实现身份验证。
    
    完成后的效果类似下表
    
   | KEY           |VALUE   |   
   | --------------| -------- | 
   | Authorization |Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJhZG1pbiJdLCJleHAiOjE2NDY3OTcyMjEsInVzZXJuYW1lIjoiemhhbmczIn0.HroVIdxf5qmpjWJlOs0QGW7OtaTcjirD9aMViK4oDdI|   
   
   注意：Bearer和令牌字符串之间有且仅有一个半角空格。
      
5. 服务端的操作。只要你完成步骤2中的配置，则对jwt token
的验证工作会全部由框架自动完成。唯一需要你亲自动手的是在请求的返回值中加入以下代码，即可实现Token的自动刷新，下一步骤中配置的过期时间和刷新机制也会同时生效。
    ```java
    resBody.put("token",KrestUtil.createNewJwtTokenIfNeeded());
    ```
   一般建议使用ResponseBodyAdvice等方式统一封装在返回结果中。如此设计是为了避免对用户的返回数据结构进行过多约束。
   
6. （可选）自定义token过期时间和自动刷新时间。

   在application.yml文件中增加如下配置：
   ```yaml
    krest:
      jwt:
        expire-time: 1m
        #↑token过期时间，默认20分钟
        refresh-time-before-expire: 40s
        #↑过期前多久更新token。默认10分钟。若设为与expire-time一致，则每次请求都会刷新。
   ```
   至此，配置完成。
   
7. 运行测试
    
    在controller中加入如下代码
    ```java
    @GetMapping("/permissionDemo")
    @RequiresPermissions("p1")//表示当用户拥有"p1"权限时才被许可访问该方法。role同理，这部分使用来自shiro语法。
    public Map permissionDemo(){
        Map<String,String> res=new HashMap<>();
        res.put("result","you have got the permission [permissionDemo]");
        res.put("token",KrestUtil.createNewJwtTokenIfNeeded());
        return res;
    }
   ```

## 进阶使用

   #### 启用框架自带的用户名-密码登录功能
   本框架包含一套完善的用户名密码登陆机制，通过实现shiro的原生组件实现，可在配置文件中一键开启。配置如下。
   
   ```yaml
    krest:
      enable-username-password-realm: true
            
  ```
   您仅需在config文件中配置一些最必要的设置即可激活该功能。其具体规则参考krest-demo-1源码。
     
   #### 自定义异常返回
   
   1. 继承KrestErrorController并覆盖getErrorResponseBody方法来自定义返回错误时的数据结构。
   
   2. 通过定义全局ExceptionHandler来捕获异常。具体规则参考demo中的GlobalExceptionController文件。
   ```java
    @Slf4j
    @RestControllerAdvice
    public class GlobalExceptionController {
    
        //按你自己的方式来统一返回格式，此处仅做示例，为了好懂就不抽象了。
        @ExceptionHandler({KrestAuthenticationException.class,AuthenticationException.class})
        public ResponseEntity KrestAuthenticationExceptionHandler(KrestAuthenticationException e) {
            log.error("krestExceptionHandler");
            log.error(e.getLocalizedMessage());
    
            Map<String,Object> body=new HashMap<String,Object>();
            body.put("status",HttpStatus.FORBIDDEN.value());//也可以自定义更详细的状态码
            body.put("message",e.getLocalizedMessage());
            body.put("exception",e.getClass().getName());
            body.put("error",HttpStatus.FORBIDDEN.getReasonPhrase());
            return new ResponseEntity(body, HttpStatus.FORBIDDEN);//仅是示例，按需求定义
        }
    
    
        //权限验证错误
        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity unauthorizedExceptionHandler(UnauthorizedException e) {
            log.error("unauthorizedExceptionHandler");
            log.error(e.getLocalizedMessage());
    
            Map<String,Object> body=new HashMap<String,Object>();
            body.put("status",HttpStatus.UNAUTHORIZED.value());
            body.put("message",e.getLocalizedMessage());
            body.put("exception",e.getClass().getName());
            body.put("error",HttpStatus.UNAUTHORIZED.getReasonPhrase());
            return new ResponseEntity(body, HttpStatus.UNAUTHORIZED);//仅是示例，按需求定义
        }
        @ExceptionHandler(Exception.class)
        public ResponseEntity exceptionHandler(Exception e) {
            log.error("exceptionHandler");
            log.error(e.getLocalizedMessage());
            log.error(e.getStackTrace().toString());
    
            Map<String,Object> body=new HashMap<String,Object>();
            body.put("message",e.getLocalizedMessage());
            body.put("exception",e.getClass().getName());
            body.put("error",HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            return new ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR);//仅是示例，按需求定义
        }
    }

   ```
   
   #### 通信加密模块
   
   本框架还包含一个通信加密模块。使用规范如下：
   
   1. 由客户端生成一个临时秘钥(tempSecretKey)，以此为秘钥，使用 对称加密 策略将整条消息体加密（也可只加密您需要的字段）。加密策略须与服务端协商一致。
   
   2. 双方约定一个不对称加密策略，用以加密解密临时秘钥（tempSecretKey）。公钥由客户端维护，私钥由服务端维护。客户端在访问服务端的加密接口前，将临时秘钥用不对称加密的公钥加密后放入头信息的Cryption字段中。伴随在步骤1中已加密的消息体一并发送到服务端对应的接口。
   
   3. 以下是服务端部分：需要先在配置文件中开启接口加密功能：
       ```yml
            krest.cryption.enable-cryption = true
       ```
   
   4. 在Krest配置文件中实现:
      ```java
    
        //解密临时秘钥的策略是一次配置一直使用
        @Override
        public AsymmetricCrypto configTempSecretKeyCryptoAlgorithm() {
    
            String privateKey="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJdor7t0PvE590FArr-hv_pqtsk1R-iXaFX0upUJ8XkmHrMU6qpZM27oZzMOm62r_DzLTWNKZal-QH987OXQj35TnhrbwLxl57PZ6wfV_hggHlMgtnp_7yYJAPgS2mVN0E5VInPmuMcES598pB-1lvnUJ0-386ny_FS9-IUJRMDBAgMBAAECgYEAlmR00aT49FFYiOc_7Lc04v9myltzLtRd3at2PZ4fze-QZN9s7IIn9Y1BHNTwy8ReiuCB4RNAAeiXFks3YFsWe5yHHsW_Y3ntN0Tla_nkVkjm2iG_dIKHS5iY3ERoheR8i0d0T1BnmwbyCwdl7-QWmjVdeZ8YPFxAQ72Wr6DLY6UCQQDwgAGW8rdxbKQjqSIoFTRfSTLfI6Ba3dsb7xNiQE3RSiq_k4LskbnNCAqf7WNy85gNjENX-W8lmP1t6rJqC5tvAkEAoSrFs6HpF2I469ALkZH6iapi7k97W4nlnnOeaNAx9uuXy9hyQiKSGZafSidxPvmbV1qV2CVxc53FhTCD5b4OzwJAK4LtRrMZD1NZiv4hqODVPdwPcSGP9ICpEK-7cQ4zRgdGHq0Ahe6DkB3BVlfrozOBMgpLcNI3ErVQPJ-2scrxzwJAWybfzisCtBD_dI-kG17evkG51mLpt-oUDjwCGfG2cJrqrYXriXAYBZTk3oHUUPPHYe5_1VHICsXu0tePob6OjQJAeZXbdfkNx7-uZ295rTj3Yq3H11uB6hB317eODHtnnCMVH0ww50C9pGnRPO2dEaShCwLUeOucxBim_usmIBaPOw";
            //↑ 用你自己的方式获取秘钥。确保和客户端保存的公钥 成对。
    
            RSA rsa = new RSA(privateKey, null);
       //   byte[] encrypt = StrUtil.bytes(cryptionString, CharsetUtil.CHARSET_UTF_8);//先转成二进制数组
    
            return rsa;//兼容hutool中继承AsymmetricCrypto实现的两种不对称加密方式
        }
    
        //使用临时秘钥解密加密的策略每次请求都需要生成一个新的，所以动词是create不是init。
        public SymmetricCrypto createMessageBodyCryptoAlgorithm(String tempSecretKey) {
    
            AES aes = new AES("CBC", "PKCS5Padding",
                    // 密钥，可以自定义
                    tempSecretKey.getBytes(),
                    // iv加盐，按照实际需求添加
                    "1111222233334444".getBytes());
            return aes;
        }
       ```
      configTempSecretKeyCryptoAlgorithm：对应步骤2中加密临时秘钥的不对称加密策略，此处配置的策略用于解密临时秘钥。
      createMessageBodyCryptoAlgorithm：对应步骤1中使用临时秘钥加密消息体的对称加解密策略，此处生成的策略用于使用临时秘钥解密消息体内容。
      
   5. 配置完成后，只需在controller中将服务端对应的加密接口由@Cryption注解标签修饰，并选择您所需要的加解密策略即可。（该注解包含四种策略：请求时的消息体全加密、返回时的消息体全加密、以上二种叠加、以及自定义的局部消息加密。）
      ```java
        @PostMapping("cryptionTest")
        @Cryption(CryptionModle.WHOLE_REQUEST)//请求时加密（框架自动解密）
        public Map cryptionTest(@RequestBody User inputUser) {
            Map result = new HashMap<>();
            result.put("isEncrypted", true);
            result.put("msgFromClient", inputUser);
            return result;
        }
      ```
   
   6. 前三种加解密模式由框架自动完成，传入/传出消息时即为已按照注解配置解/加密后的内容，全程无需用户参与，只需在客户端做好相应的加密解密操作即可。
  
      自定义局部信息加解密也会自动解析并装配好临时秘钥的算法。不同之处在于使用时在消息体中通过调用KrestUtil.decryptMessageBody和KrestUtil.encryptMessageBody来加/解密您与客户端所约定的相应密文字段。
      
      ```java
        @PostMapping("cryptionCustomize")
        @Cryption(CryptionModle.CUSTOMIZE)
        public Map cryptionCustomize(@RequestBody Map inputObj){
    
            String cryptionPart=(String)inputObj.get("cryptionPart");
            String decryptMessageBody=KrestUtil.decryptMessageBody(cryptionPart);
            //↑解密请求信息中被加密的部分
    
            Map result = new HashMap<>();
            result.put("cryptionPart", decryptMessageBody);
            result.put("nocryptionPart", inputObj.get("nocryptionPart"));
            result.put("whoyouare",KrestUtil.getJwtUser());
            result.put("token",KrestUtil.createNewJwtTokenIfNeeded());
            return result;
        }
      ```
   
   7. 本模块使用的加解密方式来自huTool中的加解密策略，如需扩展可自行学习相应规则。
   
   8. 本模块完整的示例代码参考krest-demo-1。模块中还包含了一个postman脚本，可将其导入postman客户端以供参考。

## 后续开发计划
  * 完善javadoc注解
  * 增加ip地址校验策略，防token盗用
  * 增加一个自动在返回参数的最外层增加token的机制，用application.yml控制开关
  * 其他欢迎补充
  
## 联系作者
欢迎试用并留下宝贵意见，帮助本产品进一步成熟和完善。如您在本产品的使用中有任何疑问或交流建议，请随时联系作者。
* Email: ckw1988@163.com
* QQ群：  818464800（推荐）
* 在github或gitee的本项目的issue下留言也是可以的