package com.cpcd.microservices.app.oauth.serviciooauthcas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;
import java.util.Base64;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private Environment env;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private InfoAdicionalToken infoAdicionalToken;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       clients.inMemory().withClient("MY_CLIENT1_ID")
               .secret(passwordEncoder.encode("MY_CLIENT1_SECRET"))
               .scopes("read", "write")
               .authorizedGrantTypes("password", "refresh_token")
               .accessTokenValiditySeconds(7200)
               .refreshTokenValiditySeconds(86400)
               .and()
               .withClient("MY_CLIENT2_ID")
               .secret(passwordEncoder.encode("MY_CLIENT2_SECRET"))
               .scopes("read", "write")
               .authorizedGrantTypes("password", "refresh_token")
               .accessTokenValiditySeconds(7200)
               .refreshTokenValiditySeconds(86400)
               .and()
               .withClient("MY_CLIENT3_ID")
               .secret(passwordEncoder.encode("MY_CLIENT3_SECRET"))
               .scopes("read", "write")
               .authorizedGrantTypes("password", "refresh_token")
               .accessTokenValiditySeconds(7200)
               .refreshTokenValiditySeconds(86400);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicionalToken, accessTokenConverter()));
        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain);

    }

    @Bean
    public JwtTokenStore tokenStore(){
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter(){
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        tokenConverter.setSigningKey(Base64.getEncoder().encodeToString("MY_JWT_KEY".getBytes()));
        return tokenConverter;
    }

}
