package com.chenkaiwei.krest.cryption.annotation;


//
//自定义注解教程： https://blog.csdn.net/wsm890325/article/details/93868894


import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})//除了方法上也可放在类（ElementType.TYPE）上，放在类（一般为Controller）上则该controller下定义的所有接口都采用该加密解密规则
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cryption {

    //接口的加密模式：入参是加密的（须解密），返回值须加密，二者都要，二者都不要


    CryptionModle value() default CryptionModle.WHOLE_REQUEST;// 默认：仅入参须解密，返回值不加密（比如注册用户、修改密码等操作，返回只是一个success）

//    boolean isCrytionForced() default true;//是否强制加密，一般都强制吧？会有需求做个加密不加密都一样接受的接口么。先不做了哪天有闲心了再实现吧。

}
//和自定义注解天然配对的是Aspect，但是这里要用Advice更合适。
//所以Aspect和Advice是啥关系，回头也要去 弄明白