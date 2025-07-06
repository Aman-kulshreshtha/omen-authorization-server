package com.security.omen.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class RegisteredClientRequest {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String clientName;

    @NotBlank
    private List<@NotBlank String> redirectUris;

    @NotBlank
    private List<@NotBlank String> grantTypes;

    @NotBlank
    private List<@NotBlank String> scopes;

    private Boolean requireConsent = false;

    public @NotBlank String getClientId() {
        return clientId;
    }

    public void setClientId(@NotBlank String clientId) {
        this.clientId = clientId;
    }

    public @NotBlank String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(@NotBlank String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public @NotBlank String getClientName() {
        return clientName;
    }

    public void setClientName(@NotBlank String clientName) {
        this.clientName = clientName;
    }

    public @NotBlank List<@NotBlank String> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(@NotBlank List<@NotBlank String> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public @NotBlank List<@NotBlank String> getGrantTypes() {
        return grantTypes;
    }

    public void setGrantTypes(@NotBlank List<@NotBlank String> grantTypes) {
        this.grantTypes = grantTypes;
    }

    public @NotBlank List<@NotBlank String> getScopes() {
        return scopes;
    }

    public void setScopes(@NotBlank List<@NotBlank String> scopes) {
        this.scopes = scopes;
    }

    public Boolean getRequireConsent() {
        return requireConsent;
    }

    public void setRequireConsent(Boolean requireConsent) {
        this.requireConsent = requireConsent;
    }
}
