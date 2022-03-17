package com.chenkaiwei.krest.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "krest.cryption")
public class KrestCryptionProperties {
    boolean enableCryption = false;
}
