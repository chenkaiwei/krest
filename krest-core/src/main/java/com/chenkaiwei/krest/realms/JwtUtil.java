package com.chenkaiwei.krest.realms;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chenkaiwei.krest.config.KrestConfigurer;
import com.chenkaiwei.krest.config.KrestJwtProperties;
import com.chenkaiwei.krest.entity.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Slf4j
@Configuration
@EnableConfigurationProperties(KrestJwtProperties.class)
public class JwtUtil {

    private static KrestJwtProperties krestJwtProperties;
    private static KrestConfigurer krestConfigurer;

    private static Algorithm algorithm;

    @Autowired
    public void setKrestJwtProperties(KrestJwtProperties krestJwtProperties){
        JwtUtil.krestJwtProperties=krestJwtProperties;
    }

    @Autowired
    public void setKrestConfigurer(KrestConfigurer krestConfigurer){
        JwtUtil.krestConfigurer =krestConfigurer;
        algorithm=krestConfigurer.createJwtAlgorithm();
    }

    public static String createJwtTokenByUser(JwtUser user) {


        Date date = new Date(System.currentTimeMillis() + krestJwtProperties.getExpireTime().toMillis());
//        Algorithm algorithm = Algorithm.HMAC256(secret);    //使用密钥进行哈希
        // 附带username信息的token
        return JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("roles", user.getRoles())
//                .withClaim("permissions",permissionService.getPermissionsByUser(user))
                .withExpiresAt(date)  //过期时间
                .sign(algorithm);     //签名算法
        //r-p的映射在服务端运行时做，不放进token中
    }


    /**
     * 校验token是否正确
     */
    public static boolean verifyTokenOfUser(String token) throws TokenExpiredException {//user要从sercurityManager拿，确保用户用的是自己的token
        log.info("verifyTokenOfUser");
//        String secret = jwtTokenSecretKey;//

        //根据密钥生成JWT效验器
//        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withClaim("username", getUsername(token))//从不加密的消息体中取出username
                .build();
        //生成的token会有roles的Claim，这里不加不知道行不行。
        // 一个是直接从客户端传来的token，一个是根据盐和用户名等信息生成secret后再生成的token

        DecodedJWT jwt = verifier.verify(token);

        return true;

    }

    /**
     * 在token中获取到username信息
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static JwtUser recreateUserFromToken(String token) {
        JwtUser user = new JwtUser();
        DecodedJWT jwt = JWT.decode(token);

        user.setUsername(jwt.getClaim("username").asString());
        user.setRoles(jwt.getClaim("roles").asList(String.class));
        //r-p映射在运行时去取
        return user;
    }

    /**
     * 判断是否过期
     */
    public static boolean isExpire(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis();
    }


    public static String createJwtTokenIfNeeded(JwtUser jwtUser, String jwtToken) {

        log.debug("createJwtTokenIfNeeded");
        DecodedJWT jwt = JWT.decode(jwtToken);

        log.debug(""+(jwt.getExpiresAt().getTime()-System.currentTimeMillis()));

        //过期时刻-当前时刻。若为负则已过期，不会进到该方法。
        if (jwt.getExpiresAt().getTime()-System.currentTimeMillis()<=krestJwtProperties.getRefreshTimeBeforeExpire().toMillis()){
            return createJwtTokenByUser(jwtUser);
        }else{
            return null;
        }
    }
}
