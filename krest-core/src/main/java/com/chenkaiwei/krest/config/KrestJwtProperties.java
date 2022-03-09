package com.chenkaiwei.krest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Data
@ConfigurationProperties(prefix = "krest.jwt")
public class KrestJwtProperties {

//    @DurationUnit(ChronoUnit.DAYS)//不写时默认毫秒
//            Duration durationsDemo;

    private Duration expireTime = Duration.ofMinutes(20) ;//默认20分钟

    private Duration refreshTimeBeforeExpire = Duration.ofMinutes(10) ;//过期前多久更新。默认10分钟
}
