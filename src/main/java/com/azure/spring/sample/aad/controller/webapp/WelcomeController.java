// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.spring.sample.aad.controller.webapp;

import com.azure.spring.sample.aad.model.Role;
import com.azure.spring.sample.aad.model.User;
import com.azure.spring.sample.aad.service.UserService;
import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.DirectoryObjectCollectionWithReferencesPage;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;

@Controller
public class WelcomeController {
    @Autowired
    OAuth2AuthorizedClientService graphAuthorizedClientService;
    @Autowired
    UserService userService;

    public List<Role> getUserGroups(@NonNull OAuth2AuthorizedClient graphAuthorizedClient, String prefix) {
        final DirectoryObjectCollectionWithReferencesPage groups = GraphServiceClient.builder()
                .authenticationProvider(new GraphAuthenticationProvider(graphAuthorizedClient))
                .buildClient()
                .me()
                .memberOf()
                .buildRequest(new QueryOption("$search", "\"displayName:"+prefix+"\""), new HeaderOption("ConsistencyLevel", "eventual"))
                .get();
        if(groups == null) {
            return Collections.emptyList();
        }
        return groups.getCurrentPage().stream()
                .map(m -> new Role(((Group)m).displayName))
                .collect(Collectors.toList());
    }

    @GetMapping("/welcome")
    public String index(
        Model model,
        @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient azureClient
    ) {
        final List<Role> userGroups = getUserGroups(azureClient, "SS_Business");
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final User user = userService.findByLogin(authentication.getName()).orElse(new User());

        if(ofNullable(user.getAuthorities()).orElse(emptySet()).size() != userGroups.size()){
            userService.createRoles(user, userGroups);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    userGroups
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            graphAuthorizedClientService.saveAuthorizedClient(azureClient, newAuth);
        }else{
            graphAuthorizedClientService.saveAuthorizedClient(azureClient, authentication);
        }

        model.addAttribute("userName", authentication.getName());
        model.addAttribute("preferredUsername", authentication.getName());
        model.addAttribute("clientName", azureClient.getClientRegistration().getClientName());
        return "index";
    }

    private static class GraphAuthenticationProvider extends BaseAuthenticationProvider {
        private final OAuth2AuthorizedClient graphAuthorizedClient;

        public GraphAuthenticationProvider(@NonNull OAuth2AuthorizedClient graphAuthorizedClient) {
            this.graphAuthorizedClient = graphAuthorizedClient;
        }

        @Override
        public @NotNull
        CompletableFuture<String> getAuthorizationTokenAsync(@NonNull final URL requestUrl){
            return CompletableFuture.completedFuture(
                    graphAuthorizedClient.getAccessToken() != null? graphAuthorizedClient.getAccessToken().getTokenValue() : ""
            );
        }
    }
}
