package com.security.omen.controller;

import com.security.omen.dto.RegisteredClientRequest;
import com.security.omen.entity.RegisteredClientEntity;
import com.security.omen.repository.RegisteredClientEntityRepository;
import com.security.omen.service.CustomRegisteredClientRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Client Registration", description = "Register and manage OAuth clients")
public class ClientRegistrationController {

    @Autowired
    private  CustomRegisteredClientRepository clientRepository;

    @Autowired
    private  RegisteredClientEntityRepository repo;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String registerClient(@RequestBody @Valid RegisteredClientRequest request) {
        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret()) // encoded later
                .clientName(request.getClientName());

        request.getRedirectUris().forEach(builder::redirectUri);
        request.getScopes().forEach(builder::scope);
        request.getGrantTypes().forEach(gt -> builder.authorizationGrantType(new AuthorizationGrantType(gt)));

        clientRepository.save(builder.build());
        return "Client registered successfully.";
    }

    @GetMapping
    public List<RegisteredClientEntity> listClients() {
        return repo.findAll();
    }
}
