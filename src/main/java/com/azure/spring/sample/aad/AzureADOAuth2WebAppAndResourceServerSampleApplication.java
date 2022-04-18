// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.sample.aad;

import com.azure.spring.sample.aad.service.CustomADOAuth2UserService;
import com.azure.spring.sample.aad.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.azure.spring.sample.aad.service",
        "com.azure.spring.sample.aad.controller.webapp",
        "com.azure.spring.sample.aad.config",
        "com.azure.spring.sample.aad.security"
})
public class AzureADOAuth2WebAppAndResourceServerSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzureADOAuth2WebAppAndResourceServerSampleApplication.class, args);
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService(UserService userService){
        return new CustomADOAuth2UserService(userService);
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(new UrlTemplateResolver());
        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new SpringSecurityDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }
}
