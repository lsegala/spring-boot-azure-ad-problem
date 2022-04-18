package com.azure.spring.sample.aad.service;

import com.azure.spring.sample.aad.model.Role;
import com.azure.spring.sample.aad.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private List<User> users;

    @PostConstruct
    public void init(){
        this.users = new ArrayList<>();
    }

    public Optional<User> findByLogin(String login){
        return users.stream()
                .filter(p -> p.getUsername().equals(login))
                .findFirst();
    }

    public User create(Authentication authentication) {
        User user = new User();
        user.setUsername(authentication.getName());
        users.add(user);
        return user;
    }

    public void createRoles(User user, List<Role> authoroties) {
        users.stream().filter(p -> p.getUsername().equals(user.getUsername()))
                .forEach(u -> u.setAuthorities(authoroties));
    }
}
