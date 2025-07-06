package com.security.omen.service;

import com.security.omen.entity.RegisteredClientEntity;
import com.security.omen.repository.RegisteredClientEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {


    @Autowired
    private RegisteredClientEntityRepository repo;

    @Autowired
    private PasswordEncoder encoder;


    @Override
    public void save(RegisteredClient registeredClient) {
        if (repo.findByClientId(registeredClient.getClientId()).isPresent()) {
            throw new IllegalArgumentException("Client ID already exists");
        }

        RegisteredClientEntity entity = new RegisteredClientEntity();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientSecret(encoder.encode(registeredClient.getClientSecret()));
        entity.setClientName(registeredClient.getClientName());
        entity.setRedirectUris(String.join(",", registeredClient.getRedirectUris()));
        entity.setScopes(String.join(",", registeredClient.getScopes()));
        entity.setGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue).collect(Collectors.joining(",")));

        entity.setRequireConsent(false); // extend if needed
        repo.save(entity);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        RegisteredClientEntity e = repo.findByClientId(clientId).orElseThrow();
        RegisteredClient.Builder builder = RegisteredClient.withId(e.getId())
                .clientId(e.getClientId())
                .clientSecret(e.getClientSecret())
                .clientName(e.getClientName());

        Arrays.stream(e.getRedirectUris().split(",")).forEach(builder::redirectUri);
        Arrays.stream(e.getScopes().split(",")).forEach(builder::scope);
        Arrays.stream(e.getGrantTypes().split(",")).forEach(gt -> builder.authorizationGrantType(new AuthorizationGrantType(gt)));

        return builder.build();
    }

    @Override
    public RegisteredClient findById(String id) {
        RegisteredClientEntity e = repo.findById(id).orElseThrow();
        RegisteredClient.Builder builder = RegisteredClient.withId(e.getId())
                .clientId(e.getClientId())
                .clientSecret(e.getClientSecret())
                .clientName(e.getClientName());

        Arrays.stream(e.getRedirectUris().split(",")).forEach(builder::redirectUri);
        Arrays.stream(e.getScopes().split(",")).forEach(builder::scope);
        Arrays.stream(e.getGrantTypes().split(",")).forEach(gt -> builder.authorizationGrantType(new AuthorizationGrantType(gt)));

        return builder.build();
    }
}
