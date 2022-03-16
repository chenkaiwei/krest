package com.chenkaiwei.krest.cryption.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.chenkaiwei.krest.config.KrestCryptionProperties;
import com.chenkaiwei.krest.config.KrestProperties;
import com.chenkaiwei.krest.cryption.CryptionInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(KrestCryptionProperties.class)
@Import(CryptionInterceptor.class)
public class KrestMvcConfigurer implements WebMvcConfigurer {//TODO 看源码应该是支持多个实现并存的。回头再确认下。

    @Autowired
    KrestCryptionProperties  krestCryptionProperties;

    @Autowired
    CryptionInterceptor cryptionInterceptor;
//
//    @Autowired
//    FastJsonHttpMessageConverter fastJsonHttpMessageConverter;
////
////
////
////
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        //converter的机制是只有第一个符合要求的会被挑出来选中执行read
//        //挑选、执行converter的方法是在AbstractMessageConverterMethodArgumentResolver.readWithMessageConvertersl里
////    HttpMessageConverter是这样转换数据的    https://blog.csdn.net/yusimiao/article/details/90600316
//
//
//        converters.add(0, fastJsonHttpMessageConverter);
//        //插队到第一个，保证能用它时尽量用它
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (krestCryptionProperties.isEnableCryption()){
            registry.addInterceptor(cryptionInterceptor).addPathPatterns("/**");
        }
    }
}
