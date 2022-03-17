package com.chenkaiwei.krest.config;


import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.auth0.jwt.algorithms.Algorithm;
import com.chenkaiwei.krest.cryption.CryptionRequestAdvice;
import com.chenkaiwei.krest.cryption.CryptionResponseAdvice;
import com.chenkaiwei.krest.cryption.config.KrestMvcConfigurer;
import com.chenkaiwei.krest.filters.JwtBearerHttpAuthenticationFilter;
import com.chenkaiwei.krest.JwtUtil;
import com.chenkaiwei.krest.realms.TokenValidateAndAuthorizingRealm;
import com.chenkaiwei.krest.realms.UsernamePasswordRealm;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.Filter;
import java.util.*;
@Slf4j
@Configuration
@EnableConfigurationProperties(KrestProperties.class)
@Import({JwtUtil.class, CryptionRequestAdvice.class, CryptionResponseAdvice.class,KrestMvcConfigurer.class})
public class KrestAutoConfiguration {

/**
 * 配置shiro部分 */
    /**
     * 多realm管理，主要是验证策略，首个成功即可。
     * https://blog.csdn.net/qq_35981283/article/details/78632252
     */

//    @Bean
//    public ModularRealmAuthenticator modularRealmAuthenticator() {
//        ModularRealmAuthenticator modularRealmAuthenticator = new ModularRealmAuthenticator();
//        modularRealmAuthenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
//        /*
//        AtLeastOneSuccessfulStrategy，至少有一个生效。
//        AllSuccessfulStrategy()（所有realm都验证通过才能成功登陆）
//        FirstSuccessfulStrategy()（只要有一个生效就不会去其它realm验证）。
//        ↑第一个第三个不是一回事么？第一个强制全跑一遍有啥意义？
//        */
//        return modularRealmAuthenticator;
//    }

    @ConditionalOnMissingBean//TODO 有空研究：shiro里的那个自动securityManager也是OnMissingBean，为啥自动组装了这个没组装那个。
    @Bean //shiro里自动配置的securityManager带一个参数【securityManager(List<Realm> realms)】，所以写了名字才能整个代替它
    public DefaultWebSecurityManager securityManager(KrestProperties krestProperties,KrestConfigurer krestConfiguration,List<Realm> realms) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();

        //配置多个realm时的使用策略
        ModularRealmAuthenticator modularRealmAuthenticator = new ModularRealmAuthenticator();
        modularRealmAuthenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
//        modularRealmAuthenticator.getAuthenticationListeners().add(new AuthenticationListener() {
        //listener用法，备用。
//            @Override
//            public void onSuccess(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
//
//            }
//
//            @Override
//            public void onFailure(AuthenticationToken authenticationToken, AuthenticationException e) {
//                log.error("onFailure:"+e);
//            }
//
//            @Override
//            public void onLogout(PrincipalCollection principalCollection) {
//                log.error("onFailure:");
//            }
//        });
        manager.setAuthenticator(modularRealmAuthenticator);

//        Collection<Realm> realms = new ArrayList();
//        realms.add(tokenValidateAndAuthorizingRealm());
//        //根据配置决定要不要加上usernamePasswordRealm
//        if (krestProperties.isEnableUsernamePasswordRealm()){
//            log.debug("enable-username-password-realm: true");
//            realms.add(usernamePasswordRealm);
//        }

        manager.setRealms(realms);

        //setRealms执行完会有一个afterRealmsSet，把realms同时也加到Authenticator里，所以上面两条顺序不能换（shiro源码中有好几处强制要求代码顺序的地方，稳健性差）


        return manager;
    }

    /**
     * shiroFilterFactoryBean
     * */
    @SneakyThrows
    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager,KrestConfigurer krestConfiguration) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();

        // 添加自己的过滤器并且取名为jwt

        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("authcBearerJwt", new JwtBearerHttpAuthenticationFilter());//定义filter
        //默认过滤器：authcBearer-BearerHttpAuthenticationFilter

        /*
         * filter配置规则参考官网
         * http://shiro.apache.org/web.html#urls-
         * 默认过滤器对照表
         * https://shiro.apache.org/web.html#default-filters
         */
        Map<String, String> filterRuleMap = new HashMap<>();

        krestConfiguration.configFilterChainDefinitionMap(filterRuleMap);

        // 所有请求通过我们自己的authcBearerJwt Filter
        filterRuleMap.put("/**", "authcBearerJwt");//一条路径配俩filter的语法，逗号间隔(还是翻源码看出来的，官方文档都没有)

        //  perms[actuator] 对应 PermissionsAuthorizationFilter
        factoryBean.setGlobalFilters(Arrays.asList("noSessionCreation"));//对应NoSessionCreationFilter，全局无状态

        factoryBean.setUnauthorizedUrl("/unauthorized");//有用没用？


        factoryBean.setSecurityManager(securityManager);
        factoryBean.setFilters(filterMap);
        factoryBean.setFilterChainDefinitionMap(filterRuleMap);
        //配置全局过滤器   过滤器名规则和filterRuleMap一致  本方法会给filterRuleMap里配的每个路径都加一个对应的过滤器

