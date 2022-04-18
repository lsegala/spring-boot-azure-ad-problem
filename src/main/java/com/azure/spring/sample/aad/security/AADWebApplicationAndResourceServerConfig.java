// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.sample.aad.security;

import com.azure.spring.aad.webapp.AADWebSecurityConfigurerAdapter;
import com.azure.spring.sample.aad.config.CustomSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AADWebApplicationAndResourceServerConfig extends AADWebSecurityConfigurerAdapter {
    @Autowired
    CustomSuccessHandler successHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);

        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                    .successHandler(successHandler);
    }
}
