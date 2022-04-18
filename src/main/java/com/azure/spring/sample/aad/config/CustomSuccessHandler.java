package com.azure.spring.sample.aad.config;

import com.azure.spring.sample.aad.model.User;
import com.azure.spring.sample.aad.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    UserService userService;

    private Optional<User> getUserAuthenticated() {
        return userService.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final Optional<User> optUsuarioModel = getUserAuthenticated();
        if(!optUsuarioModel.isPresent()){
            response.sendRedirect("/create-user");
        }else{
            response.sendRedirect("/welcome");
        }
    }
}