//        //调试用，用以检查filter链的配置的状况
//        AbstractShiroFilter shiroFilter=(AbstractShiroFilter)(factoryBean.getObject());
//        FilterChainResolver filterChainResolver= shiroFilter.getFilterChainResolver();

        return factoryBean;
    }

//    /**
//     * 不太懂啥意思，先取消了
//     */
//    @Bean
//    @DependsOn("lifecycleBeanPostProcessor")
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
//        // 强制使用cglib，防止重复代理和可能引起代理出错的问题
//        // https://zhuanlan.zhihu.com/p/29161098
//        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
//        return defaultAdvisorAutoProxyCreator;
//    }


    /*定义俩realm*/
    @Bean
    @ConditionalOnProperty(prefix = "krest",value = "enable-username-password-realm",matchIfMissing = false)
    //↑ 可选项，不显示设置则不装载
    public UsernamePasswordRealm usernamePasswordRealm(KrestConfigurer krestConfiguration) {
        UsernamePasswordRealm usernamePasswordRealm = new UsernamePasswordRealm();
        CredentialsMatcher credentialsMatcher=krestConfiguration.configPasswordCredentialsMatcher();
        Assert.notNull(credentialsMatcher,"您尚未实现krestConfiguration.createPasswordCredentialsMatcher()接口");
        usernamePasswordRealm.setCredentialsMatcher(credentialsMatcher);//写到realm的构造器里去
        return usernamePasswordRealm;
    }

    @Bean
    public TokenValidateAndAuthorizingRealm tokenValidateAndAuthorizingRealm() {
        TokenValidateAndAuthorizingRealm tokenValidateAndAuthorizingRealm = new TokenValidateAndAuthorizingRealm();
        //不设定credentialsMatcher时会自动生成一个SimpleCredentialsMatcher，其实压根没用，形式上却还要走一遍doCredentialsMatch，知名框架里也有凑合
        //credentialsMatcher改到realm的构造器中用匿名类实现。专用的小部件不用暴露给外面。

        //放弃使用cacheManager来临时性保存请求中携带的jwt token
        return tokenValidateAndAuthorizingRealm;
    }

//    @Bean
//    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }


//    /**
//     * 开启shiro 注解支持.API接口上限制权限/角色注解能够生效 :
//     * 需要认证 {@link org.apache.shiro.authz.annotation.RequiresAuthentication RequiresAuthentication}
//     * 需要用户 {@link org.apache.shiro.authz.annotation.RequiresUser RequiresUser}
//     * 需要访客 {@link org.apache.shiro.authz.annotation.RequiresGuest RequiresGuest}
//     * 需要角色 {@link org.apache.shiro.authz.annotation.RequiresRoles RequiresRoles}
//     * 需要权限 {@link org.apache.shiro.authz.annotation.RequiresPermissions RequiresPermissions}
//     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
//        advisor.setSecurityManager(securityManager);
//        return advisor;
//    }

    /**
     * 凭证匹配，加密算法。已转移到realm的构造器里去，
     *
     * @return
     */
//    @Bean
//    public HashedCredentialsMatcher hashedCredentialsMatcher() {
//        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
//        hashedCredentialsMatcher.setHashAlgorithmName("md5");
//        hashedCredentialsMatcher.setHashIterations(2);//密码保存策略一致，2次md5加密
//        return hashedCredentialsMatcher;
//    }

    /*原shiro部分end*/

    @Bean
    public Algorithm jwtAlgorithm(KrestConfigurer krestConfiguration){
        return krestConfiguration.configJwtAlgorithm();
    }

    @Bean
    public Map<String, Collection<String>> rolePermissionsMap(KrestConfigurer krestConfiguration){
        //↗入参部分，改成Autowire定义在外面就不行，这种写法就灵活很多，自动解决依赖顺序问题
        Map<String, Collection<String>> res=krestConfiguration.configRolePermissionsMap();


        return res;
    }

    /*Cryption策略*/
    @Bean
    public AsymmetricCrypto tempSecretKeyCryptoAlgorithm(KrestConfigurer krestConfiguration){

        return krestConfiguration.configTempSecretKeyCryptoAlgorithm();
    }

    @Bean
    //亲测俩叠加可以 ↓，且的关系
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "krest",value = "enable-fast-json-converter",matchIfMissing = true)
    //↑ 注意，用yml里的属性名，不是properties里的属性名（enableFastJsonConverter）。
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter(KrestConfigurer krestConfiguration) {

        FastJsonHttpMessageConverter res = new FastJsonHttpMessageConverter();

        res.setFastJsonConfig(krestConfiguration.fastJsonConverterConfig());

        return res;
    }
}
