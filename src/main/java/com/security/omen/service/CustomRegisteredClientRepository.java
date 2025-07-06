package com.security.omen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.omen.entity.RegisteredClientEntity;
import com.security.omen.repository.RegisteredClientEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final RegisteredClientEntityRepository repo;
    private final PasswordEncoder encoder;


    public CustomRegisteredClientRepository(RegisteredClientEntityRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void save(RegisteredClient registeredClient) {
        if (repo.findByClientId(registeredClient.getClientId()).isPresent()) {
            throw new IllegalArgumentException("Client ID already exists");
        }

        RegisteredClientEntity entity = new RegisteredClientEntity();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientSecret(
                registeredClient.getClientSecret() != null
                        ? encoder.encode(registeredClient.getClientSecret())
                        : null
        );
        entity.setClientName(registeredClient.getClientName());
        entity.setRedirectUris(String.join(",", registeredClient.getRedirectUris()));
        entity.setScopes(String.join(",", registeredClient.getScopes()));
        entity.setGrantTypes(registeredClient.getAuthorizationGrantTypes().stream()
                .map(AuthorizationGrantType::getValue).collect(Collectors.joining(",")));
        entity.setRequireConsent(false); // Extend if needed
        // Optional: You could also serialize client/token settings back to JSON
        repo.save(entity);
    }

    private ClientAuthenticationMethod getAuthMethod(String method) {
        return switch (method) {
            case "none" -> ClientAuthenticationMethod.NONE;
            case "client_secret_post" -> ClientAuthenticationMethod.CLIENT_SECRET_POST;
            case "client_secret_basic" -> ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
            default -> throw new IllegalArgumentException("Unsupported auth method: " + method);
        };
    }

    private RegisteredClient fromEntity(RegisteredClientEntity e) {
        // Parse client_settings
        Map<String, Object> clientSettingsMap = parseJsonMap(e.getClientSettings());
        String authMethod = (String) clientSettingsMap.getOrDefault(
                "token_endpoint_authentication_method", "client_secret_basic"
        );
        boolean requireProofKey = Boolean.TRUE.equals(clientSettingsMap.get("require_proof_key"));

        // Parse token_settings (optional)
        Map<String, Object> tokenSettingsMap = parseJsonMap(e.getTokenSettings());
        TokenSettings.Builder tokenBuilder = TokenSettings.builder();

        // Optional parsing logic (extend as needed)
        if (tokenSettingsMap.containsKey("access_token_time_to_live")) {
            tokenBuilder.accessTokenTimeToLive(Duration.ofSeconds(
                    ((Number) tokenSettingsMap.get("access_token_time_to_live")).longValue()));
        }

        if (tokenSettingsMap.containsKey("reuse_refresh_tokens")) {
            tokenBuilder.reuseRefreshTokens((Boolean) tokenSettingsMap.get("reuse_refresh_tokens"));
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(e.getId())
                .clientId(e.getClientId())
                .clientName(e.getClientName())
                .clientAuthenticationMethod(getAuthMethod(authMethod))
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(requireProofKey)
                        .build())
                .tokenSettings(tokenBuilder.build());

        if (e.getClientSecret() != null) {
            builder.clientSecret(e.getClientSecret());
        }

        Arrays.stream(e.getRedirectUris().split(","))
                .map(String::trim)
                .forEach(builder::redirectUri);

        Arrays.stream(e.getScopes().split(","))
                .map(String::trim)
                .forEach(builder::scope);

        Arrays.stream(e.getGrantTypes().split(","))
                .map(String::trim)
                .map(AuthorizationGrantType::new)
                .forEach(builder::authorizationGrantType);

        return builder.build();
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON: " + json, e);
        }
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        RegisteredClientEntity entity = repo.findByClientId(clientId).orElseThrow();
        return fromEntity(entity);
    }

    @Override
    public RegisteredClient findById(String id) {
        RegisteredClientEntity entity = repo.findById(id).orElseThrow();
        return fromEntity(entity);
    }
}
