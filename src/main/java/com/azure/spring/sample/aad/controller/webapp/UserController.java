package com.azure.spring.sample.aad.controller.webapp;

import com.azure.spring.sample.aad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Collections.emptyList;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    OAuth2AuthorizedClientService graphAuthorizedClientService;

    @GetMapping("/create-user")
    public String success(HttpServletRequest request, HttpServletResponse response, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.create(authentication);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                authentication.getCredentials(),
                emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
        graphAuthorizedClientService.saveAuthorizedClient(graphAuthorizedClient, newAuth);

        return "create-user";
    }
}
