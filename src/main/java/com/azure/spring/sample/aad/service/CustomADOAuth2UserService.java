package com.azure.spring.sample.aad.service;

import com.azure.spring.sample.aad.model.User;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomADOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final UserService userService;

    public CustomADOAuth2UserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest oidcUserRequest) throws OAuth2AuthenticationException {
        final User user = userService.findByLogin(oidcUserRequest.getIdToken().getPreferredUsername())
                .orElse(new User());
        return new DefaultOidcUser(
                user.getAuthorities(),
                oidcUserRequest.getIdToken()
        );
    }
}
